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
import game.views.helpers.DeckAnimationHelper;
import game.views.helpers.ViewHelper;

/**
 * This view handles the functionality related to the Stock. This view will show the 
 * collated cards view based on the deck face, and when you click on this view it will
 * cause the Talon to update the card list that it holds.
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
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
     * A reference to the Talon view. This is stored so that we don't need to query the view factory to get the Talon
     * which is costly given where it is being used
     */
    private final TalonPileView _talonView = AbstractFactory.getFactory(ViewFactory.class).get(TalonPileView.class);
    
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
                
                // Force an update to occur. We don't really need to worry about data binding
                // for something as straight forward as updating this view
                update(new ViewEventArgs(StockView.this, ""));
            }
        });        
        addSignal(BacksideCardEntity.DECK_BACKSIDE_CHANGED, new ISignalReceiver<EventArgs>() {
            @Override public void signalReceived(EventArgs event) {
                _stockCardEntities.stream().forEach(z -> z.refresh());
                DeckAnimationHelper.getInstance().setScene(_stockCardEntities);
            }
        });
        addSignal(DeckAnimationHelper.DECK_ANIMATION_UPDATED, new ISignalReceiver<EventArgs>() {
            @Override public void signalReceived(EventArgs event) {
            	//repaint();
            }
        });
    }
    
    @Override public void preprocessGraphics(IRenderable renderableData, Graphics context) {
        super.preprocessGraphics(renderableData, context);
        
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
            this.renderProperties.canDraw = true;
            this.renderProperties.x = 0;
            this.renderProperties.y = 0;
            this.renderProperties.width = CardView.CARD_WIDTH;
            this.renderProperties.height = CardView.CARD_HEIGHT;
            break;
        case 1:
            this.renderProperties.canDraw = _stockCardEntities.get(0).getBacksideVisible() && _talonView.isPhaseTwo();
            this.renderProperties.x = 2; 
            this.renderProperties.y = 1;
            this.renderProperties.width = CardView.CARD_WIDTH;
            this.renderProperties.height = CardView.CARD_HEIGHT;

            break;
        case 2:
            this.renderProperties.canDraw = _stockCardEntities.get(0).getBacksideVisible() && _talonView.isPhaseOne();
            this.renderProperties.x = 4;
            this.renderProperties.y = 2;
            this.renderProperties.width = CardView.CARD_WIDTH;
            this.renderProperties.height = CardView.CARD_HEIGHT;

            break;
        }
        
        this.renderProperties.renderData = DeckAnimationHelper.getInstance().getRenderableContent();
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH + 4, CardView.CARD_HEIGHT + 2);
    }

    @Override public void render() { 	
        super.render();
        
        DeckAnimationHelper.getInstance().setScene(_stockCardEntities);
        update(new ViewEventArgs(StockView.this, ""));
    }

    @Override public void update(EventArgs event) {
        super.update(event);
        addRenderableContent(DeckAnimationHelper.getInstance());
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