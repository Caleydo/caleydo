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
////import org.geneview.core.command.factory.CommandFactory;
////import org.geneview.core.command.base.CmdHandleSetInterface;
////import org.geneview.core.command.base.CommandAbstractBase;
////import org.geneview.core.command.window.CmdHandleSetBase;
////import org.geneview.core.command.window.CmdWindowNewIFrameJoglCanvas;
//import org.geneview.core.command.window.CmdWindowNewIFrameJoglBase;
////import org.geneview.core.manager.BaseManagerType;
//import org.geneview.core.net.dwt.swing.jogl.DSwingJoglCanvas;
//import org.geneview.core.net.dwt.swing.jogl.DSwingJoglHeatmapCanvas;
//import org.geneview.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import org.geneview.core.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
////import org.geneview.core.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import org.geneview.core.net.dwt.swing.mdi.DDesktopPane;
////import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram in JOGL.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglHistogram 
extends CmdWindowNewIFrameJoglBase {

	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param refGeneralManager reference to singelton
	 * @param iCallingFrameId Id of the calling frame
	 * @param refGLEventListener listener for GLEvents or null if listener shall be created
	 * @param initDSwingJoglCanvas reference to existing GLcanvas or null if canvas shall be created
	 */
	public CmdWindowNewIFrameJoglHistogram( final IGeneralManager refGeneralManager,
			final int iCallingFrameId
//			,
//			final GLEventForwardListener refGLEventListener,
//			final DSwingJoglCanvas initDSwingJoglCanvas ) {
		) {

		super( refGeneralManager,
				iCallingFrameId,
//				refGLEventListener,
//				initDSwingJoglCanvas,
				"Histogram - JOGL");
		
		//CommandType.WINDOW_IFRAME_OPEN_JOGL_HISTOGRAM;
	}

	
	/** 
	 * Example for using org.geneview.core.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * as a spuer class.
	 * 
	 * @see org.geneview.core.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {	
		
		super.doCommand_IFrame();

//		DSwingJoglHeatmapCanvas refDSwingJoglHistogramCanvas = 
//			new DSwingJoglHeatmapCanvas(refGeneralManager,
//					initDSwingJoglCanvas.getGLEventListener() );			

		//parentDesktopPane.repaint();
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
