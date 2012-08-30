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

	private final static int MAX_CENTER_BRICK_COLUMNS = 4;

	private ArrayList<BrickColumn> brickColumns = new ArrayList<BrickColumn>(20);;

	private HashMap<Integer, BrickColumnSpacingRenderer> brickColumnSpacers = new HashMap<Integer, BrickColumnSpacingRenderer>();

	private int centerGroupStartIndex = 0;

	private int rightGroupStartIndex = 0;

	public ArrayList<BrickColumn> getBrickColumns() {
		return brickColumns;
	}

	/**
	 * Returns the first brick column that contains the given table perspective.
	 * If not brick column is found, null is returned.
	 * 
	 * @param tablePerspective for which the brick column will be returned
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

	public int getRightGroupStartIndex() {
		return rightGroupStartIndex;
	}

	public int getCenterGroupStartIndex() {
		return centerGroupStartIndex;
	}

	public void setRightGroupStartIndex(int rightGroupStartIndex) {
		this.rightGroupStartIndex = rightGroupStartIndex;
	}

	public void setCenterGroupStartIndex(int centerGroupStartIndex) {
		this.centerGroupStartIndex = centerGroupStartIndex;
	}

	public void calculateGroupDivision() {
		if (brickColumns.size() > MAX_CENTER_BRICK_COLUMNS) {
			centerGroupStartIndex = (brickColumns.size() - MAX_CENTER_BRICK_COLUMNS) / 2;
			rightGroupStartIndex = centerGroupStartIndex + MAX_CENTER_BRICK_COLUMNS;
		}
		else {
			centerGroupStartIndex = 0;
			rightGroupStartIndex = brickColumns.size();
		}
	}

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
				ViewManager.get().unregisterGLView(brickColumn);
				brickColumnIterator.remove();
				if (count < centerGroupStartIndex) {
					centerGroupStartIndex--;
				}
				if (count < rightGroupStartIndex) {
					rightGroupStartIndex--;
				}
			}
			count++;
		}
	}
}
