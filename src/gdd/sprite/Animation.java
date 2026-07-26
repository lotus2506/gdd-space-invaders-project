package gdd.sprite;

import java.awt.Image;

/**
 * A frame cycle. Sprites hold one of these and tick it once per game frame;
 * Sprite.getImage() then returns whichever frame is current.
 */
public class Animation {

    private final Image[] frames;
    private final int ticksPerFrame;
    private final boolean loop;

    private int tick = 0;
    private int index = 0;
    private boolean finished = false;

    public Animation(Image[] frames, int ticksPerFrame) {
        this(frames, ticksPerFrame, true);
    }

    public Animation(Image[] frames, int ticksPerFrame, boolean loop) {
        this.frames = frames;
        this.ticksPerFrame = Math.max(1, ticksPerFrame);
        this.loop = loop;
    }

    public void update() {
        if (finished || frames.length <= 1) {
            return;
        }

        tick++;
        if (tick < ticksPerFrame) {
            return;
        }

        tick = 0;
        index++;
        if (index >= frames.length) {
            if (loop) {
                index = 0;
            } else {
                index = frames.length - 1;
                finished = true;
            }
        }
    }

    public Image getFrame() {
        return frames.length == 0 ? null : frames[index];
    }

    public boolean isFinished() {
        return finished;
    }

    public int getFrameCount() {
        return frames.length;
    }

    public void reset() {
        tick = 0;
        index = 0;
        finished = false;
    }
}
