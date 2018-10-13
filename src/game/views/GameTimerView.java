package game.views;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

import framework.core.mvc.view.PanelView;

public final class GameTimerView extends PanelView {

    private Timer _timer = new Timer(true);

    private JLabel _label = new JLabel();
    
    private long _tick = 0;
    
    private boolean _running;
    
    /**
     * Constructs a new instance of this class type
     */
    public GameTimerView() {
        this.setBackground(Color.WHITE);
        _label.setText(this.toString());
        add(_label);
    }
    
    public void startGameTimer() {
        if(_running || !isVisible()) {
            return;
        }
        
        _running = true;
        _timer.schedule(new TimerTask() {
            @Override public void run() {
                ++_tick;
                _label.setText(GameTimerView.this.toString());
                System.out.println("Tick");
            }
        }, 1000, 1000);
    }
    
    public void stop() {
        _timer.cancel();
        _running = false;
        _tick = 0;
        _label.setText(toString());
    }
    
    @Override public void destructor() {
        _timer.cancel();
        super.destructor();
    }
    
    @Override public String toString() {
        return "Time: " + String.valueOf(_tick);
    }
}
