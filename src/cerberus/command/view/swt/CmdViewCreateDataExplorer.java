package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;

/**
 * Class implementes the command for creating a data explorer view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateDataExplorer 
extends ACmdCreate 
implements ICommand
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateDataExplorer(
			IGeneralManager refGeneralManager,
			final LinkedList<String> listAttributes)
	{
		super(refGeneralManager, listAttributes);
	}

	/**
	 * Method creates a data explorer view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException
	{
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		
		DataExplorerViewRep dataExplorerView = (DataExplorerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER,
							iCreatedObjectId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				dataExplorerView, 
				iCreatedObjectId, 
				ManagerObjectType.VIEW);
		
		dataExplorerView.setAttributes(refVecAttributes);
		dataExplorerView.extractAttributes();
		dataExplorerView.retrieveGUIContainer();
		dataExplorerView.initView();
		dataExplorerView.drawView();
	}
}
