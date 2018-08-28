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

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import framework.core.mvc.view.DialogView;
import framework.core.system.Application;
import framework.utils.globalisation.Localization;

public final class OptionsDialogView extends DialogView {

    public OptionsDialogView() {
        super(Application.instance(), Localization.instance().getLocalizedString("Options Title"), 400, 400);
        
        this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setAutomaticDialogCentering(true);
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
        
        // Draw Panel UI
        JPanel drawPanel = new JPanel();
        drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
        drawPanel.setBorder(BorderFactory.createTitledBorder("Draw"));
        drawPanel.add(new JRadioButton("Draw One", true));
        drawPanel.add(new JRadioButton("Draw Three"));
        drawPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        // Scoring Panel UI
        JPanel scoringPanel = new JPanel();
        scoringPanel.setLayout(new BoxLayout(scoringPanel, BoxLayout.Y_AXIS));
        scoringPanel.setBorder(BorderFactory.createTitledBorder("Scoring"));
        scoringPanel.add(new JRadioButton("Standard", true));
        scoringPanel.add(new JRadioButton("Vegas"));
        scoringPanel.add(new JRadioButton("None"));
        
        JPanel drawAndScoringPanel = new JPanel();
        drawAndScoringPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        drawAndScoringPanel.add(drawPanel);
        drawAndScoringPanel.add(scoringPanel);

        add(drawAndScoringPanel);
    }
}