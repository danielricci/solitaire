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

package views;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import application.Application;
import engine.core.mvc.view.PanelView;
import engine.core.mvc.view.layout.DraggableListener;
import entities.DiamondCardEntity;
import entities.SpadeCardEntity;
import generated.DataLookup.DIAMONDS;
import generated.DataLookup.SPADES;
import models.CardModel;

/**
 * The main window view is the outer most shell that wraps everything
 * 
 * @author {@literal Daniel Ricci <thedanny09@gmail.com>}
 *
 */
public class CardView extends PanelView {

    /**
     * Draggable listener that listens in on this class for draggable events and handles them accordingly
     */
    private DraggableListener _draggableListener = new DraggableListener(this);
        
    //private CollisionListener _collisionListener = new CollisionListener(this);
    
    /**
     * Creates a new instance of this class type
     */
    public CardView(int i) {
        setPreferredSize(new Dimension(71, 96));
        CardModel model = new CardModel(i == 0 ? new DiamondCardEntity(DIAMONDS.D10) : new SpadeCardEntity(SPADES.S4));
        addRenderableContent(model);
    }
    
    @Override public void onViewInitialized() {
        addMouseListener(new MouseAdapter() {
            
            private Container _realParent;
            private int _index = 0;
            
            @Override public void mousePressed(MouseEvent e) {
                _realParent = CardView.this.getParent();
                Component[] components =_realParent.getComponents();
                for(int i = 0; i < components.length; ++i) {
                    if(components[i].equals(CardView.this)) {
                        _index = i;
                        break;
                    }
                }
                _realParent.remove(CardView.this);
                Application.instance().add(CardView.this);
            }
            @Override public void mouseReleased(MouseEvent e) {
                Application.instance().remove(CardView.this);
                Application.instance().repaint();
                _realParent.add(CardView.this, _index);
                _index = 0;
            }
        });
    }
    
    @Override public void clear() {       
    }

    @Override public void registerSignalListeners() {
    }
}