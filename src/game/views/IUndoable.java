package game.views;

import framework.api.IView;

/**
 * Defines the functionality required to perform an undo operation on a particular component
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public interface IUndoable extends IView {    
           
    /**
     * Performs an undo based on the last backup that was performed on this component
     */
    public void undoLastAction();

    /**
     * Performs a logical backup with respect to the state and business logic of this component
     */
    public void performBackup();
    
    /**
     * Clears the backup associated to this component
     */
    public void clearBackup();
}