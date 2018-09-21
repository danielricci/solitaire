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

import javax.swing.BoxLayout;

import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.physics.CollisionListener;
import framework.core.physics.ICollide;

import game.application.Game;

public final class CardProxyView extends PanelView implements ICollide {

    /**
     * The draggable listener associated to this proxy view
     */
    private final DragListener _draggableListener;

    /**
     * The collision listener associated to this proxy view
     */
    private final CollisionListener _collisionListener;

    /**
     * The card view associated to this proxy view
     */
    private final CardView _cardView; 
    
    /**
     * Constructs a new instance of this class type
     */
    public CardProxyView(CardView cardView) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
        setBackground(Color.PINK);
        
        _draggableListener = new DragListener(
            this
            //,cardView
        );
        
        _collisionListener = new CollisionListener(cardView);
        _cardView = cardView;
        
        
    }

    @Override public void render() {
        Game.instance().add(this, 0);
        Game.instance().repaint();
        
        super.render();
    }
    
    @Override public boolean isValidCollision(Component source) {
        return false;
    }
}