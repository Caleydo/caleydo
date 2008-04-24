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
 * Command for setting flags in org.caleydo.core from the RCP interface.
 * 
 * @author Michael Kalkusch
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
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {

		refCommandManager.runDoCommand(this);
		
		Object viewObject = generalManager.getSingleton().getViewGLCanvasManager()
			.getItem(iViewId);
		
		if (viewObject.getClass().equals(GLCanvasJukeboxPathway3D.class))
		{
			switch ( externalFlagSetterType ) 
			{
			case PATHWAY_ENABLE_GENE_MAPPING: 
				((GLCanvasJukeboxPathway3D)viewObject).enableGeneMapping(bFlag);
				break;
			case PATHWAY_ENABLE_ANNOTATION:
				((GLCanvasJukeboxPathway3D)viewObject).enableAnnotation(bFlag);
				break;
			case PATHWAY_ENABLE_IDENTICAL_NODE_HIGHLIGHTING:
				((GLCanvasJukeboxPathway3D)viewObject).enableIdenticalNodeHighlighting(bFlag);			
				break;
			case PATHWAY_ENABLE_NEIGHBORHOOD:			
				((GLCanvasJukeboxPathway3D)viewObject).enableNeighborhood(bFlag);
				break;
			case PATHWAY_ENABLE_TEXTURES:			
				((GLCanvasJukeboxPathway3D)viewObject).enablePathwayTextures(bFlag);
				break;
				
				default:
					throw new CaleydoRuntimeException("unsupported EExternalFlagSetterType",
							CaleydoRuntimeExceptionType.DATAHANDLING);
			}
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
			final boolean bFlag,
			final EExternalFlagSetterType externalFlagSetterType) {
		
		this.bFlag = bFlag;
		this.externalFlagSetterType = externalFlagSetterType;
		this.iViewId = iViewId;
	}
}
