/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.ANode;

/**
 * Base class for renderers of optional attributes for {@link ANode}s.
 * 
 * @author Christian
 * 
 */
public abstract class ANodeAttributeRenderer {

	protected GLLinearizedPathway view;

	protected PickingManager pickingManager;

	protected TextureManager textureManager;

	protected PixelGLConverter pixelGLConverter;

	/**
	 * Node for which the attribute shall be rendered.
	 */
	protected ANode node;

	public ANodeAttributeRenderer(GLLinearizedPathway view, ANode node) {
		this.view = view;
		this.node = node;
		this.pickingManager = view.getPickingManager();
		this.textureManager = view.getTextureManager();
		this.pixelGLConverter = view.getPixelGLConverter();
		registerPickingListeners();
	}

	/**
	 * Renders the attribute.
	 * 
	 * @param gl
	 */
	public abstract void render(GL2 gl);

	/**
	 * Registers all picking listeners necessary for this node. This method is
	 * automatically called during object creation.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * This method shall be called when the attribute renderer is no longer
	 * needed to unregister its picking listeners.
	 */
	public abstract void unregisterPickingListeners();

}
