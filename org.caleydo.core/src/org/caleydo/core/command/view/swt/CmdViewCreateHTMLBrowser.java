package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.view.swt.browser.EBrowserType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;

/**
 * Class implements the command for creating a simple browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateHTMLBrowser
	extends ACmdCreational<HTMLBrowserViewRep> {
	private EBrowserType browserType;

	/**
	 * Constructor.
	 */
	public CmdViewCreateHTMLBrowser(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1) {
			iParentContainerId = generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}

		HTMLBrowserViewRep browserView = null;

		if (browserType == EBrowserType.GENOME) {
			browserView =
				(HTMLBrowserViewRep) viewManager.createView(EManagedObjectType.VIEW_SWT_BROWSER_GENOME,
					iParentContainerId, sLabel);
		}
		else {
			browserView =
				(HTMLBrowserViewRep) viewManager.createView(EManagedObjectType.VIEW_SWT_BROWSER_GENERAL,
					iParentContainerId, sLabel);
		}

		viewManager.registerItem(browserView);

		browserView.initView();
		browserView.drawView();

		if (iExternalID != -1) {
			generalManager.getIDManager().mapInternalToExternalID(browserView.getID(), iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		String sBrowserType = sDetail;

		if (!sBrowserType.equals("")) {
			browserType = EBrowserType.valueOf(sBrowserType);
		}
		else {
			browserType = EBrowserType.GENERAL;
		}
	}

	public void setAttributes(EBrowserType browserType) {
		this.browserType = browserType;
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}
}
