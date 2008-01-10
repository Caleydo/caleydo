/**
 * 
 */
package org.geneview.core.manager.parser;

//import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
//import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
//import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager;
//import org.geneview.core.manager.IXmlParserManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.xml.sax.handler.IXmlParserHandler;
import org.geneview.core.parser.xml.sax.handler.command.CommandSaxHandler;
import org.geneview.core.parser.xml.sax.handler.pathway.BioCartaPathwayImageMapSaxHandler;
import org.geneview.core.parser.xml.sax.handler.pathway.KgmlSaxHandler;
import org.geneview.core.parser.xml.sax.handler.pathway.PathwayImageMapSaxHandler;
import org.geneview.core.parser.xml.sax.handler.recursion.OpenExternalXmlFileSaxHandler;
import org.geneview.core.util.system.GeneViewInputStream;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Administer several XML-SaxHandelers.
 * Switches between several XML-SaxHandeler automatical, based by a registered tag.
 * Acts as proxy for other derived objects from IXmlParserManager
 * 
 * @see org.geneview.core.parser.xml.sax.handler.IXmlParserHandler
 * @see org.geneview.core.manager.IXmlParserManager
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class XmlParserManager 
extends AXmlParserManager {

//  /**
//   * necessary to catch the special pathway XML file case
//	 * when the pathway tag needs to be parsed again.
//   */
//  private static final String xml_tag_for_pathways = "pathway";
  
	protected final ILoggerManager refLoggerManager;

	/** Define log level for log inforamtion */
	protected final LoggerType logLevel;
	
	/**
	 * Define maximum number of recursions
	 */
	public final int iCountMaximumOpenedFile = 513;

	/**
	 * count number of recusrions in order to detect misbehaviour.
	 */
	private int iCountOpenedFiles = 0;		
	
	protected boolean bUnloadSaxHandlerAfterBootstraping = false;
	
	/**
	 * Default constructor.
	 * 
	 * @param generalManager reference to IGeneralManager
	 * @param bUseCascadingHandler TRUE enabeld cascading handlers and slows down parsing speed.
	 */
	public XmlParserManager(final IGeneralManager generalManager) {
		
		super( generalManager );
		
		refLoggerManager = generalManager.getSingelton().getLoggerManager();
		
		this.logLevel = LoggerType.VERBOSE;
		
		OpenExternalXmlFileSaxHandler externalFileHandler =
			new OpenExternalXmlFileSaxHandler( generalManager, this );
						
		KgmlSaxHandler kgmlParser = 
			new KgmlSaxHandler( generalManager, this );	
		
		PathwayImageMapSaxHandler pathwayImageMapParser =
			new PathwayImageMapSaxHandler ( generalManager, this );
		
		BioCartaPathwayImageMapSaxHandler biocartaPathwayParser =
			new BioCartaPathwayImageMapSaxHandler ( generalManager, this);
		
		CommandSaxHandler cmdHandler = 
			new CommandSaxHandler( generalManager, this );
		
		registerAndInitSaxHandler( externalFileHandler );		
		registerAndInitSaxHandler( kgmlParser );
		registerAndInitSaxHandler( pathwayImageMapParser );
		registerAndInitSaxHandler( biocartaPathwayParser );
		registerAndInitSaxHandler( cmdHandler );
		
		//openCurrentTag( cmdHandler );
	}


	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public final void startDocument() throws SAXException {
		
		setXmlFileProcessedNow( true );
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public final void endDocument() throws SAXException {
		
		setXmlFileProcessedNow( false );	
		
		if ( currentHandler != null ) 
		{
			refLoggerManager.logMsg( "XmlParserManager.endDocument()  key=[" +
					currentHandler.getXmlActivationTag() + "]  call " +
					currentHandler.getClass().getSimpleName() + 
					".endDocument() ...",
					LoggerType.FULL );
			
			currentHandler.endDocument();
		} // if ( currentHandler != null ) 
		else 
		{
			if ( bUnloadSaxHandlerAfterBootstraping ) {				
				this.destroyHandler();
			}
			
		} // else .. if ( currentHandler != null ) 
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, 
			String localName, 
			String qName,
			Attributes attrib) throws SAXException {
		
		if ( currentHandler == null ) 
		{
			refLoggerManager.logMsg( " < TAG= " + qName,
					LoggerType.FULL );
			
			startElement_search4Tag(uri,
					localName, 
					qName,
					attrib);
			
			if ( currentHandler != null )
			{
				/* forwared event if currentHandler was set inside startElement_search4Tag(..) */
				currentHandler.startElement( uri, 
						localName, 
						qName,
						attrib );
			} //if ( currentHandler != null )
			
			/* early return from if ()*/
			return;
			
		} // if ( currentHandler == null ) 
		
		/* else; regular case with valid current Handler */ 			
		/* test, if new Handler has to be activated. */
		startElement_search4Tag(uri,
				localName, 
				qName,
				attrib);	
		
		currentHandler.startElement( uri, 
				localName, 
				qName,
				attrib );		
	}

	/**
	 * @see org.geneview.core.manager.IXmlParserManager#startElement_search4Tag(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public void startElement_search4Tag(String uri, 
			String localName, 
			String qName,
			Attributes attrib) {
		
		if ( hashTag2XmlParser.containsKey( qName ) ) 
		{
			/**
			 * Get handler registered to this "qName" ..
			 */
			IXmlParserHandler handler = hashTag2XmlParser.get( qName );
			
			try // catch (SAXException se) 
			{
				/**
				 * Register handler only if it is not 
				 * the OpenExternalXmlFileSaxHandler ...
				 */
				if ( handler.getClass().equals( 
						OpenExternalXmlFileSaxHandler.class) ) 
				{
					/**
					 * Special case: 
					 * 
					 * Open new file, but do not register new handler...
					 * Attention: do not call  sectionFinishedByHandler() from FileLoaderSaxHandler !
					 */
						/**
						 * 
						 * pass event to current handler
						 */
						handler.startElement( uri,
								localName,
								qName,
								attrib );
						
						/* early exit from try-catch block and if */
						return;
						
				} // if ( handler.getClass().equals(OpenExternalXmlFileSaxHandler.class) ) 						
				
				
				/**
				 * Regular case: register new handler ...
				 */
				
				refGeneralManager.getSingelton().logMsg(
						"AXmlParserManager.openCurrentTag( key=[" + 
						handler.getXmlActivationTag() + "] " +
						handler.getClass().getSimpleName() +	" )",
						LoggerType.VERBOSE_EXTRA );
				
				/**
				 * register new handler ...
				 */
				llXmlParserStack.add( handler );				
				currentHandler = handler;	
				
			} // try
			catch (SAXException se) 
			{
				refLoggerManager.logMsg( "XmlParserManager.startElement_search4Tag() SAX error: " +
						se.toString(),
						LoggerType.ERROR );
				
			} // try .. catch (SAXException se) 		
			
		} // if ( hashTag2XmlParser.containsKey( qName ) ) 
		
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		refLoggerManager.logMsg( "        " + qName + " TAG -->",
				LoggerType.FULL );
		
		if ( currentHandler != null ) {
//			if ( sCurrentClosingTag.equals( qName ) ) {
//				this.closeCurrentTag();
//				return;
//			}
			
			currentHandler.endElement( uri, 
					localName,
					qName);
		}

	}
	
	
	/**
	 * @see org.geneview.core.manager.IXmlParserManager#endElement_search4Tag(Stringt, Stringt, Stringt)
	 */
	public void endElement_search4Tag(String uri, 
			String localName, 
			String qName) {
		
		assert false : "should not be called but overloaded by derived class.";
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) 
		throws SAXException {
		
		if ( currentHandler != null ) {				
			currentHandler.characters(ch, start, length);
		}
	}

	
	public final void sectionFinishedByHandler( IXmlParserHandler handler ) {
		
		assert handler != null : "Can not handel null pointer!";
		
		/**
		 * allow unregistering of handler
		 */
		setXmlFileProcessedNow( false );
		
		/**
		 * 
		 */
		if ( currentHandler != handler ) {
			throw new GeneViewRuntimeException("sectionFinishedByHandler() called by wrong handler!",
					GeneViewRuntimeExceptionType.SAXPARSER);
		}
				
		closeCurrentTag();				
		
		/**
		 * enable processing flag again. Return "token".		 
		 */
		setXmlFileProcessedNow( true );
	}

	
	/**
	 * @see org.geneview.core.manager.IXmlParserManager#parseXmlFileByName(java.lang.String)
	 */
	public boolean parseXmlString( final String sMuddlewareXPath, final String xmlString ) {
		
		iCountOpenedFiles++;
		try 
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlString( " + sMuddlewareXPath + ") parse...",
					LoggerType.VERBOSE );
			
			InputSource inStream = new InputSource( xmlString );	
			
			refLoggerManager.logMsg("XmlParserManager.parseXmlString( XPath=[" + sMuddlewareXPath + "] , ..) done.",
					LoggerType.VERBOSE_EXTRA );
			
			boolean status = GeneViewInputStream.parseOnce( inStream ,
					sMuddlewareXPath,
					this,
					refLoggerManager );
			
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( XPath=[" + sMuddlewareXPath + "], ..) done.",
					LoggerType.STATUS );
			
			return 	status;
		
		} 
		catch (GeneViewRuntimeException gve)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlString( " + sMuddlewareXPath + 
					"," + xmlString + ") failed; geneview_error: " +
					gve.toString(),
					LoggerType.MINOR_ERROR_XML );
			
			return false;
		}	
		catch (RuntimeException e)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlString( " + sMuddlewareXPath + 
					"," + xmlString + ") failed; system_error: " +
					e.toString(),
					LoggerType.MINOR_ERROR_XML );			
			
			return false;
		}	
	}
	
	/**
	 * @see org.geneview.core.manager.IXmlParserManager#parseXmlFileByName(Stringt)
	 */
	public boolean parseXmlFileByName( final String filename ) {
		
		iCountOpenedFiles++;
		
		try
		{
			URL resourceUrl =  this.getClass().getClassLoader().getResource(filename);
			InputSource inSource = null;
			
			if (resourceUrl != null) {
				inSource = GeneViewInputStream.openInputStreamFromUrl(resourceUrl,	refLoggerManager );			
			}
			else
			{
				inSource = GeneViewInputStream.openInputStreamFromFile(filename, refLoggerManager);
			}
			
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( " + filename + ") parse...",
					LoggerType.VERBOSE_EXTRA );
		
		
			boolean status = GeneViewInputStream.parseOnce( inSource ,
					filename,
					this,
					refLoggerManager );
			
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( " + filename + ") done.",
					LoggerType.STATUS );
			
			return 	status;
			
		} 
		catch (GeneViewRuntimeException gve)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( " + filename + ") failed; geneview_error: " +
					gve.toString(),
					LoggerType.MINOR_ERROR_XML );
			
			return false;
		}	
		catch (RuntimeException e)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByName( " + filename + ") failed; system_error: " +
					e.toString(),
					LoggerType.MINOR_ERROR_XML );			
			
			return false;
		}			
	}
	
	/**
	 * @see org.geneview.core.manager.IXmlParserManager#parseXmlFileByName(Stringt)
	 */
	public boolean parseXmlFileByNameAndHandler( final String filename, 
			final OpenExternalXmlFileSaxHandler openFileHandler ) {
		
		// this.swapXmlParserHandler( currentHandler, openFileHandler );
		
		iCountOpenedFiles++;
		
		try {
			URL resourceUrl =  this.getClass().getClassLoader().getResource(filename);
			InputSource inSource = null;
			
			if (resourceUrl != null) {
				inSource = GeneViewInputStream.openInputStreamFromUrl(resourceUrl,	refLoggerManager );			
			}
			else
			{
				inSource = GeneViewInputStream.openInputStreamFromFile(filename, refLoggerManager);
			}
			
			return GeneViewInputStream.parseOnce( inSource , 
					filename,
					this,
					refLoggerManager );		
			
		} 
		catch (GeneViewRuntimeException gve)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByNameAndHandler( " + filename + ") failed; geneview_error: " +
					gve.toString(),
					LoggerType.MINOR_ERROR_XML );
			
			return false;
		}	
		catch (RuntimeException e)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByNameAndHandler( " + filename + ") failed; system_error: " +
					e.toString(),
					LoggerType.MINOR_ERROR_XML );			
			
			return false;
		}		
	}
	
	
	/**
	 * @see org.geneview.core.manager.IXmlParserManager#parseXmlFileByInputStream(org.xml.sax.InputSource)
	 */
	public boolean parseXmlFileByInputStream( InputSource inputStream,
			final String inputStreamText ) {
		
		iCountOpenedFiles++;
		try {
			
			return GeneViewInputStream.parseOnce( inputStream ,
					inputStreamText,
					this,
					refLoggerManager );	
		} 
		catch (GeneViewRuntimeException gve)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByInputStream( ) failed; geneview_error: " +
					gve.toString(),
					LoggerType.MINOR_ERROR_XML );
			
			return false;
		}	
		catch (RuntimeException e)
		{
			refLoggerManager.logMsg("XmlParserManager.parseXmlFileByInputStream( ) failed; system_error: " +
					e.toString(),
					LoggerType.MINOR_ERROR_XML );			
			
			return false;
		}	
	}


	public void destroyHandler() {
		
		refLoggerManager.logMsg( "XmlParserManager.destoryHandler() ... ",
				LoggerType.VERBOSE );
		
		/**
		 * Linked list...
		 */
		
		if ( llXmlParserStack == null )
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() llXmlParserStack is null",
					LoggerType.FULL );
		} // if ( llXmlParserStack == null )
		else 
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() llXmlParserStack remove objects..",
					LoggerType.FULL );
			
			if ( ! llXmlParserStack.isEmpty() ) 
			{
				Iterator <IXmlParserHandler> iterParserHandler = 
					llXmlParserStack.iterator();
				
				while ( iterParserHandler.hasNext() ) 
				{
					IXmlParserHandler handler = iterParserHandler.next();
					
					unregisterSaxHandler( handler.getXmlActivationTag() );
					handler.destroyHandler();
				} // while ( iterParserHandler.hasNext() ) 
				
				llXmlParserStack.clear();	
				
			} // if ( ! llXmlParserStack.isEmpty() ) 
			
			llXmlParserStack = null;
		} // else .. if ( llXmlParserStack == null )
		
		
		/**
		 * Hashtable ...
		 */
		
		if ( hashTag2XmlParser == null )
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() hashTag2XmlParser is null",
					LoggerType.FULL );
		} // if ( hashTag2XmlParser == null )
		else
		{
			refLoggerManager.logMsg( "XmlParserManager.destoryHandler() hashTag2XmlParser remove objects..",
					LoggerType.FULL );
			
			if ( ! hashTag2XmlParser.isEmpty() ) {
				Iterator <IXmlParserHandler> iterHandler =  hashTag2XmlParser.values().iterator();
										
				while ( iterHandler.hasNext() ) 
				{
					IXmlParserHandler refHandler = iterHandler.next(); 
					
					if ( refHandler != null )
					{
						refHandler.destroyHandler();
						refHandler = null;
					}
					
				} // while ( iterHandler.hasNext() ) 
			
				hashTag2XmlParser.clear();	
				
			} // if ( ! hashTag2XmlParser.isEmpty() ) {
			hashTag2XmlParser = null;
			
		} // else .. if ( hashTag2XmlParser == null )
		
		refLoggerManager.logMsg( "XmlParserManager.destoryHandler() ... done!",
				LoggerType.FULL );
		
		refLoggerManager.logMsg( "XML file was read sucessfully.",
				LoggerType.STATUS );
		
	}
}
