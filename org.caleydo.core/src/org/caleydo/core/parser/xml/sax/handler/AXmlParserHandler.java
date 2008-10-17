package org.caleydo.core.parser.xml.sax.handler;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
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
	protected AXmlParserHandler()
	{
		this.generalManager = GeneralManager.get();
		this.xmlParserManager = generalManager.getXmlParserManager();
	}

	public final void setXmlActivationTag(final String tag)
	{
		if (tag.length() < 2)
		{
			throw new IllegalStateException(
					"setXmlActivationTag() tag must be at least one char!");
		}

		this.sOpeningTag = tag;
	}

	@Override
	public final String getXmlActivationTag()
	{

		return sOpeningTag;
	}

	@Override
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
