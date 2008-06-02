package org.caleydo.core.data.collection.parser;

import java.util.StringTokenizer;

import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ASaxParserHandler;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.xml.sax.Attributes;


/**
 * Parsing pieces of information present in each DComponent derived from JComponent.
 * 
 * @author Michael Kalkusch
 * 
 * @see org.caleydo.core.net.dwt.swing.parser.DPanelSaxHandler
 * @see org.caleydo.core.net.dwt.swing.parser.DButtonSaxHandler
 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler
 * 
 * @see org.xml.sax.helpers.DefaultHandler
 *
 */
public class CollectionFlatStorageSaxParserHandler 
extends ASaxParserHandler 
implements ISaxParserHandler
{
	
	protected boolean bXML_Section_DataComponent_Container;
	protected boolean bXML_Section_DataComponent;
	protected boolean bXML_Section_DataComponent_details;
	
	protected final String sTag_XML_DataCollection 					= "DataComponentItem";
	protected final String sTag_XML_DataCollection_Container 		= "DataComponent";
	protected final String sTag_XML_DataCollection_attr_id 			= "data_Id";
	protected final String sTag_XML_DataCollection_attr_type 		= "type";
	protected       String sTag_XML_DataCollection_attr_type_value 	= ManagerObjectType.STORAGE_FLAT.name();
	protected       String sTag_XML_DataCollection_details 			= "DataComponentItemDetails";
	protected       String sTag_XML_DataCollection_details_attr_type  = "type";
	protected       String sTag_XML_DataCollection_details_attr_value = "";
	
	protected int iXML_DataComponent_Id;
	
	protected StringBuffer sStringBuffer = new StringBuffer(); // collects text
	
	protected short[] dataShort = null;
	
	protected int[] dataInt = null;
	
	protected long[] dataLong = null;
	
	protected float[] dataFloat = null;
	
	protected double[] dataDouble = null;
	
	protected boolean[] dataBoolean = null;
	
	protected String[] dataString = null;
	
//	private final static String sDelimiter = " ";
	
	/**
	 * 
	 */
	public CollectionFlatStorageSaxParserHandler() {
		super();
	}
	
	/**
	 * 
	 */
	public CollectionFlatStorageSaxParserHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
	}
	

	/**
	 * Resets all parameters for new parsing.
	 * 
	 * Important: derived classes must call super.reset() inside thier reset().
	 * 
	 * @see org.caleydo.core.parser.xml.sax.ISaxParserHandler#reset()
	 */
	public void reset() {
		super.reset();	
		
		bXML_Section_DataComponent = false;
		bXML_Section_DataComponent_details = false;
		bXML_Section_DataComponent_Container = false;
		
		iXML_DataComponent_Id = -1;
		
		reset_DataContainer();
	}
	
	/**
	 * 
	 */
	public void reset_DataContainer() {
		
		dataInt = null;
		dataShort = null;
		dataLong = null;
		dataFloat = null;
		dataDouble = null;
		dataBoolean = null;
		dataString = null;
	}
	
	/**
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.AComponentSaxParserHandler#endElement_DComponent(String, String, String)
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#endElement(String, String, String)
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#startElement(String, String, String, Attributes)
	 * 
	 * @return TRUE if the token was pased already, else false
	 */
	final protected boolean startElement_DComponent( final String uri, 
			final String localName, 
			final String qName, 
			final Attributes attributes) {
		
		if ( bXML_Section_DataComponent_Container ) {
		
			if (qName.equalsIgnoreCase( sTag_XML_DataCollection )) {
				
				if ( attributes.getLength() > 1 ) {
					String bufferId = attributes.getValue(sTag_XML_DataCollection_attr_id);
					String bufferType = attributes.getValue(sTag_XML_DataCollection_attr_type);
					
					/**
					 * Check if type is handled ...
					 */
					if ( bufferType.equalsIgnoreCase( 
							sTag_XML_DataCollection_attr_type_value ) ) {
						
						try {
							iXML_DataComponent_Id = Integer.valueOf( bufferId );						
							bXML_Section_DataComponent = true;
							
							return true;
						} 
						catch (Exception e) {
							bXML_Section_DataComponent_details = false;
							appandErrorMsg("ERROR <DNetEventComponent  dNetEvent_Id=...  > does not contain an interger!");						
							return false;
						} // end try-catch
					}
					else {
						appandInfoMsg("INFO  SKIP:  <DNetEventComponent type=\"" + bufferType + 
								"\"> was not " + sTag_XML_DataCollection_attr_type_value );
						
						bXML_Section_DataComponent = false;
						return false;
						
					} // end if ( bufferType.equalsIgnoreCase("DButton"))
					
				}
				else {
					appandErrorMsg("ERROR  found <DNetEventComponent> without 2 attributes!");
					return false;
				} // end if ( attributes.getLength() == 3 )
				
			} // end if (name.equals("DNetEventComponent"))		
			else if ( bXML_Section_DataComponent ) {
				
				if ( qName.equals( sTag_XML_DataCollection_details ) ) {
					
					final String bufferType = attributes.getValue(sTag_XML_DataCollection_details_attr_type);					
					final StorageType currentStorageType = StorageType.valueOf( bufferType );
					
					if ( currentStorageType.isDataType() ) {
						// reset buffer...
						sStringBuffer.setLength( 0 );
						
						/**
						 * Enable flag for detail section...
						 */
						bXML_Section_DataComponent_details = true;
						sTag_XML_DataCollection_details_attr_value = bufferType;
					}
					
					return true;
				} 
				else if ( bXML_Section_DataComponent_details ) {
					/**
					 * Pares details now...
					 * 
					 * NOT USED YET!
					 */
					
					return true;								
				}
				
			} // end else if ( bXML_Section_DNetEventComponent ) 		
			else {
				if (qName.equalsIgnoreCase( sTag_XML_DataCollection_Container )) {
					bXML_Section_DataComponent_Container = true;
					
					/**
					 * enter section
					 */
					
					return true;
				}
				
			} // end else { ...
			
		} // end if ( bXML_Section_DataComponent_Container ) {
		else if ( qName.equalsIgnoreCase( sTag_XML_DataCollection_Container )) {			
			bXML_Section_DataComponent_Container = true;
			
			return true;
		}
		
		// tag did not match...
		return false;
		
	} // end startElement(String,Attributes) 
	
	/**
	 * Parsing details that are contained in each DComponent.
	 * Important: Each derived class has to call this method also.
	 *  
	 * @return TRUE if the token was pased already, else false
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.ASaxParserHandler#endElement(String, String, String) 
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)	 
	 * @see org.caleydo.core.net.dwt.swing.parser.AComponentSaxParserHandler#startElement_DComponent(String, String, String, Attributes)
	 */
	final protected boolean endElement_DComponent( final String uri, 
			final String localName, 
			final String qName ) {
		
		if ( localName.equals( sTag_XML_DataCollection_details )) {
			
			if ( bXML_Section_DataComponent_details ) {
				/**
				 * Call derived function to handle data...
				 */
				endElement_insideDetail(uri,
						localName,
						qName,
						sTag_XML_DataCollection_details_attr_value);
			}
			
			bXML_Section_DataComponent_details = false;
			
			return true;
		}
		else if ( localName.equals( sTag_XML_DataCollection )) {
			
			if ( bXML_Section_DataComponent ) {
				/**
				 * trigger callback if reference is set...
				 */
				if ( parentMementoCaller != null ) {
					parentMementoCaller.callbackForParser( 
							ManagerObjectType.STORAGE_FLAT,
							"",
							sTag_XML_DataCollection_attr_type_value , 
							this );
					
					reset_DataContainer();
				} 
			}
			
			bXML_Section_DataComponent = false;
			
			return true;
		} // end else if ( localName.equals( sTag_XML_DataCollection )) {
		
		else if (qName.equalsIgnoreCase( sTag_XML_DataCollection_Container )) {
			bXML_Section_DataComponent_Container = false;
			
//			if ( bXML_Section_DataComponent_details ) {
//					
//			}
			
			/**
			 * exit section
			 */			
			return true;
		} // end else if (qName.equalsIgnoreCase( sTag_XML_DataCollection_Container )) {
		
		return false;
		
	} // end endElement(String,Attributes) 

	
	/**
	 * Get unique data collection Id.
	 * 
	 * @return unique data collection Id
	 */
	public final int getXML_DataComponent_Id(){
		return this.iXML_DataComponent_Id;
	}
	
	/**
	 * Get type of data collection.
	 * 
	 * @return type of data collection
	 */
	public final String getXML_DataCollection_Type(){
		return sTag_XML_DataCollection_attr_type_value;
	}
	
	/**
	 * Appand char to StringBuffer sStringBuffer.
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] chars, int start, int length) { 
		
		if ( this.bXML_Section_DataComponent_details ) {
		    // collect the characters
			sStringBuffer.append(chars, start, length); 
		}
	}
	
	/**
	 * Note: do not forget to call startElement_DComponent(String, String, String, Attributes)!
	 * 
	 * @see org.caleydo.core.net.dwt.swing.parser.AComponentSaxParserHandler#startElement_DComponent(String, String, String, Attributes)
	 * @see org.xml.sax.ContentHandler#startElement(Stringt, Stringt, Stringt, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		
		this.startElement_DComponent(uri,localName,qName,attributes);
	}
	
	/**
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)
	 */
	public void endElement(String uri, String localName, String qName) {
		
		this.endElement_DComponent(uri,localName,qName);
	}
	
	/**
	 * Methode triggert by internal parser when a detailed-section is found.
	 * Intended to overlad this call for any kind of data storage.
	 */
	protected void endElement_insideDetail(String uri, String localName, String qName, String details) {			
		
		StringTokenizer tokenizer = 
			new StringTokenizer( new String( sStringBuffer ),
					IGeneralManager.sDelimiter_Parser_DataItems );
		
		if ( tokenizer.hasMoreElements() ) {
			
			final int iInitSize = tokenizer.countTokens();			
			final StorageType currentStorageType = StorageType.valueOf( details );
			
			if ( ! currentStorageType.isDataType()) {
				return;
			}
			
			switch ( currentStorageType ) {
			
			/**
			 * BOOLEAN
			 */
			case BOOLEAN: {
				dataBoolean = new boolean[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataBoolean[iCounter] =  Boolean.valueOf( tokenizer.nextToken() );
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need STRING, " + nfe.toString() );
						dataBoolean[iCounter] = false;
					}	
					
				} // end for...
				
				return;
			} // end case
			
			/**
			 * SHORT
			 */
			case SHORT: {
				dataShort = new short[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataShort[iCounter] =  Short.valueOf( tokenizer.nextToken() );
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need Integer, " + nfe.toString() );
						dataShort[iCounter] = 0;
					}	
					
				} // end for...
				
				return;
			} // end case
			
			
			/**
			 * INTEGER
			 */
			case INT: {
				dataInt = new int[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataInt[iCounter] =  Integer.valueOf( tokenizer.nextToken() );
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need Integer, " + nfe.toString() );
						dataInt[iCounter] = 0;
					}	
					
				} // end for...
				
				return;
			} // end case
			
			
			/**
			 * LONG
			 */
			case LONG: {
				dataLong = new long[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataLong[iCounter] =  Long.valueOf( tokenizer.nextToken() );
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need Integer, " + nfe.toString() );
						dataLong[iCounter] = 0;
					}	
					
				} // end for...
				
				return;
			} // end case
			
			/**
			 * FLOAT
			 */
			case FLOAT: {
				dataFloat = new float[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataFloat[iCounter] =  Float.valueOf( tokenizer.nextToken() );
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need Float, " + nfe.toString() );
						dataFloat[iCounter] = 0;
					}	
					
				} // end for...
				
				return;
			} // end case
			
			/**
			 * DOUBLE
			 */
			case DOUBLE: {
				dataDouble = new double[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataDouble[iCounter] =  Double.valueOf( tokenizer.nextToken() );
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need Double, " + nfe.toString() );
						dataDouble[iCounter] = 0;
					}	
					
				} // end for...
				
				return;
			} // end case
			
			/**
			 * STRING
			 */
			case STRING: {
				dataString = new String[iInitSize];
				
				for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
					try {				
						dataString[iCounter] =  tokenizer.nextToken();
					}
					catch ( NumberFormatException nfe) {
						appandErrorMsg("need STRING, " + nfe.toString() );
						dataString[iCounter] = "";
					}	
					
				} // end for...
				
				return;
			} // end case
			
			
			
			
			default:
				throw new CaleydoRuntimeException("Can not handle unkonw type [" + details + "]",
						CaleydoRuntimeExceptionType.SAXPARSER );
			
			} // end switch
		
	
	
			
			
		
		} // end if ( tokenizer.hasMoreElements() ) {
				
	}
	
	public boolean[] getDataBoolean() {
		return dataBoolean;
	}
	
	public short[] getDataShort() {
		return dataShort;
	}
	
	public int[] getDataInteger() {
		return dataInt;
	}
	
	public long[] getDataLong() {
		return dataLong;
	}
	
	public float[] getDataFloat() {
		return dataFloat;
	}
	
	public double[] getDataDouble() {
		return dataDouble;
	}
	
	public String[] getDataString() {
		return dataString;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.net.dwt.swing.parser.DParseBaseSaxHandler#getXML_ViewCanvas_Type()
	 */
	public String getXML_ViewCanvas_Type() {		
		return null;
	}
	
}
