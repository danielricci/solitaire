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

import javax.swing.BoxLayout;

import framework.core.mvc.view.PanelView;
import framework.core.mvc.view.layout.DragListener;
import framework.core.physics.CollisionListener;

public final class CardProxyView extends PanelView {

    /**
     * The draggable listener associated to this view
     */
    private final DragListener _draggableListener = new DragListener(this);

    /**
     * The collision listener associated to this view
     */
    private final CollisionListener _collisionListener = new CollisionListener(this);
    
    /**
     * Constructs a new instance of this class type
     */
    public CardProxyView(CardView cardView) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT));
        setBackground(Color.PINK);

        // Set the controller of this proxy to the same controller of the specified card
        getViewProperties().setEntity(cardView.getViewProperties().getEntity());

    }
    
    private class WaitingThread extends Thread
    {
        @Override public void run() {
            synchronized(_draggableListener) {
                try {
                    _draggableListener.wait();
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("ALL DONE!");
        }
    }
    
    @Override public void render() {
        super.render();
        
//        // Perform a programmatic mouse-down at the location of the now proxy so that
//        // the drag appears to be happening
//        try {
//            Robot rob = new Robot();
//            rob.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//            
//            Thread waitThread = new WaitingThread();
//            waitThread.start();
//            
//        } catch (Exception exception) {
//            Tracelog.log(Level.SEVERE, true, exception);
//        }
    }
}