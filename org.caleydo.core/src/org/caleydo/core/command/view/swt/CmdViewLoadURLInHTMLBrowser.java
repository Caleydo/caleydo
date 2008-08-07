package org.caleydo.core.command.view.swt;

import java.util.Iterator;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;

/**
 * Class implements the command for loading an URL in a simple browser. As
 * arguments the view ID of the target browser and the target URL are handed
 * over to the command.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewLoadURLInHTMLBrowser
	extends ACmdCreate_IdTargetLabelParentXY
{

	protected int iTargetHTMLBrowserViewId = -1;

	protected String sTargetURL = "";

	/**
	 * Constructor
	 */
	public CmdViewLoadURLInHTMLBrowser(final CommandType cmdType)
	{
		super(cmdType);
	}

	/**
	 * Method retrieves the the browser ViewReps and sets the new URL. The URL
	 * is then automatically reloaded.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		try
		{

			Iterator<IView> iterHTMLBrowser = generalManager.getViewGLCanvasManager()
					.getViewRepByType(ViewType.SWT_HTML_BROWSER).iterator();

			while (iterHTMLBrowser.hasNext())
			{
				((HTMLBrowserViewRep) iterHTMLBrowser.next()).setUrl(sTargetURL);
			}

		}
		catch (Exception e)
		{
			// generalManager.logMsg(
			// this.getClass().getSimpleName() +
			//": doCommand(): Invalid view ID. Requested view is not a browser!",
			// LoggerType.MINOR_ERROR );

			e.printStackTrace();
		}

		commandManager.runDoCommand(this);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		assert parameterHandler != null : "ParameterHandler object is null!";

		super.setParameterHandler(parameterHandler);
	}

	public void setAttributes(String sTargetURL)
	{

		this.sTargetURL = sTargetURL;
	}

	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}