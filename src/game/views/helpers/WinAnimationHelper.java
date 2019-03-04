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

package game.views.helpers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.system.Application;

import game.config.OptionsPreferences;
import game.views.CardView;
import game.views.FoundationPileView;
import game.views.GameView;
import game.views.StatusBarView;

/**
 * This helper class performs a win animation on the specified card
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class WinAnimationHelper {

    /**
     * The timer used to update the position of the cards associated to the foundations
     */
    private static Timer _timer;

    /**
     * The queue of foundation views, ordered in priority of rendering importance
     */
    private static Queue<FoundationPileView> _foundations = new LinkedList<FoundationPileView>();
    
    /**
     * The key adapter that handles when a key is pressed during the animation phase
     */
    private static KeyAdapter _keyAdapter = new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            clear();
            GameView.showGameOverDialog();
        }
    };
    
    /**
     * The mouse adapter that handles when a mouse button is pressed during the animation phase
     */
    private static MouseAdapter _mouseAdapter = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            clear();
            GameView.showGameOverDialog();
        }
    };
    
    /**
     * The canvas width
     */
    private static int _canvasWidth;
    
    /**
     * The canvas height
     */
    private static int _canvasHeight;
    
    /**
     * The card view that is being manipulated
     */
    private final CardView _cardView;

    /**
     * The x-position being used for the card coordinate
     */
    private double _x;
    
    /**
     * The y-position being used for the card coordinate
     */
    private double _y;
    
    /**
     * The change in `x` over time
     */
    private double _deltaX = Math.floor(Math.random() * 6 - 3) * 2;
    
    /*
     * The change in `y` over time
     */
    private double _deltaY = -Math.random() * 16;
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardView The card view to animate
     */
    private WinAnimationHelper(CardView cardView) {
        _cardView = cardView;
        Point position = cardView.getParentIView().getContainerClass().getLocation();
        _x = position.getX();
        _y = position.getY();

        if(_deltaX == 0) {
            _deltaX = 1;
        }
    }
    
    /**
     * Process all the cards held by the foundation views
     */
    public static void processCards() {
        // Get the list of foundation piles
        List<FoundationPileView> foundationsList = AbstractFactory.getFactory(ViewFactory.class).getAll(FoundationPileView.class);
        
        // Reverse the list so that we start with the left-most pile.
        Collections.reverse(foundationsList);

        // Initialize this helper class
        initialize();
        
        // Populate the queue of items to be processed
        _foundations.addAll(foundationsList);
    }
    
    /**
     * Initializes this helper in preparation for rendering the cards associated to the foundations
     */
    private static void initialize() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        GameView gameView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class);
        _canvasWidth = gameView.getWidth();
        _canvasHeight = gameView.getHeight() - (preferences.statusBar ? AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).getHeight() : 0);
        
        // Clear this class before proceeding
        clear();

        Application.instance.getJMenuBar().addMouseListener(_mouseAdapter);
        for(int i = 0; i < Application.instance.getJMenuBar().getMenuCount(); ++i) {
            Application.instance.getJMenuBar().getMenu(i).setEnabled(false);
        }

        gameView.addMouseListener(_mouseAdapter);
        Application.instance.addKeyListener(_keyAdapter);
        
        _timer = new Timer(true);
        _timer.schedule(new TimerTask() {
            WinAnimationHelper helper = null;
            boolean hadValues = false;
            @Override public void run() {
                if(_foundations.size() > 0) {
                    hadValues = true;
                    if(helper != null) {
                        if(!helper.update()) {
                            helper._cardView.getParent().remove(helper._cardView);
                            helper = null;
                        }
                    }
                    else {
                        // Get a reference to the current head of the foundations list
                        FoundationPileView foundation = _foundations.poll();

                        // If the foundation exists then remove it from the list and get the
                        // last card. Provided that it exists then create a helper object to
                        // animate the card and put the foundation at the back of the queue
                        if(foundation != null) {
                            _foundations.remove(foundation);
                            CardView card = foundation.getLastCard();
                            if(card != null) {
                                helper = new WinAnimationHelper(card);
                                _foundations.add(foundation);
                            }
                        }
                    }
                }
                else {
                    if(hadValues) {
                        clear();
                        GameView.showGameOverDialog();
                    }
                }
            }
        }, 0, 1000/80);
    }

    /**
     * Performs an update by performing both a next step point calculation and a draw routine
     *
     * @return TRUE if the operation was successful, false otherwise
     */
    private boolean update() {

        Point point = calculateNextStep();
        if(point == null) {
            return false;
        }
        
        draw(point);
        return true;    
    }
    
    /**
     * Draws the currently set card view to the specified position
     *
     * @param point The position to draw to
     */
    private void draw(Point point) {
        CardView cardView = CardView.createLightWeightCard(_cardView);
        cardView.render();
        
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        GameView gameView = viewFactory.get(GameView.class);
        gameView.add(cardView, 0);
        cardView.setBounds(new Rectangle(point.x, point.y, _cardView.getWidth(), _cardView.getHeight()));        
    }
    
    /**
     * Calculates the next position that the currently set card will be at
     *
     * @return The position associated to the next step where the card would be at 
     */
    private Point calculateNextStep() {

        // Take the change in X and the change in Y and apply them respectively
        _x += _deltaX;
        _y += _deltaY;

        // If you are outside the left or right canvas limits then the card should not 
        // longer be positioned anywhere relevant so do not return any position
        if(_x < -CardView.CARD_WIDTH || _x > _canvasWidth) {
            return null;
        }
        
        // If the position is outside canvas height (with respect to the bottom of the card)
        if(_y > _canvasHeight - CardView.CARD_HEIGHT) {
            
            // Normalize the position of the card by placing it on the theoretical bottom of the canvas
            _y = _canvasHeight - CardView.CARD_HEIGHT;
            
            // Take the change in `y` inverse it, this along will cause the card to bounce upwards
            // Take only a small percentage of the delta so that it bounces less
            _deltaY = -_deltaY * 0.85;
        } 

        _deltaY += 0.98;

        return new Point((int)_x, (int)_y);
    }
    
    /**
     * Clears the contents of this helper
     */
    public static void clear() {
        if(_timer != null) {
            _timer.cancel();
            _timer = null;
        }
        
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        if(viewFactory != null) {
            GameView gameView = viewFactory.get(GameView.class);
            if(gameView != null) {
                gameView.removeMouseListener(_mouseAdapter);
            }
        }
        Application.instance.removeKeyListener(_keyAdapter);
        Application.instance.getJMenuBar().removeMouseListener(_mouseAdapter);
        
        for(int i = 0; i < Application.instance.getJMenuBar().getMenuCount(); ++i) {
            Application.instance.getJMenuBar().getMenu(i).setEnabled(true);
        }
        
        _foundations.clear();
    }
}