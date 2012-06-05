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
/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Base class for all attribute renderers that need to be rendered at a position
 * relative to the total length of the connection line.
 * 
 * @author Christian
 * 
 */
public abstract class ARelativeLinePositionRenderer implements
		IConnectionLineAttributeRenderer {

	protected PixelGLConverter pixelGLConverter;

	/**
	 * Value between 0 and 1 that determines the relative position of the
	 * attribute to be rendered for the connection line. The start of the line
	 * is 0 and the end 1.
	 */
	protected float linePositionProportion = 0.5f;
	

	/**
	 * Constructor.
	 * 
	 * @param linePositionProportion
	 *            see {@link #linePositionProportion}
	 */
	public ARelativeLinePositionRenderer(float linePositionProportion,
			PixelGLConverter pixelGLConverter) {
		this.linePositionProportion = linePositionProportion;
		this.pixelGLConverter = pixelGLConverter;
	}

	@Override
	public void render(GL2 gl, List<Vec3f> linePoints) {

		// Calculate list of cumulative distances
		float[] distanceList = new float[linePoints.size()];
		distanceList[0] = 0;
		for (int i = 0; i < linePoints.size() - 1; i++) {
			Vec3f point1 = linePoints.get(i);
			Vec3f point2 = linePoints.get(i + 1);
			distanceList[i + 1] = distanceList[i] + point2.minus(point1).length();
		}

		float desiredDistance = distanceList[distanceList.length - 1]
				* linePositionProportion;

		// Binary search
		int minIndex = 0;
		int maxIndex = distanceList.length - 1;

		while (maxIndex - minIndex > 1) {
			int currentIndex = (int) Math.floor((double) (maxIndex + minIndex) / 2.0);

			if (distanceList[currentIndex] < desiredDistance) {
				minIndex = currentIndex;
			} else {
				maxIndex = currentIndex;
			}
		}

		// Calculate final Point
		float distanceToGo = desiredDistance - distanceList[minIndex];
		float pointDistance = Math.abs(distanceList[minIndex] - distanceList[maxIndex]);
		float scalingFactor = distanceToGo / pointDistance;

		Vec3f basePoint = linePoints.get(minIndex);
		Vec3f direction = linePoints.get(maxIndex).minus(basePoint);
		direction.scale(scalingFactor);
		Vec3f relativePositionOnLine = new Vec3f(basePoint.x() + direction.x(),
				basePoint.y() + direction.y(), basePoint.z());

		render(gl, linePoints, relativePositionOnLine, basePoint,
				linePoints.get(maxIndex));

	}

	/**
	 * Rendering method for subclasses.
	 * 
	 * @param gl
	 * @param linePoints
	 * @param relativePositionOnLine
	 *            Point on the line where the attribute is supposed to be
	 *            rendered.
	 * @param enclosingPoint1
	 *            Line point before relativePositionOnLine.
	 * @param enclosingPoint2
	 *            Line point after relativePositionOnLine.
	 * 
	 */
	protected abstract void render(GL2 gl, List<Vec3f> linePoints,
			Vec3f relativePositionOnLine, Vec3f enclosingPoint1, Vec3f enclosingPoint2);

	/**
	 * @return the linePositionProportion, see {@link #linePositionProportion}
	 */
	public float getLinePositionProportion() {
		return linePositionProportion;
	}

	/**
	 * @param linePositionProportion
	 *            setter, see {@link #linePositionProportion}
	 */
	public void setLinePositionProportion(float linePositionProportion) {
		this.linePositionProportion = linePositionProportion;
	}

}
