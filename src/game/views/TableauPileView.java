/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci <thedanny09@icloud.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package game.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import framework.api.IView;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.graphics.IRenderable;
import framework.core.mvc.view.PanelView;
import framework.core.physics.ICollidable;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.controllers.CardController;
import game.models.CardModel;

/**
 * This view represents a single Tableau pile 
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class TableauPileView extends AbstractPileView implements ICollidable {
    
    /**
     * The backside offset that the card should assume
     */
    private final int CARD_OFFSET_BACKSIDE = 3;
    
    /**
     * The offset for each card when in this view
     */
    private final int CARD_OFFSET = 15;
    
    /**
     * This panel is used as a placeholder within this view when there are no cards to be shown
     */
    private final PanelView _noCardPanelView = new PanelView();

    /**
     * A reference to the options preferences resource
     */
    private final OptionsPreferences _preferences = new OptionsPreferences();
    
    /**
     * Constructs a new instance of this class type
     */
    private TableauPileView() {
        
        // Force the rendering engine to attempt to render this view so that it can
        // render the panel view when no cards are available and the player is in outline mode
        setIsForceRendering(true);
        setOpaque(true);
        setBackground(new Color(0, 128, 0));
        
        // Set the background of the panel and render it
        _noCardPanelView.setBackground(Color.BLACK);
        _noCardPanelView.render();
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cards A list of card models to associate to this pile view
     */
    public TableauPileView(List<CardModel> cards) {
        this();
        for(int i = 0; i < cards.size(); ++i) {
            //Create the card view
            cards.get(i).setBackside(i + 1 < cards.size());
            CardView view = AbstractFactory.getFactory(ViewFactory.class).add(new CardView(cards.get(i)));
            
            // Add the view to the layered pane
            layeredPane.add(view);
            layeredPane.setLayer(view, i);
            
            // Set the bounds of the view within the layered pane
            Point offset = getCardOffset(view);
            view.setBounds(new Rectangle(offset.x, offset.y, view.getPreferredSize().width, view.getPreferredSize().height));
        }
    }
    
    @Override public void preProcessGraphics(IRenderable renderableData, Graphics context) {
        super.preProcessGraphics(renderableData, context);
        if(getIsHighlighted() && layeredPane.getComponentCount() == 0) {
            _preferences.load();
            if(_preferences.outlineDragging) {
                _noCardPanelView.setSize(new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
                _noCardPanelView.setPreferredSize(_noCardPanelView.getSize());
                add(_noCardPanelView);
                context.setXORMode(Color.WHITE);
            }
        }
        else {
            remove(_noCardPanelView);
        }
    }

    @Override public void render() {
        super.render();
        for(Component component : layeredPane.getComponents()) {
            if(component instanceof CardView) {
                CardView view = (CardView) component;
                view.render();
            }
        }
        repaint();
    }
    
    @Override protected Point getCardOffset(CardView cardView) {
        
        List<Component> cardComponents = Arrays.asList(layeredPane.getComponents());
        Collections.reverse(cardComponents);
        int index = cardComponents.indexOf(cardView);
        if(index == -1) {
            Tracelog.log(Level.SEVERE, true, "Cannot find the offset for the card " + cardView);
            return new Point();
        }
        
        // If there is only one card then it will have an offset of 0
        if(index == 0) {
            return new Point();
        }
       
        // Get a reference to the previous card and it's bounds
        CardView previousCard = (CardView)cardComponents.get(index - 1);
        Rectangle previousCardBounds = previousCard.getBounds();
        
        // If the card before this one has it's backside showing, then it should be
        // positioned 3 pixels lower than that card
        if(previousCard.isBacksideShowing()) {
            return new Point(0, previousCardBounds.y + CARD_OFFSET_BACKSIDE);
        }
        // Subsequent cards should be 12 pixels down from the previous card
        else {
            return new Point(0, previousCardBounds.y + CARD_OFFSET);
        }
    }

    @Override public boolean isValidCollision(Component source) {

        // If there are no components then only allow a king to be placed
        if(layeredPane.getComponentCount() == 0) {
            IView cardView = (IView)source;
            boolean isValidCollision = cardView.getViewProperties().getEntity(CardController.class).getCard().getCardEntity().isCardKing(); 
            return isValidCollision;
        }
        
        if(!(layeredPane.getComponent(0) instanceof CardView) ){
            return false;
        }
        
        // Get the bottom most card within the pile view.
        CardView cardView = (CardView) layeredPane.getComponent(0);

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

    @Override public void onCollisionStart(Component source) {
        CardView cardView = this.getLastCard();
        if(cardView != null) {
            cardView.onCollisionStart(source);
        }
    }

    @Override public void onCollisionStop(Component source) {
        CardView cardView = this.getLastCard();
        if(cardView != null) {
            cardView.onCollisionStop(source);
        }
    }   
}