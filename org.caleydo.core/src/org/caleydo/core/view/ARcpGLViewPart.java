/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for all RCP views that use OpenGL.
 *
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public abstract class ARcpGLViewPart extends CaleydoRCPViewPart {

	protected IGLCanvas glCanvas;
	protected MinimumSizeComposite minSizeComposite;

	/**
	 * Constructor.
	 */
	public ARcpGLViewPart() {
		super();
	}

	public ARcpGLViewPart(Class<? extends ASerializedView> serializedViewClass) {
		this();
		try {
			viewContext = JAXBContext.newInstance(serializedViewClass);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		minSizeComposite = new MinimumSizeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		minSizeComposite.setMinSize(0, 0);
		minSizeComposite.setExpandHorizontal(true);
		minSizeComposite.setExpandVertical(true);

		glCanvas = createGLCanvas(minSizeComposite);
		parentComposite = glCanvas.asComposite();
		ViewManager.get().registerGLCanvasToAnimator(glCanvas);
		minSizeComposite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// if (!PlatformUI.getWorkbench().isClosing())
				ViewManager.get().unregisterGLCanvas(glCanvas);
			}
		});

		minSizeComposite.setContent(parentComposite);

		view = createView();
	}

	@SuppressWarnings("unchecked")
	protected <T extends AView & IGLView> T createView() {
		return (T) view;
	}

	protected IGLCanvas createGLCanvas(Composite parent) {
		ViewManager viewManager = ViewManager.get();
		return viewManager.getCanvasFactory().create(createCapabilities(), parent);
	}

	protected GLCapabilities createCapabilities() {
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(profile);
		caps.setStencilBits(1);
		caps.setDoubleBuffered(true);
		return caps;
	}

	public void createPartControlGL() {
		GeneralManager.get().getViewManager().registerRCPView(this, view);

		addToolBarContent();
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
		view = null;
		minSizeComposite = null;
		// getGLView().destroy();
	}

	@Override
	public List<IView> getAllViews() {

		List<IView> views = new ArrayList<IView>();
		views.add(getGLView());
		if (getGLView() instanceof IGLRemoteRenderingView) {
			List<AGLView> renderedViews = ((IGLRemoteRenderingView) getGLView())
					.getRemoteRenderedViews();
			if (renderedViews != null) {
				for (AGLView view : renderedViews) {
					views.add(view);
				}
			}
		}

		return views;
	}

	public IGLView getGLView() {
		return (IGLView) view;
	}

	public IGLCanvas getGLCanvas() {
		return glCanvas;
	}

	/**
	 * Returns the rcp-ID of the view
	 *
	 * @return rcp-ID of the view
	 */
	public abstract String getViewGUIID();

	/** Returns a current serializable snapshot of the view */
	@Override
	public ASerializedView getSerializedView() {
		return getGLView().getSerializableRepresentation();
	}
}
