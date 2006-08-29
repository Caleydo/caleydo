
package cerberus.manager.event.mediator;

/**
 * Object that shall receive an event.
 * 
 * @author kalkusch
 *
 */
public interface IMediatorReceiver
{
	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger Calling object, that created the update
	 */
	public void update( Object eventTrigger );	
}
