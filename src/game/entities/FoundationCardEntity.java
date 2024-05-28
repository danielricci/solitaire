package game.entities;

import generated.DataLookup;

public final class FoundationCardEntity extends BacksideCardEntity {
    
    public FoundationCardEntity() {
        setActiveData(DataLookup.MISC.FOUNDATION.identifier);
    }
}