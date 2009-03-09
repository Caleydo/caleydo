package org.caleydo.core.parser.xml.sax.handler;

/**
 * Interface for all XML Handler's registered to the org.caleydo.core.manager.IXmlParserManager
 * 
 * @see org.caleydo.core.manager.IXmlParserManager
 * @author Michael Kalkusch
 */
public interface IXmlParserHandler
	extends IXmlBaseHandler {
	/**
	 * Initilization of handler. Called once by Manager before using the handler.
	 * 
	 * @see org.caleydo.core.manager.IXmlParserManager#registerAndInitSaxHandler(IXmlParserHandler)
	 */
	public void initHandler();

	/**
	 * Cleanup called by Manager after Handler is not used any more.
	 */
	public void destroyHandler();

	/**
	 * Get the XmlActivationTag, which makes this Handler the current XMLHandler, that receives all events from
	 * the org.caleydo.core.manager.IXmlParserManager. XmlActivationTag is set via the Constructor.
	 * 
	 * @return tag that enables this Handler inside the org.caleydo.core.manager.IXmlParserManager
	 */
	public String getXmlActivationTag();

	// public boolean setXmlActivationTag( final String sXmlActivationTag );

	/**
	 * TRUE if handler is destoryed after activation tag is closed.
	 * 
	 * @see org.caleydo.core.xml.parser.manager.IXmlParserHandler#setHandlerDestoryedAfterClosingTag(boolean)
	 * @see org.caleydo.core.manager.IXmlParserManager
	 * @return TRUE if handler is destoryed after activation tag is closed
	 */
	public boolean isHandlerDestoryedAfterClosingTag();

	/**
	 * TRUE if handler is destoryed after activation tag is closed.
	 * 
	 * @see org.caleydo.core.xml.parser.manager.IXmlParserHandler#isHandlerDestoryedAfterClosingTag()
	 * @param setHandlerDestoryedAfterClosingTag
	 */
	public void setHandlerDestoryedAfterClosingTag(final boolean setHandlerDestoryedAfterClosingTag);

}
