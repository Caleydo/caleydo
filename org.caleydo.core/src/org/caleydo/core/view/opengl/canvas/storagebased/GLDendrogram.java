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
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
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
import org.caleydo.core.view.opengl.canvas.listener.UpdateViewListener;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionEvent;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionListener;
import org.caleydo.core.view.opengl.canvas.radial.event.IClusterNodeEventReceiver;
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
public class GLDendrogram
	extends AStorageBasedView
	implements IClusterNodeEventReceiver {

	boolean bUseDetailLevel = true;

	private Tree<ClusterNode> tree;
	private DendrogramRenderStyle renderStyle;

	private ClusterNode currentRootNode;

	private ArrayList<Integer> iAlCutOffClusters = new ArrayList<Integer>();
	private GroupList groupList = null;

	/**
	 * true for gene tree, false for experiment tree
	 */
	private boolean bRenderGeneTree;

	private boolean bIsDraggingActive = false;
	private float fPosCut = 0.0f;

	// for gene tree
	private float yPosInit = 0.5f;
	private float xGlobalMax = 6;
	private float fSampleHeight = 0;
	private float fLevelWidth = 0;

	// for experiment tree
	private float xPosInit = 0.5f;
	private float yGlobalMin = 0;
	private float fSampleWidth = 0;
	private float fLevelHeight = 0;

	private int iMaxDepth = 0;
	private boolean bEnableDepthCheck = false;

	private ColorMapping colorMapper;

	private TreePorter treePorter = new TreePorter();

	private boolean bRedrawDendrogram = true;

	private ClusterNodeSelectionListener clusterNodeMouseOverListener;
	private UpdateViewListener updateViewListener;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 * @param bRenderGeneTree
	 *            boolean to determine whether a gene(horizontal) or a experiment(vertical) dendrogram should
	 *            be rendered
	 */
	public GLDendrogram(final GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum,
		final boolean bRenderGeneTree) {
		super(glCanvas, sLabel, viewFrustum);

		viewType = EManagedObjectType.GL_DENDOGRAM;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new SelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		storageSelectionManager = new SelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		renderStyle = new DendrogramRenderStyle(this, viewFrustum);

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		this.bRenderGeneTree = bRenderGeneTree;

		if (bRenderGeneTree)
			fPosCut = 0.1f;
		else
			fPosCut = 2f;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		updateViewListener = new UpdateViewListener();
		updateViewListener.setHandler(this);
		eventPublisher.addListener(UpdateViewEvent.class, updateViewListener);

		clusterNodeMouseOverListener = new ClusterNodeSelectionListener();
		clusterNodeMouseOverListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class, clusterNodeMouseOverListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}
		if (clusterNodeMouseOverListener != null) {
			eventPublisher.removeListener(clusterNodeMouseOverListener);
			clusterNodeMouseOverListener = null;
		}
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
		final GLMouseListener glMouseListener, final IGLCanvasRemoteRendering remoteRenderingGLCanvas,
		GLInfoAreaManager infoAreaManager) {

		this.remoteRenderingGLView = remoteRenderingGLCanvas;

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
			// renderStyle.setDetailLevel(detailLevel);
		}
	}

	@Override
	public void displayLocal(GL gl) {
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
	public void displayRemote(GL gl) {
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
	public void display(GL gl) {
		processEvents();

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

		float fHeight = viewFrustum.getHeight();
		float fWidth = viewFrustum.getWidth();

		gl.glColor4f(1f, 0f, 0f, 0.4f);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_CUT_SELECTION, 1));
		if (bRenderGeneTree) {
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fPosCut, 0.0f, 0);
			gl.glVertex3f(fPosCut, fHeight, 0);
			gl.glVertex3f(fPosCut + 0.1f, fHeight, 0);
			gl.glVertex3f(fPosCut + 0.1f, 0.0f, 0);
			gl.glEnd();
		}
		else {
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, fPosCut, 0);
			gl.glVertex3f(fWidth, fPosCut, 0);
			gl.glVertex3f(fWidth, fPosCut + 0.1f, 0);
			gl.glVertex3f(0.0f, fPosCut + 0.1f, 0);
			gl.glEnd();
		}
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
		Texture tempTexture = null;

		if (bRenderGeneTree)
			tempTexture = textureManager.getIconTexture(gl, EIconTextures.DENDROGRAM_HORIZONTAL_SYMBOL);
		else
			tempTexture = textureManager.getIconTexture(gl, EIconTextures.DENDROGRAM_VERTICAL_SYMBOL);

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

		if (bRenderGeneTree)
			determinePosRecGenes(currentRootNode);
		// determinePosRecGenes(tree.getRoot());
		else
			determinePosRecExperiments(tree.getRoot());

	}

	/**
	 * Function calculates for each node in the dendrogram recursive the corresponding position inside the
	 * view frustum
	 * 
	 * @param currentNode
	 *            current node for calculation
	 * @return Vec3f position of the current node
	 */
	private Vec3f determinePosRecGenes(ClusterNode currentNode) {

		Vec3f pos = new Vec3f();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = (ClusterNode) alChilds.get(i);
				positions[i] = determinePosRecGenes(node);
			}

			float fXmin = Float.MAX_VALUE;
			float fYmax = Float.MIN_VALUE;
			float fYmin = Float.MAX_VALUE;

			for (Vec3f vec : positions) {
				fXmin = Math.min(fXmin, vec.x());
				fYmax = Math.max(fYmax, vec.y());
				fYmin = Math.min(fYmin, vec.y());
			}

			float fCoeff = currentNode.getCoefficient();

			pos.setX(fXmin - fLevelWidth * (1 + fCoeff));
			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(0f);

		}
		else {
			pos.setY(yPosInit);
			yPosInit -= fSampleHeight;
			pos.setX(xGlobalMax);
			pos.setZ(0f);
		}

		currentNode.setPos(pos);

		return pos;
	}

	private Vec3f determinePosRecExperiments(ClusterNode currentNode) {

		Vec3f pos = new Vec3f();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = (ClusterNode) alChilds.get(i);
				positions[i] = determinePosRecExperiments(node);
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

			float fCoeff = currentNode.getCoefficient();

			pos.setX(fXmin + (fXmax - fXmin) / 2);
			pos.setY(fYmax + fLevelHeight * (1 + fCoeff));
			pos.setZ(0f);

		}
		else {
			pos.setX(xPosInit);
			xPosInit += fSampleWidth;
			pos.setY(yGlobalMin);
			pos.setZ(0f);
		}

		currentNode.setPos(pos);

		return pos;
	}

	private void renderSelections(final GL gl, ClusterNode currentNode) {
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

		List<ClusterNode> listGraph = null;

		if (tree.hasChildren(currentNode)) {
			listGraph = tree.getChildren(currentNode);

			int iNrChildsNode = listGraph.size();

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = (ClusterNode) listGraph.get(i);
				renderSelections(gl, current);
			}
		}

	}

	/**
	 * Render a node of the dendrogram (recursive)
	 * 
	 * @param gl
	 * @param currentNode
	 */
	private void renderDendrogramGenes(final GL gl, ClusterNode currentNode) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);
		float fOpacity = 1;
		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

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
			boolean[] bCutOffActive = new boolean[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = (ClusterNode) listGraph.get(i);

				tempPositions[i] = new Vec3f();
				tempPositions[i].setX(current.getPos().x());
				tempPositions[i].setY(current.getPos().y());
				tempPositions[i].setZ(current.getPos().z());

				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				bCutOffActive[i] = false;

				if (bEnableDepthCheck) {
					if (current.getDepth() >= iMaxDepth)
						renderDendrogramGenes(gl, current);
					else
						bCutOffActive[i] = true;
				}
				else
					renderDendrogramGenes(gl, current);

			}

			fDiff = fTemp - xmin;

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DENDROGRAM_HORIZONTAL_SELECTION, currentNode.getClusterNr()));

			// vertical line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin - 0.1f, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin - 0.1f, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all childs with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin - 0.1f, tempPositions[i].y(), tempPositions[i].z());
				if (bCutOffActive[i])
					gl.glVertex3f(xGlobalMax, tempPositions[i].y(), tempPositions[i].z());
				else
					gl.glVertex3f(tempPositions[i].x() - 0.1f, tempPositions[i].y(), tempPositions[i].z());
				gl.glEnd();
			}

			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DENDROGRAM_HORIZONTAL_SELECTION, currentNode.getClusterNr()));

			// horizontal line visualizing leaf nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
			gl
				.glVertex3f(currentNode.getPos().x() - 0.1f, currentNode.getPos().y(), currentNode.getPos()
					.z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff - 0.1f, currentNode.getPos().y(), currentNode.getPos()
			.z());
		gl.glVertex3f(currentNode.getPos().x() - 0.1f, currentNode.getPos().y(), currentNode.getPos().z());
		gl.glEnd();

	}

	private void renderDendrogramExperiments(final GL gl, ClusterNode currentNode) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);
		float fOpacity = 1;
		gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);

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

			Vec3f[] tempPositions = new Vec3f[iNrChildsNode];
			boolean[] bCutOffActive = new boolean[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = (ClusterNode) listGraph.get(i);

				tempPositions[i] = new Vec3f();
				tempPositions[i].setX(current.getPos().x());
				tempPositions[i].setY(current.getPos().y());
				tempPositions[i].setZ(current.getPos().z());

				xmax = Math.max(xmax, current.getPos().x());
				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				bCutOffActive[i] = false;

				if (bEnableDepthCheck) {
					if (current.getDepth() >= iMaxDepth)
						renderDendrogramExperiments(gl, current);
					else
						bCutOffActive[i] = true;
				}
				else
					renderDendrogramExperiments(gl, current);
			}

			fDiff = fTemp - ymax;

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_VERTICAL_SELECTION,
				currentNode.getClusterNr()));

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymax - 0.0f, currentNode.getPos().z());
			gl.glVertex3f(xmax, ymax - 0.0f, currentNode.getPos().z());
			gl.glEnd();

			for (int i = 0; i < iNrChildsNode; i++) {

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(tempPositions[i].x(), ymax + 0.0f, tempPositions[i].z());
				if (bCutOffActive[i])
					gl.glVertex3f(tempPositions[i].x(), yGlobalMin, tempPositions[i].z());
				else
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y() + 0.0f, tempPositions[i].z());
				gl.glEnd();

			}
			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_VERTICAL_SELECTION,
				currentNode.getClusterNr()));

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
			gl
				.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y() - 0.1f, currentNode.getPos()
					.z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y() - fDiff, currentNode.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
		gl.glEnd();
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (tree == null) {

			iAlCutOffClusters.clear();

			if (bRenderGeneTree == true) {

				// try {
				// tree = treePorter.importTree("data/clustering/tree.xml");
				// currentRootNode = tree.getRoot();
				// }
				// catch (FileNotFoundException e) {
				// e.printStackTrace();
				// }
				// catch (JAXBException e) {
				// e.printStackTrace();
				// }

				if (set.getClusteredTreeGenes() != null) {
					tree = set.getClusteredTreeGenes();
					groupList = new GroupList(1);
					currentRootNode = tree.getRoot();
				}
				else
					renderSymbol(gl);
			}
			else {
				if (set.getClusteredTreeExps() != null) {
					tree = set.getClusteredTreeExps();
					groupList = new GroupList(1);
				}
				else
					renderSymbol(gl);
			}

		}

		if (tree != null) {
			if (bHasFrustumChanged || bRedrawDendrogram) {
				if (bRenderGeneTree) {
					xGlobalMax = viewFrustum.getWidth() - 0.2f;
					fSampleHeight = (viewFrustum.getHeight() - 0.7f) / tree.getRoot().getNrElements();
					fLevelWidth = (viewFrustum.getWidth() - 3f) / tree.getRoot().getDepth();
					yPosInit = viewFrustum.getHeight() - 0.4f;
				}
				else {
					yGlobalMin = 0.1f;
					fSampleWidth = (viewFrustum.getWidth() - 1f) / tree.getRoot().getNrElements();
					fLevelHeight = (viewFrustum.getHeight() - 2f) / tree.getRoot().getDepth();
					xPosInit = 0.4f;
				}
				determinePositions();
				bRedrawDendrogram = false;
				bHasFrustumChanged = false;
			}

			gl.glLineWidth(0.1f);

			if (bRenderGeneTree) {
				gl.glTranslatef(0.1f, 0, 0);
				renderDendrogramGenes(gl, currentRootNode);
				// renderDendrogramGenes(gl, tree.getRoot());
			}
			else {
				gl.glTranslatef(0, 0.1f, 0);
				renderDendrogramExperiments(gl, tree.getRoot());
			}

			renderSelections(gl, tree.getRoot());
			renderCut(gl);

			if (bRenderGeneTree)
				gl.glTranslatef(-0.1f, 0, 0);
			else
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

		float fWidth = viewFrustum.getWidth() - 0.3f;
		float fHeight = viewFrustum.getHeight() - 0.3f;

		if (bRenderGeneTree) {
			if ((fArTargetWorldCoordinates[0] - 0.1f) > 0.0f
				&& (fArTargetWorldCoordinates[0] - 0.1f) < fWidth)
				fPosCut = fArTargetWorldCoordinates[0] - 0.1f;
		}
		else {
			if ((fArTargetWorldCoordinates[1] - 0.1f) > 0.0f
				&& (fArTargetWorldCoordinates[1] - 0.1f) < fHeight)
				fPosCut = fArTargetWorldCoordinates[1] - 0.1f;
		}
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

		iMaxDepth = Integer.MAX_VALUE;
		determineSelectedNodesRec(tree.getRoot());

		iAlCutOffClusters.clear();
		getNumberOfClustersRec(tree.getRoot());
		buildNewGroupList();

	}

	/**
	 * Function which merges the clusters determined by the cut off value to group lists used for rendering
	 * the clusters assignments in hierarchical heat map.
	 */
	private void buildNewGroupList() {

		if (iAlCutOffClusters.size() < 2) {

			groupList = null;
			if (bRenderGeneTree) {
				useCase.getSet().setGroupListGenes(groupList);
			}
			else {
				useCase.getSet().setGroupListExperiments(groupList);
			}

			eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(EVAType.CONTENT));

			bRedrawDendrogram = true;
			bEnableDepthCheck = false;
			setDisplayListDirty();

			return;
		}

		groupList = new GroupList(iAlCutOffClusters.size());

		bEnableDepthCheck = true;

		int cnt = 0;
		for (Integer iter : iAlCutOffClusters) {
			Group temp = new Group(iter, false, 0, ESelectionType.NORMAL);
			groupList.append(temp);
			cnt++;
		}

		if (bRenderGeneTree) {
			useCase.getSet().setGroupListGenes(groupList);
		}
		else {
			useCase.getSet().setGroupListExperiments(groupList);
		}

		eventPublisher.triggerEvent(new ReplaceVirtualArrayEvent(EVAType.CONTENT));

	}

	/**
	 * Recursive function determines the sizes of clusters set by the cut off value
	 * 
	 * @param node
	 *            current node
	 */
	private void getNumberOfClustersRec(ClusterNode node) {

		if (node.getSelectionType() == ESelectionType.MOUSE_OVER) {
			if (tree.hasChildren(node)) {
				for (ClusterNode current : tree.getChildren(node)) {
					if (current.getSelectionType() == ESelectionType.NORMAL) {
						// System.out.println("nr elements: " + current.getNrElements());
						iAlCutOffClusters.add(current.getNrElements());
					}
					else
						getNumberOfClustersRec(current);
				}
			}
		}

	}

	/**
	 * Determines for each node in the dendrogram if the current node is selected by the "cut" or not
	 * 
	 * @param node
	 *            current node
	 */
	private void determineSelectedNodesRec(ClusterNode node) {

		if (bRenderGeneTree) {

			if (node.getPos().x() < fPosCut) {
				node.setSelectionType(ESelectionType.MOUSE_OVER);
				if (node.getDepth() < iMaxDepth) {
					iMaxDepth = node.getDepth();
				}
			}
			else {
				node.setSelectionType(ESelectionType.NORMAL);
				// remove eventually selections of nodes
				resetAllTreeSelectionsRec(node);
				return;
			}
		}
		else {
			if (node.getPos().y() > fPosCut) {
				node.setSelectionType(ESelectionType.MOUSE_OVER);
				if (node.getDepth() < iMaxDepth) {
					iMaxDepth = node.getDepth();
				}
			}
			else {
				node.setSelectionType(ESelectionType.NORMAL);
				// remove eventually selections of nodes
				resetAllTreeSelectionsRec(node);
				return;
			}
		}

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

			case DENDROGRAM_HORIZONTAL_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						if (contentSelectionManager.checkStatus(iExternalID))
							bupdateSelectionManager = true;
						if (storageSelectionManager.checkStatus(iExternalID))
							bupdateSelectionManager = true;

						bTriggerClusterNodeEvent = true;

						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						// if (contentSelectionManager.checkStatus(iExternalID) == false)
						bTriggerClusterNodeEvent = true;
						// if (tree.getNodeByNumber(iExternalID) != null)
						// System.out.println(tree.getNodeByNumber(iExternalID).getNodeName());
						break;
				}

				if (bupdateSelectionManager) {

					ISelectionDelta selectionDelta = null;
					SelectionManager selectionManager = null;

					selectionManager = contentSelectionManager;

					selectionManager.clearSelection(eSelectionType);
					selectionManager.addToType(eSelectionType, iExternalID);
					selectionDelta = selectionManager.getDelta();

					handleConnectedElementRep(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta((SelectionDelta) selectionDelta);
					event.setInfo(getShortInfo());
					eventPublisher.triggerEvent(event);

				}
				if (bTriggerClusterNodeEvent) {
					if (tree.getNodeByNumber(iExternalID) != null) {
						ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
						event.setClusterNumber(iExternalID);
						event.setSelectionType(eSelectionType);
						eventPublisher.triggerEvent(event);
					}
				}
				break;

			case DENDROGRAM_VERTICAL_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						if (contentSelectionManager.checkStatus(iExternalID))
							bupdateSelectionManager = true;
						if (storageSelectionManager.checkStatus(iExternalID))
							bupdateSelectionManager = true;

						bTriggerClusterNodeEvent = true;

						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						// if (contentSelectionManager.checkStatus(iExternalID) == false)
						bTriggerClusterNodeEvent = true;
						// if (tree.getNodeByNumber(iExternalID) != null)
						// System.out.println(tree.getNodeByNumber(iExternalID).getNodeName());
						break;
				}

				if (bupdateSelectionManager) {

					ISelectionDelta selectionDelta = null;
					SelectionManager selectionManager = null;

					selectionManager = storageSelectionManager;

					selectionManager.clearSelection(eSelectionType);
					selectionManager.addToType(eSelectionType, iExternalID);
					selectionDelta = selectionManager.getDelta();

					handleConnectedElementRep(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta((SelectionDelta) selectionDelta);
					event.setInfo(getShortInfo());
					eventPublisher.triggerEvent(event);

				}
				break;
		}
	}

	@Override
	public String getShortInfo() {
		return new String("Dendrogram view shortinfo()");
	}

	@Override
	public String getDetailedInfo() {
		return new String("Dendrogram view detailedInfo()");
	}

	@Override
	public void clearAllSelections() {
		if (bRenderGeneTree)
			fPosCut = 0.1f;
		else
			fPosCut = viewFrustum.getHeight() - 0.2f;

		resetAllTreeSelections();

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	// @Override
	// public void broadcastElements() {
	// }

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

		if (bRenderOnlyContext)
			contentVAType = EVAType.CONTENT_CONTEXT;
		else
			contentVAType = EVAType.CONTENT;

		contentVA = useCase.getVA(contentVAType);
		storageVA = useCase.getVA(storageVAType);

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);
	}

	/**
	 * Function called any time a update is triggered external
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(boolean scrollToSelection) {

		if (bRenderGeneTree == true) {
			if (tree != null) {
				int iIndex;

				resetAllTreeSelections();

				Set<ClusterNode> nodeSet = tree.getGraph().vertexSet();
				for (ClusterNode node : nodeSet) {
					node.setSelectionType(ESelectionType.NORMAL);
				}

				Set<Integer> setMouseOverElements =
					contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);
				for (Integer iSelectedID : setMouseOverElements) {

					iIndex = iSelectedID;// contentVA.indexOf(iSelectedID);
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.MOUSE_OVER);
				}

				Set<Integer> setSelectionElements =
					contentSelectionManager.getElements(ESelectionType.SELECTION);
				for (Integer iSelectedID : setSelectionElements) {

					iIndex = contentVA.indexOf(iSelectedID);
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.SELECTION);
				}

				setDisplayListDirty();
			}
		}
		else {
			if (tree != null) {

				int iIndex;

				resetAllTreeSelections();

				Set<ClusterNode> nodeSet = tree.getGraph().vertexSet();
				for (ClusterNode node : nodeSet) {
					node.setSelectionType(ESelectionType.NORMAL);
				}

				Set<Integer> setMouseOverElements =
					storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
				for (Integer iSelectedID : setMouseOverElements) {

					iIndex = iSelectedID;// storageVA.indexOf(iSelectedID);
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.MOUSE_OVER);
				}

				Set<Integer> setSelectionElements =
					storageSelectionManager.getElements(ESelectionType.SELECTION);
				for (Integer iSelectedID : setSelectionElements) {
					iIndex = storageVA.indexOf(iSelectedID);
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.SELECTION);
				}
				setDisplayListDirty();
			}
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
	public void resetView() {
		super.resetView();
		tree = null;
		bRedrawDendrogram = true;
	}

	private void resetAllTreeSelections() {
		if (tree != null)
			resetAllTreeSelectionsRec(tree.getRoot());
	}

	private void resetAllTreeSelectionsRec(ClusterNode currentNode) {

		currentNode.setSelectionType(ESelectionType.NORMAL);

		if (tree.hasChildren(currentNode)) {
			for (ClusterNode current : tree.getChildren(currentNode)) {
				resetAllTreeSelectionsRec(current);
			}
		}
	}

	@Override
	public void handleClusterNodeSelection(int clusterNr, ESelectionType selectionType) {
		// cluster mouse over events only used for gene trees
		if (tree != null && bRenderGeneTree) {
			resetAllTreeSelections();
			if (tree.getNodeByNumber(clusterNr) != null) {
				tree.getNodeByNumber(clusterNr).setSelectionType(selectionType);
				
				if(bIsRenderedRemote)
					 currentRootNode = tree.getNodeByNumber(clusterNr);
			}

			setDisplayListDirty();
		}
	}

	@Override
	public void handleUpdateView() {
		tree = null;
		bRedrawDendrogram = true;
		setDisplayListDirty();
	}
}
