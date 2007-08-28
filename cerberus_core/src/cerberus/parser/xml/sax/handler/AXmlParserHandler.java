/**
 * 
 */
package cerberus.parser.handler;

//import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IXmlParserManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.GeneViewRuntimeExceptionType;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AXmlParserHandler 
extends DefaultHandler 
implements IXmlParserHandler
{
	private boolean bDestroyHandlerAfterClosingTag = false;
		
	protected final IGeneralManager refGeneralManager;
	
	protected final IXmlParserManager refXmlParserManager;
	
	protected String sOpeningTag = "";

	/**
	 * 
	 */
	protected AXmlParserHandler( final IGeneralManager refGeneralManager,
			final IXmlParserManager refXmlParserManager )
	{
		this.refGeneralManager = refGeneralManager;
		this.refXmlParserManager = refXmlParserManager;
	}


	public final void setXmlActivationTag( final String tag)
	{
		assert tag != null : "can not assing null as tag";
		
		if ( tag.length() < 2 ) {
			throw new GeneViewRuntimeException("setXmlActivationTag() tag must be at least one char!",
					GeneViewRuntimeExceptionType.SAXPARSER);
		}
		
		this.sOpeningTag = tag;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.parser.handler.IXmlParserHandler#getXmlActivationTag()
	 */
	public final String getXmlActivationTag()
	{
		return sOpeningTag;
	}

	/* (non-Javadoc)
	 * @see cerberus.parser.handler.IXmlParserHandler#hasOpeningTagOnlyOnce()
	 */
	public final boolean isHandlerDestoryedAfterClosingTag()
	{
		if ( bDestroyHandlerAfterClosingTag ) 
		{
			return true;
		}
		
		return false;
	}
	
	public final void setHandlerDestoryedAfterClosingTag( 
			final boolean setHandlerDestoryedAfterClosingTag )
	{
		this.bDestroyHandlerAfterClosingTag = setHandlerDestoryedAfterClosingTag;
	}
	
	/**
	 * Sends init message to logger.
	 * 
	 * @see cerberus.parser.handler.IXmlParserHandler#initHandler()
	 */
	public void initHandler()
	{
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": initHandler", LoggerType.VERBOSE );
	}
	
	/**
	 * Sends init message to logger.
	 * 
	 * @see cerberus.parser.handler.IXmlParserHandler#destroyHandler()
	 */
	public void destroyHandler()
	{
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": destroyHandler", 
				LoggerType.VERBOSE );
	}
}
