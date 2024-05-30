package game.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.graphics.IRenderable;

import game.views.StockView;

public class SceneAnimationRenderer implements IRenderable {
	private Image deckImage;
	private Image deckImageAnimation = null;
	private List<Image> _deckImageAnimations = new ArrayList<Image>();

	private int delay = 0;
	private int delta = 1000;

	private Timer timer;

	private static final String DECK_ANIMATION_UPDATED = "DECK_ANIMATION_UPDATED";
	
	public SceneAnimationRenderer() {
		
	}
	
	public void setScene(Image deckImage, List<Image> deckImageAnimations, int timer) {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
		this.deckImage = null;
		this.deckImageAnimation = null;
		this._deckImageAnimations = null;
	
		this.deckImage = deckImage;
		this._deckImageAnimations = deckImageAnimations;

		this.timer = new Timer(true);
		this.timer.schedule(new TimerTask() {
			private int index = 0;

			@Override public void run() {
				deckImageAnimation = _deckImageAnimations.get(index);
				index = (index + 1) % _deckImageAnimations.size();
				AbstractFactory.getFactory(ViewFactory.class).multicastSignalListeners(StockView.class, new EventArgs(this, DECK_ANIMATION_UPDATED));
			}
		}, delay, delta);
	}

	@Override public Image getRenderableContent() {
		if (this.deckImage == null) {
			return null;
		}

		if (this._deckImageAnimations == null || this._deckImageAnimations.isEmpty()) {
			return deckImage;
		}

		BufferedImage bufferedImage = new BufferedImage(deckImage.getWidth(null), deckImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.drawImage(deckImage, 0, 0, null);
		if (this.deckImageAnimation != null) {
			graphics.drawImage(this.deckImageAnimation, 32, 32, null);
		}

		return bufferedImage;
	}
}