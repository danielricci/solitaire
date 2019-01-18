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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import framework.core.mvc.view.DialogView;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.config.OptionsPreferences;
import game.entities.BacksideCardEntity;

import generated.DataLookup;
import generated.DataLookup.BACKSIDES;

/**
 * The deck selection dialog view shows the list of deck images that the user can choose from while playing the game
 *
 * @author Daniel Ricci <thedanny09@icloud.com>
 *
 */
public final class DeckSelectionDialogView extends DialogView {

    /**
     * This class represents a model that is used within one of the deck buttons
     * 
     * @author Daniel Ricci <thedanny09@icloud.com>
     */
    private class FixedStateButtonModel extends DefaultButtonModel {
        @Override public boolean isPressed() {
            return false;
        }
        @Override public boolean isRollover() {
            return false;
        }
        @Override public boolean isArmed() {
            return false;
        }
        @Override public boolean isSelected() {
            return false;
        }
    }
    
    /**
     * The number of rows in the card list
     */
    private final int _cardRows = 2;
    
    /**
     * The number of columns in the card list
     */
    private final int _cardColumns = 6;
    
    /**
     * The button width
     */
    private final int _buttonWidth = 45;
            
    /**
     * The button height
     */
    private final int _buttonHeight = 74;
    
    /**
     * The image width of the button
     */
    private final int _buttonImageWidth = 39;
    
    /**
     * The image height of the button
     */
    private final int _buttonImageHeight = 68;
    
    /**
     * The OK button
     */
    private final JButton _okButton = new JButton("OK");
    
    /**
     * The Cancel button
     */
    private final JButton _cancelButton = new JButton("Cancel");
    
    /**
     * The current active button
     */
    private JButton _activeButton;
    
    /**
     * The list of buttons that hold a deck option
     */
    private final List<JButton> _deckButtons = new ArrayList<JButton>();
    
    /**
     * Constructs a new instance of this class type
     */
    public DeckSelectionDialogView() {
        super(Application.instance, Localization.instance().getLocalizedString("Select Card Back"));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setAutomaticDialogCentering(true);
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
    }
    
    @Override protected void enterActionPerformed(ActionEvent event) {
        _okButton.doClick();
    }
    
    @Override public void render() {
        
        // Get a reference to all the backside data values
        DataLookup.BACKSIDES[] backsides = DataLookup.BACKSIDES.values();
        
        // Set the initial constraints for the grid bag layout
        GridBagConstraints drawPanelConstraints = new GridBagConstraints();
        drawPanelConstraints.weighty = 1;
        drawPanelConstraints.weightx = 1;
        drawPanelConstraints.insets = new Insets(5, 5, 5, 5);
        
        // The panel that holds the list of cards
        JPanel cardPanel = new JPanel(new GridBagLayout());
        
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        // Go through card rows and card columns, and populate each index with a JButton
        // containing one of the card images
        for(int row = 0, index = 0; row < _cardRows; ++row) {
            drawPanelConstraints.gridy = row;
            for(int column = 0; column < _cardColumns; ++column, ++index) {
                
                // Create the backside entity and assign it to the button
                BacksideCardEntity entity = new BacksideCardEntity();
                entity.setActiveData(backsides[index].identifier);
                
                // Create the button and set the size we want it to be
                JButton button = new JButton(new ImageIcon(entity.getRenderableContent().getScaledInstance(_buttonImageWidth, _buttonImageHeight, java.awt.Image.SCALE_SMOOTH)));
                
                // Set the border of the button based on what was currently set in the preferences, and make sure to set it as active within this dialog
                button.setBorder(preferences.deck == backsides[index] ? BorderFactory.createLineBorder(Color.BLUE, 2) : null);
                _activeButton = button;
                
                button.setModel(new FixedStateButtonModel());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(_buttonWidth, _buttonHeight));
                button.putClientProperty(button, backsides[index]);
                
                // Add a listener event for when the deck image is selected
                button.addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed(MouseEvent event) {
                        _deckButtons.forEach(z -> z.setBorder(null));
                        button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                        _activeButton = button;
                        // If more than one click count was registered, then do what the OK button would do.
                        // Note that I don't call doClick because I dont want the OK button to look like it was clicked
                        if(event.getClickCount() > 1) {
                            ok();
                        }
                    }
                });
                
                // Add a reference to the list of buttons
                _deckButtons.add(button);
                
                // Specify the proper column constraint
                drawPanelConstraints.gridx = column;
                
                // Add the button to the panel
                cardPanel.add(button, drawPanelConstraints);
            }
        }

        // The OK button action event
        _okButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                ok();
            }
        });

        // The Cancel button action event        
        _cancelButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                setDialogResult(JOptionPane.CANCEL_OPTION);
                setVisible(false);
            }
        });
        
        // Add the okay and cancel buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.add(_okButton);
        actionsPanel.add(_cancelButton);
        
        // Add the main housing panels to this dialog
        add(cardPanel);
        add(actionsPanel);
        
        // Pack the UI to fit
        pack();

        // Render the UI
        super.render();
    }
    
    private void ok() {
        BACKSIDES back = (BACKSIDES) _activeButton.getClientProperty(_activeButton);
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        preferences.deck = back;
        preferences.save();
        setDialogResult(JOptionPane.OK_OPTION);
        setVisible(false);
    }
}