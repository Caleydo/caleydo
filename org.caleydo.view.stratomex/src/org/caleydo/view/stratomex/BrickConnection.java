/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
/**
 *
 */
package org.caleydo.view.stratomex;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 *
 * A BrickConnection captures the relation of two bricks which are connected with a ribbon. The brick connection
 * contains the ID of the ribbon, the bricks which it connects and a {@link RecordVirtualArray} which holds all
 * recordIDs the two bricks share.
 *
 * @author Alexander Lex
 *
 */
public class BrickConnection {

	/**
	 * The id of the connection band that connects {@link #leftBrick} and {@link #rightBrick}
	 */
	private int connectionBandID;
	/** The brick at the left end of the connection band */
	private GLBrick leftBrick;
	/** The brick at the right end of the connection band */
	private GLBrick rightBrick;
	/**
	 * A virtual array that holds all elements the two bricks share. The {@link IDType} of the va is either the same as
	 * the IDTYpe of the brick's vas (if the two bricks have the same IDType) or of the primary mapping type of the
	 * associated IDCategory (see {@link IDCategory#getPrimaryMappingType()})
	 */
	private VirtualArray sharedRecordVirtualArray;

	/**
	 * @param connectionBandID
	 *            setter, see {@link #connectionBandID}
	 */
	public void setConnectionBandID(int connectionBandID) {
		this.connectionBandID = connectionBandID;
	}

	/**
	 * @return the connectionBandID, see {@link #connectionBandID}
	 */
	public int getConnectionBandID() {
		return connectionBandID;
	}

	/**
	 * @param leftBrick
	 *            setter, see {@link #leftBrick}
	 */
	public void setLeftBrick(GLBrick leftBrick) {
		this.leftBrick = leftBrick;
	}

	/**
	 * @return the leftBrick, see {@link #leftBrick}
	 */
	public GLBrick getLeftBrick() {
		return leftBrick;
	}

	/**
	 * @param rightBrick
	 *            setter, see {@link #rightBrick}
	 */
	public void setRightBrick(GLBrick rightBrick) {
		this.rightBrick = rightBrick;
	}

	/**
	 * @return the rightBrick, see {@link #rightBrick}
	 */
	public GLBrick getRightBrick() {
		return rightBrick;
	}

	/**
	 * @param sharedRecordVirtualArray
	 *            setter, see {@link #sharedRecordVirtualArray}
	 */
	public void setSharedRecordVirtualArray(VirtualArray sharedRecordVirtualArray) {
		this.sharedRecordVirtualArray = sharedRecordVirtualArray;
	}

	/**
	 * @return the sharedRecordVirtualArray, see {@link #sharedRecordVirtualArray}
	 */
	public VirtualArray getSharedRecordVirtualArray() {
		return sharedRecordVirtualArray;
	}

}
