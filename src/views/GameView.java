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

        // Create a globally accessible game controller
        AbstractFactory.getFactory(ControllerFactory.class).add(new GameController(), true);
    }

    /**
     * Renders an instance of the card placeholder view
     */
    private CardPlaceholderView renderCardPlaceholderView() {
        CardPlaceholderView cardPlaceholderView = AbstractFactory.getFactory(ViewFactory.class).add(new CardPlaceholderView());
        this.add(cardPlaceholderView, _constraints);
        cardPlaceholderView.render();
        return cardPlaceholderView;
    }


    /**
     * Renders a singular instance of the stock view
     */
    private StockView renderStockView() {
        StockView stockView = AbstractFactory.getFactory(ViewFactory.class).add(new StockView(), true);
        this.add(stockView, _constraints);
        stockView.render();
        return stockView;
    }
    
    private TalonView renderTalonView() {
        TalonView talonView = AbstractFactory.getFactory(ViewFactory.class).add(new TalonView(), true);
        this.add(talonView);
        talonView.render();
        return talonView;
    }
    
     private void handleRendering() {
        
        CardPlaceholderView placeHolder = null;
        
        // Render a placeholder view so long as the constraints are not on the first row and occupying the first 3 cells
        if(!(_constraints.gridy == 0 && (_constraints.gridx >= 0 && _constraints.gridx <= 2))) {
            placeHolder = renderCardPlaceholderView();
        }
        
        if(_constraints.gridy == 0)
        {
            switch(_constraints.gridx)
            {
            case 0:
            {
                renderStockView();
                break;
            }
            case 1:
            {
                renderTalonView();
                break;
            }
            }
        }
        else
        {
            switch(_constraints.gridx) {
            case 0:
            case 1:
            case 2:{
                PileView pileView = AbstractFactory.getFactory(ViewFactory.class).add(new PileView(1));
                placeHolder.add(pileView);
                pileView.render();
                break;
            }
            }
        }
    }

    @Override public void onViewInitialized() {

        for(int row = 0; row < _rowSize; ++row) {
            _constraints.gridy = row;

            // If the rendering pass is on row 2 (0th based) then make sure the constraints are properly reflected
            if(_constraints.gridy ==  1) {
                _constraints.weighty = 1;
                _constraints.insets = new Insets(40, 0, 20, 0);
                _constraints.fill = GridBagConstraints.VERTICAL;
            }
            
            for(int col = 0; col < _columnSize; ++col) {
                _constraints.gridx = col;

                // Perform a render based on the updated constaints
                handleRendering();
            }
        }
    }
    
    @Override public void registerSignalListeners() {
    }

    @Override public void clear() {
    }
}