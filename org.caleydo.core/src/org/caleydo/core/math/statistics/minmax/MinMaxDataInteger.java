/**
 * 
 */
package org.caleydo.core.math.statistics.minmax;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
//import org.caleydo.core.data.collection.set.SetMultiDim;
import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;

/**
 * @author Michael Kalkusch
 *
 */
public final class MinMaxDataInteger {

	protected float fMinValue[]; 
	
	protected float fMaxValue[]; 
	
	protected float fMeanValue[];
	
	protected int iCountItmes[];
	
	private int iDimensions = -1;
	
	protected boolean bIsValid = false;
	
	// protected SetMultiDim set = null;
	
	protected ISet set = null;
	
	/**
	 * 
	 */
	public MinMaxDataInteger( final int iSetDimension) {
		allocateMinMax( iSetDimension );
	}
	
	protected void allocateMinMax( final int iSetDim ) {
		if ( iDimensions != iSetDim ) {
			iDimensions = iSetDim;
			
			fMinValue = new float[iDimensions]; 
			fMaxValue = new float[iDimensions]; 
			fMeanValue = new float[iDimensions];	
			iCountItmes = new int [iDimensions];
		}	
	}
	
	public final boolean isValid() {
		return bIsValid;
	}
	
	public final int getDimension() {
		return this.iDimensions;
	}
	
//	/**
//	 * @deprecated
//	 * 
//	 * @return true if ok
//	 */
//	private boolean updateDataOld() {
//		
//		bIsValid = false;
//		
//		  if ( set != null ) {
//		    	
//			  if ( set.getDimensions() < iDimensions ) {
//	    			System.out.println("MinMaxDataInteger.updateDataOld() deprecated method! Can not use a ISet with only one dimension!");
//	    			return false;
//			  }
//			  
//		    	if ( set.getReadToken() ) {
//		    		
//		    		//allocateMinMax( set.getDimensions() );
//		    	
//			    	IStorage storageX = set.getStorageByDimAndIndex(0,0);
//			    	IStorage storageY = set.getStorageByDimAndIndex(1,0);
//			    			    	
//			    	int[] i_dataValuesX = storageX.getArrayInt();
//			    	int[] i_dataValuesY = storageY.getArrayInt();
//			    	
//			    	if (( i_dataValuesX != null )&&
//			    			( i_dataValuesY != null )) {
//			    		
//				    	IVirtualArrayIterator iterX = set.getVirtualArrayByDimAndIndex(0,0).iterator();
//				    	IVirtualArrayIterator iterY = set.getVirtualArrayByDimAndIndex(1,0).iterator();
//			    		
//				    	/**
//				    	 * update ...
//				    	 */		
//				    	
//				    	int iMinX = i_dataValuesX[iterX.next()];
//				    	int iMaxX = iMinX;
//				    	int iMinY = i_dataValuesX[iterX.next()];
//				    	int iMaxY = iMinY;
//				    	
//				    	int iSumX = iMinX;
//				    	int iSumY = iMaxX;
//				    	
//				    	int i = 0;
//				    	while (( iterX.hasNext() )&&
//				    			( iterY.hasNext() )) {	    
//				    		int iBufferX = i_dataValuesX[iterX.next()];
//				        	int iBufferY = i_dataValuesY[iterY.next()];
//				        	
//				        	if (iBufferX < iMinX) { iMinX = iBufferX; }
//				        	else if ( iBufferX > iMaxX ) {iMaxX = iBufferX; }
//				        	
//				        	if (iBufferY < iMinY) { iMinY = iBufferY; }
//				        	else if ( iBufferY > iMaxY ) {iMaxY = iBufferY; }
//				        	
//				    		iSumX += iBufferX;
//				    		iSumY += iBufferY;
//				    		i++;
//				    	} //end: while (( iterX.hasNext() )&&( iterY.hasNext() )) {
//				    	
//				    	fMinValue[0] = iMinX;
//				    	fMaxValue[0] = iMaxX;
//				    	fMinValue[1] = iMinY;
//				    	fMaxValue[1] = iMaxY;
//				    	
//				    	fMeanValue[0] = (float) iSumX / (float) i;
//				    	fMeanValue[1] = (float) iSumY / (float) i;
//				    	
//				    	set.returnReadToken();	
//				    	bIsValid = true;
//				    	return true;
//			    	} //end: if (( i_dataValuesX != null )&&( i_dataValuesY != null )) {
//			    	
//			    	set.returnReadToken();
//			    	return false;
//		    	} //end: if ( set.getReadToken() ) {
//		    	
//		  } //end: if (set != null) {
//		  
//		  return false;
//	}
	
	
	public boolean updateData() 
	{
		bIsValid = false;
		
		if ( set != null ) 
		{
		   	
		  if ( set.getDimensions() < iDimensions ) 
		  {
	    		System.out.println("MinMaxDataInteger.updateData()  Can not use a ISet with only one dimension!");
	    		return false;
		  }
			  
		  if ( set.getReadToken() ) 
		  {
		    		
		  	//int iDimension = set.getDimensions();
		  		
		  	//this.allocateMinMax( iDimension );
		    		
		  	for ( int iIndex = 0; iIndex < iDimensions; iIndex++ ) 
		  	{		    					
		    	IVirtualArray select = set.getVirtualArrayByDimAndIndex(iIndex,0);		    			
		    	IVirtualArrayIterator iter = select.iterator();
		    	IStorage storage = set.getStorageByDimAndIndex(iIndex,0);
		    			
		    	int[] i_dataValues = storage.getArrayInt();
		    			
		    	if ( i_dataValues == null ) 
		    	{
		    		set.returnReadToken();
		    		return false;
		    	}
		    			
		    	int iMin = i_dataValues[iter.next()];
				int iMax = iMin;
				int iSum = iMin;
				    	
				int iCountAllItems = select.length();
				    	
				try 
				{
					while ( iter.hasNext() ) 
					{	    
						int iBuffer = i_dataValues[iter.next()];
					        	
					    if (iBuffer < iMin) { iMin = iBuffer; }
					    else if ( iBuffer > iMax ) {iMax = iBuffer; }
					    iSum += iBuffer;
					} //end: while (( iterX.hasNext() )&&( iterY.hasNext() )) {
				} catch (ArrayIndexOutOfBoundsException ae) 
				 	{
				    		iCountAllItems = iCountAllItems - iter.remaining();
				 	}
				    	
				 fMinValue[iIndex] = iMin;
				 fMaxValue[iIndex] = iMax;
				    	
				 fMeanValue[iIndex] = (float) iSum / (float) iCountAllItems;
				    	
				 iCountItmes[iIndex] = iCountAllItems;
				    	
		  	} // end: for ( int iIndex = 0; iIndex < iDimension; iIndex++ ) {
		    					    	
		    		
			set.returnReadToken();
			return true;
		  } //end: if ( set.getReadToken() ) {
		    	
		} //end: if (set != null) {
		  
		return false;
	}
	
	
	/**
	 * Defein a ISet.
	 * updateData() is called automatically.
	 * 
	 * @param useSet ISet to calcualte min max and mean value from.
	 */
	public void useSet( ISet useSet ) {
		set = useSet;		
		//allocateMinMax( useSet.getDimensions() );		
		bIsValid = updateData();	
	}
	
	public float getMin( int iAtDim ) {
		return fMinValue[iAtDim];
	}
	
	public float getMax( int iAtDim ) {
		return fMaxValue[iAtDim];
	}
	
	public float getMean( int iAtDim ) {
		return fMeanValue[iAtDim];
	}
	
	public int getItems( int iAtDim ) {
		return iCountItmes[iAtDim];
	}
	

}
