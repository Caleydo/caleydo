package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.browser.EBrowserType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;

/**
 * Class implementes the command for creating a 
 * simple browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateHTMLBrowser 
extends ACmdCreate_IdTargetLabelParentXY {
	
	private EBrowserType browserType;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdViewCreateHTMLBrowser(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY#checkOpenGLSetting()
	 */
	protected final void checkOpenGLSetting() {
		/** not openGL settings are required */	
	}
	
	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_BROWSER,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				browserView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		browserView.setAttributes(iWidthX, iHeightY, browserType);
		browserView.initView();
		browserView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		String sBrowserType = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey());
				
		if(!sBrowserType.equals(""))
			browserType = EBrowserType.valueOf(sBrowserType);	
		else
			browserType = EBrowserType.GENERAL;
	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
