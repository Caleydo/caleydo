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
//import org.caleydo.core.net.dwt.swing.collection.DSwingSelectionCanvas;
//import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameSelection 
extends ACmdHandleSet {

//	private DSwingSelectionCanvas refDSwingSelectionCanvas = null;

	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameSelection( final IGeneralManager refGeneralManager,
			final int iTargetFrameId ) {
		super( refGeneralManager,
				iTargetFrameId,
				"IVirtualArray" );
		
		//CommandType.WINDOW_IFRAME_OPEN_SELECTION;
	}
	
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		setGuiTextHeader( "IVirtualArray" );
		
//		DInternalFrame newDInternalFrame = subCmdNewIFrame.doCommand_getDInternalFrame();
//	
//		refDSwingSelectionCanvas = new DSwingSelectionCanvas( refGeneralManager );
//		refDSwingSelectionCanvas.updateState();
//		refDSwingSelectionCanvas.setVisible( true );
//		
//		newDInternalFrame.add( refDSwingSelectionCanvas );
//		newDInternalFrame.setMaximizable( false );		
//		newDInternalFrame.pack();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}

}
