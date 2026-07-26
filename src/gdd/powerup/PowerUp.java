/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package gdd.powerup;

import gdd.sprite.Animation;
import gdd.sprite.Player;
import gdd.sprite.Sprite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


abstract public class PowerUp extends Sprite {

    protected static final int SIZE = 32;

    PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.dx = -2; // drift left with the scroll
    }

    abstract public void upgrade(Player player);

    @Override
    public void act() {
        super.act();

        x += dx;
        y += dy;

        if (x < -SIZE) {
            die();
        }
    }

    /**
     * Draws a pulsing capsule badge, one image per animation frame. Power-ups have
     * no art on the sheet, so they are drawn rather than clipped - the spec allows
     * either technique.
     */
    protected static Image[] capsuleFrames(Color core, String letter, int count) {
        Image[] frames = new Image[count];

        for (int i = 0; i < count; i++) {
            // Pulse the glow between tight and wide across the cycle.
            double phase = Math.sin(Math.PI * 2 * i / count);
            int glow = (int) (4 + 3 * phase);

            BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(new Color(core.getRed(), core.getGreen(), core.getBlue(), 70));
            g.fillOval(SIZE / 2 - glow - 8, SIZE / 2 - glow - 8, (glow + 8) * 2, (glow + 8) * 2);

            g.setColor(core);
            g.fillRoundRect(4, 8, SIZE - 8, SIZE - 16, 10, 10);

            g.setColor(Color.WHITE);
            g.drawRoundRect(4, 8, SIZE - 8, SIZE - 16, 10, 10);

            g.setFont(new Font("Helvetica", Font.BOLD, 14));
            int tw = g.getFontMetrics().stringWidth(letter);
            g.drawString(letter, (SIZE - tw) / 2, SIZE / 2 + 5);

            g.dispose();
            frames[i] = img;
        }

        return frames;
    }

    protected void initCapsule(Color core, String letter) {
        setAnimation(new Animation(capsuleFrames(core, letter, 8), 4));
    }
}
