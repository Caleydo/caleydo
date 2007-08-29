/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import cerberus.manager.IGeneralManager;
import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACommand;
//import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class CmdWindowSetActiveFrame 
extends ACommand {
	
//	protected final IViewGLCanvasManager refViewGLCanvasManager;
	
	protected int iTargetFrameId = -1;
	
	protected int iCallerFrameId = -1;

	
	/**
	 * 
	 */
	public CmdWindowSetActiveFrame(final IGeneralManager setRefGeneralManager,
			final String details ) {
		super( -1,
				setRefGeneralManager,
				setRefGeneralManager.getSingelton().getCommandManager(),
				null);		
		
//		refViewGLCanvasManager = refGeneralManager.getSingelton().getViewGLCanvasManager();
		
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
		
		this.setCommandQueueSaxType(CommandQueueSaxType.WINDOW_SET_ACTIVE_FRAME);

	}

	public void setCallerAndTargetFrameId( final int iSetCallerFrameId, 
			final int iSetTargetFrameId ) {
		iCallerFrameId = iSetCallerFrameId;
		iTargetFrameId = iSetTargetFrameId;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		// TODO Auto-generated method stub

	}

}
