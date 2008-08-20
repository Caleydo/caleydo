package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;

/**
 * Class implements the command for the data exchanger view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateDataExchanger
	extends ACmdExternalAttributes
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateDataExchanger(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand()
	{

		// IViewManager viewManager = ((IViewManager) generalManager
		// .getManagerByObjectType(ManagerObjectType.VIEW));
		//		
		// DataExchangerViewRep dataExchangerView =
		// (DataExchangerViewRep)viewManager
		// .createView(ManagerObjectType.VIEW_SWT_DATA_EXCHANGER,
		// iUniqueID,
		// iParentContainerId,
		// sLabel);
		//		
		// viewManager.registerItem(
		// dataExchangerView,
		// iUniqueID);
		//
		// viewManager.addViewRep(dataExchangerView);
		//
		// dataExchangerView.setAttributes(iWidthX, iHeightY);
		// dataExchangerView.initView();
		// dataExchangerView.drawView();
		//		
		// commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}
}
