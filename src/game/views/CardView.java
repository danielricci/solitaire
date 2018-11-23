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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import framework.api.IView;
import framework.communication.internal.signal.ISignalReceiver;
import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.graphics.IRenderable;
import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.navigation.MenuBuilder;
import framework.core.physics.CollisionListener;
import framework.core.physics.ICollide;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.controllers.CardController;
import game.gameplay.MovementType;
import game.menu.ExitMenuItem;
import game.menu.NewGameMenuItem;
import game.models.CardModel;

import resources.LocalizationStrings;

/**
 * This view represents the representation of a single card
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 *
 */
public final class CardView extends PanelView implements ICollide {

    /**
     * The card selection events for this proxy view
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     *
     */
    private final class CardSelectionEvents extends MouseAdapter {
        
        private JLayeredPane _parentLayeredPane;
        
        @Override public void mousePressed(MouseEvent event) {

            if(!isEnabled()) {
                return;
            }
            
            // Get the parent of this card view, used as a reference to go back to whatever we were coming from
            _parentLayeredPane = (JLayeredPane) CardView.this.getParent();

            // Get the list of components that the parent owns
            Component[] components =_parentLayeredPane.getComponents();

            // Find the card that is being dragged and take all the siblings below it and populate them into the
            // layered pane composed by the CardView.this reference
            mainLabel : for(int i = components.length - 1; i >= 0; --i) {

                if(components[i].equals(CardView.this)) {

                    // Get the siblings of cards within the components list (excluding CardView.this)
                    List<CardView> cardViews = Arrays.asList(Arrays.copyOfRange(components, 0, i, CardView[].class));

                    // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
                    Collections.reverse(cardViews);

                    // Fixes a bug where the layered pane is chopped from the waist down
                    CardView.this._layeredPane.setSize(_layeredPane.getWidth(), _layeredPane.getHeight() + (cardViews.size() * 12));
                    
                    // For each sibling add it into the associated layere pane and position it correctly within
                    // the pane, accounting for the fact that CardView.this is the temporary 'root'
                    for(int j = 0; j < cardViews.size(); ++j) {
                        _layeredPane.add(cardViews.get(j));
                        _layeredPane.setLayer(cardViews.get(j), j);
                        
                        // Account for the border offsets of -1 for left and bottom
                        cardViews.get(j).setBounds(new Rectangle(-1, (12 * (j + 1)) - 1, cardViews.get(j).getPreferredSize().width, cardViews.get(j).getPreferredSize().height));
                        _parentLayeredPane.remove(cardViews.get(j));
                    }

                    // Position the card at the same place where the drag was attempted from, because when you
                    // add to the application it will position the component at the origin which is not desired
                    Point initialLocation = CardView.this.getLocation();
                    
                    // Note: Add +2 to the width and height because of the initial border size offset
                    CardView.this.setBounds(new Rectangle(_parentLayeredPane.getParent().getLocation().x + initialLocation.x, _parentLayeredPane.getParent().getLocation().y + initialLocation.y, _layeredPane.getWidth() + 2, _layeredPane.getHeight() + 2));

                    // Remove the card view reference from it's initial parent
                    _parentLayeredPane.remove(CardView.this);
                    
                    // Get a reference to the game view and status view, and add the card into the proper
                    // z-order so that it appears underneath the status bar, but over everything else in the game
                    ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
                    GameView gameView = viewFactory.get(GameView.class);
                    StatusBarView statusBarView = viewFactory.get(StatusBarView.class);

                    gameView.add(CardView.this, gameView.getComponentZOrder(statusBarView) + 1);
                    gameView.repaint();

                    break mainLabel;
                }
            }
        }

        @Override public void mouseReleased(MouseEvent event) {
            
            if(!isEnabled()) {
                return;
            }
            
            // Ensure that a valid parent was set on the mouse pressed before continuing
            if(_parentLayeredPane == null) {
                return;
            }
            
            // If there is a valid collider, set that as the new parent
            if(_collisionListener.getCollision() != null) {
                ICollide collision = _collisionListener.getCollision();
                TableauView pileView = (TableauView) collision;

                // Get the before movement type to know where the move is coming from
                MovementType movementTypeFrom = MovementType.fromClass(_parentLayeredPane.getParent());

                Optional<Component> layeredPane = Arrays.asList(pileView.getComponents()).stream().filter(z -> z.getClass() == JLayeredPane.class).findFirst();
                if(layeredPane.isPresent()) {
                    _parentLayeredPane = (JLayeredPane) layeredPane.get();
                    AbstractFactory.getFactory(ViewFactory.class).get(GameScoreView.class).updateScore(movementTypeFrom, MovementType.fromClass(collision));
                }
                else {
                    Tracelog.log(Level.SEVERE, true, "Could not find JLayeredPane within the CardView mouseReleased event...");
                    return;
                }
            }
            
            // Get the offset that was set, and use this within our calculations
            TableauView parent = (TableauView) _parentLayeredPane.getParent();
            int offset = parent.CARD_OFFSET;

            // Get the list of components associated to the CardView.this reference. This list represents all the children associated
            // to the said CardView.this reference.
            List<Component> components = Arrays.asList(CardView.this._layeredPane.getComponents());

            // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
            Collections.reverse(components);

            // Get the initial count of the number of components in the layered pane.  It is assumed that they hold only
            // CardView or the calculations wont work properly.
            // This number represents the number of cards that exist within the list after the drag operation had occured, so if
            // you have a pile with 5 cards and you drag three out, then you would be left with 2 cards in the parent, CardView.this would
            // represent the card actually being dragged, and the layered pane associated to CardView.this would attached itself.
            int initialSize = _parentLayeredPane.getComponents().length;
            
            // Add this card view to the pane and update the layer within the component that it has been added to
            _parentLayeredPane.add(CardView.this);
            _parentLayeredPane.setLayer(CardView.this, initialSize);

            // Set the bounds of this card so that it appears at the right position offset
            CardView.this.setBounds(new Rectangle(0, offset * initialSize, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));

            ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
            GameView gameView = viewFactory.get(GameView.class);
            gameView.remove(CardView.this);
            
            // Increment the initial size to include the fact that CardView.this was added back to the parent
            ++initialSize;

            // Take the remainder of the components held by this card and put them back into the parents pane
            for(int i = 0; i < components.size(); ++i) {

                // Add this card view to the pane and update the layer within the component that it has been added to
                _parentLayeredPane.add(components.get(i));
                _parentLayeredPane.setLayer(components.get(i), i + initialSize);

                // Set the bounds of this card so that it appears at the right position offset                    
                components.get(i).setBounds(new Rectangle(0, offset * (i + initialSize), components.get(i).getPreferredSize().width, components.get(i).getPreferredSize().height));
            }
            
            // Clear the card views that were added within this cards' layered pane
            CardView.this._layeredPane.removeAll();
            
            // Repaint the components accordingly
            gameView.repaint();
            _parentLayeredPane.repaint();
            _parentLayeredPane = null;            

            // Verify if there is a game winner
            if(GameView.IsGameWinner()) {
                // Stop the game timer
                GameTimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(GameTimerView.class);
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
     * This mouse adapter handles events when the card is pressed with the mouse
     */
    private MouseAdapter _mouseAdapter = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            
            if(!isEnabled()) {
                return;
            }
            
            GameTimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(GameTimerView.class);
            if(gameTimerView != null) {
                gameTimerView.startGameTimer();
            }
            
            if(CardView.this.getParent().getComponents()[0].equals(CardView.this)) {
                if(event.getClickCount() == 1) {
                    uncoverBackside();
                }
                else {
                    performCardAutoMovement();
                }
            }
        }
    };
    
    /**
     * The preferred width of this card
     */
    public static final int CARD_WIDTH = 71;

    /**
     * The preferred height of this card
     */
    public static final int CARD_HEIGHT = 100;

    /**
     * The controller associated to this card view
     */
    private final CardController _controller;

    /**
     * The draggable listener associated to this view
     */
    protected final DragListener draggableListener = new DragListener(this);

    /**
     * The collision listener associated to this view
     */
    private final CollisionListener _collisionListener = new CollisionListener(this);

    /**
     * The layered pane that holds the potential list of cards that would be dragged along-side this card vuew
     */
    private final JLayeredPane _layeredPane = new JLayeredPane();

    /**
     * Sets this card as being highlighted visually
     */
    private boolean _isHighlighted;
    
    /**
     * Indicates if selections are enabled
     */
    private boolean _highlightsEnabled;
    
    /**
     * The card selection events associated to this card view
     */
    private final CardSelectionEvents _cardSelectionEvents = new CardSelectionEvents();
    
    /**
     * The card proxy associated to this view
     */
    private CardProxyView _cardProxy;
    
    /**
     * Signal indicating that this view should synchronizr with the outline option
     */
    public final static String EVENT_OUTLINE_SYNCHRONIZE = "EVENT_OUTLINE_SYNCHRONIZE";
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardModel The cards model underlying this card view
     */
    public CardView(CardModel cardModel) {
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(true);
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        add(_layeredPane);
        
        // Set the collision style for this object
        _collisionListener.setIsSingularCollision(true);
        
        cardModel.addListeners(this);
        _controller = new CardController(cardModel);
        getViewProperties().setEntity(_controller);   

        // Create the card proxy view
        _cardProxy = new CardProxyView(this);
        
        // Add the mouse listener responsible for handling single clicks and double clicks on this card.
        // Note: This will sometimes not be called depending on if the proxy is enabled or not, since the 
        //       proxy sits on top of this card. However, when the backside is being shown, this would indeed
        //       be called, however the double click will not be called since the single click will
        //       initiate the proxy, thus the double click of the proxy will be called
        //
        // Note: This mouse listener should be before any other mouse listener within this class
        addMouseListener(_mouseAdapter);

        /**
         * Listen in on events when we need to synchronize withthe online option
         */
        addSignalListener(EVENT_OUTLINE_SYNCHRONIZE, new ISignalReceiver<EventArgs>() {
            @Override public void signalReceived(EventArgs event) {
                synchronizeWithOptions();                
            }
        });
    
        // Synchronize with the options preferences.
        // 
        // Note: 
        //      This must be done at the end to ensure that the order of added events is done properly
        synchronizeWithOptions();
    }
    
    /**
     * Synchronizes this card view w.r.t the current outline options that are set within the game.
     */
    private void synchronizeWithOptions()
    {
        // Verify if the option for highlighting is enabled or not
        OptionsPreferences optionsPreferences = new OptionsPreferences();
        optionsPreferences.load();
        _highlightsEnabled = optionsPreferences.outlineDragging;

        // If the card has its backside shown or the outline option is enabled
        // then do not allow dragging or collision to work as normal
        draggableListener.setEnabled(!_controller.getCard().getIsBackside() && !optionsPreferences.outlineDragging);
        _collisionListener.setEnabled(!_controller.getCard().getIsBackside() && !optionsPreferences.outlineDragging);
        
        // If the backside is not being shown, then add the event handler for card drag event
        // Note: In the event that the options preferences calls for outline mode, the entire
        //       operation of performing a click-down, click-up, should be done by the proxy and
        //       not this card explicitely.
        if(!_controller.getCard().getIsBackside()) {
            if(!optionsPreferences.outlineDragging) {
                remove(_cardProxy);
                addMouseListener(_cardSelectionEvents);
            }
            else {
                removeMouseListener(_cardSelectionEvents);
                add(_cardProxy);
            }   
        } 
        
        // TODO - Is this really necessary???
        repaint();
        _cardProxy.repaint();
    }

    /**
     * Sets if this card is highlighted
     *
     * @param isHighlighted TRUE if this card is to be highlighed, false otherwise
     */
    public void setHighlighted(boolean isHighlighted) {
        _isHighlighted = isHighlighted;
        repaint();
    }
    
    /**
     * @return The card proxy associated to this view
     */
    public CardProxyView getProxyView() {
        return _cardProxy;
    }
    
    /**
     * Attempts to uncover the backside of this view
     */
    private void uncoverBackside() {
        if(_controller.getCard().getIsBackside()){
            _controller.getCard().setBackside(false);
            _controller.getCard().refresh();
            AbstractFactory.getFactory(ViewFactory.class).get(GameScoreView.class).updateScoreCardTurnOver();
            // Only allow this card view to have dragging and collision working `vanilla`
            // style if the outline option is not selected
            OptionsPreferences preferences = new OptionsPreferences();
            preferences.load();
            if(!preferences.outlineDragging) {
                draggableListener.setEnabled(true);
                _collisionListener.setEnabled(true);
            }
            
            if(!preferences.outlineDragging) {
                addMouseListener(_cardSelectionEvents);
            }
            else {
                add(_cardProxy);
                _cardProxy.setVisible(true);
                repaint();
            }
        }
    }
    
    /**
     * Performs an auto card movement, attempting to move this card to the foundation
     * 
     * @return TRUE if the auto movement was successfull, FALSE otherwise
     */
    public boolean performCardAutoMovement() {
        if(!_controller.getCard().getIsBackside()) {
            
            // Make sure that we are not double clicking on an ACE. That doesn't make much sense here in this case
            if(AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationView.class).stream().anyMatch(z -> z.layeredPane.getComponentCount() == 1 && z.layeredPane.getComponents()[0] == CardView.this)) {
                return false;
            }
            
            // Get the list of foundation views. 
            List<FoundationView> foundationViews = AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationView.class);
            
            // Reverse the list, so that the card populating the left-most foundation view, this just looks a lot better
            Collections.reverse(foundationViews);
            
            // Go through the list of foundation views and see if there is a match
            for(FoundationView foundationView : foundationViews) {
                if(foundationView.isValidCollision(CardView.this)) {

                    AbstractFactory.getFactory(ViewFactory.class).get(GameScoreView.class).updateScore(MovementType.fromClass(CardView.this.getParentIView()), MovementType.FOUNDATION);
                    
                    // Halt any drag events that could occur
                    draggableListener.stopDragEvent();
                    
                    // Hack:    Update the card selection event because there are cases where the mouse up event would reset all that is done here
                    // Ex:      Double clicking on an ace that is in the pile view (outline mode off) will cause the card to go to a foundation
                    //          on mouse down (the second mouse down), however once the mouse button is released, the mouseReleased code would
                    //          have thought that there was no valid move, thus putting the card back to the parent layered pane that was
                    //          originally recorded when the first mouse down was initiated. There is a guard within the mouse released code
                    //          that is required for this to have any meaning.
                    _cardSelectionEvents._parentLayeredPane = null;
                    
                    // Remove from the layered pane source
                    CardView.this.getParent().remove(CardView.this);;
                                                    
                    // Add to the layered pane destination
                    foundationView.layeredPane.add(CardView.this);
                    foundationView.layeredPane.setLayer(CardView.this, foundationView.layeredPane.getComponentCount());
                    
                    // Set the proper bounds of the component
                    CardView.this.setBounds(new Rectangle(0, 0, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));
                                        
                    // Repaint the components
                    _layeredPane.repaint();
                    foundationView.repaint();
                    
                    return true;
                }
            }                            
        }
        
        return false;
    }
    
    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        // Propagate the state change to the proxy. Ensure that no circular reference could ever occur
        if(_cardProxy.isEnabled() != enabled) {
            _cardProxy.setEnabled(enabled);
        }

        draggableListener.setEnabled(enabled);
        _collisionListener.setEnabled(enabled);
    }
    
    @Override public boolean isValidCollision(Component source) {
        IView view = (IView)source;
        
        // A card is coming into this card, and we are on the foundation view
        if(getParent().getParent() instanceof FoundationView) {
            CardController thisCardViewController = view.getViewProperties().getEntity(CardController.class);
            return _controller.isValidFoundationMove(thisCardViewController.getCard());            
        }
        // The card is coming onto this card which is on the pile view (should be, there are only two options for this game)
        else {
            CardController cardViewController = this.getViewProperties().getEntity(CardController.class);
            return cardViewController.getCard().isCardBeforeAndOppositeSuite(view.getViewProperties().getEntity(CardController.class).getCard());
        }
    }
    
    @Override protected void preProcessGraphics(Graphics context) {
        super.preProcessGraphics(context);
        if(_highlightsEnabled && _isHighlighted) {
            context.setXORMode(Color.WHITE);
        }
    }
    
    @Override public void removeAll() {
        super.removeAll();
        draggableListener.setEnabled(false);
        _collisionListener.setEnabled(false);
    }

    @Override public void render() {
        super.render();
        setRenderLimits(0, 0, this.getWidth(), this.getHeight());
        _controller.refresh();
    }

    @Override public void update(EventArgs event) {
        super.update(event);
        addRenderableContent((IRenderable)event.getSource()); 
        repaint();
    }
    
    @Override public void setBounds(int x, int y, int width, int height) {
        // HACK I feel so bad doing this. but I must, for I love cake and I want my game to work properly for now.
        // TODO Stop being a bad boy and fix the actual root cause of the bug.
        if(x != 10 && y != 5) {
            super.setBounds(x, y, width, height);
        }
    }
    
    @Override public String toString() {
        return (isVisible() ? "[V] " : "[H]") + getViewProperties().getEntity(CardController.class).getCard().toString() + "\t[" + JLayeredPane.getLayer(this) + "]";
    }
}