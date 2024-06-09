package game.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.views.StatusBarView;

import resources.LocalizationStrings;

/**
 * The about menu item that displays information about the application
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class AboutMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public AboutMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString(LocalizationStrings.ABOUT)), parent);
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_A);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("About Solitaire");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        JTextArea textArea = new JTextArea(8, 20);
        textArea.setText(Localization.instance().getLocalizedString(LocalizationStrings.ABOUT_MESSAGE));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(UIManager.getColor("OptionsPane.background"));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        JPanel panel = new JPanel();
        panel.add(scrollPane);

        // Create a custom dialog
        JOptionPane.showMessageDialog(
        		Application.instance, 
        		panel, 
        		Localization.instance().getLocalizedString(LocalizationStrings.ABOUT), 
        		JOptionPane.INFORMATION_MESSAGE);
    }
}