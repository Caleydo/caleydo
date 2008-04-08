/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.base;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.command.window.CmdWindowNewInternalFrame;
import org.geneview.core.data.collection.ISet;
//import org.geneview.core.data.collection.ViewCanvas;
//import org.geneview.core.manager.BaseManagerType;
//import org.geneview.core.manager.singelton.SingeltonManager;
//import org.geneview.core.net.dwt.swing.mdi.DInternalFrame;
//import org.geneview.core.net.dwt.swing.mdi.DDesktopPane;


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
	 * @see org.geneview.core.command.base.ACmdHandleSet#setSet(ISet)
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
				
		super( -1,
				setRefGeneralManager,
				setRefGeneralManager.getSingelton().getCommandManager(),
				null);
		
		refGeneralManager = setRefGeneralManager;
		this.iTargetFrameId = iTargetFrameId;
		
		assert refGeneralManager != null:"ACmdHandleSet() with null-pointer to general manager";
		
		subCmdNewIFrame= 
			new CmdWindowNewInternalFrame(refGeneralManager,
					iTargetFrameId,
					sHeaderText );
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.command.base.ICmdHandleSet#setSet(org.geneview.core.data.collection.ISet)
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
