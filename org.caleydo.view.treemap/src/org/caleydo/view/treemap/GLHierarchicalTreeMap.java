package org.caleydo.view.treemap;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.CmdCreateView;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.treemap.ZoomInEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.ISetBasedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.treemap.layout.ATreeMapNode;
import org.caleydo.view.treemap.layout.ClusterTreeMapNode;
import org.caleydo.view.treemap.layout.GlPainter;
import org.caleydo.view.treemap.layout.SimpleLayoutAlgorithm;
import org.caleydo.view.treemap.listener.ZoomInListener;
import org.caleydo.view.treemap.renderstyle.TreeMapRenderStyle;

/**
 * Rendering the Treemap
 * 
 * @author Alexander Lex
 */

public class GLHierarchicalTreeMap extends AGLView implements IViewCommandHandler, ISetBasedView, ISelectionUpdateHandler, IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.treemap.hierarchical";

	private TreeMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private SelectionManager treeSelectionManager;

	GlPainter painter;

	// private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	// private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	// toggleable feature flags

	boolean bUseDetailLevel = true;

	private boolean bUpdateMainView = false;

	private ASetBasedDataDomain dataDomain;

	private Tree<ClusterNode> tree;
	private Tree<ATreeMapNode> treeMapModel;

	private boolean bIsHighlightingListDirty = false;

	private boolean bIsMouseWheeleUsed = false;

	int mouseOverClusterId;
	int mouseWheeleSelectionHeight = 0;

	// private EIDType primaryIDType = EIDType.CLUSTER_NUMBER;

	private SelectionUpdateListener selectionUpdateListener;
	
	private GLTreeMap glTreeMap;
	
	private Vector<GLTreeMap> treemapViews = new Vector<GLTreeMap>(4);
	
	

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHierarchicalTreeMap(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		// super(glCanvas, sLabel, viewFrustum, null);
		viewType = GLHierarchicalTreeMap.VIEW_ID;

		// ArrayList<SelectionType> alSelectionTypes = new
		// ArrayList<SelectionType>();
		// alSelectionTypes.add(SelectionType.NORMAL);
		// alSelectionTypes.add(SelectionType.MOUSE_OVER);
		// alSelectionTypes.add(SelectionType.SELECTION);

//		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		parentGLCanvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					treeSelectionManager.clearSelection(SelectionType.SELECTION);
					treeSelectionManager.addToType(SelectionType.SELECTION, mouseOverClusterId);
					bIsMouseWheeleUsed = true;
					mouseWheeleSelectionHeight = 0;
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					bIsMouseWheeleUsed = false;
				}
			}
		});

		parentGLCanvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
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
		});
		
		// treeSelectionManager = new SelectionManager(primaryIDType);

	}

	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TreeMapRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
		
		createEmbeddedHeatMap();
		
		glTreeMap.initRemote(gl, this, glMouseListener, null);

	}

	@Override
	public void initLocal(GL gl) {

		if (glTreeMap != null)
			glTreeMap.processEvents();
		
		// Register keyboard listener to GL canvas
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new
		// Runnable() {
		// public void run() {
		// parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
		// }
		// });

		// iGLDisplayListIndexLocal = gl.glGenLists(2);
		// iGLDisplayListToCall = iGLDisplayListIndexLocal;
		// iGLDisplayListIndexCoord = iGLDisplayListIndexLocal + 1;
		// iGLDisplayListIndexMouseOver = iGLDisplayListIndexLocal + 2;
		// iGLDisplayListIndexSelection = iGLDisplayListIndexLocal + 3;
		// iGLDisplayListIndexMatrixFull = iGLDisplayListIndexLocal + 4;
		// iGLDisplayListIndexMatrixSelection = iGLDisplayListIndexLocal + 5;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		// iGLDisplayListToCall = iGLDisplayListIndexLocal;

//		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		init(gl);

//		gl.glNewList(iGLDisplayListIndexLocal, GL.GL_COMPILE);
//		gl.glEndList();

		// ScatterPlotRenderStyle.setTextureNr(NR_TEXTURESX,NR_TEXTURESY);

		// TODO: check when to update data
		

	}
	
	public void initData(){
		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		for(GLTreeMap view : treemapViews)
			view.initData();
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView, final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				glParentView.getParentGLCanvas().getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}
	}

	@Override
	public void displayLocal(GL gl) {

		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);

	}

	@Override
	public void displayRemote(GL gl) {

	}

	@Override
	public void display(GL gl) {

		glTreeMap.displayRemote(gl);
		
		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawPointAt(gl, 1, 1, 1);
		// gl.glPushName(pickingManager.getPickingID(getID(),
		// EPickingType.TREEMAP_ELEMENT_SELECTED, 1));

		// gl.glBegin(GL.GL_QUADS);
		// gl.glColor3f(0, 1, 0);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, 1, 0);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(1, 0, 0);
		// gl.glEnd();
		// gl.glPopName();

		// GlPainter painter = new GlPainter(gl, viewFrustum);

		// painter.paintRectangle(0, 0,(float) 1.0/3, 1, Color.RED);
		// painter.paintRectangle((float) 1.0/3, 0,(float) 2.0/3, 1,
		// Color.GREEN);
		// painter.paintRectangle((float) 2.0/3, 0, 1, 1, Color.BLUE);

		// Tree<ClusterNode> storageTree = dataDomain.getSet()
		// .getStorageData(StorageVAType.STORAGE).getStorageTree();

		// AbstractTree tree = DefaultTree.createSampleTree();
		// SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
		//
		// layouter.layout(tree, painter);
		// painter.paintTreeMap(tree);
		//
		// painter.paintRectangle((float) 0.0, (float) 0.0, (float) 1 / 3,
		// (float) 1,
		// Color.YELLOW);

		// SelectionManager contentSelectionManager = dataDomain
		// .getContentSelectionManager();

//		if (bIsDisplayListDirtyLocal) {
//			painter = new GlPainter(gl, viewFrustum, pickingManager, getID(), treeSelectionManager);
//
//			// ATreeMapNode root = DefaultTreeNode.createSampleTree();
//
//			// Tree<ClusterNode> contentTree =
//			// dataDomain.getSet().getContentData(ContentVAType.CONTENT).getContentTree();
//			//
//			//
//			// ClusterTreeMapNode root =
//			// ClusterTreeMapNode.createFromClusterNodeTree(tree, colorMapper);
//			// SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
//			// layouter.layout(root, painter);
//			// treeMapModel = root.getTree();
//
//			painter.paintTreeMap(treeMapModel.getRoot());
//
//			bIsDisplayListDirtyLocal = false;
//			setHighLichtingListDirty();
//		}
//
//		if (bIsHighlightingListDirty) {
//			painter.paintHighlighting(treeMapModel, treeSelectionManager);
//			bIsHighlightingListDirty = false;
//		}
//
//		painter.paintTreeMapFromCache();

	}

	public void zoomIn() {
		System.out.println("zooming!!!!!");
		Set<Integer> elements = treeSelectionManager.getElements(SelectionType.SELECTION);
		if (elements.size() == 1) {

			// ATreeMapNode root = DefaultTreeNode.createSampleTree();

			ClusterNode dataRoot = tree.getNodeByNumber(elements.iterator().next());
			ClusterTreeMapNode root = ClusterTreeMapNode.createFromClusterNodeTree(dataRoot, colorMapper);

			SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
			layouter.layout(root, painter);

			treeMapModel = root.getTree();

			setDisplayListDirty();

		}

	};

	@Override
	public String getShortInfo() {

		return "Tree Map";
	}

	@Override
	public String getDetailedInfo() {
		return "Tree Map";

	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (ePickingType) {
		case TREEMAP_ELEMENT_SELECTED:
			ATreeMapNode node;
			// iCurrentMouseOverElement = iExternalID;
			switch (pickingMode) {

			case CLICKED:
				// {
				// System.out.println(iExternalID+" clicked");
				// selectionType = SelectionType.SELECTION;
				// ArrayList<SelectionType> selections =
				// treeSelectionManager.getSelectionTypes(iExternalID);
				// if (selections != null &&
				// selections.contains(SelectionType.SELECTION)) {
				// treeSelectionManager.removeFromType(SelectionType.SELECTION,
				// iExternalID);
				// } else
				// treeSelectionManager.addToType(SelectionType.SELECTION,
				// iExternalID);
				// }
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				// System.out.println("mouse over: " + iExternalID);
				mouseOverClusterId = iExternalID;
				treeSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				treeSelectionManager.addToType(SelectionType.MOUSE_OVER, iExternalID);
				break;
			case RIGHT_CLICKED:

				break;
			case DRAGGED:
				selectionType = SelectionType.SELECTION;
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

	// int currentNode;

	private void selectNode(int id, boolean selected) {
		if (selected)
			treeSelectionManager.addToType(SelectionType.SELECTION, id);
		else
			treeSelectionManager.removeFromType(SelectionType.SELECTION, id);

		ATreeMapNode node = treeMapModel.getNodeByNumber(id);
		if (node.hasChildren()) {
			for (ATreeMapNode childNode : node.getChildren())
				selectNode(childNode.getID(), selected);
		}

	}

	@Override
	public void clearAllSelections() {

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTreeMapView serializedForm = new SerializedTreeMapView(dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void handleRedrawView() {

		setDisplayListDirty();
	}

	@Override
	public String toString() {
		return "Standalone Scatterplot, rendered remote: " + isRenderedRemote() + ", contentSize: " + contentVA.size() + ", storageSize: " + storageVA.size()
				+ ", contentVAType: " + contentVAType + ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		// selectionUpdateListener.setDataDomainType(dataDomain.getDataDomainType());
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);
		ZoomInListener zoomInListener = new ZoomInListener();
		zoomInListener.setHandler(this);
		eventPublisher.addListener(ZoomInEvent.class, zoomInListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

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
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSet(ISet set) {
		throw new IllegalStateException("Should not be used");
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// treeSelectionManager.setDelta(selectionDelta);
		if (treeSelectionManager.getIDType() == selectionDelta.getIDType())
			treeSelectionManager.setDelta(selectionDelta);

		setDisplayListDirty();
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		treeSelectionManager = new SelectionManager(tree.getNodeIDType());
		//TODO set selectionmanager
		for(GLTreeMap view: treemapViews){
			view.setDataDomain(dataDomain);
			view.setSelectionManager(treeSelectionManager);
		}
	}

	public void setHighLichtingListDirty() {
		bIsHighlightingListDirty = true;
	}

	private void createEmbeddedHeatMap() {
		CmdCreateView cmdView = (CmdCreateView) generalManager.getCommandManager().createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(GLTreeMap.VIEW_ID);

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();

		cmdView.setAttributes(CameraProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0, fHeatMapWidth, -20, 20, -1);
		cmdView.setDataDomainType(dataDomain.getDataDomainType());
		cmdView.doCommand();

		glTreeMap = (GLTreeMap) cmdView.getCreatedObject();
		glTreeMap.setDataDomain(dataDomain);
		glTreeMap.setRemoteRenderingGLView(this);
//		glTreeMap.setRemoteLevelElement(heatMapRemoteElement);
//		heatMapRemoteElement.setGLView(glHeatMapView);

		//glTreeMap.setContentVAType(ContentVAType.CONTENT_EMBEDDED_HM);
		glTreeMap.initData();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		ArrayList<AGLView> remoteRenderedViews = new ArrayList<AGLView>();
		remoteRenderedViews.add(glTreeMap);
		return remoteRenderedViews;
	}
}
