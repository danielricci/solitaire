package game.entities;

import generated.DataLookup;

public final class StockCardEntity extends AbstractCardEntity {
    public static int identifier = 0;
    public int identity = ++identifier;
    
    public StockCardEntity() {
        setBacksideVisible(true);
    }
    
    public void enableTalonRecycled() {
        setActiveData(DataLookup.MISC.TALON_RESTART.identifier);
        setBacksideVisible(false);
    }
    
    public void enableTalonEnd() {
        setActiveData(DataLookup.MISC.TALON_END.identifier);
        setBacksideVisible(false);
    }
    
    @Override public void refresh() {
    	super.refresh();
    }
        
    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return false;
    }
    
    @Override public String toString() {
        return "Identity: " + identity;
    }
}