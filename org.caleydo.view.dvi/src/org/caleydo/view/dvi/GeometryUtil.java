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
package org.caleydo.view.dvi;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Utility class for geometry related calculations.
 * 
 * @author Christian
 * 
 */
public class GeometryUtil
{
	/**
	 * Calculates the intersection point of the line between point1 and point2
	 * and the specified rectangle that is closest to point1.
	 * 
	 * @param point1
	 * @param point2
	 * @param rect
	 * @return The intersection point or null, if the line does not intersect the rectangle.
	 */
	public static Point2D calcIntersectionPoint(Point2D point1, Point2D point2,
			Rectangle2D rect)
	{
		
		int code1 = rect.outcode(point1);
		int code2 = rect.outcode(point2);
		
		if ((code1 & code2) != 0) {
			return null;
		}

		double k = 0;

		if (point1.getX() != point2.getX())
		{
			k = (point2.getY() - point1.getY()) / (point2.getX() - point1.getX());
		}

		if ((code1 & Rectangle2D.OUT_LEFT) != 0)
		{
			double y = point1.getY() + ((rect.getMinX() - point1.getX()) * k);

			if (y <= rect.getMaxY() && y >= rect.getMinY())
				return new Point2D.Double(rect.getMinX(), y);
		}

		if ((code1 & Rectangle2D.OUT_RIGHT) != 0)
		{
			double y = point1.getY() + ((rect.getMaxX() - point1.getX()) * k);
			if (y <= rect.getMaxY() && y >= rect.getMinY())
				return new Point2D.Double(rect.getMaxX(), y);
		}

		if ((code1 & Rectangle2D.OUT_TOP) != 0)
		{
			double x = point1.getX();
			if (k != 0)
			{
				x += (rect.getMinY() - point1.getY()) / k;
			}

			if (x <= rect.getMaxX() && x >= rect.getMinX())
				return new Point2D.Double(x, rect.getMinY());
		}

		if ((code1 & Rectangle2D.OUT_BOTTOM) != 0)
		{
			double x = point1.getX();
			if (k != 0)
			{
				x += (rect.getMaxY() - point1.getY()) / k;
			}

			if (x <= rect.getMaxX() && x >= rect.getMinX())
				return new Point2D.Double(x, rect.getMaxY());
		}

		return null;
	}
}
