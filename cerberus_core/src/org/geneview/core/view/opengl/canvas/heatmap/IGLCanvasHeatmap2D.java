package org.geneview.core.view.opengl.canvas.heatmap;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

/**
 * Interface for accessing Heatmap from a Command.
 * 
 * @see cerberus.command.view.opengl.CmdGlObjectHeatmap2D#doCommand()
 * @see cerberus.command.view.opengl.CmdGlObjectHeatmap2D#undoCommand()
 * 
 * @author Michael Kalkusch
 *
 */
public interface IGLCanvasHeatmap2D {

	public void setResolution(float[] setResolution);

	public void setTargetSetId(final int iTargetCollectionSetId);

	public void setSelectionItems(int[] selectionStartAtIndexX,
			int[] selectionLengthX, int[] selectionStartAtIndexY,
			int[] selectionLengthY);

	public void setOriginRotation( final Vec3f origin,	
			final Rotf rotation );
	
	public void destroyGLCanvas();
	
}