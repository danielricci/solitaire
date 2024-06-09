package game.menu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.utils.globalisation.Localization;

import game.views.StatusBarView;

import resources.LocalizationStrings;

public class GitHubMenuItem extends AbstractMenuItem {
    public GitHubMenuItem(JComponent parent) {
        super(new JMenuItem("GitHub Repository"), parent);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("GitHub Repository - " + Localization.instance().getLocalizedString(LocalizationStrings.GITHUB));
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }

    @Override public void onExecute(ActionEvent actionEvent) {
    	String url = Localization.instance().getLocalizedString(LocalizationStrings.GITHUB);
		try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            } else {
                Runtime.getRuntime().exec("cmd /c start " + url);
            }
		}
		catch(Exception exception) {
			exception.printStackTrace();
		}
    }
}