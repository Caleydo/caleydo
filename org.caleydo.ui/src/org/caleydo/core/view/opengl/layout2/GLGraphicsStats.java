/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
