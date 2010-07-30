package org.caleydo.view.template;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.template.renderstyle.TemplateRenderStyle;

/**
 * Sample GL view.
 * 
 * @author Marc Streit
 */

public class GLTemplate extends AGLView implements IViewCommandHandler,
		ISelectionUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.template";

	private TemplateRenderStyle renderStyle;
	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLTemplate(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = GLTemplate.VIEW_ID;
	}

	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TemplateRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;
	}

	@Override
	public void initLocal(GL gl) {

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
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
	public void displayLocal(GL gl) {

		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {

	}

	@Override
	public void display(GL gl) {

		// TODO: IMPLEMENT GL STUFF
		
		gl.glBegin(GL.GL_QUADS);
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

		return "Template Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "Template Caleydo View";

	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {

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
