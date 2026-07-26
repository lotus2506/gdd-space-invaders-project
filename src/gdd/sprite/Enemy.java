package gdd.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

/**
 * Base class for everything that flies in from the right and shoots back.
 *
 * Enemy art is drawn rather than clipped: the ripped sheets only carry player
 * ships, and the spec allows either technique.
 */
public class Enemy extends Sprite {

    protected int hp = 1;
    protected int scoreValue = 100;
    protected int age = 0;

    // Where the enemy entered, so movement patterns can swing around it.
    protected final int spawnY;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnY = y;
        this.dx = -3;
    }

    @Override
    public void act() {
        super.act();

        age++;
        x += dx;
        y += dy;

        // Anything that has scrolled off the left is gone for good.
        if (x < -getWidth()) {
            die();
        }
    }

    /**
     * Kept so the original call site in the scenes still works; direction was
     * only meaningful for the old marching-formation Space Invaders movement.
     */
    public void act(int direction) {
        act();
    }

    /** Returns true when this hit destroyed the enemy. */
    public boolean hit(int damage) {
        hp -= damage;
        return hp <= 0;
    }

    public int getScore() {
        return scoreValue;
    }

    public int getHp() {
        return hp;
    }

    /**
     * Enemies that shoot override this and return a shot on the frames they fire.
     * Returning null means "not firing this frame".
     */
    public EnemyShot fire(Player player) {
        return null;
    }

    /**
     * What the scene actually calls. Most enemies fire at most one shot, so the
     * default wraps fire(); the boss overrides this to throw whole volleys.
     */
    public List<EnemyShot> fireVolley(Player player) {
        EnemyShot shot = fire(player);
        return shot == null ? Collections.emptyList() : Collections.singletonList(shot);
    }

    // ---- helpers for the procedurally drawn enemy art ----

    protected static BufferedImage newFrame(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    protected static Graphics2D beginFrame(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    protected static Image[] toImages(BufferedImage[] frames) {
        Image[] out = new Image[frames.length];
        System.arraycopy(frames, 0, out, 0, frames.length);
        return out;
    }
}
