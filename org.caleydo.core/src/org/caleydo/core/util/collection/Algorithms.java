/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * This class provides algorithms similar to the {@link Collections} class.
 *
 * @author Alexander Lex
 *
 */
public class Algorithms {

	/**
	 * Merges the input lists to a unique list and sorts it in descending order
	 *
	 * @param inputArray
	 *            varargs for lists
	 * @return the sorted, unique list
	 */
	public static <T extends Comparable<T>> List<T> mergeListsToUniqueList(List<T>... inputArray) {
		return mergeListsToUniqueList(Arrays.asList(inputArray));

	}

	/**
	 * Same as {@link #mergeListsToUniqueList(ArrayList...)} but with list
	 * instead of varargs
	 *
	 * @param inputLists
	 * @return
	 */
	public static <T extends Comparable<T>> List<T> mergeListsToUniqueList(
			Collection<? extends Collection<T>> inputLists) {

		List<T> resultList = new ArrayList<T>();
		HashSet<T> hs = new HashSet<T>();
		for (Collection<T> selectionList : inputLists) {
			hs.addAll(selectionList);
		}
		resultList.addAll(hs);

		Collections.sort(resultList, Collections.reverseOrder());
		return resultList;

	}

	/**
	 * Returns a randomly sampled list of Integers of a size not larger than
	 * numberOfSamples based on the provided sourceList, or the unmodified
	 * sourceList if numberOfSamples is null or bigger then the size of
	 * sourceList. </p>
	 * <p>
	 * Notice that the sourceList will also be modified, so clone it beforehand
	 * if you don't want that.
	 * </p>
	 *
	 * @param numberOfSamples
	 *            the maximum number of elements in the returned list
	 * @param sourceList
	 *            the source list
	 * @return the sourceList if it's length is smaller than numberOfSamples or
	 *         a new randomly sampled list of length numberOfSamples
	 */
	public static List<Integer> sampleList(Integer numberOfSamples,
			List<Integer> sourceList) {
		if (numberOfSamples == null || sourceList.size() < numberOfSamples)
			return sourceList;

		Logger.log(new Status(IStatus.INFO, "Sampling in core util.Algorithms",
				"Sampling a list of size " + sourceList.size() + " to " + numberOfSamples
						+ " values."));
		Collections.shuffle(sourceList);
		ArrayList<Integer> sampledList = new ArrayList<Integer>(sourceList.subList(0,
				numberOfSamples));
		return sampledList;
	}
}
