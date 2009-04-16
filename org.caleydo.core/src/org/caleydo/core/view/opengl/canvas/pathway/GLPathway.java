package org.caleydo.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.EPathwayVertexType;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.data.selection.VADeltaItem;
import org.caleydo.core.data.selection.VirtualArrayDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.EViewCommand;
import org.caleydo.core.manager.event.IDListEventContainer;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.ViewCommandEventContainer;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingMouseListener;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLPathway
	extends AGLEventListener
	implements IMediatorReceiver, IMediatorSender {
	private int iPathwayID = -1;

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

	EnableTexturesListener enableTexturesListener = null;
	DisableTexturesListener disableTexturesListener = null;
	
	/**
	 * Constructor.
	 */
	public GLPathway(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum) {
		super(iGLCanvasID, sLabel, viewFrustum, false);
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

		registerEventListeners();
		
		// textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 24),
		// false);
	}

	public synchronized void setPathwayID(final int iPathwayID) {
		// Unregister former pathway in visibility list
		if (iPathwayID != -1) {
			generalManager.getPathwayManager().setPathwayVisibilityStateByID(this.iPathwayID, false);
		}

		this.iPathwayID = iPathwayID;
	}

	public int getPathwayID() {

		return iPathwayID;
	}

	@Override
	public void initLocal(final GL gl) {
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
		// TODO: individual toolboxrenderer
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
		final PickingMouseListener pickingTriggerMouseAdapter,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas) {
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void init(final GL gl) {
		// Check if pathway exists or if it's already loaded
		if (!generalManager.getPathwayManager().hasItem(iPathwayID))
			return;

		initPathwayData(gl);
	}

	@Override
	public synchronized void displayLocal(final GL gl) {
		// Check if pathway exists or if it's already loaded
		// FIXME: not good because check in every rendered frame
		if (!generalManager.getPathwayManager().hasItem(iPathwayID))
			return;

		pickingManager.handlePicking(iUniqueID, gl);
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
		if (!generalManager.getPathwayManager().hasItem(iPathwayID))
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
		renderPathwayById(gl, iPathwayID);

		gl.glCallList(iGLDisplayListToCall);
	}

	protected void initPathwayData(final GL gl) {
		// Initialize all elements in selection manager
		Iterator<IGraphItem> iterPathwayVertexGraphItem =
			generalManager.getPathwayManager().getItem(iPathwayID).getAllItemsByKind(EGraphItemKind.NODE)
				.iterator();
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
		while (iterPathwayVertexGraphItem.hasNext()) {
			tmpPathwayVertexGraphItemRep = (PathwayVertexGraphItemRep) iterPathwayVertexGraphItem.next();

			selectionManager.initialAdd(tmpPathwayVertexGraphItemRep.getId());
		}

		gLPathwayContentCreator.init(gl, alSets, selectionManager);

		// Create new pathway manager for GL context
		if (!hashGLcontext2TextureManager.containsKey(gl)) {
			hashGLcontext2TextureManager.put(gl, new GLPathwayTextureManager());
		}

		calculatePathwayScaling(gl, iPathwayID);
		pathwayManager.setPathwayVisibilityStateByID(iPathwayID, true);

		// gLPathwayContentCreator.buildPathwayDisplayList(gl, this,
		// iPathwayID);
	}

	private void renderPathwayById(final GL gl, final int iPathwayId) {
		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());

		if (bEnablePathwayTexture) {
			float fPathwayTransparency = 1.0f;

			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, iPathwayId, fPathwayTransparency,
				false);
		}

		float tmp = PathwayRenderStyle.SCALING_FACTOR_Y * pathwayManager.getItem(iPathwayId).getHeight();

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);

		if (remoteRenderingGLCanvas.getBucketMouseWheelListener() != null) {
			// if
			// (remoteRenderingGLCanvas.getHierarchyLayerByGLEventListenerId(iUniqueID)
			// .getLevel().equals(EHierarchyLevel.UNDER_INTERACTION)
			// &&
			// remoteRenderingGLCanvas.getBucketMouseWheelListener().isZoomedIn())
			if (detailLevel == EDetailLevel.HIGH) {
				gLPathwayContentCreator.renderPathway(gl, iPathwayId, true);
			}
			else {
				gLPathwayContentCreator.renderPathway(gl, iPathwayId, false);
			}
		}
		else {
			gLPathwayContentCreator.renderPathway(gl, iPathwayId, false);
		}

		gl.glTranslatef(0, -tmp, 0);

		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

		gl.glPopMatrix();
	}

	private void rebuildPathwayDisplayList(final GL gl, int iGLDisplayListIndex) {
		gLPathwayContentCreator.buildPathwayDisplayList(gl, this, iPathwayID);

		// gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		// renderPathwayName(gl);
		// gl.glEndList();
	}

	private void handleSelectionUpdate(IMediatorSender eventTrigger, ISelectionDelta selectionDelta) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX)
		{
			for (SelectionDeltaItem item : selectionDelta.getAllItems())
			{
				if (item.getSelectionType() == ESelectionType.MOUSE_OVER)
				{
					iCurrentStorageIndex = item.getPrimaryID();
					break;
				}
			}
		}
		else if (selectionDelta.getIDType() != EIDType.REFSEQ_MRNA_INT)
			return;

		ISelectionDelta resolvedDelta = resolveExternalSelectionDelta(selectionDelta);
		selectionManager.setDelta(resolvedDelta);

		setDisplayListDirty();

		int iPathwayHeight = generalManager.getPathwayManager().getItem(iPathwayID).getHeight();
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

	private ArrayList<Integer> getRefSeqIDsFromPathwayVertexGraphItemRep(int iPathwayVertexGraphItemRepID) {
		ArrayList<Integer> alRefSeqID = new ArrayList<Integer>();

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
				generalManager.getLogger().log(Level.SEVERE, "No RefSeq IDs found for David: " + iDavidID);
				continue;
			}

			for (int iRefSeqID : iSetRefSeq) {
				alRefSeqID.add(iRefSeqID);
			}
		}

		return alRefSeqID;
	}

	// private ArrayList<Integer> getRefSeqFromPathwayVertexGraphItemRep(
	// int iPathwayVertexGraphItemRepID)
	// {
	// ArrayList<Integer> alRefSeq = new ArrayList<Integer>();
	//
	// for (IGraphItem pathwayVertexGraphItem :
	// generalManager.getPathwayItemManager()
	// .getItem(iPathwayVertexGraphItemRepID).getAllItemsByProp(
	// EGraphItemProperty.ALIAS_PARENT))
	// {
	// int iRefSeqID = generalManager.getPathwayItemManager()
	// .getDavidIdByPathwayVertexGraphItemId(pathwayVertexGraphItem.getId());
	//
	// if (iDavidID == -1)
	// continue;
	//
	// alRefSeq.add(iDavidID);
	// }
	//
	// return alRefSeq;
	// }

	private ISelectionDelta createExternalSelectionDelta(ISelectionDelta selectionDelta) {
		ISelectionDelta newSelectionDelta = new SelectionDelta(EIDType.REFSEQ_MRNA_INT);

		for (SelectionDeltaItem item : selectionDelta) {
			for (int iRefSeqID : getRefSeqIDsFromPathwayVertexGraphItemRep(item.getPrimaryID())) {
				newSelectionDelta.addSelection((Integer) iRefSeqID, item.getSelectionType(), item
					.getPrimaryID());

				for (Integer iConnectionID : item.getConnectionID()) {
					newSelectionDelta.addConnectionID((Integer) iRefSeqID, iConnectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private ISelectionDelta resolveExternalSelectionDelta(ISelectionDelta selectionDelta) {
		ISelectionDelta newSelectionDelta =
			new SelectionDelta(EIDType.PATHWAY_VERTEX, EIDType.REFSEQ_MRNA_INT);

		int iRefSeqID = 0;
		Integer iDavidID = 0;
		int iPathwayVertexGraphItemID = 0;

		IIDMappingManager idMappingManager = generalManager.getIDMappingManager();

		for (SelectionDeltaItem item : selectionDelta) {
			iRefSeqID = item.getPrimaryID();

			iDavidID = idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, iRefSeqID);

			if (iDavidID == null)
				throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");

			iPathwayVertexGraphItemID =
				generalManager.getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidID);

			// Ignore David IDs that do not exist in any pathway
			if (iPathwayVertexGraphItemID == -1) {
				continue;
			}

			// Convert DAVID ID to pathway graph item representation ID
			for (IGraphItem tmpGraphItemRep : generalManager.getPathwayItemManager().getItem(
				iPathwayVertexGraphItemID).getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)) {
				if (!pathwayManager.getItem(iPathwayID).containsItem(tmpGraphItemRep)) {
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

	private void calculatePathwayScaling(final GL gl, final int iPathwayId) {

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

		if (generalManager.getPathwayManager().getItem(iPathwayId).getType().equals(
			EPathwayDatabaseType.BIOCARTA)) {
			fPathwayScalingFactor = 5;
		}
		else {
			fPathwayScalingFactor = 3.2f;
		}

		PathwayGraph tmpPathwayGraph = generalManager.getPathwayManager().getItem(iPathwayId);

		int iImageWidth = tmpPathwayGraph.getWidth();
		int iImageHeight = tmpPathwayGraph.getHeight();

		generalManager.getLogger().log(Level.FINE,
			"Pathway texture width=" + iImageWidth + " / height=" + iImageHeight);

		if (iImageWidth == -1 || iImageHeight == -1) {
			generalManager.getLogger().log(Level.SEVERE,
				"Problem because pathway texture width or height is invalid!");
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
			pickingManager.flushHits(iUniqueID, ePickingType);
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
							int iPathwayID =
								generalManager.getPathwayManager().searchPathwayIdByName(
									tmpVertexGraphItemRep.getName(), EPathwayDatabaseType.KEGG);

							if (iPathwayID != -1) {
								IDListEventContainer<Integer> idListEventContainer =
									new IDListEventContainer<Integer>(EEventType.LOAD_PATHWAY_BY_PATHWAY_ID,
										EIDType.PATHWAY);
								idListEventContainer.addID(iPathwayID);

								triggerEvent(EMediatorType.SELECTION_MEDIATOR, idListEventContainer);
							}
						}
						else {
							// Load pathways based on a david ID
							IDListEventContainer<Integer> idListEventContainer =
								new IDListEventContainer<Integer>(EEventType.LOAD_PATHWAY_BY_GENE,
									EIDType.REFSEQ_MRNA_INT);
							ArrayList<Integer> alRefSeqID =
								getRefSeqIDsFromPathwayVertexGraphItemRep(tmpVertexGraphItemRep.getID());
							idListEventContainer.setIDs(alRefSeqID);
							triggerEvent(EMediatorType.SELECTION_MEDIATOR, idListEventContainer);
						}
						break;

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					default:
						pickingManager.flushHits(iUniqueID, ePickingType);
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

				triggerEvent(EMediatorType.SELECTION_MEDIATOR, new SelectionCommandEventContainer(
					EIDType.REFSEQ_MRNA_INT,
					new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType)));
				ISelectionDelta selectionDelta = createExternalSelectionDelta(selectionManager.getDelta());
				triggerEvent(EMediatorType.SELECTION_MEDIATOR, new DeltaEventContainer<ISelectionDelta>(
					selectionDelta));

				break;
		}

		pickingManager.flushHits(iUniqueID, ePickingType);
	}

	private void createConnectionLines(ESelectionType eSelectionType, int iConnectionID) {
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep;
		int iPathwayHeight = generalManager.getPathwayManager().getItem(iPathwayID).getHeight();

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
	
		for (IGraphItem tmpPathwayVertexGraphItemRep 
			: generalManager.getPathwayManager().getItem(iPathwayID).getAllItemsByKind(EGraphItemKind.NODE))
		{			
			for (IGraphItem tmpPathwayVertexGraphItem 
				: tmpPathwayVertexGraphItemRep.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) 
			{
				int iDavidID =
					generalManager.getPathwayItemManager().getDavidIdByPathwayVertexGraphItemId(
						tmpPathwayVertexGraphItem.getId());

				if (iDavidID == -1 || iDavidID == 0) {
					generalManager.getLogger().log(Level.WARNING, "Invalid David Gene ID.");
					continue;
				}

				Set<Integer> iSetRefSeq =
					idMappingManager.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT, iDavidID);

				if (iSetRefSeq == null) {
					
					generalManager.getLogger()
						.log(Level.SEVERE, "No RefSeq IDs found for David: " + iDavidID);
					continue;
				}

				for (Object iRefSeqID : iSetRefSeq) {
					delta.add(VADeltaItem.create(type, (Integer) iRefSeqID));
				}
			}
		}

		triggerEvent(EMediatorType.SELECTION_MEDIATOR, new DeltaEventContainer<IVirtualArrayDelta>(delta));
	}

	@Override
	public synchronized String getShortInfo() {
		PathwayGraph pathway = generalManager.getPathwayManager().getItem(iPathwayID);

		return pathway.getTitle() + " (" + pathway.getType().getName() + ")";
	}

	@Override
	public synchronized String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		PathwayGraph pathway = generalManager.getPathwayManager().getItem(iPathwayID);

		sInfoText.append("<b>Pathway</b>\n\n<b>Name:</b> " + pathway.getTitle() + "\n<b>Type:</b> "
			+ pathway.getType().getName());

		// generalManager.getSWTGUIManager().setExternalRCPStatusLineMessage(
		// pathway.getType().getName() + " Pathway: " + sPathwayTitle);

		return sInfoText.toString();
	}

	@Override
	public synchronized void addSet(int setID) {
		super.addSet(setID);
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
	}

	@Override
	public synchronized void addSet(ISet set) {
		super.addSet(set);
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
	}

	// public void renderPathwayName(final GL gl)
	// {
	// if (!bEnableTitleRendering)
	// return;
	//
	// float fHorizontalPosition = 0;
	// float fVerticalPosition = 0;
	//
	// if (iHorizontalTextAlignment == SWT.LEFT)
	// fHorizontalPosition = 0.2f;
	// else if (iHorizontalTextAlignment == SWT.RIGHT)
	// fHorizontalPosition = 3.5f;
	// else if (iHorizontalTextAlignment == SWT.CENTER)
	// fHorizontalPosition = 1.8f;
	//
	// if (iVerticalTextAlignment == SWT.TOP)
	// fVerticalPosition = 7.8f;
	// else if (iVerticalTextAlignment == SWT.BOTTOM)
	// fVerticalPosition = 0.2f;
	// else if (iVerticalTextAlignment == SWT.CENTER)
	// fVerticalPosition = 1;
	//
	// String sPathwayName =
	// generalManager.getPathwayManager().getItem(iPathwayID)
	// .getTitle();
	//
	// int iMaxChars = 40;
	// if (iHorizontalTextAlignment == SWT.RIGHT)
	// iMaxChars = 30;
	//
	// if (sPathwayName.length() > iMaxChars)
	// sPathwayName = sPathwayName.subSequence(0, iMaxChars - 3) + "...";
	//
	// textRenderer.begin3DRendering();
	// textRenderer.setColor(0.2f, 0.2f, 0.2f, 1.0f);
	// textRenderer.draw3D(sPathwayName, fHorizontalPosition, fVerticalPosition,
	// 0.05f,
	// 0.011f);
	// textRenderer.end3DRendering();
	// }

	// public void enableTitleRendering(boolean bEnable)
	// {
	// bEnableTitleRendering = bEnable;
	// }
	//
	// public void setAlignment(int iHorizontalAlignment, int
	// iVerticalAlignment)
	// {
	// this.iHorizontalTextAlignment = iHorizontalAlignment;
	// this.iVerticalTextAlignment = iVerticalAlignment;
	// }

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		return selectionManager.getElements(eSelectionType).size();
	}

	@Override
	public void handleExternalEvent(IMediatorSender eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		switch (eventContainer.getEventType()) {
			case SELECTION_UPDATE:
				DeltaEventContainer<ISelectionDelta> selectionDeltaEventContainer =
					(DeltaEventContainer<ISelectionDelta>) eventContainer;
				handleSelectionUpdate(eventTrigger, selectionDeltaEventContainer.getSelectionDelta());
				break;
			case VA_UPDATE:
				DeltaEventContainer<IVirtualArrayDelta> vaDeltaEventContainer =
					(DeltaEventContainer<IVirtualArrayDelta>) eventContainer;
				handleVAUpdate(eventTrigger, vaDeltaEventContainer.getSelectionDelta());
				break;
			case TRIGGER_SELECTION_COMMAND:
				SelectionCommandEventContainer commandEventContainer =
					(SelectionCommandEventContainer) eventContainer;
				switch (commandEventContainer.getIDType()) {
					case DAVID:
					case REFSEQ_MRNA_INT:
					case EXPRESSION_INDEX:
						selectionManager.executeSelectionCommands(commandEventContainer
							.getSelectionCommands());
						break;
				}
				break;
			case VIEW_COMMAND:
				ViewCommandEventContainer viewCommandEventContainer =
					(ViewCommandEventContainer) eventContainer;
				if (viewCommandEventContainer.getViewCommand() == EViewCommand.REDRAW) {
					setDisplayListDirty();
				}
				break;
		}
	}

	private void handleVAUpdate(IMediatorSender eventTrigger, IVirtualArrayDelta delta) {
		selectionManager.setVADelta(delta);
		// reactOnExternalSelection();
		setDisplayListDirty();
	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);

	}

	@Override
	public void destroy() {
		generalManager.getPathwayManager().setPathwayVisibilityStateByID(iPathwayID, false);

		super.destroy();
	}

	@Override
	public void clearAllSelections() {
		selectionManager.clearSelections();
	}

	public void registerEventListeners() {
		IEventPublisher eventPublisher = generalManager.getEventPublisher();
		
		enableTexturesListener = new EnableTexturesListener();
		enableTexturesListener.setGLPathway(this);
		eventPublisher.addListener(EnableTexturesEvent.class, enableTexturesListener);

		disableTexturesListener = new DisableTexturesListener();
		disableTexturesListener.setGLPathway(this);
		eventPublisher.addListener(DisableTexturesEvent.class, disableTexturesListener);
	}
	
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
	}

}