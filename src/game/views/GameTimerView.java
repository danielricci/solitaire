package game.views;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

import framework.core.mvc.view.PanelView;

public final class GameTimerView extends PanelView {

    private Timer _timer = new Timer(true);

    JLabel label = new JLabel();
    
    private long _tick = 0;
    
    /**
     * Constructs a new instance of this class type
     */
    public GameTimerView() {
        this.setBackground(Color.WHITE);
        label.setText(this.toString());
        add(label);
        start();
    }
    
    public void start() {
        _timer.schedule(new TimerTask() {
            @Override public void run() {
                ++_tick;
                label.setText(GameTimerView.this.toString());
            }
        }, 0, 1000);
        
    }
    
    public void stop() {
        _timer.cancel();
    }
    
    @Override public String toString() {
        return "Time: " + String.valueOf(_tick);
    }
}
