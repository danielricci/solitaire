package game.controllers;

import java.util.logging.Level;

import framework.communication.internal.signal.ISignalListener;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.controller.BaseController;
import framework.utils.logging.Tracelog;

import game.models.MovementModel;
import game.models.MovementModel.MovementType;
import game.views.GameView;
import game.views.IUndoable;

/**
 * The controller that handles recording of movement
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public class MovementRecorderController extends BaseController {
    
    /**
     * The model representation of a movement
     */
    private final MovementModel _movementModel = new MovementModel();
    
    /**
     * This flag indicates if the current state of the game can perform an undo
     */
    private boolean _canUndo;
    
    /**
     * This flag indicates if this instance can record undo's.
     */
    private boolean _lockRecording;
    
    /**
     * The last recorded `from` movement
     */
    private IUndoable _source;
    
    /**
     * The last recorded `from` movement
     */
    private IUndoable _destination;
    
    /**
     * Records the specified movement from one pile view implement to the other
     *
     * @param source The pile view implementation source
     * @param destination The pile view implementation destination
     * 
     */
    public void recordMovement(IUndoable source, IUndoable destination) {
        // Do not preoceed with the record movement if the lock is enabled
        if(_lockRecording) {
            return;
        }
        
        // Reset the values of this recorder
        reset();

        // Perform a backup on the source location
        _source = source;
        _source.performBackup();
        _destination = destination;
        
        MovementType fromMovement = MovementType.fromClass(source);
        MovementType toMovement = MovementType.fromClass(destination);

        //Tracelog.log(Level.INFO, true, String.format("Movement Detected: from [%s] to [%s]", fromMovement, toMovement));
            
        if(fromMovement == MovementType.NONE || toMovement == MovementType.NONE) {
            _canUndo = false;
        }
        else {
            _canUndo = true;
        }
            
        // Update the model
        _movementModel.setMovement(fromMovement, toMovement, false);
    }
        
    /**
     * Performs an undo of the last recorded move
     */
    public void undoLastMovement() {
        if(!canUndo()) {
            Tracelog.log(Level.SEVERE, true, "Cannot perform an undo");
            return;
        }

        // Prevent recording undo's, to avoid performing an undo and have that movement recorded
        _lockRecording = true;

        // Undo the last action associated to the source
        _source.undoLastAction();
        
        // Update the model to notify listeners that a movement has occurred
        _movementModel.setMovement(MovementType.fromClass(_source), MovementType.fromClass(_destination), true);

        // Repaint the source and destination
        AbstractFactory.getFactory(ViewFactory.class).get(GameView.class).repaint();
        _source.getContainerClass().repaint();
        _destination.getContainerClass().repaint();
        
        // Reset the state of this recorder
        reset();
               
        // Enable back the lock
        _lockRecording = false;
    }

    /**
     * @return TRUE if an undo operation can be made, FALSE otherwise
     */
    public boolean canUndo() {
        return _canUndo;
    }
    
    /**
     * Clears the undo availability
     */
    public void clearUndo() {
        _canUndo = false;
        reset();
    }
    
    /**
     * Resets the contents of this recorder
     */
    private void reset() {
        _canUndo = false;
        
        if(_source != null) {
            _source.clearBackup();
        }
        _source = null;
        
        if(_destination != null) {
            _destination.clearBackup();
        }
        _destination = null;
    }
    
    public void addSignalListener(ISignalListener listener) {
        _movementModel.addListener(listener);
    }
}