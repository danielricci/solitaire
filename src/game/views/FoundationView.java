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

import java.awt.Component;
import java.awt.Dimension;

import framework.api.IView;
import framework.communication.internal.signal.arguments.EventArgs;

import game.controllers.CardController;

public class FoundationView extends TableauView {
    
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
    
    @Override public void update(EventArgs event) {
        super.update(event);
        repaint();
    }

    @Override public boolean isValidCollision(Component source) {
        if(layeredPane.getComponentCount() == 0) {
            return ((IView)source).getViewProperties().getEntity(CardController.class).getCard().getCardEntity().isAceCard();
        }
        else {
            CardView thisCardView = (CardView) layeredPane.getComponent(0);
            return thisCardView.isValidCollision(source);
        }
    }
}