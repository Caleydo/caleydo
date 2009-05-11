package org.caleydo.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.event.view.storagebased.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.genetic.IPathwayManager;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.IVirtualArrayUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.VirtualArrayUpdateListener;
import org.caleydo.core.view.opengl.canvas.pathway.listeners.DisableGeneMappingListener;
import org.caleydo.core.view.opengl.canvas.pathway.listeners.DisableNeighborhoodListener;
import org.caleydo.core.view.opengl.canvas.pathway.listeners.DisableTexturesListener;
import org.caleydo.core.view.opengl.canvas.pathway.listeners.EnableGeneMappingListener;
import org.caleydo.core.view.opengl.canvas.pathway.listeners.EnableNeighborhoodListener;
import org.caleydo.core.view.opengl.canvas.pathway.listeners.EnableTexturesListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.eclipse.core.runtime.Status;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLPathway
	extends AGLEventListener
	implements ISelectionUpdateHandler, IVirtualArrayUpdateHandler, IViewCommandHandler {

	private PathwayGraph pathway;

	private boolean bEnablePathwayTexture = true;

	private IPathwayManager pathwayManager;

	private GLPathwayContentCreator gLPathwayContentCreator;

	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	private GenericSelectionManager selectionManager;

	/**
	 * Own texture manager is needed for each GL context, because textures cannot be bound to multiple GL
	 * contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	int iCurrentStorageIndex = -1;

	// private TextRenderer textRenderer;
	// private boolean bEnableTitleRendering = true;
	// private int iHorizontalTextAlignment = SWT.CENTER;
	// private int iVerticalTextAlignment = SWT.BOTTOM;

	protected EnableTexturesListener enableTexturesListener = null;
	protected DisableTexturesListener disableTexturesListener = null;

	protected EnableGeneMappingListener enableGeneMappingListener = null;
	protected DisableGeneMappingListener disableGeneMappingListener = null;

	protected EnableNeighborhoodListener enableNeighborhoodListener = null;
	protected DisableNeighborhoodListener disableNeighborhoodListener = null;

	protected SelectionUpdateListener selectionUpdateListener = null;
	protected VirtualArrayUpdateListener virtualArrayUpdateListener = null;

	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	/**
	 * Constructor.
	 */
	public GLPathway(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, false);
		viewType = EManagedObjectType.GL_PATHWAY;
		pathwayManager = generalManager.getPathwayManager();

		gLPathwayContentCreator = new GLPathwayContentCreator(viewFrustum, this);
		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();
		// hashPathwayContainingSelectedVertex2VertexCount = new
		// HashMap<Integer, Integer>();

		connectedElementRepresentationManager =
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);

		// initialize internal gene selection manager
		ArrayList<ESelectionType> alSelectionType = new ArrayList<ESelectionType>();
		for (ESelectionType selectionType : ESelectionType.values()) {
			alSelectionType.add(selectionType);
		}

		selectionManager = new GenericSelectionManager.Builder(EIDType.PATHWAY_VERTEX).build();

		// textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 24),
		// false);
	}

	public synchronized void setPathway(final PathwayGraph pathway) {
		// Unregister former pathway in visibility list
		if (pathway != null) {
			generalManager.getPathwayManager().setPathwayVisibilityState(pathway, false);
		}

		this.pathway = pathway;
	}

	public synchronized void setPathway(final int iPathwayID) {

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
	public void init(final GL gl) {
		// Check if pathway exists or if it's already loaded
		if (!generalManager.getPathwayManager().hasItem(pathway.getID()))
			return;

		initPathwayData(gl);
	}

	@Override
	public synchronized void displayLocal(final GL gl) {
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
	public synchronized void displayRemote(final GL gl) {
		// Check if pathway exists or if it is already loaded
		// FIXME: not good because check in every rendered frame
		if (!generalManager.getPathwayManager().hasItem(pathway.getID()))
			return;

		if (bIsDisplayListDirtyRemote) {
			rebuildPathwayDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);
	}

	@Override
	public synchronized void display(final GL gl) {
		checkForHits(gl);

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		// TODO: also put this in global DL
		renderPathway(gl, pathway);

		gl.glCallList(iGLDisplayListToCall);
	}

	protected void initPathwayData(final GL gl) {
		// Initialize all elements in selection manager
		Iterator<IGraphItem> iterPathwayVertexGraphItem =
			pathway.getAllItemsByKind(EGraphItemKind.NODE).iterator();
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
		while (iterPathwayVertexGraphItem.hasNext()) {
			tmpPathwayVertexGraphItemRep = (PathwayVertexGraphItemRep) iterPathwayVertexGraphItem.next();

			selectionManager.initialAdd(tmpPathwayVertexGraphItemRep.getId());
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
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());

		if (bEnablePathwayTexture) {
			float fPathwayTransparency = 1.0f;

			hashGLcontext2TextureManager.get(gl)
				.renderPathway(gl, this, pathway, fPathwayTransparency, false);
		}

		float tmp = PathwayRenderStyle.SCALING_FACTOR_Y * pathway.getHeight();

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);

		if (remoteRenderingGLView.getBucketMouseWheelListener() != null) {
			// if
			// (remoteRenderingGLCanvas.getHierarchyLayerByGLEventListenerId(iUniqueID)
			// .getLevel().equals(EHierarchyLevel.UNDER_INTERACTION)
			// &&
			// remoteRenderingGLCanvas.getBucketMouseWheelListener().isZoomedIn())
			if (detailLevel == EDetailLevel.HIGH) {
				gLPathwayContentCreator.renderPathway(gl, pathway, true);
			}
			else {
				gLPathwayContentCreator.renderPathway(gl, pathway, false);
			}
		}
		else {
			gLPathwayContentCreator.renderPathway(gl, pathway, false);
		}

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
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
			for (SelectionDeltaItem item : selectionDelta.getAllItems()) {
				if (item.getSelectionType() == ESelectionType.MOUSE_OVER) {
					iCurrentStorageIndex = item.getPrimaryID();
					break;
				}
			}
		}
		else if (selectionDelta.getIDType() != EIDType.EXPRESSION_INDEX)
			return;

		ISelectionDelta resolvedDelta = resolveExternalSelectionDelta(selectionDelta);
		selectionManager.setDelta(resolvedDelta);

		setDisplayListDirty();

		int iPathwayHeight = pathway.getHeight();
		for (SelectionDeltaItem item : resolvedDelta) {
			if (item.getSelectionType() != ESelectionType.MOUSE_OVER
				&& item.getSelectionType() != ESelectionType.SELECTION) {
				continue;
			}

			PathwayVertexGraphItemRep vertexRep =
				(PathwayVertexGraphItemRep) generalManager.getPathwayItemManager().getItem(
					item.getPrimaryID());

			SelectedElementRep elementRep =
				new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, vertexRep.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x() + vecTranslation.x(),
					(iPathwayHeight - vertexRep.getYOrigin()) * PathwayRenderStyle.SCALING_FACTOR_Y
						* vecScaling.y() + vecTranslation.y(), 0);

			for (Integer iConnectionID : item.getConnectionID()) {
				connectedElementRepresentationManager.addSelection(iConnectionID, elementRep);
			}
		}
	}

	private ArrayList<Integer> getExpressionIndicesFromPathwayVertexGraphItemRep(
		int iPathwayVertexGraphItemRepID) {

		ArrayList<Integer> alExpressionIndex = new ArrayList<Integer>();

		for (IGraphItem pathwayVertexGraphItem : generalManager.getPathwayItemManager().getItem(
			iPathwayVertexGraphItemRepID).getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {
			int iDavidID =
				generalManager.getPathwayItemManager().getDavidIdByPathwayVertexGraphItemId(
					pathwayVertexGraphItem.getId());

			if (iDavidID == -1) {
				continue;
			}

			Set<Integer> iSetRefSeq =
				idMappingManager.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT, iDavidID);

			if (iSetRefSeq == null) {
				generalManager.getLogger().log(
					new Status(Status.ERROR, GeneralManager.PLUGIN_ID, "No RefSeq IDs found for David: "
						+ iDavidID));
				continue;
			}

			for (Integer iRefSeqID : iSetRefSeq) {

				Set<Integer> iSetExpressionIndex =
					idMappingManager.getMultiID(EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID);
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
			for (int iExpressionIndex : getExpressionIndicesFromPathwayVertexGraphItemRep(item.getPrimaryID())) {
				newSelectionDelta.addSelection((Integer) iExpressionIndex, item.getSelectionType(), item
					.getPrimaryID());

				for (Integer iConnectionID : item.getConnectionID()) {
					newSelectionDelta.addConnectionID((Integer) iExpressionIndex, iConnectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private ISelectionDelta resolveExternalSelectionDelta(ISelectionDelta selectionDelta) {
		ISelectionDelta newSelectionDelta =
			new SelectionDelta(EIDType.PATHWAY_VERTEX, EIDType.EXPRESSION_INDEX);

		int iPathwayVertexGraphItemID = 0;

		IIDMappingManager idMappingManager = generalManager.getIDMappingManager();

		for (SelectionDeltaItem item : selectionDelta) {

			int iExpressionIndex = item.getPrimaryID();
			Integer iRefSeqID =
				idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iExpressionIndex);

			if (iRefSeqID == null) {
				continue;
				// throw new IllegalStateException("Cannot resolve Expression Index to RefSeq ID.");
			}

			Integer iDavidID = idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, iRefSeqID);

			if (iDavidID == null) {
				continue;
//				throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");				
			}

			iPathwayVertexGraphItemID =
				generalManager.getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidID);

			// Ignore David IDs that do not exist in any pathway
			if (iPathwayVertexGraphItemID == -1) {
				continue;
			}

			// Convert DAVID ID to pathway graph item representation ID
			for (IGraphItem tmpGraphItemRep : generalManager.getPathwayItemManager().getItem(
				iPathwayVertexGraphItemID).getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {
				if (!pathway.containsItem(tmpGraphItemRep)) {
					continue;
				}

				SelectionDeltaItem newItem =
					newSelectionDelta
						.addSelection(tmpGraphItemRep.getId(), item.getSelectionType(), iDavidID);
				for (int iConnectionID : item.getConnectionID()) {
					newItem.setConnectionID(iConnectionID);
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
		}
		else {
			fPathwayScalingFactor = 3.2f;
		}

		int iImageWidth = pathway.getWidth();
		int iImageHeight = pathway.getHeight();

		generalManager.getLogger().log(
			new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Pathway texture width=" + iImageWidth
				+ " / height=" + iImageHeight));

		if (iImageWidth == -1 || iImageHeight == -1) {
			generalManager.getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID,
					"Problem because pathway texture width or height is invalid!"));
		}

		float fTmpPathwayWidth = iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X * fPathwayScalingFactor;
		float fTmpPathwayHeight = iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y * fPathwayScalingFactor;

		if (fTmpPathwayWidth > viewFrustum.getRight() - viewFrustum.getLeft()
			&& fTmpPathwayWidth > fTmpPathwayHeight) {
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft())
				/ (iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X) * fPadding);
			vecScaling.setY(vecScaling.x());

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
				* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, (viewFrustum.getTop()
				- viewFrustum.getBottom() - iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y
				* vecScaling.y()) / 2.0f, 0);
		}
		else if (fTmpPathwayHeight > viewFrustum.getTop() - viewFrustum.getBottom()) {
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom())
				/ (iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y) * fPadding);
			vecScaling.setX(vecScaling.y());

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
				* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, (viewFrustum.getTop()
				- viewFrustum.getBottom() - iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y
				* vecScaling.y()) / 2.0f, 0);
		}
		else {
			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);

			vecTranslation
				.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f - fTmpPathwayWidth / 2.0f,
					(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f - fTmpPathwayHeight / 2.0f, 0);
		}
	}

	public synchronized void setMappingRowCount(final int iMappingRowCount) {
		gLPathwayContentCreator.setMappingRowCount(iMappingRowCount);
	}

	public synchronized void enableGeneMapping(final boolean bEnableMapping) {
		gLPathwayContentCreator.enableGeneMapping(bEnableMapping);
		setDisplayListDirty();
	}

	public synchronized void enablePathwayTextures(final boolean bEnablePathwayTexture) {
		gLPathwayContentCreator.enableEdgeRendering(!bEnablePathwayTexture);
		setDisplayListDirty();

		this.bEnablePathwayTexture = bEnablePathwayTexture;
	}

	public synchronized void enableNeighborhood(final boolean bEnableNeighborhood) {
		setDisplayListDirty();

		gLPathwayContentCreator.enableNeighborhood(bEnableNeighborhood);
	}

	public synchronized void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting) {
		setDisplayListDirty();

		gLPathwayContentCreator.enableIdenticalNodeHighlighting(bEnableIdenticalNodeHighlighting);
	}

	public synchronized void enableAnnotation(final boolean bEnableAnnotation) {
		gLPathwayContentCreator.enableAnnotation(bEnableAnnotation);
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		switch (ePickingType) {
			case PATHWAY_ELEMENT_SELECTION:

				ESelectionType eSelectionType;

				PathwayVertexGraphItemRep tmpVertexGraphItemRep =
					(PathwayVertexGraphItemRep) generalManager.getPathwayItemManager().getItem(iExternalID);

				// Do nothing if new selection is the same as previous selection
				// if ((selectionManager.checkStatus(ESelectionType.MOUSE_OVER,
				// tmpVertexGraphItemRep.getId()) && pickingMode
				// .equals(EPickingMode.MOUSE_OVER))
				// || (selectionManager.checkStatus(ESelectionType.SELECTION,
				// tmpVertexGraphItemRep.getId()) && pickingMode
				// .equals(EPickingMode.CLICKED))
				// || (selectionManager.checkStatus(ESelectionType.SELECTION,
				// tmpVertexGraphItemRep.getId()) && pickingMode
				// .equals(EPickingMode.DOUBLE_CLICKED)))
				// {
				// pickingManager.flushHits(iUniqueID, ePickingType);
				// return;
				// }

				setDisplayListDirty();

				selectionManager.clearSelection(ESelectionType.NEIGHBORHOOD_1);
				selectionManager.clearSelection(ESelectionType.NEIGHBORHOOD_2);
				selectionManager.clearSelection(ESelectionType.NEIGHBORHOOD_3);

				switch (pickingMode) {
					case DOUBLE_CLICKED:
						// same behavior as for single click except that
						// pathways are also loaded
						eSelectionType = ESelectionType.SELECTION;

						// Load embedded pathway
						if (tmpVertexGraphItemRep.getType() == EPathwayVertexType.map) {
							PathwayGraph pathway =
								generalManager.getPathwayManager().searchPathwayByName(
									tmpVertexGraphItemRep.getName(), EPathwayDatabaseType.KEGG);

							if (pathway != null) {
								LoadPathwayEvent event = new LoadPathwayEvent();
								event.setSender(this);
								event.setPathwayID(pathway.getID());
								eventPublisher.triggerEvent(event);
							}
						}
						else {

							// Load pathways
							for (IGraphItem pathwayVertexGraphItem : tmpVertexGraphItemRep
								.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {

								LoadPathwaysByGeneEvent loadPathwaysByGeneEvent =
									new LoadPathwaysByGeneEvent();
								loadPathwaysByGeneEvent.setSender(this);
								loadPathwaysByGeneEvent.setGeneID(pathwayVertexGraphItem.getId());
								loadPathwaysByGeneEvent.setIdType(EIDType.PATHWAY_VERTEX);
								generalManager.getEventPublisher().triggerEvent(loadPathwaysByGeneEvent);

							}

							// ArrayList<Integer> alExpressionIndexID =
							// getExpressionIndicesFromPathwayVertexGraphItemRep(tmpVertexGraphItemRep.getID());
							//
							// for (int iRefSeqID : alExpressionIndexID) {
							// LoadPathwaysByGeneEvent loadPathwaysByGeneEvent =
							// new LoadPathwaysByGeneEvent();
							// loadPathwaysByGeneEvent.setGeneID(iRefSeqID);
							// loadPathwaysByGeneEvent.setIdType(EIDType.PATHWAY_VERTEX);
							// generalManager.getEventPublisher().triggerEvent(loadPathwaysByGeneEvent);
							// }
						}
						break;

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED:
						eSelectionType = ESelectionType.SELECTION;

						// for (IGraphItem pathwayVertexGraphItem : tmpVertexGraphItemRep
						// .getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {
						//							
						// GeneContextMenuItemContainer geneContextMenuItemContainer =
						// new GeneContextMenuItemContainer(pathwayVertexGraphItem.getId());
						// contextMenu.addItemContanier(geneContextMenuItemContainer);
						// }

					default:
						return;
				}

				if (selectionManager.checkStatus(eSelectionType, iExternalID)) {
					break;
				}

				selectionManager.clearSelection(eSelectionType);

				// Add new vertex to internal selection manager
				selectionManager.addToType(eSelectionType, tmpVertexGraphItemRep.getId());

				int iConnectionID = generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);
				selectionManager.addConnectionID(iConnectionID, tmpVertexGraphItemRep.getId());
				connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
				gLPathwayContentCreator.performIdenticalNodeHighlighting(eSelectionType);

				createConnectionLines(eSelectionType, iConnectionID);

				SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType);
				sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

				ISelectionDelta selectionDelta = createExternalSelectionDelta(selectionManager.getDelta());
				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setSender(this);
				event.setSelectionDelta(selectionDelta);
				event.setInfo(getShortInfo());
				eventPublisher.triggerEvent(event);

				break;
		}
	}

	private void createConnectionLines(ESelectionType eSelectionType, int iConnectionID) {
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep;
		int iPathwayHeight = pathway.getHeight();

		for (int iVertexRepID : selectionManager.getElements(eSelectionType)) {
			tmpPathwayVertexGraphItemRep =
				generalManager.getPathwayItemManager().getPathwayVertexRep(iVertexRepID);

			SelectedElementRep elementRep =
				new SelectedElementRep(EIDType.EXPRESSION_INDEX, this.getID(), tmpPathwayVertexGraphItemRep
					.getXOrigin()
					* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x() + vecTranslation.x(),
					(iPathwayHeight - tmpPathwayVertexGraphItemRep.getYOrigin())
						* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y() + vecTranslation.y(), 0);

			// for (Integer iConnectionID : selectionManager
			// .getConnectionForElementID(iVertexRepID))
			// {
			connectedElementRepresentationManager.addSelection(iConnectionID, elementRep);
			// }
		}
		// }
	}

	@Override
	public synchronized void broadcastElements(EVAOperation type) {

		IVirtualArrayDelta delta = new VirtualArrayDelta(EIDType.REFSEQ_MRNA_INT);
		IIDMappingManager idMappingManager = generalManager.getIDMappingManager();

		for (IGraphItem tmpPathwayVertexGraphItemRep : pathway.getAllItemsByKind(EGraphItemKind.NODE)) {
			for (IGraphItem tmpPathwayVertexGraphItem : tmpPathwayVertexGraphItemRep
				.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {
				int iDavidID =
					generalManager.getPathwayItemManager().getDavidIdByPathwayVertexGraphItemId(
						tmpPathwayVertexGraphItem.getId());

				if (iDavidID == -1 || iDavidID == 0) {
					generalManager.getLogger().log(
						new Status(Status.WARNING, GeneralManager.PLUGIN_ID, "Invalid David Gene ID."));
					continue;
				}

				Set<Integer> iSetRefSeq =
					idMappingManager.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT, iDavidID);

				if (iSetRefSeq == null) {

					generalManager.getLogger().log(
						new Status(Status.ERROR, GeneralManager.PLUGIN_ID, "No RefSeq IDs found for David: "
							+ iDavidID));
					continue;
				}

				for (Object iRefSeqID : iSetRefSeq) {
					delta.add(VADeltaItem.create(type, (Integer) iRefSeqID));
				}
			}
		}

		VirtualArrayUpdateEvent virtualArrayUpdateEvent = new VirtualArrayUpdateEvent();
		virtualArrayUpdateEvent.setSender(this);
		virtualArrayUpdateEvent.setVirtualArrayDelta(delta);
		virtualArrayUpdateEvent.setInfo(getShortInfo());
		eventPublisher.triggerEvent(virtualArrayUpdateEvent);
	}

	@Override
	public synchronized String getShortInfo() {

		return pathway.getTitle() + " (" + pathway.getType().getName() + ")";
	}

	@Override
	public synchronized String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();

		sInfoText.append("<b>Pathway</b>\n\n<b>Name:</b> " + pathway.getTitle() + "\n<b>Type:</b> "
			+ pathway.getType().getName());

		// generalManager.getSWTGUIManager().setExternalRCPStatusLineMessage(
		// pathway.getType().getName() + " Pathway: " + sPathwayTitle);

		return sInfoText.toString();
	}

	@Override
	public synchronized void setSet(ISet set) {
		super.setSet(set);
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		return selectionManager.getElements(eSelectionType).size();
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
	public void handleVirtualArrayUpdate(IVirtualArrayDelta delta, String info) {
		selectionManager.setVADelta(delta);
		// reactOnExternalSelection();
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
		IEventPublisher eventPublisher = generalManager.getEventPublisher();

		enableTexturesListener = new EnableTexturesListener();
		enableTexturesListener.setHandler(this);
		eventPublisher.addListener(EnableTexturesEvent.class, enableTexturesListener);

		disableTexturesListener = new DisableTexturesListener();
		disableTexturesListener.setHandler(this);
		eventPublisher.addListener(DisableTexturesEvent.class, disableTexturesListener);

		enableNeighborhoodListener = new EnableNeighborhoodListener();
		enableNeighborhoodListener.setHandler(this);
		eventPublisher.addListener(EnableNeighborhoodEvent.class, enableNeighborhoodListener);

		disableNeighborhoodListener = new DisableNeighborhoodListener();
		disableNeighborhoodListener.setHandler(this);
		eventPublisher.addListener(DisableNeighborhoodEvent.class, disableNeighborhoodListener);

		enableGeneMappingListener = new EnableGeneMappingListener();
		enableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(EnableGeneMappingEvent.class, enableGeneMappingListener);

		disableGeneMappingListener = new DisableGeneMappingListener();
		disableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(DisableGeneMappingEvent.class, disableGeneMappingListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new VirtualArrayUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		IEventPublisher eventPublisher = generalManager.getEventPublisher();

		if (enableTexturesListener != null) {
			eventPublisher.removeListener(EnableTexturesEvent.class, enableTexturesListener);
			enableTexturesListener = null;
		}
		if (disableTexturesListener != null) {
			eventPublisher.removeListener(DisableTexturesEvent.class, disableTexturesListener);
			disableTexturesListener = null;
		}
		if (enableNeighborhoodListener != null) {
			eventPublisher.removeListener(EnableNeighborhoodEvent.class, enableNeighborhoodListener);
			enableNeighborhoodListener = null;
		}
		if (disableNeighborhoodListener != null) {
			eventPublisher.removeListener(DisableNeighborhoodEvent.class, disableNeighborhoodListener);
			disableNeighborhoodListener = null;
		}
		if (enableGeneMappingListener != null) {
			eventPublisher.removeListener(EnableGeneMappingEvent.class, enableGeneMappingListener);
			enableGeneMappingListener = null;
		}
		if (disableGeneMappingListener != null) {
			eventPublisher.removeListener(DisableGeneMappingEvent.class, disableGeneMappingListener);
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
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

}