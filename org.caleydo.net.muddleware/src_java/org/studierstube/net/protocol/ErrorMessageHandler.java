/**
 * 
 */
package org.studierstube.net.protocol;

/**
 * Interface for handling error messages.
 * 
 * Message, Operation ,Header and Footer objects use 
 * this Interface to export error and logging informations.
 * 
 * Note: JAVA only
 * 
 * @author Michael Kalkusch
 *
 * @see org.studierstube.util.LogInterface
 */
public interface ErrorMessageHandler {

	/**
	 * Handles an error message.
	 * Note: forwareded to org.studierstube.util.LogInterface#log(Object, String)
	 * 
	 * @param cl object logging the error
	 * @param msg error message
	 * 
	 * @see org.studierstube.util.LogInterface#log(Object, String)
	 */
	public void logMsg(Object cl, String msg);
}
