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

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.graph.tree.TreePorter;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingMouseListener;
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
public class GLDendrogram
	extends AStorageBasedView {

	boolean bUseDetailLevel = true;
	ISet set;

	private Tree<ClusterNode> tree;
	DendrogramRenderStyle renderStyle;

	private boolean bRenderGeneTree = true;

	private boolean bIsDraggingActive = false;
	private float fPosCut = 0.0f;

	private float yPosInit = 0.5f;
	private float xmax = 6;
	private float fSampleHeight = 0;
	private float fLevelHeight = 0;

	private ColorMapping colorMapper;

	private TreePorter treePorter = new TreePorter();

	/**
	 * Constructor.
	 * 
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLDendrogram(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum) {
		super(iGLCanvasID, sLabel, viewFrustum);

		viewType = EManagedObjectType.GL_DENDOGRAM;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager =
			new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX).externalIDType(
				EIDType.REFSEQ_MRNA_INT).mappingType(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT,
				EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		renderStyle = new DendrogramRenderStyle(this, viewFrustum);
		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
	}

	@Override
	public void init(GL gl) {

		if (tree != null) {
			tree = null;
		}

		if (bRenderGeneTree)
			tree = set.getClusteredTreeGenes();
		else
			tree = set.getClusteredTreeExps();

	}

	@Override
	public void initLocal(GL gl) {

		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR, this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
		final PickingMouseListener pickingTriggerMouseAdapter,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

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

		pickingManager.handlePicking(iUniqueID, gl);

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
			if (pickingTriggerMouseAdapter.wasMouseReleased()) {
				bIsDraggingActive = false;
			}
		}

		gl.glCallList(iGLDisplayListToCall);
	}

	private void renderCut(final GL gl) {

		float fHeight = 0.1f;
		float fWidth = viewFrustum.getWidth() - 0.2f;

		gl.glColor4f(0f, 0f, 0f, 0.4f);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0, 0.1f, 0);
		gl.glVertex3f(0, 0.1f + fHeight, 0);
		gl.glVertex3f(fWidth, 0.1f + fHeight, 0);
		gl.glVertex3f(fWidth, 0.1f, 0);
		gl.glEnd();

		gl.glColor4f(1f, 0f, 0f, 0.4f);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_CUT_SELECTION, 1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fPosCut, 0.0f, 0);
		gl.glVertex3f(fPosCut, 0.2f + fHeight, 0);
		gl.glVertex3f(fPosCut + 0.1f, 0.2f + fHeight, 0);
		gl.glVertex3f(fPosCut + 0.1f, 0.0f, 0);
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
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_SYMBOL);
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

	private void determinePositions() {

		determinePosRec(tree.getRoot());

	}

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

			float fXmin = Float.MAX_VALUE;
			float fYmax = Float.MIN_VALUE;
			float fYmin = Float.MAX_VALUE;

			for (Vec3f vec : positions) {
				fXmin = Math.min(fXmin, vec.x());
				fYmax = Math.max(fYmax, vec.y());
				fYmin = Math.min(fYmin, vec.y());
			}

			pos.setX(fXmin - fLevelHeight);

			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(0f);

		}
		else {
			pos.setY(yPosInit);
			yPosInit -= fSampleHeight;
			pos.setX(xmax);
			pos.setZ(0f);
		}

		currentNode.setPos(pos);

		return pos;
	}

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
		float fTemp = currentNode.getPos().x();

		List<ClusterNode> listGraph = null;

		if (tree.hasChildren(currentNode)) {

			listGraph = tree.getChildren(currentNode);

			int iNrChildsNode = listGraph.size();

			float xmin = Float.MAX_VALUE;
			float ymax = Float.MIN_VALUE;
			float ymin = Float.MAX_VALUE;

			Vec3f[] temp = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = (ClusterNode) listGraph.get(i);

				temp[i] = new Vec3f();
				temp[i].setX(current.getPos().x());
				temp[i].setY(current.getPos().y());
				temp[i].setZ(current.getPos().z());

				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				renderDendrogram(gl, current);
			}

			fDiff = fTemp - xmin;

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

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_SELECTION,
				currentNode.getClusterNr()));

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin - 0.1f, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin - 0.1f, ymax, currentNode.getPos().z());
			gl.glEnd();

			gl.glColor4f(0, 0, 0, 1);

			for (int i = 0; i < iNrChildsNode; i++) {

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin - 0.1f, temp[i].y(), temp[i].z());
				gl.glVertex3f(temp[i].x() - 0.1f, temp[i].y(), temp[i].z());
				gl.glEnd();

			}
			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_SELECTION,
				currentNode.getClusterNr()));

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

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
			gl
				.glVertex3f(currentNode.getPos().x() - 0.1f, currentNode.getPos().y(), currentNode.getPos()
					.z());
			gl.glEnd();

			gl.glPopName();
		}

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

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff - 0.1f, currentNode.getPos().y(), currentNode.getPos()
			.z());
		gl.glVertex3f(currentNode.getPos().x() - 0.1f, currentNode.getPos().y(), currentNode.getPos().z());
		gl.glEnd();

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (tree == null) {
			if (bRenderGeneTree)
				tree = set.getClusteredTreeGenes();
			else
				tree = set.getClusteredTreeExps();

			initData();

			// tree = treePorter.importTree("riesen_baum.xml");

			renderSymbol(gl);

		}
		else {

			if (tree != null && bRenderGeneTree) {
				xmax = viewFrustum.getWidth() - 0.2f;
				fSampleHeight = (viewFrustum.getHeight() - 0.7f) / tree.getRoot().getNrElements();
				fLevelHeight = (viewFrustum.getWidth() - 3f) / tree.getRoot().getDepth();
				yPosInit = viewFrustum.getHeight() - 0.4f;
				determinePositions();
			}

			gl.glTranslatef(0.1f, 0, 0);
			gl.glLineWidth(0.1f);

			renderDendrogram(gl, tree.getRoot());

			renderCut(gl);

			gl.glTranslatef(-0.1f, 0, 0);
		}
		gl.glEndList();
	}

	/**
	 * Function used for updating cursor position in case of dragging
	 * 
	 * @param gl
	 */
	private void handleDragging(final GL gl) {
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fWidth = viewFrustum.getWidth() - 0.2f;

		if ((fArTargetWorldCoordinates[0] - 0.1f) > 0.0f
			&& (fArTargetWorldCoordinates[0] - 0.1f) < (fWidth - 0.1f))
			fPosCut = fArTargetWorldCoordinates[0] - 0.1f;

		setDisplayListDirty();

		if (pickingTriggerMouseAdapter.wasMouseReleased()) {
			bIsDraggingActive = false;

			determineSelectedNodes();

		}
	}

	private void determineSelectedNodes() {

		determineSelectedNodesrec(tree.getRoot());

	}

	private void determineSelectedNodesrec(ClusterNode node) {

		if (node.getPos().x() < fPosCut)
			node.setSelectionType(ESelectionType.MOUSE_OVER);
		else
			node.setSelectionType(ESelectionType.NORMAL);

		if (tree.hasChildren(node)) {
			for (ClusterNode current : tree.getChildren(node)) {
				determineSelectedNodesrec(current);
			}
		}

	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
		switch (ePickingType) {

			case DENDROGRAM_CUT_SELECTION:

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

			case DENDROGRAM_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						if (tree.getNodeByNumber(iExternalID) != null)
							tree.getNodeByNumber(iExternalID).setSelectionType(ESelectionType.SELECTION);
						setDisplayListDirty();
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						if (tree.getNodeByNumber(iExternalID) != null)
							System.out.println(tree.getNodeByNumber(iExternalID).getNodeName());
						break;
				}
				break;

		}
	}

	@Override
	public String getShortInfo() {
		return null;
	}

	@Override
	public String getDetailedInfo() {
		return new String("DetailInfo");
	}

	@Override
	public void clearAllSelections() {
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

		int iIndex;

		Set<ClusterNode> nodeSet = tree.getGraph().vertexSet();
		for (ClusterNode node : nodeSet) {
			node.setSelectionType(ESelectionType.NORMAL);
		}

		Set<Integer> setMouseOverElements = contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		for (Integer iSelectedID : setMouseOverElements) {

			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue());
			tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.MOUSE_OVER);
		}

		Set<Integer> setSelectionElements = contentSelectionManager.getElements(ESelectionType.SELECTION);
		for (Integer iSelectedID : setSelectionElements) {

			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue());
			tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.SELECTION);
		}
	}

	@Override
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {

		Set<Integer> setMouseOverElements = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);

		if (setMouseOverElements.size() >= 0) {
			for (Integer iSelectedID : setMouseOverElements) {
				int i = iSelectedID;
				System.out.println("mouse over :" + i);
			}
		}

		Set<Integer> setSelectionElements = storageSelectionManager.getElements(ESelectionType.SELECTION);

		if (setSelectionElements.size() >= 0) {
			for (Integer iSelectedID : setSelectionElements) {
				int i = iSelectedID;
				System.out.println("selected :" + i);
			}
		}
		setDisplayListDirty();

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
		// @Bernhard: something to do here?
	}
}
