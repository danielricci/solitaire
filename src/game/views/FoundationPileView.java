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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import framework.api.IView;
import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.physics.ICollidable;

import game.controllers.CardController;
import game.entities.FoundationCardEntity;

public class FoundationPileView extends AbstractPileView implements ICollidable {

    /**
     * Creates a new instance of this class type
     */
    public FoundationPileView() {
        
        // The background the the opaqueness of this view
        // must be set this way to achieve the proper xor effect
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        
        CARD_OFFSET = 0;
        addRenderableContent(new FoundationCardEntity());
    }

    @Override public void preProcessGraphics(Graphics context) {
        if(getIsHighlighted() && layeredPane.getComponentCount() == 0) {
            context.setXORMode(Color.WHITE);
        }
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }

    @Override public void update(EventArgs event) {
        super.update(event);
        repaint();
    }

    @Override public boolean isValidCollision(Component source) {
        if (layeredPane.getComponentCount() == 0) {
            return ((IView) source).getViewProperties().getEntity(CardController.class).getCard().getCardEntity().isAceCard();
        } else {
            CardView thisCardView = (CardView) layeredPane.getComponent(0);
            return thisCardView.isValidCollision(source);
        }
    }
}