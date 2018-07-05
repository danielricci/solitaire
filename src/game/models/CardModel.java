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

package game.models;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.core.mvc.model.BaseModel;
import game.entities.AbstractCardEntity;
import game.entities.ClubCardEntity;
import game.entities.DiamondCardEntity;
import game.entities.HeartCardEntity;
import game.entities.SpadeCardEntity;
import generated.DataLookup;

public final class CardModel extends BaseModel {
    
    public static final String EVENT_NEXT_CARD = "EVENT_NEXT_CARD";
    
    private final AbstractCardEntity _cardEntity;
        
    private CardModel(AbstractCardEntity cardEntity) {
        _cardEntity = cardEntity;
    }
    
    public static CardModel Empty() {
        return new CardModel(null);
    }
    
    public boolean isEmpty() {
        return _cardEntity == null;
    }
    
    public static List<CardModel> newInstances() {
        List<CardModel> entities = new ArrayList<CardModel>();
        for(DataLookup.HEARTS heart : DataLookup.HEARTS.values()) {
            entities.add(new CardModel(new HeartCardEntity(heart)));
        }
        for(DataLookup.CLUBS club : DataLookup.CLUBS.values()) {
            entities.add(new CardModel(new ClubCardEntity(club)));
        }
        for(DataLookup.DIAMONDS diamond : DataLookup.DIAMONDS.values()) {
            entities.add(new CardModel(new DiamondCardEntity(diamond)));
        }
        for(DataLookup.SPADES spade : DataLookup.SPADES.values()) {
            entities.add(new CardModel(new SpadeCardEntity(spade)));
        }
        Collections.shuffle(entities);

        // Create an empty card model to denote the end of the pile
        // TODO - See if we can avoid this shit
        entities.add(CardModel.Empty());
        
        return entities;
    }
    
    @Override public Image getRenderableContent() {
        if(_cardEntity == null) {
            return null;
        }
        return _cardEntity.getRenderableContent();
    }
}
