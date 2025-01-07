#!C:\Users\derwo\AppData\Local\Programs\Python\Python311
# -*- coding: utf-8 -*-
#
#     W = Wand
#     L = Leer
#     S = Snack
#     G = Geist
#     SM = SnackMan
#
#     W W W W W -> two_North_two_West_square, two_North_one_West_square, two_North_square, two_North_one_East_square, two_North_two_East_square
#     W W W W W -> one_North_two_West_square, one_North_one_West_square, one_North_square, one_North_one_East_square, one_North_two_East_square = squares_liste
#     W W   W W -> two_West_square, one_West_square, chickens_square, one_East_square, two_East_square = squares_liste
#     W W W W W -> one_South_two_West_square, one_South_one_West_square, one_South_square, one_South_one_East_square, one_Soutn_two_East_square = squares_liste
#     W W W W W -> two_South_two_West_square, two_South_one_West_square, two_South_square, two_South_one_East_square, two_Soutn_two_East_square = squares_liste

#     direction: the direction index in which the chickens head is looking
#     e.g. if the solution liste is [W,G, ,W,2] the chicken is walking in south direction and looking into south direction too
#
#    returns: [one_north_square, one_east_square, one_south_square, one_west_square, indexOfNextPosition]

import random
import time

def choose_next_square(squares_liste):
    """Checks if Snackman is in chickens field of view and returns solution list, with next step in direction to Snackman"""

    squares_liste = list(squares_liste)

    two_North_two_West_square, two_North_one_West_square, two_North_square, two_North_one_East_square, two_North_two_East_square = squares_liste[:5]
    one_North_two_West_square, one_North_one_West_square, one_North_square, one_North_one_East_square, one_North_two_East_square = squares_liste[5:10]
    two_West_square, one_West_square, chickens_square, one_East_square, two_East_square = squares_liste[10:15]
    one_South_two_West_square, one_South_one_West_square, one_South_square, one_South_one_East_square, one_Soutn_two_East_square = squares_liste[15:20]
    two_South_two_West_square, two_South_one_West_square, two_South_square, two_South_one_East_square, two_Soutn_two_East_square = squares_liste[20:25]

    solution_liste = [one_North_square, one_East_square, one_South_square, one_West_square]
    #generates solutionList with Indexes next to Chicken(because Chicken just can go one step

    if "SM" in squares_liste:
        solution_liste =  findWayToSM(solution_liste, squares_liste)
        return solution_liste
    else:
        return add_direction(choose_random_square(solution_liste))

def findWayToSM(solution, liste):
    """Finds Way to Snackman and returns solution_list with next step in direction to Snackman"""
    chickenIndex = 12
    directionListe =  [-5, +5, -1, +1]
    chickensFirstStepisSet = False
    nextDirectionIndex = 12
    chickensStepBefore = 0
    resettedChickenPosition_Counter = 0

    while liste[chickenIndex] != 'SM':

        if(resettedChickenPosition_Counter >= 20):# when SnackMan not reachable
            print("ich gebe auf!!!")
            return solution

        randomDirectionIndex = random.randint(0, 3)
        if(0 <= chickenIndex + directionListe[randomDirectionIndex] <= 24):
            if (chickenIndex + directionListe[randomDirectionIndex] != chickensStepBefore):
                if(randomDirectionIndex == 3): 
                    if((chickenIndex + 1 )% 5 == 0): #when Chicken is at right side of visibleEnvironment, chicken could go +1 and is on the left side of visibleEnvironment
                        continue
                if(randomDirectionIndex == 2):
                    if((chickenIndex + 1) % 5 == 1): #when Chicken is at right side of visibleEnvironment, chicken could go-#1 and is on the right side of visibleEnvironment
                        continue

                if liste[(chickenIndex + directionListe[randomDirectionIndex])] != 'W': #check if next chickenIndex is a Wall
                    chickensStepBefore = chickenIndex
                    chickenIndex += directionListe[randomDirectionIndex]
                    if(not chickensFirstStepisSet): #set the final destination Chicken will go (saves first step in every try to get so SM. Is reset when Chicken starts a new try(Index[12]))
                        nextDirectionIndex = chickenIndex
                        chickensFirstStepisSet = True
                    if 0 > chickenIndex or chickenIndex > 24:#new try when Chicken goes out of Index(reset chickensIndex to 12)
                        chickenIndex = 12
                        resettedChickenPosition_Counter += 1
                        chickensStepBefore = 12
                        chickensFirstStepisSet = False
                        continue
        else:
            nextDirectionIndex = 12
            chickenIndex = 12
            resettedChickenPosition_Counter += 1
            chickensStepBefore = 12
            chickensFirstStepisSet = False
    
    if chickenIndex == 7:
        if(solution[0] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 0)
    if chickenIndex == 11:
        if(solution[3] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 3)
    if chickenIndex == 13:
        if(solution[1] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 1)
    if chickenIndex == 17:
        if(solution[2] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 2)

    if nextDirectionIndex == 7:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 0)
        return solution
    if nextDirectionIndex == 11:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 3)
        return solution
    if nextDirectionIndex == 13:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 1)
        return solution
    if nextDirectionIndex == 17:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 2)
        return solution


def printMovement(chickensStepBefore, chickenIndex, liste):
    time.sleep(0.5)

    testliste = liste
    for i in range(len(testliste)):
        if testliste[i] == 'L':
            testliste[i] = ' '

    if(testliste[chickensStepBefore] != liste[chickensStepBefore]):
        testliste[chickensStepBefore] == liste[chickensStepBefore]

    testliste[chickenIndex] = 'O'
    testliste[chickensStepBefore] = ' '
    for i in range(0, len(testliste), 5):
        print(testliste[i:i+5])
    print()

def replace_Element_with_whiteSpace_and_append_ElementIndex(solution, index):
    solution[index] = ''
    solution.append(index)
    return solution

def one_fiels_has_SnackMan(liste):
    return "SM" in liste

def replace_SM(liste):
    print("Snackman wurde ersetzt... ")
    return replace_first_element(liste, "SM")

def eliminate_walls_as_options(liste):
    return ['X' if item =='W' else item for item in liste]

def do_all_square_have_ghosts(liste):
    return all('G' == element or 'X' == element for element in liste)

def replace_first_g(liste):
    return replace_first_element(liste, "G")

# liste element with " " is the new square the chicken moves to
def replace_first_element(liste, toReplace):
    new_liste = [liste[0]] + liste[1:]
    first_g_index = next((i for i, x in enumerate(new_liste) if x == toReplace), None)
    if first_g_index is not None:
        new_liste[first_g_index] = ' '
    return new_liste

# sets direction of first ' ' in liste items
def add_direction(original_liste):
    new_liste = [original_liste[0]] + original_liste[1:]
    first_empthy_index = next((i for i, x in enumerate(new_liste) if x == ' '), None)
    result = new_liste + [first_empthy_index]
    return result

def all_square_have_snack(original_liste):
    return set(original_liste).issubset({'S', 'X'})

def choose_random_snack(original_liste):
    return replace_first_element(original_liste, "S")

def at_least_one_square_with_snack(original_liste):
    return "S" in original_liste

def at_least_one_square_with_ghost(original_liste):
    return "G" in original_liste

def choose_a_snack_away_from_ghost(original_liste):
    north_square, east_square, south_square, west_square = original_liste
    # check if there are ghosts, if not -> choose snack
    if not at_least_one_square_with_ghost(original_liste):
        if at_least_one_square_with_snack:
            # choose snack
            return replace_first_element(original_liste, "S")
        else:
            # choose empthy space
            return replace_first_element(original_liste, "L")
    # alle gegenüber der G anschauen
    # wenn S da: dort hingehen
    if (north_square == "G" and south_square == "S") or (north_square == "S" and south_square == "G"):
        if north_square == "G" and south_square == "S":
            return [north_square, east_square, " ", west_square]
        if north_square == "S" and south_square == "G":
            return [" ", east_square, south_square, west_square]
    elif (west_square == "G" and east_square == "S") or (west_square == "S" and east_square == "G"):
        if west_square == "G" and east_square == "S":
            return [north_square, " ", south_square, west_square]
        if west_square == "S" and east_square == "G":
            return [north_square, east_square, south_square, " "]
    # wenn L da: weiter nach S ggü von G suchen
    return choose_square_without_snack_away_from_ghost(north_square, east_square, south_square, west_square)

def choose_square_without_snack_away_from_ghost(north_square, east_square, south_square, west_square):
    if (north_square == "G" and south_square == "L") or (north_square == "L" and south_square == "G"):
        if north_square == "G" and south_square == "L":
            return [north_square, east_square, " ", west_square]
        if north_square == "L" and south_square == "G":
            return [" ", east_square, south_square, west_square]
    elif (west_square == "G" and east_square == "L") or (west_square == "L" and east_square == "G"):
        if north_square == "G" and south_square == "L":
            return [north_square, east_square, " ", west_square]
        if north_square == "L" and south_square == "G":
            return [" ", east_square, south_square, west_square]
    # wenn nichts gefunden: irgendein L nehmen
    return replace_first_element([north_square, east_square, south_square, west_square], "L")

def choose_random_square(original_liste):
    return replace_first_element(original_liste, "L")

liste = ["W", "W", "W", "L", "W",
                    "SM", "L", "L", "L", "L",
                    "L", "W", "H", "W", "L",
                    "L", "W", "L", "W", "L",
                    "L", "W", "L", "W", "L"]

liste1 = ["W", "W", "W", "L", "W",
                    "SM", "L", "W", "L", "L",
                    "L", "L", "H", "W", "L",
                    "L", "W", "L", "W", "L",
                    "L", "W", "L", "W", "L"]

liste2 = ["W", "W", "L", "L", "W", #darf nichts zurückgeben, weil es keinen weg gibt
                    "SM", "L", "W", "L", "L",
                    "L", "W", "H", "L", "L",
                    "L", "W", "L", "W", "L",
                    "L", "W", "L", "W", "L"]

liste3 = ["W", "W", "L", "L", "W",
                    "SM", "L", "W", "L", "W",
                    "L", "L", "H", "L", "L",
                    "L", "W", "L", "W", "L",
                    "L", "W", "L", "W", "L"]


print("Ergebnis: ", choose_next_square(liste3))