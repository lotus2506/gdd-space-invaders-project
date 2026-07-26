package gdd.sprite;

import static gdd.Global.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/**
 * Interceptor. Faster than the drifter and fires aimed shots on a cooldown.
 */
public class Alien2 extends Enemy {

    private static final int W = 44;
    private static final int H = 28;
    private static final int FRAMES = 4;
    private static final int FIRE_INTERVAL = 95;

    private static Image[] sharedFrames;

    // Stagger the first shot so a whole wave does not volley on the same frame.
    private int fireTimer;

    public Alien2(int x, int y) {
        super(x, y);

        this.hp = 2;
        this.scoreValue = 250;
        this.dx = -5;
        this.fireTimer = 40 + Math.abs(x + y * 7) % FIRE_INTERVAL;

        setAnimation(new Animation(frames(), 4));
    }

    private static Image[] frames() {
        if (sharedFrames != null) {
            return sharedFrames;
        }

        BufferedImage[] out = new BufferedImage[FRAMES];
        for (int i = 0; i < FRAMES; i++) {
            BufferedImage img = newFrame(W, H);
            Graphics2D g = beginFrame(img);

            // Thruster flares at the back, length flickering per frame.
            int flame = 6 + (i % 2 == 0 ? 6 : 0) + i;
            g.setColor(new Color(255, 190, 60, 220));
            g.fillPolygon(new int[]{W - 6, W - 6 + flame, W - 6},
                          new int[]{H / 2 - 5, H / 2, H / 2 + 5}, 3);
            g.setColor(new Color(255, 245, 200, 200));
            g.fillPolygon(new int[]{W - 6, W - 6 + flame / 2, W - 6},
                          new int[]{H / 2 - 2, H / 2, H / 2 + 2}, 3);

            // Hull: an arrowhead pointing left, the way it flies.
            Polygon hull = new Polygon(
                    new int[]{2, W - 8, W - 8, W - 14},
                    new int[]{H / 2, 2, H - 2, H / 2}, 4);
            g.setColor(new Color(140, 50, 170));
            g.fillPolygon(hull);
            g.setColor(new Color(230, 160, 255));
            g.setStroke(new BasicStroke(2f));
            g.drawPolygon(hull);

            // Canopy blinks between frames so the sprite reads as alive.
            g.setColor(i % 2 == 0 ? new Color(255, 240, 180) : new Color(255, 130, 130));
            g.fillOval(12, H / 2 - 4, 10, 8);

            g.dispose();
            out[i] = img;
        }

        sharedFrames = toImages(out);
        return sharedFrames;
    }

    @Override
    public void act() {
        super.act();

        if (fireTimer > 0) {
            fireTimer--;
        }
    }

    @Override
    public EnemyShot fire(Player player) {
        // Only shoot once fully on screen.
        if (fireTimer > 0 || x > BOARD_WIDTH - 20 || !isVisible()) {
            return null;
        }

        fireTimer = FIRE_INTERVAL;
        return new EnemyShot(
            x,
            y + H / 2,
            -ENEMY_SHOT_SPEED,
            0
        );
        


    }
}
