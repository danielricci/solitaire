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

import framework.core.mvc.view.PanelView;
import framework.core.system.Application;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.ScoringOption;
import game.gameplay.MovementType;

/**
 * This view shows the game score
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class GameScoreView extends PanelView {

    /**
     * The score title
     */
    protected final JLabel scoreTitle = new JLabel("Score:");
    
    /**
     * The value of the score
     */
    protected final JLabel scoreValue = new JLabel();
    
    /**
     * The score. 
     * 
     * Note: The score is static because in some cases the score persists to the next game, but not if the application exist
     */
    protected static long SCORE = 0;
    
    /**
     * Constructs a new instance of this class type
     */
    public GameScoreView() {
        this.setBackground(Color.WHITE);
        add(scoreTitle);
        add(scoreValue);
    }
    
    /**
     * Updates the score based on the bonus logic
     *
     *
     * @param seconds The number of seconds that has elapsed in the game
     */
    public void updateScoreBonus(int seconds) {
        if(seconds > 30) {
            SCORE += (700000 / seconds);
            scoreValue.setText(toString());
        }
    }
    
    public void updateScoreDeckFinished() {
        SCORE = Math.max(0, SCORE - 100);
        scoreValue.setText(toString());
    }
    
    /**
     * Updates the score based on a timer interval tick of the game
     */
    public void updateScoreTimerTick() {
        SCORE = Math.max(0, SCORE - 2);
        scoreValue.setText(toString());
    }
    
    /**
     * Updates the score based on a cards' backside being revealed
     */
    public void updateScoreCardTurnOver() {
        SCORE += 5;
        scoreValue.setText(toString());
    }
    
    /**
     * Updates the score based on an operation
     *
     * @param from Where the operation started from
     * @param to Where the operation ended at
     */
    public void updateScore(MovementType from, MovementType to) {
        
        long scoreBefore = SCORE;
        if(from == MovementType.TALON && to == MovementType.TABLEAU) {
            SCORE += 5;
        }
        else if(from == MovementType.TALON && to == MovementType.FOUNDATION) {
            SCORE += 10;
        }
        else if (from == MovementType.TABLEAU && to == MovementType.FOUNDATION) {
            SCORE += 10;
        }
        else if(from == MovementType.FOUNDATION && to == MovementType.TABLEAU) {
            SCORE = Math.max(0, SCORE - 15);
        }
        else {
            return;
        }
        
        long scoreAfter = SCORE;
        Tracelog.log(Level.INFO, true, String.format("Score Changed from %d to %d after performing move [%s, %s]", scoreBefore, scoreAfter, from, to));
        
        scoreValue.setText(toString());
    }
    
    @Override public void render() {
        super.render();
        scoreValue.setText(this.toString());
    }
    
    @Override public void destructor() {
        super.destructor();
        
        // Get the current option preferences of the game and reset the score if certain conditions are met
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(Application.instance.isRestarting || !(preferences.scoringOption == ScoringOption.VEGAS && preferences.cumulativeScore)) {
            SCORE = 0;
        }
    }
        
    @Override public String toString() {
        return String.valueOf(SCORE);
    }
}