
package cerberus.manager.event.mediator;

import cerberus.data.collection.selection.SetSelection;

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
	public void update(Object eventTrigger);	
	
	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger Calling object, that created the update
	 * @param updatedSelectionSet Set containing update information
	 */
	public void updateSelection(Object eventTrigger, 
			SetSelection updatedSelectionSet);
	
	//public void updateViewingData(Object eventTrigger, ***);
}
