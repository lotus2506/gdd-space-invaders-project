package gdd.scene;

import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;

/**
 * Stage 2. Denser waves leaning on the interceptor, ending in the boss fight.
 * The stage is only cleared once the boss is destroyed.
 */
public class Scene2 extends StageScene {

    public Scene2(Game game) {
        super(game);
    }

    @Override
    protected String csvPath() {
        return CSV_STAGE2;
    }

    @Override
    protected String audioPath() {
        // The starter only ships two tracks; stage 2 reuses the gameplay one.
        return "src/audio/scene1.wav";
    }

    @Override
    protected String stageName() {
        return "STAGE 2 - THE CORE";
    }

    @Override
    protected Color backdrop() {
        return new Color(24, 6, 18);
    }

    @Override
    protected boolean endsWithBoss() {
        return true;
    }

    @Override
    protected String clearedText() {
        return "GAME COMPLETE";
    }

    @Override
    protected void onStageComplete() {
        stop();
        game.loadTitle();
    }
}
