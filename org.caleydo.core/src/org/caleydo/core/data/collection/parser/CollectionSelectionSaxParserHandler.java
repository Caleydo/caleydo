package org.caleydo.core.data.collection.parser;

import java.util.StringTokenizer;

import org.xml.sax.Attributes;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ASaxParserHandler;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;


/**
 * Parsing pieces of information present in each Colleciton.
 * 
 * @author Michael Kalkusch
 * 
 * @see prometheus.net.dwt.swing.parser.DPanelSaxHandler
 * @see prometheus.net.dwt.swing.parser.DButtonSaxHandler
 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler
 * 
 * @see org.xml.sax.helpers.DefaultHandler
 *
 */
public class CollectionSelectionSaxParserHandler 
extends ASaxParserHandler 
implements ISaxParserHandler
{
	
	protected boolean bXML_Section_DataComponent_Container;
	protected boolean bXML_Section_DataComponent;
	protected boolean bXML_Section_DataComponent_details;
	
	private boolean bXML_Section_DataComponent_details_OffsetLength;
	private boolean bXML_Section_DataComponent_details_Lookup;
	private boolean bXML_Section_DataComponent_details_MultiOffset;
	private boolean bXML_Section_DataComponent_details_MultiRepeat;
	private boolean bXML_Section_DataComponent_details_filename;

	
	
	protected final String sTag_XML_DataCollection 					= "DataComponentItem";
	protected final String sTag_XML_DataCollection_Container 		= "DataComponent";
	protected final String sTag_XML_DataCollection_attr_id 			= "data_Id";
	protected final String sTag_XML_DataCollection_attr_type 		= "type";
	protected final String sTag_XML_DataCollection_attr_type_value 	= ManagerObjectType.VIRTUAL_ARRAY.name();
	protected final String sTag_XML_DataCollection_details 			= "DataComponentItemDetails";
	protected final String sTag_XML_DataCollection_details_attr_filetag = "FileName";
	protected final String sTag_XML_DataCollection_details_attr_filename = "microArrayFileName";
	
	protected       String sTag_XML_DataCollection_details_attr_type  = "type";
	protected       String sTag_XML_DataCollection_details_attr_value = "";
	
	/** used for microarray datasets */	
	protected       String sTag_XML_DataCollection_details_attr_filename_value = "";
	
	/** Contains token for parsing microArray data file */
	protected	    String sTag_XML_DataCollection_details_attr_filename_value_tokenPattern = "";
	
	protected final String sTag_XML_DataCollection_details_attr_len 		= "Offset_Length";
	protected final String sTag_XML_DataCollection_details_attr_multi_off 	= "MultiOffset";
	protected final String sTag_XML_DataCollection_details_attr_multi_rep 	= "MultiRepeat";
	protected final String sTag_XML_DataCollection_details_attr_lookup   	= "RandomLookup";
	
	protected int iXML_DataComponent_Id;
	
	protected StringBuffer sStringBuffer = new StringBuffer(); // collects text
	
	protected int iData_Offset;
	
	protected int[] iData_RLE_Random_LookupTable = null;
	
	protected int iData_Length;
	
	protected int iData_MultiOffset;
	
	protected int iData_MultiRepeat;
	
	protected String sData_Label;
	
	/**
	 * Stores the current selection type as String.
	 */
	protected String sSelectionType;
	
	/**
	 * Stores the current IVirtualArray type.
	 */
	protected ManagerObjectType eSelectionType;
	
	//private final static String sDelimiter = " ";
	
	/**
	 * 
	 */
	public CollectionSelectionSaxParserHandler() {
		super();
	}
	
	/**
	 * 
	 */
	public CollectionSelectionSaxParserHandler(final boolean bEnableHaltOnParsingError) {
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
		
		/**
		 * Reset all flags for <DataComponentItemDetails>..
		 */
		bXML_Section_DataComponent_details_OffsetLength = false;
		bXML_Section_DataComponent_details_Lookup = false;
		bXML_Section_DataComponent_details_MultiOffset = false;
		bXML_Section_DataComponent_details_MultiRepeat = false;
		bXML_Section_DataComponent_details_filename = false;
		
		sTag_XML_DataCollection_details_attr_filename_value_tokenPattern = "";
		sTag_XML_DataCollection_details_attr_filename_value = "";
	}
	
	
	
	/**
	 * 
	 * @see prometheus.net.dwt.swing.parser.AComponentSaxParserHandler#endElement_DComponent(String, String, String)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#endElement(String, String, String)
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#startElement(String, String, String, Attributes)
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
					if ( bufferType.startsWith( 
							sTag_XML_DataCollection_attr_type_value ) ) {
						
						try {
							iXML_DataComponent_Id = Integer.valueOf( bufferId );						
							bXML_Section_DataComponent = true;
							sSelectionType = bufferType;
							eSelectionType = ManagerObjectType.valueOf( bufferType );
							
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
					
					if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_len )) {
						bXML_Section_DataComponent_details_OffsetLength = true;
					}
					else if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_lookup )) {
						bXML_Section_DataComponent_details_Lookup = true;
					}
					else if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_multi_off )) {
						bXML_Section_DataComponent_details_MultiOffset = true;
					}
					else if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_multi_rep )) {
						bXML_Section_DataComponent_details_MultiRepeat = true;
					}
					else if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_filetag )) {
						
						sTag_XML_DataCollection_details_attr_filename_value = 
							attributes.getValue( sTag_XML_DataCollection_details_attr_filename );
						
						bXML_Section_DataComponent_details_filename = true;
						
					}
					else {
						/**
						 * nothing matched...
						 */
						return false;
					}
					
					/**
					 * only reach if something matched previousely...
					 */
					bXML_Section_DataComponent_details = true;
					
					// reset Stringbuffer...
					sStringBuffer.setLength(0);
					
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
	 * @see prometheus.net.dwt.swing.parser.ASaxParserHandler#endElement(String, String, String) 
	 * @see org.xml.sax.ContentHandler#endElement(Stringt, Stringt, Stringt)	 
	 * @see prometheus.net.dwt.swing.parser.AComponentSaxParserHandler#startElement_DComponent(String, String, String, Attributes)
	 */
	final protected boolean endElement_DComponent( final String uri, 
			final String localName, 
			final String qName ) {
		
		if ( localName.equals( sTag_XML_DataCollection_details )) {
			
			if ( bXML_Section_DataComponent_details ) {
				/**
				 * Call derived function to handle data...
				 */
				endElement_insideDetail();
				bXML_Section_DataComponent_details = false;
				
				return true;
			}
			
			return true;
		}
		else if ( localName.equals( sTag_XML_DataCollection )) {
			
			if ( bXML_Section_DataComponent ) {
				/**
				 * trigger callback if reference is set...
				 */
				if ( refParentMementoCaller != null ) {
					refParentMementoCaller.callbackForParser( 
							eSelectionType,
							"",
							sSelectionType, 
							this );
					
					
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
	 * Get the file name of a mircoarray dataset.
	 * Only set when type="VIRTUAL_ARRAY_LOAD_MICROARRAY".
	 * Used by class prometheus.data.loader.MicroArrayLoader .
	 * 
	 * @see prometheus.data.loader.MicroArrayLoader1Storage
	 * 
	 * @return file name of a mircoarray dataset
	 */
	public final String getXML_MicroArray_FileName() {
		return sTag_XML_DataCollection_details_attr_filename_value; 
	}
	
	public final String getXML_MicroArray_TokenPattern() { 
		return sTag_XML_DataCollection_details_attr_filename_value_tokenPattern;
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
	 * @see prometheus.net.dwt.swing.parser.AComponentSaxParserHandler#startElement_DComponent(String, String, String, Attributes)
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
	private void endElement_insideDetail() {			
		
		try {	
			if ( bXML_Section_DataComponent_details_OffsetLength ) {
				StringTokenizer tokenizer = 
					new StringTokenizer( new String( sStringBuffer ),
							IGeneralManager.sDelimiter_Parser_DataItems );
				
				if ( tokenizer.countTokens() < 2 ) {
					appandErrorMsg("OffsetLength need 2 Integer");
					return;
				}
				
				iData_Offset = Integer.valueOf(  tokenizer.nextToken() );	
				iData_Length = Integer.valueOf(  tokenizer.nextToken() );							
					
				bXML_Section_DataComponent_details_OffsetLength = false;
				return;
			}
			else if ( bXML_Section_DataComponent_details_MultiOffset ) {
				
				iData_MultiOffset = Integer.valueOf(  new String( sStringBuffer ) );				
				bXML_Section_DataComponent_details_MultiOffset = false;
				return;
			}
			else if ( bXML_Section_DataComponent_details_MultiRepeat ) {
				
				iData_MultiRepeat = Integer.valueOf(  new String( sStringBuffer ) );				
				bXML_Section_DataComponent_details_MultiRepeat = false;
				return;
			}
			else if ( bXML_Section_DataComponent_details_filename ) {
				
				sTag_XML_DataCollection_details_attr_filename_value_tokenPattern = 
					new String( sStringBuffer );
				
				bXML_Section_DataComponent_details_filename = false;
				return;
			}
			
		}
		catch ( NumberFormatException nfe) {
			appandErrorMsg("need Integer, " + nfe.toString() );
			return;
		}
		
		if ( bXML_Section_DataComponent_details_Lookup ) {
			bXML_Section_DataComponent_details_Lookup = false;
			
			StringTokenizer tokenizer = 
				new StringTokenizer( new String( sStringBuffer ),
						IGeneralManager.sDelimiter_Parser_DataItems );
			
			iData_RLE_Random_LookupTable = new int[tokenizer.countTokens()];
			
			for ( int iCounter =0 ; tokenizer.hasMoreElements(); iCounter++ ) {
				try {				
					iData_RLE_Random_LookupTable[iCounter] =  Integer.valueOf( tokenizer.nextToken() );
				}
				catch ( NumberFormatException nfe) {
					appandErrorMsg("need Integer, " + nfe.toString() );
					iData_RLE_Random_LookupTable[iCounter] = 0;
				}	
				
			} // end for...
			
			return;
		}
				
	}

	public int getXML_DataOffset() {
		return iData_Offset;
	}
	
	public int getXML_DataLength() {
		return iData_Length;
	}
	
	public int getXML_DataMultiRepeat() {
		return iData_MultiRepeat;
	}

	
	public int getXML_DataMultiOffset() {
		return iData_MultiOffset;
	}

	public String getXML_DataLabel() {
		return sData_Label;
	}
	
	public int[] getXML_RLE_Random_LookupTable () {
		return iData_RLE_Random_LookupTable;
	}
	/*
	 *  (non-Javadoc)
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#getXML_ViewCanvas_Type()
	 */
	public String getXML_ViewCanvas_Type() {		
		return null;
	}
	
}
