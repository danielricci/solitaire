package entities;

import generated.DataLookup;
import generated.DataLookup.LAYER;

public final class SpadeCardEntity extends AbstractCardEntity {
    public SpadeCardEntity(DataLookup.SPADES card) {
        super(DataLookup.LAYER.SPADES);
        setActiveData(card.identifier);
    }
    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card._layer == LAYER.HEARTS || card._layer == LAYER.DIAMONDS; 
    }

    @Override public boolean isCardRankedBefore(AbstractCardEntity card) {
        return false;
    }
}