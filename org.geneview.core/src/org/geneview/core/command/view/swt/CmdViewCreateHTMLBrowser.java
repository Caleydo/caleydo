package org.geneview.core.command.view.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.browser.EBrowserType;
import org.geneview.core.view.swt.browser.HTMLBrowserViewRep;

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
	 * @see org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY#checkOpenGLSetting()
	 */
	protected final void checkOpenGLSetting() {
		/** not openGL settings are required */	
	}
	
	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_BROWSER,
							iUniqueId, 
							iParentContainerId, 
							iGlForwarderId,
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
	
	public void undoCommand() throws GeneViewRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
