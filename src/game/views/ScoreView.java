/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci {@literal <thedanny09@icloud.com>}
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package game.views;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.mvc.view.PanelView;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.config.OptionsPreferences.ScoringOption;
import game.models.MovementModel;
import game.models.MovementModel.MovementType;

import resources.LocalizationStrings;

/**
 * This view shows the game score
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public class ScoreView extends PanelView {

    /**
     * The label that represents the title of this class
     */
    protected final JLabel scoreTitle = new JLabel(Localization.instance().getLocalizedString(LocalizationStrings.SCORE_TITLE));
    
    /**
     * The label that represents the visual representation of this class
     */
    protected final JLabel scoreValue = new JLabel();
    
    /**
     * The current score 
     */
    protected static long SCORE_CURRENT;
    
    /**
     * The previous score
     */
    @SuppressWarnings("unused")
    private static long SCORE_BEFORE = SCORE_CURRENT;
    
    /**
     * Constructs a new instance of this class type
     */
    public ScoreView() {
        this.setBackground(Color.WHITE);

        // Set the VGap so that the score renders appropriately
        FlowLayout layout = (FlowLayout) this.getLayout();
        layout.setVgap(0);

        add(scoreTitle);
        add(scoreValue);
    }
    
    /**
     * Adds the specified offset to the current score.
     *
     * @param offsetToScore The score to offset the current score with, a +- value of something defined on your end
     */
    protected void addToScore(long offsetToScore) {
        SCORE_CURRENT = Math.max(0, SCORE_CURRENT + offsetToScore);
        scoreValue.setText(toString());        
    }
    
    /**
     * Adds the specified offset to the current score. This method will perform a backup of what the
     * score previously was. How this method differs from the `addToScore` method is that it
     * is friendly for performing an undo.
     * 
     * The reason to not call this method over the other is in the case where the score being perform
     * could not ever be undone, such as when 10 seconds has elapsed and the score loses -2, you can never
     * get that back so dont backup the score there
     *
     * @param offsetToScore The score to offset the current score with, a +- value of something defined on your end
     */
    private void addToScoreAndBackup(long offsetToScore) {
        SCORE_BEFORE = SCORE_CURRENT;
        addToScore(offsetToScore);
    }
       
    /**
     * Updates the score based on the bonus logic
     *
     * @param seconds The number of seconds that has elapsed in the game
     * 
     * @return The bonus that will be used
     */
    public long updateScoreBonus(long seconds) {
        long bonus = 0;
        
        if(seconds > 30) {
            bonus = 700000 / seconds;
            addToScore(bonus);
        }
        
        return bonus;
    }
    
    /**
     * Updates the score based on the specified deck plays.
     *
     * @param deckPlays The number of decks played
     */
    public void updateScoreDeckFinished(int deckPlays) {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        if(preferences.drawOption == DrawOption.THREE && preferences.scoringOption == ScoringOption.STANDARD) {
            if(deckPlays > 3) {
                addToScoreAndBackup(-20);
            }
        }
        else {
            addToScoreAndBackup(-100);    
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
     * @param from where the operation started from
     * @param to where the operation ended at
     * @param isUndo If the operation being done is an undo operation
     * 
     */
     protected void updateScore(MovementType from, MovementType to, boolean isUndo) {
         long scoreFromMovement = getMovementScore(from, to, isUndo); 
         addToScoreAndBackup(scoreFromMovement + (isUndo ? - 2 : 0));
     }
     
    private long getMovementScore(MovementType from, MovementType to, boolean isUndo) {
        
        long score = 0;
        
        if(from == MovementType.TALON && to == MovementType.TABLEAU) {
            score = 5;
        }
        else if(from == MovementType.TALON && to == MovementType.FOUNDATION) {
            score = 10;
        }
        else if (from == MovementType.TABLEAU && to == MovementType.FOUNDATION) {
            score = 10;
        }
        else if(from == MovementType.FOUNDATION && to == MovementType.TABLEAU) {
            score = -15;
        }
        
        // If the score is being undone then flip the sign of the score
        if(isUndo) {
            score *= -1;
        }
        
        return score;
    }
     
    @Override public void render() {
        super.render();
        addToScore(0);
    }
    
    @Override public void destructor() {
        super.destructor();
        SCORE_CURRENT = 0;
        SCORE_BEFORE = 0;
    }
        
    @Override public String toString() {
        return String.valueOf(SCORE_CURRENT);
    }
    
    @Override public final void update(EventArgs event) {
        if(event.getSource() instanceof MovementModel) {
            MovementModel movement = (MovementModel) event.getSource();
            updateScore(movement.getFrom(), movement.getTo(), movement.getIsUndo());
        }
    }
}