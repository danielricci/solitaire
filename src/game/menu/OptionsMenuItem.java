package game.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.core.navigation.MenuBuilder;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.views.CardView;
import game.views.OptionsDialogView;
import game.views.StatusBarView;

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
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("Change Solitaire options");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        
        // Clear the description when the execution has occured. This is so that the description does not stay
        // stuck until the dialog has closed
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
        
        OptionsDialogView options = new OptionsDialogView();
        options.render();
        
        if(options.getDialogResult() == JOptionPane.OK_OPTION) {
            if(options.refreshGameRequired) {
                Application.instance.isRestarting = true;
                MenuBuilder.search(Application.instance.getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
            }
            else {
                if(options.statusBarChanged) {
                    AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).synchronizeWithOptions();
                }
                if(options.outlineDraggingChanged) {
                    EventArgs args = new EventArgs(this, CardView.EVENT_OUTLINE_SYNCHRONIZE);
                    args.setSuppressUpdate(true);
                    AbstractFactory.getFactory(ViewFactory.class).multicastSignalListeners(CardView.class, args);
                }
            }
        }
    }
}