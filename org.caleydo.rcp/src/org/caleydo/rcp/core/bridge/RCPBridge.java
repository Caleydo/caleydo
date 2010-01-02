package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.bookmarking.GLBookmarkManager;
import org.caleydo.core.view.opengl.canvas.cell.GLCell;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.GLGlyphSliderView;
import org.caleydo.core.view.opengl.canvas.grouper.GLGrouper;
import org.caleydo.core.view.opengl.canvas.histogram.GLHistogram;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.GLDataFlipper;
import org.caleydo.core.view.opengl.canvas.remote.viewbrowser.GLPathwayViewBrowser;
import org.caleydo.core.view.opengl.canvas.remote.viewbrowser.GLTissueViewBrowser;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLDendrogram;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;
import org.caleydo.core.view.opengl.canvas.tissue.GLTissue;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.caleydo.view.base.rcp.ARcpGLViewPart;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class RCPBridge
	implements IGUIBridge {
	private String sFileNameCurrentDataSet;

	@Override
	public void closeApplication() {
		try {
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e) {
			throw new IllegalStateException("Cannot execute exit command.");
		}
	}

	@Override
	public void setShortInfo(String sMessage) {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public void setFileNameCurrentDataSet(String sFileName) {
		this.sFileNameCurrentDataSet = sFileName;
	}

	@Override
	public String getFileNameCurrentDataSet() {
		return sFileNameCurrentDataSet;
	}

	@Override
	public Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

	@Override
	public void createView(final ASerializedView serializedView) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					ARcpGLViewPart viewPart = (ARcpGLViewPart) page.showView(serializedView.getViewGUIID());
					AGLView view = viewPart.getGLEventListener();
					view.initFromSerializableRepresentation(serializedView);
					// TODO re-init view with its serializedView

				}
				catch (PartInitException ex) {
					throw new RuntimeException("could not create view with gui-id="
						+ serializedView.getViewGUIID(), ex);
				}
			}
		});
	}

	@Override
	public void closeView(final String viewGUIID) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IViewPart viewToClose =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewGUIID);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(viewToClose);

			}
		});
	}

	@Override
	public AGLView createGLEventListener(ECommandType type, GLCaleydoCanvas glCanvas, String label,
		IViewFrustum viewFrustum) {
		
		//TODO check if view plugin is loaded
		
		AGLView glView = null;
		
		switch (type) {
			case CREATE_GL_HEAT_MAP_3D:
				glView = new GLHeatMap(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_SCATTERPLOT:
				glView = new GLScatterplot(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_PROPAGATION_HEAT_MAP_3D:
				glView = new GLBookmarkManager(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_TEXTURE_HEAT_MAP_3D:
				glView = new GLHierarchicalHeatMap(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_PATHWAY_3D:
				glView = new GLPathway(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_PARALLEL_COORDINATES:
				glView = new GLParallelCoordinates(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_GLYPH:
				glView = new GLGlyph(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_GLYPH_SLIDER:
				glView = new GLGlyphSliderView(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_CELL:
				glView = new GLCell(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_TISSUE:
				glView = new GLTissue(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_BUCKET_3D:
				glView =
					new GLRemoteRendering(glCanvas, label, viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);
				break;
			case CREATE_GL_JUKEBOX_3D:
				glView =
					new GLRemoteRendering(glCanvas, label, viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX);
				break;
			case CREATE_GL_DATA_FLIPPER:
				glView = new GLDataFlipper(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_TISSUE_VIEW_BROWSER:
				glView = new GLTissueViewBrowser(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_PATHWAY_VIEW_BROWSER:
				glView = new GLPathwayViewBrowser(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_RADIAL_HIERARCHY:
				glView = new GLRadialHierarchy(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_HISTOGRAM:
				glView = new GLHistogram(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_GROUPER:
				glView = new GLGrouper(glCanvas, label, viewFrustum);
				break;
			case CREATE_GL_DENDROGRAM_HORIZONTAL:
				glView = new GLDendrogram(glCanvas, label, viewFrustum, true);
				break;
			case CREATE_GL_DENDROGRAM_VERTICAL:
				glView = new GLDendrogram(glCanvas, label, viewFrustum, false);
				break;
			default:
				throw new RuntimeException(
					"ViewJoglManager.createGLCanvasUser() failed due to unhandled type [" + type.toString()
						+ "]");
		}
		
		return glView;
	}
}
