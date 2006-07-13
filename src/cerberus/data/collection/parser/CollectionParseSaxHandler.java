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
import org.xml.sax.Attributes;

import cerberus.manager.type.BaseManagerType;

//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;

import cerberus.xml.parser.DParseSaxHandler;
import cerberus.xml.parser.DParseBaseSaxHandler;

//import prometheus.util.exception.PrometheusSaxParserException;

/**
 * Parsing pices of information present in each DComponent derived from JComponent.
 * 
 * @author Michael Kalkusch
 * 
 * @see prometheus.net.dwt.swing.parser.DPanelSaxHandler
 * @see prometheus.net.dwt.swing.parser.DButtonSaxHandler
 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler
 * 
 * @see org.xml.sax.helpers.DefaultHandler
 *
 */
public abstract class CollectionParseSaxHandler 
extends DParseBaseSaxHandler 
implements DParseSaxHandler
{
	
	protected boolean bXML_Section_DataComponent_Container;
	protected boolean bXML_Section_DataComponent;
	protected boolean bXML_Section_DataComponent_details;
	
	
	protected String sTag_XML_DataComponent_details = "DataComponentItemDetails";
	protected final String sTag_XML_DataCollection ="DataComponentItem";
	protected final String sTag_XML_DataCollection_Container = "DataComponent";
	protected final String sTag_XML_DataComponent_attr_id = "data_Id";
	protected final String sTag_XML_DataComponent_attr_type = "type";
	
	protected int iXML_DataComponent_Id;
	
	protected StringBuffer sStringBuffer = new StringBuffer(); // collects text
	
	
	/**
	 * Type of collection as String
	 * 
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#eXML_DataComonent_type
	 */
	private String sTag_XML_DataCollection_attr_type_value = "Collection";
	
	
	/**
	 * Type of collection
	 * 
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#sTag_XML_DataCollection_attr_type_value
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#setXML_DataCollection_Type(String)
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#getXML_DataCollection_Type()
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#getXML_DataCollection_BaseManagerType()
	 * 
	 */
	private BaseManagerType eXML_DataComonent_type;
	
	/**
	 * 
	 */
	public CollectionParseSaxHandler() {
		super();
	}
	
	/**
	 * 
	 */
	public CollectionParseSaxHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
	}
	

	/**
	 * Resets all parameters for new parsing.
	 * 
	 * Important: derived classes must call super.reset() inside thier reset().
	 * 
	 * @see cerberus.xml.parser.DParseSaxHandler#reset()
	 */
	public void reset() {
		super.reset();	
		
		bXML_Section_DataComponent = false;
		bXML_Section_DataComponent_details = false;
		bXML_Section_DataComponent_Container = false;
		
		iXML_DataComponent_Id = -1;
		
	}
	
	
	
	/**
	 * 
	 * @see prometheus.net.dwt.swing.parser.DParseComponentSaxHandler#endElement_DComponent(String, String, String)
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#endElement(String, String, String)
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#startElement(String, String, String, Attributes)
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
					String bufferId = attributes.getValue(sTag_XML_DataComponent_attr_id);
					String bufferType = attributes.getValue(sTag_XML_DataComponent_attr_type);
					
					if ( bufferType.equalsIgnoreCase( sTag_XML_DataCollection_attr_type_value )) {
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
						bXML_Section_DataComponent_details = false;
						return false;
						
					} // end if ( bufferType.equalsIgnoreCase("DButton"))
					
				}
				else {
					appandErrorMsg("ERROR  found <DNetEventComponent> without 2 attributes!");
					return false;
				} // end if ( attributes.getLength() == 3 )
				
			} // end if (name.equals("DNetEventComponent"))		
			else if ( bXML_Section_DataComponent ) {
				
				if ( qName.equals( sTag_XML_DataComponent_details ) ) {
					bXML_Section_DataComponent_details = true;
					
					// reset buffer...
					sStringBuffer.setLength( 0 );
					
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
		}
		
		// tag did not match...
		return false;
		
	} // end startElement(String,Attributes) 
	
	/**
	 * Parsing details that are contained in each DComponent.
	 * Important: Each derived class has to call this methode also.
	 *  
	 * @return TRUE if the token was pased already, else false
	 * 
	 * @see prometheus.net.dwt.swing.parser.DParseBaseSaxHandler#endElement(String, String, String) 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)	 
	 * @see prometheus.net.dwt.swing.parser.DParseComponentSaxHandler#startElement_DComponent(String, String, String, Attributes)
	 */
	final protected boolean endElement_DComponent( final String uri, 
			final String localName, 
			final String qName ) {
		
		if ( localName.equals( sTag_XML_DataComponent_details )) {
			
			if ( bXML_Section_DataComponent_details ) {
				/**
				 * Call derived function to handle data...
				 */
				endElement_insideDetail(uri,
						localName,
						qName,
						sTag_XML_DataCollection_attr_type_value);
			}
			
			bXML_Section_DataComponent_details = false;
			
			return true;
		}
		else if ( localName.equals( sTag_XML_DataCollection )) {
			
			if ( bXML_Section_DataComponent ) {
				/**
				 * trigger callback if reference is set...
				 */
				if ( refParentMementoCaller != null ) {
					refParentMementoCaller.callbackForParser( 
							eXML_DataComonent_type,
							"",
							sTag_XML_DataCollection_attr_type_value,
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
	 * Get type of data collection.
	 * 
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#setXML_DataCollection_Type
	 * 
	 * @return type of data collection
	 */
	public final String getXML_DataCollection_Type(){
		return sTag_XML_DataCollection_attr_type_value;
	}
	
	/**
	 * Get type of data collection as BaseManagerType
	 * 
	 * @see prometheus.data.collection.parser.CollectionParseSaxHandler#setXML_DataCollection_Type
	 * 
	 * @return type of data collection
	 */
	public final BaseManagerType getXML_DataCollection_BaseManagerType(){
		return eXML_DataComonent_type;
	}
	
	public final void setXML_DataCollection_Type( String setType ) {
		sTag_XML_DataCollection_attr_type_value = setType;
		
		BaseManagerType buffer = BaseManagerType.getType( setType );
		
		if ( buffer == null ) {
			throw new RuntimeException("setXML_DataCollection_Type() failed due to unkown type " +
					setType );
		}
		
		eXML_DataComonent_type = buffer;
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
	 * @see prometheus.net.dwt.swing.parser.DParseComponentSaxHandler#startElement_DComponent(String, String, String, Attributes)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public abstract void startElement(String uri, 
			String localName, 
			String qName, 
			Attributes attributes);
	
	/**
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public abstract void endElement(String uri, String localName, String qName);
	
	/**
	 * Methode triggert by internal parser when a detailed-section is found.
	 * Intended to overlad this call for any kind of data storage.
	 */
	abstract protected void endElement_insideDetail(String uri, 
			String localName, 
			String qName,
			String details );
	
}
