package entities;

import game.gameplay.AbstractDataEntity;
import generated.DataLookup;

public abstract class AbstractCardEntity extends AbstractDataEntity {
    
    /**
     * The layer associated to this entity
     */
    protected final DataLookup.LAYER _layer;
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param layer The layer to set this card entity to
     */
    public AbstractCardEntity(DataLookup.LAYER layer) {
        super(layer.identifier);
        _layer = layer;
    }
    
    /**
     * Indicates if this card's suite is the same as the specified card
     * 
     * @param card The card check suite against
     * 
     * @return TRUE if this card and the card specified are of the SAME suit, FALSE otherwise
     */
    public final boolean isSameSuite(AbstractCardEntity card) {
        return card._layer.equals(_layer);
    }
    
    /**
     * Indicates if this card's suite is opposite to the card specified. An opposite suite is one that is of different color
     *  
     * @return TRUE if this card's suite is opposite to the one specified, FALSE otherwise
     */
    public abstract boolean isOppositeSuite(AbstractCardEntity card);
    
    /**
     * Indicates if this card is before the one specified. A card that is before is one that has one rank higher than the one specified
     * 
     * Note: This method is suite agnostic
     * 
     * @param card The card to check rank against
     * 
     * @return TRUE if the card is ranked before the one specified, false otherwise
     */
    public abstract boolean isCardRankedBefore(AbstractCardEntity card);
}