# text adventure part 2 (title wip)

## used tools

- kotlin
- swing
- (git)

## project structure

- rigid separation into model, view, controller (see below)
- main class launches controller, which in turn launches view and model
- all communication according to diagram
- view is separated according to major components which extend the classes provided by swing

<img src="res/mvc.png" alt="model-view-controller concept" width="400">

## Development
Development in spiral model
implementing new features and testing them until they work and fit into game 

## Features
- UI
  - menu bar (top)
  - text input (bottom)
  - text output/history (center)
  - map (top right)
  - inventory/status (bottom right)
- random map generation
  - rooms
    - NPCs (non-playable character)
      - monsters
      - bosses?
      - other heroes
    - chests
- movement between rooms
- items
  - weapons
  - armor/other (backpacks etc.)
  - consumables
    - potions with effects
    - healing
- combat
  - attacks
  - evasions/other options?
  - rewards after defeating enemies
- progression?
  - locked doors
  - reaching of new floors with harder enemies
  - story/at least context

point marked with ? considered nice to have but optional


## Roadmap
- [ ] UI
- [ ] Mapgeneration
- [ ] Movement
- [ ] Items
- [ ] Combat
- [ ] Progression / Complexity
