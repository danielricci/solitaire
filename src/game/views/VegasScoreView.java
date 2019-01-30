/**
 * MIT License
 * 
 * Copyright (c) 2019 Daniel Ricci <thedanny09@icloud.com>
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
import java.util.logging.Level;

import framework.core.system.Application;
import framework.utils.logging.Tracelog;

import game.config.OptionsPreferences;
import game.models.MovementModel.MovementType;

/**
 * This view shows the game score when playing in a vegas styled environment
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class VegasScoreView extends ScoreView {

    /**
     * Constructs a new instance of this class type
     */
    public VegasScoreView() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(preferences.cumulativeScore) {
            SCORE += -52;            
        }
        else {
            SCORE = -52;
        }
    }
    
    @Override protected void addToScore(long score) {
        SCORE += score;
        scoreValue.setText(toString());
    }
    
    @Override public long updateScoreBonus(long seconds) {
        return 0;
    }
    
    @Override public void updateScoreDeckFinished(int deckPlays) {
    }
    
    @Override public void updateScoreTimerTick() {
    }
    
    @Override public void updateScoreCardTurnOver() {
    }
    
    @Override protected void updateScore(MovementType from, MovementType to, boolean isUndo) {
        long scoreBefore = SCORE;
        if(from == MovementType.TALON && to == MovementType.TABLEAU) {
            addToScore(isUndo ? -5 : 5);
        }
        else if(from == MovementType.TALON && to == MovementType.FOUNDATION) {
            addToScore(isUndo ? -5 : 5);
        }
        else if (from == MovementType.TABLEAU && to == MovementType.FOUNDATION) {
            addToScore(isUndo ? -5 : 5);
        }
        else if(from == MovementType.FOUNDATION && to == MovementType.TABLEAU) {
            addToScore(isUndo ? 5 : -5);
        }
        else {
            return;
        }
                
        Tracelog.log(Level.INFO, true, String.format("Score %s: Changed from %d to %d after performing move [%s] to [%s]", isUndo ? "Undo" : "Updated",scoreBefore, SCORE, from, to));
    }
    

    @Override public void destructor() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(!preferences.cumulativeScore || Application.instance.isRestarting) {
            super.destructor();
        }
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
