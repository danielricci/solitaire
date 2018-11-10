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

package game.config;

import java.util.logging.Level;

import framework.core.system.GamePreferences;
import framework.utils.logging.Tracelog;

import game.views.OptionsDialogView;

import generated.DataLookup;

public final class OptionsPreferences extends GamePreferences {

    public enum DrawOption { ONE, THREE };
    
    public enum ScoringOption { STANDARD, VEGAS, NONE };
    
    public boolean timedGame;
    
    public boolean statusBar;
    
    public boolean outlineDragging;
    
    public boolean cumulativeScore;
    
    public DrawOption drawOption;
    
    public ScoringOption scoringOption;
    
    public DataLookup.BACKSIDES deck;
    
    public OptionsPreferences() {
        super(OptionsDialogView.class);
    }

    @Override public void load() {
        drawOption = DrawOption.values()[preferences.getInt("drawOption", DrawOption.ONE.ordinal())];
        scoringOption = ScoringOption.values()[preferences.getInt("scoringOption", ScoringOption.STANDARD.ordinal())];
        timedGame = preferences.getBoolean("timedGame", false);
        statusBar = preferences.getBoolean("statusBar", false);
        outlineDragging = preferences.getBoolean("outlineDragging", false);
        cumulativeScore = preferences.getBoolean("cumulativeScore", false);
        deck = DataLookup.BACKSIDES.values()[preferences.getInt("deck", DataLookup.BACKSIDES.DECK_1.ordinal())];
    }

    @Override public void save() {
        try {
            preferences.putInt("drawOption", drawOption.ordinal());
            preferences.putInt("scoringOption", scoringOption.ordinal());
            preferences.putBoolean("timedGame", timedGame);
            preferences.putBoolean("statusBar", statusBar);
            preferences.putBoolean("outlineDragging", outlineDragging);
            preferences.putBoolean("cumulativeScore", cumulativeScore);
            preferences.putInt("deck", deck.ordinal());
            preferences.flush();
        } 
        catch (Exception exception) {
            Tracelog.log(Level.SEVERE, true, exception);
        }
    }
    
    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        String header = "=========" + this.getClass().getSimpleName() + "=========";
        builder.append(header + System.getProperty("line.separator"));
        builder.append("Draw Option: " + drawOption + System.getProperty("line.separator"));
        builder.append("Scoring Option: " + scoringOption + System.getProperty("line.separator"));
        builder.append("Timed Game: " + Boolean.toString(timedGame) + System.getProperty("line.separator"));
        builder.append("Status Bar: " + Boolean.toString(statusBar) + System.getProperty("line.separator"));
        builder.append("Outline Dragging: " + Boolean.toString(outlineDragging) + System.getProperty("line.separator"));
        builder.append("Cumulative Score: " + Boolean.toString(cumulativeScore) + System.getProperty("line.separator"));
        builder.append("Deck: " + deck.toString() + System.getProperty("line.separator"));
        builder.append(new String(new char[header.length()]).replace("\0", "="));
        return builder.toString();
    }
}