/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.window;

import cerberus.manager.GeneralManager;
import cerberus.command.base.CommandAbstractBase;
import cerberus.command.base.CmdHandleSetInterface;
import cerberus.data.collection.Set;
//import cerberus.data.collection.ViewCanvas;
//import cerberus.manager.BaseManagerType;
//import cerberus.manager.singelton.SingeltonManager;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;
//import cerberus.net.dwt.swing.mdi.DDesktopPane;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class CmdHandleSetBase
extends CommandAbstractBase
implements CmdHandleSetInterface {

	/**
	 * Reference to GeneralManager set in constructor only.
	 */
	protected final GeneralManager refGeneralManager;
	
	/**
	 * Reference to current Set.
	 * 
	 * @see cerberus.command.window.CmdHandleSetBase#setSet(Set)
	 */
	protected Set refCurrentSet;
	
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
	public CmdHandleSetBase( final GeneralManager setRefGeneralManager,
			final int iTargetFrameId,
			final String sHeaderText ) {
				
		refGeneralManager = setRefGeneralManager;
		this.iTargetFrameId = iTargetFrameId;
		
		assert refGeneralManager != null:"CmdHandleSetBase() with null-pointer to general manager";
		
		subCmdNewIFrame= 
			new CmdWindowNewInternalFrame(refGeneralManager,
					iTargetFrameId,
					sHeaderText );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.base.CmdHandleSetInterface#setSet(cerberus.data.collection.Set)
	 */
	public final void setSet( final Set useSet) {
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
