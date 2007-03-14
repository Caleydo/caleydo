/**
 * 
 */
package cerberus.math.statistics.minmax;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
//import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;

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
	
	// protected SetMultiDim refSet = null;
	
	protected ISet refSet = null;
	
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
//		  if ( refSet != null ) {
//		    	
//			  if ( refSet.getDimensions() < iDimensions ) {
//	    			System.out.println("MinMaxDataInteger.updateDataOld() deprecated methode! Can not use a ISet with only one dimension!");
//	    			return false;
//			  }
//			  
//		    	if ( refSet.getReadToken() ) {
//		    		
//		    		//allocateMinMax( refSet.getDimensions() );
//		    	
//			    	IStorage refStorageX = refSet.getStorageByDimAndIndex(0,0);
//			    	IStorage refStorageY = refSet.getStorageByDimAndIndex(1,0);
//			    			    	
//			    	int[] i_dataValuesX = refStorageX.getArrayInt();
//			    	int[] i_dataValuesY = refStorageY.getArrayInt();
//			    	
//			    	if (( i_dataValuesX != null )&&
//			    			( i_dataValuesY != null )) {
//			    		
//				    	IVirtualArrayIterator iterX = refSet.getVirtualArrayByDimAndIndex(0,0).iterator();
//				    	IVirtualArrayIterator iterY = refSet.getVirtualArrayByDimAndIndex(1,0).iterator();
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
//				    	refSet.returnReadToken();	
//				    	bIsValid = true;
//				    	return true;
//			    	} //end: if (( i_dataValuesX != null )&&( i_dataValuesY != null )) {
//			    	
//			    	refSet.returnReadToken();
//			    	return false;
//		    	} //end: if ( refSet.getReadToken() ) {
//		    	
//		  } //end: if (refSet != null) {
//		  
//		  return false;
//	}
	
	
	public boolean updateData() {
		
		bIsValid = false;
		
		  if ( refSet != null ) {
		    	
			  if ( refSet.getDimensions() < iDimensions ) {
	    			System.out.println("MinMaxDataInteger.updateData()  Can not use a ISet with only one dimension!");
	    			return false;
			  }
			  
		    	if ( refSet.getReadToken() ) {
		    		
		    		//int iDimension = refSet.getDimensions();
		    		
		    		//this.allocateMinMax( iDimension );
		    		
		    		for ( int iIndex = 0; iIndex < iDimensions; iIndex++ ) {
		    					
		    			IVirtualArray select = refSet.getVirtualArrayByDimAndIndex(iIndex,0);		    			
		    			IVirtualArrayIterator iter = select.iterator();
		    			IStorage refStorage = 
		    				refSet.getStorageByDimAndIndex(iIndex,0);
		    			
		    			int[] i_dataValues = refStorage.getArrayInt();
		    			
		    			if ( i_dataValues == null ) {
		    				refSet.returnReadToken();
		    				return false;
		    			}
		    			
		    			int iMin = i_dataValues[iter.next()];
				    	int iMax = iMin;
				    	int iSum = iMin;
				    	
				    	int iCountAllItems = select.length();
				    	
				    	try {
					    	while ( iter.hasNext() ) {	    
					    		int iBuffer = i_dataValues[iter.next()];
					        	
					        	if (iBuffer < iMin) { iMin = iBuffer; }
					        	else if ( iBuffer > iMax ) {iMax = iBuffer; }
					    		iSum += iBuffer;
					    	} //end: while (( iterX.hasNext() )&&( iterY.hasNext() )) {
				    	} catch (ArrayIndexOutOfBoundsException ae) {
				    		iCountAllItems = iCountAllItems - iter.remaining();
				    	}
				    	
				    	fMinValue[iIndex] = iMin;
				    	fMaxValue[iIndex] = iMax;
				    	
				    	fMeanValue[iIndex] = (float) iSum / (float) iCountAllItems;
				    	
				    	iCountItmes[iIndex] = iCountAllItems;
				    	
		    		} // end: for ( int iIndex = 0; iIndex < iDimension; iIndex++ ) {
		    					    	
		    		
			    	refSet.returnReadToken();
			    	return true;
		    	} //end: if ( refSet.getReadToken() ) {
		    	
		  } //end: if (refSet != null) {
		  
		  return false;
	}
	
	
	/**
	 * Defein a ISet.
	 * updateData() is called automatically.
	 * 
	 * @param useSet ISet to calcualte min max and mean value from.
	 */
	public void useSet( ISet useSet ) {
		refSet = useSet;		
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
