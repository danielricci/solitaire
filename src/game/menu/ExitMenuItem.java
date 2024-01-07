package game.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.views.StatusBarView;

/**
 * Menu item for exiting the game
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class ExitMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public ExitMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString("Exit")), parent);
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_X);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("Exit Solitaire");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        Application.instance.dispatchEvent(new WindowEvent(Application.instance, WindowEvent.WINDOW_CLOSING));
    }
}