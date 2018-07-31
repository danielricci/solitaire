/**
 *   Ricci <thedanny09@gmail.com>
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

import java.awt.Dimension;

import framework.communication.internal.signal.arguments.AbstractEventArgs;
import framework.core.mvc.view.PanelView;
import framework.core.physics.CollisionListener;

import game.models.CardModel;

public final class TalonView extends PanelView {

    //private final CardController _controller = AbstractFactory.getFactory(ControllerFactory.class).add(new CardController(null));
    
    @SuppressWarnings("unused")
    private CollisionListener _collisionListener = new CollisionListener(this);
    
    public TalonView() {
        setPreferredSize(new Dimension(71, 96));
        setOpaque(false);

        //new DragListener(this);
        
        //getViewProperties().setEntity(_controller);
    }
    
    @Override public void onViewInitialized() {
        super.onViewInitialized();
        
//        addMouseMotionListener(new MouseAdapter() {
//            @Override public void mouseDragged(MouseEvent e) {
//                ICollide collidedView = _collisionListener.getCollision();
//                if(collidedView != null) {
//                    System.out.println("YES!");
//                }
//                else {
//                    System.out.println("NO!");
//                }
//            }
//        });
    }
    
    
    @Override public void update(AbstractEventArgs event) {
        super.update(event);
        if(event.getOperationName() == CardModel.EVENT_NEXT_CARD) {
            CardModel cardModel = (CardModel)event.getSource();
            //_controller.setCard(cardModel);
            addRenderableContent(cardModel);
        }
        repaint();
    }
}