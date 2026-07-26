package gdd.powerup;

import gdd.sprite.Player;
import java.awt.Color;

/**
 * Multi-shot power-up: four steps of fire power, from a single laser up to a
 * four-way spread.
 */
public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        initCapsule(new Color(220, 120, 40), "M");
    }

    public void upgrade(Player player) {
        player.upgradeShot();
        this.die();
    }
}
