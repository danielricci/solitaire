package game.entities;

import java.awt.Image;
import java.util.UUID;

import framework.core.entity.DataEntity;

import generated.DataLookup.BACKSIDES;
import generated.DataLookup.LAYER;

public abstract class AbstractCardEntity extends DataEntity {

    /**
     * The backside entity associated to this card
     */
    public final BacksideCardEntity backsideCardEntity = new BacksideCardEntity();
    
    /**
     * The layer associated to this entity
     */
    protected final LAYER layer;
    
    /**
     * The UUID associated to the entity data
     */
    protected final UUID identifier;
    
    /**
     * The ordinal associated to the entity data
     */
    protected final int ordinal;
    
    /**
     * Constructs a new instance of this class type
     */
    protected AbstractCardEntity() {
        layer = null;
        identifier = null;
        ordinal = -1;
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param layer The layer to set this card entity to
     * @param ordinal The oridinal position of the card
     * @param the uuid associated to the card
     */
    protected AbstractCardEntity(LAYER layer, int ordinal, UUID identifier) {
        this.layer = layer;
        this.identifier = identifier;
        this.ordinal = ordinal;
        
        setActiveData(identifier);
    }
    
    /**
     * Indicates if this card is before the one specified.
     * 
     * Note: This method is suite agnostic
     * 
     * @param card The card to check rank against
     * 
     * @return TRUE if the card is ranked before the one specified, false otherwise
     */
    public final boolean isCardRankedBefore(AbstractCardEntity card) {
        return ordinal + 1 == card.ordinal;
    }
    
    /**
     * Indicates if this card is after the one specified.
     * 
     * Note: This method is suite agnostic
     * 
     * @param card The card to check rank against
     * 
     * @return TRUE if the card is ranked after the one specified, false otherwise
     */
    public final boolean isCardRankedAfter(AbstractCardEntity card) {
        return ordinal - 1 == card.ordinal;
    }

    /**
     * @return TRUE if this card is an ACE, false otherwise
     */
    public final boolean isAceCard() {
        return ordinal == 0;
    }
    
    /**
     * Indicates if this card's suite is opposite to the card specified. An opposite suite is one that is of different color
     *
     * @param card The abstract card entity
     *  
     * @return TRUE if this card's suite is opposite to the one specified, FALSE otherwise
     */
    public abstract boolean isOppositeSuite(AbstractCardEntity card);

    /**
     * Indicates if this card's suite is the same as the specified card
     * 
     * @param card The card check suite against
     * 
     * @return TRUE if this card and the card specified are of the SAME suit, FALSE otherwise
     */
    public final boolean isSameSuite(AbstractCardEntity card) {
        return card.layer.equals(layer);
    }
    
    /**
     * Sets the visibility of the cards' backside
     * 
     * @param isVisible TRUE if the backside of the card is visible, FALSE otherwise
     */
    public final void setBacksideVisible(boolean isVisible) {
        backsideCardEntity.setIsBacksideShowing(isVisible);
    }
    
    /**
     * @return TRUE if the backside is visible, FALSE otherwise
     */
    public boolean getBacksideVisible() {
        return backsideCardEntity.getIsBacksideShowing();
    }
    
    /**
     * @return TRUE if this card is a king, FALSE otherwise
     */
    public final boolean isCardKing() {
        return ordinal == 12;
    }
    
    @Override public void refresh() {
        backsideCardEntity.refresh();
    }
    
    @Override public Image getRenderableContent() {
        if(getBacksideVisible()) {
            return backsideCardEntity.getRenderableContent();
        }
        return super.getRenderableContent();
    }
    
    @Override public String toString() {
        if(layer != null) {
            return (!getBacksideVisible() ? "[F]" : "[B]") + ("\t") + (ordinal + 1) + " of " + layer.toString();    
        }
        return super.toString();
    }

    /**
     * Sets the backside of this entity
     *
     * @param backside The backside to set this entity with
     */
    public void setBackside(BACKSIDES backside) {
        backsideCardEntity.setActiveData(backside.identifier);
    }
}