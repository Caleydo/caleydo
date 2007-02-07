/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.base;

import cerberus.manager.IGeneralManager;
import cerberus.command.window.CmdWindowNewInternalFrame;
import cerberus.data.collection.ISet;
//import cerberus.data.collection.ViewCanvas;
//import cerberus.manager.BaseManagerType;
//import cerberus.manager.singelton.SingeltonManager;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
//import cerberus.net.dwt.swing.mdi.DDesktopPane;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdHandleSet
extends ACommand
implements ICmdHandleSet {

	/**
	 * Reference to IGeneralManager set in constructor only.
	 */
	protected final IGeneralManager refGeneralManager;
	
	/**
	 * Reference to current ISet.
	 * 
	 * @see cerberus.command.base.ACmdHandleSet#setSet(ISet)
	 */
	protected ISet refCurrentSet;
	
	/**
	 * Header text shown in internal frame
	 */
	protected String sGui_TextHeader;   // = "";
	
	
	protected final int iTargetFrameId;
	
	protected CmdWindowNewInternalFrame subCmdNewIFrame; // = null;
	
//	protected String sGui_TextMessage = "";
	
	/**
	 * 
	 */
	protected ACmdHandleSet( final IGeneralManager setRefGeneralManager,
			final int iTargetFrameId,
			final String sHeaderText ) {
				
		super( setRefGeneralManager.getSingelton().getCommandManager() );
		
		refGeneralManager = setRefGeneralManager;
		this.iTargetFrameId = iTargetFrameId;
		
		assert refGeneralManager != null:"ACmdHandleSet() with null-pointer to general manager";
		
		subCmdNewIFrame= 
			new CmdWindowNewInternalFrame(refGeneralManager,
					iTargetFrameId,
					sHeaderText );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.base.ICmdHandleSet#setSet(cerberus.data.collection.ISet)
	 */
	public final void setSet( final ISet useSet) {
		refCurrentSet = useSet;
	}
	
	public final void setGuiTextHeader( final String sTextHeader ) {
		sGui_TextHeader = sTextHeader;
	}
	
//	public final void setGuiTextAll( final String sTextHeader, final String sTextMessage) {
//		sGui_TextHeader = sTextHeader;
//		sGui_TextMessage = sTextMessage;
//	}
//
//	public final DInternalFrame createDInternalFrame( final String sTargetFrameId,
//			final String sTextHeader ) {
//		SingeltonManager buf = refGeneralManager.getSingelton();
//		
//		DInternalFrame refNewViewCanvas = (DInternalFrame)
//			buf.getViewCanvasManager().createCanvas( ManagerObjectType.VIEW_NEW_IFRAME,
//				sTargetFrameId );
//		
//		return refNewViewCanvas;
//	}
}
