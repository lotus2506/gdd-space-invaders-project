package gdd.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Clips animation frames out of a sprite sheet.
 *
 * The sheets ripped from the NES are laid out as fixed cells on a flat backdrop
 * (black cell over 1P blue / 2P red), so a frame is just a rectangle. Sheets are
 * cached because every sprite of the same kind clips from the same file.
 */
public class SpriteSheet {

    private static final Map<String, BufferedImage> SHEETS = new HashMap<>();

    // Backdrop colours used by the rip. Everything matching these becomes transparent.
    private static final int[] BACKDROP = {0xFF000000, 0xFF003663, 0xFF790000};

    private SpriteSheet() {
        // Prevent instantiation
    }

    private static BufferedImage load(String path) {
        BufferedImage sheet = SHEETS.get(path);
        if (sheet != null) {
            return sheet;
        }

        try {
            BufferedImage raw = ImageIO.read(new File(path));
            sheet = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < raw.getHeight(); y++) {
                for (int x = 0; x < raw.getWidth(); x++) {
                    int argb = raw.getRGB(x, y) | 0xFF000000;
                    sheet.setRGB(x, y, isBackdrop(argb) ? 0x00000000 : argb);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load sprite sheet " + path + ": " + e.getMessage());
            sheet = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        SHEETS.put(path, sheet);
        return sheet;
    }

    private static boolean isBackdrop(int argb) {
        for (int c : BACKDROP) {
            if (argb == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clips one frame and scales it by the given factor with nearest-neighbour,
     * which keeps the pixel art crisp (SCALE_SMOOTH would blur it).
     */
    public static Image frame(String path, int x, int y, int w, int h, int scale) {
        return frame(path, x, y, w, h, w * scale, h * scale);
    }

    /**
     * Clips one frame and scales it into an exact target box. Frames of differing
     * source sizes can be normalised this way so an animation does not jump around.
     */
    public static Image frame(String path, int x, int y, int w, int h, int targetW, int targetH) {
        BufferedImage sheet = load(path);

        // Stay inside the sheet even if a rectangle is mistyped, rather than throwing
        // out of a sprite constructor mid-game.
        int cx = Math.max(0, Math.min(x, sheet.getWidth() - 1));
        int cy = Math.max(0, Math.min(y, sheet.getHeight() - 1));
        int cw = Math.max(1, Math.min(w, sheet.getWidth() - cx));
        int ch = Math.max(1, Math.min(h, sheet.getHeight() - cy));

        BufferedImage sub = sheet.getSubimage(cx, cy, cw, ch);
        BufferedImage out = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(sub, 0, 0, targetW, targetH, null);
        g.dispose();
        return out;
    }

    /**
     * Clips a run of frames laid out on a regular grid, all the same size.
     * rects is a flat list of x,y pairs sharing one width/height.
     */
    public static Image[] frames(String path, int w, int h, int scale, int... xy) {
        Image[] out = new Image[xy.length / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = frame(path, xy[i * 2], xy[i * 2 + 1], w, h, w * scale, h * scale);
        }
        return out;
    }
}
