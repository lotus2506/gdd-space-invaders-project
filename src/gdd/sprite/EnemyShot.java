package gdd.sprite;

import static gdd.Global.*;

/**
 * Projectile fired by enemies and the boss. Uses the 2P (red) orb frames so
 * incoming fire is easy to tell apart from the player's blue laser.
 */
public class EnemyShot extends Sprite {

    public EnemyShot(int x, int y, int dx, int dy) {
        setAnimation(new Animation(
                SpriteSheet.frames(SHEET, ORB_W, ORB_H, 2, ORB_FRAMES), 5));

        this.dx = dx;
        this.dy = dy;

        setX(x);
        setY(y);
    }

    /** Aims from a source point at the player, at a fixed speed. */
    public static EnemyShot aimedAt(int fromX, int fromY, Player target, int speed) {
        int tx = target.getX() + target.getWidth() / 2;
        int ty = target.getY() + target.getHeight() / 2;

        double dist = Math.max(1.0, Math.hypot(tx - fromX, ty - fromY));
        int dx = (int) Math.round((tx - fromX) / dist * speed);
        int dy = (int) Math.round((ty - fromY) / dist * speed);

        // Never let a shot stall on the spot.
        if (dx == 0 && dy == 0) {
            dx = -speed;
        }

        return new EnemyShot(fromX, fromY, dx, dy);
    }

    @Override
    public void act() {
        super.act();

        x += dx;
        y += dy;

        if (x < -getWidth() || x > BOARD_WIDTH || y < -getHeight() || y > BOARD_HEIGHT) {
            die();
        }
    }
}
