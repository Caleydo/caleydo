package org.caleydo.core.view.opengl.canvas.remote.glyph;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.GL;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.GLCanvasGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphMouseListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLSharedObjects;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Rendering glyph views remotely.
 * 
 * @author Stefan Sauer
 * @author Marc Streit
 */

public class GLCanvasRemoteGlyph
	extends AGLEventListener
{

	private ArrayList<Integer> viewIDs_;

	private GlyphMouseListener mouseWheelListener_;

	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasRemoteGlyph(final int iGLCanvasID, 
			final String sLabel, final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);

		viewIDs_ = new ArrayList<Integer>();
		mouseWheelListener_ = new GlyphMouseListener(this, generalManager);

		// Unregister standard mouse wheel listener
		// parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized bucket mouse wheel listener
		// parentGLCanvas.addMouseWheelListener(mouseWheelListener_);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl
	 * .GL)
	 */
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
				throw new CaleydoRuntimeException("Cannot render canvas object which is null!");
			tmpCanvasUser.init(gl);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media
	 * .opengl.GL)
	 */
	public void initLocal(GL gl)
	{

		init(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media
	 * .opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer,
	 * org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener,
	 * org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{

		// not implemented for a remote view
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.
	 * media.opengl.GL)
	 */
	public void displayLocal(GL gl)
	{

		pickingManager.handlePicking(iUniqueID, gl, true);

		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax
	 * .media.opengl.GL)
	 */
	public void displayRemote(GL gl)
	{

		display(gl);
		checkForHits(gl);
		// pickingTriggerMouseAdapter.resetEvents();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media
	 * .opengl.GL)
	 */
	public void display(GL gl)
	{

		gl.glTranslatef(0f, 0f, -5f);

		// GLSharedObjects.drawAxis(gl);
		// GLSharedObjects.drawViewFrustum(gl, viewFrustum);

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

			GLSharedObjects.drawViewFrustum(gl, viewFrustum);
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

			if (tmpGLEventListener == this
					|| tmpGLEventListener.getClass() != GLCanvasGlyph.class)
				continue;

			int iViewID = ((AGLEventListener) tmpGLEventListener).getID();

			viewIDs_.add(iViewID);
		}

	}

	private void renderViewByID(final GL gl, final int iViewID)
	{

		AGLEventListener tmpCanvasUser = ((AGLEventListener) generalManager.getViewGLCanvasManager()
				.getItem(iViewID));

		if (tmpCanvasUser == null)
			throw new CaleydoRuntimeException("Cannot render canvas object which is null!");

		tmpCanvasUser.displayRemote(gl);

		// System.out.println(iViewID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo()
	{

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Type: Glyph Map");
		alInfo.add("GL: Showing test clinical data");
		return alInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo
	 * .core.manager.view.EPickingType,
	 * org.caleydo.core.manager.view.EPickingMode, int,
	 * org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

	}
}
