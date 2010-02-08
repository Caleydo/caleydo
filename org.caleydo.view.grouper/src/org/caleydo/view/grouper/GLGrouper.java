package org.caleydo.view.grouper;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.grouper.CopyGroupsEvent;
import org.caleydo.core.manager.event.view.grouper.CreateGroupEvent;
import org.caleydo.core.manager.event.view.grouper.DeleteGroupsEvent;
import org.caleydo.core.manager.event.view.grouper.PasteGroupsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.CopyGroupsItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.CreateGroupItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.DeleteGroupsItem;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.PasteGroupsItem;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.grouper.compositegraphic.GroupRepresentation;
import org.caleydo.view.grouper.compositegraphic.ICompositeGraphic;
import org.caleydo.view.grouper.draganddrop.DragAndDropController;
import org.caleydo.view.grouper.drawingstrategies.DrawingStrategyManager;
import org.caleydo.view.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.view.grouper.drawingstrategies.group.IGroupDrawingStrategy;
import org.caleydo.view.grouper.listener.CopyGroupsEventListener;
import org.caleydo.view.grouper.listener.CreateGroupEventListener;
import org.caleydo.view.grouper.listener.DeleteGroupsEventListener;
import org.caleydo.view.grouper.listener.PasteGroupsEventListener;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public class GLGrouper extends AGLView implements IViewCommandHandler {

	public final static String VIEW_ID = "org.caleydo.view.grouper";

	boolean bUseDetailLevel = true;

	private boolean bControlPressed = false;
	private double dCollapseButtonDragOverTime;
	private int iDraggedOverCollapseButtonID;

	private Integer iLastUsedGroupID;

	private boolean bHierarchyChanged;

	private GrouperRenderStyle renderStyle;
	private HashMap<Integer, GroupRepresentation> hashGroups;

	private Set<Integer> setCopiedGroups;

	private GroupRepresentation rootGroup;

	private DrawingStrategyManager drawingStrategyManager = null;
	private DragAndDropController dragAndDropController = null;
	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	private CreateGroupEventListener createGroupEventListener = null;
	private CopyGroupsEventListener copyGroupsEventListener = null;
	private PasteGroupsEventListener pasteGroupsEventListener = null;
	private DeleteGroupsEventListener deleteGroupsEventListener = null;

	private TextRenderer textRenderer;
	private SelectionManager selectionManager;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGrouper(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = VIEW_ID;

		hashGroups = new HashMap<Integer, GroupRepresentation>();

		dragAndDropController = new DragAndDropController(this);
		// TODO:if this should be general, use dynamic idType
		selectionManager = new SelectionManager.Builder(EIDType.CLUSTER_NUMBER)
				.build();

		renderStyle = new GrouperRenderStyle(this, viewFrustum);
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32),
				true, true);

		glKeyListener = new GLGrouperKeyListener(this);

		iDraggedOverCollapseButtonID = -1;
		bHierarchyChanged = true;
		iLastUsedGroupID = 0;
		// registerEventListeners();
	}

	@Override
	public void init(GL gl) {

		storageVA = useCase.getVA(EVAType.STORAGE);
		drawingStrategyManager = new DrawingStrategyManager(pickingManager,
				iUniqueID, renderStyle);
		if (set.getClusteredTreeExps() != null) {
			initHierarchy(set.getClusteredTreeExps());
		} else {
			createNewHierarchy();
		}
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL canvas
		parentGLCanvas.getParentComposite().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		init(gl);
	}

	private void createNewHierarchy() {
		Tree<ClusterNode> experimentTree = new Tree<ClusterNode>();
		IGroupDrawingStrategy groupDrawingStrategy = drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
		iLastUsedGroupID = 0;

		ClusterNode rootNode = new ClusterNode("Root", iLastUsedGroupID++,
				0.0f, 0, true, -1);
		experimentTree.setRootNode(rootNode);

		rootGroup = new GroupRepresentation(rootNode, renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, false);
		hashGroups.put(rootGroup.getID(), rootGroup);
		selectionManager.initialAdd(rootGroup.getID());
		ArrayList<Integer> indexList = storageVA.getIndexList();

		for (Integer currentIndex : indexList) {

			String nodeName = set.get(currentIndex).getLabel();
			int leafID = currentIndex;
			ClusterNode currentNode = new ClusterNode(nodeName,
					iLastUsedGroupID++, 0.0f, 0, false, leafID);
			experimentTree.addChild(rootNode, currentNode);
		

			GroupRepresentation groupRep = new GroupRepresentation(currentNode,
					renderStyle, groupDrawingStrategy, drawingStrategyManager,
					this, true);
			rootGroup.add(groupRep);

			hashGroups.put(groupRep.getID(), groupRep);
			selectionManager.initialAdd(groupRep.getID());
		}
		
		rootGroup.calculateHierarchyLevels(0);
		ClusterHelper.determineNrElements(experimentTree);
		ClusterHelper.determineHierarchyDepth(experimentTree);
		ClusterHelper.determineExpressionValue(experimentTree,
				EClustererType.EXPERIMENTS_CLUSTERING, set);
		set.setClusteredTreeExps(experimentTree);
		// useCase.replaceVirtualArray(idCategory, vaType, virtualArray)
	}

	private void initHierarchy(Tree<ClusterNode> tree) {

		ClusterNode rootNode = tree.getRoot();
		rootGroup = new GroupRepresentation(
				rootNode,
				renderStyle,
				drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL),
				drawingStrategyManager, this, !tree.hasChildren(rootNode));
		hashGroups.put(rootGroup.getID(), rootGroup);
		selectionManager.initialAdd(rootGroup.getID());
		iLastUsedGroupID = rootGroup.getID();

		buildGroupHierarchyFromTree(tree, rootNode, rootGroup);
		rootGroup.calculateHierarchyLevels(0);
	}

	private void buildGroupHierarchyFromTree(Tree<ClusterNode> tree,
			ClusterNode currentNode, GroupRepresentation parentGroupRep) {

		ArrayList<ClusterNode> alChildren = tree.getChildren(currentNode);
		IGroupDrawingStrategy groupDrawingStrategy = drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);

		for (ClusterNode child : alChildren) {
			boolean bHasChildren = tree.hasChildren(child);
			GroupRepresentation groupRep = new GroupRepresentation(child,
					renderStyle, groupDrawingStrategy, drawingStrategyManager,
					this, !bHasChildren);
			parentGroupRep.add(groupRep);

			hashGroups.put(groupRep.getID(), groupRep);
			selectionManager.initialAdd(groupRep.getID());
			if (groupRep.getID() > iLastUsedGroupID)
				iLastUsedGroupID = groupRep.getID();

			if (bHasChildren) {
				buildGroupHierarchyFromTree(tree, child, groupRep);
			}
		}
	}
	
	public void updateClusterTreeAccordingToGroupHierarchy() {
		
		Tree<ClusterNode> tree = new Tree<ClusterNode>();
		tree.setRootNode(rootGroup.getClusterNode());
		iLastUsedGroupID = 0;
		rootGroup.getClusterNode().setClusterNr(iLastUsedGroupID++);
		hashGroups.clear();
		selectionManager.add(rootGroup.getID());
		hashGroups.put(rootGroup.getID(), rootGroup);
		
		buildTreeFromGroupHierarchy(tree, rootGroup.getClusterNode(), rootGroup);
		
		ClusterHelper.determineNrElements(tree);
		ClusterHelper.determineHierarchyDepth(tree);
		ClusterHelper.determineExpressionValue(tree,
				EClustererType.EXPERIMENTS_CLUSTERING, set);
		set.setClusteredTreeExps(tree);
		
		ArrayList<Integer> alIndices = ClusterHelper.getGeneIdsOfNode(tree, tree.getRoot());
		storageVA = new VirtualArray(EVAType.STORAGE, alIndices.size(), alIndices);
		set.replaceVA(useCase.getVA(EVAType.STORAGE).getID(), storageVA);
		
		UpdateViewEvent event = new UpdateViewEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(EIDCategory.EXPERIMENT, EVAType.STORAGE));
	}
	
	private void buildTreeFromGroupHierarchy(Tree<ClusterNode> tree,
			ClusterNode parentNode, GroupRepresentation parentGroupRep) {
		ArrayList<ICompositeGraphic> alChildren = parentGroupRep.getChildren();
		
		for(ICompositeGraphic child : alChildren) {
			GroupRepresentation groupRep = (GroupRepresentation) child;
			ClusterNode childNode = groupRep.getClusterNode();
			childNode.setClusterNr(iLastUsedGroupID++);
			tree.addChild(parentNode, childNode);
			if(!child.isLeaf()) {
				buildTreeFromGroupHierarchy(tree, childNode, groupRep);
			}
			selectionManager.add(groupRep.getID());
			hashGroups.put(groupRep.getID(), groupRep);
		}
	}

	public void initTestHierarchy() {

		IGroupDrawingStrategy groupDrawingStrategy = drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);

		rootGroup = new GroupRepresentation(new ClusterNode("root",
				iLastUsedGroupID++, 0, 0, true, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, false);

		GroupRepresentation group1 = new GroupRepresentation(new ClusterNode(
				"group1", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, false);
		GroupRepresentation group2 = new GroupRepresentation(new ClusterNode(
				"group2", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, false);
		GroupRepresentation group3 = new GroupRepresentation(new ClusterNode(
				"group3", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, false);

		GroupRepresentation element1 = new GroupRepresentation(new ClusterNode(
				"one", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, true);

		GroupRepresentation element2 = new GroupRepresentation(new ClusterNode(
				"two", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, true);

		GroupRepresentation element3 = new GroupRepresentation(new ClusterNode(
				"three", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, true);

		GroupRepresentation element4 = new GroupRepresentation(new ClusterNode(
				"four", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, true);

		GroupRepresentation element5 = new GroupRepresentation(new ClusterNode(
				"five", iLastUsedGroupID++, 0, 0, false, -1), renderStyle,
				groupDrawingStrategy, drawingStrategyManager, this, true);

		// VAElementRepresentation element1 =
		// new VAElementRepresentation(new ClusterNode("one",
		// iLastUsedCompositeID++, 0, 0, false),
		// elementDrawingStrategy, drawingStrategyManager, this);
		// VAElementRepresentation element2 =
		// new VAElementRepresentation(new ClusterNode("two",
		// iLastUsedCompositeID++, 0, 0, false),
		// elementDrawingStrategy, drawingStrategyManager, this);
		// VAElementRepresentation element3 =
		// new VAElementRepresentation(new ClusterNode("three",
		// iLastUsedCompositeID++, 0, 0, false),
		// elementDrawingStrategy, drawingStrategyManager, this);
		// VAElementRepresentation element4 =
		// new VAElementRepresentation(new ClusterNode("four",
		// iLastUsedCompositeID++, 0, 0, false),
		// elementDrawingStrategy, drawingStrategyManager, this);
		// VAElementRepresentation element5 =
		// new VAElementRepresentation(new ClusterNode("five",
		// iLastUsedCompositeID++, 0, 0, false),
		// elementDrawingStrategy, drawingStrategyManager, this);

		rootGroup.add(group1);
		rootGroup.add(element1);
		rootGroup.add(group3);
		group1.add(element2);
		group1.add(group2);
		group2.add(element3);
		group2.add(element4);
		group3.add(element5);

		hashGroups.put(rootGroup.getID(), rootGroup);
		hashGroups.put(group1.getID(), group1);
		hashGroups.put(group2.getID(), group2);
		hashGroups.put(group3.getID(), group3);
		hashGroups.put(element1.getID(), element1);
		hashGroups.put(element2.getID(), element2);
		hashGroups.put(element3.getID(), element3);
		hashGroups.put(element4.getID(), element4);
		hashGroups.put(element5.getID(), element5);

		selectionManager.initialAdd(rootGroup.getID());
		selectionManager.initialAdd(group1.getID());
		selectionManager.initialAdd(group2.getID());
		selectionManager.initialAdd(group3.getID());
		selectionManager.initialAdd(element1.getID());
		selectionManager.initialAdd(element2.getID());
		selectionManager.initialAdd(element3.getID());
		selectionManager.initialAdd(element4.getID());
		selectionManager.initialAdd(element5.getID());

		rootGroup.calculateHierarchyLevels(0);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
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
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();
		if (!isVisible())
			return;
		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {
		// processEvents();
		gl.glCallList(iGLDisplayListToCall);

		dragAndDropController.handleDragging(gl, glMouseListener);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.GROUPER_BACKGROUND_SELECTION, 0));

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(viewFrustum.getWidth(), 0.0f, 0.0f);
		gl.glVertex3f(viewFrustum.getWidth(), viewFrustum.getHeight(), 0.0f);
		gl.glVertex3f(0.0f, viewFrustum.getHeight(), 0.0f);
		gl.glEnd();
		gl.glPopAttrib();

		gl.glPopName();

		Vec3f vecPosition = new Vec3f(viewFrustum.getWidth() / 2.0f,
				viewFrustum.getHeight(), 0.001f);
		rootGroup.setPosition(vecPosition);
		rootGroup.setHierarchyPosition(vecPosition);
		if (bHierarchyChanged) {
			rootGroup.calculateHierarchyLevels(0);
			rootGroup.calculateDrawingParameters(gl, textRenderer);
			bHierarchyChanged = false;
			// rootGroup.printTree();
			// System.out.println("==========================================");
		}
		rootGroup.draw(gl, textRenderer);

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		switch (ePickingType) {

		case GROUPER_GROUP_SELECTION:
			GroupRepresentation groupRep = hashGroups.get(iExternalID);
			switch (pickingMode) {
			case CLICKED:
				iDraggedOverCollapseButtonID = -1;
				if (groupRep != null) {
					if (!bControlPressed
							&& !selectionManager.checkStatus(
									SelectionType.SELECTION, groupRep.getID())) {
						dragAndDropController.clearDraggables();
						selectionManager
								.clearSelection(SelectionType.SELECTION);
					}
					groupRep.addAsDraggable(dragAndDropController);
					dragAndDropController.startDragging();

					groupRep.setSelectionType(SelectionType.SELECTION,
							selectionManager);
					rootGroup.updateDrawingStrategies(selectionManager,
							drawingStrategyManager);
					setDisplayListDirty();
				}
				break;
			case DRAGGED:
				iDraggedOverCollapseButtonID = -1;
				if (groupRep != null) {
					if (dragAndDropController.isDragging()) {
						dragAndDropController.setDropArea(groupRep);
					}
				}
				break;
			case MOUSE_OVER:
				iDraggedOverCollapseButtonID = -1;
				if (groupRep != null) {
					if (selectionManager.checkStatus(SelectionType.MOUSE_OVER,
							groupRep.getID())
							|| selectionManager.checkStatus(
									SelectionType.SELECTION, groupRep.getID())) {
						return;
					}
					selectionManager.clearSelection(SelectionType.MOUSE_OVER);
					selectionManager.addToType(SelectionType.MOUSE_OVER,
							groupRep.getID());
					rootGroup.updateDrawingStrategies(selectionManager,
							drawingStrategyManager);
					setDisplayListDirty();
				}
				break;
			case RIGHT_CLICKED:
				if (groupRep != null) {
					if (selectionManager.checkStatus(SelectionType.SELECTION,
							groupRep.getID())
							&& groupRep != rootGroup) {

						Set<Integer> setSelectedGroups = new HashSet<Integer>(
								selectionManager
										.getElements(SelectionType.SELECTION));

						CreateGroupItem createGroupItem = new CreateGroupItem(
								setSelectedGroups);
						contextMenu.addContextMenueItem(createGroupItem);

						CopyGroupsItem copyGroupsItem = new CopyGroupsItem(
								setSelectedGroups);
						contextMenu.addContextMenueItem(copyGroupsItem);

						DeleteGroupsItem deleteGroupsItem = new DeleteGroupsItem(
								setSelectedGroups);
						contextMenu.addContextMenueItem(deleteGroupsItem);

					}
					if (setCopiedGroups == null
							|| !setCopiedGroups.contains(groupRep.getID())) {
						PasteGroupsItem pasteGroupItem = new PasteGroupsItem(
								groupRep.getID());
						contextMenu.addContextMenueItem(pasteGroupItem);
					}

					if (!isRenderedRemote()) {
						contextMenu.setLocation(pick.getPickedPoint(),
								getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
						contextMenu.setMasterGLView(this);
					}

				}
				break;
			default:
				return;
			}
			break;

		// case GROUPER_VA_ELEMENT_SELECTION:
		// VAElementRepresentation elementRep =
		// hashElements.get(iExternalID);
		// switch (pickingMode) {
		// case CLICKED:
		// iDraggedOverCollapseButtonID = -1;
		// if (elementRep != null) {
		//
		// if (!bControlPressed
		// && !selectionManager
		// .checkStatus(SelectionType.SELECTION, elementRep.getID())) {
		// dragAndDropController.clearDraggables();
		// selectionManager.clearSelection(SelectionType.SELECTION);
		// }
		// dragAndDropController.addDraggable(elementRep);
		// dragAndDropController.startDragging();
		//
		// selectionManager.addToType(SelectionType.SELECTION,
		// elementRep.getID());
		// rootGroup.updateDrawingStrategies(selectionManager,
		// drawingStrategyManager);
		// setDisplayListDirty();
		// }
		// break;
		// case MOUSE_OVER:
		// iDraggedOverCollapseButtonID = -1;
		// if (elementRep != null) {
		// if (selectionManager.checkStatus(SelectionType.MOUSE_OVER,
		// elementRep.getID())
		// || selectionManager.checkStatus(SelectionType.SELECTION,
		// elementRep.getID())) {
		// return;
		// }
		// selectionManager.clearSelection(SelectionType.MOUSE_OVER);
		// selectionManager.addToType(SelectionType.MOUSE_OVER,
		// elementRep.getID());
		// rootGroup.updateDrawingStrategies(selectionManager,
		// drawingStrategyManager);
		// setDisplayListDirty();
		// }
		// break;
		// case RIGHT_CLICKED:
		// if (elementRep != null) {
		// if (selectionManager.checkStatus(SelectionType.SELECTION,
		// elementRep.getID())) {
		// CreateGroupItem createGroupItem = new CreateGroupItem();
		// contextMenu.addContextMenueItem(createGroupItem);
		//
		// if (!isRenderedRemote()) {
		// contextMenu.setLocation(pick.getPickedPoint(),
		// getParentGLCanvas()
		// .getWidth(), getParentGLCanvas().getHeight());
		// contextMenu.setMasterGLView(this);
		// }
		// }
		// }
		// break;
		// default:
		// return;
		// }
		// break;

		case GROUPER_BACKGROUND_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				iDraggedOverCollapseButtonID = -1;
				dragAndDropController.clearDraggables();
				selectionManager.clearSelections();
				rootGroup.updateDrawingStrategies(selectionManager,
						drawingStrategyManager);
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
					double dCurrentTimeStamp = GregorianCalendar.getInstance()
							.getTimeInMillis();

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
				dataDomain);
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
		eventPublisher.addListener(ClearSelectionsEvent.class,
				clearSelectionsListener);

		createGroupEventListener = new CreateGroupEventListener();
		createGroupEventListener.setHandler(this);
		eventPublisher.addListener(CreateGroupEvent.class,
				createGroupEventListener);

		copyGroupsEventListener = new CopyGroupsEventListener();
		copyGroupsEventListener.setHandler(this);
		eventPublisher.addListener(CopyGroupsEvent.class,
				copyGroupsEventListener);

		pasteGroupsEventListener = new PasteGroupsEventListener();
		pasteGroupsEventListener.setHandler(this);
		eventPublisher.addListener(PasteGroupsEvent.class,
				pasteGroupsEventListener);

		deleteGroupsEventListener = new DeleteGroupsEventListener();
		deleteGroupsEventListener.setHandler(this);
		eventPublisher.addListener(DeleteGroupsEvent.class,
				deleteGroupsEventListener);

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
		if (createGroupEventListener != null) {
			eventPublisher.removeListener(createGroupEventListener);
			createGroupEventListener = null;
		}
		if (copyGroupsEventListener != null) {
			eventPublisher.removeListener(copyGroupsEventListener);
			copyGroupsEventListener = null;
		}
		if (pasteGroupsEventListener != null) {
			eventPublisher.removeListener(pasteGroupsEventListener);
			pasteGroupsEventListener = null;
		}
		if (deleteGroupsEventListener != null) {
			eventPublisher.removeListener(deleteGroupsEventListener);
			deleteGroupsEventListener = null;
		}
	}

	public boolean isControlPressed() {
		return bControlPressed;
	}

	public void setControlPressed(boolean bControlPressed) {
		this.bControlPressed = bControlPressed;
	}

	public boolean isHierarchyChanged() {
		return bHierarchyChanged;
	}

	public void setHierarchyChanged(boolean bHierarchyChanged) {
		this.bHierarchyChanged = bHierarchyChanged;
	}

	public void addGroupRepresentation(int iID,
			GroupRepresentation groupRepresentation) {
		hashGroups.put(iID, groupRepresentation);
	}

	// public void addVAElementRepresentation(int iID, VAElementRepresentation
	// elementRepresentation) {
	// hashElements.put(iID, elementRepresentation);
	// }

	public void removeGroupRepresentation(int iID) {
		hashGroups.remove(iID);
	}

	// public void removeVAElementRepresentation(int iID) {
	// hashElements.remove(iID);
	// }

	private ArrayList<ICompositeGraphic> getOrderedTopElementCompositeList(
			Set<Integer> setGroupIds) {

		Set<ICompositeGraphic> setComposites = new HashSet<ICompositeGraphic>();
		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = new ArrayList<ICompositeGraphic>();

		for (Integer id : setGroupIds) {
			if (hashGroups.containsKey(id))
				setComposites.add(hashGroups.get(id));
		}

		rootGroup.getOrderedTopElementCompositeList(setComposites,
				alOrderedTopLevelComposites);

		return alOrderedTopLevelComposites;
	}

	public void createNewGroup(Set<Integer> setContainedGroups) {

		GroupRepresentation newGroup = new GroupRepresentation(
				new ClusterNode("group" + iLastUsedGroupID, iLastUsedGroupID++,
						0, 0, false, -1),
				renderStyle,
				drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL),
				drawingStrategyManager, this, false);

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedTopElementCompositeList(setContainedGroups);

		ICompositeGraphic commonParent = findCommonParent(alOrderedTopLevelComposites);

		if (commonParent == null)
			return;
		boolean bSharedParent = true;

		for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
			if (composite.getParent() != commonParent) {
				bSharedParent = false;
			}
		}

		if (bSharedParent) {
			commonParent.replaceChild(alOrderedTopLevelComposites.get(0),
					newGroup);
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
						.createDeepCopyWithNewIDs(iTempID);
				copy.setParent(newGroup);
				newGroup.add(copy);
			}
			iLastUsedGroupID = iTempID[0] + 1;
		}

		newGroup.setParent(commonParent);

		hashGroups.put(newGroup.getID(), newGroup);
		selectionManager.add(newGroup.getID());

		bHierarchyChanged = true;
		
		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	private ICompositeGraphic findCommonParent(
			ArrayList<ICompositeGraphic> alComposites) {

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
		selectionManager.add(iID);
	}

	public void copyGroups(Set<Integer> setGroupsToCopy) {
		setCopiedGroups = setGroupsToCopy;
	}

	public void pasteGroups(int iParentGroupID) {
		GroupRepresentation parent = hashGroups.get(iParentGroupID);

		if (parent == null || setCopiedGroups == null)
			return;

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedTopElementCompositeList(setCopiedGroups);

		int iTempID[] = { iLastUsedGroupID };
		for (ICompositeGraphic composite : alOrderedTopLevelComposites) {
			iTempID[0]++;
			ICompositeGraphic copy = composite
					.createDeepCopyWithNewIDs(iTempID);
			parent.add(copy);
		}
		iLastUsedGroupID = iTempID[0] + 1;

		bHierarchyChanged = true;
		updateClusterTreeAccordingToGroupHierarchy();
		setDisplayListDirty();
	}

	public void deleteGroups(Set<Integer> setGroupsToDelete) {

		ArrayList<ICompositeGraphic> alOrderedTopLevelComposites = getOrderedTopElementCompositeList(setGroupsToDelete);

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
}
