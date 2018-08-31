/**
* Daniel Ricci <thedanny09@gmail.com>
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

public final class OptionsPreferences extends GamePreferences {

    public enum DrawOption { DRAW_ONE, DRAW_THREE };
    
    public enum ScoringOption { STANDARD, VEGAS, NONE };
    
    private DrawOption _drawOption;
    
    private ScoringOption _scoringOption;
    
    public OptionsPreferences() {
        super(OptionsDialogView.class);
    }

    @Override public void load() {
        _drawOption = DrawOption.values()[preferences.getInt("drawOption", DrawOption.DRAW_ONE.ordinal())];
        _scoringOption = ScoringOption.values()[preferences.getInt("scoringOption", ScoringOption.STANDARD.ordinal())];
    }

    @Override public void save() {
        try {
            preferences.putInt("drawOption", _drawOption.ordinal());
            preferences.putInt("scoringOption", _scoringOption.ordinal());
            preferences.flush();
        } 
        catch (Exception exception) {
            Tracelog.log(Level.SEVERE, true, exception);
        }
    }

    public void setDrawOption(DrawOption drawOption) {
        _drawOption = drawOption;
    }
    
    public DrawOption getDrawOption() {
        return _drawOption;
    }
    
    public void setScoringOption(ScoringOption scoringOption) {
        _scoringOption = scoringOption;
    }
    
    public ScoringOption getScoringOption() {
        return _scoringOption;
    }
}