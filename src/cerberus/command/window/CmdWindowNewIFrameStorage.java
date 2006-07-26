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
import cerberus.command.window.CmdWindowNewInternalFrame;
//import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameStorage 
extends CmdHandleSetBase 
implements CommandInterface, CmdHandleSetInterface {

//	private DSwingStorageCanvas refDSwingNewIFrameStorage = null;
	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameStorage( final GeneralManager refGeneralManager,
			final int iTargetFrameId ) {
		super( refGeneralManager, 
				iTargetFrameId,
				"Storage" );
	}


	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
			
		//TODO: include header
		//setGuiTextHeader( "Storage" );
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
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}


	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_IFRAME_OPEN_STORAGE;
	}

}
