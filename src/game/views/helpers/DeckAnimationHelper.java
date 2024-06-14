package game.views.helpers;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.DataFactory;
import framework.core.factories.ViewFactory;
import framework.core.graphics.IRenderable;
import framework.core.graphics.IRenderableContainer;

import game.entities.StockCardEntity;
import game.views.StockView;

import generated.DataLookup;

public class DeckAnimationHelper implements IRenderableContainer {
	private List<StockCardEntity> stockCardEntities;
	
	private Image deckImageOriginal;
	private Image currentDeckImageAnimation;
	
	private int delay = 0;
	private int period = 1000;

	private Timer timer;

	public static final String DECK_ANIMATION_UPDATED = "DECK_ANIMATION_UPDATED";

	private static volatile DeckAnimationHelper instance = null;
	
	private DeckAnimationHelper() {
	}
	
	public static DeckAnimationHelper getInstance() {
		synchronized(DeckAnimationHelper.class) {
			if(instance == null) {
				instance = new DeckAnimationHelper();
			}
		}
		
		return instance;
	}
	
	public void clear() {
		if(timer != null) {
			timer.cancel();
			timer = null;
        }
		
		this.currentDeckImageAnimation = null;
		this.deckImageOriginal = null;
		this.stockCardEntities = null;
	}
	
	public void setScene(List<StockCardEntity> stockCardEntities) {
		this.stockCardEntities = stockCardEntities;
		
		Image deckImageOriginal = stockCardEntities.get(0).backsideCardEntity.getRenderableContent();
		if(this.deckImageOriginal == deckImageOriginal) {
			return;
		}
		
		ArrayList<Image> deckImageAnimations = new ArrayList<Image>();
		switch(stockCardEntities.get(0).backsideCardEntity.getBacksideData()) {
			case DECK_7: // ROBOT
				deckImageAnimations.addAll(
					AbstractFactory.getFactory(DataFactory.class).getDataEntities(
							DataLookup.ANIMATIONS.DECK_7_2.identifier,
							DataLookup.ANIMATIONS.DECK_7_1.identifier,
							null,
							DataLookup.ANIMATIONS.DECK_7_1.identifier
				));
				
				this.period = 700;
				this.delay = 0;
				break;
			case DECK_10: // CASTLE
				deckImageAnimations.addAll(AbstractFactory.getFactory(DataFactory.class).getDataEntities(
						null,
						DataLookup.ANIMATIONS.DECK_10_1.identifier, null
					));
				this.period = 1000;
				this.delay = 0;
				break;
			case DECK_11: // BEACH
				deckImageAnimations.addAll(AbstractFactory.getFactory(DataFactory.class).getDataEntities(
						null,
						DataLookup.ANIMATIONS.DECK_11_1.identifier,
						DataLookup.ANIMATIONS.DECK_11_2.identifier
					));
				
				this.period = 1500;
				this.delay = 8500;
				break;
			case DECK_12: // POKER
				deckImageAnimations.addAll(AbstractFactory.getFactory(DataFactory.class).getDataEntities(
						null,
						DataLookup.ANIMATIONS.DECK_12_1.identifier,
						DataLookup.ANIMATIONS.DECK_12_2.identifier,
						DataLookup.ANIMATIONS.DECK_12_1.identifier
					));
				
				this.period = 1000;
				this.delay = 9000;

				break;
			default:
				break;
		}
		
		this.deckImageOriginal = deckImageOriginal;
		this.currentDeckImageAnimation = null;
		
		this.timer = new Timer(true);
		this.timer.schedule(new TimerTask() {
			private int index = 0;
			@Override public void run() {
				System.out.println(index);
				currentDeckImageAnimation = deckImageAnimations.get(index);	
				index = (index + 1) % deckImageAnimations.size();
				
				AbstractFactory.getFactory(ViewFactory.class).multicastSignalListeners(StockView.class, new EventArgs(this, DECK_ANIMATION_UPDATED));
			}
		}, 0, 1000);
	}
	
	@Override public List<IRenderable> getRenderableContents() {
		return this.stockCardEntities.stream().map(z -> z.toRenderable()).collect(Collectors.toList());
	}

	@Override public Image getRenderableContent() {
		if(this.currentDeckImageAnimation == null) {
			return this.deckImageOriginal;
		}
		
		return this.currentDeckImageAnimation;		
	}
}