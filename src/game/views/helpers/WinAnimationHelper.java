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

    private static Timer _timer;

    private static Queue<FoundationPileView> _foundations = new LinkedList<FoundationPileView>();
    
    private static KeyAdapter _keyAdapter = new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            clear();
            GameView.showGameOverAlert();
        }
    };
    
    private static MouseAdapter _mouseAdapter = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            clear();
            GameView.showGameOverAlert();
        }
    };

    private static int _gameWidth;
    
    private static int _gameHeight;
    
    private final CardView _cardView;
    
    private int _x;
    
    private int _y;
    
    private double _deltaX = Math.floor(Math.random() * 6 - 3) * 2;
    
    private double _deltaY = -Math.random() * 16;
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardView The card view to animate
     */
    private WinAnimationHelper(CardView cardView) {
        _cardView = cardView;
        Point initialPoint = cardView.getParentIView().getContainerClass().getLocation();

        _x = initialPoint.x;
        _y = initialPoint.y;
        
        if(_deltaX == 0) {
            _deltaX = 1;
        }
    }
    
    /**
     * Process all the cards help by the foundation views
     */
    public static void processCards() {
        
        initialize();
        
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        List<FoundationPileView> foundationsList = viewFactory.getAll(FoundationPileView.class);
        Collections.reverse(foundationsList);
        synchronized(_foundations) {
            _foundations.addAll(foundationsList);
        }
    }
    
    /**
     * Static initializer for this class type
     */
    private static void initialize() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        GameView gameView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class);
        _gameWidth = gameView.getWidth();
        _gameHeight = gameView.getHeight() - (preferences.statusBar ? AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).getHeight() : 0);
        
        // Clear this class before proceeding
        clear();
        
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
                            helper = null;
                        }
                    }
                    else {
                        FoundationPileView foundation = _foundations.remove();
                        CardView card = foundation.getLastCard();
                        if(card != null) {
                            helper = new WinAnimationHelper(card);
                        }
                        _foundations.add(foundation);
                    }
                }
                else {
                    if(hadValues) {
                        clear();
                        GameView.showGameOverAlert();
                    }
                }
            }
        }, 0, 1000/60);
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
        gameView.add(cardView, gameView.getComponentZOrder(viewFactory.get(StatusBarView.class)));
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
        
        if(_x < (-CardView.CARD_HEIGHT) || _x > (_gameWidth + CardView.CARD_HEIGHT)) {
            return null;
        }
        
        // If y position is more than half the card height in distnce to the 
        // bottom of the game view, reposition the y-pos to be at that length
        // and bounce the card upwards by applying an inverse force to the change in y
        if(_y > _gameHeight - CardView.CARD_HEIGHT) {
            _y = _gameHeight - CardView.CARD_HEIGHT;
            _deltaY = -_deltaY * 0.85;
        }
        
        _deltaY += 0.98;
        
        return new Point(_x, _y);
    }

    /**
     * Clears the contents of this helper
     */
    public static void clear() {
        if(_timer != null) {
            _timer.cancel();
            _timer = null;
        }
        
        // TODO - remove both mouse and key adapters!!!!
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        if(viewFactory != null) {
            GameView gameView = viewFactory.get(GameView.class);
            if(gameView != null) {
                gameView.removeMouseListener(_mouseAdapter);
            }
        }
        Application.instance.removeKeyListener(_keyAdapter);
                
        _foundations.clear();
    }
}