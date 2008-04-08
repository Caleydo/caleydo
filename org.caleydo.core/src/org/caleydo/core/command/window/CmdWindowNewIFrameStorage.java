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
import org.caleydo.core.command.base.ACmdHandleSet;
//import org.caleydo.core.command.window.CmdWindowNewInternalFrame;
//import org.caleydo.core.net.dwt.swing.collection.DSwingStorageCanvas;
//import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameStorage 
extends ACmdHandleSet  {

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
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
			
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
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}

}
