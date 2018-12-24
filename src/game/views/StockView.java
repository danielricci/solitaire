/**
 * Daniel Ricci <thedanny09@icloud.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package game.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.communication.internal.signal.arguments.ViewEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.utils.MouseListenerEvent;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.config.OptionsPreferences.ScoringOption;
import game.entities.TalonCardEntity;
import game.views.TalonPileView.TalonCardState;

public final class StockView extends PanelView implements IUndoable {

    /**
     * The number of times that the deck was played
     */
    private int _deckPlays;
    
    public TalonCardEntity backside = new TalonCardEntity();
        
    /**
     * Constructs a new instance of this class type
     */
    public StockView() {
        setOpaque(false);
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        addMouseListener(new MouseListenerEvent() {            
            @Override public void mousePressed(MouseEvent event) {
                
                super.mousePressed(event);
                if(event.isConsumed()) {
                    return;
                }
                
                TalonPileView talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonPileView.class);
                
                TalonCardState cardState = talonView.showCardHand();
                if(cardState == TalonCardState.EMPTY) {
                    removeMouseListener(this);
                }
                else if(cardState == TalonCardState.DECK_PLAYED) {
                    
                    // Increase the number of times that the deck has been cycled through
                    ++_deckPlays;
                    
                    //In Draw One Vegas, you can only cycle through the card system once.
                    if(preferences.drawOption == DrawOption.ONE && preferences.scoringOption == ScoringOption.VEGAS && _deckPlays == 1) {
                        removeMouseListener(this);
                        backside.enableTalonEnd();
                    }
                    // In Draw Three Vegas, you can only cycle through the card system three times
                    else if(preferences.drawOption == DrawOption.THREE && preferences.scoringOption == ScoringOption.VEGAS && _deckPlays == 3) {
                        removeMouseListener(this);
                        backside.enableTalonEnd();
                    }
                    else
                    {
                        backside.enableTalonRecycled();
                    }
                    
                    render();
                }
                else {
                    backside = new TalonCardEntity();
                    render();
                    
                    GameTimerView gameTimerView = AbstractFactory.getFactory(ViewFactory.class).get(GameTimerView.class);
                    if(gameTimerView != null) {
                        gameTimerView.startGameTimer();
                    }
                }
            }
        });
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }

    @Override public void render() {
        super.render();
        update(new ViewEventArgs(this, ""));
    }
    
    @Override public void update(EventArgs event) {
        super.update(event);
        addRenderableContent(backside);
        repaint();
    }

    @Override public void undoLastAction() {
        TalonPileView talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonPileView.class);
        talonView.revertLastHand();

        if(talonView.isDeckPlayed()) {
            backside.enableTalonRecycled();
        }
        else {
            backside = new TalonCardEntity();
        }
        
        render();
    }

    @Override public void performBackup() {
        // There is nothing here we need to backup, because the data can be easily
        // retrieved.
        // Also, this method gets called too early in this case, so we can't read
        // the talon in time
    }

    @Override public void clearBackup() {
        
    }
}