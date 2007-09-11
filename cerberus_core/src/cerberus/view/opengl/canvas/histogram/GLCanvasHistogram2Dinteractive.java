/**
 * 
 */
package cerberus.view.opengl.canvas.histogram;

import java.util.HashMap;

import cerberus.manager.IGeneralManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.opengl.canvas.histogram.HistogramContainer;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasHistogram2Dinteractive extends GLCanvasHistogram2D {

	private int iSizeOfGroups = 1;
	
	protected HashMap <Integer,Integer> hashSelectionNCBI_2_index;
	
	protected HashMap <Integer,HistogramContainer> hashSelectionId2HistogramContainer;
	
	protected HistogramContainer [] arrayHistogramContainer;
	
	/**
	 * @param setGeneralManager
	 * @param viewId
	 * @param parentContainerId
	 * @param label
	 */
	public GLCanvasHistogram2Dinteractive(IGeneralManager setGeneralManager,
			int viewId,
			int parentContainerId,
			String label) {

		super(setGeneralManager, viewId, parentContainerId, label);
		
	}
	
	private void createHistoram_IdContainer(final int iSize) {
		
		if ( hashSelectionNCBI_2_index == null ) {
			hashSelectionNCBI_2_index = new HashMap <Integer,Integer> (iSize);
		}
	}
	
	 public void setHistogramLength( final int iSetLength ) {
		 
		 if ( arrayHistogramContainer == null ) 
		 {
			 arrayHistogramContainer = new HistogramContainer [iSetLength];
			 for (int i=0; i<iSetLength; i++) {
				 arrayHistogramContainer[i] = new HistogramContainer(iSizeOfGroups);
			 }
		 }
		 else
		 {
			 if  (iSetLength != arrayHistogramContainer.length) {
				 
				 HistogramContainer [] arrayHistogramContainer_buffer = 
					 new HistogramContainer [iSetLength];
				 
				 if ( arrayHistogramContainer.length < iSetLength ) 
				 {
					 System.arraycopy(arrayHistogramContainer, 
							 0, 
							 arrayHistogramContainer_buffer, 
							 0, 
							 arrayHistogramContainer.length );
					 
					 /* initialize new containers */
					 for ( int i=arrayHistogramContainer.length; i<iSetLength ;i++)
					 {
						 arrayHistogramContainer_buffer[i] =
							 new HistogramContainer(iSizeOfGroups);
					 }
				 }
				 else 
				 {
					 System.arraycopy(this.arrayHistogramContainer, 
							 0, 
							 arrayHistogramContainer_buffer, 
							 0, 
							 iSetLength );
					 
					/* remaining containers are deleted by garbage collector. */
				 }
				 
				 /* swap arrays.. */
				 arrayHistogramContainer = arrayHistogramContainer_buffer;
			 }
		 }
		 super.setHistogramLength(iSetLength);
	 }

	
	/**
	 * @return the iSizeOfGrous
	 */
	public final int getSizeOfGrous() {
	
		return iSizeOfGroups;
	}

	
	/**
	 * @param sizeOfGrous the iSizeOfGrous to set
	 */
	public final void setSizeOfGroups(final int sizeOfGrous) {
	
		if ( sizeOfGrous < 1 ) {
			throw new GeneViewRuntimeException(this.getClass().getSimpleName() + 
					".setSizeOfGrous() called with value smaller than 1!");
		}
		
		iSizeOfGroups = sizeOfGrous;
		
		/* update size of group containers for each container.. */
		if ( arrayHistogramContainer[0].length() != sizeOfGrous) 
		{
			 for (int i=0; i<arrayHistogramContainer.length; i++)
			 {
				 arrayHistogramContainer[i].setSelectionGroupSize(sizeOfGrous);
			 }
		}
	}

}
