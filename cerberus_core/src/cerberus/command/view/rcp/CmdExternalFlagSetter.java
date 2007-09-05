package cerberus.command.view.rcp;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;


/**
 * 
 * Command for turning on edges in pathway views.
 * 
 * @author Marc Streit
 *
 */
public class CmdExternalFlagSetter 
extends ACmdCreate_IdTargetLabelAttrDetail {

	private boolean bFlag = false;
	
	private EExternalFlagSetterType externalFlagSetterType;
	
	private int iViewId;
	
	public CmdExternalFlagSetter(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {

		refCommandManager.runDoCommand(this);
		
		Object viewObject = refGeneralManager.getSingelton().getViewGLCanvasManager()
			.getItem(iViewId);
		
		if (viewObject.getClass().getName().equals(
				cerberus.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D.class.getName()))
		{
			if (externalFlagSetterType.equals(EExternalFlagSetterType.PATHWAY_ENABLE_EDGE_RENDERING))
			{
				((GLCanvasJukeboxPathway3D)viewObject).enableEdgeRendering(bFlag);				
			}
			else if (externalFlagSetterType.equals(EExternalFlagSetterType.PATHWAY_ENABLE_GENE_MAPPING))
			{
				((GLCanvasJukeboxPathway3D)viewObject).enableGeneMapping(bFlag);
			}
			else if (externalFlagSetterType.equals(EExternalFlagSetterType.PATHWAY_ENABLE_TEXTURES))
			{
				((GLCanvasJukeboxPathway3D)viewObject).enablePathwayTextures(bFlag);
			}
			else if (externalFlagSetterType.equals(EExternalFlagSetterType.PATHWAY_ENABLE_NEIGHBORHOOD))
			{
				((GLCanvasJukeboxPathway3D)viewObject).enableNeighborhood(bFlag);
			}
			else if (externalFlagSetterType.equals(EExternalFlagSetterType.PATHWAY_ENABLE_IDENTICAL_NODE_HIGHLIGHTING))
			{
				((GLCanvasJukeboxPathway3D)viewObject).enableIdenticalNodeHighlighting(bFlag);
			}
			else if (externalFlagSetterType.equals(EExternalFlagSetterType.PATHWAY_ENABLE_ANNOTATION))
			{
				((GLCanvasJukeboxPathway3D)viewObject).enableAnnotation(bFlag);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
	
	public void setAttributes(final int iViewId,
			final boolean bFlag,
			final EExternalFlagSetterType externalFlagSetterType) {
		
		this.bFlag = bFlag;
		this.externalFlagSetterType = externalFlagSetterType;
		this.iViewId = iViewId;
	}
}
