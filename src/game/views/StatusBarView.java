package game.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;
import framework.core.system.Application;

import game.config.OptionsPreferences;

public final class StatusBarView extends PanelView {

    /**
     * Constructs a new instance of this class type
     */
    public StatusBarView() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(getPreferredSize().width, 24));
        this.setFocusable(false);
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        if(preferences.timedGame) {
            
            // Add a dummy mouse listener to avoid the click from hitting the green background
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent event) {
                }
            });
            
            GameTimerView timer = AbstractFactory.getFactory(ViewFactory.class).add(new GameTimerView(), true);
            Application.instance.getContentPane().addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent event) {
                    timer.startGameTimer();
                }
            });
            add(timer, BorderLayout.EAST);
        }
    }
}