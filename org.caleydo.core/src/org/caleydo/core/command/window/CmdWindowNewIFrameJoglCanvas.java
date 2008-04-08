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
import org.caleydo.core.command.base.ACommand;
//import org.caleydo.core.command.base.CmdHandleSetInterface;
import org.caleydo.core.command.window.CmdWindowNewInternalFrame;
////import org.caleydo.core.data.collection.ViewCanvas;
////import org.caleydo.core.command.window.CmdHandleSetBase;
////import org.caleydo.core.manager.BaseManagerType;
//import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglCanvas;
////import org.caleydo.core.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
////import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglHistogramCanvas;
//import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
//import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import org.caleydo.core.net.dwt.swing.mdi.DDesktopPane;
////import org.caleydo.core.net.dwt.swing.mdi.DInternalFrame;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

//import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventObserverListener;

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
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
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
//	 * org.caleydo.core.net.dwt.swing.jogl.DSwingJoglCanvas#getGLEventListener() 
//	 * and org.caleydo.core.net.dwt.swing.jogl.listener.GLEventObserverListener#registerSource(GLEventListenerTarget)
//	 * 
//	 * Example see org.caleydo.core.command.window.CmdWindowNewIFrameJogleHistogram
//	 * 
//	 * @see org.caleydo.core.net.dwt.swing.jogl.DSwingJoglCanvas#getGLEventListener()
//	 * @see org.caleydo.core.net.dwt.swing.jogl.listener.GLEventObserverListener#registerSource(GLEventListenerTarget)
//	 * @see org.caleydo.core.command.window.CmdWindowNewIFrameJogleHistogram
//	 * 
//	 * @return canvas
//	 */
//	public DSwingJoglCanvas getGLCanvas() {		
//		return refDSwingJoglCanvas;
//	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		
	}

}
