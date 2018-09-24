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

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.utils.logging.Tracelog;

import game.models.CardModel;

public final class TalonView extends PileView {

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
            }
        });
        
        // Add it to the top, but make sure that the layer number is unique
        layeredPane.add(pv);
        layeredPane.setLayer(pv, layeredPane.getComponentCount() - 1);
    }
    
    /**
     * Cycles to the next card
     */
    public void showNextCard() {
        
        // If there is only one component then go o further. The idea is that the "blank" placeholder view that
        // mimics that switching of cards should never be removed from this view, thus if that is the only view that
        // exists then it should mean that all the playing cards having been removed from this view
        if(layeredPane.getComponentCount() == 1) {
            Tracelog.log(Level.INFO, true, "There are no more cards left in the Talon to play.");
            return;
        }
        
        // Get the layer id of the blank card within this view
        Component blankCardLayer = Arrays.asList(layeredPane.getComponents()).stream().filter(z -> !(z instanceof CardView)).findFirst().get();
        int blankCardLayerId = layeredPane.getLayer(blankCardLayer);
        
        if(blankCardLayerId != layeredPane.lowestLayer()) {
            Component cardDirectlyBelowBlankCard = layeredPane.getComponent(layeredPane.getIndexOf(blankCardLayer) + 1);
            layeredPane.setLayer(cardDirectlyBelowBlankCard, layeredPane.highestLayer() + 1);
            for(int i = layeredPane.getComponentCount() - 1, layerId = 0;  i >= 0; --i, ++layerId) {
                layeredPane.setLayer(layeredPane.getComponent(i), layerId);
            }
        }
        else {
            
            // Remove the list of components from the layered pane. 
            Component[] components = layeredPane.getComponents();
            layeredPane.removeAll();
            
            // Go through the list in reverse order and re-assign the layer identifiers as they were before
            // Note: This is done this way because if we re-order when they are within the layered pane
            // then the layered pane will control the index positions after the layer id is set, and during the
            // the resetting of the layer id's, these positions are not properly managed the way that we would want
            // them to be.
            for(int i = components.length - 1; i >= 0; --i) {
                layeredPane.add(components[i]);
                layeredPane.setLayer(components[i], i);
            }
        }
        
        // Debugging code
        //Arrays.asList(_layeredPane.getComponents()).stream().forEach(z -> System.out.println(_layeredPane.getLayer(z) + (!(z instanceof CardView) ? " <----------> " : "")));
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }
}