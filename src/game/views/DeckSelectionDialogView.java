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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

    private final int _cardRows = 2;
    
    private final int _cardColumns = 6;
    
    private final JPanel _drawPanel = new JPanel(new GridBagLayout());
    
    private final GridBagConstraints _drawPanelConstraints = new GridBagConstraints();
    
    private final JPanel _actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    
    private final JButton _okButton = new JButton("OK");
    
    private final JButton _cancelButton = new JButton("Cancel");
    
    /**
     * Indicates if the cumulative score option has changed since it's last saved value
     */
    public boolean cumulativeScoreChanged;
    
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
        _drawPanelConstraints.weighty = 1;
        _drawPanelConstraints.weightx = 1;
        _drawPanelConstraints.insets = new Insets(5, 5, 5, 5);
        
        // Go through card rows and card columns, and populate each index with a JButton
        // containing one of the card images
        for(int row = 0, index = 0; row < _cardRows; ++row) {
            _drawPanelConstraints.gridy = row;
            for(int column = 0; column < _cardColumns; ++column, ++index) {
                
                // Create the button and set the size we want it to be
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(39, 68));
                
                // Create the backside entity and assign it to the button
                BacksideCardEntity entity = new BacksideCardEntity(backsides[index]);
                button.putClientProperty(button, entity);
                
                // Set the icon of the button, make sure to scale it appropriately
                button.setIcon(new ImageIcon(entity.getRenderableContent().getScaledInstance(
                    button.getPreferredSize().width, 
                    button.getPreferredSize().height,
                    java.awt.Image.SCALE_SMOOTH
                )));
                
                // Specify the proper column constraint
                _drawPanelConstraints.gridx = column;
                
                // Add the button to the panel
                _drawPanel.add(button, _drawPanelConstraints);
            }
        }
       
        // Add the okay and cancel buttons
        _actionsPanel.add(_okButton);
        _actionsPanel.add(_cancelButton);
        
        // Add the main housing panels to this dialog
        add(_drawPanel);
        add(_actionsPanel);
        
        // Pack the UI to fit
        pack();

        // Render the UI
        super.render();
    }
}