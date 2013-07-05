/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
