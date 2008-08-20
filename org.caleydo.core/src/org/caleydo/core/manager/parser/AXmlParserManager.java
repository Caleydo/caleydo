package org.caleydo.core.manager.parser;

import java.util.Hashtable;
import java.util.LinkedList;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Abstract parser manager.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AXmlParserManager
	extends DefaultHandler
	implements IXmlParserManager
{
	protected IGeneralManager generalManager;
	
	/**
	 * Token to avoid registering and unregistering handlers during processing
	 * XMl data.
	 */
	private boolean bProcessingXmlDataNow = false;

	/**
	 * Defines the active handler if the opening tag was found. If no opening
	 * tag was found or the closing tag was precessed this reference is null.
	 */
	protected IXmlParserHandler currentHandler = null;

	// protected String sCurrentClosingTag = "";

	/**
	 * Contains the Handler of the previous opening tags. If a new tag is opened
	 * the currentHandler is stored as last element of this vector. If a closing
	 * tag is processed the currentHandler is set to the last element in the
	 * vector and the last element in the vector is removed.
	 */
	protected LinkedList<IXmlParserHandler> llXmlParserStack;

	/**
	 * Hashtable of handlers and registered XMl Tags bound to them.
	 */
	protected Hashtable<String, IXmlParserHandler> hashTag2XmlParser;

	/**
	 * Constructor.
	 */
	protected AXmlParserManager()
	{
		generalManager = GeneralManager.get();
		
		hashTag2XmlParser = new Hashtable<String, IXmlParserHandler>();
		llXmlParserStack = new LinkedList<IXmlParserHandler>();
	}

	protected final void setXmlFileProcessedNow(boolean bStatus)
	{
		this.bProcessingXmlDataNow = bStatus;
	}

	protected final boolean closeCurrentTag()
	{

		if (this.currentHandler == null)
		{
			throw new CaleydoRuntimeException(
					"AXmlParserManager.closeCurrentTag() current handler is null! Can not close handler");
			// return false;
		}

		IXmlParserHandler buffer = currentHandler;

		// generalManager.logMsg(
		// "AXmlParserManger.closeCurrentTag() key=[" +
		// currentHandler.getXmlActivationTag() + "] " +
		// currentHandler.getClass().getSimpleName(),
		// LoggerType.VERBOSE_EXTRA );

		if (!llXmlParserStack.isEmpty())
		{

			// llXmlParserStack.removeLast();

			if (!llXmlParserStack.remove(buffer))
			{
				// generalManager.logMsg(
				// "AXmlParserManger.closeCurrentTag() can not remove IXmlParserHandler from list, because it is not inside!"
				// ,
				// LoggerType.MINOR_ERROR);
				return false;
			}

			/**
			 * Get previous item from stack ...
			 */
			if (llXmlParserStack.isEmpty())
			{
				/**
				 * stack is empty, set currentHandler null!
				 */
				currentHandler = null;
			} // if ( llXmlParserStack.isEmpty() )
			else
			{
				/**
				 * Get previous item from stack.
				 */
				currentHandler = llXmlParserStack.getLast();
			} // else .. if ( llXmlParserStack.isEmpty() )

		} // if ( ! llXmlParserStack.isEmpty() ) {
		else
		{
			currentHandler = null;
		} // else ... if ( ! llXmlParserStack.isEmpty() ) {


			/**
		 * Clean up XmlParserHandler..
		 */
		if (buffer.isHandlerDestoryedAfterClosingTag())
		{
			unregisterSaxHandler(buffer.getXmlActivationTag());
			buffer.destroyHandler();
			buffer = null;
		}
		else
		{
			// generalManager.logMsg(
			// "AXmlParserManger.closeCurrentTag() key=[" +
			// buffer.getXmlActivationTag() + "] " +
			// buffer.getClass().getSimpleName() +
			// " do not destroyHandler() since it may be needed later on.",
			// LoggerType.FULL );
		}

		return true;
	}

	@Override
	public final boolean registerAndInitSaxHandler(IXmlParserHandler handler)
	{

		assert handler != null : "Can not handle null pointer as handler";

		if (bProcessingXmlDataNow)
		{
			throw new CaleydoRuntimeException(
					"AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because Xml file is processed now!");
			// return false;
		}

		if (hashTag2XmlParser.contains(handler))
		{
			throw new CaleydoRuntimeException(
					"AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because it is already registered!");
			// return false;
		}

		String key = handler.getXmlActivationTag();

		if (hashTag2XmlParser.containsKey(key))
		{
			throw new CaleydoRuntimeException(
					"AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because String ["
							+ handler.getXmlActivationTag() + "] is already registered!");
			// return false;
		}

		hashTag2XmlParser.put(key, handler);

		// generalManager.logMsg(
		// "XmlParserManager.registerAndInitSaxHandler( key=["
		// + handler.getXmlActivationTag() + "] " +
		// handler.getClass().getSimpleName() + " ) done.",
		// LoggerType.TRANSITION );

		handler.initHandler();

		return true;
	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#unregisterSaxHandler(Stringt)
	 */
	public final boolean unregisterSaxHandler(final String sActivationXmlTag)
	{

		assert sActivationXmlTag != null : "Can not handle null pointer as handler";

		if (bProcessingXmlDataNow)
		{
			throw new CaleydoRuntimeException(
					"AXmlParserManager.registerSaxHandler() can not register Handler, because Xml file is processed now!");
			// return false;
		}

		if (!hashTag2XmlParser.containsKey(sActivationXmlTag))
		{
			throw new CaleydoRuntimeException(
					"AXmlParserManager.unregisterSaxHandler() can not unregister Handler, because it is not registered!");
			// return false;
		}

		IXmlParserHandler parserHandler = hashTag2XmlParser.remove(sActivationXmlTag);

		if (parserHandler != null)
		{

			// generalManager.logMsg(
			// "XmlParserManager.unregisterHandler( key=["
			// + sActivationXmlTag + "] "
			// + parserHandler.getClass().getSimpleName()
			// + " ) done.",
			// LoggerType.TRANSITION );

			return true;
		}

		// generalManager.logMsg(
		// "XmlParserManager.unregisterHandler( "
		// + sActivationXmlTag + " ) failed to unload!",
		// LoggerType.ERROR );

		return false;
	}

	/*
	 * @seeorg.caleydo.core.xml.parser.manager.IXmlParserManager#
	 * getCurrentXmlParserHandler()
	 */
	public final IXmlParserHandler getCurrentXmlParserHandler()
	{

		return this.currentHandler;
	}
}
