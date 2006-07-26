/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import cerberus.manager.GeneralManager;
import cerberus.manager.ViewCanvasManager;
import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.AbstractCommand;
//import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdWindowSetActiveFrame 
extends AbstractCommand
implements CommandInterface {

	private final GeneralManager refGeneralManager;
	
	private final ViewCanvasManager refViewCanvasManager;
	
	private int iTargetFrameId = -1;
	
	private int iCallerFrameId = -1;

	
	/**
	 * 
	 */
	public CmdWindowSetActiveFrame(final GeneralManager setRefGeneralManager,
			final String details ) {
		refGeneralManager = setRefGeneralManager;
		
		refViewCanvasManager = refGeneralManager.getSingelton().getViewCanvasManager();
		
		if ( details == null ) {
			return;
		}
		
		int iIndex_seperator = details.indexOf(" ");					
		
		try {
			iCallerFrameId = Integer.valueOf( 
					details.substring( 0, iIndex_seperator-1 ) );
			
			iTargetFrameId =Integer.valueOf( 
					details.substring( iIndex_seperator, details.length()-1 ) );
		} catch (NumberFormatException nfe) {
//			assert false : "CmdWindowSetActiveFrame() can not convert id ["+
//				details + "] from String to int";
		}

	}

	public void setCallerAndTargetFrameId( final int iSetCallerFrameId, 
			final int iSetTargetFrameId ) {
		iCallerFrameId = iSetCallerFrameId;
		iTargetFrameId = iSetTargetFrameId;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
//		WorkspaceSwingFrame targetFrame = 
//			refViewCanvasManager.getItemWorkspace( iTargetFrameId );
//		
//		WorkspaceSwingFrame callerFrame = 
//			refViewCanvasManager.getItemWorkspace( iCallerFrameId );
//				
//		if (( callerFrame != null ) && (targetFrame != null)) {
//			callerFrame.setTargetFrame( targetFrame );
//		}
//		else {
//			assert false :"doCommand() falied because either callerFrameId or tragetFrameId were not valid";
//		}

	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_SET_ACTIVE_FRAME;
	}

}
