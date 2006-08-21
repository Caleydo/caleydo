/**
 * 
 */
package cerberus.application.core;

import org.xml.sax.InputSource;

import cerberus.application.prototype.CerberusPrototype;
import cerberus.manager.IGeneralManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.util.system.CerberusInputStream;
import cerberus.xml.parser.handler.IXmlParserHandler;
import cerberus.xml.parser.handler.command.CommandSaxHandler;
import cerberus.xml.parser.manager.XmlParserManager;
import cerberus.xml.parser.manager.IXmlParserManager;


/**
 * @author kalkusch
 *
 */
public class CerberusBootloaderTest
{

	protected XmlParserManager parserManager;
	
	protected final IGeneralManager refGeneralManager;
	
	
	public static void main(String[] args) 
	{
		CerberusBootloaderTest prototype = new CerberusBootloaderTest();	
	}
	
	/**
	 * 
	 */
	public CerberusBootloaderTest()
	{
		refGeneralManager = new OneForAllManager( null );
		
		((OneForAllManager) refGeneralManager).initAll();
		
		
		parserManager = new XmlParserManager( refGeneralManager, false );
		
		CommandSaxHandler cmdHandler = 
			new CommandSaxHandler( refGeneralManager,
					parserManager );
		
		
		parserManager.registerSaxHandler( cmdHandler );
		
		
		String filename = "data/XML/bootstrap/cerberus_bootstrap_sample.xml";
		
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( filename );
					
		CerberusInputStream.parseOnce( inSource , parserManager );
	}

}
