package game.views.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.LineBorder;

/**
 * Creates a line border that attempts to render it's edges by applying an XOR bitmask with 
 * the underlying pixel color (per-pixel precision)
 *
 * @author Daniel Ricci {@literal <thedanny09@gmail.com> }
 *
 */
public class ExclusiveLineBorder extends LineBorder {

    /**
     * Constructs a new instance of this class type
     *
     * @param thickness The thickness of the borders
     */
    public ExclusiveLineBorder(int thickness) {
        super(Color.BLACK, thickness, true);
    }

    @Override public void paintBorder(Component component, Graphics graphic, int x, int y, int width, int height) {
        graphic.setXORMode(Color.WHITE);
        super.paintBorder(component, graphic, x, y, width, height);
    }
}