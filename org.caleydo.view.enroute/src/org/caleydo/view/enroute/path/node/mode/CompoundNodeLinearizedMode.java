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
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
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

	protected IPickingListener pickingListener;

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
			attributeRenderer.addNodeId(node.hashCode());
			attributeRenderer.registerPickingListeners();
			addAttributeRenderer(attributeRenderer);
		}
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		// circleColor = SelectionType.MOUSE_OVER.getColor();
		// determineHighlightColor(pathwayPathRenderer.getMetaboliteSelectionManager());
		renderCircle(gl, glu, node.getPosition(), pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels()));

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode#renderHighlight(javax.media.opengl.GL2,
	 * javax.media.opengl.glu.GLU)
	 */
	@Override
	public void renderHighlight(GL2 gl, GLU glu) {

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

		if (!determineHighlightColor())
			return;
		gl.glColor3fv(highlightColor, 0);
		gl.glPushMatrix();
		gl.glTranslatef(node.getPosition().x(), node.getPosition().y(), 1);
		GLPrimitives.renderCircleBorder(gl, glu,
				pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels()) / 2.0f, 16, 0.1f);
		gl.glPopMatrix();
	}

	@Override
	protected void init() {
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				if (!node.isPickable())
					return;
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				vertexSelectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.clearSelection(SelectionType.SELECTION);
				selectionManager.addToType(SelectionType.SELECTION, node.getPrimaryPathwayVertexRep().getName()
						.hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				// EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getGeneSelectionManager();
				// vertexSelectionManager.clearSelection(SelectionType.SELECTION);
				// vertexSelectionManager.addToType(SelectionType.SELECTION, node.getPrimaryPathwayVertexRep().getID());
				// vertexSelectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.SELECTION);
				// pathwayPathRenderer.setHighlightDirty(true);

			}

			@Override
			public void mouseOver(Pick pick) {
				if (!node.isPickable())
					return;
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				vertexSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.addToType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep().getName()
						.hashCode());
				selectionManager.triggerSelectionUpdateEvent();
				//
				// EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				// vertexSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				// vertexSelectionManager.addToType(SelectionType.MOUSE_OVER,
				// node.getPrimaryPathwayVertexRep().getID());
				// vertexSelectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				// pathwayPathRenderer.setHighlightDirty(true);
			}

			@Override
			public void mouseOut(Pick pick) {
				if (!node.isPickable())
					return;
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				selectionManager.removeFromType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep().getName()
						.hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				// EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				// vertexSelectionManager.removeFromType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep()
				// .getID());
				// vertexSelectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.NORMAL);
				// circleColor = DEFAULT_CIRCLE_COLOR;
				// pathwayPathRenderer.setHighlightDirty(true);
			}
		};
		view.addIDPickingListener(pickingListener, EPickingType.LINEARIZABLE_NODE.name(), node.hashCode());
	}

	@Override
	public void destroy() {
		super.destroy();
		view.removeIDPickingListener(pickingListener, EPickingType.LINEARIZABLE_NODE.name(), node.hashCode());
	}

	@Override
	public int getMinWidthPixels() {
		return pathwayPathRenderer.getSizeConfig().getCircleNodeRadius() * 2;
	}

}
