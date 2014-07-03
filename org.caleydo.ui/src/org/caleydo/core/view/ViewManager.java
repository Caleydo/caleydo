/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.NewViewEvent;
import org.caleydo.core.event.view.ViewClosedEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.internal.MyPreferences;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.MyAnimator;
import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.awt.AWTGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.newt.NEWTGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.swt.SWTGLCanvasFactory;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Manage all canvas, view and canvas objects.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ViewManager {
	private final Map<Integer, IView> views = new HashMap<Integer, IView>();

	private final Map<Integer, IView> hashGLViewID2View = new HashMap<>();

	private final Map<CaleydoRCPViewPart, IView> hashRCP2View = new HashMap<>();

	private final Map<IView, CaleydoRCPViewPart> hashView2RCP = new HashMap<>();

	/**
	 * Map that maps from a remote rendering view to a list of its remote rendered views.
	 */
	private Map<AGLView, Set<AGLView>> hashRemoteRenderingView2RemoteRenderedViews = new HashMap<AGLView, Set<AGLView>>();

	/**
	 * Map that maps from a top level remote rendering view to remote rendering views that need to be destroyed. These
	 * views shall be destroyed via {@link #executePendingRemoteViewDestruction(GL2, AGLView)} in a display cycle.
	 */
	// private Map<AGLView, Set<AGLView>> hashTopLevelView2ViewsToBeDestroyed = new HashMap<AGLView, Set<AGLView>>();

	private GLAnimatorControl fpsAnimator;

	private PickingManager pickingManager = new PickingManager();

	private Set<Object> busyRequests = new HashSet<Object>();

	private Set<IGLCanvas> registeredGLCanvas = Sets.newHashSet();

	/**
	 * Determines whether the views that were deserialized have already been initialized. Views do not get initialized
	 * by default when they are not visible.
	 */
	private boolean areSerializedViewsInitialized = false;

	private final IGLCanvasFactory canvasFactory;

	/**
	 * chooses the implementation for the jogl canvas, possible values are: awt, swt (default) and newt
	 */
	{
		String kind = System.getProperty("org.caleydo.opengl", "swt");
		if ("awt".equalsIgnoreCase(kind)) {
			canvasFactory = new AWTGLCanvasFactory();
		} else if ("swt".equalsIgnoreCase(kind)) {
			canvasFactory = new SWTGLCanvasFactory();
		} else if ("newt".equalsIgnoreCase(kind)) {
			canvasFactory = new NEWTGLCanvasFactory();
		} else {
			throw new IllegalStateException("unknown opengl implementation: " + kind);
		}
	}

	private volatile static ViewManager instance;

	public static ViewManager get() {
		if (instance == null) {
			synchronized (ViewManager.class) {
				if (instance == null) {
					instance = new ViewManager();
				}
			}
		}
		return instance;
	}

	public IGLCanvasFactory getCanvasFactory() {
		return canvasFactory;
	}

	public boolean contains(int iItemId) {
		if (this.views.containsKey(iItemId))
			return true;

		if (hashGLViewID2View.containsKey(iItemId))
			return true;

		return false;
	}

	public AGLView getGLView(int iItemID) {
		IView view = hashGLViewID2View.get(iItemID);
		if (view instanceof AGLView)
			return (AGLView) view;
		return null;
	}

	public boolean unregisterGLCanvas(final IGLCanvas glCanvas) {

		if (glCanvas == null)
			return false;

		if (!registeredGLCanvas.contains(glCanvas))
			return true;

		registeredGLCanvas.remove(glCanvas);

		if (registeredGLCanvas.isEmpty())
			fpsAnimator.stop();
		fpsAnimator.remove(glCanvas.asGLAutoDrawAble());

		return true;
	}

	/**
	 * Registers the specified {@link AGLView}.
	 *
	 * @param vuew
	 * @param assignInstanceNumber
	 *            If true, a number that is unique among all instances of the class of the specified view is assigned to
	 *            the view. Otherwise -1 is assigned.
	 */
	public void registerView(IView view, boolean assignInstanceNumber) {
		int instanceNumber = 0;

		if (assignInstanceNumber) {
			Class<? extends IView> viewClass = view.getClass();

			// Extract instance numbers from views of the same class that have a
			// unique instance number
			List<Integer> existingInstanceNumbers = new ArrayList<Integer>();
			for (IView eview : hashGLViewID2View.values()) {
				if (eview.getClass().equals(viewClass)) {
					if (eview.getInstanceNumber() != -1) {
						existingInstanceNumbers.add(eview.getInstanceNumber());
					}
				}
			}

			// Pick lowest available positive number
			Collections.sort(existingInstanceNumbers);
			for (int number : existingInstanceNumbers) {
				if (number == instanceNumber) {
					instanceNumber++;
				}
			}

			view.setInstanceNumber(instanceNumber);
		} else {
			view.setInstanceNumber(-1);
		}

		hashGLViewID2View.put(view.getID(), view);
		Logger.log(new Status(IStatus.INFO, this.toString(), "Registering view: " + view));
		NewViewEvent event = new NewViewEvent(view);
		event.setSender(this);
		EventPublisher.trigger(event);
	}

	/**
	 * Registers the dependency between a remote rendering and a remote rendered view.
	 *
	 * @param remoteRenderedView
	 *            The remote rendered view.
	 * @param remoteRenderingView
	 *            The view that renders the remoteRenderedView.
	 */
	public void registerRemoteRenderedView(AGLView remoteRenderedView, AGLView remoteRenderingView) {

		Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews.get(remoteRenderingView);

		if (remoteRenderedViews == null) {
			remoteRenderedViews = new HashSet<AGLView>();
			hashRemoteRenderingView2RemoteRenderedViews.put(remoteRenderingView, remoteRenderedViews);
		}
		remoteRenderedViews.add(remoteRenderedView);
	}

	/**
	 * Destroys all remote views of topLevelRemoteRenderingView that have previously been unregistered via
	 * {@link #unregisterGLView(AGLView)}.
	 *
	 * @param gl
	 * @param topLevelRemoteRenderingView
	 */
	// public void executePendingRemoteViewDestruction(GL2 gl, AGLView topLevelRemoteRenderingView) {
	//
	// Set<AGLView> viewsToBeDestroyed = hashTopLevelView2ViewsToBeDestroyed.get(topLevelRemoteRenderingView);
	//
	// if (viewsToBeDestroyed != null) {
	// for (AGLView view : viewsToBeDestroyed) {
	// view.destroy(gl);
	// }
	// viewsToBeDestroyed.clear();
	// }
	// }

	// /**
	// * Destroys the specified view and all of its remote rendered views.
	// *
	// * @param gl
	// * @param remoteRenderingView
	// */
	// public void destroyTopLevelGLView(GL2 gl, AGLView remoteRenderingView) {
	//
	// destroyRemoteViews(gl, remoteRenderingView);
	//
	// remoteRenderingView.destroy(gl);
	// }

	/**
	 * Recursively destroys all remote views of the specified view.
	 *
	 * @param gl
	 * @param remoteRenderingView
	 */
	// public void destroyRemoteViews(GL2 gl, AGLView remoteRenderingView) {
	// Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews.get(remoteRenderingView);
	//
	// if (remoteRenderedViews != null) {
	// Set<AGLView> tempRemoteRenderedViews = new HashSet<AGLView>(remoteRenderedViews);
	// for (AGLView remoteRenderedView : tempRemoteRenderedViews) {
	// destroyRemoteViews(gl, remoteRenderedView);
	// unregisterGLView(remoteRenderedView, false);
	// remoteRenderedView.destroy(gl);
	// }
	// }
	// }

	/**
	 * Destroys and unregisters a view and all its remote rendered views.
	 *
	 * @param gl
	 * @param view
	 *            The view to be destroyed.
	 */
	public void destroyView(GL2 gl, AGLView view) {
		if (view == null)
			return;

		IGLCanvas parentGLCanvas = view.getParentGLCanvas();

		if (parentGLCanvas != null) {
			parentGLCanvas.removeGLEventListener(view);
		}

		AGLView parentView = (AGLView) view.getRemoteRenderingGLView();

		// Remove this view from the parent's remote rendering list
		if (parentView != null) {
			Set<AGLView> parentRemoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews.get(parentView);
			if (parentRemoteRenderedViews != null)
				parentRemoteRenderedViews.remove(view);
		}

		Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews.get(view);

		if (remoteRenderedViews != null) {
			Set<AGLView> tempRemoteRenderedViews = new HashSet<AGLView>(remoteRenderedViews);
			for (AGLView remoteRenderedView : tempRemoteRenderedViews) {
				destroyView(gl, remoteRenderedView);
				// unregisterGLView(remoteRenderedView, false);
				// remoteRenderedView.destroy(gl);
			}
		}

		view.destroy(gl);

		AGLView topLevelGLView = view.getTopLevelGLView();
		if (topLevelGLView == view) {
			unregisterGLCanvas(view.getParentGLCanvas());
		}

		hashRemoteRenderingView2RemoteRenderedViews.remove(view);

		destroyView(view);
	}

	public void destroyView(IView view) {
		if (view == null)
			return;

		hashGLViewID2View.remove(view.getID());

		ViewClosedEvent event = new ViewClosedEvent(view);
		event.setSender(this);
		EventPublisher.trigger(event);
	}

	public void registerGLEventListenerByGLCanvas(final IGLCanvas glCanvas, final AGLView glView) {

		// This is the case when a view is rendered remote
		if (glCanvas == null)
			return;

		glCanvas.addGLEventListener(glView);
	}

	/**
	 * Associate an RCP view with the Caleydo view contained inside the RCP view.
	 *
	 * @param rcpView
	 * @param view
	 */
	public void registerRCPView(final CaleydoRCPViewPart rcpView, final IView view) {
		if (!hashRCP2View.containsKey(rcpView))
			hashRCP2View.put(rcpView, view);

		if (!hashView2RCP.containsKey(view))
			hashView2RCP.put(view, rcpView);
	}

	/**
	 * Remove association between an RCP view and the Caleydo view contained inside the RCP view.
	 *
	 * @param rcpView
	 * @param view
	 */
	public void unregisterRCPView(final CaleydoRCPViewPart rcpView, final IView view) {

		if (hashRCP2View.containsKey(rcpView))
			hashRCP2View.remove(rcpView);

		if (hashView2RCP.containsKey(view))
			hashView2RCP.remove(view);
	}

	public CaleydoRCPViewPart getViewPartFromView(IView view) {
		return hashView2RCP.get(view);
	}

	public IView getViewFromViewPart(CaleydoRCPViewPart viewPart) {
		return hashRCP2View.get(viewPart);
	}

	/**
	 * Removes all views, canvas and GL2 event listeners
	 */
	public void cleanup() {

		hashGLViewID2View.clear();
		this.views.clear();
	}

	/**
	 * This method unregisters the specified view and also triggers the destruction of that view and all remote rendered
	 * child views if it is remotely rendered.
	 *
	 * @param glView
	 */
	// public void unregisterGLView(final AGLView glView) {
	// unregisterGLView(glView, glView.getTopLevelGLView() != glView);
	// }

	/**
	 * Unregisters the specified view from this manager.
	 *
	 * @param glView
	 * @param registerAtTopLevelViewForDestruction
	 *            Specifies whether the view (if remote rendered) shall be destroyed in the next display cycle of its
	 *            top level remote rendering view.
	 */
	// private void unregisterGLView(final AGLView glView, boolean registerAtTopLevelViewForDestruction) {
	// if (glView == null)
	// return;
	//
	// IGLCanvas parentGLCanvas = (glView).getParentGLCanvas();
	//
	// if (parentGLCanvas != null) {
	// parentGLCanvas.removeGLEventListener(glView);
	// }
	//
	// hashGLViewID2GLView.remove(glView.getID());
	//
	// AGLView parentView = (AGLView) glView.getRemoteRenderingGLView();
	//
	// // Remove this view from the parent's remote rendering list
	// if (parentView != null) {
	// Set<AGLView> parentRemoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews.get(parentView);
	// if (parentRemoteRenderedViews != null)
	// parentRemoteRenderedViews.remove(glView);
	// }
	//
	// AGLView topLevelGLView = glView.getTopLevelGLView();
	// if (topLevelGLView != glView) {
	// Set<AGLView> viewsToBeDestroyed = hashTopLevelView2ViewsToBeDestroyed.get(topLevelGLView);
	//
	// if (registerAtTopLevelViewForDestruction) {
	// if (viewsToBeDestroyed == null) {
	// viewsToBeDestroyed = new HashSet<AGLView>();
	// hashTopLevelView2ViewsToBeDestroyed.put(topLevelGLView, viewsToBeDestroyed);
	// }
	// viewsToBeDestroyed.add(glView);
	// }
	// Set<AGLView> remoteRenderedViews = hashRemoteRenderingView2RemoteRenderedViews.get(glView);
	//
	// if (remoteRenderedViews != null) {
	// Set<AGLView> tempRemoteRenderedViews = new HashSet<AGLView>(remoteRenderedViews);
	// for (AGLView remoteRenderedView : tempRemoteRenderedViews) {
	// // Unregister remote rendered views of glView
	// unregisterGLView(remoteRenderedView, registerAtTopLevelViewForDestruction);
	// // Register them to be destroyed in the next display cycle
	// // of the top level remote rendering view
	// if (registerAtTopLevelViewForDestruction)
	// viewsToBeDestroyed.add(remoteRenderedView);
	// }
	// remoteRenderedViews.clear();
	// }
	// } else {
	// hashTopLevelView2ViewsToBeDestroyed.remove(glView);
	// unregisterGLCanvas(glView.getParentGLCanvas());
	// }
	//
	// hashRemoteRenderingView2RemoteRenderedViews.remove(glView);
	//
	// ViewClosedEvent event = new ViewClosedEvent(glView);
	// event.setSender(this);
	// EventPublisher.trigger(event);
	// }

	public Iterable<AGLView> getAllGLViews() {
		return Iterables.filter(hashGLViewID2View.values(), AGLView.class);
	}

	public Iterable<IView> getAllViews() {
		return hashGLViewID2View.values();
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}


	public void startAnimator() {
		if (fpsAnimator == null) {
			// FPSAnimator f = new FPSAnimator(30);
			// f.setIgnoreExceptions(true);
			// f.setPrintExceptions(true);
			// fpsAnimator = f;
			fpsAnimator = new MyAnimator(MyPreferences.getFPS());
		}

		if (!fpsAnimator.isAnimating())
			fpsAnimator.start();

		Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Start animator"));
	}

	public void stopAnimator() {
		if (fpsAnimator != null)
			fpsAnimator.stop();

		Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Stop animator"));
	}

	public void registerGLCanvasToAnimator(final IGLCanvas glCanvas) {

		// Lazy creation of animator
		startAnimator();

		if (registeredGLCanvas.contains(glCanvas))
			return;

		registeredGLCanvas.add(glCanvas);
		fpsAnimator.add(glCanvas.asGLAutoDrawAble());

		Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Add canvas to animator" + glCanvas));
	}

	@SuppressWarnings("rawtypes")
	public AGLView createGLView(Class<? extends AGLView> viewClass, IGLCanvas glCanvas,
			ViewFrustum viewFrustum) {

		AGLView view;
		try {
			Class[] argTypes = { IGLCanvas.class, ViewFrustum.class };
			Constructor aConstructor = viewClass.getConstructor(argTypes);
			view = (AGLView) aConstructor.newInstance(glCanvas, viewFrustum);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Cannot create GL view " + viewClass);
		}

		return view;
	}

	/**
	 * Creates a new view for remote rendering from a plug-in (extension point
	 * <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param viewID
	 *            ID of the view to be remotely rendered
	 * @param parentID
	 *            ID of the parent that renders the view remotely. Note that this ID can refer to a different entity
	 *            than the parentView.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent. This ID is used to determine the appropriate
	 *            {@link IRemoteViewCreator} for the embedded view.
	 * @param parentView
	 *            View that renders the view to be created.
	 * @param tablePerspectives
	 *            {@link TablePerspective}s that shall be shown by the created view.
	 * @param embeddingEventSpace
	 *            Event space that shall be used for events that only a restricted set of receivers in the embedding
	 *            should get.
	 * @return Instance of the view type specified by the parameters. Null, if the view could not be created.
	 */
	public AGLView createRemotePlugInView(String viewID, String parentID, String embeddingID, AGLView parentView,
			List<TablePerspective> tablePerspectives, String embeddingEventSpace) {

		IConfigurationElement embedding = getEmbedding(viewID, parentID, embeddingID);
		if (embedding != null) {
			IConfigurationElement[] creators = embedding.getChildren("ViewCreator");
			if (creators.length > 0) {
				IRemoteViewCreator viewCreator;
				try {
					// only one creator is allowed
					viewCreator = (IRemoteViewCreator) creators[0].createExecutableExtension("class");
					return viewCreator.createRemoteView(parentView, tablePerspectives, embeddingEventSpace);
				} catch (CoreException e) {
					Logger.log(new Status(IStatus.WARNING, "ViewManager", "Could not create view " + viewID));
				}
			} else {
				Logger.log(new Status(IStatus.WARNING, "ViewManager", "Could not create view " + viewID
						+ ", no creator found."));
			}
		} else {
			Logger.log(new Status(IStatus.WARNING, "ViewManager", "No view found! ID: " + viewID + ", Parent: "
					+ parentView.getViewType() + ", Embedding: " + embeddingID));
		}

		return null;
	}

	/**
	 * Convenience method for getting a specified embedding.
	 *
	 * @param id
	 * @param embeddingID
	 * @param parentID
	 * @return The embedding as {@link IConfigurationElement}, or null if no embedding was found.
	 */
	private IConfigurationElement getEmbedding(String id, String parentID, String embeddingID) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("org.caleydo.view.EmbeddedView");
		IExtension[] extensions = point.getExtensions();

		for (IExtension extension : extensions) {
			IConfigurationElement[] embeddingInfos = extension.getConfigurationElements();
			for (IConfigurationElement embeddingInfo : embeddingInfos) {
				if (embeddingInfo.getAttribute("viewID").equals(id)) {
					IConfigurationElement[] parentViews = embeddingInfo.getChildren("ParentView");
					for (IConfigurationElement parent : parentViews) {
						if (parent.getAttribute("viewID").equals(parentID)) {
							IConfigurationElement[] embeddings = parent.getChildren("Embedding");
							for (IConfigurationElement embedding : embeddings) {
								if (embedding.getAttribute("embeddingID").equals(embeddingID)) {
									return embedding;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Creates a new view for remote rendering from a plug-in (extension point
	 * <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param rendererID
	 *            ID of the renderer to be remotely rendered.
	 * @param parentID
	 *            ID of the parent that renders the renderer remotely. Note that this ID can refer to a different entity
	 *            than the parentView.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent. This ID is used to determine the appropriate
	 *            {@link IRemoteRendererCreator} for the embedded renderer.
	 * @param parentView
	 *            View that renders the renderer to be created.
	 * @param tablePerspectives
	 *            {@link TablePerspective}s that shall be shown by the created view.
	 * @param embeddingEventSpace
	 *            Event space that shall be used for events that only a restricted set of receivers in the embedding
	 *            should get.
	 * @return Instance of the view type specified by the parameters. NULL, if the view could not be created.
	 */
	public ALayoutRenderer createRemotePlugInRenderer(String rendererID, String parentID, String embeddingID,
			AGLView parentView, List<TablePerspective> tablePerspectives, String embeddingEventSpace) {

		IConfigurationElement embedding = getEmbedding(rendererID, parentID, embeddingID);

		if (embedding != null) {
			IConfigurationElement[] creators = embedding.getChildren("RendererCreator");
			if (creators.length > 0) {
				IRemoteRendererCreator viewCreator;
				try {
					// only one creator is allowed
					viewCreator = (IRemoteRendererCreator) creators[0].createExecutableExtension("class");
					return viewCreator.createRemoteView(parentView, tablePerspectives, embeddingEventSpace);
				} catch (CoreException e) {
					Logger.log(new Status(IStatus.WARNING, "ViewManager", "Could not create renderer " + rendererID));
				}
			} else {
				Logger.log(new Status(IStatus.WARNING, "ViewManager", "Could not create renderer " + rendererID
						+ ", no creator found."));
			}
		} else {

			Logger.log(new Status(IStatus.WARNING, "ViewManager", "No renderer found! ID: " + rendererID + ", Parent: "
					+ parentView.getViewType() + ", Embedding: " + embeddingID));

		}

		return null;
	}

	/**
	 * Gets an {@link IEmbeddedVisualizationInfo} for a specified plugin view (extension point
	 * <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param id
	 *            ID of the plugin view or renderer that is rendered remotely.
	 * @param parentID
	 *            ID of the remote rendering parent.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent.
	 * @return Instance of the visualization info specified by the parameters. Null, if it could not be created.
	 */
	public IEmbeddedVisualizationInfo getEmbeddedVisualizationInfoOfPluginView(String id, String parentID,
			String embeddingID) {

		IConfigurationElement embedding = getEmbedding(id, parentID, embeddingID);
		if (embedding.getAttribute("embeddingID").equals(embeddingID)) {
			try {
				return (IEmbeddedVisualizationInfo) embedding.createExecutableExtension("visInfo");
			} catch (CoreException e) {
				Logger.log(new Status(IStatus.WARNING, "ViewManager", "Could not create vis info for " + id));
			}
		}

		return null;
	}

	/**
	 * Retrieves IDs of all views and renderers that have defined via plug-in mechanism to be remote rendered with the
	 * specified parameters (extension point <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param parentID
	 *            ID of the remote rendering parent.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent.
	 * @return ID list of all views and renderers that shall be rendered remote according to the specified parameters.
	 */
	public Set<String> getRemotePlugInViewIDs(String parentID, String embeddingID) {
		Set<String> viewIDs = new HashSet<>();

		for (IConfigurationElement embeddingInfo : RegistryFactory.getRegistry().getConfigurationElementsFor(
				"org.caleydo.view.EmbeddedView")) {
			IConfigurationElement[] parentViews = embeddingInfo.getChildren("ParentView");
			for (IConfigurationElement parent : parentViews) {
				if (parent.getAttribute("viewID").equals(parentID)) {
					IConfigurationElement[] embeddings = parent.getChildren("Embedding");
					for (IConfigurationElement embedding : embeddings) {
						if (embedding.getAttribute("embeddingID").equals(embeddingID)) {
							viewIDs.add(embeddingInfo.getAttribute("viewID"));
						}
					}
				}
			}
		}
		return viewIDs;
	}

	/**
	 * Gets the path of the icon that is associated with the specified plugin view or renderer (extension point
	 * <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param id
	 *            ID of the view or renderer.
	 * @param parentID
	 *            ID of the remote rendering parent.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent.
	 * @return File path to the icon. Null, if no icon was specified or could be found.
	 */
	public String getRemotePlugInViewIcon(String id, String parentID, String embeddingID) {

		IConfigurationElement embedding = getEmbedding(id, parentID, embeddingID);
		if (embedding != null) {
			String iconPath = embedding.getAttribute("icon");
			if (iconPath == null || iconPath == "") {
				return null;
			}

			Bundle viewPlugin = Platform.getBundle(embedding.getContributor().getName());

			URL iconURL = viewPlugin.getEntry(iconPath);
			try {
				iconPath = FileLocator.toFileURL(iconURL).getPath();
			} catch (IOException e) {
				return null;
			}
			return iconPath;
		}

		return null;
	}

	/**
	 * Determines whether the plugin view ore renderer is a default view for the specified embedding (extension point
	 * <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param viewID
	 *            ID of the view or renderer.
	 * @param parentID
	 *            ID of the remote rendering parent.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent.
	 * @return True, if the view or renderer was specified as default, false otherwise.
	 */
	public boolean isPlugInViewDefault(String viewID, String parentID, String embeddingID) {

		IConfigurationElement embedding = getEmbedding(viewID, parentID, embeddingID);
		if (embedding != null) {
			if (embedding.getAttribute("embeddingID").equals(embeddingID)) {
				return Boolean.valueOf(embedding.getAttribute("isDefaultView"));
			}
		}

		return false;
	}

	/**
	 * Determines whether the embedding specifies a {@link AGLView} or a {@link ALayoutRenderer} (extension point
	 * <code>org.caleydo.view.EmbeddedView</code>).
	 *
	 * @param id
	 *            ID of the view or renderer.
	 * @param parentID
	 *            ID of the remote rendering parent.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent.
	 * @return True, if the embedding specifies a view, false otherwise.
	 */
	public boolean isPluginView(String id, String parentID, String embeddingID) {
		IConfigurationElement embedding = getEmbedding(id, parentID, embeddingID);
		if (embedding != null) {
			IConfigurationElement[] creators = embedding.getChildren("ViewCreator");
			if (creators.length > 0) {
				return true;
			}
		}
		return false;
	}

	public synchronized void initializeUnserializedViews() {
		if (!areSerializedViewsInitialized) {
			areSerializedViewsInitialized = true;
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					// System.out.println("unserialized view initialization");
					IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.getViewReferences();

					try {
						for (IViewReference view : views) {

							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(view.getId(), view.getSecondaryId(), IWorkbenchPage.VIEW_VISIBLE);
						}
					} catch (PartInitException e) {
						throw new IllegalStateException();
					}

					// Make DVI visible
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.showView("org.caleydo.view.dvi");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * @param aswtView
	 */
	public void addView(IView view) {
		this.views.put(view.getID(), view);
	}
}
