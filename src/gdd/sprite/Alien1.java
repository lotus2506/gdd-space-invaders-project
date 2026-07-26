package gdd.sprite;

import static gdd.Global.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Drifter. Weaves on a sine path across the screen and never shoots - the easy
 * enemy that fills out the early waves.
 */
public class Alien1 extends Enemy {

    private static final int SIZE = 36;
    private static final int FRAMES = 6;

    // Frames are the same for every instance, so build them once.
    private static Image[] sharedFrames;

    private final double waveAmplitude;
    private final double wavePeriod;

    public Alien1(int x, int y) {
        super(x, y);

        this.hp = 1;
        this.scoreValue = 100;
        this.dx = -3;

        // Vary the weave a little per spawn so a wave does not fly in lockstep.
        this.waveAmplitude = 26 + (Math.abs(y) % 3) * 8;
        this.wavePeriod = 70 + (Math.abs(x + y) % 40);

        setAnimation(new Animation(frames(), 5));
    }

    private static Image[] frames() {
        if (sharedFrames != null) {
            return sharedFrames;
        }

        BufferedImage[] out = new BufferedImage[FRAMES];
        for (int i = 0; i < FRAMES; i++) {
            double t = (double) i / FRAMES;

            BufferedImage img = newFrame(SIZE, SIZE);
            Graphics2D g = beginFrame(img);

            int c = SIZE / 2;

            // Rotating spokes.
            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(60, 200, 140));
            for (int s = 0; s < 4; s++) {
                double a = Math.PI * 2 * (t + s / 4.0);
                int ex = c + (int) (Math.cos(a) * (c - 2));
                int ey = c + (int) (Math.sin(a) * (c - 2));
                g.drawLine(c, c, ex, ey);
            }

            // Hull.
            g.setColor(new Color(20, 110, 90));
            g.fillOval(6, 6, SIZE - 12, SIZE - 12);
            g.setColor(new Color(120, 240, 200));
            g.setStroke(new BasicStroke(2f));
            g.drawOval(6, 6, SIZE - 12, SIZE - 12);

            // Core pulses over the cycle.
            int pulse = (int) (4 + 3 * Math.sin(Math.PI * 2 * t));
            g.setColor(new Color(230, 255, 160));
            g.fillOval(c - pulse, c - pulse, pulse * 2, pulse * 2);

            g.dispose();
            out[i] = img;
        }

        sharedFrames = toImages(out);
        return sharedFrames;
    }

    @Override
    public void act() {
        super.act(); // ages the sprite and drifts it left

        // Weave vertically around the line it entered on, staying inside the
        // playfield so it never slides under the dashboard.
        int wave = spawnY + (int) (Math.sin(age / wavePeriod * Math.PI * 2) * waveAmplitude);
        y = Math.max(HUD_HEIGHT, Math.min(BOARD_HEIGHT - SIZE, wave));
    }
}
