/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

/**
 * helper class for defining the position and size of a cell in one dimension
 * 
 * @author Samuel Gratzl
 * 
 */
public final class CellSpace {
	private final float position;
	private final float size;

	/**
	 * @param position
	 * @param size
	 */
	public CellSpace(float position, float size) {
		this.position = position;
		this.size = size;
	}

	/**
	 * @return the position, see {@link #position}
	 */
	public float getPosition() {
		return position;
	}

	/**
	 * @return the size, see {@link #size}
	 */
	public float getSize() {
		return size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(position);
		result = prime * result + Float.floatToIntBits(size);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CellSpace other = (CellSpace) obj;
		if (Float.floatToIntBits(position) != Float.floatToIntBits(other.position))
			return false;
		if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CellSpace [position=");
		builder.append(position);
		builder.append(", size=");
		builder.append(size);
		builder.append("]");
		return builder.toString();
	}

}
