package entities;

import generated.DataLookup;
import generated.DataLookup.LAYER;

public final class DiamondCardEntity extends AbstractCardEntity {
    public DiamondCardEntity(DataLookup.DIAMONDS card) {
        super(DataLookup.LAYER.DIAMONDS);
        setActiveData(card.identifier);
    }
    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card._layer == LAYER.SPADES || card._layer == LAYER.CLUBS; 
    }

    @Override public boolean isCardRankedBefore(AbstractCardEntity card) {
        return false;
    }
}