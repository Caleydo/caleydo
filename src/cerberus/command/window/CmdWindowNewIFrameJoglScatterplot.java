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
//import cerberus.command.factory.CommandFactory;
//import cerberus.command.base.CmdHandleSetInterface;
//import cerberus.command.base.CommandAbstractBase;
//import cerberus.command.window.CmdHandleSetBase;
//import cerberus.command.window.CmdWindowNewIFrameJoglCanvas;
import cerberus.command.window.CmdWindowNewIFrameJoglBase;
////import cerberus.manager.BaseManagerType;
//import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
//import cerberus.net.dwt.swing.jogl.DSwingJoglScatterplotCanvas;
//import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;
//import cerberus.net.dwt.swing.jogl.mouse.AWTRegionMouseEventHandler;
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
public class CmdWindowNewIFrameJoglScatterplot 
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
	public CmdWindowNewIFrameJoglScatterplot( final IGeneralManager refGeneralManager,
			final int iCallingFrameId
//			,
//			final GLEventForwardListener refGLEventListener,
//			final DSwingJoglCanvas initDSwingJoglCanvas ) {
			) {

		super( refGeneralManager,
				iCallingFrameId,
//				refGLEventListener,
//				initDSwingJoglCanvas,
				"Scatterplot - JOGL");
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

//		int[] iStaticIndex = {71100,71200};
//		
//		DSwingJoglScatterplotCanvas refDSwingJoglScatterplotCanvas = 
//			new DSwingJoglScatterplotCanvas(refGeneralManager,
//					//initDSwingJoglCanvas.getGLEventListener(),
//					initDSwingJoglCanvas,
//					iStaticIndex );	
//		
//		AWTRegionMouseEventHandler meHandler = new AWTRegionMouseEventHandler();
//		
//		initDSwingJoglCanvas.getGLCanvas().addMouseListener( meHandler );
//		initDSwingJoglCanvas.getGLCanvas().addMouseMotionListener( meHandler );
//		
//		meHandler.addViewCameraTarget( refDSwingJoglScatterplotCanvas );
//		
//		//refDSwingJoglScatterplotCanvas.setSet();
//
//		//parentDesktopPane.repaint();
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}

}
