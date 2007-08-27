package cerberus.command.view.swt;

import java.util.Iterator;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.IViewManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.gui.IViewRep;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

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
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
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
	public void doCommand() throws GeneViewRuntimeException {
		
		try {
			
			Iterator<IViewRep> iterHTMLBrowser =
				refGeneralManager.getSingelton().getViewGLCanvasManager().
				getViewRepByType(ViewType.SWT_HTML_BROWSER).iterator();

			while (iterHTMLBrowser.hasNext())
			{
				((HTMLBrowserViewRep)iterHTMLBrowser.next()).setUrl(sTargetURL);
			}

		} catch (Exception e)
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": doCommand(): Invalid view ID. Requested view is not a browser!",
					LoggerType.MINOR_ERROR );
			
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
	
	public void undoCommand() throws GeneViewRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}