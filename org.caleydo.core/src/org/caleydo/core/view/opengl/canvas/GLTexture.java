/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.serialize.ASerializedView;
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
	public GLTexture(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

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
								viewFrustum.getTop() - topMargin, 0), 1, 1, 1, 1);
			} catch (IllegalStateException e) {
				// Render nothing if texture does not exist
			}
		}
		// gl.glPopName();

		if (currentSelectionType != SelectionType.NORMAL) {
			gl.glColor3fv(currentSelectionType.getColor(), 0);
			gl.glLineWidth(4);
			gl.glBegin(GL2.GL_LINE_LOOP);
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