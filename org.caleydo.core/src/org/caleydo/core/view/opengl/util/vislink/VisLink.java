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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

/**
 * VisLink offers static and non-static methods to render a connection line.
 *
 * @author Oliver Pimas
 * @version 2009-10-21
 */

public class VisLink {

	private ArrayList<Vec3f> linePoints;

	/**
	 * Constructor (for straight lines)
	 *
	 * @param srcPoint
	 *            Specifies the source point
	 * @param dstPoint
	 *            Specifies the destination point
	 * @param numberOfSegments
	 *            Specifies the subintervals of the spline. Note that for n subintervals there are n+3 curve
	 *            points. The begin of the curve, the end of the curve and n+1 vertices connecting the n
	 *            segments.
	 */
	public VisLink(final Vec3f srcPoint, final Vec3f dstPoint, int numberOfSegments) {
		StraightLine line = new StraightLine(srcPoint, dstPoint, numberOfSegments);

		// this.width =
		// GeneralManager.get().getPreferenceStore().getFloat(PreferenceConstants.VISUAL_LINKS_WIDTH);
		// this.color = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR;
		// ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR;

		this.linePoints = line.getLinePoints();
	}

	/**
	 * Constructor
	 *
	 * @param controlPoints
	 *            Specifies the set of control points of which the spline is generated.
	 * @param offset
	 *            Specifies the offset in controlPoints
	 * @param numberOfSegments
	 *            Specifies the subintervals of the spline. Note that for n subintervals there are n+3 curve
	 *            points. The begin of the curve, the end of the curve and n+1 vertices connecting the n
	 *            segments.
	 * @throws IllegalArgumentException
	 *             if there are < 2 control points
	 */
	public VisLink(final ArrayList<Vec3f> controlPoints, final int offset, int numberOfSegments)
		throws IllegalArgumentException {

		ArrayList<Vec3f> points = new ArrayList<Vec3f>(controlPoints.subList(offset, controlPoints.size()));
		if (points.size() == (offset + 2)) {
			// this.linePoints = points;
			StraightLine line = new StraightLine(points.get(0), points.get(1), numberOfSegments);
			this.linePoints = line.getLinePoints();
		}
		else if (points.size() > (offset + 2)) {
			NURBSCurve curve = new NURBSCurve(points, numberOfSegments);
			this.linePoints = curve.getCurvePoints();
		}
		else
			throw new IllegalArgumentException("Need at least two points");
		// this.generalManager = GeneralManager.get();
	}

	/**
	 * Constructor
	 *
	 * @param controlPoints
	 *            Specifies the set of control points of which the spline is generated.
	 * @param offset
	 *            Specifies the offset in controlPoints
	 * @param segmentLength
	 *            Specifies the length of the splines subintervals. The number of subintervals is calculated
	 *            by the length of the line divided by the segmentLength. Note that for n subintervals there
	 *            are n+3 curve points. The begin of the curve, the end of the curve and n+1 vertices
	 *            connecting the n segments.
	 * @throws IllegalArgumentException
	 *             if there are < 2 control points
	 */
	public VisLink(final ArrayList<Vec3f> controlPoints, final int offset, float segmentLength)
		throws IllegalArgumentException {

		ArrayList<Vec3f> points = new ArrayList<Vec3f>(controlPoints.subList(offset, controlPoints.size()));
		if (points.size() < 2)
			throw new IllegalArgumentException("Need at least two points");

		int numberOfSegments = calculateNumberOfSegmentsByEuclideanDistance(points, segmentLength);

		if (points.size() == 2) {
			StraightLine line = new StraightLine(points.get(0), points.get(1), numberOfSegments);
			this.linePoints = line.getLinePoints();
		}
		else {
			NURBSCurve curve = new NURBSCurve(points, numberOfSegments);
			this.linePoints = curve.getCurvePoints();
		}

		// this.generalManager = GeneralManager.get();
	}

	/**
	 * Creates a visual link. Static method. When the number of control points is 2, this method renders a
	 * straight line. If the number of control points is greater then 2, a curved line (using NURBS) is
	 * rendered.
	 *
	 * @param gl
	 *            The GL2 object
	 * @param controlPoints
	 *            Set of control points for the NURBS spline
	 * @param numberOfSegments
	 *            Specifies the number of sub-intervals the spline is evaluated with (affects u in the Cox-de
	 *            Boor recursive formula when evaluating the spline) Note: For straight lines (only 2 control
	 *            points), this value doesn't effect the resulting line.
	 * @param shadow
	 *            Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * @throws IllegalArgumentException
	 *             if there are < 2 control points
	 */
	public static void renderLine(final GL2 gl, final ArrayList<Vec3f> controlPoints, final int offset,
		final int numberOfSegments, boolean shadow) throws IllegalArgumentException {
		if (controlPoints.size() == (offset + 2)) {
			line(gl, controlPoints.get(0), controlPoints.get(1), shadow);
		}
		else if (controlPoints.size() > (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			visLink.spline(gl, shadow);
		}
		else
			throw new IllegalArgumentException("Need at least two points to render a line!");
	}

	/**
	 * Creates a straight visual link. Static method.
	 *
	 * @param gl
	 *            The GL2 object
	 * @param srcPoint
	 *            The lines point of origin
	 * @param destPoint
	 *            The lines end point
	 * @param shadow
	 *            Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	public static void renderLine(final GL2 gl, Vec3f srcPoint, Vec3f destPoint, boolean shadow) {
		line(gl, srcPoint, destPoint, shadow);
	}

	/**
	 * Called for creating a curved visual link. This method is not recommended when drawing visual links with
	 * higher width, which would cause v-shaped gaps to appear at the intersections.
	 *
	 * @param gl
	 *            The GL2 object
	 * @param shadow
	 *            Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected void spline(final GL2 gl, boolean shadow) {
		// line shadow
		if (shadow == true) {
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2.0f);
			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < linePoints.size(); i++)
				gl.glVertex3f(linePoints.get(i).x(), linePoints.get(i).y(), linePoints.get(i).z());
			gl.glEnd();
		}

		// the spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);

		// the spline
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < linePoints.size(); i++)
			gl.glVertex3f(linePoints.get(i).x(), linePoints.get(i).y(), linePoints.get(i).z());
		gl.glEnd();
	}

	/**
	 * Called for creating a straight visual link.
	 *
	 * @param gl
	 *            The GL2 object
	 * @param vecSrcPoint
	 *            The lines point of origin
	 * @param vecDestPoint
	 *            The lines end point
	 * @param shadow
	 *            Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected static void line(final GL2 gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint, boolean shadow) {

		// Line shadow
		if (shadow == true) {
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2.0f);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
			gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
			gl.glEnd();
		}

		// the line attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);

		// the line
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
		gl.glEnd();
	}

	/**
	 * Generates vertices for a polygon line from a given set of curve points.
	 *
	 * @param gl
	 *            The GL2 Object
	 * @param curvePoints
	 *            Set of curve points
	 * @param width
	 *            The width of the resulting line
	 * @return A list of vertices (polygon line)
	 * @throws IllegalargumentException
	 *             if there are < 2 control points
	 */
	protected static ArrayList<Vec3f> generatePolygonVertices(final GL2 gl, ArrayList<Vec3f> points,
		float width) throws IllegalArgumentException {
		if (points.size() <= 0)
			throw new IllegalArgumentException("given set of curve points is empty");
		if (points.size() == 1)
			throw new IllegalArgumentException("at least two points are needed for rendering a line");

		ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();

		Vec3f dirVec = new Vec3f();
		Vec3f invDirVec = new Vec3f();

		for (int i = 0; i < points.size(); i++) {
			if (i == 0) {
				dirVec = directionVec(points.get(i), points.get(i), points.get(i + 1));
				invDirVec = invDirectionVec(points.get(i), points.get(i), points.get(i + 1));
			}
			else if (i == (points.size() - 1)) {
				dirVec = directionVec(points.get(i - 1), points.get(i), points.get(i));
				invDirVec = invDirectionVec(points.get(i - 1), points.get(i), points.get(i));
			}
			else {
				dirVec = directionVec(points.get(i - 1), points.get(i), points.get(i + 1));
				invDirVec = invDirectionVec(points.get(i - 1), points.get(i), points.get(i + 1));
			}

			dirVec.normalize();
			invDirVec.normalize();
			dirVec.scale(0.01f);
			invDirVec.scale(0.01f);

			vertices.add(points.get(i).addScaled(width, dirVec));
			vertices.add(points.get(i).addScaled(width, invDirVec));
		}
		return vertices;
	}

	/**
	 * Generates a vector which is from the viewpoint normal on the line.
	 *
	 * @param v1
	 *            point k-1
	 * @param v2
	 *            point k
	 * @param v3
	 *            point k+1
	 * @return a direction vector
	 */
	protected static Vec3f directionVec(Vec3f prevPoint, Vec3f point, Vec3f nextPoint) {
		Vec3f straight = nextPoint.minus(prevPoint);
		Vec3f eyepoint = new Vec3f(0.0f, 0.0f, 6.0f);
		Vec3f epVec = eyepoint.minus(point);
		return new Vec3f(straight.cross(epVec));
	}

	/**
	 * Generates a vector which is from the viewpoint normal on the line. This direction vector is inverse to
	 * the one returned by directionVec(...)
	 *
	 * @param v1
	 *            point k-1
	 * @param v2
	 *            point k
	 * @param v3
	 *            point k+1
	 * @return a direction vector inverse to the one returned by directionVec(...)
	 */
	protected static Vec3f invDirectionVec(Vec3f prevPoint, Vec3f point, Vec3f nextPoint) {
		Vec3f straight = nextPoint.minus(prevPoint);
		Vec3f eyepoint = new Vec3f(0.0f, 0.0f, 6.0f);
		Vec3f epVec = eyepoint.minus(point);
		return new Vec3f(epVec.cross(straight));
	}

	/**
	 * Checks if the given array is a valid rgba value. If the values are < 0 or > 1 they are capped to this
	 * limits. If the array contains less then 4 values, IllegalArgumentException is thrown. If the array
	 * contains more then 4 values, the values beyond the 4th are ignored.
	 *
	 * @param rgba
	 *            Specifies the array to be checked
	 * @throws IllegalArgumentException
	 *             If there are less then 4 values
	 */
	protected void checkRGBA(float[] rgba) throws IllegalArgumentException {
		if (rgba.length < 4)
			throw new IllegalArgumentException("given color-parameter is no valid rgba color-value");
		for (int i = 0; i < 4; i++) {
			if (rgba[i] < 0f)
				rgba[i] = 0f;
			if (rgba[i] > 1f)
				rgba[i] = 1f;
		}
	}

	/**
	 * Returns the number of line-segments
	 *
	 * @return The number of line-segments
	 */
	public int numberOfSegments() {
		/** n segments have n+1 vertices */
		return this.linePoints.size() - 1;
	}

	/**
	 * Returns the number of line-points
	 *
	 * @return The number of line-points
	 */
	public int numberOfLinePoints() {
		return this.linePoints.size();
	}

	/**
	 * Renders a polygon-line with the given attributes
	 *
	 * @param gl
	 *            The GL-object
	 * @param controlPoints
	 *            A set of control points specifying the line/curve
	 * @param offset
	 *            Specifies the offset of the given parameter controlPoints
	 * @param numberOfSegments
	 *            Specifies the number of line-segments
	 * @param width
	 *            Specifies the width of the line
	 * @param color
	 *            Specifies the lines color (rgba)
	 * @param style
	 *            Specifies the lines style
	 * @param antiAliasingQuality
	 *            Specifies the anti-aliasing quality of the rendered line
	 * @throws IllegalArgumentException
	 *             If the number of given control points is < 2 (at least 2 points are needed to specify a
	 *             line)
	 */
	public static void renderPolygonLine(final GL2 gl, final ArrayList<Vec3f> controlPoints, int offset,
		int numberOfSegments, float width, float[] color, EVisLinkStyleType style, int antiAliasingQuality)
		throws IllegalArgumentException {
		if (controlPoints.size() >= (offset + 2)) { // fixme: special case if == 2
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			if (style == EVisLinkStyleType.SHADOW_VISLINK) {
				visLink.drawPolygonLine(gl,
					(width * ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_WIDTH_FACTOR),
					ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, antiAliasingQuality, false,
					false, false);
			}
			else if (style == EVisLinkStyleType.HALO_VISLINK) {
				float[] haloColor = { color[0], color[1], color[2], (color[3] / 2.0f) };
				visLink.drawPolygonLine(gl,
					(width * ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH_FACTOR), haloColor,
					antiAliasingQuality, false, false, true);
			}
			visLink.drawPolygonLine(gl, width, color, antiAliasingQuality, false, false, false);

		}
		else
			throw new IllegalArgumentException("Need at least two points to render a line!");
	}

	/**
	 * Draws a polygon line with the given attributes
	 *
	 * @param gl
	 *            The GL-object
	 * @param width
	 *            Specifies the width of the line
	 * @param color
	 *            Specifies the lines color (rgba)
	 * @param antiAliasingQuality
	 *            Specifies the anti-aliasing quality of the rendered line (1 <= quality <= 20)
	 * @param roundedStart
	 *            Toggles rounded beginning of the line
	 * @param roundedEnd
	 *            Toggles rounded ending of the line
	 * @param halo
	 *            Set this to true if you want to draw a halo (alters the way alpha changes during iterations)
	 * @throws IllegalArgumentException
	 *             If the specified anti-aliasing quality is <1 or >20
	 */
	protected void drawPolygonLine(final GL2 gl, float width, float[] color, int antiAliasingQuality,
		boolean roundedStart, boolean roundedEnd, boolean halo) throws IllegalArgumentException {
		try {
			checkRGBA(color);
		}
		catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}

		// antiAliasingQuality shouldn't be < 1 or > 20
		if (antiAliasingQuality < 1 || antiAliasingQuality > 20)
			throw new IllegalArgumentException("parameter antiAliasingQuality has to be >=1 and <= 20");

		float red = color[0];
		float green = color[1];
		float blue = color[2];
		float alpha = color[3];
		float alphaChange = 0;
		if (halo)
			alphaChange = alpha / antiAliasingQuality;
		else
			alphaChange = (alpha + 0.2f) / antiAliasingQuality;
		float unit = width / antiAliasingQuality / 2.5f;
		width = width - (((antiAliasingQuality - 1) * unit) / 2);

		for (int j = 1; j <= antiAliasingQuality; j++) {
			// The spline attributes
			gl.glColor4fv(new float[] { red, green, blue, alpha }, 0);

			ArrayList<Vec3f> vertices = generatePolygonVertices(gl, linePoints, width);

			// the spline
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int i = 0; i < vertices.size(); i++) {
				gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
			}
			gl.glEnd();

			// rounded start
			if (roundedStart) {
				Vec3f startPoint = linePoints.get(0);
				drawRoundedEndSegment(gl, startPoint, width);
			}

			// rounded end
			if (roundedEnd) {
				int endPointIndex = linePoints.size() - 1;
				Vec3f endPoint = linePoints.get(endPointIndex);
				drawRoundedEndSegment(gl, endPoint, width);
			}

			alpha -= alphaChange; // fade out at borders for anti aliasing effect
			width += unit;
		}
	}

	/**
	 * Draws a point to smooth the start and end of the line
	 *
	 * @param gl
	 *            The GL-object
	 * @param point
	 *            The coords where the point should be drawn (start or end of the line)
	 * @param width
	 *            The width of the line where the point should fit
	 */
	void drawRoundedEndSegment(GL2 gl, Vec3f point, float width) {
		gl.glPointSize(width * 3); // FIXME: width calculation to be corrected
		// gl.glPointSize( (calculateEuclideanDistance(vertices.get(0), vertices.get(1))) * 100);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3f(point.x(), point.y(), point.z());
		gl.glEnd();
	}

	/**
	 * Draws the first n segments of a polygon line with the given attributes
	 *
	 * @param gl
	 *            The GL-object
	 * @param width
	 *            Specifies the width of the line
	 * @param color
	 *            Specifies the lines color (rgba)
	 * @param antiAliasingQuality
	 *            Specifies the anti-aliasing quality of the rendered line (1 <= quality <= 20)
	 * @param segmentsToDraw
	 *            Specifies the number of segments to be drawn (=n)
	 * @param roundedStart
	 *            Toggles rounded beginning of the line
	 * @param roundedEnd
	 *            Toggles rounded ending of the line
	 * @param halo
	 *            Set this to true if you want to draw a halo (alters the way alpha changes during iterations)
	 * @throws IllegalArgumentException
	 *             If the specified anti-aliasing quality is <1 or >20
	 */
	protected void drawPolygonLine(final GL2 gl, float width, float[] color, int antiAliasingQuality,
		int segmentsToDraw, boolean roundedStart, boolean roundedEnd, boolean halo)
		throws IllegalArgumentException {
		try {
			checkRGBA(color);
		}
		catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}

		// antiAliasingQuality shouldn't be < 1 or > 20
		if (antiAliasingQuality < 1 || antiAliasingQuality > 20)
			throw new IllegalArgumentException("parameter antiAliasingQuality has to be >= 1 and <= 20");

		if (segmentsToDraw >= linePoints.size())
			throw new IllegalArgumentException(
				"Specified parameter 'segmentsToDraw' too high (not enough curve-segments)");

		float red = color[0];
		float green = color[1];
		float blue = color[2];
		float alpha = color[3];
		float alphaChange = 0;
		if (halo)
			alphaChange = alpha / antiAliasingQuality;
		else
			alphaChange = (alpha + 0.2f) / antiAliasingQuality;
		float unit = width / antiAliasingQuality / 2.5f;
		width = width - (((antiAliasingQuality - 1) * unit) / 2);
		int limit = (segmentsToDraw + 1) * 2; // n segments have n+1 vertices, *2 because of polygons

		for (int j = 1; j <= antiAliasingQuality; j++) {
			// The spline attributes
			gl.glColor4fv(new float[] { red, green, blue, alpha }, 0);

			ArrayList<Vec3f> vertices = generatePolygonVertices(gl, linePoints, width);

			if (limit > vertices.size())
				throw new IllegalArgumentException(
					"Specified parameter 'segmentsToDraw' too high (not enough curve-segments)");

			// the spline
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int i = 0; i < limit; i++) {
				gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
			}
			gl.glEnd();

			// rounded start
			if (roundedStart) {
				Vec3f startPoint = linePoints.get(0);
				drawRoundedEndSegment(gl, startPoint, width);
			}

			// rounded end
			if (roundedEnd) {
				Vec3f endPoint = linePoints.get(segmentsToDraw);
				drawRoundedEndSegment(gl, endPoint, width);
			}

			alpha -= alphaChange; // fade out at borders for anti aliasing effect
			width += unit;
		}
	}

	/**
	 * Draws the first n segments of a polygon line with the given attributes in inverse direction (so it
	 * actually draws the last n segments)
	 *
	 * @param gl
	 *            The GL-object
	 * @param width
	 *            Specifies the width of the line
	 * @param color
	 *            Specifies the lines color (rgba)
	 * @param antiAliasingQuality
	 *            Specifies the anti-aliasing quality of the rendered line (1 <= quality <= 20)
	 * @param segmentsToDraw
	 *            Specifies the number of segments to be drawn (=n)
	 * @param roundedStart
	 *            Toggles rounded beginning of the line
	 * @param roundedEnd
	 *            Toggles rounded ending of the line
	 * @param halo
	 *            Set this to true if you want to draw a halo (alters the way alpha changes during iterations)
	 * @throws IllegalArgumentException
	 *             If the specified anti-aliasing quality is <1 or >20
	 */
	protected void drawPolygonLineReverse(final GL2 gl, float width, float[] color, int antiAliasingQuality,
		int segmentsToDraw, boolean roundedStart, boolean roundedEnd, boolean halo)
		throws IllegalArgumentException {
		try {
			checkRGBA(color);
		}
		catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}

		// antiAliasingQuality shouldn't be < 1 or > 20
		if (antiAliasingQuality < 1 || antiAliasingQuality > 20)
			throw new IllegalArgumentException("parameter antiAliasingQuality has to be >= 1 and <= 20");

		float red = color[0];
		float green = color[1];
		float blue = color[2];
		float alpha = color[3];
		float alphaChange = 0;
		if (halo)
			alphaChange = alpha / antiAliasingQuality;
		else
			alphaChange = (alpha + 0.2f) / antiAliasingQuality;
		float unit = width / antiAliasingQuality / 2.5f;
		width = width - (((antiAliasingQuality - 1) * unit) / 2);
		int limit = (segmentsToDraw + 1) * 2; // n segments have n+1 vertices, *2 because of polygons

		for (int j = 1; j <= antiAliasingQuality; j++) {
			// The spline attributes
			gl.glColor4fv(new float[] { red, green, blue, alpha }, 0);

			ArrayList<Vec3f> vertices = generatePolygonVertices(gl, linePoints, width);

			if (limit > vertices.size())
				throw new IllegalArgumentException(
					"Specified parameter 'segmentsToDraw' too high (not enough curve-segments)");

			// the spline
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int i = (vertices.size() - 1); i >= (vertices.size() - limit); i--) {
				gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
			}
			gl.glEnd();

			// rounded start
			if (roundedStart) {
				int startPointIndex = linePoints.size() - 1;
				Vec3f startPoint = linePoints.get(startPointIndex);
				drawRoundedEndSegment(gl, startPoint, width);
			}

			// rounded end
			if (roundedEnd) {
				int endPointIndex = linePoints.size() - segmentsToDraw - 1;
				Vec3f endPoint = linePoints.get(endPointIndex);
				drawRoundedEndSegment(gl, endPoint, width);

			}

			alpha -= alphaChange; // fade out at borders for anti aliasing effect
			width += unit;
		}
	}

	/**
	 * Calculates the euclidean distance between two points
	 *
	 * @param srcPoint
	 *            The source point
	 * @param dstPoint
	 *            The destination point
	 * @return The euclidean distance
	 */
	protected float calculateEuclideanDistance(Vec3f srcPoint, Vec3f dstPoint) {
		float distance = 0.0f;

		distance = dstPoint.minus(srcPoint).length();
		// System.out.println(distance);
		// System.out.println("calculateEclideanDistance(): distance=" + distance);
		return distance;
	}

	protected int calculateNumberOfSegmentsByEuclideanDistance(ArrayList<Vec3f> controlPoints,
		float segmentLength) throws IllegalArgumentException {
		int numberOfSegments = 0;
		float distance = 0.0f;
		if (controlPoints.size() < 2)
			throw new IllegalArgumentException("parameter controlPoints has not enough points (must be >= 2)");
		for (int i = 1; i < controlPoints.size(); i++) {
			distance += calculateEuclideanDistance(controlPoints.get(i - 1), controlPoints.get(i));
		}
		numberOfSegments = (int) (distance / segmentLength);
		// System.out.println("number of segments = " + numberOfSegments + "   (distance = " + distance +
		// " )");
		return numberOfSegments;
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// NOTE: outdated stuff
	// ---------------------------------------------------------------------------------------------------------------------

	// /**
	// * Creates a polygon visual link.
	// * When the number of control points is 2, this method renders a straight line.
	// * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	// *
	// * @param gl the GL2 object
	// * @param controlPoints the control points for the NURBS spline
	// * @param offset specifies the offset of control points
	// * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	// * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	// * Note: For straight lines (only 2 control points), this value doesn't effect the resulting line.
	// * @param shadow turns shadow on/off
	// * @param antiAliasing turns AA on/off
	// *
	// * @throws IllegalArgumentException if there are < 2 control points
	// */
	// public static void renderPolygonLine(final GL2 gl, final ArrayList<Vec3f> controlPoints, final int
	// offset, final int numberOfSegments, boolean shadow, boolean antiAliasing)
	// throws IllegalArgumentException
	// {
	// if(controlPoints.size() >= (offset + 2)) {
	// VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
	// if(antiAliasing == true)
	// visLink.polygonLineAA(gl, shadow);
	// else
	// visLink.polygonLine(gl, shadow);
	//
	// }
	// else
	// throw new IllegalArgumentException( "Need at least two points to render a line!" );
	// }
	//
	//
	// /**
	// * Creates a straight polygon visual link.
	// *
	// * @param gl the GL2 object
	// * @param srcPoint the lines point of origin
	// * @param destPoint the lines end point
	// * @param shadow turns shadow on/off
	// * @param antiAliasing turns AA on/off
	// */
	// public static void renderPolygonLine(final GL2 gl, Vec3f srcPoint, Vec3f destPoint, boolean shadow,
	// boolean antiAliasing) {
	// VisLink visLink = new VisLink(srcPoint, destPoint);
	// if(antiAliasing == true)
	// visLink.polygonLineAA(gl, shadow);
	// else
	// visLink.polygonLine(gl, shadow);
	// }
	//
	//
	// /**
	// * Renders a polygon line. Recommended for lines with higher width.
	// *
	// * @param gl the GL2 object
	// * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	// */
	// protected void polygonLine(final GL2 gl, boolean shadow) {
	//
	// if(shadow == true)
	// drawPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR,
	// (ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH * 1.5f), 1);
	//
	// drawPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR,
	// ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH, 1);
	// }
	//
	//
	// /**
	// * Renders a polygon line with AA. Recommended for lines with higher width.
	// *
	// * @param gl The GL2 object
	// * @param shadow Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	// */
	// protected void polygonLineAA(final GL2 gl, boolean shadow) {
	// if(shadow == true) {
	// drawPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR,
	// (ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH * 1.5f),
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// }
	//
	// drawPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR,
	// ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// }

	// /**
	// * Draws the line (AA-Quality can be specified)
	// *
	// * @param gl The GL2 object
	// * @param RGBA Specifies the color of the line
	// * @param width Specifies the width of the line
	// * @param quality Specifies the AA-Quality of the line. Higher value is better quality but costs
	// performance.
	// */
	// protected void drawPolygonLine(final GL2 gl, float[] RGBA, float width, int quality) {
	//
	// try {
	// checkRGBA(RGBA);
	// } catch(IllegalArgumentException iae) {throw iae;}
	//
	// float red = RGBA[0];
	// float green = RGBA[1];
	// float blue = RGBA[2];
	// float alpha = RGBA[3];
	// float alphaChange = alpha / quality;
	// float unit = width / quality;
	// float lineWidth = width - (((quality - 1) * unit) / 2);
	//
	// ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();
	//
	// for(int j = 1; j <= quality; j++) {
	// // The spline attributes
	// gl.glColor4fv(new float[]{red, green, blue, alpha}, 0);
	//
	// vertices.clear();
	// vertices = generatePolygonVertices(gl, linePoints, lineWidth);
	//
	// // the spline
	// gl.glBegin(GL2.GL_QUAD_STRIP);
	// for(int i = 0; i < vertices.size(); i++) {
	// gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
	// }
	// gl.glEnd();
	//
	// alpha -= alphaChange;
	// lineWidth += unit;
	// }
	// }
	//
	//
	// /**
	// * Draws the first [segments] segments of the line (AA-Quality can be specified)
	// *
	// * @param gl The GL2 object
	// * @param RGBA Specifies the color of the line
	// * @param width Specifies the width of the line
	// * @param quality Specifies the AA-Quality of the line. Higher value is better quality but costs
	// performance.
	// * @param drawSegments Specifies the first n segments to be drawn
	// *
	// * @return true if the whole line has been drawn, false if it's only partly drawn
	// *
	// * @throws IllegalArgumentException if drawSegments exceeds the number of line-segments
	// */
	// public boolean drawPolygonLineBySegments(final GL2 gl, float[] RGBA, float width, int quality, long
	// drawSegments)
	// throws IllegalArgumentException
	// {
	//
	// try {
	// checkRGBA(RGBA);
	// } catch(IllegalArgumentException iae) {throw iae;}
	//
	// quality = (quality < 1) ? 1 : quality; // line should be drawn at least 1 time
	//
	// float red = RGBA[0];
	// float green = RGBA[1];
	// float blue = RGBA[2];
	// float alpha = RGBA[3];
	// float alphaChange = alpha / quality;
	// float unit = width / quality;
	// float lineWidth = width - (((quality - 1) * unit) / 2);
	// long limit = (drawSegments + 1) * 2; // n segments have n+1 vertices
	//
	// ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();
	//
	// for(int j = 1; j <= quality; j++) {
	// // The spline attributes
	// gl.glColor4fv(new float[]{red, green, blue, alpha}, 0);
	//
	// vertices.clear();
	// vertices = generatePolygonVertices(gl, linePoints, lineWidth);
	//
	// if(limit > vertices.size()) {
	// limit = vertices.size();
	// throw new
	// IllegalArgumentException("Parameter segments is higher than the number of curve-segments. Capped it to max.");
	// }
	//
	// // the spline
	// gl.glBegin(GL2.GL_QUAD_STRIP);
	// for(int i = 0; i < limit; i++) {
	// gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
	// }
	// gl.glEnd();
	//
	// alpha -= alphaChange;
	// lineWidth += unit;
	// }
	// return (limit >= vertices.size()) ? true : false;
	// }
	//
	//
	// /**
	// * Draws the last [segments] segments of the line (AA-Quality can be specified)
	// *
	// * @param gl The GL2 object
	// * @param RGBA Specifies the color of the line
	// * @param width Specifies the width of the line
	// * @param quality Specifies the AA-Quality of the line. Higher value is better quality but costs
	// performance.
	// * @param drawSegments Specifies the last n segments to be drawn
	// *
	// * @return true if the whole line has been drawn, false if it's only partly drawn
	// *
	// * @throws IllegalArgumentException if drawSegments exceeds the number of line-segments
	// */
	// public boolean drawPolygonLineBySegmentsReverse(final GL2 gl, float[] RGBA, float width, int quality,
	// long drawSegments)
	// throws IllegalArgumentException
	// {
	//
	// try {
	// checkRGBA(RGBA);
	// } catch(IllegalArgumentException iae) {throw iae;}
	//
	// float red = RGBA[0];
	// float green = RGBA[1];
	// float blue = RGBA[2];
	// float alpha = RGBA[3];
	// float alphaChange = alpha / quality;
	// float unit = width / quality;
	// float lineWidth = width - (((quality - 1) * unit) / 2);
	// long limit = (drawSegments + 1) * 2; // n segments have n+1 vertices
	//
	// ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();
	//
	// for(int j = 1; j <= quality; j++) {
	// // The spline attributes
	// gl.glColor4fv(new float[]{red, green, blue, alpha}, 0);
	//
	// vertices.clear();
	// vertices = generatePolygonVertices(gl, linePoints, lineWidth);
	//
	// if(limit > vertices.size()) {
	// limit = vertices.size();
	// throw new
	// IllegalArgumentException("Parameter segments is higher than the number of curve-segments. Capped it to max.");
	// }
	//
	// // the spline
	// gl.glBegin(GL2.GL_QUAD_STRIP);
	// for(int i = (vertices.size() - 1); i >= (vertices.size() - limit); i--) {
	// gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
	// }
	// gl.glEnd();
	//
	// alpha -= alphaChange;
	// lineWidth += unit;
	// }
	// return (limit >= vertices.size()) ? true : false;
	// }

	// public void renderPolygonLine(final GL2 gl, float width, float[] color, EVisLinkStyleType style, int
	// antiAliasingQuality, long segmentsToDraw) {
	// if(style == EVisLinkStyleType.SHADOW_VISLINK) {
	// drawPolygonLine(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// drawPolygonLine(gl, (width * ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_WIDTH_FACTOR),
	// ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, antiAliasingQuality, segmentsToDraw);
	// // drawPolygonLine(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// }
	// else if(style == EVisLinkStyleType.HALO_VISLINK) {
	// float[] haloColor = {color[0], color[1], color[2], (color[3] / 2.0f) };
	// drawPolygonLine(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// drawPolygonLine(gl, (width * ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH_FACTOR), haloColor,
	// antiAliasingQuality, segmentsToDraw);
	// // drawPolygonLine(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// }
	// else
	// drawPolygonLine(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// }
	//
	// public void renderPolygonLineReverse(final GL2 gl, float width, float[] color, EVisLinkStyleType style,
	// int antiAliasingQuality, long segmentsToDraw) {
	// if(style == EVisLinkStyleType.SHADOW_VISLINK) {
	// drawPolygonLineReverse(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// drawPolygonLineReverse(gl, (width * ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_WIDTH_FACTOR),
	// ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, antiAliasingQuality, segmentsToDraw);
	// // drawPolygonLineReverse(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// }
	// else if(style == EVisLinkStyleType.HALO_VISLINK) {
	// float[] haloColor = {color[0], color[1], color[2], (color[3] / 2.0f) };
	// drawPolygonLineReverse(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// drawPolygonLineReverse(gl, (width * ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH_FACTOR),
	// haloColor, antiAliasingQuality, segmentsToDraw);
	// // drawPolygonLineReverse(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// }
	// else
	// drawPolygonLineReverse(gl, width, color, antiAliasingQuality, segmentsToDraw);
	// }

	//
	// /**
	// * Renders the polygon line w/o further options
	// *
	// * @param gl the GL2 object
	// * @param controlPoints the control points for the NURBS spline
	// * @param offset specifies the offset of control points
	// *
	// * @throws IllegalArgumentException if there are < 2 control points
	// */
	// public static void renderPolygonLine(final GL2 gl, final ArrayList<Vec3f> controlPoints, final int
	// offset, float width)
	// throws IllegalArgumentException
	// {
	// if(controlPoints.size() >= (offset + 2)) {
	// VisLink visLink = new VisLink(controlPoints, offset, 10);
	// visLink.polygonLine(gl, width);
	// }
	// else
	// throw new IllegalArgumentException( "Need at least two points to render a line!" );
	// }
	//
	// /**
	// * Renders the halo of the polygon line
	// *
	// * @param gl the GL2 object
	// * @param controlPoints the control points for the NURBS spline
	// * @param offset specifies the offset of control points
	// *
	// * @throws IllegalArgumentException if there are < 2 control points
	// */
	// public static void renderPolygonLineHalo(final GL2 gl, final ArrayList<Vec3f> controlPoints, final int
	// offset)
	// throws IllegalArgumentException
	// {
	// if(controlPoints.size() >= (offset + 2)) {
	// VisLink visLink = new VisLink(controlPoints, offset, 10);
	// visLink.polygonLineHalo(gl);
	// }
	// else
	// throw new IllegalArgumentException( "Need at least two points to render a line!" );
	// }
	//
	// /**
	// * This method is called by renderPolygonLineHalo
	// * @param gl the GL2 Object
	// */
	// protected void polygonLineHalo(final GL2 gl) {
	// float[] halo = new float[4];
	// halo[0] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[0];
	// halo[1] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[1];
	// halo[2] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[2];
	// halo[3] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[3] / 2f;
	//
	// float halo_width = ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH * 3;
	//
	// drawPolygonLine(gl, halo, halo_width, 5);
	// }
	//
	// /**
	// * This method is called by renderPolygonLine
	// * @param gl the GL2 Object
	// */
	// protected void polygonLine(final GL2 gl, float width) {
	// drawPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, width,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// }
	//
	//
	// protected void polygonLineWithHaloTexture(final GL2 gl) {
	//
	// ArrayList<Vec3f> polygonLineVertices = generatePolygonVertices(gl, linePoints,
	// ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
	//
	// // The spline attributes
	// gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
	//
	// Texture texture = null;
	// texture = generalManager.getResourceLoader().getTexture(new
	// String("resources/vislinktextures/glowTextureYellow.png"));
	// texture.enable();
	// texture.bind();
	//
	// gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
	// // gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
	// // gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);
	// // gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
	//
	// // gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	// // gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
	// TextureCoords texCoords = texture.getImageTexCoords();
	//
	// // the spline
	// gl.glBegin(GL2.GL_QUAD_STRIP);
	// for(int i = 0; i < polygonLineVertices.size(); i++) {
	// gl.glVertex3f(polygonLineVertices.get(i).x(), polygonLineVertices.get(i).y(),
	// polygonLineVertices.get(i).z());
	// if(i%2 == 0)
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// else
	// gl.glTexCoord2f( texCoords.right(), texCoords.bottom());
	// }
	// gl.glEnd();
	//
	// texture.disable();
	// }

	// protected void polygonLineWithHalo(final GL2 gl) {
	// try {
	// float[] halo = new float[4];
	// halo[0] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[0];
	// halo[1] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[1];
	// halo[2] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[2];
	// halo[3] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[3] / 2;
	//
	// // int[] curBlendFunc = new int[5];
	// // gl.glGetIntegerv(GL.GL_BLEND_SRC_RGB, curBlendFunc, 0);
	// // gl.glGetIntegerv(GL.GL_BLEND_DST_RGB, curBlendFunc, 1);
	// // gl.glGetIntegerv(GL.GL_BLEND_SRC_ALPHA, curBlendFunc, 2);
	// // gl.glGetIntegerv(GL.GL_BLEND_DST_ALPHA, curBlendFunc, 3);
	// // gl.glGetIntegerv(GL.GL_BLEND_EQUATION, curBlendFunc, 4);
	//
	// // System.out.println(curBlendFunc[0] + "  " + curBlendFunc[1] + "  " + curBlendFunc[2] + "  " +
	// curBlendFunc[3]);
	//
	// // gl.glBlendFunc(GL.GL_SRC_ALPHA_SATURATE, GL.GL_ONE);
	// // gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	// // gl.glBlendColor(halo[0], halo[1], halo[2], halo[3]);
	// // gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_SRC_ALPHA,
	// GL.GL_ONE_MINUS_SRC_ALPHA);
	// // gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_SRC_ALPHA,
	// GL.GL_ONE_MINUS_SRC_ALPHA); // standard
	//
	// // gl.glLogicOp(GL2.GL_SET);
	// // gl.glBlendEquation(GL2.GL_LOGIC_OP);
	// // gl.glBlendEquationSeparate(GL2.GL_MAX, GL2.GL_LOGIC_OP);
	//
	// drawPolygonLine(gl, halo, 4f, 5);
	// drawPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 1f,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	//
	// // gl.glBlendFunc(curBlendFunc[0], curBlendFunc[1]);
	// // gl.glBlendFuncSeparate(curBlendFunc[0], curBlendFunc[1], curBlendFunc[2], curBlendFunc[3]);
	// // gl.glBlendEquation(curBlendFunc[4]);
	// } catch(IllegalArgumentException iae) {
	// iae.printStackTrace();
	// }
	// }

	// protected void polygonLineWithHalo(final GL2 gl) {
	//
	// this.blurTexture[0] = createBlurTexture(gl);
	// this.frameBufferObject[0] = createFrameBufferObject(gl);
	//
	// if (frameBufferObject[0] == -1) {
	// System.err.println("error: couldn't create framebuffer object needed for halo. rendering normal polygon line instead.");
	// polygonLine(gl, false);
	// return;
	// }
	//
	// IntBuffer viewportBuffer = BufferUtil.newIntBuffer(4);
	//
	// // store current viewport
	// gl.glGetIntegerv(GL2.GL_VIEWPORT, viewportBuffer);
	// this.viewportX = viewportBuffer.get(0);
	// this.viewportY = viewportBuffer.get(1);
	// this.viewportWidth = viewportBuffer.get(2);
	// this.viewportHeight = viewportBuffer.get(3);
	//
	// // for(int i = 0; i < viewportBuffer.capacity(); i++)
	// // System.out.print(viewportBuffer.get(i) + "  ");
	// // System.out.println();
	//
	//
	// // line with blur effect
	// renderToTexture(gl);
	// // restore default FB
	// gl.glBindFramebufferEXT(GL2.GL_FRAMEBUFFER_EXT, 0);
	// // restore the viewport
	// gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
	// polygonLine(gl, false);
	// drawBlur(gl, 25, 0.02f);
	//
	// // delete the Buffers
	// gl.glDeleteTextures(1, blurTexture, 0);
	// gl.glDeleteFramebuffersEXT(1, frameBufferObject, 0);
	// gl.glDeleteTextures(1, colorBuff, 0);
	// gl.glDeleteRenderbuffersEXT(1, depthBuff, 0);
	// }
	//
	//
	// private int createBlurTexture(GL2 gl) { // Create An Empty Texture
	//
	// ByteBuffer data = BufferUtil.newByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE); // Create Dimension Space For
	// Texture Data (128x128x4)
	// data.limit(data.capacity());
	//
	// int[] txtnumber = new int[1];
	// gl.glGenTextures(1, txtnumber, 0); // Create 1 Texture
	// gl.glBindTexture(GL.GL_TEXTURE_2D, txtnumber[0]); // Bind The Texture
	// gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, TEXTURE_SIZE, TEXTURE_SIZE, 0,
	// GL2.GL_LUMINANCE,
	// GL2.GL_UNSIGNED_BYTE, data); // Build Texture Using Information In data
	// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	//
	// return txtnumber[0]; // Return The Texture ID
	// }
	//
	// private void viewOrtho(GL2 gl) // Set Up An Ortho View
	// {
	// gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // Select Projection
	// gl.glPushMatrix(); // Push The Matrix
	// gl.glLoadIdentity(); // Reset The Matrix
	// gl.glOrtho(0, viewportWidth, viewportHeight, 0, -1, 1); // Select Ortho Mode (640x480)
	// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW); // Select Modelview Matrix
	// gl.glPushMatrix(); // Push The Matrix
	// gl.glLoadIdentity(); // Reset The Matrix
	// }
	//
	// private void viewPerspective(GL2 gl) // Set Up A Perspective View
	// {
	// gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // Select Projection
	// gl.glPopMatrix(); // Pop The Matrix
	// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW); // Select Modelview
	// gl.glPopMatrix(); // Pop The Matrix
	// }
	//
	// private void renderToTexture(GL2 gl) {
	//
	// // if FBO couldn't be created, we can't render to texture and therefore can't use halo
	// if (frameBufferObject[0] == -1) {
	// System.err.println("error: couldn't create framebuffer object needed for halo.");
	// return;
	// }
	//
	// gl.glBindFramebufferEXT(GL2.GL_FRAMEBUFFER_EXT, frameBufferObject[0]);
	//
	// // Set Our Viewport (Match Texture Size)
	// gl.glViewport(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);
	//
	// gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
	// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	//
	// polygonLine(gl, false);
	//
	// // Copy Our ViewPort To The Blur Texture (From 0,0 To 128,128... No Border)
	// gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]);
	// gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_LUMINANCE, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0);
	// }
	//
	//
	// private void drawBlur(GL2 gl, int times, float inc) {
	//
	// // if fbo couldn't be created, we can't render to texture and therefore can't use halo
	// if (frameBufferObject[0] == -1) {
	// System.err.println("error: couldn't create framebuffer object needed for halo.");
	// return;
	// }
	//
	// float spost = 0.0f; // Starting Texture Coordinate Offset
	// float alpha = 0.2f; // Starting Alpha Value
	//
	// // Disable AutoTexture Coordinates
	// // gl.glDisable(GL.GL_TEXTURE_GEN_S);
	// // gl.glDisable(GL.GL_TEXTURE_GEN_T);
	//
	// gl.glEnable(GL.GL_TEXTURE_2D); // Enable 2D Texture Mapping
	// gl.glDisable(GL.GL_DEPTH_TEST); // Disable Depth Testing
	// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE); // Set Blending Mode
	// gl.glEnable(GL.GL_BLEND); // Enable Blending
	// gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]); // Bind To The Blur Texture
	// viewOrtho(gl); // Switch To An Ortho View
	//
	// float alphainc = alpha / times; // alphainc=0.2f / Times To Render Blur
	//
	// gl.glBegin(GL2.GL_QUADS); // Begin Drawing Quads
	// for (int num = 0; num < times; num++) // Number Of Times To Render Blur
	// {
	// gl.glColor4f(1.0f, 1.0f, 1.0f, alpha); // Set The Alpha Value (Starts At 0.2)
	// gl.glTexCoord2f(0 + spost, 1 - spost); // Texture Coordinate ( 0, 1 )
	// gl.glVertex2f(0, 0); // First Vertex ( 0, 0 )
	//
	// gl.glTexCoord2f(0 + spost, 0 + spost); // Texture Coordinate ( 0, 0 )
	// gl.glVertex2f(0, viewportHeight); // Second Vertex ( 0, 480 )
	//
	// gl.glTexCoord2f(1 - spost, 0 + spost); // Texture Coordinate ( 1, 0 )
	// gl.glVertex2f(viewportWidth, viewportHeight); // Third Vertex ( 640, 480 )
	//
	// gl.glTexCoord2f(1 - spost, 1 - spost); // Texture Coordinate ( 1, 1 )
	// gl.glVertex2f(viewportWidth, 0); // Fourth Vertex ( 640, 0 )
	//
	// spost += inc; // Gradually Increase spost (Zooming Closer To Texture Center)
	// alpha = alpha - alphainc; // Gradually Decrease alpha (Gradually Fading Image Out)
	// }
	// gl.glEnd(); // Done Drawing Quads
	//
	// viewPerspective(gl); // Switch To A Perspective View
	//
	// gl.glEnable(GL.GL_DEPTH_TEST); // Enable Depth Testing
	// gl.glDisable(GL.GL_TEXTURE_2D);
	// gl.glDisable(GL.GL_BLEND); // Disable Blending
	// gl.glBindTexture(GL.GL_TEXTURE_2D, 0); // Unbind The Blur Texture
	// }
	//
	//
	// /**
	// * Creates a frame buffer object.
	// * @return the newly created frame buffer object is or -1 if a frame buffer object could not be created
	// */
	// private int createFrameBufferObject(GL2 gl) {
	// // Create the FBO
	// int[] frameBuffer = new int[1];
	// gl.glGenFramebuffersEXT(1, frameBuffer, 0);
	// gl.glBindFramebufferEXT(GL2.GL_FRAMEBUFFER_EXT, frameBuffer[0]);
	//
	// // Create a TEXTURE_SIZE x TEXTURE_SIZE RGBA texture that will be used as color attachment
	// // for the fbo.
	// int[] colorBuffer = new int[1];
	// gl.glGenTextures(1, colorBuffer, 0); // Create 1 Texture
	// gl.glBindTexture(GL.GL_TEXTURE_2D, colorBuffer[0]); // Bind The Texture
	// gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA,TEXTURE_SIZE, TEXTURE_SIZE,0, GL.GL_RGBA,
	// GL2.GL_UNSIGNED_BYTE, BufferUtil.newByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE * 4));
	// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	//
	// // Attach the texture to the frame buffer as the color attachment. This
	// // will cause the results of rendering to the FBO to be written in the blur texture.
	// gl.glFramebufferTexture2DEXT(GL2.GL_FRAMEBUFFER_EXT, GL2.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D,
	// colorBuffer[0], 0);
	//
	// gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	//
	// // Create a 24-bit TEXTURE_SIZE x TEXTURE_SIZE depth buffer for the FBO.
	// // We need this to get correct rendering results.
	// int[] depthBuffer = new int[1];
	// gl.glGenRenderbuffersEXT(1, depthBuffer, 0);
	// gl.glBindRenderbufferEXT(GL2.GL_RENDERBUFFER_EXT, depthBuffer[0]);
	// gl.glRenderbufferDimensionEXT(GL2.GL_RENDERBUFFER_EXT, GL2.GL_DEPTH_COMPONENT24, TEXTURE_SIZE,
	// TEXTURE_SIZE);
	//
	// // Attach the newly created depth buffer to the FBO.
	// gl.glFramebufferRenderbufferEXT(GL2.GL_FRAMEBUFFER_EXT, GL2.GL_DEPTH_ATTACHMENT_EXT,
	// GL2.GL_RENDERBUFFER_EXT, depthBuffer[0]);
	//
	// // Make sure the framebuffer object is complete (i.e. set up correctly)
	// int status = gl.glCheckFramebufferStatusEXT(GL2.GL_FRAMEBUFFER_EXT);
	// if (status == GL2.GL_FRAMEBUFFER_COMPLETE_EXT) {
	// colorBuff = colorBuffer;
	// depthBuff = depthBuffer;
	// return frameBuffer[0];
	// } else {
	// // No matter what goes wrong, we simply delete the frame buffer object
	// // This switch statement simply serves to list all possible error codes
	// switch(status) {
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
	// // One of the attachments is incomplete
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
	// // Not all attachments have the same size
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
	// // The desired read buffer has no attachment
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
	// // The desired draw buffer has no attachment
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
	// // Not all color attachments have the same internal format
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
	// // No attachments have been attached
	// case GL2.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
	// // The combination of internal formats is not supported
	// case GL2.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
	// // This value is no longer in the EXT_framebuffer_object specification
	// default:
	// // Delete the color buffer texture
	// gl.glDeleteTextures(1, colorBuffer, 0);
	// // gl.glDeleteTextures(1, blurTexture, 0);
	// // Delete the depth buffer
	// gl.glDeleteRenderbuffersEXT(1, depthBuffer, 0);
	// // Delete the FBO
	// gl.glDeleteFramebuffersEXT(1, frameBuffer, 0);
	// return -1;
	// }
	// }
	// }

}
