/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANodeAttributeRenderer;

/**
 * Base class for the different modes (preview, linearized) of nodes.
 *
 * @author Christian
 *
 */
public abstract class ALinearizeableNodeMode {

	protected final static float[] DEFAULT_BACKGROUND_COLOR = new float[] { 1, 1, 1, 1 };

	/**
	 * The node associated with this mode.
	 */
	protected ALinearizableNode node;

	/**
	 * The background color of the node.
	 */
	protected float[] backgroundColor = DEFAULT_BACKGROUND_COLOR;

	protected AGLView view;

	protected PickingManager pickingManager;

	protected CaleydoTextRenderer textRenderer;

	protected TextureManager textureManager;

	protected APathwayPathRenderer pathwayPathRenderer;

	/**
	 * The {@link ANodeAttributeRenderer}s that shall be rendered in the current mode.
	 */
	protected List<ANodeAttributeRenderer> attributeRenderers = new ArrayList<ANodeAttributeRenderer>();

	public ALinearizeableNodeMode(AGLView view, APathwayPathRenderer pathwayPathRenderer) {
		this.pathwayPathRenderer = pathwayPathRenderer;
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
	 * @return Minimum pixel height that is required by the node in the current mode.
	 */
	public abstract int getMinHeightPixels();

	/**
	 * @return Minimum pixel width that is required by the node in the current mode.
	 */
	public abstract int getMinWidthPixels();

	/**
	 * Method that is intended to initialize the mode and, e.g., register all picking listeners.
	 */
	protected abstract void init();

	/**
	 * This method is intended to set the background color according to the selection status of the data that is
	 * associated with the {@link #node}
	 *
	 * @param selectionManager
	 */
	protected void determineBackgroundColor(EventBasedSelectionManager selectionManager) {
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
	}

	/**
	 * Method that shall be called when the mode is no longer needed.
	 */
	public void destroy() {
		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.unregisterPickingListeners();
		}
	}

	/**
	 * @param attributeRenderers
	 *            setter, see {@link #attributeRenderers}
	 */
	public void setAttributeRenderers(List<ANodeAttributeRenderer> attributeRenderers) {
		this.attributeRenderers = attributeRenderers;
	}

	/**
	 * @return the attributeRenderers, see {@link #attributeRenderers}
	 */
	public List<ANodeAttributeRenderer> getAttributeRenderers() {
		return attributeRenderers;
	}

	public void addAttributeRenderer(ANodeAttributeRenderer attributeRenderer) {
		attributeRenderers.add(attributeRenderer);
	}

}
