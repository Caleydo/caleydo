package org.caleydo.view.treemap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.ISetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.treemap.layout.ATreeMapNode;
import org.caleydo.view.treemap.layout.ClusterTreeMapNode;
import org.caleydo.view.treemap.layout.GlPainter;
import org.caleydo.view.treemap.layout.SimpleLayoutAlgorithm;

/**
 * TODO
 * 
 * @author TODO
 * 
 */
public class GLTreeMap extends AGLView implements ISetBasedView {

	public final static String VIEW_ID = "org.caleydo.view.treemap";

	private ASetBasedDataDomain dataDomain;

	private GlPainter painter;

	private boolean bIsHighlightingListDirty;

	private Tree<ATreeMapNode> treeMapModel;

	private SelectionManager treeSelectionManager;

	private Tree<ClusterNode> tree;

	private ColorMapping colorMapper;

	public GLTreeMap(GLCaleydoCanvas glCanvas, ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLTreeMap.VIEW_ID;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {

		throw new IllegalStateException();
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

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
		tree=dataDomain.getSet().getContentData(contentVAType).getContentTree();
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		ClusterTreeMapNode root = ClusterTreeMapNode.createFromClusterNodeTree(tree, colorMapper);
		SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
		layouter.layout(root, painter);
		treeMapModel = root.getTree();
	}

	

	@Override
	public void display(GL gl) {
		// GLHelperFunctions.drawAxis(gl);
		if (bIsDisplayListDirtyLocal) {
			painter = new GlPainter(gl, viewFrustum, pickingManager, getID(), treeSelectionManager);
			painter.paintTreeMap(treeMapModel.getRoot());
			bIsDisplayListDirtyLocal = false;
			setHighLichtingListDirty();
		}

		if (bIsHighlightingListDirty) {
			painter.paintHighlighting(treeMapModel, treeSelectionManager);
			bIsHighlightingListDirty = false;
		}

		painter.paintTreeMapFromCache();
	}

	private void setHighLichtingListDirty() {
		// TODO Auto-generated method stub

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

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode ePickingMode, int externalPickingID, Pick pick) {
		// TODO Auto-generated method stub

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
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setSet(ISet set) {
		throw new IllegalStateException("Should not be used");
	}

	public SelectionManager getSelectionManager() {
		return treeSelectionManager;
	}

	public void setSelectionManager(SelectionManager treeSelectionManager) {
		this.treeSelectionManager = treeSelectionManager;
	}

}
