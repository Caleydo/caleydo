package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EIconIDs;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
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
					return;
				case PATHWAY_NEIGHBORHOOD:
					((GLPathway) viewObject).enableNeighborhood(bFlag);
					return;
				case PATHWAY_TEXTURES:
					((GLPathway) viewObject).enablePathwayTextures(bFlag);
					return;
			}
		
		}
		
		if (viewObject instanceof AStorageBasedView)
		{
			AStorageBasedView sbView = (AStorageBasedView) viewObject;
			switch (externalFlagSetterType)
			{
				case STORAGEBASED_USE_RANDOM_SAMPLING:
					sbView.useRandomSampling(bFlag);
					return;
				case STORAGEBASED_RENDER_CONTEXT:
					((AStorageBasedView) viewObject).renderContext(bFlag);
					break;
			}
		}
		
		if (viewObject instanceof GLParallelCoordinates)
		{
			GLParallelCoordinates parCoords = (GLParallelCoordinates) viewObject;
			switch (externalFlagSetterType)
			{
				case PARCOORDS_OCCLUSION_PREVENTION:
					parCoords.preventOcclusion(bFlag);
					return;
				case STORAGEBASED_CHANGE_ORIENTATION:
					parCoords.renderStorageAsPolyline(bFlag);
					return;
			}
			return;
		}
		
		if (viewObject instanceof GLHeatMap)
		{
			GLHeatMap heatMap = (GLHeatMap) viewObject;
			switch (externalFlagSetterType)
			{
				case STORAGEBASED_CHANGE_ORIENTATION:
					heatMap.renderHorizontally(bFlag);
					return;
			}
		}
		
		if(viewObject instanceof GLGlyph)
		{
			GLGlyph glyphview = (GLGlyph)viewObject;
			switch(externalFlagSetterType)
			{
				case GLYPH_VIEWMODE_SCATTERLOT:
					glyphview.setViewMode(EIconIDs.DISPLAY_SCATTERPLOT);
					return;
				case GLYPH_VIEWMODE_RECTANGLE:
					glyphview.setViewMode(EIconIDs.DISPLAY_RECTANGLE);
					return;
				case GLYPH_VIEWMODE_CIRCLE:
					glyphview.setViewMode(EIconIDs.DISPLAY_CIRCLE);
					return;
				case GLYPH_VIEWMODE_RANDOM:
					glyphview.setViewMode(EIconIDs.DISPLAY_RANDOM);
					return;
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
