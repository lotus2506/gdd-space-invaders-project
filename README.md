# STAR BLASTER — GDD Space Invaders Project

STAR Blaster is a side-scrolling arcade shooter inspired by Life Force / Salamander, developed by extending the provided GDD Space Invaders Project template. The game features two playable stages, animated sprites, power-ups, multiple enemy types, and a final boss battle.

## Team Members
- Chaw Yadanar Oo - 6632782
- Min Khant Aung - 6632753

## Project Requirements
This project satisfies the assignment requirements:
Side-scrolling shooter (horizontal)
Extended from the provided mchayapol/gdd-space-invaders-project
Original gameplay additions while preserving the original codebase structure
Two playable stages
Boss fight in the final stage
Two enemy types
Animated sprites
Speed Up and Multi-Shot power-ups
Dashboard displaying player status

## How to run

#Requirements

Java Development Kit (JDK 17 or later)
#Run
From the project root directory:
./run.sh

The script compiles the source code into the build/ directory and launches:
  gdd.Main
Important: Run the game from the project root so all image, audio, and stage resources can be loaded correctly.

## Controls
Key	            |Action
← ↑ ↓ →         |	Move the player
Space	          |Shoot / Start Game / Return to Title

## Gameplay
Stage 1 – Outer Belt
-Enemy waves
-Animated obstacles
-Speed Up power-ups
-Multi-Shot upgrades
Stage 2 – The Core
-More challenging enemies
-Increased difficulty
-Final boss battle with multiple attack phases

- **Power-ups**:
  Speed Up
     ~2 upgrade levels
     ~Increases player movement speed
  Multi-Shot
     ~4 upgrade levels
     ~Expands the player's firing pattern up to four simultaneous shots
  
- **Dashboard** The in-game dashboard displays:
*Score
*Remaining Lives
*Current Speed Level
*Shot Upgrade Level
*Stage Progress
*Boss Health Bar (Stage 2)
## Stage data

Enemy spawn data is stored in:
    -src/data/stage1.csv
    -src/data/stage2.csv
Each row follows the format:
    -frame,type,x,y
The files are loaded by gdd.StageLoader, allowing enemy waves to be modified without changing the game code.

## Debug hotkeys
Enable debugging by setting:
Global.DEBUG = true;
Available debug shortcuts:
Key	Function
9	Skip ahead 3000 frames
0	Jump near the end of the stage
B	Skip directly to the boss (Stage 2 only)
Credits
This project was developed by extending the provided GDD Space Invaders Project starter code for educational purposes.
Game inspiration:
Life Force / Salamander (Konami)
Sprite resources:
The Spriters Resource (Life Force / Salamander sprite sheets)

