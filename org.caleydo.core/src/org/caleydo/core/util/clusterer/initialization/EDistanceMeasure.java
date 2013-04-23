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
	 * @return the name, see {@link #name}
	 */
	public String getName() {
		return name;
	}
}
