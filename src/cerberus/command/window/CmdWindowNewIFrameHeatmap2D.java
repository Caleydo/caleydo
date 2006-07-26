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

import cerberus.manager.GeneralManager;

//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;


import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.CmdHandleSetInterface;
import cerberus.data.collection.Set;
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
extends CmdHandleSetBase 
implements CommandInterface, CmdHandleSetInterface {

	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameHeatmap2D( final GeneralManager setRefGeneralManger,
			final int iTargetFrameId  ) {
		
		super( setRefGeneralManger, 
				iTargetFrameId,
				"Heatmap 2D");
	}

	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
//		DInternalFrame newDInternalFrame = subCmdNewIFrame.doCommand_getDInternalFrame();
//		
//		DSwingHeatMap2DCanvas heatmap = 
//			new DSwingHeatMap2DCanvas(refGeneralManager);
//		
//		Set refSet = (Set) refGeneralManager.getItem( 70300 );
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
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}
	
//	/* (non-Javadoc)
//	 * @see cerberus.command.CommandInterface#isEqualType(cerberus.command.CommandInterface)
//	 */
//	public boolean isEqualType(CommandInterface compareToObject) {		
//		return compareToObject.getCommandType() == CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
//	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D;
	}

}
