/**
 * 
 */
package cerberus.xml.parser.handler;

import org.xml.sax.helpers.DefaultHandler;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.manager.IXmlParserManager;

/**
 * @author kalkusch
 *
 */
public abstract class AXmlParserHandler 
extends DefaultHandler 
implements IXmlParserHandler
{
	protected final IGeneralManager refGeneralManager;
	
	protected final IXmlParserManager refXmlParserManager;
	
	protected String sOpeningTag = "";
	
	protected boolean bHasOpeningTagOnlyOnce = true;
	
	
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
			throw new CerberusRuntimeException("setXmlActivationTag() tag must be at least one char!",
					CerberusExceptionType.SAXPARSER);
		}
		
		this.sOpeningTag = tag;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#getXmlActivationTag()
	 */
	public final String getXmlActivationTag()
	{
		return sOpeningTag;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#hasOpeningTagOnlyOnce()
	 */
	public final boolean hasOpeningTagOnlyOnce()
	{
		if ( bHasOpeningTagOnlyOnce ) 
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sends init message to logger.
	 * 
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#initHandler()
	 */
	public void initHandler()
	{
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				this.getClass().getSimpleName() + 
				": initHandler", LoggerType.VERBOSE );
	}
	
	/**
	 * Sends init message to logger.
	 * 
	 * @see cerberus.xml.parser.handler.IXmlParserHandler#destroyHandler()
	 */
	public void destroyHandler()
	{
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				this.getClass().getSimpleName() + 
				": destroyHandler", 
				LoggerType.VERBOSE );
	}


}
