package org.caleydo.core.util.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.xml.sax.handler.IXmlBaseHandler;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Static convenience methods to handle input streams.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CaleydoInputStream
{

	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName name and path of the file
	 * @return input stream of the file, or null
	 */
	public static InputSource openInputStreamFromFile(final String sXMLFileName,
			final IGeneralManager generalManager)
	{

		try
		{
			File inputFile = new File(sXMLFileName);
			FileReader inReader = new FileReader(inputFile);

			InputSource inStream = new InputSource(inReader);

			generalManager.getLogger().logp(Level.FINE, "CaleydoInputStream",
					"openInputStreamFromFile", "open input stream  [" + sXMLFileName + "]");

			return inStream;
		}
		catch (FileNotFoundException fnfe)
		{

			generalManager.getLogger().logp(Level.SEVERE, "CaleydoInputStream",
					"openInputStreamFromFile", "File not found " + fnfe.toString());
		}
		return null;
	}

	/**
	 * Opens a resource and returns an input stream to that resource.
	 * 
	 * @param resourceUrl
	 * @param generalManager
	 * @return
	 */
	public static InputSource openInputStreamFromUrl(final URL resourceUrl,
			final IGeneralManager generalManager)
	{

		try
		{
			InputSource inStream = new InputSource(resourceUrl.openStream());

			generalManager.getLogger().log(Level.INFO,
					"Open input stream " + resourceUrl.toString());

			return inStream;
		}
		catch (IOException e)
		{
			generalManager.getLogger().log(Level.SEVERE, "Error loading resource.", e);
		}
		return null;
	}

	/**
	 * @param inStream
	 * @param sInputStreamLabel
	 * @param handler
	 * @param generalManager
	 * @return
	 */
	public static boolean parseOnce(InputSource inStream, final String sInputStreamLabel,
			IXmlBaseHandler handler, final IGeneralManager generalManager)
	{

		if (handler == null)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"Error during parsing. Handler is null.");
			return false;
		} // if

		if (inStream == null)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"Error during parsing. No input stream; skip file");
			return false;
		}

		try
		{
			XMLReader reader = null;

			if (sInputStreamLabel.contains(".xml"))
			{
				reader = XMLReaderFactory.createXMLReader();

				// Entity resolver avoids the XML Reader
				// to check external DTDs.
				reader.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-external-dtd",
						false);

				reader.setEntityResolver(handler);
				reader.setContentHandler(handler);
			}
			else
			{
				reader = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");
				// reader.setFeature(org.ccil.cowan.tagsoup.Parser.
				// defaultAttributesFeature, false);

				reader.setEntityResolver(handler);
				reader.setContentHandler(handler);

				HTMLSchema htmlSchema = new HTMLSchema();
				reader.setProperty(Parser.schemaProperty, htmlSchema);
			}

			try
			{
				reader.parse(inStream);
			}
			catch (SAXParseException saxe)
			{
				generalManager.getLogger().log(
						Level.SEVERE,
						"SAXParser-error during parsing line=" + saxe.getLineNumber()
								+ " at column=" + saxe.getColumnNumber() + ".\n SAX error: "
								+ saxe.toString(), saxe);
			}
			catch (IOException e)
			{
				generalManager.getLogger().log(Level.SEVERE, "IO-error during parsing", e);
			} // try
			catch (Exception e)
			{
				generalManager.getLogger().log(Level.SEVERE, "Error during parsing", e);
			} // try

			/**
			 * close file...
			 */
			if (inStream.getByteStream() != null)
			{
				inStream.getByteStream().close();
			}
			else if (inStream.getCharacterStream() != null)
			{
				inStream.getCharacterStream().close();
			}

			generalManager.getLogger().log(Level.INFO,
					"Close input stream: " + sInputStreamLabel);

		} // try
		catch (SAXException saxe)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"SAXParser-error during parsing.\n SAX error: " + saxe.toString(), saxe);
		}
		catch (IOException ioe)
		{
			generalManager.getLogger().log(Level.SEVERE, "IO-error during parsing", ioe);
		}
		catch (Exception e)
		{
			generalManager.getLogger().log(Level.SEVERE, "Error during parsing", e);
		}

		return true;
	}
}
