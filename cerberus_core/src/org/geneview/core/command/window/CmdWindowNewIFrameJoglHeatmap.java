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
////import org.geneview.core.command.window.CmdHandleSetBase;
////import org.geneview.core.command.window.CmdWindowNewIFrameJoglCanvas;
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
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglHeatmap 
extends CmdWindowNewIFrameJoglBase {

		
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameJoglHeatmap( IGeneralManager refGeneralManager,
			final int iCallingFrameId
//			,
//			final GLEventForwardListener refGLEventListener,
//			final DSwingJoglCanvas initDSwingJoglCanvas 
			) {
	
		super( refGeneralManager,
				iCallingFrameId,
//				refGLEventListener,
//				initDSwingJoglCanvas,
				"Heatmap - JOGL" );
		
		//CommandType.WINDOW_IFRAME_OPEN_JOGL_HEATMAP;
	}

	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
//		super.doCommand_IFrame();
//
//		DSwingJoglHeatmapCanvas refDSwingJoglHeatmapCanvas = 
//			new DSwingJoglHeatmapCanvas(refGeneralManager,
//					initDSwingJoglCanvas.getGLEventListener() );		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
