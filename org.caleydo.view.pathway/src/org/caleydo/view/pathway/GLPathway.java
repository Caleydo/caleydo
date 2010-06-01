package org.caleydo.view.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.genetic.IPathwayManager;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ContentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.view.pathway.contextmenu.EmbeddedPathwayContextMenuItemContainer;
import org.caleydo.view.pathway.listener.DisableGeneMappingListener;
import org.caleydo.view.pathway.listener.DisableNeighborhoodListener;
import org.caleydo.view.pathway.listener.DisableTexturesListener;
import org.caleydo.view.pathway.listener.EnableGeneMappingListener;
import org.caleydo.view.pathway.listener.EnableNeighborhoodListener;
import org.caleydo.view.pathway.listener.EnableTexturesListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLPathway extends AGLView implements IDataDomainBasedView<IDataDomain>,
		ISelectionUpdateHandler, IViewCommandHandler, ISelectionCommandHandler {

	public final static String VIEW_ID = "org.caleydo.view.pathway";

	private PathwayGraph pathway;

	private boolean bEnablePathwayTexture = true;

	private IPathwayManager pathwayManager;

	private GLPathwayContentCreator gLPathwayContentCreator;

	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	private SelectionManager selectionManager;

	/**
	 * Own texture manager is needed for each GL context, because textures
	 * cannot be bound to multiple GL contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	int iCurrentStorageIndex = -1;

	// private TextRenderer textRenderer;
	// private boolean bEnableTitleRendering = true;
	// private int iHorizontalTextAlignment = SWT.CENTER;
	// private int iVerticalTextAlignment = SWT.BOTTOM;

	protected EnableTexturesListener enableTexturesListener;
	protected DisableTexturesListener disableTexturesListener;

	protected EnableGeneMappingListener enableGeneMappingListener;
	protected DisableGeneMappingListener disableGeneMappingListener;

	protected EnableNeighborhoodListener enableNeighborhoodListener;
	protected DisableNeighborhoodListener disableNeighborhoodListener;

	protected SelectionUpdateListener selectionUpdateListener;
	protected ContentVAUpdateListener virtualArrayUpdateListener;

	protected ReplaceContentVAListener replaceVirtualArrayListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected SelectionCommandListener selectionCommandListener;

	protected IDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public GLPathway(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, false);
		viewType = VIEW_ID;

		pathwayManager = generalManager.getPathwayManager();

		gLPathwayContentCreator = new GLPathwayContentCreator(viewFrustum, this);
		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();
		// hashPathwayContainingSelectedVertex2VertexCount = new
		// HashMap<Integer, Integer>();

		connectedElementRepresentationManager = generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);

		// initialize internal gene selection manager
		selectionManager = new SelectionManager(EIDType.PATHWAY_VERTEX);

		// textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 24),
		// false);
	}

	public void setPathway(final PathwayGraph pathway) {
		// Unregister former pathway in visibility list
		if (pathway != null) {
			generalManager.getPathwayManager().setPathwayVisibilityState(pathway, false);
		}

		this.pathway = pathway;
	}

	public void setPathway(final int iPathwayID) {

		setPathway(generalManager.getPathwayManager().getItem(iPathwayID));
	}

	public PathwayGraph getPathway() {

		return pathway;
	}

	@Override
	public void initLocal(final GL gl) {
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
		// TODO: individual toolboxrenderer
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void init(final GL gl) {
		// Check if pathway exists or if it's already loaded
		if (!generalManager.getPathwayManager().hasItem(pathway.getID()))
			return;

		initPathwayData(gl);
	}

	@Override
	public void displayLocal(final GL gl) {

		// Check if pathway exists or if it's already loaded
		// FIXME: not good because check in every rendered frame
		if (!generalManager.getPathwayManager().hasItem(pathway.getID()))
			return;

		pickingManager.handlePicking(this, gl);
		if (bIsDisplayListDirtyLocal) {
			rebuildPathwayDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		display(gl);
	}

	@Override
	public void displayRemote(final GL gl) {
		// // Check if pathway exists or if it is already loaded
		// // FIXME: not good because check in every rendered frame
		// if (!generalManager.getPathwayManager().hasItem(pathway.getID()))
		// return;

		if (bIsDisplayListDirtyRemote) {
			rebuildPathwayDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);
	}

	@Override
	public void display(final GL gl) {
		// processEvents();
		checkForHits(gl);

		// TODO: also put this in global DL
		renderPathway(gl, pathway);

		gl.glCallList(iGLDisplayListToCall);
	}

	protected void initPathwayData(final GL gl) {
		// Initialize all elements in selection manager
		Iterator<IGraphItem> iterPathwayVertexGraphItem = pathway.getAllItemsByKind(
				EGraphItemKind.NODE).iterator();
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
		while (iterPathwayVertexGraphItem.hasNext()) {
			tmpPathwayVertexGraphItemRep = (PathwayVertexGraphItemRep) iterPathwayVertexGraphItem
					.next();

			// selectionManager.initialAdd(tmpPathwayVertexGraphItemRep.getId());
		}

		gLPathwayContentCreator.init(gl, selectionManager);

		// Create new pathway manager for GL context
		if (!hashGLcontext2TextureManager.containsKey(gl)) {
			hashGLcontext2TextureManager.put(gl, new GLPathwayTextureManager());
		}

		calculatePathwayScaling(gl, pathway);
		pathwayManager.setPathwayVisibilityState(pathway, true);

		// gLPathwayContentCreator.buildPathwayDisplayList(gl, this,
		// iPathwayID);
	}

	private void renderPathway(final GL gl, final PathwayGraph pathway) {
		gl.glPushMatrix();
		// GLHelperFunctions.drawPointAt(gl, new Vec3f(0,0,0));
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());

		if (bEnablePathwayTexture) {
			float fPathwayTransparency = 1.0f;

			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, pathway,
					fPathwayTransparency, false);
		}

		float tmp = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);

		// FIXME: after view plugin reorganization
		// if (glRemoteRenderingView instanceof IGLRemoteRenderingBucketView
		// && ((IGLRemoteRenderingBucketView)
		// glRemoteRenderingView).getBucketMouseWheelListener() != null) {
		// // if
		// //
		// (remoteRenderingGLCanvas.getHierarchyLayerByGLEventListenerId(iUniqueID)
		// // .getLevel().equals(EHierarchyLevel.UNDER_INTERACTION)
		// // &&
		// //
		// remoteRenderingGLCanvas.getBucketMouseWheelListener().isZoomedIn())
		// if (detailLevel == EDetailLevel.HIGH) {
		// gLPathwayContentCreator.renderPathway(gl, pathway, true);
		// }
		// else {
		// gLPathwayContentCreator.renderPathway(gl, pathway, false);
		// }
		// }
		// else {
		gLPathwayContentCreator.renderPathway(gl, pathway, false);
		// }

		gl.glTranslatef(0, -tmp, 0);

		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

		gl.glPopMatrix();
	}

	private void rebuildPathwayDisplayList(final GL gl, int iGLDisplayListIndex) {
		gLPathwayContentCreator.buildPathwayDisplayList(gl, this, pathway);

		// gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		// renderPathwayName(gl);
		// gl.glEndList();
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
			for (SelectionDeltaItem item : selectionDelta.getAllItems()) {
				if (item.getSelectionType() == SelectionType.MOUSE_OVER) {
					iCurrentStorageIndex = item.getPrimaryID();
					break;
				}
			}
			setDisplayListDirty();

		} else if (selectionDelta.getIDType().getCategory() == EIDCategory.GENE) {

			ISelectionDelta resolvedDelta = resolveExternalSelectionDelta(selectionDelta);
			selectionManager.setDelta(resolvedDelta);

			setDisplayListDirty();

			int iPathwayHeight = pathway.getHeight();
			for (SelectionDeltaItem item : resolvedDelta) {
				if (item.getSelectionType() != SelectionType.MOUSE_OVER
						&& item.getSelectionType() != SelectionType.SELECTION) {
					continue;
				}

				PathwayVertexGraphItemRep vertexRep = (PathwayVertexGraphItemRep) generalManager
						.getPathwayItemManager().getItem(item.getPrimaryID());

				int iViewID = iUniqueID;
				// If rendered remote (hierarchical heat map) - use the remote
				// view ID
				// if (glRemoteRenderingView != null && glRemoteRenderingView
				// instanceof AGLViewBrowser)
				// iViewID = glRemoteRenderingView.getID();

				SelectedElementRep elementRep = new SelectedElementRep(
						EIDType.EXPRESSION_INDEX, iViewID, vertexRep.getXOrigin()
								* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
								+ vecTranslation.x(), (iPathwayHeight - vertexRep
								.getYOrigin())
								* PathwayRenderStyle.SCALING_FACTOR_Y
								* vecScaling.y()
								+ vecTranslation.y(), 0);

				for (Integer iConnectionID : item.getConnectionIDs()) {
					connectedElementRepresentationManager.addSelection(iConnectionID,
							elementRep);
				}
			}
		}
	}

	private ArrayList<Integer> getExpressionIndicesFromPathwayVertexGraphItemRep(
			int iPathwayVertexGraphItemRepID) {

		ArrayList<Integer> alExpressionIndex = new ArrayList<Integer>();

		for (IGraphItem pathwayVertexGraphItem : generalManager.getPathwayItemManager()
				.getItem(iPathwayVertexGraphItemRepID).getAllItemsByProp(
						EGraphItemProperty.ALIAS_PARENT)) {
			int iDavidID = generalManager.getPathwayItemManager()
					.getDavidIdByPathwayVertexGraphItem(
							(PathwayVertexGraphItem) pathwayVertexGraphItem);

			if (iDavidID == -1) {
				continue;
			}

			Set<Integer> iSetRefSeq = idMappingManager.getID(EIDType.DAVID,
					EIDType.REFSEQ_MRNA_INT, iDavidID);

			if (iSetRefSeq == null) {
				generalManager.getLogger().log(
						new Status(IStatus.ERROR, IGeneralManager.PLUGIN_ID,
								"No RefSeq IDs found for David: " + iDavidID));
				continue;
			}

			for (Integer iRefSeqID : iSetRefSeq) {

				Set<Integer> iSetExpressionIndex = idMappingManager.getIDAsSet(
						EIDType.REFSEQ_MRNA_INT, EIDType.EXPRESSION_INDEX, iRefSeqID);
				if (iSetExpressionIndex == null)
					continue;
				alExpressionIndex.addAll(iSetExpressionIndex);
			}
		}

		return alExpressionIndex;
	}

	private ISelectionDelta createExternalSelectionDelta(ISelectionDelta selectionDelta) {
		ISelectionDelta newSelectionDelta = new SelectionDelta(EIDType.EXPRESSION_INDEX);

		for (SelectionDeltaItem item : selectionDelta) {
			for (int iExpressionIndex : getExpressionIndicesFromPathwayVertexGraphItemRep(item
					.getPrimaryID())) {

				SelectionDeltaItem newItem = newSelectionDelta.addSelection(
						iExpressionIndex, item.getSelectionType(), item.getPrimaryID());
				newItem.setRemove(item.isRemove());

				for (Integer iConnectionID : item.getConnectionIDs()) {
					newSelectionDelta.addConnectionID(iExpressionIndex, iConnectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private ISelectionDelta resolveExternalSelectionDelta(ISelectionDelta selectionDelta) {

		ISelectionDelta newSelectionDelta = new SelectionDelta(EIDType.PATHWAY_VERTEX,
				EIDType.DAVID);

		PathwayVertexGraphItem pathwayVertexGraphItem;

		IIDMappingManager idMappingManager = generalManager.getIDMappingManager();

		for (SelectionDeltaItem item : selectionDelta) {

			// FIXME: Due to new mapping system, a mapping involving expression
			// index can return a Set of
			// values, depending on the IDType that has been specified when
			// loading expression data.
			// Possibly a different handling of the Set is required.
			Set<Integer> setIDs = idMappingManager.getIDAsSet(selectionDelta.getIDType(),
					EIDType.DAVID, item.getPrimaryID());

			if (setIDs == null || setIDs.isEmpty()) {
				continue;
				// throw new
				// IllegalStateException("Cannot resolve RefSeq ID to David ID.");
			}
			Integer iDavidID = (Integer) setIDs.toArray()[0];

			pathwayVertexGraphItem = generalManager.getPathwayItemManager()
					.getPathwayVertexGraphItemByDavidId(iDavidID);

			// Ignore David IDs that do not exist in any pathway
			if (pathwayVertexGraphItem == null) {
				continue;
			}

			// Convert DAVID ID to pathway graph item representation ID
			for (IGraphItem tmpGraphItemRep : pathwayVertexGraphItem
					.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {
				if (!pathway.containsItem(tmpGraphItemRep)) {
					continue;
				}

				SelectionDeltaItem newItem = newSelectionDelta.addSelection(
						tmpGraphItemRep.getId(), item.getSelectionType(), iDavidID);
				newItem.setRemove(item.isRemove());
				for (int iConnectionID : item.getConnectionIDs()) {
					newItem.addConnectionID(iConnectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private void calculatePathwayScaling(final GL gl, final PathwayGraph pathway) {

		if (hashGLcontext2TextureManager.get(gl) == null)
			return;

		// // Missing power of two texture GL extension workaround
		// PathwayGraph tmpPathwayGraph =
		// (PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayId);
		// ImageIcon img = new ImageIcon(generalManager.getPathwayManager()
		// .getPathwayDatabaseByType(tmpPathwayGraph.getType()).getImagePath()
		// + tmpPathwayGraph.getImageLink());
		// int iImageWidth = img.getIconWidth();
		// int iImageHeight = img.getIconHeight();
		// tmpPathwayGraph.setWidth(iImageWidth);
		// tmpPathwayGraph.setHeight(iImageHeight);
		// img = null;

		float fPathwayScalingFactor = 0;
		float fPadding = 0.98f;

		if (pathway.getType().equals(EPathwayDatabaseType.BIOCARTA)) {
			fPathwayScalingFactor = 5;
		} else {
			fPathwayScalingFactor = 3.2f;
		}

		int iImageWidth = pathway.getWidth();
		int iImageHeight = pathway.getHeight();

		generalManager.getLogger().log(
				new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID,
						"Pathway texture width=" + iImageWidth + " / height="
								+ iImageHeight));

		if (iImageWidth == -1 || iImageHeight == -1) {
			generalManager
					.getLogger()
					.log(
							new Status(IStatus.ERROR, IGeneralManager.PLUGIN_ID,
									"Problem because pathway texture width or height is invalid!"));
		}

		float fTmpPathwayWidth = iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X
				* fPathwayScalingFactor;
		float fTmpPathwayHeight = iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y
				* fPathwayScalingFactor;

		if (fTmpPathwayWidth > viewFrustum.getRight() - viewFrustum.getLeft()
				&& fTmpPathwayWidth > fTmpPathwayHeight) {
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft())
					/ (iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X) * fPadding);
			vecScaling.setY(vecScaling.x());

			vecTranslation
					.set(
							(viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
									* PathwayRenderStyle.SCALING_FACTOR_X
									* vecScaling.x()) / 2.0f, (viewFrustum.getTop()
									- viewFrustum.getBottom() - iImageHeight
									* PathwayRenderStyle.SCALING_FACTOR_Y
									* vecScaling.y()) / 2.0f, 0);
		} else if (fTmpPathwayHeight > viewFrustum.getTop() - viewFrustum.getBottom()) {
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom())
					/ (iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y) * fPadding);
			vecScaling.setX(vecScaling.y());

			vecTranslation
					.set(
							(viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
									* PathwayRenderStyle.SCALING_FACTOR_X
									* vecScaling.x()) / 2.0f, (viewFrustum.getTop()
									- viewFrustum.getBottom() - iImageHeight
									* PathwayRenderStyle.SCALING_FACTOR_Y
									* vecScaling.y()) / 2.0f, 0);
		} else {
			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f
					- fTmpPathwayWidth / 2.0f, (viewFrustum.getTop() - viewFrustum
					.getBottom())
					/ 2.0f - fTmpPathwayHeight / 2.0f, 0);
		}
	}

	public void setMappingRowCount(final int iMappingRowCount) {
		gLPathwayContentCreator.setMappingRowCount(iMappingRowCount);
	}

	public void enableGeneMapping(final boolean bEnableMapping) {
		gLPathwayContentCreator.enableGeneMapping(bEnableMapping);
		setDisplayListDirty();
	}

	public void enablePathwayTextures(final boolean bEnablePathwayTexture) {
		gLPathwayContentCreator.enableEdgeRendering(!bEnablePathwayTexture);
		setDisplayListDirty();

		this.bEnablePathwayTexture = bEnablePathwayTexture;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		setDisplayListDirty();

		gLPathwayContentCreator.enableNeighborhood(bEnableNeighborhood);
	}

	public void enableIdenticalNodeHighlighting(
			final boolean bEnableIdenticalNodeHighlighting) {
		setDisplayListDirty();

		gLPathwayContentCreator
				.enableIdenticalNodeHighlighting(bEnableIdenticalNodeHighlighting);
	}

	public void enableAnnotation(final boolean bEnableAnnotation) {
		gLPathwayContentCreator.enableAnnotation(bEnableAnnotation);
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		switch (ePickingType) {
		case PATHWAY_ELEMENT_SELECTION:

			SelectionType selectionType;

			PathwayVertexGraphItemRep tmpVertexGraphItemRep = (PathwayVertexGraphItemRep) generalManager
					.getPathwayItemManager().getItem(iExternalID);

			setDisplayListDirty();

			switch (pickingMode) {
			case DOUBLE_CLICKED:
				// same behavior as for single click except that
				// pathways are also loaded
				selectionType = SelectionType.SELECTION;

				// Load embedded pathway
				if (tmpVertexGraphItemRep.getType() == EPathwayVertexType.map) {
					PathwayGraph pathway = generalManager.getPathwayManager()
							.searchPathwayByName(tmpVertexGraphItemRep.getName(),
									EPathwayDatabaseType.KEGG);

					if (pathway != null) {
						LoadPathwayEvent event = new LoadPathwayEvent();
						event.setSender(this);
						event.setPathwayID(pathway.getID());
						eventPublisher.triggerEvent(event);
					}
				} else {

					// // Load pathways
					// for (IGraphItem pathwayVertexGraphItem :
					// tmpVertexGraphItemRep
					// .getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD))
					// {
					//
					// LoadPathwaysByGeneEvent loadPathwaysByGeneEvent =
					// new LoadPathwaysByGeneEvent();
					// loadPathwaysByGeneEvent.setSender(this);
					// loadPathwaysByGeneEvent.setGeneID(pathwayVertexGraphItem.getId());
					// loadPathwaysByGeneEvent.setIdType(EIDType.PATHWAY_VERTEX);
					// generalManager.getEventPublisher().triggerEvent(loadPathwaysByGeneEvent);
					//
					// }
				}
				break;

			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				if (tmpVertexGraphItemRep.getType() == EPathwayVertexType.map) {

					EmbeddedPathwayContextMenuItemContainer pathwayContextMenuItemContainer = new EmbeddedPathwayContextMenuItemContainer();
					pathwayContextMenuItemContainer.setPathway(generalManager
							.getPathwayManager().searchPathwayByName(
									tmpVertexGraphItemRep.getName(),
									EPathwayDatabaseType.KEGG));
					contextMenu.addItemContanier(pathwayContextMenuItemContainer);
				} else if (tmpVertexGraphItemRep.getType() == EPathwayVertexType.gene) {
					for (IGraphItem pathwayVertexGraphItem : tmpVertexGraphItemRep
							.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {

						ContentContextMenuItemContainer geneContextMenuItemContainer = new ContentContextMenuItemContainer();
						geneContextMenuItemContainer.setID(EIDType.DAVID, generalManager
								.getPathwayItemManager()
								.getDavidIdByPathwayVertexGraphItem(
										(PathwayVertexGraphItem) pathwayVertexGraphItem));
						contextMenu.addItemContanier(geneContextMenuItemContainer);
					}
				} else {
					// do nothing if the type is neither a gene nor an
					// embedded pathway
					break;
				}

			default:
				return;
			}

			if (selectionManager.checkStatus(selectionType, iExternalID)) {
				break;
			}

			selectionManager.clearSelection(selectionType);

			SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR,
					selectionType);
			sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

			// Add new vertex to internal selection manager
			selectionManager.addToType(selectionType, tmpVertexGraphItemRep.getId());

			int iConnectionID = generalManager.getIDManager().createID(
					EManagedObjectType.CONNECTION);
			selectionManager
					.addConnectionID(iConnectionID, tmpVertexGraphItemRep.getId());
			connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
			// gLPathwayContentCreator
			// .performIdenticalNodeHighlighting(selectionType);

			createConnectionLines(selectionType, iConnectionID);

			ISelectionDelta selectionDelta = createExternalSelectionDelta(selectionManager
					.getDelta());
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			event.setInfo(getShortInfoLocal());

			eventPublisher.triggerEvent(event);

			break;
		}
	}

	private void createConnectionLines(SelectionType SelectionType, int iConnectionID) {
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep;
		int iPathwayHeight = pathway.getHeight();

		int iViewID = iUniqueID;
		// If rendered remote (hierarchical heat map) - use the remote view ID
		// if (glRemoteRenderingView != null && glRemoteRenderingView instanceof
		// AGLViewBrowser)
		// iViewID = glRemoteRenderingView.getID();

		for (int iVertexRepID : selectionManager.getElements(SelectionType)) {
			tmpPathwayVertexGraphItemRep = generalManager.getPathwayItemManager()
					.getPathwayVertexRep(iVertexRepID);

			SelectedElementRep elementRep = new SelectedElementRep(
					EIDType.EXPRESSION_INDEX, iViewID, tmpPathwayVertexGraphItemRep
							.getXOrigin()
							* PathwayRenderStyle.SCALING_FACTOR_X
							* vecScaling.x()
							+ vecTranslation.x(),
					(iPathwayHeight - tmpPathwayVertexGraphItemRep.getYOrigin())
							* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y()
							+ vecTranslation.y(), 0);

			// for (Integer iConnectionID : selectionManager
			// .getConnectionForElementID(iVertexRepID))
			// {
			connectedElementRepresentationManager.addSelection(iConnectionID, elementRep);
			// }
		}
		// }
	}

	@Override
	public void broadcastElements(EVAOperation type) {

		ContentVADelta delta = new ContentVADelta(ContentVAType.CONTENT_CONTEXT,
				EIDType.REFSEQ_MRNA_INT);
		IIDMappingManager idMappingManager = generalManager.getIDMappingManager();

		for (IGraphItem tmpPathwayVertexGraphItemRep : pathway
				.getAllItemsByKind(EGraphItemKind.NODE)) {
			for (IGraphItem tmpPathwayVertexGraphItem : tmpPathwayVertexGraphItemRep
					.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {
				int iDavidID = generalManager.getPathwayItemManager()
						.getDavidIdByPathwayVertexGraphItem(
								(PathwayVertexGraphItem) tmpPathwayVertexGraphItem);

				if (iDavidID == -1 || iDavidID == 0) {
					// generalManager.getLogger().log(
					// new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					// "Invalid David Gene ID."));
					continue;
				}

				Set<Integer> iSetRefSeq = idMappingManager.getID(EIDType.DAVID,
						EIDType.REFSEQ_MRNA_INT, iDavidID);

				if (iSetRefSeq == null) {

					generalManager.getLogger().log(
							new Status(IStatus.ERROR, IGeneralManager.PLUGIN_ID,
									"No RefSeq IDs found for David: " + iDavidID));
					continue;
				}

				for (Object iRefSeqID : iSetRefSeq) {
					delta.add(VADeltaItem.create(type, (Integer) iRefSeqID));
				}
			}
		}

		ContentVAUpdateEvent virtualArrayUpdateEvent = new ContentVAUpdateEvent();
		virtualArrayUpdateEvent.setSender(this);
		virtualArrayUpdateEvent.setVirtualArrayDelta(delta);
		virtualArrayUpdateEvent.setInfo(getShortInfoLocal());
		eventPublisher.triggerEvent(virtualArrayUpdateEvent);
	}

	@Override
	public String getShortInfo() {
		return pathway.getTitle() + " (" + pathway.getType().getName() + ")";
	}

	@Override
	public String getDetailedInfo() {

		if (isRenderedRemote())
			return (((AGLView) getRemoteRenderingGLCanvas()).getDetailedInfo());

		StringBuffer sInfoText = new StringBuffer();

		sInfoText.append("<b>Pathway</b>\n\n<b>Name:</b> " + pathway.getTitle()
				+ "\n<b>Type:</b> " + pathway.getType().getName());

		// generalManager.getSWTGUIManager().setExternalRCPStatusLineMessage(
		// pathway.getType().getName() + " Pathway: " + sPathwayTitle);

		return sInfoText.toString();
	}

	@Override
	public void initData() {
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		iCurrentStorageIndex = -1;
		super.initData();

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		return selectionManager.getElements(SelectionType).size();
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
		clearAllSelections();
		setDisplayListDirty();
	}

	@Override
	public void destroy() {
		generalManager.getPathwayManager().setPathwayVisibilityState(pathway, false);

		super.destroy();
	}

	@Override
	public void clearAllSelections() {
		selectionManager.clearSelections();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		enableTexturesListener = new EnableTexturesListener();
		enableTexturesListener.setHandler(this);
		eventPublisher.addListener(EnableTexturesEvent.class, enableTexturesListener);

		disableTexturesListener = new DisableTexturesListener();
		disableTexturesListener.setHandler(this);
		eventPublisher.addListener(DisableTexturesEvent.class, disableTexturesListener);

		enableNeighborhoodListener = new EnableNeighborhoodListener();
		enableNeighborhoodListener.setHandler(this);
		eventPublisher.addListener(EnableNeighborhoodEvent.class,
				enableNeighborhoodListener);

		disableNeighborhoodListener = new DisableNeighborhoodListener();
		disableNeighborhoodListener.setHandler(this);
		eventPublisher.addListener(DisableNeighborhoodEvent.class,
				disableNeighborhoodListener);

		enableGeneMappingListener = new EnableGeneMappingListener();
		enableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(EnableGeneMappingEvent.class,
				enableGeneMappingListener);

		disableGeneMappingListener = new DisableGeneMappingListener();
		disableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(DisableGeneMappingEvent.class,
				disableGeneMappingListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		// virtualArrayUpdateListener = new ContentVAUpdateListener();
		// virtualArrayUpdateListener.setHandler(this);
		// eventPublisher.addListener(VirtualArrayUpdateEvent.class,
		// virtualArrayUpdateListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		// replaceVirtualArrayListener = new ReplaceContentVAListener();
		// replaceVirtualArrayListener.setHandler(this);
		// eventPublisher.addListener(ReplaceVAEvent.class,
		// replaceVirtualArrayListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (enableTexturesListener != null) {
			eventPublisher.removeListener(EnableTexturesEvent.class,
					enableTexturesListener);
			enableTexturesListener = null;
		}
		if (disableTexturesListener != null) {
			eventPublisher.removeListener(DisableTexturesEvent.class,
					disableTexturesListener);
			disableTexturesListener = null;
		}
		if (enableNeighborhoodListener != null) {
			eventPublisher.removeListener(EnableNeighborhoodEvent.class,
					enableNeighborhoodListener);
			enableNeighborhoodListener = null;
		}
		if (disableNeighborhoodListener != null) {
			eventPublisher.removeListener(DisableNeighborhoodEvent.class,
					disableNeighborhoodListener);
			disableNeighborhoodListener = null;
		}
		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(EnableGeneMappingEvent.class,
					enableGeneMappingListener);
			enableGeneMappingListener = null;
		}
		if (disableGeneMappingListener != null) {
			eventPublisher.removeListener(DisableGeneMappingEvent.class,
					disableGeneMappingListener);
			disableGeneMappingListener = null;
		}
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (replaceVirtualArrayListener != null) {
			eventPublisher.removeListener(replaceVirtualArrayListener);
			replaceVirtualArrayListener = null;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedPathwayView serializedForm = new SerializedPathwayView(dataDomain
				.getDataDomainType());
		serializedForm.setViewID(this.getID());
		serializedForm.setPathwayID(pathway.getID());
		return serializedForm;
	}

	@Override
	public void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand) {
		if (EIDCategory.GENE == category)
			selectionManager.executeSelectionCommand(selectionCommand);

	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;

	}

}