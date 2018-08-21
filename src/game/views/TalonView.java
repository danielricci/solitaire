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

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

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
            
            int layerPos = _layeredPane.getComponentCount();
            _layeredPane.add(cardView);
            _layeredPane.setLayer(cardView, layerPos);
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
        
        List<Component> components = Arrays.asList(_layeredPane.getComponents());
        
        System.out.println("BEFORE");
        for(Component component : components) {
           System.out.println(_layeredPane.getLayer(component) + (!(component instanceof CardView) ? " <----------> " : "")); 
        }
        
        int indexOfEmpty = components.indexOf(components.stream().filter(z -> !(z instanceof CardView)).findFirst().get());
        if(indexOfEmpty == components.size() - 1) { // WRONG
            // end
        }
        else {
            Component emptyCard = _layeredPane.getComponent(indexOfEmpty);
            int emptyCardIndex = _layeredPane.getLayer(emptyCard);
            
            Component newCard = _layeredPane.getComponent(indexOfEmpty + 1);
            int newCardIndex = _layeredPane.getLayer(newCard);
            
            _layeredPane.setLayer(newCard, emptyCardIndex);
            _layeredPane.setLayer(emptyCard, newCardIndex);
        
            System.out.println("AFTER");
            for(Component component : components) {
                System.out.println(_layeredPane.getLayer(component) + (!(component instanceof CardView) ? " <----------> " : "")); 
            }
        }
        
        
        //System.out.println("Index is " + indexOfEmpty);
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }
}