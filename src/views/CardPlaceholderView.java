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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;

import engine.core.mvc.view.PanelView;
import engine.core.mvc.view.layout.DraggableListener;

public final class CardPlaceholderView extends PanelView {

    //private CollisionListener _collisionListener = new CollisionListener(this);
    
    private DraggableListener _draggableListener = new DraggableListener(this);
    
    /**
     * Creates a new instance of this class type
     */
    public CardPlaceholderView() {
        //setBackground(Color.gray);
        //setPreferredSize(new Dimension(200, 200));
        setPreferredSize(new Dimension(71, 96));
        setBorder(BorderFactory.createLineBorder(Color.red));
        setBackground(Color.gray);
    }
    
    @Override public void onViewInitialized() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                System.out.println("Setting to " + (CardPlaceholderView.this.getParent().getComponentCount() - 1));
//                CardPlaceholderView.this.getParent().setComponentZOrder(
//                    CardPlaceholderView.this, 
//                    CardPlaceholderView.this.getParent().getComponentCount() - 1
//                );
            }
        });
        addMouseListener(new MouseAdapter() {
            
            @Override public void mouseReleased(MouseEvent e) {
                
            }
        });
    }
    
    @Override public void clear() {       
    }
}