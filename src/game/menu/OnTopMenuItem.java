package game.menu;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.views.StatusBarView;

/**
 * The always-on-top menu item that keeps the game above all other applications
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class OnTopMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public OnTopMenuItem(JComponent parent) {
    	super(new JCheckBoxMenuItem(Localization.instance().getLocalizedString("Always on Top"), Application.instance.isAlwaysOnTop()), parent);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("Sets this application to always show above other applications");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }
    
    @Override protected boolean isEnabled() {
    	return super.isEnabled() && Application.instance.isAlwaysOnTopSupported();
    }
    
    @Override public void onExecute(ActionEvent actionEvent) {
    	OptionsPreferences preferences = new OptionsPreferences();
    	preferences.load();
    	preferences.alwaysOnTop = !preferences.alwaysOnTop;
    	preferences.save();
    	
    	Application.instance.setAlwaysOnTop(preferences.alwaysOnTop);
    }
}