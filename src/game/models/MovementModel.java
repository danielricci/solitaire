package game.models;

import framework.core.mvc.model.BaseModel;

import game.views.FoundationPileView;
import game.views.StockView;
import game.views.TableauPileView;
import game.views.TalonPileView;

public class MovementModel extends BaseModel {
    
    public enum MovementType {
        STOCK,
        TALON,
        FOUNDATION,
        TABLEAU, 
        NONE;
        
        public static MovementType fromClass(Object clazz) {
            if(clazz instanceof StockView) {
                return MovementType.STOCK;
            }
            else if(clazz instanceof TalonPileView) {
                return MovementType.TALON;
            }
            else if(clazz instanceof FoundationPileView) {
                return MovementType.FOUNDATION;
            }
            else if(clazz instanceof TableauPileView) {
                return MovementType.TABLEAU;
            }
            else {
                return MovementType.NONE;
            }
        }
    }
    
    private MovementType _from;
    
    private MovementType _to;
    
    private boolean _isUndo;
    
    public void setMovement(MovementType from, MovementType to, boolean isUndo) {
        _from = from;
        _to = to;
        _isUndo = isUndo;
        
        doneUpdating();
    }
    
    public MovementType getFrom() {
        return _from;
    }
    
    public MovementType getTo() {
        return _to;
    }
    
    public boolean getIsUndo() {
        return _isUndo;
    }
}