/**
 * 
 */
package cerberus.xml.parser.manager;

import org.xml.sax.ContentHandler;


/**
 * @author kalkusch
 *
 */
public interface IXmlParserHandler
extends ContentHandler
{

	/**
	 * Initilisation of handler.
	 * Called once by Manager before using the handler.
	 *
	 * @see cerberus.xml.parser.manager.IXmlParserManager#registerSaxHandler(IXmlParserHandler, boolean)
	 */
	public void initHandler();
	
	
	/**
	 * Cleanup called by Mananger after Handler is not used any more. 
	 */
	public void destroyHandler();
	
	/**
	 * 
	 * @return
	 */
	public String getXmlActivationTag();
	
	//public boolean setXmlActivationTag( final String sXmlActivationTag );
	
	/**
	 * @see cerberus.xml.parser.manager.IXmlParserManager
	 * if TURE the manager can destroy the SaxHandler after reading teh closing tag.
	 * 
	 * @return TRUE if the opening activation tag only exists once in teh XML file.
	 */
	public boolean hasOpeningTagOnlyOnce();
}
