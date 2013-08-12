/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Histogram holds the data structure of a histogram. It is based on an ArrayList<Integer> and maps the relevant
 * functions directly. It adds the functionality of keeping score of the biggest element.
 * <p>
 * The buckets have a unique ID across all instances of Histogram. This id is a running increment starting with
 * {@link #firstBucketID}.
 * </p>
 *
 * @author Alexander Lex
 */
public class Histogram {

	/** Static counter for buckets to guarantee unique bucket IDs */
	private static int bucketCounter = 0;

	/**
	 * One entry per bucket in the list, the value of a cell corresponds to the number of buckets + one for the nans
	 */
	private final int[] histogram;

	/**
	 * Contains the IDs of the elements in the buckets in the ArrayList and an Identifier as the first member of the
	 * pair. Same order as {@link #histogram}.
	 */
	private final List<SortedSet<Integer>> ids;

	private int sizeOfBiggestBucket = -1;

	/** The id of the first bucket in this histogram instance. */
	private final int firstBucketID;

	private final int nanIndex;

	/**
	 * Constructor initializing the Histogram with the specified number of buckets
	 *
	 * @param numberOfBuckets
	 *            the number of buckets in the histogram
	 */
	public Histogram(int numberOfBuckets) {
		synchronized (this) {
			firstBucketID = bucketCounter;
			bucketCounter += numberOfBuckets;
		}
		histogram = new int[numberOfBuckets + 1];
		ids = new ArrayList<>(numberOfBuckets + 1);
		nanIndex = numberOfBuckets;
		for (int i = 0; i < ids.size(); i++) {
			ids.add(new TreeSet<Integer>());
		}
	}

	/**
	 * optimize the data structure to be immutable
	 *
	 * @return
	 */
	public Histogram optimize() {
		// optimize the data structures to immutables for faster access
		for (int i = 0; i < ids.size(); ++i) {
			ids.set(i, ImmutableSortedSet.copyOf(ids.get(i)));
		}
		return this;
	}

	/**
	 * Adds one to the bucket at the specified bucketNumber. Adds the id to the ids associated with this bucket.
	 *
	 * @param bucketNumber
	 *            the bucket in which to increase the number of elements
	 * @param value
	 *            the value to set at the index
	 * @return the value previously at this position
	 */
	public void add(int bucketNumber, Integer objectID) {
		int act = histogram[bucketNumber];
		histogram[bucketNumber] = ++act;
		if (act > sizeOfBiggestBucket)
			sizeOfBiggestBucket = act;

		ids.get(bucketNumber).add(objectID);

		// histogram.set(index, value);

	}

	/** Adds an object to the dedicated NaN bucket. */
	public void addNAN(Integer objectID) {
		add(nanIndex, objectID);
	}

	/**
	 * Returns the value of the histogram at the specified index.
	 *
	 * @param bucketNumber
	 * @return
	 */
	public int get(int bucketNumber) {
		return histogram[bucketNumber];
	}

	/**
	 * @return the nanCount, see {@link #nanCount}
	 */
	public int getNanCount() {
		return get(nanIndex);
	}

	/**
	 * @return the nanIDs, see {@link #nanIDs}
	 */
	public SortedSet<Integer> getNanIDs() {
		return getIDsForBucket(nanIndex);
	}

	public Integer getBucketID(int bucketNumber) {
		return Integer.valueOf(bucketNumber + firstBucketID);
	}

	public SortedSet<Integer> getIDsForBucketFromBucketID(Integer bucketID) {
		return getIDsForBucket(bucketID - firstBucketID);
	}

	public SortedSet<Integer> getIDsForBucket(int bucketNumber) {
		return ids.get(bucketNumber);
	}

	/**
	 * The size of the histogram
	 *
	 * @return the size
	 */
	public int size() {
		return histogram.length - 1; // for nan
	}

	/**
	 * The largest value in the histogram
	 *
	 * @return the largest value
	 */
	public int getLargestValue() {
		return sizeOfBiggestBucket;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Histogram [histogram=");
		builder.append(Arrays.toString(histogram));
		builder.append("]");
		return builder.toString();
	}
}
