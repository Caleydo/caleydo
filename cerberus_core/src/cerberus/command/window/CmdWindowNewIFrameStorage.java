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
import cerberus.command.ICommand;
import cerberus.command.base.ACmdHandleSet;
import cerberus.command.base.ICmdHandleSet;
//import cerberus.command.window.CmdWindowNewInternalFrame;
//import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameStorage 
extends ACmdHandleSet 
implements ICommand, ICmdHandleSet {

//	private DSwingStorageCanvas refDSwingNewIFrameStorage = null;
	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameStorage( final IGeneralManager refGeneralManager,
			final int iTargetFrameId ) {
		super( refGeneralManager, 
				iTargetFrameId,
				"IStorage" );
		
		//CommandType.WINDOW_IFRAME_OPEN_STORAGE;
	}


	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
			
		//TODO: include header
		//setGuiTextHeader( "IStorage" );
		//DInternalFrame newDInternalFrame = createDInternalFrame( sGui_TextHeader );
		
//		DInternalFrame refNewDInternalFrame = 
//			subCmdNewIFrame.doCommand_getDInternalFrame();
//					
//		refDSwingNewIFrameStorage = new DSwingStorageCanvas( refGeneralManager );
//		refDSwingNewIFrameStorage.updateState();
//		refDSwingNewIFrameStorage.setVisible( true );
//		
//		refNewDInternalFrame.add( refDSwingNewIFrameStorage );
//		refNewDInternalFrame.setMaximizable( false );		
//		refNewDInternalFrame.pack();	
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
