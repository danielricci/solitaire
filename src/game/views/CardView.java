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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import framework.api.IView;
import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.navigation.MenuBuilder;
import framework.core.physics.CollisionListener;
import framework.core.physics.ICollide;
import framework.utils.globalisation.Localization;
import framework.utils.logging.Tracelog;

import game.application.Game;
import game.controllers.CardController;
import game.menu.NewGameMenuItem;
import game.models.CardModel;

import editor.menu.ExitMenuItem;
import resources.LocalizationStrings;

public final class CardView extends PanelView implements ICollide {

    private class CardDragEvents extends MouseAdapter {
        /**
         * The parent associated to this card view
         */
        private JLayeredPane _parentSource;

        @Override public void mousePressed(MouseEvent event) {

            // Get the parent of this card view, used as a reference to go back to whatever we were coming from
            _parentSource = (JLayeredPane) CardView.this.getParent();

            // Get the list of components that the parent owns
            Component[] components =_parentSource.getComponents();

            // Find the card that is being dragged and take all the siblings below it and populate them into the
            // layered pane composed by the CardView.this reference
            mainLabel : for(int i = components.length - 1; i >= 0; --i) {

                if(components[i].equals(CardView.this)) {

                    // Get the siblings of cards within the components list (excluding CardView.this)
                    List<CardView> cardViews = Arrays.asList(Arrays.copyOfRange(components, 0, i, CardView[].class));

                    // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
                    Collections.reverse(cardViews);

                    CardView.this._layeredPane.setSize(_layeredPane.getWidth(), _layeredPane.getHeight() + (cardViews.size() * 12));
                    
                    // For each sibling add it into the associated layere pane and position it correctly within
                    // the pane, accounting for the fact that CardView.this is the temporary 'root'
                    for(int j = 0; j < cardViews.size(); ++j) {
                        _layeredPane.add(cardViews.get(j));
                        _layeredPane.setLayer(cardViews.get(j), j);
                        cardViews.get(j).setBounds(new Rectangle(0, 12 * (j + 1), cardViews.get(j).getPreferredSize().width, cardViews.get(j).getPreferredSize().height));
                        _parentSource.remove(cardViews.get(j));
                    }

                    // Position the card at the same place where the drag was attempted from, because when you
                    // add to the application it will position the component at the origin which is not desired
                    Point initialLocation = CardView.this.getLocation();
                    CardView.this.setBounds(new Rectangle(_parentSource.getParent().getLocation().x + initialLocation.x, _parentSource.getParent().getLocation().y + initialLocation.y, _layeredPane.getWidth(), _layeredPane.getHeight()));

                    // Remove the card view reference from it's initial parent
                    _parentSource.remove(CardView.this);
                    Game.instance().add(CardView.this, 0);
                    
                    // Repaint the application to show the changes
                    Game.instance().repaint();

                    break mainLabel;
                }
            }
        }

        @Override public void mouseReleased(MouseEvent event) {

            // If there is a valid collider, set that as the new parent
            if(_collisionListener.getCollision() != null) {
                ICollide collision = _collisionListener.getCollision();
                PileView pileView = (PileView) collision;

                Optional<Component> layeredPane = Arrays.asList(pileView.getComponents()).stream().filter(z -> z.getClass() == JLayeredPane.class).findFirst();
                if(layeredPane.isPresent()) {
                    _parentSource = (JLayeredPane) layeredPane.get();
                }
                else {
                    Tracelog.log(Level.SEVERE, true, "Could not find JLayeredPane within the CardView mouseReleased event...");
                    return;
                }
            }

            // Get the offset that was set, and use this within our calculations
            PileView parent = (PileView) _parentSource.getParent();
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
            int initialSize = _parentSource.getComponents().length;

            // Add this card view to the pane and update the layer within the component that it has been added to
            _parentSource.add(CardView.this);
            _parentSource.setLayer(CardView.this, initialSize);

            // Set the bounds of this card so that it appears at the right position offset
            CardView.this.setBounds(new Rectangle(0, offset * initialSize, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));

            // Remove this card from the application which was used as a temporary measure to support the dragging
            Game.instance().remove(CardView.this);

            // Increment the initial size to include the fact that CardView.this was added back to the parent
            ++initialSize;

            // Take the remainder of the components held by this card and put them back into the parents pane
            for(int i = 0; i < components.size(); ++i) {

                // Add this card view to the pane and update the layer within the component that it has been added to
                _parentSource.add(components.get(i));
                _parentSource.setLayer(components.get(i), i + initialSize);

                // Set the bounds of this card so that it appears at the right position offset                    
                components.get(i).setBounds(new Rectangle(0, offset * (i + initialSize), components.get(i).getPreferredSize().width, components.get(i).getPreferredSize().height));
            }

            // Clear the card views that were added within this cards' layered pane
            CardView.this._layeredPane.removeAll();

            // Repaint the components accordingly
            Game.instance().repaint();
            _parentSource.repaint();
            _parentSource = null;            

            if(parent instanceof FoundationView) {
                boolean winner = true;
                for(FoundationView foundationView : AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationView.class)) {
                    if(foundationView.layeredPane.getComponentCount() == 13) { 
                        CardView cardView = (CardView) foundationView.layeredPane.getComponent(0);
                        if(!cardView.getViewProperties().getEntity(CardController.class).isKing()) {
                            winner = false;
                            break;
                        }
                    }
                    else {
                        winner = false;
                        break;
                    }
                }

                if(winner) {
                    if(JOptionPane.showConfirmDialog(null, Localization.instance().getLocalizedString(LocalizationStrings.GAME_OVER), Localization.instance().getLocalizedString(LocalizationStrings.GAME_OVER_HEADER), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) { 
                        MenuBuilder.search(Game.instance().getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
                    }
                    else {
                        MenuBuilder.search(Game.instance().getJMenuBar(), ExitMenuItem.class).getComponent(AbstractButton.class).doClick();
                    }
                }
            }
        }
    }

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
    private final DragListener _draggableListener = new DragListener(this);

    /**
     * The collision listener associated to this view
     */
    private final CollisionListener _collisionListener = new CollisionListener(this);

    /**
     * The layered pane that holds the potential list of card that would be dragged along-side this card vuew
     */
    private final JLayeredPane _layeredPane = new JLayeredPane();

    /**
     * Sets this card as being *visually* selected
     */
    private boolean _selectIt = false;
    
    /**
     * Creates a new instance of this class type
     */
    public CardView(CardModel card) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(true);
        setBackground(Color.BLACK);
        add(_layeredPane);

        card.addListeners(this);
        _controller = new CardController(card);
        getViewProperties().setEntity(_controller);   

        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent event) {
                ICollide collider = _collisionListener.getCollision();
                if(collider != null) {
                    PileView pile = (PileView) collider;
                    CardView cardView = pile.getLastCard();
                    cardView._selectIt = true;
                    pile.repaint();
                }
                else {
                    
                }
            }
        });
        
        // Add a mouse adapter to handle the mouse click event on a click
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                
                if(CardView.this.getParent().getComponents()[0].equals(CardView.this)) {
                    if(event.getClickCount() == 2 && !_controller.getCard().getIsBackside()) {
                        
                        // Make sure that we are not double clicking on an ACE. That doesn't make much sense here in this case
                        if(AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationView.class).stream().anyMatch(z -> z.layeredPane.getComponentCount() == 1 && z.layeredPane.getComponents()[0] == CardView.this)) {
                            return;
                        }
                        
                        for(FoundationView foundationView : AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationView.class)) {
                            if(foundationView.isValidCollision(CardView.this)) {

                                // Remove from the layered pane source
                                JLayeredPane parentPane = (JLayeredPane) CardView.this.getParent();
                                parentPane.remove(CardView.this);
                                                                
                                // Add to the layered pane destination
                                foundationView.layeredPane.add(CardView.this);
                                foundationView.layeredPane.setLayer(CardView.this, foundationView.layeredPane.getComponentCount());
                                
                                // Set the proper bounds of the component
                                CardView.this.setBounds(new Rectangle(0, 0, CardView.this.getPreferredSize().width, CardView.this.getPreferredSize().height));
                                
                                // Repaint the components
                                _layeredPane.repaint();
                                foundationView.repaint();
                                
                                break;
                            }
                        }                            
                    }
                    else if(_controller.getCard().getIsBackside()){
                        _controller.getCard().setBackside(false);
                        _controller.getCard().refresh();
                    }
                }
            }
        });
    }
    
    @Override protected void PreProcessGraphics(Graphics context) {
        super.PreProcessGraphics(context);
        if(_selectIt) {
            context.setXORMode(Color.WHITE);
        }
    }
    
    @Override public void removeAll() {
        super.removeAll();
        _draggableListener.setEnabled(false);
        _collisionListener.setEnabled(false);
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

    @Override public void render() {
        super.render();
        setRenderLimits(0, 0, this.getWidth(), this.getHeight());
        _controller.refresh();
    }

    @Override public void update(AbstractEventArgs event) {
        super.update(event);

        CardModel card = (CardModel)event.getSource();
        addRenderableContent(card); 

        // Properly set the liseteners associated to this view
        _draggableListener.setEnabled(!card.getIsBackside());
        _collisionListener.setEnabled(!card.getIsBackside());

        // Append the mouse listener if the backside is not shown
        if(!card.getIsBackside())
        {
            boolean found = false;
            for(MouseListener listener : getMouseListeners()) {
                if(listener instanceof CardDragEvents) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                addMouseListener(new CardDragEvents());
            }
        }

        repaint();
    }
}