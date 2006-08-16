/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import java.awt.Dimension;

import cerberus.manager.IGeneralManager;

//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;


import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACmdHandleSet;
import cerberus.command.base.ICmdHandleSet;
import cerberus.data.collection.ISet;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
//import cerberus.net.dwt.swing.DSwingHeatMap2DCanvas;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a internal frame dispaying a 2D heatmap.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameHeatmap2D 
extends ACmdHandleSet 
implements ICommand, ICmdHandleSet {

	
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
	}

	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}
	
//	/* (non-Javadoc)
//	 * @see cerberus.command.ICommand#isEqualType(cerberus.command.ICommand)
//	 */
//	public boolean isEqualType(ICommand compareToObject) {		
//		return compareToObject.getCommandType() == CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
//	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
	}

}
