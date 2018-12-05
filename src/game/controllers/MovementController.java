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

public class MovementController extends BaseController {
    
    private final MovementModel _movementModel = new MovementModel();
    
    private boolean _canUndo;
    
    private MovementType _fromOld;
    private MovementType _fromNew;
    
    private MovementType _toOld;
    private MovementType _toNew;
    
    public void recordMovement(MovementType from, MovementType to) {
        
        Tracelog.log(Level.INFO, true, String.format("Movement Detected: from [%s] to [%s]", from, to));
        
        // Copy the values over that were last registered
        _fromOld = _fromNew;
        _toOld = _toNew;
        
        // Assign the new values
        _fromNew = from;
        _toNew = to;
    
        _canUndo = true;
        
        // Update the model
        _movementModel.setMovement(from, to, false);
    }
    
    public void undoLastMovement() {
        if(!canUndo()) {
            Tracelog.log(Level.SEVERE, true, "Cannot perform an undo");
            return;
        }

        _canUndo = false;
        
        // Update the new values to be the old values
        _fromNew = _fromOld;
        _toNew = _toOld;
        _toOld = null;
        _fromOld = null;
   
        // Perform the movement
        _movementModel.setMovement(_fromNew, _toNew, true);
    }
    
    public boolean canUndo() {
        return _canUndo;
    }
    
    @Override public void addSignalListener(ISignalListener listener) {
        super.addSignalListener(listener);
        _movementModel.addListener(listener);
    }
}