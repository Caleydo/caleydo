/**
 * 
 */
package cerberus.xml.parser;

/**
 * Provides methodes vor initialization aund cleanup of parser.
 * 
 * @author kalkusch
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
