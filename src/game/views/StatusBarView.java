package game.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ViewFactory;
import framework.core.mvc.view.PanelView;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.ScoringOption;
import game.controllers.MovementRecorderController;

public final class StatusBarView extends PanelView {

    /**
     * The game timer view
     */
    private final TimerView _gameTimerView = AbstractFactory.getFactory(ViewFactory.class).add(new TimerView(), true);
    
    /**
     * The game score view
     */
    private ScoreView _scoreView = null;
    
    /**
     * The menu description label
     */
    private final JLabel _menuDescription = new JLabel();
    
    /**
     * Constructs a new instance of this class type
     */
    public StatusBarView() {
        this.setBackground(Color.WHITE);

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(getPreferredSize().width, 17));
        this.setFocusable(false);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        // Add a dummy mouse listener to avoid the click from hitting the green background
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
            }
        });
        
        // Menu Descrition
        _menuDescription.setBorder(new EmptyBorder(0, 5, 0, 0));
        add(_menuDescription, BorderLayout.WEST);
        
        // Game Score + Game Timer
        JPanel rightSidePanel = new JPanel(new BorderLayout());
        rightSidePanel.setBorder(BorderFactory.createEmptyBorder());

        
        // The scoring option should only be shown in Standard and Vegas scoring modes
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        // Create the score view based on the currently set scoring standard      
        _scoreView = AbstractFactory.getFactory(ViewFactory.class).add(preferences.scoringOption == ScoringOption.VEGAS ? new VegasScoreView() : new ScoreView(), true);
        _scoreView.setBorder(null);
        _scoreView.render();
        
        // register the score view to recieve events from the movement controller
        AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).addSignalListener(_scoreView);
        
        if(preferences.scoringOption != ScoringOption.NONE) {
            rightSidePanel.add(_scoreView,BorderLayout.WEST);    
        }
        
        rightSidePanel.add(_gameTimerView, BorderLayout.EAST);
        add(rightSidePanel, BorderLayout.EAST); 
        
        // Synchronize w.r.t the currently set options
        synchronizeWithOptions();
    }
    
    /**
     * Sets the menu description

     * @param text The description to set
     */
    public void setMenuDescription(String text) {
        _menuDescription.setText(text);
    }
    
    /**
     * Convenience method to clear the text
     */
    public void clearMenuDescription() {
        setMenuDescription("");
    }
    
    /**
     * Synchronizes the options results w.r.t the status bar and it's related content
     */
    public void synchronizeWithOptions() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        this.setVisible(preferences.statusBar);
        _gameTimerView.setVisible(preferences.timedGame);
        _scoreView.setVisible(preferences.scoringOption != ScoringOption.NONE);
    }
    
    @Override public void render() {
        synchronizeWithOptions();
    }
}