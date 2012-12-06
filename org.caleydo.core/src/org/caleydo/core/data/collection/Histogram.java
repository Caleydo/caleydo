/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection;

import java.util.ArrayList;

/**
 * Histogram holds the data structure of a histogram. It is based on an ArrayList<Integer> and maps the relevant
 * functions directly. It adds the functionality of keeping score of the biggest element.
 *
 * @author Alexander Lex
 */
public class Histogram {

	private static int bucketCounter = 0;

	/**
	 * One entry per bucket in the list, the value of a cell corresponds to the number of buckets
	 */
	private ArrayList<Integer> histogram;

	/** Dedicated bucket for NAN values */
	private Integer nanCount;
	/**
	 * Contains the IDs of the elements in the buckets in the ArrayList and an Identifier as the first member of the
	 * pair. Same order as {@link #histogram}.
	 */
	private ArrayList<ArrayList<Integer>> ids;

	/** Same as {@link #ids} but for the dedicate NAN bucket */
	private ArrayList<Integer> nanIDs;

	int sizeOfBiggestBucket = -1;

	float min;
	float max;

	int firstBucketID;

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
		histogram = new ArrayList<Integer>(numberOfBuckets);
		ids = new ArrayList<ArrayList<Integer>>();
		for (int count = 0; count < numberOfBuckets; count++) {
			histogram.add(0);
			ids.add(new ArrayList<Integer>(new ArrayList<Integer>()));
		}
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
		Integer bucketSize = histogram.get(bucketNumber);
		histogram.set(bucketNumber, ++bucketSize);
		if (bucketSize > sizeOfBiggestBucket)
			sizeOfBiggestBucket = bucketSize;

		ids.get(bucketNumber).add(objectID);

		// histogram.set(index, value);

	}

	public void addNAN(Integer objectID) {
		nanCount += 1;
		nanIDs.add(objectID);
	}

	/**
	 * Returns the value of the histogram at the specified index.
	 *
	 * @param bucketNumber
	 * @return
	 */
	public Integer get(int bucketNumber) {
		return histogram.get(bucketNumber);
	}

	/**
	 * @return the nanCount, see {@link #nanCount}
	 */
	public Integer getNanCount() {
		return nanCount;
	}

	/**
	 * @return the nanIDs, see {@link #nanIDs}
	 */
	public ArrayList<Integer> getNanIDs() {
		return nanIDs;
	}

	public Integer getBucketID(int bucketNumber) {
		return new Integer(bucketNumber + firstBucketID);
	}

	public ArrayList<Integer> getIDsForBucketFromBucketID(Integer bucketID) {
		return ids.get(bucketID - firstBucketID);
	}

	public ArrayList<Integer> getIDsForBucket(int bucketNumber) {
		return ids.get(bucketNumber);
	}

	/**
	 * The size of the histogram
	 *
	 * @return the size
	 */
	public int size() {
		return histogram.size();
	}

	/**
	 * The largest value in the histogram
	 *
	 * @return the largest value
	 */
	public Integer getLargestValue() {
		return sizeOfBiggestBucket;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

}
