package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    // Dev-only hotkeys (stage skip, boss skip, invulnerability). Ship with this off.
    public static final boolean DEBUG = false;

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    // Side-scroll layout: wide, short board (landscape) instead of the old
    // near-square vertical-shooter board.
    public static final int BOARD_WIDTH = 960; // Landscape width
    public static final int BOARD_HEIGHT = 540; // Landscape height (16:9)
    public static final int BORDER_RIGHT = 60;
    public static final int BORDER_LEFT = 10;

    public static final int GROUND = 500;
    public static final int BOMB_HEIGHT = 10;

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;

    // Player ship is the 24x16 side-view Vic Viper cell, scaled up.
    public static final int PLAYER_WIDTH = 24 * SCALE_FACTOR;
    public static final int PLAYER_HEIGHT = 16 * SCALE_FACTOR;

    // The dashboard is drawn across the top; gameplay stays below it.
    public static final int HUD_HEIGHT = 56;

    // Frames per second driven by the scene timers.
    public static final int FPS = 60;

    // Stage length in frames. 18600 / 60 = 310s = 5 min 10 s of scrolling per stage.
    public static final int STAGE_LENGTH = 18600;

    // Player tuning
    public static final int PLAYER_LIVES = 7;
    public static final int PLAYER_BASE_SPEED = 4;
    public static final int SPEED_LEVEL_MAX = 3; // base + 2 upgrade steps
    public static final int SHOT_LEVEL_MAX = 4; // multi-shot, 4 steps
    public static final int PLAYER_INVULNERABLE_FRAMES = 120;
    public static final int SHOT_COOLDOWN_FRAMES = 8;
    public static final int SHOT_SPEED = 14;
    public static final int ENEMY_SHOT_SPEED = 6;

    // Images
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_ENEMY2 = "src/images/alien2.png";
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";

    // Sprite sheet (NES Life Force rip) and the frame rectangles clipped from it.
    // Layout is a regular grid of black cells: ships 24x16 at x=56/88/120, y=8.
    public static final String SHEET = "src/images/spites.png";

    public static final int SHIP_W = 24;
    public static final int SHIP_H = 16;
    // Three-frame idle cycle of the side-view Vic Viper (1P blue).
    public static final int[] SHIP_FRAMES = {56, 8, 88, 8, 120, 8};

    // Player laser: two pulse frames, normalised to one box.
    public static final int LASER_W = 24;
    public static final int LASER_H = 8;
    public static final int[] LASER_FRAMES = {328, 8, 328, 17};

    // Enemy shot: the 2P (red) orb, so enemy fire reads differently from the player's.
    public static final int ORB_W = 8;
    public static final int ORB_H = 16;
    public static final int[] ORB_FRAMES = {328, 144, 344, 144};

    // Explosion: two 24x24 burst cells plus a smaller dispersing one.
    public static final int BOOM_W = 24;
    public static final int BOOM_H = 24;
    public static final int[] BOOM_FRAMES = {56, 56, 88, 56, 296, 56};

    // Stage spawn tables (CSV: frame,type,x,y)
    public static final String CSV_STAGE1 = "src/data/stage1.csv";
    public static final String CSV_STAGE2 = "src/data/stage2.csv";
}
