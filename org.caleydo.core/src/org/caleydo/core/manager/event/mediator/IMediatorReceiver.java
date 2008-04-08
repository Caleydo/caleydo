
package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.collection.ISet;

/**
 * Object that shall receive an event.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 	
 */
public interface IMediatorReceiver {
	
	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger Calling object, that created the update
	 */
	public void updateReceiver(Object eventTrigger);	
	
	
	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger Calling object, that created the update
	 * @param updatedSet Set containing update information
	 */
	public void updateReceiver(Object eventTrigger, 
			ISet updatedSet);
	
}
