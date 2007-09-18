/**
 * 
 */
package org.geneview.core.parser.xml.sax.handler;

//import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IXmlParserManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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
	 * @see org.geneview.core.parser.handler.IXmlParserHandler#getXmlActivationTag()
	 */
	public final String getXmlActivationTag()
	{
		return sOpeningTag;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.parser.handler.IXmlParserHandler#hasOpeningTagOnlyOnce()
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
	 * @see org.geneview.core.parser.xml.sax.handler.IXmlParserHandler#initHandler()
	 */
	public void initHandler()
	{
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": initHandler", LoggerType.VERBOSE_EXTRA );
	}
	
	/**
	 * Sends init message to logger.
	 * 
	 * @see org.geneview.core.parser.xml.sax.handler.IXmlParserHandler#destroyHandler()
	 */
	public void destroyHandler()
	{
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": destroyHandler", 
				LoggerType.VERBOSE );
	}
}
