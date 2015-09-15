##Sandbox-Manager

Der SandboxManager läuft in einer virtuellen Maschine auf dem Worker mit einem
Host-only-Netzwerk. Er verbindet sich mit dem Worker und nimmt Befehle von
selbigem entgegen. Um die KIs, die vom SandboxManager ausgeführt werden, vom
restlichen System zu trennen, wird isolate verwendet. *Dafür muss isolate auf
dem Rechner installiert sein*. Um an den Inhalt der KIs und Bibliotheken zu
kommen, benutzt der SandboxManager den MirrorServer des Workers.
