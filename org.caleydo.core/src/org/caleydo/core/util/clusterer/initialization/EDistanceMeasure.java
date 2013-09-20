/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.initialization;

import org.caleydo.core.util.clusterer.distancemeasures.ChebyshevDistance;
import org.caleydo.core.util.clusterer.distancemeasures.EuclideanDistance;
import org.caleydo.core.util.clusterer.distancemeasures.IDistanceMeasure;
import org.caleydo.core.util.clusterer.distancemeasures.ManhattanDistance;
import org.caleydo.core.util.clusterer.distancemeasures.PearsonCorrelation;

public enum EDistanceMeasure {

	EUCLIDEAN_DISTANCE("Euclidean Distance", new EuclideanDistance()),
	PEARSON_CORRELATION("Pearson Corrleation",new PearsonCorrelation()),
	MANHATTAN_DISTANCE("Manhattan Distance",new ManhattanDistance()),
	CHEBYSHEV_DISTANCE("Chebyshev Distance", new ChebyshevDistance());

	private final String name;
	private final IDistanceMeasure impl;

	private EDistanceMeasure(String name, IDistanceMeasure impl) {
		this.name = name;
		this.impl = impl;
	}

	public static EDistanceMeasure getTypeForName(String name) {
		for (EDistanceMeasure type : EDistanceMeasure.values()) {
			if (type.getName().equals(name))
				return type;
		}
		return null;
	}

	public static String[] getNames() {
		String[] names = new String[values().length];
		int count = 0;
		for (EDistanceMeasure type : values()) {
			names[count] = type.getName();
			count++;
		}
		return names;
	}

	public float apply(float[] vector1, float[] vector2) {
		return impl.getMeasure(vector1, vector2);
	}

	/**
	 * @return the impl, see {@link #impl}
	 */
	public IDistanceMeasure getImpl() {
		return impl;
	}

	/**
	 * @return the name, see {@link #name}
	 */
	public String getName() {
		return name;
	}
}
