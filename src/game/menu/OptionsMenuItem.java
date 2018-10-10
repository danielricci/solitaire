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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import framework.core.navigation.AbstractMenuItem;
import framework.core.navigation.MenuBuilder;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.views.OptionsDialogView;

import resources.LocalizationStrings;

/**
 * Menu item for pulling up the options menu
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class OptionsMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public OptionsMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString(LocalizationStrings.OPTIONS)), parent);
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_O);
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        OptionsDialogView options = new OptionsDialogView();
        options.render();
        
        if(options.getDialogResult() == JOptionPane.OK_OPTION && options.refreshGameRequired) {
            MenuBuilder.search(Application.instance.getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
        }
    }
}