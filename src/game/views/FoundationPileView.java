package game.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import framework.api.IView;
import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.graphics.IRenderable;
import framework.core.physics.ICollidable;

import game.controllers.CardController;
import game.entities.FoundationCardEntity;
import game.views.helpers.ViewHelper;

/**
 * This view represents the foundation pile view
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public final class FoundationPileView extends AbstractPileView implements ICollidable {

    /**
     * Creates a new instance of this class type
     */
    public FoundationPileView() {
        
        // The background the the opaqueness of this view
        // must be set this way to achieve the proper xor effect
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        addRenderableContent(new FoundationCardEntity());
        ViewHelper.registerForCardsAutocomplete(this);
    }

    @Override public void preprocessGraphics(IRenderable renderableData, Graphics context) {
        super.preprocessGraphics(renderableData, context);
        if(getIsHighlighted() && layeredPane.getComponentCount() == 0) {
            context.setXORMode(Color.WHITE);
        }
    }
    
    @Override public Dimension getPreferredSize() {
        return new Dimension(CardView.CARD_WIDTH, CardView.CARD_HEIGHT);
    }

    @Override public void update(EventArgs event) {
        super.update(event);
        repaint();
    }

    @Override public boolean isValidCollision(Component source) {
        if (layeredPane.getComponentCount() == 0) {
            return ((IView) source).getViewProperties().getEntity(CardController.class).getCard().getCardEntity().isAceCard();
        } else {
            CardView thisCardView = (CardView) layeredPane.getComponent(0);
            return thisCardView.isValidCollision(source);
        }
    }

    @Override public void addCard(CardView cardView) {
        super.addCard(cardView);
        GameView.scanGameForWin();
    }

    @Override protected Point getCardOffset(CardView cardView) {
        return new Point(0, 0);
    }

    @Override public void onCollisionStart(Component source) {
        CardView cardView = this.getLastCard();
        if(cardView != null) {
            cardView.onCollisionStart(source);
        }
    }

    @Override public void onCollisionStop(Component source) {
        CardView cardView = this.getLastCard();
        if(cardView != null) {
            cardView.onCollisionStop(source);
        }
    }
}