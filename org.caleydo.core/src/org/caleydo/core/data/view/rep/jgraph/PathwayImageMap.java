package org.caleydo.core.data.view.rep.jgraph;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class holds the data of a pathway image map.
 * A image map holds regions of the image and
 * a link that should followed when the user clicks in
 * the (rectangular) region.
 * 
 * @author	Marc Streit
 */

public class PathwayImageMap 
implements Serializable 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Path to the image file that is the basis of the 
	 * image map.
	 */
	protected String sImageLink;
	
	protected ArrayList<Rectangle> rectArray;
	
	protected ArrayList<String> imageLinkArray;

	/**
	 * Constructor.
	 * 
	 * @param sLink
	 */
	public PathwayImageMap(final String sImageLink) {
		
		rectArray = new ArrayList<Rectangle>();
		imageLinkArray = new ArrayList<String>();
		
		this.sImageLink = sImageLink;
	}
	
	
	public void addArea(Rectangle rect, String sImageLink) {
		
		rectArray.add(rect);
		imageLinkArray.add(sImageLink);
	}
	
	public String getImageLink() {
	
		return sImageLink;
	}
	
	public void setImageLink(final String setImageLink) {
		
		sImageLink = setImageLink;		
	}


	/**
	 * Method takes a point and looks if the point is
	 * contained in one of the areas.
	 * If a containing area is found the corresponding
	 * link is returned.
	 * 
	 * @param point Point to check for intersection.
	 * @return Link that should be followed.
	 */
	public String processPoint(Point point) {
		
		for (int iAreaIndex = 0; iAreaIndex < rectArray.size(); iAreaIndex++)
		{
			if (rectArray.get(iAreaIndex).contains(point))
			{
				return imageLinkArray.get(iAreaIndex);
			} 
		}
		
		return "";
	}
}
