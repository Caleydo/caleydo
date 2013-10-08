/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDimension;

/**
 * @author Samuel Gratzl
 *
 */
@XmlType
public class KNNImputeDescription extends ImputeDescription {
	private int k = 10;
	private float rowmax = 0.5f;
	private float colmax = Float.NaN; // use by default NaN to avoid scanning all columns 0.8f;
	private int maxp = 1500;
	private int rng_seed = 362436069;
	private int maxit = 5;
	private double eps = 0.001;
	private EDimension dimension = EDimension.RECORD;

	/**
	 * @return the dimension, see {@link #dimension}
	 */
	public EDimension getDimension() {
		return dimension;
	}

	/**
	 * @param dimension
	 *            setter, see {@link dimension}
	 */
	public void setDimension(EDimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * @return the maxit, see {@link #maxit}
	 */
	public int getMaxit() {
		return maxit;
	}

	/**
	 * @param maxit
	 *            setter, see {@link maxit}
	 */
	public void setMaxit(int maxit) {
		this.maxit = maxit;
	}

	/**
	 * @return the eps, see {@link #eps}
	 */
	public double getEps() {
		return eps;
	}

	/**
	 * @param eps
	 *            setter, see {@link eps}
	 */
	public void setEps(double eps) {
		this.eps = eps;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public float getRowmax() {
		return rowmax;
	}

	public void setRowmax(float rowmax) {
		this.rowmax = rowmax;
	}

	public float getColmax() {
		return colmax;
	}

	public void setColmax(float colmax) {
		this.colmax = colmax;
	}

	public int getMaxp() {
		return maxp;
	}

	public void setMaxp(int maxp) {
		this.maxp = maxp;
	}

	public int getRng_seed() {
		return rng_seed;
	}

	public void setRng_seed(int rng_seed) {
		this.rng_seed = rng_seed;
	}


}
