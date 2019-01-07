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
import framework.core.system.Application;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.config.OptionsPreferences.ScoringOption;
import game.models.MovementModel;
import game.models.MovementModel.MovementType;

/**
 * This view shows the game score
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class ScoreView extends PanelView {

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
    protected static long SCORE = 1000;
    
    /**
     * Constructs a new instance of this class type
     */
    public ScoreView() {
        this.setBackground(Color.WHITE);
        add(scoreTitle);
        add(scoreValue);  
    }

    protected ScoreView(int initialScore) {
        this();
        SCORE = initialScore;
    }
    
    protected void addToScore(int score) {
        SCORE = Math.max(0, SCORE + score);
        scoreValue.setText(toString());
    }
    
    /**
     * Updates the score based on the bonus logic
     *
     *
     * @param seconds The number of seconds that has elapsed in the game
     */
    public void updateScoreBonus(int seconds) {
        if(seconds > 30) {
            addToScore(700000 / seconds);
        }
    }
    
    public void updateScoreDeckFinished(int deckPlays) {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        if(preferences.drawOption == DrawOption.THREE && preferences.scoringOption == ScoringOption.STANDARD) {
            if(deckPlays > 3) {
                addToScore(-20);
            }
        }
        else {
            addToScore(-100);    
        }
    }
    
    /**
     * Updates the score based on a timer interval tick of the game
     */
    public void updateScoreTimerTick() {
        addToScore(-2);
    }
    
    /**
     * Updates the score based on a cards' backside being revealed
     */
    public void updateScoreCardTurnOver() {
        addToScore(5);
    }
    
    /**
     * Updates the score based on an operation
     *
     * @param from Where the operation started from
     * @param to Where the operation ended at
     */
     protected void updateScore(MovementType from, MovementType to, boolean isUndo) {

        long scoreBefore = SCORE;
        if(from == MovementType.TALON && to == MovementType.TABLEAU) {
            addToScore(isUndo ? -5 : 5);
        }
        else if(from == MovementType.TALON && to == MovementType.FOUNDATION) {
            addToScore(isUndo ? -10 : 10);
        }
        else if (from == MovementType.TABLEAU && to == MovementType.FOUNDATION) {
            addToScore(isUndo ? -10 : 10);
        }
        else if(from == MovementType.FOUNDATION && to == MovementType.TABLEAU) {
            addToScore(isUndo ? 15 : -15);
        }
        else if(!isUndo) {
            // Go no further if none of the scoring conditions were done, and there wasn't an undo being performed.
            return;
        }
        
        // Whenever there is an undo, subtract 2 from the score
        if(isUndo) {
            addToScore(-2);
        }

        Tracelog.log(Level.INFO, true, String.format("Score %s: Changed from %d to %d after performing move [%s] to [%s]", isUndo ? "Undo" : "Updated", scoreBefore, SCORE, from, to));
    }
    
    @Override public void render() {
        super.render();
        addToScore(0);
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
    
    @Override public void update(EventArgs event) {
        if(event.getSource() instanceof MovementModel) {
            MovementModel movement = (MovementModel) event.getSource();
            updateScore(movement.getFrom(), movement.getTo(), movement.getIsUndo());
        }
    }
}