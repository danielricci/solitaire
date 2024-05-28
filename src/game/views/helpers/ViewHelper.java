package game.views.helpers;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import framework.api.IView;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.utils.MouseListenerEvent;
import framework.utils.MouseListenerEvent.SupportedActions;

import game.views.CardView;
import game.views.TableauPileView;
import game.views.TalonPileView;

/**
 * Helper class for common view related functionality
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public class ViewHelper {
    
    /**
     * Registers the specified view so that it can initiate an Autocomplete opertation 
     *
     * @param view The specified view to register this event onto
     */
    public static void registerForCardsAutocomplete(IView view) {
        view.getContainerClass().addMouseListener(new MouseListenerEvent(SupportedActions.RIGHT) {
            @Override public void mousePressed(MouseEvent event) {
                super.mousePressed(event);
                if(this.getIsConsumed()) {
                    return;
                }
                if(SwingUtilities.isLeftMouseButton(event)) {
                    return;
                }
                
                performCardsAutocomplete();
            }
        });
    }
    
    /**
     * Performs an auto complete based on all available cards
     */
    private static void performCardsAutocomplete() {
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        List<CardView> cards = new ArrayList<CardView>();
        
        // Get the top-most talon card
        CardView talonCard = viewFactory.get(TalonPileView.class).getLastCard();
        if(talonCard != null) {
            cards.add(talonCard);    
        }
        
        // Get all available top-most front-facing cards
        List<TableauPileView> tableauPileViews = viewFactory.getAll(TableauPileView.class);
        Collections.reverse(tableauPileViews);
        for(TableauPileView view : tableauPileViews) {
            CardView card = view.getLastCard();
            if(card != null && !card.isBacksideShowing()) {
                cards.add(card);
            }
        }
        
        // Go through the list and apply the automove on each card until there are no cards
        // left or all cards have been iterated over
        while(cards.size() > 0) {
            boolean keepGoing = false;
            for(int i = 0; i < cards.size(); ++i) {
                if(cards.get(i).performCardAutoMovement()) {
                    cards.remove(i);
                    keepGoing = true;
                    break;
                }
            }
            
            if(!keepGoing) {
                break;
            }
        }
        
        // If the talon card was moved then enabled the top-most card so that the next card can be played
        // TODO Can this be self-contained??
        if(talonCard != null && !cards.contains(talonCard)) {
            CardView lastTalonCard = viewFactory.get(TalonPileView.class).getLastCard();
            if(lastTalonCard != null) {
                lastTalonCard.setEnabled(true);
            }
        }
    }
}