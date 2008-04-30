package org.caleydo.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.data.GeneralRenderStyle;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IPathwayManager;
import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.ESelectionMode;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.manager.view.SelectionManager;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.EInputDataType;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;
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
implements IMediatorReceiver, IMediatorSender {
	
	private int iPathwayID = -1;
	
	private boolean bIsDisplayListDirtyLocal = true;
	private boolean bIsDisplayListDirtyRemote = true;
	
	private boolean bEnablePathwayTexture = true;

	private IPathwayManager pathwayManager;
	
	private GLPathwayManager refGLPathwayManager;

	private SelectionManager selectionManager;
	
	private GenericSelectionManager pathwayVertexSelectionManager;
	
	private PathwayVertexGraphItemRep selectedVertex;

//	/**
//	 * Hash map stores which pathways contain the currently selected vertex and
//	 * how often this vertex is contained.
//	 */
//	private HashMap<Integer, Integer> refHashPathwayContainingSelectedVertex2VertexCount;
	
	/**
	 * Own texture manager is needed for each GL context, 
	 * because textures cannot be bound to multiple GL contexts.
	 */
	private HashMap<GL, GLPathwayTextureManager> refHashGLcontext2TextureManager;
	
	private Vec3f vecScaling;
	private Vec3f vecTranslation;
	
	private GeneralRenderStyle renderStyle;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasPathway3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
		pathwayManager = generalManager.getPathwayManager();
		
		refGLPathwayManager = new GLPathwayManager(generalManager);
		refHashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();
//		refHashPathwayContainingSelectedVertex2VertexCount = new HashMap<Integer, Integer>();
		
		selectionManager = generalManager.getViewGLCanvasManager().getSelectionManager();
	
		vecScaling = new Vec3f(1,1,1);
		vecTranslation = new Vec3f(0,0,0);
		renderStyle = new GeneralRenderStyle(viewFrustum);
		
		// initialize internal gene selection manager
		ArrayList<EViewInternalSelectionType> alSelectionType = new ArrayList<EViewInternalSelectionType>();
		for(EViewInternalSelectionType selectionType : EViewInternalSelectionType.values())
		{
			alSelectionType.add(selectionType);
		}		
		
		pathwayVertexSelectionManager = new GenericSelectionManager(
				alSelectionType, EViewInternalSelectionType.NORMAL);		
	}

	public void setPathwayID(final int iPathwayID) {
		
		// Unregister former pathway in visibility list
		if (iPathwayID != -1)
			generalManager.getPathwayManager().setPathwayVisibilityStateByID(this.iPathwayID, false);
		
		this.iPathwayID = iPathwayID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		init(gl);
		pickingTriggerMouseAdapter.resetEvents();
		// TODO: individual toolboxrenderer
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, renderStyle);
		
		containedHierarchyLayer = layer;
				
		init(gl);

//		// Only send out contained genes for pathways inside the bucket (not in pool)
//		if (containedHierarchyLayer != null && containedHierarchyLayer.getCapacity() <= 4)
			initialContainedGenePropagation(); 
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {

		initPathwayData(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {
		
		pickingManager.handlePicking(iUniqueId, gl, false);
		if(bIsDisplayListDirtyLocal)
		{
			rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyLocal = false;			
		}	
		display(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {
		
		if(bIsDisplayListDirtyRemote)
		{
			rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyRemote = false;
		}	
		display(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		checkForHits(gl);
		renderScene(gl);
		glToolboxRenderer.render(gl);
	}
	
	protected void initPathwayData(final GL gl) {

		refGLPathwayManager.init(gl, alSetData, pathwayVertexSelectionManager);
		
		// Create new pathway manager for GL context
		if(!refHashGLcontext2TextureManager.containsKey(gl))
		{
			refHashGLcontext2TextureManager.put(gl, 
					new GLPathwayTextureManager(generalManager));	
		}		
		
		calculatePathwayScaling(gl, iPathwayID);
		pathwayManager.setPathwayVisibilityStateByID(iPathwayID, true);
		
//		refGLPathwayManager.buildPathwayDisplayList(gl, this, iPathwayID);
	}


	public void renderScene(final GL gl) {
		
		renderPathwayById(gl, iPathwayID);
	}

	private void renderPathwayById(final GL gl,
			final int iPathwayId) {
		
		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());
		
		if (bEnablePathwayTexture)
		{
			float fPathwayTransparency = 1.0f;
			
//			if (containedHierarchyLayer.getCapacity() == 4) // check if layer is the stack layer (todo: better would be a stack type)
//				fPathwayTransparency = 0.6f;
				
			refHashGLcontext2TextureManager.get(gl).renderPathway(
					gl, this, iPathwayId, fPathwayTransparency, false);
		}

		float tmp = GLPathwayManager.SCALING_FACTOR_Y * 
			((PathwayGraph)pathwayManager.getItem(iPathwayId)).getHeight();
		
		// Pathway texture height is subtracted from Y to align pathways to
		// front level
		gl.glTranslatef(0, tmp, 0);
		refGLPathwayManager.renderPathway(gl, iPathwayId, false);
		gl.glTranslatef(0, -tmp, 0);
		
		gl.glScalef(1/vecScaling.x(), 1/vecScaling.y(),1/ vecScaling.z());
		gl.glTranslatef(-vecTranslation.x(), -vecTranslation.y(), -vecTranslation.z());
		
		gl.glPopMatrix();
	}

	private void rebuildPathwayDisplayList(final GL gl) {
		
//		if (selectedVertex != null)
//		{
//			// Write currently selected vertex to selection set
//			// Selected elements are rendered highlighted by GLPathwayManager
//			ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>();
//			ArrayList<Integer> iAlTmpGroup = new ArrayList<Integer>();
//			
//			alSetSelection.get(1).getReadToken();
//			int iPreviousSelectedElement = -1;
//			if (alSetSelection.get(1).getSelectionIdArray() != null)
//			{
//				iPreviousSelectedElement = alSetSelection.get(1).getSelectionIdArray().get(0);
//				alSetSelection.get(1).returnReadToken();
//				iAlTmpSelectionId.add(iPreviousSelectedElement);
//				iAlTmpGroup.add(-1);
//			}
//			else
//				return;
//			
//			iAlTmpSelectionId.add(selectedVertex.getId());
//			iAlTmpGroup.add(0);
//			
//			alSetSelection.get(1).getWriteToken();
//			alSetSelection.get(1).mergeSelection(iAlTmpSelectionId, iAlTmpGroup, null);
//			alSetSelection.get(1).returnWriteToken();
//			
//			loadNodeInformationInBrowser(((PathwayVertexGraphItem)selectedVertex.getAllItemsByProp(
//					EGraphItemProperty.ALIAS_PARENT).get(0)).getExternalLink());
//		
//		}
		
		refGLPathwayManager.performIdenticalNodeHighlighting();
		refGLPathwayManager.buildPathwayDisplayList(gl, this, iPathwayID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {
		
//		generalManager.logMsg(
//				this.getClass().getSimpleName()
//						+ " ("+iUniqueId+"): updateReceiver(Object eventTrigger, ISet updatedSet): Update called by "
//						+ eventTrigger.getClass().getSimpleName()+" ("+((AGLCanvasUser)eventTrigger).getId(),
//				LoggerType.VERBOSE);
		
		pathwayVertexSelectionManager.clearSelection(
				EViewInternalSelectionType.MOUSE_OVER);
		
		// Rebuild display list to remove old selection highlighting
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		
		selectedVertex = null;
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		ArrayList<Integer> iAlSelection = refSetSelection.getSelectionIdArray();
		ArrayList<Integer> iAlSelectionMode = refSetSelection.getGroupArray();
		if (iAlSelection.size() != 0)
		{
			int iPathwayHeight = ((PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayID)).getHeight();
			
			int iAccessionID = iAlSelection.get(0);
			
			// Ignore initial gene propagation
			if (iAlSelectionMode.get(0) == 0)
				return;
			
			String sAccessionCode = generalManager.getGenomeIdManager()
				.getIdStringFromIntByMapping(iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
		
			System.out.println("Accession Code: " +sAccessionCode);
								
			int iNCBIGeneID = generalManager.getGenomeIdManager()
				.getIdIntFromIntByMapping(iAccessionID, EGenomeMappingType.ACCESSION_2_NCBI_GENEID);

			String sNCBIGeneIDCode = generalManager.getGenomeIdManager()
				.getIdStringFromIntByMapping(iNCBIGeneID, EGenomeMappingType.NCBI_GENEID_2_NCBI_GENEID_CODE);
		
			int iNCBIGeneIDCode = StringConversionTool.convertStringToInt(sNCBIGeneIDCode, -1);
			
			if(iNCBIGeneID == -1)
			{
				System.out.println("Error: No Gene ID for accession");
				return;
			}
			
			PathwayVertexGraphItem tmpPathwayVertexGraphItem = 
				((PathwayVertexGraphItem)generalManager.getPathwayItemManager().getItem(
					generalManager.getPathwayItemManager().getPathwayVertexGraphItemIdByNCBIGeneId(iNCBIGeneIDCode)));

			if(tmpPathwayVertexGraphItem == null)
			{
//				generalManager.logMsg(
//						this.getClass().getSimpleName()
//								+ " ("+iUniqueId+"): Irgendwas mit graph vertex item das eigentlich net passiern sullt "
//								+ eventTrigger.getClass().getSimpleName()+" ("+((AGLCanvasUser)eventTrigger).getId(),
//						LoggerType.VERBOSE);
				return;
			}
			
			Iterator<IGraphItem> iterPathwayVertexGraphItemRep = 
				tmpPathwayVertexGraphItem.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD).iterator();
			
			PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
			while (iterPathwayVertexGraphItemRep.hasNext())
			{
				tmpPathwayVertexGraphItemRep = 
					((PathwayVertexGraphItemRep)iterPathwayVertexGraphItemRep.next());
				
				// Check if vertex is contained in this pathway viewFrustum
				if (!((PathwayGraph)generalManager.getPathwayManager()
						.getItem(iPathwayID)).containsItem(tmpPathwayVertexGraphItemRep))
					continue;
				
				selectionManager.modifySelection(iAccessionID, new SelectedElementRep(this.getId(), 
						(tmpPathwayVertexGraphItemRep.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X) * vecScaling.x()  + vecTranslation.x(),
						((iPathwayHeight - tmpPathwayVertexGraphItemRep.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y) * vecScaling.y() + vecTranslation.y(), 0), 
						ESelectionMode.AddPick);
				
				selectedVertex = tmpPathwayVertexGraphItemRep;
				
				// Add new vertex to internal selection manager
				pathwayVertexSelectionManager.addToType(
						EViewInternalSelectionType.MOUSE_OVER, tmpPathwayVertexGraphItemRep.getId());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

//		generalManager.logMsg(
//				this.getClass().getSimpleName()
//						+ ": updateReceiver(Object eventTrigger): Update called by "
//						+ eventTrigger.getClass().getSimpleName(),
//				LoggerType.VERBOSE);
	}
	
//	public void loadDependentPathwayBySingleVertex(final GL gl,
//			final PathwayVertexGraphItemRep vertex) {
//		
//		Iterator<IGraphItem> iterVertexGraphItems = 
//			vertex.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).iterator();
//
//		ArrayList<IGraphItem> alSelectedVertexGraphItemReps = 
//			new ArrayList<IGraphItem>();
//		
//		// Remove duplicates by adding to a hash list
//		HashSet<IGraphItem> set = new HashSet<IGraphItem>();
//		
//		while(iterVertexGraphItems.hasNext())
//		{
//			set.addAll(iterVertexGraphItems.next().getAllItemsByProp(
//					EGraphItemProperty.ALIAS_CHILD));
//		}
//		
//		alSelectedVertexGraphItemReps.addAll(set);
//				
//		loadDependentPathways(gl, alSelectedVertexGraphItemReps);
//	}
//		
//	public void loadDependentPathways(final GL gl,
//			final List<IGraphItem> alVertexRep) {
//
//		refHashPathwayContainingSelectedVertex2VertexCount.clear();
//		
//		Iterator<IGraphItem> iterIdenticalPathwayGraphItemReps = 
//			alVertexRep.iterator();
//		
//		IGraphItem identicalPathwayGraphItemRep;
//		int iPathwayId = 0;
//		int iMaxPathwayCount = 0;
//		
//		while (iterIdenticalPathwayGraphItemReps.hasNext())
//		{
//			identicalPathwayGraphItemRep = iterIdenticalPathwayGraphItemReps.next();
//	
//			iPathwayId = ((PathwayGraph)identicalPathwayGraphItemRep
//					.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).toArray()[0]).getKeggId();
//
//
//			// Check if pathway has already a vertex counted
//			if (refHashPathwayContainingSelectedVertex2VertexCount.containsKey(iPathwayId))
//			{
//				// Increase current stored identical vertex count by 1
//				refHashPathwayContainingSelectedVertex2VertexCount.put(
//						iPathwayId, refHashPathwayContainingSelectedVertex2VertexCount.get(iPathwayId) + 1);
//			}
//			else
//			{
//				refHashPathwayContainingSelectedVertex2VertexCount.put(iPathwayId, 1);
//			}
//		}
//		
//		bIsDisplayListDirtyLocal = true;
//		bIsDisplayListDirtyRemote = true;
//	}

	private void calculatePathwayScaling(final GL gl, final int iPathwayId) {
		
		if (refHashGLcontext2TextureManager.get(gl) == null)
			return;
		
		int iImageWidth = ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getWidth();
		int iImageHeight = ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayId)).getHeight();
	
		float fAspectRatio = (float)iImageWidth / (float)iImageHeight;
		float fPathwayScalingFactor = 0;
		
		if (((PathwayGraph)generalManager.getPathwayManager()
				.getItem(iPathwayId)).getType().equals(EPathwayDatabaseType.BIOCARTA))
		{
			fPathwayScalingFactor = 5;
		}
		else
		{
			fPathwayScalingFactor = 3.2f;
		}
		
		float fTmpPathwayWidth = iImageWidth * GLPathwayManager.SCALING_FACTOR_X * fPathwayScalingFactor;
		float fTmpPathwayHeight = iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * fPathwayScalingFactor;
		
		if (fTmpPathwayWidth > (viewFrustum.getRight() - viewFrustum.getLeft())
				&& fTmpPathwayWidth > fTmpPathwayHeight)
		{			
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft()) / (iImageWidth * GLPathwayManager.SCALING_FACTOR_X) * 0.9f);
			vecScaling.setY(vecScaling.x());	

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - 
					iImageWidth * GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, 
					(viewFrustum.getTop() - viewFrustum.getBottom() - 
							iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
		}
		else if (fTmpPathwayHeight > (viewFrustum.getTop() - viewFrustum.getBottom()))
		{
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom()) / (iImageHeight * GLPathwayManager.SCALING_FACTOR_Y) * 0.9f);
			vecScaling.setX(vecScaling.y());

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - 
							iImageWidth * GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, 
							(viewFrustum.getTop() - viewFrustum.getBottom() - 
									iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
		}
		else
		{
			vecScaling.set(fPathwayScalingFactor, fPathwayScalingFactor, 1f);			
		
			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft()) / 2.0f - fTmpPathwayWidth / 2.0f,
					(viewFrustum.getTop() - viewFrustum.getBottom()) / 2.0f - fTmpPathwayHeight / 2.0f, 0);
		}
	}

	public void loadNodeInformationInBrowser(String sUrl) {

		if (sUrl.isEmpty())
			return;

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) generalManager
				.getCommandManager().createCommandByType(CommandQueueSaxType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}
	
	public void setMappingRowCount(final int iMappingRowCount) {
		
		refGLPathwayManager.setMappingRowCount(iMappingRowCount);
	}
		
	public void enableGeneMapping(final boolean bEnableMapping) {
		
		refGLPathwayManager.enableGeneMapping(bEnableMapping);
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
	}
	
	public void enablePathwayTextures(final boolean bEnablePathwayTexture) {
		
		refGLPathwayManager.enableEdgeRendering(!bEnablePathwayTexture);
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		
		this.bEnablePathwayTexture = bEnablePathwayTexture;
	}
	
	public void enableNeighborhood(final boolean bEnableNeighborhood) {
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		refGLPathwayManager.enableNeighborhood(bEnableNeighborhood);
	}
	
	public void enableIdenticalNodeHighlighting(final boolean bEnableIdenticalNodeHighlighting) {
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		refGLPathwayManager.enableIdenticalNodeHighlighting(bEnableIdenticalNodeHighlighting);
	}
	
	public void enableAnnotation(final boolean bEnableAnnotation) {
		
		refGLPathwayManager.enableAnnotation(bEnableAnnotation);
	}
	
	@Override
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) 
	{
//		// Check if selection occurs in the pool or memo layer of the bucket
//		if (containedHierarchyLayer != null 
//				&& containedHierarchyLayer.getCapacity() >= 5)
//		{
//			return;
//		}
		
		switch (pickingType)
		{	
		case PATHWAY_ELEMENT_SELECTION:
			
			pathwayVertexSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);
			
			PathwayVertexGraphItemRep tmpVertexGraphItemRep = (PathwayVertexGraphItemRep) generalManager
				.getPathwayItemManager().getItem(iExternalID);
		
			PathwayVertexGraphItem tmpVertexGraphItem = (PathwayVertexGraphItem) tmpVertexGraphItemRep
				.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0);
			
			// Actively deselect previously selected gene
			int iGeneID = generalManager.getGenomeIdManager()
				.getIdIntFromStringByMapping(tmpVertexGraphItem.getName().substring(4), 
					EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
			
			int iUnselectAccessionID = generalManager.getGenomeIdManager()
				.getIdIntFromIntByMapping(iGeneID, EGenomeMappingType.NCBI_GENEID_2_ACCESSION);

			
			selectedVertex = tmpVertexGraphItemRep;

			// Add new vertex to internal selection manager
			pathwayVertexSelectionManager.addToType(
					EViewInternalSelectionType.MOUSE_OVER, selectedVertex.getId());
												
			bIsDisplayListDirtyLocal = true;
			bIsDisplayListDirtyRemote = true;
			
			iGeneID = generalManager.getGenomeIdManager()
				.getIdIntFromStringByMapping(
						tmpVertexGraphItem.getName().substring(4), 
						EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
					
			if (iGeneID == -1)
			{	
				break;
			}
			
			int iAccessionID = generalManager.getGenomeIdManager()
				.getIdIntFromIntByMapping(iGeneID, EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
			
			generalManager.getViewGLCanvasManager().getInfoAreaManager()
				.setData(iUniqueId, iAccessionID, EInputDataType.GENE, getInfo());
			
			selectionManager.clear();
			
			Iterator<IGraphItem> iterPathwayVertexGraphItemRep = 
				tmpVertexGraphItem.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD).iterator();
			
			PathwayVertexGraphItemRep tmpPathwayVertexGraphItemRep = null;
			while (iterPathwayVertexGraphItemRep.hasNext())
			{
				tmpPathwayVertexGraphItemRep = 
					((PathwayVertexGraphItemRep)iterPathwayVertexGraphItemRep.next());
				
				// Check if vertex is contained in this pathway viewFrustum
				if (!((PathwayGraph)generalManager.getPathwayManager()
						.getItem(iPathwayID)).containsItem(tmpPathwayVertexGraphItemRep))
					continue;
				
				int iPathwayHeight = ((PathwayGraph)generalManager.getPathwayManager().getItem(iPathwayID)).getHeight();
				
				selectionManager.modifySelection(iAccessionID, new SelectedElementRep(this.getId(), 
						(tmpPathwayVertexGraphItemRep.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X) * vecScaling.x()  + vecTranslation.x(),
						((iPathwayHeight - tmpPathwayVertexGraphItemRep.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y) * vecScaling.y() + vecTranslation.y(), 0), 
						ESelectionMode.AddPick);
			}
			
			// Write currently selected vertex to selection set and trigger update
			ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(2);
			ArrayList<Integer> iAlTmpGroupId = new ArrayList<Integer>(2);
			
//			// Active unselection
//			iAlTmpSelectionId.add(iUnselectAccessionID);
//			iAlTmpGroupId.add(0);
			
			switch (pickingMode)
			{
			case CLICKED:
				
				iAlTmpSelectionId.add(iAccessionID);
				iAlTmpGroupId.add(2); 
				
				alSetSelection.get(0).getWriteToken();
				alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpGroupId, null);
				alSetSelection.get(0).returnWriteToken();
				
				break;
				
			case MOUSE_OVER:

				iAlTmpSelectionId.add(iAccessionID);
				iAlTmpGroupId.add(1); 
				
				alSetSelection.get(0).getWriteToken();
				alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpGroupId, null);
				alSetSelection.get(0).returnWriteToken();
				
				break;
			}	

			pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
			pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_TEXTURE_SELECTION);
			break;					
		}
	}
	
	private void initialContainedGenePropagation () {
		
		// TODO: Move to own method (outside this class)
		// Store all genes in that pathway with selection group 0
		Iterator<IGraphItem> iterPathwayVertexGraphItem = ((PathwayGraph)generalManager
				.getPathwayManager().getItem(iPathwayID)).getAllItemsByKind(EGraphItemKind.NODE).iterator();
		
		ArrayList<Integer> iAlSelectedGenes = new ArrayList<Integer>();
		ArrayList<Integer> iAlTmpGroupId = new ArrayList<Integer>();
		PathwayVertexGraphItemRep tmpPathwayVertexGraphItem = null;
		int iGeneID = -1;
		while(iterPathwayVertexGraphItem.hasNext()) 
		{
			tmpPathwayVertexGraphItem = ((PathwayVertexGraphItemRep)iterPathwayVertexGraphItem.next());
			
//			if (tmpPathwayVertexGraphItem..getType().equals(EPathwayVertexType.gene))
//			{
			pathwayVertexSelectionManager.initialAdd(tmpPathwayVertexGraphItem.getId());
			
				String sGeneID = tmpPathwayVertexGraphItem.getName();
			
				// Remove prefix ("hsa:")
				if (sGeneID.length() < 5)
					continue;
				
				sGeneID = sGeneID.substring(4);
				
				iGeneID = generalManager.getGenomeIdManager()
					.getIdIntFromStringByMapping(sGeneID, 
						EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
						
				if (iGeneID == -1)
					continue;
				
				int iTmpAccessionID = generalManager.getGenomeIdManager()
					.getIdIntFromIntByMapping(iGeneID, EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
			
				if (iTmpAccessionID == -1)
					continue;
				
//				pathwayVertexSelectionManager.initialAdd(iTmpAccessionID);
				
				iAlSelectedGenes.add(iTmpAccessionID);
				iAlTmpGroupId.add(0);
//			}
		}
		
		alSetSelection.get(0).getWriteToken();
		alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlSelectedGenes, iAlTmpGroupId, null);
		alSetSelection.get(0).returnWriteToken();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		
		PathwayGraph pathway = ((PathwayGraph)generalManager.getPathwayManager()
				.getItem(iPathwayID));
	
		String sPathwayTitle = pathway.getTitle();
		
		sAlInfo.add("Type: " +pathway.getType().getName() +" Pathway");
		sAlInfo.add(sPathwayTitle);
		
		return sAlInfo;
	}
}