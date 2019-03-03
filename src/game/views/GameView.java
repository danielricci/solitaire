/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci <thedanny09@icloud.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package game.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.navigation.MenuBuilder;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.controllers.MovementRecorderController;
import game.menu.NewGameMenuItem;
import game.models.CardModel;
import game.views.helpers.ViewHelper;
import game.views.helpers.WinAnimationHelper;

import resources.LocalizationStrings;

/**
 * The game view wraps a draggable layout around the entire game
 * 
 * @author {@literal Daniel Ricci <thedanny09@icloud.com>}
 *
 */
public final class GameView extends PanelView {

    /**
     * Creates a new instance of this class type
     */
    public GameView() {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(0, 128, 0));
        
        // Configure constraint initial values
        final int _rowSize = 2;
        final int _columnSize = 7;
        GridBagConstraints gameConstraints = new GridBagConstraints();
        gameConstraints.weightx = 1.0;
        gameConstraints.anchor = GridBagConstraints.NORTH;
        
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        
        // Create the total list of cards
        List<CardModel> cards = CardModel.newInstances();
        
        // Create the globally available movement controller
        AbstractFactory.getFactory(ControllerFactory.class).add(new MovementRecorderController(), true);
        
        for(int row = _rowSize - 1; row >= 0; --row) {
            gameConstraints.gridy = row;

            if(gameConstraints.gridy ==  1) {
                gameConstraints.weighty = 1;
                gameConstraints.insets = new Insets(10, 0, 0, 0);
                gameConstraints.fill = GridBagConstraints.VERTICAL;
            }
            else {
                gameConstraints.weighty = 0;
                gameConstraints.insets = new Insets(10, 0, 0, 0);
            }

            for(int col = _columnSize - 1; col >= 0; --col) {
                gameConstraints.gridx = col;
                
                if(gameConstraints.gridy == 0) {
                    switch(gameConstraints.gridx) {
                    case 0: {
                        // Create the stock view 
                        StockView stockView = viewFactory.add(new StockView(), true);
                        GridBagConstraints stockConstraints = (GridBagConstraints)gameConstraints.clone();
                        stockConstraints.insets = new Insets(gameConstraints.insets.top, 0, -5, -5);
                        stockConstraints.ipadx = 5;
                        stockConstraints.ipady = 5;
                        this.add(stockView, stockConstraints);
                        break;
                    }
                    case 1: {
                        // Create the talon view
                        TalonPileView talonView = viewFactory.add(new TalonPileView(cards), true);
                        GridBagConstraints talonConstraints = (GridBagConstraints)gameConstraints.clone();
                        talonConstraints.insets = new Insets(gameConstraints.insets.top, 0, -5, -30);
                        talonConstraints.ipadx = 30;
                        talonConstraints.ipady = 5;
                        this.add(talonView, talonConstraints, 0);                        
                        break;
                    }
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        // Create the foundation view
                        FoundationPileView foundationView = viewFactory.add(new FoundationPileView());
                        this.add(foundationView, gameConstraints);
                    break;
                    }
                }
                else {
                    
                    List<CardModel> subList = cards.subList(0, gameConstraints.gridx + 1);
                    TableauPileView view = viewFactory.add(new TableauPileView(new ArrayList<CardModel>(subList)));
                    subList.clear();
                    
                    this.add(view, gameConstraints);
                }
            }
        }
        
        addStatusBarView();
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                OptionsPreferences preferences = new OptionsPreferences();
                preferences.load();
                if(preferences.timedGame) {
                    if(!SwingUtilities.isRightMouseButton(event)) {
                        AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                        removeMouseListener(this);
                    }
                }
            }
        });
        
        ViewHelper.registerForCardsAutocomplete(this);
    }
    
    /**
     * Adds the status bar view to this view
     */
    private void addStatusBarView() {
        StatusBarView statusBarView = AbstractFactory.getFactory(ViewFactory.class).add(new StatusBarView(), true);
        GridBagConstraints barConstraints = new GridBagConstraints(); 
        barConstraints.anchor = GridBagConstraints.SOUTH;
        barConstraints.gridx = 0;
        barConstraints.gridy = 1;
        barConstraints.fill = GridBagConstraints.HORIZONTAL;
        barConstraints.weightx = 1.0;
        barConstraints.weighty = 1.0;
        barConstraints.gridwidth = 7;
        barConstraints.insets = new Insets(0, -2, 0, -2);
        add(statusBarView, barConstraints, 0);
    }
    
    /**
     * Gets the current card component used on this view. This is the card
     * that would exist when performing a drag operation
     *
     * @return The currently dragged card of this view
     */
    public CardView getCardComponent() {
        for(Component comp : getComponents()) {
            if(comp instanceof CardView) {
                return (CardView) comp;
            }
        }
        
        return null;
    }
    
    /**
     * Scans the board for a win condition
     */
    public static void scanGameForWin() {
        boolean isWinner = true;
        for(FoundationPileView foundationView : AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationPileView.class)) {
            if(foundationView.layeredPane.getComponentCount() != 13) {
                isWinner = false;
                break;
            }
        }
        
        if(isWinner) {
            processWin();
        }
    }
    
    /**
     * Forces the game to win
     */
    public static void forceGameWin() {
        
        List<CardView> cards = AbstractFactory.getFactory(ViewFactory.class).getAll(CardView.class);
        cards.stream().forEach(z -> z.uncoverBackside(true));
        cards.stream().forEach(z -> z.setVisible(true));
        
        while(cards.size() > 0) {
            boolean keepGoing = false;
            for(int i = 0; i < cards.size(); ++i) {
                if(cards.get(i).performCardAutoMovement(true)) {
                    cards.get(i).setEnabled(false);
                    cards.remove(i);
                    keepGoing = true;
                    break;
                }
            }
            
            if(!keepGoing) {
                break;
            }
        }
    }
    
    /**
     * Process the events that will occur after a win has been detected 
     */
    private static void processWin() {
        // Stop the game timer
        TimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class);
        gameTimerView.stop();

        // Update the score with the bonus
        long bonus = AbstractFactory.getFactory(ViewFactory.class).get(ScoreView.class).updateScoreBonus(gameTimerView.getTime());
        
        // Show the updated text on the status bar
        AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).setMenuDescription(String.format(Localization.instance().getLocalizedString(LocalizationStrings.GAME_WON_STATUS_BAR), bonus));
        
        // Perform the animation on all the cards
        WinAnimationHelper.processCards();
    }
    
    /**
     * Shows the game over dialog, prompting the user to choose what they would like to do
     */
    public static void showGameOverDialog() {
        // Show the dialog indicating that game has won
        if(JOptionPane.showConfirmDialog(Application.instance, Localization.instance().getLocalizedString(LocalizationStrings.GAME_OVER), Localization.instance().getLocalizedString(LocalizationStrings.GAME_OVER_HEADER), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) { 
            MenuBuilder.search(Application.instance.getJMenuBar(), NewGameMenuItem.class).getComponent(AbstractButton.class).doClick();
        }
        else {
            // Clear the description and other status bar texts
            AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).clearMenuDescription();

            
            GameView gameView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class);
            // Get the tree and synchronize against it. Exceptions will be thrown during the removal process if we dont
            synchronized(gameView.getTreeLock()) {
                Component[] components = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class).getComponents();
                for(int i = 0; i < components.length; ++i) {
                    if(!(components[i] instanceof StatusBarView)) {
                        gameView.remove(components[i]);
                    }
                }
                Application.instance.repaint();
            }                
        }
    }
}