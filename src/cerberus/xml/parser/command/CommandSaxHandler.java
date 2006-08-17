/**
 * 
 */
package cerberus.xml.parser.command;

import java.util.LinkedList;

//import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.queue.ICommandQueue;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.ICommandManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.ACerberusDefaultSaxHandler;


/**
 * Create Menus in Frames from XML file.
 * 
 * @author java
 *
 */
public class CommandSaxHandler extends ACerberusDefaultSaxHandler  {

	private final ICommandManager refCommandManager;
	
	private boolean bCommandBuffer_isActive = false;
	
	private boolean bCommandQueue_isActive = false;
	
	private boolean bData_EnableMenu = true;
	
	protected boolean bApplicationActive = false;
	
//	private int iData_MenuId = -1;
//	private int iData_MenuParentId = -1;
//	private int iData_TargetFrameId = -1;
//	private int iData_CommandId = -1;
//	
//	private String sData_MenuType = "none";
//	private String sData_MenuTitle = "none";
//	private String sData_MenuTooltip = "";
	private String sData_MenuMemento = "-";
	
	protected ICommandQueue refCommandQueueIter = null;
	
	protected String sData_Queue_process;
	protected String sData_Queue_type;
	protected int iData_Queue_CmdId;
	protected int iData_Queue_CmdQueueId;
	
	protected int iDefaultFrameWidht = 100;
	protected int iDefaultFrameHeight = 100;
	protected int iDefaultFrameX = 0;
	protected int iDefaultFrameY = 0;
	protected int iDefaultFrameId = -1;
	
	/* XML Attributes */
	protected static final String sMenuKey_processType = "process";
	protected static final String sMenuKey_commandId = "cmdId";
	//protected static final String sMenuKey_parentMenuId = "parentMenuId";
	//protected static final String sMenuKey_enabled = "enabled";
	//protected static final String sMenuKey_title = "title";
	protected static final String sMenuKey_details = "tooltip";
	protected static final String sMenuKey_memento = "mementoId";
	protected static final String sCmdKey_type = "type";
	//protected static final String sMenuKey_objectId = "id";
	
	public static final String sCmdQueueKey_process = "process";
	public static final String sCmdQueueKey_type = "type";
	public static final String sCmdQueueKey_cmdQueueId = "cmdQueueId";
	
	/* XML Tags */
	public static final String sTag_Application = "Application";	
	public static final String sTag_CommandBuffer = "CommandBuffer";	
	public static final String sTag_Command = "Cmd";
	public static final String sTag_CommandQueue = "CmdQueue";
	
	
	
	/**
	 * <Application >
	 *  <CommandBuffer>
	 *    <Cmd />
	 *    <Cmd />
	 *  </CommandBuffer>
	 * </Application>
	 */
	public CommandSaxHandler( final IGeneralManager setGeneralManager  ) {
		super( setGeneralManager );
		
		refCommandManager = 
			refGeneralManager.getSingelton().getCommandManager();

		assert refCommandManager != null : "ICommandManager was not created by ISingelton!";
	}
	
	/**
	 * ISet state of application-tag.
	 * 
	 * @param stateApplication TRUE ot indicate, that teh application tag is opened, FALSE if it is closed.
	 */
	public final void setApplicationStatus( boolean stateApplication ) {
		this.bApplicationActive = stateApplication;
	}
	
	/**
	 * Get state of application tag.
	 * 
	 * @return TRUE if application tag is opened.
	 */
	public final boolean getApplicationStatus() {
		return this.bApplicationActive;
	}
	
	public String createXMLcloseingTag( final Object frame, final String sIndent ) {
		
		assert false : "not implemented!";
				
		return "";
	}
	
	public String createXML( final Object frame, final String sIndent ) {
		String result = sIndent;		

		
//		if ( frame.getClass().equals( SwingJoglJFrame.class )) {
//			SwingJoglJFrame jframe = (SwingJoglJFrame) frame;
//			
////			result += "<" + sMenuTag;
////			
////			iCurrentFrameId = jframe.getId();
////			dim = jframe.getSize();
////			location = jframe.getLocation();			
////			bIsVisible = jframe.isVisible();
////			sTypeName = jframe.getFrameType().getTypeNameForXML();
////			sTitle = jframe.getTitle();
////			sName = jframe.getName();
////			sClosingTag = ">\n";
//			
//		} else if ( frame.getClass().equals( SwingJoglJInternalFrame.class )) {
//			SwingJoglJInternalFrame jiframe = (SwingJoglJInternalFrame) frame;
//			
////			result += "<" + sInternalFrameTag;
////			
////			iCurrentFrameId = jiframe.getId();
////			dim = jiframe.getSize();
////			location = jiframe.getLocation();			
////			bIsVisible = jiframe.isVisible();
////			sTitle = jiframe.getTitle();
////			sTypeName = jiframe.getFrameType().getTypeNameForXML();
////			sName = jiframe.getName();
////			sClosingTag = "> </" + sInternalFrameTag + ">\n";
//			
//		} else {
//			throw new RuntimeException("Can not create XML string from class [" +
//					frame.getClass().getName() + "] ;only support SwingJoglJFrame and SwingJoglJInternalFrame");
//		}
//		
////		result +=          " " + sMenuKey_objectId + sArgumentBegin + Integer.toString(iData_MenuId);
//		result += sArgumentEnd + sMenuKey_processType + sArgumentBegin + Integer.toString(this.iData_TargetFrameId);
//		result += sArgumentEnd + sMenuKey_commandId + sArgumentBegin + Integer.toString(this.iData_CommandId);
////		result += sArgumentEnd + sMenuKey_parentMenuId + sArgumentBegin + Integer.toString(this.iData_MenuParentId);;
//		result += sArgumentEnd + sCmdKey_type + sArgumentBegin + sData_MenuType;
//		
////		result += sArgumentEnd + sMenuKey_enabled + sArgumentBegin + Boolean.toString(bData_EnableMenu);	
//		result += sArgumentEnd + sMenuKey_memento + sArgumentBegin + sData_MenuMemento;
//		
////		result += sArgumentEnd + sMenuKey_title + sArgumentBegin + sData_MenuTitle;
//		result += sArgumentEnd + sMenuKey_details + sArgumentBegin + sData_MenuTooltip;					
//				
//		
		return result;
	}

//	public void setApplicationValue( String sValue ) {
//		sApplicationValue = sValue;
//	}
	
	
	
//	/**
//	 * 
//	 * Read values of class: iCurrentFrameId
//	 * @param attrs
//	 * @param bIsExternalFrame
//	 */
//	private void parseCommandQueueData( final Attributes attrs, boolean bIsExternalFrame ) {
//		
//		try 
//		{
//			/* create new Frame */
//			sData_Queue_process = assignStringValue( attrs, 
//					CommandQueueSaxType.RUN_QUEUE_ON_DEMAND.getXmlKey(), 
//					CommandQueueSaxType.RUN_QUEUE_ON_DEMAND.toString() );
//			
//			iData_Queue_CmdId = assignIntValueIfValid( attrs, 
//					CommandQueueSaxType.CMD_ID.getXmlKey(),
//					-1  );
//			
//			iData_Queue_CmdQueueId = assignIntValueIfValid( attrs, 
//					CommandQueueSaxType.CMDQUEUE_ID.getXmlKey(),
//					-1  );
//			
//			sData_Queue_type = assignStringValue( attrs,
//					CommandQueueSaxType.COMMAND_QUEUE_RUN.getXmlKey(), 
//					CommandQueueSaxType.COMMAND_QUEUE_RUN.toString() );		
//			
//			int iData_Queue_ThreadPool_Id = assignIntValueIfValid( attrs, 
//					CommandQueueSaxType.CMD_THREAD_POOL_ID.getXmlKey(),
//					-1  );
//						
//			int iData_Queue_ThreadPool_Wait_Id = assignIntValueIfValid( attrs, 
//					CommandQueueSaxType.CMD_THREAD_POOL_WAIT_ID.getXmlKey(),
//					-1  );
//			
//			this.refCommandManager.createCommandQueue( sData_Queue_type,
//					sData_Queue_process,
//					iData_Queue_CmdId,
//					iData_Queue_CmdQueueId,
//					iData_Queue_ThreadPool_Id,
//					-iData_Queue_ThreadPool_Wait_Id );
//				
//		}
//		catch ( Exception e) 
//		{
//			System.err.println("CommandSaxHandler::readCommandQueueData() ERROR while parsing " + e.toString() );
//		}
//	}
	

	
	/**
	 * 
	 * Read values of class: iCurrentFrameId
	 * 
	 * @param attrs
	 * @param bIsExternalFrame
	 */
	private ICommand readCommandData( final Attributes attrs, boolean bIsExternalFrame ) {
		
		ICommand lastCommand = null;
		
		try 
		{
			/* create new Frame */
			String sData_Cmd_process = assignStringValue( attrs, 
					CommandQueueSaxType.TAG_PROCESS.getXmlKey(), 
					CommandQueueSaxType.TAG_PROCESS.toString() );
			
			String sData_Cmd_label = assignStringValue( attrs, 
					CommandQueueSaxType.TAG_LABEL.getXmlKey(), 
					CommandQueueSaxType.TAG_LABEL.toString() );
			
			String sData_CmdId = assignStringValue( attrs, 
					CommandQueueSaxType.TAG_CMD_ID.getXmlKey(),
					Integer.toString(-1)  );
			
			String sData_TargetId = assignStringValue( attrs, 
					CommandQueueSaxType.TAG_TARGET_ID.getXmlKey(),
					Integer.toString(-1)  );
			
			String sData_Cmd_MementoId = assignStringValue( attrs, 
					CommandQueueSaxType.TAG_MEMENTO_ID.getXmlKey(),
					Integer.toString(-1)  );
			
			String sData_Cmd_type = assignStringValue( attrs,
					CommandQueueSaxType.TAG_TYPE.getXmlKey(), 
					CommandQueueSaxType.TAG_TYPE.toString() );										

			String sData_Cmd_attribute1 = assignStringValue( attrs,
					CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey(), 
					CommandQueueSaxType.TAG_ATTRIBUTE1.toString() );	
			
			String sData_Cmd_attribute2 = assignStringValue( attrs,
					CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey(), 
					CommandQueueSaxType.TAG_ATTRIBUTE2.toString() );	
				
			String sData_Cmd_detail = assignStringValue( attrs,
					CommandQueueSaxType.TAG_DETAIL.getXmlKey(), 
					CommandQueueSaxType.TAG_DETAIL.toString() );										
				
			LinkedList <String> llAttributes = 
				new LinkedList <String> ();
			
			llAttributes.add( sData_CmdId );	
			llAttributes.add( sData_TargetId );			
			llAttributes.add( sData_Cmd_label );
			llAttributes.add( sData_Cmd_process );
			llAttributes.add( sData_Cmd_MementoId );
			llAttributes.add( sData_Cmd_detail );
			llAttributes.add( sData_Cmd_attribute1 );
			llAttributes.add( sData_Cmd_attribute2 );
						
			lastCommand = refCommandManager.createCommand( 
					sData_Cmd_type,
					llAttributes );
			
			
			if (( lastCommand != null )&&(sData_Cmd_process.equals( CommandQueueSaxType.RUN_CMD_NOW.toString() )))
			{				
				this.refGeneralManager.getSingelton().getLoggerManager().logMsg("status: do command: " + 
						lastCommand.toString() );
				lastCommand.doCommand();
			}
			
			return lastCommand;
			
		}
		catch ( Exception e) 
		{
			System.err.println(" ERROR while parsing " + e.toString() );
			
			return null;
		}
	}
	
	/**
	 * 
	 * Read values of class: iCurrentFrameId
	 * @param attrs
	 * @param bIsExternalFrame
	 */
	private void readCommandQueueData( final Attributes attrs, boolean bIsExternalFrame ) {
		
		ICommand lastCommand = null;
		
		int iData_Queue_ThreadPool_Id = -1;					
		int iData_Queue_ThreadPool_Wait_Id = -1;
		
		try 
		{
			/* create new Frame */
			sData_Queue_process = assignStringValue( attrs, 
					CommandQueueSaxType.RUN_QUEUE_ON_DEMAND.getXmlKey(), 
					CommandQueueSaxType.RUN_QUEUE_ON_DEMAND.toString() );
			
			iData_Queue_CmdId = assignIntValueIfValid( attrs, 
					CommandQueueSaxType.CMD_ID.getXmlKey(),
					-1  );
			
			iData_Queue_CmdQueueId = assignIntValueIfValid( attrs, 
					CommandQueueSaxType.CMDQUEUE_ID.getXmlKey(),
					-1  );
			
			sData_Queue_type = assignStringValue( attrs,
					CommandQueueSaxType.COMMAND_QUEUE_RUN.getXmlKey(), 
					CommandQueueSaxType.COMMAND_QUEUE_RUN.toString() );		
			
			iData_Queue_ThreadPool_Id = assignIntValueIfValid( attrs, 
					CommandQueueSaxType.CMD_THREAD_POOL_ID.getXmlKey(),
					-1  );
						
			iData_Queue_ThreadPool_Wait_Id = assignIntValueIfValid( attrs, 
					CommandQueueSaxType.CMD_THREAD_POOL_WAIT_ID.getXmlKey(),
					-1  );
			
			lastCommand = refCommandManager.createCommandQueue( 
					sData_Queue_type,
					sData_Queue_process,
					iData_Queue_CmdId,
					iData_Queue_CmdQueueId,
					iData_Queue_ThreadPool_Id,
					-iData_Queue_ThreadPool_Wait_Id );
				
		}
		catch ( Exception e) 
		{
			System.err.println("CommandSaxHandler::readCommandQueueData() ERROR while parsing " + e.toString() );
		}
		
		/* -------------------------------------------- */
		
//		/* create new Frame */
//		iData_TargetFrameId = assignIntValueIfValid( attrs, sMenuKey_processType, -1 );
//		iData_CommandId = assignIntValueIfValid( attrs, sMenuKey_commandId, -1  );
//								
////		iData_MenuParentId = assignIntValueIfValid( attrs, sMenuKey_parentMenuId, -1 );
////		iData_MenuId = assignIntValueIfValid( attrs, sMenuKey_objectId, -1 );
////		
////		bData_EnableMenu = assignBooleanValueIfValid( attrs, sMenuKey_enabled, true );											
//		
//		sData_MenuTooltip = attrs.getValue( sMenuKey_details );
//		sData_MenuMemento = attrs.getValue( sMenuKey_memento );
//			
//		String sData_Queue_Type = attrs.getValue( sCmdQueueKey_type );		
//		String sData_Queue_Id = attrs.getValue( sCmdQueueKey_cmdQueueId );			
//		String sData_Queue_process = attrs.getValue( sCmdQueueKey_process );
		
		
		
		if ( sData_Queue_type.equals( CommandType.COMMAND_QUEUE_RUN.toString() )) {
			
			if ( sData_Queue_process.equals( "RUN_QUEUE" )) {
				lastCommand.doCommand();
				
				refCommandQueueIter = null;
			}
			
		} 
		else if ( sData_Queue_type.equals( CommandType.COMMAND_QUEUE_OPEN.toString() )) {
			
			refCommandQueueIter = (ICommandQueue) lastCommand;
			
		}

//			throw new CerberusRuntimeException( "can not create command from [" +
//					attrs.toString() + "]");
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
				
				if (eName.equals(sTag_CommandBuffer)) {
					/* <sFrameStateTag> */
					if ( bCommandBuffer_isActive ) {
						throw new SAXException ( "<" + sTag_CommandBuffer + "> already opened!");
					} else {
						bCommandBuffer_isActive = true;
						return;
					}
					
				} //end: if (eName.equals(sFrameStateTag)) {
				else if (eName.equals(sTag_Command)) {
					
					
					
					if ( bCommandBuffer_isActive ) {						
						/**
						 * <CommandBuffer>
						 *   ... 
						 *  <Cmd ...> 
						 */
						
						if  ( bCommandQueue_isActive ) {
							/**
							 * <CommandBuffer>
							 * ...
							 * <CmdQueue> <br>
							 *  ...
							 * <Cmd ...>
							 */
							
							//readCommandQueueData( attrs, true );
							ICommand lastCommand = 
								readCommandData( attrs, true );
							
							if ( lastCommand != null ) {
								refCommandQueueIter.addCmdToQueue( lastCommand );
							} 
							else 
							{
								refGeneralManager.getSingelton().getLoggerManager().logMsg(
										"CommandQueue: no Command to add. skip it.");
							}
							
							
														
						} else {
							/**
							 * <CommandBuffer>
							 * ...
							 * <Cmd ...>
							 */
							
							//readCommandQueueData( attrs, true );
							ICommand lastCommand = readCommandData( attrs, true );
							
							if ( lastCommand == null ) 
							{
								refGeneralManager.getSingelton().getLoggerManager().logMsg(
										"Command: can not execute command du to error while parsing. skip it.");
							}
							
						

							
						}
						

						
					}  //if ( bCommandBuffer_isActive ) {
					else 
					{ 
						throw new SAXException ( "<"+ sTag_Command + "> opens without <" + 
								sTag_CommandBuffer + "> being opened!");
					}
				}
				else if (eName.equals( sTag_CommandQueue )) {
					
					/**
					 *  <CmdQueue ...> 
					 */
					if ( bCommandBuffer_isActive ) {
						
						if ( bCommandQueue_isActive ) {
							throw new SAXException ( "<"+ sTag_CommandQueue + "> opens inside a <" + 
									sTag_CommandQueue + "> block!");
						}
						
						bCommandQueue_isActive = true;
						
						readCommandQueueData( attrs, true );
						

						
					} else {
						throw new SAXException ( "<"+ sTag_Command + "> opens without <" + 
								sTag_CommandBuffer + "> being opened!");
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
				if (eName.equalsIgnoreCase(sTag_Application)) {
					/* </sApplicationTag> */
					if ( bCommandBuffer_isActive ) {
						throw new SAXException ( "<" + sTag_CommandBuffer + "> still opened while <" +
								sTag_Application + "> is closed.");
					}
					bApplicationActive = false;
					return;
				} 
				else if (eName.equals(sTag_CommandBuffer)) {	
					
					/* </CommandBuffer> */
					if ( bCommandBuffer_isActive ) {
						bCommandBuffer_isActive = false;
						return;
					} else {
						throw new SAXException ( "<" + sTag_CommandBuffer + "> still opened while <" +
								sTag_Application + "> is closed.");
					}	
					
				} 
				else if (eName.equals(sTag_Command)) {	
					
					/* </cmd> */
					if ( ! bCommandBuffer_isActive ) {
						throw new SAXException ( "<" + sTag_Command + "> opens without " + 
								sTag_CommandBuffer + " being opened.");
					}	
					
				}
				else if (eName.equals( sTag_CommandQueue )) {
					
					/**
					 *  </CmdQueue ...> 
					 */
					if ( bCommandBuffer_isActive ) {
						
						bCommandQueue_isActive = false;
					} else {
						throw new SAXException ( "<" + sTag_CommandQueue + "> opens without " + 
								sTag_CommandBuffer + " being opened.");
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
