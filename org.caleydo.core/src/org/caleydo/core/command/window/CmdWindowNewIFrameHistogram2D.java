/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.command.window;

//import javax.swing.JComponent;
//import javax.swing.JOptionPane;
//import javax.swing.JLabel;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.base.ACmdHandleSet;
import org.caleydo.core.command.base.ICmdHandleSet;
//import org.caleydo.core.data.collection.ISet;
//import org.caleydo.core.net.dwt.swing.DSwingHistogramCanvas;
////import org.caleydo.core.net.dwt.swing.mdi.DDesktopPane;
//import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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

		//return CommandType.WINDOW_IFRAME_OPEN_HISTOGRAM2D;
	}
	
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
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
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}

}
