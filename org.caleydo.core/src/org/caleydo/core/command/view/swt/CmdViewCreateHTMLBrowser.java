package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.browser.EBrowserType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;

/**
 * Class implements the command for creating a simple browser.
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
	public CmdViewCreateHTMLBrowser(final CommandType cmdType)
	{
		super(cmdType);
	}

	/**
	 * Method creates a slider view, sets the attributes and calls the init and
	 * draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (iExternalID != -1)
		{
			iParentContainerId = 
				generalManager.getIDManager().getInternalFromExternalID(iParentContainerId);
		}
		
		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_BROWSER, iParentContainerId, sLabel);

		viewManager.registerItem(browserView);

		browserView.setAttributes(iWidthX, iHeightY, browserType);
		browserView.initView();
		browserView.drawView();
		
		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(browserView.getID(), iExternalID);
		}

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		String sBrowserType = parameterHandler.getValueString(CommandType.TAG_DETAIL
				.getXmlKey());

		if (!sBrowserType.equals(""))
			browserType = EBrowserType.valueOf(sBrowserType);
		else
			browserType = EBrowserType.GENERAL;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
