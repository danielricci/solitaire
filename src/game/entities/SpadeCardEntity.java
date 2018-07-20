package game.entities;

import generated.DataLookup;
import generated.DataLookup.LAYER;

public final class SpadeCardEntity extends AbstractCardEntity {
    public SpadeCardEntity(DataLookup.SPADES card) {
        super(DataLookup.LAYER.SPADES, card.ordinal(), card.identifier);
    }
    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return card.layer == LAYER.HEARTS || card.layer == LAYER.DIAMONDS; 
    }
}