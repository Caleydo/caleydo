package org.caleydo.core.data.selection;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import org.caleydo.core.data.mapping.EIDType;

/**
 * Class that holds information about a selected element in a specific view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class SelectedElementRep
{
	private int iContainingViewID;

	private EIDType idType;

	// private float fXCoord = Float.NaN;
	// private float fYCoord = Float.NaN;

	private ArrayList<Vec3f> alPoints;

	/**
	 * Constructor. Use this constructor for a one point element rep
	 * 
	 * @param idType the type of the element to be connected. 
	 * @param iContainingViewID the unique id of the view
	 * @param fXCoord
	 * @param fYCoord
	 */
	public SelectedElementRep(EIDType idType, final int iContainingViewID,
			final float fXCoord, final float fYCoord, final float fZCoord)
	{

		this.idType = idType;
		this.iContainingViewID = iContainingViewID;
		alPoints = new ArrayList<Vec3f>();
		alPoints.add(new Vec3f(fXCoord, fYCoord, fZCoord));
	}

	/**
	 * Constructor. Use this constructor for a list of points
	 * 
	 * @param iContainingViewID the unique id of the view
	 * @param alPoints the list of connection points
	 */
	public SelectedElementRep(EIDType idType, final int iContainingViewID,
			final ArrayList<Vec3f> alPoints)
	{

		this.idType = idType;
		this.iContainingViewID = iContainingViewID;
		this.alPoints = alPoints;
	}

	/**
	 * Returns the type which the representation belongs to. Examples are gene
	 * expression values, expression experiments. See {@link EIDType} for
	 * details.
	 * 
	 * @return the type of the id
	 */
	public EIDType getIDType()
	{
		return idType;
	}

	/**
	 * Get the ID of the view that created the element rep
	 * 
	 * @return the id
	 */
	public int getContainingViewID()
	{
		return iContainingViewID;
	}

	/**
	 * Get the point list
	 * 
	 * @return
	 */
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
