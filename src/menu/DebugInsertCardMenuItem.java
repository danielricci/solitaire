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

package menu;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import application.Application;
import engine.core.factories.AbstractFactory;
import engine.core.factories.AbstractSignalFactory;
import engine.core.navigation.AbstractMenuItem;
import engine.utils.globalisation.Localization;
import game.core.factories.ViewFactory;
import views.CardView;
import views.GameView;

/**
 * Menu item for starting a new game
 * 
 * @author Daniel Ricci {@literal <thedanny09@gmail.com>}
 *
 */
public class DebugInsertCardMenuItem extends AbstractMenuItem {
    
    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public DebugInsertCardMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString("Insert Card")), parent);
    }

    @Override protected boolean enabled() {
        return Application.instance().isDebug() && AbstractFactory.isRunning();
    }
    
    @Override public void onExecute(ActionEvent actionEvent) {
        GameView gameView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class);
        CardView view = AbstractSignalFactory.getFactory(ViewFactory.class).add(new CardView(), false);
        gameView.add(view);
        view.render();
    }
    
}