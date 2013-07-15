/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.vis.rank.model;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Lists;

/**
 * utilities for handling {@link ARankColumnModel}s
 * 
 * @author Samuel Gratzl
 * 
 */
public class RankColumnModels {

	/**
	 * flatten the hierarchy of the given columns
	 * 
	 * @param cols
	 * @return
	 */
	public static Set<ARankColumnModel> flatten(ARankColumnModel... cols) {
		return flatten(Arrays.asList(cols));
	}
	
	/**
	 * flatten the hierarchy of the given columns
	 * 
	 * @param cols
	 * @return
	 */
	public static Set<ARankColumnModel> flatten(Iterable<ARankColumnModel> cols) {
		Set<ARankColumnModel> result = new HashSet<>();
		Deque<ARankColumnModel> queue = Lists.newLinkedList(cols);
		while (!queue.isEmpty()) {
			ARankColumnModel s = queue.pollFirst();
			if (!result.add(s))
				continue;
			if (s instanceof ACompositeRankColumnModel)
				queue.addAll(((ACompositeRankColumnModel) s).getChildren());
		}
		return result;
	}

}
