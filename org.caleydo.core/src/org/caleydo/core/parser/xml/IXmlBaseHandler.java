package org.caleydo.core.parser.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Put ContentHandler and EntityResolver into one interface.
 * 
 * @see org.caleydo.core.util.system.CaleydoInputStream
 * @see org.caleydo.core.util.system.CaleydoInputStream#parseOnce(InputSource, String, IXmlBaseHandler,
 *      ILoggerManager)
 * @see org.caleydo.core.parser.xml.IXmlParserHandler
 * @see org.caleydo.core.manager.XmlParserManager
 * @author Michael Kalkusch
 */
public interface IXmlBaseHandler
	extends ContentHandler, EntityResolver {

}
