/**
 * 
 */
package cerberus.xml.parser.manager;

import java.util.Hashtable;
//import java.util.Iterator;
import java.util.LinkedList;

//import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import cerberus.data.IManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.xml.parser.handler.IXmlParserHandler;
import cerberus.xml.parser.handler.importer.OpenExternalXmlFileSaxHandler;
import cerberus.util.exception.CerberusRuntimeException;
/**
 * @author kalkusch
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
			throw new CerberusRuntimeException("AXmlParserManager.closeCurrentTag() current handler is null! Can not close handler");
			//return false;
		}
		
		IXmlParserHandler buffer = currentHandler;
		
		if ( ! llXmlParserStack.isEmpty() ) {
			currentHandler = llXmlParserStack.getLast();
			llXmlParserStack.removeLast();
		}
		else
		{
			currentHandler = null;
		}
		
		/**
		 * Clean up XmlParserHandler..
		 */
		if ( buffer.hasOpeningTagOnlyOnce() ) {
			buffer.destroyHandler();
			unregisterSaxHandler( buffer.getXmlActivationTag() );
			buffer = null;
		}
		
		return true;
	}
	
	protected final void openCurrentTag( IXmlParserHandler newHandler ) {
		
		if ( newHandler == null ) {
			throw new CerberusRuntimeException("AXmlParserManager.openCurrentTag() new handler is null!");
		}
		
		if ( ! newHandler.getClass().getName().equals( 
				OpenExternalXmlFileSaxHandler.class.getName()) ) 
		{
			
			
			
			llXmlParserStack.add( currentHandler );		
			currentHandler = newHandler;
		}
		
		llXmlParserStack.add( currentHandler );		
		currentHandler = newHandler;
				
	}
	
	/**
	 * @see cerberus.data.collection.UniqueManagedInterface#getManager()
	 */
	public final IGeneralManager getManager() {
		return this.refGeneralManager;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.manager.IXmlParserManager#registerSaxHandler(cerberus.xml.parser.manager.IXmlParserHandler, boolean)
	 */
	public final boolean registerSaxHandler(IXmlParserHandler handler )
	{
		assert handler != null : "Can not handle null pointer as handler";
		
		if ( bProcessingXmlDataNow ) {
			throw new CerberusRuntimeException("AXmlParserManager.registerSaxHandler() can not register Handler, because Xml file is processed now!");			
			//return false;
		}
		
		if ( hashTag2XmlParser.contains( handler ) ) {
			throw new CerberusRuntimeException("AXmlParserManager.registerSaxHandler() can not register Handler, because it is already registered!");
			//return false;
		}
		
		String key = handler.getXmlActivationTag();
		
		if ( hashTag2XmlParser.containsKey( key ) ) {
			throw new CerberusRuntimeException("AXmlParserManager.registerSaxHandler() can not register Handler, because String [" + 
					handler.getXmlActivationTag() + 
					"] is already registered!");
			//return false;
		}
		
		hashTag2XmlParser.put( key, handler );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				"XmlParserManager.registerSaxHandler( "
				+ handler.getXmlActivationTag() + ") done.",
				LoggerType.STATUS );
		
		handler.initHandler();
		
		return true;
	}

	
	/**
	 * @see cerberus.xml.parser.manager.IXmlParserManager#unregisterSaxHandler(cerberus.xml.parser.manager.IXmlParserHandler)
	 */
	public final boolean unregisterSaxHandler(IXmlParserHandler handler)
	{
		assert handler != null : "Can not handel null pointer as handler";
		
		if ( bProcessingXmlDataNow ) {
			throw new CerberusRuntimeException("AXmlParserManager.registerSaxHandler() can not register Handler, because Xml file is processed now!");			
			//return false;
		}
		
		if ( ! hashTag2XmlParser.containsValue( handler ) ) {
			throw new CerberusRuntimeException("AXmlParserManager.unregisterSaxHandler() can not unregister Handler, because it is not registered!");
			//return false;
		}
		
		if ( hashTag2XmlParser.remove( handler.getXmlActivationTag() ) != null ) {
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"XmlParserManager.unregisterSaxHandler( "
					+ handler.getXmlActivationTag() + ") done.",
					LoggerType.STATUS );
			
//			if ( handler.hasOpeningTagOnlyOnce() ) {
//				handler.destroyHandler();
//			}
			handler.destroyHandler();
			
			return true;
		}		
		return false;
	}
	
	
	/**
	 * @see cerberus.xml.parser.manager.IXmlParserManager#unregisterSaxHandler(java.lang.String)
	 */
	public final boolean unregisterSaxHandler(String sActivationXmlTag)
	{
		assert sActivationXmlTag != null : "Can not handle null pointer as handler";
		
		if ( bProcessingXmlDataNow ) {
			throw new CerberusRuntimeException("AXmlParserManager.registerSaxHandler() can not register Handler, because Xml file is processed now!");			
			//return false;
		}
		
		if ( ! hashTag2XmlParser.containsKey( sActivationXmlTag ) ) {
			throw new CerberusRuntimeException("AXmlParserManager.unregisterSaxHandler() can not unregister Handler, because it is not registered!");
			//return false;
		}
		
		IXmlParserHandler refParserHandler = hashTag2XmlParser.remove( sActivationXmlTag );
		
		if ( refParserHandler != null ) {	
						
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"XmlParserManager.unregisterHandler( "
					+ sActivationXmlTag + ") done.",
					LoggerType.STATUS );
			
			return true;
		}
		return false;
	}
	
	
	public final boolean isXmlFileProcessedNow() {
		if ( this.bProcessingXmlDataNow ) {
			return true;
		}
		return false;
	}


}
