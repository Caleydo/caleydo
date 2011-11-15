package org.caleydo.view.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.event.view.SwitchDataRepresentationEvent;
import org.caleydo.core.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.datadomain.pathway.contextmenu.item.LoadPathwaysByPathwayItem;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.view.pathway.listener.DisableGeneMappingListener;
import org.caleydo.view.pathway.listener.EnableGeneMappingListener;
import org.caleydo.view.pathway.listener.SwitchDataRepresentationListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

/**
 * Single OpenGL2 pathway view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLPathway extends ATableBasedView implements ISelectionUpdateHandler,
		IViewCommandHandler, ISelectionCommandHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.pathway";

	protected PathwayDataDomain pathwayDataDomain;
	private PathwayGraph pathway;

	private boolean bEnablePathwayTexture = true;

	private PathwayManager pathwayManager;
	private PathwayItemManager pathwayItemManager;

	private GLPathwayContentCreator gLPathwayContentCreator;

	private SelectionManager geneSelectionManager;

	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * Own texture manager is needed for each GL2 context, because textures
	 * cannot be bound to multiple GL2 contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;
	private Vec3f vecTranslation;

	int selectedSampleIndex = -1;

	protected EnableGeneMappingListener enableGeneMappingListener;
	protected DisableGeneMappingListener disableGeneMappingListener;
	protected SwitchDataRepresentationListener switchDataRepresentationListener;

	/**
	 * Constructor.
	 */
	public GLPathway(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum);
		label = "Pathway";
		viewType = VIEW_TYPE;

		pathwayManager = PathwayManager.get();
		pathwayItemManager = PathwayItemManager.get();

		pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType("org.caleydo.datadomain.pathway");

		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();

		connectedElementRepresentationManager = generalManager.getViewManager()
				.getConnectedElementRepresentationManager();

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);
	}

	public void setPathway(final PathwayGraph pathway) {
		// Unregister former pathway in visibility list
		if (pathway != null) {
			pathwayManager.setPathwayVisibilityState(pathway, false);
		}

		this.pathway = pathway;
	}
	
	@Override
	public void setDataContainer(DataContainer dataContainer) {

		super.setDataContainer(dataContainer);
		
		if (dataContainer instanceof PathwayDataContainer)
			pathway = ((PathwayDataContainer)dataContainer).getPathway();
	}

	public void setPathway(final int iPathwayID) {

		setPathway(pathwayManager.getItem(iPathwayID));
	}

	public PathwayGraph getPathway() {

		return pathway;
	}

	@Override
	public void initialize() {
		super.initialize();
		gLPathwayContentCreator = new GLPathwayContentCreator(viewFrustum, this);
	}

	@Override
	public void initLocal(final GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void init(final GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		// Check if pathway exists or if it's already loaded
		if (pathway == null || !pathwayManager.hasItem(pathway.getID()))
			return;

		initPathwayData(gl);
	}

	@Override
	public void displayLocal(final GL2 gl) {

		// Check if pathway exists or if it's already loaded
		if (pathway == null || !pathwayManager.hasItem(pathway.getID()))
			return;

		// FIXME - check if already initialized with dirty flag
		initPathwayData(gl);

		pickingManager.handlePicking(this, gl);
		if (isDisplayListDirty) {
			rebuildPathwayDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}
		display(gl);
	}

	@Override
	public void displayRemote(final GL2 gl) {

		if (isDisplayListDirty) {
			calculatePathwayScaling(gl, pathway);
			rebuildPathwayDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		display(gl);

	}

	@Override
	public void display(final GL2 gl) {
		checkForHits(gl);
		if (pathway != null) {
			// TODO: also put this in global DL
			renderPathway(gl, pathway);

			gl.glCallList(displayListIndex);
		}
	}

	protected void initPathwayData(final GL2 gl) {
		// Initialize all elements in selection manager
		// Iterator<IGraphItem> iterPathwayVertexGraphItem =
		// pathway.getAllItemsByKind(
		// EGraphItemKind.NODE).iterator();
		// PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
		// while (iterPathwayVertexGraphItem.hasNext()) {
		// tmpPathwayVertexGraphItemRep = (PathwayVertexGraphItemRep)
		// iterPathwayVertexGraphItem
		// .next();
		// selectionManager.initialAdd(tmpPathwayVertexGraphItemRep.getId());
		// }

		gLPathwayContentCreator.init(gl, geneSelectionManager);

		// Create new pathway manager for GL2 context
		if (!hashGLcontext2TextureManager.containsKey(gl)) {
			hashGLcontext2TextureManager.put(gl, new GLPathwayTextureManager());
		}

		calculatePathwayScaling(gl, pathway);
		pathwayManager.setPathwayVisibilityState(pathway, true);

		// gLPathwayContentCreator.buildPathwayDisplayList(gl, this,
		// iPathwayID);
	}

	private void renderPathway(final GL2 gl, final PathwayGraph pathway) {

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
		// (remoteRenderingGLCanvas.getHierarchyLayerByGLEventListenerId(uniqueID)
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

	private void rebuildPathwayDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gLPathwayContentCreator.buildPathwayDisplayList(gl, this, pathway);

		// gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);
		// renderPathwayName(gl);
		// gl.glEndList();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		if (pathway == null)
			return;

		if (selectionDelta.getIDType().getIDCategory() == geneSelectionManager
				.getIDType().getIDCategory()) {
			SelectionDelta resolvedDelta = resolveExternalSelectionDelta(selectionDelta);
			geneSelectionManager.setDelta(resolvedDelta);

			setDisplayListDirty();

			int pathwayHeight = pathway.getHeight();
			for (SelectionDeltaItem item : resolvedDelta) {
				if (item.getSelectionType() != SelectionType.MOUSE_OVER
						&& item.getSelectionType() != SelectionType.SELECTION) {
					continue;
				}

				PathwayVertexGraphItemRep vertexRep = (PathwayVertexGraphItemRep) pathwayItemManager
						.getItem(item.getID());

				int viewID = uniqueID;
				// If rendered remote (hierarchical heat map) - use the remote
				// view ID
				// if (glRemoteRenderingView != null && glRemoteRenderingView
				// instanceof AGLViewBrowser)
				// viewID = glRemoteRenderingView.getID();

				SelectedElementRep elementRep = new SelectedElementRep(
						dataDomain.getRecordIDType(), viewID, vertexRep.getXOrigin()
								* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
								+ vecTranslation.x(),
						(pathwayHeight - vertexRep.getYOrigin())
								* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y()
								+ vecTranslation.y(), 0);

				for (Integer iConnectionID : item.getConnectionIDs()) {
					connectedElementRepresentationManager.addSelection(iConnectionID,
							elementRep, item.getSelectionType());
				}
			}
		} else if (selectionDelta.getIDType().getIDCategory() == dimensionSelectionManager
				.getIDType().getIDCategory()) {

			for (SelectionDeltaItem item : selectionDelta.getAllItems()) {
				if (item.getSelectionType() == SelectionType.MOUSE_OVER
						&& !item.isRemove()) {
					selectedSampleIndex = item.getID();
					break;
				}
			}
			setDisplayListDirty();
		}
	}

	private ArrayList<Integer> getExpressionIndicesFromPathwayVertexGraphItemRep(
			int iPathwayVertexGraphItemRepID) {

		ArrayList<Integer> alExpressionIndex = new ArrayList<Integer>();

		for (IGraphItem pathwayVertexGraphItem : pathwayItemManager.getItem(
				iPathwayVertexGraphItemRepID).getAllItemsByProp(
				EGraphItemProperty.ALIAS_PARENT)) {
			int davidID = pathwayItemManager
					.getDavidIdByPathwayVertexGraphItem((PathwayVertexGraphItem) pathwayVertexGraphItem);

			if (davidID == -1) {
				continue;
			}

			IDType geneIDType = geneSelectionManager.getIDType();

			Set<Integer> dataTableExpressionIndex = pathwayDataDomain
					.getGeneIDMappingManager().getIDAsSet(
							pathwayDataDomain.getDavidIDType(), geneIDType, davidID);
			if (dataTableExpressionIndex == null)
				continue;
			alExpressionIndex.addAll(dataTableExpressionIndex);
		}

		return alExpressionIndex;
	}

	private SelectionDelta createExternalSelectionDelta(SelectionDelta selectionDelta) {
		SelectionDelta newSelectionDelta = new SelectionDelta(
				geneSelectionManager.getIDType());

		for (SelectionDeltaItem item : selectionDelta) {
			for (Integer expressionIndex : getExpressionIndicesFromPathwayVertexGraphItemRep(item
					.getID())) {

				SelectionDeltaItem newItem = newSelectionDelta.addSelection(
						expressionIndex, item.getSelectionType());
				newItem.setRemove(item.isRemove());

				for (Integer connectionID : item.getConnectionIDs()) {
					newSelectionDelta.addConnectionID(expressionIndex, connectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private SelectionDelta resolveExternalSelectionDelta(SelectionDelta selectionDelta) {

		SelectionDelta newSelectionDelta = new SelectionDelta(
				pathwayDataDomain.getPrimaryIDType());

		PathwayVertexGraphItem pathwayVertexGraphItem;

		IDMappingManager idMappingManager = pathwayDataDomain.getGeneIDMappingManager();

		for (SelectionDeltaItem item : selectionDelta) {

			Set<Integer> tableIDs = idMappingManager.getIDAsSet(
					selectionDelta.getIDType(), pathwayDataDomain.getDavidIDType(),
					item.getID());

			if (tableIDs == null || tableIDs.isEmpty()) {
				continue;
			}

			Integer davidID = (Integer) tableIDs.toArray()[0];

			pathwayVertexGraphItem = pathwayItemManager
					.getPathwayVertexGraphItemByDavidId(davidID);

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
						tmpGraphItemRep.getId(), item.getSelectionType());
				newItem.setRemove(item.isRemove());
				for (int iConnectionID : item.getConnectionIDs()) {
					newItem.addConnectionID(iConnectionID);
				}
			}
		}

		return newSelectionDelta;
	}

	private void calculatePathwayScaling(final GL2 gl, final PathwayGraph pathway) {

		if (hashGLcontext2TextureManager.get(gl) == null)
			return;

		// // Missing power of two texture GL2 extension workaround
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

		if (pathway.getType().equals(PathwayDatabaseType.BIOCARTA)) {
			fPathwayScalingFactor = 5;
		} else {
			fPathwayScalingFactor = 3.2f;
		}

		int iImageWidth = pathway.getWidth();
		int iImageHeight = pathway.getHeight();

		if (iImageWidth == -1 || iImageHeight == -1) {
			Logger.log(new Status(IStatus.ERROR, this.toString(),
					"Problem because pathway texture width or height is invalid!"));
		}

		float fTmpPathwayWidth = iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X
				* fPathwayScalingFactor;
		float fTmpPathwayHeight = iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y
				* fPathwayScalingFactor;

		float pathwayAspectRatio = fTmpPathwayWidth / fTmpPathwayHeight;
		float viewFrustumWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		float viewFrustumHeight = viewFrustum.getTop() - viewFrustum.getBottom();
		float viewFrustumAspectRatio = viewFrustumWidth / viewFrustumHeight;
		boolean pathwayFitsViewFrustum = true;

		if (viewFrustumAspectRatio < pathwayAspectRatio
				&& fTmpPathwayWidth > viewFrustumWidth) {

			// if (fTmpPathwayWidth > viewFrustum.getRight() -
			// viewFrustum.getLeft()
			// && fTmpPathwayWidth > fTmpPathwayHeight) {
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft())
					/ (iImageWidth * PathwayRenderStyle.SCALING_FACTOR_X) * fPadding);
			vecScaling.setY(vecScaling.x());

			vecTranslation
					.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()) / 2.0f,
							(viewFrustum.getTop() - viewFrustum.getBottom() - iImageHeight
									* PathwayRenderStyle.SCALING_FACTOR_Y
									* vecScaling.y()) / 2.0f, 0);
			pathwayFitsViewFrustum = false;
		}
		if (viewFrustumAspectRatio >= pathwayAspectRatio
				&& fTmpPathwayHeight > viewFrustumHeight) {
			//
			// else if (fTmpPathwayHeight > viewFrustum.getTop()
			// - viewFrustum.getBottom()) {
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom())
					/ (iImageHeight * PathwayRenderStyle.SCALING_FACTOR_Y) * fPadding);
			vecScaling.setX(vecScaling.y());

			vecTranslation
					.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()) / 2.0f,
							(viewFrustum.getTop() - viewFrustum.getBottom() - iImageHeight
									* PathwayRenderStyle.SCALING_FACTOR_Y
									* vecScaling.y()) / 2.0f, 0);
			pathwayFitsViewFrustum = false;

		} // else {

		if (pathwayFitsViewFrustum) {
			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f
					- fTmpPathwayWidth / 2.0f,
					(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f
							- fTmpPathwayHeight / 2.0f, 0);
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

	public void enableAnnotation(final boolean bEnableAnnotation) {
		gLPathwayContentCreator.enableAnnotation(bEnableAnnotation);
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		switch (pickingType) {
		case PATHWAY_ELEMENT_SELECTION:

			SelectionType selectionType;

			PathwayVertexGraphItemRep tmpVertexGraphItemRep = (PathwayVertexGraphItemRep) pathwayItemManager
					.getItem(externalID);

			setDisplayListDirty();

			switch (pickingMode) {
			case DOUBLE_CLICKED:
				// same behavior as for single click except that
				// pathways are also loaded
				selectionType = SelectionType.SELECTION;

				// Load embedded pathway
				if (tmpVertexGraphItemRep.getType() == EPathwayVertexType.map) {
					PathwayGraph pathway = pathwayManager.searchPathwayByName(
							tmpVertexGraphItemRep.getName(), PathwayDatabaseType.KEGG);

					if (pathway != null) {
						LoadPathwayEvent event = new LoadPathwayEvent();
						event.setSender(this);
						event.setPathwayID(pathway.getID());
						event.setDataDomainID(dataDomain.getDataDomainID());
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

					LoadPathwaysByPathwayItem menuItem = new LoadPathwaysByPathwayItem(
							pathwayManager.searchPathwayByName(
									tmpVertexGraphItemRep.getName(),
									PathwayDatabaseType.KEGG),
							dataDomain.getDataDomainID());
					contextMenuCreator.addContextMenuItem(menuItem);

				} else if (tmpVertexGraphItemRep.getType() == EPathwayVertexType.gene) {
					for (IGraphItem pathwayVertexGraphItem : tmpVertexGraphItemRep
							.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {

						GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
						contexMenuItemContainer
								.setDataDomain((ATableBasedDataDomain) dataDomain);
						contexMenuItemContainer
								.setData(
										pathwayDataDomain.getDavidIDType(),
										pathwayItemManager
												.getDavidIdByPathwayVertexGraphItem((PathwayVertexGraphItem) pathwayVertexGraphItem));
						contextMenuCreator
								.addContextMenuItemContainer(contexMenuItemContainer);
					}
				} else {
					// do nothing if the type is neither a gene nor an
					// embedded pathway
					break;
				}

			default:
				return;
			}

			if (geneSelectionManager.checkStatus(selectionType, externalID)) {
				break;
			}

			geneSelectionManager.clearSelection(selectionType);

			SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR,
					selectionType);
			sendSelectionCommandEvent(geneSelectionManager.getIDType(), command);

			// Add new vertex to internal selection manager
			geneSelectionManager.addToType(selectionType, tmpVertexGraphItemRep.getId());

			int iConnectionID = generalManager.getIDCreator().createID(
					ManagedObjectType.CONNECTION);
			geneSelectionManager.addConnectionID(iConnectionID,
					tmpVertexGraphItemRep.getId());
			connectedElementRepresentationManager.clear(geneSelectionManager.getIDType(),
					selectionType);

			createConnectionLines(selectionType, iConnectionID);

			SelectionDelta selectionDelta = createExternalSelectionDelta(geneSelectionManager
					.getDelta());
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setDataDomainID(dataDomain.getDataDomainID());
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			event.setInfo(getLabel());

			eventPublisher.triggerEvent(event);

			break;
		}
	}

	private void createConnectionLines(SelectionType selectionType, int iConnectionID) {
		// check in preferences if we should draw connection lines for mouse
		// over
		if (!connectedElementRepresentationManager
				.isSelectionTypeRenderedWithVisuaLinks(selectionType))
			return;
		// check for selections
		if (!generalManager.getPreferenceStore().getBoolean(
				PreferenceConstants.VISUAL_LINKS_FOR_SELECTIONS)
				&& selectionType == SelectionType.SELECTION)
			return;

		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep;
		int pathwayHeight = pathway.getHeight();

		int viewID = uniqueID;
		// If rendered remote (hierarchical heat map) - use the remote view ID
		// if (glRemoteRenderingView != null && glRemoteRenderingView instanceof
		// AGLViewBrowser)
		// viewID = glRemoteRenderingView.getID();

		for (int vertexRepID : geneSelectionManager.getElements(selectionType)) {
			tmpPathwayVertexGraphItemRep = pathwayItemManager
					.getPathwayVertexRep(vertexRepID);

			SelectedElementRep elementRep = new SelectedElementRep(
					dataDomain.getRecordIDType(), viewID,
					tmpPathwayVertexGraphItemRep.getXOrigin()
							* PathwayRenderStyle.SCALING_FACTOR_X * vecScaling.x()
							+ vecTranslation.x(),
					(pathwayHeight - tmpPathwayVertexGraphItemRep.getYOrigin())
							* PathwayRenderStyle.SCALING_FACTOR_Y * vecScaling.y()
							+ vecTranslation.y(), 0);

			// for (Integer iConnectionID : selectionManager
			// .getConnectionForElementID(iVertexRepID))
			// {
			connectedElementRepresentationManager.addSelection(iConnectionID, elementRep,
					selectionType);
			// }
		}
		// }
	}

	@Override
	public void broadcastElements(EVAOperation type) {

		RecordVADelta delta = new RecordVADelta(dataContainer.getRecordPerspective()
				.getID(), pathwayDataDomain.getDavidIDType());

		for (IGraphItem tmpPathwayVertexGraphItemRep : pathway
				.getAllItemsByKind(EGraphItemKind.NODE)) {
			for (IGraphItem tmpPathwayVertexGraphItem : tmpPathwayVertexGraphItemRep
					.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT)) {
				int iDavidID = pathwayItemManager
						.getDavidIdByPathwayVertexGraphItem((PathwayVertexGraphItem) tmpPathwayVertexGraphItem);

				if (iDavidID == -1 || iDavidID == 0) {
					// generalManager.getLogger().log(
					// new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
					// "Invalid David Gene ID."));
					continue;
				}

				// for (Object iRefSeqID : DataTableRefSeq) {
				delta.add(VADeltaItem.create(type, (Integer) iDavidID));
				// }
			}
		}

		RecordVADeltaEvent virtualArrayDeltaEvent = new RecordVADeltaEvent();
		virtualArrayDeltaEvent.setSender(this);
		virtualArrayDeltaEvent.setDataDomainID(dataDomain.getDataDomainID());
		virtualArrayDeltaEvent.setVirtualArrayDelta(delta);
		virtualArrayDeltaEvent.setInfo(getLabel());
		eventPublisher.triggerEvent(virtualArrayDeltaEvent);
	}

	@Override
	public String getLabel() {
		return label + ": " + pathway.getName();
	}

	@Override
	public void initData() {
		connectedElementRepresentationManager.clear(dataDomain.getRecordIDType());
		selectedSampleIndex = -1;
		super.initData();

	}

	@Override
	public void destroy() {
		pathwayManager.setPathwayVisibilityState(pathway, false);

		super.destroy();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		enableGeneMappingListener = new EnableGeneMappingListener();
		enableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(EnableGeneMappingEvent.class,
				enableGeneMappingListener);

		disableGeneMappingListener = new DisableGeneMappingListener();
		disableGeneMappingListener.setHandler(this);
		eventPublisher.addListener(DisableGeneMappingEvent.class,
				disableGeneMappingListener);

		switchDataRepresentationListener = new SwitchDataRepresentationListener();
		switchDataRepresentationListener.setHandler(this);
		eventPublisher.addListener(SwitchDataRepresentationEvent.class,
				switchDataRepresentationListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

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

		if (switchDataRepresentationListener != null) {
			eventPublisher.removeListener(switchDataRepresentationListener);
			switchDataRepresentationListener = null;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedPathwayView serializedForm = new SerializedPathwayView(
				pathwayDataDomain.getDataDomainID());
		serializedForm.setViewID(this.getID());

		if (pathway != null)
			serializedForm.setPathwayID(pathway.getID());

		return serializedForm;
	}

	// @Override
	// public void handleSelectionCommand(IDCategory category,
	// SelectionCommand selectionCommand) {
	// if (dataDomain.getRecordIDCategory() == category)
	// selectionManager.executeSelectionCommand(selectionCommand);
	//
	// }

	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public int getMinPixelHeight() {
		return 120;
	}

	@Override
	public int getMinPixelWidth() {
		if (pathway == null)
			return 70;
		float aspectRatio = (float) pathway.getWidth() / (float) pathway.getHeight();
		return (int) (120.0f * aspectRatio);
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	public void switchDataRepresentation() {
		gLPathwayContentCreator.switchDataRepresentation();
		setDisplayListDirty();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		if (!(dataDomain instanceof GeneticDataDomain))
			throw new IllegalArgumentException(
					"Pathway view can handle only genetic data domain, tried to set: "
							+ dataDomain);

		if (pathwayDataDomain.getGeneIDMappingManager().hasMapping(
				pathwayDataDomain.getDavidIDType(), dataDomain.getRecordIDType())) {
			geneSelectionManager = dataDomain.getRecordSelectionManager();
		} else {
			geneSelectionManager = dataDomain.getDimensionSelectionManager();
		}
		super.setDataDomain(dataDomain);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the geneSelectionManager, see {@link #geneSelectionManager}
	 */
	public SelectionManager getGeneSelectionManager() {
		return geneSelectionManager;
	}
	
	@Override
	public int getMinPixelWidth(DetailLevel detailLevel) {
		return 200;
	}
}