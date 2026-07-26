package gdd.powerup;

import gdd.sprite.Player;
import java.awt.Color;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


public class SpeedUp extends PowerUp {

    public SpeedUp(int x, int y) {
        super(x, y);
        initCapsule(new Color(40, 160, 220), "S");
    }

    public void upgrade(Player player) {
        // Two upgrade steps; beyond that the capsule is just points.
        player.upgradeSpeed();
        this.die();
    }

}
