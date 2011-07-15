package org.caleydo.view.template;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.template.renderstyle.TemplateRenderStyle;

/**
 * Sample GL2 view.
 * 
 * @author Marc Streit
 */

public class GLTemplate extends AGLView implements IViewCommandHandler,
		ISelectionUpdateHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.template";

	private TemplateRenderStyle renderStyle;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLTemplate(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLTemplate.VIEW_TYPE;
	}

	@Override
	public void init(GL2 gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TemplateRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {

	}

	@Override
	public void display(GL2 gl) {

		// TODO: IMPLEMENT GL2 STUFF

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 1, 0);
		gl.glVertex3f(1, 1, 0);
		gl.glVertex3f(1, 0, 0);
		gl.glEnd();
		gl.glPopName();
	}

	@Override
	public String getShortInfo() {

		return "LayoutTemplate Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "LayoutTemplate Caleydo View";

	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {

		// TODO: Implement picking processing here!
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTemplateView serializedForm = new SerializedTemplateView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
