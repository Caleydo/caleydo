package org.caleydo.core.command.view.rcp;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EIconIDs;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
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
			// Pathway settings should apply to all pathways
			for (AGLEventListener glEventListener : generalManager.getViewGLCanvasManager()
					.getAllGLEventListeners())
			{
				if (!(glEventListener instanceof GLPathway))
					continue;

				switch (externalFlagSetterType)
				{
					case PATHWAY_GENE_MAPPING:
						((GLPathway) glEventListener).enableGeneMapping(bFlag);
						break;
					case PATHWAY_NEIGHBORHOOD:
						((GLPathway) glEventListener).enableNeighborhood(bFlag);
						break;
					case PATHWAY_TEXTURES:
						((GLPathway) glEventListener).enablePathwayTextures(bFlag);
						break;
				}
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
				case STORAGEBASED_CHANGE_ORIENTATION:
					((AStorageBasedView) viewObject).changeOrientation(bFlag);
					break;
				case STORAGEBASED_HEATMAP_IN_FOCUS:
					((GLHierarchicalHeatMap) viewObject).changeFocus(bFlag);
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
					// case STORAGEBASED_CHANGE_ORIENTATION:
					// parCoords.renderStorageAsPolyline(bFlag);
					// return;
			}
			return;
		}

		// if (viewObject instanceof GLHeatMap)
		// {
		// GLHeatMap heatMap = (GLHeatMap) viewObject;
		// switch (externalFlagSetterType)
		// {
		// case STORAGEBASED_CHANGE_ORIENTATION:
		// heatMap.renderHorizontally(bFlag);
		// return;
		// }
		// }

		if (viewObject instanceof GLGlyph)
		{
			GLGlyph glyphview = (GLGlyph) viewObject;
			switch (externalFlagSetterType)
			{
				case GLYPH_VIEWMODE_SCATTERLOT:
					glyphview.setPositionModel(EIconIDs.DISPLAY_SCATTERPLOT);
					return;
				case GLYPH_VIEWMODE_RECTANGLE:
					glyphview.setPositionModel(EIconIDs.DISPLAY_RECTANGLE);
					return;
				case GLYPH_VIEWMODE_CIRCLE:
					glyphview.setPositionModel(EIconIDs.DISPLAY_CIRCLE);
					return;
				case GLYPH_VIEWMODE_RANDOM:
					glyphview.setPositionModel(EIconIDs.DISPLAY_RANDOM);
					return;
				case GLYPH_VIEWMODE_PLUS:
					glyphview.setPositionModel(EIconIDs.DISPLAY_PLUS);
					return;
				case GLYPH_SELECTION:
					if (!bFlag)
						glyphview.resetSelection();
					else
						glyphview.removeUnselected();

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
