/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.CompoundNode;

/**
 * The preview mode for {@link CompoundNode}s.
 *
 * @author Christian
 *
 */
public class CompoundNodePreviewMode extends ACompoundNodeMode {

	// protected static final int MIN_NODE_WIDTH_PIXELS = 70;
	// protected static final int TEXT_HEIGHT_PIXELS = 12;
	// protected static final int SPACING_PIXELS = 4;

	// protected EnRoutePathRenderer enRoutePathRenderer;

	protected IPickingListener pickingListener;

	/**
	 * @param view
	 */
	public CompoundNodePreviewMode(AGLView view, APathwayPathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		destroy();
		init();
		attributeRenderers.clear();
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		Vec3f nodePosition = node.getPosition();
		float width = pixelGLConverter.getGLWidthForPixelWidth(node.getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels());
		Vec3f circlePosition = new Vec3f(nodePosition.x() + width / 2.0f - height / 2.0f, nodePosition.y(),
				nodePosition.z());

		float textHeight = pixelGLConverter.getGLHeightForPixelHeight(pathwayPathRenderer.getSizeConfig()
				.getNodeTextHeight());
		float horizontalSpacing = pixelGLConverter.getGLHeightForPixelHeight(4);
		float verticalSpacing = (height - textHeight) / 2.0f;
		float leftX = nodePosition.x() - width / 2.0f;
		float bottomY = nodePosition.y() - height / 2.0f;

		// determineHighlightColor(pathwayPathRenderer.getMetaboliteSelectionManager());

		gl.glPushName(view.getPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.hashCode()));
		gl.glColor4fv(backgroundColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(leftX, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY + height, nodePosition.z());
		gl.glVertex3f(leftX, bottomY + height, nodePosition.z());
		gl.glEnd();

		gl.glColor4f(0, 0, 0, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(leftX, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY + height, nodePosition.z());
		gl.glVertex3f(leftX, bottomY + height, nodePosition.z());
		gl.glEnd();

		textRenderer.renderTextInBounds(gl, node.getLabel(), leftX + horizontalSpacing, bottomY + verticalSpacing
				+ pixelGLConverter.getGLHeightForPixelHeight(2), nodePosition.z(), width - height, textHeight);
		gl.glPopName();

		renderCircle(gl, glu, circlePosition, textHeight);

		// for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
		// attributeRenderer.render(gl);
		// }

	}

	@Override
	protected void init() {
		pickingListener = new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				if (!node.isPickable())
					return;
				ALinearizableNode branchNode = node;
				while (branchNode.getParentNode() != null) {
					branchNode = branchNode.getParentNode();
				}
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				EventBasedSelectionManager vertexSelectionManager = pathwayPathRenderer.getVertexSelectionManager();
				vertexSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);

				pathwayPathRenderer.selectBranch(branchNode);
			}

			@Override
			public void mouseOver(Pick pick) {
				if (!node.isPickable())
					return;
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.addToType(SelectionType.MOUSE_OVER, node.getPrimaryPathwayVertexRep().getName()
						.hashCode());
				selectionManager.triggerSelectionUpdateEvent();

				node.setSelectionType(SelectionType.MOUSE_OVER);
				// circleColor = SelectionType.MOUSE_OVER.getColor();
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
		return pathwayPathRenderer.getSizeConfig().getRectangleNodeWidth();
	}

	@Override
	public int getMinHeightPixels() {
		return pathwayPathRenderer.getSizeConfig().getRectangleNodeHeight();
	}

}
