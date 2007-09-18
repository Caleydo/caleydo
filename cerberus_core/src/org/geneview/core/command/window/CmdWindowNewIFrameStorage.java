/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.window;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.command.base.ACmdHandleSet;
//import org.geneview.core.command.window.CmdWindowNewInternalFrame;
//import org.geneview.core.net.dwt.swing.collection.DSwingStorageCanvas;
//import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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
