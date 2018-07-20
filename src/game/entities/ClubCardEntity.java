package game.entities;

import generated.DataLookup;
import generated.DataLookup.CLUBS;
import generated.DataLookup.LAYER;

public final class ClubCardEntity extends AbstractCardEntity {
    
    public ClubCardEntity(CLUBS card) {
        super(DataLookup.LAYER.CLUBS, card.ordinal(), card.identifier);
    }

    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card.layer == LAYER.HEARTS || card.layer == LAYER.DIAMONDS; 
    }
}