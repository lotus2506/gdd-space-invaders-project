package gdd.sprite;

import java.awt.Image;

abstract public class Sprite {

    protected boolean visible;
    protected Image image;
    protected Animation animation;
    protected boolean dying;
    protected int visibleFrames = 10;

    protected int x;
    protected int y;
    protected int dx;
    protected int dy;

    public Sprite() {
        visible = true;
    }

    // Default behaviour is just to advance the animation. Sprites that also move
    // (Player, Enemy, Shot, PowerUp) override this and call super.act().
    public void act() {
        if (animation != null) {
            animation.update();
        }
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getWidth() {
        Image img = getImage();
        return img == null ? 0 : img.getWidth(null);
    }

    public int getHeight() {
        Image img = getImage();
        return img == null ? 0 : img.getHeight(null);
    }

    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        if (this.getImage() == null || other.getImage() == null) {
            return false;
        }
        return this.getX() < other.getX() + other.getWidth()
                && this.getX() + this.getWidth() > other.getX()
                && this.getY() < other.getY() + other.getHeight()
                && this.getY() + this.getHeight() > other.getY();
    }

    public void die() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void visibleCountDown() {
        if (visibleFrames > 0) {
            visibleFrames--;
        } else {
            visible = false;
        }
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        if (animation != null && animation.getFrame() != null) {
            return animation.getFrame();
        }
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return this.dying;
    }
}
