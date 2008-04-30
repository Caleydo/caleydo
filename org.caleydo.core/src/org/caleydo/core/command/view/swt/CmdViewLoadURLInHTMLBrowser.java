package org.caleydo.core.command.view.swt;

import java.util.Iterator;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.IViewRep;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;

/**
 * Class implementes the command for loading an URL 
 * in a simple browser.
 * As arguments the view ID of the target browser and
 * the target URL are handed over to the command.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewLoadURLInHTMLBrowser 
extends ACmdCreate_IdTargetLabelParentXY {
	
	protected int iTargetHTMLBrowserViewId = -1;
	
	protected String sTargetURL = "";
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewLoadURLInHTMLBrowser(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method retrieves the the browser ViewReps
	 * and sets the new URL.
	 * The URL is then automatically reloaded.
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		try {
			
			Iterator<IViewRep> iterHTMLBrowser =
				generalManager.getViewGLCanvasManager().
				getViewRepByType(ViewType.SWT_HTML_BROWSER).iterator();

			while (iterHTMLBrowser.hasNext())
			{
				((HTMLBrowserViewRep)iterHTMLBrowser.next()).setUrl(sTargetURL);
			}

		} catch (Exception e)
		{
//			generalManager.logMsg(
//					this.getClass().getSimpleName() + 
//					": doCommand(): Invalid view ID. Requested view is not a browser!",
//					LoggerType.MINOR_ERROR );
			
			e.printStackTrace();
		} 
				
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
	}
	
	public void setAttributes(String sTargetURL) {

		this.sTargetURL = sTargetURL;
	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}