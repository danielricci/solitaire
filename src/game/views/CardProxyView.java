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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.border.Border;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.physics.CollisionListener;
import framework.core.physics.ICollide;
import framework.core.system.Application;

/**
 * This view represents the outline of a normal card view
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 *
 */
public final class CardProxyView extends PanelView {

    /**
     * The card drag events for this proxy view
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     *
     */
    private class CardDragEvents extends MouseMotionAdapter {
        
        private CardView _selectedView = null;
        
        @Override public void mouseDragged(MouseEvent event) {
            ICollide collider = _collisionListener.getCollision();
            if(collider != null) {
                PileView pile = (PileView) collider;
                _selectedView = pile.getLastCard();
                if(_selectedView != null) {
                    _selectedView.setHighlighted(true);
                }
            }
            else if(_selectedView != null) {
                _selectedView.setHighlighted(false);
                _selectedView = null;
            }
        }        
    }

    /**
     * The card selection events for this proxy view
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     *
     */
    private class CardSelectionEvents extends MouseAdapter {
        
        @Override public void mousePressed(MouseEvent event) {

            // Get the list of components that are in the parent container
            Component[] components = ((JLayeredPane) _cardView.getParent()).getComponents();
            
            // Go through the list of cards until the one that is being dragged is found. This is done
            // in a vanilla for loop so that we can take advantage of the index found
            for(int i = components.length - 1; i >= 0; --i) {
                if(components[i].equals(_cardView)) {

                    // Get the list of cardviews from the found card view that is being dragged to the end
                    List<CardView> cardViews = Arrays.asList(Arrays.copyOfRange(components, 0, i, CardView[].class));
                    
                    // Reverse the list so that when the iteration occurs, it uses the same ordering that is represente visually
                    Collections.reverse(cardViews);

                    // Go through the list of cards and add them to the layered pane within the proxy
                    for(int j = 0; j < cardViews.size(); ++j) {
                        CardProxyView proxy = cardViews.get(j).getProxyView();
                        _layeredPane.add(proxy);
                        _layeredPane.setLayer(proxy, j);
                        proxy.setBounds(new Rectangle(0, 12 * (j + 1), cardViews.get(j).getPreferredSize().width, cardViews.get(j).getPreferredSize().height));
                        proxy.setBorder(proxy._border);
                        proxy.render();
                    }
                    
                    // Position the card at the same place where the drag was attempted from, because when you
                    // add to the application it will position the component at the origin which is not desired
                    Point initialLocation = _cardView.getLocation();
                    CardProxyView.this.setBounds(new Rectangle(_cardView.getParent().getLocation().x + initialLocation.x, _cardView.getParent().getLocation().y + initialLocation.y, _layeredPane.getWidth(), _layeredPane.getHeight()));

                    // Set the border of this proxy
                    setBorder(_border);
                    
                    // Set the size of the layered pane such that it fits based on the number of cards entered
                    //_layeredPane.setPreferredSize(new Dimension(_layeredPane.getWidth(), _layeredPane.getHeight() + (cardViews.size() * 12)));
                    Application.instance.add(CardProxyView.this, 0);
                    Application.instance.repaint();

                    // Do not continue iterating, the card was found so there is nothing left to do
                    break;
                }
            }            
        }
        
        @Override public void mouseReleased(MouseEvent event) {
            
            ICollide collider = _collisionListener.getCollision();
          
            // Add the this proxy back to it's underlying card view
            CardProxyView.this.setBorder(null);
            _cardView.add(CardProxyView.this);
            
            
            if(collider instanceof PileView) {
                
                // Get a reference to the pile view that has has been collided with
                PileView pileViewCollider = (PileView) collider;
                
                // Unselect all the cards within this pile view to remove the outline xor'd highlight
                pileViewCollider.removeHighlight();
                
                // Repaint the pile view
                pileViewCollider.repaint();
//
//                
//                int initialSize = pileViewCollider.layeredPane.getComponents().length;
//                pileViewCollider.layeredPane.add(_cardView);
//                pileViewCollider.layeredPane.setLayer(_cardView, initialSize);
//                _cardView.setBounds(new Rectangle(0, pileViewCollider.CARD_OFFSET * initialSize, _cardView.getPreferredSize().width, _cardView.getPreferredSize().height));
//                
                
//                Component[] cards 
//                for(Component component : _layeredPane.getComponents()) {
//                    CardProxyView proxy = (CardProxyView) component;
//                    proxy._cardView.add(proxy);
//                    //proxy._cardView.getParent().remove(proxy._cardView);
//                }
//                
//                int x = 55;
//                
//                // Remove the card linked to this proxy
//                Container parent = _cardView.getParent();
//                parent.remove(_cardView);
//                
                // Go through all the cards underlying the main proxy and remove them from their source
                // by piping the command through the list of proxies that are stored within the layered pane
                //
                // Note: The removal is done here without an `addition`. The addition will be wh

                
                // Repaint the parent component to show the results of the list being changed
                //parent.repaint();
                
                // Get the initial size of the number of cards that exist at the place where we want to put the cards at
//                int initialSize = pileViewCollider.layeredPane.getComponents().length;
//                pileViewCollider.layeredPane.add(_cardView);
//                pileViewCollider.layeredPane.setLayer(_cardView, initialSize);
//                _cardView.setBounds(new Rectangle(0, pileViewCollider.CARD_OFFSET * initialSize, _cardView.getPreferredSize().width, _cardView.getPreferredSize().height));
//                
//                // Repaint the components involved
//                pileViewCollider.repaint();
            }

          
            
//            // Get the list of components
//            Component[] components = Arrays.copyOf(_layeredPane.getComponents(), _layeredPane.getComponentCount());
//            for(int i = 0; i < components.length; ++i) {
//                CardProxyView proxy = (CardProxyView) components[i];
//                proxy.setBorder(BorderFactory.createEmptyBorder());
//                proxy._cardView.add(proxy);
//                proxy.repaint();
//                proxy._cardView.repaint();
//            }
//            
//            _layeredPane.removeAll();
//            
//            // Put the outline back to its original state
//            setBorder(BorderFactory.createEmptyBorder());
//            Application.instance.remove(CardProxyView.this);
//            _cardView.add(CardProxyView.this);
//            
            // Repaint the components involved
            Application.instance.repaint();
            _cardView.repaint();
        }
    
        @Override public void mouseClicked(MouseEvent event) {
            if(event.getClickCount() == 2) {
                _cardView.doubleClick();
            }
        }
    }
    
    /**
     * The layered pane that holds the potential list of cards that would be dragged along-side this card vuew
     */
    private final JLayeredPane _layeredPane = new JLayeredPane();

    /**
     * The draggable listener associated to this view
     */
    private final DragListener _dragListener = new DragListener(this);

    /**
     * The collision listener associated to this view
     */
    private final CollisionListener _collisionListener = new CollisionListener(this);
    
    /**
     * The card view associated to this proxy
     */
    private CardView _cardView;
    
    /**
     * The border layout associated to this view
     */
    private final Border _border = BorderFactory.createLineBorder(Color.BLACK, 1);
    
    /**
     * Constructs a new instance of this class type
     */
    private CardProxyView() {
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        add(_layeredPane);
        
        addMouseListener(new CardSelectionEvents());
        addMouseMotionListener(new CardDragEvents());
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardView The card view to associate to this proxy
     */
    public CardProxyView(CardView cardView) {
        this();
        
        // Set the controller of this proxy to the same controller of the specified card
        _cardView = cardView;
        getViewProperties().setEntity(cardView.getViewProperties().getEntity());        
    }

    /**
     *  @return The card view that is `linked` to this proxy
     */
    public CardView getCardView() {
        return _cardView;
    }
}