/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
