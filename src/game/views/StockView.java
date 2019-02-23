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
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import framework.communication.internal.signal.ISignalReceiver;
import framework.communication.internal.signal.arguments.EventArgs;
import framework.communication.internal.signal.arguments.ViewEventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.graphics.IRenderable;
import framework.core.mvc.view.PanelView;
import framework.utils.MouseListenerEvent;
import framework.utils.MouseListenerEvent.SupportedActions;

import game.entities.BacksideCardEntity;
import game.entities.StockCardEntity;
import game.views.TalonPileView.TalonCardState;
import game.views.helpers.ViewHelper;

/**
 * This view handles the functionality related to the Stock. This view will show the 
 * collated cards view based on the deck face, and when you click on this view it wil
 * cause the Talon to update the card list that it holds.
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public final class StockView extends PanelView implements IUndoable {
 
    /**
     * The list of card entities that make up this view
     */
    private final List<StockCardEntity> _stockCardEntities = new ArrayList<StockCardEntity>() {{
       add(new StockCardEntity());
       add(new StockCardEntity());
       add(new StockCardEntity());
    }};
        
    /**
     * A reference to the talon view. This is stored so that we dont need to query the view factory to get the Talon
     * which is costly given where it is being used
     */
    private final TalonPileView _talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonPileView.class);
    
    /**
     * Constructs a new instance of this class type
     */
    public StockView() {
        setOpaque(false);
        ViewHelper.registerForCardsAutocomplete(this);
        
        // Register a mouse listener to start the timer
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if(!SwingUtilities.isRightMouseButton(event)) {
                    AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                    removeMouseListener(this);
                }
            }
        });
        
        // Register a mouse listener to handle when the left click action occurs on the view
        addMouseListener(new MouseListenerEvent(SupportedActions.LEFT) {
            @Override public void mousePressed(MouseEvent event) {
                
                super.mousePressed(event);
                if(getIsConsumed()) {
                    return;
                }

                _talonView.cycleNextHand();
                
                TalonCardState talonState = _talonView.getState();
                if(talonState == TalonCardState.DECK_PLAYED) {
                    if(_talonView.isTalonEnded()) {
                        _stockCardEntities.get(0).enableTalonEnd();
                    }
                    else {
                        _stockCardEntities.get(0).enableTalonRecycled();
                    }
                }
                else {
                    _stockCardEntities.remove(0);
                    _stockCardEntities.add(0, new StockCardEntity());
                    if(!SwingUtilities.isRightMouseButton(event)) {
                        AbstractFactory.getFactory(ViewFactory.class).get(TimerView.class).startGameTimer();
                    }
                }
                
                // Force an update to occur. We dont really need to worry about data binding
                // for something as straight forward as updating this view
                update(new ViewEventArgs(StockView.this, ""));
            }
        });
        
        // Add a signal to listen to when the deck backside has been changed so that this class can
        // update all the card entities backside to reflect the newly selected deck face
        addSignal(BacksideCardEntity.DECK_BACKSIDE_UPDATED, new ISignalReceiver<EventArgs>() {
            @Override public void signalReceived(EventArgs event) {
                _stockCardEntities.stream().forEach(z -> z.refresh());
            }
        });
    }
    
    @Override public void preProcessGraphics(IRenderable renderableData, Graphics context) {
        super.preProcessGraphics(renderableData, context);
        
        int index = -1;
        for(StockCardEntity stockCardEntity : _stockCardEntities) {
            if(stockCardEntity.equals(renderableData)) {
                index = _stockCardEntities.indexOf(stockCardEntity);
                break;
            }
        }
        
        switch(index)
        {
        case 0:
            this.extents.canDraw = true;
            this.extents.x = 0;
            this.extents.y = 0;
            break;
        case 1:
            this.extents.canDraw = _stockCardEntities.get(0).getBacksideVisible() && _talonView.isPhaseTwo();
            this.extents.x = 2; 
            this.extents.y = 1;
            break;
        case 2:
            this.extents.canDraw = _stockCardEntities.get(0).getBacksideVisible() && _talonView.isPhaseOne();
            this.extents.x = 4;
            this.extents.y = 2;
            break;
        }
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }

    @Override public void render() {
        super.render();
        
        // Because this view had to be stretched a bit in the contraints from the game view
        // to be able to render cards collated, we need to specify the width and height of the
        // extents, else the graphics pipeline will render the entire width and height of this view
        // causing the cards to be out of proportion.
        extents.width = this.getPreferredSize().width;
        extents.height = this.getPreferredSize().height;
        
        update(new ViewEventArgs(StockView.this, ""));
    }

    @Override public void update(EventArgs event) {
        super.update(event);
        _stockCardEntities.forEach(z -> addRenderableContent(z));
        repaint();
    }

    @Override public void undoLastAction() {
        _talonView.revertLastHand();

        if(_talonView.isDeckPlayed()) {
            _stockCardEntities.get(0).enableTalonRecycled();
        }
        else {
            _stockCardEntities.remove(0);
            _stockCardEntities.add(0, new StockCardEntity());
        }
        
        render();
    }

    @Override public void performBackup() {
        // Not needed
    }

    @Override public void clearBackup() {
        // Not needed
    }
}