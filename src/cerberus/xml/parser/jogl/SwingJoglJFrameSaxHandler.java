/**
 * 
 */
package cerberus.xml.parser.jogl;

import java.awt.Point;
import java.awt.Dimension;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
//import javax.xml.parsers.SAXParserFactory;
//import org.xml.sax.helpers.DefaultHandler;


import cerberus.manager.FrameManagerInterface;
import cerberus.manager.GeneralManager;
import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.SwingJoglJFrame;
import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;
import cerberus.xml.parser.ACerberusDefaultSaxHandler;


/**
 * @author java
 *
 */
public class SwingJoglJFrameSaxHandler extends ACerberusDefaultSaxHandler  {

	
	private boolean bFrameStateActive = false;
	private boolean bFrameIsCreated = false;
	
	private final FrameManagerInterface viewManager;
	
	private int iCurrentFrameId = -1;
	
	protected int iDefaultFrameWidht = 100;
	protected int iDefaultFrameHeight = 100;
	protected int iDefaultFrameX = 0;
	protected int iDefaultFrameY = 0;
	protected int iDefaultFrameId = -1;
	
	public String sApplicationTag = "Application";
	
	public String sApplicationKey = "IP";	
	public String sApplicationValue = "localhost";
	
	public String sFrameStateTag = "FrameState";
	
	public String sFrameTag = "JFrame";
	
	public String sFrameKey_x = "x";
	public String sFrameKey_y = "y";
	public String sFrameKey_width = "width";
	public String sFrameKey_height = "height";
	public String sFrameKey_title = "title";
	public String sFrameKey_name = "name";
	public String sFrameKey_type = "type";
	public String sFrameKey_objectId = "id";
	
	public String sFrameKey_visible = "visible";
	
	public String sInternalFrameTag = "JInternalFrame";
	
	
	
	public boolean bApplicationActive = false;
	
	
	/**
	 * 
	 */
	public SwingJoglJFrameSaxHandler( final GeneralManager refGeneralManager,
			final FrameManagerInterface viewManager ) {
		super( refGeneralManager );
		
		assert viewManager != null : "viewManager can not be null";
		
		this.viewManager = viewManager;

	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.jogl.CerberusSaxHandler#createXMLcloseingTag(java.lang.Object, java.lang.String)
	 */
	public String createXMLcloseingTag( final Object frame, final String sIndent ) {
		
		if ( frame.getClass().equals( SwingJoglJFrame.class )) {
			return sIndent + "</" + sFrameTag + ">\n";
		}
		else if ( frame.getClass().equals( SwingJoglJInternalFrame.class )) {
			return sIndent + "</" + sInternalFrameTag + ">\n";
		}
		
		return "";
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.jogl.CerberusSaxHandler#createXML(java.lang.Object, java.lang.String)
	 */
	public String createXML( final Object frame, final String sIndent ) {
		String result = sIndent;		
		String sTitle;
		String sName;
		String sClosingTag;
		String sTypeName;
		Dimension dim;
		Point location;
		boolean bIsVisible;
		
		if ( frame.getClass().equals( SwingJoglJFrame.class )) {
			SwingJoglJFrame jframe = (SwingJoglJFrame) frame;
			
			result += "<" + sFrameTag;
			
			iCurrentFrameId = jframe.getId();
			dim = jframe.getSize();
			location = jframe.getLocation();			
			bIsVisible = jframe.isVisible();
			sTypeName = jframe.getFrameType().getTypeNameForXML();
			sTitle = jframe.getTitle();
			sName = jframe.getName();
			sClosingTag = ">\n";
			
		} else if ( frame.getClass().equals( SwingJoglJInternalFrame.class )) {
			SwingJoglJInternalFrame jiframe = (SwingJoglJInternalFrame) frame;
			
			result += "<" + sInternalFrameTag;
			
			iCurrentFrameId = jiframe.getId();
			dim = jiframe.getSize();
			location = jiframe.getLocation();			
			bIsVisible = jiframe.isVisible();
			sTitle = jiframe.getTitle();
			sTypeName = jiframe.getFrameType().getTypeNameForXML();
			sName = jiframe.getName();
			sClosingTag = "> </" + sInternalFrameTag + ">\n";
			
		} else {
			throw new RuntimeException("Can not create XML string from class [" +
					frame.getClass().getName() + "] ;only support SwingJoglJFrame and SwingJoglJInternalFrame");
		}
		
		result += " " + sFrameKey_title + sArgumentBegin + sTitle;
		result += sArgumentEnd + sFrameKey_objectId + sArgumentBegin + Integer.toString(iCurrentFrameId);
		result += sArgumentEnd + sFrameKey_name + sArgumentBegin + sName;
		result += sArgumentEnd + sFrameKey_type + sArgumentBegin + sTypeName;
		result += sArgumentEnd + sFrameKey_x + sArgumentBegin + Integer.toString(location.x);
		result += sArgumentEnd + sFrameKey_y + sArgumentBegin + Integer.toString(location.y);
		result += sArgumentEnd + sFrameKey_width + sArgumentBegin + Integer.toString(dim.width);
		result += sArgumentEnd + sFrameKey_height + sArgumentBegin + Integer.toString(dim.height);
		result += sArgumentEnd + sFrameKey_visible + sArgumentBegin + Boolean.toString(bIsVisible);				
		result += sArgumentEnd + sClosingTag;
		
		return result;
	}

	public void setApplicationValue( String sValue ) {
		sApplicationValue = sValue;
	}
	


	
	/**
	 * 
	 * Read values of class: iCurrentFrameId
	 * @param attrs
	 * @param bIsExternalFrame
	 */
	private void readFrameData( final Attributes attrs, boolean bIsExternalFrame ) {
		
		/* create new Frame */
		int ix = assignIntValueIfValid( attrs, sFrameKey_x, iDefaultFrameX );
		int iy = assignIntValueIfValid( attrs, sFrameKey_y, iDefaultFrameY  );
								
		int iWidth= assignIntValueIfValid( attrs, sFrameKey_width, iDefaultFrameWidht );
		int iHeight= assignIntValueIfValid( attrs, sFrameKey_height, iDefaultFrameHeight );
			
		int iUniqueObjectId = assignIntValueIfValid( attrs, sFrameKey_objectId, iDefaultFrameId );
		
		boolean bVisible = assignBooleanValueIfValid( attrs, sFrameKey_visible, true );											
		
		String sTitle= attrs.getValue( sFrameKey_title );
		String sName= attrs.getValue( sFrameKey_name );
		String sTypeName = attrs.getValue( sFrameKey_type );	
		
		FrameBaseType frameType = FrameBaseType.valueOf( sTypeName );
		
		if ( bIsExternalFrame ) {
			
			SwingJoglJFrame frame = (SwingJoglJFrame) viewManager.addWindow( 
					frameType,
					iUniqueObjectId,
					-1 );
			
			//SwingJoglJFrame frame = viewManager.createNewJFrame( sTitle );
			
			/**
			 * ReadBack FrameId..
			 */
			iCurrentFrameId = frame.getId();
			
			frame.setLocation(ix,iy);
			frame.setSize( iWidth, iHeight );
			frame.setName( sName );
			frame.setTitle( sTitle );
			frame.setVisible( bVisible );		
			
			iCurrentFrameId = iUniqueObjectId;
			
			return;
			
		} else {
			
			SwingJoglJInternalFrame intFrame = 
				(SwingJoglJInternalFrame) viewManager.addWindow( 
						frameType, 
						iUniqueObjectId,
						iCurrentFrameId );
			
			intFrame.setLocation(ix,iy);
			intFrame.setSize( iWidth, iHeight );
			intFrame.setVisible( bVisible );
			intFrame.setName( sName );
			intFrame.setTitle( sTitle );
		}
	}
	
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		
		String eName = ("".equals(localName)) ? qName : localName;
		
		if (null != eName) {
			
			if ( ! bApplicationActive ) {
				if (eName.equals(sApplicationTag)) {
						/* <sApplicationTag> */
						int iAttrLength = attrs.getLength();
						for ( int i=0; i<iAttrLength; i++) {
							if (attrs.getLocalName(i).equalsIgnoreCase( sApplicationKey )) {
								if ( attrs.getValue(i).equals(sApplicationValue)) {
									bApplicationActive = true;
									return;
								} //end: if ( attrs.getValue(i).equals(sApplicationValue)) {
							} //end: if (attrs.getLocalName(i).equalsIgnoreCase( sApplicationKey )) {
						} // end: for ( int i=0; i<iAttrLength; i++) {
						bApplicationActive = true;
						
				} //end: if (eName.equals(sApplicationTag)) {
			}
			else //end: if ( ! bApplicationActive ) {
			{
				String sName;
				String sTitle;
				String sTypeName;
				
				if (eName.equals(sFrameStateTag)) {
					/* <sFrameStateTag> */
					if ( bFrameStateActive ) {
						throw new SAXException ( "<" + sFrameStateTag + "> already opened!");
					} else {
						bFrameStateActive = true;
						return;
					}
				} //end: if (eName.equals(sFrameStateTag)) {
				else if (eName.equals(sFrameTag)) {
					
					
					/**
					 *  <sFrameTag> 
					 */
					if ( bFrameStateActive ) {
						
						bFrameIsCreated = true;
						
						readFrameData( attrs, true );
						
//						/* create new Frame */
//						int ix = assignIntValueIfValid( attrs, sFrameKey_x, iDefaultFrameX );
//						int iy = assignIntValueIfValid( attrs, sFrameKey_y, iDefaultFrameY  );
//												
//						int iWidth= assignIntValueIfValid( attrs, sFrameKey_width, iDefaultFrameWidht );
//						int iHeight= assignIntValueIfValid( attrs, sFrameKey_height, iDefaultFrameHeight );
//							
//						int iUniqueObjectId = assignIntValueIfValid( attrs, sFrameKey_height, iDefaultFrameId );
//						
//						boolean bVisible = assignBooleanValueIfValid( attrs, sFrameKey_visible, true );											
//						
//						sTitle= attrs.getValue( sFrameKey_title );
//						sName= attrs.getValue( sFrameKey_name );
//						sTypeName = attrs.getValue( sFrameKey_type );		
//						
//						
//						
//						SwingJoglJFrame frame = (SwingJoglJFrame) viewManager.addWindow( 
//								FrameBaseType.valueOf( sTypeName ),
//								false,
//								iUniqueObjectId );
//						
//						//SwingJoglJFrame frame = viewManager.createNewJFrame( sTitle );
//						
//						/**
//						 * ReadBack FrameId..
//						 */
//						iCurrentFrameId = frame.getId();
//						
//						frame.setLocation(ix,iy);
//						frame.setSize( iWidth, iHeight );
//						frame.setName( sName );
//						frame.setTitle( sTitle );
//						frame.setVisible( bVisible );						
//						
//						bFrameIsCreated = true;
						
					} else {
						throw new SAXException ( "<"+ sFrameTag + "> opens without <" + 
								sFrameStateTag + "> being opened!");
					}
				}
				else if (eName.equals(sInternalFrameTag)) {
					/* <sInternalFrameTag> */
					if ( bFrameStateActive ) {
						if (bFrameIsCreated) {
							
							readFrameData( attrs, false );
							
//							int ix=iDefaultFrameX;
//							int iy=iDefaultFrameY;
//							int iWidth=iDefaultFrameWidht;
//							int iHeight = iDefaultFrameHeight;
//							boolean bVisible = assignBooleanValueIfValid( attrs, sFrameKey_visible, true );
//							
//							/* create new Frame */
//							ix = Integer.valueOf( attrs.getValue( sFrameKey_x ) );
//							iy = Integer.valueOf( attrs.getValue( sFrameKey_y ) );
//							iWidth= Integer.valueOf( attrs.getValue( sFrameKey_width ) );
//							iHeight= Integer.valueOf( attrs.getValue( sFrameKey_height ) );
//							
//							bVisible = Boolean.valueOf( attrs.getValue( sFrameKey_visible ) );
//							
//							sTypeName = attrs.getValue( sFrameKey_type );
//							sTitle= attrs.getValue( sFrameKey_title );
//							sName= attrs.getValue( sFrameKey_name );
//							
////							SwingJoglJInternalFrame intFrame = 
////								viewManager.createNewJInternalFrame( sTitle, iCurrentFrameId );
//							
//							SwingJoglJInternalFrame intFrame = 
//								(SwingJoglJInternalFrame) viewManager.addWindow( 
//										FrameBaseType.valueOf( sTypeName ), 
//										true, 
//										iCurrentFrameId );
//														
//							intFrame.setLocation(ix,iy);
//							intFrame.setSize( iWidth, iHeight );
//							intFrame.setVisible( bVisible );
//							intFrame.setName( sName );
//							intFrame.setTitle( sTitle );
							
							
						} else {
							throw new SAXException ( "<"+ sInternalFrameTag + "> opens without <" + 
									sFrameTag + "> being opened!");
						}
						
					} else {
						throw new SAXException ( "<"+ sFrameTag + "> opens without <" + 
								sFrameStateTag + "> being opened!");
					}
				}
				
			} //end: if ( ! bApplicationActive ) {
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if ( bApplicationActive ) {
			
			String eName = ("".equals(localName)) ? qName : localName;
		
			if (null != eName) {
				if (eName.equalsIgnoreCase(sApplicationTag)) {
					/* </sApplicationTag> */
					if ( bFrameStateActive ) {
						throw new SAXException ( "<" + sFrameStateTag + "> still opened while <" +
								sApplicationTag + "> is closed.");
					}
					bApplicationActive = false;
					return;
				} 
				else if (eName.equals(sFrameStateTag)) {	
					
					/* </sFrameStateTag> */
					if ( bFrameStateActive ) {
						bFrameStateActive = false;
						return;
					} else {
						throw new SAXException ( "<" + sFrameStateTag + "> still opened while <" +
								sApplicationTag + "> is closed.");
					}	
					
				} 
				else if (eName.equals(sFrameTag)) {	
					
					/* </sFrameTag> */
					if ( bFrameStateActive ) {
						if ( bFrameIsCreated ) {
							bFrameIsCreated = false;
						} else {
							throw new SAXException ( "<" + sFrameTag + "> is already opened.");
						}
						
					} else {
						throw new SAXException ( "<" + sFrameTag + "> opens without " + 
								sFrameStateTag + " being opened.");
					}	
					
				}
				// end:else if (eName.equals(...)) {	
			} //end: if (null != eName) {
			
		} //end: if ( bApplicationActive ) {
	}

//	public void characters(char[] buf, int offset, int len) throws SAXException {
//		if ( bApplicationActive ) {
//		
//		}
//	}

}
