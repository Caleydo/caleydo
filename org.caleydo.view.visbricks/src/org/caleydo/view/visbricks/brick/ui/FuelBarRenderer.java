package org.caleydo.view.visbricks.brick.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Renderer for a fuel bar. 
 * 
 * @author Christian Partl
 *
 */
public class FuelBarRenderer extends LayoutRenderer {

	private GLBrick brick;
	private SelectionManager selectionManager;
	private DataTable dataTable;

	public FuelBarRenderer(GLBrick brick, DataTable dataTable) {
		this.brick = brick;
		this.dataTable = dataTable;
		selectionManager = brick.getRecordGroupSelectionManager();
	}

	@Override
	public void render(GL2 gl) {

//		DataTable set = brick.getDataTable();
		RecordVirtualArray recordVA = brick.getRecordVA();

		if (dataTable == null || recordVA == null)
			return;

		RecordVirtualArray setRecordVA = dataTable.getRecordData(DataTable.RECORD).getRecordVA();

		if (setRecordVA == null)
			return;

		int totalNumElements = setRecordVA.size();

		int currentNumElements = recordVA.size();

		float fuelWidth = (float) x / totalNumElements * currentNumElements;

		gl.glPushName(brick.getPickingManager().getPickingID(brick.getID(),
				PickingType.BRICK, brick.getID()));

		gl.glBegin(GL2.GL_QUADS);
		
		
		
//		if (selectionManager.checkStatus(SelectionType.SELECTION, brick.getGroup()
//				.getID()))
//			gl.glColor4fv(SelectionType.SELECTION.getColor(),0);
//		else
			gl.glColor3f(0.3f, 0.3f, 0.3f);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);

		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fuelWidth, 0, 0);
		if (selectionManager.checkStatus(SelectionType.SELECTION, brick.getGroup()
				.getID())) {
			float[] baseColor = SelectionType.SELECTION.getColor();
			
			gl.glColor3f(baseColor[0] + 0.3f, baseColor[1] + 0.3f, baseColor[2] + 0.3f);
		}
		else
			gl.glColor3f(1f, 1f, 1f);
		gl.glVertex3f(fuelWidth, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();
		gl.glPopName();

	}
}
