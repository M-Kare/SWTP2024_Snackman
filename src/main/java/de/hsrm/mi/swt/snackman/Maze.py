import random

def erstelleLabyrinth(width, height):
    laby = [["#" for i in range(width)] for i in range(height)] #erstmal alles #
    stack = [(1, 1)]
    laby[1][1] = " "

    while stack: #läuft so lange, wie es Elemente im Stack gibt
        x, y = stack[-1]
        richtungen = [(0, 2), (2, 0), (0, -2), (-2, 0)]
        random.shuffle(richtungen)#in jedem Durchlauf neu shuffeln, damit richtungen zufällig festgelegt werden

        for rx, ry in richtungen:
            aktx, akty = x + rx, y + ry
            if 1 <= aktx < width - 1 and 1 <= akty < height -1 and laby[akty][aktx] == "#":
                laby[akty][aktx] = " "
                laby[y + ry//2][x + rx//2] = " "
                stack.append((aktx, akty))
                break
        else:#else einer for schleife wird ausgeführt, wenn Schleife nicht curch break verlassen wurde
            stack.pop()

    for y in range(len(laby)):
        for x in range(len(laby[y])):
            if(width/3 < x < 2*width/3 and height/3 < y <(2*height/3)):
                laby[y][x] = " "

    return laby

def speicherInFile(laby, filename="mini-maze.txt"):
    with open(filename, "w") as file:
        for row in laby:
            file.write("".join(row) + "\n")

width, height = 15, 15
laby = erstelleLabyrinth(width, height)
#laby = Labyrinth(width, height)
speicherInFile(laby)
print("Labyrinth fertig")