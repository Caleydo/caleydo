package org.caleydo.core.manager.parser;

import java.util.Hashtable;
import java.util.LinkedList;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Abstract parser manager.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AXmlParserManager
	extends DefaultHandler
	implements IXmlParserManager {
	protected IGeneralManager generalManager;

	/**
	 * Token to avoid registering and unregistering handlers during processing XMl data.
	 */
	private boolean bProcessingXmlDataNow = false;

	/**
	 * Defines the active handler if the opening tag was found. If no opening tag was found or the closing tag
	 * was precessed this reference is null.
	 */
	protected IXmlParserHandler currentHandler = null;

	/**
	 * Contains the Handler of the previous opening tags. If a new tag is opened the currentHandler is stored
	 * as last element of this vector. If a closing tag is processed the currentHandler is set to the last
	 * element in the vector and the last element in the vector is removed.
	 */
	protected LinkedList<IXmlParserHandler> llXmlParserStack;

	/**
	 * Hashtable of handlers and registered XMl Tags bound to them.
	 */
	protected Hashtable<String, IXmlParserHandler> hashTag2XmlParser;

	/**
	 * Constructor.
	 */
	protected AXmlParserManager() {
		generalManager = GeneralManager.get();

		hashTag2XmlParser = new Hashtable<String, IXmlParserHandler>();
		llXmlParserStack = new LinkedList<IXmlParserHandler>();
	}

	protected final void setXmlFileProcessedNow(boolean bStatus) {
		this.bProcessingXmlDataNow = bStatus;
	}

	protected final boolean closeCurrentTag() {

		if (this.currentHandler == null)
			throw new IllegalStateException(
				"AXmlParserManager.closeCurrentTag() current handler is null! Can not close handler");
		// return false;

		IXmlParserHandler buffer = currentHandler;

		if (!llXmlParserStack.isEmpty()) {

			if (!llXmlParserStack.remove(buffer))
				return false;

			/**
			 * Get previous item from stack ...
			 */
			if (llXmlParserStack.isEmpty()) {
				/**
				 * stack is empty, set currentHandler null!
				 */
				currentHandler = null;
			}
			else {
				/**
				 * Get previous item from stack.
				 */
				currentHandler = llXmlParserStack.getLast();
			}

		}
		else {
			currentHandler = null;
		}

		/**
		 * Clean up XmlParserHandler..
		 */
		if (buffer.isHandlerDestoryedAfterClosingTag()) {
			unregisterSaxHandler(buffer.getXmlActivationTag());
			buffer.destroyHandler();
			buffer = null;
		}

		return true;
	}

	@Override
	public final boolean registerAndInitSaxHandler(IXmlParserHandler handler) {

		assert handler != null : "Can not handle null pointer as handler";

		if (bProcessingXmlDataNow)
			throw new IllegalStateException(
				"AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because Xml file is processed now!");
		// return false;

		if (hashTag2XmlParser.contains(handler))
			throw new IllegalStateException(
				"AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because it is already registered!");
		// return false;

		String key = handler.getXmlActivationTag();

		if (hashTag2XmlParser.containsKey(key))
			throw new IllegalStateException(
				"AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because String ["
					+ handler.getXmlActivationTag() + "] is already registered!");
		// return false;

		hashTag2XmlParser.put(key, handler);

		handler.initHandler();

		return true;
	}

	@Override
	public final void unregisterSaxHandler(final String sActivationXmlTag) {
		if (bProcessingXmlDataNow)
			throw new IllegalStateException(
				"AXmlParserManager.registerSaxHandler() can not register Handler, because Xml file is processed now!");
		// return false;

		if (!hashTag2XmlParser.containsKey(sActivationXmlTag))
			throw new IllegalStateException(
				"AXmlParserManager.unregisterSaxHandler() can not unregister Handler, because it is not registered!");
		// return false;

		IXmlParserHandler parserHandler = hashTag2XmlParser.remove(sActivationXmlTag);

		if (parserHandler == null)
			throw new IllegalStateException("Cannot unregister parser handler!");

		return;
	}

	@Override
	public final IXmlParserHandler getCurrentXmlParserHandler() {
		return this.currentHandler;
	}
}
