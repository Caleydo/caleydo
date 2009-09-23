package org.caleydo.core.view.opengl.canvas.hyperbolic;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.TestNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.HTLayouter;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.ITreeLayouter;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * Rendering the hyperbolic view.
 * 
 * @author Helmut Pichlhoefer
 * @author Georg Neubauer
 */
public class GLHyperbolic
	extends AGLEventListener
	implements IClusterNodeEventReceiver, IViewCommandHandler, ISelectionUpdateHandler {

	// private Tree<DefaultNode> tree;
	public List<Vec3f> vec;

	boolean bIsInListMode = false;

	boolean bUseDetailLevel = true;
	// ISet set;

	boolean bIsSomethingHighlighted = false;

	Tree<IDrawAbleNode> tree = null;

	ITreeLayouter layouter = null;

	HyperbolicRenderStyle renderStyle = null;

	private ColorMappingManager colorMappingManager = null;

	private RedrawViewListener redrawViewListener = null;

	private int iGLDisplayListNode;
	private int iGLDisplayListConnection;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHyperbolic(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = EManagedObjectType.GL_HYPERBOLIC;

		// ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		// alSelectionTypes.add(ESelectionType.NORMAL);
		// alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		// alSelectionTypes.add(ESelectionType.SELECTION);

		colorMappingManager = ColorMappingManager.get();
		renderStyle = new HyperbolicRenderStyle(viewFrustum);

		// Build the Test Tree in Constructor
		// TreeTester tester = new TreeTester();
		// tree = tester;
		// tree.runTest();

		// tree = new Tree<ADrawAbleNode>();
		// ADrawAbleNode test = new TestNode("first Test", 0);
		// tree.setRootNode(test);
		// for (int i = 1; i <= 6; ++i) {
		// ADrawAbleNode test2 = new TestNode("childs", i);
		// tree.addChild(test, test2);
		// test = test2;
		// }
		//
		// test = tree.getRoot();
		// ADrawAbleNode test2 = new TestNode("childs", 500);
		// tree.addChild(test, test2);
		// blabla(test);
		// int lala = tree.getDepth();
		// lala = tree.getDepth(test);
		// for(ADrawAbleNode node : tree.getChildren(test))
		// lala = tree.getDepth(node);
		// ADrawAbleNode test2 = new TestNode("childs", 500);
		// tree.addChild(test, test2);
		// lala = tree.getDepth(test2);
		// lala = tree.getDepth(test);
		// lala = tree.getDepth();

		// ADrawAbleObject obj = DrawAbleObjectsFactory.getDrawAbleObject("Polygon");
		// obj.setAlpha(0.8f);
		// obj.setBgColor3f(0.4f, 0.3f, 0.5f);
		// test.setDetailLevel(EDrawAbleNodeDetailLevel.VeryHigh, obj);
		// obj = DrawAbleObjectsFactory.getDrawAbleObject("Polygon");
		// obj.setAlpha(0.2f);
		// obj.setBgColor3f(0.2f, 0.7f, 0.3f);
		// test.setDetailLevel(EDrawAbleNodeDetailLevel.High, obj);

		// tree.addChild(test, test2);
		// layouter = new LinearTreeLayouter(viewFrustum);

		tree = buildTestTree(7, 5);
		System.out.println(tree.getGraph().toString());
		layouter = new HTLayouter(viewFrustum, pickingManager, iUniqueID);
		layouter.setTree(tree);
	}

	@Override
	public void init(GL gl) {
		Tree<ClusterNode> tree = set.getClusteredTreeGenes();
		iGLDisplayListNode = gl.glGenLists(1);
		iGLDisplayListConnection = gl.glGenLists(1);
		layouter.init(iGLDisplayListNode, iGLDisplayListConnection);
		if (tree == null)
			return;
		if (set == null)
			return;
	}

	@Override
	public void initLocal(GL gl) {
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	// public void setToListMode(boolean bSetToListMode) {
	// this.bIsInListMode = bSetToListMode;
	// super.setDetailLevel(EDetailLevel.HIGH);
	// bUseDetailLevel = false;
	// setDisplayListDirty();
	// }

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}

	}

	@Override
	public void displayLocal(GL gl) {
		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			layouter.setLayoutDirty();
		}
		buildDisplayList(gl, iGLDisplayListIndexLocal);
		bIsDisplayListDirtyLocal = false;
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
		// if (bIsSomethingHighlighted) {
		// bIsSomethingHighlighted = false;
		// setDisplayListDirty();
		// }
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
		if (bIsSomethingHighlighted) {
			bIsSomethingHighlighted = false;
			setDisplayListDirty();
		}
	}

	@Override
	public void display(GL gl) {
		processEvents();
		layouter.display(gl);
		gl.glCallList(iGLDisplayListToCall);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		layouter.buildDisplayLists(gl);
	}

	private void render(GL gl) {
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		switch (ePickingType) {

			case HYPERBOLIC_NODE_SELECTION:

				switch (pickingMode) {
					case CLICKED:
						tree = convertTreeToNewOne(iExternalID);
						// layouter.animateToNewTree(tree);
						layouter.setTree(tree);
						break;
					case MOUSE_OVER:
						bIsSomethingHighlighted = true;
						layouter.setHighlightedNode(iExternalID);
						break;
					case DOUBLE_CLICKED:
						break;
					case RIGHT_CLICKED:
						break;

					default:
						break;
				}

				break;

			case HYPERBOLIC_LINE_SELECTION:

				switch (pickingMode) {
					case CLICKED:
						break;
					case MOUSE_OVER:
						bIsSomethingHighlighted = true;
						layouter.setHiglightedLine(iExternalID);
						break;
					case DOUBLE_CLICKED:
						break;
					case RIGHT_CLICKED:
						break;

					default:
						break;
				}

				break;

		}
	}

	private Tree<IDrawAbleNode> convertTreeToNewOne(int iExternalID) {
		IDrawAbleNode rootNode = tree.getNodeByNumber(iExternalID);
		if (tree.getRoot().compareTo(rootNode) == 0)
			return tree;
		Tree<IDrawAbleNode> newTree = new Tree<IDrawAbleNode>();

		newTree.setRootNode(rootNode);
		convertTreeToNewOneWorker(rootNode, newTree);
		return newTree;
	}

	private void convertTreeToNewOneWorker(IDrawAbleNode rootNode, Tree<IDrawAbleNode> newTree) {

		IDrawAbleNode theirRoot = tree.getParent(rootNode);
		IDrawAbleNode ourRoot = newTree.getParent(rootNode);
		if (theirRoot != null) {
			if (ourRoot == null || theirRoot.compareTo(ourRoot) != 0) {
				newTree.addChild(rootNode, theirRoot);
				convertTreeToNewOneWorker(theirRoot, newTree);
			}
		}
		// IDrawAbleNode ourRoot=null;
		// if(newTree.getRoot().compareTo(rootNode) != 0)
		if (tree.hasChildren(rootNode))
			for (IDrawAbleNode node : tree.getChildren(rootNode)) {
				if (ourRoot != null)
					if (node.compareTo(ourRoot) == 0)
						continue;
				newTree.addChild(rootNode, node);
				convertTreeToNewOneWorker(node, newTree);
			}
	}

	public boolean isInListMode() {
		return bIsInListMode;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(ESelectionType selectionType) {
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
	public ASerializedView getSerializableRepresentation() {
		SerializedHyperbolicView serializedForm = new SerializedHyperbolicView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	private Tree<IDrawAbleNode> buildTestTree(int iDepth, int iMaxNodesOnLayer) {
		int iComp = 1;
		Tree<IDrawAbleNode> tree = new Tree<IDrawAbleNode>();
		ADrawAbleNode root = new TestNode("root node: " + iComp + " Layer: " + 1, iComp);
		tree.setRootNode(root);
		for (int j = 0; j <= iMaxNodesOnLayer; ++j) {
			++iComp;
			tree.addChild(root, new TestNode("child node: " + iComp + " Layer: " + 2, iComp));
		}
		ArrayList<IDrawAbleNode> nodesOnLayer = tree.getChildren(root);
		for (int i = 2; i <= iDepth; ++i) {

			ArrayList<IDrawAbleNode> nodes = new ArrayList<IDrawAbleNode>();
			for (int j = 0; j < iMaxNodesOnLayer; ++j) {
				++iComp;
				nodes.add(new TestNode("child node: " + iComp + " Layer: " + i, iComp));
			}
			ArrayList<IDrawAbleNode> nStore = new ArrayList<IDrawAbleNode>(nodes);
			while (!nodes.isEmpty())
				for (IDrawAbleNode node : nodesOnLayer) {
					if (nodes.isEmpty())
						continue;
					int s =
						Math.min(nodes.size(), (int) Math
							.round((Math.random() * (double) iMaxNodesOnLayer) / 4));
					for (int j = 0; j < s; ++j) {
						tree.addChild(node, nodes.get(0));
						nodes.remove(0);
					}
				}
			nodesOnLayer = nStore;
		}

		return tree;
	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub

	}

}
