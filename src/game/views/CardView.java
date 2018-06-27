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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.communication.internal.signal.arguments.ModelEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DraggableListener;
import game.application.Application;
import game.controllers.GameController;
import game.models.CardModel;

/**
 * A single card view represents a draggable and collision enabled entity within the game
 * 
 * @author {@literal Daniel Ricci <thedanny09@gmail.com>}
 *
 */
public class CardView extends PanelView {

    /**
     * Creates a new instance of this class type
     */
    public CardView() {

        // Add draggable functionality to this view
        new DraggableListener(this);
        
        setPreferredSize(new Dimension(71, 96));

        // Register to an available card
        AbstractFactory.getFactory(ControllerFactory.class).get(GameController.class).registerPileCard(this);
    }
    
    @Override public void onViewInitialized() {
        addMouseListener(new MouseAdapter() {
            
            private Container _realParent;
            private int _index = 0;
            
            @Override public void mousePressed(MouseEvent e) {
                _realParent = CardView.this.getParent();
                Component[] components =_realParent.getComponents();
                for(int i = components.length - 1; i >= 0; --i, ++_index) {
                    if(components[i].equals(CardView.this)) {
                        break;
                    }
                }
                
                Application.instance().add(CardView.this);
                _realParent.remove(CardView.this);
                
                Application.instance().repaint();
                _realParent.repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                
                // Remove the application held card
                Application.instance().remove(CardView.this);
                Application.instance().repaint();
                
                // Put the card back into the position where it originally was
                _realParent.add(CardView.this, _index);
                
                CardView.this.setBounds(new Rectangle(0, 12 * _index, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));
                
                // Reset the index back to it's default position
                _index = 0;
            }
        });
    }
 
    @Override public void update(AbstractEventArgs event) {
        
        super.update(event);
        
        if(event instanceof ModelEventArgs) {
            ModelEventArgs args = (ModelEventArgs) event;
            if(args.getSource() instanceof CardModel) {
                addRenderableContent((CardModel)args.getSource()); 
            }
        }
        
        repaint();
    }
}