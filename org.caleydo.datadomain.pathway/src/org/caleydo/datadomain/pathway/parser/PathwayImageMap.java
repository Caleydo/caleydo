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
package org.caleydo.datadomain.pathway.parser;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Class holds the data of a pathway image map. A image map holds regions of the
 * image and a link that should followed when the user clicks in the
 * (rectangular) region.
 * 
 * @author Marc Streit
 */

public class PathwayImageMap {
	/**
	 * Path to the image file that is the basis of the image map.
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
	 * Method takes a point and looks if the point is contained in one of the
	 * areas. If a containing area is found the corresponding link is returned.
	 * 
	 * @param point
	 *            Point to check for intersection.
	 * @return Link that should be followed.
	 */
	public String processPoint(Point point) {

		for (int iAreaIndex = 0; iAreaIndex < rectArray.size(); iAreaIndex++) {
			if (rectArray.get(iAreaIndex).contains(point))
				return imageLinkArray.get(iAreaIndex);
		}

		return "";
	}
}
