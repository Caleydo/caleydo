package org.caleydo.core.view.opengl.canvas.grouper;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;

import com.sun.opengl.util.j2d.TextRenderer;

public class GroupDrawingStrategyMouseOver
	extends AGroupDrawingStrategyRectangular {

	private PickingManager pickingManager;
	private GrouperRenderStyle renderStyle;
	private int iViewID;

	public GroupDrawingStrategyMouseOver(PickingManager pickingManager, int iViewID,
		GrouperRenderStyle renderStyle) {
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.renderStyle = renderStyle;
	}

	@Override
	public void draw(GL gl, GroupRepresentation groupRepresentation, TextRenderer textRenderer) {

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GROUPER_GROUP_SELECTION,
			groupRepresentation.getID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT | GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glColor4fv(renderStyle.getGroupColorForLevel(groupRepresentation.getHierarchyLevel()), 0);

		drawGroupRectangular(gl, groupRepresentation, textRenderer);
		
		gl.glColor4fv(GrouperRenderStyle.MOUSE_OVER_COLOR, 0);
		gl.glLineWidth(3.0f);

		drawRectangularBorder(gl, groupRepresentation, textRenderer);

		gl.glPopAttrib();

		gl.glPopName();

		drawChildren(gl, groupRepresentation, textRenderer);

	}

}
