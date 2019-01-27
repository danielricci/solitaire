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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import framework.communication.internal.signal.ISignalReceiver;
import framework.communication.internal.signal.arguments.EventArgs;
import framework.communication.internal.signal.arguments.ViewEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.utils.MouseListenerEvent;
import framework.utils.MouseListenerEvent.SupportedActions;

import game.entities.BacksideCardEntity;
import game.entities.StockCardEntity;
import game.views.TalonPileView.TalonCardState;

public final class StockView extends PanelView implements IUndoable {

    /**
     * The backside card entity associated to this view
     */
    private StockCardEntity _stockCardEntity = new StockCardEntity();
        
    /**
     * Constructs a new instance of this class type
     */
    public StockView() {
        setOpaque(false);
        
        ViewHelper.registerForCardsAutocomplete(this);
        
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if(!SwingUtilities.isRightMouseButton(event)) {
                    AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                    removeMouseListener(this);
                }
            }
        });
        
        addMouseListener(new MouseListenerEvent(SupportedActions.LEFT) {
            @Override public void mousePressed(MouseEvent event) {
                
                super.mousePressed(event);
                if(getIsConsumed()) {
                    return;
                }

                TalonPileView talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonPileView.class);
                talonView.cycleNextHand();
                
                TalonCardState talonState = talonView.getState();
                if(talonState == TalonCardState.DECK_PLAYED) {
                    if(talonView.isTalonEnded()) {
                        _stockCardEntity.enableTalonEnd();
                    }
                    else {
                        _stockCardEntity.enableTalonRecycled();
                    }
                }
                else {
                    _stockCardEntity = new StockCardEntity();
                    if(!SwingUtilities.isRightMouseButton(event)) {
                        AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                    }
                }
                
                render();
            }
        });
        
        addSignal(BacksideCardEntity.DECK_BACKSIDE_UPDATED, new ISignalReceiver<EventArgs>() {
            @Override public void signalReceived(EventArgs event) {
                _stockCardEntity.refresh();
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
        addRenderableContent(_stockCardEntity);
        repaint();
    }

    @Override public void undoLastAction() {
        TalonPileView talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonPileView.class);
        talonView.revertLastHand();

        if(talonView.isDeckPlayed()) {
            _stockCardEntity.enableTalonRecycled();
        }
        else {
            _stockCardEntity = new StockCardEntity();
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