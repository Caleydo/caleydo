package org.caleydo.view.visbricks;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.GeneralManager;
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
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.renderstyle.VisBricksRenderStyle;

/**
 * VisBricks main view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLVisBricks extends AGLView implements IGLRemoteRenderingView,
		IViewCommandHandler, ISelectionUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.visbricks";

	private VisBricksRenderStyle renderStyle;

	GLBrick brick;
	ArrayList<AGLView> brickList;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLVisBricks(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);
		brickList = new ArrayList<AGLView>(20);
		viewType = GLVisBricks.VIEW_ID;
		ViewFrustum brickFrustum = new ViewFrustum(viewFrustum.getProjectionMode(), 0, 3,
				0, 3, -4, 4);
		brick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
				.createGLView(GLBrick.class, glCanvas, brickFrustum);
		brickList.add(brick);

		brick.setRemoteRenderingGLView(this);
		brick.setFrustum(brickFrustum);
	}

	@Override
	public void init(GL2 gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new VisBricksRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

	}

	@Override
	public void initLocal(GL2 gl) {

	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

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

		brick.display(gl);
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
		gl.glColor3f(1, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 1, 0);
		gl.glVertex3f(1, 1, 0);
		gl.glVertex3f(1, 0, 0);
		gl.glEnd();
		gl.glPopName();
	}

	@Override
	public String getShortInfo() {

		return "Template Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "Template Caleydo View";

	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {

		// TODO: Implement picking processing here!
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedVisBricksView serializedForm = new SerializedVisBricksView();
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

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return brickList;
	}
}
