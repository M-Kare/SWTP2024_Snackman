
def process_translation_entries(translation_entries, languages, language_dicts):
    '''
    Durchläuft jede Zeile mit Übersetzungen:
        1. entfernt Whitespace & Kommentarzeilen
        2. zerlegt Zeile in key und values
        3. zip() kombiniert Elemente aus languages & values mit gleichem Index in einem Tupel
            - alle Tupel werden durchlaufen und in Variablen language & value aufgeteilt
            - das Key-value-Pair wird formatiert und der Liste der entsprechenden Sprache angehängt
    '''
    
    for line in translation_entries:
        
        # 1.
        line = line.replace("\n", "").strip()
        if line == "" or line.startswith("#"):
            continue

        # 2.
        line_attr = line.split(";")
        key = line_attr[0]
        values = line_attr[1:]

        # 3
        for language, value in zip(languages, values):
            translation_string = f"{key}={value}"
            language_dicts[language].append(translation_string)


def write_property_file(language, translation_lists):
    '''
    Erstellt / überschreibt .properties-File:
        Key = property
        Value = Übersetzung
    '''
    
    with open(f"./src/main/resources/languages/{language}_translation.properties", "w") as propertyFile:
        for line in translation_lists:
            propertyFile.write(line + "\n")


def create_standard_properties(languages):
    '''
    Erstellt standardLanguage.properties anhand der Werte der 1. Sprache der CSV-Datei
    -> kopiert Datei und speichert unter anderem Namen
    '''
    
    standard_lang = languages[0]
    with open(f"./src/main/resources/languages/{standard_lang}_translation.properties", "r") as sourceFile:
        with open("./src/main/resources/languages/standard_language.properties", "w") as fallbackFile:
            fallbackFile.write("# Standard properties \n")
            fallbackFile.write(sourceFile.read())


def main():
    # Datei einlesen
    file_path = "src/main/resources/static/translations.csv"
    with open(file_path, "r") as translations:
        content = translations.readlines()

    # Sprachen aus Header-Zeile extrahieren
    header_line_attr = content[0].replace("\n", "").split(";")
    languages = header_line_attr[1:]

    # Dict, da Key-Value-Pairs verarbeitet werden
    # Für jede Sprache wird leere Liste erstellt, die im nächsten Schritt gefüllt wird
    language_dict = {}
    for language in languages:
        language_dict[language] = []

    # Zeilen mit Übersetzungen verarbeiten & somit die translationLists füllen
    translation_entries = content[1:]
    process_translation_entries(translation_entries, languages, language_dict)

    # .properties-Dateien für jede Sprache erstellen
    for language in language_dict:
        write_property_file(language, language_dict[language])

    # Fallback-Datei erstellen
    create_standard_properties(languages)

main()