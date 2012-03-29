package org.caleydo.view.grouper.drawingstrategies.group;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import com.jogamp.opengl.util.awt.TextRenderer;

public class GroupDrawingStrategyDragged extends AGroupDrawingStrategyRectangular {

	private GrouperRenderStyle renderStyle;

	public GroupDrawingStrategyDragged(GrouperRenderStyle renderStyle) {
		this.renderStyle = renderStyle;
	}

	@Override
	public void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {
		// Use drawDraggedGroup Method

	}

	public void drawDraggedGroup(GL2 gl, GroupRepresentation groupRepresentation,
			float fMouseCoordinateX, float fMouseCoordinateY,
			float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY) {

		float fGroupColor[] = renderStyle.getGroupColorForLevel(groupRepresentation
				.getHierarchyLevel());
		float fColor[] = { fGroupColor[0], fGroupColor[1], fGroupColor[2], 0.5f };
		drawDragged(gl, groupRepresentation, fMouseCoordinateX, fMouseCoordinateY,
				fDraggingStartMouseCoordinateX, fDraggingStartMouseCoordinateY, fColor);

	}

	public void drawDraggedLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			float fMouseCoordinateX, float fMouseCoordinateY,
			float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY) {

		float fColor[] = { GrouperRenderStyle.TEXT_BG_COLOR[0],
				GrouperRenderStyle.TEXT_BG_COLOR[1], GrouperRenderStyle.TEXT_BG_COLOR[2],
				0.5f };
		drawDragged(gl, groupRepresentation, fMouseCoordinateX, fMouseCoordinateY,
				fDraggingStartMouseCoordinateX, fDraggingStartMouseCoordinateY, fColor);

	}

	private void drawDragged(GL2 gl, GroupRepresentation groupRepresentation,
			float fMouseCoordinateX, float fMouseCoordinateY,
			float fDraggingStartMouseCoordinateX, float fDraggingStartMouseCoordinateY,
			float fColor[]) {

		float fHeight = groupRepresentation.getHeight();
		float fWidth = groupRepresentation.getWidth();

		Vec3f vecRealGroupPosition = groupRepresentation.getDraggingStartPosition();
		Vec3f vecScaledRealGroupPosition = getScaledPosition(gl, vecRealGroupPosition,
				groupRepresentation.getHierarchyPosition());

		float fRealRelDraggingPosX = vecScaledRealGroupPosition.x()
				- fDraggingStartMouseCoordinateX;
		float fRealRelDraggingPosY = vecScaledRealGroupPosition.y()
				- fDraggingStartMouseCoordinateY;

		Vec3f vecPosition = new Vec3f(fMouseCoordinateX + fRealRelDraggingPosX,
				fMouseCoordinateY + fRealRelDraggingPosY, 0.2f);
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);

		gl.glColor4fv(fColor, 0);

		beginGUIElement(gl, vecPosition);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(vecPosition.x(), vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y(), vecPosition.z());
		gl.glVertex3f(vecPosition.x() + fWidth, vecPosition.y() - fHeight,
				vecPosition.z());
		gl.glVertex3f(vecPosition.x(), vecPosition.y() - fHeight, vecPosition.z());
		gl.glEnd();

		endGUIElement(gl);

		gl.glPopAttrib();
	}

	@Override
	public void drawAsLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {
		// Use drawDraggedLeaf Method

	}

}
