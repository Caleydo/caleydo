/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Single OpenGL2 tissue view
 *
 * @author Marc Streit
 */
public class GLTexture extends AGLView implements IDataDomainBasedView<IDataDomain> {
	public static String VIEW_TYPE = "org.caleydo.view.texture";

	public static String VIEW_NAME = "Texture";
	private String texturePath;
	private int experimentIndex;

	private IDataDomain dataDomain;

	private boolean updateTexture = false;

	private SelectionType currentSelectionType = SelectionType.NORMAL;

	/**
	 * Constructor.
	 */
	public GLTexture(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

	}

	@Override
	public void initLocal(final GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void init(final GL2 gl) {
	}

	@Override
	public void displayLocal(final GL2 gl) {

		pickingManager.handlePicking(this, gl);
		if (isDisplayListDirty) {
			// rebuildPathwayDisplayList(gl);
			isDisplayListDirty = false;
		}
		display(gl);
	}

	@Override
	public void displayRemote(final GL2 gl) {

		display(gl);
	}

	@Override
	public void display(final GL2 gl) {
		// processEvents();

		checkForHits(gl);
		renderScene(gl);
	}

	private void renderScene(final GL2 gl) {

		if (updateTexture)
			renewTextureInCache();

		float topMargin = 0.07f;

		// gl.glPushName(pickingManager.getPickingID(uniqueID,
		// EPickingType.TISSUE_SELECTION, experimentIndex));
		if (texturePath != null && !texturePath.isEmpty()) {

			try {
				textureManager.renderTexture(gl, texturePath,
						new Vec3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0),
						new Vec3f(viewFrustum.getRight(), viewFrustum.getBottom(), 0),
						new Vec3f(viewFrustum.getRight(), viewFrustum.getTop()
								- topMargin, 0), new Vec3f(viewFrustum.getLeft(),
 viewFrustum.getTop() - topMargin, 0), Color.WHITE);
			} catch (IllegalStateException e) {
				// Render nothing if texture does not exist
			}
		}
		// gl.glPopName();

		if (currentSelectionType != SelectionType.NORMAL) {
			gl.glColor3fv(currentSelectionType.getColor().getRGB(), 0);
			gl.glLineWidth(4);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
					viewFrustum.getBottom(), 0);
			gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
					viewFrustum.getTop() - viewFrustum.getBottom(), 0);
			gl.glVertex3f(viewFrustum.getLeft(),
					viewFrustum.getTop() - viewFrustum.getBottom(), 0);

			gl.glEnd();
		}

	}

	public void setCurrentSelectionType(SelectionType currentSelectionType) {
		this.currentSelectionType = currentSelectionType;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public void updateTexture() {
		updateTexture = true;
	}

	private void renewTextureInCache() {

		updateTexture = false;

		try {
			textureManager.renewTexture(texturePath);
		} catch (Exception e) {
			updateTexture = true;
			System.out.println("invalid!");
		}
	}

	public void setExperimentIndex(int experimentIndex) {
		this.experimentIndex = experimentIndex;
	}

	public int getExperimentIndex() {
		return experimentIndex;
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		// TODO Auto-generated method stub

	}
}
