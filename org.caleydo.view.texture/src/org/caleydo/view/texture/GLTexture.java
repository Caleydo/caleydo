package org.caleydo.view.texture;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.eclipse.swt.widgets.Composite;

/**
 * Single OpenGL2 tissue view
 * 
 * @author Marc Streit
 */
public class GLTexture extends AGLView implements IDataDomainBasedView<IDataDomain> {

	public final static String VIEW_TYPE = "org.caleydo.view.texture";

	private String texturePath;
	private int experimentIndex;

	private IDataDomain dataDomain;

	private boolean updateTexture = false;

	private SelectionType currentSelectionType = SelectionType.NORMAL;

	private String info = "not set";

	/**
	 * Constructor.
	 */
	public GLTexture(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;

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

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		// if (detailLevel == EDetailLevel.VERY_LOW) {
		// return;
		// }

		// switch (ePickingType) {
		// case TISSUE_SELECTION:
		// switch (pickingMode) {
		// case MOUSE_OVER: {
		//
		// break;
		// }
		// }
		// }
	}

	public void setCurrentSelectionType(SelectionType currentSelectionType) {
		this.currentSelectionType = currentSelectionType;
	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTextureView serializedForm = new SerializedTextureView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
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

	public void setInfo(String info) {
		this.info = info;
	}

}