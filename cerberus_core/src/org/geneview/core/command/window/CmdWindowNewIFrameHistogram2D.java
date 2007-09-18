/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.window;

//import javax.swing.JComponent;
//import javax.swing.JOptionPane;
//import javax.swing.JLabel;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.command.ICommand;
import org.geneview.core.command.base.ACmdHandleSet;
import org.geneview.core.command.base.ICmdHandleSet;
//import org.geneview.core.data.collection.ISet;
//import org.geneview.core.net.dwt.swing.DSwingHistogramCanvas;
////import org.geneview.core.net.dwt.swing.mdi.DDesktopPane;
//import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
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
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
