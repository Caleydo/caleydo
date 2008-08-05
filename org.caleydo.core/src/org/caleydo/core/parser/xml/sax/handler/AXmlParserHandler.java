package org.caleydo.core.parser.xml.sax.handler;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AXmlParserHandler
	extends DefaultHandler
	implements IXmlParserHandler
{

	private boolean bDestroyHandlerAfterClosingTag = false;

	protected final IGeneralManager generalManager;

	protected final IXmlParserManager xmlParserManager;

	protected String sOpeningTag = "";

	/**
	 * Constructor.
	 */
	protected AXmlParserHandler(final IGeneralManager generalManager,
			final IXmlParserManager xmlParserManager)
	{
		this.generalManager = generalManager;
		this.xmlParserManager = xmlParserManager;
	}

	public final void setXmlActivationTag(final String tag)
	{
		if (tag.length() < 2)
		{
			throw new CaleydoRuntimeException(
					"setXmlActivationTag() tag must be at least one char!",
					CaleydoRuntimeExceptionType.SAXPARSER);
		}

		this.sOpeningTag = tag;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.parser.handler.IXmlParserHandler#getXmlActivationTag()
	 */
	public final String getXmlActivationTag()
	{

		return sOpeningTag;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.parser.handler.IXmlParserHandler#hasOpeningTagOnlyOnce()
	 */
	public final boolean isHandlerDestoryedAfterClosingTag()
	{
		if (bDestroyHandlerAfterClosingTag)
		{
			return true;
		}

		return false;
	}

	public final void setHandlerDestoryedAfterClosingTag(
			final boolean setHandlerDestoryedAfterClosingTag)
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

		// generalManager.logMsg(
		// this.getClass().getSimpleName() +
		// ": initHandler", LoggerType.VERBOSE_EXTRA );
	}

	/**
	 * Sends init message to logger.
	 * 
	 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler#destroyHandler()
	 */
	public void destroyHandler()
	{

		// generalManager.logMsg(
		// this.getClass().getSimpleName() +
		// ": destroyHandler",
		// LoggerType.VERBOSE );
	}
}
