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
import org.caleydo.core.manager.event.view.hyperbolic.ChangeCanvasDrawingEvent;
import org.caleydo.core.manager.event.view.hyperbolic.ChangeTreeTypeEvent;
import org.caleydo.core.manager.event.view.hyperbolic.SetMaxLayoutDepthEvent;
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
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.TextRenderingNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.listeners.ChangeCanvasDrawingListener;
import org.caleydo.core.view.opengl.canvas.hyperbolic.listeners.ChangeTreeTypeListener;
import org.caleydo.core.view.opengl.canvas.hyperbolic.listeners.SetMaxLayoutDepthListener;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.HTLayouter;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.ITreeLayouter;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.LTLayouter;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

import com.sun.opengl.util.j2d.TextRenderer;

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

	Tree<ClusterNode> clusteredTree = null;
	Tree<IDrawAbleNode> drawAbleTree = null;

	ITreeLayouter layouter = null;

	HyperbolicRenderStyle renderStyle = null;

	private ColorMappingManager colorMappingManager = null;

	private RedrawViewListener redrawViewListener = null;

	private int iGLDisplayListNode;
	private int iGLDisplayListConnection;

	private ChangeTreeTypeListener changeTreeTypeListener;

	private ChangeCanvasDrawingListener changeCanvasDrawingListener;
	
	private SetMaxLayoutDepthListener setMaxLayoutDepthListener;

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

		// tree = buildTestTree(HyperbolicRenderStyle.MAX_DEPTH, 7);
		// System.out.println(tree.getGraph().toString());
		// layouter = new HTLayouter(viewFrustum, pickingManager, iUniqueID);
		// layouter.setTree(tree);
	}

	@Override
	public void init(GL gl) {
		iGLDisplayListNode = gl.glGenLists(1);
		iGLDisplayListConnection = gl.glGenLists(1);
		if (set == null)
			return;
		// TODO: enable control for switching beetween genes and experiments
		//clusteredTree = set.getClusteredTreeExps();
		 clusteredTree = set.getClusteredTreeGenes();
		// TODO: outtaken for test reasons
		if (clusteredTree == null)
			return;

		layouter = new LTLayouter(viewFrustum, pickingManager, iUniqueID);
		layouter.init(iGLDisplayListNode, iGLDisplayListConnection);
		buildDrawAbleTree();
		layouter.setTree(getSubTreeForDisplay());
	}

	private Tree<IDrawAbleNode> getSubTreeForDisplay() {
		Tree<IDrawAbleNode> tree = new Tree<IDrawAbleNode>();
		IDrawAbleNode rootNode = drawAbleTree.getRoot();
		tree.setRootNode(rootNode);
		getSubTreeForDisplayWorker(rootNode, tree, 1);
		return tree;
	}
	
	private void getSubTreeForDisplayWorker(IDrawAbleNode rootNode, Tree<IDrawAbleNode> tree, int layer) {
		if(layer == HyperbolicRenderStyle.MAX_DEPTH)
			return;
		if(drawAbleTree.hasChildren(rootNode))
			for (IDrawAbleNode node : drawAbleTree.getChildren(rootNode)){
				tree.addChild(rootNode, node);
				getSubTreeForDisplayWorker(node, tree, layer+1);
			}	
	}

	private void buildDrawAbleTree() {
		drawAbleTree = new Tree<IDrawAbleNode>();
		ClusterNode clRootNode = clusteredTree.getRoot();
		IDrawAbleNode daRootNode = convertClusterNodeToDrawAbleNode(clRootNode); 
		//TextRenderingNode(clRootNode.getNodeName(), clRootNode.getClusterNr());//
		drawAbleTree.setRootNode(daRootNode);
		buildDrawAbleTreeWorker(clRootNode, daRootNode, 0);
	}

	private void buildDrawAbleTreeWorker(ClusterNode clRootNode, IDrawAbleNode daRootNode, int level) {
		if (clusteredTree.hasChildren(clRootNode))
			for (ClusterNode clNode : clusteredTree.getChildren(clRootNode)) {
				IDrawAbleNode daNode = convertClusterNodeToDrawAbleNode(clNode);
				drawAbleTree.addChild(daRootNode, daNode);
				buildDrawAbleTreeWorker(clNode, daNode, level + 1);
			}
	}

	private IDrawAbleNode convertClusterNodeToDrawAbleNode(ClusterNode node) {
		// TODO: maybe add different NodeTypes... but they need to be defined first
		return new TestNode(node.getNodeName(), node.getClusterNr());
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
		if (layouter == null) {
			return;
		}

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
						if (!layouter.isAnimating()) {
							if(drawAbleTree.getRoot().getID() == iExternalID)
								break;
							drawAbleTree = convertTreeToNewOne(iExternalID);
							//buildDrawAbleTree();
							//drawAbleTree = convertTreeToNewOne(iExternalID);
							layouter.animateToNewTree(getSubTreeForDisplay());
						}
						// layouter.setTree(drawAbleTree);
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

//	private Tree<ClusterNode> convertTreeToNewOne(int iExternalID) {
//		ClusterNode rootNode = clusteredTree.getNodeByNumber(iExternalID);
//		if (clusteredTree.getRoot().compareTo(rootNode) == 0)
//			return clusteredTree;
//		Tree<ClusterNode> newTree = new Tree<ClusterNode>();
//		newTree.setRootNode(rootNode);
//		convertTreeToNewOneWorker(rootNode, newTree);
//		return newTree;
//	}
//	
//	private void convertTreeToNewOneWorker(ClusterNode rootNode, Tree<ClusterNode> newTree) {
//
//		ClusterNode theirRoot = clusteredTree.getParent(rootNode);
//		ClusterNode ourRoot = newTree.getParent(rootNode);
//		if (theirRoot != null) {
//			if (ourRoot == null || theirRoot.compareTo(ourRoot) != 0) {
//				newTree.addChild(rootNode, theirRoot);
//				convertTreeToNewOneWorker(theirRoot, newTree);
//			}
//		}
//		if (clusteredTree.hasChildren(rootNode))
//			for (ClusterNode node : clusteredTree.getChildren(rootNode)) {
//				if (ourRoot != null)
//					if (node.compareTo(ourRoot) == 0)
//						continue;
//				newTree.addChild(rootNode, node);
//				convertTreeToNewOneWorker(node, newTree);
//			}
//	}
	
	private Tree<IDrawAbleNode> convertTreeToNewOne(int iExternalID) {
		IDrawAbleNode rootNode = drawAbleTree.getNodeByNumber(iExternalID);
		if (drawAbleTree.getRoot().compareTo(rootNode) == 0)
			return drawAbleTree;
		Tree<IDrawAbleNode> newTree = new Tree<IDrawAbleNode>();
		newTree.setRootNode(rootNode);
		convertTreeToNewOneWorker(rootNode, newTree);
		return newTree;
	}
	
/*	private Tree<IDrawAbleNode> convertTreeToNewOne(int iExternalID) {
		IDrawAbleNode rootNode = drawAbleTree.getNodeByNumber(iExternalID);
		if (drawAbleTree.getRoot().compareTo(rootNode) == 0)
			return drawAbleTree;
		Tree<IDrawAbleNode> newTree = new Tree<IDrawAbleNode>();
		newTree.setRootNode(rootNode);
		convertTreeToNewOneWorker(rootNode, newTree);
		return newTree;
	}*/

	private void convertTreeToNewOneWorker(IDrawAbleNode rootNode, Tree<IDrawAbleNode> newTree) {

		IDrawAbleNode theirRoot = drawAbleTree.getParent(rootNode);
		IDrawAbleNode ourRoot = newTree.getParent(rootNode);
		if (theirRoot != null) {
			if (ourRoot == null || theirRoot.compareTo(ourRoot) != 0) {
				newTree.addChild(rootNode, theirRoot);
				convertTreeToNewOneWorker(theirRoot, newTree);
			}
		}
		if (drawAbleTree.hasChildren(rootNode))
			for (IDrawAbleNode node : drawAbleTree.getChildren(rootNode)) {
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
		
		setMaxLayoutDepthListener = new SetMaxLayoutDepthListener();
		setMaxLayoutDepthListener.setHandler(this);
		eventPublisher.addListener(SetMaxLayoutDepthEvent.class, setMaxLayoutDepthListener);

		changeCanvasDrawingListener = new ChangeCanvasDrawingListener();
		changeCanvasDrawingListener.setHandler(this);
		eventPublisher.addListener(ChangeCanvasDrawingEvent.class, changeCanvasDrawingListener);

		changeTreeTypeListener = new ChangeTreeTypeListener();
		changeTreeTypeListener.setHandler(this);
		eventPublisher.addListener(ChangeTreeTypeEvent.class, changeTreeTypeListener);

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

//	private Tree<IDrawAbleNode> buildTestTree(int iDepth, int iMaxNodesOnLayer) {
//		int iComp = 1;
//		Tree<IDrawAbleNode> tree = new Tree<IDrawAbleNode>();
//		ADrawAbleNode root = new TestNode("root node: " + iComp + " Layer: " + 1, iComp);
//		tree.setRootNode(root);
//		for (int j = 0; j <= iMaxNodesOnLayer; ++j) {
//			++iComp;
//			tree.addChild(root, new TestNode("child node: " + iComp + " Layer: " + 2, iComp));
//		}
//		ArrayList<IDrawAbleNode> nodesOnLayer = tree.getChildren(root);
//		for (int i = 2; i <= iDepth; ++i) {
//
//			ArrayList<IDrawAbleNode> nodes = new ArrayList<IDrawAbleNode>();
//			for (int j = 0; j < iMaxNodesOnLayer; ++j) {
//				++iComp;
//				nodes.add(new TestNode("child node: " + iComp + " Layer: " + i, iComp));
//			}
//			ArrayList<IDrawAbleNode> nStore = new ArrayList<IDrawAbleNode>(nodes);
//			while (!nodes.isEmpty())
//				for (IDrawAbleNode node : nodesOnLayer) {
//					if (nodes.isEmpty())
//						continue;
//					int s =
//						Math.min(nodes.size(), (int) Math
//							.round((Math.random() * (double) iMaxNodesOnLayer) / 0.9f));
//
//					for (int j = 0; j < s; ++j) {
//						tree.addChild(node, nodes.get(0));
//						nodes.remove(0);
//					}
//				}
//			nodesOnLayer = nStore;
//		}
//
//		return tree;
//	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub

	}

	public void changeTreeType() {
		if (layouter == null)
			return;
		if (layouter instanceof LTLayouter)
			layouter = new HTLayouter(viewFrustum, pickingManager, iUniqueID);
		else
			layouter = new LTLayouter(viewFrustum, pickingManager, iUniqueID);
		layouter.init(iGLDisplayListNode, iGLDisplayListConnection);
		layouter.setTree(getSubTreeForDisplay());
		setDisplayListDirty();
	}

	public void changeCanvasDrawing() {
		HyperbolicRenderStyle.PROJECTION_DRAW_CANVAS = !HyperbolicRenderStyle.PROJECTION_DRAW_CANVAS;
	}
	public void setMaxLayoutDepth(int iMaxLayoutDepth){
		HyperbolicRenderStyle.MAX_DEPTH = iMaxLayoutDepth;
		if(layouter != null)
			layouter.animateToNewTree(getSubTreeForDisplay());
		
	}

}
