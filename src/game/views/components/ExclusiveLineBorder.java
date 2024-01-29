/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci {@literal <thedanny09@icloud.com>}
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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