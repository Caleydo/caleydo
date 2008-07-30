package org.caleydo.core.parser.xml.sax.handler;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;

/**
 * Put ContentHandler and EntityResolver into one interface.
 * 
 * @see org.caleydo.core.util.system.CaleydoInputStream
 * @see org.caleydo.core.util.system.CaleydoInputStream#parseOnce(InputSource,
 *      String, IXmlBaseHandler, ILoggerManager)
 * @see org.caleydo.core.parser.xml.sax.handler.IXmlParserHandler
 * @see org.caleydo.core.manager.IXmlParserManager
 * @author Michael Kalkusch
 */
public interface IXmlBaseHandler
	extends ContentHandler, EntityResolver
{

}
