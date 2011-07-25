package org.caleydo.view.grouper.drawingstrategies.group;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.dimension.NominalDimension;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;

import com.jogamp.opengl.util.awt.TextRenderer;

public class GroupDrawingStrategyNormal extends AGroupDrawingStrategyRectangular {

	private PickingManager pickingManager;
	private GrouperRenderStyle renderStyle;
	private int iViewID;

	public GroupDrawingStrategyNormal(PickingManager pickingManager, int iViewID,
			GrouperRenderStyle renderStyle) {
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.renderStyle = renderStyle;
	}

	@Override
	public void draw(GL2 gl, GroupRepresentation groupRepresentation,
			TextRenderer textRenderer) {

		gl.glPushName(pickingManager.getPickingID(iViewID,
				PickingType.GROUPER_GROUP_SELECTION, groupRepresentation.getID()));
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		gl.glColor4fv(renderStyle.getGroupColorForLevel(groupRepresentation
				.getHierarchyLevel()), 0);

		// gl.glColor4f(0.74f, 0.11f, 0.18f, 0.2f);

		drawGroupRectangular(gl, groupRepresentation, textRenderer);

		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iViewID,
				PickingType.GROUPER_COLLAPSE_BUTTON_SELECTION,
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
				PickingType.GROUPER_GROUP_SELECTION, groupRepresentation.getID()));
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		// gl.glColor4fv(GrouperRenderStyle.TEXT_BG_COLOR, 0);

		DataTable set = groupRepresentation.getClusterNode().getMetaSet();
		DimensionVirtualArray storageVA = set.getStorageData(DataTable.DIMENSION).getStorageVA();

		boolean isNominal = false;
		boolean isNumerical = false;
		for (Integer storageID : storageVA) {

			if (set.get(storageID) instanceof NominalDimension<?>) {
				gl.glColor4f(116f / 255f, 196f / 255f, 118f / 255f, 1f);
				isNominal = true;
			} else {
				gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
				isNumerical = true;
			}
		}
		if (isNominal && isNumerical) {
			gl.glColor4f(1f, 0.6f, 0.6f, 1f);
		}

		drawLeafRectangular(gl, groupRepresentation, textRenderer);

		gl.glPopAttrib();

		gl.glPopName();

	}
}
