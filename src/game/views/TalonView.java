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

import java.awt.Dimension;
import java.util.Collection;

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.communication.internal.signal.arguments.ModelEventArgs;
import framework.core.mvc.view.PanelView;
import game.models.CardModel;

public final class TalonView extends PanelView {

    /**
     * Creates a new instance of this class type
     */
    public TalonView(Collection<CardModel> cards) {
        setPreferredSize(new Dimension(71, 96));
        setOpaque(false);
        
        cards.forEach(z -> z.addListeners(this));
    }
    
    @Override public void onViewInitialized() {
        super.onViewInitialized();
        
    }
    
    @Override public void update(AbstractEventArgs event) {
        super.update(event);
        if(event.getOperationName() == CardModel.EVENT_NEXT_CARD && event instanceof ModelEventArgs && event.getSource() instanceof CardModel) {
            CardModel cardModel = (CardModel)event.getSource();
            addRenderableContent(cardModel);
        }
        repaint();
    }
}