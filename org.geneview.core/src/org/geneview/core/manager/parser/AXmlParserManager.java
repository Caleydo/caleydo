/**
 * 
 */
package org.geneview.core.manager.parser;

import java.util.Hashtable;
//import java.util.Iterator;
import java.util.LinkedList;

//import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import org.geneview.core.data.IManagedObject;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IXmlParserManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.xml.sax.handler.IXmlParserHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
/**
 * @author Michael Kalkusch
 *
 */
public abstract class AXmlParserManager 
extends DefaultHandler
implements IXmlParserManager, IManagedObject
{

	/**
	 * Token to avoid registering and unregistering handlers during processing XMl data.
	 */
	private boolean bProcessingXmlDataNow = false;
	
	/**
	 * Defines the active handler if the opening tag was found.
	 * If no opening tag was found or  the closing tag was precessed this reference is null.
	 */
	protected IXmlParserHandler currentHandler = null;
	
	//protected String sCurrentClosingTag = "";
	
	/**
	 * Contains the Handler of the previouse opening tags. 
	 * If a new tag is opened the currentHandler is stored as last element of this vector.
	 * If a closing tag is processed the currentHandler is set to the last element in the vector and the last element in the vector is removed.
	 */
	protected LinkedList <IXmlParserHandler> llXmlParserStack;
	
	/**
	 * Hashtable of handlers and registered XMl Tags bound to them.
	 */
	protected Hashtable <String,IXmlParserHandler> hashTag2XmlParser;
	
	
	/**
	 * Reference to manager, who created this object.
	 */
	protected final IGeneralManager refGeneralManager;	
	
	
	/**
	 * 
	 */
	protected AXmlParserManager( final IGeneralManager generalManager)
	{
		assert generalManager != null: "SetFlatSimple() with null pointer";		
		
		refGeneralManager = generalManager;
		
		hashTag2XmlParser = new Hashtable < String, IXmlParserHandler > ();
		
		llXmlParserStack = new LinkedList <IXmlParserHandler> ();		
	}
	
	
	protected final void setXmlFileProcessedNow( boolean bStatus ) {
		this.bProcessingXmlDataNow = bStatus;
	}
	
	protected final boolean closeCurrentTag() {
		
		if ( this.currentHandler == null ) {
			throw new GeneViewRuntimeException("AXmlParserManager.closeCurrentTag() current handler is null! Can not close handler");
			//return false;
		}
		
		IXmlParserHandler buffer = currentHandler;
		
		refGeneralManager.getSingelton().logMsg(
				"AXmlParserManger.closeCurrentTag() key=[" +
				currentHandler.getXmlActivationTag() + "] " +
				currentHandler.getClass().getSimpleName(),
				LoggerType.VERBOSE_EXTRA );
		
		if ( ! llXmlParserStack.isEmpty() ) {
			
			//llXmlParserStack.removeLast();
			
			if ( ! llXmlParserStack.remove( buffer ) ) {
				refGeneralManager.getSingelton().logMsg(
						"AXmlParserManger.closeCurrentTag() can not remove IXmlParserHandler from list, because it is not inside!",
						LoggerType.MINOR_ERROR);
				return false;
			}
			
			/**
			 * Get previouse item from stack ...
			 */
			if ( llXmlParserStack.isEmpty() ) 
			{
				/**
				 * stack is empty, set currentHandler null!
				 */
				currentHandler = null;
			} // if ( llXmlParserStack.isEmpty() ) 
			else 
			{
				/**
				 * Get previouse item from stack.
				 */
				currentHandler = llXmlParserStack.getLast();
			} // else .. if ( llXmlParserStack.isEmpty() ) 
			
		} // if ( ! llXmlParserStack.isEmpty() ) {
		else
		{
			currentHandler = null;
		} // else ... if ( ! llXmlParserStack.isEmpty() ) {
		
		/**
		 * Clean up XmlParserHandler..
		 */
		if ( buffer.isHandlerDestoryedAfterClosingTag() ) 
		{					
			unregisterSaxHandler( buffer.getXmlActivationTag() );
			buffer.destroyHandler();
			buffer = null;
		} 
		else
		{
			refGeneralManager.getSingelton().logMsg(
					"AXmlParserManger.closeCurrentTag() key=[" +
					buffer.getXmlActivationTag() + "] " +
					buffer.getClass().getSimpleName() + 
					" do not destroyHandler() since it may be needed later on.",
					LoggerType.FULL );
		}
		
		return true;
	}
	
//	/**
//	 * Special case for recursive opening of files.
//	 * 
//	 * @param filename filename for recursive XML file
//	 * @param openFileHandler reference to SaxHandler opening the file
//	 * @return TRUE if  startElement(String,String,String,Attributes) should be called twice, else FALSE
//	 *
//	 */
//	public final boolean openCurrentTagForRecursiveReader( 
//			OpenExternalXmlFileSaxHandler newHandler,
//			final IXmlParserManager refIXmlParserManager ) {
//		
//		if ( ! refIXmlParserManager.equals( this) ) 
//		{
//			throw new GeneViewRuntimeException("AXmlParserManager.openCurrentTagForRecursiveReader() must be called by IXmlParserManager!");			
//		}
//		
//		if ( newHandler == null ) 
//		{
//			throw new GeneViewRuntimeException("AXmlParserManager.openCurrentTagForRecursiveReader() new handler is null!");
//		}
//		
//		if ( currentHandler != null ) 
//		{
//			/**
//			 * insert OpenExternalXmlFileSaxHandler in front of currentHandler...
//			 */
//			llXmlParserStack.remove( currentHandler );		
//			llXmlParserStack.add( newHandler );		
//			llXmlParserStack.add( currentHandler );
//			
//			/**
//			 * trigger calling startElement(String,String,String,Attributes) twice!
//			 */
//			return true;
//		}
//		else 
//		{
//			/**
//			 * Empty queue, no swapping necessary!
//			 */
//			llXmlParserStack.add( newHandler );	
//			
//			currentHandler = newHandler;
//			
//			/**
//			 * avoid calling startElement(String,String,String,Attributes) twice!
//			 */
//			return false;
//		}
//				
//	}
	
	/**
	 * @see org.geneview.core.data.collection.UniqueManagedInterface#getManager()
	 */
	public final IGeneralManager getManager() {
		return this.refGeneralManager;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.xml.parser.manager.IXmlParserManager#registerAndInitSaxHandler(IXmlParserHandler)
	 */
	public final boolean registerAndInitSaxHandler(IXmlParserHandler handler )
	{
		assert handler != null : "Can not handle null pointer as handler";
		
		if ( bProcessingXmlDataNow ) {
			throw new GeneViewRuntimeException("AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because Xml file is processed now!");			
			//return false;
		}
		
		if ( hashTag2XmlParser.contains( handler ) ) {
			throw new GeneViewRuntimeException("AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because it is already registered!");
			//return false;
		}
		
		String key = handler.getXmlActivationTag();
		
		if ( hashTag2XmlParser.containsKey( key ) ) {
			throw new GeneViewRuntimeException("AXmlParserManager.registerAndInitSaxHandler() can not register Handler, because String [" + 
					handler.getXmlActivationTag() + 
					"] is already registered!");
			//return false;
		}
		
		hashTag2XmlParser.put( key, handler );
		
		refGeneralManager.getSingelton().logMsg(
				"XmlParserManager.registerAndInitSaxHandler( key=["
				+ handler.getXmlActivationTag() + "] " +
				handler.getClass().getSimpleName() + " ) done.",
				LoggerType.TRANSITION );
		
		handler.initHandler();
		
		return true;
	}

	
	
	/**
	 * @see org.geneview.core.manager.IXmlParserManager#unregisterSaxHandler(Stringt)
	 */
	public final boolean unregisterSaxHandler( final String sActivationXmlTag)
	{
		assert sActivationXmlTag != null : "Can not handle null pointer as handler";
		
		if ( bProcessingXmlDataNow ) {
			throw new GeneViewRuntimeException("AXmlParserManager.registerSaxHandler() can not register Handler, because Xml file is processed now!");			
			//return false;
		}
		
		if ( ! hashTag2XmlParser.containsKey( sActivationXmlTag ) ) {
			throw new GeneViewRuntimeException("AXmlParserManager.unregisterSaxHandler() can not unregister Handler, because it is not registered!");
			//return false;
		}
		
		IXmlParserHandler refParserHandler = hashTag2XmlParser.remove( sActivationXmlTag );
		
		if ( refParserHandler != null ) {	
						
			refGeneralManager.getSingelton().logMsg(
					"XmlParserManager.unregisterHandler( key=[" 
					+ sActivationXmlTag + "] "
					+ refParserHandler.getClass().getSimpleName()
					+ " ) done.",
					LoggerType.TRANSITION );
			
			return true;
		}
		
		refGeneralManager.getSingelton().logMsg(
				"XmlParserManager.unregisterHandler( "
				+ sActivationXmlTag + " ) failed to unload!",
				LoggerType.ERROR_ONLY );
		
		return false;
	}
	
	
	public final boolean isXmlFileProcessedNow() {
		if ( this.bProcessingXmlDataNow ) {
			return true;
		}
		return false;
	}

//	/**
//	 * Swap two IXmlParserHandler.
//	 * 
//	 * @deprecated not used any more. use openCurrentTagForRecursiveReader(OpenExternalXmlFileSaxHandler, IXmlParserManager) instead.
//	 * 
//	 * @see org.geneview.core.manager.IXmlParserManager#openCurrentTagForRecursiveReader(OpenExternalXmlFileSaxHandler, IXmlParserManager) 
//	 */
//	protected final void swapXmlParserHandler( IXmlParserHandler from, 
//			IXmlParserHandler to ) {
//		int iIndexFrom = llXmlParserStack.indexOf( from );
//		
//		if ( iIndexFrom == -1 ) {
//			refGeneralManager.getSingelton().logMsg( 
//					"Error: can not find IXmlParserHandler 'from' in AXmlParserManager! " +
//					from.getXmlActivationTag(),
//					LoggerType.ERROR_ONLY );
//			return;
//		} // if
//		
//		int iIndexTo = llXmlParserStack.indexOf( to );
//		
//		if ( iIndexTo == -1 ) {
//			refGeneralManager.getSingelton().logMsg( 
//					"Error: can not find IXmlParserHandler 'to' in AXmlParserManager! " +
//					from.getXmlActivationTag(),
//					LoggerType.ERROR_ONLY );
//			return;
//		} // if
//		
//		if ( iIndexFrom < iIndexTo ) 
//		{
//			llXmlParserStack.remove( iIndexFrom );
//			llXmlParserStack.add( iIndexFrom, to );
//			
//			llXmlParserStack.remove( iIndexTo );
//			llXmlParserStack.add( iIndexTo, from );
//		}
//		else 
//		{
//			llXmlParserStack.remove( iIndexTo );
//			llXmlParserStack.add( iIndexTo, from );
//			
//			llXmlParserStack.remove( iIndexFrom );
//			llXmlParserStack.add( iIndexFrom, to );
//		} // else
//	}
	

	/*
	 * @see org.geneview.core.xml.parser.manager.IXmlParserManager#getCurrentXmlParserHandler()
	 */
	public final IXmlParserHandler getCurrentXmlParserHandler() {
		return this.currentHandler;
	}
	

}
