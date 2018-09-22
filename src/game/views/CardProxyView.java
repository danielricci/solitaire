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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.physics.CollisionListener;

import game.application.Game;

public final class CardProxyView extends PanelView {

    private class CardDragProxyEvents extends MouseAdapter {
        
        @Override public void mousePressed(MouseEvent event) {
            System.out.println("CardProxyView::mousePressed");
            
            // Remove this proxy from the associated card view
            _cardView.remove(CardProxyView.this);
            
            // Add this proxy to the game main container
            Game.instance().add(CardProxyView.this, 0);
            
            // Position this proxy at the same location where it was before
            // Note: Adding to the main game container will put this control
            //       at the main game container origin (0x, 0y)
            Point initialLocation = _cardView.getLocation();
            CardProxyView.this.setBounds(
                new Rectangle(
                        _cardView.getParent().getParent().getLocation().x + initialLocation.x, 
                        _cardView.getParent().getParent().getLocation().y + initialLocation.y, 
                        CardProxyView.this.getWidth(), 
                        CardProxyView.this.getHeight()
                )
            );
            
            // Redraw the corresponding container views
            _cardView.repaint();
            Game.instance().repaint();
        }
        
        @Override public void mouseReleased(MouseEvent event) {
            System.out.println("CardProxyView::mouseReleased");
            Game.instance().remove(CardProxyView.this);
            Game.instance().repaint();
        }
    }
    
    /**
     * The draggable listener associated to this view
     */
    private final DragListener _draggableListener = new DragListener(this);

    /**
     * The collision listener associated to this view
     */
    private final CollisionListener _collisionListener = new CollisionListener(this);
    
    /**
     * The card view associated to this proxy
     */
    private final CardView _cardView;
    
    /**
     * Constructs a new instance of this class type
     */
    public CardProxyView(CardView cardView) {
        setBackground(Color.PINK);

        // Set the controller of this proxy to the same controller of the specified card
        _cardView = cardView;
        getViewProperties().setEntity(cardView.getViewProperties().getEntity());

        addMouseListener(new CardDragProxyEvents());
    }
    
    @Override public Dimension getPreferredSize() {
        return _cardView.getPreferredSize();
    }
}