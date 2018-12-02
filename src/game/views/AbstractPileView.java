/**
 * Daniel Ricci <thedanny09@icloud.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package game.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLayeredPane;

import framework.core.mvc.view.PanelView;

public abstract class AbstractPileView extends PanelView {
        
    /**
     * Specifies the offset of each card within this view
     */
    public int CARD_OFFSET = 12;
    
    /**
     * The layered pane that holds the list of cards
     */
    protected final JLayeredPane layeredPane = new JLayeredPane();

    /**
     * Constructs a new instance of this class type
     */
    protected AbstractPileView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, this.getPreferredSize().height));
        setOpaque(false);
        add(layeredPane, BorderLayout.CENTER);
    }

    public void removeHighlight() {
        for(Component comp : layeredPane.getComponents()) {
            CardView cardView = (CardView)comp;
            cardView.setIsHighlighted(false);
        }
        repaint();
    }
    
    public CardView getLastCard() {
        if(layeredPane.getComponentCount() == 0) {
            return null;
        }
        return (CardView)layeredPane.getComponents()[0];
    }
    
    @Override public void render() {
        super.render();
        for(Component component : layeredPane.getComponents()) {
            if(component instanceof CardView) {
                CardView view = (CardView) component;
                view.render();
            }
        }
        repaint();
    }
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        String header = "========" + this.getClass().getSimpleName().toUpperCase() + "========";
        builder.append(header + System.getProperty("line.separator"));
        
        for(Component comp : layeredPane.getComponents()) {
            builder.append(comp + System.getProperty("line.separator"));
        }
        builder.append(new String(new char[header.length()]).replace("\0", "="));
        
        return builder.toString();
    }
}