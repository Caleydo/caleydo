package org.caleydo.rcp.view.rcp;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Shared object for all Caleydo RCP OpenGL views.
 * 
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public abstract class ARcpGLViewPart
	extends CaleydoRCPViewPart {

	protected Frame frameGL;
	protected GLCaleydoCanvas glCanvas;
	protected MinimumSizeComposite minSizeComposite;

	/**
	 * Constructor.
	 */
	public ARcpGLViewPart() {
		super();
	}

	protected void createGLCanvas() {
		CmdViewCreateRcpGLCanvas cmdCanvas =
			(CmdViewCreateRcpGLCanvas) GeneralManager.get().getCommandManager()
				.createCommandByType(ECommandType.CREATE_VIEW_RCP_GLCANVAS);
		cmdCanvas.setAttributes(-1, false, false, false);
		cmdCanvas.doCommand();

		glCanvas = cmdCanvas.getCreatedObject();
		glCanvas.setParentComposite(parentComposite);
	}

	@Override
	public void createPartControl(Composite parent) {
		minSizeComposite = new MinimumSizeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		// fillToolBar();
		parentComposite = new Composite(minSizeComposite, SWT.EMBEDDED);
		minSizeComposite.setContent(parentComposite);
		minSizeComposite.setMinSize(0, 0);
		minSizeComposite.setExpandHorizontal(true);
		minSizeComposite.setExpandVertical(true);
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
		getGLView().destroy();
	}

	@Override
	public List<IView> getAllViews() {

		// FIXXXME: rcp-view id is the same as the first gl-view-id, so
		// rcp-view-ids have to be omitted
		// List<Integer> ids = super.getAllViewIDs();

		List<IView> views = new ArrayList<IView>();
		views.add(getGLView());
		if (getGLView() instanceof IGLRemoteRenderingView) {
			for (AGLView view : ((IGLRemoteRenderingView) getGLView()).getRemoteRenderedViews()) {
				views.add(view);
			}
		}

		return views;
	}

	public AGLView getGLView() {
		return (AGLView) view;
	}

	public GLCaleydoCanvas getGLCanvas() {
		return glCanvas;
	}

	/**
	 * Returns the rcp-ID of the view
	 * 
	 * @return rcp-ID of the view
	 */
	public abstract String getViewGUIID();
}
