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

import game.models.MovementModel;
import game.models.MovementModel.MovementType;
import game.views.AbstractPileView;
import game.views.CardView;

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
     * This flag indicates if this instance can record undo's.
     */
    private boolean _lockRecording;
    
    /**
     * The last recorded `from` movement
     */
    private AbstractPileView _from;
    
    /**
     * The last recorded `to` movement
     */
    public AbstractPileView _to;
        
    /**
     * Records the specified movement from one pile view implement to the other
     *
     * @param from The pile view implementation source
     * @param to The pile view implementation destination
     * 
     */
    public void recordMovement(AbstractPileView from, AbstractPileView to, CardView cardView) {
        
        // Do not preoceed with the record movement if the lock is enabled
        if(_lockRecording) {
            return;
        }
        
        _from = from;
        _to = to;
        
        MovementType fromMovement = MovementType.fromClass(from);
        MovementType toMovement = MovementType.fromClass(to);

        Tracelog.log(Level.INFO, true, String.format("Movement Detected: from [%s] to [%s]", fromMovement, toMovement));
            
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
        
        // Undo the move that was performed 
        //_from.addCard(resource);
        
        // Update the movement model
        _movementModel.setMovement(MovementType.fromClass(_from), MovementType.fromClass(_to), true);
        
        // Enable back the lock
        _lockRecording = false;
        
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