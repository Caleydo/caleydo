/**
 * 
 */
package org.caleydo.core.parser.xml.sax.handler;

//import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
			throw new CaleydoRuntimeException("setXmlActivationTag() tag must be at least one char!",
					CaleydoRuntimeExceptionType.SAXPARSER);
		}
		
		this.sOpeningTag = tag;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.parser.handler.IXmlParserHandler#getXmlActivationTag()
	 */
	public final String getXmlActivationTag()
	{
		return sOpeningTag;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.parser.handler.IXmlParserHandler#hasOpeningTagOnlyOnce()
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
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#initHandler()
	 */
	public void initHandler()
	{
		refGeneralManager.getSingleton().logMsg(
				this.getClass().getSimpleName() + 
				": initHandler", LoggerType.VERBOSE_EXTRA );
	}
	
	/**
	 * Sends init message to logger.
	 * 
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#destroyHandler()
	 */
	public void destroyHandler()
	{
		refGeneralManager.getSingleton().logMsg(
				this.getClass().getSimpleName() + 
				": destroyHandler", 
				LoggerType.VERBOSE );
	}
}
