package game.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

import game.config.OptionsPreferences;

public final class StatusBarView extends PanelView {

    /**
     * Constructs a new instance of this class type
     */
    public StatusBarView() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(getPreferredSize().width, 24));
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        if(preferences.timedGame) {
            GameTimerView timer = AbstractFactory.getFactory(ViewFactory.class).add(new GameTimerView(), true);
            add(timer, BorderLayout.EAST);
        }
    }
}
