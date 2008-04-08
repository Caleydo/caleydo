package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;


/**
 * 
 * Command for triggering simple actions from the RCP interface that
 * are executed in org.caleydo.core.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdExternalActionTrigger 
extends ACmdCreate_IdTargetLabelAttrDetail {
	
	private EExternalActionType externalActionType;
	
	private int iViewId;
	
	public CmdExternalActionTrigger(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {

		refCommandManager.runDoCommand(this);
		
		try 
		{
			Object viewObject = generalManager.getSingelton().getViewGLCanvasManager()
				.getItem(iViewId);
		
			if (viewObject.getClass().equals(GLCanvasJukeboxPathway3D.class))
			{
				if (externalActionType.equals(EExternalActionType.PATHWAY_CLEAR_ALL))
				{
					((GLCanvasJukeboxPathway3D)viewObject).clearAllPathways();
				}
			}
		}
		catch ( Exception e )
		{
			throw new CaleydoRuntimeException("ERROR in CMD: " + e.toString(),
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
	
	public void setAttributes(final int iViewId,
			final EExternalActionType externalActionType) {
		
		this.externalActionType = externalActionType;
		this.iViewId = iViewId;
	}
}
