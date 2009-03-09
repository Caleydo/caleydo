package org.caleydo.rcp.action.toolbar;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.rcp.CmdExternalActionTrigger;
import org.caleydo.core.command.view.rcp.CmdExternalFlagSetter;
import org.caleydo.core.command.view.rcp.CmdExternalObjectSetter;
import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.core.command.view.rcp.EExternalObjectSetterType;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.action.Action;

public class AToolBarAction
	extends Action
{
	protected int iViewID;

	/**
	 * Constructor.
	 */
	public AToolBarAction(int iViewID)
	{
		this.iViewID = iViewID;
	}

	public final void triggerCmdExternalAction(EExternalActionType type)
	{

		CmdExternalActionTrigger tmpCmd = (CmdExternalActionTrigger) GeneralManager.get()
				.getCommandManager().createCommandByType(ECommandType.EXTERNAL_ACTION_TRIGGER);

		tmpCmd.setAttributes(iViewID, type);
		tmpCmd.doCommand();
	}

	public final void triggerCmdExternalFlagSetter(final boolean bFlag,
			EExternalFlagSetterType type)
	{

		CmdExternalFlagSetter tmpCmd = (CmdExternalFlagSetter) GeneralManager.get()
				.getCommandManager().createCommandByType(ECommandType.EXTERNAL_FLAG_SETTER);

		tmpCmd.setAttributes(iViewID, bFlag, type);
		tmpCmd.doCommand();
	}

	public final void triggerCmdExternalObjectSetter(final Object object,
			EExternalObjectSetterType type)
	{

		CmdExternalObjectSetter tmpCmd = (CmdExternalObjectSetter) GeneralManager.get()
				.getCommandManager().createCommandByType(ECommandType.EXTERNAL_OBJECT_SETTER);

		tmpCmd.setAttributes(iViewID, object, type);
		tmpCmd.doCommand();
	}
}
