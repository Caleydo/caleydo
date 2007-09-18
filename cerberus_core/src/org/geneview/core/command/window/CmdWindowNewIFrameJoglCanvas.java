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
import org.geneview.core.command.base.ACommand;
//import org.geneview.core.command.base.CmdHandleSetInterface;
import org.geneview.core.command.window.CmdWindowNewInternalFrame;
////import org.geneview.core.data.collection.ViewCanvas;
////import org.geneview.core.command.window.CmdHandleSetBase;
////import org.geneview.core.manager.BaseManagerType;
//import org.geneview.core.net.dwt.swing.jogl.DSwingJoglCanvas;
////import org.geneview.core.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
////import org.geneview.core.net.dwt.swing.jogl.DSwingJoglHistogramCanvas;
//import org.geneview.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import org.geneview.core.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
//import org.geneview.core.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import org.geneview.core.net.dwt.swing.mdi.DDesktopPane;
////import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
import org.geneview.core.util.exception.GeneViewRuntimeException;

//import org.geneview.core.net.dwt.swing.jogl.listener.GLEventObserverListener;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglCanvas 
extends ACommand  {

	private int iCountCanvas = 1;		
	
//	private DSwingJoglCanvas refDSwingJoglCanvas = null;
	
	protected CmdWindowNewInternalFrame subCmdWindowNewInternalFrame;
	
//	private final GLEventForwardListener refGLEventListener;
	
	protected int iSetCallingFrameId;
	
	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param setParentComonent parent JComponenet
	 */
	public CmdWindowNewIFrameJoglCanvas( final IGeneralManager refGeneralManager,
//			final GLEventForwardListener refGLEventListener,
			final int iSetCallingFrameId,
			final String sHeaderText ) {
		
		super( -1, 
				refGeneralManager,
				refGeneralManager.getSingelton().getCommandManager(),
				null);
		
		this.iSetCallingFrameId = iSetCallingFrameId;
		
//		this.refGLEventListener = refGLEventListener;
		
		subCmdWindowNewInternalFrame = 
			new CmdWindowNewInternalFrame(refGeneralManager, 
					iSetCallingFrameId,
					sHeaderText );
		
		//CommandType.WINDOW_IFRAME_OPEN_JOGL_CANVAS;
	}


	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
//		DInternalFrame refNewDInternalFrame = 
//			subCmdWindowNewInternalFrame.doCommand_getDInternalFrame();
//		
//		
////			refGeneralManager.getSingelton().getViewCanvasManager().createCanvas( 
////				ManagerObjectType.VIEW_JOGL_CANVAS_MULTIPLE, "" );
//		
//		if ( refGLEventListener != null ) {
//			refDSwingJoglCanvas =
//				new DSwingJoglCanvas( refGeneralManager, refGLEventListener, iSetCallingFrameId );
//		}
//		else {
//			GLEventListenerMultiSource GLEventListener = 
//				new GLEventListenerMultiSource();
//			
//			refDSwingJoglCanvas =
//				new DSwingJoglCanvas( refGeneralManager, GLEventListener, iSetCallingFrameId );
//		}
//		
//		refDSwingJoglCanvas.initGLCanvas( "JOGL sample " + Integer.valueOf( iCountCanvas ),
//				refNewDInternalFrame );
		
		iCountCanvas++;
		
		System.out.println("CmdWindowNewIFrameJoglCanvas: init JOGL");		
	}
//	
//	/**
//	 * Attention- Side Effect: Canvas is not initialized after creating the class!
//	 * Need to register canvas component to listener via
//	 * cerberus.net.dwt.swing.jogl.DSwingJoglCanvas#getGLEventListener() 
//	 * and cerberus.net.dwt.swing.jogl.listener.GLEventObserverListener#registerSource(GLEventListenerTarget)
//	 * 
//	 * Example see cerberus.command.window.CmdWindowNewIFrameJogleHistogram
//	 * 
//	 * @see cerberus.net.dwt.swing.jogl.DSwingJoglCanvas#getGLEventListener()
//	 * @see cerberus.net.dwt.swing.jogl.listener.GLEventObserverListener#registerSource(GLEventListenerTarget)
//	 * @see cerberus.command.window.CmdWindowNewIFrameJogleHistogram
//	 * 
//	 * @return canvas
//	 */
//	public DSwingJoglCanvas getGLCanvas() {		
//		return refDSwingJoglCanvas;
//	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
	}

}
