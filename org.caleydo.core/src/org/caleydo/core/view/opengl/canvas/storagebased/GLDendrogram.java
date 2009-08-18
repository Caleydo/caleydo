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
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IVirtualArray;
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
import org.caleydo.core.serialize.ASerializedView;
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

	/**
	 * Used in case of we want to render only a sub-tree instead of the whole tree.
	 */
	private ClusterNode currentRootNode;

	// variables used to build a group list
	private ArrayList<ClusterNode> iAlClusterNodes = new ArrayList<ClusterNode>();
	private GroupList groupList = null;

	/**
	 * true for gene tree, false for experiment tree
	 */
	private boolean bRenderGeneTree;

	private boolean bIsDraggingActive = false;
	private float fPosCut = 0.0f;

	// variables needed for gene tree dendrogram
	private float yPosInit = 0;
	private float xGlobalMax = 0;
	private float fSampleHeight = 0;
	private float fLevelWidth = 0;

	// variables needed for experiment tree dendrogram
	private float xPosInit = 0;
	private float yGlobalMin = 0;
	private float fSampleWidth = 0;
	private float fLevelHeight = 0;

	private int iMaxDepth = 0;
	private boolean bEnableDepthCheck = false;

	private ColorMapping colorMapper;

	private boolean bRedrawDendrogram = true;

	private ClusterNodeSelectionListener clusterNodeMouseOverListener;
	private UpdateViewListener updateViewListener;

	private int iGLDisplayListCutOffValue = 0;

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

		fPosCut = 0f;
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
		iGLDisplayListCutOffValue = gl.glGenLists(1);
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
		iGLDisplayListCutOffValue = gl.glGenLists(1);
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

		// display list for cut off value
		gl.glCallList(iGLDisplayListCutOffValue);
	}

	/**
	 * Render the handles for the "cut of value"
	 * 
	 * @param gl
	 */
	private void renderCut(final GL gl) {

		float fHeight = viewFrustum.getHeight();
		float fWidth = viewFrustum.getWidth();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_CUT_SELECTION, 1));
		if (bRenderGeneTree) {
			gl.glColor4f(0f, 0f, 1f, 0.4f);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fPosCut, 0.0f, 0);
			gl.glVertex3f(fPosCut, fHeight, 0);
			gl.glVertex3f(fPosCut + 0.05f, fHeight, 0);
			gl.glVertex3f(fPosCut + 0.05f, 0.0f, 0);
			gl.glEnd();

			gl.glColor4f(0f, 0f, 1f, 1f);
			gl.glBegin(GL.GL_TRIANGLE_STRIP);
			gl.glVertex3f(fPosCut - 0.2f, -0.1f, 0);
			gl.glVertex3f(fPosCut + 0.0f, -0.0f, 0);
			gl.glVertex3f(fPosCut + 0.0f, -0.2f, 0);
			gl.glVertex3f(fPosCut + 0.05f, -0.0f, 0);
			gl.glVertex3f(fPosCut + 0.05f, -0.2f, 0);
			gl.glVertex3f(fPosCut + 0.25f, -0.1f, 0);
			gl.glEnd();

			// gl.glBegin(GL.GL_TRIANGLE_STRIP);
			// gl.glVertex3f(fPosCut - 0.2f, fHeight + 0.1f, 0);
			// gl.glVertex3f(fPosCut + 0.0f, fHeight + 0.0f, 0);
			// gl.glVertex3f(fPosCut + 0.0f, fHeight + 0.2f, 0);
			// gl.glVertex3f(fPosCut + 0.05f, fHeight + 0.0f, 0);
			// gl.glVertex3f(fPosCut + 0.05f, fHeight + 0.2f, 0);
			// gl.glVertex3f(fPosCut + 0.25f, fHeight + 0.1f, 0);
			// gl.glEnd();
		}
		else {
			gl.glColor4f(0f, 0f, 1f, 0.4f);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, fPosCut, 0);
			gl.glVertex3f(fWidth, fPosCut, 0);
			gl.glVertex3f(fWidth, fPosCut + 0.05f, 0);
			gl.glVertex3f(0.0f, fPosCut + 0.05f, 0);
			gl.glEnd();

			gl.glColor4f(0f, 0f, 1f, 1f);
			gl.glBegin(GL.GL_TRIANGLE_STRIP);
			gl.glVertex3f(-0.1f, fPosCut - 0.2f, 0);
			gl.glVertex3f(-0.2f, fPosCut + 0.0f, 0);
			gl.glVertex3f(-0.0f, fPosCut + 0.0f, 0);
			gl.glVertex3f(-0.2f, fPosCut + 0.05f, 0);
			gl.glVertex3f(-0.0f, fPosCut + 0.05f, 0);
			gl.glVertex3f(-0.1f, fPosCut + 0.25f, 0);
			gl.glEnd();

			// gl.glBegin(GL.GL_TRIANGLE_STRIP);
			// gl.glVertex3f(fWidth + 0.1f, fPosCut - 0.2f, 0);
			// gl.glVertex3f(fWidth + 0.2f, fPosCut + 0.0f, 0);
			// gl.glVertex3f(fWidth - 0.0f, fPosCut + 0.0f, 0);
			// gl.glVertex3f(fWidth + 0.2f, fPosCut + 0.05f, 0);
			// gl.glVertex3f(fWidth - 0.0f, fPosCut + 0.05f, 0);
			// gl.glVertex3f(fWidth + 0.1f, fPosCut + 0.25f, 0);
			// gl.glEnd();
		}
		gl.glPopName();

	}

	/**
	 * In case of no tree is available render the symbol of the view instead of the view
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

	public void setFromTo(int from, int to) {

		if (tree == null)
			return;

		resetAllTreeSelections();
		renderNodesFromToRec(currentRootNode, from, to, false);
		setDisplayListDirty();
	}

	/**
	 * This function calls a recursive function which is responsible for the calculation of the position
	 * inside the view frustum of the nodes in the dendrogram
	 */
	private void determinePositions() {

		if (bRenderGeneTree) {
			determinePosRecGenes(currentRootNode);
			// determinePosRecGenes(tree.getRoot());
		}
		else
			determinePosRecExperiments(tree.getRoot());

	}

	private boolean renderNodesFromToRec(ClusterNode currentNode, int from, int to, boolean inBlock) {

		boolean boolVar = inBlock;

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = (ClusterNode) alChilds.get(i);
				if (renderNodesFromToRec(node, from, to, boolVar)) {
					node.setSelectionType(ESelectionType.SELECTION);
					boolVar = true;
				}
				else {
					node.setSelectionType(ESelectionType.NORMAL);
					boolVar = false;
				}
			}
		}
		else {
			if (currentNode.getClusterNr() == from) {
				currentNode.setSelectionType(ESelectionType.SELECTION);
				return true;
			}
			if (currentNode.getClusterNr() == to) {
				currentNode.setSelectionType(ESelectionType.NORMAL);
				return false;
			}
		}

		return boolVar;
	}

	/**
	 * Function calculates for each node (gene or entity) in the dendrogram recursive the corresponding
	 * position inside the view frustum
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

			// float fCoeff = currentNode.getCoefficient();

			pos.setX(fXmin - fLevelWidth);// * (1 + fCoeff));
			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(0f);

		}
		else {
			pos.setY(yPosInit);
			yPosInit -= fSampleHeight;
			pos.setX(xGlobalMax);// - currentNode.getCoefficient());
			pos.setZ(0f);
		}

		currentNode.setPos(pos);

		return pos;
	}

	/**
	 * Function calculates for each node (experiment) in the dendrogram recursive the corresponding position
	 * inside the view frustum
	 * 
	 * @param currentNode
	 *            current node for calculation
	 * @return Vec3f position of the current node
	 */
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

			// float fCoeff = currentNode.getCoefficient();

			pos.setX(fXmin + (fXmax - fXmin) / 2);
			pos.setY(fYmax + fLevelHeight);// * (1 + fCoeff));
			pos.setZ(0f);

		}
		else {
			pos.setX(xPosInit);
			xPosInit += fSampleWidth;
			pos.setY(yGlobalMin + fLevelHeight);// currentNode.getCoefficient());
			pos.setZ(0f);
		}

		currentNode.setPos(pos);

		return pos;
	}

	/**
	 * Recursive function which renders selection markers for all nodes in the tree.
	 * 
	 * @param gl
	 * @param currentNode
	 */
	private void renderSelections(final GL gl, ClusterNode currentNode) {

		if (currentNode.getSelectionType() == ESelectionType.MOUSE_OVER) {
			gl.glColor4fv(MOUSE_OVER_COLOR, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() - 0.025f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() - 0.025f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() + 0.025f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() + 0.025f, currentNode
				.getPos().z());
			gl.glEnd();

		}
		else if (currentNode.getSelectionType() == ESelectionType.SELECTION) {
			gl.glColor4fv(SELECTED_COLOR, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() - 0.025f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() - 0.025f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() + 0.025f, currentNode
				.getPos().z());
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() + 0.025f, currentNode
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
	 * Render a node (gene or entity) of the dendrogram (recursive)
	 * 
	 * @param gl
	 * @param currentNode
	 * @param fOpacity
	 *            Opacity value of the current node. In case of determine clusters with the cut of value.
	 */
	private void renderDendrogramGenes(final GL gl, ClusterNode currentNode, float fOpacity) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);
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
					if (current.getSelectionType() == ESelectionType.DESELECTED) {
						renderDendrogramGenes(gl, current, 0.3f);
						gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);
					}
					else {
						renderDendrogramGenes(gl, current, 1);
						// bCutOffActive[i] = true;
					}
				}
				else
					renderDendrogramGenes(gl, current, 1);

			}

			fDiff = fTemp - xmin;

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DENDROGRAM_HORIZONTAL_SELECTION, currentNode.getClusterNr()));

			// vertical line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin - 0.1f, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin - 0.1f, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all children with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin - 0.1f, tempPositions[i].y(), tempPositions[i].z());
				// if (bCutOffActive[i])
				// gl.glVertex3f(xGlobalMax, tempPositions[i].y(), tempPositions[i].z());
				// else
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
			gl
				.glVertex3f(currentNode.getPos().x() - 0.1f, currentNode.getPos().y(), currentNode.getPos()
					.z());
			gl.glVertex3f(xGlobalMax, currentNode.getPos().y(), currentNode.getPos().z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff - 0.1f, currentNode.getPos().y(), currentNode.getPos()
			.z());
		gl.glVertex3f(currentNode.getPos().x() - 0.1f, currentNode.getPos().y(), currentNode.getPos().z());
		gl.glEnd();

	}

	/**
	 * Render a node (experiment) of the dendrogram (recursive)
	 * 
	 * @param gl
	 * @param currentNode
	 * @param fOpacity
	 *            Opacity value of the current node. In case of determine clusters with the cut of value.
	 */
	private void renderDendrogramExperiments(final GL gl, ClusterNode currentNode, float fOpacity) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);
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
					if (current.getSelectionType() == ESelectionType.DESELECTED) {
						renderDendrogramExperiments(gl, current, 0.3f);
						gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);
					}
					else {
						renderDendrogramExperiments(gl, current, 1);
						// bCutOffActive[i] = true;
					}
				}
				else
					renderDendrogramExperiments(gl, current, 1);
			}

			fDiff = fTemp - ymax;

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_VERTICAL_SELECTION,
				currentNode.getClusterNr()));

			// horizontal line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glVertex3f(xmax, ymax, currentNode.getPos().z());
			gl.glEnd();

			for (int i = 0; i < iNrChildsNode; i++) {

				// vertical lines connecting all children with their parent
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(tempPositions[i].x(), ymax + 0.0f, tempPositions[i].z());
				// if (bCutOffActive[i])
				// gl.glVertex3f(tempPositions[i].x(), yGlobalMin, tempPositions[i].z());
				// else
				gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y() + 0.0f, tempPositions[i].z());
				gl.glEnd();

			}
			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_VERTICAL_SELECTION,
				currentNode.getClusterNr()));

			// vertical line visualizing leaf nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
			gl.glVertex3f(currentNode.getPos().x(), yGlobalMin, currentNode.getPos().z());
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

		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), 0);
		// gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), 0);
		// gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), 0);
		// gl.glEnd();

		if (tree == null) {

			iAlClusterNodes.clear();

			if (bRenderGeneTree == true) {
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
			if (bIsDisplayListDirtyRemote || bHasFrustumChanged || bRedrawDendrogram) {
				if (bRenderGeneTree) {
					xGlobalMax = viewFrustum.getWidth();
					fSampleHeight = viewFrustum.getHeight() / tree.getRoot().getNrElements();
					fLevelWidth = (viewFrustum.getWidth() - 0.1f) / tree.getRoot().getDepth();
					yPosInit = viewFrustum.getHeight();
				}
				else {
					yGlobalMin = 0.0f;
					fSampleWidth = viewFrustum.getWidth() / tree.getRoot().getNrElements();
					fLevelHeight = (viewFrustum.getHeight() - 0.1f) / tree.getRoot().getDepth();
					xPosInit = 0.0f;
				}
				determinePositions();
				bIsDisplayListDirtyRemote = false;
				bRedrawDendrogram = false;
				bHasFrustumChanged = false;
			}

			gl.glLineWidth(0.1f);

			if (bRenderGeneTree) {
				gl.glTranslatef(0, -fSampleHeight / 2, 0);
				renderDendrogramGenes(gl, currentRootNode, 1);
				// renderDendrogramGenes(gl, tree.getRoot());
			}
			else {
				gl.glTranslatef(fSampleWidth / 2, 0, 0);
				renderDendrogramExperiments(gl, tree.getRoot(), 1);
			}

			renderSelections(gl, tree.getRoot());

			if (bRenderGeneTree)

				gl.glTranslatef(0, +fSampleHeight / 2, 0);
			else
				gl.glTranslatef(-fSampleWidth / 2, 0, 0);
		}
		gl.glEndList();

		// display list for cut off value
		gl.glNewList(iGLDisplayListCutOffValue, GL.GL_COMPILE);

		if (tree != null)
			renderCut(gl);

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
		float fHeight = viewFrustum.getHeight();

		if (bRenderGeneTree) {
			if (fArTargetWorldCoordinates[0] > -0.1f && fArTargetWorldCoordinates[0] < fWidth)
				fPosCut = fArTargetWorldCoordinates[0];
		}
		else {
			if (fArTargetWorldCoordinates[1] > -0.1f && fArTargetWorldCoordinates[1] < fHeight)
				fPosCut = fArTargetWorldCoordinates[1];
		}
		setDisplayListDirty();

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActive = false;

			determineSelectedNodes();

		}
	}

	/**
	 * This function calls a recursive function which is responsible for setting nodes in the dendrogram
	 * deselected depending on the current position of the "cut of value"
	 */
	private void determineSelectedNodes() {

		iMaxDepth = Integer.MAX_VALUE;
		determineSelectedNodesRec(tree.getRoot());

		iAlClusterNodes.clear();
		getNumberOfClustersRec(tree.getRoot());
		buildNewGroupList();

	}

	/**
	 * Function which merges the clusters determined by the cut off value to group lists used for rendering
	 * the clusters assignments in {@link GLHierarchicalHeatMap}.
	 */
	private void buildNewGroupList() {

		if (iAlClusterNodes.size() < 2) {

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

		groupList = new GroupList(iAlClusterNodes.size());

		bEnableDepthCheck = true;

		int cnt = 0;
		int iExample = 0;

		IVirtualArray currentVA = null;

		if (bRenderGeneTree) {
			currentVA = contentVA;
		}
		else {
			currentVA = storageVA;
		}

		for (ClusterNode iter : iAlClusterNodes) {
			// Group temp = new Group(iter.getNrElements(), false, currentVA.get(iExample),
			// iter.getRepresentativeElement(), ESelectionType.NORMAL, iter);
			Group temp =
				new Group(iter.getNrElements(), false, currentVA.get(iExample), ESelectionType.NORMAL, iter);
			groupList.append(temp);
			cnt++;
			iExample += iter.getNrElements();
		}

		if (bRenderGeneTree) {
			contentVA.setGroupList(groupList);
			useCase.replaceVirtualArray(EVAType.CONTENT, contentVA);
		}
		else {
			storageVA.setGroupList(groupList);
			useCase.replaceVirtualArray(EVAType.STORAGE, storageVA);
		}
	}

	/**
	 * Recursive function determines the sizes of clusters set by the cut off value
	 * 
	 * @param node
	 *            current node
	 */
	private void getNumberOfClustersRec(ClusterNode node) {

		if (node.getSelectionType() == ESelectionType.NORMAL) {
			if (tree.hasChildren(node)) {
				for (ClusterNode current : tree.getChildren(node)) {
					if (current.getSelectionType() == ESelectionType.DESELECTED) {
						// System.out.println("nr elements: " + current.getNrElements());
						iAlClusterNodes.add(current);
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
				node.setSelectionType(ESelectionType.NORMAL);
				if (node.getDepth() < iMaxDepth) {
					iMaxDepth = node.getDepth();
				}
			}
			else {
				node.setSelectionType(ESelectionType.DESELECTED);
			}
		}
		else {
			if (node.getPos().y() > fPosCut) {
				node.setSelectionType(ESelectionType.NORMAL);
				if (node.getDepth() < iMaxDepth) {
					iMaxDepth = node.getDepth();
				}
			}
			else {
				node.setSelectionType(ESelectionType.DESELECTED);
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

						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						// if (contentSelectionManager.checkStatus(iExternalID))
						// bupdateSelectionManager = true;
						// if (storageSelectionManager.checkStatus(iExternalID))
						// bupdateSelectionManager = true;

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

				if (tree.getNodeByNumber(iExternalID) != null) {
					ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
					event.setClusterNumber(iExternalID);
					event.setSelectionType(eSelectionType);
					eventPublisher.triggerEvent(event);
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

						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						// if (contentSelectionManager.checkStatus(iExternalID))
						// bupdateSelectionManager = true;
						// if (storageSelectionManager.checkStatus(iExternalID))
						// bupdateSelectionManager = true;

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

					setDisplayListDirty();

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

		fPosCut = 0;
		iMaxDepth = Integer.MAX_VALUE;
		iAlClusterNodes.clear();
		buildNewGroupList();
		resetAllTreeSelections();
		tree = null;
		bRedrawDendrogram = true;
		bEnableDepthCheck = false;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		ASerializedView serializedForm;
		if (bRenderGeneTree) {
			SerializedDendogramHorizontalView horizontal = new SerializedDendogramHorizontalView();
			serializedForm = horizontal;
		}
		else {
			SerializedDendogramVerticalView vertical = new SerializedDendogramVerticalView();
			serializedForm = vertical;
		}
		serializedForm.setViewID(this.getID());
		return serializedForm;
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

					iIndex = iSelectedID;// contentVA.indexOf(iSelectedID);
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
					iIndex = iSelectedID;// storageVA.indexOf(iSelectedID);
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
	}

	/**
	 * This function calls a recursive function which is responsible for setting all nodes in the dendrogram
	 * to {@link EselectionType.NORMAL}
	 */
	private void resetAllTreeSelections() {
		if (tree != null)
			resetAllTreeSelectionsRec(tree.getRoot());
	}

	/**
	 * Recursive function resets all selections in the tree
	 * 
	 * @param node
	 *            current node
	 */
	private void resetAllTreeSelectionsRec(ClusterNode currentNode) {

		currentNode.setSelectionType(ESelectionType.NORMAL);

		if (tree.hasChildren(currentNode)) {
			for (ClusterNode current : tree.getChildren(currentNode)) {
				resetAllTreeSelectionsRec(current);
			}
		}
	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {

		int clusterNr = event.getClusterNumber();
		ESelectionType selectionType = event.getSelectionType();

		// cluster mouse over events only used for gene trees
		if (tree != null && bRenderGeneTree) {
			resetAllTreeSelections();
			if (tree.getNodeByNumber(clusterNr) != null) {
				tree.getNodeByNumber(clusterNr).setSelectionType(selectionType);

				// if (bIsRenderedRemote)
				// currentRootNode = tree.getNodeByNumber(clusterNr);
			}

			setDisplayListDirty();
		}
	}

	@Override
	public void handleUpdateView() {
		tree = null;
		fPosCut = 0f;
		resetAllTreeSelections();
		bRedrawDendrogram = true;
		setDisplayListDirty();
	}
}
