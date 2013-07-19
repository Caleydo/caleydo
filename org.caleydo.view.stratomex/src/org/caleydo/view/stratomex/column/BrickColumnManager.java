/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.perspective.table.TablePerspective;

public class BrickColumnManager {

	private ArrayList<BrickColumn> brickColumns = new ArrayList<BrickColumn>(20);

	private HashMap<Integer, BrickColumnSpacingRenderer> brickColumnSpacers = new HashMap<Integer, BrickColumnSpacingRenderer>();

	/**
	 * The index of the first column in the center referring to {@link #brickColumns}
	 */
	private int centerColumnStartIndex = 0;

	/**
	 * The index of the first column in the right leg of the arc, referring to {@link #brickColumns}
	 */
	private int rightColumnStartIndex = 0;

	public ArrayList<BrickColumn> getBrickColumns() {
		return brickColumns;
	}

	public BrickColumn getActiveBrickColumn() {
		for (BrickColumn c : brickColumns)
			if (c.isActive())
				return c;
		return null;
	}

	/**
	 * Returns the first brick column that contains the given table perspective. If not brick column is found, null is
	 * returned.
	 *
	 * @param tablePerspective
	 *            for which the brick column will be returned
	 */
	public BrickColumn getBrickColumn(TablePerspective tablePerspective) {

		for (BrickColumn brickColumn : brickColumns) {
			if (brickColumn.getTablePerspective() == tablePerspective)
				return brickColumn;
		}

		return null;
	}

	public HashMap<Integer, BrickColumnSpacingRenderer> getBrickColumnSpacers() {
		return brickColumnSpacers;
	}

	public int getRightColumnStartIndex() {
		return rightColumnStartIndex;
	}

	public int getCenterColumnStartIndex() {
		return centerColumnStartIndex;
	}

	public void setRightColumnStartIndex(int rightGroupStartIndex) {
		this.rightColumnStartIndex = rightGroupStartIndex;
	}

	public void setCenterColumnStartIndex(int centerGroupStartIndex) {
		this.centerColumnStartIndex = centerGroupStartIndex;
	}

	// public void calculateGroupDivision() {
	// if (brickColumns.size() > MAX_CENTER_BRICK_COLUMNS) {
	// centerColumnStartIndex = (brickColumns.size() - MAX_CENTER_BRICK_COLUMNS)
	// / 2;
	// rightColumnStartIndex = centerColumnStartIndex +
	// MAX_CENTER_BRICK_COLUMNS;
	// }
	// else {
	// centerColumnStartIndex = 0;
	// rightColumnStartIndex = brickColumns.size();
	// }
	// }

	public int indexOfBrickColumn(BrickColumn brickColumn) {
		return brickColumns.indexOf(brickColumn);
	}

	public void moveBrickColumn(BrickColumn brickColumn, int newPosIndex) {

		brickColumns.remove(indexOfBrickColumn(brickColumn));
		brickColumns.add(newPosIndex, brickColumn);
	}

	public void removeBrickColumn(int tablePerspectiveID) {
		Iterator<BrickColumn> brickColumnIterator = brickColumns.iterator();

		int count = 0;
		while (brickColumnIterator.hasNext()) {
			BrickColumn brickColumn = brickColumnIterator.next();
			if (brickColumn.getTablePerspective().getID() == tablePerspectiveID) {
				// ViewManager.get().unregisterGLView(brickColumn);
				brickColumn.destroyView();
				brickColumnIterator.remove();
				if (count < centerColumnStartIndex) {
					centerColumnStartIndex--;
				}
				if (count < rightColumnStartIndex) {
					rightColumnStartIndex--;
				}
			} else {
				count++;
			}
		}
	}
}
