/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.window;

//import java.awt.Dimension;

import org.geneview.core.manager.IGeneralManager;

import org.geneview.core.command.base.ACmdHandleSet;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Creates a internal frame dispaying a 2D heatmap.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameHeatmap2D 
extends ACmdHandleSet {

	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameHeatmap2D( final IGeneralManager setRefGeneralManger,
			final int iTargetFrameId  ) {
		
		super( setRefGeneralManger, 
				iTargetFrameId,
				"Heatmap 2D");
		
		//CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
	}

	
	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
//		DInternalFrame newDInternalFrame = subCmdNewIFrame.doCommand_getDInternalFrame();
//		
//		DSwingHeatMap2DCanvas heatmap = 
//			new DSwingHeatMap2DCanvas(refGeneralManager);
//		
//		ISet refSet = (ISet) refGeneralManager.getItem( 70300 );
//		
//		heatmap.setSetRationXY(refSet,20,20);
//		heatmap.setVisible( true );
//		heatmap.updateState();
//		
//		newDInternalFrame.add( heatmap );
//		newDInternalFrame.setPreferredSize( new Dimension(600,600 ) );
//		newDInternalFrame.pack();
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}
	
//	/* (non-Javadoc)
//	 * @see org.geneview.core.command.ICommand#isEqualType(org.geneview.core.command.ICommand)
//	 */
//	public boolean isEqualType(ICommand compareToObject) {		
//		return compareToObject.getCommandType() == CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
//	}

}
