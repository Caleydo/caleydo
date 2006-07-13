/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.parser;

import java.lang.NullPointerException;
import org.xml.sax.Attributes;

import cerberus.manager.type.BaseManagerType;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.xml.parser.DParseBaseSaxHandler;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;


/**
 * Parsing pices of information present in each DComponent derived from JComponent.
 * 
 * @author Michael Kalkusch
 * 
 * @see cerberus.net.dwt.swing.parser.DPanelSaxHandler
 * @see cerberus.net.dwt.swing.parser.DButtonSaxHandler
 * @see cerberus.net.dwt.swing.parser.DParseBaseSaxHandler
 * 
 * @see org.xml.sax.helpers.DefaultHandler
 *
 */
public abstract class DParseComponentSaxHandler 
extends DParseBaseSaxHandler 
implements DParseSaxHandler
{
	
	protected boolean bXML_Section_DNetEventComponent;
	protected boolean bXML_Section_NetEvent_Details;
	
	protected String sTag_XML_DEvent_type;
	
	protected String sTag_XML_DEvent_details = "DNetEventDetails";
	protected final String sTag_XML_DNetEventComponent ="DNetEventComponent";		
	protected final String sTag_XML_DEvent_details_attr_position = "position";
	protected final String sTag_XML_DEvent_details_attr_state = "state";
	
	protected int iXML_position_x;
	protected int iXML_position_y;
	protected int iXML_position_width;
	protected int iXML_position_height;
	
	protected boolean bXML_state_enabled;
	protected boolean bXML_state_visible;
	protected String sXML_state_label;
	protected String sXML_state_tooltip;
	
	protected int iXML_dNetEvent_Id;
	
	/**
	 * 
	 */
	public DParseComponentSaxHandler() {
		super();
	}
	
	/**
	 * 
	 */
	public DParseComponentSaxHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
	}
	

	/**
	 * Resets all parameters for new parsing.
	 * 
	 * Important: derived classes must call super.reset() inside thier reset().
	 * 
	 * @see cerberus.net.dwt.swing.parser.DParseSaxHandler#reset()
	 */
	public void reset() {
		super.reset();	
		
		bXML_state_enabled = false;
		bXML_state_visible = false;
		bXML_Section_DNetEventComponent = false;
		bXML_Section_NetEvent_Details = false;
		
		sXML_state_label = "none";
		sXML_state_tooltip = "none";
		
		iXML_dNetEvent_Id = -1;
		iXML_position_x = 0;
		iXML_position_y = 0;
		iXML_position_width = 0;
		iXML_position_height = 0;
	}
	
	
	
	/**
	 * 
	 * @see cerberus.net.dwt.swing.parser.DParseComponentSaxHandler#endElement_DComponent(String, String, String)
	 * @see cerberus.net.dwt.swing.parser.DParseBaseSaxHandler#endElement(String, String, String)
	 * @see cerberus.net.dwt.swing.parser.DParseBaseSaxHandler#startElement(String, String, String, Attributes)
	 * 
	 * @return TRUE if the token was pased already, else false
	 */
	final protected boolean startElement_DComponent( final String uri, 
			final String localName, 
			final String qName, 
			final Attributes attributes) {
		
		if (qName.equalsIgnoreCase( sTag_XML_DNetEventComponent )) {
			
			if ( attributes.getLength() >= 2 ) {
				
				String bufferType = attributes.getValue("type");
				
				//FIXME tag label is ignored!				
				//String bufferXMLLabel = attributes.getValue("label");
				
				if ( bufferType.equalsIgnoreCase( sTag_XML_DEvent_type )) {
					String bufferId = attributes.getValue("dNetEvent_Id");
					
					try {
						iXML_dNetEvent_Id = Integer.valueOf( bufferId );						
						bXML_Section_DNetEventComponent = true;
						
						return true;
					} 
					catch (Exception e) {
						appandErrorMsg("ERROR <DNetEventComponent  dNetEvent_Id=...  > does not contain an interger!");						
						return false;
					} // end try-catch
				}
				else {
					appandInfoMsg("INFO  SKIP:  <DNetEventComponent type=\"" + bufferType +"\"> was not DPanel.");
					bXML_Section_DNetEventComponent = false;
					return false;
					
				} // end if ( bufferType.equalsIgnoreCase("DButton"))
				
			}
			else {
				appandErrorMsg("ERROR  found <DNetEventComponent> without 2 attributes!");
				return false;
			} // end if ( attributes.getLength() == 3 )
			
		} // end if (name.equals("DNetEventComponent"))		
		else if ( bXML_Section_DNetEventComponent ) {
			
			if ( qName.equals( sTag_XML_DEvent_details ) ) {
				bXML_Section_NetEvent_Details = true;
				
				return true;
			} 
			else if ( bXML_Section_NetEvent_Details ) {
				/**
				 * Pares details now...
				 */
				
				if ( qName.equals(sTag_XML_DEvent_details_attr_position) ) {
					if ( attributes.getLength() > 3 ) {
						try {
							iXML_position_x =
								parseStringToInt( attributes.getValue("x") );
							iXML_position_y = 
								parseStringToInt( attributes.getValue("y") );
							iXML_position_width =
								parseStringToInt( attributes.getValue("width") );
							iXML_position_height = 
								parseStringToInt( attributes.getValue("height") );
							
							return true;
						}
						catch (NullPointerException ne) {
							appandErrorMsg("error while parsing <"+
									sTag_XML_DEvent_details_attr_position +
									" for x= y= widht= height= >");
							return false;
						}
					}
					else {
						appandErrorMsg("ERROR in Syntax: <" +
								sTag_XML_DEvent_details_attr_position + 
								"  x=  y=  widht=  height= >");												
						return false;
					}
				}				
				else if ( qName.equals(sTag_XML_DEvent_details_attr_state) ) {
					try {
						bXML_state_enabled =
							parseStringToBool( attributes.getValue("enabled") );
						bXML_state_visible = 
							parseStringToBool( attributes.getValue("visible") );
						sXML_state_label = attributes.getValue("label");
						sXML_state_tooltip = attributes.getValue("tooltip");
						
						return true;
					}
					catch (NullPointerException ne) {
						appandErrorMsg("error while parsing <"+
								sTag_XML_DEvent_details_attr_position +
								" for x= y= widht= height= >");
						return false;
					}
				}
			}
		} // end else if ( bXML_Section_DNetEventComponent ) 
		
		// tag did not match...
		return false;
		
	} // end startElement(String,Attributes) 
	
	/**
	 * Parsing details that are contained in each DComponent.
	 * Important: Each derived class has to call this methode also.
	 *  
	 * @return TRUE if the token was pased already, else false
	 * 
	 * @see cerberus.net.dwt.swing.parser.DParseBaseSaxHandler#endElement(String, String, String) 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)	 
	 * @see cerberus.net.dwt.swing.parser.DParseComponentSaxHandler#startElement_DComponent(String, String, String, Attributes)
	 */
	final protected boolean endElement_DComponent( final String uri, 
			final String localName, 
			final String qName ) {
		
		if ( localName.equals( sTag_XML_DEvent_details )) {
			this.bXML_Section_NetEvent_Details = false;
			return true;
		}
		else if ( localName.equals( sTag_XML_DNetEventComponent )) {
			this.bXML_Section_DNetEventComponent = false;
			
			/**
			 * trigger callback if reference is set...
			 */
			if ( refParentMementoCaller != null ) {
				refParentMementoCaller.callbackForParser(
						//BaseManagerType.GUI_COMPONENT,
						BaseManagerType.VIEW_HISTOGRAM2D,
						"",
						sTag_XML_DEvent_type , 
						this );
			} 
			
			return true;
		}
		
		return false;
		
	} // end endElement(String,Attributes) 

	public final int getXML_position_x() {
		return iXML_position_x;
	}
	
	public final int getXML_position_y(){
		return iXML_position_y;
	}
	
	public final int getXML_position_width(){
		return iXML_position_width;
	}
	
	public final int getXML_position_height(){
		return iXML_position_height;
	}
	
	
	public final boolean getXML_state_enabled(){
		return bXML_state_enabled;
	}
	
	public final boolean getXML_state_visible(){
		return bXML_state_visible;
	}
	
	public final String getXML_state_label(){
		return sXML_state_label;
	}
	
	public final String getXML_state_tooltip(){
		return sXML_state_tooltip;
	}
	
	public final int getXML_dNetEvent_Id(){
		return iXML_dNetEvent_Id;
	}
	
	public final String getXML_ViewCanvas_Type(){
		return sTag_XML_DEvent_type;
	}
	
	/**
	 * Note: do not forget to call startElement_DComponent(String, String, String, Attributes)!
	 * 
	 * @see cerberus.net.dwt.swing.parser.DParseComponentSaxHandler#startElement_DComponent(String, String, String, Attributes)
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
}
