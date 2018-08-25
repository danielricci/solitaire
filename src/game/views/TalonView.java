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
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        
        for(int i = 0; i < 4;/*cards.size();*/ ++ i) {
            CardView cardView = AbstractFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
            cardView.setBounds(new Rectangle(0, 0, cardView.getPreferredSize().width, cardView.getPreferredSize().height));
            
            _layeredPane.add(cardView);
            _layeredPane.setLayer(cardView, i);
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
        _layeredPane.add(pv);
        _layeredPane.setLayer(pv, _layeredPane.getComponentCount() - 1);
    }
    
    /**
     * Cycles to the next card
     */
    public void showNextCard() {
        
        // If there is only one component then go o further. The idea is that the "blank" placeholder view that
        // mimics that switching of cards should never be removed from this view, thus if that is the only view that
        // exists then it should mean that all the playing cards having been removed from this view
        if(_layeredPane.getComponentCount() == 1) {
            Tracelog.log(Level.INFO, true, "There are no more cards left in the Talon to play.");
            return;
        }
        
        // Convert the array into a list for ease of use with the code down below
        List<Component> components = Arrays.asList(_layeredPane.getComponents());
        
        // Snapshot of the state of the component before modification
        //System.out.println("BEFORE");
        //components.stream().forEach(z -> System.out.println(_layeredPane.getLayer(z) + (!(z instanceof CardView) ? " <----------> " : "")));
        
        // Get the layer id of the blank card within this view
        Component blankCardLayer = components.stream().filter(z -> !(z instanceof CardView)).findFirst().get();
        int blankCardLayerId = _layeredPane.getLayer(blankCardLayer);
        
        // Find the card that has layer id one less than the blank card. Take that card
        // and 
        Optional<Component> cardToSwapWith = components.stream().filter(z -> _layeredPane.getLayer(z) == blankCardLayerId - 1).findFirst(); 
        if(cardToSwapWith.isPresent()) {
            
            
            
            int cardToSwapWithId = _layeredPane.getLayer(cardToSwapWith.get());
            _layeredPane.setLayer(cardToSwapWith.get(), blankCardLayerId);
            _layeredPane.setLayer(blankCardLayer, cardToSwapWithId);
        }
        else {
            // The blank card is at the bottom, reset the stack
            System.out.println("TODO: MUST RESET!!!!");
        }
        
//        int indexOfEmpty = components.indexOf(components.stream().filter(z -> !(z instanceof CardView)).findFirst().get());
//        if(indexOfEmpty == components.size() - 1) { // WRONG - should be if layer is 0
//            // end
//        }
//        else {
//            Component emptyCard = _layeredPane.getComponent(indexOfEmpty);
//            int emptyCardIndex = _layeredPane.getLayer(emptyCard);
//            
//            Component newCard = _layeredPane.getComponent(indexOfEmpty + 1);
//            int newCardIndex = _layeredPane.getLayer(newCard);
//            
//            _layeredPane.setLayer(newCard, emptyCardIndex);
//            _layeredPane.setLayer(emptyCard, newCardIndex);
//        
        
        //}
        
        // Snapshot of the state of the component after odification        
        //System.out.println("AFTER");
        //components.stream().forEach(z -> System.out.println(_layeredPane.getLayer(z)));
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }
}