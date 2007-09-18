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
//import org.geneview.core.net.dwt.swing.collection.DSwingSelectionCanvas;
//import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
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
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
