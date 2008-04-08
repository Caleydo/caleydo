package org.caleydo.core.data.view.rep.jgraph;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Class holds the data of a pathway image map.
 * A image map holds regions of the image and
 * a link that should followed when the user clicks in
 * the (rectangular) region.
 * 
 * @author	Michael Kalkusch
 * @author	Marc Streit
 */

public class PathwayImageMap {

	/**
	 * Path to the image file that is the basis of the 
	 * image map.
	 */
	protected String sImageLink;
	
	protected ArrayList<Rectangle> refRectArray;
	
	protected ArrayList<String> refImageLinkArray;

	/**
	 * Constructor
	 * 
	 * @param sLink
	 */
	public PathwayImageMap(String sImageLink) {
		
		refRectArray = new ArrayList<Rectangle>();
		refImageLinkArray = new ArrayList<String>();
		
		this.sImageLink = sImageLink;
	}
	
	
	public void addArea(Rectangle rect, String sImageLink) {
		
		refRectArray.add(rect);
		refImageLinkArray.add(sImageLink);
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
		
		for (int iAreaIndex = 0; iAreaIndex < refRectArray.size(); iAreaIndex++)
		{
			if (refRectArray.get(iAreaIndex).contains(point))
			{
				return refImageLinkArray.get(iAreaIndex);
			} 
		}
		
		return "";
	}
}
