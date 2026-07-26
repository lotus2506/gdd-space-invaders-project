package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.StageLoader;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Boss;
import gdd.sprite.Enemy;
import gdd.sprite.EnemyShot;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The gameplay shared by every stage: scrolling backdrop, spawn table, the
 * update loop, collisions and the dashboard.
 *
 * Each stage subclass supplies its spawn file, music, name and what happens when
 * it is cleared. Scene1 and Scene2 are those subclasses.
 */
public abstract class StageScene extends JPanel {

    // Stage states
    protected static final int RUNNING = 0;
    protected static final int CLEARED = 1;
    protected static final int GAME_OVER = 2;

    protected int frame = 0;
    protected int score = 0;
    protected int state = RUNNING;
    protected int bannerTimer = 0;

    protected List<PowerUp> powerups;
    protected List<Enemy> enemies;
    protected List<Explosion> explosions;
    protected List<Shot> shots;
    protected List<EnemyShot> enemyShots;
    protected Player player;
    protected Boss boss;

    // True while SPACE is held. Shooting is driven from the update loop off this
    // flag, not off the key event, so holding a movement key can't block it.
    private boolean firing = false;

    protected HashMap<Integer, List<SpawnDetails>> spawnMap = new HashMap<>();

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    protected final Game game;
    private AudioPlayer audioPlayer;

    // Parallax starfield: three layers scrolling at different speeds.
    private static final int STAR_LAYERS = 3;
    private final int[][] starX = new int[STAR_LAYERS][];
    private final int[][] starY = new int[STAR_LAYERS][];

    private static final int MAX_SHOTS = 16;

    protected StageScene(Game game) {
        this.game = game;
        setPreferredSize(d); // so the frame packs to a full 960x540 playfield
        initStars();
        spawnMap = StageLoader.load(csvPath());
    }

    // ---- stage configuration ----

    protected abstract String csvPath();

    protected abstract String audioPath();

    protected abstract String stageName();

    /** Called once the stage has been cleared and the banner has been shown. */
    protected abstract void onStageComplete();

    /** Stage 2 ends on the boss, not on the frame counter. */
    protected boolean endsWithBoss() {
        return false;
    }

    /** Banner shown once the stage is cleared. */
    protected String clearedText() {
        return "STAGE CLEAR";
    }

    /** Backdrop tint, so the two stages do not look identical. */
    protected Color backdrop() {
        return new Color(6, 6, 22);
    }

    // ---- lifecycle ----

    private void initStars() {
        // Fixed seed: the field is decorative, and a stable one is easier to look at
        // across runs than a field that reshuffles every launch.
        Random rng = new Random(4242);
        int[] counts = {70, 45, 25};

        for (int layer = 0; layer < STAR_LAYERS; layer++) {
            starX[layer] = new int[counts[layer]];
            starY[layer] = new int[counts[layer]];
            for (int i = 0; i < counts[layer]; i++) {
                starX[layer][i] = rng.nextInt(BOARD_WIDTH);
                starY[layer][i] = HUD_HEIGHT + rng.nextInt(BOARD_HEIGHT - HUD_HEIGHT);
            }
        }
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        gameInit();

        timer = new Timer(1000 / FPS, new GameCycle());
        timer.start();

        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initAudio() {
        try {
            audioPlayer = new AudioPlayer(audioPath());
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        enemyShots = new ArrayList<>();
        player = new Player();
        boss = null;

        frame = 0;
        score = 0;
        state = RUNNING;
        bannerTimer = 0;
    }

    // ---- drawing ----

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(backdrop());
        g.fillRect(0, 0, d.width, d.height);

        drawStars(g);

        if (state != GAME_OVER) {
            drawExplosions(g);
            drawPowerUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShots(g);
            drawDashboard(g);
            drawBanner(g);
        } else {
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    /** Parallax starfield; the far layer barely moves, the near one streaks. */
    private void drawStars(Graphics g) {
        int[] speeds = {1, 2, 4};
        Color[] shades = {new Color(90, 90, 130), new Color(160, 160, 200), Color.WHITE};

        for (int layer = 0; layer < STAR_LAYERS; layer++) {
            g.setColor(shades[layer]);
            int size = layer == 2 ? 2 : 1;

            for (int i = 0; i < starX[layer].length; i++) {
                // Scroll right-to-left, wrapping around the board width.
                int x = Math.floorMod(starX[layer][i] - frame * speeds[layer], BOARD_WIDTH);
                g.fillRect(x, starY[layer][i], size, size);
            }
        }
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
        }
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (!player.isVisible()) {
            return;
        }

        // Blink while the ship is recovering from a hit.
        if (player.isInvulnerable() && frame % 10 < 5) {
            return;
        }

        g.drawImage(player.getImage(), player.getX(), player.getY(), this);
    }

    private void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
        for (EnemyShot shot : enemyShots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
            }
        }
    }

    // HUD palette
    private static final Color HUD_CYAN = new Color(90, 210, 255);
    private static final Color HUD_AMBER = new Color(255, 180, 70);
    private static final Color HUD_GREEN = new Color(110, 235, 150);
    private static final Color HUD_DIM = new Color(70, 82, 110);
    private static final Color HUD_LABEL = new Color(150, 170, 205);

    /** Status dashboard across the top of the board. */
    private void drawDashboard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Panel: a dark gradient with a glowing cyan seam along the bottom.
        g2.setPaint(new GradientPaint(0, 0, new Color(12, 16, 32, 235),
                0, HUD_HEIGHT, new Color(4, 6, 16, 235)));
        g2.fillRect(0, 0, BOARD_WIDTH, HUD_HEIGHT);
        g2.setColor(new Color(20, 60, 90));
        g2.fillRect(0, HUD_HEIGHT - 1, BOARD_WIDTH, 1);
        g2.setColor(HUD_CYAN);
        g2.fillRect(0, HUD_HEIGHT, BOARD_WIDTH, 2);

        // ---- SCORE (left, two lines) ----
        drawLabel(g2, "SCORE", 16, 15, HUD_CYAN);
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        String scoreText = String.format("%07d", score);
        g2.setColor(new Color(0, 18, 34));            // soft drop shadow
        g2.drawString(scoreText, 17, 40);
        g2.setColor(new Color(220, 245, 255));        // bright face, no halo
        g2.drawString(scoreText, 16, 39);
        drawLabel(g2, stageName(), 16, HUD_HEIGHT - 4, HUD_LABEL);

        // ---- SHIPS / SPD / PWR: one aligned row so nothing overlaps ----
        int rowY = 34;

        drawLabel(g2, "SHIPS", 250, rowY, HUD_LABEL);
        for (int i = 0; i < player.getLives(); i++) {
            drawShipIcon(g2, 296 + i * 22, rowY - 4, HUD_CYAN);
        }

        drawMeter(g2, "SPD", 390, rowY, player.getSpeedLevel(), SPEED_LEVEL_MAX, HUD_CYAN);
        drawMeter(g2, "PWR", 510, rowY, player.getShotLevel(), SHOT_LEVEL_MAX, HUD_AMBER);

        // ---- STAGE progress (right) with a ship riding the leading edge ----
        int barW = 250;
        int barX = BOARD_WIDTH - barW - 20;
        int barY = 34;
        double progress = Math.min(1.0, (double) frame / STAGE_LENGTH);

        drawLabel(g2, "STAGE PROGRESS", barX, 16, HUD_LABEL);

        g2.setColor(new Color(20, 24, 40));
        g2.fillRoundRect(barX, barY, barW, 9, 6, 6);
        int fillW = (int) (barW * progress);
        if (fillW > 0) {
            g2.setPaint(new GradientPaint(barX, 0, new Color(60, 180, 120),
                    barX + barW, 0, HUD_GREEN));
            g2.fillRoundRect(barX, barY, fillW, 9, 6, 6);
        }
        // tick marks every 25%
        g2.setColor(new Color(90, 110, 140));
        for (int t = 1; t < 4; t++) {
            int tx = barX + barW * t / 4;
            g2.fillRect(tx, barY, 1, 9);
        }
        g2.setColor(new Color(90, 120, 160));
        g2.drawRoundRect(barX, barY, barW, 9, 6, 6);
        drawShipIcon(g2, barX + fillW, barY + 4, HUD_GREEN);

        if (boss != null && boss.isVisible()) {
            drawBossHealth(g2);
        }
    }

    /** Small letter-spaced caps used for every HUD label. */
    private void drawLabel(Graphics2D g, String text, int x, int y, Color c) {
        g.setFont(new Font("Helvetica", Font.BOLD, 11));
        g.setColor(c);
        int cx = x;
        for (char ch : text.toCharArray()) {
            g.drawString(String.valueOf(ch), cx, y);
            cx += g.getFontMetrics().charWidth(ch) + 1; // 1px tracking
        }
    }

    /** A little right-facing viper, used for lives and the progress marker. */
    private void drawShipIcon(Graphics2D g, int cx, int cy, Color c) {
        java.awt.Polygon p = new java.awt.Polygon(
                new int[]{cx + 9, cx - 6, cx - 2, cx - 6},
                new int[]{cy, cy - 5, cy, cy + 5}, 4);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
        g.fillOval(cx - 8, cy - 6, 18, 12); // soft glow
        g.setColor(c);
        g.fillPolygon(p);
        g.setColor(Color.WHITE);
        g.fillRect(cx - 2, cy - 1, 3, 2); // cockpit glint
    }

    /** Segmented power meter: lit cells glow, empty cells are hollow. */
    private void drawMeter(Graphics2D g, String label, int x, int y, int level, int max, Color on) {
        drawLabel(g, label, x, y, HUD_LABEL);

        int bx = x + 34;
        int segW = 14, segH = 10, gap = 4;
        for (int i = 0; i < max; i++) {
            int sx = bx + i * (segW + gap);
            int sy = y - segH + 1;
            if (i < level) {
                g.setColor(new Color(on.getRed(), on.getGreen(), on.getBlue(), 70));
                g.fillRoundRect(sx - 2, sy - 2, segW + 4, segH + 4, 5, 5); // glow
                g.setColor(on);
                g.fillRoundRect(sx, sy, segW, segH, 4, 4);
                g.setColor(Color.WHITE);
                g.fillRect(sx + 2, sy + 2, segW - 4, 2); // highlight strip
            } else {
                g.setColor(new Color(24, 28, 44));
                g.fillRoundRect(sx, sy, segW, segH, 4, 4);
                g.setColor(HUD_DIM);
                g.drawRoundRect(sx, sy, segW, segH, 4, 4);
            }
        }
    }

    private void drawBossHealth(Graphics2D g) {
        int barW = BOARD_WIDTH - 320;
        int barX = 160;
        int barY = HUD_HEIGHT + 14;
        boolean enraged = boss.isEnraged();
        double hp = Math.max(0.0, (double) boss.getHp() / boss.getMaxHp());

        // Label; flashes when enraged.
        g.setFont(new Font("Helvetica", Font.BOLD, 13));
        g.setColor(enraged && frame % 20 < 10 ? Color.WHITE : new Color(255, 90, 70));
        g.drawString("BOSS", barX - 52, barY + 12);

        // Track and fill.
        g.setColor(new Color(30, 12, 16));
        g.fillRoundRect(barX, barY, barW, 14, 6, 6);
        Color a = enraged ? new Color(255, 60, 40) : new Color(255, 150, 50);
        Color b = enraged ? new Color(255, 140, 90) : new Color(255, 210, 90);
        g.setPaint(new GradientPaint(barX, 0, a, barX, barY + 14, b));
        g.fillRoundRect(barX, barY, (int) (barW * hp), 14, 6, 6);

        // Notches so it reads as segmented armour, plus a bright frame.
        g.setColor(new Color(0, 0, 0, 120));
        for (int t = 1; t < 20; t++) {
            g.fillRect(barX + barW * t / 20, barY, 1, 14);
        }
        g.setColor(enraged ? new Color(255, 120, 100) : new Color(255, 200, 120));
        g.drawRoundRect(barX, barY, barW, 14, 6, 6);
    }

    /** Centre banner used for stage clear / stage intro. */
    private void drawBanner(Graphics g) {
        if (bannerTimer <= 0) {
            return;
        }

        String text = state == CLEARED ? clearedText() : stageName();

        g.setFont(new Font("Helvetica", Font.BOLD, 40));
        int w = g.getFontMetrics().stringWidth(text);

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, BOARD_HEIGHT / 2 - 40, BOARD_WIDTH, 70);
        g.setColor(frame % 30 < 15 ? Color.WHITE : new Color(120, 220, 255));
        g.drawString(text, (BOARD_WIDTH - w) / 2, BOARD_HEIGHT / 2 + 5);
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_HEIGHT / 2 - 40, BOARD_WIDTH - 100, 80);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_HEIGHT / 2 - 40, BOARD_WIDTH - 100, 80);

        g.setFont(new Font("Helvetica", Font.BOLD, 26));
        String message = "GAME OVER";
        int w = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (BOARD_WIDTH - w) / 2, BOARD_HEIGHT / 2 - 4);

        g.setFont(new Font("Helvetica", Font.BOLD, 15));
        String sub = "SCORE " + score + "   -   press SPACE for the title screen";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (BOARD_WIDTH - sw) / 2, BOARD_HEIGHT / 2 + 26);
    }

    // ---- update ----

    private void update() {
        if (state == GAME_OVER) {
            return;
        }

        if (state == CLEARED) {
            // Hold the banner, then hand over to whatever comes next.
            if (--bannerTimer <= 0) {
                onStageComplete();
            }
            return;
        }

        if (bannerTimer > 0) {
            bannerTimer--;
        }

        if (firing) {
            fire();
        }

        spawnForFrame();
        player.act();
        updatePowerUps();
        updateEnemies();
        updateShots();
        updateEnemyShots();
        updateExplosions();
        checkStageComplete();
    }

    private void spawnForFrame() {
        List<SpawnDetails> spawns = spawnMap.get(frame);
        if (spawns == null) {
            return;
        }

        for (SpawnDetails sd : spawns) {
            switch (sd.type) {
                case "Alien1":
                    enemies.add(new Alien1(sd.x, sd.y));
                    break;
                case "Alien2":
                    enemies.add(new Alien2(sd.x, sd.y));
                    break;
                case "Boss":
                    boss = new Boss(sd.x, sd.y);
                    enemies.add(boss);
                    break;
                case "PowerUp-SpeedUp":
                    powerups.add(new SpeedUp(sd.x, sd.y));
                    break;
                case "PowerUp-MultiShot":
                    powerups.add(new MultiShot(sd.x, sd.y));
                    break;
                default:
                    System.out.println("Unknown spawn type: " + sd.type);
                    break;
            }
        }
    }

    private void updatePowerUps() {
        List<PowerUp> done = new ArrayList<>();

        for (PowerUp powerup : powerups) {
            if (!powerup.isVisible()) {
                done.add(powerup);
                continue;
            }

            powerup.act();
            if (powerup.collidesWith(player)) {
                powerup.upgrade(player);
                score += 50;
                done.add(powerup);
            }
        }

        powerups.removeAll(done);
    }

    private void updateEnemies() {
        List<Enemy> done = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (!enemy.isVisible()) {
                done.add(enemy);
                continue;
            }

            enemy.act();
            enemyShots.addAll(enemy.fireVolley(player));

            // Ramming the player costs a life. While the player is blinking after
            // a hit nothing happens, so the ship cannot clear waves by ramming.
            if (enemy.collidesWith(player) && !player.isInvulnerable()) {
                hitPlayer();
                if (enemy != boss) {
                    explosions.add(new Explosion(enemy.getX() + enemy.getWidth() / 2,
                            enemy.getY() + enemy.getHeight() / 2));
                    enemy.die();
                    done.add(enemy);
                }
            }
        }

        enemies.removeAll(done);
    }

    private void updateShots() {
        List<Shot> done = new ArrayList<>();

        for (Shot shot : shots) {
            if (!shot.isVisible()) {
                done.add(shot);
                continue;
            }

            shot.act();

            for (Enemy enemy : enemies) {
                if (!enemy.isVisible() || !shot.collidesWith(enemy)) {
                    continue;
                }

                shot.die();
                done.add(shot);

                if (enemy.hit(1)) {
                    explosions.add(new Explosion(enemy.getX() + enemy.getWidth() / 2,
                            enemy.getY() + enemy.getHeight() / 2));
                    score += enemy.getScore();
                    enemy.die();

                    if (enemy == boss) {
                        onBossDefeated();
                    }
                }
                break;
            }
        }

        shots.removeAll(done);
    }

    private void updateEnemyShots() {
        List<EnemyShot> done = new ArrayList<>();

        for (EnemyShot shot : enemyShots) {
            if (!shot.isVisible()) {
                done.add(shot);
                continue;
            }

            shot.act();

            if (shot.collidesWith(player)) {
                shot.die();
                done.add(shot);
                hitPlayer();
            }
        }

        enemyShots.removeAll(done);
    }

    private void updateExplosions() {
        List<Explosion> done = new ArrayList<>();

        for (Explosion explosion : explosions) {
            explosion.act();
            explosion.visibleCountDown();
            if (!explosion.isVisible()) {
                done.add(explosion);
            }
        }

        explosions.removeAll(done);
    }

    private void hitPlayer() {
        if (player.isInvulnerable()) {
            return;
        }

        explosions.add(new Explosion(player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight() / 2));

        if (player.hit()) {
            state = GAME_OVER;
            if (timer != null) {
                timer.stop();
            }
            stopAudio();
        }
    }

    private void stopAudio() {
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void onBossDefeated() {
        // A cluster of bursts across the hull, then the stage ends.
        for (int i = 0; i < 6; i++) {
            explosions.add(new Explosion(
                    boss.getX() + 20 + i * 26,
                    boss.getY() + (i % 2 == 0 ? 40 : 100), 3));
        }
        clearStage();
    }

    private void checkStageComplete() {
        if (state != RUNNING) {
            return;
        }

        if (endsWithBoss()) {
            return; // stage 2 ends when the boss dies
        }

        // Run out the tail of the stage, then clear once the screen is quiet.
        if (frame >= STAGE_LENGTH && enemies.isEmpty()) {
            clearStage();
        }
    }

    protected void clearStage() {
        state = CLEARED;
        bannerTimer = 180;
        enemyShots.clear();
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                firing = false;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                if (state == GAME_OVER) {
                    stop();
                    game.loadTitle();
                    return;
                }
                // Just record that fire is held; the update loop does the shooting,
                // so movement key-repeat can never swallow it.
                firing = true;
            }

            if (DEBUG) {
                debugKeys(key);
            }
        }
    }

    /** Multi-shot: each level adds another barrel to the volley. */
    private void fire() {
        if (!player.canFire() || shots.size() >= MAX_SHOTS) {
            return;
        }

        int x = player.getX();
        int y = player.getY();

        switch (player.getShotLevel()) {
            case 1:
                shots.add(new Shot(x, y));
                break;
            case 2:
                shots.add(new Shot(x, y - 10));
                shots.add(new Shot(x, y + 10));
                break;
            case 3:
                shots.add(new Shot(x, y));
                shots.add(new Shot(x, y, -4));
                shots.add(new Shot(x, y, 4));
                break;
            default:
                shots.add(new Shot(x, y - 10));
                shots.add(new Shot(x, y + 10));
                shots.add(new Shot(x, y, -5));
                shots.add(new Shot(x, y, 5));
                break;
        }

        player.noteFired();
    }

    /** Dev-only shortcuts, so a 5 minute stage does not have to be played out. */
    private void debugKeys(int key) {
        if (key == KeyEvent.VK_9) {
            frame += 3000;
        }
        if (key == KeyEvent.VK_0) {
            frame = Math.max(frame, STAGE_LENGTH);
        }
        if (key == KeyEvent.VK_B && endsWithBoss()) {
            frame = 17399;
        }
    }
}
