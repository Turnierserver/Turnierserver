Server, der für die Koordination der Sandbox-Server und das Starten und Ausführen von Spielen verantwortlich ist.
Sprache: Java

Der Server kommuniziert mit Frontend und/oder ESU, die dem Backend-Server das Signal zum Starten eines Spieles geben. Der
Server sorgt dann dafür, dass er die aktuellste Version der ausgewählten Spiellogik hat, und besorgt sich diese bei Bedarf
vom Datastore. Anschließend schickt er die KIs an nach der aktuellen Beschäftigung ausgewählte Sandbox-Server, die die KIs
ausführen und deren Züge an den Backend-Server schicken. Anschließend wird das Spiel auf dem Datastore abgelegt und Frontend
bzw. ESU erhalten das Signal, dass das Spiel fertig ist.
