package org.caleydo.view.treemap;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.treemap.layout.ATreeMapNode;
import org.caleydo.view.treemap.layout.ClusterTreeMapNode;
import org.caleydo.view.treemap.layout.TreeMapRenderer;
import org.caleydo.view.treemap.layout.algorithm.ILayoutAlgorithm;
import org.caleydo.view.treemap.layout.algorithm.SimpleLayoutAlgorithm;
import org.caleydo.view.treemap.layout.algorithm.SquarifiedLayoutAlgorithm;

/**
 * TODO
 * 
 * @author TODO
 * 
 */
public class GLTreeMap extends AGLView implements IDataDomainSetBasedView {

	public final static String VIEW_ID = "org.caleydo.view.treemap";

	private ASetBasedDataDomain dataDomain;

	private TreeMapRenderer renderer;

	private boolean bIsHighlightingListDirty;

	private Tree<ATreeMapNode> treeMapModel;

	private SelectionManager treeSelectionManager;

	private Tree<ClusterNode> tree;

	private ColorMapping colorMapper;

	private PickingManager remotePickingManager = null;

	private int remoteViewID = 0;

	private boolean bIsMouseWheeleUsed;

	private int mouseWheeleSelectionHeight;

	private int mouseOverClusterId;

	private int rootClusterID;

	private boolean bIsZoomActive = false;

	private ILayoutAlgorithm layoutAlgorithm = new SimpleLayoutAlgorithm();

	// private ILayoutAlgorithm layoutAlgorithm = new
	// SquarifiedLayoutAlgorithm();

	public GLTreeMap(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLTreeMap.VIEW_ID;
		
		renderer= new TreeMapRenderer();

	}

	@Override
	public ASerializedView getSerializableRepresentation() {

		throw new IllegalStateException();
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub
		treeSelectionManager.clearSelections();
	}

	@Override
	public void init(GL gl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initLocal(GL gl) {
		throw new IllegalStateException();
	}

	@Override
	public void initRemote(GL gl, AGLView glParentView, GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {
		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	public void initData() {
		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		ClusterTreeMapNode root;
		if (bIsZoomActive) {
			ClusterNode dataRoot = tree.getNodeByNumber(rootClusterID);
			if (dataRoot == null)
				throw new IllegalStateException("selected wrong cluster id for zooming");
			root = ClusterTreeMapNode.createFromClusterNodeTree(dataRoot, colorMapper);
		} else
			root = ClusterTreeMapNode.createFromClusterNodeTree(tree, colorMapper);

		// SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
		layoutAlgorithm.layout(root);
		treeMapModel = root.getTree();
	}

	@Override
	public void display(GL gl) {
		if (bIsDisplayListDirtyLocal) {
			renderer.initPainter(gl, viewFrustum, getActivePickingManager(), getPickingViewID(), treeSelectionManager, textRenderer);
			renderer.paintTreeMap(gl, treeMapModel.getRoot());
			bIsDisplayListDirtyLocal = false;
			setHighLichtingListDirty();
		}

		if (bIsHighlightingListDirty) {
			renderer.paintHighlighting(gl,treeMapModel, treeSelectionManager);
			bIsHighlightingListDirty = false;
		}

		renderer.paintTreeMapFromCache(gl);
	}

	private void setHighLichtingListDirty() {
		bIsHighlightingListDirty = true;

	}

	@Override
	protected void displayLocal(GL gl) {
		throw new IllegalStateException();
	}

	@Override
	public void displayRemote(GL gl) {
		// if (bIsDisplayListDirtyRemote) {
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// bIsDisplayListDirtyRemote = false;
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);
	}

	public void handleRemotePickingEvents(EPickingType ePickingType, EPickingMode ePickingMode, int externalPickingID, Pick pick) {
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

				break;
			case MOUSE_OVER:

				System.out.println("mouse over: " + pickingID);
				mouseOverClusterId = pickingID;
				treeSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				treeSelectionManager.addToType(SelectionType.MOUSE_OVER, pickingID);
				break;
			case RIGHT_CLICKED:

				break;
			case DRAGGED:
				// System.out.println(iExternalID+" dragged");
				break;
			default:
				return;

			}
			// treeSelectionManager.addToType(selectionType, iExternalID);

			// treeSelectionManager.getElements(SelectionType.SELECTION);
			// treeSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
			// iElementID);
			//
			// ArrayList<SelectionType> selectionTypes =
			// treeSelectionManager.getSelectionTypes(elementID);
			//
			// setDisplayListDirty();

			setHighLichtingListDirty();
			break;

		default:
			return;
		}

	}

	public void processMouseWheeleEvent(MouseWheelEvent e) {
		if (bIsMouseWheeleUsed) {
			// System.out.println("wheel used: " +
			// e.getWheelRotation());
			if (e.getWheelRotation() > 0) {
				ATreeMapNode node = treeMapModel.getNodeByNumber(mouseOverClusterId);
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
				ATreeMapNode node = treeMapModel.getNodeByNumber(mouseOverClusterId);
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
			setHighLichtingListDirty();
		}
	}

	public void processMousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			treeSelectionManager.clearSelection(SelectionType.SELECTION);
			treeSelectionManager.addToType(SelectionType.SELECTION, mouseOverClusterId);
			bIsMouseWheeleUsed = true;
			mouseWheeleSelectionHeight = 0;
		}
	}

	public void processMouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			bIsMouseWheeleUsed = false;
		}
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
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
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		treeSelectionManager = new SelectionManager(tree.getNodeIDType());
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
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

}
