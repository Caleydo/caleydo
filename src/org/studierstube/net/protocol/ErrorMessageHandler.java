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
 */
public interface ErrorMessageHandler {

	/**
	 * Handles an error message.
	 * 
	 * @param text text of error message
	 * @param newLine TRUE for add new line
	 */
	public void logMsg( String text, boolean newLine );
}
