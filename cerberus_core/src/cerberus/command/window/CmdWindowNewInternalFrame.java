/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

//import javax.swing.JComponent;
//import javax.swing.JOptionPane;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACommand;

//import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;

import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Creates a popup window dispaying info.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewInternalFrame 
extends ACommand {

//	private DInternalFrame refNewDInternalFrame = null;
	
	private int iCallingFrameId;
	
	protected final IGeneralManager refGeneralManage;
	
	protected final IViewGLCanvasManager refViewGLCanvasManager;
	
	protected String sHeaderText;
	
	/**
	 * Does not set the reference to the parent JComponent.
	 */
	public CmdWindowNewInternalFrame( final IGeneralManager refGeneralManager,
			final int iCallingFrameId,
			final String sHeaderText ) {
		super( -1,refGeneralManager,refGeneralManager.getSingelton().getCommandManager(), null);
		
		this.refGeneralManage = refGeneralManager;
		this.iCallingFrameId = iCallingFrameId;		
		this.refViewGLCanvasManager = 
			refGeneralManage.getSingelton().getViewGLCanvasManager();
		this.sHeaderText = sHeaderText;
		
		this.setCommandQueueSaxType(CommandQueueSaxType.WINDOW_IFRAME_NEW_INTERNAL_FRAME);
	}


//	/**
//	 * Get the reference to the new created internal frame after calling doCommand() internal.
//	 * 
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#getCurrentViewCanvas()
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#setTargetFrameId(String)
//	 * 
//	 * @return new created IViewCanvas object 
//	 */
//	public DInternalFrame doCommand_getDInternalFrame() {
//		this.doCommand();
//		return refNewDInternalFrame;
//	}
//	
//	/**
//	 * Get the last IViewCanvas created with doCommand().
//	 * 
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#doCommand_getViewCanvas()
//	 * @see cerberus.command.window.CmdWindowNewInternalFrame#setTargetFrameId(String)
//	 * 
//	 * @return last IViewCanvas created by doCommand() 
//	 */
//	public DInternalFrame getCurrentDInternalFrame() {
//		return refNewDInternalFrame;
//	}
	
	public void setHeaderText( final String sSetHeaderText ) {
		this.sHeaderText = sSetHeaderText;
	}
	/**
	 * Reset the TargetFrameId for the next doCommand().
	 * 
	 * @see cerberus.command.window.CmdWindowNewInternalFrame#getCurrentViewCanvas()
	 * @see cerberus.command.window.CmdWindowNewInternalFrame#doCommand_getViewCanvas()
	 * 
	 * @param sTargetFrameId TargetFramId of the parent frame of the internal frame to be created
	 */
	public void setTargetFrameId( final int iCallingFrameId ) {
		this.iCallingFrameId = iCallingFrameId;
	}
	
	public int getTargetFrameId( ) {
		return iCallingFrameId;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
//		WorkspaceSwingFrame refCallingFrame =
//			this.refViewCanvasManager.getItemWorkspace( iCallingFrameId );
//		
//		refNewDInternalFrame = 
//			refGeneralManage.getSingelton().getViewCanvasManager().createNewInternalFrame(
//					refCallingFrame.getTargetFrameId() , sHeaderText );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
