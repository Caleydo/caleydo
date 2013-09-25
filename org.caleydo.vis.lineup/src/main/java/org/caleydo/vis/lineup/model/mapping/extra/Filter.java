/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.vis.lineup.model.mapping.extra;

/**
 * @author Samuel Gratzl
 *
 */
public class Filter {
	private double raw_min = Double.NaN;
	private double raw_max = Double.NaN;
	private double normalized_min = 0;
	private double normalized_max = 1;


	public double getRaw_min() {
		return raw_min;
	}

	public void setRaw_min(double raw_min) {
		this.raw_min = raw_min;
	}

	public double getRaw_max() {
		return raw_max;
	}

	public void setRaw_max(double raw_max) {
		this.raw_max = raw_max;
	}

	public double getNormalized_min() {
		return normalized_min;
	}

	public void setNormalized_min(double normalized_min) {
		this.normalized_min = normalized_min;
	}

	public double getNormalized_max() {
		return normalized_max;
	}

	public void setNormalized_max(double normalized_max) {
		this.normalized_max = normalized_max;
	}

	/**
	 * @param actMin
	 * @param actMax
	 */
	public void use(double actMin, double actMax) {
		if (Double.isNaN(raw_min))
			raw_min = actMin;
		if (Double.isNaN(raw_max))
			raw_max = actMax;
	}

}
