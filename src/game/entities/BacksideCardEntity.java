package game.entities;

import framework.core.entity.DataEntity;

import game.config.OptionsPreferences;

import generated.DataLookup;

public class BacksideCardEntity extends DataEntity {
	
	public static final String DECK_BACKSIDE_CHANGED = "DECK_BACKSIDE_CHANGED";

	private boolean isBacksideVisible = false;

	private DataLookup.BACKSIDES _backside;

	public BacksideCardEntity() {		
		this.setBackside();
	}
	
	public void setIsBacksideShowing(boolean isShowing) {
		isBacksideVisible = isShowing;
	}

	public boolean getIsBacksideShowing() {
		return isBacksideVisible;
	}

	public DataLookup.BACKSIDES getBacksideData() {
		return _backside;
	}

	private void setBackside() {
		OptionsPreferences preferences = new OptionsPreferences();
		preferences.load();
		
		if (_backside == preferences.deck) {
			return;
		}
		_backside = preferences.deck;
		super.setActiveData(preferences.deck.identifier);
	}
		
	@Override public void refresh() {
		setBackside();
	}
}