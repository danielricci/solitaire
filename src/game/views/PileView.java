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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JLayeredPane;

import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.physics.ICollide;

import game.controllers.CardController;
import game.models.CardModel;

public class PileView extends PanelView implements ICollide {

    /**
     * Specifies the offset of each card within this view
     */
    public int CARD_OFFSET = 12;
    
    /**
     * The layered pane that holds the list of cards
     */
    protected final JLayeredPane _layeredPane = new JLayeredPane();

    /**
     * Constructs a new instance of this class type
     */
    protected PileView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, this.getPreferredSize().height));
        setOpaque(false);
        add(_layeredPane, BorderLayout.CENTER);
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cards A list of card models to associate to this pile view
     */
    public PileView(List<CardModel> cards) {
        this();
        for(int i = 0; i < cards.size(); ++i) {
            
            //Create the card view
            CardView view = ViewFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
            cards.get(i).setBackside(i + 1 < cards.size());
            
            // Add the view to the layered pane
            _layeredPane.add(view);
            _layeredPane.setLayer(view, i);
            
            // Set the bounds of the view within the layered pane
            view.setBounds(new Rectangle(0, CARD_OFFSET * i, view.getPreferredSize().width, view.getPreferredSize().height));
        } 
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardViews The card views to associate to this view
     */
    public PileView(CardView[] cardViews) {
        this();
        for(int i = 0; i < cardViews.length; ++i) {

            CardView view = cardViews[i];
            
            // Add the view to the layered pane
            _layeredPane.add(view);
            _layeredPane.setLayer(view, i);
            
            // Set the bounds of the view within the layered pane
            view.setBounds(new Rectangle(0, CARD_OFFSET * i, view.getPreferredSize().width, view.getPreferredSize().height));
        }
    }
    
    @Override public void render() {
        super.render();
        for(Component component : _layeredPane.getComponents()) {
            if(component instanceof CardView) {
                CardView view = (CardView) component;
                view.render();
            }
        }
        repaint();
    }

    @Override public boolean isValidCollision(Component source) {

        // If there are no components then only allow a king to be placed
        if(_layeredPane.getComponentCount() == 0) {
            CardView cardView = (CardView) source;
            return cardView.getViewProperties().getEntity(CardController.class).getCard().getCardEntity().isCardKing();
        }
        
        if(!(_layeredPane.getComponent(0) instanceof CardView) ){
            return false;
        }
        
        // Get the bottom most card within the pile view.
        CardView cardView = (CardView) _layeredPane.getComponent(0);

        // Get the bounds associated to this pile view
        Rectangle thisBounds = this.getBounds();
        
        Rectangle thatBounds = cardView.getBounds();
        Rectangle rect = new Rectangle(
            thisBounds.x + thatBounds.x, 
            thisBounds.y + thatBounds.y, 
            source.getWidth(),
            source.getHeight()
        );
        
        // If the intersection is valid then verify if the card allows
        // for the collision
        if(source.getBounds().intersects(rect)) {
            return cardView.isValidCollision(source); 
        }
        
        return false;
    }
}