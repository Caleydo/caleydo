package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.util.AGLGUIElement;

import com.sun.opengl.util.j2d.TextRenderer;

public class VAElementRepresentation
	extends AGLGUIElement
	implements ICompositeGraphic {

	private static final String sTextForHeightCalculation =
		"Text without characters below the bottom textline";

	private Vec3f vecPosition;
	private float fHeight;
	private float fWidth;
	private int iVAIndex;
	private int iViewID;
	private PickingManager pickingManager;
	private ClusterNode clusterNode;

	public VAElementRepresentation(int iViewID, PickingManager pickingManager, ClusterNode clusterNode) {
		setMinSize(GrouperRenderStyle.GUI_ELEMENT_MIN_SIZE);
		vecPosition = new Vec3f();
		this.iViewID = iViewID;
		this.pickingManager = pickingManager;
		this.clusterNode = clusterNode;
	}

	@Override
	public void add(ICompositeGraphic graphic) {
		// This element is a leaf and therefore has no children.
	}

	@Override
	public void delete(ICompositeGraphic graphic) {
		// This element is a leaf and therefore has no children.
	}

	@Override
	public void draw(GL gl, TextRenderer textRenderer, Vec3f vecRelativeDrawingPosition) {

		beginGUIElement(gl, vecRelativeDrawingPosition);

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GROUPER_VA_ELEMENT_SELECTION,
			clusterNode.getClusterNr()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight, vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		float[] text_color = GrouperRenderStyle.TEXT_COLOR;
		textRenderer.setColor(text_color[0], text_color[1], text_color[2], text_color[3]);

		textRenderer.begin3DRendering();

		textRenderer.draw3D(clusterNode.getNodeName(), vecPosition.x() + GrouperRenderStyle.TEXT_SPACING,
			vecPosition.y() - fHeight + GrouperRenderStyle.TEXT_SPACING, vecPosition.z(),
			GrouperRenderStyle.TEXT_SCALING);
		textRenderer.flush();

		textRenderer.end3DRendering();

		gl.glPopAttrib();

		gl.glPopName();

		endGUIElement(gl);
	}

	@Override
	public Vec3f getPosition() {
		return vecPosition;
	}

	@Override
	public void setPosition(Vec3f vecPosition) {
		this.vecPosition = vecPosition;
	}

	@Override
	public float getHeight() {
		return fHeight;
	}

	public int getVAIndex() {
		return iVAIndex;
	}

	public void setVAIndex(int iVAIndex) {
		this.iVAIndex = iVAIndex;
	}

	public void setViewID(int iViewID) {
		this.iViewID = iViewID;
	}

	@Override
	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer) {
		calculateDimensions(gl, textRenderer);
	}

	@Override
	public float getWidth() {
		return fWidth;
	}

	@Override
	public void setToMaxWidth(float fWidth) {
		if (this.fWidth < fWidth)
			this.fWidth = fWidth;
	}

	@Override
	public void calculateDimensions(GL gl, TextRenderer textRenderer) {
		Rectangle2D bounds = textRenderer.getBounds(sTextForHeightCalculation);
		fHeight =
			(float) bounds.getHeight() * GrouperRenderStyle.TEXT_SCALING + 2.0f
				* GrouperRenderStyle.TEXT_SPACING;
		bounds = textRenderer.getBounds(clusterNode.getNodeName());
		float fTempWidth =
			(float) bounds.getWidth() * GrouperRenderStyle.TEXT_SCALING + 2.0f
				* GrouperRenderStyle.TEXT_SPACING;
		fWidth = fTempWidth;
	}
}
