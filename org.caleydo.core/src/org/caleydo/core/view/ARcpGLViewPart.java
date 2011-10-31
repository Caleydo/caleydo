package org.caleydo.core.view;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Shared object for all Caleydo RCP OpenGL2 views.
 * 
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public abstract class ARcpGLViewPart
	extends CaleydoRCPViewPart {

	protected GLCanvas glCanvas;
	protected MinimumSizeComposite minSizeComposite;

	/**
	 * Constructor.
	 */
	public ARcpGLViewPart() {
		super();
	}

	protected void createGLCanvas() {
		GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		glCapabilities.setStencilBits(1);

		glCanvas = new GLCanvas(glCapabilities);
	}

	@Override
	public void createPartControl(Composite parent) {
		minSizeComposite = new MinimumSizeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		parentComposite = new Composite(minSizeComposite, SWT.EMBEDDED);
		minSizeComposite.setContent(parentComposite);
		minSizeComposite.setMinSize(0, 0);
		minSizeComposite.setExpandHorizontal(true);
		minSizeComposite.setExpandVertical(true);
	}
	
	protected void initializeData()
	{
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					((ASerializedTopLevelDataView) serializedView).getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
	}

	public void createPartControlGL() {

		Frame frameGL = SWT_AWT.new_Frame(parentComposite);
		frameGL.add(glCanvas);
		
		GeneralManager.get().getViewManager().registerRCPView(this, view);
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
		GeneralManager.get().getViewManager().unregisterRCPView(this, view);
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
			List<AGLView> renderedViews = ((IGLRemoteRenderingView) getGLView()).getRemoteRenderedViews();
			if (renderedViews != null) {
				for (AGLView view : renderedViews) {
					views.add(view);
				}
			}
		}

		return views;
	}

	public AGLView getGLView() {
		return (AGLView) view;
	}

	public GLCanvas getGLCanvas() {
		return glCanvas;
	}

	/**
	 * Returns the rcp-ID of the view
	 * 
	 * @return rcp-ID of the view
	 */
	public abstract String getViewGUIID();
}
