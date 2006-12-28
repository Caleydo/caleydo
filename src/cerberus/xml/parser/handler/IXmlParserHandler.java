/**
 * 
 */
package cerberus.xml.parser.handler;

//import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

//import cerberus.xml.parser.manager.IXmlParserManager;


/**
 * @author Michael Kalkusch
 *
 */
public interface IXmlParserHandler
extends ContentHandler
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
	 * 
	 * @return
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
