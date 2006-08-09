/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.math.statistics.histogram_old;

import cerberus.data.IStatefulItem;
import cerberus.data.collection.Set;


/**
 * Creation of a hisogram.
 * 
 * @author Michael Kalkusch
 *
 */
public interface HistogramCreator extends IStatefulItem {

	/**
	 * Remove all content and data.	 
	 */
	public void clear();
	
	/**
	 * Create the histogram.
	 * 
	 * @see prometheus.data.IStatefulItem#updateState()
	 */
	public void updateState();
	
	/**
	 * Get the histogram information.
	 * 
	 * @return
	 */
	public int[] getCounterPerRow();
	
	/**
	 * Get the number of itmes in the row with the most items per row.
	 * 
	 * @return number of items in row with most items.
	 */
	public int getMaxCountPerRow();
		
	/**
	 * Set the number of rows the historgam is based on.
	 * 
	 * @return number of rows
	 */
	public int getRowWidth();
	
	/**
	 * Set the number of rows.
	 * 
	 * Note: update() must be called to recreate the histogram.
	 * 
	 * @param iSetRowWidth
	 */
	public void setRowWidth( int iSetRowWidth );

	/**
	 * Get the reference to the selection, that is the base for this histogram.
	 * 
	 * @return selection the histogram is bound to.
	 */
	public Set getSet();
	
	/**
	 * Set the Selection the histogram is based on.
	 * 
	 * @param setSelection reference to the Selection the histogram is based on
	 */
	public void setSet( Set setSet );
	
	/**
	 * Get minumum value.
	 * 
	 * @return minumum value
	 */
	public int getMinValueI();
	
	/**
	 * Get maximum Value.
	 * 
	 * @return maximum value
	 */
	public int getMaxValueI();
	
	/**
	 * Get minumum value.
	 * 
	 * @return minumum value
	 */
	public float getMinValueF();
	
	/**
	 * Get maximum Value.
	 * 
	 * @return maximum value
	 */
	public float getMaxValueF();
	
}
