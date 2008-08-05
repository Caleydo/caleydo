package org.caleydo.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.selection.ISelection;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.GeneralRenderStyle;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.ESelectionMode;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.manager.view.SelectionManager;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.EInputDataType;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.EHierarchyLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;
import org.caleydo.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.caleydo.core.view.opengl.util.selection.GenericSelectionManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 */
public class GLCanvasPathway3D
	extends AGLCanvasUser
	implements IMediatorReceiver, IMediatorSender
{

	private int iPathwayID = -1;

	private boolean bIsDisplayListDirtyLocal = true;

	private boolean bIsDisplayListDirtyRemote = true;

	private boolean bEnablePathwayTexture = true;

	private IPathwayManager pathwayManager;

	private GLPathwayManager gLPathwayManager;

	private SelectionManager selectionManager;

	private GenericSelectionManager pathwayVertexSelectionManager;

	private PathwayVertexGraphItemRep selectedVertex;

	// /**
	// * Hash map stores which pathways contain the currently selected vertex
	// and
	// * how often this vertex is contained.
	// */
	// private HashMap<Integer, Integer>
	// hashPathwayContainingSelectedVertex2VertexCount;

	/**
	 * Own texture manager is needed for each GL context, because textures
	 * cannot be bound to multiple GL contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> hashGLcontext2TextureManager;

	private Vec3f vecScaling;

	private Vec3f vecTranslation;

	private GeneralRenderStyle renderStyle;

	/**
	 * Constructor.
	 */
	public GLCanvasPathway3D(final IGeneralManager generalManager, final int iViewId,
			final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum)
	{

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum, false); // false
		// -
		// >
		// do
		// not
		// register
		// to
		// parent
		// canvas

		pathwayManager = generalManager.getPathwayManager();

		gLPathwayManager = new GLPathwayManager(generalManager);
		hashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();
		// hashPathwayContainingSelectedVertex2VertexCount = new
		// HashMap<Integer, Integer>();

		selectionManager = generalManager.getViewGLCanvasManager().getSelectionManager();

		vecScaling = new Vec3f(1, 1, 1);
		vecTranslation = new Vec3f(0, 0, 0);
		renderStyle = new GeneralRenderStyle(viewFrustum);

		// initialize internal gene selection manager
		ArrayList<EViewInternalSelectionType> alSelectionType = new ArrayList<EViewInternalSelectionType>();
		for (EViewInternalSelectionType selectionType : EViewInternalSelectionType.values())
		{
			alSelectionType.add(selectionType);
		}

		pathwayVertexSelectionManager = new GenericSelectionManager(alSelectionType,
				EViewInternalSelectionType.NORMAL);
	}

	public void setPathwayID(final int iPathwayID)
	{

		// Unregister former pathway in visibility list
		if (iPathwayID != -1)
			generalManager.getPathwayManager().setPathwayVisibilityStateByID(this.iPathwayID,
					false);

		this.iPathwayID = iPathwayID;
	}

	public int getPathwayID()
	{

		return iPathwayID;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media
	 * .opengl.GL)
	 */
	public void initLocal(final GL gl)
	{

		init(gl);
		pickingTriggerMouseAdapter.resetEvents();
		// TODO: individual toolboxrenderer
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media
	 * .opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer,
	 * org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener,
	 * org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager, iUniqueID,
				iRemoteViewID, new Vec3f(0, 0, 0), layer, true, renderStyle);

		init(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl
	 * .GL)
	 */
	public void init(final GL gl)
	{

		// Check if pathway exists or if it's already loaded
		if (!generalManager.getPathwayManager().hasItem(iPathwayID))
			return;

		initPathwayData(gl);

		// // Only send out contained genes for pathways inside the bucket (not
		// in pool)
		// if (containedHierarchyLayer != null &&
		// containedHierarchyLayer.getCapacity() <= 4)
		initialContainedGenePropagation();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.
	 * media.opengl.GL)
	 */
	public void displayLocal(final GL gl)
	{

		// Check if pathway exists or if it's already loaded
		// FIXME: not good because check in every rendered frame
		if (!generalManager.getPathwayManager().hasItem(iPathwayID))
			return;

		pickingManager.handlePicking(iUniqueID, gl, false);
		if (bIsDisplayListDirtyLocal)
		{
			rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyLocal = false;
		}
		display(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax
	 * .media.opengl.GL)
	 */
	public void displayRemote(final GL gl)
	{

		// Check if pathway exists or if it's already loaded
		// FIXME: not good because check in every rendered frame
		if (!generalManager.getPathwayManager().hasItem(iPathwayID))
			return;

		if (bIsDisplayListDirtyRemote)
		{
			rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyRemote = false;
		}

		display(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media
	 * .opengl.GL)
	 */
	public void display(final GL gl)
	{

		checkForHits(gl);
		renderScene(gl);
		glToolboxRenderer.render(gl);
	}

	protected void initPathwayData(final GL gl)
	{

		gLPathwayManager.init(gl, alSetData, pathwayVertexSelectionManager);

		// Create new pathway manager for GL context
		if (!hashGLcontext2TextureManager.containsKey(gl))
		{
			hashGLcontext2TextureManager.put(gl, new GLPathwayTextureManager(generalManager));
		}

		calculatePathwayScaling(gl, iPathwayID);
		pathwayManager.setPathwayVisibilityStateByID(iPathwayID, true);

		// gLPathwayManager.buildPathwayDisplayList(gl, this, iPathwayID);
	}

	public void renderScene(final GL gl)
	{

		renderPathwayById(gl, iPathwayID);
	}

	private void renderPathwayById(final GL gl, final int iPathwayId)
	{

		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());

		if (bEnablePathwayTexture)
		{
			float fPathwayTransparency = 1.0f;

			// if (containedHierarchyLayer.getCapacity() == 4) // check if layer
			// is the stack layer (todo: better would be a stack type)
			// fPathwayTransparency = 0.6f;

			hashGLcontext2TextureManager.get(gl).renderPathway(gl, this, iPathwayId,
					fPathwayTransparency, false);
		}

		float tmp = GLPathwayManager.SCALING_FACTOR_Y
				* ((PathwayGraph) pathwayManager.getItem(iPathwayId)).getHeight();

		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);

		if (remoteRenderingGLCanvas.getBucketMouseWheelListener() != null)
		{
			if (remoteRenderingGLCanvas.getHierarchyLayerByGLCanvasListenerId(iUniqueID)
					.getLevel().equals(EHierarchyLevel.UNDER_INTERACTION)
					&& remoteRenderingGLCanvas.getBucketMouseWheelListener()
							.isBucketBottomReached())
			{
				gLPathwayManager.renderPathway(gl, iPathwayId, true);
			}
			else
			{
				gLPathwayManager.renderPathway(gl, iPathwayId, false);
			}
		}
		else
		{
			gLPathwayManager.renderPathway(gl, iPathwayId, false);
		}

		gl.glTranslatef(0, -tmp, 0);

		gl.glScalef(1 / vecScaling.x(), 1 / vecScaling.y(), 1 / vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());

		gl.glPopMatrix();
	}

	private void rebuildPathwayDisplayList(final GL gl)
	{

		if (selectedVertex != null)
		{
			// // Write currently selected vertex to selection set
			// // Selected elements are rendered highlighted by GLPathwayManager
			// ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>();
			// ArrayList<Integer> iAlTmpGroup = new ArrayList<Integer>();
			//			
			// alSetSelection.get(1).getReadToken();
			// int iPreviousSelectedElement = -1;
			// if (alSetSelection.get(1).getSelectionIdArray() != null)
			// {
			// iPreviousSelectedElement =
			// alSetSelection.get(1).getSelectionIdArray().get(0);
			// alSetSelection.get(1).returnReadToken();
			// iAlTmpSelectionId.add(iPreviousSelectedElement);
			// iAlTmpGroup.add(-1);
			// }
			// else
			// return;
			//			
			// iAlTmpSelectionId.add(selectedVertex.getId());
			// iAlTmpGroup.add(0);
			//			
			// alSetSelection.get(1).getWriteToken();
			// alSetSelection.get(1).mergeSelection(iAlTmpSelectionId,
			// iAlTmpGroup, null);
			// alSetSelection.get(1).returnWriteToken();
			//				
		}

		gLPathwayManager.performIdenticalNodeHighlighting();
		gLPathwayManager.buildPathwayDisplayList(gl, this, iPathwayID);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver
	 * (java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISelection updatedSelection)
	{

		generalManager.getLogger().log(Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName());

		pathwayVertexSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);

		// Rebuild display list to remove old selection highlighting
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;

		selectedVertex = null;

		ISelection setSelection = (ISelection) updatedSelection;

		ArrayList<Integer> iAlSelection = setSelection.getSelectionIdArray();
		ArrayList<Integer> iAlSelectionMode = setSelection.getGroupArray();
		if (iAlSelection.size() != 0)
		{
			int iPathwayHeight = ((PathwayGraph) generalManager.getPathwayManager().getItem(
					iPathwayID)).getHeight();

			int iDavidId = iAlSelection.get(0);

			// Ignore initial gene propagation
			if (iAlSelectionMode.get(0) == 0)
				return;

			PathwayVertexGraphItem tmpPathwayVertexGraphItem = ((PathwayVertexGraphItem) generalManager
					.getPathwayItemManager().getItem(
							generalManager.getPathwayItemManager()
									.getPathwayVertexGraphItemIdByDavidId(iDavidId)));

			if (tmpPathwayVertexGraphItem == null)
			{
				generalManager.getLogger().log(Level.WARNING,
						"Something is wrong with pathway vertex! Check!");
				return;
			}

			PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
			for (IGraphItem tmpGraphItemRep : tmpPathwayVertexGraphItem
					.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD))
			{
				tmpPathwayVertexGraphItemRep = (PathwayVertexGraphItemRep) tmpGraphItemRep;

				// Check if vertex is contained in this pathway viewFrustum
				if (!((PathwayGraph) generalManager.getPathwayManager().getItem(iPathwayID))
						.containsItem(tmpPathwayVertexGraphItemRep))
					continue;

				selectionManager
						.modifySelection(
								iDavidId,
								new SelectedElementRep(
										this.getId(),
										(tmpPathwayVertexGraphItemRep.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X)
												* vecScaling.x() + vecTranslation.x(),
										((iPathwayHeight - tmpPathwayVertexGraphItemRep
												.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y)
												* vecScaling.y() + vecTranslation.y(), 0),
								ESelectionMode.AddPick);

				selectedVertex = tmpPathwayVertexGraphItemRep;

				// Add new vertex to internal selection manager
				pathwayVertexSelectionManager.addToType(EViewInternalSelectionType.MOUSE_OVER,
						tmpPathwayVertexGraphItemRep.getId());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#updateReceiver(java
	 * .lang.Object)
	 */
	public void updateReceiver(Object eventTrigger)
	{

	}

	private void calculatePathwayScaling(final GL gl, final int iPathwayId)
	{

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

		if (((PathwayGraph) generalManager.getPathwayManager().getItem(iPathwayId)).getType()
				.equals(EPathwayDatabaseType.BIOCARTA))
		{
			fPathwayScalingFactor = 5;
		}
		else
		{
			fPathwayScalingFactor = 3.2f;
		}

		PathwayGraph tmpPathwayGraph = (PathwayGraph) generalManager.getPathwayManager()
				.getItem(iPathwayId);

		int iImageWidth = tmpPathwayGraph.getWidth();
		int iImageHeight = tmpPathwayGraph.getHeight();

		generalManager.getLogger().log(Level.INFO,
				"Pathway texture width=" + iImageWidth + " / height=" + iImageHeight);

		if (iImageWidth == -1 || iImageHeight == -1)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"Problem because pathway texture width or height is invalid!");
		}

		float fTmpPathwayWidth = iImageWidth * GLPathwayManager.SCALING_FACTOR_X
				* fPathwayScalingFactor;
		float fTmpPathwayHeight = iImageHeight * GLPathwayManager.SCALING_FACTOR_Y
				* fPathwayScalingFactor;

		if (fTmpPathwayWidth > (viewFrustum.getRight() - viewFrustum.getLeft())
				&& fTmpPathwayWidth > fTmpPathwayHeight)
		{
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft())
					/ (iImageWidth * GLPathwayManager.SCALING_FACTOR_X) * fPadding);
			vecScaling.setY(vecScaling.x());

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
					* GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, (viewFrustum
					.getTop()
					- viewFrustum.getBottom() - iImageHeight
					* GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
		}
		else if (fTmpPathwayHeight > (viewFrustum.getTop() - viewFrustum.getBottom()))
		{
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom())
					/ (iImageHeight * GLPathwayManager.SCALING_FACTOR_Y) * fPadding);
			vecScaling.setX(vecScaling.y());

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - iImageWidth
					* GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, (viewFrustum
					.getTop()
					- viewFrustum.getBottom() - iImageHeight
					* GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
		}
		else
		{
			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f
					- fTmpPathwayWidth / 2.0f,
					(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f
							- fTmpPathwayHeight / 2.0f, 0);
		}
	}

	public void setMappingRowCount(final int iMappingRowCount)
	{

		gLPathwayManager.setMappingRowCount(iMappingRowCount);
	}

	public void enableGeneMapping(final boolean bEnableMapping)
	{

		gLPathwayManager.enableGeneMapping(bEnableMapping);
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
	}

	public void enablePathwayTextures(final boolean bEnablePathwayTexture)
	{

		gLPathwayManager.enableEdgeRendering(!bEnablePathwayTexture);
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;

		this.bEnablePathwayTexture = bEnablePathwayTexture;
	}

	public void enableNeighborhood(final boolean bEnableNeighborhood)
	{

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		gLPathwayManager.enableNeighborhood(bEnableNeighborhood);
	}

	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting)
	{

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		gLPathwayManager.enableIdenticalNodeHighlighting(bEnableIdenticalNodeHighlighting);
	}

	public void enableAnnotation(final boolean bEnableAnnotation)
	{

		gLPathwayManager.enableAnnotation(bEnableAnnotation);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo
	 * .core.manager.view.EPickingType,
	 * org.caleydo.core.manager.view.EPickingMode, int,
	 * org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

		// Check if selection occurs in the pool or memo layer of the remote
		// rendered view (i.e. bucket, jukebox)
		if (remoteRenderingGLCanvas.getHierarchyLayerByGLCanvasListenerId(iUniqueID)
				.getCapacity() > 5)
		{
			return;
		}

		switch (pickingType)
		{
			case PATHWAY_ELEMENT_SELECTION:

				ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>();
				ArrayList<Integer> iAlTmpGroupId = new ArrayList<Integer>();

				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;

				PathwayVertexGraphItemRep tmpVertexGraphItemRep = (PathwayVertexGraphItemRep) generalManager
						.getPathwayItemManager().getItem(iExternalID);

				// Do nothing if new selection is the same as previous selection
				if (tmpVertexGraphItemRep == selectedVertex
						&& !pickingMode.equals(EPickingMode.CLICKED))
				{
					pickingManager
							.flushHits(iUniqueID, EPickingType.PATHWAY_ELEMENT_SELECTION);
					pickingManager
							.flushHits(iUniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION);

					// Write info area content
					// TODO: now only the first parent graph item is read
					// actually the whole array (all genes) must me displayed in
					// the
					// info area
					PathwayVertexGraphItem tmp = (PathwayVertexGraphItem) tmpVertexGraphItemRep
							.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0);

					int iDavidId = generalManager.getPathwayItemManager()
							.getDavidIdByPathwayVertexGraphItemId(tmp.getId());

					if (iDavidId == -1 || iDavidId == 0)
						return;

					generalManager.getViewGLCanvasManager().getInfoAreaManager().setData(
							iUniqueID, iDavidId, EInputDataType.GENE, getInfo());

					return;
				}

				pathwayVertexSelectionManager
						.clearSelection(EViewInternalSelectionType.MOUSE_OVER);

				selectedVertex = tmpVertexGraphItemRep;

				PathwayVertexGraphItem tmpVertexGraphItem = null;
				for (IGraphItem tmpGraphItem : tmpVertexGraphItemRep
						.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT))
				{
					tmpVertexGraphItem = (PathwayVertexGraphItem) tmpGraphItem;

					// Actively deselect previously selected gene
					// int iGeneID = generalManager.getGenomeIdManager()
					//.getIdIntFromStringByMapping(tmpVertexGraphItem.getName().
					// substring(4),
					// EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);

					// int iUnselectAccessionID =
					// generalManager.getGenomeIdManager()
					// .getIdIntFromIntByMapping(iGeneID,
					// EGenomeMappingType.NCBI_GENEID_2_ACCESSION);

					// Add new vertex to internal selection manager
					pathwayVertexSelectionManager.addToType(
							EViewInternalSelectionType.MOUSE_OVER, tmpVertexGraphItemRep
									.getId());

					loadURLInBrowser(((PathwayVertexGraphItem) selectedVertex
							.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0))
							.getExternalLink());

					int iDavidId = generalManager.getPathwayItemManager()
							.getDavidIdByPathwayVertexGraphItemId(tmpVertexGraphItem.getId());

					if (iDavidId == -1 || iDavidId == 0)
					{
						generalManager.getLogger()
								.log(Level.WARNING, "Invalid David Gene ID.");
						pickingManager.flushHits(iUniqueID,
								EPickingType.PATHWAY_ELEMENT_SELECTION);
						pickingManager.flushHits(iUniqueID,
								EPickingType.PATHWAY_TEXTURE_SELECTION);
						// selectionManager.clear();

						continue;
					}

					selectionManager.clear();

					// TODO: do this just for first or think about better
					// solution!
					generalManager.getViewGLCanvasManager().getInfoAreaManager().setData(
							iUniqueID, iDavidId, EInputDataType.GENE, getInfo());

					Iterator<IGraphItem> iterPathwayVertexGraphItemRep = tmpVertexGraphItem
							.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD).iterator();

					PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
					while (iterPathwayVertexGraphItemRep.hasNext())
					{
						tmpPathwayVertexGraphItemRep = ((PathwayVertexGraphItemRep) iterPathwayVertexGraphItemRep
								.next());

						// Check if vertex is contained in this pathway
						// viewFrustum
						if (!((PathwayGraph) generalManager.getPathwayManager().getItem(
								iPathwayID)).containsItem(tmpPathwayVertexGraphItemRep))
							continue;

						int iPathwayHeight = ((PathwayGraph) generalManager
								.getPathwayManager().getItem(iPathwayID)).getHeight();

						selectionManager
								.modifySelection(
										iDavidId,
										new SelectedElementRep(
												this.getId(),
												(tmpPathwayVertexGraphItemRep.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X)
														* vecScaling.x() + vecTranslation.x(),
												((iPathwayHeight - tmpPathwayVertexGraphItemRep
														.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y)
														* vecScaling.y() + vecTranslation.y(),
												0), ESelectionMode.AddPick);
					}

					// // Active unselection
					// iAlTmpSelectionId.add(iUnselectAccessionID);
					// iAlTmpGroupId.add(0);

					switch (pickingMode)
					{
						case CLICKED:

							iAlTmpSelectionId.add(iDavidId);
							iAlTmpGroupId.add(2);

							break;

						case MOUSE_OVER:

							iAlTmpSelectionId.add(iDavidId);
							iAlTmpGroupId.add(1);

							break;
					}

					// TODO: Exit loop after first run and publish only first
					// selected gene
					// This can be removed when other views can handle multiple
					// selections
					break;
				}

				// Do nothing if no element was selected
				if (iAlTmpSelectionId.isEmpty())
					return;

				alSelection.get(0).updateSelectionSet(iUniqueID, iAlTmpSelectionId,
						iAlTmpGroupId, null);

				pickingManager.flushHits(iUniqueID, EPickingType.PATHWAY_ELEMENT_SELECTION);
				pickingManager.flushHits(iUniqueID, EPickingType.PATHWAY_TEXTURE_SELECTION);
				break;
		}
	}

	private void initialContainedGenePropagation()
	{

		// TODO: Move to own method (outside this class)
		// Store all genes in that pathway with selection group 0
		Iterator<IGraphItem> iterPathwayVertexGraphItem = ((PathwayGraph) generalManager
				.getPathwayManager().getItem(iPathwayID)).getAllItemsByKind(
				EGraphItemKind.NODE).iterator();
		Iterator<IGraphItem> iterPathwayVertexGraphItemRep;
		ArrayList<Integer> iAlSelectedGenes = new ArrayList<Integer>();
		ArrayList<Integer> iAlTmpGroupId = new ArrayList<Integer>();
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
		PathwayVertexGraphItem tmpPathwayVertexGraphItem = null;
		while (iterPathwayVertexGraphItem.hasNext())
		{
			tmpPathwayVertexGraphItemRep = ((PathwayVertexGraphItemRep) iterPathwayVertexGraphItem
					.next());

			pathwayVertexSelectionManager.initialAdd(tmpPathwayVertexGraphItemRep.getId());

			iterPathwayVertexGraphItemRep = tmpPathwayVertexGraphItemRep.getAllItemsByProp(
					EGraphItemProperty.ALIAS_PARENT).iterator();

			while (iterPathwayVertexGraphItemRep.hasNext())
			{
				tmpPathwayVertexGraphItem = (PathwayVertexGraphItem) iterPathwayVertexGraphItemRep
						.next();

				int iDavidId = generalManager.getPathwayItemManager()
						.getDavidIdByPathwayVertexGraphItemId(
								tmpPathwayVertexGraphItem.getId());

				if (iDavidId == -1 || iDavidId == 0)
				{
					generalManager.getLogger().log(Level.WARNING, "Invalid David Gene ID.");
					continue;
				}

				iAlSelectedGenes.add(iDavidId);
				iAlTmpGroupId.add(0);
			}
		}

		alSelection.get(0)
				.updateSelectionSet(iUniqueID, iAlSelectedGenes, iAlTmpGroupId, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo()
	{

		ArrayList<String> sAlInfo = new ArrayList<String>();

		PathwayGraph pathway = ((PathwayGraph) generalManager.getPathwayManager().getItem(
				iPathwayID));

		String sPathwayTitle = pathway.getTitle();

		sAlInfo.add("Type: " + pathway.getType().getName() + " Pathway");
		sAlInfo.add(sPathwayTitle);

		return sAlInfo;
	}
}