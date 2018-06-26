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
     * The row size of this view
     */
    private final int _rowSize = 2;

    /**
     * The column size of this view
     */
    private final int _columnSize = 7;

    /**
     * The constaints associated to this view
     */
    private final GridBagConstraints _constraints = new GridBagConstraints();

    /**
     * The game controller
     */
    private final GameController _gameController = AbstractFactory.getFactory(ControllerFactory.class).add(new GameController(), true);
    
    /**
     * Creates a new instance of this class type
     */
    public GameView() {

        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(0, 128, 0));

        // Configure constraint initial values
        _constraints.weightx = 1.0;
        _constraints.weighty = 0;
        _constraints.anchor = GridBagConstraints.NORTH;
        _constraints.insets = new Insets(10, 0, 0, 0);
    }

    @Override public void onViewInitialized() {

        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        
        for(int row = 0; row < _rowSize; ++row) {
            _constraints.gridy = row;

            // If the rendering pass is on row 2 (0th based) then make sure the constraints are properly reflected
            if(_constraints.gridy ==  1) {
                _constraints.weighty = 1;
                _constraints.insets = new Insets(20, 0, 20, 0);
                _constraints.fill = GridBagConstraints.VERTICAL;
            }

            for(int col = 0; col < _columnSize; ++col) {
                _constraints.gridx = col;
                
                if(_constraints.gridy == 0) {
                    switch(_constraints.gridx) {
                    case 0: {
                        
                        StockView stockView = viewFactory.add(new StockView(), true);
                        this.add(stockView, _constraints);
                        stockView.render();
                        break;
                    }
                    case 1: {
                        
                        TalonView talonView = viewFactory.add(new TalonView(), true);
                        this.add(talonView, _constraints);
                        talonView.render();
                        break;
                    }
                    }
                }
                else {
                    PileView view = viewFactory.add(new PileView(_constraints.gridx + 1));
                    this.add(view, _constraints);
                    view.render();
                }
            }
        }
        
        _gameController.registerCards(viewFactory.get(TalonView.class));
    }

    @Override public void registerSignalListeners() {
    }
    @Override public void clear() {
    }
}