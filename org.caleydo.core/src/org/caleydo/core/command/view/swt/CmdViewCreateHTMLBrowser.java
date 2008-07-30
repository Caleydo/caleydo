package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.browser.EBrowserType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;

/**
 * Class implementes the command for creating a simple browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateHTMLBrowser
	extends ACmdCreate_IdTargetLabelParentXY
{

	private EBrowserType browserType;

	/**
	 * Constructor.
	 */
	public CmdViewCreateHTMLBrowser(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	/**
	 * Method creates a slider view, sets the attributes and calls the init and
	 * draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep) viewManager.createView(
				EManagerObjectType.VIEW_SWT_BROWSER, iUniqueId, iParentContainerId, sLabel);

		viewManager.registerItem(browserView, iUniqueId);

		browserView.setAttributes(iWidthX, iHeightY, browserType);
		browserView.initView();
		browserView.drawView();

		commandManager.runDoCommand(this);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);

		String sBrowserType = parameterHandler.getValueString(CommandQueueSaxType.TAG_DETAIL
				.getXmlKey());

		if (!sBrowserType.equals(""))
			browserType = EBrowserType.valueOf(sBrowserType);
		else
			browserType = EBrowserType.GENERAL;
	}

	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}
