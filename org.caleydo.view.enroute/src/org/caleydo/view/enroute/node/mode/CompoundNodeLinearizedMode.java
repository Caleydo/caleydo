/**
 * 
 */
package org.caleydo.view.enroute.node.mode;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.ALinearizableNode;
import org.caleydo.view.enroute.node.ANode;
import org.caleydo.view.enroute.node.ANodeAttributeRenderer;
import org.caleydo.view.enroute.node.CompoundNode;
import org.caleydo.view.enroute.node.RemoveNodeButtonAttributeRenderer;

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
	public CompoundNodeLinearizedMode(GLEnRoutePathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		unregisterPickingListeners();
		registerPickingListeners();
		attributeRenderers.clear();
		if (node.getParentNode() == null) {
			RemoveNodeButtonAttributeRenderer attributeRenderer = new RemoveNodeButtonAttributeRenderer(
					view, node);
			attributeRenderer.addNodeId(node.getNodeId());
			attributeRenderer.registerPickingListeners();
			addAttributeRenderer(attributeRenderer);
		}
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		// circleColor = SelectionType.MOUSE_OVER.getColor();
		determineBackgroundColor(view.getMetaboliteSelectionManager());
		renderCircle(gl, glu, node.getPosition(),
				pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels()));

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				EventBasedSelectionManager selectionManager = view
						.getMetaboliteSelectionManager();
				EventBasedSelectionManager geneSelectionManager = view
						.getGeneSelectionManager();
				geneSelectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.addToType(SelectionType.SELECTION, node
						.getPathwayVertexRep().getName().hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.SELECTION);
				view.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				EventBasedSelectionManager selectionManager = view
						.getMetaboliteSelectionManager();
				EventBasedSelectionManager geneSelectionManager = view
						.getGeneSelectionManager();
				geneSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.addToType(SelectionType.MOUSE_OVER, node
						.getPathwayVertexRep().getName().hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {

				EventBasedSelectionManager selectionManager = view
						.getMetaboliteSelectionManager();
				selectionManager.removeFromType(SelectionType.MOUSE_OVER, node
						.getPathwayVertexRep().getName().hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.NORMAL);
				// circleColor = DEFAULT_CIRCLE_COLOR;
				view.setDisplayListDirty();
			}
		}, EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());
	}

	@Override
	public int getMinWidthPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

}
