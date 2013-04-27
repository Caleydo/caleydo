/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.ViewManager;

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
				ViewManager.get().destroyView(brickColumn.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2(),
						brickColumn);
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
