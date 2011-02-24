package org.caleydo.view.grouper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ClusterNodeSelectionListener;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.StatisticsFoldChangeReductionItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.StatisticsPValueReductionItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.StatisticsTwoSidedTTestReductionItem;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import org.caleydo.view.grouper.compositegraphic.ICompositeGraphic;
import org.caleydo.view.grouper.contextmenu.CompareGroupsItem;
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

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public class GLGrouper extends AGLView implements IDataDomainSetBasedView,
		IViewCommandHandler, ISelectionUpdateHandler, IClusterNodeEventReceiver {

	public final static String VIEW_ID = "org.caleydo.view.grouper";

	boolean bUseDetailLevel = true;

	private boolean bControlPressed = false;
	private double dCollapseButtonDragOverTime;
	private int iDraggedOverCollapseButtonID;

	private Integer iLastUsedGroupID;

	private boolean bHierarchyChanged;
	private boolean bPotentialNewSelection;

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

	private ASetBasedDataDomain dataDomain;

	private ISet set;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 */
	public GLGrouper(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = VIEW_ID;
		hashGroups = new HashMap<Integer, GroupRepresentation>();

		dragAndDropController = new DragAndDropController(this);
		selectionTypeClicked = new SelectionType("Clicked", new float[] { 1.0f, 0.0f,
				1.0f, 0.0f }, 1, true, false, 1.0f);

		renderStyle = new GrouperRenderStyle(viewFrustum);

		glKeyListener = new GLGrouperKeyListener(this);

		iDraggedOverCollapseButtonID = -1;
		bHierarchyChanged = true;
		iLastUsedGroupID = 0;
		bPotentialNewSelection = false;
		// registerEventListeners();
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL2 canvas
		parentGLCanvas.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	/**
	 * Creates a new shallow tree for cluster nodes and GroupRepresentations.
	 */
	private void createNewHierarchy() {
		ClusterTree tree = new ClusterTree(dataDomain.getStorageIDType());
		IGroupDrawingStrategy groupDrawingStrategy = drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
		iLastUsedGroupID = 0;

		ClusterNode rootNode = new ClusterNode(tree, "Root", iLastUsedGroupID++, true, -1);
		tree.setRootNode(rootNode);

		rootGroup = new GroupRepresentation(rootNode, renderStyle, groupDrawingStrategy,
				drawingStrategyManager, this, false);
		hashGroups.put(rootGroup.getID(), rootGroup);
		// selectionManager.initialAdd(rootGroup.getID());
		ArrayList<Integer> indexList = storageVA.getIndexList();

		for (Integer currentIndex : indexList) {

			String nodeName = set.get(currentIndex).getLabel();
			int leafID = currentIndex;
			ClusterNode currentNode = new ClusterNode(tree, nodeName, iLastUsedGroupID++,
					false, leafID);
			tree.addChild(rootNode, currentNode);

			GroupRepresentation groupRep = new GroupRepresentation(currentNode,
					renderStyle, groupDrawingStrategy, drawingStrategyManager, this, true);
			rootGroup.add(groupRep);

			hashGroups.put(groupRep.getID(), groupRep);
			// selectionManager.initialAdd(groupRep.getID());
		}

		rootGroup.calculateHierarchyLevels(0);
		// ClusterHelper.determineNrElements(tree);
		// ClusterHelper.determineHierarchyDepth(tree);
		ClusterHelper.determineExpressionValue(tree, EClustererType.STORAGE_CLUSTERING,
				set);
		set.getStorageData(storageVAType).setStorageTree(tree);
		// useCase.replaceVirtualArray(idCategory, vaType, virtualArray)
	}

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
		iLastUsedGroupID = rootGroup.getID();

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
			if (groupRep.getID() > iLastUsedGroupID)
				iLastUsedGroupID = groupRep.getID();

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
		tree = new ClusterTree(dataDomain.getStorageIDType());
		ClusterNode rootNode = rootGroup.getClusterNode();
		rootNode.setTree(tree);
		tree.setRootNode(rootNode);
		iLastUsedGroupID = 0;
		rootGroup.getClusterNode().setID(iLastUsedGroupID++);
		hashGroups.clear();

		selectionManager.clearSelections();
		Set<SelectionType> selectionTypes = rootGroup.getSelectionTypes();
		for (SelectionType selectionType : selectionTypes) {
			if (selectionType != SelectionType.NORMAL)
				selectionManager.addToType(selectionType, rootGroup.getID());
		}

		hashGroups.put(rootGroup.getID(), rootGroup);

		buildTreeFromGroupHierarchy(tree, rootGroup.getClusterNode(), rootGroup);

		// ClusterHelper.determineNrElements(tree);
		// ClusterHelper.determineHierarchyDepth(tree);
		// FIXME: do that differently.
		// set = set.getStorageTree().getRoot().getMetaSet();
		ClusterHelper.determineExpressionValue(tree, EClustererType.STORAGE_CLUSTERING,
				set);
		tree.setDirty();
		tree.createMetaSets((org.caleydo.core.data.collection.set.Set) set);

		ArrayList<Integer> alIndices = tree.getRoot().getLeaveIds();
		storageVA = new StorageVirtualArray(
				org.caleydo.core.data.collection.set.Set.STORAGE, alIndices);

		eventPublisher.triggerEvent(new ReplaceStorageVAInUseCaseEvent(set, dataDomain
				.getDataDomainType(), storageVAType, storageVA));

		// FIXME no one is notified that there is a new tree
		set.getStorageData(storageVAType).setStorageTree(tree);

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
			childNode.setID(iLastUsedGroupID++);
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
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
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
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
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
				&& bPotentialNewSelection) {

			bPotentialNewSelection = false;
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

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
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

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.GROUPER_BACKGROUND_SELECTION, 0));

		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(viewFrustum.getWidth(), 0.0f, 0.0f);
		gl.glVertex3f(viewFrustum.getWidth(), viewFrustum.getHeight(), 0.0f);
		gl.glVertex3f(0.0f, viewFrustum.getHeight(), 0.0f);
		gl.glEnd();
		gl.glPopAttrib();

		gl.glPopName();

		Vec3f vecPosition = new Vec3f(0.0f, viewFrustum.getHeight(), 0.001f);
		rootGroup.setPosition(vecPosition);
		rootGroup.setHierarchyPosition(vecPosition);

		if (bHierarchyChanged) {
			rootGroup.calculateHierarchyLevels(0);
			rootGroup.calculateDrawingParameters(gl, textRenderer);

			// rootGroup.printTree();
			// System.out.println("==========================================");
		}
		rootGroup.draw(gl, textRenderer);

		if (bHierarchyChanged) {
			float fHierarchyHeight = rootGroup.getScaledHeight(parentGLCanvas.getWidth());
			float fHierarchyWidth = rootGroup.getScaledWidth(parentGLCanvas.getWidth());

			System.out.println(fHierarchyHeight);
			System.out.println(fHierarchyWidth);

			int minViewportHeight = (int) (parentGLCanvas.getHeight()
					/ viewFrustum.getHeight() * fHierarchyHeight) + 10;
			int minViewportWidth = (int) (parentGLCanvas.getWidth()
					/ viewFrustum.getWidth() * fHierarchyWidth) + 10;
			renderStyle.setMinViewDimensions(minViewportWidth, minViewportHeight, this);
			bHierarchyChanged = false;

			if (parentGLCanvas.getHeight() <= 0) {
				// Draw again in next frame where the viewport size is hopefully
				// correct
				setDisplayListDirty();
				bHierarchyChanged = true;
			}
		}

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		switch (pickingType) {

		case GROUPER_GROUP_SELECTION:
			final GroupRepresentation groupRep = hashGroups.get(iExternalID);
			switch (pickingMode) {

			case CLICKED:
				iDraggedOverCollapseButtonID = -1;
				if (groupRep != null) {
					if (!bControlPressed
							&& !selectionManager.checkStatus(SelectionType.SELECTION,
									groupRep.getID())) {
						dragAndDropController.clearDraggables();
						selectionManager.clearSelection(SelectionType.SELECTION);
						selectionManager.clearSelection(selectionTypeClicked);
					}
					if (!bControlPressed) {
						potentialNewSelectedGroup = groupRep;
						bPotentialNewSelection = true;
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
				iDraggedOverCollapseButtonID = -1;
				if (groupRep != null && dragAndDropController.hasDraggables()) {
					if (!dragAndDropController.isDragging()) {
						if (dragAndDropController.containsDraggable(groupRep)) {
							bPotentialNewSelection = false;
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
				iDraggedOverCollapseButtonID = -1;
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

						RenameGroupItem renameItem = new RenameGroupItem(iExternalID);
						contextMenu.addContextMenueItem(renameItem);
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

						Set<Integer> setSelectedGroups = new HashSet<Integer>(
								selectionManager.getElements(SelectionType.SELECTION));

						CreateGroupItem createGroupItem = new CreateGroupItem(
								setSelectedGroups);
						contextMenu.addContextMenueItem(createGroupItem);

						CopyGroupsItem copyGroupsItem = new CopyGroupsItem(
								setSelectedGroups);
						contextMenu.addContextMenueItem(copyGroupsItem);

						DeleteGroupsItem deleteGroupsItem = new DeleteGroupsItem(
								setSelectedGroups);
						contextMenu.addContextMenueItem(deleteGroupsItem);

						Set<Integer> setClickedGroups = new HashSet<Integer>(
								selectionManager.getElements(selectionTypeClicked));
						ArrayList<ICompositeGraphic> orderedComposites = getOrderedCompositeList(
								setClickedGroups, false);

						ArrayList<ISet> selectedSets = new ArrayList<ISet>();
						boolean isLeafContained = false;
						for (ICompositeGraphic composite : orderedComposites) {
							selectedSets.add(((GroupRepresentation) composite)
									.getClusterNode().getMetaSet());

							if (isLeafContained == false)
								isLeafContained = composite.isLeaf();
						}

						// Lazy loading of R
						// GeneralManager.get().getRStatisticsPerformer();
						//
						// StatisticsPValueReductionItem pValueReductionItem =
						// new StatisticsPValueReductionItem(
						// selectedSets);
						// contextMenu
						// .addContextMenueItem(pValueReductionItem);

						// if (orderedComposites.size() == 2) {
						// StatisticsFoldChangeReductionItem
						// foldChangeReductionItem = new
						// StatisticsFoldChangeReductionItem(
						// selectedSets.get(0), selectedSets
						// .get(1));

						if (Platform.getBundle("org.caleydo.util.r") != null) {

							contextMenu.addSeparator();

							// Lazy loading of R
							GeneralManager.get().getRStatisticsPerformer();

							// Do not allow p-value stats for multiple groups or
							// leaf meta sets
							if (!isLeafContained && orderedComposites.size() < 2) {
								StatisticsPValueReductionItem pValueReductionItem = new StatisticsPValueReductionItem(
										selectedSets);
								contextMenu.addContextMenueItem(pValueReductionItem);
							}

							if (orderedComposites.size() == 2) {
								StatisticsFoldChangeReductionItem foldChangeReductionItem = new StatisticsFoldChangeReductionItem(
										selectedSets.get(0), selectedSets.get(1));
								contextMenu.addContextMenueItem(foldChangeReductionItem);

								StatisticsTwoSidedTTestReductionItem twoSidedTTestReductionItem = new StatisticsTwoSidedTTestReductionItem(
										selectedSets);
								contextMenu
										.addContextMenueItem(twoSidedTTestReductionItem);
							}
						}

						if (orderedComposites.size() >= 2) {

							contextMenu.addSeparator();

							CompareGroupsItem compareGroupsItem = new CompareGroupsItem(
									selectedSets);
							contextMenu.addContextMenueItem(compareGroupsItem);
						}

						bContextMenueItemsAvailable = true;

					}
					if (setCopiedGroups != null
							&& !setCopiedGroups.contains(groupRep.getID())
							&& !groupRep.isLeaf()) {
						PasteGroupsItem pasteGroupItem = new PasteGroupsItem(
								groupRep.getID());
						contextMenu.addContextMenueItem(pasteGroupItem);
						bContextMenueItemsAvailable = true;
					}

					if (!isRenderedRemote() && bContextMenueItemsAvailable) {
						contextMenu.setLocation(pick.getPickedPoint(),
								getParentGLCanvas().getWidth(), getParentGLCanvas()
										.getHeight());
						contextMenu.setMasterGLView(this);
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
				iDraggedOverCollapseButtonID = -1;
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
			GroupRepresentation group = hashGroups.get(iExternalID);
			switch (pickingMode) {
			case CLICKED:
				iDraggedOverCollapseButtonID = -1;
				if (group != null) {
					group.setCollapsed(!group.isCollapsed());
					setDisplayListDirty();
				}
				break;
			case DRAGGED:
				if (group != null && group.isCollapsed()) {
					double dCurrentTimeStamp = Calendar.getInstance().getTimeInMillis();

					if (dCurrentTimeStamp - dCollapseButtonDragOverTime > 500
							&& group.getID() == iDraggedOverCollapseButtonID) {
						group.setCollapsed(false);
						iDraggedOverCollapseButtonID = -1;
						setDisplayListDirty();
						return;
					}
					if (group.getID() != iDraggedOverCollapseButtonID)
						dCollapseButtonDragOverTime = dCurrentTimeStamp;
					iDraggedOverCollapseButtonID = group.getID();
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

		SelectionDelta delta = new SelectionDelta(dataDomain.getStorageIDType());
		for (SelectionDeltaItem item : clusterIDDelta.getAllItems()) {
			GroupRepresentation groupRep = hashGroups.get(item.getPrimaryID());
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
		selectionUpdateEvent.setDataDomainType(dataDomain.getDataDomainType());
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
				dataDomain.getDataDomainType());
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
		eventPublisher.addListener(CreateGroupEvent.class, createGroupListener);

		copyGroupsListener = new CopyGroupsListener();
		copyGroupsListener.setHandler(this);
		eventPublisher.addListener(CopyGroupsEvent.class, copyGroupsListener);

		pasteGroupsListener = new PasteGroupsListener();
		pasteGroupsListener.setHandler(this);
		eventPublisher.addListener(PasteGroupsEvent.class, pasteGroupsListener);

		deleteGroupsListener = new DeleteGroupsListener();
		deleteGroupsListener.setHandler(this);
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
		return bControlPressed;
	}

	/**
	 * Sets whether the control key is pressed or not.
	 * 
	 * @param bControlPressed
	 *            Specifies if the control key is pressed.
	 */
	public void setControlPressed(boolean bControlPressed) {
		this.bControlPressed = bControlPressed;
	}

	/**
	 * @return True if the hierarchy (composite GroupRepresentation tree) has
	 *         changed, false otherwise.
	 */
	public boolean isHierarchyChanged() {
		return bHierarchyChanged;
	}

	/**
	 * Sets whether the hierarchy (composite GroupRepresentation tree) was
	 * changed or not.
	 * 
	 * @param bHierarchyChanged
	 *            Specifies if the hierarchy has changed.
	 */
	public void setHierarchyChanged(boolean bHierarchyChanged) {
		this.bHierarchyChanged = bHierarchyChanged;
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

		tree = new ClusterTree(dataDomain.getStorageIDType());
		GroupRepresentation newGroup = new GroupRepresentation(new ClusterNode(tree,
				"group" + iLastUsedGroupID, iLastUsedGroupID++, false, -1), renderStyle,
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
			int iTempID[] = { iLastUsedGroupID };
			for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
				iTempID[0]++;
				ICompositeGraphic copy = composite
						.createDeepCopyWithNewIDs(tree, iTempID);
				copy.setParent(newGroup);
				newGroup.add(copy);
			}
			iLastUsedGroupID = iTempID[0] + 1;
		}

		newGroup.setParent(commonParent);

		hashGroups.put(newGroup.getID(), newGroup);
		// selectionManager.add(newGroup.getID());

		bHierarchyChanged = true;

		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	/**
	 * Determine a node label for the new node
	 * 
	 * @param storageIDs
	 * @return
	 */
	private String determineNodeLabel(ArrayList<ICompositeGraphic> storageIDs) {

		String baseLabel = null;
		for (ICompositeGraphic storageID : storageIDs) {
			String currentLabel = storageID.getName();
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
		tree = new ClusterTree(dataDomain.getStorageIDType());
		GroupRepresentation parent = hashGroups.get(iParentGroupID);

		if (parent == null || setCopiedGroups == null || parent.isLeaf())
			return;

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedCompositeList(
				setCopiedGroups, true);

		int iTempID[] = { iLastUsedGroupID };
		for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
			iTempID[0]++;
			ICompositeGraphic copy = composite.createDeepCopyWithNewIDs(tree, iTempID);
			parent.add(copy);
		}
		iLastUsedGroupID = iTempID[0] + 1;

		bHierarchyChanged = true;
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

		bHierarchyChanged = true;
		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType() == selectionManager.getIDType()
				|| selectionDelta.getIDType() == dataDomain.getStorageIDType()) {
			Collection<SelectionDeltaItem> deltaItems = selectionDelta.getAllItems();
			Tree<ClusterNode> experimentTree = set.getStorageData(storageVAType)
					.getStorageTree();

			if (experimentTree != null) {
				// selectionManager.clearSelections();
				dragAndDropController.clearDraggables();

				for (SelectionDeltaItem item : deltaItems) {
					ArrayList<Integer> alNodeIDs = experimentTree
							.getNodeIDsFromLeafID(item.getPrimaryID());

					for (Integer nodeID : alNodeIDs) {
						GroupRepresentation groupRep = hashGroups.get(nodeID);

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
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				ChangeGroupNameDialog.run(GeneralManager.get().getGUIBridge()
						.getDisplay(), groupRep);
				groupRep.getClusterNode().getMetaSet()
						.setLabel(groupRep.getClusterNode().getLabel());
				setDisplayListDirty();
			}
		});

		// groupRep.getClusterNode().getMetaSet().setLabel(groupRep.getClusterNode().getNodeName());
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {

		this.dataDomain = dataDomain;
		set = this.dataDomain.getSet();

		storageVA = set.getStorageData(org.caleydo.core.data.collection.set.Set.STORAGE)
				.getStorageVA();
		drawingStrategyManager = new DrawingStrategyManager(pickingManager, iUniqueID,
				renderStyle);
		if (set.getStorageData(storageVAType).getStorageTree() != null) {
			// FIXME: do that differently.
			// set = set.getStorageTree().getRoot().getMetaSet();
			tree = set.getStorageData(storageVAType).getStorageTree();

			initHierarchy(tree);
		} else {
			createNewHierarchy();
		}

		selectionManager = new SelectionManager(tree.getNodeIDType());

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				selectionTypeClicked);
		eventPublisher.triggerEvent(selectionTypeEvent);

		selectionManager.addTypeToDeltaBlacklist(selectionTypeClicked);
	}
}
