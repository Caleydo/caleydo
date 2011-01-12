package org.caleydo.view.grouper.drawingstrategies.group;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;

import com.jogamp.opengl.util.awt.TextRenderer;

public class GroupDrawingStrategySelection extends AGroupDrawingStrategyRectangular {

	private PickingManager pickingManager;
	private GrouperRenderStyle renderStyle;
	private int iViewID;

	public GroupDrawingStrategySelection(PickingManager pickingManager, int iViewID,
			GrouperRenderStyle renderStyle) {
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.renderStyle = renderStyle;
	}

	@Override
	public void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		gl.glPushName(pickingManager.getPickingID(iViewID,
				EPickingType.GROUPER_GROUP_SELECTION, groupRepresentation.getID()));
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		gl.glColor4fv(renderStyle.getGroupColorForLevel(groupRepresentation
				.getHierarchyLevel()), 0);

		drawGroupRectangular(gl, groupRepresentation, textRenderer);

		gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
		gl.glLineWidth(3.0f);

		drawRectangularBorder(gl, groupRepresentation, textRenderer);

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iViewID,
				EPickingType.GROUPER_COLLAPSE_BUTTON_SELECTION,
				groupRepresentation.getID()));

		drawCollapseButton(gl, groupRepresentation, textRenderer);

		gl.glPopName();
		gl.glPopAttrib();

		drawChildren(gl, groupRepresentation, textRenderer);
	}

	@Override
	public void drawAsLeaf(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		gl.glPushName(pickingManager.getPickingID(iViewID,
				EPickingType.GROUPER_GROUP_SELECTION, groupRepresentation.getID()));
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		gl.glColor4fv(GrouperRenderStyle.TEXT_BG_COLOR, 0);

		drawLeafRectangular(gl, groupRepresentation, textRenderer);

		gl.glLineWidth(3.0f);
		gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);

		drawRectangularBorder(gl, groupRepresentation, textRenderer);

		gl.glPopAttrib();

		gl.glPopName();

	}
}
