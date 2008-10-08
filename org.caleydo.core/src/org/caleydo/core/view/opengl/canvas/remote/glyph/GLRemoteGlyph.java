package org.caleydo.core.view.opengl.canvas.remote.glyph;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.GL;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphMouseListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Rendering glyph views remotely.
 * 
 * @author Stefan Sauer
 * @author Marc Streit
 */

public class GLRemoteGlyph
	extends AGLEventListener
{

	private static final long serialVersionUID = 5300993249138796018L;

	private ArrayList<Integer> viewIDs_;

	private GlyphMouseListener mouseWheelListener_;

	/**
	 * Constructor.
	 * 
	 */
	public GLRemoteGlyph(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);
		viewType = EManagedObjectType.GL_GLYPH;
		viewIDs_ = new ArrayList<Integer>();
		mouseWheelListener_ = new GlyphMouseListener(this, generalManager);

		// Unregister standard mouse wheel listener
		// parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized bucket mouse wheel listener
		// parentGLCanvas.addMouseWheelListener(mouseWheelListener_);
	}

	@Override
	public void init(GL gl)
	{

		retrieveContainedViews(gl);

		Iterator<Integer> it = viewIDs_.iterator();
		int iViewId;

		while (it.hasNext())
		{
			iViewId = it.next();
			AGLEventListener tmpCanvasUser = ((AGLEventListener) generalManager
					.getViewGLCanvasManager().getItem(iViewId));

			if (tmpCanvasUser == null)
				throw new RuntimeException("Cannot render canvas object which is null!");
			tmpCanvasUser.init(gl);
		}
	}

	@Override
	public void initLocal(GL gl)
	{

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{

		// not implemented for a remote view
	}

	@Override
	public void displayLocal(GL gl)
	{

		pickingManager.handlePicking(iUniqueID, gl, true);

		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void displayRemote(GL gl)
	{

		display(gl);
		checkForHits(gl);
		// pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void display(GL gl)
	{

		gl.glTranslatef(0f, 0f, -5f);

		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		// Iterate over glyph views and set tranlation / rotation / scaleing

		Iterator<Integer> it = viewIDs_.iterator();
		int iViewId;

		ArrayList<Vec3f> scale = new ArrayList<Vec3f>();
		ArrayList<Vec3f> pos = new ArrayList<Vec3f>();

		scale.add(new Vec3f(2f, 2f, 2f));
		scale.add(new Vec3f(0.5f, 0.5f, 0.5f));
		scale.add(new Vec3f(1f, 1f, 1f));

		pos.add(new Vec3f(0f, -2f, 0f));
		pos.add(new Vec3f(1f, 2f, 0f));
		pos.add(new Vec3f(-4f, 0f, 0f));

		gl.glPushMatrix();

		int counter = 0;
		while (it.hasNext())
		{
			iViewId = it.next();

			gl.glTranslatef(pos.get(counter).get(0), pos.get(counter).get(1), pos.get(counter)
					.get(2));
			gl.glScalef(scale.get(counter).get(0), scale.get(counter).get(1), scale.get(
					counter).get(2));

			GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
			renderViewByID(gl, iViewId);

			++counter;
		}

		// gl.glTranslatef(-5f,0f,0f);

		gl.glPopMatrix();

		gl.glTranslatef(0f, 0f, 5f);
		// mouseWheelListener_.render();
	}

	private void retrieveContainedViews(final GL gl)
	{

		Iterator<AGLEventListener> iterGLEventListener = generalManager
				.getViewGLCanvasManager().getAllGLEventListeners().iterator();

		viewIDs_ = new ArrayList<Integer>();

		while (iterGLEventListener.hasNext())
		{
			AGLEventListener tmpGLEventListener = iterGLEventListener.next();

			if (tmpGLEventListener == this || tmpGLEventListener.getClass() != GLGlyph.class)
				continue;

			int iViewID = (tmpGLEventListener).getID();

			viewIDs_.add(iViewID);
		}

	}

	private void renderViewByID(final GL gl, final int iViewID)
	{

		AGLEventListener tmpCanvasUser = ((AGLEventListener) generalManager
				.getViewGLCanvasManager().getItem(iViewID));

		if (tmpCanvasUser == null)
			throw new RuntimeException("Cannot render canvas object which is null!");

		tmpCanvasUser.displayRemote(gl);

		// System.out.println(iViewID);
	}

	@Override
	public String getShortInfo()
	{
		return "Glyph Bucket";
	}

	@Override
	public String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Map");
		sInfoText.append("GL: Showing test clinical data");
		return sInfoText.toString();
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

	}

	@Override
	public void broadcastElements(ESelectionType type)
	{

	}
}
