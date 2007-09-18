/**
 * 
 */
package org.geneview.core.view.opengl.canvas.histogram;

import java.util.ArrayList;


/**
 * @author Michael Kalkusch
 *
 */
public class HistogramContainerIndexAware extends HistogramContainer {

	protected ArrayList <Integer> alIndex_inContainer;
	
	/**
	 * @param sizeOfGroups
	 * @param totalItemsPerContainer
	 */
	public HistogramContainerIndexAware(int sizeOfGroups,
			int totalItemsPerContainer) {

		super(sizeOfGroups, totalItemsPerContainer);
		
		alIndex_inContainer = new ArrayList <Integer> (25);
	}

	/**
	 * @param sizeOfGroups
	 */
	public HistogramContainerIndexAware(int sizeOfGroups) {

		this(sizeOfGroups,0);
	}
	
	/**
	 * Adds an index and thereby increments size of container; 
	 * Call updateTotalItems_perContainer() after adding the last value;
	 * updateTotalItems_perContainer() will call setTotalItems_perContainer(int) internally
	 * 
	 * @see HistogramContainerIndexAware#updateTotalItems_perContainer()
	 * @see org.geneview.core.view.opengl.canvas.histogram.HistogramContainer#setTotalItems_perContainer(int)
	 * 
	 * @param index
	 */
	public void incContainer_addIndexOfValue(int index) {
		alIndex_inContainer.add(index);
	}
	
	/**
	 * @Override method from super class; redirect it to updateTotalItems_perContainer()
	 * 
	 * @see HistogramContainerIndexAware#updateTotalItems_perContainer()
	 * @see org.geneview.core.view.opengl.canvas.histogram.HistogramContainer#setTotalItems_perContainer(int)
	 */
	@Override
	public final void setTotalItems_perContainer(final int totalItems_perContainer) {
		this.updateTotalItems_perContainer();
	}
	
	/**
	 * converts number of added indices to total size.
	 * 
	 * @see HistogramContainerIndexAware#incContainer_addIndexOfValue(int)
	 */
	public void updateTotalItems_perContainer() {
		this.setTotalItems_perContainer(this.alIndex_inContainer.size());
	}
	
	/**
	 * Expose internal data structure containing a list of all indices added to this container.
	 * 
	 * @see HistogramContainerIndexAware#incContainer_addIndexOfValue(int)
	 * 
	 * @return expose internal data structure; handle it as "final" variable and do not alter it!
	 */
	public final ArrayList <Integer> exposeIndices() {
		return alIndex_inContainer;
	}

}
