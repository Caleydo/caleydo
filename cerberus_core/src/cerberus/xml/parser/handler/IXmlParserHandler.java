/**
 * 
 */
package cerberus.xml.parser.handler;

//import org.xml.sax.Attributes;
import cerberus.xml.parser.handler.IXmlBaseHandler;

//import cerberus.xml.parser.manager.IXmlParserManager;


/**
 * Interface for all XML Handler's registered 
 * to the cerberus.manager.IXmlParserManager
 * 
 * @see cerberus.manager.IXmlParserManager
 *  
 * @author Michael Kalkusch
 *
 */
public interface IXmlParserHandler
extends IXmlBaseHandler
{


	
	/**
	 * Initilisation of handler.
	 * Called once by Manager before using the handler.
	 *
	 * @see cerberus.manager.IXmlParserManager#registerAndInitSaxHandler(IXmlParserHandler)
	 */
	public void initHandler();
	
	
	/**
	 * Cleanup called by Mananger after Handler is not used any more. 
	 */
	public void destroyHandler();
	
	/**
	 * Get the XmlActivationTag, which makes this Handler the current XMLHandler, 
	 * that receives all events from the cerberus.manager.IXmlParserManager.
	 * XmlActivationTag is set via the Constructor.
	 * 
	 * @return tag that enables this Handler inside the cerberus.manager.IXmlParserManager
	 */
	public String getXmlActivationTag();
	
	//public boolean setXmlActivationTag( final String sXmlActivationTag );
	
	/**
	 * TRUE if handler is destoryed after activation tag is closed.
	 * 
	 * @see cerberus.xml.parser.manager.IXmlParserHandler#setHandlerDestoryedAfterClosingTag(boolean)
	 * @see cerberus.manager.IXmlParserManager	 
	 * 
	 * @return TRUE if handler is destoryed after activation tag is closed
	 */
	public boolean isHandlerDestoryedAfterClosingTag();
	
	
	/**
	 * TRUE if handler is destoryed after activation tag is closed.
	 * 
	 * @see cerberus.xml.parser.manager.IXmlParserHandler#isHandlerDestoryedAfterClosingTag()
	 * @param setHandlerDestoryedAfterClosingTag
	 */
	public void setHandlerDestoryedAfterClosingTag( 
			final boolean setHandlerDestoryedAfterClosingTag );
}
