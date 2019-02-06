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
import framework.core.system.Application;

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

    private static Timer _timer = new Timer(true);

    private static Queue<FoundationPileView> _foundations = new LinkedList<FoundationPileView>();
    
    private final CardView _cardView;
    
    private int _x;
    
    private int _y;
    
    private final int _widthHalf = CardView.CARD_WIDTH / 2;
    
    private final int _heightHalf = CardView.CARD_HEIGHT / 2;
    
    private final int _gameWidth = Application.instance.getWidth();
    
    private final int _gameHeight = Application.instance.getHeight();
    
    private double _deltaX = Math.floor(Math.random() * 6 - 3) * 2;
    
    private double _deltaY = -Math.random() * 16;
    
    private boolean _isPreProcessed;
    
    static {
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
    
    public static void processCards() {
        ViewFactory viewFactory = AbstractFactory.getFactory(ViewFactory.class);
        List<FoundationPileView> foundationsList = viewFactory.getAll(FoundationPileView.class);
        Collections.reverse(foundationsList);
        
        synchronized(_foundations) {
            _foundations.addAll(foundationsList);
        }
    }
    
    private boolean update() {
        preProcessCard();
        
        _x += _deltaX;
        _y += _deltaY;
        
        if(_x < (-_widthHalf) || _x > (_gameWidth + _widthHalf)) {
            return false;
        }
        
        if(_y > _gameHeight - _heightHalf) {
            _y = _gameHeight - _heightHalf;
            _deltaY = -_deltaY * 0.85;
        }
        
        _deltaY += 0.98;
        
        int newPosX = (int)Math.floor(_x - _widthHalf);
        int newPosY = (int)Math.floor(_y - _heightHalf);
        _cardView.setLocation(newPosX, newPosY);
        System.out.println(_cardView.getLocation());
        
        return true;        
    }
}