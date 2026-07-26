package gdd.sprite;

import static gdd.Global.*;

public class Shot extends Sprite {

    public Shot() {
    }

    public Shot(int x, int y) {
        this(x, y, 0);
    }

    /**
     * @param dy vertical drift, used by the spread shots at higher shot levels.
     */
    public Shot(int x, int y, int dy) {
        initShot(x, y, dy);
    }

    private void initShot(int x, int y, int dy) {
        setAnimation(new Animation(
                SpriteSheet.frames(SHEET, LASER_W, LASER_H, 2, LASER_FRAMES), 3));

        this.dx = SHOT_SPEED;
        this.dy = dy;

        // Spawn bullet in front of the player, centred on the hull.
        setX(x + PLAYER_WIDTH);
        setY(y + PLAYER_HEIGHT / 2 - getHeight() / 2);
    }

    @Override
    public void act() {
        super.act();

        x += dx;
        y += dy;

        if (x > BOARD_WIDTH || y < -getHeight() || y > BOARD_HEIGHT) {
            die();
        }
    }
}
