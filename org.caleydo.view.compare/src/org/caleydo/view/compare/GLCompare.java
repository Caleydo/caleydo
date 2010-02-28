package org.caleydo.view.compare;

import static org.caleydo.view.heatmap.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
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
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ContentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.listener.CompareGroupsEventListener;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLCompare extends AGLView implements IViewCommandHandler,
		IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.compare";

	private ArrayList<ISet> setsToCompare;
	private HashMap<Integer, HeatMapWrapper> hashHeatMapWrappers;

	// private TextRenderer textRenderer;
	private HeatMapLayoutLeft heatMapLayoutLeft;
	private HeatMapLayoutRight heatMapLayoutRight;
	private HeatMapWrapper leftHeatMapWrapper;
	private HeatMapWrapper rightHeatMapWrapper;

	private CompareGroupsEventListener compareGroupsEventListener;

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	// private SetRelations relations;

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
		hashHeatMapWrappers = new HashMap<Integer, HeatMapWrapper>();
	}

	@Override
	public void init(GL gl) {
		// contentVA = useCase.getContentVA(ContentVAType.CONTENT);
		// storageVA = useCase.getStorageVA(StorageVAType.STORAGE);
		heatMapLayoutLeft = new HeatMapLayoutLeft();
		heatMapLayoutRight = new HeatMapLayoutRight();
		leftHeatMapWrapper = new HeatMapWrapper(0, heatMapLayoutLeft, this,
				null, useCase, this, dataDomain);
		hashHeatMapWrappers.put(0, leftHeatMapWrapper);
		rightHeatMapWrapper = new HeatMapWrapper(1, heatMapLayoutRight, this,
				null, useCase, this, dataDomain);
		hashHeatMapWrappers.put(1, rightHeatMapWrapper);
		leftHeatMapWrapper.init(gl, this, glMouseListener, null, useCase, this,
				dataDomain);
		rightHeatMapWrapper.init(gl, this, glMouseListener, null, useCase,
				this, dataDomain);

		leftHeatMapWrapper.setSet(set);
		rightHeatMapWrapper.setSet(set);
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

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
		leftHeatMapWrapper.processEvents();
		rightHeatMapWrapper.processEvents();
		if (!isVisible())
			return;
		pickingManager.handlePicking(this, gl);
		heatMapLayoutLeft.setLayoutParameters(0.0f, 0.0f, viewFrustum.getTop(),
				viewFrustum.getRight() / 3.0f);
		heatMapLayoutRight.setLayoutParameters(
				2.0f * viewFrustum.getRight() / 3.0f, 0.0f, viewFrustum
						.getTop(), viewFrustum.getRight() / 3.0f);

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			leftHeatMapWrapper.setDisplayListDirty();
			rightHeatMapWrapper.setDisplayListDirty();
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
		if (leftHeatMapWrapper.handleDragging(gl, glMouseListener)) {
			rightHeatMapWrapper.selectGroupsFromContentVAList(gl,
					glMouseListener, leftHeatMapWrapper
							.getContentVAsOfHeatMaps());
			setDisplayListDirty();
		} else if (rightHeatMapWrapper.handleDragging(gl, glMouseListener)) {
			leftHeatMapWrapper.selectGroupsFromContentVAList(gl,
					glMouseListener, rightHeatMapWrapper
							.getContentVAsOfHeatMaps());
			setDisplayListDirty();
		}

		gl.glCallList(iGLDisplayListToCall);

		leftHeatMapWrapper.drawRemoteItems(gl);
		rightHeatMapWrapper.drawRemoteItems(gl);
		// renderTree(gl);
		// renderOverviewRelations(gl);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		leftHeatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
				iUniqueID);
		rightHeatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
				iUniqueID);

		renderTree(gl);

		renderOverviewToDetailRelations(gl);

		renderDetailRelations(gl);

		if (leftHeatMapWrapper.getContentVAsOfHeatMaps().size() == 0)
			renderOverviewRelations(gl);

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	private void renderDetailRelations(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		float alpha = 0.3f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();

		gl.glColor3f(0, 1, 0);

		// Iterate over all detail content VAs on the left
		for (ContentVirtualArray contentVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			for (Integer contentID : contentVA) {

				for (SelectionType type : contentSelectionManager
						.getSelectionTypes(contentID)) {

					float[] typeColor = type.getColor();
					typeColor[3] = alpha;
					gl.glColor4fv(typeColor, 0);

					if (type == SelectionType.MOUSE_OVER
							|| type == SelectionType.SELECTION) {
						gl.glLineWidth(5);
						break;
					} else {
						gl.glLineWidth(1);
					}
				}

				Vec2f leftPos = leftHeatMapWrapper
						.getRightDetailLinkPositionFromContentID(contentID);

				if (leftPos == null)
					continue;

				Vec2f rightPos = null;
				for (ContentVirtualArray rightVA : rightHeatMapWrapper
						.getContentVAsOfHeatMaps()) {

					rightPos = rightHeatMapWrapper
							.getLeftDetailLinkPositionFromContentID(contentID);

					if (rightPos != null)
						break;
				}

				if (rightPos == null)
					continue;

				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.POLYLINE_SELECTION, contentID));

				ArrayList<Vec3f> points = new ArrayList<Vec3f>();
				points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));

				// Tree<ClusterNode> tree;
				// int nodeID;
				// ClusterNode node;
				// ArrayList<ClusterNode> pathToRoot;
				//
				// // Add spline points for left hierarchy
				// tree = setsToCompare.get(0).getContentTree();
				// nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
				// node = tree.getNodeByNumber(nodeID);
				// pathToRoot = node.getParentPath(tree.getRoot());
				//
				// // Remove last because it is root bundling
				// pathToRoot.remove(pathToRoot.size() - 1);
				//
				// for (ClusterNode pathNode : pathToRoot) {
				// Vec3f nodePos = pathNode.getPos();
				// points.add(nodePos);
				// }
				//
				// // Add spline points for right hierarchy
				// tree = setsToCompare.get(1).getContentTree();
				// nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
				// node = tree.getNodeByNumber(nodeID);
				// pathToRoot = node.getParentPath(tree.getRoot());
				//
				// // Remove last because it is root bundling
				// pathToRoot.remove(pathToRoot.size() - 1);
				//
				// for (ClusterNode pathNode : pathToRoot) {
				// Vec3f nodePos = pathNode.getPos();
				// points.add(nodePos);
				// break; // FIXME: REMOVE BREAK
				// }

				points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

				NURBSCurve curve = new NURBSCurve(points, 30);
				points = curve.getCurvePoints();

				gl.glBegin(GL.GL_LINE_STRIP);
				for (int i = 0; i < points.size(); i++)
					gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
				gl.glEnd();

				gl.glPopName();
			}
		}

	}

	private void renderOverviewRelations(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		// FIXME: Just for testing.
		// leftHeatMapWrapper.useDetailView(false);
		// rightHeatMapWrapper.useDetailView(false);

		float alpha = 0.3f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();
		ContentVirtualArray contentVALeft = setsToCompare.get(0).getContentVA(
				ContentVAType.CONTENT);

		// gl.glColor3f(0, 0, 0);
		for (Integer contentID : contentVALeft) {

			for (SelectionType type : contentSelectionManager
					.getSelectionTypes(contentID)) {

				float[] typeColor = type.getColor();
				typeColor[3] = alpha;
				gl.glColor4fv(typeColor, 0);

				if (type == SelectionType.MOUSE_OVER
						|| type == SelectionType.SELECTION) {
					gl.glLineWidth(5);
					break;
				} else {
					gl.glLineWidth(1);
				}
			}

			Vec2f leftPos = leftHeatMapWrapper
					.getRightOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				continue;

			Vec2f rightPos = rightHeatMapWrapper
					.getLeftOverviewLinkPositionFromContentID(contentID);

			if (rightPos == null)
				continue;

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.POLYLINE_SELECTION, contentID));

			// gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f(leftPos.x()
			// + heatMapLayoutLeft.getOverviewGroupWidth()
			// + heatMapLayoutLeft.getOverviewHeatmapWidth()
			// + heatMapLayoutLeft.getOverviewSliderWidth(), viewFrustum
			// .getHeight()
			// - leftPos.y(), 0);
			// gl.glVertex3f(rightPos.x(), viewFrustum.getHeight() -
			// rightPos.y(),
			// 0);
			// gl.glEnd();

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));

			Tree<ClusterNode> tree;
			int nodeID;
			ClusterNode node;
			ArrayList<ClusterNode> pathToRoot;

			// Add spline points for left hierarchy
			tree = setsToCompare.get(0).getContentTree();
			nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
			node = tree.getNodeByNumber(nodeID);
			pathToRoot = node.getParentPath(tree.getRoot());

			// Remove last because it is root bundling
			pathToRoot.remove(pathToRoot.size() - 1);

			for (ClusterNode pathNode : pathToRoot) {
				Vec3f nodePos = pathNode.getPos();
				points.add(nodePos);
			}

			// Add spline points for right hierarchy
			tree = setsToCompare.get(1).getContentTree();
			nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
			node = tree.getNodeByNumber(nodeID);
			pathToRoot = node.getParentPath(tree.getRoot());

			// Remove last because it is root bundling
			pathToRoot.remove(pathToRoot.size() - 1);

			for (ClusterNode pathNode : pathToRoot) {
				Vec3f nodePos = pathNode.getPos();
				points.add(nodePos);
				break; // FIXME: REMOVE BREAK
			}

			// Center point
			// points.add(new Vec3f(viewFrustum.getWidth() / 2f, viewFrustum
			// .getHeight() / 2f, 0));
			// points.add(new Vec3f(2, 4, 0));
			// points.add(new Vec3f(1,5,0));

			points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

			NURBSCurve curve = new NURBSCurve(points, 30);
			points = curve.getCurvePoints();

			// VisLink.renderLine(gl, controlPoints, offset, numberOfSegments,
			// shadow)

			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < points.size(); i++)
				gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
			gl.glEnd();

			gl.glPopName();
		}
	}

	private void renderOverviewToDetailRelations(GL gl) {
		gl.glColor3f(0, 0, 0);
		gl.glLineWidth(1);
		for (ContentVirtualArray va : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			for (Integer contentID : va) {

				Vec2f leftPos = leftHeatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID);
				if (leftPos == null)
					continue;

				Vec2f rightPos = leftHeatMapWrapper
						.getLeftDetailLinkPositionFromContentID(contentID);
				if (rightPos == null)
					continue;

				ArrayList<Vec3f> points = new ArrayList<Vec3f>();
				points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));

				points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

				NURBSCurve curve = new NURBSCurve(points, 30);
				points = curve.getCurvePoints();

				gl.glBegin(GL.GL_LINE_STRIP);
				for (int i = 0; i < points.size(); i++)
					gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
				gl.glEnd();
			}
		}

		gl.glColor3f(0, 0, 0);
		gl.glLineWidth(1);
		for (ContentVirtualArray va : rightHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			for (Integer contentID : va) {

				Vec2f leftPos = rightHeatMapWrapper
						.getRightDetailLinkPositionFromContentID(contentID);
				if (leftPos == null)
					continue;

				Vec2f rightPos = rightHeatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(contentID);
				if (rightPos == null)
					continue;

				ArrayList<Vec3f> points = new ArrayList<Vec3f>();
				points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));

				points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

				NURBSCurve curve = new NURBSCurve(points, 30);
				points = curve.getCurvePoints();

				gl.glBegin(GL.GL_LINE_STRIP);
				for (int i = 0; i < points.size(); i++)
					gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
				gl.glEnd();
			}
		}
	}

	private void renderTree(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		// Left hierarchy
		xPosInitLeft = heatMapLayoutLeft.getOverviewHeatmapWidth()
				+ heatMapLayoutLeft.getOverviewGroupWidth()
				+ heatMapLayoutLeft.getOverviewSliderWidth();
		yPosInitLeft = viewFrustum.getHeight();
		Tree<ClusterNode> tree = setsToCompare.get(0).getContentTree();
		ClusterNode rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitLeft);

		// Right hierarchy
		xPosInitRight = viewFrustum.getWidth()
				- heatMapLayoutLeft.getOverviewHeatmapWidth()
				- heatMapLayoutLeft.getOverviewGroupWidth()
				- heatMapLayoutLeft.getOverviewSliderWidth();
		yPosInitRight = viewFrustum.getHeight();
		tree = setsToCompare.get(1).getContentTree();
		rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitRight);
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

	/**
	 * Function calculates for each node (gene or entity) in the dendrogram
	 * recursive the corresponding position inside the view frustum
	 * 
	 * @param currentNode
	 *            current node for calculation
	 * @return Vec3f position of the current node
	 */
	private Vec3f determineTreePositions(ClusterNode currentNode,
			Tree<ClusterNode> tree) {

		Vec3f pos = new Vec3f();

		float levelWidth = (viewFrustum.getWidth() / 2f
				- heatMapLayoutLeft.getOverviewSliderWidth()
				- heatMapLayoutLeft.getOverviewSliderWidth() - heatMapLayoutLeft
				.getOverviewHeatmapWidth())
				/ tree.getRoot().getDepth();

		float sampleHeight = viewFrustum.getHeight()
				/ tree.getRoot().getNrLeaves();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = alChilds.get(i);
				positions[i] = determineTreePositions(node, tree);
			}

			float fXmin = Float.MAX_VALUE;
			float fYmax = Float.MIN_VALUE;
			float fYmin = Float.MAX_VALUE;

			for (Vec3f vec : positions) {
				fXmin = Math.min(fXmin, vec.x());
				fYmax = Math.max(fYmax, vec.y());
				fYmin = Math.min(fYmin, vec.y());
			}

			if (tree == setsToCompare.get(0).getContentTree()) {
				pos.setX(fXmin + levelWidth);
			} else {
				pos.setX(fXmin - levelWidth);
			}

			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(DENDROGRAM_Z);

		} else {

			if (tree == setsToCompare.get(0).getContentTree()) {
				pos.setX(xPosInitLeft + levelWidth);
				pos.setY(yPosInitLeft);
				yPosInitLeft -= sampleHeight;
			} else {
				pos.setX(xPosInitRight - levelWidth);
				pos.setY(yPosInitRight);
				yPosInitRight -= sampleHeight;
			}

			pos.setZ(DENDROGRAM_Z);
		}

		currentNode.setPos(pos);

		return pos;
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType = null;

		switch (ePickingType) {
		case COMPARE_EMBEDDED_VIEW_SELECTION:
			if (pickingMode == EPickingMode.RIGHT_CLICKED) {
				contextMenu.setLocation(pick.getPickedPoint(),
						getParentGLCanvas().getWidth(), getParentGLCanvas()
								.getHeight());
				contextMenu.setMasterGLView(this);
			}
			break;
		case POLYLINE_SELECTION:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				ContentContextMenuItemContainer contentContextMenuItemContainer = new ContentContextMenuItemContainer();
				contentContextMenuItemContainer.setID(EIDType.EXPRESSION_INDEX,
						iExternalID);
				contextMenu.addItemContanier(contentContextMenuItemContainer);
				break;

			default:
				return;

			}

			// FIXME: Check if is ok to share the content selection manager
			// of the use case
			ContentSelectionManager contentSelectionManager = useCase
					.getContentSelectionManager();
			if (contentSelectionManager.checkStatus(selectionType, iExternalID)) {
				break;
			}

			contentSelectionManager.clearSelection(selectionType);
			contentSelectionManager.addToType(selectionType, iExternalID);

			ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			event.setInfo(getShortInfoLocal());
			eventPublisher.triggerEvent(event);

			setDisplayListDirty();
			break;

		case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION:
			HeatMapWrapper heatMapWrapper = hashHeatMapWrappers
					.get(iExternalID);
			if (heatMapWrapper != null) {
				heatMapWrapper.handleOverviewSliderSelection(ePickingType,
						pickingMode);
			}
			break;

		case COMPARE_GROUP_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			}

			// leftHeatMapWrapper.handleGroupSelection(selectionType,
			// iExternalID);

			break;
		}
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

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (compareGroupsEventListener != null) {
			eventPublisher.removeListener(compareGroupsEventListener);
			compareGroupsEventListener = null;
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
		clusterState.setAffinityPropClusterFactorGenes(5);
		clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		// clusterState.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);
		// clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		// clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		for (ISet set : sets) {
			set.cluster(clusterState);
		}

		if (sets.size() >= 2) {
			ISet setLeft = setsToCompare.get(0);
			ISet setRight = setsToCompare.get(1);
			// relations = SetComparer.compareSets(setLeft, setRight);
			//
			leftHeatMapWrapper.setSet(setLeft);
			rightHeatMapWrapper.setSet(setRight);
			setDisplayListDirty();
		}
	}
}
