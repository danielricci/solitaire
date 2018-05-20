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

import engine.core.mvc.view.PanelView;
import engine.core.mvc.view.layout.DraggableListener;
import engine.core.physics.CollisionListener;

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
        
    private CollisionListener _collisionListener = new CollisionListener(this);
    
    /**
     * Creates a new instance of this class type
     */
    public CardView() {
        setBackground(Color.red);
        setPreferredSize(new Dimension(400, 400));
        
        //CardModel model = new CardModel();
        //addRenderableContent(model._cardEntity);
    }
    
    @Override public void onViewInitialized() {
        
    }
    
    @Override public void clear() {       
    }
}