package org.caleydo.view.subgraph;

import gleem.linalg.Vec2f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.IMultiFormChangeListener;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout2.AGLElementGLView;
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.InOutInitializers;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.InOutTransitionBase;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.ShowPortalNodesEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.subgraph.event.ShowNodeInfoEvent;
import org.caleydo.view.subgraph.event.ShowPathwayBrowserEvent;
import org.caleydo.view.subgraph.ranking.RankingElement;
import org.eclipse.swt.widgets.Composite;

public class GLSubGraph extends AGLElementGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView,
		IMultiFormChangeListener {

	public static String VIEW_TYPE = "org.caleydo.view.subgraph";

	public static String VIEW_NAME = "SubGraph";

	private List<TablePerspective> tablePerspectives = new ArrayList<>();

	private Set<String> remoteRenderedPathwayMultiformViewIDs;

	private String pathEventSpace = GeneralManager.get().getEventPublisher().createUniqueEventSpace();

	private AnimatedGLElementContainer baseContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout(
			true, 10, GLPadding.ZERO));
	private GLElementContainer root = new GLElementContainer(GLLayouts.LAYERS);
	private AnimatedGLElementContainer pathwayRow = new AnimatedGLElementContainer();
	// private AnimatedGLElementContainer pathwayRow = new AnimatedGLElementContainer();
	private GLSubGraphAugmentation augmentation = new GLSubGraphAugmentation();
	private AnimatedGLElementContainer nodeInfoContainer = new AnimatedGLElementContainer(
			new GLSizeRestrictiveFlowLayout(true, 10, GLPadding.ZERO));

	private GLPathwayBackground currentActiveBackground = null;

	// private List<IPathwayRepresentation> pathwayRepresentations = new ArrayList<>();

	private PathEventSpaceHandler pathEventSpaceHandler = new PathEventSpaceHandler();

	/**
	 * All segments of the currently selected path.
	 */
	private List<PathwayPath> pathSegments = new ArrayList<>();

	/**
	 * Info for the {@link MultiFormRenderer} of the selected path.
	 */
	private MultiFormInfo pathInfo;

	/**
	 * List of infos for all pathways.
	 */
	protected List<PathwayMultiFormInfo> pathwayInfos = new ArrayList<>();

	protected static int currentPathwayAge = Integer.MAX_VALUE;

	protected MultiFormRenderer lastUsedRenderer;

	protected GLPathwayGridLayout pathwayLayout = new GLPathwayGridLayout(this, GLPadding.ZERO, 10);

	// /**
	// * Maps from the embeddingID to all associated {@link MultiFormRenderer}s and their wrapping {@link GLElement}.
	// */
	// private Map<String, List<Pair<MultiFormRenderer, GLElement>>> multiFormRenderers = new HashMap<>();

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLSubGraph(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
		GLElementContainer column = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 10, GLPadding.ZERO));
		column.add(baseContainer);
		nodeInfoContainer.setSize(Float.NaN, 0);
		column.add(nodeInfoContainer);
		RankingElement rankingElement = new RankingElement(this, eventListeners);
		rankingElement.setSize(200, Float.NaN);
		baseContainer.add(rankingElement);
		// pathwayRow.setLayout(new GLMultiFormPathwayLayout(10, GLPadding.ZERO, this, pathwayRow));
		pathwayRow.setLayout(pathwayLayout);
		//
		baseContainer.add(pathwayRow);

		root.add(column);
		root.add(augmentation);

	}

	@Override
	public void init(GL2 gl) {
		super.init(gl);
		augmentation.init(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedSubGraphView serializedForm = new SerializedSubGraphView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "Subgraph view";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		eventListeners.register(AddTablePerspectivesEvent.class, new AddTablePerspectivesListener().setHandler(this));
		eventListeners.register(pathEventSpaceHandler, pathEventSpace);
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	public void addTablePerspective(TablePerspective newTablePerspective) {
		if (newTablePerspective != null) {
			tablePerspectives.add(newTablePerspective);
		}
		createRemoteRenderedViews();
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		if (newTablePerspectives != null) {
			for (TablePerspective tablePerspective : newTablePerspectives) {
				if (tablePerspective != null) {
					tablePerspectives.add(tablePerspective);
				}
			}
		}
		createRemoteRenderedViews();
	}

	private void createRemoteRenderedViews() {
		if (remoteRenderedPathwayMultiformViewIDs == null) {
			// addPathway(PathwayManager.get().getPathwayByTitle("Glioma", EPathwayDatabaseType.KEGG));
			// addPathway(PathwayManager.get().getPathwayByTitle("Pathways in cancer", EPathwayDatabaseType.KEGG));
			pathInfo = new MultiFormInfo();
			createMultiformRenderer(tablePerspectives, EnumSet.of(EEmbeddingID.PATH_LEVEL1, EEmbeddingID.PATH_LEVEL2),
					baseContainer, 0.3f, pathInfo);
			// This assumes that a path level 2 view exists.
			int rendererID = pathInfo.embeddingIDToRendererIDs.get(EEmbeddingID.PATH_LEVEL2).get(0);
			if (pathInfo.multiFormRenderer.getActiveRendererID() != rendererID) {
				pathInfo.multiFormRenderer.setActive(rendererID);
			} else {
				setPathLevel(EEmbeddingID.PATH_LEVEL2);
			}

		}
	}

	public void addPathway(PathwayGraph pathway) {

		if (tablePerspectives.size() <= 0)
			return;

		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);

		TablePerspective tablePerspective = tablePerspectives.get(0);
		Perspective oldRecordPerspective = tablePerspective.getRecordPerspective();
		Perspective newRecordPerspective = new Perspective(tablePerspective.getDataDomain(),
				oldRecordPerspective.getIdType());

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(oldRecordPerspective.getVirtualArray());

		newRecordPerspective.init(data);

		Perspective oldDimensionPerspective = tablePerspective.getDimensionPerspective();
		Perspective newDimensionPerspective = new Perspective(tablePerspective.getDataDomain(),
				oldDimensionPerspective.getIdType());
		data = new PerspectiveInitializationData();
		data.setData(oldDimensionPerspective.getVirtualArray());

		newDimensionPerspective.init(data);
		PathwayTablePerspective pathwayTablePerspective = new PathwayTablePerspective(tablePerspective.getDataDomain(),
				pathwayDataDomain, newRecordPerspective, newDimensionPerspective, pathway);
		pathwayDataDomain.addTablePerspective(pathwayTablePerspective);

		List<TablePerspective> pathwayTablePerspectives = new ArrayList<>(1);
		pathwayTablePerspectives.add(pathwayTablePerspective);

		PathwayMultiFormInfo info = new PathwayMultiFormInfo();
		info.pathway = pathway;

		info.age = currentPathwayAge--;
		// pathwayColumn.setLayout(new GLSizeRestrictiveFlowLayout(false, 10, GLPadding.ZERO));
		createMultiformRenderer(pathwayTablePerspectives,
				EnumSet.of(EEmbeddingID.PATHWAY_LEVEL1, EEmbeddingID.PATHWAY_LEVEL2, EEmbeddingID.PATHWAY_LEVEL3),
				pathwayRow, Float.NaN, info);
		pathwayLayout.addColumn(info.window);

		int rendererID = info.embeddingIDToRendererIDs.get(EEmbeddingID.PATHWAY_LEVEL1).get(0);
		if (info.multiFormRenderer.getActiveRendererID() != rendererID) {
			info.multiFormRenderer.setActive(rendererID);
		}
		// for (PathwayMultiFormInfo pathwayInfo : pathwayInfos) {
		// pathwayInfo.multiFormRenderer.setActive(pathwayInfo.embeddingIDToRendererIDs.get(
		// EEmbeddingID.PATHWAY_LEVEL2).get(0));
		// }

		pathwayInfos.add(info);
		// pathwayRow.add(pathwayColumn);
	}

	private void createMultiformRenderer(List<TablePerspective> tablePerspectives, EnumSet<EEmbeddingID> embeddingIDs,
			AnimatedGLElementContainer parent, Object layoutData, MultiFormInfo info) {

		// GLElementContainer backgroundContainer = new GLElementContainer(GLLayouts.LAYERS);
		// backgroundContainer.setLayoutData(layoutData);

		// Different renderers should receive path updates from the beginning on, therefore no lazy creation.
		MultiFormRenderer renderer = new MultiFormRenderer(this, false);
		renderer.addChangeListener(this);
		info.multiFormRenderer = renderer;
		PathwayGraph pathway = null;

		for (EEmbeddingID embedding : embeddingIDs) {
			String embeddingID = embedding.id();
			Set<String> ids = ViewManager.get().getRemotePlugInViewIDs(VIEW_TYPE, embeddingID);

			for (String viewID : ids) {
				List<Integer> rendererIDList = info.embeddingIDToRendererIDs.get(embedding);
				if (rendererIDList == null) {
					rendererIDList = new ArrayList<>(ids.size());
					info.embeddingIDToRendererIDs.put(embedding, rendererIDList);
				}

				int rendererID = renderer.addPluginVisualization(viewID, getViewType(), embeddingID, tablePerspectives,
						pathEventSpace);
				rendererIDList.add(rendererID);

				IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(renderer, rendererID);
				if (pathwayRepresentation != null) {
					pathway = pathwayRepresentation.getPathway();
					pathwayRepresentation.addVertexRepBasedContextMenuItem(new VertexRepBasedContextMenuItem(
							"Show Node Info", ShowNodeInfoEvent.class, pathEventSpace));
					pathwayRepresentation.addVertexRepBasedContextMenuItem(new VertexRepBasedContextMenuItem(
							"Show Related Pathways", ShowPathwayBrowserEvent.class, pathEventSpace));
					pathwayRepresentation.addVertexRepBasedContextMenuItem(new VertexRepBasedContextMenuItem(
							"Show Portal Nodes", ShowPortalNodesEvent.class, pathEventSpace));
				}
			}
		}

		GLPathwayWindow window = new GLPathwayWindow(pathway, this, info);
		info.window = window;

		// GLElementContainer multiFormContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 1,
		// GLPadding.ZERO));
		// multiFormContainer.add(new GLTitleBar(pathway == null ? "" : pathway.getTitle()));
		// GLElementAdapter multiFormRendererAdapter = new GLElementAdapter(this, renderer, true);
		// multiFormContainer.add(multiFormRendererAdapter);
		// // multiFormRendererAdapter.onPick(pl);
		//
		// GLElementViewSwitchingBar viewSwitchingBar = new GLElementViewSwitchingBar(renderer);
		// GLPathwayBackground bg = new GLPathwayBackground(this, viewSwitchingBar);
		// GLElementContainer barRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout(true, 0, new GLPadding(0,
		// 2,
		// 5, 0)));
		//
		// barRow.add(new GLElement());
		// barRow.add(viewSwitchingBar);
		//
		// backgroundContainer.add(bg);
		// backgroundContainer.add(multiFormContainer);
		// backgroundContainer.add(barRow);

		lastUsedRenderer = renderer;

		// List<Pair<MultiFormRenderer, GLElement>> embeddingSpecificRenderers = multiFormRenderers.get(embeddingID);
		//
		// if (embeddingSpecificRenderers == null) {
		// embeddingSpecificRenderers = new ArrayList<>();
		// multiFormRenderers.put(embeddingID, embeddingSpecificRenderers);
		// }
		// embeddingSpecificRenderers.add(new Pair<MultiFormRenderer, GLElement>(renderer, multiFormRendererAdapter));

		parent.add(window);
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return new ArrayList<>(tablePerspectives);
	}

	@Override
	public void removeTablePerspective(int tablePerspectiveID) {
		// TODO: implement
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: implement
		return null;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	protected GLElement createRoot() {

		return root;
	}

	/**
	 * Gets the renderer of the specified {@link MultiFormRenderer} as {@link IPathwayRepresentation}.
	 *
	 * @param renderer
	 * @param id
	 *            id of the visualization in the renderer.
	 * @return The pathway representation or null, if the active renderer is no pathway representation.
	 */
	protected IPathwayRepresentation getPathwayRepresentation(MultiFormRenderer renderer, int id) {
		// int id = renderer.getActiveRendererID();
		IPathwayRepresentation pathwayRepresentation = null;

		if (renderer.isView(id)) {
			AGLView view = renderer.getView(id);
			if (view instanceof IPathwayRepresentation) {
				pathwayRepresentation = (IPathwayRepresentation) view;

			}
		} else {
			ALayoutRenderer layoutRenderer = renderer.getLayoutRenderer(id);
			if (layoutRenderer instanceof IPathwayRepresentation) {
				pathwayRepresentation = (IPathwayRepresentation) layoutRenderer;
			}
		}

		return pathwayRepresentation;
	}

	protected Rectangle2D getAbsoluteVertexLocation(IPathwayRepresentation pathwayRepresentation,
			PathwayVertexRep vertexRep, GLElement element) {

		Vec2f elementPosition = element.getAbsoluteLocation();
		Rectangle2D location = pathwayRepresentation.getVertexRepBounds(vertexRep);
		if (location != null) {
			return new Rectangle2D.Float((float) (location.getX() + elementPosition.x()),
					(float) (location.getY() + elementPosition.y()), (float) location.getWidth(),
					(float) location.getHeight());
			// return new Rectangle2D.Float((elementPosition.x()), (elementPosition.y()), (float) location.getWidth(),
			// (float) location.getHeight());

		}
		return null;
	}

	protected void updatePathLinks() {

		augmentation.setPxlSize(this.getParentGLCanvas().getWidth(), this.getParentGLCanvas().getHeight());

		augmentation.clearRenderers();
		PathwayVertexRep start = null;
		PathwayVertexRep end = null;
		// List<Pair<MultiFormRenderer, GLElement>> rendererList = multiFormRenderers.get(EEmbeddingID.PATHWAY_MULTIFORM
		// .id());
		// if (rendererList == null)
		// return;

		for (PathwayPath path : pathSegments) {
			if (start == null) {
				start = path.getPath().getEndVertex();
			} else {
				end = path.getPath().getStartVertex();
				// draw link
				//
				PathwayVertexRep referenceVertexRep = start;
				Rectangle2D referenceRectangle = null;
				for (PathwayMultiFormInfo info : pathwayInfos) {
					IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
							info.multiFormRenderer.getActiveRendererID());
					if (pathwayRepresentation != null
							&& pathwayRepresentation.getPathway() == referenceVertexRep.getPathway()) {
						referenceRectangle = getAbsoluteVertexLocation(pathwayRepresentation, referenceVertexRep,
								info.container);
						break;
					}
				}
				if (referenceRectangle == null)
					return;

				for (PathwayMultiFormInfo info : pathwayInfos) {
					IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
							info.multiFormRenderer.getActiveRendererID());

					if (pathwayRepresentation != null) {

						// for (PathwayVertexRep vertexRep : vertexReps) {
						Rectangle2D rect = getAbsoluteVertexLocation(pathwayRepresentation, end, info.container);
						if (rect != null) {
							augmentation.addRenderer(new GLSubGraphAugmentation.ConnectionRenderer(referenceRectangle,
									rect));
						}
						// }
					}
				}
				//
				start = path.getPath().getEndVertex();
				end = null;
			}
		}
		List<Rectangle2D> path = new ArrayList<>();

		IPathwayRepresentation pathwayRepresentation = null;
		PathwayMultiFormInfo pwInfo = null;
		for (PathwayPath segment : pathSegments) 
		{
			for (PathwayMultiFormInfo info : pathwayInfos) {
				pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
						info.multiFormRenderer.getActiveRendererID());
				if (pathwayRepresentation!=null && (segment.getPathway() == pathwayRepresentation.getPathway())) {
					pwInfo = info;
					break;
				}
			}
			if(pathwayRepresentation!=null && pwInfo!=null){
				for (PathwayVertexRep v : segment.getNodes()) {				
					Rectangle2D rect = getAbsoluteVertexLocation(pathwayRepresentation, v, pwInfo.container);
					if (rect != null)
						path.add(rect);
				}
			}
		}

		augmentation.setPath(path);

	}

	@Override
	public void display(GL2 gl) {
		boolean updateAugmentation = false;
		if (isLayoutDirty)
			updateAugmentation = true;
		super.display(gl);
		// The augmentation has to be updated after the layout was updated in super; updating on relayout would be too
		// early, as the layout is not adapted at that time.
		//if (updateAugmentation) {
			updatePathLinks();
		//}

	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID,
			boolean wasTriggeredByUser) {

		if (pathInfo == null || !pathInfo.isInitialized())
			return;
		if (multiFormRenderer == pathInfo.multiFormRenderer) {
			EEmbeddingID embeddingID = pathInfo.getEmbeddingIDFromRendererID(rendererID);
			setPathLevel(embeddingID);
		}
		if (wasTriggeredByUser) {
			for (PathwayMultiFormInfo info : pathwayInfos) {
				if (info.multiFormRenderer == multiFormRenderer && rendererID != previousRendererID) {
					if (info.getEmbeddingIDFromRendererID(rendererID) == EEmbeddingID.PATHWAY_LEVEL1) {
						pathwayLayout.setLevel1(info.window);
						info.age = currentPathwayAge--;
						lastUsedRenderer = info.multiFormRenderer;
						pathwayRow.relayout();
						break;
					}
				}
			}
		}
	}

	private void setPathLevel(EEmbeddingID embeddingID) {
		if (embeddingID == null)
			return;
		if (embeddingID == EEmbeddingID.PATH_LEVEL1) {
			pathInfo.window.setSize(Float.NaN, Float.NaN);
			pathInfo.window.setLayoutData(0.5f);
		} else if (embeddingID == EEmbeddingID.PATH_LEVEL2) {
			pathInfo.window.setLayoutData(Float.NaN);
			pathInfo.window.setSize(150, Float.NaN);
		}
		isLayoutDirty = true;
	}

	@Override
	public void rendererAdded(MultiFormRenderer multiFormRenderer, int rendererID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyed(MultiFormRenderer multiFormRenderer) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the currentActiveBackground, see {@link #currentActiveBackground}
	 */
	public GLPathwayBackground getCurrentActiveBackground() {
		return currentActiveBackground;
	}

	/**
	 * @param currentActiveBackground
	 *            setter, see {@link currentActiveBackground}
	 */
	public void setCurrentActiveBackground(GLPathwayBackground currentActiveBackground) {
		this.currentActiveBackground = currentActiveBackground;
	}

	/**
	 * @return the pathEventSpace, see {@link #pathEventSpace}
	 */
	public String getPathEventSpace() {
		return pathEventSpace;
	}

	/**
	 * Info about container, embeddings, etc. for each {@link MultiFormRenderer} in this view.
	 *
	 * @author Christian
	 *
	 */
	protected class MultiFormInfo {
		/**
		 * The multiform renderer.
		 */
		protected MultiFormRenderer multiFormRenderer;
		/**
		 * The element that represents the "window" of the multiform renderer, which includes a title bar. This element
		 * should be used for resizing.
		 */
		protected GLPathwayWindow window;
		/**
		 * The parent {@link GLElementAdapter} of this container.
		 */
		protected GLElementAdapter container;
		protected Map<EEmbeddingID, List<Integer>> embeddingIDToRendererIDs = new HashMap<>();

		protected boolean isInitialized() {
			return multiFormRenderer != null && window != null && container != null && embeddingIDToRendererIDs != null;
		}

		protected EEmbeddingID getEmbeddingIDFromRendererID(int rendererID) {
			for (Entry<EEmbeddingID, List<Integer>> entry : embeddingIDToRendererIDs.entrySet()) {
				List<Integer> rendererIDs = entry.getValue();
				if (rendererIDs.contains(rendererID)) {
					return entry.getKey();
				}
			}
			return null;
		}
	}

	/**
	 * Same as {@link MultiFormInfo}, but especially for MultiFormRenderers of pathways.
	 *
	 * @author Christian
	 *
	 */
	protected class PathwayMultiFormInfo extends MultiFormInfo {
		protected PathwayGraph pathway;
		protected int age;

		@Override
		protected boolean isInitialized() {
			return super.isInitialized() && pathway != null;
		}
	}

	protected ArrayList<Rectangle2D> portalRects = new ArrayList<>();

	private class PathEventSpaceHandler {

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowPortalNodes(ShowPortalNodesEvent event) {
			portalRects.clear();
			PathwayVertexRep vertexRep = event.getVertexRep();
			Rectangle2D nodeRect = null;
			// find in all open pathways
			for (PathwayMultiFormInfo info : pathwayInfos) {
				IPathwayRepresentation pathwayRepresentation = 
						getPathwayRepresentation(info.multiFormRenderer,info.multiFormRenderer.getActiveRendererID());
				if(pathwayRepresentation!=null){
					Set<PathwayVertexRep> portalVertexRepsInPathway = 
							PathwayManager.get().getEquivalentVertexRepsInPathway(vertexRep, pathwayRepresentation.getPathway());
					
					for (PathwayVertexRep portalVertexRep : portalVertexRepsInPathway) {
						Rectangle2D rect = getAbsoluteVertexLocation(pathwayRepresentation, portalVertexRep, info.container);
						portalRects.add(rect);
					}
					if (nodeRect == null && pathwayRepresentation.getPathway().containsVertex(vertexRep))
						nodeRect = getAbsoluteVertexLocation(pathwayRepresentation, vertexRep, info.container);
				}
			}
			augmentation.updatePortalRects(nodeRect, portalRects);
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onPathSelection(PathwayPathSelectionEvent event) {
			pathSegments = event.getPathSegments();
			updatePathLinks();
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowNodeInfo(ShowNodeInfoEvent event) {
			GLNodeInfo nodeInfo = new GLNodeInfo(event.getVertexRep());
			nodeInfo.setSize(80, 80);
			nodeInfoContainer.add(nodeInfo, 200, new InOutTransitionBase(InOutInitializers.BOTTOM,
					MoveTransitions.MOVE_LINEAR));
			nodeInfoContainer.setSize(Float.NaN, 80);
			// nodeInfoContainer.relayout();
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowPathwayBrowser(ShowPathwayBrowserEvent event) {
			// GLNodeInfo nodeInfo = new GLNodeInfo(event.getVertexRep());
			// nodeInfo.setSize(80, 80);
			// baseContainer.add(nodeInfo, 200, new InOutTransitionBase(InOutInitializers.RIGHT,
			// MoveTransitions.GROW_LINEAR));
			// baseContainer.setSize(Float.NaN, 80);
			// nodeInfoContainer.relayout();
		}

	}
}
