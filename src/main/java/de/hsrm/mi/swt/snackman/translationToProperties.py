
def processBodyLines(bodyLines, languages, translationLists):
    '''
    Durchläuft jede Zeile mit Übersetzungen:
        1. entfernt Whitespace & Kommentarzeilen
        2. zerlegt Zeile in einzelne Attribute
        3.1 für jede Sprache werden die übersetzten Werte extrahiert
        3.2 die übersetzten Werte, werden mit dem PropertyKey verbunden und danach der translationList der aktuellen Sprache hinzugefügt
    '''
    
    for line in bodyLines:
        
        # 1.
        line = line.replace("\n", "").strip()
        if line == "" or line.startswith("#"):
            continue

        # 2.
        lineAttr = line.split(";")
        propertyKey = lineAttr[0]
        propertyValues = lineAttr[1:]

        # 3.1
        for langIndex in range(len(languages)):
            currLang = languages[langIndex]
            currPropValue = propertyValues[langIndex]
            #3.2
            translationString = f"{propertyKey}={currPropValue}"
            translationLists[currLang].append(translationString)


def writePropertyFile(language, translationLists):
    '''
    Erstellt / überschreibt .properties-File:
        Key = property
        Value = Übersetzung
    '''
    
    with open(f"./src/main/resources/languages/{language}_translation.properties", "w") as propertyFile:
        for line in translationLists:
            propertyFile.write(line + "\n")


def createStandardProperties(languages):
    '''
    Erstellt standardLanguage.properties anhand der Werte der 1. Sprache der csv
    -> kopiert Datei und speichert unter anderem Namen
    '''
    
    standardLanguage = languages[0]
    with open(f"./src/main/resources/languages/{standardLanguage}_translation.properties", "r") as sourceFile:
        with open(f"./src/main/resources/languages/standardLanguage.properties", "w") as fallbackFile:
            fallbackFile.write("# Fallback properties \n")
            fallbackFile.write(sourceFile.read())


def main():
    # Datei einlesen
    filePath = "src/main/resources/static/translations.csv"
    with open(filePath, "r") as translations:
        linesInCSV = translations.readlines()

    # Sprachen aus Header-Zeile extrahieren
    headerLineAttr = linesInCSV[0].replace("\n", "").split(";")
    languages = headerLineAttr[1:]

    # Dict, da Key-Value-Pairs verarbeitet werden
    # Für jede Sprache wird leere Liste erstellt
    translationLists = {}
    for currLang in languages:
        translationLists[currLang] = []

    # Zeilen mit Übersetzungen verarbeiten & somit die translationLists füllen
    bodyLines = linesInCSV[1:]
    processBodyLines(bodyLines, languages, translationLists)

    # .properties-Dateien erstellen
    for language in translationLists:
        writePropertyFile(language, translationLists[language])

    # Fallback-Datei erstellen (1. Sprache der CSV = Standard)
    createStandardProperties(languages)


main()