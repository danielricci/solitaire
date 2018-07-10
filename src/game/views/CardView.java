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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

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
       
    private final JLayeredPane _layeredPane = new JLayeredPane();
    
    /**
     * Creates a new instance of this class type
     */
    public CardView(CardModel card) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(71, 96));
        setOpaque(false);
        add(_layeredPane);
        _controller.setCard(card);
        card.addListeners(this);
    }
    
    @Override public void onViewInitialized() {
        addMouseListener(new MouseAdapter() {
            
            private JLayeredPane _parent;
            private int _index = 0;
            
            @Override public void mousePressed(MouseEvent e) {
                _parent = (JLayeredPane) CardView.this.getParent();
                Component[] components =_parent.getComponents();
                
                mainLabel : for(int i = components.length - 1; i >= 0; --i, ++_index) {
                    if(components[i].equals(CardView.this)) {
                        List<CardView> cardViews = Arrays.asList(Arrays.copyOfRange(components, 0, i, CardView[].class));
                        Collections.reverse(cardViews);
                        
                        for(int j = 0; j < cardViews.size(); ++j) {
                            _parent.remove(cardViews.get(j));
                            _layeredPane.add(cardViews.get(j), j);
                            _layeredPane.setLayer(cardViews.get(j), j);
                            cardViews.get(j).setBounds(new Rectangle(0, 12 * (j + 1), cardViews.get(j).getPreferredSize().width, cardViews.get(j).getPreferredSize().height));
                        }

                        _parent.remove(CardView.this);
                        
                        Point initialLocation = CardView.this.getLocation();
                        CardView.this.setBounds(new Rectangle(_parent.getParent().getLocation().x + initialLocation.x, _parent.getParent().getLocation().y + initialLocation.y, _layeredPane.getWidth(), _layeredPane.getHeight()));
                        Application.instance().add(CardView.this, 0);
                        Application.instance().repaint();
                        
                        break mainLabel;
                    }
                }
            }
            
            @Override public void mouseReleased(MouseEvent e) {
                
                // Get the list of components associated to the CardView.this reference. This list represents all the children associated
                // to the said CardView.this reference.
                List<Component> components = Arrays.asList(CardView.this._layeredPane.getComponents());
                
                // Reverse the list because layerd panes associate objects closer to layer 0 as being closer to the screen.
                Collections.reverse(components);

                // Get the initial count of the number of components in the layered pane.  It is assumed that they hold only
                // CardView or the calculations wont work properly.
                // This number represents the number of cards that exist within the list after the drag operation had occured, so if
                // you have a pile with 5 cards and you drag three out, then you would be left with 2 cards in the parent, CardView.this would
                // represent the card actually being dragged, and the layered pane associated to CardView.this would attached itself.
                int initialSize = _parent.getComponents().length;
                
                // Add this card view to the pane
                _parent.add(CardView.this, initialSize);
                _parent.setLayer(CardView.this, initialSize);
                CardView.this.setBounds(new Rectangle(0, 12 * initialSize, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));
                Application.instance().remove(CardView.this);
                ++initialSize;
                
                // Take the remainder of the components held by this card and put them back into the parents pane
                for(int i = 0; i < components.size(); ++i) {
                    _parent.add(components.get(i), i + initialSize);
                    _parent.setLayer(components.get(i), i + initialSize);
                    components.get(i).setBounds(new Rectangle(0, 12 * (i + initialSize), components.get(i).getPreferredSize().width, components.get(i).getPreferredSize().height));
                }
                
                
                // Clear the card views that were added within this cards' layered pane
                CardView.this._layeredPane.removeAll();
                
                // Repaint the components accordingly
                Application.instance().repaint();
                _parent.repaint();
                
                // Reset all reference
                _index = 0;
                _parent = null;
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