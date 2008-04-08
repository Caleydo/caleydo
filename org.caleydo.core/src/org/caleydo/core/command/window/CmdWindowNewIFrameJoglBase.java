/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.command.window;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.command.base.ACommand;
//import org.caleydo.core.net.dwt.swing.jogl.DSwingJoglCanvas;
//import org.caleydo.core.net.dwt.swing.jogl.listener.GLEventForwardListener;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Base class for Jogl classes provinding a GL rendering contexst inside an internal frame.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class CmdWindowNewIFrameJoglBase 
extends ACommand {

	/**
	 * Reference to the sub command to create a new Jogl canvas.
	 * Is null if an existing frame is used instead of creating a new one
	 */
	protected final CmdWindowNewIFrameJoglCanvas refCmdWindowNewIFrameJoglCanvas;
		
	/**
	 * Define if a new frame shall be created each time the command is executed or
	 * if an existing frame shall be used!
	 */
	protected final boolean bEnableCreationOfNewIFrame;
	
	/**
	 * Reference to singelton.
	 */
	protected final IGeneralManager refGeneralManager;

	/**
	 * Current Jogl canvas.
	 * If initDSwingJoglCanvas == null this varaibel holds the reference 
	 * to the current GL canvas after calling
	 * method prometheus.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame() 
	 * which has to be done befor accessing the variable inside a doCommand() statement.
	 * 
	 * See prometheus.command.window.CmdWindowNewIFrameJoglHistogram#doCommand() as
	 * example for proper use of this abstract class.
	 * 
	 * Do not change this variable, it is suppose do be read only.
	 * 
	 * @see prometheus.command.window.CmdWindowNewIFrameJoglBase#doCommand_IFrame()
	 * @see prometheus.command.window.CmdWindowNewIFrameJoglHistogram#doCommand()
	 */
//	protected DSwingJoglCanvas initDSwingJoglCanvas;

	/**
	 * ISet the reference to the parent JComponent.
	 * 
	 * @param refGeneralManager reference to singelton
	 * @param iCallingFrameId Id of the calling frame
	 * @param refGLEventListener listener for GLEvents or null if listener shall be created
	 * @param initDSwingJoglCanvas reference to existing GLcanvas or null if canvas shall be created
	 */
	protected CmdWindowNewIFrameJoglBase( final IGeneralManager refGeneralManager,
			final int iCallingFrameId,
//			final GLEventForwardListener refGLEventListener,
//			final DSwingJoglCanvas initDSwingJoglCanvas,
			final String sHeaderText ) {

		super( -1, refGeneralManager, refGeneralManager.getSingelton().getCommandManager(), null);
		//super(iSetCmdCollectionId);
		
		assert refGeneralManager != null :"Can not handle null-pointer to DSwingJoglCanvas";
		
		this.refGeneralManager = refGeneralManager;
		
		bEnableCreationOfNewIFrame = true;
				
		refCmdWindowNewIFrameJoglCanvas = null;
		
//		this.initDSwingJoglCanvas = initDSwingJoglCanvas;
//		
//		if ( initDSwingJoglCanvas == null ) {
//			bEnableCreationOfNewIFrame = true;
//			refCmdWindowNewIFrameJoglCanvas = 
//				new CmdWindowNewIFrameJoglCanvas(refGeneralManager, 
//						refGLEventListener, 
//						iCallingFrameId,
//						sHeaderText );
//		}
//		else {
//			bEnableCreationOfNewIFrame = false;
//			
//			/// need to fit final statement!
//			refCmdWindowNewIFrameJoglCanvas = null;
//		}
	}

	/* (non-Javadoc)
	 * @see prometheus.command.ICommand#doCommand()
	 */
	protected void doCommand_IFrame() throws CaleydoRuntimeException {
		
//		if ( bEnableCreationOfNewIFrame ) {
//			refCmdWindowNewIFrameJoglCanvas.doCommand();
//			initDSwingJoglCanvas = refCmdWindowNewIFrameJoglCanvas.getGLCanvas();
//		}
	}

}
