/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci {@literal <thedanny09@icloud.com>}
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