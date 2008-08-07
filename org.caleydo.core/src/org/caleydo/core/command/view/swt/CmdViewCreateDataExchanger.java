package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;

/**
 * Class implements the command for the data exchanger view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateDataExchanger
	extends ACmdCreate_IdTargetLabelParentXY
{

	/**
	 * Constructor.
	 */
	public CmdViewCreateDataExchanger(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}
}
