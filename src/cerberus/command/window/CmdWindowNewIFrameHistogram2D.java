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

import cerberus.manager.IGeneralManager;
import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACmdHandleSet;
import cerberus.command.base.ICmdHandleSet;
import cerberus.data.collection.ISet;
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
extends ACmdHandleSet 
implements ICommand, ICmdHandleSet {

//	private DSwingHistogramCanvas refDViewHistogram2D = null;

	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameHistogram2D( IGeneralManager refGeneralManager,
			final int iTargetFrameId ) {
		super( refGeneralManager, 
				iTargetFrameId,
				"Histogram 2D" );

	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert refGeneralManager != null:"can not handle null-pointer to IGeneralManager";

//		refDViewHistogram2D = new DSwingHistogramCanvas( refGeneralManager, refCurrentSet );
//		
//		setGuiTextHeader( "Histogram" );
//		
//		DInternalFrame newDInternalFrame = 
//			subCmdNewIFrame.doCommand_getDInternalFrame();
//
//		ISet refSet = (ISet) refGeneralManager.getItem( 70300 );
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.WINDOW_IFRAME_OPEN_HISTOGRAM2D;
	}

}
