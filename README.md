# STAR VIPER — GDD Space Invaders Project

A side-scrolling shooter (Life Force / Salamander style) extended from the
`mchayapol/gdd-space-invaders-project` starter.

## Team Members
- Chaw Yadanar Oo - 6632782
- Min Khant Aung - 6632753

## How to run

Needs a JDK (`brew install --cask temurin` if you don't have one). From the
project root:

```
./run.sh
```

It compiles `src/` into `build/` and launches `gdd.Main`. Must be run from the
project root — image, audio and stage paths are relative to the working directory.

## Controls
- **Arrow keys** — move
- **Space** — fire (also starts the game / returns to title)

## Gameplay
- **Two stages**, each scrolling for over 5 minutes (18,600 frames at 60fps).
  Stage 1 (Outer Belt) hands over to Stage 2 (The Core), which ends in a boss fight.
- **Two enemy types**: a weaving drifter (Alien1) and a faster, shooting
  interceptor (Alien2). The Stage 2 boss has two attack phases.
- **Power-ups**: Speed Up (2 steps) and Multi-Shot (4 steps, up to a four-way spread).
- **Dashboard** across the top: score, lives, speed/shot meters, stage progress
  and a boss health bar.
- All sprites are animated — the player ship and shots are clipped from the sprite
  sheet, enemies/boss/power-ups/explosions are drawn frame-by-frame.

## Stage data
Spawn tables live in `src/data/stage1.csv` and `stage2.csv` as `frame,type,x,y`
rows, parsed by `gdd.StageLoader`. Edit the CSVs to retune the waves — no
recompile needed.

## Debug hotkeys
Set `Global.DEBUG = true`, then in a stage: `9` skips ahead 3000 frames, `0`
jumps to the end of the stage, `B` (Stage 2) skips to the boss.

## References
- Based on the [Java Space Invaders](git@github.com:Swanyiwinthuya/gdd-project-1.git) starter.
- Player ship frames clipped from the NES *Life Force / Salamander* Vic Viper
  sprite sheet (ripped assets from The Spriters Resource), included as
  `src/images/spites.png`.
s