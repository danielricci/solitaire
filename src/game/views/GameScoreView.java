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

import javax.swing.JLabel;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.mvc.view.PanelView;
import framework.utils.logging.Tracelog;

import game.gameplay.MovementType;

/**
 * This view shows the game score
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public final class GameScoreView extends PanelView {

    /**
     * The score label
     */
    private final JLabel _scoreLabel = new JLabel();
    
    /**
     * The internal score
     */
    private long _score = 1000;
    
    /**
     * Constructs a new instance of this class type
     */
    public GameScoreView() {
        this.setBackground(Color.WHITE);
        _scoreLabel.setText(this.toString());
        add(_scoreLabel);
    }
    
    /**
     * Updates the score based on the bonus logic
     *
     *
     * @param seconds The number of seconds that has elapsed in the game
     */
    public void updateScoreBonus(int seconds) {
        if(seconds > 30) {
            _score += (700000 / seconds);
        }
    }
    
    /**
     * Updates the score based on a timer interval tick of the game
     */
    public void updateScoreTimerTick() {
        _score = Math.max(0, _score - 2);
        render();
    }
    
    /**
     * Updates the score based on a cards' backside being revealed
     */
    public void updateScoreCardTurnOver() {
        _score += 5;
        render();
    }
    
    /**
     * Updates the score based on an operation
     *
     * @param from Where the operation started from
     * @param to Where the operation ended at
     */
    public void updateScore(MovementType from, MovementType to) {
        long scoreBefore = _score;
        if(from == MovementType.TALON && to == MovementType.TABLEAU) {
            _score += 5;
        }
        else if(from == MovementType.TALON && to == MovementType.FOUNDATION) {
            _score += 10;
        }
        else if (from == MovementType.TABLEAU && to == MovementType.FOUNDATION) {
            _score += 10;
        }
        else if(from == MovementType.FOUNDATION && to == MovementType.TABLEAU) {
            _score -= 15;
        }
        else {
            return;
        }
        
        long scoreAfter = _score;
        Tracelog.log(Level.INFO, true, String.format("Score Changed from %d to %d after performing move [%s, %s]", scoreBefore, scoreAfter, from, to));
        
        render();
    }
    
    @Override public void render() {
        super.render();
        update(EventArgs.Empty());
    }
    
    @Override public void update(EventArgs event) {
        super.update(event);
        _scoreLabel.setText(toString());
    }
    
    @Override public String toString() {
        return "Score: " + String.valueOf(_score);
    }
}
