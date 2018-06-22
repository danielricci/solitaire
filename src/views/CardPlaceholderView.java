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

package views;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import engine.communication.internal.signal.ISignalReceiver;
import engine.communication.internal.signal.arguments.BooleanEventArgs;
import engine.core.mvc.view.TransparentPanelView;

public final class CardPlaceholderView extends TransparentPanelView {

    public JLayeredPane pane = new JLayeredPane();
    
    /**
     * The event associated to toggling the placeholder views
     */
    public static final String EVENT_TOGGLE_PLACEHOLDERS = "EVENT_TOGGLE_PLACEHOLDERS";
    
    /**
     * Creates a new instance of this class type
     */
    public CardPlaceholderView() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(71, 96));
        add(pane);
    }
       
    @Override public void onViewInitialized() {
    }
    
    @Override public void clear() {       
    }

    @Override public void registerSignalListeners() {
        addSignalListener(CardPlaceholderView.EVENT_TOGGLE_PLACEHOLDERS, new ISignalReceiver<BooleanEventArgs>() {
            @Override public void signalReceived(BooleanEventArgs event) {
                CardPlaceholderView.this.setOpaque(event.getResult());
            }
        });
    }
}