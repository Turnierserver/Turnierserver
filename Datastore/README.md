Server, der für das Speichern der Daten zuständig ist.
Sprache: (vorraussichtlich) Java
Enthält außerdem einen (My)SQL-Server für die Benutzerdatenbank, insofern dieser nicht das PMS übernimmt.

Der Server speichert die folgenden Elemente ab:
- KIs
- Spiellogik
- Spieldaten
- wenn nicht PMS, Benutzerdatenbank

Anschließend schickt er diese Daten bei Bedarf an Frontend und Backend. Die ESU kann sich die Daten nur über das
Frontend runterladen.
