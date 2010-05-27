package com.jmex.game.state.load;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.GameState;
import com.jmex.scene.TimedLifeController;

/**
 * TransitionGameState
 *
 * The transition game state provides additional functionality to
 * LoadingGameState. A background image is now shown during the loading phase of
 * LoadingGameState. In addition, if a lead in game state is provided, the
 * transition state will fade frame the previous game state into the loading
 * state and then fade away. The lead in game state will be deactivated once the
 * transition is complete, but not removed from the game state manager.
 *
 * @author Andrew Carter
 */
public class TransitionGameState extends LoadingGameState {
	/** Background image will be on this */
	protected Quad background;

	/**
	 * Constructs a new Transition state without fading from the previous game
	 * state. Essentially the LoadingGameState but with a background image.
	 *
	 * @param imagePath
	 *          URL for a background image, null if none
	 */
	public TransitionGameState(URL imagePath) {
		initBackground(imagePath);
	}

	/**
	 * Constructs a new Transition state without fading from the previous game
	 * state. Essentially the LoadingGameState but with a background image.
	 *
	 * @param steps
	 *          percentage increments
	 * @param imagePath
	 *          URL for a background image, null if none
	 */
	public TransitionGameState(int steps, URL imagePath) {
		super(steps);

		initBackground(imagePath);
	}

	/**
	 * Constructs a new Transition state fading from one game state to another.
	 *
	 * @param leadIn
	 *          previous game state
	 * @param imagePath
	 *          URL for a background image, null if none
	 */
	public TransitionGameState(GameState leadIn, URL imagePath) {
		this(leadIn, 100, imagePath);
	}

	/**
	 * Constructs a new Transition state fading from one game state to another.
	 *
	 * @param leadIn
	 *          previous game state
	 * @param steps
	 *          percentage increments
	 * @param imagePath
	 *          URL for a background image, null if none
	 */
	public TransitionGameState(GameState leadIn, int steps, URL imagePath) {
		this(steps, imagePath);

		TransitionFadeIn fader = new TransitionFadeIn(1.0f, leadIn, this);
		rootNode.addController(fader);
		fader.setActive(true);
	}

	/**
	 * Places a a textured quad as the background. If the url is null, no quad is
	 * created.
	 *
	 * @param imagePath
	 *          URL to the background image
	 */
	private void initBackground(URL imagePath) {
		if (imagePath != null) {
			background = new Quad("Background", DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem
							.getDisplaySystem().getHeight());
			background.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			background.setColorBuffer(null);
			background.setDefaultColor(color);
			background.setRenderState(alphaState);
			background.setLocalTranslation(new Vector3f(DisplaySystem.getDisplaySystem().getWidth() / 2, DisplaySystem
							.getDisplaySystem().getHeight() / 2, 0.0f));

			TextureState texState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
			Texture tex = TextureManager.loadTexture(imagePath, Texture.MinificationFilter.BilinearNoMipMaps, Texture.MagnificationFilter.Bilinear);
			texState.setTexture(tex);
			background.setRenderState(texState);

			background.updateRenderState();

			background.updateRenderState();
			rootNode.attachChildAt(background, 0);
		}
	}
}

class TransitionFadeIn extends TimedLifeController {
	private static final long serialVersionUID = 1L;

	/** Game state which is fading away */
	private GameState leadIn;

	private TransitionGameState transition;

	public TransitionFadeIn(float lifeInSeconds, GameState leadIn, TransitionGameState transition) {
		super(lifeInSeconds);
		this.leadIn = leadIn;
		this.transition = transition;
		this.transition.setAlpha(0.0f);
	}

	public void updatePercentage(float percentComplete) {

		transition.setAlpha(percentComplete);

		if (percentComplete == 1.0f) {
			leadIn.setActive(false);
		}
	}
}