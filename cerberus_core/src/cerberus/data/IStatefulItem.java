/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

/**
 * Defines an item handling a internal state.
 * 
 * @author Michael Kalkusch
 *
 */
public interface IStatefulItem {

	/**
	 * Update the internal state.
	 *
	 */
	public void updateState();
	
}
