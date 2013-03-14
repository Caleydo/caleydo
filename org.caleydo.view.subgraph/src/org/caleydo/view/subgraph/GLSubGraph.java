package org.caleydo.view.subgraph;

import gleem.linalg.Vec2f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
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
import org.caleydo.core.event.EventPublisher;
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
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.GLElementAdapter;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
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
import org.caleydo.view.subgraph.GLSubGraphAugmentation.PortalHighlightRenderer;
import org.caleydo.view.subgraph.GLWindow.ICloseWindowListener;
import org.caleydo.view.subgraph.MultiLevelSlideInElement.IWindowState;
import org.caleydo.view.subgraph.SlideInElement.ESlideInElementPosition;
import org.caleydo.view.subgraph.contextmenu.ShowCommonNodeItem;
import org.caleydo.view.subgraph.datamapping.GLExperimentalDataMapping;
import org.caleydo.view.subgraph.event.HighlightAllPortalsEvent;
import org.caleydo.view.subgraph.event.ShowCommonNodePathwaysEvent;
import org.caleydo.view.subgraph.event.ShowCommonNodesPathwaysEvent;
import org.caleydo.view.subgraph.event.ShowNodeInfoEvent;
import org.caleydo.view.subgraph.event.ShowPortalLinksEvent;
import org.caleydo.view.subgraph.ranking.PathwayFilters;
import org.caleydo.view.subgraph.ranking.PathwayRankings;
import org.caleydo.view.subgraph.ranking.RankingElement;
import org.caleydo.view.subgraph.toolbar.HighlightAllPortalsAction;
import org.caleydo.view.subgraph.toolbar.ShowPortalsAction;
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

	private ShowPortalsAction showPortalsButton;
	private HighlightAllPortalsAction highlightAllPortalsButton;

	protected GLWindow rankingWindow;

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
	 * All portals currently present.
	 */
	protected Set<PathwayVertexRep> portals = new HashSet<>();

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
	 * Determines whether path selection mode is currently active.
	 */
	protected boolean isPathSelectionMode = false;

	/**
	 * Determines whether a new pathway was recently added. This information is needed to send events when dependent
	 * views need to be initialized in the first display cycle.
	 */
	protected boolean wasPathwayAdded = false;

	/**
	 * Determines whether the path window is maximized.
	 */
	protected boolean isPathWindowMaximized = false;

	/**
	 * Determines whether portal highlighting is currently enabled.
	 */
	protected boolean isHighlightPortals = false;

	private boolean isAltKeyDown = false;
	private boolean isShiftKeyDown = false;

	private final DragAndDropController dndController = new DragAndDropController(this);

	/**
	 * The element that shows the ranked pathway list
	 */
	protected RankingElement rankingElement;

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
		SlideInElement slideInElement = new SlideInElement(dataMappingWindow, ESlideInElementPosition.TOP);
		dataMappingWindow.addSlideInElement(slideInElement);

		column.add(dataMappingWindow);
		// column.add(nodeInfoContainer);
		rankingWindow = new GLWindow("Pathways", this);
		rankingWindow.setSize(150, Float.NaN);
		rankingElement = new RankingElement(this);
		rankingWindow.setContent(rankingElement);
		slideInElement = new SlideInElement(rankingWindow, ESlideInElementPosition.RIGHT);
		rankingWindow.addSlideInElement(slideInElement);
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
		MultiLevelSlideInElement slideInElement = new MultiLevelSlideInElement(pathInfo.window,
				ESlideInElementPosition.LEFT);
		slideInElement.addWindowState(new IWindowState() {

			@Override
			public void apply() {
				rankingWindow.setVisibility(EVisibility.VISIBLE);
				pathwayRow.setVisibility(EVisibility.VISIBLE);
				rankingWindow.setVisibility(EVisibility.VISIBLE);
				pathInfo.window.setLayoutData(Float.NaN);
				pathInfo.window.setSize(1, Float.NaN);
				pathInfo.window.background.setVisibility(EVisibility.NONE);
				pathInfo.window.baseContainer.setVisibility(EVisibility.NONE);
				isPathWindowMaximized = false;
			}
		});
		IWindowState currentWindowState = new IWindowState() {

			@Override
			public void apply() {
				if (isPathWindowMaximized) {
					baseContainer.remove(0);
				}
				rankingWindow.setVisibility(EVisibility.VISIBLE);
				pathwayRow.setVisibility(EVisibility.VISIBLE);
				rankingWindow.setVisibility(EVisibility.VISIBLE);
				pathInfo.window.background.setVisibility(EVisibility.PICKABLE);
				pathInfo.window.baseContainer.setVisibility(EVisibility.VISIBLE);
				isPathWindowMaximized = false;
				setPathLevel(pathInfo.getEmbeddingIDFromRendererID(pathInfo.multiFormRenderer.getActiveRendererID()));
			}
		};
		slideInElement.addWindowState(currentWindowState);
		slideInElement.addWindowState(new IWindowState() {

			@Override
			public void apply() {
				rankingWindow.setVisibility(EVisibility.NONE);
				// Adding an element to get the gap is not so nice...
				GLElement element = new GLElement();
				element.setSize(0, Float.NaN);
				baseContainer.add(0, element);
				pathwayRow.setVisibility(EVisibility.NONE);
				pathInfo.window.setLayoutData(Float.NaN);
				pathInfo.window.setSize(Float.NaN, Float.NaN);
				pathInfo.window.background.setVisibility(EVisibility.PICKABLE);
				pathInfo.window.baseContainer.setVisibility(EVisibility.VISIBLE);
				isPathWindowMaximized = true;
			}
		});
		slideInElement.setCurrentWindowState(currentWindowState);

		pathInfo.window.addSlideInElement(slideInElement);
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

	/**
	 * @return the dndController, see {@link #dndController}
	 */
	public DragAndDropController getDndController() {
		return dndController;
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
				update(e);
			}

			@Override
			public void keyReleased(IKeyEvent e) {
				update(e);
			}

			private void update(IKeyEvent e) {
				boolean isPPressed = e.isKeyDown('p');
				augmentation.showPortals(isPPressed);
				showPortalsButton.setChecked(isPPressed);
				boolean isOPressed = e.isKeyDown('o');
				HighlightAllPortalsEvent event = new HighlightAllPortalsEvent(isOPressed);
				event.setEventSpace(pathEventSpace);
				EventPublisher.INSTANCE.triggerEvent(event);
				highlightAllPortalsButton.setChecked(isOPressed);
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
		addPortalsOfPathway(info);
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
					updateAllPortals();
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

		// augmentation.clearRenderers();
		// PathwayVertexRep start = null;
		// PathwayVertexRep end = null;
		// List<Pair<MultiFormRenderer, GLElement>> rendererList = multiFormRenderers.get(EEmbeddingID.PATHWAY_MULTIFORM
		// .id());
		// if (rendererList == null)
		// return;

		// for (PathwayPath path : pathSegments) {
		// if (start == null) {
		// start = path.getPath().getEndVertex();
		// } else {
		// end = path.getPath().getStartVertex();
		// // draw link
		// //
		// PathwayVertexRep referenceVertexRep = start;
		// Rectangle2D referenceRectangle = null;
		// for (PathwayMultiFormInfo info : pathwayInfos) {
		// IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
		// info.multiFormRenderer.getActiveRendererID());
		// if (pathwayRepresentation != null
		// && pathwayRepresentation.getPathway() == referenceVertexRep.getPathway()) {
		// referenceRectangle = getAbsoluteVertexLocation(pathwayRepresentation, referenceVertexRep,
		// info.container);
		// break;
		// }
		// }
		// if (referenceRectangle == null)
		// return;
		//
		// for (PathwayMultiFormInfo info : pathwayInfos) {
		// IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
		// info.multiFormRenderer.getActiveRendererID());
		//
		// if (pathwayRepresentation != null) {
		//
		// // for (PathwayVertexRep vertexRep : vertexReps) {
		// Rectangle2D rect = getAbsoluteVertexLocation(pathwayRepresentation, end, info.container);
		// if (rect != null) {
		// // augmentation.addConnectionRenderer(new GLSubGraphAugmentation.ConnectionRenderer(referenceRectangle,
		// // rect));
		// }
		// // }
		// }
		// }
		// //
		// start = path.getPath().getEndVertex();
		// end = null;
		// }
		// }
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

	public void updateAugmentation(){
		updatePathLinks();
		updatePortalHighlights();
	}

	public void setLayoutDirty()
	{
		isLayoutDirty=true;
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
		if (updateAugmentation) {
			updateAugmentation();
		}
		// }

		// call after all other rendering because it calls the onDrag
		// methods
		// which need alpha blending...
		dndController.handleDragging(gl, glMouseListener);

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
			if (!isPathWindowMaximized) {
				pathInfo.window.setSize(Float.NaN, Float.NaN);
				pathInfo.window.setLayoutData(0.5f);
			}
		} else if (embeddingID == EEmbeddingID.PATH_LEVEL2) {
			if (!isPathWindowMaximized) {
				pathInfo.window.setLayoutData(Float.NaN);
				pathInfo.window.setSize(150, Float.NaN);
			}
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

		// @ListenTo(restrictExclusiveToEventSpace = true)
		// public void onShowNodeInfo(ShowNodeInfoEvent event) {
		// GLNodeInfo nodeInfo = new GLNodeInfo(event.getVertexRep());
		// nodeInfo.setSize(80, 80);
		// nodeInfoContainer.add(nodeInfo, 200, new InOutTransitionBase(InOutInitializers.BOTTOM,
		// MoveTransitions.MOVE_LINEAR));
		// nodeInfoContainer.setSize(Float.NaN, 80);
		// nodeInfoContainer.relayout();
		// }

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowPathwaysWithVertex(ShowCommonNodePathwaysEvent event) {
			rankingElement.setFilter(new PathwayFilters.CommonVertexFilter(event.getVertexRep(), false));
			rankingElement.setRanking(new PathwayRankings.CommonVerticesRanking(event.getVertexRep().getPathway()));
			isLayoutDirty = true;
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onMinSizeUpdate(MinSizeUpdateEvent event) {
			pathwayRow.relayout();
			isLayoutDirty = true;
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowPathwaysWithVertex(ShowCommonNodesPathwaysEvent event) {
			rankingElement.setFilter(new PathwayFilters.CommonVerticesFilter(event.getPathway(), false));
			rankingElement.setRanking(new PathwayRankings.CommonVerticesRanking(event.getPathway()));
			isLayoutDirty = true;
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onEnablePathSelection(EnablePathSelectionEvent event) {
			isPathSelectionMode = event.isPathSelectionMode();
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onHighlightAllPortals(HighlightAllPortalsEvent event) {
			isHighlightPortals = event.isHighlight();
			updatePortalHighlights();
		}

		@ListenTo(restrictExclusiveToEventSpace = true)
		public void onShowPortalLinks(ShowPortalLinksEvent event) {
			augmentation.showPortals(event.isShowPortalLinks());
		}
	}

	protected void updateAllPortals() {
		portals.clear();
		for (PathwayMultiFormInfo info : pathwayInfos) {
			addPortalsOfPathway(info);
		}
		updatePortalHighlights();
	}

	protected void addPortalsOfPathway(PathwayMultiFormInfo info) {
		for (PathwayVertexRep vertexRep : info.pathway.vertexSet()) {
			for (PathwayMultiFormInfo i : pathwayInfos) {
				if (info != i) {
					if (!portals.contains(vertexRep)) {
						Set<PathwayVertexRep> vertexReps = PathwayManager.get().getEquivalentVertexRepsInPathway(
								vertexRep, i.pathway);
						if (vertexReps.size() > 0) {
							portals.addAll(vertexReps);
							portals.add(vertexRep);
						}
					}
				} else {
					for (PathwayVertexRep v : i.pathway.vertexSet()) {
						if (v != vertexRep && PathwayManager.get().areVerticesEquivalent(vertexRep, v)) {
							portals.add(vertexRep);
							portals.add(v);
						}
					}
				}
			}
		}
		updatePortalHighlights();
	}

	protected void updatePortalHighlights() {
		augmentation.clearPortalHighlightRenderers();
		if (isHighlightPortals) {
			for (PathwayVertexRep vertexRep : portals) {
				for (PathwayMultiFormInfo info : pathwayInfos) {
					IPathwayRepresentation pathwayRepresentation = getPathwayRepresentation(info.multiFormRenderer,
							info.multiFormRenderer.getActiveRendererID());
					if (pathwayRepresentation != null) {
						Rectangle2D rect = getAbsoluteVertexLocation(pathwayRepresentation, vertexRep, info.container);
						if (rect != null)
							augmentation.addPortalHighlightRenderer(new PortalHighlightRenderer(rect));
					}
				}
			}
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

	/**
	 * @return the portals, see {@link #portals}
	 */
	public Set<PathwayVertexRep> getPortals() {
		return portals;
	}

	/**
	 * @param showPortalsButton
	 *            setter, see {@link showPortalsButton}
	 */
	public void setShowPortalsButton(ShowPortalsAction showPortalsButton) {
		this.showPortalsButton = showPortalsButton;
	}

	/**
	 * @param highlightAllPortalsButton
	 *            setter, see {@link highlightAllPortalsButton}
	 */
	public void setHighlightAllPortalsButton(HighlightAllPortalsAction highlightAllPortalsButton) {
		this.highlightAllPortalsButton = highlightAllPortalsButton;
	}
}
