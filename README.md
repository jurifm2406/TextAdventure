# Tower of Text
## Beschreibung
Tower of Text ist ein textbasierter roadlike Dungeon crawler. 
Es kann durch Ausführen der Textadventure.jar Datei gestartet werden.


## Überblick
In Tower of Text gibt der Spieler dem Spiel durch Textbefehle Anweisungen. Man bewegt sich durch ein zufällig generiertes Dungeon, 
sammelt Waffen, Rüstungen, Verbrauchsgegenstände und Goldmünzen und bekämpft verschiedene Monster.
Der Dungeon besteht aus mehreren Ebenen, welche immer stärkere Gegenstände und Gegner enthalten.
Von den Coins kann man sich in einem Shop die Gegenstände einer Ebene kaufen.
Man findet Gegenstände auch im ersten Raum des Dungeon und in sogenannten "Chest rooms".
Um eine Ebene aufzusteigen, muss man sich in einem sogenannten "End room" befinden.


## GUI Erklärung
<img src="res/GUI.png" alt="UI" width="500">


Im unteren Teil befindet sich das Texteingabefeld, hier gibt man die Textbefehle ein.
Im linken Teil des Fensters befindet sich das Ausgabefeld, in diesem gibt das Spiel Informationen zurück.
Im oberen rechten Teil befindet sich eine Karte der Ebene, welche alle bekannten Räume zeigt.
Ein raum gilt als bekannt, wenn man einmal in einem Nachbar des Raums oder dem Raum selbst war.
Das o zeigt den Raum an in dem man sich gerade befindet.
Der untere linke Teil besteht aus den Inventarinformationen, sowie der Ebenen-, Lebens und Goldcoin-Information
Ein x markiert den ausgerüsteten Gegenstand.


## Besondere Räume

In Tower of Text existieren besondere Räume in Sackgassen, diese können der end Room sein um auf eine neue Ebene zu gelangen, ein shop room in dem man Gegenstände kaufen und verkaufen kann oder ein chest Room in dem Gegenstände liegen.


## Spielmechanik & Befehle

## Bewegung 

### move
Der Befehl `move [direction]` ermöglicht es, den Charakter in eine bestimmte Richtung innerhalb des Dungeons zu bewegen. Dabei muss als Parameter eine Richtung angegeben werden, die entweder `north`, `east`, `south` oder `west` sein kann.

### climb
Der Befehl `climb` ist speziell für den Einsatz in sogenannten End-Räumen konzipiert, in denen man sich befindet, wenn man bereit ist, zur nächsten Ebene des Dungeons aufzusteigen.


## Inventar & Gegenstände

### pickup
Mit dem Befehl `pickup [item class] [item id]` kann man einen Gegenstand aus dem aktuellen Raum aufnehmen und in das Inventar übernehmen. Der Parameter `[item class]` steht für die Art des Gegenstands, beispielsweise `weapon` oder `armor`, während `[item id]` den spezifischen Gegenstand identifiziert.

### drop 
Der Befehl `drop [item class] [item id]` erlaubt es, einen Gegenstand aus dem Inventar wieder im aktuellen Raum abzulegen.


### equip 
Der Befehl `equip [item class] [item id]` dient dazu, einen Gegenstand aus dem Inventar als aktive Ausrüstung zu verwenden. Mit diesem Befehl kann man beispielsweise eine Waffe oder Rüstung anlegen, um den Charakter im Kampf zu stärken und zu schützen. Es muss angegeben werden, welcher Gegenstand aus der Kategorie `[item class]` und mit welcher spezifischen ID ausgerüstet werden soll.

### unequip 
Mit dem Befehl `unequip [item class]` hat man die Möglichkeit, einen aktuell ausgerüsteten Gegenstand wieder aus der aktiven Ausrüstung zu entfernen. Die Angabe `[item class]`, beispielsweise `weapon` oder `armor`, sorgt dafür, dass das Spiel weiß, welcher Gegenstand abgenommen werden soll. Nachdem der Befehl ausgeführt wurde, wird der Gegenstand in das Inventar zurückgelegt und steht weiterhin zur Verfügung.

### use 
Der Befehl `use [consumable id]` ermöglicht es, einen Verbrauchsgegenstand aus dem Inventar zu aktivieren. Verbrauchsgegenstände können z.B. Heiltränke sein. Durch die Angabe der eindeutigen ID des Verbrauchsgegenstands wird dieser aktiviert und seine Effekte treten in Kraft. Es ist wichtig zu beachten, dass einige Verbrauchsgegenstände auch negative Nebenwirkungen haben können, wenn sie falsch verwendet werden.


## Raum-Interaktionen

### inspect
Der Befehl `inspect` erlaubt es, den aktuellen Raum genauer zu untersuchen. Beim Ausführen dieses Befehls erhält man detaillierte Informationen über die Gegenstände aus dem Raum, in dem man sich befindet.


## Shop-Interaktionen

### shop sell 
Der Befehl `shop sell [item class] [item id]` ist ausschließlich in Shop Räumen verfügbar und ermöglicht es, einen Gegenstand aus dem Inventar zu verkaufen. Dadurch erhält man Gold, welches später im Shop für den Kauf anderer Gegenstände eingesetzt werden kann. Die Parameter `[item class]` und `[item id]` bestimmen dabei genau, welcher Gegenstand verkauft werden soll.

### shop buy 
Mit dem Befehl `shop buy [item class] [item id]` kann man in einem Shop einen Gegenstand erwerben. Dabei werden Goldmünzen gegen einen spezifischen Gegenstand eingetauscht

### shop info
Der Befehl `shop info` liefert eine Übersicht über alle im Shop verfügbaren Gegenstände, jeder Gegenstand kostet 150 Coins.


## Kampfbefehle
Die Kämpfe sind Rundenbasiert. Innerhalb einer Runde hat man eine bestimmte Anzahl von Aktionspunkten. Beendet man seinen Zug greift der Gegner an. Besiegt man den Gegner erhält man Coins. Stirbt man während des Kampfs wird das Spiel zurückgesetzt.

### attack

Der Befehl `attack` ermöglicht es, einem Gegner direkten Schaden zuzufügen. Die Höhe des Schadens richtet sich dabei nach der aktuell ausgerüsteten Waffe, und es werden Aktionspunkte verbraucht, die von den Eigenschaften der Waffe abhängen. Hierbei kann auch zufällig eine Möglichkeit für kritischen Schaden ausgelöst werden, hierbei muss man so schnell wie möglich eine zufällige Reihenfolge von Buchstaben eintippen und erhält basierend auf seiner Genauigkeit und Zeit einen Schadensmultiplikator.


### defend

Mit dem Befehl `defend` wird die defensive Stellung des Charakters verbessert. Dieser Befehl aktiviert die Schutzwirkung der getragenen Rüstung, wodurch ein Teil des eingehenden Schadens absorbiert wird. Es werden Aktionspunkte verbraucht, die von den Eigenschaften der Rüstung abhängen.


### use

Der Befehl `use [item ID]` dient dazu, einen Verbrauchsgegenstand im Kampf einzusetzen. Verbrauchsgegenstände können zum Beispiel Heiltränke sein. Beim Ausführen dieses Befehls muss man die spezifische ID des Verbrauchsgegenstands angeben, um ihn korrekt zu aktivieren.


### escape

Der Befehl `escape` erlaubt es, während eines Kampfes zu versuchen, der Auseinandersetzung zu entkommen. Allerding besteht auch die Möglichkeit, dass der Fluchtversuch fehlschlägt, ist dies der Fall macht der Gegner dreifachen Schaden im nächsten Zug. Der Fluchtversuch kostet ebenfalls Aktionspunkte.


### end

Der Befehl `end` beendet den eigenen Zug während eines Kampfes. Mit diesem Befehl signalisiert man dem Spiel, dass keine weiteren Aktionen in diesem Zug durchgeführt werden sollen. 

Alle Befehle sind auch über den help command im Spiel erklärt. Wobei die Kampf Befehle nur innerhalb eines Kampfes angezeigt werden.


## Bug list

- Effekte sind hardcoded, es existiert kein einheitliches System zur Verarbeitung
- Effekte werden teilweise nicht dem Spieler gemeldet
- Map Table text nicht perfekt mittig

## used tools

- kotlin
- IntelliJ IDEA
- swing
- gitHub

## project structure

- rigid separation into model, view, controller (see below)
- main class launches controller, which in turn launches view and model
- all communication according to diagram
- view is separated according to major components which extend the classes provided by swing
- model and View have class structure of multiple subclasses Controller is its own class
- model class contains a map hero and the Data for the UI-info
- the model also contains the object base containing items, entities and other
- the View contains UI components in separate classes and exposes APIs for the controller

<img src="res/mvc.png" alt="model-view-controller concept" width="400">

## Development

- development in spiral model
- implementing new features and testing them until they work and fit into game
- no task separation between developers because project is very interconnected, changes in one part effect other parts
  drastically

## Features

- UI
    - menu bar (top)
    - text input (bottom)
    - text output/history (center)
    - map (top right)
    - inventory/status (bottom right)
    - <img src="res/UI.png" alt="ui-sketch" width="500">
- random map generation
    - rooms
        - NPCs (non-playable character)
            - monsters
            - bosses?
            - other heroes?
        - chests
        - minigames to unlock next rooms?
- movement between rooms
- items
    - keys?
    - weapons
    - armor/other (backpacks etc.)
    - consumables?
        - potions with effects
        - healing
- combat
    - attacks
    - evasions/other options?
        - having to provide an input in a specific time
    - rewards after defeating enemies
- progression?
    - locked doors
    - reaching of new floors with harder enemies
    - story/at least context

point marked with ? considered nice to have but optional

## Roadmap

- [X] UI 15.1.
- [X] map generation 27.1
- [X] movement 22.1.
- [X] items and entities in rooms 31.1.
- [X] simple combat 10.2.
- [ ] advanced combat 24.2.
- [ ] progression 10.3.
