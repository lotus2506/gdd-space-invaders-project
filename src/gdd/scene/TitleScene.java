package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.sprite.Animation;
import gdd.sprite.SpriteSheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Title screen, drawn rather than loaded from an image: scrolling starfield, the
 * ship flying past, the logo and the team credits.
 */
public class TitleScene extends JPanel {

    private static final String TITLE = "STAR BLASTER";
    private static final String SUBTITLE = "G D D   P R O J E C T";
    private static final String[] TEAM = {"Chaw Yadanar Oo - 6632782 ", "Min Khant Aung- 6632753"};

    private int frame = 0;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private final Game game;

    private Animation ship;

    // Starfield
    private static final int STARS = 140;
    private final int[] starX = new int[STARS];
    private final int[] starY = new int[STARS];
    private final int[] starSpeed = new int[STARS];

    public TitleScene(Game game) {
        this.game = game;
        setPreferredSize(d);
        initStars();
    }

    private void initStars() {
        Random rng = new Random(99);
        for (int i = 0; i < STARS; i++) {
            starX[i] = rng.nextInt(BOARD_WIDTH);
            starY[i] = rng.nextInt(BOARD_HEIGHT);
            starSpeed[i] = 1 + rng.nextInt(3);
        }
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        initTitle();

        timer = new Timer(1000 / FPS, new GameCycle());
        timer.start();

        initAudio();
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initTitle() {
        ship = new Animation(
                SpriteSheet.frames(SHEET, SHIP_W, SHIP_H, SCALE_FACTOR, SHIP_FRAMES), 6);
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath);

            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(4, 4, 18));
        g2.fillRect(0, 0, d.width, d.height);

        drawStars(g2);
        drawShip(g2);
        drawLogo(g2);
        drawCredits(g2);

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawStars(Graphics2D g) {
        for (int i = 0; i < STARS; i++) {
            int x = Math.floorMod(starX[i] - frame * starSpeed[i], BOARD_WIDTH);
            g.setColor(starSpeed[i] == 3 ? Color.WHITE : new Color(120, 130, 180));
            g.fillRect(x, starY[i], starSpeed[i] == 3 ? 2 : 1, starSpeed[i] == 3 ? 2 : 1);
        }
    }

    /** The ship crosses the screen on a long loop, weaving as it goes. */
    private void drawShip(Graphics2D g) {
        int span = BOARD_WIDTH + 200;
        int x = Math.floorMod(frame * 3, span) - 100;
        int y = 300 + (int) (Math.sin(frame / 40.0) * 26);

        // Engine trail.
        for (int i = 1; i <= 6; i++) {
            int alpha = 130 - i * 20;
            g.setColor(new Color(120, 180, 255, Math.max(0, alpha)));
            g.fillRect(x - i * 10, y + PLAYER_HEIGHT / 2 - 2, 8, 4);
        }

        g.drawImage(ship.getFrame(), x, y, this);
    }

    private void drawLogo(Graphics2D g) {
        g.setFont(new Font("Helvetica", Font.BOLD, 78));
        int w = g.getFontMetrics().stringWidth(TITLE);
        int x = (d.width - w) / 2;
        int y = 165;

        // Layered offsets make a cheap chrome/glow without any image asset.
        g.setColor(new Color(20, 60, 140));
        g.drawString(TITLE, x + 5, y + 5);
        g.setColor(new Color(60, 130, 220));
        g.drawString(TITLE, x + 2, y + 2);
        g.setColor(new Color(200, 235, 255));
        g.drawString(TITLE, x, y);

        g.setColor(new Color(120, 200, 255));
        g.setFont(new Font("Helvetica", Font.BOLD, 18));
        int sw = g.getFontMetrics().stringWidth(SUBTITLE);
        g.drawString(SUBTITLE, (d.width - sw) / 2, y + 34);

        // Prompt, blinking.
        if (frame % 60 < 32) {
            g.setColor(new Color(255, 220, 120));
            g.setFont(new Font("Helvetica", Font.BOLD, 30));
            String text = "Press SPACE to Start";
            int tw = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (d.width - tw) / 2, d.height - 120);
        }

        g.setColor(new Color(150, 160, 190));
        g.setFont(new Font("Helvetica", Font.PLAIN, 14));
        String controls = "ARROWS  move        SPACE  fire";
        int cw = g.getFontMetrics().stringWidth(controls);
        g.drawString(controls, (d.width - cw) / 2, d.height - 88);
    }

    private void drawCredits(Graphics2D g) {
        g.setColor(new Color(120, 130, 160));
        g.setFont(new Font("Helvetica", Font.BOLD, 13));
        g.drawString("TEAM", 24, d.height - 52);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.PLAIN, 15));
        for (int i = 0; i < TEAM.length; i++) {
            g.drawString(TEAM[i], 24, d.height - 32 + i * 18);
        }
    }

    private void update() {
        frame++;
        ship.update();
    }

    private void doGameCycle() {
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

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                // Load the first stage
                game.loadScene1();
            }

        }
    }
}
