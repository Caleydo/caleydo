/**
 *
 */
package org.caleydo.view.enroute.path.node;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.enroute.path.APathwayPathRenderer;

/**
 * Base class for renderers of optional attributes for {@link ANode}s.
 *
 * @author Christian
 *
 */
public abstract class ANodeAttributeRenderer {

	protected final AGLView view;

	protected final PickingManager pickingManager;

	protected final TextureManager textureManager;

	protected final PixelGLConverter pixelGLConverter;

	protected final APathwayPathRenderer pathwayPathRenderer;

	/**
	 * Node for which the attribute shall be rendered.
	 */
	protected ANode node;

	public ANodeAttributeRenderer(AGLView view, ANode node, APathwayPathRenderer pathwayPathRenderer) {
		this.view = view;
		this.node = node;
		this.pathwayPathRenderer = pathwayPathRenderer;
		this.pickingManager = view.getPickingManager();
		this.textureManager = view.getTextureManager();
		this.pixelGLConverter = view.getPixelGLConverter();
	}

	/**
	 * Renders the attribute.
	 *
	 * @param gl
	 */
	public abstract void render(GL2 gl);

	/**
	 * Registers all picking listeners necessary for this node. This method is automatically called during object
	 * creation.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * This method shall be called when the attribute renderer is no longer needed to unregister its picking listeners.
	 */
	public abstract void unregisterPickingListeners();

}
