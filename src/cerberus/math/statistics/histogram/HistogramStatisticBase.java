package cerberus.math.statistics.histogram;


/**
 * Interface for historagms.
 * 
 * @author kalkusch
 *
 * @param <T> basic data type of histogram
 */
public interface HistogramStatisticBase<T extends Number> {

	public abstract void setIntervalBorders(T[] setBorders);

	/**
	 * Get number of intervalls used in this histogram.
	 * 
	 * @return number of intervalls
	 */
	public abstract int length();

	public abstract void setIntervalEqualSpaced(
			final int iNumberHistorgamLevel, 
			final StatisticHistogramType setEnumHistogramType,
			final boolean bGetMinMaxfromData,	
			final T useMinValue, 
			final T useMaxValue );
	

	public abstract void addDataValues(T[] setData);

	/**
	 * Recalculates the histogram.
	 * 
	 * @return TRUE if histogram was created successful.
	 */
	public abstract boolean updateHistogram();

	/**
	 * Calls updateHistogram() and returns the HistogramData obejct.
	 * 
	 * @return HistogramData object
	 */
	public abstract HistogramData getUpdatedHistogramData();
	
	/**
	 * 
	 * @return
	 */
	public abstract int[] getHistogramData();
	
	public abstract float[] getHistogramDataPercent();
	
	public abstract T getMaxValue();
	
	public abstract T getMinValue();
	
	public abstract T getMeanValue();
	
	public abstract T getVarianceValue();
	
	public abstract T[] getHistogramIntervalls();

	public abstract int getMaxValuesInAllIntervalls();
}