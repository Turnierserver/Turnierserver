##GameBuilder

Dieses Programm liest eine Datei mit Informationen zum Bauen und Hochladen eines Spiels ein und baut dann die Spiellogik sowie alle Ai-Bibliotheken und läst diese hoch. Hier ist ein Beispiel für die `game.txt`-Datei:

    NAME = Beisiel
    VERSION = 1.0
    
    # die id des Spiels auf dem Server
    GAMEID = 2
    
    # den Code mit Debug-Optionen bauen
    DEBUG = true
    
    # die Spiellogik bauen
    logic {
        FILES += com/example/mypackage/Logic.java
        LOGICCLASS = com.example.mypackage.Logic
    }
    
    # die Java Bibliothek bauen
    java {
        FILES += com/example/mypackage/Lib.java
    }
