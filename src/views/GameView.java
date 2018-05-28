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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import controllers.GameController;
import engine.core.factories.AbstractFactory;
import engine.core.mvc.view.PanelView;
import engine.core.mvc.view.layout.DraggableLayout;
import game.core.factories.ControllerFactory;
import game.core.factories.ViewFactory;

/**
 * The game view wraps a draggable layout around the entire game
 * 
 * @author {@literal Daniel Ricci <thedanny09@gmail.com>}
 *
 */
public final class GameView extends PanelView {

    /**
     * Creates a new instance of this class type
     */
    public GameView() {

        // Set the background color to green
        setBackground(new Color(0, 128, 0));
        
        // Set the draggable layout so that child views can be dragged around
        setLayout(new DraggableLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        //gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
                
        int x = 1000;
        for(int row = 0, rowSize = 2; row < rowSize; ++row) {
            for(int col = 0, colSize = 7; col < colSize; ++col) {
                CardPlaceholderView view = AbstractFactory.getFactory(ViewFactory.class).add(new CardPlaceholderView(), false);
                gbc.gridx = col;
                gbc.gridy = row;
                this.add(view, gbc);
                view.render();
                this.setComponentZOrder(view, col);
            }
        }
    }

    @Override public void onViewInitialized() {
        
        // Set the controller that will be used by this view
        getViewProperties().setEntity(AbstractFactory.getFactory(ControllerFactory.class).add(new GameController()));
        
        // 
        //CardPlaceholderView view = new CardPlaceholderView();
        //this.add(view);
        //view.render();
    }
}