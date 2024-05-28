package game.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

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
        super.getComponent(JMenuItem.class).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true));
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_D);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("Deal a new game");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        Application.instance.onRestart();
    }
}
