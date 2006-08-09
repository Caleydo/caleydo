/**
 * 
 */
package cerberus.math.statistics.histogram;

//import java.lang.Comparable;
import java.lang.Number;

import cerberus.data.collection.ISet;

/**
 * @author java
 *
 */
public class HistogramStatisticData <T extends Number > {

	private boolean bHistoramRangeIsSet = false;
	
	private boolean bHistoramDataIsValid = false;

	private boolean bIsIntLongData = true;
	
	/**
	 * Number of histogram intervals.
	 * 
	 * @see cerberus.math.statistics.histogram.HistogramStatisticData#iHistogramIntervalLength
	 */
	protected int iHistogramBorderLength = 100;
	
	/**
	 * iHistogramIntervalLength = iHistogramBorderLength + 1;
	 * 
	 * Note: always use setter methode. do not assing directly!
	 * 
	 * @see cerberus.math.statistics.histogram.HistogramStatisticData#iHistogramBorderLength
	 */
	private int iHistogramIntervalLength = 101;
	
	protected double d_rangeValue;
	
	protected long l_rangeValue;
	
	protected T t_minValue;
	
	protected T t_maxValue;
	
	protected double t_meanValue;
	
	protected int[] iHistogramIntervalCounter;
	
	protected T[] t_histogramBorder;
	
	protected ISet refSet;
	
	protected boolean bUseMinMaxValueFromData = true;
	
		
	/**
	 * 
	 */
	public HistogramStatisticData() {
		
	}


	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#setIntervalBorders(T[])
	 */
	public void setIntervalBorders( T[] setBorders ) {
		
	}

	protected void setBorderIntervalLength( final int iSetLength ) {
		
		assert iSetLength < 1 : "can not create histogram with onyl one range!";		
		
		this.iHistogramBorderLength = iSetLength;
		this.iHistogramIntervalLength = iHistogramBorderLength + 1;
		
		this.t_histogramBorder = 
			(T[]) new Number[iHistogramIntervalLength];
		this.iHistogramIntervalCounter = 
			new int[iHistogramBorderLength];
		
		this.bHistoramDataIsValid = false;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#length()
	 */
	public int length() {
		return this.iHistogramBorderLength;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#setIntervalEqualSpaced(int, boolean, T, T)
	 */
	public void setIntervalEqualSpaced( 
			final int iNumberHistorgamLevel,
			final boolean bGetMinMaxfromData,
			final T useMinValue,
			final T useMaxValue ) {
		
		
		setBorderIntervalLength( iNumberHistorgamLevel );
		
//		this.t_histogramIntervalBorder = 
//			(T[]) new Number[iHistogramIntervalLength];
		
		/*
		 * Check data type...
		 */
		String bufferClassName = useMinValue.getClass().getName();
		
		/**
		 * Integer and Long data...
		 */
		if (( bufferClassName.equals( Integer.class.getName()) )
				||( bufferClassName.equals( Long.class.getName() )) ) {
			
			bIsIntLongData = true;
			
			if ( bGetMinMaxfromData ) {
				bHistoramRangeIsSet = false;
			}
			else 
			{
				long lMin = useMinValue.longValue();
				long lMax = useMaxValue.longValue();
				
				if ( lMax < lMin ) {
					/*
					 * input data was set wrong. 
					 * ==> Swap min & max ...
					 */
					long lSwap = lMax;
					lMax = lMin;
					lMin = lSwap;
					this.t_minValue = useMaxValue;
					this.t_maxValue = useMinValue;
				}
				else {
					this.t_minValue = useMinValue;
					this.t_maxValue = useMaxValue;
				}
				
				l_rangeValue = lMax - lMin;
				
				float fCurrentBorder  = (float) lMin;
				float fInc = (float) l_rangeValue / 
					(float) (iHistogramBorderLength);				
				
				
				for ( int i=0; i<iHistogramIntervalLength; i++) {
					iHistogramIntervalCounter[i] = (int) fCurrentBorder;
					
					
				}
				
				bHistoramRangeIsSet = true;
			}
			
			return;
		}
		
		/**
		 * Float and Double data...
		 */
		if (( bufferClassName.equals( Integer.class.getName()) )
				||( bufferClassName.equals( Long.class.getName() )) ) {
			
			bIsIntLongData = false;
			
			if ( bGetMinMaxfromData ) {
				bHistoramRangeIsSet = false;
			}
			else 
			{
				double dMin = useMinValue.doubleValue();
				double dMax = useMaxValue.doubleValue();
				
				if ( dMax < dMin ) {
					/*
					 * input data was set wrong. 
					 * ==> Swap min & max ...
					 */
					double lSwap = dMax;
					dMax = dMin;
					dMin = lSwap;
					this.t_minValue = useMaxValue;
					this.t_maxValue = useMinValue;
				}
				else {
					this.t_minValue = useMinValue;
					this.t_maxValue = useMaxValue;
				}
				
				d_rangeValue = dMax - dMin;
				
				double dCurrentBorder  = (float) dMin;
				double dInc = (float) d_rangeValue / 
					(float) (iHistogramBorderLength);				
				
				
				for ( int i=0; i<iHistogramIntervalLength; i++) {
					iHistogramIntervalCounter[i] = (int) dCurrentBorder;
					
					T buffer = (T) new Object();
//					buffer.
//					t_histogramBorder[i] = 
//						fCurrentBorder += fInc;
					
					dCurrentBorder += dInc;
				}
				
				bHistoramRangeIsSet = true;
			}
			
			return;
		}
		
		assert false : "Unsupported data-type=[" + bufferClassName +  "]";
		
	
		
	}
	
	public void defineSet( final ISet refUseSet ) {
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#addDataValues(T[])
	 */
	public void addDataValues( T[] setData ) {
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#updateHistogram()
	 */
	public void updateHistogram() {
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getHistogramData()
	 */
	public int[] getHistogramData() {
		return iHistogramIntervalCounter;
	}
	
	public T getVarianceValue() {
		return null;
	}
	
	public T getMinValue() {
		return null;
	}
	
	public T getMaxValue() {
		return null;
	}
	
	public T getMeanValue() {
		return null;
	}
}
