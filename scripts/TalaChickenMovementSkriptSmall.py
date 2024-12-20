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
#    returns: [north_square, east_square, south_square, west_square, indexOfNextPosition]

import random
import time
from ChickenMovementSkript import choose_next_square as defaultBehavior

# todo: nimmt gerade immer die erste Option die es findet, kann man noch random machen
#testingliste = ["W", "W", "G", "G", "W", "G", "G", "W", 1]
#testingliste = ["W", "S", "G", "S", "W", "G", "G", "W", 1]
#testingliste = ["W", "G", "G", "S", "W", "S", "G", "W", 1]
#testingliste = ["W", "L", "G", "L", "W", "G", "G", "W", 1]
#testingliste = ["W","S","W","L","W","L","W","L","1"]

def choose_next_square(squares_liste):
    squares_liste = list(squares_liste)

    two_North_two_West_square, two_North_one_West_square, two_North_square, two_North_one_East_square, two_North_two_East_square = squares_liste[:5]
    one_North_two_West_square, one_North_one_West_square, one_North_square, one_North_one_East_square, one_North_two_East_square = squares_liste[5:10]
    two_West_square, one_West_square, chickens_square, one_East_square, two_East_square = squares_liste[10:15]
    one_South_two_West_square, one_South_one_West_square, one_South_square, one_South_one_East_square, one_Soutn_two_East_square = squares_liste[15:20]
    two_South_two_West_square, two_South_one_West_square, two_South_square, two_South_one_East_square, two_Soutn_two_East_square = squares_liste[20:25]

    solution_liste = [one_North_square, one_East_square, one_South_square, one_West_square]

    #northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, direction = squares_liste
    #solution_liste = [north_square, east_square, south_square, west_square]

    # make sure you cannot walk into a wall
    solution_liste = eliminate_walls_as_options(solution_liste)

    if(one_fiels_has_SnackMan(solution_liste)):
        if(len(squares_liste) > 9):
            print("--------------------------")
            return choose_next_square_to_get_to_SnackMan()
        liste_without_SM = replace_SM(solution_liste)
        return add_direction(liste_without_SM)
    
    defaultBehavior(squares_liste)

def choose_next_square_to_get_to_SnackMan(squares_liste):
    """Checks if Snackman is in chickens field of view and returns solution list, with next step in direction to Snackman"""
    #generates solutionList with Indexes next to Chicken(because Chicken just can go one step
    solution_liste = [squares_liste[7], squares_liste[11], squares_liste[13], squares_liste[17]]
    if "SM" in squares_liste:
        solution_liste =  findWayToSM(solution_liste, squares_liste)
        return solution_liste
        #print(solution_liste)
    else:
        return add_direction(choose_random_square(solution_liste))

def findWayToSM(solution, liste):
    """Finds Way to Snackman and returns solution_list with next step in direction to Snackman"""
    Chickenindex = 12
    directionListe =  [-5, +5, -1, +1]
    chickensStepisSet = False
    nextDirectionIndex = 12
    chickensStepBefore = 0
    versuche = 0
    stack = []
    anzahlZurueckGesetzt = 0

    while liste[Chickenindex] != 'SM':
        versuche += 1
        stack.append(Chickenindex)

        if(anzahlZurueckGesetzt >= 10): #falls Snackman nicht erreichbar ist
            return solution

        #time.sleep(0.5)

        testliste = liste
        for i in range(len(testliste)):
            if testliste[i] == 'L':
                testliste[i] = ' '

        if(testliste[chickensStepBefore] != liste[chickensStepBefore]):
            testliste[chickensStepBefore] == liste[chickensStepBefore]

        testliste[Chickenindex] = 'O'
        #for i in range(0, len(testliste), 9):
         #   print(testliste[i:i+9])
        #print()

        zahl = random.randint(0, 3)
        if(0 <= Chickenindex + zahl <= 24):
            if 0 <= (Chickenindex + directionListe[zahl]) <= 24 and (Chickenindex + directionListe[zahl]) != chickensStepBefore: #neuer index muss innerhalb des Sichtfeldes(liste) liegen -- und darf nicht der letzte Indes sein
                if liste[(Chickenindex + directionListe[zahl])] != 'W': #neuer index darf keine Wand sein
                    Chickenindex += directionListe[zahl]
                    if(not chickensStepisSet):
                        nextDirectionIndex = Chickenindex
                        chickensStepisSet = True
                    if 0 > Chickenindex or Chickenindex > 24:
                        Chickenindex = 12
                        anzahlZurueckGesetzt += 1
                        chickensStepBefore = 12
                        chickensStepisSet = False
                        continue
        else:
            Chickenindex = 12
            anzahlZurueckGesetzt += 1
            chickensStepBefore = 12
            chickensStepisSet = False
    
    print("Snackman ist hier???: Index ", Chickenindex)

    #print("Snackman gefunden!!! auf Feld: ",  Chickenindex, "Nächster Schritt: ", nextDirectionIndex)
    print("Verscuhe: ",  versuche, "Gegangene Schritte: ", [int for int in stack])
    print("Anz zurueckgesetzt: ", anzahlZurueckGesetzt)

    print("NewDirectionIndex ", nextDirectionIndex)
    if Chickenindex == 7:#31
        if(solution[0] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 0)
    if Chickenindex == 11:#39
        if(solution[1] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 1)
    if Chickenindex == 13:#41
        if(solution[2] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 2)
    if Chickenindex == 17:#49
        if(solution[3] == 'SM'):
            solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 3)

    if nextDirectionIndex == 7:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 0)
        return solution
    if nextDirectionIndex == 11:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 1)
        #solution[1] = ''
        return solution
    if nextDirectionIndex == 13:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 2)
        #solution[2] = ''
        return solution
    if nextDirectionIndex == 17:
        solution = replace_Element_with_whiteSpace_and_append_ElementIndex(solution, 3)
        #solution[3] = ''
        return solution

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

#print(choose_next_square(testingliste))
#print(choose_next_square(["W", "L", "W", "L", "W", "L", "W", "L", "L"]))
#print(choose_next_square(["W", "SM", "W", "L", "W", "L", "W", "L", "0"]));

liste = ["W", "W", "W", "W", "L", "W", "W", "W", "W",
"W", "SM", "L", "L", "W", "L", "W", "L", "W",
"W", "W", "L", "W", "W", "L", "L", "L", "W",
"L", "L", "L", "L", "W", "L", "W", "W", "W",
"W", "L", "W", "SM", "H", "L", "W", "L", "W",
"W", "L", "W", "L", "W", "L", "W", "L", "L",
"W", "L", "W", "L", "W", "W", "W", "L", "W",
"W", "W", "W", "L", "L", "W", "W", "W", "W",
"W", "W", "W", "W", "L", "W", "W", "W", "W"]

liste2 = ["W", "W", "W", "W", "W", "W", "W", "W", "W",
         "W", "SM", "L", "L", "W", "L", "L", "L", "W",
         "W", "W", "W", "L", "W", "L", "W", "L", "W",
         "W", "L", "W", "L", "W", "L", "W", "W", "W",
         "W", "L", "W", "L", "H", "L", "L", "L", "L",
         "W", "L", "W", "L", "W", "L", "W", "L", "L",
         "W", "L", "W", "L", "W", "L", "W", "L", "W",
         "W", "L", "W", "L", "L", "L", "W", "L", "W",
         "W", "L", "W", "L", "W", "W", "W", "L", "W"]

liste3 = ["W", "W", "W", "W", "W", "W", "W", "W", "W",
          "W", "SM", "W", "L", "W", "L", "L", "L", "W",
          "W", "W", "W", "L", "W", "L", "W", "L", "W",
          "W", "L", "W", "L", "W", "L", "W", "W", "W",
          "W", "L", "W", "L", "H", "L", "L", "L", "L",
          "W", "L", "W", "L", "W", "L", "W", "L", "L",
          "W", "L", "W", "L", "W", "L", "W", "L", "W",
          "W", "L", "W", "L", "L", "L", "W", "L", "W",
          "W", "L", "W", "L", "W", "W", "W", "L", "W"]

liste4 = ["W", "W", "W", "L", "W",
                    "SM", "L", "L", "L", "L",
                    "L", "W", "H", "W", "L",
                    "L", "W", "L", "W", "L",
                    "L", "W", "L", "W", "L"]

print("Ergebnis: ", choose_next_square_to_get_to_SnackMan(liste4))