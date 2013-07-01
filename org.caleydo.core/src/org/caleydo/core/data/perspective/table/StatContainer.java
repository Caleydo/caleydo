/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.perspective.table;


/**
 * @author cturkay
 *
 */
public class StatContainer {

	 
	
	/**
	 * The estimated mean value  
	 */
	private double mean;
	
	private double standardDev;
	
	private double median;
	
	private double interQuarileRange;
	
	private double skewness;
	
	private double kurtosis;
	
	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}
	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}
	/**
	 * @return the standardDev
	 */
	public double getStandardDev() {
		return standardDev;
	}
	/**
	 * @param standardDev the standardDev to set
	 */
	public void setStandardDev(double standardDev) {
		this.standardDev = standardDev;
	}
	/**
	 * @return the median
	 */
	public double getMedian() {
		return median;
	}
	/**
	 * @param median the median to set
	 */
	public void setMedian(double median) {
		this.median = median;
	}
	/**
	 * @return the interQuarileRange
	 */
	public double getInterQuarileRange() {
		return interQuarileRange;
	}
	/**
	 * @param interQuarileRange the interQuarileRange to set
	 */
	public void setInterQuarileRange(double interQuarileRange) {
		this.interQuarileRange = interQuarileRange;
	}
	/**
	 * @return the skewness
	 */
	public double getSkewness() {
		return skewness;
	}
	/**
	 * @param skewness the skewness to set
	 */
	public void setSkewness(double skewness) {
		this.skewness = skewness;
	}
	/**
	 * @return the kurtosis
	 */
	public double getKurtosis() {
		return kurtosis;
	}
	/**
	 * @param kurtosis the kurtosis to set
	 */
	public void setKurtosis(double kurtosis) {
		this.kurtosis = kurtosis;
	}
	
	
	
}
