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
//import org.caleydo.core.command.factory.CommandFactory;
//import org.caleydo.core.command.base.CmdHandleSetInterface;
//import org.caleydo.core.command.base.CommandAbstractBase;
//import org.caleydo.core.command.window.CmdHandleSetBase;
//import org.caleydo.core.command.window.CmdWindowNewIFrameJoglCanvas;
import org.caleydo.core.command.window.CmdWindowNewIFrameJoglBase;
////import org.caleydo.core.manager.BaseManagerType;
//import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglCanvas;
//import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglScatterplotCanvas;
//import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
//import org.caleydo.core.net.dwt.swing.jogl.mouse.AWTRegionMouseEventHandler;
////import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
////import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import org.caleydo.core.net.dwt.swing.mdi.DDesktopPane;
////import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Creates a internal frame dispaying a 2D histogram in JOGL.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglScatterplot 
extends CmdWindowNewIFrameJoglBase {

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
	 * Example for using org.caleydo.core.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * as a spuer class.
	 * 
	 * @see org.caleydo.core.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {	
		
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
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}

}
