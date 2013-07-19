package org.caleydo.core.util.statistics;

/**
 * A lookup class for the Critical values of Student's t distribution
 * Currently, only 0.95 and 0.975 significance levels are considered for single/double
 * sided t-tests
 * 
 * @author cturkay
 *
 */
public class TDistributionLookup {
	
	private static double[][] tValueLookup = {
			{6.314,2.920,2.353,2.132,2.015,1.943,1.895,1.860,1.833,1.812,1.796,1.782,1.771,1.761,1.753,1.746,1.740,1.734,1.729,1.725,1.721,1.717,1.714,1.711,1.708,1.706,1.703,1.701,1.699,1.697,1.696,1.694,1.692,1.691,1.690,1.688,1.687,1.686,1.685,1.684,1.683,1.682,1.681,1.680,1.679,1.679,1.678,1.677,1.677,1.676,1.675,1.675,1.674,1.674,1.673,1.673,1.672,1.672,1.671,1.671,1.670,1.670,1.669,1.669,1.669,1.668,1.668,1.668,1.667,1.667,1.667,1.666,1.666,1.666,1.665,1.665,1.665,1.665,1.664,1.664,1.664,1.664,1.663,1.663,1.663,1.663,1.663,1.662,1.662,1.662,1.662,1.662,1.661,1.661,1.661,1.661,1.661,1.661,1.660,1.660,1.645},
			{12.706,4.303,3.182,2.776,2.571,2.447,2.365,2.306,2.262,2.228,2.201,2.179,2.160,2.145,2.131,2.120,2.110,2.101,2.093,2.086,2.080,2.074,2.069,2.064,2.060,2.056,2.052,2.048,2.045,2.042,2.040,2.037,2.035,2.032,2.030,2.028,2.026,2.024,2.023,2.021,2.020,2.018,2.017,2.015,2.014,2.013,2.012,2.011,2.010,2.009,2.008,2.007,2.006,2.005,2.004,2.003,2.002,2.002,2.001,2.000,2.000,1.999,1.998,1.998,1.997,1.997,1.996,1.995,1.995,1.994,1.994,1.993,1.993,1.993,1.992,1.992,1.991,1.991,1.990,1.990,1.990,1.989,1.989,1.989,1.988,1.988,1.988,1.987,1.987,1.987,1.986,1.986,1.986,1.986,1.985,1.985,1.985,1.984,1.984,1.984,1.960}
	};
	
	/**
	 * Returns the critical t value
	 * @param singleSided, if true 0.95 significance level is used, otherwise 0.975 is used
	 * @param df, degrees of freedom
	 * @return
	 */
	public static float returnCriticalTValue(boolean singleSided, int df)
	{
		int valueCount = tValueLookup[0].length;
		
		// Fixing df for array indexing
		df = df - 1;
		
		// for degrees of freedom higher than 100, clamp it to 101
		if (df >= valueCount)
		{
			df = valueCount - 1;
		}
		else if ( df <= 0)
		{
			df = 0;
		}
		
		
		// For single sided test, use 0.95 confidence values,
		// For double sided test, use 0.975 confidence values,
		int arrayIndex = 0;
		if (!singleSided)
		{
			arrayIndex = 1;
		}
		
		return (float) tValueLookup[arrayIndex][df];
	}
}
