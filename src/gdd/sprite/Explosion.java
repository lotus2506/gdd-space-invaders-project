package gdd.sprite;

import static gdd.Global.*;

public class Explosion extends Sprite {

    private static final int SCALE = 2;
    private static final int TICKS_PER_FRAME = 5;

    public Explosion(int x, int y) {
        initExplosion(x, y);
    }

    /** Bigger burst, used when the boss dies. */
    public Explosion(int x, int y, int scale) {
        initExplosion(x, y, scale);
    }

    private void initExplosion(int x, int y) {
        initExplosion(x, y, SCALE);
    }

    private void initExplosion(int x, int y, int scale) {
        Animation anim = new Animation(
                SpriteSheet.frames(SHEET, BOOM_W, BOOM_H, scale, BOOM_FRAMES),
                TICKS_PER_FRAME, false);
        setAnimation(anim);

        // Live exactly as long as the animation runs; the scenes tick this down
        // through visibleCountDown() and drop the sprite when it hits zero.
        visibleFrames = anim.getFrameCount() * TICKS_PER_FRAME;

        // Centre the burst on the point that blew up.
        setX(x - getWidth() / 2);
        setY(y - getHeight() / 2);
    }
}
