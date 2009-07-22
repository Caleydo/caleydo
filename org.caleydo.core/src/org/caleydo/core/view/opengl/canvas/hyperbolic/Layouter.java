package org.caleydo.core.view.opengl.canvas.hyperbolic;

import static org.caleydo.core.view.opengl.canvas.histogram.HistogramRenderStyle.SIDE_SPACING;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.canvas.hyperbolic.DefaultNode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;


/**
 * Base Class for different ways to display trees
 * 
 * @author Helmut Pichlhöfer
 */

public class Layouter {
	
	protected float fRightBorder;
	protected float fLeftBorder;
	protected float fWidth;
	protected float fHight;
	protected float fCenterX;
	protected float fCenterY;
	protected DefaultNode rootNode;
	
	
	public Layouter(GL gl, IViewFrustum frustum)
	{
		fRightBorder = frustum.getRight();
		fLeftBorder = frustum.getLeft();
		fWidth = frustum.getWidth();
		fHight = frustum.getHeight();
		fCenterX = fHight/2;
		fCenterY = fWidth/2;
		 
		
		gl.glVertex3f(fCenterX, fCenterY, 0.0f);
		
		
		
	}
	public void DrawLayout()
	{
		
	}

	public void UpdateLayouter()
	{
		DrawLayout();
		
	}

}
