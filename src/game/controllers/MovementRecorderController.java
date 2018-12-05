/**
* Daniel Ricci <thedanny09@icloud.com>
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge,
* publish, distribute, sublicense, and/or sell copies of the Software,
* and to permit persons to whom the Software is furnished to do so, subject
* to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
* THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
* IN THE SOFTWARE.
*/

package game.controllers;

import java.util.logging.Level;

import framework.communication.internal.signal.ISignalListener;
import framework.core.mvc.controller.BaseController;
import framework.utils.logging.Tracelog;

import game.gameplay.MovementType;
import game.models.MovementModel;

/**
 * The controller that handles recording of movement
 * @author Daniel Ricci <thedanny09@icloud.com>
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
     * The last recorded `from` movement
     */
    private MovementType _from;
    
    /**
     * The last recorded `to` movement
     */
    private MovementType _to;
    
    /**
     * Records the specified movement
     *
     * @param from The `from` movement that was performed
     * @param to The `to` movement that was performed
     */
    public void recordMovement(MovementType from, MovementType to) {
        
        Tracelog.log(Level.INFO, true, String.format("Movement Detected: from [%s] to [%s]", from, to));
        
        // Assign the new values
        _from = from;
        _to = to;
    
        if(from == MovementType.NONE || to == MovementType.NONE) {
            _canUndo = false;
        }
        else {
            _canUndo = true;
        }
            
        // Update the model
        _movementModel.setMovement(from, to, false);
    }
    
    /**
     * Performs an undo of the last recorded move
     */
    public void undoLastMovement() {
        if(!canUndo()) {
            Tracelog.log(Level.SEVERE, true, "Cannot perform an undo");
            return;
        }

        // Update the movement model
        _movementModel.setMovement(_from, _to, true);
        
        // Reset the values
        _canUndo = false;
        _from = _to = null;
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
    }
    
    @Override public void addSignalListener(ISignalListener listener) {
        super.addSignalListener(listener);
        _movementModel.addListener(listener);
    }
}