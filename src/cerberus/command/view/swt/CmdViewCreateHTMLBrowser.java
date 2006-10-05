package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Class implementes the command for creating a 
 * simple browser.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateHTMLBrowser 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateHTMLBrowser(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) {
		
		super(refGeneralManager, refParameterHandler);
		
		setAttributes(refParameterHandler);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		HTMLBrowserViewRep browserView = (HTMLBrowserViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_BROWSER,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				browserView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		browserView.readInAttributes(refParameterHandler);
		
		browserView.extractAttributes();
		browserView.retrieveGUIContainer();
		browserView.initView();
		browserView.drawView();
	}

	protected void setAttributes( final IParameterHandler refParameterHandler ) {
		
	}
	
	public void undoCommand() throws CerberusRuntimeException {

		// TODO Auto-generated method stub
	}
}
