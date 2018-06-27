package game.entities;

import generated.DataLookup;
import generated.DataLookup.LAYER;

public final class ClubCardEntity extends AbstractCardEntity {
    public ClubCardEntity(DataLookup.CLUBS card) {
        super(DataLookup.LAYER.CLUBS);
        setActiveData(card.identifier);
    }

    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card.layer == LAYER.HEARTS || card.layer == LAYER.DIAMONDS; 
    }

    @Override public boolean isCardRankedBefore(AbstractCardEntity card) {
        return false;
    }
}