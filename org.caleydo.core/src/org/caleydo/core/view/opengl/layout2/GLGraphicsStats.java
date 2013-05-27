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
package org.caleydo.core.view.opengl.layout2;

/**
 * simple statistics based on the {@link GLGraphics} usage
 *
 * @author Samuel Gratzl
 *
 */
public class GLGraphicsStats {
	private int numRects;
	private int numCircles;
	private int numRoundedRects;
	private int numChars;
	private boolean dirtyTextTexture = false;
	private int numVertices;
	private int numImages;

	/**
	 * @return the numRects, see {@link #numRects}
	 */
	public int getNumRects() {
		return numRects;
	}

	/**
	 * @return the numCircles, see {@link #numCircles}
	 */
	public int getNumCircles() {
		return numCircles;
	}

	/**
	 * @return the numRoundedRects, see {@link #numRoundedRects}
	 */
	public int getNumRoundedRects() {
		return numRoundedRects;
	}

	/**
	 * @return the numChars, see {@link #numChars}
	 */
	public int getNumChars() {
		return numChars;
	}

	/**
	 * @return the numVertices, see {@link #numVertices}
	 */
	public int getNumVertices() {
		return numVertices;
	}

	/**
	 * @return the numImages, see {@link #numImages}
	 */
	public int getNumImages() {
		return numImages;
	}

	/**
	 *
	 */
	public void incRect() {
		numRects++;
		numVertices += 4;
	}

	/**
	 *
	 */
	public void incImage() {
		numImages++;
		numVertices += 4;
	}

	/**
	 *
	 */
	public void incCircle(int segments) {
		numCircles++;
		numVertices += segments;
	}

	/**
	 *
	 */
	public void incLine() {
		numVertices += 2;
	}

	/**
	 * @param chars
	 *
	 */
	public void incText(int chars) {
		numChars += chars;
		numVertices += chars * 4; // assume a quad per char
	}

	/**
	 * @param dirtyTextTexture
	 *            setter, see {@link dirtyTextTexture}
	 */
	public void dirtyTextTexture() {
		this.dirtyTextTexture = true;
	}

	/**
	 * @return the dirtyTextTexture, see {@link #dirtyTextTexture}
	 */
	public boolean isDirtyTextTexture() {
		return dirtyTextTexture;
	}

	/**
	 * @param count
	 * 
	 */
	public void incPath(int count) {
		numVertices += count;
	}

	/**
	 * @param count
	 *
	 */
	public void incRoundedRect(int count) {
		numRoundedRects++;
		numVertices += count;
	}

}
