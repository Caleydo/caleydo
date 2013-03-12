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
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.view.MinSizeUpdateEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
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
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.ShowPortalNodesEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.subgraph.GLWindow.ESlideInButtonPosition;
import org.caleydo.view.subgraph.GLWindow.ICloseWindowListener;
import org.caleydo.view.subgraph.contextmenu.ShowCommonNodeItem;
import org.caleydo.view.subgraph.datamapping.GLExperimentalDataMapping;
import org.caleydo.view.subgraph.event.ShowCommonNodePathwaysEvent;
import org.caleydo.view.subgraph.event.ShowCommonNodesPathwaysEvent;
import org.caleydo.view.subgraph.event.ShowNodeInfoEvent;
import org.caleydo.view.subgraph.ranking.PathwayFilters;
import org.caleydo.view.subgraph.ranking.PathwayRankings;
import org.caleydo.view.subgraph.ranking.RankingElement;
import org.eclipse.swt.widgets.Composite;

public class GLSubGraph extends AGLElementGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView,
		IMultiFormChangeListener {

	public static String VIEW_TYPE = "org.caleydo.view.subgraph";

	public static String VIEW_NAME = "Entourage";

	// private List<TablePerspective> tablePerspectives = new ArrayList<>();

	// private Set<String> remoteRenderedPathwayMultiformViewIDs;

	private String pathEventSpace = GeneralManager.get().getEventPublisher().createUniqueEventSpace();

	private AnimatedGLElementContainer baseContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout(
			true, 10, GLPadding.ZERO));
	private GLElementContainer root = new GLElementContainer(GLLayouts.LAYERS);
	private AnimatedGLElementContainer pathwayRow = new AnimatedGLElementContainer();
	// private AnimatedGLElementContainer pathwayRow = new AnimatedGLElementContainer();
	private GLSubGraphAugmentation augmentation = new GLSubGraphAugmentation();
	private AnimatedGLElementContainer nodeInfoContainer = new AnimatedGLElementContainer(
			new GLSizeRestrictiveFlowLayout(true, 10, GLPadding.ZERO));

	private GLWindow activeWindow = null;

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

	protected MultiFormRenderer lastUsedLevel1Renderer;

	protected GLPathwayGridLayout pathwayLayout = new GLPathwayGridLayout(this, GLPadding.ZERO, 10);

	protected GLExperimentalDataMapping experimentalDataMappingElement = new GLExperimentalDataMapping(this);
	/**
	 * The element that shows the ranked pathway list
	 */
	protected RankingElement rankingElement = new RankingElement(this);

	/**
	 * Determines whether path selection mode is currently active.
	 */
	protected boolean isPathSelectionMode = false;

	/**
	 * Determines whether a new pathway was recently added. This information is needed to send events when dependent
	 * views need to be initialized in the first display cycle.
	 */
	protected boolean wasPathwayAdded = false;

	private boolean isAltKeyDown = false;
	private boolean isShiftKeyDown = false;

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
		final GLWindow dataMappingWindow = new GLWindow("Data Mapping", this);
		dataMappingWindow.setSize(Float.NaN, 80);
		dataMappingWindow.setContent(experimentalDataMappingElement);
		dataMappingWindow.setShowCloseButton(false);
		dataMappingWindow.setButtonPosition(ESlideInButtonPosition.TOP);

		column.add(dataMappingWindow);
		column.add(nodeInfoContainer);
		GLWindow rankingWindow = new GLWindow("Pathways", this);
		rankingWindow.setSize(100, Float.NaN);
		rankingWindow.setContent(rankingElement);
		rankingWindow.setButtonPosition(ESlideInButtonPosition.RIGHT);
		rankingWindow.setShowCloseButton(false);
		rankingElement.setWindow(rankingWindow);
		baseContainer.add(rankingWindow);
		// pathwayRow.setLayout(new GLMultiFormPathwayLayout(10, GLPadding.ZERO, this, pathwayRow));
		pathwayRow.setLayout(pathwayLayout);
		// pathwayRow.setDefaultDuration(Durations.fix(600));
		// pathwayRow
		// .setDefaultInTransition(new InOutTransitionBase(InOutInitializers.RIGHT, MoveTransitions.MOVE_LINEAR));
		//
		baseContainer.add(pathwayRow);

		root.add(column);
		root.add(augmentation);
		registeKeyListeners();

	}

	@Override
	public void init(GL2 gl) {
		super.init(gl);
		pathInfo = new MultiFormInfo();
		createMultiformRenderer(new ArrayList<>(experimentalDataMappingElement.getTablePerspectives()),
				EnumSet.of(EEmbeddingID.PATH_LEVEL1, EEmbeddingID.PATH_LEVEL2), baseContainer, 0.3f, pathInfo);
		pathInfo.window.setButtonPosition(ESlideInButtonPosition.LEFT);
		pathInfo.window.setShowCloseButton(false);
		// This assumes that a path level 2 view exists.
		int rendererID = pathInfo.embeddingIDToRendererIDs.get(EEmbeddingID.PATH_LEVEL2).get(0);
		if (pathInfo.multiFormRenderer.getActiveRendererID() != rendererID) {
			pathInfo.multiFormRenderer.setActive(rendererID);
		} else {
			setPathLevel(EEmbeddingID.PATH_LEVEL2);
		}
		augmentation.init(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedSubGraphView serializedForm = new SerializedSubGraphView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	protected void registeKeyListeners() {

		parentGLCanvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyPressed(IKeyEvent e) {

				augmentation.showPortals(e.isKeyDown('p'));

			}

			@Override
			public void keyReleased(IKeyEvent e) {

				augmentation.showPortals(e.isKeyDown('p'));

			}
		});
	}

	@Override
	public String toString() {
		return "Subgraph view";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		eventListeners.register(pathEventSpaceHandler, pathEventSpace);
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
	}

	public void addPathway(PathwayGraph pathway) {

		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);
		List<TablePerspective> tablePerspectives = new ArrayList<>(
				experimentalDataMappingElement.getTablePerspectives());
		TablePerspective tablePerspective = tablePerspectives.get(0);
		Perspective recordPerspective = tablePerspective.getRecordPerspective();
		Perspective dimensionPerspective = tablePerspective.getDimensionPerspective();

		PathwayTablePerspective pathwayTablePerspective = new PathwayTablePerspective(tablePerspective.getDataDomain(),
				pathwayDataDomain, recordPerspective, dimensionPerspective, pathway);
		pathwayDataDomain.addTablePerspective(pathwayTablePerspective);

		List<TablePerspective> pathwayTablePerspectives = new ArrayList<>(1);
		pathwayTablePerspectives.add(pathwayTablePerspective);
		for (int i = 1; i < tablePerspectives.size(); i++) {
			pathwayTablePerspectives.add(tablePerspectives.get(i));
		}

		PathwayMultiFormInfo info = new PathwayMultiFormInfo();
		info.pathway = pathway;

		info.age = currentPathwayAge--;
		// pathwayColumn.setLayout(new GLSizeRestrictiveFlowLayout(false, 10, GLPadding.ZERO));
		createMultiformRenderer(pathwayTablePerspectives, EnumSet.of(EEmbeddingID.PATHWAY_LEVEL1,
				EEmbeddingID.PATHWAY_LEVEL2, EEmbeddingID.PATHWAY_LEVEL3, EEmbeddingID.PATHWAY_LEVEL4), pathwayRow,
				Float.NaN, info);
		pathwayLayout.addColumn(info.window);

		int rendererID = info.embeddingIDToRendererIDs.get(EEmbeddingID.PATHWAY_LEVEL1).get(0);
		if (info.multiFormRenderer.getActiveRendererID() != rendererID) {
			info.multiFormRenderer.setActive(rendererID);
		}
		lastUsedLevel1Renderer = info.multiFormRenderer;
		lastUsedRenderer = info.multiFormRenderer;

		pathwayInfos.add(info);
		wasPathwayAdded = true;
	}

	private void createMultiformRenderer(List<TablePerspective> tablePerspectives, EnumSet<EEmbeddingID> embeddingIDs,
			final AnimatedGLElementContainer parent, Object layoutData, MultiFormInfo info) {

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
					pathwayRepresentation.addVertexRepBasedContextMenuItem(new ShowCommonNodeItem(
							ShowCommonNodePathwaysEvent.class, pathEventSpace));
					pathwayRepresentation.addVertexRepBasedContextMenuItem(new VertexRepBasedContextMenuItem(
							"Show Portal Nodes", ShowPortalNodesEvent.class, pathEventSpace));
				}
			}
		}

		final GLPathwayWindow window = new GLPathwayWindow(pathway == null ? "" : pathway.getTitle(), this, info,
				pathway == null);
		if (pathway != null) {
			window.onClose(new ICloseWindowListener() {
				@Override
				public void onWindowClosed(GLWindow w) {
					pathwayLayout.removeWindow(window);
					parent.remove(window);
					pathwayInfos.remove(window.info);
				}
			});
		}
		info.window = window;
		parent.add(window);
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
		augmentation.isDirty = true;
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
		for (PathwayPath segment : pathSegments) {
			for (PathwayMultiFormInfo info : pathwayInfos) {
				pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
						info.multiFormRenderer.getActiveRendererID());
				if (pathwayRepresentation != null && (segment.getPathway() == pathwayRepresentation.getPathway())) {
					pwInfo = info;
					break;
				}
			}
			if (pathwayRepresentation != null && pwInfo != null) {
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
		if (wasPathwayAdded) {
			EnablePathSelectionEvent event = new EnablePathSelectionEvent(isPathSelectionMode);
			event.setEventSpace(pathEventSpace);
			eventPublisher.triggerEvent(event);
			wasPathwayAdded = false;
		}
		// The augmentation has to be updated after the layout was updated in super; updating on relayout would be too
		// early, as the layout is not adapted at that time.
		if (updateAugmentation)
			updatePathLinks();
		// }

	}

	@Override
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID,
			boolean wasTriggeredByUser) {

		if (pathInfo != null && pathInfo.isInitialized()) {

			if (multiFormRenderer == pathInfo.multiFormRenderer) {
				EEmbeddingID embeddingID = pathInfo.getEmbeddingIDFromRendererID(rendererID);
				setPathLevel(embeddingID);
			}
		}
		if (wasTriggeredByUser && rendererID != previousRendererID) {
			for (PathwayMultiFormInfo info : pathwayInfos) {
				if (info.multiFormRenderer == multiFormRenderer) {
					if (info.getEmbeddingIDFromRendererID(rendererID) == EEmbeddingID.PATHWAY_LEVEL1) {
						pathwayLayout.setLevel1(info.window);
						lastUsedLevel1Renderer = info.multiFormRenderer;
					}
					info.age = currentPathwayAge--;
					lastUsedRenderer = info.multiFormRenderer;
					break;
				}
			}

			pathwayRow.relayout();
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
	 * @return the currentActiveBackground, see {@link #activeWindow}
	 */
	public GLWindow getActiveWindow() {
		return activeWindow;
	}

	/**
	 * @param currentActiveBackground
	 *            setter, see {@link currentActiveBackground}
	 */
	public void setActiveWindow(GLWindow activeWindow) {
		this.activeWindow = activeWindow;
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
				IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
						info.multiFormRenderer.getActiveRendererID());
				if (pathwayRepresentation != null) {
					Set<PathwayVertexRep> portalVertexRepsInPathway = PathwayManager.get()
							.getEquivalentVertexRepsInPathway(vertexRep, pathwayRepresentation.getPathway());

					for (PathwayVertexRep portalVertexRep : portalVertexRepsInPathway) {
						Rectangle2D rect = getAbsoluteVertexLocation(pathwayRepresentation, portalVertexRep,
								info.container);
						if (rect != null)
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
		public void onShowPathwaysWithVertex(ShowCommonNodePathwaysEvent event) {
			rankingElement.setFilter(new PathwayFilters.CommonVertexFilter(event.getVertexRep(), false));
			rankingElement.setRanking(new PathwayRankings.CommonVerticesRanking(event.getVertexRep().getPathway()));
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onMinSizeUpdate(MinSizeUpdateEvent event) {
			pathwayRow.relayout();
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowPathwaysWithVertex(ShowCommonNodesPathwaysEvent event) {
			rankingElement.setFilter(new PathwayFilters.CommonVerticesFilter(event.getPathway(), false));
			rankingElement.setRanking(new PathwayRankings.CommonVerticesRanking(event.getPathway()));
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onEnablePathSelection(EnablePathSelectionEvent event) {
			isPathSelectionMode = event.isPathSelectionMode();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.ITablePerspectiveBasedView#getDataSupportDefinition()
	 */
	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.IMultiTablePerspectiveBasedView#addTablePerspective(org.caleydo.core.data.perspective.table
	 * .TablePerspective)
	 */
	@Override
	public void addTablePerspective(TablePerspective newTablePerspective) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.IMultiTablePerspectiveBasedView#addTablePerspectives(java.util.List)
	 */
	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.IMultiTablePerspectiveBasedView#getTablePerspectives()
	 */
	@Override
	public List<TablePerspective> getTablePerspectives() {
		// TODO Auto-generated method stub
		return experimentalDataMappingElement.getTablePerspectives();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.core.view.IMultiTablePerspectiveBasedView#removeTablePerspective(org.caleydo.core.data.perspective
	 * .table.TablePerspective)
	 */
	@Override
	public void removeTablePerspective(TablePerspective tablePerspective) {
		// TODO Auto-generated method stub

	}
}
