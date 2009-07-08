package org.caleydo.rcp.view.opengl;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.view.CaleydoViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Shared object for all Caleydo RCP OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AGLViewPart
	extends CaleydoViewPart {
	protected Frame frameGL;
	protected GLCaleydoCanvas glCanvas;
	protected AGLEventListener glEventListener;

	/**
	 * Contains view IDs for initially contained remote rendered views
	 */
	protected ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public AGLViewPart() {
		super();
	}

	protected void createGLCanvas() {
		CmdViewCreateRcpGLCanvas cmdCanvas =
			(CmdViewCreateRcpGLCanvas) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.CREATE_VIEW_RCP_GLCANVAS);
		cmdCanvas.setAttributes(-1, false, false, false);
		cmdCanvas.doCommand();

		glCanvas = cmdCanvas.getCreatedObject();
		glCanvas.setParentComposite(parentComposite);
	}

	/**
	 * This class creates the GL event listener contained in a RCP view for a RCP view.
	 * 
	 * @param glViewType
	 *            The type of view. See {@link ECommandType}
	 * @param iParentCanvasID
	 *            the id of canvas where you want to render
	 * @param bRegisterToOverallMediator
	 *            true if you want this to listen and send to main mediator
	 * @return the ID of the view
	 */
	protected AGLEventListener createGLEventListener(ECommandType glViewType, int iParentCanvasID,
		boolean bRegisterToOverallMediator) {
		IGeneralManager generalManager = GeneralManager.get();

		CmdCreateGLEventListener cmdView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(glViewType);
	
		IUseCase useCase;
		ISet set;
		
		if (glViewType == ECommandType.CREATE_GL_BUCKET_3D) {
			
			useCase = GeneralManager.get().getUseCase();
			set = useCase.getSet();
			
			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 1.9f, 100, set,
//			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -2f, 2f, -2f, 2f, 3.82f, 100, set,
				iParentCanvasID, 0, 0, -8, 0, 0, 0, 0);
		}
		else if (glViewType == ECommandType.CREATE_GL_GLYPH) {
			
			useCase = GeneralManager.get().getClinicalUseCase();
			set = useCase.getSet();
			
			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 2.9f, 100, set,
				iParentCanvasID, 0, 0, -8, 0, 0, 0, 0);
		}
		else {
			useCase = GeneralManager.get().getUseCase();
			set = useCase.getSet();
			
			cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, set, iParentCanvasID);
		}

		cmdView.doCommand();

		AGLEventListener glView = cmdView.getCreatedObject();

		if (iAlContainedViewIDs != null && glViewType == ECommandType.CREATE_GL_BUCKET_3D) {
			((GLRemoteRendering) glView).setInitialContainedViews(iAlContainedViewIDs);
		}

		setGLData(glCanvas, glView);
		createPartControlGL();

		glView.setUseCase(useCase);
		glView.setSet(set);
				
		useCase.addView(glView);
		
		return glView;
	}

	protected AGLEventListener createGLRemoteEventListener(ECommandType glViewType, int iParentCanvasID,
		boolean bRegisterToOverallMediator, ArrayList<Integer> iAlContainedViewIDs) {
		this.iAlContainedViewIDs = iAlContainedViewIDs;
		return createGLEventListener(glViewType, iParentCanvasID, bRegisterToOverallMediator);
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.EMBEDDED);
		// fillToolBar();
	}

	public void setGLData(final GLCaleydoCanvas glCanvas, final AGLEventListener glEventListener) {
		this.glCanvas = glCanvas;
		this.glEventListener = glEventListener;
		this.iViewID = glEventListener.getID();
	}

	public void createPartControlGL() {
		if (frameGL == null) {
			frameGL = SWT_AWT.new_Frame(parentComposite);
		}

		frameGL.add(glCanvas);
	}

	@Override
	public void setFocus() {
		// final IToolBarManager toolBarManager =
		// getViewSite().getActionBars().getToolBarManager();
		// toolBarManager.add(ActionFactory.QUIT.create(this.getViewSite().getWorkbenchWindow()));
		// toolBarManager.update(true);
	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getUseCase().removeView(glEventListener);
		GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID).destroy();
	}

	@Override
	public List<Integer> getAllViewIDs() {

		// FIXXXME: rcp-view id is the same as the first gl-view-id, so rcp-view-ids have to be omitted
		// List<Integer> ids = super.getAllViewIDs();

		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(this.getGLEventListener().getAllViewIDs());
		return ids;
	}

	public AGLEventListener getGLEventListener() {
		return glEventListener;
	}

	public GLCaleydoCanvas getGLCanvas() {
		return glCanvas;
	}
}
