/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;

/**
 * A utility class that contains static functions to compute several statistics
 * @author cturkay
 *
 */
public class StatisticsUtils {
	
	
	
	public static ArrayList<Double> computeStatistics(EStatisticsType statToCompute)
	{
		ArrayList<Double> result = new ArrayList<Double>();
		
		switch (statToCompute) {
		case MEAN:			
			break;
		case MEDIAN:
			break;
		case STAND_DEV:
			break;
		case IQR:
			break;
		case SKEWNESS:
			break;
		case KURTOSIS:
			break;
		} 
		
		return result;
	}
	
	public static StatContainer computeFullStatContainer()
	{
		StatContainer fullStatContainer = new StatContainer();
		return fullStatContainer;
	}

}
