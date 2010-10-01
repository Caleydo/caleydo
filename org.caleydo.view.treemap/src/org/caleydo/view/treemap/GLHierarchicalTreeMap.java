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

import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.manager.event.view.treemap.ToggleColoringModeEvent;
import org.caleydo.core.manager.event.view.treemap.ToggleLabelEvent;
import org.caleydo.core.manager.event.view.treemap.ZoomInEvent;
import org.caleydo.core.manager.event.view.treemap.ZoomOutEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceStorageVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.StorageVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.treemap.layout.ATreeMapNode;
import org.caleydo.view.treemap.layout.TreeMapRenderer;
import org.caleydo.view.treemap.listener.ToggleColoringModeListener;
import org.caleydo.view.treemap.listener.ToggleLabelListener;
import org.caleydo.view.treemap.listener.ZoomInListener;
import org.caleydo.view.treemap.listener.ZoomOutListener;
import org.caleydo.view.treemap.renderstyle.TreeMapRenderStyle;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Rendering the Treemap
 * 
 * @author Alexander Lex
 */

public class GLHierarchicalTreeMap extends AGLView implements IViewCommandHandler, IDataDomainSetBasedView, ISelectionUpdateHandler, IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.treemap.hierarchical";

	private TreeMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private SelectionManager treeSelectionManager;

	TreeMapRenderer painter;

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

	private boolean bDisplayData = false;

	int mouseOverClusterId;
	int mouseWheeleSelectionHeight = 0;

	// private EIDType primaryIDType = EIDType.CLUSTER_NUMBER;

	private SelectionUpdateListener selectionUpdateListener;

	private GLTreeMap mainTreeMapView;

	private Vector<GLTreeMap> thumbnailTreemapViews = new Vector<GLTreeMap>(4);

	int thumbnailDisplayList;

	private ZoomInListener zoomInListener;
	private ZoomOutListener zoomOutListener;


	private float xMargin = 0.05f;
	private float yMargin = 0.01f;

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

		// colorMapper =
		// ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		parentGLCanvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mainTreeMapView.processMousePressed(e);
				// System.out.println(e);
				// if (e.getButton() == MouseEvent.BUTTON1) {
				// treeSelectionManager.clearSelection(SelectionType.SELECTION);
				// treeSelectionManager.addToType(SelectionType.SELECTION,
				// mouseOverClusterId);
				// bIsMouseWheeleUsed = true;
				// mouseWheeleSelectionHeight = 0;
				// }
			}

			public void mouseReleased(MouseEvent e) {
				mainTreeMapView.processMouseReleased(e);
				// System.out.println(e);
				// if (e.getButton() == MouseEvent.BUTTON1) {
				// bIsMouseWheeleUsed = false;
				// }
			}
		});

		parentGLCanvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				mainTreeMapView.processMouseWheeleEvent(e);

				// System.out.println(e);
				// if (bIsMouseWheeleUsed) {
				// // System.out.println("wheel used: " +
				// // e.getWheelRotation());
				// if (e.getWheelRotation() > 0) {
				// ATreeMapNode node =
				// treeMapModel.getNodeByNumber(mouseOverClusterId);
				// mouseWheeleSelectionHeight++;
				// // System.out.println("selectionlevel: " +
				// // node.selectionLevel);
				// ATreeMapNode parent =
				// node.getParentWithLevel(node.getHierarchyLevel() -
				// mouseWheeleSelectionHeight);
				// if (parent != null) {
				// treeSelectionManager.clearSelection(SelectionType.SELECTION);
				// treeSelectionManager.addToType(SelectionType.SELECTION,
				// parent.getID());
				// } else {
				// mouseWheeleSelectionHeight--;
				// }
				//
				// } else {
				// ATreeMapNode node =
				// treeMapModel.getNodeByNumber(mouseOverClusterId);
				// if (mouseWheeleSelectionHeight > 0) {
				// mouseWheeleSelectionHeight--;
				// ATreeMapNode parent;
				// if (mouseWheeleSelectionHeight == 0)
				// parent = node;
				// else
				// parent = node.getParentWithLevel(node.getHierarchyLevel() -
				// mouseWheeleSelectionHeight);
				// treeSelectionManager.clearSelection(SelectionType.SELECTION);
				// treeSelectionManager.addToType(SelectionType.SELECTION,
				// parent.getID());
				// }
				//
				// }
				// setHighLichtingListDirty();
				// }
			}
		});

		// treeSelectionManager = new SelectionManager(primaryIDType);
//		setMainTreeMapView(createEmbeddedTreeMap());
//		mainTreeMapView.setRemotePickingManager(pickingManager, getID());

	}

	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TreeMapRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		// mainTreeMapView = createEmbeddedTreeMap();
		setMainTreeMapView(createEmbeddedTreeMap());
		mainTreeMapView.setRemotePickingManager(pickingManager, getID());

		mainTreeMapView.initRemote(gl, this, glMouseListener, null);
		mainTreeMapView.setDrawLabel(true);

		thumbnailDisplayList = gl.glGenLists(1);
	}

	@Override
	public void initLocal(GL gl) {

		if (mainTreeMapView != null)
			mainTreeMapView.processEvents();

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

		// tree =
		// dataDomain.getSet().getContentData(contentVAType).getContentTree();
		init(gl);

		// gl.glNewList(iGLDisplayListIndexLocal, GL.GL_COMPILE);
		// gl.glEndList();

		// ScatterPlotRenderStyle.setTextureNr(NR_TEXTURESX,NR_TEXTURESY);

		// TODO: check when to update data

	}

	public void initData() {
		tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
		bDisplayData = tree != null;
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		for (GLTreeMap view : thumbnailTreemapViews)
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
		if (bDisplayData) {
			if (thumbnailTreemapViews.size() > 0) {

				displayThumbnailTreemaps(gl);

				mainTreeMapView.getViewFrustum().setTop((float) (viewFrustum.getTop() - viewFrustum.getHeight() * 0.2));
				mainTreeMapView.getViewFrustum().setBottom(viewFrustum.getBottom());
				mainTreeMapView.getViewFrustum().setLeft(viewFrustum.getLeft());
				mainTreeMapView.getViewFrustum().setRight(viewFrustum.getRight());

				mainTreeMapView.displayRemote(gl);
			} else {

				mainTreeMapView.getViewFrustum().setTop(viewFrustum.getTop());
				mainTreeMapView.getViewFrustum().setBottom(viewFrustum.getBottom());
				mainTreeMapView.getViewFrustum().setLeft(viewFrustum.getLeft());
				mainTreeMapView.getViewFrustum().setRight(viewFrustum.getRight());
				mainTreeMapView.displayRemote(gl);
			}
		} else {
			renderSymbol(gl, EIconTextures.RADIAL_SYMBOL, 0.5f);
		}

	}

	private void displayThumbnailTreemaps(GL gl) {
		int maxThumbNailViews = 3;

		// double thumbNailWidth = 0.26;
		double thumbNailWidth = (1 - xMargin * (maxThumbNailViews + 1)) / maxThumbNailViews;
		double thumbNailHeight = 0.18;
		double xOffset = 0;
		// double yOffset = 0;
		if (thumbnailTreemapViews.size() > 3)
			drawArrow(gl, (float) xOffset, (float) (1.0f - yMargin - thumbNailHeight), (float) (xOffset + xMargin), (float) (1 - yMargin));
		for (int i = Math.max(0, thumbnailTreemapViews.size() - maxThumbNailViews); /*
																					 * i
																					 * <
																					 * maxThumbNailViews
																					 * &&
																					 */i < thumbnailTreemapViews.size(); i++) {
			xOffset += xMargin;

			GLTreeMap treemap = thumbnailTreemapViews.get(i);

			treemap.getViewFrustum().setLeft((float) (viewFrustum.getLeft() + viewFrustum.getWidth() * xOffset));
			treemap.getViewFrustum().setRight((float) (viewFrustum.getLeft() + viewFrustum.getWidth() * (xOffset + thumbNailWidth)));
			treemap.getViewFrustum().setBottom((float) (viewFrustum.getTop() - viewFrustum.getHeight() * (yMargin + thumbNailHeight)));
			treemap.getViewFrustum().setTop((float) (viewFrustum.getTop() - viewFrustum.getHeight() * yMargin));

			gl.glPushMatrix();
			gl.glTranslated(viewFrustum.getWidth() * xOffset, viewFrustum.getHeight() * (1.0 - yMargin - thumbNailHeight), 0);
			gl.glPushName(pickingManager.getPickingID(getID(), EPickingType.TREEMAP_THUMBNAILVIEW_SELECTED, i));
			treemap.displayRemote(gl);
			gl.glPopName();
			gl.glPopMatrix();

			xOffset += thumbNailWidth;

			drawArrow(gl, (float) xOffset, (float) (1.0f - yMargin - thumbNailHeight), (float) (xOffset + xMargin), (float) (1 - yMargin));
		}
	}

	private void drawArrow(GL gl, float x, float y, float xmax, float ymax) {
		x = (x + 0.01f) * viewFrustum.getWidth();
		y = y * viewFrustum.getHeight();
		xmax = (xmax - 0.01f) * viewFrustum.getWidth();
		ymax = ymax * viewFrustum.getHeight();
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(x, ymax, 0);
		gl.glVertex3f(xmax, y + (ymax - y) / 2, 0);
		gl.glEnd();

	}

	// copied from radialhierarchy
	protected void renderSymbol(GL gl, EIconTextures texture, float buttonSize) {

		float xButtonOrigin = viewFrustum.getLeft() + viewFrustum.getWidth() / 2 - buttonSize / 2;
		float yButtonOrigin = viewFrustum.getBottom() + viewFrustum.getHeight() / 2 - buttonSize / 2;
		Texture tempTexture = textureManager.getIconTexture(gl, texture);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(xButtonOrigin, yButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(xButtonOrigin, yButtonOrigin + buttonSize, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(xButtonOrigin + buttonSize, yButtonOrigin + buttonSize, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(xButtonOrigin + buttonSize, yButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	public void zoomIn() {
		System.out.println("zooming!!!!!");
		Set<Integer> elements = mainTreeMapView.getSelectionManager().getElements(SelectionType.SELECTION);
		if (elements.size() == 1 /* && thumbnailTreemapViews.size() < 3 */) {

			ClusterNode dataRoot = tree.getNodeByNumber(elements.iterator().next());

			mainTreeMapView.setRemotePickingManager(null, 0);
			mainTreeMapView.clearAllSelections();
			mainTreeMapView.getSelectionManager().addToType(SelectionType.SELECTION, dataRoot.getID());
			mainTreeMapView.setDrawLabel(false);

			thumbnailTreemapViews.add(mainTreeMapView);

			// mainTreeMapView = createEmbeddedTreeMap();
			setMainTreeMapView(createEmbeddedTreeMap());
			mainTreeMapView.setDrawLabel(true);

			mainTreeMapView.setRootClusterID(dataRoot.getID());
			mainTreeMapView.setZoomActive(true);
			mainTreeMapView.initData();

			setDisplayListDirty();

		}

	};

	public void zoomOut() {
		zoomOut(thumbnailTreemapViews.size() - 1);
	}

	private void zoomOut(int index) {
		if (thumbnailTreemapViews.size() > 0) {
			// mainTreeMapView = thumbnailTreemapViews.get(index);
			setMainTreeMapView(thumbnailTreemapViews.get(index));
			for (int i = thumbnailTreemapViews.size() - 1; i >= index; i--){
				thumbnailTreemapViews.get(i).unregisterEventListeners();
				thumbnailTreemapViews.remove(i);
			}

			mainTreeMapView.setDrawLabel(true);
			mainTreeMapView.setRemotePickingManager(pickingManager, getID());
			setDisplayListDirty();
		}
	}

	public void setDisplayListDirty() {
		super.setDisplayListDirty();
		if (bDisplayData) {
			mainTreeMapView.setDisplayListDirty();
			for (GLTreeMap view : thumbnailTreemapViews)
				view.setDisplayListDirty();
		}
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
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode, int iExternalID, Pick pick) {

		System.out.println(pickingType + " " + pickingMode + ": " + iExternalID);

		if (pickingType == EPickingType.TREEMAP_THUMBNAILVIEW_SELECTED && pickingMode == EPickingMode.DOUBLE_CLICKED) {
			zoomOut(iExternalID);
		} else
			mainTreeMapView.handleRemotePickingEvents(pickingType, pickingMode, iExternalID, pick);

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
		selectionUpdateListener.setDataDomainType(dataDomain.getDataDomainType());
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		zoomInListener = new ZoomInListener();
		zoomInListener.setHandler(this);
		eventPublisher.addListener(ZoomInEvent.class, zoomInListener);

		zoomOutListener = new ZoomOutListener();
		zoomOutListener.setHandler(this);
		eventPublisher.addListener(ZoomOutEvent.class, zoomOutListener);


//		ToggleLabelListener labelListener = new ToggleLabelListener();
//		labelListener.setHandler(this);
//		eventPublisher.addListener(ToggleLabelEvent.class, labelListener);
			
			
	
		
		// replaceContentVAListener = new ReplaceContentVAListener();
		// replaceContentVAListener.setHandler(this);
		// replaceContentVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		// eventPublisher.addListener(ReplaceContentVAEvent.class,
		// replaceContentVAListener);
		//
		// replaceStorageVAListener = new ReplaceStorageVAListener();
		// replaceStorageVAListener.setHandler(this);
		// replaceStorageVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		// eventPublisher.addListener(ReplaceStorageVAEvent.class,
		// replaceStorageVAListener);
		//
		//
		// contentVAUpdateListener = new ContentVAUpdateListener();
		// contentVAUpdateListener.setHandler(this);
		// contentVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		// eventPublisher.addListener(ContentVAUpdateEvent.class,
		// contentVAUpdateListener);
		//
		// storageVAUpdateListener = new StorageVAUpdateListener();
		// storageVAUpdateListener.setHandler(this);
		// storageVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		// eventPublisher.addListener(StorageVAUpdateEvent.class,
		// storageVAUpdateListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		eventPublisher.removeListener(zoomInListener);
		eventPublisher.removeListener(zoomOutListener);
		
		if(mainTreeMapView!=null)
			mainTreeMapView.unregisterEventListeners();
		
		for(GLTreeMap view: thumbnailTreemapViews){
			view.unregisterEventListeners();
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
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// treeSelectionManager.setDelta(selectionDelta);

		// TODO move to glTreeMap, listeners as well

		if (dataDomain.getContentIDType() == selectionDelta.getIDType()) {
			for (SelectionDeltaItem item : selectionDelta) {
				//

				ArrayList<Integer> nodeIDs = tree.getNodeIDsFromLeafID(item.getPrimaryID());
			}

			// treeSelectionManager.setDelta(newDelta)

		}

		if (treeSelectionManager.getIDType() == selectionDelta.getIDType())
			treeSelectionManager.setDelta(selectionDelta);

		if (selectionDelta.getIDType() == dataDomain.getStorageIDType()) {
			// StorageSelectionManager storageSelectionManager =
			// dataDomain.getStorageSelectionManager();
			// storageSelectionManager.setDelta()

			// // todo: colors for storages, this should be done somewhere else
			// Set<Integer> storageIDs =
			// storageSelectionManager.getElements(SelectionType.SELECTION);
			//
			// for (Integer storageID : storageIDs) {
			// int expressionValue =
			// dataDomain.getSet().get(storageID).get(EDataRepresentation.NORMALIZED,
			// leafID);
			// }

		}

		setDisplayListDirty();
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		if (dataDomain != null) {
			tree = dataDomain.getSet().getContentData(contentVAType).getContentTree();
			if (tree != null) {
				treeSelectionManager = new SelectionManager(tree.getNodeIDType());
				// TODO set selectionmanager
				for (GLTreeMap view : thumbnailTreemapViews) {
					view.setDataDomain(dataDomain);
					// view.setSelectionManager(treeSelectionManager);
				}
				bDisplayData = true;
				;
			}
		} else
			bDisplayData = false;
	}

	public void setHighLichtingListDirty() {
		bIsHighlightingListDirty = true;
	}

	private GLTreeMap createEmbeddedTreeMap() {

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, (int) fHeatMapHeight, 0, (int) fHeatMapWidth, -20, 20);

		GLTreeMap treemap = new GLTreeMap(this.getParentGLCanvas(), viewFrustum);
		treemap.setDataDomain(dataDomain);
		treemap.setRemoteRenderingGLView(this);
		treemap.registerEventListeners();
		treemap.setRemotePickingManager(pickingManager, getID());
		treemap.initData();
		return treemap;
	}

	// private GLTreeMap createEmbeddedTreeMap(){
	// CmdCreateView cmdView = (CmdCreateView)
	// generalManager.getCommandManager().createCommandByType(ECommandType.CREATE_GL_VIEW);
	// cmdView.setViewID(GLTreeMap.VIEW_ID);
	//
	// float fHeatMapHeight = viewFrustum.getHeight();
	// float fHeatMapWidth = viewFrustum.getWidth();
	//
	// cmdView.setAttributes(CameraProjectionMode.ORTHOGRAPHIC, 0,
	// fHeatMapHeight, 0, fHeatMapWidth, -20, 20, -1);
	// cmdView.setDataDomainType(dataDomain.getDataDomainType());
	// cmdView.doCommand();
	//
	// GLTreeMap treeMap = (GLTreeMap) cmdView.getCreatedObject();
	// treeMap.setDataDomain(dataDomain);
	// treeMap.setRemoteRenderingGLView(this);
	// //treeMap.setRemotePickingManager(pickingManager, getID());
	// treeMap.initData();
	//
	// return treeMap;
	//
	// }

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		ArrayList<AGLView> remoteRenderedViews = new ArrayList<AGLView>();
		remoteRenderedViews.add(mainTreeMapView);
		return remoteRenderedViews;
	}

	private void setMainTreeMapView(GLTreeMap treemap) {
		mainTreeMapView = treemap;
	}
	
	public void toogleLabel(){
		System.out.println("toggle label");
	}

	

}
