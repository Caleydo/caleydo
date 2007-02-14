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
import cerberus.command.base.ACommand;
//import cerberus.command.base.CmdHandleSetInterface;
import cerberus.command.window.CmdWindowNewInternalFrame;
////import cerberus.data.collection.ViewCanvas;
////import cerberus.command.window.CmdHandleSetBase;
////import cerberus.manager.BaseManagerType;
//import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
////import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
////import cerberus.net.dwt.swing.jogl.DSwingJoglHistogramCanvas;
//import cerberus.net.dwt.swing.jogl.listener.GLEventForwardListener;
////import cerberus.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
//import cerberus.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
////import cerberus.net.dwt.swing.mdi.DDesktopPane;
////import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.util.exception.CerberusRuntimeException;

//import cerberus.net.dwt.swing.jogl.listener.GLEventObserverListener;

/**
 * Creates a internal frame dispaying a 2D histogram.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdWindowNewIFrameJoglCanvas 
extends ACommand //ACmdHandleSet 
implements ICommand {

	private int iCountCanvas = 1;
		
	private final IGeneralManager refGeneralManager;
	
//	private DSwingJoglCanvas refDSwingJoglCanvas = null;
	
	private CmdWindowNewInternalFrame subCmdWindowNewInternalFrame;
	
//	private final GLEventForwardListener refGLEventListener;
	
	private int iSetCallingFrameId;
	
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
				refGeneralManager.getSingelton().getCommandManager());
		
		this.refGeneralManager = refGeneralManager;
		
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
	public void doCommand() throws CerberusRuntimeException {
		
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
		
		System.out.println("init JOGL");		
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
	public void undoCommand() throws CerberusRuntimeException {
		
	}

}
