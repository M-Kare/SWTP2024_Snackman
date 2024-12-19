#!C:\Users\derwo\AppData\Local\Programs\Python\Python311
# -*- coding: utf-8 -*-
#
#     W = Wand
#     L = Leer 
#     S = Snack -> treated same as Leer
#     G = Geist -> treated same as Leer
#     C = Huhn -> treated same as Leer (when looking for snackMan)
#     M = SnackMan
#
#     W W W -> northwest_square, north_square, northeast_square
#     W   W -> west_square, east_square
#     W W W -> southwest_square, south_square, southeast_square
#
#     direction: the direction index in which the ghosts head is looking and last walked into
#     e.g. if the solution liste is [W,L, ,W,2] the ghost is walking in south direction and looking into south direction too
#
#     The direction in which the ghost is moving is defined in the wiki.
#     The ghost will always go after SnackMan if it can see him and try to find the shortest path in the maze.
#     Otherwise, it will try to hunt chicken. If neither SnackMan nor chicken are visible, it will try to walk straight forward
#     (and if this is not possible, it chooses a random direction)
#
#     websites used for the path finding:
#     https://sqlpad.io/tutorial/python-maze-solver/,
#     https://levelup.gitconnected.com/a-star-a-search-for-solving-a-maze-using-python-with-visualization-b0cae1c3ba92
#
#    returns: indexOfNextPosition
import random
import heapq  # This module provides an implementation of the heap queue algorithm
from GhostMovementSkript import choose_next_square as choose_simple_next_step

WALL = 'W'
EMPTY = 'L'
SNACK = 'S'
GHOST = 'G'
CHICKEN = 'C'
SNACKMAN = 'M'
INVALID = 'X'

lab = [
    ["W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"],
    ["W", "M", "L", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"],
    ["W", "L", "W", "L", "W", "W", "W", "W", "W", "W", "W", "W", "L", "W", "W"],
    ["W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"],
    ["W", "L", "W", "L", "W", "W", "W", "W", "W", "L", "W", "W", "W", "W", "W"],
    ["W", "L", "L", "L", "L", "L", "L", "L", "W", "L", "L", "L", "W", "L", "W"],
    ["W", "L", "L", "W", "W", "L", "L", "L", "L", "L", "L", "W", "W", "W", "W"],
    ["W", "L", "L", "L", "L", "L", "G", "L", "W", "L", "L", "L", "W", "L", "W"],
    ["W", "W", "W", "L", "W", "W", "L", "L", "L", "W", "W", "W", "L", "W", "W"],
    ["W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"],
    ["W", "W", "W", "L", "W", "L", "W", "W", "W", "W", "W", "W", "L", "W", "W"],
    ["W", "L", "W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "W"],
    ["W", "L", "W", "W", "W", "L", "W", "L", "W", "L", "W", "W", "W", "W", "W"],
    ["W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"],
    ["W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"]
]
def choose_next_move(labyrinth, direction):
    """
    Chooses the next move for the ghost.
    :param labyrinth: Which the ghost can see
    :return: the index of the next cardinal point
    """
    ghostPosition = (1,1)   # (x, z)
    snackmanPosition = (1,1)    # (x, z)
    result = choose_next_square_finding_snackman(labyrinth, ghostPosition, snackmanPosition)
    if result is None:
        x = ghostPosition[0]
        z = ghostPosition[1]
        northwest_square = labyrinth[x - 1][z - 1]
        north_square = labyrinth[x - 1][z]
        northeast_square = labyrinth[x - 1][z + 1]
        east_square = labyrinth[x][z + 1]
        southeast_square = labyrinth[x + 1][z + 1]
        south_square = labyrinth[x + 1][z]
        southwest_square = labyrinth[x + 1][z - 1]
        west_square = labyrinth[x][z - 1]
        last_direction = direction

        return choose_simple_next_step(list(northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, last_direction))

def seeing_snackman(lab):
    return any('M' in row for row in lab)

def choose_next_square_finding_snackman(labyrinth, ghostPosition, snackmanPosition):
    """
    calculates the shortest path from ghost toward snackman, and returns the cardinal direction of the first step
    :param labyrinth: the labyrinth that the ghost can see from where he is standing
    :param ghostPosition: the position the ghost is standing on in (a, b)
    :param snackmanPosition: the position the snackman is standing on in (a, b)
    :return: the direction the ghosts next step is
    """
    path = find_path_to_snackman(labyrinth, ghostPosition, snackmanPosition)
    (a, b) = (path[1][0] - ghostPosition[0], path[1][1] - ghostPosition[1])
    direction = a + 2 * b
    match direction:
        case -1:
            return 3
        case 1:
            return 1
        case -2:
            return 0
        case 2:
            return 2


def find_path_to_snackman(maze, ghostPosition, snackmanPosition):
    """
    calculates the shortest path from ghost toward snackman, and returns the path to walk towards the snackman
    :param maze: the labyrinth that the ghost can see from where he is standing
    :param ghostPosition: the position the ghost is standing on in (x, z)
    :param snackmanPosition: the position the snackman is standing on in (x, z)
    :return: a list of (x, z) to walk in order to finde the snackman from the ghosts position
    """
    # Initialize both the open and closed set
    open_set = []
    heapq.heappush(open_set, (0, ghostPosition))
    came_from = {}

    # Initial cost from ghostPosition to the current node
    actual_path_cost = {ghostPosition: 0}  # g_score in A* algorithm
    # Estimated cost from ghostPosition to snackmanPosition through the current node
    total_path_cost = {
        ghostPosition: get_estimated_path_cost(ghostPosition, snackmanPosition)}  # f_score in A* algorithm

    while open_set:
        # Current node is the node in open_set with the lowest total_path_cost value
        current = heapq.heappop(open_set)[1]

        # return result when reached snackmanPosition
        if current == snackmanPosition:
            return reconstruct_path(came_from, current)

        # compare possible neighbor steps (depending on their cost)
        for neighbor in get_neighbors(maze, current):
            neighbouring_actual_path_cost = actual_path_cost[
                                                current] + 1  # Assume cost for any step is 1 -> because neighbor is one step away from current
            if neighbor not in actual_path_cost or neighbouring_actual_path_cost < actual_path_cost[
                neighbor]:  # if find cheaper neighbor step
                # This path to neighbor is better than any previous one
                came_from[neighbor] = current
                actual_path_cost[neighbor] = neighbouring_actual_path_cost  # g_score in A* algorithm
                total_path_cost[neighbor] = neighbouring_actual_path_cost + get_estimated_path_cost(neighbor,
                                                                                                    snackmanPosition)  # f_score in A* algorithm
                if neighbor not in open_set:
                    heapq.heappush(open_set, (total_path_cost[neighbor], neighbor))  # add better step

    return None  # No path found


def reconstruct_path(came_from, current):
    """
    Reconstructs the shortest path the ghost has to walk to get to the snackman's position
    :param came_from: the list the algorithm went trying to find the shortest path
    :param current: the current (last) position
    :return: a list of the path the ghost has to walk to reach the snackman
    """
    path = []
    while current in came_from:
        path.append(current)
        current = came_from[current]
    path.append(current)  # add the start node
    path.reverse()  # reverse the path to start-to-goal order
    return path


def get_estimated_path_cost(cell_n, snackmanPosition):
    """
    Calculates the horizontal plus vertical distance between start and snackmanPosition (Manhattan distance).
    This distance is used as the heuristic cost to reach to the snackmanPosition cell from the current cell_n.
    It is the estimated cost to reach the snackmanPosition cell from cell_n.
    :param cell_n: the start point from which to calculate the distance
    -> initially this is the ghosts position
    :param snackmanPosition: the endpoint the ghost wants to reach
    :return: the manhattan distance between start and snackmanPosition
    """
    x1, z1 = cell_n
    x2, z2 = snackmanPosition
    return abs(x1 - x2) + abs(z1 - z2)


def get_neighbors(maze, currentNode):
    """
    The neighbors function is used to get neighbouring positions which are not a wall
    :param maze: the maze
    :param currentNode: the current node
    :return: a list of valid, non-wall neighbors of the currentNode
    """
    directions = [(0, 1), (1, 0), (0, -1), (-1, 0)]  # the directions around the currentNode
    result = []
    for direction in directions:
        neighbor = (currentNode[0] + direction[0], currentNode[1] + direction[1])  # get neighbour
        if 0 <= neighbor[0] < len(maze) and 0 <= neighbor[1] < len(maze[0]) and maze[neighbor[0]][
            neighbor[1]] != "W":  # if neighbor still within maze & neighbor not wall
            result.append(neighbor)  # neighbor possible direction for the currentNode to use
    return result

choose_next_square_finding_snackman(lab, (6, 7), (1, 1))
