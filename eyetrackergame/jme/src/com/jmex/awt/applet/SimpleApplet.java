package com.jmex.awt.applet;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.app.SimpleGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.MouseInput;
import com.jme.renderer.Renderer;

/**
 * LWJGL2 Applet imlpementation similar to {@link SimpleGame}
 * A addComponentListener is added in the initSystem Method, to enable applet resizing.
 */
public abstract class SimpleApplet extends BaseSimpleApplet {
	private static final long serialVersionUID = 1L;

	/**
	 * Called every frame to update scene information.
	 * 
	 * @param interpolation
	 *            unused in this implementation
	 * @see BaseSimpleGame#update(float interpolation)
	 */
	protected final void update(float interpolation) {
		super.update(interpolation);

		if (!pause) {
			/** Call simpleUpdate in any derived classes of SimpleGame. */
			simpleUpdate();

			/** Update controllers/render states/transforms/bounds for rootNode. */
			rootNode.updateGeometricState(tpf, true);
			statNode.updateGeometricState(tpf, true);
		}
	}

	/**
	 * This is called every frame in BaseGame.start(), after update()
	 * 
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected final void render(float interpolation) {
		super.render(interpolation);

		Renderer r = display.getRenderer();

		/** Draw the rootNode and all its children. */
		r.draw(rootNode);

		/** Call simpleRender() in any derived classes. */
		simpleRender();

		/** Draw the stats node to show our stat charts. */
		r.draw(statNode);

		doDebug(r);
	}

	/**
	 * Makes sure the MouseCursor is visible and disables mouse look.
	 */
	protected void initGame() {
		super.initGame();
		MouseInput.get().setCursorVisible(true);
		((FirstPersonHandler) input).getMouseLookHandler().setEnabled(false);
	}

	/**
	 * Add a addComponentListener which reinitializes the renderer if the
	 * Applet is resized.
	 */
	protected void initSystem() {
		super.initSystem();
		this.addComponentListener(new AppletResizeListener(this));
	}

}