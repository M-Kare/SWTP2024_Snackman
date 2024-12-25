#!C:\Users\derwo\AppData\Local\Programs\Python\Python311
# -*- coding: utf-8 -*-
#
#     W = Wand 
#     L = Leer 
#     S = Snack 
#     G = Geist
#    SM = Snackman
#
#     W W W -> northwest_square, north_square, northeast_square
#     W   W -> west_square, east_square
#     W W W -> southwest_square, south_square, southeast_square
#
#     direction: the direction index in which the chickens head is looking and last walked into
#     e.g. if the solution liste is [W,G, ,W,2] the chicken is walking in south direction and looking into south direction too
#
#     The direction in which the chicken is moving is defined in the wiki. It moved according to the definition,
#     it prefers walking into the same direction it last walked into. If this is (due to the wiki definition) not possible,
#     it will randomly choose a walking direction (according to the wiki)
#
#    returns: [north_square, east_square, south_square, west_square, indexOfNextPosition]
import random

def choose_next_square(squares_liste):
    ##northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, direction = squares_liste
    ##solution_liste = [north_square, east_square, south_square, west_square]

    #two_North_two_West_square, two_North_one_West_square, two_North_square, two_North_one_East_square, two_North_two_East_square = squares_liste[:5]
    one_North_two_West_square, one_North_one_West_square, one_North_square, one_North_one_East_square, one_North_two_East_square = squares_liste[5:10]
    two_West_square, one_West_square, chickens_square, one_East_square, two_East_square = squares_liste[10:15]
    one_South_two_West_square, one_South_one_West_square, one_South_square, one_South_one_East_square, one_Soutn_two_East_square = squares_liste[15:20]
    #two_South_two_West_square, two_South_one_West_square, two_South_square, two_South_one_East_square, two_Soutn_two_East_square = squares_liste[20:25]

    solution_liste = [one_North_square, one_East_square, one_South_square, one_West_square]
    direction = squares_liste[len(squares_liste) - 1]
    direction = int(direction)

    # make sure you cannot walk into a wall
    solution_liste = eliminate_walls_as_options(solution_liste)
    solution_liste = eliminate_snackman_as_options(solution_liste)
    # make sure you do not walk into a ghost
    if all_squares_have_ghosts(solution_liste):
        return add_walking_direction(choose_random_square(solution_liste, "G", direction))
    # choose square with snack
    if all_squares_have_snack(solution_liste):
        # choose random snack
        return add_walking_direction(choose_random_square(solution_liste, "S", direction))
    elif at_least_one_square_with_snack(solution_liste):
        # choose a square with snack (far away from ghost)
        return add_walking_direction(choose_snack_away_from_ghost(solution_liste, direction))
    elif at_least_one_square_with_ghost(solution_liste):
        # choose square without snack, away from ghosts
        north, east, south, west = solution_liste
        return add_walking_direction(choose_square_without_snack_away_from_ghost(north, east, south, west, direction))
    else:
        # choose random square, no snacks there + no ghosts
        return add_walking_direction(choose_random_square(solution_liste, "L", direction))

def eliminate_walls_as_options(liste):
    return ['X' if item == 'W' else item for item in liste]

def eliminate_snackman_as_options(liste):
    return ['X' if item == 'SM' else item for item in liste]

def all_squares_have_ghosts(liste):
    return all('G' == element or 'X' == element for element in liste)

# replaces a random element which is of the kind of toReplace
def choose_random_square(liste, toReplace, direction):
    # check if current walking direction is ok
    if liste[direction] == toReplace:
        liste[direction] = " "
        return liste
    # get list of indexes of things to replace
    indexes_to_choose_from = []
    for i in range(len(liste)):
        if liste[i] == toReplace:
            indexes_to_choose_from.append(i)
    last_step = get_last_step(direction)    # the last square as the sky direction on which the chicken stood
    if len(indexes_to_choose_from) > 1 and last_step in indexes_to_choose_from:
        indexes_to_choose_from.remove(last_step)    # delete the previous walking direction
    # replace random index
    zufall_index = random.choice(indexes_to_choose_from)
    liste[zufall_index] = " "
    return liste

#   Returns the last step the chicken took.
#   For example, if it looks in the direction of 0 (north).
#   If it came from the south and should therefore not decide to run back to the south
def get_last_step(direction):
    if direction == 0:
        return 2
    elif direction == 1:
        return 3
    elif direction == 2:
        return 0
    elif direction == 3:
        return 1

# sets direction of first ' ' in liste items
def add_walking_direction(original_liste):
    new_liste = [original_liste[0]] + original_liste[1:]
    first_empty_index = next((i for i, x in enumerate(new_liste) if x == ' '), None)
    result = new_liste + [first_empty_index]
    return result

def all_squares_have_snack(original_liste):
    return set(original_liste).issubset({'S', 'X'})

def at_least_one_square_with_snack(original_liste):
    return "S" in original_liste

def at_least_one_square_with_ghost(original_liste):
    return "G" in original_liste

def choose_snack_away_from_ghost(original_liste, direction):
    north_square, east_square, south_square, west_square = original_liste
    # check if there are ghosts, if not -> choose snack
    if not at_least_one_square_with_ghost(original_liste):
        if at_least_one_square_with_snack(original_liste):
            # choose snack
            return choose_random_square(original_liste, "S", direction)
        else:
            # choose empty space
            return choose_random_square(original_liste, "L", direction)
    # alle gegenüber der G anschauen, wenn S da: dort hingehen
    if original_liste[direction] == "S":
        original_liste[direction] = " "
        return original_liste
    if north_square == "G" and south_square == "S":
        return [north_square, east_square, " ", west_square]
    if north_square == "S" and south_square == "G":
        return [" ", east_square, south_square, west_square]
    if west_square == "G" and east_square == "S":
        return [north_square, " ", south_square, west_square]
    if west_square == "S" and east_square == "G":
        return [north_square, east_square, south_square, " "]
    # wenn L da: weiter nach S ggü von G suchen
    return choose_square_without_snack_away_from_ghost(north_square, east_square, south_square, west_square, direction)

def choose_square_without_snack_away_from_ghost(north_square, east_square, south_square, west_square, direction):
    if list([north_square, east_square, south_square, west_square])[direction] == "L":
        solution_list =  list([north_square, east_square, south_square, west_square])
        solution_list[direction] = " "
        return solution_list
    if north_square == "G" and south_square == "L":
        return [north_square, east_square, " ", west_square]
    if north_square == "L" and south_square == "G":
        return [" ", east_square, south_square, west_square]
    if north_square == "G" and south_square == "L":
        return [north_square, east_square, " ", west_square]
    if north_square == "L" and south_square == "G":
        return [" ", east_square, south_square, west_square]
    # wenn nichts gefunden: irgendein L nehmen
    return choose_random_square([north_square, east_square, south_square, west_square], "L", direction)

