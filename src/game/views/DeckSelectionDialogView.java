/**
 * Daniel Ricci <thedanny09@icloud.com>
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import framework.core.mvc.view.DialogView;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

import game.entities.BacksideCardEntity;

import generated.DataLookup;

/**
 * The deck selection dialog view shows the list of deck images that the user can choose from while playing the game
 *
 * @author Daniel Ricci <thedanny09@icloud.com>
 *
 */
public final class DeckSelectionDialogView extends DialogView {

    /**
     * The number of rows in the card list
     */
    private final int _cardRows = 2;
    
    /**
     * The number of columns in the card list
     */
    private final int _cardColumns = 6;
    
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

    @Override public void render() {
        
        // Get a reference to all the backside data values
        DataLookup.BACKSIDES[] backsides = DataLookup.BACKSIDES.values();
        
        // Set the initial constraints for the grid bag layout
        GridBagConstraints drawPanelConstraints = new GridBagConstraints();
        drawPanelConstraints.weighty = 1;
        drawPanelConstraints.weightx = 1;
        drawPanelConstraints.insets = new Insets(5, 5, 5, 5);
        
        List<JButton> buttons = new ArrayList<JButton>();
        
        // The panel that holds the list of cards
        JPanel cardPanel = new JPanel(new GridBagLayout());
        
        // The OK and Cancel buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        // Go through card rows and card columns, and populate each index with a JButton
        // containing one of the card images
        for(int row = 0, index = 0; row < _cardRows; ++row) {
            drawPanelConstraints.gridy = row;
            for(int column = 0; column < _cardColumns; ++column, ++index) {
                
                // Create the button and set the size we want it to be
                JButton button = new JButton();
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(45, 74));
                
                // Create the backside entity and assign it to the button
                BacksideCardEntity entity = new BacksideCardEntity(backsides[index]);
                button.putClientProperty(button, entity);
                
                // Set the icon of the button, make sure to scale it appropriately
                button.setIcon(new ImageIcon(entity.getRenderableContent().getScaledInstance(39, 68, java.awt.Image.SCALE_SMOOTH)));
                
                // Add a listener event for when the deck image is selected
                button.addMouseListener(new MouseAdapter() {
                    @Override public void mousePressed(MouseEvent event) {
                        buttons.forEach(z -> z.setBorder(null));
                        button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                        
                        // If more than one click count was registered, then do what the OK button would do.
                        // Note that I don't call doClick because I dont want the OK button to look like it was clicked
                        if(event.getClickCount() > 1) {
                            setDialogResult(JOptionPane.OK_OPTION);
                            setVisible(false);
                        }
                    }
                });
                
                // Add a reference to the list of buttons
                buttons.add(button);
                
                // Specify the proper column constraint
                drawPanelConstraints.gridx = column;
                
                // Add the button to the panel
                cardPanel.add(button, drawPanelConstraints);
            }
        }

        // The OK button action event
        okButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                setDialogResult(JOptionPane.OK_OPTION);
                setVisible(false);
            }
        });

        // The Cancel button action event        
        cancelButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                setDialogResult(JOptionPane.CANCEL_OPTION);
                setVisible(false);
            }
        });
        
        // Add the okay and cancel buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionsPanel.add(okButton);
        actionsPanel.add(cancelButton);
        
        // Add the main housing panels to this dialog
        add(cardPanel);
        add(actionsPanel);
        
        // Pack the UI to fit
        pack();

        // Render the UI
        super.render();
    }
}