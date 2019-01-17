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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.physics.ICollidable;
import framework.utils.MouseListenerEvent;
import framework.utils.MouseListenerEvent.SupportedActions;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.config.OptionsPreferences.ScoringOption;
import game.controllers.MovementRecorderController;
import game.models.CardModel;

/**
 * This views represents the talon pile view
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public final class TalonPileView extends AbstractPileView implements ICollidable {

    /**
     * The available states of the talon
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     */
    public enum TalonCardState {
        // An Empty Deck
        EMPTY,
        // The Deck has been played through
        DECK_PLAYED,
        // Normal card played
        NORMAL
    }

    /**
     * A helper class for holding onto a layer and it's associated card state
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     */
    private class TalonCardReference {
        
        /**
         * The layer of the last card that was clicked
         */
        public final int layer;
        
        /**
         * The last card that was clicked w.r.t this view
         */
        public final CardView card;
        
        /**
         * Constructs a new instance of this class type
         *
         * @param card A card view
         * @param layer A specified layer
         */
        public TalonCardReference(CardView card, int layer) {
            this.card = card;
            this.layer = layer;
        }
        
        /**
         * Constructs a new instance of this class type
         *
         * @param card A card view
         */
        public TalonCardReference(CardView card) {
            this.card = card;
            this.layer = JLayeredPane.getLayer(card);
        }
        
        @Override public String toString() {
            return String.format("Layer: %s | Card: %s", this.layer, this.card);
        }
    }
    
    /**
     * The last card hand state of this talon
     */
    private TalonCardState _lastCardHandState = null;
    
    /**
     * The number of times that the deck was played
     */
    private int _deckPlays;
    
    /**
     * The blank card associated to the talon view
     */
    private final PanelView _blankCard = new PanelView();

    /**
     * The last card that was interacted with
     */
    private TalonCardReference _lastCardInteracted = null;
    
    /**
     * The card that can be undone
     */
    private TalonCardReference _undoableCard = null;
    
    /**
     * Constructs a new instance of this class type
     */
    private TalonPileView() {
        _blankCard.setBackground(new Color(0, 128, 0));
        _blankCard.setPreferredSize(new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
        _blankCard.setBounds(new Rectangle(0, 0, _blankCard.getPreferredSize().width, _blankCard.getPreferredSize().height));
        _blankCard.setVisible(true);
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(preferences.drawOption == DrawOption.THREE) {
            CARD_OFFSET = 12;
        }
        else {
            CARD_OFFSET = 0;    
        }
        
        // The blank card will always be in this view, so right clicking on it should autocomplete
        // whatever is on the board
        ViewHelper.registerForCardsAutoMove(_blankCard);
        
        // Add a listener to the blank card since it is sitting above the board. If someone tries to click in this area
        // the timer will start, unknowing to the player that they really clicked on a special area of the board
        _blankCard.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if(!SwingUtilities.isRightMouseButton(event)) {
                    AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                    _blankCard.removeMouseListener(this);
                }
            }
        });
        
        
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cards The card models to load within this view
     */
    public TalonPileView(List<CardModel> cards) {
        this();
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        for(int i = 0, layer = 0; i < cards.size(); ++i) {
            CardView cardView = AbstractFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
            MouseListenerEvent adapter = new MouseListenerEvent(SupportedActions.LEFT) {
                @Override public void mousePressed(MouseEvent event) {
                    
                    super.mousePressed(event);
                    if(event.isConsumed()) {
                        return;
                    }
                    
                    // Do not allow non-enabled cards to run
                    if(!cardView.isEnabled()) {
                        return;
                    }
                    
                    // Take the card that was pressed on and record it's layer location 
                    _lastCardInteracted = new TalonCardReference(cardView);
                }
                @Override public void mouseReleased(MouseEvent event) {
            
                    super.mouseReleased(event);
                    if(event.isConsumed()) {
                        return;
                    }
                    
                    // Prevent other released events from being called by other cards that are not yet enabled
                    if(!cardView.isEnabled()) {
                        return;
                    }
                   
                    // If the card is no longer associated to the talon then attempt to get the next one
                    if(!(cardView.getParentIView() instanceof TalonPileView)) {
                        
                        // The top-most card cannot be the layered pane
                        boolean cond1 = layeredPane.highestLayer() != JLayeredPane.getLayer(_blankCard);
                        
                        // There must not be any more visible cards (excluding the blank card)
                        boolean cond2 = Arrays.asList(layeredPane.getComponents()).stream().anyMatch(z -> !z.equals(_blankCard) && z.isVisible());
                        
                        if(cond1 && !cond2) {
                            for(int iterations = 0, layerId = JLayeredPane.getLayer(_blankCard) + 1; layerId <= layeredPane.highestLayer() || iterations < 3; ++layerId, ++iterations) {
                                Component component = layeredPane.getComponentsInLayer(layerId)[0];
                                component.setVisible(true);
                            }
                        }
                    }
                    // The card was put back, so position it accordingly so that it can be shown again
                    // Make sure that the card is enabled. Since when a card is not enabled, the event
                    // handlers are not applied to the card
                    else if(cardView.isEnabled()){
                        // If the blank card is on the same layer as this card, put this card to the next layer above. 
                        // This could only occur if this was already top-most
                        if(JLayeredPane.getLayer(_blankCard) == JLayeredPane.getLayer(cardView)) {
                            layeredPane.setLayer(cardView, JLayeredPane.getLayer(cardView) + 1);
                        }
                        setBounds(cardView);
                    }
                    
                    // When the mouse is released, ensure that the component located at the highest layer is enabled
                    layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0].setEnabled(true);
                }
            };
            cardView.addMouseListener(adapter);
            cardView.getProxyView().addMouseListener(adapter);
            
            // Set the default bounds of the card
            cardView.setBounds(new Rectangle(0, 0, cardView.getPreferredSize().width, cardView.getPreferredSize().height));
            
            // All cards are disabled by default, and should be disabled by default after a subsequent deck has been played through
            cardView.setEnabled(false);
            
            // Add the card to the layered pane and set it's layer accordingly
            layeredPane.add(cardView);
            
            if(preferences.drawOption == DrawOption.THREE) {
                layeredPane.setLayer(cardView, layer / 3);
                ++layer;
            }
            else {
                layeredPane.setLayer(cardView, i);
            }
        }
        
        // Add the blank card last, and make sure that it has the topmost layer
        layeredPane.add(_blankCard);
        layeredPane.setLayer(_blankCard, layeredPane.highestLayer() + 1);
    }
     
    @Override public void addCard(CardView cardView) {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(preferences.drawOption == DrawOption.THREE) {
            addCard(cardView, _lastCardInteracted.layer);
            layeredPane.setPosition(cardView, 0);           
        }
        else {
            super.addCard(cardView);
        }
    }
    
    /**
     * Recycles the deck to the beginning
     */
    public void recycleDeck() {
        // New deck has the score updated
        AbstractFactory.getFactory(ViewFactory.class).get(ScoreView.class).updateScoreDeckFinished(_deckPlays);
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        switch(preferences.drawOption) {
        case ONE:
            Component[] components = layeredPane.getComponents();
            for(int i = components.length - 1; i >= 0; --i) {
                Component component = components[i];
                layeredPane.setLayer(component, i);
                component.setBounds(new Rectangle(0, 0, component.getPreferredSize().width, component.getPreferredSize().height));

                // Set the visible state of the component. This should make everything but the blank layer not visible
                components[i].setVisible(components[i].equals(_blankCard));
            }
            break;
        case THREE:

            // Remove all the cards from the layered pane
            layeredPane.remove(_blankCard);
            Component[] allComponents = layeredPane.getComponents();
            
            // Re-populate the layered pane 
            for(int i = 0, layer = 0; i < allComponents.length; ++i) {
                // All cards are disabled by default, and should be disabled by default after a subsequent deck has been played through
                allComponents[i].setEnabled(false);
                
                // Add the card to the layered pane and set it's layer accordingly
                layeredPane.add(allComponents[i]);
                
                if(preferences.drawOption == DrawOption.THREE) {
                    layeredPane.setLayer(allComponents[i], layer / 3);
                    allComponents[i].setVisible(false);
                    allComponents[i].setEnabled(false);
                    ++layer;
                }
            }
            
            // Add back the blank card as the top-most card
            layeredPane.add(_blankCard);
            layeredPane.setLayer(_blankCard, layeredPane.highestLayer() + 1);
            
            break;
        } 
    }
     
    /*
     * Re-syncs the deck, ensuring that the layers are sequentially ordered
     */
    private void resyncDeck() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        if(preferences.drawOption == DrawOption.ONE) {
            // Starting from the lowest layer upwards, re-synchronize all the layer positions of the cards.
            for(int i = layeredPane.getComponentCount() - 1, layerId = 0;  i >= 0; --i, ++layerId) {
                
                // Re-synchronize the layer position of the card
                layeredPane.setLayer(layeredPane.getComponent(i), layerId);
    
                // Set the enabled state of the component. The only component that should be enabled is the top-most one, the rest should be disabled
                layeredPane.getComponent(i).setEnabled(layeredPane.highestLayer() == layeredPane.getLayer(layeredPane.getComponent(i)));
            }
        }
        else if(preferences.drawOption == DrawOption.THREE) {
            
            // Get the list of components grouped by their layer
            List<Component[]> componentsGroupedByLayer = getComponentsGroupedByLayer();
            
            // Re-input components by their layer, ensuring that their layer is ordered
            // sequentially WITHOUT any gaps between layer numbers
            for(int i = 0; i < componentsGroupedByLayer.size(); ++i) {
                for(Component comp : componentsGroupedByLayer.get(i)) {
                    layeredPane.setLayer(comp, i);
                }
            }
        }
        else {
            Tracelog.log(Level.SEVERE, true, "Implement me");
        }
    }
    /**
     * Displays the next card hand on this view
     * 
     * @return TRUE if the card is not the last card in the deck
     */
    public void cycleNextHand() {
        
        // If the talon can no longer be played with, then go no futher
        if(isTalonEnded()) {
            _lastCardHandState = TalonCardState.DECK_PLAYED;
            return;
        }
        
        // If there is only one component then dont go further. The idea is that the "blank" placeholder view that
        // mimics that switching of cards should never be removed from this view, thus if that is the only view that
        // exists then it should mean that all the playing cards having been removed from this view
        if(layeredPane.getComponentCount() == 1) {
            Tracelog.log(Level.INFO, true, "There are no more cards left in the Talon to play.");
            _lastCardHandState = TalonCardState.EMPTY;
            return;
        }
        
        // Notify the movement controller that there was a movement that occured of the talon, from the stock view
        AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).recordMovement(AbstractFactory.getFactory(ViewFactory.class).get(StockView.class), this);
        
        // If we are at the end then restart the deck
        if(JLayeredPane.getLayer(_blankCard) == layeredPane.lowestLayer()) {
            recycleDeck();  
        }
        else {
            
            OptionsPreferences preferences = new OptionsPreferences();
            preferences.load();

            if(preferences.drawOption == DrawOption.ONE) {
                // Get the card that is directly below the blank card
                Component cardDirectlyBelowBlankCard = layeredPane.getComponent(layeredPane.getIndexOf(_blankCard) + 1);
                cardDirectlyBelowBlankCard.setVisible(true);
                
                // Set the layer of the card that is directly below the blank card, to the highest layer
                layeredPane.setLayer(cardDirectlyBelowBlankCard, layeredPane.highestLayer() + 1);
                resyncDeck();
            }
            else if(preferences.drawOption == DrawOption.THREE) {
                // Reposition all the cards to the origin
                repositionCardsAboveBlankCardToOrigin();
                
                // Take the blank card and position to the layer one below. Take that layer where the blank card is at
                // and make the other cards top most. Also make sure to position them correctly and set their bounds
                int blankCardLayerIndex = JLayeredPane.getLayer(_blankCard);
                int cardsUnderBlankCard = blankCardLayerIndex - 1;
                int highestLayer = layeredPane.highestLayer();
                Component[] components = layeredPane.getComponentsInLayer(cardsUnderBlankCard);
                for(Component component : components) {
                    component.setVisible(true);
                    component.setEnabled(components[0].equals(component));
                    layeredPane.setLayer(component, highestLayer + 1);
                    this.setBounds(component);
                }
                // Note: Set the layer of the blank card after the positioning of the other cards occur, or the blank card
                // will also be moved back to top, not something we want to do.
                layeredPane.setLayer(_blankCard, cardsUnderBlankCard);
                resyncDeck();
            }
            else {
                Tracelog.log(Level.SEVERE, true, "Implement me");
                _lastCardHandState = null;
                return;
            }
            
            if(layeredPane.lowestLayer() == JLayeredPane.getLayer(_blankCard)) {
                ++_deckPlays;
                _lastCardHandState = TalonCardState.DECK_PLAYED;
                return;
            }
        }
                
        _lastCardHandState = TalonCardState.NORMAL;
    }
    
    private void repositionCardsAboveBlankCardToOrigin() {
        // Before proceeding, everything that has a higher layer than the blank card needs to be re-positioned to the origin
        if(layeredPane.highestLayer() != JLayeredPane.getLayer(_blankCard)) {
            int layer = layeredPane.highestLayer();
            while(layer != JLayeredPane.getLayer(_blankCard)) {
                List<Component> components = Arrays.asList(layeredPane.getComponentsInLayer(layer));
                for(Component component : components) {
                    component.setBounds(new Rectangle(0, 0, component.getPreferredSize().width, component.getPreferredSize().height));    
                }
                
                --layer;
            }
        }
    }
    
    public boolean isDeckPlayed() {
        return layeredPane.lowestLayer() == JLayeredPane.getLayer(_blankCard);
    }
    
    public boolean isDeckEmpty() {
        return layeredPane.getComponentCount() == 1;
    }
    
    public boolean isTalonEnded() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        if(preferences.drawOption == DrawOption.ONE && preferences.scoringOption == ScoringOption.VEGAS && _deckPlays == 1) {
            return true;
        }
        
        if(preferences.drawOption == DrawOption.THREE && preferences.scoringOption == ScoringOption.VEGAS && _deckPlays == 3) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Reverts the last hand played
     */
    public void revertLastHand() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();

        // If the blank card is at the top then make sure to reduce the deck plays by one.
        if(JLayeredPane.getLayer(_blankCard) == layeredPane.highestLayer()) {
            _deckPlays = Math.max(0, _deckPlays - 1);
        }
        
        if(preferences.drawOption == DrawOption.ONE) {
            if(JLayeredPane.getLayer(_blankCard) == layeredPane.highestLayer()) {
                Component[] components = layeredPane.getComponents();
                for(int i = 0; i < components.length; ++i) {
                    layeredPane.setLayer(components[i], i);
                }
                
                // Enable the top-most card so that it can be used
                Component comp = layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0];
                comp.setEnabled(true);
                comp.setVisible(true);
            }
            else {
                // Get the top-most component and set it underneath the blank card.
                Component comp = layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0];
                comp.setEnabled(false);
                layeredPane.setLayer(comp, JLayeredPane.getLayer(_blankCard));
                layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0].setEnabled(true);
    
                // From the bottom upwards, re-order the layer of each card
                Component[] components = layeredPane.getComponents();
                for(int layer = 0, i = components.length - 1; i >= 0; --i, ++layer) {
                    layeredPane.setLayer(components[i], layer);
                }
            }
        }
        else if(preferences.drawOption == DrawOption.THREE) {
            if(JLayeredPane.getLayer(_blankCard) == layeredPane.highestLayer()) {
                List<Component[]> components = getComponentsGroupedByLayer();
                layeredPane.removeAll();
                
                for(int layer = 0, i = components.size() - 1; i >= 0; --i, ++layer) {
                    Component[] comps = components.get(i);
                    for(int j = 0; j < comps.length; ++j) {
                        layeredPane.add(comps[j]);
                        layeredPane.setLayer(comps[j], layer);
                    }
                }

                // Show all the top-most cards, and set the first card to be enabled
                Component[] highestLayerComps = layeredPane.getComponentsInLayer(layeredPane.highestLayer());
                for(int i = 0; i < highestLayerComps.length; ++i) {
                    highestLayerComps[i].setEnabled(i == 0);
                    highestLayerComps[i].setVisible(true);
                }
            }
            else {
                // From the blank card upwards, up each components layer by 1
                int blankCardLayer = JLayeredPane.getLayer(_blankCard);
                Component[] components = layeredPane.getComponents();
                for(Component comp : components) {
                    if(layeredPane.getLayer(comp) >= blankCardLayer) {
                        layeredPane.setLayer(comp, layeredPane.getLayer(comp) + 1);
                    }
                }
                
                // Take the top three cards and move them to the original blank card index
                Component[] cardsBeingReverted = layeredPane.getComponentsInLayer(layeredPane.highestLayer());
                for(Component comp : cardsBeingReverted) {
                    layeredPane.setLayer(comp, blankCardLayer);
                    comp.setVisible(false);
                }
                
                // Take the highest layered cards at this point, set their screen position accordingly, and enable
                // the first card to be moved
                Component[] highestCards = layeredPane.getComponentsInLayer(layeredPane.highestLayer());
                
                // If the card is not the blank card, then remove the cards and re-add them, they will
                // go through the process of being properly re-positioned
                if(!(highestCards.length == 1 && highestCards[0] == _blankCard)) {
                   for(Component comp : highestCards) {
                       setBounds(comp);
                   }
                   
                   highestCards[0].setEnabled(true);
                }
            }
        }
        else {
            Tracelog.log(Level.WARNING, true, String.format("Invalid draw option %s trying to be reverted", preferences.drawOption));
        }
    }
    
    /**
     * Sets the bounds of the specified component
     *
     * @param component The component to set bounds
     */
    private void setBounds(Component component) {

        // The position of the card when playing with `three` is all that concerns us since position matters, vs `single` card which are all stacked.
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(preferences.drawOption == DrawOption.THREE) {
            // Re-order the cards that exist currently, before determining where this card should be placed.
            Component[] components = layeredPane.getComponentsInLayer(layeredPane.getLayer(component));
            for(int i = components.length - 1; i >= 0; --i) {
                
                Rectangle bounds = new Rectangle(0, 0, component.getPreferredSize().width, component.getPreferredSize().height);                
                int positionIndex = layeredPane.getPosition(components[i]);
                int offset = 3 - components.length;
                switch(positionIndex + offset) {
                case 0:
                    bounds.x = 2 * CARD_OFFSET;
                    break;
                case 1:
                    bounds.x = CARD_OFFSET;
                    break;
                case 2:
                    bounds.x = 0;
                    break;
                }
                
                // Set the new bounds of the specified component
                components[i].setBounds(bounds);
            }            
        }
        else {
            // Set the default bounds of the card view
            Rectangle bounds = new Rectangle(0, 0, component.getPreferredSize().width, component.getPreferredSize().height);
            component.setBounds(bounds);
        }
    }
    
    public TalonCardState getState() {
        return _lastCardHandState;
    }
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        String header = "========" + this.getClass().getSimpleName().toUpperCase() + "========";
        builder.append(header + System.getProperty("line.separator"));
        
        int blankLayer = JLayeredPane.getLayer(_blankCard);
        JLayeredPane blankParentLayeredPane = (JLayeredPane) _blankCard.getParent();
        List<Component> components = Arrays.asList(blankParentLayeredPane.getComponentsInLayer(blankLayer));
        int blankPositionWithinlayer = components.indexOf(_blankCard);
        
        for(Component comp : layeredPane.getComponents()) {
            if(comp instanceof CardView) {
                builder.append(comp + System.getProperty("line.separator"));
            }
            else if(comp.equals(_blankCard)) {
                builder.append("===BLANK CARD===\t[" + blankLayer + "][" + blankPositionWithinlayer + "]" + System.getProperty("line.separator"));
            }
        }
        
        builder.append(System.getProperty("line.separator"));
        builder.append("Decks Played: " + _deckPlays + System.getProperty("line.separator"));
        builder.append("Last Card Hand State: " + _lastCardHandState + System.getProperty("line.separator"));
        builder.append("Undoable Card: " + String.valueOf(_undoableCard) + System.getProperty("line.separator"));
        builder.append("Last Card Interacted: " + String.valueOf(_lastCardInteracted) + System.getProperty("line.separator"));
        builder.append(System.getProperty("line.separator"));
        builder.append(new String(new char[header.length()]).replace("\0", "="));
        
        return builder.toString();
    }
    
    @Override public void render() {
        super.render();
        for(Component comp : layeredPane.getComponents()) {
            if(!comp.equals(_blankCard)) {
                comp.setVisible(false);
            }
        }
    }

    @Override public boolean isValidCollision(Component source) {
        return false;
    }

    @Override public void undoLastAction() {
        
        // If there was a card recoreded
        if(_undoableCard != null ) {
            
            // Get the highest component and set the enabled flag to so that it does not move anymore
            Component highestComponent = layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0];
            highestComponent.setEnabled(false);
            
            // This is required to be done, because when we call `addCard` below, it uses the `_lastCardInteracted`, so we
            // update this value accordingly
            TalonCardReference temp = _lastCardInteracted;
            _lastCardInteracted = _undoableCard;
            addCard(_undoableCard.card);
            _lastCardInteracted = temp;
            
            // Set the bounds of the card
            setBounds(_undoableCard.card);
            repaint();
        }
    }

    @Override public void performBackup() {
        
        // Get the card that is owned by the game view. When a drag occurs, the card is owned by the game view so that
        // it can be freely dragged around the entire game.
        CardView cardView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class).getCardComponent();
        
        // If the card cannot be found and if the talon doesnt have the blank card as the top most card
        if(cardView == null && layeredPane.highestLayer() != JLayeredPane.getLayer(_blankCard)) {
            
            // Take the card that is at the top-most of the talon. This is the case when we are
            // playing in outline mode, and the card still exists on the talon, because the card proxy
            // is the thing that is actually movings
            cardView = (CardView) layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0];
        }
        
        // If no card was specified and a backup is required, take the top most
        // card. This can only happen if an automove occurs
        if(_lastCardInteracted == null) {
            _lastCardInteracted = new TalonCardReference(getLastCard());
        }
        
        // Set the undoable card as the card that can be undone
        _undoableCard = new TalonCardReference(cardView, _lastCardInteracted.layer);            
    }

    @Override public void clearBackup() {
        _undoableCard = null;
    }

    @Override protected Point getCardOffset(CardView cardView) {
        // Handled by setBounds
        return new Point(0, 0);
    }
}