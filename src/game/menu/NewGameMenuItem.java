/**
 * Daniel Ricci <thedanny09@icloud.com>
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

package game.menu;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.views.GameView;
import game.views.StatusBarView;

/**
 * Menu item for starting a new game
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class NewGameMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public NewGameMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString("Deal")), parent);
        super.getComponent(JMenuItem.class).setAccelerator(KeyStroke.getKeyStroke("F2"));
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_D);
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        
        if(AbstractFactory.isRunning()) {
            
            // Clear the factory of it's contents
            AbstractFactory.clearFactories();
            
            // Remove everything from the application UI
            Application.instance.getContentPane().removeAll();            
        }

        // Spawn a new game view and render its contents
        GameView gameView = AbstractFactory.getFactory(ViewFactory.class).add(new GameView(), true);
        Application.instance.setContentPane(gameView);
        
        StatusBarView statusBarView = AbstractFactory.getFactory(ViewFactory.class).add(new StatusBarView(), true);
        GridBagConstraints barConstraints = new GridBagConstraints(); 
        barConstraints.anchor = GridBagConstraints.SOUTH;
        barConstraints.gridx = 0;
        barConstraints.gridy = 1;
        barConstraints.fill = GridBagConstraints.HORIZONTAL;
        barConstraints.weightx = 1.0;
        barConstraints.weighty = 1.0;
        barConstraints.gridwidth = 7;
        barConstraints.insets = new Insets(0, -2, 0, -2);
        
        gameView.add(statusBarView, barConstraints, 0);
        gameView.render();
    }
}
