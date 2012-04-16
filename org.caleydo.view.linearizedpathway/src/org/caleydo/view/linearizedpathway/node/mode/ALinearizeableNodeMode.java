/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;

/**
 * Base class for the different modes (preview, linearized) of nodes.
 * 
 * @author Christian
 * 
 */
public abstract class ALinearizeableNodeMode {

	/**
	 * The node associated with this mode.
	 */
	protected ALinearizableNode node;

	protected GLLinearizedPathway view;

	protected PickingManager pickingManager;

	protected CaleydoTextRenderer textRenderer;

	protected TextureManager textureManager;

	public ALinearizeableNodeMode(GLLinearizedPathway view) {
		this.view = view;
		this.pickingManager = view.getPickingManager();
		this.textRenderer = view.getTextRenderer();
		this.textureManager = view.getTextureManager();
	}

	/**
	 * Renders the node in its current mode.
	 * 
	 * @param gl
	 * @param glu
	 */
	public abstract void render(GL2 gl, GLU glu);

	/**
	 * Applies the mode for the specified node.
	 * 
	 * @param node
	 */
	public abstract void apply(ALinearizableNode node);

	/**
	 * @return Minimum pixel height that is required by the node in the current
	 *         mode.
	 */
	public abstract int getMinHeightPixels();

	/**
	 * @return Minimum pixel width that is required by the node in the current
	 *         mode.
	 */
	public abstract int getMinWidthPixels();

	/**
	 * Method that is intended to register all picking listeners for the current
	 * mode.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * Method that shall be called when the mode is no longer needed to
	 * unregister its picking listeners.
	 */
	public abstract void unregisterPickingListeners();

}
