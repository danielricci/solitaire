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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.mvc.view.PanelView;
import game.controllers.GameController;
import game.entities.BacksideCardEntity;

public final class StockView extends PanelView {

    /**
     * The backside card entity
     */
    private final BacksideCardEntity _backside = new BacksideCardEntity();
    
    /**
     * Creates a new instance of this class type
     */
    public StockView() {
        setPreferredSize(new Dimension(71, 96));
    }

    @Override public void onViewInitialized() {
        GameController _gameController = AbstractFactory.getFactory(ControllerFactory.class).get(GameController.class);
        this.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent event) {
                _gameController.nextCard();
            }
        });
    }

    @Override public void render() {
        super.render();
        addRenderableContent(_backside);
        repaint();
    }
}