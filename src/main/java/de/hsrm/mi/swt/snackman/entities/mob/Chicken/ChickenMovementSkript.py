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
#     e.g. if the solution list is [W,G, ,W,2] the chicken is walking in south direction and looking into south direction too

# todo: nimmt gerade immer die erste Option die es findet, kann man noch random machen
#testingList = ["W", "W", "G", "G", "W", "G", "G", "W", 1]
#testingList = ["W", "S", "G", "S", "W", "G", "G", "W", 1]
#testingList = ["W", "G", "G", "S", "W", "S", "G", "W", 1]
testingList = ["W", "L", "G", "L", "W", "G", "G", "W", 1]

def choose_next_square(squares_list):
      northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, direction = squares_list
      solution_list = [north_square, east_square, south_square, west_square]
      # make sure you cannot walk into a wall
      solution_list = eliminate_walls_as_options(solution_list)
      # make sure you do not walk into a ghost
      if(do_all_square_have_ghosts(solution_list)):
            list_without_g = replace_first_g(solution_list)
            return add_direction(list_without_g)
      # choose square with snack
      if(all_square_have_snack(solution_list)):
           # choose random snack
           return add_direction(choose_random_snack(solution_list))
      elif(at_least_one_square_with_snack(solution_list)):
           # choose a square with snack (far away from ghost)
           return add_direction(choose_a_snack_away_from_ghost(solution_list))
      elif(at_least_one_square_with_ghost(solution_list)):
           # choose square without snack, away from ghosts
           north, east, south, west = solution_list
           return add_direction(choose_square_without_snack_away_from_ghost(north, east, south, west))
      else:
           # choose random square, no snacks there + no ghosts
           return add_direction(choose_random_square(solution_list))

def eliminate_walls_as_options(list):
      return ['X' if item =='W' else item for item in list]

def do_all_square_have_ghosts(list):
      return all('G' == element or 'X' == element for element in list)

def replace_first_g(list):
    return replace_first_element(list, "G")

# list element with " " is the new square the chicken moves to
def replace_first_element(list, toReplace):
    new_list = [list[0]] + list[1:]
    first_g_index = next((i for i, x in enumerate(new_list) if x == toReplace), None)
    if first_g_index is not None:
        new_list[first_g_index] = ' '
    return new_list

# sets direction of first ' ' in list items
def add_direction(original_list):
    new_list = [original_list[0]] + original_list[1:]
    first_empthy_index = next((i for i, x in enumerate(new_list) if x == ' '), None)    
    result = new_list + [first_empthy_index]
    return result

def all_square_have_snack(original_list):  
     return set(original_list).issubset({'S', 'X'})

def choose_random_snack(original_list):
     return replace_first_element(original_list, "S")

def at_least_one_square_with_snack(original_list):
     return "S" in original_list

def at_least_one_square_with_ghost(original_list):
     return "G" in original_list

def choose_a_snack_away_from_ghost(original_list):
      north_square, east_square, south_square, west_square = original_list
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

def choose_random_square(original_list):
     return replace_first_element(original_list, "L")

print(choose_next_square(testingList))