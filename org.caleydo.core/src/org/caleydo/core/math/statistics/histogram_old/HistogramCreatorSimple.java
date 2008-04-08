/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.math.statistics.histogram_old;

//import java.util.Vector;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.IStorage;

import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.caleydo.core.data.collection.virtualarray.iterator.VirtualArrayProxyIterator;


/**
 * Creation of a simple histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class HistogramCreatorSimple implements HistogramCreator {

	protected int [] arrayHistogram = null;
	
	protected int iHistogramWidth = 100;
	
	protected ISet refSet = null;
	
	private int iMaxValue = 0;
	
	private int iMinValue = 0;
	
	private int iMaxItemsPerRow = 0;
	/**
	 * 
	 */
	public HistogramCreatorSimple(ISet setRefSet ) {
		refSet = setRefSet;		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#clear()
	 */
	public void clear() {
		arrayHistogram = null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#update()
	 */
	public void updateState() {
		if ( arrayHistogram == null ) {
			// create array...
			arrayHistogram = new int[iHistogramWidth];
		}
		else {
			if ( arrayHistogram.length != iHistogramWidth ) {
				//reset size of array...
				arrayHistogram = null;
				arrayHistogram = new int[iHistogramWidth];
			}
		}
		
		if ( refSet == null ) {
			assert false: "HistogramCreator ISet is null";
			return;
		}
		
		IStorage[] refStorage = refSet.getStorageByDim( 0 );
		IVirtualArray[] refSelection = refSet.getVirtualArrayByDim( 0 );
		
		int [] refArray = refStorage[0].getArrayInt();
					
		if ( refArray == null ) {
			return;
		}
		
		// ... read out data from storage ...
		IVirtualArrayIterator iter =
			new VirtualArrayProxyIterator( refSelection[0] );
		
		iMaxItemsPerRow = 0;
		iMinValue = refArray[0];
		iMaxValue = refArray[0];
		
		//... search min-max values
		while ( iter.hasNext() ) {
			
			int iCurrentRealIndex = iter.next();
			
			if ( refArray[iCurrentRealIndex] < iMinValue) {
				iMinValue = refArray[iCurrentRealIndex];
			} 
			else if (refArray[iCurrentRealIndex] > iMaxValue) {
				iMaxValue = refArray[iCurrentRealIndex];
			}
		}
		System.out.print("\n\n");
		
		//reset iterator to begin...
		iter.begin();
		
		final int iRange = iMaxValue - iMinValue;
		final float fSteps = (float) iRange / (float) (iHistogramWidth-1);
		final int iOffset = iMinValue;
		
		// initialize histogram...
		for ( int i=0; i< arrayHistogram.length; i++) {
			arrayHistogram[i] = 0;
		}
		
		
		//... create histogram ...
		while ( iter.hasNext() ) {

			int iNorm = refArray[ iter.next() ] - iOffset;
			int iIncIndex = (int) ((float) iNorm / fSteps);
			
			arrayHistogram[iIncIndex]++;
			
			if ( arrayHistogram[iIncIndex] > iMaxItemsPerRow ) {
				iMaxItemsPerRow = arrayHistogram[iIncIndex];
			}
		}

	}

	/**
	 * Get minumum value.
	 * 
	 * @return minumum value
	 */
	public int getMinValueI() {
		return this.iMinValue;
	}
	
	/**
	 * Get maximum Value.
	 * 
	 * @return maximum value
	 */
	public int getMaxValueI() {
		return this.iMaxValue;
	}
	
	/**
	 * Get the number of itmes in the row with the most items per row.
	 * 
	 * @return number of items in row with most items.
	 */
	public int getMaxCountPerRow() {
		return this.iMaxItemsPerRow;
	}
	
	/**
	 * Get minumum value.
	 * 
	 * @return minumum value
	 */
	public float getMinValueF() {
		return this.iMinValue;
	}
	
	/**
	 * Get maximum Value.
	 * 
	 * @return maximum value
	 */
	public float getMaxValueF() {
		return this.iMaxValue;
	}
	
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#getCounterPerRow()
	 */
	public int[] getCounterPerRow() {
		return arrayHistogram;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#getRowWidth()
	 */
	public int getRowWidth() {
		return iHistogramWidth;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#setRowWidth(int)
	 */
	public void setRowWidth(int iSetRowWidth) {
		if ( iSetRowWidth > 1 ) {
			iHistogramWidth = iSetRowWidth;
		}
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#getSelection()
	 */
	public ISet getSet() {
		return refSet;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.statistic.histogram.HistogramCreator#setSelection(org.caleydo.core.data.set.Selection)
	 */
	public void setSet( ISet setSet ) {
		refSet = setSet;
	}

}
