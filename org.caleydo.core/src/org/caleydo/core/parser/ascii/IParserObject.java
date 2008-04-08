/**
 * 
 */
package org.caleydo.core.parser.ascii;

/**
 * Provides methods vor initialization aund cleanup of parser.
 * 
 * @author Michael Kalkusch
 *
 */
public interface IParserObject
{

	/**
	 * Initialize parser.
	 *
	 */
	public abstract void init();
	
	/**
	 * Cleanup parser.
	 *
	 */
	public abstract void destroy();
	
}
