/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.Collections;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.statistics.TDistributionLookup;
import org.caleydo.core.view.*;

import DistLib.DistLib_h;

/**
 * A utility class that contains static functions to compute several statistics
 * @author cturkay
 *
 */
public class StatisticsUtils {
	
	/**
	 * Computes the selected statistics either for rows or columns
	 * 
	 * @param computationType, if 0, the stats are computed over rows. If 1, the stats are computed over columns
	 * @param tablePerspective
	 * @param referenceTablePerspective, this provides the true ID mappings when difference view is used between different table perspectives
	 *                                   currently only used in rendering difference as a brick
	 * @param statToCompute
	 * @param selectedIDs, if provided the stats is computed only for the selected rows/columns
	 * @return
	 */
	public static ArrayList<Float> computeStatistics(int computationType, TablePerspective tablePerspective, TablePerspective referenceTablePerspective, EStatisticsType statToCompute, ArrayList<Integer> selectedIDs)
	{
		ArrayList<Float> result = new ArrayList<Float>();
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		
		/**
		 * Currently this is only true when rendering in a brick
		 */
		boolean useReferenceTableIDs = false;
		
		if (referenceTablePerspective != null)
		{
			useReferenceTableIDs = true;
			if(computationType == 0)
			{
				recordVA = referenceTablePerspective.getRecordPerspective().getVirtualArray();
			}
			else
			{
				dimensionVA = referenceTablePerspective.getDimensionPerspective().getVirtualArray();
			}
		}
		
		Table table = tablePerspective.getDataDomain().getTable();
		
		// It is an item space visualization
		// The statistics are computed over the rows
		if(computationType == 0)
		{
			for (Integer recordID : recordVA) {
				ArrayList<Float> tempDataColumn = new ArrayList<Float>();
				// No selection, use all the values
				if(selectedIDs == null)
				{
					for (Integer dimensionID : dimensionVA) {
						tempDataColumn.add((float) table.getNormalizedValue(dimensionID, recordID));
					}
				}
				// Use only the selected dimensions in computing the stats
				else
				{
					for (Integer dimensionID : selectedIDs) {
						tempDataColumn.add((float) table.getNormalizedValue(dimensionID, recordID));
					}
				}
				
				result.add(computeStatisticForDataColumn(statToCompute, tempDataColumn));
			}
		}
		// It is a dimension space visualization
		// The statistics are computed over the columns
		else if(computationType == 1)
		{
			for (Integer dimensionID : dimensionVA) {
				ArrayList<Float> tempDataColumn = new ArrayList<Float>();
				// No selection, use all the values
				if(selectedIDs == null)
				{
					for (Integer recordID : recordVA) {
						tempDataColumn.add((float) table.getNormalizedValue(dimensionID, recordID));
					}
				}
				else
				{
					for (Integer recordID : selectedIDs) {
						tempDataColumn.add((float) table.getNormalizedValue(dimensionID, recordID));
					}
				}
				
				result.add(computeStatisticForDataColumn(statToCompute, tempDataColumn));
			}
		}
		
		return result;
	}
	
	public static Float computeStatisticForDataColumn(EStatisticsType statToCompute, ArrayList<Float> dataColumn)
	{
		Float result = 0f;
		
		switch (statToCompute) {
		case MEAN:	
			result = computeMean(dataColumn);
			break;
		case MEDIAN:
			result = computeMedian(dataColumn);
			break;
		case STAND_DEV:
			result = computeStandardDeviation(dataColumn);
			break;
		case IQR:
			result = computeIQR(dataColumn);
			break;
		case SKEWNESS:
			result = computeSkewness(dataColumn);
			break;
		case KURTOSIS:
			result = computeKurtosis(dataColumn);
			break;
		} 
		
		return result;
	}
	
	/**
	 * Computes the sample mean of the values
	 * @param dataColumn
	 * @return
	 */
	private static Float computeMean(ArrayList<Float> dataColumn)
	{
		Float result = 0.0f;
		for (Float value: dataColumn)
		{
			result += value;
		}
		result = result / (float) dataColumn.size();
		return result;
	
	}
	
	/**
	 * Computes the sample standard deviation of the set of values (corrected estimation)
	 * @param dataColumn
	 * @return
	 */
	private static Float computeStandardDeviation(ArrayList<Float> dataColumn)
	{
		float result = 0.0f;
		float mean = computeMean(dataColumn);
		float sum = 0.0f;
        for (Float i : dataColumn)
            sum += Math.pow((i - mean), 2);
        result =  (float) Math.sqrt( sum / ( dataColumn.size() - 1 ) ); 
        return result;
	}
	
	/**
	 * Computes the median of the values
	 * @param dataColumn
	 * @return
	 */
	private static Float computeMedian(ArrayList<Float> dataColumn)
	{
		Collections.sort(dataColumn);
		int middle = dataColumn.size()/2;
		 
        if (dataColumn.size() % 2 == 1) {
            return dataColumn.get(middle);
        } else {
           return (dataColumn.get(middle-1) + dataColumn.get(middle)) / 2.0f;
        }
	}
	
	/**
	 * Computes the InterQuartile Range for the values (Q3 - Q1)
	 * @param dataColumn
	 * @return
	 */
	private static Float computeIQR(ArrayList<Float> dataColumn)
	{
		float result = 0.0f;
		
		Collections.sort(dataColumn);
		
		// First quartile
		float q1 = computePercentile(dataColumn, 0.25f);
		// Third quartile
		float q3 = computePercentile(dataColumn, 0.75f);
		result = q3 - q1;
		return result;
	}
	
	/**
	 * Helper function to compute the percentile of the 
	 * data (can be used to compute quartiles, IQR, etc.)
	 * @param dataColumn
	 * @param percentile
	 * @return
	 */
	private static Float computePercentile(ArrayList<Float> dataColumn, float percentile)
	{
		float result = 0.0f;
		int n = dataColumn.size();
		float k = (n-1) * percentile;
		int f = (int) Math.floor(k);
		int c = (int) Math.ceil(k);
	    if (f == c)
	    {
	    	result = dataColumn.get((int) k);
	    }
	    else
	    {
	    	float d0 =  dataColumn.get((int) f) * (c-k);
	    	float d1 =  dataColumn.get((int) c) * (k-f);
	    	result = d0 + d1;
	    }
	    return result;
	}
	
	/**
	 * Computes sample skewness given the values in the array
	 * @param dataColumn
	 * @return
	 */
	private static Float computeSkewness(ArrayList<Float> dataColumn)
	{
		float result = 0.0f;
		
		float mean = computeMean(dataColumn);
		float m3 = 0.0f;
        for (Float i : dataColumn)
            m3 += Math.pow((i - mean), 3);
        m3 = m3 / (float) dataColumn.size();
        
        float m2 = 0.0f;
        for (Float i : dataColumn)
            m2 += Math.pow((i - mean), 2);
        m2 = m2 / (float) dataColumn.size();
        m2 = (float) Math.pow(m2, 1.5);
        float g1 = m3 / m2;
        
        result =  (float) Math.sqrt( dataColumn.size() * ( dataColumn.size() - 1 ) ) / (float) (dataColumn.size() - 2) * g1; 
		
		return result;
	}
	
	/**
	 * Computes sample (excess) kurtosis given the values in the array
	 * @param dataColumn
	 * @return
	 */
	private static Float computeKurtosis(ArrayList<Float> dataColumn)
	{
		float result = 0.0f;
		
		float mean = computeMean(dataColumn);
		float m4 = 0.0f;
        for (Float i : dataColumn)
            m4 += Math.pow((i - mean), 4);
        m4 = m4 / (float) dataColumn.size();
        
        float m2 = 0.0f;
        for (Float i : dataColumn)
            m2 += Math.pow((i - mean), 2);
        m2 = m2 / (float) dataColumn.size();
        m2 = (float) Math.pow(m2, 2);
        float g2 = m4 / m2 - 3.0f;
        
        result =  g2; 
		
        
		return result;
	}
	
	public static StatContainer computeFullStatContainer()
	{
		StatContainer fullStatContainer = new StatContainer();
		return fullStatContainer;
	}
	
	/**
	 * Helper function for two-sample Welch's test
	 * @param s1, variance for the first sample set
	 * @param s2, variance for the second sample set
	 * @param n1, number of items in set 1
	 * @param n2, number of items in set 2
	 * @return
	 */
	public static Integer computeDegreesOfFreedomForWelch(float s1, float s2, float n1, float n2)
	{
		Integer df = 0;
		
		float frac1 = (float) Math.pow((s1 / n1), 2) / (n1 - 1);
		float frac2 = (float) Math.pow((s2 / n2), 2) / (n2 - 1);
		
		df = (int) (((float) Math.pow((s1 / n1 + s2 / n2), 2)) / (frac1 + frac2));
		
		return df;
	}
	
	/**
	 * Computes the variance estimator for two sampled welch test
	 * @param s1, variance for the first sample set
	 * @param s2, variance for the second sample set
	 * @param n1, number of items in set 1
	 * @param n2, number of items in set 2
	 * @return
	 */
	public static float computeVarianceEstimatorTwoSampleForWelch(float s1, float s2, float n1, float n2)
	{
		return (float) Math.sqrt(s1 / n1 + s2 / n2);
	}
	
	/**
	 * Computes the degrees of freedom for the samples and looks up
	 * the t-value for the df 
	 * 
	 * @param singleSided
	 * @param s1
	 * @param s2
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static float computeCriticalTValue(boolean singleSided, float s1, float s2, float n1, float n2)
	{
		int df = computeDegreesOfFreedomForWelch(s1, s2, n1, n2);
		return TDistributionLookup.returnCriticalTValue(singleSided, df);
	}
	
	
	
	/**
	 * Returns the minimum difference between two samples s.t.
	 * 
	 * 
	 * @param s1, variance for the first sample set
	 * @param s2, variance for the second sample set
	 * @param n1, number of items in set 1
	 * @param n2, number of items in set 2
	 * @return
	 */
	public static float computeCriticalMeanDifference(float s1, float s2, int n1, int n2)
	{
		float criticalT = computeCriticalTValue(false, s1, s2, n1, n2);
		float sampleVariance = computeVarianceEstimatorTwoSampleForWelch(s1, s2, n1, n2);
		return criticalT * sampleVariance;
	}
	
	/**
	 * 
	 * @param m1, mean of subset 1
	 * @param m2, mean of subset 2
	 * @param s1, variance for the first sample set
	 * @param s2, variance for the second sample set
	 * @param n1, number of items in set 1
	 * @param n2, number of items in set 2
	 * @return
	 */
	public static float computeTValue(float m1, float m2, float s1, float s2, int n1, int n2)
	{
		float sampleVariance = computeVarianceEstimatorTwoSampleForWelch(s1, s2, n1, n2);
		return (m1 - m2) / sampleVariance;
	}
	
	public static ArrayList<Float> computeTValues(ArrayList<Float> mean1, ArrayList<Float> mean2, ArrayList<Float> var1, ArrayList<Float> var2, int size1, int size2)
	{
		ArrayList<Float> tValues = new ArrayList<Float>();
		
		for(int i = 0; i < mean1.size(); i++)
		{
			tValues.add(computeTValue(mean1.get(i), mean2.get(i), var1.get(i), var2.get(i), size1, size2));
		}
		
		return tValues;
	}
	
	public static ArrayList<Float> computeCriticalTValues(boolean singleSided, ArrayList<Float> mean1, ArrayList<Float> mean2, ArrayList<Float> var1, ArrayList<Float> var2, int size1, int size2)
	{
		ArrayList<Float> tValues = new ArrayList<Float>();
		
		for(int i = 0; i < mean1.size(); i++)
		{
			tValues.add(computeCriticalTValue(singleSided, var1.get(i), var2.get(i), size1, size2));
		}
		
		return tValues;
	}
	
	
	public static ArrayList<Boolean> computeSignificanceOnTwoSampleTtest(boolean singleSided, ArrayList<Float> mean1, ArrayList<Float> mean2, ArrayList<Float> var1, ArrayList<Float> var2, int size1, int size2)
	{
		// First compute the t statistic values
		ArrayList<Float> tValues = computeTValues(mean1, mean2, var1, var2, size1, size2);
		// then compute the critical t statistic values
		ArrayList<Float> criticalTValues = computeCriticalTValues(singleSided, mean1, mean2, var1, var2, size1, size2);
		
		// now compare for significance test
		ArrayList<Boolean> significanceFlags = new ArrayList<Boolean>();
		
		for(int i = 0; i < tValues.size(); i++)
		{
			if(Math.abs(tValues.get(i)) >= criticalTValues.get(i))
			{
				significanceFlags.add(true);
			}
			else
			{
				significanceFlags.add(false);
			}
		}
		
		return significanceFlags;
	}
	
	public static Integer computeSampleSize(int computationType, TablePerspective tablePerspective, boolean considerSelections )
	{
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		
		//TODO: consider selections needs to be included for later use
		
		// It is an item space visualization
		// The statistics are computed over the rows
		if(computationType == 0)
		{
			return dimensionVA.size();
		}
		else
		{
			return recordVA.size();
		}
	}
	
	public static float computeFstatForVarianceTest(float s1, float s2, int size1, int size2)
	{
		float fStatistic = s1 / s2;
		float pValue = (float) DistLib.f.cumulative(fStatistic, size1 - 1, size2 - 1);
		
		return pValue;
	}
	
	public static float computeCriticalFstatForVarianceTest(int size1, int size2)
	{
		//float fStatistic = (float) weka.core.Statistics.FProbability(0.05f, size1 - 1, size2 - 1);
		
		//float fStatisticTest7 = (float) weka.core.Statistics. FProbability(0.95f, 10, 18);
		
		float fStatistic = (float) DistLib.f.cumulative(0.95f, size1 - 1, size2 - 1);
		//float fStatisticTest4 = (float) DistLib.f.cumulative(0.05f, 18, 10);
		
		//float fStatisticTest2 = (float) DistLib.f.density(0.95f, 18, 10);
		//float fStatisticTest5 = (float) DistLib.f.density(0.05f, 18, 10);
		
		//float fStatisticTest3 = (float) DistLib.f.quantile(0.9f,  size1 - 1, size2 - 1);
		
		
		
		
		//float fStatisticTest6 = (float) DistLib.f.quantile(0.05f, 18, 10);
		
		return fStatistic;
	}
	
	public static ArrayList<Float> computeFValues(ArrayList<Float> var1, ArrayList<Float> var2, int size1, int size2)
	{
		ArrayList<Float> fValues = new ArrayList<Float>();
		
		for(int i = 0; i < var1.size(); i++)
		{
			fValues.add(computeFstatForVarianceTest(var1.get(i), var2.get(i), size1, size2));
		}
		
		return fValues;
	}
	
	public static ArrayList<Boolean> computeSignificanceOnTwoSampleVarianceFTest( ArrayList<Float> var1, ArrayList<Float> var2, int size1, int size2)
	{
		// First compute the t statistic values
		ArrayList<Float> fValues1 = computeFValues(var1, var2, size1, size2);
		ArrayList<Float> fValues2 = computeFValues(var2, var1, size2, size1);
		// then compute the critical t statistic values
		float criticalFValue = computeCriticalFstatForVarianceTest(size1, size2);
		
		// now compare for significance test
		ArrayList<Boolean> significanceFlags = new ArrayList<Boolean>();
		
		for(int i = 0; i < fValues1.size(); i++)
		{
			if(fValues1.get(i) < 0.001 | fValues2.get(i) < 0.001)
			{
				significanceFlags.add(false);
			}
			else
			{
				significanceFlags.add(false);
			}
		}
		
		return significanceFlags;
	}

}
