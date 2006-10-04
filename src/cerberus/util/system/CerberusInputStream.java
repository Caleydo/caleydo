/**
 * 
 */
package cerberus.util.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;

/**
 * @author kalkusch
 *
 */
public class CerberusInputStream
{

	/**
	 * 
	 */
	private CerberusInputStream()
	{
		
	}
	
	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName name abd path of the file
	 * @return input stream of the file, or null
	 */
	public static InputSource openInputStreamFromFile( final String sXMLFileName,
			final ILoggerManager refLoggerManager ) {
		
		try {
			File inputFile = new File( sXMLFileName );		
			FileReader inReader = new FileReader( inputFile );
			
			InputSource inStream = new InputSource( inReader );
			
			refLoggerManager.logMsg("open input stream  [" + sXMLFileName + "]",
					LoggerType.STATUS );
			
			return inStream;
		}
		catch ( FileNotFoundException fnfe) {
			refLoggerManager.logMsg("CerberusInputStream.openInputStreamFromFile() File not found " + fnfe.toString(),
					LoggerType.ERROR_ONLY );
		}
		return null;
	}
	
	public static boolean parseOnce( InputSource inStream,
			final String sInputStreamLabel,
			ContentHandler handler,
			final ILoggerManager refLoggerManager ) {
		
		if ( handler == null ) 
		{
			refLoggerManager.logMsg("CerberusInputStream.parseOnce( " +
					sInputStreamLabel +
					") error because handler is null!",
					LoggerType.ERROR_ONLY );
			
			return false;
		} //if
		
		try 
		{			
			XMLReader reader = XMLReaderFactory.createXMLReader();			
			reader.setContentHandler( handler );
			
			try 
			{						
				reader.parse( inStream );
			} 
			catch ( IOException e) 
			{
				refLoggerManager.logMsg("CerberusInputStream.parseOnce( " +
						sInputStreamLabel +
						") IO-error during parsing: " +
						e.toString(),
						LoggerType.ERROR_ONLY );
			} // try
			catch ( Exception e) 
			{
				refLoggerManager.logMsg("CerberusInputStream.parseOnce( " +
						sInputStreamLabel +
						") error during parsing: " +
						e.toString() + "\n",
						LoggerType.ERROR_ONLY );
				
				e.printStackTrace();
			} // try
			
			
			/**
			 * close file...
			 */
		
			inStream.getCharacterStream().close();
			
			refLoggerManager.logMsg("close input stream [" + 
					sInputStreamLabel + 
					"]",
					LoggerType.STATUS );
			
		} // try
		catch (SAXException se) 
		{
			refLoggerManager.logMsg("CerberusInputStream.parseOnce( " +
					sInputStreamLabel +
					") SAXError  while parsing: " +
					se.toString(),
					LoggerType.ERROR_ONLY );
		} // end try-catch SAXException
		catch (IOException ioe) 
		{
			refLoggerManager.logMsg("CerberusInputStream.parseOnce( " +
					sInputStreamLabel +
					") IO-error while parsing: " +
					ioe.toString(),
					LoggerType.ERROR_ONLY );
		} // end try-catch SAXException, IOException
		catch (Exception e) 
		{
			refLoggerManager.logMsg("CerberusInputStream.parseOnce( " +
					sInputStreamLabel +
					") general error while parsing: " +
					e.toString(),
					LoggerType.ERROR_ONLY );
		} // end try-catch SAXException, IOException
		
		
		return true;
	}

}
