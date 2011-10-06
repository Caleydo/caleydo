package org.caleydo.view.grouper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ClusterNodeSelectionListener;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.events.ReplaceDimensionPerspectiveEvent;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.event.view.tablebased.UpdateViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.initialization.ClustererType;
import org.caleydo.core.view.ITableBasedDataDomainView;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import org.caleydo.view.grouper.compositegraphic.ICompositeGraphic;
import org.caleydo.view.grouper.contextmenu.AddGroupsToVisBricksItem;
import org.caleydo.view.grouper.contextmenu.AggregateGroupItem;
import org.caleydo.view.grouper.contextmenu.CopyGroupsItem;
import org.caleydo.view.grouper.contextmenu.CreateGroupItem;
import org.caleydo.view.grouper.contextmenu.DeleteGroupsItem;
import org.caleydo.view.grouper.contextmenu.PasteGroupsItem;
import org.caleydo.view.grouper.contextmenu.RenameGroupItem;
import org.caleydo.view.grouper.drawingstrategies.DrawingStrategyManager;
import org.caleydo.view.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.view.grouper.drawingstrategies.group.IGroupDrawingStrategy;
import org.caleydo.view.grouper.event.CopyGroupsEvent;
import org.caleydo.view.grouper.event.CreateGroupEvent;
import org.caleydo.view.grouper.event.DeleteGroupsEvent;
import org.caleydo.view.grouper.event.PasteGroupsEvent;
import org.caleydo.view.grouper.event.RenameGroupEvent;
import org.caleydo.view.grouper.listener.CopyGroupsListener;
import org.caleydo.view.grouper.listener.CreateGroupListener;
import org.caleydo.view.grouper.listener.DeleteGroupsListener;
import org.caleydo.view.grouper.listener.PasteGroupsListener;
import org.caleydo.view.grouper.listener.RenameGroupListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLGrouper extends AGLView implements ITableBasedDataDomainView,
		IViewCommandHandler, ISelectionUpdateHandler, IClusterNodeEventReceiver {

	public final static String VIEW_TYPE = "org.caleydo.view.grouper";

	boolean useDetailLevel = true;

	private boolean controlPressed = false;
	private double collapseButtonDragOverTime;
	private int draggedOverCollapseButtonID;

	private Integer lastUsedGroupID;

	private boolean hierarchyChanged;
	private boolean potentialNewSelection;

	private GrouperRenderStyle renderStyle;
	private HashMap<Integer, GroupRepresentation> hashGroups;

	private Set<Integer> setCopiedGroups;

	private GroupRepresentation rootGroup;
	private GroupRepresentation potentialNewSelectedGroup;

	private DrawingStrategyManager drawingStrategyManager = null;
	private DragAndDropController dragAndDropController = null;
	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;
	protected SelectionUpdateListener selectionUpdateListener = null;
	protected ClusterNodeSelectionListener clusterNodeSelectionListener = null;

	private CreateGroupListener createGroupListener = null;
	private CopyGroupsListener copyGroupsListener = null;
	private PasteGroupsListener pasteGroupsListener = null;
	private DeleteGroupsListener deleteGroupsListener = null;
	private RenameGroupListener renameGroupListener = null;

	private SelectionManager selectionManager;

	private SelectionType selectionTypeClicked;

	private ClusterTree tree;

	private ATableBasedDataDomain dataDomain;

	private DataTable table;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 */
	public GLGrouper(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;
		hashGroups = new HashMap<Integer, GroupRepresentation>();

		dragAndDropController = new DragAndDropController(this);
		selectionTypeClicked = new SelectionType("Clicked", new float[] { 1.0f, 0.0f,
				1.0f, 0.0f }, 1, true, false, 1.0f);

		renderStyle = new GrouperRenderStyle(viewFrustum);

		glKeyListener = new GLGrouperKeyListener(this);

		draggedOverCollapseButtonID = -1;
		hierarchyChanged = true;
		lastUsedGroupID = 0;
		potentialNewSelection = false;
		// registerEventListeners();
	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	public void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	/**
	 * Creates a new shallow tree for cluster nodes and GroupRepresentations.
	 */
	// private void createNewHierarchy() {
	// ClusterTree tree = new ClusterTree(dataDomain.getDimensionIDType());
	// IGroupDrawingStrategy groupDrawingStrategy = drawingStrategyManager
	// .getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
	// lastUsedGroupID = 0;
	//
	// ClusterNode rootNode = new ClusterNode(tree, "Root", lastUsedGroupID++,
	// true, -1);
	// tree.setRootNode(rootNode);
	//
	// rootGroup = new GroupRepresentation(rootNode, renderStyle,
	// groupDrawingStrategy,
	// drawingStrategyManager, this, false);
	// hashGroups.put(rootGroup.getID(), rootGroup);
	// // selectionManager.initialAdd(rootGroup.getID());
	// ArrayList<Integer> indexList = dimensionVA.getIndexList();
	//
	// for (Integer currentIndex : indexList) {
	//
	// String nodeName = dataDomain.getDimensionLabel(currentIndex);
	// int leafID = currentIndex;
	// ClusterNode currentNode = new ClusterNode(tree, nodeName,
	// lastUsedGroupID++,
	// false, leafID);
	// tree.addChild(rootNode, currentNode);
	//
	// GroupRepresentation groupRep = new GroupRepresentation(currentNode,
	// renderStyle, groupDrawingStrategy, drawingStrategyManager, this, true);
	// rootGroup.add(groupRep);
	//
	// hashGroups.put(groupRep.getID(), groupRep);
	// // selectionManager.initialAdd(groupRep.getID());
	// }
	//
	// rootGroup.calculateHierarchyLevels(0);
	// // ClusterHelper.determineNrElements(tree);
	// // ClusterHelper.determineHierarchyDepth(tree);
	// ClusterHelper.calculateClusterAveragesRecursive(tree, tree.getRoot(),
	// ClustererType.DIMENSION_CLUSTERING, dataDomain.getTable(), table
	// .getDimensionPerspective(dimensionPerspectiveID)
	// .getVirtualArray(),
	// table.getRecordPerspective(recordPerspectiveID).getVirtualArray());
	// table.getDimensionPerspective(dimensionPerspectiveID).setTree(tree);
	//
	// // dataDomain.createDimensionGroupsFromDimensionTree(tree);
	// // useCase.replaceVirtualArray(idCategory, vaType, virtualArray)
	// }

	/**
	 * Creates a new composite GroupRepresentation tree according to the
	 * specified cluster node tree.
	 * 
	 * @param tree
	 *            Tree of cluster nodes that should be used to build a composite
	 *            GroupRepresentation tree.
	 */
	private void initHierarchy(Tree<ClusterNode> tree) {

		ClusterNode rootNode = tree.getRoot();
		rootGroup = new GroupRepresentation(rootNode, renderStyle,
				drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL),
				drawingStrategyManager, this, !tree.hasChildren(rootNode));
		hashGroups.put(rootGroup.getID(), rootGroup);
		// selectionManager.initialAdd(rootGroup.getID());
		lastUsedGroupID = rootGroup.getID();

		buildGroupHierarchyFromTree(tree, rootNode, rootGroup);
		rootGroup.calculateHierarchyLevels(0);
	}

	/**
	 * Recursive method that builds the composite tree of GroupRepresentations.
	 * Thereby each GroupRepresentation object corresponds to one ClusterNode of
	 * the specified tree.
	 * 
	 * @param tree
	 *            Tree of cluster nodes that should be used to build a composite
	 *            GroupRepresentation tree.
	 * @param currentNode
	 *            Current Cluster node for which a GroupRepresentation will be
	 *            created.
	 * @param parentGroupRep
	 *            Parent of the GroupRepresentation that will be created.
	 */
	private void buildGroupHierarchyFromTree(Tree<ClusterNode> tree,
			ClusterNode currentNode, GroupRepresentation parentGroupRep) {

		ArrayList<ClusterNode> alChildren = tree.getChildren(currentNode);
		IGroupDrawingStrategy groupDrawingStrategy = drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);

		for (ClusterNode child : alChildren) {
			boolean bHasChildren = tree.hasChildren(child);
			GroupRepresentation groupRep = new GroupRepresentation(child, renderStyle,
					groupDrawingStrategy, drawingStrategyManager, this, !bHasChildren);
			parentGroupRep.add(groupRep);

			hashGroups.put(groupRep.getID(), groupRep);
			// selectionManager.initialAdd(groupRep.getID());
			if (groupRep.getID() > lastUsedGroupID)
				lastUsedGroupID = groupRep.getID();

			if (bHasChildren) {
				buildGroupHierarchyFromTree(tree, child, groupRep);
			}
		}
	}

	/**
	 * This method updates the cluster node tree in the set (from use case)
	 * according to the structure of the composite GroupRepresentation tree.
	 */
	public void updateClusterTreeAccordingToGroupHierarchy() {
		tree = new ClusterTree(dataDomain.getDimensionIDType(), rootGroup.getChildren()
				.size());
		ClusterNode rootNode = rootGroup.getClusterNode();
		rootNode.setTree(tree);
		tree.setRootNode(rootNode);
		lastUsedGroupID = 0;
		rootGroup.getClusterNode().tableID(lastUsedGroupID++);
		hashGroups.clear();

		selectionManager.clearSelections();
		Set<SelectionType> selectionTypes = rootGroup.getSelectionTypes();
		for (SelectionType selectionType : selectionTypes) {
			if (selectionType != SelectionType.NORMAL)
				selectionManager.addToType(selectionType, rootGroup.getID());
		}

		hashGroups.put(rootGroup.getID(), rootGroup);

		buildTreeFromGroupHierarchy(tree, rootGroup.getClusterNode(), rootGroup);

		ClusterHelper.calculateClusterAveragesRecursive(tree, tree.getRoot(),
				ClustererType.DIMENSION_CLUSTERING, dataDomain.getTable(), table
						.getDimensionPerspective(dimensionPerspectiveID)
						.getVirtualArray(),
				table.getRecordPerspective(recordPerspectiveID).getVirtualArray());

		tree.setDirty();
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(tree, tree.getRoot());

		eventPublisher.triggerEvent(new ReplaceDimensionPerspectiveEvent(dataDomain
				.getDataDomainID(), dimensionPerspectiveID, data));

		UpdateViewEvent event = new UpdateViewEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);

		triggerSelectionEvents();
	}

	/**
	 * Recursive method that builds the a tree of ClusterNodes according to the
	 * composite GroupRepresentation tree. Thereby each GroupRepresentation
	 * object corresponds to one ClusterNode.
	 * 
	 * @param tree
	 *            Tree of cluster nodes that shall recursively be built.
	 * @param parentNode
	 *            Parent of the cluster nodes that will be created in one step.
	 * @param parentGroupRep
	 *            Parent of the GroupRepresentation whose children will be used
	 *            to create cluster nodes.
	 */
	private void buildTreeFromGroupHierarchy(Tree<ClusterNode> tree,
			ClusterNode parentNode, GroupRepresentation parentGroupRep) {
		ArrayList<ICompositeGraphic> alChildren = parentGroupRep.getChildren();

		for (ICompositeGraphic child : alChildren) {
			GroupRepresentation groupRep = (GroupRepresentation) child;
			ClusterNode childNode = groupRep.getClusterNode();
			childNode.setTree(tree);
			childNode.tableID(lastUsedGroupID++);
			tree.addChild(parentNode, childNode);
			if (!child.isLeaf()) {
				buildTreeFromGroupHierarchy(tree, childNode, groupRep);
			}
			// selectionManager.add(groupRep.getID());
			Set<SelectionType> selectionTypes = groupRep.getSelectionTypes();
			for (SelectionType selectionType : selectionTypes) {
				if (selectionType != SelectionType.NORMAL)
					selectionManager.addToType(selectionType, groupRep.getID());
			}
			hashGroups.put(groupRep.getID(), groupRep);
		}
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (useDetailLevel) {
			super.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);

		if (!lazyMode)
			checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		if (bIsDisplayListDirtyRemote) {
			bIsDisplayListDirtyRemote = false;
			buildDisplayList(gl, iGLDisplayListIndexRemote);
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL2 gl) {
		// processEvents();
		gl.glCallList(iGLDisplayListToCall);

		if (glMouseListener.wasMouseReleased() && !dragAndDropController.isDragging()
				&& potentialNewSelection) {

			potentialNewSelection = false;
			dragAndDropController.clearDraggables();
			selectionManager.clearSelection(SelectionType.SELECTION);

			potentialNewSelectedGroup.addAsDraggable(dragAndDropController);

			potentialNewSelectedGroup.setSelectionTypeRec(SelectionType.SELECTION,
					selectionManager);
			selectionManager.addToType(selectionTypeClicked,
					potentialNewSelectedGroup.getID());
			rootGroup.updateSelections(selectionManager, drawingStrategyManager);
			triggerSelectionEvents();
			setDisplayListDirty();
		}
		dragAndDropController.handleDragging(gl, glMouseListener);
	}

	/**
	 * Builds a display list of graphical elements that do not have to be
	 * updated in every frame.
	 * 
	 * @param gl
	 *            GL2 context.
	 * @param iGLDisplayListIndex
	 *            Index of display list.
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		// FIXME: on intel graphics card we cannot use Z=0!
		float Z = 0.0001f;

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.GROUPER_BACKGROUND_SELECTION, 0));

		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0.0f, 0.0f, Z);
		gl.glVertex3f(viewFrustum.getWidth(), 0.0f, Z);
		gl.glVertex3f(viewFrustum.getWidth(), viewFrustum.getHeight(), Z);
		gl.glVertex3f(0.0f, viewFrustum.getHeight(), Z);
		gl.glEnd();
		gl.glPopAttrib();

		gl.glPopName();

		Vec3f vecPosition = new Vec3f(0.0f, viewFrustum.getHeight(), 0.1f);
		rootGroup.setPosition(vecPosition);
		rootGroup.setHierarchyPosition(vecPosition);

		if (hierarchyChanged) {
			rootGroup.calculateHierarchyLevels(0);
			rootGroup.calculateDrawingParameters(gl, textRenderer);

			// rootGroup.printTree();
			// System.out.println("==========================================");
		}
		rootGroup.draw(gl, textRenderer);

		if (hierarchyChanged) {
			float fHierarchyHeight = rootGroup.getScaledHeight(parentGLCanvas.getWidth());
			float fHierarchyWidth = rootGroup.getScaledWidth(parentGLCanvas.getWidth());

			// System.out.println(fHierarchyHeight);
			// System.out.println(fHierarchyWidth);

			int minViewportHeight = (int) (parentGLCanvas.getHeight()
					/ viewFrustum.getHeight() * fHierarchyHeight) + 10;
			int minViewportWidth = (int) (parentGLCanvas.getWidth()
					/ viewFrustum.getWidth() * fHierarchyWidth) + 10;
			renderStyle.setMinViewDimensions(minViewportWidth, minViewportHeight, this);
			hierarchyChanged = false;

			if (parentGLCanvas.getHeight() <= 0) {
				// Draw again in next frame where the viewport size is hopefully
				// correct
				setDisplayListDirty();
				hierarchyChanged = true;
			}
		}

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		switch (pickingType) {

		case GROUPER_GROUP_SELECTION:
			final GroupRepresentation groupRep = hashGroups.get(externalID);
			switch (pickingMode) {

			case CLICKED:
				draggedOverCollapseButtonID = -1;
				if (groupRep != null) {
					if (!controlPressed
							&& !selectionManager.checkStatus(SelectionType.SELECTION,
									groupRep.getID())) {
						dragAndDropController.clearDraggables();
						selectionManager.clearSelection(SelectionType.SELECTION);
						selectionManager.clearSelection(selectionTypeClicked);
					}
					if (!controlPressed) {
						potentialNewSelectedGroup = groupRep;
						potentialNewSelection = true;
					}
					dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
					groupRep.addAsDraggable(dragAndDropController);

					groupRep.setSelectionTypeRec(SelectionType.SELECTION,
							selectionManager);
					selectionManager.addToType(selectionTypeClicked, groupRep.getID());

					if (groupRep.isLeaf())
						rootGroup.updateSelections(selectionManager,
								drawingStrategyManager);
					triggerSelectionEvents();
					setDisplayListDirty();
				}
				break;
			case DRAGGED:
				draggedOverCollapseButtonID = -1;
				if (groupRep != null && dragAndDropController.hasDraggables()) {
					if (!dragAndDropController.isDragging()) {
						if (dragAndDropController.containsDraggable(groupRep)) {
							potentialNewSelection = false;
							dragAndDropController.startDragging();
						}

					}
					if (groupRep.isLeaf()) {
						GroupRepresentation parent = (GroupRepresentation) groupRep
								.getParent();
						if (parent != null)
							dragAndDropController.setDropArea(parent);
					} else {
						dragAndDropController.setDropArea(groupRep);
					}
				}
				break;
			case MOUSE_OVER:
				draggedOverCollapseButtonID = -1;
				if (groupRep != null) {
					if (selectionManager.checkStatus(SelectionType.MOUSE_OVER,
							groupRep.getID())
							|| selectionManager.checkStatus(SelectionType.SELECTION,
									groupRep.getID())) {
						return;
					}
					selectionManager.clearSelection(SelectionType.MOUSE_OVER);
					selectionManager
							.addToType(SelectionType.MOUSE_OVER, groupRep.getID());
					rootGroup.updateSelections(selectionManager, drawingStrategyManager);
					triggerSelectionEvents();
					setDisplayListDirty();
				}
				break;
			case RIGHT_CLICKED:
				if (groupRep != null) {
					boolean bContextMenueItemsAvailable = false;
					if (selectionManager.checkStatus(SelectionType.SELECTION,
							groupRep.getID())
							&& groupRep != rootGroup) {

						Set<Integer> selectedGroups = new HashSet<Integer>(
								selectionManager.getElements(SelectionType.SELECTION));

						Set<Integer> setClickedGroups = new HashSet<Integer>(
								selectionManager.getElements(selectionTypeClicked));
						ArrayList<ICompositeGraphic> orderedComposites = getOrderedCompositeList(
								setClickedGroups, false);

						boolean isLeafContained = false;
						for (ICompositeGraphic composite : orderedComposites) {
							// FIXME here
							// selectedTables.add(((GroupRepresentation)
							// composite)
							// .getClusterNode().getPerspective());

							if (isLeafContained == false)
								isLeafContained = composite.isLeaf();
						}

						RenameGroupItem renameItem = new RenameGroupItem(externalID,
								dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(renameItem);
						// groupRep.addAsDraggable(dragAndDropController);
						//
						// groupRep.setSelectionTypeRec(SelectionType.SELECTION,
						// selectionManager);
						// selectionManager.addToType(selectionTypeClicked,
						// groupRep
						// .getID());
						// rootGroup.updateSelections(selectionManager,
						// drawingStrategyManager);
						// triggerSelectionEvents();
						// setDisplayListDirty();

						CreateGroupItem createGroupItem = new CreateGroupItem(
								selectedGroups, dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(createGroupItem);

						CopyGroupsItem copyGroupsItem = new CopyGroupsItem(
								selectedGroups, dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(copyGroupsItem);

						DeleteGroupsItem deleteGroupsItem = new DeleteGroupsItem(
								selectedGroups, dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(deleteGroupsItem);

						AggregateGroupItem aggregateGroupItem = new AggregateGroupItem(
								selectedGroups, dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(aggregateGroupItem);

						ArrayList<ClusterNode> selectedNodes = new ArrayList<ClusterNode>(
								selectedGroups.size());
						for (Integer groupID : selectedGroups) {
							selectedNodes.add(hashGroups.get(groupID).getClusterNode());
						}

						AddGroupsToVisBricksItem addGroupsToVisBricksItem = new AddGroupsToVisBricksItem(
								dataDomain, dimensionPerspectiveID, recordPerspectiveID,
								selectedNodes);

						contextMenuCreator.addContextMenuItem(addGroupsToVisBricksItem);

						// if (orderedComposites.size() >= 2) {
						//
						// CompareGroupsItem compareGroupsItem = new
						// CompareGroupsItem(
						// selectedTables);
						// contextMenuCreator.addContextMenuItem(compareGroupsItem);
						// }

						contextMenuCreator.addContextMenuItem(new SeparatorMenuItem());

						bContextMenueItemsAvailable = true;

						if (Platform.getBundle("org.caleydo.util.r") != null) {

							// Lazy loading of R
							GeneralManager.get().getRStatisticsPerformer();

							// Do not allow p-value stats for multiple groups or
							// leaf meta sets
							// FIXME re-enable
							// if (!isLeafContained && orderedComposites.size()
							// < 2) {
							// StatisticsPValueReductionItem pValueReductionItem
							// = new StatisticsPValueReductionItem(
							// selectedTables);
							// contextMenuCreator
							// .addContextMenuItem(pValueReductionItem);
							// }
							//
							// if (orderedComposites.size() == 2) {
							// StatisticsFoldChangeReductionItem
							// foldChangeReductionItem = new
							// StatisticsFoldChangeReductionItem(
							// selectedTables.get(0), selectedTables.get(1));
							// contextMenuCreator
							// .addContextMenuItem(foldChangeReductionItem);
							//
							// StatisticsTwoSidedTTestReductionItem
							// twoSidedTTestReductionItem = new
							// StatisticsTwoSidedTTestReductionItem(
							// selectedTables);
							// contextMenuCreator
							// .addContextMenuItem(twoSidedTTestReductionItem);
							// }
						}

					}
					if (setCopiedGroups != null
							&& !setCopiedGroups.contains(groupRep.getID())
							&& !groupRep.isLeaf()) {
						PasteGroupsItem pasteGroupItem = new PasteGroupsItem(
								groupRep.getID(), dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(pasteGroupItem);
						bContextMenueItemsAvailable = true;
					}
				}
				break;
			default:
				return;
			}
			break;

		case GROUPER_BACKGROUND_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				draggedOverCollapseButtonID = -1;
				dragAndDropController.clearDraggables();
				selectionManager.clearSelections();
				rootGroup.updateSelections(selectionManager, drawingStrategyManager);
				triggerSelectionEvents();
				setDisplayListDirty();
				break;
			default:
				return;
			}
			break;

		case GROUPER_COLLAPSE_BUTTON_SELECTION:
			GroupRepresentation group = hashGroups.get(externalID);
			switch (pickingMode) {
			case CLICKED:
				draggedOverCollapseButtonID = -1;
				if (group != null) {
					group.setCollapsed(!group.isCollapsed());
					setDisplayListDirty();
				}
				break;
			case DRAGGED:
				if (group != null && group.isCollapsed()) {
					double dCurrentTimeStamp = Calendar.getInstance().getTimeInMillis();

					if (dCurrentTimeStamp - collapseButtonDragOverTime > 500
							&& group.getID() == draggedOverCollapseButtonID) {
						group.setCollapsed(false);
						draggedOverCollapseButtonID = -1;
						setDisplayListDirty();
						return;
					}
					if (group.getID() != draggedOverCollapseButtonID)
						collapseButtonDragOverTime = dCurrentTimeStamp;
					draggedOverCollapseButtonID = group.getID();
				}
			default:
				return;
			}
			break;
		}
	}

	/**
	 * Triggers events for currently selected elements.
	 */
	private void triggerSelectionEvents() {

		// ClearSelectionsEvent clearSelectionsEvent = new
		// ClearSelectionsEvent();
		// clearSelectionsEvent.setSender(this);
		// eventPublisher.triggerEvent(clearSelectionsEvent);

		SelectionDelta clusterIDDelta = selectionManager.getDelta();

		ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
		event.setSender(this);
		event.setSelectionDelta(clusterIDDelta);
		eventPublisher.triggerEvent(event);

		SelectionDelta delta = new SelectionDelta(dataDomain.getDimensionIDType());
		for (SelectionDeltaItem item : clusterIDDelta.getAllItems()) {
			GroupRepresentation groupRep = hashGroups.get(item.getID());
			if (groupRep != null && groupRep.isLeaf()) {
				ClusterNode clusterNode = groupRep.getClusterNode();
				if (item.isRemove())
					delta.removeSelection(clusterNode.getLeafID(),
							item.getSelectionType());
				else
					delta.addSelection(clusterNode.getLeafID(), item.getSelectionType());
			}
		}

		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSender(this);
		selectionUpdateEvent.setDataDomainID(dataDomain.getDataDomainID());
		selectionUpdateEvent.setSelectionDelta(delta);
		eventPublisher.triggerEvent(selectionUpdateEvent);
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		// nothing to do because histogram has no selections
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedGrouperView serializedForm = new SerializedGrouperView(
				dataDomain.getDataDomainID());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		createGroupListener = new CreateGroupListener();
		createGroupListener.setHandler(this);
		createGroupListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(CreateGroupEvent.class, createGroupListener);

		copyGroupsListener = new CopyGroupsListener();
		copyGroupsListener.setHandler(this);
		copyGroupsListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(CopyGroupsEvent.class, copyGroupsListener);

		pasteGroupsListener = new PasteGroupsListener();
		pasteGroupsListener.setHandler(this);
		pasteGroupsListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(PasteGroupsEvent.class, pasteGroupsListener);

		deleteGroupsListener = new DeleteGroupsListener();
		deleteGroupsListener.setHandler(this);
		deleteGroupsListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(DeleteGroupsEvent.class, deleteGroupsListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		clusterNodeSelectionListener = new ClusterNodeSelectionListener();
		clusterNodeSelectionListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class,
				clusterNodeSelectionListener);

		renameGroupListener = new RenameGroupListener();
		renameGroupListener.setHandler(this);
		renameGroupListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RenameGroupEvent.class, renameGroupListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
		if (createGroupListener != null) {
			eventPublisher.removeListener(createGroupListener);
			createGroupListener = null;
		}
		if (copyGroupsListener != null) {
			eventPublisher.removeListener(copyGroupsListener);
			copyGroupsListener = null;
		}
		if (pasteGroupsListener != null) {
			eventPublisher.removeListener(pasteGroupsListener);
			pasteGroupsListener = null;
		}
		if (deleteGroupsListener != null) {
			eventPublisher.removeListener(deleteGroupsListener);
			deleteGroupsListener = null;
		}
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (clusterNodeSelectionListener != null) {
			eventPublisher.removeListener(clusterNodeSelectionListener);
			clusterNodeSelectionListener = null;
		}

		if (renameGroupListener != null) {
			eventPublisher.removeListener(renameGroupListener);
			renameGroupListener = null;
		}
	}

	/**
	 * @return True if the control key is pressed, false otherwise.
	 */
	public boolean isControlPressed() {
		return controlPressed;
	}

	/**
	 * Sets whether the control key is pressed or not.
	 * 
	 * @param bControlPressed
	 *            Specifies if the control key is pressed.
	 */
	public void setControlPressed(boolean bControlPressed) {
		this.controlPressed = bControlPressed;
	}

	/**
	 * @return True if the hierarchy (composite GroupRepresentation tree) has
	 *         changed, false otherwise.
	 */
	public boolean isHierarchyChanged() {
		return hierarchyChanged;
	}

	/**
	 * Sets whether the hierarchy (composite GroupRepresentation tree) was
	 * changed or not.
	 * 
	 * @param bHierarchyChanged
	 *            Specifies if the hierarchy has changed.
	 */
	public void setHierarchyChanged(boolean bHierarchyChanged) {
		this.hierarchyChanged = bHierarchyChanged;
	}

	/**
	 * Adds a GroupRepresentation object to the registered GroupRepresentations.
	 * 
	 * @param iID
	 *            ID for the GroupRepresentation object.
	 * @param groupRepresentation
	 *            The GroupRepresentation object to be added.
	 */
	public void addGroupRepresentation(int iID, GroupRepresentation groupRepresentation) {
		hashGroups.put(iID, groupRepresentation);
	}

	// public void addVAElementRepresentation(int iID, VAElementRepresentation
	// elementRepresentation) {
	// hashElements.put(iID, elementRepresentation);
	// }

	/**
	 * Removes/unregisters a GroupRepresentation object.
	 * 
	 * @param iID
	 *            ID of the GroupRepresentation object that shall be removed.
	 */
	public void removeGroupRepresentation(int iID) {
		hashGroups.remove(iID);
	}

	// public void removeVAElementRepresentation(int iID) {
	// hashElements.remove(iID);
	// }

	/**
	 * Returns the ordered list of composites that correspond to the specified
	 * set of group IDs. The ordering is given by the appearance of the
	 * composites in the tree, hence their visual appearance from top to bottom
	 * of this view.
	 * 
	 * @param setGroupIds
	 *            IDs of the composites that should be retrieved.
	 * @param topLevelElementsOnly
	 *            Specifies whether the list should only contain top level
	 *            composites, i.e. if the specified set of ids contains
	 *            composites with parent-child relation, this parameter
	 *            determines if only the parents or parents and children should
	 *            be added to the list.
	 * @return The ordered list of composites that correspond to the specified
	 *         set of group IDs
	 */
	private ArrayList<ICompositeGraphic> getOrderedCompositeList(
			Set<Integer> setGroupIds, boolean topLevelElementsOnly) {

		Set<ICompositeGraphic> setComposites = new HashSet<ICompositeGraphic>();
		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = new ArrayList<ICompositeGraphic>();

		for (Integer id : setGroupIds) {
			if (hashGroups.containsKey(id))
				setComposites.add(hashGroups.get(id));
		}

		rootGroup.getOrderedCompositeList(setComposites, alOrderedTopLevelComposites,
				topLevelElementsOnly);

		return alOrderedTopLevelComposites;
	}

	/**
	 * Creates a new Group as child of the common parent of the specified
	 * GroupRepresentations. If all of the specified GroupRepresentations have
	 * the same parent, they will be the children of the newly created group,
	 * otherwise the newly created group's children are copies of them.
	 * 
	 * @param setContainedGroups
	 *            IDs of the GroupRepresentations a new parent shall be created
	 *            for.
	 */
	public void createNewGroup(Set<Integer> setContainedGroups) {

		tree = new ClusterTree(dataDomain.getDimensionIDType(), 3);
		GroupRepresentation newGroup = new GroupRepresentation(new ClusterNode(tree,
				"group" + lastUsedGroupID, lastUsedGroupID++, false, -1), renderStyle,
				drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL),
				drawingStrategyManager, this, false);

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedCompositeList(
				setContainedGroups, true);

		String groupName = determineNodeLabel(alOrderedTopLevelComposites);
		if (groupName != "")
			newGroup.getClusterNode().setLabel(groupName);

		ICompositeGraphic commonParent = findCommonParent(alOrderedTopLevelComposites);

		if (commonParent == null)
			return;
		boolean bSharedParent = true;

		for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
			if (composite.getParent() != commonParent) {
				bSharedParent = false;
				break;
			}
		}

		if (bSharedParent) {
			commonParent.replaceChild(alOrderedTopLevelComposites.get(0), newGroup);
			for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
				composite.setParent(newGroup);
				commonParent.delete(composite);
				newGroup.add(composite);
			}
		} else {
			commonParent.add(newGroup);
			int iTempID[] = { lastUsedGroupID };
			for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
				iTempID[0]++;
				ICompositeGraphic copy = composite
						.createDeepCopyWithNewIDs(tree, iTempID);
				copy.setParent(newGroup);
				newGroup.add(copy);
			}
			lastUsedGroupID = iTempID[0] + 1;
		}

		newGroup.setParent(commonParent);

		hashGroups.put(newGroup.getID(), newGroup);

		hierarchyChanged = true;

		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	/**
	 * Determine a node label for the new node
	 * 
	 * @param dimensionIDs
	 * @return
	 */
	private String determineNodeLabel(ArrayList<ICompositeGraphic> dimensionIDs) {

		String baseLabel = null;
		for (ICompositeGraphic dimensionID : dimensionIDs) {
			String currentLabel = dimensionID.getName();
			if (baseLabel == null)
				baseLabel = currentLabel;
			else {
				baseLabel = getCommonBeginning(baseLabel, currentLabel);
				if (baseLabel == "") {

					break;
				}
			}
		}
		return baseLabel;
	}

	private String getCommonBeginning(String baseLabel, String newString) {
		String result = "";

		char[] baseLabelAR = baseLabel.toCharArray();
		char[] newStringAR = newString.toCharArray();
		for (int index = 0; index < baseLabelAR.length; index++) {
			if (index == newStringAR.length)
				return result;

			if (baseLabelAR[index] == newStringAR[index]) {
				result += baseLabelAR[index];
			} else
				return result;

		}
		return result;
	}

	/**
	 * Finds the common parent of the specified composites, i.e. the first
	 * composite that all composites have along their parent paths.
	 * 
	 * @param alComposites
	 *            Composites whose common parent shall be found for.
	 * @return The common parent if there exists one, null otherwise.
	 */
	private ICompositeGraphic findCommonParent(ArrayList<ICompositeGraphic> alComposites) {

		ICompositeGraphic compositeWithLowestHierarchyLevel = null;
		int iLowestHierarchyLevel = Integer.MAX_VALUE;

		for (ICompositeGraphic composite : alComposites) {
			if (composite.getHierarchyLevel() < iLowestHierarchyLevel) {
				iLowestHierarchyLevel = composite.getHierarchyLevel();
				compositeWithLowestHierarchyLevel = composite;
			}
		}

		ICompositeGraphic commonParent = null;
		int iNumberOfCompositesWithCommonParent = 0;

		while (commonParent == null) {
			ICompositeGraphic commonParentGuess = compositeWithLowestHierarchyLevel
					.getParent();
			if (commonParentGuess == null)
				return null;

			for (ICompositeGraphic composite : alComposites) {
				if (composite.hasParent(commonParentGuess))
					iNumberOfCompositesWithCommonParent++;
			}
			if (iNumberOfCompositesWithCommonParent == alComposites.size()) {
				commonParent = commonParentGuess;
			} else {
				compositeWithLowestHierarchyLevel = commonParentGuess;
				iNumberOfCompositesWithCommonParent = 0;
			}
		}

		return commonParent;
	}

	public void addNewSelectionID(int iID) {
		// selectionManager.add(iID);
	}

	/**
	 * Sets the groups that shall be copied to the specified set of groups.
	 * 
	 * @param setGroupsToCopy
	 *            IDs of groups that shall be copied.
	 */
	public void copyGroups(Set<Integer> setGroupsToCopy) {
		setCopiedGroups = setGroupsToCopy;
	}

	/**
	 * Pastes copied groups as children of the specified group.
	 * 
	 * @param iParentGroupID
	 *            ID of the group where the copied groups should be pasted in.
	 */
	public void pasteGroups(int iParentGroupID) {
		tree = new ClusterTree(dataDomain.getDimensionIDType(), 3);
		GroupRepresentation parent = hashGroups.get(iParentGroupID);

		if (parent == null || setCopiedGroups == null || parent.isLeaf())
			return;

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedCompositeList(
				setCopiedGroups, true);

		int iTempID[] = { lastUsedGroupID };
		for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
			iTempID[0]++;
			ICompositeGraphic copy = composite.createDeepCopyWithNewIDs(tree, iTempID);
			parent.add(copy);
		}
		lastUsedGroupID = iTempID[0] + 1;

		hierarchyChanged = true;
		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	/**
	 * Removes the specified groups from the composite GroupRepresentation tree.
	 * 
	 * @param setGroupsToDelete
	 *            IDs of groups that should be deleted.
	 */
	public void deleteGroups(Set<Integer> setGroupsToDelete) {

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedCompositeList(
				setGroupsToDelete, true);

		for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
			ICompositeGraphic parent = composite.getParent();
			if (parent != null) {
				parent.delete(composite);
				composite.setParent(null);
				if (parent != rootGroup)
					parent.removeOnChildAbsence();
			}
		}

		hierarchyChanged = true;
		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType() == selectionManager.getIDType()
				|| selectionDelta.getIDType() == dataDomain.getDimensionIDType()) {
			Collection<SelectionDeltaItem> deltaItems = selectionDelta.getAllItems();
			Tree<ClusterNode> experimentTree = table.getDimensionPerspective(
					dimensionPerspectiveID).getTree();

			if (experimentTree != null) {
				// selectionManager.clearSelections();
				dragAndDropController.clearDraggables();

				for (SelectionDeltaItem item : deltaItems) {
					ArrayList<Integer> alNodeIDs = experimentTree
							.getNodeIDsFromLeafID(item.getID());

					for (Integer nodeID : alNodeIDs) {
						GroupRepresentation groupRep = hashGroups.get(nodeID);

						if (groupRep == null)
							continue;
						if (item.isRemove()) {
							groupRep.setSelectionTypeRec(SelectionType.NORMAL,
									selectionManager);
							selectionManager.remove(nodeID);
						} else {
							if (item.getSelectionType() == SelectionType.SELECTION) {
								groupRep.addAsDraggable(dragAndDropController);
							}

							groupRep.setSelectionTypeRec(item.getSelectionType(),
									selectionManager);

						}

					}
					rootGroup.updateSelections(selectionManager, drawingStrategyManager);
				}
				setDisplayListDirty();
			}
		}

	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {
		SelectionDelta selectionDelta = event.getSelectionDelta();

		if (selectionDelta.getIDType() == selectionManager.getIDType()) {

			selectionManager.clearSelections();
			selectionManager.setDelta(selectionDelta);
			rootGroup.updateSelections(selectionManager, drawingStrategyManager);
			setDisplayListDirty();
		}

	}

	/**
	 * Triggers a dialog to rename the specified group.
	 * 
	 * @param groupID
	 *            ID of the group that shall be renamed.
	 */
	public void renameGroup(int groupID) {
		final GroupRepresentation groupRep = hashGroups.get(groupID);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				ChangeGroupNameDialog.run(PlatformUI.getWorkbench().getDisplay(),
						groupRep);
				// groupRep.getClusterNode().getSubDataTable()
				// .setLabel(groupRep.getClusterNode().getLabel());
				setDisplayListDirty();
			}
		});

		// groupRep.getClusterNode().getSubDataTable().setLabel(groupRep.getClusterNode().getNodeName());
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {

		if (this.dataDomain == dataDomain)
			return;

		this.dataDomain = dataDomain;
		table = this.dataDomain.getTable();

		// FIXME: we need to determine which perspective to use!
		if (dataDomain.getDimensionPerspectiveIDs().iterator().hasNext())
			dimensionPerspectiveID = dataDomain.getDimensionPerspectiveIDs().iterator()
					.next();
		if (dataDomain.getRecordPerspectiveIDs().iterator().hasNext())
			recordPerspectiveID = dataDomain.getRecordPerspectiveIDs().iterator().next();

		dimensionVA = table.getDimensionPerspective(dimensionPerspectiveID)
				.getVirtualArray();
		drawingStrategyManager = new DrawingStrategyManager(dimensionPerspectiveID,
				pickingManager, uniqueID, renderStyle);
		DimensionPerspective dimensionPerspective = table
				.getDimensionPerspective(dimensionPerspectiveID);
		if (dimensionPerspective.getTree() != null) {
			tree = table.getDimensionPerspective(dimensionPerspectiveID).getTree();
			initHierarchy(tree);
		} else {
			throw new IllegalStateException("No tree available for "
					+ dimensionPerspectiveID);
		}

		selectionManager = new SelectionManager(tree.getNodeIDType());

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				selectionTypeClicked);
		eventPublisher.triggerEvent(selectionTypeEvent);

		selectionManager.addTypeToDeltaBlacklist(selectionTypeClicked);
		
		setDisplayListDirty();
	}

	@Override
	public void setRecordPerspectiveID(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
	}

	@Override
	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
		this.dimensionPerspectiveID = dimensionPerspectiveID;
	}
}
