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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;

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

    private static int _gameWidth;
    
    private static int _gameHeight;
    
    private final CardView _cardView;
    
    private float _x;
    
    private float _y;
    
    private final int _cardWidthHalf = CardView.CARD_WIDTH / 2;

    private final int _cardHeightHalf = CardView.CARD_HEIGHT / 2;
    
    private double _deltaX = Math.floor(Math.random() * 6 - 3) * 2;
    
    private double _deltaY = -Math.random() * 16;
    
    private boolean _isPreProcessed;
    
    private static void initialize() {
        OptionsPreferences preferences = new OptionsPreferences();
        preferences.load();
        
        GameView gameView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class);
        _gameWidth = gameView.getWidth() + 4;
        _gameHeight = gameView.getHeight() - (preferences.statusBar ? AbstractFactory.getFactory(ViewFactory.class).get(StatusBarView.class).getHeight() : 0);
        
        if(_timer != null) {
            _timer.cancel();
        }
        
        _timer = new Timer(true);
        _timer.schedule(new TimerTask() {
            WinAnimationHelper helper = null;
            @Override public void run() {
                if(_foundations.size() > 0) {
                    
                    if(helper != null) {
                        if(!helper.update()) {
                            helper = null;
                        }
                    }
                    else {
                        FoundationPileView foundation = _foundations.remove();
                        CardView cardView = foundation.getLastCard();
                        if(cardView != null) {
                            helper = new WinAnimationHelper(cardView);
                        }
                        _foundations.add(foundation);
                    }
                }
                else {
                    _timer.cancel();
                }
            }
        }, 0, 1000/60);
    }
    
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
            _deltaX = 2;
        }
    }
    
    /**
     * Performs a preprocess of the currently set card view
     */
    private void preProcessCard() {
        if(!_isPreProcessed) {
            _isPreProcessed = true;
            
            ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
            GameView gameView = viewFactory.get(GameView.class);
            Point parentLocation = this._cardView.getParentIView().getContainerClass().getLocation();
            
            gameView.add(this._cardView, gameView.getComponentZOrder(viewFactory.get(StatusBarView.class)) + 1);
            this._cardView.setLocation(parentLocation);
        }
    }
    
    /**
     * Process all the cards help by the foundation views
     */
    public static void processCards() {

        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        List<FoundationPileView> foundationsList = viewFactory.getAll(FoundationPileView.class);
        Collections.reverse(foundationsList);
        
        synchronized(_foundations) {
            _foundations.addAll(foundationsList);
            initialize();
        }
    }
    
    /**
     * Performs an update
     *
     * @return TRUE if the operation was successful, false otherwise
     */
    private boolean update() {
        preProcessCard();
        
        // Take the change in X and the change in Y and apply them respectively
        _x += _deltaX;
        _y += _deltaY;
        
        // TODO -2 is the offset of the extr border from each card. Technically this can be removed
        // once an explicit border is no longer necessary because the art style will have a border
        // around it by default
        if(_x < (-_cardWidthHalf - 2) || _x > (_gameWidth + _cardWidthHalf)) {
            return false;
        }
        
        // If y position is more than half the card height in distnce to the 
        // bottom of the game view, reposition the y-pos to be at that length
        // and bounce the card upwards by applying an inverse force to the change in y
        if(_y > _gameHeight - _cardHeightHalf) {
            _y = _gameHeight - _cardHeightHalf;
            _deltaY = -_deltaY * 0.85;
        }
        
        _deltaY += 0.98;
        
        // Calculate the new position by subtracting half the width|height from the current locations
        int newPosX = (int)Math.floor(_x - _cardWidthHalf);
        int newPosY = (int)Math.floor(_y - _cardHeightHalf);
        _cardView.setLocation(newPosX, newPosY);
        
        return true;        
    }

    /**
     * Clears the contents of this helper
     */
    public static void clear() {
        if(_timer != null) {
            _timer.cancel();
            _timer = null;
        }
        
        _foundations.clear();
    }
}