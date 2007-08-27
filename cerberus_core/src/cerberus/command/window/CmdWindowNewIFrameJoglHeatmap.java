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
////import cerberus.command.factory.CommandFactory;
////import cerberus.command.base.CmdHandleSetInterface;
////import cerberus.command.window.CmdHandleSetBase;
////import cerberus.command.window.CmdWindowNewIFrameJoglCanvas;
////import cerberus.manager.BaseManagerType;
//import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
//import cerberus.net.dwt.swing.jogl.DSwingJoglHeatmapCanvas;
//import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import cerberus.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
////import cerberus.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import cerberus.net.dwt.swing.mdi.DDesktopPane;
////import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglHeatmap 
extends CmdWindowNewIFrameJoglBase 
implements ICommand //, ICmdHandleSet {
{

		
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
