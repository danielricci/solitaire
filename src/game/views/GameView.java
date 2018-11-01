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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

import game.controllers.CardController;
import game.models.CardModel;

/**
 * The game view wraps a draggable layout around the entire game
 * 
 * @author {@literal Daniel Ricci <thedanny09@icloud.com>}
 *
 */
public final class GameView extends PanelView {

    /**
     * Creates a new instance of this class type
     */
    public GameView() {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(0, 128, 0));
    
        // Configure constraint initial values
        final int _rowSize = 2;
        final int _columnSize = 7;
        GridBagConstraints gameConstraints = new GridBagConstraints();
        gameConstraints.weightx = 1.0;
        gameConstraints.anchor = GridBagConstraints.NORTH;
        
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        
        // Create the total list of cards
        List<CardModel> cards = CardModel.newInstances();
        
        for(int row = _rowSize - 1; row >= 0; --row) {
            gameConstraints.gridy = row;

            if(gameConstraints.gridy ==  1) {
                gameConstraints.weighty = 1;
                gameConstraints.insets = new Insets(20, 0, 20, 0);
                gameConstraints.fill = GridBagConstraints.VERTICAL;
            }
            else {
                gameConstraints.weighty = 0;
                gameConstraints.insets = new Insets(10, 0, 0, 0);
            }

            for(int col = _columnSize - 1; col >= 0; --col) {
                gameConstraints.gridx = col;
                
                if(gameConstraints.gridy == 0) {
                    switch(gameConstraints.gridx) {
                    case 0: {
                        
                        // Create the stock view 
                        StockView stockView = viewFactory.add(new StockView(), true);
                        this.add(stockView, gameConstraints);
                        break;
                    }
                    case 1: {
                        
                        // Create the talon view
                        TalonView talonView = viewFactory.add(new TalonView(cards), true);
                        this.add(talonView, gameConstraints, 0);
                        break;
                    }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        // Create the foundation view
                        FoundationView foundationView = viewFactory.add(new FoundationView());
                        this.add(foundationView, gameConstraints);
                    break;
                    }
                }
                else {
                    
                    List<CardModel> subList = cards.subList(0, gameConstraints.gridx + 1);
                    TableauView view = viewFactory.add(new TableauView(new ArrayList<CardModel>(subList)));
                    subList.clear();
                    
                    this.add(view, gameConstraints);
                }
            }
        }
    }
    
    public static boolean IsGameWinner() {
        boolean winner = true;
        for(FoundationView foundationView : AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationView.class)) {
            if(foundationView.layeredPane.getComponentCount() == 13) { 
                CardView cardView = (CardView) foundationView.layeredPane.getComponent(0);
                if(!cardView.getViewProperties().getEntity(CardController.class).isKing()) {
                    winner = false;
                    break;
                }
            }
            else {
                winner = false;
                break;
            }
        }

        return winner;
    }
}