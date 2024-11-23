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

# todo: nimmt gerade immer die erste Option die es findet, kann man noch random machen
#testingliste = ["W", "W", "G", "G", "W", "G", "G", "W", 1]
#testingliste = ["W", "S", "G", "S", "W", "G", "G", "W", 1]
#testingliste = ["W", "G", "G", "S", "W", "S", "G", "W", 1]
#testingliste = ["W", "L", "G", "L", "W", "G", "G", "W", 1]

def choose_next_square(squares_liste):
      squares_liste = list(squares_liste)
      print("Python chicken skript working with list of:")
      print(squares_liste)

      northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, direction = squares_liste
      solution_liste = [north_square, east_square, south_square, west_square]
      # make sure you cannot walk into a wall
      solution_liste = eliminate_walls_as_options(solution_liste)
      # make sure you do not walk into a ghost
      if(do_all_square_have_ghosts(solution_liste)):
            liste_without_g = replace_first_g(solution_liste)
            return add_direction(liste_without_g)
      # choose square with snack
      if(all_square_have_snack(solution_liste)):
           # choose random snack
           return add_direction(choose_random_snack(solution_liste))
      elif(at_least_one_square_with_snack(solution_liste)):
           # choose a square with snack (far away from ghost)
           return add_direction(choose_a_snack_away_from_ghost(solution_liste))
      elif(at_least_one_square_with_ghost(solution_liste)):
           # choose square without snack, away from ghosts
           north, east, south, west = solution_liste
           return add_direction(choose_square_without_snack_away_from_ghost(north, east, south, west))
      else:
           # choose random square, no snacks there + no ghosts
           return add_direction(choose_random_square(solution_liste))

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