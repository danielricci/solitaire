package game.entities;

import generated.DataLookup;
import generated.DataLookup.LAYER;

public final class HeartCardEntity extends AbstractCardEntity {
    public HeartCardEntity(DataLookup.HEARTS card) {
        super(DataLookup.LAYER.HEARTS);
        setActiveData(card.identifier);
    }
    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card.layer == LAYER.SPADES || card.layer == LAYER.CLUBS; 
    }

    @Override public boolean isCardRankedBefore(AbstractCardEntity card) {
        return false;
    }
}