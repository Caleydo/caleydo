package org.caleydo.core.data.selection;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.EIDType;

/**
 * Class that holds information about a selected element in a specific view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public class SelectedElementRep {

	/** viewID of the original view that contained the selection point */
	private int sourceViewID;

	/** viewID of the view the 3d-coordinates are transformed to */
	private int remoteViewID;

	private EIDType idType;

	// private float fXCoord = Float.NaN;
	// private float fYCoord = Float.NaN;

	/** list of points for this selection in the views 3d coordinate system */
	private ArrayList<Vec3f> points;

	/**
	 * Constructor. Use this constructor for a one point element rep
	 * 
	 * @param iDType
	 *            the type of the element to be connected.
	 * @param sourceViewID
	 *            the unique id of the view
	 * @param x coordinate in x direction
	 * @param y coordinate in y direction
	 * @param z coordinate in z direction
	 */
	public SelectedElementRep(EIDType idType, final int sourceViewID, final float x, final float y,
		final float z) {
		this(idType, sourceViewID, sourceViewID, x, y, z);
	}

	/**
	 * Constructor. Use this constructor for a one point element rep
	 * 
	 * @param iDType
	 *            the type of the element to be connected.
	 * @param sourceViewID
	 *            the unique id of the view that created the selection
	 * @param remoteViewID
	 *            the unique id of the view the coordinates are related to
	 * @param x
	 * @param y
	 * @param z
	 */
	public SelectedElementRep(EIDType idType, final int sourceViewID, int remoteViewID, final float x,
		final float y, final float z) {
		this.idType = idType;
		this.sourceViewID = sourceViewID;
		this.remoteViewID = remoteViewID;
		points = new ArrayList<Vec3f>();
		points.add(new Vec3f(x, y, z));
	}

	/**
	 * Constructor. Use this constructor for a list of points
	 * 
	 * @param containingViewID
	 *            the unique id of the view
	 * @param points3d
	 *            the list of connection points
	 */
	public SelectedElementRep(EIDType idType, final int sourceViewID, final ArrayList<Vec3f> points3d) {
		this(idType, sourceViewID, sourceViewID, points3d);
	}

	/**
	 * Constructor. Use this constructor for a list of points
	 * 
	 * @param containingViewID
	 *            the unique id of the view
	 * @param remoteViewID
	 *            the unique id of the view the coordinates are related to
	 * @param points3d
	 *            the list of connection points
	 */
	public SelectedElementRep(EIDType idType, final int sourceViewID, int remoteViewID,
		final ArrayList<Vec3f> points3d) {
		this.idType = idType;
		this.sourceViewID = sourceViewID;
		this.remoteViewID = remoteViewID;
		this.points = points3d;
	}

	/**
	 * Returns the type which the representation belongs to. Examples are gene expression values, expression
	 * experiments. See {@link EIDType} for details.
	 * 
	 * @return the type of the id
	 */
	public EIDType getIDType() {
		return idType;
	}

	/**
	 * Get the ID of the view that created the element rep
	 * 
	 * @return view-id
	 */
	public int getSourceViewID() {
		return sourceViewID;
	}

	/**
	 * Get the list of selection points in the view's 3d coordinate system
	 * 
	 * @return 3d point list of selection points
	 */
	public ArrayList<Vec3f> getPoints() {
		return points;
	}

	/**
	 * Get the ID of the view that the coordinates are related to
	 * 
	 * @return view-id
	 */
	public int getRemoteViewID() {
		return remoteViewID;
	}
}
