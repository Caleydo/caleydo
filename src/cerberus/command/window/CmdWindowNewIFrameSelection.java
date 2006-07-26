/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import cerberus.manager.GeneralManager;
import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.CmdHandleSetInterface;
import cerberus.command.window.CmdHandleSetBase;
//import cerberus.net.dwt.swing.collection.DSwingSelectionCanvas;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameSelection 
extends CmdHandleSetBase 
implements CommandInterface, CmdHandleSetInterface {

//	private DSwingSelectionCanvas refDSwingSelectionCanvas = null;

	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameSelection( final GeneralManager refGeneralManager,
			final int iTargetFrameId ) {
		super( refGeneralManager,
				iTargetFrameId,
				"Selection" );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		setGuiTextHeader( "Selection" );
		
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
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_IFRAME_OPEN_SELECTION;
	}

}
