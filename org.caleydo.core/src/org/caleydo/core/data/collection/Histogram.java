package org.caleydo.core.data.collection;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Histogram holds the data structure of a histogram. It is based on an ArrayList<Integer> and maps the
 * relevant functions directly. It adds the functionality of keeping score of the biggest element.
 * 
 * @author Alexander Lex
 */
public class Histogram
	implements Iterable<Integer> {
	private ArrayList<Integer> histogram;
	int iLargestValue = -1;

	float min;
	float max;

	/**
	 * Default Constructor
	 */
	public Histogram() {
		histogram = new ArrayList<Integer>();
	}

	/**
	 * Constructor with size indicator for the ArrayList
	 * 
	 * @param iDefaultSize
	 *            the default size
	 */
	public Histogram(int iDefaultSize) {
		histogram = new ArrayList<Integer>(iDefaultSize);
	}

	/**
	 * Add a value to the end of the histogram
	 * 
	 * @param iValue
	 *            the value to be added
	 * @return true
	 */
	public boolean add(Integer iValue) {
		if (iValue > iLargestValue)
			iLargestValue = iValue;
		return histogram.add(iValue);
	}

	/**
	 * Iterator for the histogram.
	 */
	@Override
	public Iterator<Integer> iterator() {
		return histogram.iterator();
	}

	/**
	 * Set a value at a specified index. The old value is replaced.
	 * 
	 * @param iIndex
	 *            the index, where to set the value
	 * @param iValue
	 *            the value to set at the index
	 * @return the value previously at this position
	 */
	public Integer set(int iIndex, Integer iValue) {
		if (iValue > iLargestValue)
			iLargestValue = iValue;
		return histogram.set(iIndex, iValue);
	}

	/**
	 * Returns the value of the histogram at the specified index.
	 * 
	 * @param iIndex
	 * @return
	 */
	public Integer get(int iIndex) {
		return histogram.get(iIndex);
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
		return iLargestValue;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMax(float max) {
		this.max = max;
	}

}
