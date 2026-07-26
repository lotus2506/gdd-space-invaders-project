package gdd.scene;

import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;

/**
 * Stage 1. Five minutes of open space: drifters early, interceptors mixed in as
 * the stage wears on. Clearing it hands over to Scene2.
 */
public class Scene1 extends StageScene {

    public Scene1(Game game) {
        super(game);
    }

    @Override
    protected String csvPath() {
        return CSV_STAGE1;
    }

    @Override
    protected String audioPath() {
        return "src/audio/scene1.wav";
    }

    @Override
    protected String stageName() {
        return "STAGE 1 - OUTER BELT";
    }

    @Override
    protected Color backdrop() {
        return new Color(6, 8, 26);
    }

    @Override
    protected void onStageComplete() {
        stop();
        game.loadScene2();
    }
}
