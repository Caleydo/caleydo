package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeMouseOverEvent;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeMouseOverListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Rendering the dendrogram.
 * 
 * @author Bernhard Schlegl
 */
public class GLDendrogramVertical
	extends AGLDendrogram {

	boolean bUseDetailLevel = true;

	private Tree<ClusterNode> tree;
	DendrogramRenderStyle renderStyle;

	private boolean bIsDraggingActive = false;
	private float fPosCut = viewFrustum.getHeight() - 0.3f;

	private float xPosInit = 0.5f;
	private float ymin = 0;
	private float fSampleWidth = 0;
	private float fLevelHeight = 0;

	private int iMaxDepth = 0;

	private ColorMapping colorMapper;

	private TreePorter treePorter = new TreePorter();

	private ClusterNodeMouseOverListener clusterNodeMouseOverListener;

	/**
	 * Constructor.
	 * 
	 * @param glCanvass
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLDendrogramVertical(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum);

		viewType = EManagedObjectType.GL_DENDOGRAM;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		renderStyle = new DendrogramRenderStyle(this, viewFrustum);
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		clusterNodeMouseOverListener = new ClusterNodeMouseOverListener();
		clusterNodeMouseOverListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeMouseOverEvent.class, clusterNodeMouseOverListener);

	}

	@Override
	public void init(GL gl) {

	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {

		this.remoteRenderingGLView = remoteRenderingGLCanvas;

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public synchronized void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}
	}

	@Override
	public synchronized void displayLocal(GL gl) {
		if (set == null)
			return;

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public synchronized void displayRemote(GL gl) {
		if (set == null)
			return;

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public synchronized void display(GL gl) {

		// GLHelperFunctions.drawAxis(gl);

		if (bIsDraggingActive) {
			handleDragging(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingActive = false;
			}
		}

		gl.glCallList(iGLDisplayListToCall);
	}

	/**
	 * Render the handles for the "cut of value"
	 * 
	 * @param gl
	 */
	private void renderCut(final GL gl) {

		float fWidth = 0.1f;
		float fHeight = viewFrustum.getHeight() - 0.2f;

		gl.glColor4f(0f, 0f, 0f, 0.4f);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0.1f, 0f, 0);
		gl.glVertex3f(0.1f, fHeight, 0);
		gl.glVertex3f(0.1f + fWidth, fHeight, 0);
		gl.glVertex3f(0.1f + fWidth, 0f, 0);
		gl.glEnd();

		gl.glColor4f(1f, 0f, 0f, 0.4f);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_CUT_VERTI_SELECTION, 1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0, fPosCut, 0);
		gl.glVertex3f(0.2f + fWidth, fPosCut, 0);
		gl.glVertex3f(0.2f + fWidth, fPosCut + 0.1f, 0);
		gl.glVertex3f(0.0f, fPosCut + 0.1f, 0);
		gl.glEnd();
		gl.glPopName();

	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl) {
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.DENDROGRAM_VERTICAL_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	/**
	 * This function calls a recursive function which is responsible for the calculation of the position
	 * inside the view frustum of the nodes in the dendrogram
	 */
	private void determinePositions() {

		determinePosRec(tree.getRoot());

	}

	/**
	 * Function calculates for each node in the dendrogram recursive the corresponding position inside the
	 * view frustum
	 * 
	 * @param currentNode
	 *            current node for calculation
	 * @return Vec3f position of the current node
	 */
	private Vec3f determinePosRec(ClusterNode currentNode) {

		Vec3f pos = new Vec3f();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = (ClusterNode) alChilds.get(i);
				positions[i] = determinePosRec(node);
			}

			float fXmax = Float.MIN_VALUE;
			float fXmin = Float.MAX_VALUE;
			float fYmax = Float.MIN_VALUE;
			float fYmin = Float.MAX_VALUE;

			for (Vec3f vec : positions) {
				fXmax = Math.max(fXmax, vec.x());
				fXmin = Math.min(fXmin, vec.x());
				fYmax = Math.max(fYmax, vec.y());
				fYmin = Math.min(fYmin, vec.y());
			}

			pos.setX(fXmin + (fXmax - fXmin) / 2);
			pos.setY(fYmax + fLevelHeight);
			pos.setZ(0f);

		}
		else {
			pos.setX(xPosInit);
			xPosInit += fSampleWidth;
			pos.setY(ymin);
			pos.setZ(0f);
		}

		currentNode.setPos(pos);

		return pos;
	}

	/**
	 * Render a node of the dendrogram (recursive)
	 * 
	 * @param gl
	 * @param currentNode
	 */
	private void renderDendrogram(final GL gl, ClusterNode currentNode) {

		if (currentNode.getSelectionType() == ESelectionType.MOUSE_OVER) {
			gl.glColor4fv(MOUSE_OVER_COLOR, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.05f, currentNode.getPos().y() - 0.05f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.05f, currentNode.getPos().y() - 0.05f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.05f, currentNode.getPos().y() + 0.05f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() - 0.05f, currentNode.getPos().y() + 0.05f, currentNode
				.getPos().z());
			gl.glEnd();

		}
		else if (currentNode.getSelectionType() == ESelectionType.SELECTION) {
			gl.glColor4fv(SELECTED_COLOR, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.05f, currentNode.getPos().y() - 0.05f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.05f, currentNode.getPos().y() - 0.05f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.05f, currentNode.getPos().y() + 0.05f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() - 0.05f, currentNode.getPos().y() + 0.05f, currentNode
				.getPos().z());
			gl.glEnd();

		}
		else {
			float fLookupValue = currentNode.getAverageExpressionValue();

			float[] fArMappingColor = colorMapper.getColor(fLookupValue);
			float fOpacity = 1;

			gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		}

		float fDiff = 0;
		float fTemp = currentNode.getPos().y();

		List<ClusterNode> listGraph = null;

		if (tree.hasChildren(currentNode)) {

			listGraph = tree.getChildren(currentNode);

			int iNrChildsNode = listGraph.size();

			float xmin = Float.MAX_VALUE;
			float xmax = Float.MIN_VALUE;
			float ymax = Float.MIN_VALUE;
			float ymin = Float.MAX_VALUE;

			Vec3f[] temp = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = (ClusterNode) listGraph.get(i);

				temp[i] = new Vec3f();
				temp[i].setX(current.getPos().x());
				temp[i].setY(current.getPos().y());
				temp[i].setZ(current.getPos().z());

				xmax = Math.max(xmax, current.getPos().x());
				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				if (current.getDepth() > iMaxDepth)
					renderDendrogram(gl, current);
			}

			fDiff = fTemp - ymax;

			// if (currentNode.getSelectionType() == ESelectionType.MOUSE_OVER) {
			// gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			// }
			// else if (currentNode.getSelectionType() == ESelectionType.SELECTION) {
			// gl.glColor4fv(SELECTED_COLOR, 0);
			// }
			// else {
			// gl.glColor4f(0, 0, 0, 1);
			// }

			float fLookupValue = currentNode.getAverageExpressionValue();

			float[] fArMappingColor = colorMapper.getColor(fLookupValue);
			float fOpacity = 1;

			gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_VERTI_SELECTION,
				currentNode.getClusterNr()));

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymax - 0.0f, currentNode.getPos().z());
			gl.glVertex3f(xmax, ymax - 0.0f, currentNode.getPos().z());
			gl.glEnd();

			gl.glColor4f(0, 0, 0, 1);

			for (int i = 0; i < iNrChildsNode; i++) {

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(temp[i].x(), ymax + 0.0f, temp[i].z());
				gl.glVertex3f(temp[i].x(), temp[i].y() + 0.0f, temp[i].z());
				gl.glEnd();

			}
			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_VERTI_SELECTION,
				currentNode.getClusterNr()));

			float fLookupValue = currentNode.getAverageExpressionValue();

			float[] fArMappingColor = colorMapper.getColor(fLookupValue);
			float fOpacity = 1;

			gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
			gl
				.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y() - 0.1f, currentNode.getPos()
					.z());
			gl.glEnd();

			gl.glPopName();
		}

		float fLookupValue = currentNode.getAverageExpressionValue();

		float[] fArMappingColor = colorMapper.getColor(fLookupValue);
		float fOpacity = 1;

		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y() - fDiff, currentNode.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
		gl.glEnd();

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		// tree = treePorter.importTree("riesen_baum.xml");

		// tree = set.getClusteredTreeExps();
		if (tree == null) {

			if (set.getClusteredTreeExps() != null) {
				tree = set.getClusteredTreeExps();
			}

			renderSymbol(gl);

		}
		else {

			if (tree != null) {
				ymin = 0.1f;
				fSampleWidth = (viewFrustum.getWidth() - 1f) / tree.getRoot().getNrElements();
				fLevelHeight = (viewFrustum.getHeight() - 0.7f) / tree.getRoot().getDepth();
				xPosInit = 0.4f;
				determinePositions();
			}

			gl.glTranslatef(0, 0.1f, 0);
			gl.glLineWidth(0.1f);

			renderDendrogram(gl, tree.getRoot());

			renderCut(gl);

			gl.glTranslatef(0, -0.1f, 0);
		}
		gl.glEndList();
	}

	/**
	 * Function used for updating cursor position in case of dragging
	 * 
	 * @param gl
	 */
	private void handleDragging(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fWidth = viewFrustum.getHeight() - 0.3f;

		if ((fArTargetWorldCoordinates[1] - 0.1f) > 0.0f && (fArTargetWorldCoordinates[1] - 0.1f) < fWidth)
			fPosCut = fArTargetWorldCoordinates[1] - 0.1f;

		setDisplayListDirty();

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActive = false;

			determineSelectedNodes();

		}
	}

	/**
	 * This function calls a recursive function which is responsible for setting nodes in the dendrogram
	 * selected/mouseOver depending on the current position of the "cut"
	 */
	private void determineSelectedNodes() {

		determineSelectedNodesRec(tree.getRoot());

	}

	/**
	 * Determines for each node in the dendrogram if the current node is selected by the "cut" or not
	 * 
	 * @param node
	 *            current node
	 */
	private void determineSelectedNodesRec(ClusterNode node) {

		if (node.getPos().y() > fPosCut)
			node.setSelectionType(ESelectionType.MOUSE_OVER);
		else
			node.setSelectionType(ESelectionType.NORMAL);

		if (tree.hasChildren(node)) {
			for (ClusterNode current : tree.getChildren(node)) {
				determineSelectedNodesRec(current);
			}
		}

	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		boolean bupdateSelectionManager = false;
		boolean bTriggerClusterNodeEvent = false;
		ESelectionType eSelectionType = ESelectionType.NORMAL;

		switch (ePickingType) {

			case DENDROGRAM_CUT_VERTI_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						break;
					case DRAGGED:
						bIsDraggingActive = true;
						setDisplayListDirty();
						break;
					case MOUSE_OVER:
						break;
				}
				break;

			case DENDROGRAM_VERTI_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						if (contentSelectionManager.checkStatus(iExternalID))
							bupdateSelectionManager = true;
						if (storageSelectionManager.checkStatus(iExternalID))
							bupdateSelectionManager = true;

						if (tree.getNodeByNumber(iExternalID) != null)
							tree.getNodeByNumber(iExternalID).setSelectionType(ESelectionType.SELECTION);
						setDisplayListDirty();
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						// if (contentSelectionManager.checkStatus(iExternalID) == false)
						bTriggerClusterNodeEvent = true;
						// if (tree.getNodeByNumber(iExternalID) != null)
						// System.out.println(tree.getNodeByNumber(iExternalID).getNodeName());
						break;
				}

				if (bupdateSelectionManager) {

					ISelectionDelta selectionDelta = null;

					storageSelectionManager.clearSelection(eSelectionType);
					storageSelectionManager.addToType(eSelectionType, iExternalID);

					selectionDelta = storageSelectionManager.getDelta();
					// }

					handleConnectedElementRep(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta(selectionDelta);
					event.setInfo(getShortInfo());
					eventPublisher.triggerEvent(event);

				}
				if (bTriggerClusterNodeEvent) {
					if (tree.getNodeByNumber(iExternalID) != null) {
						ClusterNodeMouseOverEvent event = new ClusterNodeMouseOverEvent();
						event.setSender(this);

						event.setClusterNodeName(tree.getNodeByNumber(iExternalID).getNodeName());
						eventPublisher.triggerEvent(event);
					}
				}
				pickingManager.flushHits(iUniqueID, ePickingType);

				break;
		}
	}

	@Override
	public String getShortInfo() {
		return new String("Dendrogram view");
	}

	@Override
	public String getDetailedInfo() {
		return new String("Dendrogram view");
	}

	@Override
	public void clearAllSelections() {
		fPosCut = 0f;
		Set<ClusterNode> nodeSet = tree.getGraph().vertexSet();
		for (ClusterNode node : nodeSet) {
			node.setSelectionType(ESelectionType.NORMAL);
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void broadcastElements() {
	}

	@Override
	public void changeOrientation(boolean defaultOrientation) {
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int storageIndex)
		throws InvalidAttributeValueException {
		return null;
	}

	@Override
	protected void initLists() {
		if (bRenderOnlyContext) {
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		}
		else {
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
				initCompleteList();
			}
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);

		}
		iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		storageSelectionManager.setVA(set.getVA(iStorageVAID));
	}

	/**
	 * Function called any time a update is triggered external
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(boolean scrollToSelection) {

		if (tree != null) {

			int iIndex;

			Set<ClusterNode> nodeSet = tree.getGraph().vertexSet();
			for (ClusterNode node : nodeSet) {
				node.setSelectionType(ESelectionType.NORMAL);
			}

			Set<Integer> setMouseOverElements =
				storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
			for (Integer iSelectedID : setMouseOverElements) {
				iIndex = set.getVA(iStorageVAID).indexOf(iSelectedID);
				if (tree.getNodeByNumber(iIndex) != null)
					tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.MOUSE_OVER);
			}

			Set<Integer> setSelectionElements = storageSelectionManager.getElements(ESelectionType.SELECTION);
			for (Integer iSelectedID : setSelectionElements) {
				iIndex = set.getVA(iStorageVAID).indexOf(iSelectedID);
				if (tree.getNodeByNumber(iIndex) != null)
					tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.SELECTION);
			}
			setDisplayListDirty();
		}
	}

	@Override
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {
	}

	@Override
	public boolean isInDefaultOrientation() {
		return false;
	}

	@Override
	public void renderContext(boolean renderContext) {
	}

	@Override
	public synchronized void resetView() {
		tree = null;
	}

	@Override
	public void handleMouseOver(String clusterNodeName) {
		// System.out.println(clusterNodeName);
	}

	private void resetAllTreeSelections() {
		resetAllTreeSelectionsRec(tree.getRoot());
	}

	private void resetAllTreeSelectionsRec(ClusterNode currentNode) {

		currentNode.setSelectionType(ESelectionType.NORMAL);

		if (tree.hasChildren(currentNode)) {
			for (ClusterNode current : tree.getChildren(currentNode)) {
				determineSelectedNodesRec(current);
			}
		}

	}

	public void handleMouseOver(int clusterNr) {
		if (tree.getNodeByNumber(clusterNr) != null)
			tree.getNodeByNumber(clusterNr).setSelectionType(ESelectionType.MOUSE_OVER);

		setDisplayListDirty();
	}
}
