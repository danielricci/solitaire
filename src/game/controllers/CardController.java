/**
* Daniel Ricci <thedanny09@gmail.com>
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

package game.controllers;

import framework.core.mvc.controller.BaseController;
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

    /**
     * Handles when a click event is performed on the view associated to this controller
     */
    public void handleSingleClickAction() {
        if(_card.getIsBackside()) {
            _card.setBackside(false);
            _card.refresh();
        }
    }
}