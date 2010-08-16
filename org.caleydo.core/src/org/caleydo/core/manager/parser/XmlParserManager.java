package org.caleydo.core.manager.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler;
import org.caleydo.core.parser.xml.sax.handler.command.CommandSaxHandler;
import org.caleydo.core.parser.xml.sax.handler.recursion.OpenExternalXmlFileSaxHandler;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Administer several XML-SaxHandelers. Switches between several XML-SaxHandeler automatically, based by a
 * registered tag. Acts as proxy for other derived objects from IXmlParserManager
 * 
 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
 * @see org.caleydo.core.manager.IXmlParserManager
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class XmlParserManager
	extends DefaultHandler
	implements IXmlParserManager {

	protected boolean bUnloadSaxHandlerAfterBootstraping = false;

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
	public XmlParserManager() {
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

	@Override
	public void initHandlers() {
		OpenExternalXmlFileSaxHandler externalFileHandler = new OpenExternalXmlFileSaxHandler();
		CommandSaxHandler cmdHandler = new CommandSaxHandler();

		registerAndInitSaxHandler(externalFileHandler);
		registerAndInitSaxHandler(cmdHandler);
	}

	@Override
	public final void startDocument() throws SAXException {
		setXmlFileProcessedNow(true);
	}

	@Override
	public final void endDocument() throws SAXException {
		setXmlFileProcessedNow(false);

		if (currentHandler != null) {

			currentHandler.endDocument();
		}
		else {
			if (bUnloadSaxHandlerAfterBootstraping) {
				this.destroyHandler();
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrib)
		throws SAXException {
		if (currentHandler == null) {

			startElementSearch4Tag(uri, localName, qName, attrib);

			if (currentHandler != null) {
				/*
				 * forwared event if currentHandler was set inside startElement_search4Tag(..)
				 */
				currentHandler.startElement(uri, localName, qName, attrib);
			}

			/* early return from if () */
			return;

		} // if ( currentHandler == null )

		/* else; regular case with valid current Handler */
		/* test, if new Handler has to be activated. */
		startElementSearch4Tag(uri, localName, qName, attrib);

		currentHandler.startElement(uri, localName, qName, attrib);
	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#startElementSearch4Tag(Stringt, Stringt, Stringt,
	 *      org.xml.sax.Attributes)
	 */
	public void startElementSearch4Tag(String uri, String localName, String qName, Attributes attrib) {

		if (hashTag2XmlParser.containsKey(qName)) {
			/**
			 * Get handler registered to this "qName" ..
			 */
			IXmlParserHandler handler = hashTag2XmlParser.get(qName);

			try {
				/**
				 * Register handler only if it is not the OpenExternalXmlFileSaxHandler ...
				 */
				if (handler instanceof OpenExternalXmlFileSaxHandler) {
					/**
					 * Special case: Open new file, but do not register new handler... Attention: do not call
					 * sectionFinishedByHandler() from FileLoaderSaxHandler !
					 */
					/**
					 * pass event to current handler
					 */
					handler.startElement(uri, localName, qName, attrib);

					/* early exit from try-catch block and if */
					return;

				}

				llXmlParserStack.add(handler);
				currentHandler = handler;

			} // try
			catch (SAXException se) {
				// generalManager.logMsg(
				// "XmlParserManager.startElement_search4Tag() SAX error: " +
				// se.toString(),
				// LoggerType.ERROR );

			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (currentHandler != null) {

			currentHandler.endElement(uri, localName, qName);
		}

	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#endElementSearch4Tag(Stringt, Stringt, Stringt)
	 */
	public void endElementSearch4Tag(String uri, String localName, String qName) {

		assert false : "should not be called but overloaded by derived class.";
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (currentHandler != null) {
			currentHandler.characters(ch, start, length);
		}
	}

	public final void sectionFinishedByHandler(IXmlParserHandler handler) {

		assert handler != null : "Can not handel null pointer!";

		/**
		 * allow unregistering of handler
		 */
		setXmlFileProcessedNow(false);

		// if (currentHandler != handler)
		// throw new IllegalStateException("sectionFinishedByHandler() called by wrong handler!");

		if (currentHandler != null)
			closeCurrentTag();

		/**
		 * enable processing flag again. Return "token".
		 */
		setXmlFileProcessedNow(true);
	}

	@Override
	public boolean parseXmlFileByName(final String fileName) {

		InputSource inputSource = getInputSource(fileName);

		try {
			XMLReader reader = null;

			if (fileName.contains(".xml")) {
				reader = XMLReaderFactory.createXMLReader();

				// Entity resolver avoids the XML Reader
				// to check external DTDs.
				reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

				reader.setEntityResolver(this);
				reader.setContentHandler(this);
			}
			else {
				reader = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");
				// reader.setFeature(org.ccil.cowan.tagsoup.Parser.
				// defaultAttributesFeature, false);

				reader.setEntityResolver(this);
				reader.setContentHandler(this);

				HTMLSchema htmlSchema = new HTMLSchema();
				reader.setProperty(Parser.schemaProperty, htmlSchema);
			}

			// generalManager.getLogger().log(new Status(Status.INFO, GeneralManager.PLUGIN_ID,
			// "Start parsing file " + sFileName));

			reader.parse(inputSource);

			if (inputSource.getByteStream() != null) {
				inputSource.getByteStream().close();
			}
			else if (inputSource.getCharacterStream() != null) {
				inputSource.getCharacterStream().close();
			}

			// generalManager.getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
			// "Finished parsing file " + sFileName));

		}
		catch (SAXException saxe) {
			throw new IllegalStateException("SAXParser-error during parsing file " + fileName
				+ ".\n SAX error: " + saxe.toString());
		}
		catch (IOException ioe) {
			throw new IllegalStateException("IO-error during parsing");
		}

		return true;
	}

	public InputSource getInputSource(String fileName) {

		InputSource inputSource = null;
		try {
			inputSource = generalManager.getResourceLoader().getInputSource(fileName);
		}
		catch (FileNotFoundException e) {
			throw new IllegalStateException("Cannot load input file " + fileName);
		}
		return inputSource;
	}

	public void destroyHandler() {

		if (llXmlParserStack != null) {
			if (!llXmlParserStack.isEmpty()) {
				Iterator<IXmlParserHandler> iterParserHandler = llXmlParserStack.iterator();

				while (iterParserHandler.hasNext()) {
					IXmlParserHandler handler = iterParserHandler.next();

					unregisterSaxHandler(handler.getXmlActivationTag());
					handler.destroyHandler();
				}

				llXmlParserStack.clear();
			}
			llXmlParserStack = null;
		}

		if (hashTag2XmlParser != null) {
			if (!hashTag2XmlParser.isEmpty()) {
				Iterator<IXmlParserHandler> iterHandler = hashTag2XmlParser.values().iterator();

				while (iterHandler.hasNext()) {
					IXmlParserHandler handler = iterHandler.next();

					if (handler != null) {
						handler.destroyHandler();
						handler = null;
					}
				}
				hashTag2XmlParser.clear();
			}
			hashTag2XmlParser = null;
		}
	}
}
