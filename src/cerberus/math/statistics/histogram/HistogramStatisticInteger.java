/**
 * 
 */
package cerberus.math.statistics.histogram;


import cerberus.data.collection.ISet;
//import cerberus.data.collection.IVirtualArray;
//import cerberus.data.collection.IStorage;

/**
 * @author Michael Kalkusch
 *
 */
public class HistogramStatisticInteger 
extends HistogramStatisticItem
implements IHistogramStatistic {

	private boolean bDistroyArrayAfterUpdate = false;
	
	protected float fMean;
	
	protected float fVariance;
	
	private int iMaxValue;
	
	private int iMinValue;
	
	private int iRangeValue;
	
	protected int [] iData = null;
	
	protected int[] iHistogramBorder = null;
	
	protected float[] fHistogramIntervalCounter = null;
	
	/**
	 * 
	 */
	public HistogramStatisticInteger( final int iBorderItervallLength ) {
		super();
		
		assert iBorderItervallLength > 0 : "HistogramStatisticInteger( iBorderItervallLength ) must be > 0!";
		
		this.setBorderIntervallLength( iBorderItervallLength );
	}

	/**
	 * Calculates variance using float.
	 * 
	 * @param fUseEstimate estimated value (or mean value)
	 * @return variance
	 */
	protected synchronized float calculateVariance( final float fUseEstimate ) {
		
		float fBufferVariance = 0.0f;
		
		int iLength = iData.length;
		
		/**
		 * Variance =  1/(n-1)* SUM_n( (fUseEstimate - data_Value )^2 )
		 */
		for ( int i=0; i< iLength; i ++ ) {
			float buffer = fUseEstimate - (float) iData[i]; 
			fBufferVariance += buffer * buffer;
		} 
		
		fBufferVariance = fBufferVariance / (float) (iLength -1);
		
		return fBufferVariance;
	}
	
	/**
	 * Calculates variance using double.
	 * 
	 * @param dUseEstimate estimated value (or mean value)
	 * @return variance
	 */
	protected synchronized double calculateVarianceDouble( final double dUseEstimate ) {
		
		double dBufferVariance = 0.0f;
		
		int iLength = iData.length;
		
		/**
		 * Variance =  1/(n-1)* SUM_n( (fUseEstimate - data_Value )^2 )
		 */
		for ( int i=0; i< iLength; i ++ ) {
			double buffer = dUseEstimate - (double) iData[i]; 
			dBufferVariance += buffer * buffer;
		} 
		
		dBufferVariance = dBufferVariance / (double) (iLength -1);
		
		return dBufferVariance;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticItem#setBorderIntervalLength(int)
	 */
	protected final void setBorderIntervallLength( final int iSetLength ) {
		
		super.setBorderIntervallLength( iSetLength );
		
		if ( iHistogramBorder == null ) {
			iHistogramBorder = new int[iHistogramBorderLength];		
		}
		else if (iHistogramBorderLength != iHistogramBorder.length) {
			iHistogramBorder = new int[iHistogramBorderLength];	
		}
		
		
		if ( iHistogramIntervallCounter == null ) {
			iHistogramIntervallCounter = new int[iHistogramIntervallLength];
		}
		else if (iHistogramIntervallLength != iHistogramIntervallCounter.length) {
			iHistogramIntervallCounter = new int[iHistogramIntervallLength];
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#setIntervalBorders(T[])
	 */
	public final void setIntervalBorders( final Number[] setBorders) {
		
		setBorderIntervallLength( setBorders.length );
		
		for ( int i=0; i<iHistogramBorderLength; i++) {
			iHistogramBorder[i] = setBorders[i].intValue();	
			
			if ( i != 0 ) {
				assert iHistogramBorder[i-1] <= iHistogramBorder[i] : 
					"Error in border Interval! Must use strictly monoton row. error at position [" +
					Integer.toString( i-1 ) +" - " +
					Integer.toString( i ) +" ] values: (" +
					Integer.toString( iHistogramBorder[i-1] ) +";" +
					Integer.toString( iHistogramBorder[i] ) +")";
			}
		}
		
		this.iMinValue = iHistogramBorder[0];
		this.iMaxValue = iHistogramBorder[iHistogramBorderLength-1];
		
		iRangeValue = iMaxValue - iMinValue;
		
		assert iRangeValue < 0 : "Error in border Interval! min and max are flipped!";
		
		bHistoramRangeIsSet = true;
		
	}

	public final void setIntervalEqualSpaced(final int iNumberHistorgamLevel,
			final StatisticHistogramType setEnumHistogramType,
			final boolean bGetMinMaxfromData,
			final Number useMinValue, 
			final Number useMaxValue ) {
		
		iMinValue = useMinValue.intValue();
		iMaxValue = useMaxValue.intValue();
		
		setIntervalEqualSpacedInt(iNumberHistorgamLevel,
				setEnumHistogramType,
				bGetMinMaxfromData,
				iMinValue,
				iMaxValue );
		
		bHistoramRangeIsSet = true;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#setIntervalEqualSpaced(int, boolean, T, T)
	 */
	public final void setIntervalEqualSpacedInt(
			final int iNumberHistorgamLevel,
			final StatisticHistogramType setEnumHistogramType,
			final boolean bGetMinMaxfromData,
			final int useMinValue, 
			final int useMaxValue ) {
		
		bHistoramGetMinMaxFromData = bGetMinMaxfromData;
		
		/*
		 * default is REGULAR_LINEAR.
		 */
		if ( setEnumHistogramType == null ) {
			enumHistogramType = StatisticHistogramType.REGULAR_LINEAR;
		} else {
			enumHistogramType = setEnumHistogramType;
		}
		
		setBorderIntervallLength( iNumberHistorgamLevel );
		
		if ( ! bHistoramGetMinMaxFromData ) {
			
			switch (enumHistogramType) {
			
			case REGULAR_LINEAR: {
				iMinValue = useMinValue;
				iMaxValue = useMaxValue;
				
				if ( iMaxValue < iMinValue ) {
					/*
					 * input data was set wrong. 
					 * ==> Swap min & max ...
					 */
					int lSwap = iMaxValue;
					iMaxValue = iMinValue;
					iMinValue = lSwap;
				} //end: if ( iMaxValue < iMinValue ) {
				
				iRangeValue = iMaxValue - iMinValue;
				
				/*
				 * special case: if range is smaller than number of possible classes
				 */
				if ( iRangeValue < iHistogramIntervallLength) {
					setBorderIntervallLength(iRangeValue);
				}
							
				float fCurrentBorder  = (float) iMinValue;
				float fInc = (float) iRangeValue / 
					(float) (iHistogramIntervallLength);							
				
				for ( int i=0; i<iHistogramBorderLength; i++) {
					iHistogramBorder[i] = (int) fCurrentBorder;
					fCurrentBorder += fInc;				
				} //end: for ( int i=0; i<iHistogramBorderLength; i++) { 
				
			} break; //end: case(REGULAR_LINEAR)
			
			
			case REGULAR_LOG: {
				iMinValue = useMinValue;
				iMaxValue = useMaxValue;
								
				if ( iMaxValue < iMinValue ) {
					/*
					 * input data was set wrong. 
					 * ==> Swap min & max ...
					 */
					int lSwap = iMaxValue;
					iMaxValue = iMinValue;
					iMinValue = lSwap;
				} //end: if ( iMaxValue < iMinValue ) {
				
				int iOffset = 0;
				
				if ( iMinValue < 1 ) {
					iOffset = Math.abs(iMinValue) + 1;
				}
				
				double dLgMin = Math.log( (float)iMinValue + iOffset );
				double dLgMax = Math.log( (float)iMaxValue + iOffset);				
				double dLgRange = dLgMax - dLgMin;							
				double dCurrentBorder  = dLgMin;
				
				double dInc = dLgRange/ 
					(double) (iHistogramIntervallLength);							
				
				for ( int i=0; i<iHistogramBorderLength; i++) {
					
					double test = Math.exp( dCurrentBorder );
					iHistogramBorder[i] = ((int) test) - iOffset;
					
					dCurrentBorder += dInc;				
				} //end: for ( int i=0; i<iHistogramBorderLength; i++) { 
				
			} break; //end: case(REGULAR_LOG)
			
			
			case REGULAR_LOG_INV: {
				iMinValue = useMinValue;
				iMaxValue = useMaxValue;
								
				if ( iMaxValue < iMinValue ) {
					/*
					 * input data was set wrong. 
					 * ==> Swap min & max ...
					 */
					int lSwap = iMaxValue;
					iMaxValue = iMinValue;
					iMinValue = lSwap;
				} //end: if ( iMaxValue < iMinValue ) {
				
				int iOffset = 0;
				
				if ( iMinValue < 1 ) {
					iOffset = Math.abs(iMinValue) + 1;
				}
				
				double dLgMin = Math.log( (float)iMinValue + iOffset );
				double dLgMax = Math.log( (float)iMaxValue + iOffset);				
				double dLgRange = dLgMax - dLgMin;							
				double dCurrentBorder  = dLgMin;
				
				double dInc = dLgRange/ 
					(double) (iHistogramIntervallLength);							
				
				for ( int i=iHistogramBorderLength-1; i>=0; i--) {
					
					double test = Math.exp( dCurrentBorder );
					
					int iTest = iMaxValue - ((int) (test) - iMinValue);
					// iHistogramBorder[i] = ((int) test) - iOffset;
					iHistogramBorder[i] = iTest;
					
					dCurrentBorder += dInc;				
				} //end: for ( int i=0; i<iHistogramBorderLength; i++) {
				
				iHistogramBorder[0] = iMinValue;
				
			} break; //end: case(REGULAR_LOG_INV)
			
			
			default:
				assert false : "Unknown type [" + this.enumHistogramType.toString() + "]";
				
			} // end: switch
			
			bHistoramRangeIsSet = true;
			
		} // end: if ( ! bHistoramGetMinMaxFromData ) {
		

	}
	
	public void addData( final ISet useSet ) {
		useSet.getSelectionByDimAndIndex(0,0);
		useSet.getStorageByDimAndIndex(0,0);
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#addDataValues(T[])
	 */
	public void addDataValues( final Number[] setData) {

		bDistroyArrayAfterUpdate = true;
		
		for ( int i=0; i< setData.length; i ++ ) {
			iData[i] = setData[i].intValue();
		} 
		
		bRawDataIsValid = true;
		bHistoramDataIsValid = false;
		bHistoramPercentIsValid = false;
	}
	
	public void addDataValues( final int[] setData) {

		bDistroyArrayAfterUpdate = false;
		
		try {
			this.iData = (int[]) setData;
		} catch (NullPointerException npe) {
			this.bHistoramDataIsValid = false;
			assert false : "can not handel data";
		}
				
		bRawDataIsValid = true;
		bHistoramDataIsValid = false;
		bHistoramPercentIsValid = false;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#updateHistogram()
	 */
	public synchronized boolean updateHistogram() {
		
		
		assert bRawDataIsValid : "not int[] data was set!";
	
		if ( ! bRawDataIsValid ) {
			return false;
		}
		
		if ( iData.length < 1 ) {
			assert false : "Only a singel value!";
			return false;		
		}
		
		
		if ( bHistoramGetMinMaxFromData ) {
			
			int iDataMin = this.iData[0];
			int iDataMax = this.iData[0];
			int iSum = this.iData[0];
			
			for (int i=1; i< iData.length; i++) {
				iSum += iData[i];
				if ( iData[i] > iDataMax ) {
					iDataMax = iData[i];
				}
				else if ( iData[i] < iDataMin ) {
					iDataMin = iData[i];
				}
			}
			
			fMean = (float) iSum / (float) iData.length;
			
			setIntervalEqualSpaced(iHistogramIntervallLength,
					enumHistogramType,
					false,
					iDataMin,
					iDataMax );
		}
		
		assert bHistoramRangeIsSet : "no borders are set!";	
		
		if (!bHistoramRangeIsSet) {
			return false;
		}
			
		
		/*
		 * create the histogram...
		 */
		
		/*
		 * reset values..
		 */
		iMaxValuesInAllIntervalls = 0;
		iValuesBelowBounds = 0;
		iValuesOverBounds = 0;
		
		/*
		 * Reset...
		 */
		for ( int k=0; k < this.iHistogramIntervallLength;k++) {
			iHistogramIntervallCounter[k] = 0;
		}
		
		  for (int i = 0; i < iData.length; i++) {

			int iBuffer = iData[i];

			if (iBuffer < iHistogramBorder[0]) {
				iValuesBelowBounds++;
			}
			else 
			{
				int j = 0;			
				boolean bSearchIntervall = true;
							
				while ( j < iHistogramIntervallLength) {
									
					if (iBuffer <= iHistogramBorder[j+1]) {
						iHistogramIntervallCounter[j]++;
	
						if (iHistogramIntervallCounter[j] > iMaxValuesInAllIntervalls) {
							iMaxValuesInAllIntervalls = iHistogramIntervallCounter[j];
						}
	
						/* exit while loop ... */
						bSearchIntervall = false;
						break;
						
					} //end: if (iData[i] <= iHistogramBorder[j+1]) {
					j++;
	
				} // end:while

				/*
				 * data value is above upper bound ==> add up upper bound intervall
				 */
				if (bSearchIntervall) {
					iValuesOverBounds++;
				}
			} //end: if (iData[i] < iHistogramBorder[0]) {..} else {

		} // end: for ( int i=0;i<i_dataValues.length;i++) {
		
		bHistoramDataIsValid = true;
		
		/*
		 * Remove copy of data the histogram was vreated from
		 */
		if ( bDistroyArrayAfterUpdate ) {
			
			/*
			 * calculate variance before deleting data...
			 */
			getVarianceValueF();
			
			bRawDataIsValid = false;
			bDistroyArrayAfterUpdate = false;
			
			this.iData = null;
		}
		
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getUpdatedHistogramData()
	 */
	public HistogramData getUpdatedHistogramData() {
		if ( this.updateHistogram() ) {
			//TODO: optimize 
			HistogramData resultData = new HistogramData( this );
			
			return resultData;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getHistogramData()
	 */
	public final int[] getHistogramData() {
		return iHistogramIntervallCounter;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getHistogramDataPercent()
	 */
	public final float[] getHistogramDataPercent() {
		return fHistogramIntervalCounter;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getMaxValue()
	 */
	public final Number getMaxValue() {
		return iMaxValue;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getMinValue()
	 */
	public final Number getMinValue() {
		return iMinValue;
	}

	public final Number getRangeValue() {
		return iRangeValue;
	}
	

	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getMeanValue()
	 */
	public final Number getMeanValue() {
		return fMean;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getVarianceValue()
	 */
	public Number getVarianceValue() {
				
		if ( ! bVarianceIsCalculated ) {
			
			if ( this.iData == null ) {
				return 0.0f;
			}
			
			fVariance = calculateVariance( fMean );
			bVarianceIsCalculated = true;
		}
		return fVariance;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getMaxValue()
	 */
	public final int getMaxValueI() {
		return iMaxValue;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getMinValue()
	 */
	public final int getMinValueI() {
		return iMinValue;
	}

	public final int getRangeValueI() {
		return iRangeValue;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getMinValue()
	 */
	public final float getMeanValueF() {
		return fMean;
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getVarianceValue()
	 */
	public float getVarianceValueF() {
		if ( ! bVarianceIsCalculated ) {
			fVariance = calculateVariance( fMean );
			bVarianceIsCalculated = true;
		}
		return fVariance;
	}
	
	/**
	 * Copy histogram intervall borders to a new Number array.
	 * 
	 * @see cerberus.math.statistics.histogram.IHistogramStatistic#getHistogramIntervalls()
	 * 
	 */
	public final Number[] getHistogramIntervalls() {
		Number[] resultBuffer = new Number[ iHistogramBorder.length ];
		
		for ( int i=0; i<resultBuffer.length; i++ ) {
			resultBuffer[i] = this.iHistogramBorder[i];
		}
		
		return resultBuffer;
	}
	
	
	/**
	 * Copy histogram intervall borders to a new Number array.
	 * 
	 * @see cerberus.math.statistics.histogram.IHistogramStatistic#getHistogramIntervalls()
	 * 
	 */
	public final int[] getHistogramIntervallsToInt() {		
		return this.iHistogramIntervallCounter;
	}
	
	public final boolean isHistoramGetMinMaxFromDataEnabled() {
		return bHistoramGetMinMaxFromData;
	}
	
	public final void setHistoramGetMinMaxFromDataEnabled( final boolean bSet ) {
		bHistoramGetMinMaxFromData = bSet;
	}
	
	
}
