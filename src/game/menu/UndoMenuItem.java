package game.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.utils.globalisation.Localization;

import game.controllers.MovementRecorderController;
import game.views.StatusBarView;

/**
 * Menu item for starting a new game
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class UndoMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public UndoMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString("Undo")), parent);
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_U);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("Undo last action");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }
    
    @Override protected boolean isEnabled() {
        return AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).canUndo();
    }
    
    @Override public void onExecute(ActionEvent actionEvent) {
        AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).undoLastMovement();
    }
}