package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JFrame  {

    private TitleScene titleScene;
    private Scene1 scene1;
    private Scene2 scene2;

    // Whatever is on screen right now, so it can be stopped before the swap.
    private JPanel current;

    public Game() {
        initUI();
        loadTitle();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {

        setTitle("Vic Viper - GDD Project");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

    }

    /** Stops the running scene and puts the given panel on screen. */
    private void swapTo(JPanel panel) {
        if (current instanceof TitleScene) {
            ((TitleScene) current).stop();
        } else if (current instanceof Scene1) {
            ((Scene1) current).stop();
        } else if (current instanceof Scene2) {
            ((Scene2) current).stop();
        }

        getContentPane().removeAll();
        getContentPane().add(panel);
        current = panel;

        revalidate();
        repaint();
    }

    public void loadTitle() {
        // Rebuild each time so a new run starts from a clean state.
        titleScene = new TitleScene(this);
        swapTo(titleScene);
        titleScene.start();
    }

    public void loadScene1() {
        scene1 = new Scene1(this);
        swapTo(scene1);
        scene1.start();
    }

    public void loadScene2() {
        scene2 = new Scene2(this);
        swapTo(scene2);
        scene2.start();
    }
}
