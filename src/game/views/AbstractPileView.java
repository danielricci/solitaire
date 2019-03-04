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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JLayeredPane;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

/**
 * Abstract representation of a pile view
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public abstract class AbstractPileView extends PanelView implements IUndoable {
        
    /**
     * The layered pane that holds the list of cards
     */
    protected final JLayeredPane layeredPane = new JLayeredPane();

    /**
     * The list of cards that were previously moved, used for undo purposes
     */
    private final List<Component> _previousCards = new ArrayList<Component>();
    
    /**
     * Constructs a new instance of this class type
     */
    protected AbstractPileView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, this.getPreferredSize().height));
        setOpaque(false);
        add(layeredPane, BorderLayout.CENTER);
    }

    /**
     * Removes the highlights from this view and its underlying cards
     */
    public void removeHighlight() {
        for(Component comp : layeredPane.getComponents()) {
            CardView cardView = (CardView)comp;
            cardView.setIsHighlighted(false);
        }
        setIsHighlighted(false);
        repaint();
    }

    /**
     * @return The last ordered card held within this pile view
     */
    public CardView getLastCard() {
        if(layeredPane.getComponentCount() == 0) {
            return null;
        }
        
        Component comp = layeredPane.getComponents()[0];
        if(!(comp instanceof CardView)) {
            return null;
        }
        
        return (CardView)comp;
    }
        
    /**
     * Adds the specified card view to this pile
     *
     * @param card The card to add to this pile
     * @param position The position of the card
     * 
     */
    public void addCard(CardView cardView) {
        addCard(cardView, layeredPane.getComponents().length);
    }
    
    public void addCard(CardView cardView, int layerPosition) {
     
        // Hold onto a reference of the parent for repainting reasons
        Container parentCardView = cardView.getParent();
        
        // Get the list of components associated to the card view.
        // This list represents all the children associated to the said CardView.this reference.
        List<Component> components = new ArrayList<Component>(Arrays.asList(cardView.layeredPane.getComponents()));
        
        // Add the card view component, this will add it to the end
        components.add(cardView);
        
        // Reverse the list because layered panes associate objects closer to layer 0 as being closer to the screen.
        Collections.reverse(components);

        // Add the cards to this pile view
        for(Component comp : components) {
            layeredPane.add(comp);
            layeredPane.setLayer(comp, layerPosition);
            Point offset = getCardOffset((CardView)comp);
            comp.setBounds(new Rectangle(offset.x, offset.y, comp.getPreferredSize().width, comp.getPreferredSize().height));

            ++layerPosition;
        }
        
        parentCardView.repaint();
        repaint();
    }
    
    /**
     * Gets the offset that should be set to the specified card view
     *
     * @param cardView The cardview
     * 
     * @return The offset that this card should be at
     */
    protected abstract Point getCardOffset(CardView cardView);
    
    /**
     * @return The components associated to the layered pane of this view, grouped by layer identifier.
     */
    protected final List<Component[]> getComponentsGroupedByLayer() {
        List<Component[]> components = new ArrayList<Component[]>();
        for(int i = 0; i <= layeredPane.highestLayer(); ++i) {
            Component[] comps = layeredPane.getComponentsInLayer(i);
            if(comps.length > 0) {
                components.add(layeredPane.getComponentsInLayer(i));
            }
        }
        
        return components;
    }
    
    @Override public void undoLastAction() {
        List<Component> componentsList = Arrays.asList(layeredPane.getComponents());
        for(Component comp : _previousCards) {
            if(!componentsList.contains(comp)) {
                addCard((CardView) comp);
            }
        }
        
        repaint();
    }

    @Override public void performBackup() {

        // Get all the cards currently in the pile view
        List<Component> allComponents = Arrays.asList(layeredPane.getComponents());
        Collections.reverse(allComponents);
        _previousCards.addAll(allComponents);
        
        // Attempt to get the card that is currently being dragged.
        // Note: This is only valid in a non-outline scenario. In this scenario, the card that we are getting
        //       here is the card that was clicked on. The subsequent cards that are being dragged along with it
        //       if any can be found in the layered pane associated to this clicked card.
        CardView cardView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class).getCardComponent();
        if(cardView != null) {
            _previousCards.add(cardView);
            
            List<Component> components = Arrays.asList(cardView.layeredPane.getComponents());
            Collections.reverse(components);
            
            // Add any subsequent cards below the clicked card if any
            for(Component comp : components) {
                _previousCards.add(comp);
            }
        }
    }

    @Override public void clearBackup() {        
        _previousCards.clear();
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
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        String header = "========" + this.getClass().getSimpleName().toUpperCase() + "========";
        builder.append(header + System.getProperty("line.separator"));
        
        for(Component comp : layeredPane.getComponents()) {
            builder.append(comp + System.getProperty("line.separator"));
        }
        builder.append(new String(new char[header.length()]).replace("\0", "="));
        
        return builder.toString();
    }
}