#!C:\Users\derwo\AppData\Local\Programs\Python\Python311
# -*- coding: utf-8 -*-
#
#     W = Wand 
#     L = Leer 
#     S = Snack 
#     G = Geist
#
#     W W W -> northwest_square, north_square, northeast_square
#     W   W -> west_square, east_square
#     W W W -> southwest_square, south_square, southeast_square
#
#     direction: the direction index in which the chickens head is looking
#     e.g. if the solution liste is [W,G, ,W,2] the chicken is walking in south direction and looking into south direction too
#
#
#    returns: [north_square, east_square, south_square, west_square, indexOfNextPosition]
import random

def choose_next_square(squares_liste):
    northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, direction = list(squares_liste)
    direction = int(direction)
    solution_liste = [north_square, east_square, south_square, west_square]
    # make sure you cannot walk into a wall
    solution_liste = eliminate_walls_as_options(solution_liste)
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
    # replace random index
    zufall_index = random.choice(indexes_to_choose_from)
    liste[zufall_index] = " "
    return liste

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

