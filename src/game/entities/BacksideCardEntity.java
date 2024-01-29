package game.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.entity.DataEntity;
import framework.core.factories.AbstractFactory;
import framework.core.factories.ViewFactory;
import framework.core.graphics.IRenderable;

import game.config.OptionsPreferences;
import game.views.StockView;

import generated.DataLookup;

public class BacksideCardEntity extends DataEntity {
	private class SceneRenderer implements IRenderable {
		private Image deckImage;
		private Image deckImageAnimation = null;
		private List<Image> _deckImageAnimations = new ArrayList<Image>();
		public boolean enabled = false;

		private int delay = 0;
		private int delta = 1000;

		private Timer timer;

		private void clear() {
			if (this.timer != null) {
				this.timer.cancel();
				this.timer = null;
			}
			this.deckImage = null;
			this.deckImageAnimation = null;
			this._deckImageAnimations = null;
		}
		
		public void setScene(Image deckImage, List<Image> deckImageAnimations, int timer) {
			this.clear();

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
			this.enabled = true;
		}

		@Override public Image getRenderableContent() {
			if (!this.enabled) {
				return null;
			}

			if (this.deckImage == null) {
				return null;
			}

			if (this._deckImageAnimations == null || this._deckImageAnimations.isEmpty()) {
				return deckImage;
			}

			BufferedImage bufferedImage = new BufferedImage(deckImage.getWidth(null), deckImage.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.drawImage(deckImage, 0, 0, null);
			if (this.deckImageAnimation != null) {
				graphics.drawImage(this.deckImageAnimation, 32, 32, null);
			}

			return bufferedImage;
		}
	}

	private final SceneRenderer sceneRenderer = new SceneRenderer();

	public static final String DECK_BACKSIDE_UPDATED = "DECK_BACKSIDE_UPDATED";

	public static final String DECK_ANIMATION_UPDATED = "DECK_ANIMATION_UPDATED";

	private boolean isBacksideVisible = false;

	private DataLookup.BACKSIDES _backside;

	public BacksideCardEntity() {		
		this.setBackside();
	}
	
	public void setIsBacksideShowing(boolean isShowing) {
		isBacksideVisible = isShowing;
	}

	public boolean getIsBacksideShowing() {
		return isBacksideVisible;
	}

	public DataLookup.BACKSIDES getBacksideData() {
		return _backside;
	}

	private void setBackside() {
		OptionsPreferences preferences = new OptionsPreferences();
		preferences.load();
		
		if (_backside == preferences.deck) {
			return;
		}
		_backside = preferences.deck;
		super.setActiveData(preferences.deck.identifier);
		
		switch (_backside) {
		case DECK_7:
			break;
		case DECK_10:
			break;
		case DECK_11:
			break;
		case DECK_12:
			this.sceneRenderer.setScene(super.getRenderableContent(),
					Arrays.asList(
							null,
							new DataEntity(DataLookup.ANIMATIONS.POKER_1.identifier).getRenderableContent(), 
							new DataEntity(DataLookup.ANIMATIONS.POKER_2.identifier).getRenderableContent(), 
							new DataEntity(DataLookup.ANIMATIONS.POKER_1.identifier).getRenderableContent(), 
							null),
					500);
			break;
		default:
			break;
		}
	}

	@Override public Image getRenderableContent() {
		Image image = this.sceneRenderer.getRenderableContent();
		if (image == null) {
			image = super.getRenderableContent();
		}

		return image;
	}

	@Override public void refresh() {
		setBackside();
	}
}