/**
 * 
 */
package cerberus.math.statistics.histogram;

import cerberus.data.collection.selection.iterator.SelectionIterator;

import cerberus.data.collection.ISet;
import cerberus.data.collection.ISelection;
import cerberus.data.collection.IStorage;

/**
 * @author kalkusch
 *
 */
public class HistogramStatisticsSet 
extends HistogramStatisticInteger 
implements IHistogramStatistic {
	
	private ISet refSet = null;
	private ISelection refSelection = null;
	private IStorage refStorage = null;
	
	/**
	 * 
	 */
	public HistogramStatisticsSet() {
		
	}


	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#addDataValues(T[])
	 */
	public void addData( final ISet useSet ) {
		refSet = useSet;
				
		bRawDataIsValid = false;
		bHistoramDataIsValid = false;
		bHistoramPercentIsValid = false;
		
	}
	
	/**
	 * Get the required references and set lock to ISet.	 
	 */
	private boolean getReferencesFromSet() {
		
		if ( refSet.getReadTokenWait() ) {
			refSelection = refSet.getSelectionByDimAndIndex(0,0);
			refStorage = refSet.getStorageByDimAndIndex(0,0);
			
			if (( this.refSelection == null )
					||( this.refStorage == null )) {
				
				refSet.returnReadToken();
				bRawDataIsValid = false;
				
				assert false : "Only a singel value!";
				return false;		
			}
			
			bRawDataIsValid = true;
			
			return true;
		}
		else {
			return false;
		}
	
	}
	
	/**
	 * Attention: ReadLock for ISet must be valid!
	 *  
	 * @see cerberus.math.statistics.histogram.HistogramStatisticInteger#calculateVariance(float)
	 */
	protected synchronized float calculateVariance( final float fUseEstimate ) {

		if ( this.bRawDataIsValid ) {
					
			 assert iData != null :"no storage is assigned.";
			 
			 float fBufferVariance = 0.0f;
			
			SelectionIterator iter = refSelection.iterator();
			
			/**
			 * Variance =  1/(n-1)* SUM_n( (fUseEstimate - data_Value )^2 )
			 */
			while ( iter.hasNext() ) {
				float buffer = fUseEstimate - (float) iData[iter.next()]; 
				fBufferVariance += buffer * buffer;
			}
			
			fBufferVariance = fBufferVariance / (float) (refSelection.length() -1);
			
			return fBufferVariance;
		}
		
		assert false :"no valid raw data is available.";
		
		return 0.0f;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticInteger#calculateVarianceDouble(double)
	 */
	protected synchronized double calculateVarianceDouble( final double dUseEstimate ) {
		assert false : "Not supported yet";

		throw new RuntimeException("methode addDataValues(int[] setData) not supported.");
	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#addDataValues(T[])
	 */
	public void addDataValues(int[] setData) {
		assert false : "Not supported yet";

		throw new RuntimeException("methode addDataValues(int[] setData) not supported.");
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticInteger#addDataValues(java.lang.Number[])
	 */
	public void addDataValues(Number[] setData) {
		assert false : "Not supported yet";

		throw new RuntimeException("methode addDataValues(Number[] setData) not supported.");
	}

	
	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#updateHistogram()
	 */
	public boolean updateHistogram() {
		
		assert bRawDataIsValid : "not int[] data was set!";
		
		if ( ! getReferencesFromSet() ) {
			System.err.println("updateHistogram() can not get lock for ISet=" +
					this.refSet.toString() + " ==> no histogram!");
			return false;			
		}
		
		iData = refStorage.getArrayInt();
		
		SelectionIterator iter = this.refSelection.iterator();
		
		int iFirstIndex = iter.next();
		
		if ( bHistoramGetMinMaxFromData ) {
			
			int iDataMin = this.iData[iFirstIndex];
			int iDataMax = this.iData[iFirstIndex];
			int iSum = this.iData[iFirstIndex];
			
			int iCountItems = 0;
			
			while (iter.hasNext()) {
				int iIndexBuffer = iter.next();
				
				iSum += iData[iIndexBuffer];
				if ( iData[iIndexBuffer] > iDataMax ) {
					iDataMax = iData[iIndexBuffer];
				}
				else if ( iData[iIndexBuffer] < iDataMin ) {
					iDataMin = iData[iIndexBuffer];
				}
				
				iCountItems++;
			}
			
			fMean = (float) iSum / (float) iCountItems;
			
			setIntervalEqualSpaced(iHistogramIntervallLength,
					enumHistogramType,
					false,
					iDataMin,
					iDataMax );
		}
		
		if (!bHistoramRangeIsSet) {
			refSet.returnReadToken();
			
			assert bHistoramRangeIsSet : "no borders are set!";	
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
		
		iter = this.refSelection.iterator();
		
		while (iter.hasNext()) {
			
			int iBuffer = iData[ iter.next() ];
			
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
			
		} // end: while (iter.hasNext()) {
		
		fVariance = calculateVariance( fMean );
		bVarianceIsCalculated = true;
		
		refSet.returnReadToken();
		
		bHistoramDataIsValid = true;
		
		return true;

	}

	/* (non-Javadoc)
	 * @see cerberus.math.statistics.HistogramStatisticBase#getUpdatedHistogramData()
	 */
	public HistogramData getUpdatedHistogramData() {
		if ( this.updateHistogram() ) {
			//TODO: optimze
			HistogramData resultData = new HistogramData( this );
			
			return resultData;
		}
		
		return null;
	}


}
