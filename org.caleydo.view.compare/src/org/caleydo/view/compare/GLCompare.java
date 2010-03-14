package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.view.compare.DuplicateSetBarItemEvent;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.compare.listener.CompareGroupsEventListener;
import org.caleydo.view.compare.listener.DuplicateSetBarItemEventListener;
import org.caleydo.view.compare.state.CompareViewStateController;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLCompare extends AGLView implements IViewCommandHandler,
		IGLRemoteRenderingView, ISelectionUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.compare";

	private ArrayList<ISet> setsToCompare;

	private TextRenderer textRenderer;
	private CompareViewStateController compareViewStateController;

	private CompareGroupsEventListener compareGroupsEventListener;
	private DuplicateSetBarItemEventListener duplicateSetBarItemEventListener;
	private SelectionUpdateListener selectionUpdateListener;

	private boolean isControlPressed;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLCompare(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = VIEW_ID;
		setsToCompare = new ArrayList<ISet>();
		glKeyListener = new GLCompareKeyListener(this);
		isControlPressed = false;
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32),
				true, true);

	}

	@Override
	public void init(GL gl) {
		// contentVA = useCase.getContentVA(ContentVAType.CONTENT);
		// storageVA = useCase.getStorageVA(StorageVAType.STORAGE);
		compareViewStateController = new CompareViewStateController(this,
				iUniqueID, textRenderer, textureManager, pickingManager,
				glMouseListener, contextMenu, dataDomain, useCase);

		compareViewStateController.init(gl);
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

	}

	@Override
	public void displayLocal(GL gl) {

		processEvents();

		compareViewStateController.executeDrawingPreprocessing(gl,
				bIsDisplayListDirtyLocal);

		if (!isVisible())
			return;
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
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			bIsDisplayListDirtyRemote = false;
			buildDisplayList(gl, iGLDisplayListIndexRemote);
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {
		// processEvents();

		compareViewStateController.drawActiveElements(gl);

		gl.glCallList(iGLDisplayListToCall);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		compareViewStateController.drawDisplayListElements(gl);

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	private void renderDendrogram(final GL gl, ClusterNode currentNode,
			float fOpacity, Tree<ClusterNode> tree, float xPosInit) {

		// float fLookupValue = currentNode.getAverageExpressionValue();
		// float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		// if (bUseBlackColoring)
		gl.glColor4f(0, 0, 0, 1);
		// else
		// gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
		// fArMappingColor[2], fOpacity);

		float fDiff = 0;
		float fTemp = currentNode.getPos().x();

		List<ClusterNode> listGraph = null;

		if (tree.hasChildren(currentNode)) {
			listGraph = tree.getChildren(currentNode);

			int iNrChildsNode = listGraph.size();

			float xmin = Float.MAX_VALUE;
			float ymax = Float.MIN_VALUE;
			float ymin = Float.MAX_VALUE;

			Vec3f[] tempPositions = new Vec3f[iNrChildsNode];
			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = listGraph.get(i);

				tempPositions[i] = new Vec3f();
				tempPositions[i].setX(current.getPos().x());
				tempPositions[i].setY(current.getPos().y());
				tempPositions[i].setZ(current.getPos().z());

				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				// if (bEnableDepthCheck && bRenderUntilCut == true) {
				// if (current.getPos().x() <= fPosCut) {
				// // if (current.getSelectionType() !=
				// // SelectionType.DESELECTED) {
				// renderDendrogramGenes(gl, current, 1);
				// // renderDendrogramGenes(gl, current, 0.3f);
				// // gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
				// // fArMappingColor[2], fOpacity);
				// } else {
				// // renderDendrogramGenes(gl, current, 1);
				// bCutOffActive[i] = true;
				// }
				// } else

				renderDendrogram(gl, current, 1, tree, xPosInit);

			}

			fDiff = fTemp - xmin;

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode
							.getID()));

			// vertical line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all children with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin, tempPositions[i].y(), tempPositions[i].z());
				gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
						tempPositions[i].z());
				gl.glEnd();
			}

			gl.glPopName();

		} else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.DENDROGRAM_GENE_LEAF_SELECTION, currentNode
							.getID()));

			// horizontal line visualizing leaf nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(),
					currentNode.getPos().z());
			gl.glVertex3f(xPosInit, currentNode.getPos().y(), currentNode
					.getPos().z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff, currentNode.getPos()
				.y(), currentNode.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(),
				currentNode.getPos().z());
		gl.glEnd();

	}

	
	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		compareViewStateController.handlePickingEvents(ePickingType,
				pickingMode, iExternalID, pick, isControlPressed);
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
		SerializedCompareView serializedForm = new SerializedCompareView(
				dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		compareGroupsEventListener = new CompareGroupsEventListener();
		compareGroupsEventListener.setHandler(this);
		eventPublisher.addListener(CompareGroupsEvent.class,
				compareGroupsEventListener);

		duplicateSetBarItemEventListener = new DuplicateSetBarItemEventListener();
		duplicateSetBarItemEventListener.setHandler(this);
		eventPublisher.addListener(DuplicateSetBarItemEvent.class,
				duplicateSetBarItemEventListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				selectionUpdateListener);

		// if (leftHeatMapWrapper != null)
		// leftHeatMapWrapper.registerEventListeners();
		// if (rightHeatMapWrapper != null)
		// rightHeatMapWrapper.registerEventListeners();
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (compareGroupsEventListener != null) {
			eventPublisher.removeListener(compareGroupsEventListener);
			compareGroupsEventListener = null;
		}
		if (duplicateSetBarItemEventListener != null) {
			eventPublisher.removeListener(duplicateSetBarItemEventListener);
			duplicateSetBarItemEventListener = null;
		}
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: Do it differently?
		return new ArrayList<AGLView>();
	}

	public void setGroupsToCompare(ArrayList<ISet> sets) {
		setsToCompare.clear();
		setsToCompare.addAll(sets);

		ClusterState clusterState = new ClusterState();
		clusterState.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
		clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		clusterState.setAffinityPropClusterFactorGenes(2);
		clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		// clusterState.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);
		// clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		// clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);
		// clusterState.setTreeClustererAlgo(ETreeClustererAlgo.COMPLETE_LINKAGE);

		for (ISet set : sets) {
			set.cluster(clusterState);
		}

		compareViewStateController.setSetsToCompare(sets);
	}

	public boolean isControlPressed() {
		return isControlPressed;
	}

	public void setControlPressed(boolean isControlPressed) {
		this.isControlPressed = isControlPressed;
	}

	public void handleDuplicateSetBarItem(int itemID) {
		compareViewStateController.duplicateSetBarItem(itemID);
		// setBar.handleDuplicateSetBarItem(itemID);
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		compareViewStateController.handleSelectionUpdate(selectionDelta,
				scrollToSelection, info);

	}
}
