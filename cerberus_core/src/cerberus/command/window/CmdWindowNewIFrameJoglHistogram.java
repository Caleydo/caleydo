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
////import cerberus.command.base.CommandAbstractBase;
////import cerberus.command.window.CmdHandleSetBase;
////import cerberus.command.window.CmdWindowNewIFrameJoglCanvas;
//import cerberus.command.window.CmdWindowNewIFrameJoglBase;
////import cerberus.manager.BaseManagerType;
//import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
//import cerberus.net.dwt.swing.jogl.DSwingJoglHeatmapCanvas;
//import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import cerberus.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
////import cerberus.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import cerberus.net.dwt.swing.mdi.DDesktopPane;
////import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram in JOGL.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglHistogram 
extends CmdWindowNewIFrameJoglBase
//extends ACmdHandleSet
implements ICommand {

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
	 * Example for using cerberus.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * as a spuer class.
	 * 
	 * @see cerberus.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {	
		
		super.doCommand_IFrame();

//		DSwingJoglHeatmapCanvas refDSwingJoglHistogramCanvas = 
//			new DSwingJoglHeatmapCanvas(refGeneralManager,
//					initDSwingJoglCanvas.getGLEventListener() );			

		//parentDesktopPane.repaint();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

}
