package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.DendrogramRenderStyle.CUT_OFF_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.DendrogramRenderStyle.DENDROGRAM_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.DendrogramRenderStyle.SELECTION_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.HeatMapRenderStyle.BUTTON_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.manager.event.view.storagebased.NewGroupInfoEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClusterNodeSelectionListener;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.UpdateViewListener;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ExperimentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GeneContextMenuItemContainer;
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

	// variables used to build a group list
	private ArrayList<ClusterNode> iAlClusterNodes = new ArrayList<ClusterNode>();
	private GroupList groupList = null;

	/**
	 * true for gene tree, false for experiment tree
	 */
	private boolean bRenderGeneTree;

	private boolean bRenderUntilCut = false;

	private boolean bIsDraggingActive = false;
	private float fPosCut = 0.0f;

	// sub tree stuff
	private float yPosInitSubTree = 0;
	private float xGlobalMaxSubTree = 0;
	private float fSampleHeightSubTree = 0;
	private float fLevelWidthSubTree = 0;
	private int iMaxDepthSubTree = 0;

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

	private boolean bUseBlackColoring = false;

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

		renderStyle = new DendrogramRenderStyle(this, viewFrustum);

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		this.bRenderGeneTree = bRenderGeneTree;

		fPosCut = 0f;
		// if (bRenderGeneTree)
		// fPosCut = viewFrustum.getWidth() / 5f;
		// else
		// fPosCut = viewFrustum.getHeight() / 5f;
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
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

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

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	/**
	 * Returns the position of the cut. This function is used in HHM to determine how many area the dendrogram
	 * up to the cut requires.
	 * 
	 * @return position of cut
	 */
	public float getPositionOfCut() {
		return fPosCut;
	}

	/**
	 * Function sets an initial value for the position of the cut off value. Used in HHM when rendering a
	 * dendrogram the first time.
	 * 
	 * @return True in case of tree is available and clusters are determined, false in case of no tree
	 *         available.
	 */
	public boolean setInitialPositionOfCut() {
		if (bRenderGeneTree)
			fPosCut = viewFrustum.getWidth() / 4f;
		else
			fPosCut = viewFrustum.getHeight() - viewFrustum.getHeight() / 4f;

		if (tree != null) {
			determineSelectedNodes();
			return true;
		}
		return false;
	}

	/**
	 * Function is responsible for activating/deactivating "render up to cut" mode.
	 * 
	 * @param bRenderUntilCut
	 */
	public void setRenderUntilCut(boolean bRenderUntilCut) {
		this.bRenderUntilCut = bRenderUntilCut;
	}

	/**
	 * Render the handles for the "cut off value"
	 * 
	 * @param gl
	 */
	private void renderCut(final GL gl) {

		float fHeight = viewFrustum.getHeight();
		float fWidth = viewFrustum.getWidth();
		float fWidthCutOf = renderStyle.getWidthCutOff();

		gl.glColor4f(1, 1, 1, 1);

		if (bRenderGeneTree) {
			gl.glTranslatef(+fLevelWidth, 0, 0);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

			Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.SLIDER_ENDING);
			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, fHeight + 0.1f, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, fHeight + 0.1f, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glEnd();

			tempTexture.disable();
			tempTexture = textureManager.getIconTexture(gl, EIconTextures.SLIDER_MIDDLE);
			tempTexture.enable();
			tempTexture.bind();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, 0, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, 0, CUT_OFF_Z);
			gl.glEnd();
			tempTexture.disable();

			float fSizeDendrogramArrow = renderStyle.getSizeDendrogramArrow();

			Texture textureArrow = textureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_ARROW);
			textureArrow.enable();
			textureArrow.bind();

			TextureCoords texCoordsArrow = textureArrow.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_CUT_SELECTION, 1));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fPosCut, -fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fPosCut + fSizeDendrogramArrow, -fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fPosCut + fSizeDendrogramArrow, 0, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fPosCut, 0, BUTTON_Z);
			gl.glEnd();

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fPosCut, -fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fPosCut - fSizeDendrogramArrow, -fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fPosCut - fSizeDendrogramArrow, 0, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fPosCut, 0, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();

			textureArrow.disable();

			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

			gl.glTranslatef(-fLevelWidth, 0, 0);
		}
		else {

			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			if (fPosCut > fLevelHeight)
				gl.glTranslatef(0, -fLevelHeight, 0);

			Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.SLIDER_ENDING);
			tempTexture.enable();
			tempTexture.bind();
			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-0.1f, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(+0.0f, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(+0.0f, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(-0.1f, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glEnd();

			tempTexture.disable();
			tempTexture = textureManager.getIconTexture(gl, EIconTextures.SLIDER_MIDDLE);
			tempTexture.enable();
			tempTexture.bind();
			texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fWidth, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fWidth, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glEnd();
			tempTexture.disable();

			float fSizeDendrogramArrow = renderStyle.getSizeDendrogramArrow();

			Texture textureArrow = textureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_ARROW);
			textureArrow.enable();
			textureArrow.bind();

			TextureCoords texCoordsArrow = textureArrow.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_CUT_SELECTION, 1));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut + fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fWidth, fPosCut + fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth, fPosCut, BUTTON_Z);
			gl.glEnd();

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut - fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fWidth, fPosCut - fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth, fPosCut, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();

			textureArrow.disable();

			if (fPosCut > fLevelHeight)
				gl.glTranslatef(0, +fLevelHeight, 0);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		}

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

	/**
	 * This function calls a recursive function which is responsible for the calculation of the position
	 * inside the view frustum of the nodes in the dendrogram
	 */
	private void determinePositions() {

		if (bRenderGeneTree)
			determinePosRecGenes(tree.getRoot());
		else
			determinePosRecExperiments(tree.getRoot());

	}

	/**
	 * Functions renders a sub part of the dendrogram determined by two indexes (first and last element).
	 * 
	 * @param gl
	 * @param fromIndex
	 *            index of the first element in sub dendrogram
	 * @param toIndex
	 *            index (+1) of the last element in sub dendrogram
	 * @param iNrLeafs
	 *            number of leaf nodes in sub dendrogram
	 * @param fWidth
	 *            width of area for sub dendrogram
	 * @param fHeight
	 *            height of area for sub dendrogram
	 */
	public void renderSubTreeFromIndexToIndex(GL gl, int fromIndex, int toIndex, int iNrLeafs, float fWidth,
		float fHeight) {

		if (tree == null)
			return;

		determineNodesToRender(tree.getRoot(), fromIndex, toIndex, false);
		removeWronglySelectedNodes(tree.getRoot());
		iMaxDepthSubTree = 0;
		yPosInitSubTree = fHeight;
		xGlobalMaxSubTree = fWidth;

		determineMaxDepthSubTree(tree.getRoot());

		fLevelWidthSubTree = fWidth / (iMaxDepthSubTree + 1);
		fSampleHeightSubTree = fHeight / iNrLeafs;

		determinePosRecSubTree(tree.getRoot());

		gl.glTranslatef(0, -fSampleHeightSubTree / 2, 0);
		gl.glLineWidth(renderStyle.getDendrogramLineWidth());
		renderSubTreeRec(gl, tree.getRoot());
		gl.glTranslatef(0, +fSampleHeightSubTree / 2, 0);
	}

	/**
	 * Recursive function responsible for determine sub dendrogram.
	 * 
	 * @param currentNode
	 * @param from
	 *            index of the first element in sub dendrogram
	 * @param to
	 *            index (+1) of the last element in sub dendrogram
	 * @param inBlock
	 *            boolean which identifies nodes in sub dendrogram
	 * @return true if node is part of sub dendrogram, false otherwise
	 */
	private boolean determineNodesToRender(ClusterNode currentNode, int from, int to, boolean inBlock) {

		boolean boolVar = inBlock;

		if (tree.hasChildren(currentNode)) {

			for (ClusterNode current : tree.getChildren(currentNode)) {
				if (determineNodesToRender(current, from, to, boolVar)) {
					current.setIsPartOfSubTree(true);
					boolVar = true;
				}
				else {
					current.setIsPartOfSubTree(false);
					boolVar = false;
				}
			}
		}
		else {
			if (currentNode.getClusterNr() == from)
				return true;

			if (currentNode.getClusterNr() == to)
				return false;
		}
		return boolVar;
	}

	/**
	 * Helper function which removes wrongly added nodes from sub dendrogram. Only useful in combination with
	 * determineNodesToRender()
	 * 
	 * @param currentNode
	 */
	private void removeWronglySelectedNodes(ClusterNode currentNode) {

		if (tree.hasChildren(currentNode)) {

			boolean bAllChildsPartOfSubTree = true;

			for (ClusterNode current : tree.getChildren(currentNode)) {
				removeWronglySelectedNodes(current);
			}

			for (ClusterNode current : tree.getChildren(currentNode)) {
				if (current.isPartOfSubTree() == false) {
					bAllChildsPartOfSubTree = false;
				}
			}

			if (bAllChildsPartOfSubTree == false)
				currentNode.setIsPartOfSubTree(false);
		}
	}

	/**
	 * Helper function which determines the maximum hierarchy depth of the sub denrogram.
	 * 
	 * @param currentNode
	 */
	private void determineMaxDepthSubTree(ClusterNode currentNode) {

		int temp = 0;
		if (currentNode.isPartOfSubTree() == true) {
			temp = currentNode.getDepth();
			iMaxDepthSubTree = Math.max(iMaxDepthSubTree, temp);
		}

		if (tree.hasChildren(currentNode)) {
			for (ClusterNode current : tree.getChildren(currentNode)) {
				determineMaxDepthSubTree(current);
			}
		}
	}

	/**
	 * Function calculates for each node (gene or entity) in the sub dendrogram recursive the corresponding
	 * position inside the view frustum
	 * 
	 * @param currentNode
	 * @return position of node in sub dendrogram
	 */
	private Vec3f determinePosRecSubTree(ClusterNode currentNode) {

		Vec3f pos = new Vec3f();

		if (tree.hasChildren(currentNode)) {
			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);
			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {
				ClusterNode node = alChilds.get(i);
				positions[i] = determinePosRecSubTree(node);
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

			pos.setX(fXmin - fLevelWidthSubTree);// * (1 - fCoeff));
			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(DENDROGRAM_Z);

		}
		else {
			if (currentNode.isPartOfSubTree()) {

				// float fCoeff = currentNode.getCoefficient();

				pos.setY(yPosInitSubTree);
				yPosInitSubTree -= fSampleHeightSubTree;
				pos.setX(xGlobalMaxSubTree - fLevelWidthSubTree);// * (1 - fCoeff));
				pos.setZ(DENDROGRAM_Z);
			}
		}

		currentNode.setPosSubTree(pos);

		return pos;
	}

	/**
	 * Renders the sub dendrogram recursive
	 * 
	 * @param gl
	 * @param currentNode
	 */
	private void renderSubTreeRec(GL gl, ClusterNode currentNode) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		if (bUseBlackColoring)
			gl.glColor4f(0, 0, 0, 1);
		else
			gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], 1);

		if (currentNode.isPartOfSubTree())
			currentNode.getPosSubTree().x();

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
				tempPositions[i].setX(current.getPosSubTree().x());
				tempPositions[i].setY(current.getPosSubTree().y());
				tempPositions[i].setZ(current.getPosSubTree().z());

				xmin = Math.min(xmin, current.getPosSubTree().x());
				ymax = Math.max(ymax, current.getPosSubTree().y());
				ymin = Math.min(ymin, current.getPosSubTree().y());

				renderSubTreeRec(gl, current);
			}

			if (currentNode.isPartOfSubTree()) {

				gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode.getClusterNr()));

				// vertical line connecting all child nodes
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin, ymin, currentNode.getPosSubTree().z());
				gl.glVertex3f(xmin, ymax, currentNode.getPosSubTree().z());
				gl.glEnd();

				// horizontal lines connecting all children with their parent
				for (int i = 0; i < iNrChildsNode; i++) {
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(xmin, tempPositions[i].y(), tempPositions[i].z());
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(), tempPositions[i].z());
					gl.glEnd();
				}
				gl.glPopName();
			}

		}
		else {
			// horizontal line visualizing leaf nodes
			if (currentNode.isPartOfSubTree()) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(currentNode.getPosSubTree().x(), currentNode.getPosSubTree().y(), currentNode
					.getPosSubTree().z());
				gl.glVertex3f(xGlobalMaxSubTree, currentNode.getPosSubTree().y(), currentNode.getPosSubTree()
					.z());
				gl.glEnd();
			}
		}

		if (currentNode.isPartOfSubTree()) {
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_GENE_LEAF_SELECTION,
				currentNode.getClusterNr()));
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPosSubTree().x() + fLevelWidthSubTree, currentNode.getPosSubTree()
				.y(), currentNode.getPosSubTree().z());
			gl.glVertex3f(currentNode.getPosSubTree().x(), currentNode.getPosSubTree().y(), currentNode
				.getPosSubTree().z());
			gl.glEnd();
			gl.glPopName();
		}
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

				ClusterNode node = alChilds.get(i);
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

			pos.setX(fXmin - fLevelWidth);// * (1 - fCoeff));
			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(DENDROGRAM_Z);

		}
		else {
			// float fCoeff = currentNode.getCoefficient();

			pos.setY(yPosInit);
			yPosInit -= fSampleHeight;
			pos.setX(xGlobalMax - fLevelWidth);// * (1 - fCoeff));
			pos.setZ(DENDROGRAM_Z);
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

				ClusterNode node = alChilds.get(i);
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
			pos.setY(fYmax + fLevelHeight);// * (1 - fCoeff));
			pos.setZ(DENDROGRAM_Z);

		}
		else {

			// float fCoeff = currentNode.getCoefficient();

			pos.setX(xPosInit);
			xPosInit += fSampleWidth;
			pos.setY(yGlobalMin + fLevelHeight);// * (1 - fCoeff));
			pos.setZ(DENDROGRAM_Z);
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
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glEnd();

		}
		else if (currentNode.getSelectionType() == ESelectionType.SELECTION) {
			gl.glColor4fv(SELECTED_COLOR, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f, currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f, currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glEnd();

		}

		if (tree.hasChildren(currentNode)) {
			for (ClusterNode current : tree.getChildren(currentNode)) {
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
	 *            Opacity value of the current node. In case of determine clusters with the cut off value.
	 */
	private void renderDendrogramGenes(final GL gl, ClusterNode currentNode, float fOpacity) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		if (bUseBlackColoring)
			gl.glColor4f(0, 0, 0, 1);
		else
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

				ClusterNode current = listGraph.get(i);

				tempPositions[i] = new Vec3f();
				tempPositions[i].setX(current.getPos().x());
				tempPositions[i].setY(current.getPos().y());
				tempPositions[i].setZ(current.getPos().z());

				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				bCutOffActive[i] = false;

				if (bEnableDepthCheck && bRenderUntilCut == true) {
					if (current.getPos().x() <= fPosCut) {
						// if (current.getSelectionType() != ESelectionType.DESELECTED) {
						renderDendrogramGenes(gl, current, 1);
						// renderDendrogramGenes(gl, current, 0.3f);
						// gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);
					}
					else {
						// renderDendrogramGenes(gl, current, 1);
						bCutOffActive[i] = true;
					}
				}
				else
					renderDendrogramGenes(gl, current, 1);

			}

			fDiff = fTemp - xmin;

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_GENE_NODE_SELECTION,
				currentNode.getClusterNr()));

			// vertical line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all children with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin, tempPositions[i].y(), tempPositions[i].z());
				if (bCutOffActive[i] && bRenderUntilCut == false)
					gl.glVertex3f(xGlobalMax, tempPositions[i].y(), tempPositions[i].z());
				else if (bCutOffActive[i] && bRenderUntilCut == true)
					gl.glVertex3f(fPosCut + 0.1f, tempPositions[i].y(), tempPositions[i].z());
				else
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(), tempPositions[i].z());
				gl.glEnd();
			}

			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_GENE_LEAF_SELECTION,
				currentNode.getClusterNr()));

			// horizontal line visualizing leaf nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
			gl.glVertex3f(xGlobalMax, currentNode.getPos().y(), currentNode.getPos().z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff, currentNode.getPos().y(), currentNode.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos().z());
		gl.glEnd();

	}

	/**
	 * Render a node (experiment) of the dendrogram (recursive)
	 * 
	 * @param gl
	 * @param currentNode
	 * @param fOpacity
	 *            Opacity value of the current node. In case of determine clusters with the cut off value.
	 */
	private void renderDendrogramExperiments(final GL gl, ClusterNode currentNode, float fOpacity) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		if (bUseBlackColoring)
			gl.glColor4f(0, 0, 0, 1);
		else
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

				ClusterNode current = listGraph.get(i);

				tempPositions[i] = new Vec3f();
				tempPositions[i].setX(current.getPos().x());
				tempPositions[i].setY(current.getPos().y());
				tempPositions[i].setZ(current.getPos().z());

				xmax = Math.max(xmax, current.getPos().x());
				xmin = Math.min(xmin, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				bCutOffActive[i] = false;

				if (bEnableDepthCheck && bRenderUntilCut == true) {
					if (current.getPos().y() >= fPosCut) {
						// if (current.getSelectionType() != ESelectionType.DESELECTED) {
						renderDendrogramExperiments(gl, current, 1f);
						// renderDendrogramExperiments(gl, current, 0.3f);
						// gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);
					}
					else {
						// renderDendrogramExperiments(gl, current, 1);
						bCutOffActive[i] = true;
					}
				}
				else
					renderDendrogramExperiments(gl, current, 1);
			}

			fDiff = fTemp - ymax;

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DENDROGRAM_EXPERIMENT_NODE_SELECTION, currentNode.getClusterNr()));

			// horizontal line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glVertex3f(xmax, ymax, currentNode.getPos().z());
			gl.glEnd();

			for (int i = 0; i < iNrChildsNode; i++) {

				// vertical lines connecting all children with their parent
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(tempPositions[i].x(), ymax, tempPositions[i].z());
				if (bCutOffActive[i] && bRenderUntilCut == false)
					gl.glVertex3f(tempPositions[i].x(), yGlobalMin, tempPositions[i].z());
				else if (bCutOffActive[i] && bRenderUntilCut == true)
					gl.glVertex3f(tempPositions[i].x(), fPosCut - 0.1f, tempPositions[i].z());
				else
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(), tempPositions[i].z());
				gl.glEnd();

			}
			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.DENDROGRAM_EXPERIMENT_LEAF_SELECTION, currentNode.getClusterNr()));

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

		if (tree == null) {

			iAlClusterNodes.clear();

			if (bRenderGeneTree == true) {
				if (set.getClusteredTreeGenes() != null) {
					tree = set.getClusteredTreeGenes();
					groupList = new GroupList(1);
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
				// bIsDisplayListDirtyRemote = false;
				bRedrawDendrogram = false;
				bHasFrustumChanged = false;
			}

			gl.glLineWidth(renderStyle.getDendrogramLineWidth());

			if (bRenderGeneTree) {
				gl.glTranslatef(0, -fSampleHeight / 2, 0);
				renderDendrogramGenes(gl, tree.getRoot(), 1);
			}
			else {
				gl.glTranslatef(fSampleWidth / 2, 0, 0);
				renderDendrogramExperiments(gl, tree.getRoot(), 1);
			}

			if (bRenderUntilCut == false)
				renderSelections(gl, tree.getRoot());

			if (bRenderGeneTree)

				gl.glTranslatef(0, +fSampleHeight / 2, 0);
			else
				gl.glTranslatef(-fSampleWidth / 2, 0, 0);
		}
		gl.glEndList();

		// display list for cut off value
		gl.glNewList(iGLDisplayListCutOffValue, GL.GL_COMPILE);

		if (tree != null && bRenderUntilCut == false)
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

		float fWidth = viewFrustum.getWidth();
		float fHeight = viewFrustum.getHeight();

		if (bRenderGeneTree) {
			if (fArTargetWorldCoordinates[0] > 0.1f && fArTargetWorldCoordinates[0] < fWidth)
				fPosCut = fArTargetWorldCoordinates[0] - fLevelWidth;
		}
		else {
			if (fArTargetWorldCoordinates[1] > -0.1f && fArTargetWorldCoordinates[1] < fHeight)
				fPosCut = fArTargetWorldCoordinates[1] + fLevelHeight;
		}
		setDisplayListDirty();

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActive = false;

			determineSelectedNodes();

		}
	}

	/**
	 * This function calls a recursive function which is responsible for setting nodes in the dendrogram
	 * deselected depending on the current position of the "cut off value"
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

		if (iAlClusterNodes.size() < 1) {

			groupList = new GroupList(iAlClusterNodes.size());
			Group temp =
				new Group(tree.getRoot().getNrElements(), false, 0, ESelectionType.NORMAL, tree.getRoot());

			groupList.append(temp);

			if (bRenderGeneTree) {
				NewGroupInfoEvent newGroupInfoEvent = new NewGroupInfoEvent();
				newGroupInfoEvent.setSender(this);
				newGroupInfoEvent.setEVAType(EVAType.CONTENT);
				newGroupInfoEvent.setGroupList(groupList);
				newGroupInfoEvent.setDeleteTree(false);
				eventPublisher.triggerEvent(newGroupInfoEvent);
			}
			else {
				NewGroupInfoEvent newGroupInfoEvent = new NewGroupInfoEvent();
				newGroupInfoEvent.setSender(this);
				newGroupInfoEvent.setEVAType(EVAType.STORAGE);
				newGroupInfoEvent.setGroupList(groupList);
				newGroupInfoEvent.setDeleteTree(false);
				eventPublisher.triggerEvent(newGroupInfoEvent);
			}

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
				new Group(iter.getNrElements(), false, currentVA.indexOf(iExample), ESelectionType.NORMAL,
					iter);
			groupList.append(temp);
			cnt++;
			iExample += iter.getNrElements();
		}

		if (bRenderGeneTree) {
			NewGroupInfoEvent newGroupInfoEvent = new NewGroupInfoEvent();
			newGroupInfoEvent.setSender(this);
			newGroupInfoEvent.setEVAType(EVAType.CONTENT);
			newGroupInfoEvent.setGroupList(groupList);
			newGroupInfoEvent.setDeleteTree(false);
			eventPublisher.triggerEvent(newGroupInfoEvent);
		}
		else {
			NewGroupInfoEvent newGroupInfoEvent = new NewGroupInfoEvent();
			newGroupInfoEvent.setSender(this);
			newGroupInfoEvent.setEVAType(EVAType.STORAGE);
			newGroupInfoEvent.setGroupList(groupList);
			newGroupInfoEvent.setDeleteTree(false);
			eventPublisher.triggerEvent(newGroupInfoEvent);
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
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

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

			case DENDROGRAM_GENE_LEAF_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED:

						if (contentSelectionManager.checkStatus(iExternalID) == false
							&& storageSelectionManager.checkStatus(iExternalID) == false)
							break;

						// Prevent handling of non genetic data in context menu
						if (dataDomain != EDataDomain.GENETIC_DATA)
							break;

						if (!isRenderedRemote()) {
							contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
							contextMenu.setMasterGLView(this);
						}

						GeneContextMenuItemContainer geneContextMenuItemContainer =
							new GeneContextMenuItemContainer();
						geneContextMenuItemContainer.setID(EIDType.EXPRESSION_INDEX, iExternalID);
						contextMenu.addItemContanier(geneContextMenuItemContainer);

						break;
				}

				if (eSelectionType != ESelectionType.NORMAL) {

					resetAllTreeSelections();

					if (tree.getNodeByNumber(iExternalID) != null)
						tree.getNodeByNumber(iExternalID).setSelectionType(eSelectionType);

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

					setDisplayListDirty();
				}

				break;

			case DENDROGRAM_GENE_NODE_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
				}
				if (eSelectionType != ESelectionType.NORMAL && tree.getNodeByNumber(iExternalID) != null) {

					resetAllTreeSelections();

					tree.getNodeByNumber(iExternalID).setSelectionType(eSelectionType);

					ClusterNodeSelectionEvent clusterNodeEvent = new ClusterNodeSelectionEvent();
					SelectionDelta selectionDeltaClusterNode = new SelectionDelta(EIDType.CLUSTER_NUMBER);
					selectionDeltaClusterNode.addSelection(iExternalID, eSelectionType);
					clusterNodeEvent.setSelectionDelta(selectionDeltaClusterNode);
					eventPublisher.triggerEvent(clusterNodeEvent);
				}

				break;

			case DENDROGRAM_EXPERIMENT_LEAF_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED:
						if (contentSelectionManager.checkStatus(iExternalID) == false
							&& storageSelectionManager.checkStatus(iExternalID) == false)
							break;

						if (!isRenderedRemote()) {
							contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
							contextMenu.setMasterGLView(this);
						}

						ExperimentContextMenuItemContainer experimentContextMenuItemContainer =
							new ExperimentContextMenuItemContainer();
						experimentContextMenuItemContainer.setID(iExternalID);
						contextMenu.addItemContanier(experimentContextMenuItemContainer);

						break;
				}

				if (eSelectionType != ESelectionType.NORMAL) {

					resetAllTreeSelections();

					if (tree.getNodeByNumber(iExternalID) != null)
						tree.getNodeByNumber(iExternalID).setSelectionType(eSelectionType);

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

		if (tree == null)
			return new String("Dendrogram - no tree available");

		if (bRenderGeneTree)
			return new String("Dendrogram - " + tree.getRoot().getNrElements() + " genes");
		else
			return new String("Dendrogram - " + tree.getRoot().getNrElements() + " experiments");
	}

	@Override
	public String getDetailedInfo() {
		return new String("Dendrogram view detailedInfo()");
	}

	@Override
	public String toString() {

		if (tree == null)
			return new String("Dendrogram - no tree available");

		return "Standalone " + ((bRenderGeneTree) ? "gene" : "experiment") + " dendrogram, rendered remote: "
			+ isRenderedRemote() + ", Tree with: " + tree.getRoot().getNrElements()
			+ ((bRenderGeneTree) ? " genes" : " experiments") + ", remoteRenderer: "
			+ getRemoteRenderingGLCanvas();
	}

	@Override
	public void clearAllSelections() {

		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		ASerializedView serializedForm;
		if (bRenderGeneTree) {
			SerializedDendogramHorizontalView horizontal = new SerializedDendogramHorizontalView(dataDomain);
			serializedForm = horizontal;
		}
		else {
			SerializedDendogramVerticalView vertical = new SerializedDendogramVerticalView(dataDomain);
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

		// if (bRenderOnlyContext)
		// contentVAType = EVAType.CONTENT_CONTEXT;
		// else
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

					iIndex = iSelectedID;
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.MOUSE_OVER);
				}

				Set<Integer> setSelectionElements =
					contentSelectionManager.getElements(ESelectionType.SELECTION);
				for (Integer iSelectedID : setSelectionElements) {

					iIndex = iSelectedID;
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.SELECTION);
				}
				// setDisplayListDirty();
			}
		}
		else {
			if (tree != null) {

				int iIndex;

				resetAllTreeSelections();

				Set<Integer> setMouseOverElements =
					storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
				for (Integer iSelectedID : setMouseOverElements) {

					iIndex = iSelectedID;
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.MOUSE_OVER);
				}

				Set<Integer> setSelectionElements =
					storageSelectionManager.getElements(ESelectionType.SELECTION);
				for (Integer iSelectedID : setSelectionElements) {
					iIndex = iSelectedID;
					if (tree.getNodeByNumber(iIndex) != null)
						tree.getNodeByNumber(iIndex).setSelectionType(ESelectionType.SELECTION);
				}
				// setDisplayListDirty();
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
	 * Function enables redraw of dendrogram, needed in case of viewfrustum changed and view is rendered
	 * remote.
	 */
	public void setRedrawDendrogram() {
		this.bRedrawDendrogram = true;
		setDisplayListDirty();
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

		SelectionDelta selectionDelta = event.getSelectionDelta();

		if (selectionDelta.getIDType() == EIDType.CLUSTER_NUMBER) {
			// cluster mouse over events only used for gene trees
			if (tree != null && bRenderGeneTree) {
				resetAllTreeSelections();

				Collection<SelectionDeltaItem> deltaItems = selectionDelta.getAllItems();

				for (SelectionDeltaItem item : deltaItems) {
					int clusterNr = item.getPrimaryID();
					if (tree.getNodeByNumber(clusterNr) != null)
						tree.getNodeByNumber(clusterNr).setSelectionType(item.getSelectionType());
				}
				setDisplayListDirty();
			}
		}
	}

	@Override
	public void handleUpdateView() {
		tree = null;
		// setInitialPositionOfCut();
		resetAllTreeSelections();
		bRedrawDendrogram = true;
		setDisplayListDirty();
	}

	/**
	 * Toggles the coloring scheme of the dendrogram view. Either normal coloring or only black coloring is
	 * used.
	 */
	public void toggleColoringScheme() {
		bUseBlackColoring = bUseBlackColoring ? (false) : (true);
	}
}