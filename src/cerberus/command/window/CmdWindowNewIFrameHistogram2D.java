/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

//import javax.swing.JComponent;
//import javax.swing.JOptionPane;
//import javax.swing.JLabel;

import cerberus.manager.GeneralManager;
import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.CmdHandleSetInterface;
import cerberus.command.window.CmdHandleSetBase;
import cerberus.data.collection.Set;
//import cerberus.net.dwt.swing.DSwingHistogramCanvas;
////import cerberus.net.dwt.swing.mdi.DDesktopPane;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameHistogram2D 
extends CmdHandleSetBase 
implements CommandInterface, CmdHandleSetInterface {

//	private DSwingHistogramCanvas refDViewHistogram2D = null;

	
	/**
	 * Set the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameHistogram2D( GeneralManager refGeneralManager,
			final int iTargetFrameId ) {
		super( refGeneralManager, 
				iTargetFrameId,
				"Histogram 2D" );

	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert refGeneralManager != null:"can not handle null-pointer to GeneralManager";

//		refDViewHistogram2D = new DSwingHistogramCanvas( refGeneralManager, refCurrentSet );
//		
//		setGuiTextHeader( "Histogram" );
//		
//		DInternalFrame newDInternalFrame = 
//			subCmdNewIFrame.doCommand_getDInternalFrame();
//
//		Set refSet = (Set) refGeneralManager.getItem( 70300 );
//				
//		refDViewHistogram2D.setSet( refSet );
//		refDViewHistogram2D.updateState();
//		refDViewHistogram2D.setVisible( true );
//		
//		newDInternalFrame.add( refDViewHistogram2D );
//		newDInternalFrame.setMaximizable( false );		
//		newDInternalFrame.pack();
//		
//		//parentDesktopPane.addInternalFrame( newDInternalFrame );
//		// newDInternalFrame.setVisible( true );
//		
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
		return CommandType.WINDOW_IFRAME_OPEN_HISTOGRAM2D;
	}

}
