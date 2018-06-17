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

import java.awt.Dimension;

import controllers.GameController;
import engine.communication.internal.signal.arguments.AbstractEventArgs;
import engine.communication.internal.signal.arguments.ModelEventArgs;
import engine.core.factories.AbstractFactory;
import engine.core.mvc.view.PanelView;
import engine.core.mvc.view.layout.DraggableListener;
import game.core.factories.ControllerFactory;
import models.CardModel;

/**
 * This view handles the stock. The stock is a view that contains the leftover cards after the cards have been 
 * properly distributed on the board. Clicking on the stock view will display a card within another view.
 *
 * @author Daniel Ricci <thedanny09@gmail.com>
 *
 */
public final class TalonView extends PanelView {

    /**
     * Hold a reference to the game controller
     */
    private GameController _gameController = AbstractFactory.getFactory(ControllerFactory.class).get(GameController.class);

    
    private final DraggableListener _draggableListener = new DraggableListener(this);
    
    /**
     * Creates a new instance of this class type
     */
    public TalonView() {
        setPreferredSize(new Dimension(71, 96));
        _gameController.addSignalListener(this);
    }

    @Override public void onViewInitialized() {
    }

    @Override public void clear() {       
    }

    @Override public void registerSignalListeners() {
    }

    @Override public void update(AbstractEventArgs event) {
        
        super.update(event);
        
        if(event instanceof ModelEventArgs && event.getSource() instanceof CardModel) {
            CardModel cardModel = (CardModel)event.getSource();
            addRenderableContent(cardModel);
            repaint();
        }
    }
}