/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.ANodeAttributeRenderer;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.RemoveNodeButtonAttributeRenderer;

/**
 * The linearized mode for {@link CompoundNode}s.
 *
 * @author Christian
 *
 */
public class CompoundNodeLinearizedMode extends ACompoundNodeMode {

	/**
	 * @param view
	 */
	public CompoundNodeLinearizedMode(AGLView view, APathwayPathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		destroy();
		init();
		attributeRenderers.clear();
		if (node.getParentNode() == null) {
			RemoveNodeButtonAttributeRenderer attributeRenderer = new RemoveNodeButtonAttributeRenderer(view, node,
					pathwayPathRenderer);
			attributeRenderer.addNodeId(node.getNodeId());
			attributeRenderer.registerPickingListeners();
			addAttributeRenderer(attributeRenderer);
		}
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		// circleColor = SelectionType.MOUSE_OVER.getColor();
		determineBackgroundColor(pathwayPathRenderer.getMetaboliteSelectionManager());
		renderCircle(gl, glu, node.getPosition(), pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels()));

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

	}

	@Override
	protected void init() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				EventBasedSelectionManager geneSelectionManager = pathwayPathRenderer.getGeneSelectionManager();
				geneSelectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.addToType(SelectionType.SELECTION, node.getPrimaryPathwayVertexRep().getName().hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.SELECTION);
				pathwayPathRenderer.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				EventBasedSelectionManager geneSelectionManager = pathwayPathRenderer.getGeneSelectionManager();
				geneSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.addToType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep().getName().hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				pathwayPathRenderer.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {

				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				selectionManager.removeFromType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep().getName()
						.hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.NORMAL);
				// circleColor = DEFAULT_CIRCLE_COLOR;
				pathwayPathRenderer.setDisplayListDirty();
			}
		}, EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

	@Override
	public void destroy() {
		super.destroy();
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

	@Override
	public int getMinWidthPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

}
