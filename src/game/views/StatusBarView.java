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
        
        // Menu Descrition
        _menuDescription.setBorder(new EmptyBorder(0, 5, 0, 0));
        add(_menuDescription, BorderLayout.WEST);
        
        // Game Score + Game Timer
        JPanel rightSidePanel = new JPanel(new BorderLayout());
        
        // The scoring option should only be shown in Standard and Vegas scoring modes
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        // Create the score view based on the currently set scoring standard
        _scoreView = AbstractFactory.getFactory(ViewFactory.class).add(preferences.scoringOption == ScoringOption.VEGAS ? new VegasScoreView() : new ScoreView(), true);
        _scoreView.render();
        
        // register the score view to recieve events from the movement controller
        AbstractFactory.getFactory(ControllerFactory.class).get(MovementRecorderController.class).addSignalListener(_scoreView);
        
        if(preferences.scoringOption != ScoringOption.NONE) {
            rightSidePanel.add(_scoreView,BorderLayout.WEST);    
        }
        
        rightSidePanel.add(_gameTimerView,BorderLayout.EAST);
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