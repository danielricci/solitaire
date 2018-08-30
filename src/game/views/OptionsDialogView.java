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

package game.views;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import game.config.OptionsPreferences.DrawOptions;

public final class OptionsDialogView extends DialogView {

    public OptionsDialogView() {
        super(Application.instance(), Localization.instance().getLocalizedString("Options"));
        
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setAutomaticDialogCentering(true);
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2));
        
        // Load the option for this view
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        
        // Draw Panel UI
        GridLayout drawPanelGridLayout = new GridLayout(2, 1);
        drawPanelGridLayout.setVgap(-10);
        JPanel drawPanel = new JPanel();
        drawPanel.setLayout(drawPanelGridLayout);
        drawPanel.setBorder(BorderFactory.createTitledBorder("Draw"));
        JRadioButton drawOneRadioButton = new JRadioButton("Draw One", preferences.getDrawOption() == DrawOptions.DRAW_ONE);
        drawOneRadioButton.putClientProperty(drawOneRadioButton, DrawOptions.DRAW_ONE);
        JRadioButton drawThreeRadioButton = new JRadioButton("Draw Three", !drawOneRadioButton.isSelected());
        drawThreeRadioButton.putClientProperty(drawThreeRadioButton, DrawOptions.DRAW_THREE);
        
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
        JRadioButton standardRadioButton = new JRadioButton("Standard", true);
        JRadioButton vegasRadioButton = new JRadioButton("Vegas");
        JRadioButton noneRadioButton = new JRadioButton("None");
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
        barOptionsPanelLeft.add(new JCheckBox("Timed Game"));
        barOptionsPanelLeft.add(new JCheckBox("Status Bar"));
        barOptionsPanelLeft.add(new JCheckBox("Outline Dragging"));
        
        // Right side options
        JPanel barOptionsPanelRight = new JPanel();
        barOptionsPanelRight.setLayout(new BoxLayout(barOptionsPanelRight, BoxLayout.Y_AXIS));
        barOptionsPanelRight.add(new JCheckBox("Cumulative Score"));

        // OK and Cancel buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                JRadioButton drawPanelSelection = drawOneRadioButton.isSelected() ? drawOneRadioButton : drawThreeRadioButton; 
                preferences.setDrawOption((DrawOptions)drawPanelSelection.getClientProperty(drawPanelSelection));
                preferences.save();
                setDialogResult(JOptionPane.OK_OPTION);
                setVisible(false);
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                setDialogResult(JOptionPane.CANCEL_OPTION);
                setVisible(false);
            }
        });
        
        actionsPanel.add(okButton);
        actionsPanel.add(cancelButton);

        mainPanel.add(drawPanel);
        mainPanel.add(scoringPanel);
        mainPanel.add(barOptionsPanelLeft);
        mainPanel.add(barOptionsPanelRight);
        
        add(mainPanel);
        add(actionsPanel);
    }
}