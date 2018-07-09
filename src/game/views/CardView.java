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
import java.util.Arrays;

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.communication.internal.signal.arguments.ModelEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DraggableListener;
import game.application.Application;
import game.controllers.CardController;
import game.models.CardModel;

public final class CardView extends PanelView {

    private final CardController _controller = AbstractFactory.getFactory(ControllerFactory.class).add(new CardController());
    
    private final DraggableListener _draggableListener = new DraggableListener(this);
        
    /**
     * Creates a new instance of this class type
     */
    public CardView(CardModel card) {
        setPreferredSize(new Dimension(71, 96));
        setOpaque(false);

        _controller.setCard(card);
        card.addListeners(this);
    }
    
    @Override public void onViewInitialized() {
        addMouseListener(new MouseAdapter() {
            
            private Container _parent;
            private int _index = 0;
            
            @Override public void mousePressed(MouseEvent e) {
                _parent = CardView.this.getParent();
                Component[] components =_parent.getComponents();
                
                for(int i = components.length - 1; i >= 0; --i, ++_index) {
                    if(components[i].equals(CardView.this)) {
                  //      Arrays.copyOfRange(components, i, components.length, );
                        break;
                    }
                }
                
                //CardModel[] cardViews = 
                //PileView view = new PileView(cardViews)
                
                //Application.instance().add(CardView.this);
                //_parent.remove(CardView.this);
            }
            
            @Override public void mouseReleased(MouseEvent e) {
                
                // Remove the application held card
                Application.instance().remove(CardView.this);
                Application.instance().repaint();
                
                // Put the card back into the position where it originally was
                _parent.add(CardView.this, _index);
                
                CardView.this.setBounds(new Rectangle(0, 12 * _index, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));
                
                // Reset the index back to it's default position
                _index = 0;
            }
        });
    }
    
    @Override public void render() {
        super.render();
        _controller.refresh();
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