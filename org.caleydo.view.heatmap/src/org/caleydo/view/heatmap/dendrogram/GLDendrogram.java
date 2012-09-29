/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.heatmap.dendrogram;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.BUTTON_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;
import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.CUT_OFF_Z;
import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec3f;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClusterNodeSelectionListener;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.event.view.tablebased.NewDimensionGroupInfoEvent;
import org.caleydo.core.event.view.tablebased.NewRecordGroupInfoEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.eclipse.swt.widgets.Composite;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Rendering the dendrogram.
 * 
 * @author Bernhard Schlegl
 */
public class GLDendrogram<GroupType extends GroupList<?, ?, ?>>
	extends ATableBasedView
	implements IClusterNodeEventReceiver {

	public static String VIEW_TYPE = "org.caleydo.view.dendrogram";
	public static String VIEW_NAME = "Dendrogram";

	boolean bUseDetailLevel = true;

	private Tree<ClusterNode> tree;
	private DendrogramRenderStyle renderStyle;

	// variables used to build a group list
	private ArrayList<ClusterNode> iAlClusterNodes = new ArrayList<ClusterNode>();
	private GroupType groupList = null;

	/**
	 * true for gene tree, false for experiment tree
	 */
	private boolean bRenderContentTree;

	private boolean bRenderUntilCut = false;

	private boolean bIsDraggingActive = false;
	private float fPosCut = 0.0f;

	private boolean isMirrored = false;

	// sub tree stuff
	private float yPosInitSubTree = 0;
	private float xGlobalMaxSubTree = 0;
	private float fSampleHeightSubTree = 0;
	private float fLevelWidthSubTree = 0;

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

	private boolean bRedrawDendrogram = true;

	private ClusterNodeSelectionListener clusterNodeMouseOverListener;

	private int iGLDisplayListCutOffValue = 0;

	private boolean bUseBlackColoring = false;

	private ClusterNode rootNode = null;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 * @param bRenderGeneTree boolean to determine whether a gene(horizontal) or
	 *            a experiment(vertical) dendrogram should be rendered
	 */
	public GLDendrogram(final GLCanvas glCanvas, Composite parentComposite,
			final ViewFrustum viewFrustum, final boolean bRenderGeneTree) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		ArrayList<SelectionType> alSelectionTypes = new ArrayList<SelectionType>();
		alSelectionTypes.add(SelectionType.NORMAL);
		alSelectionTypes.add(SelectionType.MOUSE_OVER);
		alSelectionTypes.add(SelectionType.SELECTION);

		renderStyle = new DendrogramRenderStyle(this, viewFrustum);

		this.bRenderContentTree = bRenderGeneTree;

		fPosCut = 0f;
		// if (bRenderGeneTree)
		// fPosCut = viewFrustum.getWidth() / 5f;
		// else
		// fPosCut = viewFrustum.getHeight() / 5f;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		clusterNodeMouseOverListener = new ClusterNodeSelectionListener();
		clusterNodeMouseOverListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class,
				clusterNodeMouseOverListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (clusterNodeMouseOverListener != null) {
			eventPublisher.removeListener(clusterNodeMouseOverListener);
			clusterNodeMouseOverListener = null;
		}
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		iGLDisplayListCutOffValue = gl.glGenLists(1);
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;

		init(gl);

	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}
	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);

		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {

		display(gl);

	}

	@Override
	public void display(GL2 gl) {
		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		if (bIsDraggingActive) {
			handleDragging(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingActive = false;
			}
		}

		gl.glCallList(displayListIndex);

		// display list for cut off value
		gl.glCallList(iGLDisplayListCutOffValue);
		if (!lazyMode)
			checkForHits(gl);
	}

	/**
	 * Returns the position of the cut. This function is used in HHM to
	 * determine how many area the dendrogram up to the cut requires.
	 * 
	 * @return position of cut
	 */
	public float getPositionOfCut() {
		return fPosCut;
	}

	/**
	 * Function sets an initial value for the position of the cut off value.
	 * Used in HHM when rendering a dendrogram the first time.
	 * 
	 * @return True in case of tree is available and clusters are determined,
	 *         false in case of no tree available.
	 */
	public boolean setInitialPositionOfCut() {
		if (bRenderContentTree)
			fPosCut = viewFrustum.getWidth() / 4f;
		else
			fPosCut = viewFrustum.getHeight() - viewFrustum.getHeight() / 4f;

		if (tree != null && tree.getRoot() != null) {
			determineSelectedNodes();
			return true;
		}
		return false;
	}

	/**
	 * Function is responsible for activating/deactivating "render up to cut"
	 * mode.
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
	private void renderCut(final GL2 gl) {

		float fHeight = viewFrustum.getHeight();
		float fWidth = viewFrustum.getWidth();
		float fWidthCutOf = renderStyle.getWidthCutOff();

		gl.glColor4f(1, 1, 1, 1);

		if (bRenderContentTree) {

			gl.glTranslatef(+fLevelWidth, 0, 0);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.SLIDER_ENDING);
			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, fHeight + 0.1f, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, fHeight + 0.1f, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glEnd();

			tempTexture.disable(gl);
			tempTexture = textureManager.getIconTexture(gl, EIconTextures.SLIDER_MIDDLE);
			tempTexture.enable(gl);
			tempTexture.bind(gl);

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, 0, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fPosCut - fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, fHeight, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fPosCut + fWidthCutOf / 2, 0, CUT_OFF_Z);
			gl.glEnd();
			tempTexture.disable(gl);

			float fSizeDendrogramArrow = renderStyle.getSizeDendrogramArrow();

			Texture textureArrow = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			textureArrow.enable(gl);
			textureArrow.bind(gl);

			TextureCoords texCoordsArrow = textureArrow.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_CUT_SELECTION, 1));
			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fPosCut, -fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fPosCut + fSizeDendrogramArrow, -fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fPosCut + fSizeDendrogramArrow, 0, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fPosCut, 0, BUTTON_Z);
			gl.glEnd();

			gl.glBegin(GL2.GL_POLYGON);
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

			textureArrow.disable(gl);

			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			gl.glTranslatef(-fLevelWidth, 0, 0);
		}
		else {

			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
			if (fPosCut > fLevelHeight)
				gl.glTranslatef(0, -fLevelHeight, 0);

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.SLIDER_ENDING);
			tempTexture.enable(gl);
			tempTexture.bind(gl);
			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-0.1f, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(+0.0f, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(+0.0f, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(-0.1f, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glEnd();

			tempTexture.disable(gl);
			tempTexture = textureManager.getIconTexture(gl, EIconTextures.SLIDER_MIDDLE);
			tempTexture.enable(gl);
			tempTexture.bind(gl);
			texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fWidth, fPosCut - fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fWidth, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, fPosCut + fWidthCutOf / 2, CUT_OFF_Z);
			gl.glEnd();
			tempTexture.disable(gl);

			float fSizeDendrogramArrow = renderStyle.getSizeDendrogramArrow();

			Texture textureArrow = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			textureArrow.enable(gl);
			textureArrow.bind(gl);

			TextureCoords texCoordsArrow = textureArrow.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_CUT_SELECTION, 1));
			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut + fSizeDendrogramArrow,
					BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fWidth, fPosCut + fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth, fPosCut, BUTTON_Z);
			gl.glEnd();

			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.right(), texCoordsArrow.top());
			gl.glVertex3f(fWidth + fSizeDendrogramArrow, fPosCut - fSizeDendrogramArrow,
					BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.top());
			gl.glVertex3f(fWidth, fPosCut - fSizeDendrogramArrow, BUTTON_Z);
			gl.glTexCoord2f(texCoordsArrow.left(), texCoordsArrow.bottom());
			gl.glVertex3f(fWidth, fPosCut, BUTTON_Z);
			gl.glEnd();
			gl.glPopName();

			textureArrow.disable(gl);

			if (fPosCut > fLevelHeight)
				gl.glTranslatef(0, +fLevelHeight, 0);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		}

	}

	/**
	 * In case of no tree is available render the symbol of the view instead of
	 * the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL2 gl) {
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = null;

		if (bRenderContentTree)
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.DENDROGRAM_HORIZONTAL_SYMBOL);
		else
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.DENDROGRAM_VERTICAL_SYMBOL);

		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL2.GL_POLYGON);

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
		tempTexture.disable(gl);
	}

	/**
	 * This function calls a recursive function which is responsible for the
	 * calculation of the position inside the view frustum of the nodes in the
	 * dendrogram
	 */
	private void determinePositions() {

		if (bRenderContentTree)
			determinePosRecGenes(rootNode);
		else
			determinePosRecExperiments(rootNode);

	}

	/**
	 * Functions renders a sub part of the dendrogram determined by two indexes
	 * (first and last element).
	 * 
	 * @param gl
	 * @param fromIndex index of the first element in sub dendrogram
	 * @param toIndex index (+1) of the last element in sub dendrogram
	 * @param iNrLeafs number of leaf nodes in sub dendrogram
	 * @param fWidth width of area for sub dendrogram
	 * @param fHeight height of area for sub dendrogram
	 */
	public void renderSubTreeFromIndexToIndex(GL2 gl, int fromIndex, int toIndex,
			int iNrLeafs, float fWidth, float fHeight) {

		if (tree == null || tree.getRoot() == null)
			return;

		determineNodesToRender(rootNode, fromIndex, toIndex);

		yPosInitSubTree = fHeight;
		xGlobalMaxSubTree = fWidth;

		int iMaxDepthSubTree = determineMaxDepthSubTree(rootNode);

		fLevelWidthSubTree = fWidth / (iMaxDepthSubTree + 1);
		fSampleHeightSubTree = fHeight / iNrLeafs;

		determinePosRecSubTree(rootNode);

		gl.glTranslatef(0, -fSampleHeightSubTree / 2, 0);
		gl.glLineWidth(renderStyle.getDendrogramLineWidth());
		renderSubTreeRec(gl, rootNode);

		gl.glTranslatef(0, +fSampleHeightSubTree / 2, 0);
	}

	/**
	 * Recursive function responsible for determine sub dendrogram.
	 * 
	 * @param currentNode
	 * @param from index of the first element in sub dendrogram
	 * @param to index (+1) of the last element in sub dendrogram
	 * @param inBlock boolean which identifies nodes in sub dendrogram
	 * @return true if node is part of sub dendrogram, false otherwise
	 */
	private boolean determineNodesToRender(ClusterNode currentNode, int from, int to) {

		if (currentNode.hasChildren()) {
			boolean isPartOfSubTree = true;
			for (ClusterNode child : currentNode.getChildren()) {
				if (!determineNodesToRender(child, from, to)) {
					isPartOfSubTree = false;
				}
			}
			currentNode.setIsPartOfSubTree(isPartOfSubTree);
			return isPartOfSubTree;
		}
		else {

			RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
					.getVirtualArray();
			int index = recordVA.indexOf(currentNode.getLeafID());
			if (recordVA.indicesOf(currentNode.getLeafID()).size() > 1) {
				throw new IllegalStateException("duplicate element");
			}
			if (index >= from && index <= to) {
				currentNode.setIsPartOfSubTree(true);
				return true;
			}
			else {
				currentNode.setIsPartOfSubTree(false);
				return false;
			}

		}

	}

	/**
	 * Helper function which determines the maximum hierarchy depth of the sub
	 * denrogram.
	 * 
	 * @param currentNode
	 */
	private int determineMaxDepthSubTree(ClusterNode currentNode) {

		int temp = 0;
		if (currentNode.isPartOfSubTree() == true) {
			temp = currentNode.getDepth();
		}
		int maxSubDepth = 0;
		if (currentNode.hasChildren()) {
			for (ClusterNode child : tree.getChildren(currentNode)) {
				int subDepth = determineMaxDepthSubTree(child);
				maxSubDepth = Math.max(subDepth, maxSubDepth);
			}
		}

		return Math.max(temp, maxSubDepth);

	}

	/**
	 * Function calculates for each node (gene or entity) in the sub dendrogram
	 * recursive the corresponding position inside the view frustum
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
				pos.setX(xGlobalMaxSubTree - fLevelWidthSubTree);// * (1 -
				// fCoeff));
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
	private void renderSubTreeRec(GL2 gl, ClusterNode currentNode) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = dataDomain.getColorMapper().getColor(fLookupValue);

		if (bUseBlackColoring)
			gl.glColor4f(0, 0, 0, 1);
		else
			gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], 1);

		// if (currentNode.isPartOfSubTree())
		// currentNode.getPosSubTree().x();

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

				gl.glPushName(pickingManager.getPickingID(uniqueID,
						PickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode.getID()));

				// vertical line connecting all child nodes
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(xmin, ymin, currentNode.getPosSubTree().z());
				gl.glVertex3f(xmin, ymax, currentNode.getPosSubTree().z());
				gl.glEnd();

				// horizontal lines connecting all children with their parent
				for (int i = 0; i < iNrChildsNode; i++) {
					gl.glBegin(GL2.GL_LINES);
					gl.glVertex3f(xmin, tempPositions[i].y(), tempPositions[i].z());
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
							tempPositions[i].z());
					gl.glEnd();
				}
				gl.glPopName();
			}

		}
		else {
			// horizontal line visualizing leaf nodes
			if (currentNode.isPartOfSubTree()) {
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(currentNode.getPosSubTree().x(),
						currentNode.getPosSubTree().y(), currentNode.getPosSubTree().z());
				gl.glVertex3f(xGlobalMaxSubTree, currentNode.getPosSubTree().y(), currentNode
						.getPosSubTree().z());
				gl.glEnd();
			}
		}

		if (currentNode.isPartOfSubTree()) {
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_GENE_LEAF_SELECTION, currentNode.getID()));
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(currentNode.getPosSubTree().x() + fLevelWidthSubTree, currentNode
					.getPosSubTree().y(), currentNode.getPosSubTree().z());
			gl.glVertex3f(currentNode.getPosSubTree().x(), currentNode.getPosSubTree().y(),
					currentNode.getPosSubTree().z());
			gl.glEnd();
			gl.glPopName();
		}
	}

	/**
	 * Function calculates for each node (gene or entity) in the dendrogram
	 * recursive the corresponding position inside the view frustum
	 * 
	 * @param currentNode current node for calculation
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
	 * Function calculates for each node (experiment) in the dendrogram
	 * recursive the corresponding position inside the view frustum
	 * 
	 * @param currentNode current node for calculation
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
	 * Recursive function which renders selection markers for all nodes in the
	 * tree.
	 * 
	 * @param gl
	 * @param currentNode
	 */
	private void renderSelections(final GL2 gl, ClusterNode currentNode) {

		if (currentNode.getSelectionType() == SelectionType.MOUSE_OVER) {
			gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f,
					currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f,
					currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f,
					currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f,
					currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glEnd();

		}
		else if (currentNode.getSelectionType() == SelectionType.SELECTION) {
			gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f,
					currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f,
					currentNode.getPos().y() - 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() + 0.025f,
					currentNode.getPos().y() + 0.025f, SELECTION_Z);
			gl.glVertex3f(currentNode.getPos().x() - 0.025f,
					currentNode.getPos().y() + 0.025f, SELECTION_Z);
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
	 * @param fOpacity Opacity value of the current node. In case of determine
	 *            clusters with the cut off value.
	 */
	private void renderDendrogramGenes(final GL2 gl, ClusterNode currentNode, float fOpacity) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = dataDomain.getColorMapper().getColor(fLookupValue);

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

				if (bRenderUntilCut) {
					if (current.getPos().x() <= fPosCut) {
						// if (current.getSelectionType() !=
						// SelectionType.DESELECTED) {
						renderDendrogramGenes(gl, current, 1);
						// renderDendrogramGenes(gl, current, 0.3f);
						// gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
						// fArMappingColor[2], fOpacity);
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

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode.getID()));

			// vertical line connecting all child nodes
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(xmin, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all children with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(xmin, tempPositions[i].y(), tempPositions[i].z());
				if (bCutOffActive[i] && bRenderUntilCut == false)
					gl.glVertex3f(xGlobalMax, tempPositions[i].y(), tempPositions[i].z());
				else if (bCutOffActive[i] && bRenderUntilCut == true)
					gl.glVertex3f(fPosCut + 0.1f, tempPositions[i].y(), tempPositions[i].z());
				else
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
							tempPositions[i].z());
				gl.glEnd();
			}

			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_GENE_LEAF_SELECTION, currentNode.getID()));

			// horizontal line visualizing leaf nodes
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode
					.getPos().z());
			gl.glVertex3f(xGlobalMax, currentNode.getPos().y(), currentNode.getPos().z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff, currentNode.getPos().y(), currentNode
				.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos()
				.z());
		gl.glEnd();

	}

	/**
	 * Render a node (experiment) of the dendrogram (recursive)
	 * 
	 * @param gl
	 * @param currentNode
	 * @param fOpacity Opacity value of the current node. In case of determine
	 *            clusters with the cut off value.
	 */
	private void renderDendrogramExperiments(final GL2 gl, ClusterNode currentNode,
			float fOpacity) {

		float fLookupValue = currentNode.getAverageExpressionValue();
		float[] fArMappingColor = dataDomain.getColorMapper().getColor(fLookupValue);

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

				if (bRenderUntilCut) {
					if (current.getPos().y() >= fPosCut) {
						// if (current.getSelectionType() !=
						// SelectionType.DESELECTED) {
						renderDendrogramExperiments(gl, current, 1f);
						// renderDendrogramExperiments(gl, current, 0.3f);
						// gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
						// fArMappingColor[2], fOpacity);
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

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_EXPERIMENT_NODE_SELECTION, currentNode.getID()));

			// horizontal line connecting all child nodes
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glVertex3f(xmax, ymax, currentNode.getPos().z());
			gl.glEnd();

			for (int i = 0; i < iNrChildsNode; i++) {

				// vertical lines connecting all children with their parent
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(tempPositions[i].x(), ymax, tempPositions[i].z());
				if (bCutOffActive[i] && bRenderUntilCut == false)
					gl.glVertex3f(tempPositions[i].x(), yGlobalMin, tempPositions[i].z());
				else if (bCutOffActive[i] && bRenderUntilCut == true)
					gl.glVertex3f(tempPositions[i].x(), fPosCut - 0.1f, tempPositions[i].z());
				else
					gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
							tempPositions[i].z());
				gl.glEnd();

			}
			gl.glPopName();

		}
		else {
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.DENDROGRAM_EXPERIMENT_LEAF_SELECTION, currentNode.getID()));

			// vertical line visualizing leaf nodes
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode
					.getPos().z());
			gl.glVertex3f(currentNode.getPos().x(), yGlobalMin, currentNode.getPos().z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y() - fDiff, currentNode
				.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(), currentNode.getPos()
				.z());
		gl.glEnd();
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		gl.glPushMatrix();
		if (isMirrored) {
			gl.glTranslatef(viewFrustum.getWidth(), 0, 0);
			gl.glScalef(-1.0f, 1.0f, 1.0f);
		}

		if (tree == null || tree.getRoot() == null) {

			iAlClusterNodes.clear();

			if (bRenderContentTree == true) {
				if (tablePerspective.getRecordPerspective().getTree() != null) {
					tree = tablePerspective.getRecordPerspective().getTree();
					groupList = (GroupType) new RecordGroupList();
					rootNode = tree.getRoot();
				}
				else
					renderSymbol(gl);
			}
			else {
				if (!tablePerspective.getDimensionPerspective().isTreeDefaultTree()
						&& tablePerspective.getDimensionPerspective().getTree() != null) {
					tree = tablePerspective.getDimensionPerspective().getTree();
					groupList = (GroupType) new DimensionGroupList();
					rootNode = tree.getRoot();
				}
				else
					renderSymbol(gl);
			}
		}

		if (tree != null && tree.getRoot() != null) {
			if (hasFrustumChanged || bRedrawDendrogram) {
				if (bRenderContentTree) {
					xGlobalMax = viewFrustum.getWidth();
					fSampleHeight = viewFrustum.getHeight() / rootNode.getNrLeaves();
					fLevelWidth = (viewFrustum.getWidth() - 0.1f) / rootNode.getDepth();
					yPosInit = viewFrustum.getHeight();
				}
				else {
					yGlobalMin = 0.0f;
					fSampleWidth = viewFrustum.getWidth() / rootNode.getNrLeaves();
					fLevelHeight = (viewFrustum.getHeight() - 0.1f) / rootNode.getDepth();
					xPosInit = 0.0f;
				}
				determinePositions();
				// bIsDisplayListDirtyRemote = false;
				bRedrawDendrogram = false;
				hasFrustumChanged = false;
			}

			gl.glLineWidth(renderStyle.getDendrogramLineWidth());

			if (bRenderContentTree) {
				gl.glTranslatef(0, -fSampleHeight / 2, 0);
				renderDendrogramGenes(gl, rootNode, 1);
			}
			else {
				gl.glTranslatef(fSampleWidth / 2, 0, 0);
				renderDendrogramExperiments(gl, rootNode, 1);
			}

			if (bRenderUntilCut == false)
				renderSelections(gl, rootNode);

			if (bRenderContentTree)

				gl.glTranslatef(0, +fSampleHeight / 2, 0);
			else
				gl.glTranslatef(-fSampleWidth / 2, 0, 0);
		}
		gl.glEndList();

		// display list for cut off value
		gl.glNewList(iGLDisplayListCutOffValue, GL2.GL_COMPILE);

		if (tree != null && tree.getRoot() != null && bRenderUntilCut == false)
			renderCut(gl);

		gl.glPopMatrix();
		gl.glEndList();

	}

	/**
	 * Function used for updating cursor position in case of dragging
	 * 
	 * @param gl
	 */
	private void handleDragging(final GL2 gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fWidth = viewFrustum.getWidth();
		float fHeight = viewFrustum.getHeight();

		if (bRenderContentTree) {
			if (fArTargetWorldCoordinates[0] > 0.1f && fArTargetWorldCoordinates[0] < fWidth) {
				if (isMirrored) {
					fPosCut = -fArTargetWorldCoordinates[0] + (rootNode.getDepth() - 1)
							* fLevelWidth + 0.1f;
				}
				else {
					fPosCut = fArTargetWorldCoordinates[0] - fLevelWidth;
				}

			}
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
	 * This function calls a recursive function which is responsible for setting
	 * nodes in the dendrogram deselected depending on the current position of
	 * the "cut off value"
	 */
	@SuppressWarnings("unchecked")
	private void determineSelectedNodes() {

		iMaxDepth = Integer.MAX_VALUE;
		determineSelectedNodesRec(rootNode);

		iAlClusterNodes.clear();
		getNumberOfClustersRec(rootNode);
		if (bRenderContentTree)
			groupList = (GroupType) tablePerspective.getRecordPerspective().getVirtualArray()
					.buildNewGroupList(iAlClusterNodes);
		else
			groupList = (GroupType) tablePerspective.getDimensionPerspective()
					.getVirtualArray().buildNewGroupList(iAlClusterNodes);

		triggerGroupListEvent();

	}

	private void triggerGroupListEvent() {
		if (bRenderContentTree) {
			NewRecordGroupInfoEvent newGroupInfoEvent = new NewRecordGroupInfoEvent();
			newGroupInfoEvent.setSender(this);
			newGroupInfoEvent.setVAType(tablePerspective.getRecordPerspective()
					.getPerspectiveID());
			newGroupInfoEvent.setGroupList((RecordGroupList) groupList);
			newGroupInfoEvent.setDeleteTree(false);
			eventPublisher.triggerEvent(newGroupInfoEvent);
		}
		else {
			NewDimensionGroupInfoEvent newGroupInfoEvent = new NewDimensionGroupInfoEvent();
			newGroupInfoEvent.setSender(this);
			newGroupInfoEvent.setVAType(tablePerspective.getDimensionPerspective()
					.getPerspectiveID());
			newGroupInfoEvent.setGroupList((DimensionGroupList) groupList);
			newGroupInfoEvent.setDeleteTree(false);
			eventPublisher.triggerEvent(newGroupInfoEvent);
		}
	}

	/**
	 * Recursive function determines the sizes of clusters set by the cut off
	 * value
	 * 
	 * @param node current node
	 */
	private void getNumberOfClustersRec(ClusterNode node) {

		if (node.getSelectionType() == SelectionType.NORMAL) {
			if (tree.hasChildren(node)) {
				for (ClusterNode current : tree.getChildren(node)) {
					if (current.getSelectionType() == SelectionType.DESELECTED) {
						// System.out.println("nr elements: " +
						// current.getNrElements());
						iAlClusterNodes.add(current);
					}
					else
						getNumberOfClustersRec(current);
				}
			}
		}
	}

	/**
	 * Determines for each node in the dendrogram if the current node is
	 * selected by the "cut" or not
	 * 
	 * @param node current node
	 */
	private void determineSelectedNodesRec(ClusterNode node) {

		if (bRenderContentTree) {
			if (node.getPos().x() < fPosCut) {
				node.setSelectionType(SelectionType.NORMAL);
				if (node.getDepth() < iMaxDepth) {
					iMaxDepth = node.getDepth();
				}
			}
			else {
				node.setSelectionType(SelectionType.DESELECTED);
			}
		}
		else {
			if (node.getPos().y() > fPosCut) {
				node.setSelectionType(SelectionType.NORMAL);
				if (node.getDepth() < iMaxDepth) {
					iMaxDepth = node.getDepth();
				}
			}
			else {
				node.setSelectionType(SelectionType.DESELECTED);
			}
		}

		if (tree.hasChildren(node)) {
			for (ClusterNode current : tree.getChildren(node)) {
				determineSelectedNodesRec(current);
			}
		}
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType = SelectionType.NORMAL;

		switch (pickingType) {

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
						selectionType = SelectionType.SELECTION;
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						selectionType = SelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED:

						ClusterNode leafNode = tree.getNodeByNumber(externalID);
						if (recordSelectionManager.checkStatus(leafNode.getLeafID()) == false
								&& dimensionSelectionManager.checkStatus(leafNode.getLeafID()) == false)
							break;

						// Prevent handling of non genetic data in context menu
						if (!dataDomain.getDataDomainID().equals(
								"org.caleydo.datadomain.genetic"))
							break;

						AContextMenuItem menuItem = new BookmarkMenuItem(
								"Bookmark "
										+ dataDomain.getRecordLabel(recordIDType,
												leafNode.getLeafID()), recordIDType,
								leafNode.getLeafID(), dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(menuItem);

						break;
				}

				if (selectionType != SelectionType.NORMAL) {

					resetAllTreeSelections();

					if (tree.getNodeByNumber(externalID) != null)
						tree.getNodeByNumber(externalID).setSelectionType(selectionType);

					SelectionDelta selectionDelta = null;
					SelectionManager selectionManager = null;

					selectionManager = recordSelectionManager;

					selectionManager.clearSelection(selectionType);
					selectionManager.addToType(selectionType, tree.getNodeByNumber(externalID)
							.getLeafID());
					selectionDelta = selectionManager.getDelta();

					prepareVisualLinkingInformation(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta((SelectionDelta) selectionDelta);
					eventPublisher.triggerEvent(event);

					setDisplayListDirty();
				}

				break;

			case DENDROGRAM_GENE_NODE_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						selectionType = SelectionType.SELECTION;
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						selectionType = SelectionType.MOUSE_OVER;
						break;
				}
				if (selectionType != SelectionType.NORMAL
						&& tree.getNodeByNumber(externalID) != null) {

					resetAllTreeSelections();

					tree.getNodeByNumber(externalID).setSelectionType(selectionType);

					ClusterNodeSelectionEvent clusterNodeEvent = new ClusterNodeSelectionEvent();
					SelectionDelta selectionDeltaClusterNode = new SelectionDelta(
							tree.getNodeIDType());
					selectionDeltaClusterNode.addSelection(externalID, selectionType);
					clusterNodeEvent.setSelectionDelta(selectionDeltaClusterNode);
					eventPublisher.triggerEvent(clusterNodeEvent);
				}

				break;

			case DENDROGRAM_EXPERIMENT_LEAF_SELECTION:

				switch (pickingMode) {

					case CLICKED:
						selectionType = SelectionType.SELECTION;
						break;
					case DRAGGED:
						break;
					case MOUSE_OVER:
						selectionType = SelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED:
						ClusterNode leafNode = tree.getNodeByNumber(externalID);

						if (recordSelectionManager.checkStatus(leafNode.getLeafID()) == false
								&& dimensionSelectionManager.checkStatus(leafNode.getLeafID()) == false)
							break;

						AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
								+ dataDomain.getRecordLabel(tree.getLeaveIDType(),
										leafNode.getLeafID()), tree.getLeaveIDType(),
								leafNode.getLeafID(), dataDomain.getDataDomainID());
						contextMenuCreator.addContextMenuItem(menuItem);

						break;
				}

				if (selectionType != SelectionType.NORMAL) {

					resetAllTreeSelections();

					if (tree.getNodeByNumber(externalID) != null)
						tree.getNodeByNumber(externalID).setSelectionType(selectionType);

					SelectionDelta selectionDelta = null;
					SelectionManager selectionManager = null;

					selectionManager = dimensionSelectionManager;

					selectionManager.clearSelection(selectionType);
					selectionManager.addToType(selectionType, tree.getNodeByNumber(externalID)
							.getLeafID());
					selectionDelta = selectionManager.getDelta();

					prepareVisualLinkingInformation(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta((SelectionDelta) selectionDelta);
					eventPublisher.triggerEvent(event);

					setDisplayListDirty();
				}
				break;
		}
	}

	@Override
	public String toString() {

		if (tree == null)
			return new String("Dendrogram - no tree available");

		return "Standalone " + ((bRenderContentTree) ? "gene" : "experiment")
				+ " dendrogram, rendered remote: " + isRenderedRemote() + ", Tree with: "
				+ rootNode.getNrLeaves() + ((bRenderContentTree) ? " genes" : " experiments")
				+ ", remoteRenderer: " + getRemoteRenderingGLView();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		ASerializedView serializedForm;
		if (bRenderContentTree) {
			SerializedDendogramHorizontalView horizontal = new SerializedDendogramHorizontalView(
					this);
			serializedForm = horizontal;
		}
		else {
			SerializedDendogramVerticalView vertical = new SerializedDendogramVerticalView(
					this);
			serializedForm = vertical;
		}
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int dimensionIndex) throws InvalidAttributeValueException {
		return null;
	}

	/**
	 * Function called any time a update is triggered external
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(SelectionDelta delta) {

		if (bRenderContentTree == true) {
			if (tree != null && tree.getRoot() != null) {
				int iIndex;

				resetAllTreeSelections();

				Set<Integer> setMouseOverElements = recordSelectionManager
						.getElements(SelectionType.MOUSE_OVER);
				for (Integer iSelectedID : setMouseOverElements) {

					iIndex = iSelectedID;
					ArrayList<Integer> alClusterNumbers = tree.getNodeIDsFromLeafID(iIndex);
					if (alClusterNumbers != null) {
						for (Integer clusterNumber : alClusterNumbers) {
							tree.getNodeByNumber(clusterNumber).setSelectionType(
									SelectionType.MOUSE_OVER);
						}
					}
				}

				Set<Integer> setSelectionElements = recordSelectionManager
						.getElements(SelectionType.SELECTION);
				for (Integer iSelectedID : setSelectionElements) {

					iIndex = iSelectedID;
					ArrayList<Integer> alClusterNumbers = tree.getNodeIDsFromLeafID(iIndex);
					if (alClusterNumbers != null) {
						for (Integer clusterNumber : alClusterNumbers) {
							tree.getNodeByNumber(clusterNumber).setSelectionType(
									SelectionType.SELECTION);
						}
					}
				}
				// setDisplayListDirty();
			}
		}
		else {
			if (tree != null && tree.getRoot() != null) {

				int iIndex;

				resetAllTreeSelections();

				Set<Integer> setMouseOverElements = dimensionSelectionManager
						.getElements(SelectionType.MOUSE_OVER);
				for (Integer iSelectedID : setMouseOverElements) {

					iIndex = iSelectedID;
					ArrayList<Integer> alClusterNumbers = tree.getNodeIDsFromLeafID(iIndex);
					if (alClusterNumbers != null) {
						for (Integer clusterNumber : alClusterNumbers) {
							tree.getNodeByNumber(clusterNumber).setSelectionType(
									SelectionType.MOUSE_OVER);
						}
					}

				}

				Set<Integer> setSelectionElements = dimensionSelectionManager
						.getElements(SelectionType.SELECTION);
				for (Integer iSelectedID : setSelectionElements) {
					iIndex = iSelectedID;
					ArrayList<Integer> alClusterNumbers = tree.getNodeIDsFromLeafID(iIndex);
					if (alClusterNumbers != null) {
						for (Integer clusterNumber : alClusterNumbers) {
							tree.getNodeByNumber(clusterNumber).setSelectionType(
									SelectionType.SELECTION);
						}
					}
				}
				// setDisplayListDirty();
			}
		}
	}

	@Override
	public void resetView() {
		super.resetView();
	}

	/**
	 * Function enables redraw of dendrogram, needed in case of viewfrustum
	 * changed and view is rendered remote.
	 */
	public void setRedrawDendrogram() {
		this.bRedrawDendrogram = true;
		setDisplayListDirty();
	}

	/**
	 * This function calls a recursive function which is responsible for setting
	 * all nodes in the dendrogram to {@link SelectionType.NORMAL}
	 */
	private void resetAllTreeSelections() {
		if (tree != null && tree.getRoot() != null)
			resetAllTreeSelectionsRec(rootNode);
	}

	/**
	 * Recursive function resets all selections in the tree
	 * 
	 * @param node current node
	 */
	private void resetAllTreeSelectionsRec(ClusterNode currentNode) {

		currentNode.setSelectionType(SelectionType.NORMAL);

		if (tree.hasChildren(currentNode)) {
			for (ClusterNode current : tree.getChildren(currentNode)) {
				resetAllTreeSelectionsRec(current);
			}
		}
	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {

		SelectionDelta selectionDelta = event.getSelectionDelta();

		if (tree != null && tree.getRoot() != null
				&& selectionDelta.getIDType() == tree.getNodeIDType()) {
			// cluster mouse over events only used for gene trees
			if (tree != null && tree.getRoot() != null) { // && bRenderGeneTree
				resetAllTreeSelections();

				Collection<SelectionDeltaItem> deltaItems = selectionDelta.getAllItems();

				for (SelectionDeltaItem item : deltaItems) {
					int clusterNr = item.getID();
					if (tree.getNodeByNumber(clusterNr) != null)
						tree.getNodeByNumber(clusterNr).setSelectionType(
								item.getSelectionType());
				}
				setDisplayListDirty();
			}
		}
	}

	// public void handleRedrawView() {
	// tree = null;
	// // setInitialPositionOfCut();
	// resetAllTreeSelections();
	// bRedrawDendrogram = true;
	// setDisplayListDirty();
	// }

	/**
	 * Toggles the coloring scheme of the dendrogram view. Either normal
	 * coloring or only black coloring is used.
	 */
	public void toggleColoringScheme() {
		bUseBlackColoring = bUseBlackColoring ? (false) : (true);
	}

	public boolean isMirrored() {
		return isMirrored;
	}

	public void setMirrored(boolean isMirrored) {
		this.isMirrored = isMirrored;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		super.setDataDomain(dataDomain);
		tree = null;
		rootNode = null;
	}

	@Override
	public boolean isDataView() {
		return false;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		gl.glDeleteLists(iGLDisplayListCutOffValue, 1);
	}
}