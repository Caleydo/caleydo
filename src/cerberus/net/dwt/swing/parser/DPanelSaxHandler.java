/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.parser;

import java.util.Vector;
import java.util.Iterator;
import java.lang.NullPointerException;
import org.xml.sax.Attributes;

import cerberus.data.xml.MementoNetEventXML;
import cerberus.xml.parser.DParseSaxHandler;

//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;
//import org.xml.sax.helpers.LocatorImpl;
//
//import cerberus.util.exception.PrometheusSaxParserException;

/**
 * @author Michael Kalkusch
 *
 */
public class DPanelSaxHandler 
extends DParseComponentSaxHandler 
implements DParseSaxHandler
{

	private boolean bXML_Section_SubComponents = false;
	private boolean bXML_Section_SubCommandListener = false;
	private boolean bXML_Section_SubNetEventListener = false;
	
	private final String sTag_XML_SubNetEventListener = "SubNetEventListener";
	private final String sTag_XML_SubNetEventListener_attr = "NetListener";
	private final String sTag_XML_SubCommandListener = "SubCommandListener";
	private final String sTag_XML_SubCommandListener_attr = "CmdListener";
	
	private final String sTag_XML_SubComponents = "SubPanelComponents";
	private final String sTag_XML_SubComponents_item = "DNetEventPanelItem";
	private final String sTag_XML_SubComponents_item_attr = "dNetEvent_Id";
	

	/**
	 * List of all NetEventListener Id's
	 */
	private Vector<Integer> vecNetEventListenerId;
	
	/**
	 * List of all ICommandListener Id's
	 */
	private Vector<Integer> vecCommandListenerId;
	
	/**
	 * List of all DNetComponents Id's
	 */
	private Vector<Integer> vecDNetComponentsId;
	
	/**
	 * 
	 */
	public DPanelSaxHandler() {
		super();
		sTag_XML_DEvent_type = "DPanel";
	}
	
	/**
	 * 
	 */
	public DPanelSaxHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
		sTag_XML_DEvent_type = "DPanel";
		
	}
	
	public DPanelSaxHandler(final MementoNetEventXML setRefParent) {
		super();
		
		this.refParentMementoCaller = setRefParent;
		sTag_XML_DEvent_type = "DPanel";
	}

	
	/**
	 * Reset all parameters to resart parsing.
	 * 
	 * @see cerberus.net.dwt.swing.parser.DParseSaxHandler#reset()
	 */
	public void reset() {
		super.reset();
		
		// next line would be is overwritten by super constructor!
		//sTag_XML_ViewCanvas_type = "DPanel";
		
		bXML_Section_SubComponents = false;
		bXML_Section_SubCommandListener = false;
		bXML_Section_SubNetEventListener = false;
		
		if ( vecNetEventListenerId == null) {
			vecNetEventListenerId = new Vector<Integer> ();
		}
		else {
			vecNetEventListenerId.clear();
		}
		
		if ( vecCommandListenerId == null ) {
			vecCommandListenerId = new Vector<Integer> ();
		}
		else {
			vecCommandListenerId.clear();
		}
		
		if ( vecDNetComponentsId == null ) {
			vecDNetComponentsId = new Vector<Integer> ();
		}
		else {
			vecDNetComponentsId.clear();
		}
		
		
	}

	
	/*
	 * 
	 */
	public void startElement(String uri, 
			String localName, 
			String qName, 
			Attributes attributes) {
		
		if ( startElement_DComponent( uri, localName, qName, attributes ) ) {
			//tag was already handled!
			return;
		} 
		
		if ( bXML_Section_DNetEventComponent ) {
			
		
			/**
			 * Section <SubComponents>
			 */
			if ( qName.equals( sTag_XML_SubComponents ) ) {
				bXML_Section_SubComponents = true;
			} else if ( bXML_Section_SubComponents ) {
				
				handleTag_SubComponents(qName,attributes);
			}
			
			/**
			 * Section <SubNetEventListener>
			 */
			if ( qName.equals( sTag_XML_SubNetEventListener ) ) {
				bXML_Section_SubNetEventListener = true;
			} else if ( bXML_Section_SubNetEventListener ) {
				
				handleTag_SubNetEventListener(qName,attributes);
			}
			
			/**
			 * Section <SubCommandListener>
			 */
			if ( qName.equals( sTag_XML_SubCommandListener ) ) {
				bXML_Section_SubCommandListener = true;
			} else if ( bXML_Section_SubCommandListener ) {
				
				handleTag_SubCommandListener(qName,attributes);
			}
		
		} // end else if ( bXML_Section_DNetEventComponent ) 
		
	} // end startElement(String,Attributes) 
	
	

	private void handleTag_SubComponents( final String qName, final Attributes attributes) {
						
		//String bufferXMLLabel = attributes.getValue("label");
		
		if ( sTag_XML_SubComponents_item.equalsIgnoreCase( qName )) {
			try {
				int iBufferId = Integer.valueOf( attributes.getValue(sTag_XML_SubComponents_item_attr) );						
				
				/**
				 * Check for redundant Id's...
				 */
				if ( vecDNetComponentsId.contains( iBufferId ) ) {
					appandErrorMsg("ERROR <" +
							sTag_XML_SubComponents_item + "" +
							" " + sTag_XML_SubComponents_item_attr +
							"= " + iBufferId + " > ic duplicated id!");						
					return;
				}
				
				vecDNetComponentsId.add( new Integer(iBufferId) );
				return;
				
			} 
			catch (Exception e) {
				appandErrorMsg("ERROR <" + sTag_XML_SubComponents_item +
						"  " + sTag_XML_SubComponents_item_attr + 
						"=...  > does not contain an interger!");						
				return;
			} // end try-catch
		}		
		
	} // end handleTag_DNetEventComponent(String,Attributes)
	
	private void handleTag_SubNetEventListener( final String qName, final Attributes attributes) {
		
		if ( qName.equals( sTag_XML_SubNetEventListener) ) {
			if ( attributes.getLength() > 0 ) {
				try {
					vecNetEventListenerId.add( new Integer(
						parseStringToInt( attributes.getValue( sTag_XML_SubNetEventListener_attr ))));
				}
				catch (NullPointerException ne) {
					appandErrorMsg("error while parsing <"+
							sTag_XML_SubNetEventListener + " " +
							sTag_XML_SubNetEventListener_attr +
							"=\"..\" >");
				}
			}
			else {
				appandErrorMsg("ERROR in Syntax: missing argument <" +
						sTag_XML_SubNetEventListener + " " +
						sTag_XML_SubNetEventListener_attr +
						"=\"..\" >");											
				return;
			}
		}				
		
		
	} // end handleTag_SubNetEventListener(String,Attributes)
	
	private void handleTag_SubCommandListener( final String qName, final Attributes attributes) {
		
		if ( qName.equals( sTag_XML_SubCommandListener) ) {
			if ( attributes.getLength() > 0 ) {
				try {
					vecCommandListenerId.add( new Integer(
						parseStringToInt( attributes.getValue( sTag_XML_SubCommandListener_attr ))));
					
					return;
				}
				catch (NullPointerException ne) {
					appandErrorMsg("error while parsing <"+
							sTag_XML_SubCommandListener + " " +
							sTag_XML_SubCommandListener_attr +
							"=\"..\" >");
					return;
				}
			}
			else {
				appandErrorMsg("ERROR in Syntax: missing argument <" +
						sTag_XML_SubCommandListener + " " +
						sTag_XML_SubCommandListener_attr +
						"=\"..\" >");											
				return;
			}
		}	
		
	} // end handleTag_SubCommandListener(String,Attributes)
		
	public void endElement(String uri, String localName, String qName ) {
				
		if ( bXML_Section_DNetEventComponent ) {
			
			if ( endElement_DComponent( uri, localName, qName ) ) {
				// tag was already handled!
				return;
			}			
		
			if ( qName.equals( sTag_XML_SubComponents ) ) {
				bXML_Section_SubComponents = false;
				return;
			} 
			else if ( localName.equals( this.sTag_XML_SubCommandListener )) {
				bXML_Section_SubCommandListener = false;
				return;
			}
			else if ( localName.equals( this.sTag_XML_SubNetEventListener )) {
				bXML_Section_SubNetEventListener  = false;
				return;
			}
		}
		
	} // end endElement(String,Attributes) 

	
	public Iterator<Integer> getXML_Iterator_CommandListenerId() {
		return vecCommandListenerId.iterator();
	}
	
	public Iterator<Integer> getXML_Iterator_NetEventListenerId() {
		return vecNetEventListenerId.iterator();
	}
	
	public Iterator<Integer> getXML_Iterator_NetEventCildrenComponentId() {
		return this.vecDNetComponentsId.iterator();
	}
	
	public String toString() {
		String resultString = " ( #" + this.iXML_dNetEvent_Id + "\n";
	
		resultString += "  [ChildComponentId ";
		Iterator<Integer> iter = getXML_Iterator_NetEventCildrenComponentId();
		while ( iter.hasNext() ) {
			resultString += " " + iter.toString();
		}
		
		resultString += "]\n  [NetEventListenerId ";
		iter = this.getXML_Iterator_NetEventListenerId();
		while ( iter.hasNext() ) {
			resultString += " " + iter.toString();
		}
		
		resultString += "]\n  [CommandListenerId ";
		iter = this.getXML_Iterator_CommandListenerId();
		while ( iter.hasNext() ) {
			resultString += " " + iter.toString();
		}
		resultString += "]\n )\n";
		
		return resultString;
	}
}
