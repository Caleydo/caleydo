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

import cerberus.manager.type.BaseManagerType;

import cerberus.data.xml.MementoNetEventXML;
import cerberus.data.collection.view.ViewCanvas;

import cerberus.xml.parser.DParseSaxHandler;

//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;
//import org.xml.sax.helpers.LocatorImpl;
//
//import cerberus.util.exception.cerberusSaxParserException;

/**
 * @author Michael Kalkusch
 *
 */
public class DSwingHistogramCanvasHandler 
extends DParseComponentSaxHandler 
implements DParseSaxHandler
{

	private boolean bXML_Section_CanvasLink2Model = false;
	private boolean bXML_Section_SubCanavas = false;
	private boolean bXML_Section_CanvasLink2View = false;
	
	private final String sTag_XML_CanvasLink2View = "CanvasLink2View";
	private final String sTag_XML_CanvasLink2View_attr = "NetListener";
	
	private final String sTag_XML_SubCanvas = "SubCanvas";
	private final String sTag_XML_SubCanvas_attr = "SubCanvasItem";
	
	private final String sTag_XML_CanvasLink2Model = "CanvasLink2Model";
	private final String sTag_XML_CanvasLink2Model_item = "CanvasLink2Target";
	private final String sTag_XML_CanvasLink2Model_item_attr = "dNetEvent_Id";


	/**
	 * List of all NetEventListener Id's
	 */
	private Vector<Integer> vecCanvasLink2ViewId;
	
	/**
	 * List of all CommandListener Id's
	 */
	private Vector<Integer> vecSubCanvasId;

	
	/**
	 * List of all DNetComponents Id's
	 */
	private Vector<Integer> vecCanvasLink2ModelId;
	
	/**
	 * 
	 */
	public DSwingHistogramCanvasHandler() {
		super();
		sTag_XML_DEvent_type = "DHistogramCanvas";
	}
	
	/**
	 * 
	 */
	public DSwingHistogramCanvasHandler(final boolean bEnableHaltOnParsingError) {
		super(bEnableHaltOnParsingError);
		sTag_XML_DEvent_type = "DHistogramCanvas";
		
	}
	
	public DSwingHistogramCanvasHandler(final MementoNetEventXML setRefParent) {
		super();
		
		this.refParentMementoCaller = setRefParent;
		sTag_XML_DEvent_type = "DHistogramCanvas";
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
		
		bXML_Section_CanvasLink2Model = false;
		bXML_Section_SubCanavas = false;
		bXML_Section_CanvasLink2View = false;
		
		if ( vecCanvasLink2ViewId == null) {
			vecCanvasLink2ViewId = new Vector<Integer> ();
		}
		else {
			vecCanvasLink2ViewId.clear();
		}
		
		if ( vecSubCanvasId == null ) {
			vecSubCanvasId = new Vector<Integer> ();
		}
		else {
			vecSubCanvasId.clear();
		}
		
		if ( vecCanvasLink2ModelId == null ) {
			vecCanvasLink2ModelId = new Vector<Integer> ();
		}
		else {
			vecCanvasLink2ModelId.clear();
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
			if ( qName.equals( sTag_XML_CanvasLink2Model ) ) {
				bXML_Section_CanvasLink2Model = true;
			} else if ( bXML_Section_CanvasLink2Model ) {
				
				handleTag_SubComponents(qName,attributes);
			}
			
			/**
			 * Section <SubNetEventListener>
			 */
			if ( qName.equals( sTag_XML_CanvasLink2View ) ) {
				bXML_Section_CanvasLink2View = true;
			} else if ( bXML_Section_CanvasLink2View ) {
				
				handleTag_SubNetEventListener(qName,attributes);
			}
			
			/**
			 * Section <SubCommandListener>
			 */
			if ( qName.equals( sTag_XML_SubCanvas ) ) {
				bXML_Section_SubCanavas = true;
			} else if ( bXML_Section_SubCanavas ) {
				
				handleTag_SubCommandListener(qName,attributes);
			}
		
		} // end else if ( bXML_Section_DNetEventComponent ) 
		
	} // end startElement(String,Attributes) 
	
	

	private void handleTag_SubComponents( final String qName, final Attributes attributes) {
						
		//String bufferXMLLabel = attributes.getValue("label");
		
		if ( sTag_XML_CanvasLink2Model_item.equalsIgnoreCase( qName )) {
			try {
				int iBufferId = Integer.valueOf( attributes.getValue(sTag_XML_CanvasLink2Model_item_attr) );						
				
				/**
				 * Check for redundant Id's...
				 */
				if ( vecCanvasLink2ModelId.contains( iBufferId ) ) {
					appandErrorMsg("ERROR <" +
							sTag_XML_CanvasLink2Model_item + "" +
							" " + sTag_XML_CanvasLink2Model_item_attr +
							"= " + iBufferId + " > ic duplicated id!");						
					return;
				}
				
				vecCanvasLink2ModelId.add( new Integer(iBufferId) );
				return;
				
			} 
			catch (Exception e) {
				appandErrorMsg("ERROR <" + sTag_XML_CanvasLink2Model_item +
						"  " + sTag_XML_CanvasLink2Model_item_attr + 
						"=...  > does not contain an interger!");						
				return;
			} // end try-catch
		}		
		
	} // end handleTag_DNetEventComponent(String,Attributes)
	
	private void handleTag_SubNetEventListener( final String qName, final Attributes attributes) {
		
		if ( qName.equals( sTag_XML_CanvasLink2View) ) {
			if ( attributes.getLength() > 0 ) {
				try {
					vecCanvasLink2ViewId.add( new Integer(
						parseStringToInt( attributes.getValue( sTag_XML_CanvasLink2View_attr ))));
				}
				catch (NullPointerException ne) {
					appandErrorMsg("error while parsing <"+
							sTag_XML_CanvasLink2View + " " +
							sTag_XML_CanvasLink2View_attr +
							"=\"..\" >");
				}
			}
			else {
				appandErrorMsg("ERROR in Syntax: missing argument <" +
						sTag_XML_CanvasLink2View + " " +
						sTag_XML_CanvasLink2View_attr +
						"=\"..\" >");											
				return;
			}
		}				
		
		
	} // end handleTag_SubNetEventListener(String,Attributes)
	
	private void handleTag_SubCommandListener( final String qName, final Attributes attributes) {
		
		if ( qName.equals( sTag_XML_SubCanvas) ) {
			if ( attributes.getLength() > 0 ) {
				try {
					vecSubCanvasId.add( new Integer(
						parseStringToInt( attributes.getValue( sTag_XML_SubCanvas_attr ))));
					
					return;
				}
				catch (NullPointerException ne) {
					appandErrorMsg("error while parsing <"+
							sTag_XML_SubCanvas + " " +
							sTag_XML_SubCanvas_attr +
							"=\"..\" >");
					return;
				}
			}
			else {
				appandErrorMsg("ERROR in Syntax: missing argument <" +
						sTag_XML_SubCanvas + " " +
						sTag_XML_SubCanvas_attr +
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
		
			if ( qName.equals( sTag_XML_CanvasLink2Model ) ) {
				bXML_Section_CanvasLink2Model = false;
				return;
			} 
			else if ( localName.equals( this.sTag_XML_SubCanvas )) {
				bXML_Section_SubCanavas = false;
				return;
			}
			else if ( localName.equals( this.sTag_XML_CanvasLink2View )) {
				bXML_Section_CanvasLink2View  = false;
				return;
			}
		}
		
	} // end endElement(String,Attributes) 

	
	public Iterator<Integer> getXML_Iterator_CommandListenerId() {
		return vecSubCanvasId.iterator();
	}
	
	public Iterator<Integer> getXML_Iterator_NetEventListenerId() {
		return vecCanvasLink2ViewId.iterator();
	}
	
	public Iterator<Integer> getXML_Iterator_NetEventCildrenComponentId() {
		return this.vecCanvasLink2ModelId.iterator();
	}
	
	public int getXML_link2Target_SetId() {
		return vecCanvasLink2ModelId.get(0).intValue();
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
