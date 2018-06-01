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
import java.awt.Insets;

import controllers.GameController;
import engine.core.factories.AbstractFactory;
import engine.core.mvc.view.PanelView;
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

        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(0, 128, 0));
        
        getViewProperties().setEntity(AbstractFactory.getFactory(ControllerFactory.class).add(new GameController()));
    }
    
    @Override public void onViewInitialized() {
        
        // Create the initial constaints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 0, 0, 0);
        
        for(int row = 0, rowSize = 2; row < rowSize; ++row) {
            
            gbc.gridy = row;

            if(row == 1)
            {
                gbc.weighty = 1;
                gbc.insets = new Insets(40, 0, 20, 0);
                gbc.fill = GridBagConstraints.VERTICAL;
            }
            
            for(int col = 0, colSize = 7; col < colSize; ++col) {

                // Do not render within columns 1 and 2
                if(row == 0 && (col == 1 || col == 2)) 
                {
                    continue;
                }
                
                CardPlaceholderView view = AbstractFactory.getFactory(ViewFactory.class).add(new CardPlaceholderView(), false);
                gbc.gridx = col;
                this.add(view, gbc);
                view.render();
            }
        }
    }

    
    @Override public void registerSignalListeners() {
    }

    @Override public void clear() {
    }
}