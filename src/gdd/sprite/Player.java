package gdd.sprite;

import static gdd.Global.*;
import java.awt.event.KeyEvent;

public class Player extends Sprite {

    private static final int START_X = 80;
    private static final int START_Y = (BOARD_HEIGHT - PLAYER_HEIGHT) / 2;

    private int lives = PLAYER_LIVES;
    private int speedLevel = 1;
    private int shotLevel = 1;
    private int currentSpeed = PLAYER_BASE_SPEED;

    // Counts down after a hit; while it runs the ship cannot be hit again and
    // the scene flashes it.
    private int invulnerable = 0;
    private int shotCooldown = 0;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        setAnimation(new Animation(
                SpriteSheet.frames(SHEET, SHIP_W, SHIP_H, SCALE_FACTOR, SHIP_FRAMES), 6));

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    /** Speed power-up: two upgrade steps above the base level. */
    public boolean upgradeSpeed() {
        if (speedLevel >= SPEED_LEVEL_MAX) {
            return false;
        }
        speedLevel++;
        setSpeed(PLAYER_BASE_SPEED + (speedLevel - 1) * 2);
        return true;
    }

    public int getShotLevel() {
        return shotLevel;
    }

    /** Multi-shot power-up: four steps of fire power. */
    public boolean upgradeShot() {
        if (shotLevel >= SHOT_LEVEL_MAX) {
            return false;
        }
        shotLevel++;
        return true;
    }

    public int getLives() {
        return lives;
    }

    public boolean isInvulnerable() {
        return invulnerable > 0;
    }

    public boolean canFire() {
        return shotCooldown == 0;
    }

    public void noteFired() {
        shotCooldown = SHOT_COOLDOWN_FRAMES;
    }

    /**
     * Takes a hit. Returns true if that was the last life. Losing a life also
     * costs one step of each upgrade, the way the arcade original punishes death.
     */
    public boolean hit() {
        if (isInvulnerable()) {
            return false;
        }

        lives--;
        invulnerable = PLAYER_INVULNERABLE_FRAMES;

        if (speedLevel > 1) {
            speedLevel--;
            setSpeed(PLAYER_BASE_SPEED + (speedLevel - 1) * 2);
        }
        if (shotLevel > 1) {
            shotLevel--;
        }

        setX(START_X);
        setY(START_Y);
        dx = 0;
        dy = 0;

        return lives <= 0;
    }

    @Override
    public void act() {
        super.act();

        if (invulnerable > 0) {
            invulnerable--;
        }
        if (shotCooldown > 0) {
            shotCooldown--;
        }

        x += dx;
        y += dy;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - PLAYER_WIDTH) {
            x = BOARD_WIDTH - PLAYER_WIDTH;
        }

        // Keep the ship out from behind the dashboard.
        if (y <= HUD_HEIGHT) {
            y = HUD_HEIGHT;
        }

        if (y >= BOARD_HEIGHT - PLAYER_HEIGHT) {
            y = BOARD_HEIGHT - PLAYER_HEIGHT;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -currentSpeed;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
