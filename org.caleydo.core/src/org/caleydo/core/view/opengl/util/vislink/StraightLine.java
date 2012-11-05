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
package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;
import java.util.ArrayList;

/**
 * StraightLine fragments a line given by its source- and destination-point in a given number of segments and
 * calculates the vertices (line-points) joining this segments.
 * 
 * @author Oliver Pimas
 * @version 2009-10-24
 */

public class StraightLine {

	/** The source-point of the line */
	protected Vec3f srcPoint;
	/** The destination-point of the line */
	protected Vec3f dstPoint;

	/** The calculated vertices (line-points) specifying the line */
	protected ArrayList<Vec3f> linePoints;
	/** The number of segments the line is split into */
	protected int numberOfSegments;

	/**
	 * Constructor. Builds a line given by source- and destination point and calculates a given number of
	 * points (vertices) in-between.
	 * 
	 * @param srcPoint
	 *            Specifies the source point of the line
	 * @param dstPoint
	 *            Specifies the destination point of the line
	 * @param numberOfSegments
	 *            Specifies the number of segments the line is split into
	 */
	public StraightLine(Vec3f srcPoint, Vec3f dstPoint, int numberOfSegments) {
		this.srcPoint = srcPoint;
		this.dstPoint = dstPoint;
		this.numberOfSegments = numberOfSegments;

		ArrayList<Vec3f> linePoints = new ArrayList<Vec3f>();
		linePoints.add(srcPoint);
		this.linePoints = linePoints;
		calculateLinePoints();
		this.linePoints.add(dstPoint);
		// System.out.println("i=30: x=" + this.linePoints.get(30).x() + "   y=" + this.linePoints.get(30).y()
		// + "   z=" + this.linePoints.get(30).z());
	}

	/**
	 * Calculates a certain number of line-points (vertices) between source- and destination point.
	 */
	protected void calculateLinePoints() {

		Vec3f line = new Vec3f(dstPoint.minus(srcPoint));
		float length = line.length();

		float segmentLength = length / numberOfSegments;
		Vec3f lineSegment = new Vec3f(line);
		// lineSegment.scale(1/numberOfSegments);
		lineSegment.normalize();
		lineSegment.scale(segmentLength);

		// line.normalize();

		// System.out.println("segments = " + numberOfSegments + "   points = " + numberOfPoints +
		// "   length = " + length + "   lineSegment = " + lineSegment.length() +
		// "   length / numberOfSegments = " + segmentLength);
		// System.out.println("i=0: x=" + linePoints.get(0).x() + "   y=" + linePoints.get(0).y() + "   z=" +
		// linePoints.get(0).z());

		for (int i = 1; i < numberOfSegments; i++) {
			Vec3f srcToPoint = new Vec3f(lineSegment.times(i));
			Vec3f currentPoint = new Vec3f(srcPoint.plus(srcToPoint));
			// System.out.println("i=" + i + ": x=" + currentPoint.x() + "   y=" + currentPoint.y() + "   z="
			// + currentPoint.z());
			linePoints.add(checkLinePoint(currentPoint));
		}
	}

	/**
	 * Returns the list of line-points (vertices)
	 * 
	 * @return List of line-points
	 */
	public ArrayList<Vec3f> getLinePoints() {
		return linePoints;
	}

	/**
	 * Checks for invalid linePoints caused by rounding errors and corrects them.
	 * 
	 * @param point
	 *            The line-point to be checked
	 * @return The given line-point if it was valid or the corrected one if it was invalid.
	 */
	protected Vec3f checkLinePoint(Vec3f point) {
		/**
		 * The line points must be on the line segment between srcPoint and dstPoint. If a points coordinate
		 * is greater or smaller than both (src and dst), the point not on the line-segment between src and
		 * dst. This could happen because of rounding errors and very small line segments. If we find such a
		 * point, we cap it's out-of-bounds coordinate to the maximum, which is given by dstPoint since we
		 * calculate the points from srcPoint to dstPoint.
		 */
		if (((point.x() > srcPoint.x()) && (point.x() > dstPoint.x()))
			|| ((point.x() < srcPoint.x()) && (point.x() < dstPoint.x())))
			point.setX(dstPoint.x());
		if (((point.y() > srcPoint.y()) && (point.y() > dstPoint.y()))
			|| ((point.y() < srcPoint.y()) && (point.y() < dstPoint.y())))
			point.setY(dstPoint.y());
		if (((point.z() > srcPoint.z()) && (point.z() > dstPoint.z()))
			|| ((point.z() < srcPoint.z()) && (point.z() < dstPoint.z())))
			point.setZ(dstPoint.z());
		return point;
	}

}
