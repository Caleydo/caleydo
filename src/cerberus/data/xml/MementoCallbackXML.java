/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.xml;

//import java.io.InputStream;
// import org.xml.sax.InputSource;

//import prometheus.data.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.ISaxParserHandler;
//import prometheus.data.xml.MementoXML;

/**
 * Interface for loading and saving DNetEventComponentInterface objects to and from XML file.
 * 
 * @author Michael Kalkusch
 * @see prometheus.net.dwt.DNetEventComponentInterface
 * 
 * @see prometheus.data.xml.MementoNetEventXML
 * @see prometheus.data.xml.MementoItemXML
 * @see prometheus.data.xml.MementoXML
 */
public interface MementoCallbackXML {
	
	/**
	 * Defines a callback, that can be triggert by the parser once a cetain tag is reached.
	 * 
	 * Note: this is used to notify the end of a tag in most cases, 
	 * to be able to create an object with the information provided.
	 * 
	 * This callback is triggert via the reference passed to the parser in the constructor.
	 * 
	 * @param type type of object
	 * @param tag_causes_callback Tag that cased the callback
	 * @param details additional inforamtion
	 * @param refSaxHandler SaxHandler with state for object from XML file
	 */
	public void callbackForParser( final ManagerObjectType type,
			final String tag_causes_callback,
			final String details,
			final ISaxParserHandler refSaxHandler);	
	
}
