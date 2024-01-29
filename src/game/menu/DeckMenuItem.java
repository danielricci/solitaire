package game.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ModelFactory;
import framework.core.factories.ViewFactory;
import framework.core.navigation.AbstractMenuItem;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.entities.BacksideCardEntity;
import game.models.CardModel;
import game.views.DeckSelectionDialogView;
import game.views.StatusBarView;
import game.views.StockView;

import resources.LocalizationStrings;

/**
 * Menu item for starting a new game
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public class DeckMenuItem extends AbstractMenuItem {

    /**
     * Constructs a new instance of this class type
     *
     * @param parent The parent associated to this menu item
     */
    public DeckMenuItem(JComponent parent) {
        super(new JMenuItem(Localization.instance().getLocalizedString(LocalizationStrings.DECK)), parent);
        super.getComponent(JMenuItem.class).setMnemonic(KeyEvent.VK_C);
    }
    
    @Override protected void onEntered(EventObject event) {
        super.onEntered(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription("Choose new deck back");
    }
    
    @Override protected void onExited(EventObject event) {
        super.onExited(event);
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
    }

    @Override public void onExecute(ActionEvent actionEvent) {
        
        // Clear the description when the execution has occurred. This is so that the description does not stay
        // stuck until the dialog has closed
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();
        
        DeckSelectionDialogView view = new DeckSelectionDialogView();
        view.render();
        if(view.getDialogResult() == JOptionPane.OK_OPTION) {
            
            // Update all the backsides of all the cards in the game
            EventArgs args = new EventArgs(this, CardModel.EVENT_UPDATE_BACKSIDE);
            AbstractFactory.getFactory(ModelFactory.class).multicastSignalListeners(CardModel.class, args);
            
            OptionsPreferences preferences = new OptionsPreferences();
            preferences.load();

            // Send out the signal to the stockview
            AbstractFactory.getFactory(ViewFactory.class).multicastSignalListeners(StockView.class, new EventArgs(this, BacksideCardEntity.DECK_BACKSIDE_UPDATED));
        }
    }
}