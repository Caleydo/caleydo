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
////import org.caleydo.core.command.factory.CommandFactory;
////import org.caleydo.core.command.base.CmdHandleSetInterface;
////import org.caleydo.core.command.window.CmdHandleSetBase;
////import org.caleydo.core.command.window.CmdWindowNewIFrameJoglCanvas;
////import org.caleydo.core.manager.BaseManagerType;
//import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglCanvas;
//import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglHeatmapCanvas;
//import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
////import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import org.caleydo.core.net.dwt.swing.mdi.DDesktopPane;
////import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
//		super.doCommand_IFrame();
//
//		DSwingJoglHeatmapCanvas refDSwingJoglHeatmapCanvas = 
//			new DSwingJoglHeatmapCanvas(refGeneralManager,
//					initDSwingJoglCanvas.getGLEventListener() );		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}

}
