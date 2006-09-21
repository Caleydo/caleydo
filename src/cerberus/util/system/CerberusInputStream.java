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

//import cerberus.xml.parser.ACerberusDefaultSaxHandler;

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
	public static InputSource openInputStreamFromFile( final String sXMLFileName ) {
		
		try {
			File inputFile = new File( sXMLFileName );		
			FileReader inReader = new FileReader( inputFile );
			
			InputSource inStream = new InputSource( inReader );
			
			return inStream;
		}
		catch ( FileNotFoundException fnfe) {
			System.out.println("File not found " + fnfe.toString() );
		}
		return null;
	}
	
	public static boolean parseOnce( InputSource inStream, 
			ContentHandler handler) {
		
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();			
			reader.setContentHandler( handler );
			
			try {						
				reader.parse( inStream );
			} 
			catch ( IOException e) {
				System.out.println(" EX " + e.toString() );
			}
			
			/**
			 * Use data from parser to restore state...
			 */ 
			
			handler.endDocument();
			
			/**
			 * Restore state...
			 */
		
			inStream.getCharacterStream().close();
		}
		catch (SAXException se) {
			System.out.println("ERROR SE " + se.toString() );
		} // end try-catch SAXException
		catch (IOException ioe) {
			System.out.println("ERROR IO " + ioe.toString() );
		} // end try-catch SAXException, IOException
		
		
		return true;
	}

}
