/**
 * 
 */
package org.caleydo.view.visbricks;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * 
 * A BrickConnection captures the relation of two bricks which are connected
 * with a ribbon. The brick connection contains the ID of the ribbon, the bricks
 * which it connects and a {@link RecordVirtualArray} which holds all recordIDs
 * the two bricks share.
 * 
 * @author Alexander Lex
 * 
 */
public class BrickConnection {

	/**
	 * The id of the connection band that connects {@link #leftBrick} and
	 * {@link #rightBrick}
	 */
	private int connectionBandID;
	/** The brick at the left end of the connection band */
	private GLBrick leftBrick;
	/** The brick at the right end of the connection band */
	private GLBrick rightBrick;
	/**
	 * A virtual array that holds all elements the two bricks share. The
	 * {@link IDType} of the va is either the same as the IDTYpe of the brick's
	 * vas (if the two bricks have the same IDType) or of the primary mapping
	 * type of the associated IDCategory (see
	 * {@link IDCategory#getPrimaryMappingType()})
	 */
	private RecordVirtualArray sharedRecordVirtualArray;

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
	public void setSharedRecordVirtualArray(RecordVirtualArray sharedRecordVirtualArray) {
		this.sharedRecordVirtualArray = sharedRecordVirtualArray;
	}

	/**
	 * @return the sharedRecordVirtualArray, see
	 *         {@link #sharedRecordVirtualArray}
	 */
	public RecordVirtualArray getSharedRecordVirtualArray() {
		return sharedRecordVirtualArray;
	}

}
