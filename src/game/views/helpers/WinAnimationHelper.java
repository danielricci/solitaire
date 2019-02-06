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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.system.Application;

import game.views.CardView;
import game.views.GameView;

/**
 * This helper class performs a win animation on the specified card
 * 
 * @author Daniel Ricci <thedanny09@icloud.com>
 */
public class WinAnimationHelper {

    private static Timer _timer = new Timer(true);

    private static List<WinAnimationHelper> _helpers = new ArrayList<WinAnimationHelper>();
    
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
            @Override public void run() {
                if(_helpers.size() > 0) {
                    if(!_helpers.get(0).update()) {
                        _helpers.remove(0);
                    }
                }
            }
        }, 0, 1000/90);
    }
    
    /**
     * Constructs a new instance of this class type
     * 
     * @param cardView The card view to animate
     */
    private WinAnimationHelper(CardView cardView, Point initialPoint) {
        _cardView = cardView;
        _x = initialPoint.x;
        _y = initialPoint.y;
        
        if(_deltaX == 0) {
            _deltaX = 2;
        }
    }
    
    public void preProcessCard() {
        if(!_isPreProcessed) {
            _isPreProcessed = false;
            GameView gameView = AbstractFactory.getFactory(ViewFactory.class).get(GameView.class); 
            gameView.add(card);
            //card.setLocation(new Point(foundation.getLocation().x, foundation.getLocation().y));
            
        }
    }
    
    public static void processCard(CardView cardView, Point initialPoint) {
        synchronized(_helpers) {
            _helpers.add(new WinAnimationHelper(cardView, initialPoint));
        }
    }
    
    public boolean update() {
        synchronized(_helpers) {
            System.out.println("Updating " + this._cardView);
            
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
            System.out.println(String.format("(,%d,%d)", newPosX, newPosY));
            _cardView.setLocation(newPosX, newPosY);
            
            return true;
        }
    }
}