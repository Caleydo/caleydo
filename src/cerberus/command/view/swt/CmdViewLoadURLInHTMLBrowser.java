package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
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
	 * @param refGeneralManager
	 */
	public CmdViewLoadURLInHTMLBrowser(final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager) {
		
		super(refGeneralManager, refCommandManager);
	}

	/**
	 * Method retrieves the target browser ViewRep by the given ID
	 * and sets the new URL.
	 * The URL is then automatically reloaded.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		try {
			
			HTMLBrowserViewRep refHTMLBrowserViewRep = 
				(HTMLBrowserViewRep)viewManager.getItem(iTargetHTMLBrowserViewId);

			refHTMLBrowserViewRep.setUrl(sTargetURL);

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
	
	public void setAttributes(int iTargetHTMLBrowserViewId, String sTargetURL) {
		
		this.iTargetHTMLBrowserViewId = iTargetHTMLBrowserViewId;
		this.sTargetURL = sTargetURL;
	}
	
	public void undoCommand() throws CerberusRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}