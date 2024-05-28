package game.entities;

public final class NullCardEntity extends AbstractCardEntity {
    
    public NullCardEntity() {
        backsideCardEntity.setIsBacksideShowing(true);
    }
    
    @Override public boolean isOppositeSuite(AbstractCardEntity card) {
        return false; 
    }
}