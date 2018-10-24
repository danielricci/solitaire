package game.views;

import java.awt.Color;

import javax.swing.JLabel;

import framework.core.mvc.view.PanelView;

public final class GameScoreView extends PanelView {

    private JLabel _label = new JLabel();
    
    private long _score = 0;
    
    /**
     * Constructs a new instance of this class type
     */
    public GameScoreView() {
        this.setBackground(Color.WHITE);
        _label.setText(this.toString());
        add(_label);
    }
    
    @Override public void destructor() {
        _score = 0;
        super.destructor();
    }
    
    @Override public String toString() {
        return "Score: " + String.valueOf(_score);
    }
}
