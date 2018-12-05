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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.communication.internal.signal.arguments.ViewEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.config.OptionsPreferences.ScoringOption;
import game.entities.TalonCardEntity;
import game.views.TalonView.TalonCardState;

public final class StockView extends PanelView {

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
        
        addMouseListener(new MouseAdapter() {            
            @Override public void mousePressed(MouseEvent event) {
                TalonView talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonView.class);
                
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
}