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

package game.entities;

import framework.core.entity.AbstractDataEntity;

import game.config.OptionsPreferences;

import generated.DataLookup;


public class BacksideCardEntity extends AbstractDataEntity {

    public static final String DECK_BACKSIDE_UPDATED = "DECK_BACKSIDE_UPDATED";
    
    private boolean _isBacksideVisible = false;

    private DataLookup.BACKSIDES _backside;
    
    public BacksideCardEntity() {
        setBackside();
    }
    
    public void setIsBacksideShowing(boolean isShowing) {
        _isBacksideVisible = isShowing;
    }
    
    public boolean getIsBacksideShowing() {
        return _isBacksideVisible;
    }
    
    public DataLookup.BACKSIDES getBacksideData() {
        return _backside;
    }
    
    private void setBackside() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        _backside = preferences.deck;
        super.setActiveData(preferences.deck.identifier);
    }

    @Override public void refresh() {
        setBackside();
    }
}