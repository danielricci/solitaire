/**
 * Daniel Ricci <thedanny09@gmail.com>
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

package views;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import controllers.GameController;
import engine.core.factories.AbstractFactory;
import engine.core.mvc.view.TransparentPanelView;
import game.core.factories.ControllerFactory;
import game.core.factories.ViewFactory;

/**
 * This view handles the stock. The stock is a view that contains the leftover cards after the cards have been 
 * properly distributed on the board. Clicking on the stock view will display a card within another view.
 *
 * @author Daniel Ricci <thedanny09@gmail.com>
 *
 */
public final class PileView extends TransparentPanelView {

    private final JLayeredPane _layeredPane = new JLayeredPane();

    /**
     * Hold a reference to the game controller
     */
    private GameController _gameController = AbstractFactory.getFactory(ControllerFactory.class).get(GameController.class);

    /**
     * Creates a new instance of this class type
     */
    public PileView(int initialCapacity) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(71, 96));
        add(_layeredPane);
        for(int i = 0; i < initialCapacity; ++i) {
            CardView view = AbstractFactory.getFactory(ViewFactory.class).add(new CardView());
            Rectangle rect = new Rectangle(0, 12 * i, view.getPreferredSize().width, view.getPreferredSize().height);
            view.setBounds(rect);
            _layeredPane.add(view, new Integer(i));
            view.render();
        } 
    }

    @Override public void onViewInitialized() {
    }

    @Override public void clear() {       
    }

    @Override public void registerSignalListeners() {
    }
}