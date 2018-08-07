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

import java.awt.Component;
import java.awt.Dimension;

import framework.api.IView;
import framework.communication.internal.signal.arguments.AbstractEventArgs;

import game.controllers.CardController;
import game.models.CardModel;

public class FoundationView extends PileView {
    
    /**
     * Creates a new instance of this class type
     */
    public FoundationView() {
        CARD_OFFSET = 0;
        setOpaque(true);
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }
    
    @Override public void update(AbstractEventArgs event) {
        super.update(event);
        repaint();
    }

    @Override public boolean isValidCollision(Component source) {

        IView sourceView = (IView) source;
        boolean valid = false;
        CardModel sourceCardModel = sourceView.getViewProperties().getEntity(CardController.class).getCard();
        
        if(_layeredPane.getComponentCount() == 0) {
            valid = sourceCardModel.getCardEntity().isAceCard();
        }
        else {
            CardView thisCardView = (CardView) _layeredPane.getComponent(0);
            CardController thisCardViewController = thisCardView.getViewProperties().getEntity(CardController.class);

            valid = thisCardViewController.isValidFoundationMove(sourceCardModel);
        }
        
        return valid;
    }
}