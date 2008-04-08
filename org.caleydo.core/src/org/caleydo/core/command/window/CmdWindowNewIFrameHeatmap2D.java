/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.command.window;

//import java.awt.Dimension;

import org.caleydo.core.manager.IGeneralManager;

import org.caleydo.core.command.base.ACmdHandleSet;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
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
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}
	
//	/* (non-Javadoc)
//	 * @see org.caleydo.core.command.ICommand#isEqualType(org.caleydo.core.command.ICommand)
//	 */
//	public boolean isEqualType(ICommand compareToObject) {		
//		return compareToObject.getCommandType() == CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
//	}

}
