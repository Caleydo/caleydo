package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
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
	public void doCommand()
	{

		commandManager.runDoCommand(this);

		Object viewObject = generalManager.getViewGLCanvasManager()
				.getGLEventListener(iViewId);

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
			GLParallelCoordinates parCoords = (GLParallelCoordinates) viewObject;
			switch (externalFlagSetterType)
			{
				case PARCOORDS_OCCLUSION_PREVENTION:
					parCoords.preventOcclusion(bFlag);
					break;
				case STORAGEBASED_CHANGE_ORIENTATION:
					parCoords.renderStorageAsPolyline(bFlag);
					break;
			}
		}
		else if (viewObject instanceof GLHeatMap)
		{
			GLHeatMap heatMap = (GLHeatMap) viewObject;
			switch (externalFlagSetterType)
			{
				case STORAGEBASED_CHANGE_ORIENTATION:
					heatMap.renderHorizontally(bFlag);
					break;
			}
		}
	}

	@Override
	public void undoCommand()
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
