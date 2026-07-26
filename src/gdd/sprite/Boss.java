package gdd.sprite;

import static gdd.Global.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Stage 2 boss. Flies in from the right, anchors near the right edge and works
 * through two attack phases as its hull is worn down.
 */
public class Boss extends Enemy {

    public static final int W = 180;
    public static final int H = 150;
    public static final int MAX_HP = 140;

    private static final int FRAMES = 6;
    private static final int ANCHOR_X = BOARD_WIDTH - W - 40;

    private static Image[] sharedFrames;
    private static Image[] flashFrames;

    private final Animation normal;
    private final Animation flash;

    private int flashTimer = 0;
    private int fireTimer = 90;
    private boolean anchored = false;

    public Boss(int x, int y) {
        super(x, y);

        this.hp = MAX_HP;
        this.scoreValue = 5000;
        this.dx = -2;

        normal = new Animation(frames(false), 6);
        flash = new Animation(frames(true), 3);
        setAnimation(normal);
    }

    public int getMaxHp() {
        return MAX_HP;
    }

    /** Phase 2 starts once the hull is below half. */
    public boolean isEnraged() {
        return hp <= MAX_HP / 2;
    }

    private static Image[] frames(boolean flashed) {
        if (flashed && flashFrames != null) {
            return flashFrames;
        }
        if (!flashed && sharedFrames != null) {
            return sharedFrames;
        }

        BufferedImage[] out = new BufferedImage[FRAMES];
        for (int i = 0; i < FRAMES; i++) {
            double t = (double) i / FRAMES;

            BufferedImage img = newFrame(W, H);
            Graphics2D g = beginFrame(img);

            // Main hull: a broad wedge facing left.
            Polygon hull = new Polygon(
                    new int[]{6, 70, W - 10, W - 10, 70},
                    new int[]{H / 2, 12, 22, H - 22, H - 12}, 5);
            g.setColor(new Color(70, 70, 110));
            g.fillPolygon(hull);
            g.setColor(new Color(160, 170, 220));
            g.setStroke(new BasicStroke(3f));
            g.drawPolygon(hull);

            // Armour ribs.
            g.setStroke(new BasicStroke(2f));
            g.setColor(new Color(110, 115, 165));
            for (int r = 0; r < 4; r++) {
                int rx = 80 + r * 22;
                g.drawLine(rx, 30, rx, H - 30);
            }

            // Upper and lower cannon pods.
            g.setColor(new Color(90, 40, 60));
            g.fillRect(30, 22, 40, 20);
            g.fillRect(30, H - 42, 40, 20);
            g.setColor(new Color(220, 120, 140));
            g.drawRect(30, 22, 40, 20);
            g.drawRect(30, H - 42, 40, 20);

            // Core: the eye, pulsing across the cycle.
            int pulse = (int) (18 + 6 * Math.sin(Math.PI * 2 * t));
            g.setColor(new Color(255, 90, 60, 120));
            g.fillOval(60 - pulse, H / 2 - pulse, pulse * 2, pulse * 2);
            g.setColor(new Color(255, 200, 90));
            g.fillOval(60 - pulse / 2, H / 2 - pulse / 2, pulse, pulse);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(60 - pulse, H / 2 - pulse, pulse * 2, pulse * 2);

            if (flashed) {
                // Wash the whole sprite toward white for the damage blink.
                g.setComposite(java.awt.AlphaComposite.getInstance(
                        java.awt.AlphaComposite.SRC_ATOP, 0.65f));
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, W, H);
            }

            g.dispose();
            out[i] = img;
        }

        if (flashed) {
            flashFrames = toImages(out);
            return flashFrames;
        }
        sharedFrames = toImages(out);
        return sharedFrames;
    }

    @Override
    public boolean hit(int damage) {
        flashTimer = 6;
        return super.hit(damage);
    }

    @Override
    public void act() {
        age++;

        if (!anchored) {
            x += dx;
            if (x <= ANCHOR_X) {
                x = ANCHOR_X;
                anchored = true;
            }
        } else {
            // Patrol up and down the right-hand side.
            int span = (BOARD_HEIGHT - HUD_HEIGHT - H) / 2;
            int mid = HUD_HEIGHT + span;
            y = mid + (int) (Math.sin(age / (isEnraged() ? 55.0 : 80.0)) * span);
        }

        if (flashTimer > 0) {
            flashTimer--;
            if (getAnimation() != flash) {
                setAnimation(flash);
            }
        } else if (getAnimation() != normal) {
            setAnimation(normal);
        }

        if (getAnimation() != null) {
            getAnimation().update();
        }

        if (fireTimer > 0) {
            fireTimer--;
        }
    }

    @Override
    public List<EnemyShot> fireVolley(Player player) {
        List<EnemyShot> volley = new ArrayList<>();

        if (!anchored || fireTimer > 0 || !isVisible()) {
            return volley;
        }

        int muzzleX = x + 10;
        int muzzleY = y + H / 2;

        if (!isEnraged()) {
            // Phase 1: a slow three-way spread from the core.
            fireTimer = 80;
            volley.add(new EnemyShot(muzzleX, muzzleY, -ENEMY_SHOT_SPEED, 0));
            volley.add(new EnemyShot(muzzleX, muzzleY, -ENEMY_SHOT_SPEED, -3));
            volley.add(new EnemyShot(muzzleX, muzzleY, -ENEMY_SHOT_SPEED, 3));
        } else {
            // Phase 2: faster, wider, plus an aimed shot from each cannon pod.
            fireTimer = 50;
            for (int dy = -6; dy <= 6; dy += 3) {
                volley.add(new EnemyShot(muzzleX, muzzleY, -ENEMY_SHOT_SPEED - 1, dy));
            }
            volley.add(new EnemyShot(x + 30, y + 32, -(ENEMY_SHOT_SPEED + 1), 0));
            volley.add(new EnemyShot(x + 30, y + H - 32, -(ENEMY_SHOT_SPEED + 1), 0));
        }

        return volley;
    }
}
