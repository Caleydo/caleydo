package org.caleydo.core.manager.parser;

import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler;
import org.caleydo.core.parser.xml.sax.handler.command.CommandSaxHandler;
import org.caleydo.core.parser.xml.sax.handler.glyph.GlyphDefinitionSaxHandler;
import org.caleydo.core.parser.xml.sax.handler.pathway.BioCartaPathwayImageMapSaxHandler;
import org.caleydo.core.parser.xml.sax.handler.pathway.KgmlSaxHandler;
import org.caleydo.core.parser.xml.sax.handler.pathway.PathwayImageMapSaxHandler;
import org.caleydo.core.parser.xml.sax.handler.recursion.OpenExternalXmlFileSaxHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.CaleydoInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Administer several XML-SaxHandelers. Switches between several XML-SaxHandeler
 * automatically, based by a registered tag. Acts as proxy for other derived
 * objects from IXmlParserManager
 * 
 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
 * @see org.caleydo.core.manager.IXmlParserManager
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class XmlParserManager
	extends AXmlParserManager
{

	/**
	 * count number of recursions in order to detect misbehavior.
	 */
	private int iCountOpenedFiles = 0;

	protected boolean bUnloadSaxHandlerAfterBootstraping = false;

	@Override
	public void initHandlers()
	{
		OpenExternalXmlFileSaxHandler externalFileHandler = new OpenExternalXmlFileSaxHandler();
		KgmlSaxHandler kgmlParser = new KgmlSaxHandler();
		PathwayImageMapSaxHandler pathwayImageMapParser = new PathwayImageMapSaxHandler();
		BioCartaPathwayImageMapSaxHandler biocartaPathwayParser = new BioCartaPathwayImageMapSaxHandler();
		CommandSaxHandler cmdHandler = new CommandSaxHandler();
		GlyphDefinitionSaxHandler glyphHandler = new GlyphDefinitionSaxHandler();

		registerAndInitSaxHandler(externalFileHandler);
		registerAndInitSaxHandler(kgmlParser);
		registerAndInitSaxHandler(pathwayImageMapParser);
		registerAndInitSaxHandler(biocartaPathwayParser);
		registerAndInitSaxHandler(cmdHandler);
		registerAndInitSaxHandler(glyphHandler);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public final void startDocument() throws SAXException
	{

		setXmlFileProcessedNow(true);
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public final void endDocument() throws SAXException
	{

		setXmlFileProcessedNow(false);

		if (currentHandler != null)
		{
			// generalManager.logMsg( "XmlParserManager.endDocument()  key=[" +
			// currentHandler.getXmlActivationTag() + "]  call " +
			// currentHandler.getClass().getSimpleName() +
			// ".endDocument() ...",
			// LoggerType.FULL );

			currentHandler.endDocument();
		} // if ( currentHandler != null )
		else
		{
			if (bUnloadSaxHandlerAfterBootstraping)
			{
				this.destroyHandler();
			}

		} // else .. if ( currentHandler != null )
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrib)
			throws SAXException
	{

		if (currentHandler == null)
		{
			// generalManager.logMsg( " < TAG= " + qName,
			// LoggerType.FULL );

			startElementSearch4Tag(uri, localName, qName, attrib);

			if (currentHandler != null)
			{
				/*
				 * forwared event if currentHandler was set inside
				 * startElement_search4Tag(..)
				 */
				currentHandler.startElement(uri, localName, qName, attrib);
			} // if ( currentHandler != null )

			/* early return from if () */
			return;

		} // if ( currentHandler == null )

		/* else; regular case with valid current Handler */
		/* test, if new Handler has to be activated. */
		startElementSearch4Tag(uri, localName, qName, attrib);

		currentHandler.startElement(uri, localName, qName, attrib);
	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#startElementSearch4Tag(Stringt,
	 *      Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public void startElementSearch4Tag(String uri, String localName, String qName,
			Attributes attrib)
	{

		if (hashTag2XmlParser.containsKey(qName))
		{
			/**
			 * Get handler registered to this "qName" ..
			 */
			IXmlParserHandler handler = hashTag2XmlParser.get(qName);

			try
			// catch (SAXException se)
			{
				/**
				 * Register handler only if it is not the
				 * OpenExternalXmlFileSaxHandler ...
				 */
				if (handler instanceof OpenExternalXmlFileSaxHandler)
				{
					/**
					 * Special case: Open new file, but do not register new
					 * handler... Attention: do not call
					 * sectionFinishedByHandler() from FileLoaderSaxHandler !
					 */
					/**
					 * pass event to current handler
					 */
					handler.startElement(uri, localName, qName, attrib);

					/* early exit from try-catch block and if */
					return;

				}

				/**
				 * Regular case: register new handler ...
				 */

				// generalManager.logMsg(
				// "AXmlParserManager.openCurrentTag( key=[" +
				// handler.getXmlActivationTag() + "] " +
				// handler.getClass().getSimpleName() + " )",
				// LoggerType.VERBOSE_EXTRA );
				/**
				 * register new handler ...
				 */
				llXmlParserStack.add(handler);
				currentHandler = handler;

			} // try
			catch (SAXException se)
			{
				// generalManager.logMsg(
				// "XmlParserManager.startElement_search4Tag() SAX error: " +
				// se.toString(),
				// LoggerType.ERROR );

			} // try .. catch (SAXException se)

		} // if ( hashTag2XmlParser.containsKey( qName ) )

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{

		// generalManager.logMsg( "        " + qName + " TAG -->",
		// LoggerType.FULL );

		if (currentHandler != null)
		{
			// if ( sCurrentClosingTag.equals( qName ) ) {
			// this.closeCurrentTag();
			// return;
			// }

			currentHandler.endElement(uri, localName, qName);
		}

	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#endElementSearch4Tag(Stringt,
	 *      Stringt, Stringt)
	 */
	public void endElementSearch4Tag(String uri, String localName, String qName)
	{

		assert false : "should not be called but overloaded by derived class.";
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{

		if (currentHandler != null)
		{
			currentHandler.characters(ch, start, length);
		}
	}

	public final void sectionFinishedByHandler(IXmlParserHandler handler)
	{

		assert handler != null : "Can not handel null pointer!";

		/**
		 * allow unregistering of handler
		 */
		setXmlFileProcessedNow(false);

		/**
		 * 
		 */
		if (currentHandler != handler)
		{
			throw new CaleydoRuntimeException(
					"sectionFinishedByHandler() called by wrong handler!",
					CaleydoRuntimeExceptionType.SAXPARSER);
		}

		closeCurrentTag();

		/**
		 * enable processing flag again. Return "token".
		 */
		setXmlFileProcessedNow(true);
	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#parseXmlFileByName(java.lang.String)
	 */
	public boolean parseXmlString(final String sMuddlewareXPath, final String xmlString)
	{

		iCountOpenedFiles++;
		try
		{
			// generalManager.logMsg("XmlParserManager.parseXmlString( " +
			// sMuddlewareXPath + ") parse...",
			// LoggerType.VERBOSE );

			InputSource inStream = new InputSource(xmlString);

			// generalManager.logMsg("XmlParserManager.parseXmlString( XPath=["
			// + sMuddlewareXPath + "] , ..) done.",
			// LoggerType.VERBOSE_EXTRA );

			boolean status = CaleydoInputStream.parseOnce(inStream, sMuddlewareXPath, this,
					generalManager);

			// generalManager.logMsg(
			// "XmlParserManager.parseXmlFileByName( XPath=[" + sMuddlewareXPath
			// + "], ..) done.",
			// LoggerType.STATUS );

			return status;

		}
		catch (CaleydoRuntimeException gve)
		{
			// generalManager.logMsg("XmlParserManager.parseXmlString( " +
			// sMuddlewareXPath +
			// "," + xmlString + ") failed; caleydo_error: " +
			// gve.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
		catch (RuntimeException e)
		{
			// generalManager.logMsg("XmlParserManager.parseXmlString( " +
			// sMuddlewareXPath +
			// "," + xmlString + ") failed; system_error: " +
			// e.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
	}

	@Override
	public boolean parseXmlFileByName(final String filename)
	{

		iCountOpenedFiles++;

		try
		{
			URL resourceUrl = this.getClass().getClassLoader().getResource(filename);
			InputSource inSource = null;

			if (resourceUrl != null)
			{
				inSource = CaleydoInputStream.openInputStreamFromUrl(resourceUrl,
						generalManager);
			}
			else
			{
				inSource = CaleydoInputStream
						.openInputStreamFromFile(filename, generalManager);
			}

			generalManager.getLogger().log(Level.FINE, "Start parsing file " + filename);

			boolean status = CaleydoInputStream.parseOnce(inSource, filename, this,
					generalManager);

			generalManager.getLogger().log(Level.FINE, "Finished parsing file " + filename);

			return status;

		}
		catch (CaleydoRuntimeException gve)
		{
			// generalManager.logMsg("XmlParserManager.parseXmlFileByName( " +
			// filename + ") failed; caleydo_error: " +
			// gve.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
		catch (RuntimeException e)
		{
			// generalManager.logMsg("XmlParserManager.parseXmlFileByName( " +
			// filename + ") failed; system_error: " +
			// e.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#parseXmlFileByName(Stringt)
	 */
	public boolean parseXmlFileByNameAndHandler(final String filename,
			final OpenExternalXmlFileSaxHandler openFileHandler)
	{

		// this.swapXmlParserHandler( currentHandler, openFileHandler );

		iCountOpenedFiles++;

		try
		{
			URL resourceUrl = this.getClass().getClassLoader().getResource(filename);
			InputSource inSource = null;

			if (resourceUrl != null)
			{
				inSource = CaleydoInputStream.openInputStreamFromUrl(resourceUrl,
						generalManager);
			}
			else
			{
				inSource = CaleydoInputStream
						.openInputStreamFromFile(filename, generalManager);
			}

			return CaleydoInputStream.parseOnce(inSource, filename, this, generalManager);

		}
		catch (CaleydoRuntimeException gve)
		{
			// generalManager.logMsg(
			// "XmlParserManager.parseXmlFileByNameAndHandler( " + filename +
			// ") failed; caleydo_error: " +
			// gve.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
		catch (RuntimeException e)
		{
			// generalManager.logMsg(
			// "XmlParserManager.parseXmlFileByNameAndHandler( " + filename +
			// ") failed; system_error: " +
			// e.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
	}

	/**
	 * @see org.caleydo.core.manager.IXmlParserManager#parseXmlFileByInputStream(org.xml.sax.InputSource)
	 */
	public boolean parseXmlFileByInputStream(InputSource inputStream,
			final String inputStreamText)
	{

		iCountOpenedFiles++;
		try
		{

			return CaleydoInputStream.parseOnce(inputStream, inputStreamText, this,
					generalManager);
		}
		catch (CaleydoRuntimeException gve)
		{
			// generalManager.logMsg(
			// "XmlParserManager.parseXmlFileByInputStream( ) failed; caleydo_error: "
			// +
			// gve.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
		catch (RuntimeException e)
		{
			// generalManager.logMsg(
			// "XmlParserManager.parseXmlFileByInputStream( ) failed; system_error: "
			// +
			// e.toString(),
			// LoggerType.MINOR_ERROR_XML );

			return false;
		}
	}

	public void destroyHandler()
	{

		// generalManager.logMsg( "XmlParserManager.destoryHandler() ... ",
		// LoggerType.VERBOSE );

		/**
		 * Linked list...
		 */

		if (llXmlParserStack == null)
		{
			// generalManager.logMsg(
			// "XmlParserManager.destoryHandler() llXmlParserStack is null",
			// LoggerType.FULL );
		} // if ( llXmlParserStack == null )
		else
		{
			// generalManager.logMsg(
			// "XmlParserManager.destoryHandler() llXmlParserStack remove objects.."
			// ,
			// LoggerType.FULL );

			if (!llXmlParserStack.isEmpty())
			{
				Iterator<IXmlParserHandler> iterParserHandler = llXmlParserStack.iterator();

				while (iterParserHandler.hasNext())
				{
					IXmlParserHandler handler = iterParserHandler.next();

					unregisterSaxHandler(handler.getXmlActivationTag());
					handler.destroyHandler();
				} // while ( iterParserHandler.hasNext() )

				llXmlParserStack.clear();

			} // if ( ! llXmlParserStack.isEmpty() )

			llXmlParserStack = null;
		} // else .. if ( llXmlParserStack == null )

		/**
		 * Hashtable ...
		 */

		if (hashTag2XmlParser == null)
		{
			// generalManager.logMsg(
			// "XmlParserManager.destoryHandler() hashTag2XmlParser is null",
			// LoggerType.FULL );
		} // if ( hashTag2XmlParser == null )
		else
		{
			// generalManager.logMsg(
			// "XmlParserManager.destoryHandler() hashTag2XmlParser remove objects.."
			// ,
			// LoggerType.FULL );

			if (!hashTag2XmlParser.isEmpty())
			{
				Iterator<IXmlParserHandler> iterHandler = hashTag2XmlParser.values()
						.iterator();

				while (iterHandler.hasNext())
				{
					IXmlParserHandler handler = iterHandler.next();

					if (handler != null)
					{
						handler.destroyHandler();
						handler = null;
					}

				} // while ( iterHandler.hasNext() )

				hashTag2XmlParser.clear();

			} // if ( ! hashTag2XmlParser.isEmpty() ) {
			hashTag2XmlParser = null;

		} // else .. if ( hashTag2XmlParser == null )

		// generalManager.logMsg( "XmlParserManager.destoryHandler() ... done!",
		// LoggerType.FULL );

		// generalManager.logMsg( "XML file was read sucessfully.",
		// LoggerType.STATUS );

	}
}
