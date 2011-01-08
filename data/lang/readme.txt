Um eine neue Sprache zu pxchat hinzuzufügen ist es nötig eine neue Datei in
diesem Ordner (./data/lang/) anzulegen.

Sie beginnt mit "Messages" und endet mit ".properties.xml". Der mittlere Teil
ist von der zu implementierenden Sprache und dem zugehörigen Land abhängig. Für
amerikanisches englisch lautet er "en_US". Die genaue Spezifikation ist unter

http://download.oracle.com/javase/1.4.2/docs/api/java/util/ResourceBundle.html

bzw.

http://download.oracle.com/javase/1.4.2/docs/api/java/util/Locale.html

zu finden. Die Datei selbst ist ein XML-Dokument mit folgendem DOCTYPE:

<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">

Das Wurzelelement ist <properties>, welches mehrere <entry> Tags enthalten kann.
Diese wiederum besitzen das Attribut "key" welches das Schlüsselwort der zu
übersetzenden Zeichenkette enthält. Der Inhalt des Tags ist der Wert, also der
übersetzte Text. Um also eine Zeichenkette für den Text mit dem Schlüssel
"language" zu übersetzen ist folgende Zeile notwendig:

<entry key="language">Sprache</entry>

Um pxchat vollständig zu übersetzen ist es notwendig alle Schlüsse-Wert Paare
aus der Datei "Messages.properties.xml" zu übersetzen. Ein fehlender Eintrag
wird durch pxchat mit dem Standardtext ersetzt.

Das Programm startet automatisch in der Systemsprache, oder englisch, falls
diese nicht vorhanden ist. Die neu hinzugefügte Übersetzung wird im Falle der
korrekten Verwendung des Dateinamens nach einem Neustart von pxchat in der
Menüstruktur angezeigt. Sollte dies nicht der Fall sein, so ist entweder der
Dateiname falsch, oder der Inhalt der XML-Datei ist nicht korrekt.


