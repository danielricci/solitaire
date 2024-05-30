package game.entities;

import generated.DataLookup;

public final class StockCardEntity extends AbstractCardEntity {

	private static final SceneAnimationRenderer sceneAnimationRenderer = new SceneAnimationRenderer();
	
    public static int identifier = 0;
    public int identity = ++identifier;
    
    public StockCardEntity() {
        setBacksideVisible(true);
        System.out.println("Weeeee");
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