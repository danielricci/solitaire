package game.views;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import framework.core.mvc.view.DialogView;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.config.OptionsPreferences.DrawOption;
import game.config.OptionsPreferences.ScoringOption;

/**
 * The options view shows the settings that change the way the game is played
 *
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public final class OptionsDialogView extends DialogView {

    /**
     * Indicates if the options that were changed (if any) require the game to reset to take effect
     */
    public boolean refreshGameRequired;
    
    /**
     * Indicates if the status bar option has changed since it's last saved value
     */
    public boolean statusBarChanged;
    
    /**
     * Indicates if the outline dragging option has changed since it's last saved value
     */
    public boolean outlineDraggingChanged;
    
    /**
     * Indicates if the cumulative score option has changed since it's last saved value
     */
    public boolean cumulativeScoreChanged;
    
    // The OK button
    private JButton okButton = new JButton("OK");
    
    /**
     * Constructs a new instance of this class type
     */
    public OptionsDialogView() {
        super(Application.instance, Localization.instance().getLocalizedString("Options"));
        
        // Properties of this view
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setAutomaticDialogCentering(true);
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
        
        // The main panel that houses this view
        JPanel mainPanel = new JPanel();
        
        // Use a grid to lay out the contents
        mainPanel.setLayout(new GridLayout(2, 2));
        
        // Load the option for this view
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        // Draw radio buttons
        GridLayout drawPanelGridLayout = new GridLayout(2, 1);
        drawPanelGridLayout.setVgap(-10);
        JPanel drawPanel = new JPanel();
        drawPanel.setLayout(drawPanelGridLayout);
        drawPanel.setBorder(BorderFactory.createTitledBorder("Draw"));
        JRadioButton drawOneRadioButton = new JRadioButton("Draw One", preferences.drawOption == DrawOption.ONE);
        drawOneRadioButton.putClientProperty(drawOneRadioButton, DrawOption.ONE);
        JRadioButton drawThreeRadioButton = new JRadioButton("Draw Three", preferences.drawOption == DrawOption.THREE);
        drawThreeRadioButton.putClientProperty(drawThreeRadioButton, DrawOption.THREE);
        
        // Button group for the draw radio buttons
        ButtonGroup drawPanelGroup = new ButtonGroup();
        drawPanelGroup.add(drawOneRadioButton);
        drawPanelGroup.add(drawThreeRadioButton);
        drawPanel.add(drawOneRadioButton);
        drawPanel.add(drawThreeRadioButton);

        // Scoring Panel UI
        GridLayout scoringPanelGridLayout = new GridLayout(3, 1);
        scoringPanelGridLayout.setVgap(-5);
        JPanel scoringPanel = new JPanel();
        scoringPanel.setLayout(scoringPanelGridLayout);
        scoringPanel.setBorder(BorderFactory.createTitledBorder("Scoring"));
        
        // Scoring radio buttons
        JRadioButton standardRadioButton = new JRadioButton("Standard", preferences.scoringOption == ScoringOption.STANDARD);
        standardRadioButton.putClientProperty(standardRadioButton, ScoringOption.STANDARD);
        JRadioButton vegasRadioButton = new JRadioButton("Vegas", preferences.scoringOption == ScoringOption.VEGAS);
        vegasRadioButton.putClientProperty(vegasRadioButton, ScoringOption.VEGAS);
        JRadioButton noneRadioButton = new JRadioButton("None", preferences.scoringOption == ScoringOption.NONE);
        noneRadioButton.putClientProperty(noneRadioButton, ScoringOption.NONE);
        
        // Button group for the scoring radio buttons
        ButtonGroup scoringPanelGroup = new ButtonGroup();
        scoringPanelGroup.add(standardRadioButton);
        scoringPanelGroup.add(vegasRadioButton);
        scoringPanelGroup.add(noneRadioButton);
        scoringPanel.add(standardRadioButton);
        scoringPanel.add(vegasRadioButton);
        scoringPanel.add(noneRadioButton);

        // Left side options
        GridLayout leftSideGridLayout = new GridLayout(3, 1);
        JPanel barOptionsPanelLeft = new JPanel();
        barOptionsPanelLeft.setLayout(leftSideGridLayout);

        // Mutually exclusive checkbox settings
        JCheckBox timedGameCheckBox = new JCheckBox("Timed Game", preferences.timedGame); 
        barOptionsPanelLeft.add(timedGameCheckBox);
        JCheckBox statusBarCheckBox = new JCheckBox("Status Bar", preferences.statusBar);
        barOptionsPanelLeft.add(statusBarCheckBox);
        JCheckBox outlineDraggingCheckbox = new JCheckBox("Outline Dragging", preferences.outlineDragging);
        barOptionsPanelLeft.add(outlineDraggingCheckbox);
        
        // Right side options
        JPanel barOptionsPanelRight = new JPanel();
        barOptionsPanelRight.setLayout(new BoxLayout(barOptionsPanelRight, BoxLayout.Y_AXIS));
        JCheckBox cumulativeScoreCheckBox = new JCheckBox("Cumulative Score", preferences.cumulativeScore);
        cumulativeScoreCheckBox.setEnabled(vegasRadioButton.isSelected());
        
        // Add an item listener to the vegas radio button to alter the state of the cumulative score
        vegasRadioButton.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent event) {
                cumulativeScoreCheckBox.setEnabled(event.getStateChange() == ItemEvent.SELECTED);
            }
        });

        barOptionsPanelRight.add(cumulativeScoreCheckBox);

        okButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                
                // Draw panel result
                JRadioButton drawPanelSelection = drawOneRadioButton.isSelected() ? drawOneRadioButton : drawThreeRadioButton;
                DrawOption drawOptionNew = (DrawOption)drawPanelSelection.getClientProperty(drawPanelSelection);
                refreshGameRequired = preferences.drawOption != drawOptionNew;
                preferences.drawOption = drawOptionNew;
                
                // Scoring result
                JRadioButton scoringPanelSelection = null;
                if(standardRadioButton.isSelected()) {
                    scoringPanelSelection = standardRadioButton;
                }
                else if(vegasRadioButton.isSelected()) {
                    scoringPanelSelection = vegasRadioButton;
                }
                else {
                    scoringPanelSelection = noneRadioButton;
                }
                ScoringOption scoringOption = (ScoringOption)scoringPanelSelection.getClientProperty(scoringPanelSelection);
                refreshGameRequired |= scoringOption != preferences.scoringOption;
                preferences.scoringOption = scoringOption;
                
                // Timed game
                boolean timedGame = timedGameCheckBox.isSelected();
                refreshGameRequired |= timedGame != preferences.timedGame;
                preferences.timedGame = timedGame;
                
                // Status bar
                boolean statusBar = statusBarCheckBox.isSelected();
                statusBarChanged = preferences.statusBar != statusBar;
                preferences.statusBar = statusBar;
                
                // Outline dragging
                boolean outlineDragging = outlineDraggingCheckbox.isSelected();
                outlineDraggingChanged = preferences.outlineDragging != outlineDragging;
                preferences.outlineDragging = outlineDragging;
                
                // Cumulative Score
                boolean cumulativeScore = cumulativeScoreCheckBox.isSelected();
                cumulativeScoreChanged = preferences.cumulativeScore != cumulativeScore;
                preferences.cumulativeScore = cumulativeScore;
                
                // Save the contents of the preferences and then close this dialog
                preferences.save();
                setDialogResult(JOptionPane.OK_OPTION);
                setVisible(false);
            }
        });
        
        // The Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                setDialogResult(JOptionPane.CANCEL_OPTION);
                setVisible(false);
            }
        });
        
        // Add the OK button and the Cancel button
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.add(okButton);
        actionsPanel.add(cancelButton);

        // Add the sub-panels to the main panel
        mainPanel.add(drawPanel);
        mainPanel.add(scoringPanel);
        mainPanel.add(barOptionsPanelLeft);
        mainPanel.add(barOptionsPanelRight);
        
        // Add the main panel and the action panel to the main view;
        add(mainPanel);
        add(actionsPanel);
    }
    
    @Override protected void enterActionPerformed(ActionEvent event) {
        super.enterActionPerformed(event);
        this.okButton.doClick();
    }
}