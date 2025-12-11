# Labyrinthe - Projet 3dev3a

## Description

Labyrinthe est un jeu de plateau pour 2 à 4 joueurs. L'objectif est de récolter des trésors avec son personnage dans un labyrinthe dont on peut modifier la topologie.

## Structure du projet

```
labyrinthe/
├── src/
│   ├── main/
│   │   ├── java/g65058/dev3/labyrinthe/
│   │   │   ├── model/
│   │   │   │   ├── board/       # Plateau, tuiles, positions
│   │   │   │   ├── game/        # Logique du jeu, joueurs, façade
│   │   │   │   ├── command/     # Pattern Command (undo/redo)
│   │   │   │   ├── ai/          # Stratégies IA
│   │   │   │   └── observer/    # Pattern Observer
│   │   │   ├── view/
│   │   │   │   ├── console/     # Vue console
│   │   │   │   └── javafx/      # Vue JavaFX
│   │   │   └── controller/      # Contrôleur MVC
│   │   └── resources/
│   │       └── images/          # Images des tuiles et objectifs
│   └── test/
│       └── java/g65058/dev3/labyrinthe/model/  # Tests JUnit
└── pom.xml
```

## Architecture

Le projet implémente les patterns suivants :

### MVC (Model-View-Controller)
- **Model** : Toute la logique métier dans le package `model`
- **View** : Interfaces console et JavaFX dans le package `view`
- **Controller** : Coordination dans le package `controller`

### Façade
La classe `LabyrinthFacade` expose une interface simplifiée du modèle, garantissant :
- La cohérence du jeu
- L'impossibilité de tricher
- L'encapsulation de la complexité

### Command (Undo/Redo)
- `Command` : Interface pour toutes les commandes
- `InsertTileCommand` : Insertion d'une tuile
- `MovePlayerCommand` : Déplacement d'un joueur
- `TurnCommand` : Commande composite pour un tour complet
- `CommandHistory` : Gestionnaire d'historique (Invoker)

### Observer
- `Observable` : Interface pour le modèle
- `Observer` : Interface pour les vues
- Notifications automatiques lors des changements d'état

### Strategy
- `Strategy` : Interface pour les stratégies IA
- `RandomStrategy` : Joue aléatoirement (niveau 0)

## Exécution

### Vue Console
```bash
mvn compile exec:java -Dexec.mainClass="g65058.dev3.labyrinthe.view.console.ConsoleApp"
```

### Vue JavaFX
```bash
mvn javafx:run
```

### Tests
```bash
mvn test
```

## Fonctionnalités

- [x] Plateau 7x7 avec tuiles fixes et mobiles
- [x] 24 objectifs différents
- [x] 4 joueurs (humains et/ou IA)
- [x] Insertion de tuiles avec rotation
- [x] Déplacement des joueurs
- [x] Vérification des chemins (BFS)
- [x] Undo/Redo des actions
- [x] Vue console
- [x] Vue JavaFX avec interface graphique
- [x] Joueurs IA (stratégie random)
- [x] Pattern MVC
- [x] Pattern Observer
- [x] Pattern Command
- [x] Pattern Strategy
- [x] Pattern Façade
- [x] Tests JUnit

## Règles du jeu

1. Chaque tour se compose de deux phases :
   - **Insertion** : Insérer la tuile supplémentaire par une flèche
   - **Déplacement** : Déplacer son pion vers une position accessible

2. On ne peut pas annuler l'insertion précédente (pas de va-et-vient)

3. Pour gagner (version simplifiée) : collecter tous ses objectifs

4. Pour gagner (version standard) : collecter tous ses objectifs ET revenir à sa case de départ

## Auteur

g65058 - ESI Bruxelles - 2025-2026
