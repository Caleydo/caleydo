package org.geneview.core.parser.xml.sax.handler;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;


/**
 * Put ContentHandler and EntityResolver into one interface.
 * 
 * @see org.geneview.core.util.system.GeneViewInputStream
 * @see org.geneview.core.util.system.GeneViewInputStream#parseOnce(InputSource, String, IXmlBaseHandler, ILoggerManager)
 * @see org.geneview.core.parser.xml.sax.handler.IXmlParserHandler
 * @see org.geneview.core.manager.IXmlParserManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IXmlBaseHandler 
extends ContentHandler, EntityResolver{

}
