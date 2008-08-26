package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;

/**
 * Command for setting flags in org.caleydo.core from the RCP interface.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdExternalFlagSetter
	extends ACmdExternalAttributes
{

	private boolean bFlag = false;

	private EExternalFlagSetterType externalFlagSetterType;

	private int iViewId;

	/**
	 * Constructor.
	 */
	public CmdExternalFlagSetter(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{

		commandManager.runDoCommand(this);

		Object viewObject = generalManager.getViewGLCanvasManager().getGLEventListener(iViewId);

		if (viewObject instanceof GLPathway)
		{
			switch (externalFlagSetterType)
			{
				case PATHWAY_GENE_MAPPING:
					((GLPathway) viewObject).enableGeneMapping(bFlag);
					break;
				case PATHWAY_NEIGHBORHOOD:
					((GLPathway) viewObject).enableNeighborhood(bFlag);
					break;
				case PATHWAY_TEXTURES:
					((GLPathway) viewObject).enablePathwayTextures(bFlag);
					break;
			}
		}
		else if (viewObject instanceof GLParallelCoordinates)
		{
			switch (externalFlagSetterType)
			{
				case PARCOORDS_OCCLUSION_PREVENTION:
					((GLParallelCoordinates) viewObject).preventOcclusion(bFlag);
					break;
			}
		}
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}

	public void setAttributes(final int iViewId, final boolean bFlag,
			final EExternalFlagSetterType externalFlagSetterType)
	{
		this.bFlag = bFlag;
		this.externalFlagSetterType = externalFlagSetterType;
		this.iViewId = iViewId;
	}
}
