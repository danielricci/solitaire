/**
 *   Ricci <thedanny09@gmail.com>
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

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.core.mvc.view.layout.DragListener;
import framework.core.physics.CollisionListener;

import game.models.CardModel;

public final class TalonView extends FoundationView {

    @Override public void onViewInitialized() {
        super.onViewInitialized();
        setOpaque(false);
        
        new DragListener(this);
        new CollisionListener(this);
    }
    
    @Override public void update(AbstractEventArgs event) {
        super.update(event);
        
        // Look within the layered pane, and see if there is a card view associated to this card model, if there
        // is not then create one and add it to the layered pane. Dont forget to cycle the cards!
        //if(Arrays.asList(_layeredPane.getComponents())
        //CardView view = new CardView((CardModel)event.getSource());
        
        addRenderableContent((CardModel)event.getSource());
        repaint();
    }
}