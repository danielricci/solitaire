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
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JLayeredPane;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.physics.ICollidable;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.controllers.MovementRecorderController;
import game.models.CardModel;

/**
 * This views represents the talon pile view
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public final class TalonPileView extends AbstractPileView implements ICollidable {
    
    /**
     * The blank card associated to the talon view
     */
    private final PanelView _blankCard = new PanelView();
    
    /**
     * The undoable container associated to this view
     */
    private CardView _undoableCard = null;
    
    /**
    private int _lastCardLayer = 0;
    
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
        
        // Add a listener to the blank card since it is sitting above the board. If someone tries to click in this area
        // the timer will start, unknowing to the player that they really clicked on a special area of the board
        _blankCard.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                GameTimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(GameTimerView.class);
                if(gameTimerView != null) {
                    gameTimerView.startGameTimer();
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
            MouseAdapter adapter = new MouseAdapter() {
//                @Override public void mousePressed(MouseEvent event) {
//                    int x = 55;
//                }
                @Override public void mouseReleased(MouseEvent event) {
            
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
     
    public enum TalonCardState {
        // An Empty Deck
        EMPTY,
        // The Deck has been played through
        DECK_PLAYED,
        // Normal card played
        NORMAL
    }
    
    @Override public void addCard(CardView cardView) {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(preferences.drawOption == DrawOption.THREE) {
            addCard(cardView, layeredPane.highestLayer());
            layeredPane.setPosition(cardView, 0);
        }
        else {
            super.addCard(cardView);
        }
    }
        
    /**
     * Displays the next card hand on this view
     * 
     * @return TRUE if the card is not the last card in the deck
     */
    public TalonCardState showCardHand() {
        
        // If there is only one component then dont go further. The idea is that the "blank" placeholder view that
        // mimics that switching of cards should never be removed from this view, thus if that is the only view that
        // exists then it should mean that all the playing cards having been removed from this view
        if(layeredPane.getComponentCount() == 1) {
            Tracelog.log(Level.INFO, true, "There are no more cards left in the Talon to play.");
            return TalonCardState.EMPTY;
        }
        
        // Notify the movement controller that there was a movement that occured of the talon, from the stock view
        AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).recordMovement(
                AbstractFactory.getFactory(ViewFactory.class).get(StockView.class), 
                this
        );
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        // If we are at the end then restart the deck
        if(JLayeredPane.getLayer(_blankCard) == layeredPane.lowestLayer()) {

            // New deck has the score updated
            AbstractFactory.getFactory(ViewFactory.class).get(GameScoreView.class).updateScoreDeckFinished();
            
            // Go through the list in reverse order and re-assign the layer identifiers.
            // Make sure to reference a static list of components so that when the layer identifiers change
            // within the loop, the order of the layers will not go out of position
            Component[] components = layeredPane.getComponents();
            
            switch(preferences.drawOption) {
            case ONE:
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
                layeredPane.removeAll();
                
                // Re-populate the layered pane 
                for(int i = 0, layer = 0; i < allComponents.length; ++i) {
                    // All cards are disabled by default, and should be disabled by default after a subsequent deck has been played through
                    allComponents[i].setEnabled(false);
                    
                    // Add the card to the layered pane and set it's layer accordingly
                    layeredPane.add(allComponents[i]);
                    
                    if(preferences.drawOption == DrawOption.THREE) {
                        layeredPane.setLayer(allComponents[i], layer / 3);
                        ++layer;
                    }
                }
                
                // Add back the blank card as the top-most card
                layeredPane.add(_blankCard);
                layeredPane.setLayer(_blankCard, layeredPane.highestLayer() + 1);
                
                // Repaint the layered pane
                layeredPane.repaint();
                break;
            }   
        }
        else {
            switch(preferences.drawOption) {
                case ONE: {
                    
                    // Get the card that is directly below the blank card
                    Component cardDirectlyBelowBlankCard = layeredPane.getComponent(layeredPane.getIndexOf(_blankCard) + 1);
                    cardDirectlyBelowBlankCard.setVisible(true);
                    
                    // Set the layer of the card that is directly below the blank card, to the highest layer
                    layeredPane.setLayer(cardDirectlyBelowBlankCard, layeredPane.highestLayer() + 1);
                    
                    // Starting from the lowest layer upwards, re-synchronize all the layer positions of the cards.
                    for(int i = layeredPane.getComponentCount() - 1, layerId = 0;  i >= 0; --i, ++layerId) {
                        
                        // Re-synchronize the layer position of the card
                        layeredPane.setLayer(layeredPane.getComponent(i), layerId);

                        // Set the enabled state of the component. The only component that should be enabled is the top-most one, the rest should be disabled
                        layeredPane.getComponent(i).setEnabled(layeredPane.highestLayer() == layeredPane.getLayer(layeredPane.getComponent(i)));
                    }
                    break;
                }
                case THREE: {

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
                    break;
                }
            }

            if(layeredPane.lowestLayer() == JLayeredPane.getLayer(_blankCard)) {
                return TalonCardState.DECK_PLAYED;
            }
        }
                
        return TalonCardState.NORMAL;
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
            
            // Add the card to the layered pane
            layeredPane.add(_undoableCard);
            
            // Set the layer of the card to be the highest layer
            layeredPane.setLayer(_undoableCard, layeredPane.highestLayer() + 1);
            
            // Set the bounds of the card
            setBounds(_undoableCard);
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
        
        // Set the undoable card as the card that can be undone
        _undoableCard = cardView;
    }

    @Override public void clearBackup() {
        _undoableCard = null;
    }
}