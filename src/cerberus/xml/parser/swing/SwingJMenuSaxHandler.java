/**
 * 
 */
package cerberus.xml.parser.swing;


//import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import cerberus.manager.FrameManagerInterface;
import cerberus.manager.GeneralManager;
import cerberus.manager.MenuManager;
import cerberus.view.manager.jogl.swing.SwingJoglJFrame;
import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;

import cerberus.xml.parser.CerberusDefaultSaxHandler;


/**
 * Create Menus in Frames from XML file.
 * 
 * @author java
 *
 */
public class SwingJMenuSaxHandler extends CerberusDefaultSaxHandler  {

	
	private boolean bJFrameMenuList_isActive = false;
	
	private final FrameManagerInterface viewManager;
	
	private boolean bData_EnableMenu = true;
	
	protected boolean bApplicationActive = false;
	
	private int iData_MenuId = -1;
	private int iData_MenuParentId = -1;
	private int iData_TargetFrameId = -1;
	private int iData_CommandId = -1;
	
	private String sData_MenuType = "none";
	private String sData_MenuTitle = "none";
	private String sData_MenuTooltip = "";
	private String sData_MenuMemento = "-";
	
	protected int iDefaultFrameWidht = 100;
	protected int iDefaultFrameHeight = 100;
	protected int iDefaultFrameX = 0;
	protected int iDefaultFrameY = 0;
	protected int iDefaultFrameId = -1;
	
	/* XML Attributes */
	protected static final String sMenuKey_targetFrameId = "frameId";
	protected static final String sMenuKey_linkToCommand = "cmdId";
	protected static final String sMenuKey_parentMenuId = "parentMenuId";
	protected static final String sMenuKey_enabled = "enabled";
	protected static final String sMenuKey_title = "title";
	protected static final String sMenuKey_tooltip = "tooltip";
	protected static final String sMenuKey_memento = "memento";
	protected static final String sMenuKey_type = "type";
	protected static final String sMenuKey_objectId = "id";
		

	
	/* XML Tags */
	public static final String sTag_Application = "Application";	
	public static final String sTag_MenuState = "JFrameMenuList";	
	public static final String sTag_Menu = "FrameMenu";
	
	
	
	/**
	 * 
	 */
	public SwingJMenuSaxHandler( final GeneralManager refGeneralManager,
			final FrameManagerInterface viewManager ) {
		super( refGeneralManager );
		
		assert viewManager != null : "viewManager can not be null";
		
		this.viewManager = viewManager;

	}
	
	public String createXMLcloseingTag( final Object frame, final String sIndent ) {
		
		if ( frame.getClass().equals( SwingJoglJFrame.class )) {
			return sIndent + "</" + sTag_Menu + ">\n";
		}
				
		return "";
	}
	
	public String createXML( final Object frame, final String sIndent ) {
		String result = sIndent;		

		
		if ( frame.getClass().equals( SwingJoglJFrame.class )) {
			SwingJoglJFrame jframe = (SwingJoglJFrame) frame;
			
//			result += "<" + sMenuTag;
//			
//			iCurrentFrameId = jframe.getId();
//			dim = jframe.getSize();
//			location = jframe.getLocation();			
//			bIsVisible = jframe.isVisible();
//			sTypeName = jframe.getFrameType().getTypeNameForXML();
//			sTitle = jframe.getTitle();
//			sName = jframe.getName();
//			sClosingTag = ">\n";
			
		} else if ( frame.getClass().equals( SwingJoglJInternalFrame.class )) {
			SwingJoglJInternalFrame jiframe = (SwingJoglJInternalFrame) frame;
			
//			result += "<" + sInternalFrameTag;
//			
//			iCurrentFrameId = jiframe.getId();
//			dim = jiframe.getSize();
//			location = jiframe.getLocation();			
//			bIsVisible = jiframe.isVisible();
//			sTitle = jiframe.getTitle();
//			sTypeName = jiframe.getFrameType().getTypeNameForXML();
//			sName = jiframe.getName();
//			sClosingTag = "> </" + sInternalFrameTag + ">\n";
			
		} else {
			throw new RuntimeException("Can not create XML string from class [" +
					frame.getClass().getName() + "] ;only support SwingJoglJFrame and SwingJoglJInternalFrame");
		}
		
		result +=          " " + sMenuKey_objectId + sArgumentBegin + Integer.toString(iData_MenuId);
		result += sArgumentEnd + sMenuKey_targetFrameId + sArgumentBegin + Integer.toString(this.iData_TargetFrameId);
		result += sArgumentEnd + sMenuKey_linkToCommand + sArgumentBegin + Integer.toString(this.iData_CommandId);
		result += sArgumentEnd + sMenuKey_parentMenuId + sArgumentBegin + Integer.toString(this.iData_MenuParentId);;
		result += sArgumentEnd + sMenuKey_type + sArgumentBegin + sData_MenuType;
		
		result += sArgumentEnd + sMenuKey_enabled + sArgumentBegin + Boolean.toString(bData_EnableMenu);	
		result += sArgumentEnd + sMenuKey_memento + sArgumentBegin + sData_MenuMemento;
		
		result += sArgumentEnd + sMenuKey_title + sArgumentBegin + sData_MenuTitle;
		result += sArgumentEnd + sMenuKey_tooltip + sArgumentBegin + sData_MenuTooltip;					
				
		
		return result;
	}

//	public void setApplicationValue( String sValue ) {
//		sApplicationValue = sValue;
//	}
	
	
	

	

	
	/**
	 * 
	 * Read values of class: iCurrentFrameId
	 * @param attrs
	 * @param bIsExternalFrame
	 */
	private void readFrameData( final Attributes attrs, boolean bIsExternalFrame ) {
		
		/* create new Frame */
		iData_TargetFrameId = assignIntValueIfValid( attrs, sMenuKey_targetFrameId, -1 );
		iData_CommandId = assignIntValueIfValid( attrs, sMenuKey_linkToCommand, -1  );
								
		iData_MenuParentId = assignIntValueIfValid( attrs, sMenuKey_parentMenuId, -1 );
		iData_MenuId = assignIntValueIfValid( attrs, sMenuKey_objectId, -1 );
		
		bData_EnableMenu = assignBooleanValueIfValid( attrs, sMenuKey_enabled, true );											
		
		sData_MenuTooltip = attrs.getValue( sMenuKey_tooltip );
		sData_MenuMemento = attrs.getValue( sMenuKey_memento );
			
		sData_MenuTitle = attrs.getValue( sMenuKey_title );
		sData_MenuType = attrs.getValue( sMenuKey_type );	
		
		
//		if ( bIsExternalFrame ) {
//			
//			SwingJoglJFrame frame = (SwingJoglJFrame) viewManager.addWindow( 
//					frameType,
//					iUniqueObjectId,
//					-1 );
//			
//			//SwingJoglJFrame frame = viewManager.createNewJFrame( sTitle );
//			
//			/**
//			 * ReadBack FrameId..
//			 */
//			iCurrentFrameId = frame.getId();
//			
//			frame.setLocation(ix,iy);
//			frame.setSize( iWidth, iHeight );
//			frame.setName( sName );
//			frame.setTitle( sTitle );
//			frame.setVisible( bMenuEnabled  );		
//			
//			iCurrentFrameId = iUniqueObjectId;
//			
//			return;
//			
//		} else {
//			
//			SwingJoglJInternalFrame intFrame = 
//				(SwingJoglJInternalFrame) viewManager.addWindow( 
//						frameType, 
//						iUniqueObjectId,
//						iCurrentFrameId );
//			
//			intFrame.setLocation(ix,iy);
//			intFrame.setSize( iWidth, iHeight );
//			intFrame.setVisible( bVisible );
//			intFrame.setName( sName );
//			intFrame.setTitle( sTitle );
//		}
	}
	
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		
		String eName = ("".equals(localName)) ? qName : localName;
		
		if (null != eName) {
			
			if ( ! bApplicationActive ) {
				if (eName.equalsIgnoreCase(sTag_Application)) {
						/* <sApplicationTag> */
						bApplicationActive = true;
						
				} //end: if (eName.equals(sApplicationTag)) {
			}
			else //end: if ( ! bApplicationActive ) {
			{
				
				if (eName.equals(sTag_MenuState)) {
					/* <sFrameStateTag> */
					if ( bJFrameMenuList_isActive ) {
						throw new SAXException ( "<" + sTag_MenuState + "> already opened!");
					} else {
						bJFrameMenuList_isActive = true;
						return;
					}
				} //end: if (eName.equals(sFrameStateTag)) {
				else if (eName.equals(sTag_Menu)) {
					
					
					/**
					 *  <sFrameTag> 
					 */
					if ( bJFrameMenuList_isActive ) {
						
						
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
						throw new SAXException ( "<"+ sTag_Menu + "> opens without <" + 
								sTag_MenuState + "> being opened!");
					}
				}
//				else if (eName.equals(sInternalFrameTag)) {
//					/* <sInternalFrameTag> */
//					if ( bFrameStateActive ) {
//						if (bFrameIsCreated) {
//							
//							readFrameData( attrs, false );
//							
////							int ix=iDefaultFrameX;
////							int iy=iDefaultFrameY;
////							int iWidth=iDefaultFrameWidht;
////							int iHeight = iDefaultFrameHeight;
////							boolean bVisible = assignBooleanValueIfValid( attrs, sFrameKey_visible, true );
////							
////							/* create new Frame */
////							ix = Integer.valueOf( attrs.getValue( sFrameKey_x ) );
////							iy = Integer.valueOf( attrs.getValue( sFrameKey_y ) );
////							iWidth= Integer.valueOf( attrs.getValue( sFrameKey_width ) );
////							iHeight= Integer.valueOf( attrs.getValue( sFrameKey_height ) );
////							
////							bVisible = Boolean.valueOf( attrs.getValue( sFrameKey_visible ) );
////							
////							sTypeName = attrs.getValue( sFrameKey_type );
////							sTitle= attrs.getValue( sFrameKey_title );
////							sName= attrs.getValue( sFrameKey_name );
////							
//////							SwingJoglJInternalFrame intFrame = 
//////								viewManager.createNewJInternalFrame( sTitle, iCurrentFrameId );
////							
////							SwingJoglJInternalFrame intFrame = 
////								(SwingJoglJInternalFrame) viewManager.addWindow( 
////										FrameBaseType.valueOf( sTypeName ), 
////										true, 
////										iCurrentFrameId );
////														
////							intFrame.setLocation(ix,iy);
////							intFrame.setSize( iWidth, iHeight );
////							intFrame.setVisible( bVisible );
////							intFrame.setName( sName );
////							intFrame.setTitle( sTitle );
//							
//							
//						} else {
//							throw new SAXException ( "<"+ sInternalFrameTag + "> opens without <" + 
//									sFrameTag + "> being opened!");
//						}
//						
//				
//				
//					} else {
//						throw new SAXException ( "<"+ sFrameTag + "> opens without <" + 
//								sFrameStateTag + "> being opened!");
//					}
//				}
				
			} //end: if ( ! bApplicationActive ) {
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if ( bApplicationActive ) {
			
			String eName = ("".equals(localName)) ? qName : localName;
		
			if (null != eName) {
				if (eName.equalsIgnoreCase(sTag_Application)) {
					/* </sApplicationTag> */
					if ( bJFrameMenuList_isActive ) {
						throw new SAXException ( "<" + sTag_MenuState + "> still opened while <" +
								sTag_Application + "> is closed.");
					}
					bApplicationActive = false;
					return;
				} 
				else if (eName.equals(sTag_MenuState)) {	
					
					/* </sFrameStateTag> */
					if ( bJFrameMenuList_isActive ) {
						bJFrameMenuList_isActive = false;
						return;
					} else {
						throw new SAXException ( "<" + sTag_MenuState + "> still opened while <" +
								sTag_Application + "> is closed.");
					}	
					
				} 
				else if (eName.equals(sTag_Menu)) {	
					
					/* </sFrameTag> */
					if ( ! bJFrameMenuList_isActive ) {
						throw new SAXException ( "<" + sTag_Menu + "> opens without " + 
								sTag_MenuState + " being opened.");
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
