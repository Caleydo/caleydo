package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentAttrOpenGL;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.jogl.gears.GearsViewRep;

/**
 * Class implements the command for creating a gears view.
 * 
 * @see org.caleydo.core.command.ICommand
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdViewCreateGears
	extends ACmdCreate_IdTargetLabelParentAttrOpenGL
{
	/**
	 * Constructor.
	 */
	public CmdViewCreateGears(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		GearsViewRep gearsView = (GearsViewRep) viewManager.createView(
				EManagedObjectType.VIEW_SWT_GEARS, iExternalID, iParentContainerId, sLabel);

		viewManager.registerItem(gearsView, iExternalID);

		gearsView.setAttributes(iWidthX, iHeightY);
		gearsView.initView();
		gearsView.drawView();

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
