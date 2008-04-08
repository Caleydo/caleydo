/**
 * 
 */
package org.caleydo.core.view.opengl.canvas.histogram;

import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Container for each element of the histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class HistogramContainer {
	
	/**
	 * Values are in the range of [0.0f .. 1.0f]
	 */
	public float [] fSelectedPercentage_perGroup;
	
	/**
	 * Values are in the range of [0.. MAX]
	 */
	protected int [] iSelectedItems_perGroup;
	
	/**
	 * Indicate if container is empty or not; used for calculation of percentage to avoid division by zero.
	 * Hide this to ensure using the setter
	 * 
	 * @see HistogramContainer#fTotalItems_perContainer
	 * @see HistogramContainer#setTotalItems_perContainer(int)
	 * @see HistogramContainer#getTotalItems_perContainer()
	 * @see HistogramContainer#selectionUpdatePercentagePerGroup(int)
	 */
	private boolean bTotalItems_ContainerIsEmpty;
	
	/**
	 * Total number of items in this container;
	 * HistogramContainer#iTotalItems_perContainer cast to (float)
	 * 
	 * @see HistogramContainer#bTotalItems_ContainerIsEmpty
	 * @see HistogramContainer#setTotalItems_perContainer(int)
	 * @see HistogramContainer#getTotalItems_perContainer()
	 */
	protected float fTotalItems_perContainer;
	
	/**
	 * 
	 */
	public HistogramContainer( final int iSizeOfGroups, final int iTotalItemsPerContainer ) {

		int iInitialSize = iSizeOfGroups;
		
		if ( iInitialSize < 1 ) 
		{
			iInitialSize = 1;
		}
		
		this.fSelectedPercentage_perGroup = new float[iSizeOfGroups];
		
		this.setSelectionGroupSize(iSizeOfGroups);
		this.setTotalItems_perContainer(0);
	}
	
	/**
	 * 
	 */
	public HistogramContainer( final int iSizeOfGroups ) {

		this(iSizeOfGroups,0);
	}
	
	/**
	 * Get number of groups.
	 * 
	 * @return
	 */
	public final int length() {
		return iSelectedItems_perGroup.length;
	}

	
	/**
	 * Get total number of items in this container.
	 * 
	 * @see HistogramContainer#setTotalItems_perContainer(int)
	 * 
	 * 
	 * @return the iTotalItems_perContainer
	 */
	public final int getTotalItems_perContainer() {
	
		return (int) fTotalItems_perContainer;
	}

	
	/**
	 * Always use setter!
	 * 
	 * @see HistogramContainer#getTotalItems_perContainer()
	 * 
	 * @param totalItems_perContainer the iTotalItems_perContainer to set
	 */
	public void setTotalItems_perContainer(final int totalItems_perContainer) {
	
		if ( totalItems_perContainer == 0 ) 
		{
			bTotalItems_ContainerIsEmpty = true;
		} 
		else
		{
			bTotalItems_ContainerIsEmpty = false;
		}
		
		fTotalItems_perContainer = (float) totalItems_perContainer;
	}
	
	/**
	 * Set number of groups. Also updates percentages, if size was changed.
	 * 
	 * @param iNumberOfGroups set number of new groups
	 */
	public void setSelectionGroupSize( final int iNumberOfGroups ) {
		
		if ( iNumberOfGroups < 1) {
			throw new CaleydoRuntimeException(this.getClass().getSimpleName() + ".setGroupSize( " + iNumberOfGroups + ") is out of range!");
		}
		
		if ( iSelectedItems_perGroup.length == iNumberOfGroups ) 
		{
			/* size already fits */
			return;
		}
		
		float [] fSelectedPercentage_perGroup_Buffer = new float[iNumberOfGroups];		
		int [] iSelectedItems_perGroup_Buffer = new int [iNumberOfGroups];
		
		/* default: new size is smaller than old size; in this case other values are truncated */
		int iTargetSize = iNumberOfGroups;
		
		if ( iSelectedItems_perGroup.length > iNumberOfGroups ) 
		{
			/* new size is bigger than old array length.. */
			/* use old array length for copying data.. */
			iTargetSize = iSelectedItems_perGroup.length;
		}
		
		/* is smaller; truncate! */
		for (int i=0; i< iTargetSize; i++) 
		{
			fSelectedPercentage_perGroup_Buffer[i] = this.fSelectedPercentage_perGroup[i];
			iSelectedItems_perGroup_Buffer[i] = this.iSelectedItems_perGroup[i];
		}
		
		this.selectionUpdatePercentageAll();
	}
	
	/**
	 * Increments selectedItem per group. Does not update percentage. 
	 * Note: updatePercentageAll() or updatePercentagePerGroup(int) must be called 
	 * after calling this method before reading the percentage values.
	 * 
	 * @see HistogramContainer#selectionUpdatePercentageAll()
	 * @see HistogramContainer#selectionUpdatePercentagePerGroup(int)
	 * 
	 * @param iSize
	 * @param iGroupIndex
	 */
	public void selectionIncrementValuePerGroupIndex( final int iIncrement, final int iGroupIndex ) {
		
		iSelectedItems_perGroup[iGroupIndex] += iIncrement;
		
	}
	
	/**
	 * Increments selectedItem per group and updates the group.
	 *  
	 * @see HistogramContainer#selectionUpdatePercentagePerGroup(int)
	 *  
	 * @param iSize
	 * @param iGroupIndex
	 */
	public void selectionIncrementValuePerGroupIndex_updatePercentage( final int iIncrement, final int iGroupIndex ) {
		
		iSelectedItems_perGroup[iGroupIndex] += iIncrement;
		this.selectionUpdatePercentagePerGroup(iGroupIndex);
	}
	
	/**
	 * Update percentage of all groups.
	 * 
	 * @param iSize
	 * @param iGroupIndex
	 */
	public void selectionUpdatePercentageAll() {
		
		for ( int i=0; i < iSelectedItems_perGroup.length; i++) 
		{
			selectionUpdatePercentagePerGroup(i);
		}
	}
	
	/**
	 * update percentage of one group only.
	 * 
	 * @see HistogramContainer#selectionIncrementValuePerGroupIndex(int, int)
	 * 
	 * @param iSize
	 * @param iGroupIndex
	 */
	public void selectionUpdatePercentagePerGroup( final int iGroupIndex ) {
		
		if ( bTotalItems_ContainerIsEmpty ) 
		{
			fSelectedPercentage_perGroup[iGroupIndex] = 0.0f;
			return;
		}
		
		/* no division by zero is possible due to bTotalItems_ContainerIsEmpty */
		fSelectedPercentage_perGroup[iGroupIndex] = 
			(float) iSelectedItems_perGroup[iGroupIndex] / fTotalItems_perContainer;
	}

}
