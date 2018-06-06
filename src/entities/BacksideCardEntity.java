package entities;

import game.gameplay.AbstractDataEntity;
import generated.DataLookup;

public final class BacksideCardEntity extends AbstractDataEntity {
    
    private boolean _isBacksideVisible = false;

    public BacksideCardEntity() {
        super(DataLookup.LAYER.BACKSIDES.identifier);
        super.setActiveData(DataLookup.BACKSIDES.ORIGINAL_BACKSIDE.identifier);
    }
    
    public void setIsBacksideShowing(boolean isShowing) {
        _isBacksideVisible = isShowing;
    }
    
    public boolean getIsBacksideShowing() {
        return _isBacksideVisible;
    }
}