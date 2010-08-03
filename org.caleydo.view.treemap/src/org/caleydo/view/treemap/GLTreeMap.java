package org.caleydo.view.treemap;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.ISetBasedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.treemap.layout.ATreeMapNode;
import org.caleydo.view.treemap.layout.AbstractTree;
import org.caleydo.view.treemap.layout.DefaultTree;
import org.caleydo.view.treemap.layout.DefaultTreeNode;
import org.caleydo.view.treemap.layout.GlPainter;
import org.caleydo.view.treemap.layout.SimpleLayoutAlgorithm;
import org.caleydo.view.treemap.renderstyle.TreeMapRenderStyle;

/**
 * Rendering the Treemap
 * 
 * @author Alexander Lex
 */

public class GLTreeMap extends AGLView implements IViewCommandHandler, ISetBasedView,
		ISelectionUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.treemap";

	private TreeMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private SelectionManager treeSelectionManager;

	GlPainter painter;
	
	// private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	// private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	// toggleable feature flags

	boolean bUseDetailLevel = true;

	private boolean bUpdateMainView = false;

	private ISetBasedDataDomain dataDomain;

	private Tree<ClusterNode> tree;

	private EIDType primaryIDType = EIDType.CLUSTER_NUMBER;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLTreeMap(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		// super(glCanvas, sLabel, viewFrustum, null);
		viewType = GLTreeMap.VIEW_ID;

		// ArrayList<SelectionType> alSelectionTypes = new
		// ArrayList<SelectionType>();
		// alSelectionTypes.add(SelectionType.NORMAL);
		// alSelectionTypes.add(SelectionType.MOUSE_OVER);
		// alSelectionTypes.add(SelectionType.SELECTION);

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		treeSelectionManager = new SelectionManager(primaryIDType);
		
		

	}

	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TreeMapRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		painter= new GlPainter(gl, viewFrustum, pickingManager, getID());
		
	}

	@Override
	public void initLocal(GL gl) {

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

		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		init(gl);

		gl.glNewList(iGLDisplayListIndexLocal, GL.GL_COMPILE);
		gl.glEndList();

		// ScatterPlotRenderStyle.setTextureNr(NR_TEXTURESX,NR_TEXTURESY);

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

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
	public void setDetailLevel(EDetailLevel detailLevel) {
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

		if(bIsDisplayListDirtyLocal){
			AbstractTree tree= DefaultTree.createSampleTree();
			SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
			layouter.layout(tree, painter);
			painter.paintTreeMap(tree);
			bIsDisplayListDirtyLocal=false;
		}
			
		painter.paintTreeMapFromCache();
		
		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawPointAt(gl, 1, 1, 1);
//		gl.glPushName(pickingManager.getPickingID(getID(),
//				EPickingType.TREEMAP_ELEMENT_SELECTED, 1));

		// gl.glBegin(GL.GL_QUADS);
		// gl.glColor3f(0, 1, 0);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, 1, 0);
		// gl.glVertex3f(1, 1, 0);
		// gl.glVertex3f(1, 0, 0);
		// gl.glEnd();
		// gl.glPopName();

		//GlPainter painter = new GlPainter(gl, viewFrustum);

		// painter.paintRectangle(0, 0,(float) 1.0/3, 1, Color.RED);
		// painter.paintRectangle((float) 1.0/3, 0,(float) 2.0/3, 1,
		// Color.GREEN);
		// painter.paintRectangle((float) 2.0/3, 0, 1, 1, Color.BLUE);

//		Tree<ClusterNode> contentTree = dataDomain.getSet()
//				.getContentData(ContentVAType.CONTENT).getContentTree();
		// Tree<ClusterNode> storageTree = dataDomain.getSet()
		// .getStorageData(StorageVAType.STORAGE).getStorageTree();

//		AbstractTree tree = DefaultTree.createSampleTree();
//		SimpleLayoutAlgorithm layouter = new SimpleLayoutAlgorithm();
//
//		layouter.layout(tree, painter);
//		painter.paintTreeMap(tree);
//
//		painter.paintRectangle((float) 0.0, (float) 0.0, (float) 1 / 3, (float) 1,
//				Color.YELLOW);

		// SelectionManager contentSelectionManager = dataDomain
		// .getContentSelectionManager();

	}

	@Override
	public String getShortInfo() {

		return "Tree Map";
	}

	@Override
	public String getDetailedInfo() {
		return "Tree Map";

	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (ePickingType) {
		case TREEMAP_ELEMENT_SELECTED:

			// iCurrentMouseOverElement = iExternalID;
			switch (pickingMode) {

			case CLICKED:
				selectionType = SelectionType.SELECTION;
				// selectionType = currentSelection;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.DESELECTED;
				break;
			case DRAGGED:
				selectionType = SelectionType.SELECTION;
				break;
			default:
				return;

			}
			treeSelectionManager.addToType(selectionType, iExternalID);

			// treeSelectionManager.checkStatus(SelectionType.MOUSE_OVER,
			// iElementID);
			//
			// ArrayList<SelectionType> selectionTypes =
			// treeSelectionManager.getSelectionTypes(elementID);
			//
			setDisplayListDirty();
			// TODO do something

			break;

		default:
			return;
		}

	}

	@Override
	public void clearAllSelections() {

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTreeMapView serializedForm = new SerializedTreeMapView(
				dataDomain.getDataDomainType());
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
		return "Standalone Scatterplot, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public RemoteLevelElement getRemoteLevelElement() {

		// If the view is rendered remote - the remote level element from the
		// parent is returned
		if (glRemoteRenderingView != null && glRemoteRenderingView instanceof AGLView)
			return ((AGLView) glRemoteRenderingView).getRemoteLevelElement();

		return super.getRemoteLevelElement();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

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
	public ISetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ISetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// treeSelectionManager.setDelta(selectionDelta);
		setDisplayListDirty();
	}

}
