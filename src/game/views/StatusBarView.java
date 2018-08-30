package game.views;

import java.awt.Color;
import java.awt.Dimension;

import framework.core.mvc.view.PanelView;

public final class StatusBarView extends PanelView {

    /**
     * Constructs a new instance of this class type
     */
    public StatusBarView() {
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(getPreferredSize().width, 16));
    }
}
