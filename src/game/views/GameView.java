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

package game.views;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import game.models.CardModel;

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
     * Creates a new instance of this class type
     */
    public GameView() {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(0, 128, 0));
    }
    
    @Override public void onViewInitialized() {

        // Configure constraint initial values
        _constraints.weightx = 1.0;
        _constraints.anchor = GridBagConstraints.NORTH;
        
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        
        // Create the total list of cards
        List<CardModel> cards = CardModel.newInstances();
        
        for(int row = _rowSize - 1; row >= 0; --row) {
            _constraints.gridy = row;

            if(_constraints.gridy ==  1) {
                _constraints.weighty = 1;
                _constraints.insets = new Insets(20, 0, 20, 0);
                _constraints.fill = GridBagConstraints.VERTICAL;
            }
            else {
                _constraints.weighty = 0;
                _constraints.insets = new Insets(10, 0, 0, 0);
            }

            for(int col = 0; col < _columnSize; ++col) {
                _constraints.gridx = col;
                
                if(_constraints.gridy == 0) {
                    switch(_constraints.gridx) {
                    case 0: {
                        
                        // Create the stock view 
                        StockView stockView = viewFactory.add(new StockView(cards), true);
                        this.add(stockView, _constraints);
                        break;
                    }
                    case 1: {
                        
                        // Create the talon view
                        TalonView talonView = viewFactory.add(new TalonView(), true);
                        cards.forEach(z -> z.addListeners(talonView));
                        this.add(talonView, _constraints, 0);
                        break;
                    }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        // Create the foundation view
                        FoundationView foundationView = viewFactory.add(new FoundationView());
                        this.add(foundationView, _constraints);
                    break;
                    }
                }
                else {
                    
                    List<CardModel> subList = cards.subList(0, _constraints.gridx + 1);
                    PileView view = viewFactory.add(new PileView(new ArrayList<CardModel>(subList)));
                    subList.clear();
                    
                    this.add(view, _constraints);
                }
            }
        }
    }
}