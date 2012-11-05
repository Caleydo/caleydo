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
package org.caleydo.core.util.clusterer.algorithm.tree;

public enum ETreeClustererAlgo {

	AVERAGE_LINKAGE("Average Linkage"),
	SINGLE_LINKAGE("Single Linkage"),
	COMPLETE_LINKAGE("Complete Linkage");

	private String name;

	/**
	 * 
	 */
	private ETreeClustererAlgo(String name) {
		this.name = name;
	}

	/**
	 * @return the name, see {@link #name}
	 */
	public String getName() {
		return name;
	}

	public static ETreeClustererAlgo getTypeForName(String name) {
		for (ETreeClustererAlgo algoType : ETreeClustererAlgo.values()) {
			if (algoType.getName().equals(name))
				return algoType;
		}
		return null;
	}

	public static String[] getNames() {
		String[] names = new String[ETreeClustererAlgo.values().length];
		int count = 0;
		for (ETreeClustererAlgo algoType : ETreeClustererAlgo.values()) {
			names[count] = algoType.getName();
			count++;
		}
		return names;
	}

}