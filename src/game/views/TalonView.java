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
import java.awt.Rectangle;
import java.util.List;

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;

import game.models.CardModel;

public final class TalonView extends PileView {

    /**
     * Constructs a new instance of this class type
     * 
     * @param cards The card models to load within this view
     */
    public TalonView(List<CardModel> cards) {
        CARD_OFFSET = 0;
        setOpaque(true);
        cards.forEach(z -> z.addListeners(this));
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }
    
    @Override public void update(AbstractEventArgs event) {
        super.update(event);

        if(event.getOperationName() == CardModel.NEXT_CARD)
        {
            CardModel model = (CardModel) event.getSource();
            if(model.isEmpty()) {
                for(Component view : _layeredPane.getComponents()) {
                    CardView cardView = (CardView) view;
                    AbstractFactory.getFactory(ViewFactory.class).remove(cardView);
                }
                _layeredPane.removeAll();
            }
            else {
                CardView cardView = AbstractFactory.getFactory(ViewFactory.class).add(new CardView(model));
                cardView.setBounds(new Rectangle(0, 0, cardView.getPreferredSize().width, cardView.getPreferredSize().height));

                cardView.render();
                _layeredPane.add(cardView, _layeredPane.getComponentCount());
                _layeredPane.setLayer(cardView, _layeredPane.getComponentCount());
            }    
        }
                
        repaint();
    }
}