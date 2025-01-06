import random


# returns next free(not a Wall) adjacent field
def searchFreeFieldAdjacent(maze, x, y):
    if maze[x + 1][y + 1] != '#':
        return (x + 1, y + 1)
    elif maze[x][y + 1] != '#':
        return (x, y + 1)
    elif maze[x + 1][y] != '#':
        return (x + 1, y)
    elif maze[x - 1][y] != '#':
        return (x - 1, y)
    elif maze[x][y - 1] != '#':
        return (x, y - 1)

    return None


def generateSpawnGhost(maze):
    for i in range(5):
        randome1 = random.randint(1, len(maze) - 2)
        randome2 = random.randint(1, len(maze) - 2)
        if maze[randome1][randome2] != '#':
            maze[randome1][randome2] = 'G'
        else:
            randome1, randome2 = searchFreeFieldAdjacent(maze, randome1, randome2)
            maze[randome1][randome2] = 'G'

    return maze


def generateSpawnChicken(maze):
    for i in range(1):
        randome1 = random.randint(1, len(maze) - 2)
        randome2 = random.randint(1, len(maze) - 2)
        if maze[randome1][randome2] != '#' or maze[randome1][randome2] != 'G' or maze[randome1][randome2] != 'S':
            maze[randome1][randome2] = 'C'
        else:
            randome1, randome2 = searchFreeFieldAdjacent(maze, randome1, randome2)

    return maze


def generateFreeCenter(maze, width, height):
    for y in range(len(maze)):
        for x in range(len(maze[y])):
            if (width / 3 < x < 2 * width / 3 and height / 3 < y < (2 * height / 3)):
                maze[y][x] = " "

    return maze


def generateSpawnSnackman(maze):
    center = (int)(len(maze) / 2)
    maze[center][center] = 'S'

    return maze


def generateLabyrinth(width, height):
    maze = [["#" for i in range(width)] for i in range(height)]
    stack = [(1, 1)]
    maze[1][1] = " "

    while stack:
        x, y = stack[-1]
        richtungen = [(0, 2), (2, 0), (0, -2), (-2, 0)]
        random.shuffle(richtungen)

        for rx, ry in richtungen:
            aktx, akty = x + rx, y + ry
            if 1 <= aktx < width - 1 and 1 <= akty < height - 1 and maze[akty][aktx] == "#":
                randomeNumber = random.randint(1, 10)  # snacks spawn in ratio 1:10
                if (randomeNumber == 1):
                    maze[akty][aktx] == "o"
                    maze[y + ry // 2][x + rx // 2] = "o"
                else:
                    maze[akty][aktx] = " "
                    maze[y + ry // 2][x + rx // 2] = " "
                stack.append((aktx, akty))
                break
        else:
            stack.pop()

    maze = generateSpawnGhost(maze)

    maze = generateSpawnChicken(maze)

    maze = generateFreeCenter(maze, width, height)

    maze = generateFreeCenter(maze, width, height)

    maze = generateSpawnSnackman(maze)

    return maze


def saveFile(maze, filename="Maze.txt"):
    with open(filename, "w") as file:
        for row in maze:
            file.write("".join(row) + "\n")


def main():
    width, height = 20, 20
    maze = generateLabyrinth(width, height)
    saveFile(maze)
