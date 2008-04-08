/**
 * 
 */
package org.caleydo.core.math.statistics.histogram;



/**
 * @author Michael Kalkusch
 *
 */
public abstract class HistogramStatisticItem 
implements IHistogramStatistic {

	protected StatisticHistogramType enumHistogramType = StatisticHistogramType.REGULAR_LINEAR;
	
	protected boolean bHistoramRangeIsSet = false;
	
	/**
	 * is the data array assigned?
	 */
	protected boolean bRawDataIsValid = false;
	
	protected boolean bHistoramDataIsValid = false;
	
	protected boolean bHistoramBorderIsSet = false;
	
	protected boolean bHistoramPercentIsValid = false;
	
	protected boolean bHistoramGetMinMaxFromData = true;
		
	protected boolean bVarianceIsCalculated = false;
	
	protected int[] iHistogramIntervallCounter = null;
	
	protected int iMaxValuesInAllIntervalls = 0;
	
	protected int iValuesBelowBounds = 0;
	
	protected int iValuesOverBounds = 0;
	
	/**
	 * Number of histogram intervals.
	 * iHistogramBorderLength = iHistogramIntervalLength + 1;
	 * 
	 * @see org.caleydo.core.math.statistics.histogram.HistogramStatisticData#iHistogramIntervallLength
	 */
	protected int iHistogramBorderLength = 101;
	
	/**
	 * Number of histogram values.
	 * 
	 * iHistogramIntervalLength = iHistogramBorderLength - 1;
	 * 
	 * Note: always use setter method. do not assing directly!
	 * 
	 * @see org.caleydo.core.math.statistics.histogram.HistogramStatisticData#iHistogramBorderLength
	 */
	protected int iHistogramIntervallLength = 100;
	
	/**
	 * 
	 */
	protected HistogramStatisticItem() {
		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.math.statistics.HistogramStatisticBase#setIntervalBorders(T[])
	 */
	protected void setBorderIntervallLength( final int iSetLength ) {
		
		assert iSetLength > 1 : "can not create histogram with onyl one range!";		
		
		this.iHistogramBorderLength = iSetLength + 1;
		this.iHistogramIntervallLength = iSetLength;
		
		this.bHistoramBorderIsSet = false;
		this.bHistoramDataIsValid = false;
		this.bHistoramPercentIsValid = false;
		this.bVarianceIsCalculated = false;
		
		this.iMaxValuesInAllIntervalls = 0;
	}
	

	/* (non-Javadoc)
	 * @see org.caleydo.core.math.statistics.HistogramStatisticBase#length()
	 */
	public final int length() {
		return iHistogramBorderLength;
	}
	
	public int getMaxValuesInAllIntervalls() {
		return iMaxValuesInAllIntervalls;
	}

	
}
