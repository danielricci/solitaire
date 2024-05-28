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
        
        addSignal(EVENT_UPDATE_BACKSIDE, new ISignalReceiver<EventArgs>() {
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