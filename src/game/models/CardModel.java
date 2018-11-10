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

package game.models;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.communication.internal.signal.ISignalReceiver;
import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ModelFactory;
import framework.core.mvc.model.BaseModel;

import game.config.OptionsPreferences;
import game.entities.AbstractCardEntity;
import game.entities.ClubCardEntity;
import game.entities.DiamondCardEntity;
import game.entities.HeartCardEntity;
import game.entities.SpadeCardEntity;

import generated.DataLookup;

public class CardModel extends BaseModel {
    
    public static final String EVENT_UPDATE_BACKSIDE = "EVENT_UPDATE_BACKSIDE";
    
    private final AbstractCardEntity _cardEntity;
        
    public CardModel(AbstractCardEntity cardEntity) {
        _cardEntity = cardEntity;
        
        addSignalListener(EVENT_UPDATE_BACKSIDE, new ISignalReceiver<EventArgs>() {
            @Override public void signalReceived(EventArgs event) {
                OptionsPreferences preferences = new OptionsPreferences();
                preferences.load();
                _cardEntity.setBackside(preferences.deck);
                refresh();
            }
        });
    }
        
    public boolean isEmpty() {
        return _cardEntity == null;
    }
    
    public void setBackside(boolean backside) {
        _cardEntity.setBacksideVisible(backside);
    }
    
    public boolean getIsBackside() {
        return _cardEntity.getBacksideVisible();
    }
    
    public static List<CardModel> newInstances() {
        List<CardModel> entities = new ArrayList<CardModel>();
        ModelFactory factory = AbstractFactory.getFactory(ModelFactory.class);
        for(DataLookup.HEARTS heart : DataLookup.HEARTS.values()) {
            entities.add(factory.add(new CardModel(new HeartCardEntity(heart))));
        }
        for(DataLookup.CLUBS club : DataLookup.CLUBS.values()) {
            entities.add(factory.add(new CardModel(new ClubCardEntity(club))));
        }
        for(DataLookup.DIAMONDS diamond : DataLookup.DIAMONDS.values()) {
            entities.add(factory.add(new CardModel(new DiamondCardEntity(diamond))));
        }
        for(DataLookup.SPADES spade : DataLookup.SPADES.values()) {
            entities.add(factory.add(new CardModel(new SpadeCardEntity(spade))));
        }
        Collections.shuffle(entities);
        return entities;
    }
    
    public AbstractCardEntity getCardEntity() {
        return _cardEntity;
    }
    
    /**
     * Checks if the passed in card is ordinally before and of opposite suite to this card
     * @param card The card
     * 
     * @return TRUE if the card passed in is both ordinally before and of opposite suite to this card, FALSE otherwise
     */
    public boolean isCardBeforeAndOppositeSuite(CardModel card) {
        //System.out.println("Attempting to place " + card._cardEntity.toString() + " over " + _cardEntity.toString());
        return !_cardEntity.getBacksideVisible() && !card._cardEntity.getBacksideVisible() && card._cardEntity.isOppositeSuite(_cardEntity) && card._cardEntity.isCardRankedBefore(_cardEntity);
    }
    
    @Override public String toString() {
        return _cardEntity.toString();
    }
    
    @Override public Image getRenderableContent() {
        if(_cardEntity == null) {
            return null;
        }
        return _cardEntity.getRenderableContent();
    }
}