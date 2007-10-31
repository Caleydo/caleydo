package org.geneview.core.command.view.rcp;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;


/**
 * 
 * Command for triggering simple actions from the RCP interface that
 * are executed in org.geneview.core.
 * 
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
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		refCommandManager.runDoCommand(this);
		
		Object viewObject = refGeneralManager.getSingelton().getViewGLCanvasManager()
			.getItem(iViewId);
		
		if (viewObject.getClass().equals(GLCanvasJukeboxPathway3D.class))
		{
			if (externalActionType.equals(EExternalActionType.PATHWAY_CLEAR_ALL))
			{
				((GLCanvasJukeboxPathway3D)viewObject).clearAllPathways();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
	
	public void setAttributes(final int iViewId,
			final EExternalActionType externalActionType) {
		
		this.externalActionType = externalActionType;
		this.iViewId = iViewId;
	}
}
