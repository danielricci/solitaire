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

package game.views;

import java.awt.Color;
import java.util.logging.Level;

import framework.utils.logging.Tracelog;

import game.gameplay.MovementType;

/**
 * This view shows the game score when playing in a vegas styled environment
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class VegasGameScoreView extends GameScoreView {

    /**
     * Constructs a new instance of this class type
     */
    public VegasGameScoreView() {
        SCORE -= 52;
    }
    
    @Override public void updateScoreBonus(int seconds) {
        // Do nothing
    }
    
    @Override public void updateScoreDeckFinished() {
    }
    
    @Override public void updateScoreTimerTick() {
    }
    
    @Override public void updateScoreCardTurnOver() {
    }
    
    @Override protected void updateScore(MovementType from, MovementType to, boolean isUndo) {
        long scoreBefore = SCORE;
        if(from == MovementType.TALON && to == MovementType.TABLEAU) {
            SCORE += (isUndo ? -5 : 5);
        }
        else if(from == MovementType.TALON && to == MovementType.FOUNDATION) {
            SCORE += (isUndo ? -5 : 5);
        }
        else if (from == MovementType.TABLEAU && to == MovementType.FOUNDATION) {
            SCORE += (isUndo ? -5 : 5);
        }
        else if(from == MovementType.FOUNDATION && to == MovementType.TABLEAU) {
            SCORE += (isUndo ? 5 : -5);
        }
        else {
            return;
        }
        
        // Whenever there is an undo, subtract 2 from the score
        if(isUndo) {
            SCORE -= 2;
        }
        
        long scoreAfter = SCORE;
        Tracelog.log(Level.INFO, true, String.format("Score %s: Changed from %d to %d after performing move [%s] to [%s]", isUndo ? "Undo" : "Updated",scoreBefore, scoreAfter, from, to));
        scoreValue.setText(toString());
    }
    
    @Override public String toString() {
        
        if(SCORE < 0) {
            scoreValue.setForeground(Color.RED);
        }
        else {
            scoreValue.setForeground(Color.BLACK);
        }
        
        String result = "$";
        if(SCORE < 0) {
            result = "-" + result;
        }
        
        return result + String.valueOf(Math.abs(SCORE));
    }
}
