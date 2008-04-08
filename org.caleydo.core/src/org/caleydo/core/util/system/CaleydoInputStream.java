/**
 * 
 */
package org.caleydo.core.util.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.caleydo.core.manager.ILoggerManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.parser.xml.sax.handler.IXmlBaseHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class CaleydoInputStream
{

	/**
	 * 
	 */
	private CaleydoInputStream()
	{
		
	}
	
	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName name and path of the file
	 * @return input stream of the file, or null
	 */
	public static InputSource openInputStreamFromFile( final String sXMLFileName,
			final ILoggerManager refLoggerManager ) {
		
		try {
			File inputFile = new File( sXMLFileName );		
			FileReader inReader = new FileReader( inputFile );
			
			InputSource inStream = new InputSource( inReader );
			
			refLoggerManager.logMsg("open input stream  [" + sXMLFileName + "]",
					LoggerType.VERBOSE_EXTRA );
			
			return inStream;
		}
		catch ( FileNotFoundException fnfe) {
			refLoggerManager.logMsg("CaleydoInputStream.openInputStreamFromFile() File not found " + fnfe.toString(),
					LoggerType.ERROR );
		}
		return null;
	}
	
	/**
	 * Opens a resource and returns an input stream to that resource.
	 * 
	 * @param resourceUrl
	 * @param refLoggerManager
	 * @return
	 */
	public static InputSource openInputStreamFromUrl( final URL resourceUrl,
			final ILoggerManager refLoggerManager ) {
		
		try {	
			InputSource inStream = new InputSource(resourceUrl.openStream());
			
			refLoggerManager.logMsg("open input stream  [" + resourceUrl.toString() + "]",
					LoggerType.VERBOSE_EXTRA );
			
			return inStream;
		} catch (IOException e)
		{
			refLoggerManager.logMsg("CaleydoInputStream.openInputStreamFromUrl(): Error loading resource.",
					LoggerType.ERROR );
			
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @see org.caleydo.core.manager.IXmlParserManager
	 * @see org.xml.sax.ContentHandler
	 * @see org.xml.sax.EntityResolver;
	 * @see org.xml.sax.DTDHandler;
	 * 
	 * @param inStream
	 * @param sInputStreamLabel
	 * @param handler
	 * @param refLoggerManager
	 * @return
	 */
	public static boolean parseOnce( InputSource inStream,
			final String sInputStreamLabel,
			IXmlBaseHandler handler,
			final ILoggerManager refLoggerManager ) {
		
		if ( handler == null ) 
		{
			refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
					sInputStreamLabel +
					") error because handler is null!",
					LoggerType.ERROR );
			
			return false;
		} //if
		
		if ( inStream==null ) {
			refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
					sInputStreamLabel +
					") error no input stream; skip file",
					LoggerType.ERROR );
			return false;
		}
		
		try 
		{			
			XMLReader reader = null;
			
			if (sInputStreamLabel.contains("."))
			{
				reader = XMLReaderFactory.createXMLReader();

				// Entity resolver avoids the XML Reader 
				// to check external DTDs. 
				reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", 
					false);
				
				reader.setEntityResolver(handler);
				reader.setContentHandler(handler);
			}
			else
			{
				reader =
					XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");
				//reader.setFeature(org.ccil.cowan.tagsoup.Parser.defaultAttributesFeature, false);
			
				reader.setEntityResolver(handler);
				reader.setContentHandler(handler);
				
				HTMLSchema htmlSchema = new HTMLSchema();
				reader.setProperty(Parser.schemaProperty, htmlSchema);
			}
			
			try 
			{						
				reader.parse( inStream );
			} 
			catch (SAXParseException saxe)
			{
				refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
						sInputStreamLabel +
						") SAXParser-error during parsing: line=" + saxe.getLineNumber() +
						" at column=" + saxe.getColumnNumber() +
						"  SAXParser-error during parsing:" + 
						saxe.toString(),
						LoggerType.ERROR );
			}
			catch ( IOException e) 
			{
				refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
						sInputStreamLabel +
						") IO-error during parsing: " +
						e.toString(),
						LoggerType.ERROR );
			} // try
			catch ( Exception e) 
			{
				refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
						sInputStreamLabel +
						") error during parsing: " +
						e.toString() + "\n",
						LoggerType.ERROR );
				
				e.printStackTrace();
			} // try
			
			
			/**
			 * close file...
			 */
		
			if (inStream.getByteStream() != null)
			{
				inStream.getByteStream().close();
			}
			else if(inStream.getCharacterStream() != null)
			{
				inStream.getCharacterStream().close();
			}
			
			refLoggerManager.logMsg("close input stream [" + 
					sInputStreamLabel + 
					"]",
					LoggerType.VERBOSE_EXTRA );
			
		} // try
		catch (SAXException se) 
		{
			refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
					sInputStreamLabel +
					") SAXError  while parsing: " +
					se.toString(),
					LoggerType.ERROR );
		} // end try-catch SAXException
		catch (IOException ioe) 
		{
			refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
					sInputStreamLabel +
					") IO-error while parsing: " +
					ioe.toString(),
					LoggerType.ERROR );
		} // end try-catch SAXException, IOException
		catch (Exception e) 
		{
			refLoggerManager.logMsg("CaleydoInputStream.parseOnce( " +
					sInputStreamLabel +
					") general error while parsing: " +
					e.toString(),
					LoggerType.ERROR );
			
			e.printStackTrace();
		} // end try-catch SAXException, IOException
		
		
		return true;
	}

}
