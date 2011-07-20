package org.caleydo.view.treemap;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.event.view.treemap.LevelHighlightingEvent;
import org.caleydo.core.manager.event.view.treemap.ToggleColoringModeEvent;
import org.caleydo.core.manager.event.view.treemap.ToggleLabelEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapper;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IColorMappingHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.UpdateColorMappingListener;
import org.caleydo.core.view.opengl.canvas.listener.UpdateViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.treemap.layout.ATreeMapNode;
import org.caleydo.view.treemap.layout.ClusterTreeMapNode;
import org.caleydo.view.treemap.layout.TreeMapRenderer;
import org.caleydo.view.treemap.layout.algorithm.ILayoutAlgorithm;
import org.caleydo.view.treemap.layout.algorithm.SimpleLayoutAlgorithm;
import org.caleydo.view.treemap.layout.algorithm.SquarifiedLayoutAlgorithm;
import org.caleydo.view.treemap.listener.LevelHighlightingListener;
import org.caleydo.view.treemap.listener.ToggleColoringModeListener;
import org.caleydo.view.treemap.listener.ToggleLabelListener;

/**
 * Control Class for a single treemap. Handles interaction and events. Calls
 * TreemapRenderer to display the treemap.
 * 
 * @author Michael Lafer
 * 
 */
public class GLTreeMap extends AGLView implements IDataDomainSetBasedView, ISelectionUpdateHandler, IViewCommandHandler, IColorMappingHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.treemap";

	private ATableBasedDataDomain dataDomain;

	private TreeMapRenderer renderer;

	private boolean bIsHighlightingListDirty;

	private Tree<ATreeMapNode> treeMapModel;

	private SelectionManager treeSelectionManager;

	private Tree<ClusterNode> tree;

	private ColorMapper colorMapper;

	private PickingManager remotePickingManager = null;

	private int remoteViewID = 0;

	private boolean bIsMouseWheeleUsed;

	private int mouseWheeleSelectionHeight;

	private int mouseWheeleSelectionId;

	private int rootClusterID;

	private boolean bIsZoomActive = false;

	private boolean bCalculateColor = false;

	private boolean bIsThumbNailView = false;
	private boolean bIsInteractive = true;

	private ILayoutAlgorithm layoutAlgorithm;

	private SelectionUpdateListener selectionUpdateListener;
	private ToggleColoringModeListener coloringModeListener;
	private ToggleLabelListener labelListener;
	UpdateViewListener updateViewListener;
	UpdateColorMappingListener updateColorMappingListener;
	LevelHighlightingListener levelHighlightingListener;

	public GLTreeMap(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLTreeMap.VIEW_TYPE;

		renderer = new TreeMapRenderer();
		renderer.setNodeFrame(GeneralManager.get().getPreferenceStore().getBoolean(PreferenceConstants.TREEMAP_DRAW_CLUSTER_FRAME), Color.WHITE);

		loadLayoutAlgorithmClass();
	}

	private void loadLayoutAlgorithmClass() {
		int algoID = GeneralManager.get().getPreferenceStore().getInt(PreferenceConstants.TREEMAP_LAYOUT_ALGORITHM);
		switch (algoID) {
		case ILayoutAlgorithm.SQUARIFIED_LAYOUT_ALGORITHM:
			layoutAlgorithm = new SquarifiedLayoutAlgorithm();
			break;
		default:
			layoutAlgorithm = new SimpleLayoutAlgorithm();
			break;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {

		return new SerializedTreeMapView();
	}

	@Override
	public void clearAllSelections() {
		treeSelectionManager.clearSelections();
	}

	@Override
	public void init(GL2 gl) {
		if (textRenderer == null)
			textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24));
		renderer.initCache(gl);

	}

	@Override
	protected void initLocal(GL2 gl) {
		throw new IllegalStateException();
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	public void initData() {
		if (dataDomain == null)
			return;
		tree = dataDomain.getDataTable().getContentData(contentVAType).getContentTree();
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		int maxDepth = Integer.MAX_VALUE;
		maxDepth = GeneralManager.get().getPreferenceStore().getInt(PreferenceConstants.TREEMAP_MAX_DEPTH);
		if (maxDepth == 0)
			maxDepth = Integer.MAX_VALUE;
		// maxDepth=10;

		if (tree == null)
			return;
		ClusterTreeMapNode root;
		if (bIsZoomActive) {
			ClusterNode dataRoot = tree.getNodeByNumber(rootClusterID);
			if (dataRoot == null)
				throw new IllegalStateException("selected wrong cluster id for zooming");
			root = ClusterTreeMapNode.createFromClusterNodeTree(dataRoot, colorMapper, maxDepth);
		} else
			root = ClusterTreeMapNode.createFromClusterNodeTree(tree, colorMapper, maxDepth);

		root.setColorData(bCalculateColor, dataDomain);

		layoutAlgorithm.layout(root);
		treeMapModel = root.getTree();

		setDisplayListDirty();
		// ATreeMapNode node = DefaultTreeNode.createSampleTree();
		// layoutAlgorithm.layout(node);
		// treeMapModel=node.getTree();
	}

	@Override
	public void display(GL2 gl) {
		if (bIsDisplayListDirtyLocal) {
			renderer.initCache(gl);
			renderer.initRenderer(viewFrustum, getActivePickingManager(), getPickingViewID(), treeSelectionManager, textRenderer);
			renderer.renderTreeMap(gl, treeMapModel.getRoot());
			bIsDisplayListDirtyLocal = false;
			setHighLightingListDirty();
		}

		if (bIsHighlightingListDirty) {
			renderer.paintHighlighting(gl, treeMapModel, treeSelectionManager);
			bIsHighlightingListDirty = false;
		}

		renderer.renderTreeMapFromCache(gl);
	}

	private void setHighLightingListDirty() {
		bIsHighlightingListDirty = true;

	}

	@Override
	protected void displayLocal(GL2 gl) {
		throw new IllegalStateException();
	}

	@Override
	public void displayRemote(GL2 gl) {
		// if (bIsDisplayListDirtyRemote) {
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// bIsDisplayListDirtyRemote = false;
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);
	}

	public void handleRemotePickingEvents(EPickingType ePickingType, EPickingMode ePickingMode, int externalPickingID, Pick pick) {
		if (bIsInteractive)
			handlePickingEvents(ePickingType, ePickingMode, externalPickingID, pick);
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode, int pickingID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		switch (pickingType) {
		case TREEMAP_ELEMENT_SELECTED:

			switch (pickingMode) {

			case CLICKED:
				mouseWheeleSelectionId = pickingID;
				treeSelectionManager.clearSelection(SelectionType.SELECTION);
				treeSelectionManager.addToType(SelectionType.SELECTION, mouseWheeleSelectionId);
				bIsMouseWheeleUsed = true;
				mouseWheeleSelectionHeight = 0;
				break;
			case MOUSE_OVER:

				treeSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				treeSelectionManager.addToType(SelectionType.MOUSE_OVER, pickingID);
				break;
			case RIGHT_CLICKED:

				break;
			case DRAGGED:
				// System.out.println(externalID+" dragged");
				break;
			default:
				return;

			}

			publishSelectionEvent();

			setHighLightingListDirty();
			break;

		default:
			return;
		}

	}

	private void publishSelectionEvent() {
		SelectionDelta delta = treeSelectionManager.getDelta();

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSelectionDelta(delta);
		event.setInfo(getShortInfoLocal());
		eventPublisher.triggerEvent(event);

		SelectionDelta newDelta = new SelectionDelta(treeSelectionManager.getIDType());
		newDelta.setIDType(dataDomain.getContentIDType());
		for (SelectionDeltaItem item : delta) {
			ClusterNode node = tree.getNodeByNumber(item.getPrimaryID());
			if (node.getLeafID() >= 0) {
				SelectionDeltaItem newItem = new SelectionDeltaItem(node.getLeafID(), item.getSelectionType());
				newItem.setRemove(item.isRemove());
				newDelta.add(newItem);
			}
		}
		SelectionUpdateEvent leafEvent = new SelectionUpdateEvent();
		leafEvent.setSender(this);
		leafEvent.setDataDomainID(dataDomain.getDataDomainID());

		leafEvent.setSelectionDelta(newDelta);
		leafEvent.setInfo(getShortInfoLocal());
		eventPublisher.triggerEvent(leafEvent);

	}

	public void processMouseWheeleEvent(MouseWheelEvent e) {
		if (bIsMouseWheeleUsed) {
			// System.out.println("wheel used: " +
			// e.getWheelRotation());
			if (e.getWheelRotation() > 0) {
				ATreeMapNode node = treeMapModel.getNodeByNumber(mouseWheeleSelectionId);
				mouseWheeleSelectionHeight++;
				// System.out.println("selectionlevel: " +
				// node.selectionLevel);
				ATreeMapNode parent = node.getParentWithLevel(node.getHierarchyLevel() - mouseWheeleSelectionHeight);
				if (parent != null) {
					treeSelectionManager.clearSelection(SelectionType.SELECTION);
					treeSelectionManager.addToType(SelectionType.SELECTION, parent.getID());
				} else {
					mouseWheeleSelectionHeight--;
				}

			} else {
				ATreeMapNode node = treeMapModel.getNodeByNumber(mouseWheeleSelectionId);
				if (mouseWheeleSelectionHeight > 0) {
					mouseWheeleSelectionHeight--;
					ATreeMapNode parent;
					if (mouseWheeleSelectionHeight == 0)
						parent = node;
					else
						parent = node.getParentWithLevel(node.getHierarchyLevel() - mouseWheeleSelectionHeight);
					treeSelectionManager.clearSelection(SelectionType.SELECTION);
					treeSelectionManager.addToType(SelectionType.SELECTION, parent.getID());
				}

			}
			setHighLightingListDirty();
		}
	}

	@Override
	public String getShortInfo() {
		return "TreeMap";
	}

	@Override
	public String getDetailedInfo() {
		return "TreeMap";
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		if (dataDomain != null) {
			tree = dataDomain.getDataTable().getContentData(contentVAType).getContentTree();
			if (tree != null) {
				treeSelectionManager = new SelectionManager(tree.getNodeIDType());
			}
		}
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public SelectionManager getSelectionManager() {
		return treeSelectionManager;
	}

	public void setSelectionManager(SelectionManager treeSelectionManager) {
		this.treeSelectionManager = treeSelectionManager;
	}

	private PickingManager getActivePickingManager() {
		if (remotePickingManager != null)
			return remotePickingManager;
		else
			return pickingManager;
	}

	private int getPickingViewID() {
		if (remotePickingManager != null)
			return remoteViewID;
		else
			return getID();
	}

	public PickingManager getRemotePickingManager() {
		return remotePickingManager;
	}

	public void setRemotePickingManager(PickingManager remotePickingManager, int viewID) {
		remoteViewID = viewID;
		this.remotePickingManager = remotePickingManager;
	}

	public int getRootClusterID() {
		return rootClusterID;
	}

	public void setRootClusterID(int rootClusterID) {
		this.rootClusterID = rootClusterID;
	}

	public boolean isZoomActive() {
		return bIsZoomActive;
	}

	public void setZoomActive(boolean bIsZoomActive) {
		this.bIsZoomActive = bIsZoomActive;
	}

	public void setDrawLabel(boolean flag) {
		renderer.setDrawLabel(flag && !bIsThumbNailView);
		setDisplayListDirty();
	}

	public void setCalculateColor(boolean flag) {
		// if(treeMapModel.getRoot() instanceof ClusterTreeMapNode){
		// ClusterTreeMapNode clusternode = (ClusterTreeMapNode)
		// treeMapModel.getRoot();
		// clusternode.setColorData(flag, dataDomain);
		// setDisplayListDirty();
		// }
		bCalculateColor = flag;
		initData();
	}

	public void setHighLightingLevel(int level) {
		treeSelectionManager.clearSelection(SelectionType.LEVEL_HIGHLIGHTING);
		if (level > 0) {
			ArrayList<ClusterNode> nodes, newNodes;
			nodes = tree.getRoot().getChildren();
			for (int i = 0; i < level; i++) {
				newNodes = new ArrayList<ClusterNode>();
				for (ClusterNode node : nodes) {
					ArrayList<ClusterNode> children = node.getChildren();
					if (children != null)
						newNodes.addAll(children);
				}
				nodes = newNodes;
			}

			for (ClusterNode node : nodes) {
				treeSelectionManager.addToType(SelectionType.LEVEL_HIGHLIGHTING, node.getID());
			}
		}

		setHighLightingListDirty();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		labelListener = new ToggleLabelListener();
		labelListener.setHandler(this);
		// labelListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ToggleLabelEvent.class, labelListener);

		coloringModeListener = new ToggleColoringModeListener();
		coloringModeListener.setHandler(this);
		eventPublisher.addListener(ToggleColoringModeEvent.class, coloringModeListener);

		levelHighlightingListener = new LevelHighlightingListener();
		levelHighlightingListener.setHandler(this);
		eventPublisher.addListener(LevelHighlightingEvent.class, levelHighlightingListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setDataDomainType(dataDomain.getDataDomainID());
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		updateColorMappingListener = new UpdateColorMappingListener();
		// updateColorMappingListener.setDataDomainType(dataDomain.getDataDomainType());
		updateColorMappingListener.setHandler(this);
		eventPublisher.addListener(UpdateColorMappingEvent.class, updateColorMappingListener);

		updateViewListener = new UpdateViewListener();
		// updateViewListener.setDataDomainType(dataDomain.getDataDomainType());
		updateViewListener.setHandler(this);
		eventPublisher.addListener(UpdateViewEvent.class, updateViewListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (labelListener != null) {
			eventPublisher.removeListener(labelListener);
			labelListener = null;
		}

		if (coloringModeListener != null) {
			eventPublisher.removeListener(coloringModeListener);
			coloringModeListener = null;
		}

		if (levelHighlightingListener != null) {
			eventPublisher.removeListener(levelHighlightingListener);
			levelHighlightingListener = null;
		}

		if (updateColorMappingListener != null) {
			eventPublisher.removeListener(updateColorMappingListener);
			updateColorMappingListener = null;
		}

		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		if (bIsInteractive) {
			if (dataDomain.getContentIDType() == selectionDelta.getIDType()) {
				SelectionDelta newDelta = new SelectionDelta(treeSelectionManager.getIDType());
				for (SelectionDeltaItem item : selectionDelta) {
					ArrayList<Integer> nodeIDs = tree.getNodeIDsFromLeafID(item.getPrimaryID());
					if (nodeIDs != null && nodeIDs.size() > 0) {
						SelectionDeltaItem newItem = new SelectionDeltaItem(nodeIDs.get(0), item.getSelectionType());
						newItem.setRemove(item.isRemove());
						newDelta.add(newItem);
					}

				}
				treeSelectionManager.setDelta(newDelta);
			}

			if (treeSelectionManager.getIDType() == selectionDelta.getIDType())
				treeSelectionManager.setDelta(selectionDelta);

			setDisplayListDirty();
		}
	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void distributeColorMapping(ColorMapper colorMapping) {
		colorMapper = colorMapping;

	}

	public void setInteractive(boolean flag) {
		bIsInteractive = flag;
	}

	public float[] getSelectedArea() {
		float[] rect = new float[4];

		int id = treeSelectionManager.getElements(SelectionType.SELECTION).iterator().next();
		ATreeMapNode node = treeMapModel.getNodeByNumber(id);

		rect[0] = node.getMinX();
		rect[1] = node.getMinY();
		rect[2] = node.getMaxX();
		rect[3] = node.getMaxY();

		return rect;
	}

}
