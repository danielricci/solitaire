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

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

import framework.core.mvc.controller.BaseController;
import framework.utils.logging.Tracelog;

import game.models.CardModel;

public final class GameController extends BaseController {

    private final LinkedList<CardModel> _cardsQueue = new LinkedList<CardModel>();

    public void setCards(Collection<CardModel> cards) {
        _cardsQueue.addAll(cards);
    }
    
    public void nextCard() {
        if(_cardsQueue.isEmpty()) {
            Tracelog.log(Level.INFO, true, "Attempting to get the next card when there are no cards left to play");
            return;
        }
        
        // Take the card and put it at the end of the list
        _cardsQueue.addLast(_cardsQueue.pop());
        _cardsQueue.element().refresh();
    }

    public boolean isNextCardEmpty() {
        return _cardsQueue.isEmpty() || _cardsQueue.size() <= 1 || _cardsQueue.get(1).isEmpty();
    }
}