package org.caleydo.core.data.view.rep.selection;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

/**
 * Class that holds information about a selected element in a specific view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class SelectedElementRep
{

	private int iContainingViewID;

	// private float fXCoord = Float.NaN;
	// private float fYCoord = Float.NaN;

	private ArrayList<Vec3f> alPoints;

	/**
	 * Constructor.
	 * 
	 * @param iContainingViewID
	 * @param fXCoord
	 * @param fYCoord
	 */
	public SelectedElementRep(final int iContainingViewID, final float fXCoord,
			final float fYCoord, final float fZCoord)
	{

		this.iContainingViewID = iContainingViewID;
		alPoints = new ArrayList<Vec3f>();
		alPoints.add(new Vec3f(fXCoord, fYCoord, fZCoord));
	}

	public SelectedElementRep(final int iContainingViewID, final ArrayList<Vec3f> alPoints)
	{

		this.iContainingViewID = iContainingViewID;
		this.alPoints = alPoints;
	}

	public int getContainingViewID()
	{

		return iContainingViewID;
	}

	public ArrayList<Vec3f> getPoints()
	{

		return alPoints;
	}
	// public float getXCoord() {
	// return fXCoord;
	// }
	//	
	// public float getYCoord() {
	// return fYCoord;
	// }
}
