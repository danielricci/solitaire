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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JLayeredPane;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.models.CardModel;

public final class TalonView extends TableauView {

    /**
     * Constructs a new instance of this class type
     * 
     * @param cards The card models to load within this view
     */
    public TalonView(List<CardModel> cards) {
        CARD_OFFSET = 0;
        
        for(int i = 0; i < cards.size(); ++ i) {
            CardView cardView = AbstractFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
            cardView.setBounds(new Rectangle(0, 0, cardView.getPreferredSize().width, cardView.getPreferredSize().height));
            
            layeredPane.add(cardView);
            layeredPane.setLayer(cardView, i);
        }
        
        // Create a blank panel view and blend it to the background of the game
        PanelView pv = new PanelView();
        pv.setBackground(new Color(0, 128, 0));
        pv.setBounds(new Rectangle(0, 0, CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
        pv.setVisible(true);
        
        // Note: Adding a mouse listener to the blank card prevents the card immediately behind it from being clicked 
        pv.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                GameTimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(GameTimerView.class);
                if(gameTimerView != null) {
                    gameTimerView.startGameTimer();
                }
            }
        });
        
        // Add it to the top, but make sure that the layer number is unique
        layeredPane.add(pv);
        layeredPane.setLayer(pv, layeredPane.getComponentCount() - 1);
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
        
        // If there is only one component then go o further. The idea is that the "blank" placeholder view that
        // mimics that switching of cards should never be removed from this view, thus if that is the only view that
        // exists then it should mean that all the playing cards having been removed from this view
        if(layeredPane.getComponentCount() == 1) {
            Tracelog.log(Level.INFO, true, "There are no more cards left in the Talon to play.");
            return TalonCardState.EMPTY;
        }
        
        // Get the layer id of the blank card within this view
        Component blankCardLayer = Arrays.asList(layeredPane.getComponents()).stream().filter(z -> !(z instanceof CardView)).findFirst().get();
        int blankCardLayerId = layeredPane.getLayer(blankCardLayer);
        
        // If we are at the end then restart the deck
        if(blankCardLayerId == layeredPane.lowestLayer()) {

            // New deck has the score updated
            AbstractFactory.getFactory(ViewFactory.class).get(GameScoreView.class).updateScoreDeckFinished();
            
            // Remove the list of components from the layered pane. 
            Component[] components = layeredPane.getComponents();
            layeredPane.removeAll();
            
            // Go through the list in reverse order and re-assign the layer identifiers as they were before
            // Note: This is done this way because if we re-order when they are within the layered pane
            // then the layered pane will control the index positions after the layer id is set, and during
            // the resetting of the layer id's, these positions are not properly managed the way that we would want
            // them to be.
            for(int i = components.length - 1; i >= 0; --i) {
                layeredPane.add(components[i]);
                layeredPane.setLayer(components[i], i);
            }            
        }
        else {
            OptionsPreferences preferences = new OptionsPreferences();
            preferences.load();
            switch(preferences.drawOption) {
            case ONE:
                Component cardDirectlyBelowBlankCard = layeredPane.getComponent(layeredPane.getIndexOf(blankCardLayer) + 1);
                layeredPane.setLayer(cardDirectlyBelowBlankCard, layeredPane.highestLayer() + 1);
                for(int i = layeredPane.getComponentCount() - 1, layerId = 0;  i >= 0; --i, ++layerId) {
                    layeredPane.setLayer(layeredPane.getComponent(i), layerId);
                }     
                break;
            case THREE: {
              CARD_OFFSET = 12;
              // Get the components that will be used for this card sequence
              List<CardView> components = new ArrayList<CardView>();
              final int maxIterations = 3;
              for(int blankCardIndex = layeredPane.getIndexOf(blankCardLayer), iterations = 1; blankCardIndex < layeredPane.getComponentCount() && iterations <= maxIterations; ++blankCardIndex, ++iterations) {
                  CardView component = (CardView) layeredPane.getComponent(blankCardIndex + 1);
                  components.add(component);
                  component.setBounds(new Rectangle(
                      (maxIterations - 1 - blankCardIndex) * CARD_OFFSET, 
                      0, // TODO - this needs to compond down the y-axis 
                      component.getPreferredSize().width, 
                      component.getPreferredSize().height)
                  );
              }
              //components.get(0).draggableListener.setEnabled(true);
              
              // Position the blank card underneath the last component index
              Component blankCard = layeredPane.getComponent(layeredPane.getIndexOf(blankCardLayer));
              int blankCardLayerNewPos = JLayeredPane.getLayer(components.get(components.size() - 1));
              layeredPane.setLayer(blankCard, blankCardLayerNewPos);
              
//              List<Component> components = new ArrayList<Component>();
//              for(int i = layeredPane.getIndexOf(blankCardLayer), iterations = 1; i < layeredPane.getComponentCount() && iterations <= 3; ++i, ++iterations) {
//                  components.add(layeredPane.getComponent(i + 1));
//                  
//              }
//              
//              components.stream().forEach(z -> System.out.println(z));
              
            }
            break;
            }
            
            if(blankCardLayerId - 1 == layeredPane.lowestLayer()) {
                return TalonCardState.DECK_PLAYED;
            }
        }
                
        return TalonCardState.NORMAL;
    }
    
    @Override public boolean isValidCollision(Component source) {
        return false;
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        String header = "=======TALON VIEW===========";
        builder.append(header + System.getProperty("line.separator"));
        
        for(Component comp : layeredPane.getComponents()) {
            builder.append((comp instanceof CardView ? comp : "[=======BLANK=======]") + System.getProperty("line.separator"));
        }
        builder.append(new String(new char[header.length()]).replace("\0", "="));
        return builder.toString();
    }
}