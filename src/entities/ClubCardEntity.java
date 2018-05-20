package entities;

import generated.DataLookup;
import generated.DataLookup.LAYER;

public final class ClubCardEntity extends AbstractCardEntity {
    public ClubCardEntity(DataLookup.CLUBS card) {
        super(DataLookup.LAYER.CLUBS);
        setActiveData(card.identifier);
    }

    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card._layer == LAYER.HEARTS || card._layer == LAYER.DIAMONDS; 
    }

    @Override public boolean isCardRankedBefore(AbstractCardEntity card) {
        return false;
    }
}