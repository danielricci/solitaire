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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
import game.gameplay.MovementType;
import game.models.CardModel;

public final class TalonView extends AbstractPileView implements ICollidable {
    
    /**
     * The blank card associated to the talon view
     */
    private final PanelView _blankCard = new PanelView();
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cards The card models to load within this view
     */
    public TalonView(List<CardModel> cards) {

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
        
        for(int i = 0; i < cards.size(); ++ i) {
            CardView cardView = AbstractFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
            MouseAdapter adapter = new MouseAdapter() {
                @Override public void mouseReleased(MouseEvent event) {
            
                    if(!cardView.isEnabled()) {
                        return;
                    }
                    
                    // When the mouse is released, ensure that the component located at the highest layer is enabled
                    layeredPane.getComponentsInLayer(layeredPane.highestLayer())[0].setEnabled(true);
                    
                    // If the card is no longer associated to the talon, then remove its association to this event
                    if(!(cardView.getParentIView() instanceof TalonView)) {
                        cardView.removeMouseListener(this);
                        
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
                        // The position of the card when playing with `three` is all that concerns us since position
                        // matters, vs `single` card which are all stacked.
                        OptionsPreferences preferences = new OptionsPreferences();
                        preferences.load();
                        if(preferences.drawOption == DrawOption.THREE) {
                            
                            List<Component> visibleCards = Arrays.asList(layeredPane.getComponents()).stream().filter(z -> !z.equals(_blankCard) && z.isVisible()).collect(Collectors.toList());
                            Collections.reverse(visibleCards);
                            Rectangle bounds = new Rectangle(0, 0, cardView.getPreferredSize().width, cardView.getPreferredSize().height);
                            switch(visibleCards.indexOf(cardView)) {
                            case 0: // Left-most
                                bounds.x = bounds.y = 0;
                                break;
                            case 1: // Center
                                bounds.x = CARD_OFFSET;
                                bounds.y = 0;
                                break;
                            case 2: // Right-most
                                bounds.x = CARD_OFFSET * 2;
                                bounds.y = 0;
                                break;
                            default:
                                Tracelog.log(Level.SEVERE, true, "Could not place card back into the talon, given index is " + visibleCards.indexOf(cardView));
                                break;
                            }
                            cardView.setBounds(bounds);
                        }
                    }
                }
            };
            cardView.addMouseListener(adapter);
            cardView.getProxyView().addMouseListener(adapter);
            
            cardView.setBounds(new Rectangle(0, 0, cardView.getPreferredSize().width, cardView.getPreferredSize().height));
            
            // All cards are disabled by default, and should be disabled by default after a subsequent deck has been played through
            cardView.setEnabled(false);
            
            // Add the card to the layered pane and set it's layer accordingly
            layeredPane.add(cardView);
            layeredPane.setLayer(cardView, i);
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
        
        // Add it to the top, but make sure that the layer number is unique
        layeredPane.add(_blankCard);
        
        // Ad the blank card at the top of the deck 
        layeredPane.setLayer(_blankCard, layeredPane.getComponentCount() - 1);
    }
    
    public enum TalonCardState {
        // An Empty Deck
        EMPTY,
        // The Deck has been played through
        DECK_PLAYED,
        // Normal card played
        NORMAL
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
        
        // Get the layer id of the blank card within this view
        Component blankCardLayer = Arrays.asList(layeredPane.getComponents()).stream().filter(z -> !(z instanceof CardView)).findFirst().get();

        // Notify the movement controller that there was a movement that occured of the talon
        AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).recordMovement(MovementType.TALON, MovementType.TALON);
        
        // If we are at the end then restart the deck
        if(layeredPane.getLayer(blankCardLayer) == layeredPane.lowestLayer()) {

            // New deck has the score updated
            AbstractFactory.getFactory(ViewFactory.class).get(GameScoreView.class).updateScoreDeckFinished();
            
            // Go through the list in reverse order and re-assign the layer identifiers.
            // Make sure to reference a static list of components so that when the layer identifiers change
            // within the loop, the order of the layers will not go out of position
            Component[] components = layeredPane.getComponents();
            for(int i = components.length - 1; i >= 0; --i) {
                Component component = components[i];
                layeredPane.setLayer(component, i);
                component.setBounds(new Rectangle(0, 0, component.getPreferredSize().width, component.getPreferredSize().height));

                // Set the visible state of the component. This should make everything but the blank layer not visible
                components[i].setVisible(components[i].equals(_blankCard));
            }
        }
        else {
            OptionsPreferences preferences = new OptionsPreferences();
            preferences.load();
            switch(preferences.drawOption) {
                case ONE: {
                    
                    // Get the card that is directly below the blank card
                    Component cardDirectlyBelowBlankCard = layeredPane.getComponent(layeredPane.getIndexOf(blankCardLayer) + 1);
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

                    // If the highest layer is not the blank card, then from the blank card upwards, remove
                    // the visibility of the cards. This must be done before performing the three-card move
                    if(layeredPane.highestLayer() != layeredPane.getLayer(blankCardLayer)) {
                        // Note: Go from the card just above the blank card until the end (top) is reached
                        for(int i = layeredPane.getLayer(blankCardLayer) + 1; i <= layeredPane.highestLayer(); ++i) {
                            Component component = layeredPane.getComponentsInLayer(i)[0];
                            component.setVisible(false);
                        }
                    }
                    
                    final int maxIterations = 3;
                    for(
                            int blankCardIndex = layeredPane.getIndexOf(blankCardLayer), iterations = 1; 
                            blankCardIndex < layeredPane.getComponentCount() && layeredPane.lowestLayer() != layeredPane.getLayer(blankCardLayer) && iterations <= maxIterations; 
                            ++blankCardIndex, ++iterations
                    ) {
  
                    // Get the card that will be shifted. 
                    // Note: This card shoud be above the `blank invisible card` offset w.r.t the index layer
                    CardView card = (CardView) layeredPane.getComponent(blankCardIndex + 1);
                    card.setVisible(true);
                    
                    // Set the layer of the card that is directly below the blank card, to the highest layer
                    layeredPane.setLayer(card, layeredPane.highestLayer() + 1);
                      
                    // Set the bounding position of the card
                    card.setBounds(new Rectangle(
                            (iterations - 1) * CARD_OFFSET, 
                            0, // TODO - this needs to compond down the y-axis 
                            card.getPreferredSize().width, 
                            card.getPreferredSize().height));
                    }
  
                    // Iterate backwards through the talon deck
                    for(int i = layeredPane.getComponentCount() - 1, layerId = 0;  i >= 0; --i, ++layerId) {
      
                        // Re-synchronize the layer position of the card
                        layeredPane.setLayer(layeredPane.getComponent(i), layerId);
  
                        // Set the enabled state of the component. The only component that should be enabled is the top-most one
                        layeredPane.getComponent(i).setEnabled(layeredPane.highestLayer() == layeredPane.getLayer(layeredPane.getComponent(i)));
                    }
                    break;
                }
            }
            
            if(layeredPane.lowestLayer() == layeredPane.getLayer(blankCardLayer)) {
                return TalonCardState.DECK_PLAYED;
            }
        }
                
        return TalonCardState.NORMAL;
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
    
//    @Override public String toString() {
//        StringBuilder builder = new StringBuilder();
//        String header = "========" + this.getClass().getSimpleName().toUpperCase() + "========";
//        builder.append(header + System.getProperty("line.separator"));
//        
//        for(Component comp : layeredPane.getComponents()) {
//            if(comp instanceof JComponent) {
//                JComponent jcomp = (JComponent) comp;
//                builder.append((jcomp instanceof CardView ? jcomp : "[=======BLANK=======]" + "\t[" + JLayeredPane.getLayer(jcomp) + "]") + System.getProperty("line.separator"));
//            }
//        }
//        builder.append(new String(new char[header.length()]).replace("\0", "="));
//        return builder.toString();
//    }
}