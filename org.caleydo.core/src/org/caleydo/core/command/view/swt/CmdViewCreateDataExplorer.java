package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class implements the command for creating a data explorer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateDataExplorer
	extends ACmdExternalAttributes
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateDataExplorer(final CommandType cmdType)
	{
		super(cmdType);
	}

	/**
	 * Method creates a data explorer view, sets the attributes and calls the
	 * init and draw method.
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		// IViewManager viewManager = ((IViewManager) generalManager
		// .getManagerByObjectType(ManagerObjectType.VIEW));
		//		
		// DataExplorerViewRep dataExplorerView =
		// (DataExplorerViewRep)viewManager
		// .createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER,
		// iUniqueID,
		// iParentContainerId,
		// sLabel);
		//		
		// viewManager.registerItem(
		// dataExplorerView,
		// iUniqueID);
		//		
		// viewManager.addViewRep(dataExplorerView);
		//		
		// dataExplorerView.setAttributes(iWidthX, iHeightY);
		// dataExplorerView.initView();
		// dataExplorerView.drawView();
		//		
		// commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
