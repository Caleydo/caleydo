/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.ANodeAttributeRenderer;
import org.caleydo.view.linearizedpathway.node.CompoundNode;

/**
 * The preview mode for {@link CompoundNode}s.
 * 
 * @author Christian
 * 
 */
public class CompoundNodePreviewMode extends ACompoundNodeMode {

	protected static final int MIN_NODE_WIDTH_PIXELS = 150;
	protected static final int TEXT_HEIGHT_PIXELS = 14;
	protected static final int SPACING_PIXELS = 4;

	/**
	 * @param view
	 */
	public CompoundNodePreviewMode(GLLinearizedPathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		unregisterPickingListeners();
		registerPickingListeners();
		attributeRenderers.clear();
	}

	@Override
	public void render(GL2 gl, GLU glu) {

		Vec3f nodePosition = node.getPosition();
		float width = pixelGLConverter.getGLWidthForPixelWidth(node.getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels());
		Vec3f circlePosition = new Vec3f(nodePosition.x() + width / 2.0f - height / 2.0f,
				nodePosition.y(), nodePosition.z());

		float textHeight = pixelGLConverter.getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS);
		float spacing = pixelGLConverter.getGLHeightForPixelHeight(SPACING_PIXELS);
		float leftX = nodePosition.x() - width / 2.0f;
		float bottomY = nodePosition.y() - height / 2.0f;

		gl.glPushName(pickingManager.getPickingID(view.getID(),
				PickingType.LINEARIZABLE_NODE.name(), node.getNodeId()));
		gl.glColor4fv(circleColor, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(leftX, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY + height, nodePosition.z());
		gl.glVertex3f(leftX, bottomY + height, nodePosition.z());
		gl.glEnd();

		gl.glColor4f(0, 0, 0, 1);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(leftX, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY, nodePosition.z());
		gl.glVertex3f(leftX + width, bottomY + height, nodePosition.z());
		gl.glVertex3f(leftX, bottomY + height, nodePosition.z());
		gl.glEnd();

		textRenderer.renderTextInBounds(gl, node.getCaption(), leftX + spacing, bottomY
				+ spacing, nodePosition.z(), width - height, textHeight);
		gl.glPopName();

		renderCircle(gl, glu, circlePosition, height - spacing);

		for (ANodeAttributeRenderer attributeRenderer : attributeRenderers) {
			attributeRenderer.render(gl);
		}

	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				view.setExpandedBranchSummaryNode(null);
				view.selectBranch(node);
			}

			@Override
			public void mouseOver(Pick pick) {
				node.setSelectionType(SelectionType.MOUSE_OVER);
				circleColor = SelectionType.MOUSE_OVER.getColor();
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				node.setSelectionType(SelectionType.NORMAL);
				circleColor = DEFAULT_CIRCLE_COLOR;
				view.setDisplayListDirty();
			}
		}, PickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		view.removeAllIDPickingListeners(PickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());

	}

	@Override
	public int getMinWidthPixels() {
		return MIN_NODE_WIDTH_PIXELS;
	}

}
