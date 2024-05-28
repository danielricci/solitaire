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
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 */
public class VegasScoreView extends ScoreView {

    /**
     * Constructs a new instance of this class type
     */
    public VegasScoreView() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(preferences.cumulativeScore) {
            SCORE_CURRENT += -52;            
        }
        else {
            SCORE_CURRENT = -52;
        }
    }
    
    @Override protected void addToScore(long score) {
        SCORE_CURRENT += score;
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
        long scoreBefore = SCORE_CURRENT;
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
                
        Tracelog.log(Level.INFO, true, String.format("Score %s: Changed from %d to %d after performing move [%s] to [%s]", isUndo ? "Undo" : "Updated",scoreBefore, SCORE_CURRENT, from, to));
    }
    

    @Override public void destructor() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        if(!preferences.cumulativeScore || Application.instance.isRestarting) {
            super.destructor();
        }
    }
    
    @Override public String toString() {
        
        if(SCORE_CURRENT < 0) {
            scoreValue.setForeground(Color.RED);
        }
        else {
            scoreValue.setForeground(Color.BLACK);
        }
        
        String result = "$";
        if(SCORE_CURRENT < 0) {
            result = "-" + result;
        }
        
        return result + String.valueOf(Math.abs(SCORE_CURRENT));
    }
}
