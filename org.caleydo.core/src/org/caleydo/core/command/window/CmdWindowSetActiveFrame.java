/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.command.window;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
//import org.caleydo.core.net.dwt.swing.jogl.WorkspaceSwingFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
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
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		// TODO Auto-generated method stub

	}

}
