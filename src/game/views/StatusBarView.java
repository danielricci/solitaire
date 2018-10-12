package game.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

import game.config.OptionsPreferences;

public final class StatusBarView extends PanelView {

    /**
     * The game timer view associated to this status bar
     */
    private GameTimerView _gameTimerView = AbstractFactory.getFactory(ViewFactory.class).add(new GameTimerView(), true);
    
    /**
     * Constructs a new instance of this class type
     */
    public StatusBarView() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(getPreferredSize().width, 24));
        this.setFocusable(false);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        
        // Add a dummy mouse listener to avoid the click from hitting the green background
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
            }
        });
    
        AbstractFactory.getFactory(ViewFactory.class).get(GameView.class).addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                OptionsPreferences preferences = new OptionsPreferences();
                preferences.load();
                if(preferences.timedGame) {
                    _gameTimerView.startGameTimer();
                }
            }
        });
        
        synchronizeWithOptions();        
        add(_gameTimerView, BorderLayout.EAST);
    }
    
    /**
     * Synchronizes the options results w.r.t the status bar and it's related content
     */
    public void synchronizeWithOptions() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        this.setVisible(preferences.statusBar);
        _gameTimerView.setVisible(preferences.timedGame);
    }
    
    @Override public void render() {
        synchronizeWithOptions();
    }
}