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
//import org.geneview.core.command.factory.CommandFactory;
//import org.geneview.core.command.base.CmdHandleSetInterface;
//import org.geneview.core.command.base.CommandAbstractBase;
//import org.geneview.core.command.window.CmdHandleSetBase;
//import org.geneview.core.command.window.CmdWindowNewIFrameJoglCanvas;
import org.geneview.core.command.window.CmdWindowNewIFrameJoglBase;
////import org.geneview.core.manager.BaseManagerType;
//import org.geneview.core.net.dwt.swing.jogl.DSwingJoglCanvas;
//import org.geneview.core.net.dwt.swing.jogl.DSwingJoglScatterplotCanvas;
//import org.geneview.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
//import org.geneview.core.net.dwt.swing.jogl.mouse.AWTRegionMouseEventHandler;
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
	 * Example for using org.geneview.core.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * as a spuer class.
	 * 
	 * @see org.geneview.core.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {	
		
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
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
