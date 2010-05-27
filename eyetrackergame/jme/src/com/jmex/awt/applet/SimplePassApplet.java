package com.jmex.awt.applet;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.app.SimplePassGame;
import com.jme.input.MouseInput;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;

/**
 * LWJGL2 Applet imlpementation similar to {@link SimplePassGame}<br>
 * A addComponentListener is added in the initSystem Method, to enable applet resizing.<br>
 * For use with passes which need stencil bits set(Shadow pass for example.), stencil bits<br> 
 * can be set in the applets constructor.<br>
 * <code>stencilBits = 1;</code>
 */
public abstract class SimplePassApplet extends BaseSimpleApplet {
	private static final long serialVersionUID = 1L;
	protected BasicPassManager pManager;

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
			
			pManager.updatePasses(tpf);
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

		pManager.renderPasses(r);

		/** Call simpleRender() in any derived classes. */
		simpleRender();

		/** Draw the stats node to show our stat charts. */
		r.draw(statNode);

		doDebug(r);
	}

	protected void initGame() {
		MouseInput.get().setCursorVisible(true);
		pManager = new BasicPassManager();

		super.initGame();
	}

	protected void initSystem() {
		super.initSystem();
		this.addComponentListener(new AppletResizeListener(this));
	}
}