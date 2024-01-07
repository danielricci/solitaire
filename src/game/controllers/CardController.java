package game.controllers;

import framework.core.mvc.controller.BaseController;

import game.entities.AbstractCardEntity;
import game.models.CardModel;

public class CardController extends BaseController {

    /**
     * The card model associated to this controller
     */
    private final CardModel _card;
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param card The card model to associate to this controller
     */
    public CardController(CardModel card) {
        _card = card;
    }

    /**
     * @return The card associated to this view 
     */
    public CardModel getCard() {
        return _card;
    }

    /**
     * Refreshes the content of the controller
     */
    public void refresh() {
        _card.refresh();
    }
    
    public boolean isKing() {
        return _card.getCardEntity().isCardKing();
    }

    /**
     * Verifies if the specified card provided can be used within this current card
     * 
     * @param card The card to verify if it can be used to put over this card
     * 
     * @return TRUE if this card is before the specified card and of the same suite, FALSE otherwise 
     */
    public boolean isValidFoundationMove(CardModel card) {
        AbstractCardEntity thisCardEntity = _card.getCardEntity();
        AbstractCardEntity cardEntity = card.getCardEntity();
        return cardEntity.isSameSuite(thisCardEntity) && cardEntity.isCardRankedAfter(thisCardEntity); 
    }
}