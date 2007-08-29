package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.browser.HTMLBrowserViewRep;

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
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabelParentXY#checkOpenGLSetting()
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
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel,
							iGLCanvasId,
							iGLEventListernerId);
		
		viewManager.registerItem(
				browserView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		browserView.setAttributes(iWidthX, iHeightY);
		browserView.initView();
		browserView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
	}
	
	public void undoCommand() throws GeneViewRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
