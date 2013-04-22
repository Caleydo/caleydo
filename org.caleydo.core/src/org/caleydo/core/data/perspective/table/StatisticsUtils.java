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

package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.Collections;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.*;

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
	 * @param statToCompute
	 * @param selectedIDs, if provided the stats is computed only for the selected rows/columns
	 * @return
	 */
	public static ArrayList<Float> computeStatistics(int computationType, TablePerspective tablePerspective, EStatisticsType statToCompute, ArrayList<Integer> selectedIDs)
	{
		ArrayList<Float> result = new ArrayList<Float>();
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		
		Table table = tablePerspective.getDataDomain().getTable();
		
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

}
