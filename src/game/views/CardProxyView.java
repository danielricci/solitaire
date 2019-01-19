/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci <thedanny09@icloud.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package game.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.navigation.MenuBuilder;
import framework.core.physics.CollisionListener;
import framework.core.physics.ICollidable;
import framework.core.system.Application;
import framework.utils.MouseListenerEvent;
import framework.utils.MouseListenerEvent.SupportedActions;
import framework.utils.globalisation.Localization;

import game.controllers.MovementRecorderController;
import game.menu.ExitMenuItem;
import game.menu.NewGameMenuItem;
import game.views.components.ExclusiveLineBorder;

import resources.LocalizationStrings;

/**
 * This view represents the outline of a normal card view
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 *
 */
public final class CardProxyView extends PanelView {

    /**
     * The current bounds that have been set to this proxy
     */
    private Rectangle _bounds = null;
    
    /**
     * The card drag events for this proxy view
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     *
     */
    private class CardDragEvents extends MouseMotionAdapter {
        
        private PanelView _collidedView = null;
        
        @Override public void mouseDragged(MouseEvent event) {
            
            if(!isEnabled()) {
                return;
            }
            
            ICollidable collider = _collisionListener.getCollision();
            if(collider != null) {
                
                // Determine what the collision was with. Either it was with a card, or
                // it is with the Foundation or an empty PileView
                PanelView collidedView = (PanelView)collider;
                
                if(collidedView instanceof AbstractPileView) {
                    CardView card = ((AbstractPileView)collidedView).getLastCard();
                    if(card != null) {
                        collidedView = card;
                    }    
                }
                
                // If there was something that was already collided with
                // then remove the highlight
                if(_collidedView != null) {
                    _collidedView.setIsHighlighted(false);
                }

                // Set the newly collided view
                _collidedView = collidedView;
                _collidedView.setIsHighlighted(true);
            }
            else {
                if(_collidedView != null) {
                    _collidedView.setIsHighlighted(false);
                }
                _collidedView = null;
            }
        }
    }

    /**
     * The card selection events for this proxy view
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     *
     */
    private class CardSelectionEvents extends MouseListenerEvent {
        
        public CardSelectionEvents() {
            super(SupportedActions.LEFT);
        }
        
        @Override public void mousePressed(MouseEvent event) {

            super.mousePressed(event);
            if(event.isConsumed()) {
                return;
            }
            
            if(!isEnabled()) {
                return;
            }
            
            // Indicates if the auto move had occured successfully
            boolean hasAutomoveWorked = false;
            
            
            // If the click count is two then perform the double click event of the underlying card
            // and go no further. This will handle cases where the outline mode is enabled, and the user
            // double clicks to put a potential card in the foundation
            if(event.getClickCount() == 2) {
                
                // Get a reference to the parent of the underlying card view
                Component parent = _cardView.getParent();
                
                // Perform the double click operation
                hasAutomoveWorked = _cardView.performCardAutoMovement();
                if(hasAutomoveWorked) {
                    // Disable the drag listener, it should be re-enabled on mouse released iff this component
                    // is enabled
                    _dragListener.setEnabled(false);
                    
                    // Perform a repaint of the parent
                    parent.repaint();
                }
            }
        
            if(!hasAutomoveWorked) {

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
    
                        // Fixes a bug where the layered pane is chopped from the waist down
                        _layeredPane.setSize(_layeredPane.getWidth(), _layeredPane.getHeight() + (cardViews.size() * 12));
                       
                        // Go through the list of cards and add them to the layered pane within the proxy
                        for(int j = 0; j < cardViews.size(); ++j) {
                            CardProxyView proxy = cardViews.get(j).getProxyView();
                            _layeredPane.add(proxy);
                            _layeredPane.setLayer(proxy, j);
                            
                            // The bounds here contains `-1` because I want the border to be perfectly overlapped
                            proxy.setBounds(new Rectangle(-1, 12 * (j + 1), cardViews.get(j).getPreferredSize().width, cardViews.get(j).getPreferredSize().height + _layeredPane.getHeight()));
                        }
    
                        // Position the card at the same place where the drag was attempted from, because when you
                        // add to the application it will position the component at the origin which is not desired
                        Point initialLocation = _cardView.getLocation();
                        
                        // Update the bounds of the card view
                        _bounds = new Rectangle(_cardView.getParent().getParent().getLocation().x + initialLocation.x + 1, _cardView.getParent().getParent().getLocation().y + initialLocation.y + 1, _layeredPane.getWidth(), _layeredPane.getHeight());
                        
                        // Get a reference to the game view and status view, and add the card into the proper
                        // z-order so that it appears underneath the status bar, but over everything else in the game
                        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
                        GameView gameView = viewFactory.get(GameView.class);
                        StatusBarView statusBarView = viewFactory.get(StatusBarView.class);

                        gameView.add(CardProxyView.this, gameView.getComponentZOrder(statusBarView) + 1);
                        gameView.repaint();

                        // Do not continue iterating, the card was found so there is nothing left to do
                        break;
                    }
                }            
            }
        }
        
        @Override public void mouseReleased(MouseEvent event) {
            
            super.mouseReleased(event);
            if(event.isConsumed()) {
                return;
            }
            
            if(!isEnabled()) {
                return;
            }
            
            ICollidable collider = _collisionListener.getCollision();
            _bounds = null;
            
            if(collider != null) {
            
                // Get a reference to the pile view that has has been collided with
                AbstractPileView pileViewCollider = (AbstractPileView) collider;
                
                // Get the before movement type to know where the move is coming from
                AbstractPileView fromPileView = (AbstractPileView) _cardView.getParent().getParent();

                // Record that the movement occurred
                AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).recordMovement(fromPileView, pileViewCollider);
                
                // Unselect all the cards within this pile view to remove the outline xor'd highlight
                pileViewCollider.removeHighlight();
                
                // Repaint the pile view
                pileViewCollider.repaint();

                // Add this card to the new location
                int initialSize = pileViewCollider.layeredPane.getComponents().length;
                pileViewCollider.layeredPane.add(_cardView);
                pileViewCollider.layeredPane.setLayer(_cardView, initialSize);
                Point offset = pileViewCollider.getCardOffset(_cardView);
                _cardView.setBounds(new Rectangle(offset.x, offset.y, _cardView.getPreferredSize().width, _cardView.getPreferredSize().height));

                // Increment the initial size to prepare for the other cards to be inserted
                ++initialSize;
                
                // Get the list of layered components and go through each of them, adding each one
                // to the proper destination
                List<Component> layeredComponents = Arrays.asList(_layeredPane.getComponents());
                Collections.reverse(layeredComponents);
                for(int i = 0; i < layeredComponents.size(); ++i) {
                    CardProxyView proxy = (CardProxyView) layeredComponents.get(i);
                    pileViewCollider.layeredPane.add(proxy._cardView);
                    pileViewCollider.layeredPane.setLayer(proxy._cardView, initialSize + i);
                    proxy._cardView.add(proxy);
                    proxy.setBorder(null);
                    Point offsetProxy = pileViewCollider.getCardOffset(proxy._cardView);
                    proxy._cardView.setBounds(new Rectangle(offsetProxy.x, offsetProxy.y, proxy._cardView.getPreferredSize().width, proxy._cardView.getPreferredSize().height));
                }
                _layeredPane.removeAll();
                pileViewCollider.repaint();
                

            }
            else {
                Component[] layeredComponents = _layeredPane.getComponents();
                for(int i = 0; i < layeredComponents.length; ++i) {
                    CardProxyView proxy = (CardProxyView) layeredComponents[i];
                    proxy._cardView.add(proxy);
                    proxy.setBorder(null);
                }
            }

            // Add the this proxy back to it's underlying card view
            CardProxyView.this.setBorder(null);
            _cardView.add(CardProxyView.this);

            // Repaint the components involved
            ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
            GameView gameView = viewFactory.get(GameView.class);
            gameView.repaint();

            _cardView.getParent().repaint();
            
            // Set the drag listener to be enabled. This is because it could have been disabled from other workflows, however if 
            // the code got this far it should be re-enabled
            _dragListener.setEnabled(true);
            
            // Verify if there is a game winner
            if(GameView.IsGameWinner()) {
                // Stop the game timer
                TimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class);
                gameTimerView.stop();
                
                // Show the dialog indicating that game has won
                if(JOptionPane.showConfirmDialog(null, Localization.instance().getLocalizedString(LocalizationStrings.GAME_OVER), Localization.instance().getLocalizedString(LocalizationStrings.GAME_OVER_HEADER), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) { 
                    MenuBuilder.search(Application.instance.getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
                }
                else {
                    MenuBuilder.search(Application.instance.getJMenuBar(), ExitMenuItem.class).getComponent(AbstractButton.class).doClick();
                }
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
    private final CardView _cardView;
    
    /**
     * The border layout associated to this view
     */
    private final Border _border = new ExclusiveLineBorder(1);
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardView The card view to associate to this proxy
     */
    public CardProxyView(CardView cardView) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
        setOpaque(false);
        add(_layeredPane);
        addMouseListener(new CardSelectionEvents());
        addMouseMotionListener(new CardDragEvents());
        
        // Set the collision style for this object
        _collisionListener.setIsSingularCollision(true);
        
        // Set the controller of this proxy to the same controller of the specified card
        _cardView = cardView;
        getViewProperties().setEntity(cardView.getViewProperties().getEntity());

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if(!SwingUtilities.isRightMouseButton(event)) {
                    AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                    removeMouseListener(this);
                }
            }
        });
        
        MouseListenerEvent mle = new MouseListenerEvent(SupportedActions.LEFT) {
            @Override public void mouseDragged(MouseEvent event) {
                super.mouseDragged(event);
                if(event.isConsumed()) {
                    return;
                }
                
                if(!isEnabled()) {
                    return;
                }
                
                if(!_dragListener.getIsEnabled()) {
                    return;
                }
                
                // If the border has not yet been set
                if(getBorder() != _border) {
                    
                    // Set this border of this proxy
                    setBorder(_border);
                    
                    // Go through the list of proxies owned by this proxy and show their borders
                    // Note: On mouse release should handle removing the borders set withint his drag event
                    for(Component component : _layeredPane.getComponents()) {
                        if(component instanceof CardProxyView) {
                            CardProxyView proxy = ((CardProxyView)component);
                            proxy.setBorder(proxy._border);
                        }
                    }
                }                    
            }
        };
        addMouseListener(mle);
        addMouseMotionListener(mle);
    }
    
    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        // Propagate the state change to the card view. Ensure that no circular reference could ever occur
        if(_cardView.isEnabled() != enabled) {
            _cardView.setEnabled(enabled);
        }
        
        _dragListener.setEnabled(enabled);
        _collisionListener.setEnabled(enabled);
    }
    
    @Override public void setBounds(int x, int y, int width, int height) {
        // Prevent issues related to the view being updated because of other components, causing this component
        // to be position improperly.
        if(_bounds != null && !_dragListener.isDragging()) {
            super.setBounds(_bounds.x, _bounds.y, _bounds.width, _bounds.height);
        }
        else if(y != 5) {
            super.setBounds(x, y, width, height);
        }
    }
}