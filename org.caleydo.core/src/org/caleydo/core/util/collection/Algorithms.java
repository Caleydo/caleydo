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
package org.caleydo.core.util.collection;

import java.util.ArrayList;
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
	public static <T extends Comparable<T>> ArrayList<T> mergeListsToUniqueList(
			ArrayList<T>... inputArray) {

		ArrayList<ArrayList<T>> arrayListInputLists = new ArrayList<ArrayList<T>>();
		for (ArrayList<T> selectionList : inputArray) {
			arrayListInputLists.add(selectionList);
		}
		return mergeListsToUniqueList(arrayListInputLists);

	}

	/**
	 * Same as {@link #mergeListsToUniqueList(ArrayList...)} but with list
	 * instead of varargs
	 * 
	 * @param inputLists
	 * @return
	 */
	public static <T extends Comparable<T>> ArrayList<T> mergeListsToUniqueList(
			ArrayList<ArrayList<T>> inputLists) {

		ArrayList<T> resultList = new ArrayList<T>();
		HashSet<T> hs = new HashSet<T>();
		for (ArrayList<T> selectionList : inputLists) {
			hs.addAll(selectionList);
		}
		resultList.addAll(hs);
		Collections.sort(resultList);
		Collections.reverse(resultList);
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
