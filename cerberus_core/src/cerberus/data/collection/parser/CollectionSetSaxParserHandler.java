/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.parser;

//import java.lang.NullPointerException;
//import java.util.StringTokenizer;
import java.util.Vector;

import org.xml.sax.Attributes;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;

import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.ASaxParserHandler;
import cerberus.util.exception.GeneViewRuntimeExceptionType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;

/**
 * Parsing pices of information present in each Colleciton.
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
public class CollectionSetSaxParserHandler 
extends ASaxParserHandler 
implements ISaxParserHandler
{
	
	protected boolean bXML_Section_DataComponent_Container;
	protected boolean bXML_Section_DataComponent;
	protected boolean bXML_Section_DataComponent_details;
	
	private boolean bXML_Section_DataComponent_details_Storage;
	private boolean bXML_Section_DataComponent_details_Select;

	
	
	protected final String sTag_XML_DataCollection 					= "DataComponentItem";
	protected final String sTag_XML_DataCollection_Container 		= "DataComponent";
	protected final String sTag_XML_DataCollection_attr_id 			= "data_Id";
	protected final String sTag_XML_DataCollection_attr_type 		= "type";
	protected final String sTag_XML_DataCollection_attr_type_value 	= ManagerObjectType.SET.name();
	protected final String sTag_XML_DataCollection_details 			= "DataComponentItemDetails";
	protected       String sTag_XML_DataCollection_details_attr_A_type  = "type";
	protected       String[] sTag_XML_DataCollection_details_attr_A_value = {"select","store"};
	protected       String sTag_XML_DataCollection_details_attr_B_type  = "dim";
	protected       String sTag_XML_DataCollection_details_attr_B_value = "";
	
	protected final String sTag_XML_DataCollection_details_attr_len 		= "Offset_Length";
	protected final String sTag_XML_DataCollection_details_attr_multi_off 	= "MultiOffset";
	protected final String sTag_XML_DataCollection_details_attr_multi_rep 	= "MultiRepeat";
	protected final String sTag_XML_DataCollection_details_attr_lookup   	= "RandomLookup";
	
	protected int iXML_DataComponent_Id;
	
	protected StringBuffer sStringBuffer = new StringBuffer(); // collects text
	
	protected Vector <int[]> vecStorage;

	protected Vector <int[]> vecSelect;

	/**
	 * ISet the maximum dimension.
	 * This is the maximum of all iCurrentDim values.
	 */
	private int iDimActive;
	
	/**
	 * ISet the current dimension.
	 */
	private int iCurrentDim;
	
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
	public CollectionSetSaxParserHandler() {
		super();
		
		vecSelect = new Vector<int[]>(3);
		vecStorage = new Vector<int[]>(3);
	}
	
	/**
	 * 
	 */
	public CollectionSetSaxParserHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
		
		vecSelect = new Vector<int[]>(3);
		vecStorage = new Vector<int[]>(3);
	}
	

	/**
	 * Resets all parameters for new parsing.
	 * 
	 * Important: derived classes must call super.reset() inside thier reset().
	 * 
	 * @see cerberus.xml.parser.ISaxParserHandler#reset()
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
		bXML_Section_DataComponent_details_Storage = false;
		bXML_Section_DataComponent_details_Select = false;
		
		if ( vecSelect != null )
			vecSelect.clear();
		if ( vecStorage != null ) 
			vecStorage.clear();
		
		iDimActive = -1;
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
					
					String bufferType = attributes.getValue(sTag_XML_DataCollection_attr_type);
					
					/**
					 * Check if type is handled ...
					 */
					if ( bufferType.startsWith( 
							sTag_XML_DataCollection_attr_type_value ) ) {
						
						String bufferId = attributes.getValue(sTag_XML_DataCollection_attr_id);
						
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
					
					if ( attributes.getLength() < 2 ) {
						throw new GeneViewRuntimeException("need attributes <... type=\"\" dim=\"*\">",
								GeneViewRuntimeExceptionType.SAXPARSER );
					}
					
					final String bufferType = attributes.getValue(sTag_XML_DataCollection_details_attr_A_type);
					final String bufferDim = attributes.getValue(sTag_XML_DataCollection_details_attr_B_type);
					
					if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_A_value[0] )) {
						/**
						 * select
						 */
						try {
							iCurrentDim = Integer.valueOf( bufferDim ).intValue();
						}
						catch ( NumberFormatException nfe ) {
							throw new GeneViewRuntimeException("attributes <... dim=\"" + bufferDim + "\"> mut be an integer",
									GeneViewRuntimeExceptionType.SAXPARSER );
						}
						bXML_Section_DataComponent_details_Select = true;
					}
					else if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_details_attr_A_value[1] )) {
						/**
						 * stogare
						 */
						bXML_Section_DataComponent_details_Storage = true;
						
					}
					else {
						appandErrorMsg("unkown attributes value <... type=\"" +  
								bufferType + "\" ...>");
						assert false:"unkown attributes value <... type=\"" +  
								bufferType + "\" ...>";
						return false;
					}
					
					/**
					 * get dimension...
					 */
					try {
						int iCurrentDim = Integer.valueOf( bufferDim ).intValue();
						
						if ( iCurrentDim > iDimActive ) {
							iDimActive = iCurrentDim;
						}
						bXML_Section_DataComponent_details = true;
						//	reset Stringbuffer...
						sStringBuffer.setLength(0);
						
						return true;
					}
					catch ( NumberFormatException nfe ) {
						appandErrorMsg("non-integer attributes value <... dim=\"" +  
								bufferDim + "\" ...>");
						assert false:"non-integer attributes value <... dim=\"" +  
								bufferDim + "\" ...>";
						return false;
					}
					
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
				
				bXML_Section_DataComponent_details = false;
				
				if (( bXML_Section_DataComponent_details_Select ) ||
					    ( bXML_Section_DataComponent_details_Storage )) {
						
						int[] iValueBuffer = 
							StringConversionTool.convertStringToIntArrayVariableLength(
								sStringBuffer.toString(),
								IGeneralManager.sDelimiter_Parser_DataItems );
												
						if ( bXML_Section_DataComponent_details_Select ) {
							
							/**
							 * enlarge vector if it is to small...
							 */
							if ( iCurrentDim >= vecSelect.size() ) {
								vecSelect.setSize(iCurrentDim+1);
							}
							
							vecSelect.setElementAt(iValueBuffer,iCurrentDim);				
							bXML_Section_DataComponent_details_Select = false;
						}
						else if (bXML_Section_DataComponent_details_Storage) {
							
							/**
							 * enlarge vector if it is to small...
							 */
							if ( iCurrentDim >= vecStorage.size() ) {
								vecStorage.setSize(iCurrentDim+1);
							}
							
							vecStorage.setElementAt(iValueBuffer,iCurrentDim);
							bXML_Section_DataComponent_details_Storage = false;				
						}
						
						return true;
					} // end if (( bXML_Section_DataComponent_details_Select ) || ...
								
			} // end if ( bXML_Section_DataComponent_details ) {
			
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
		return iXML_DataComponent_Id;
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
	 * Get the number of dimensions
	 * @return
	 */
	public int getDim() {
		return vecSelect.size();
	}


	
	public int[] getSelectByDim( final int iDim ) {
		if (( iDim >= 0 ) && ( iDim <= iDimActive )) {
			return  vecSelect.get(iDim);
		}
		//FIXME throw Exception
		return null;
	}

	public int[] getStorageByDim( final int iDim ) {
		if (( iDim >= 0 ) && ( iDim <= iDimActive )) {
			return  vecStorage.get(iDim);
		}
		//FIXME throw Exception
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#getXML_ViewCanvas_Type()
	 */
	public String getXML_ViewCanvas_Type() {		
		return null;
	}
	
}
