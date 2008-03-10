package org.geneview.core.view.opengl.canvas.pathway;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.geneview.core.data.GeneralRenderStyle;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IPathwayManager;
import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.EPickingMode;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.manager.view.ESelectionMode;
import org.geneview.core.manager.view.Pick;
import org.geneview.core.manager.view.SelectionManager;
import org.geneview.core.view.jogl.mouse.PickingJoglMouseListener;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.parcoords.EInputDataType;
import org.geneview.core.view.opengl.util.GLToolboxRenderer;
import org.geneview.core.view.opengl.util.JukeboxHierarchyLayer;
import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemKind;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

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
	private boolean bSelectionChanged = false;
	private boolean bUpdateReceived = false;

	private IPathwayManager pathwayManager;
	
	private GLPathwayManager refGLPathwayManager;

	private SelectionManager selectionManager;

	private PathwayVertexGraphItemRep selectedVertex;

	/**
	 * Hash map stores which pathways contain the currently selected vertex and
	 * how often this vertex is contained.
	 */
	private HashMap<Integer, Integer> refHashPathwayContainingSelectedVertex2VertexCount;
	
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
		
		pathwayManager = generalManager.getSingelton().getPathwayManager();
		
		refGLPathwayManager = new GLPathwayManager(generalManager);
		refHashGLcontext2TextureManager = new HashMap<GL, GLPathwayTextureManager>();
		refHashPathwayContainingSelectedVertex2VertexCount = new HashMap<Integer, Integer>();
		
		selectionManager = generalManager.getSingelton().getViewGLCanvasManager().getSelectionManager();
	
		vecScaling = new Vec3f(1,1,1);
		vecTranslation = new Vec3f(0,0,0);
		renderStyle = new GeneralRenderStyle(viewFrustum);
	}

	public void setPathwayID(final int iPathwayID) {
		
		this.iPathwayID = iPathwayID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */	
	public void initLocal(final GL gl)
	{
		init(gl);
		pickingTriggerMouseAdapter.resetEvents();
		// TODO: individual toolboxrenderer
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.geneview.core.view.opengl.util.JukeboxHierarchyLayer, org.geneview.core.view.jogl.mouse.PickingJoglMouseListener)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter)
	{
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		glToolboxRenderer = new GLToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, renderStyle);
		
		init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {

		initPathwayData(gl);
		// FIXME: should only be called one
		initialContainedGenePropagation(); 
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
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
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
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
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {
		
		checkForHits(gl);
		renderScene(gl);
		glToolboxRenderer.render(gl);
	}
	
	protected void initPathwayData(final GL gl) {

		refGLPathwayManager.init(gl, alSetData, alSetSelection);
		
		// Create new pathway manager for GL context
		if(!refHashGLcontext2TextureManager.containsKey(gl))
		{
			refHashGLcontext2TextureManager.put(gl, 
					new GLPathwayTextureManager(generalManager));	
		}		
		
		loadAllPathways(gl);

//		refGLPathwayManager.buildPathwayDisplayList(gl, this, iPathwayID);
	}


	public void renderScene(final GL gl) {
		
		renderPathwayById(gl, iPathwayID);
	}


	private void loadAllPathways(final GL gl) {
		
		// Check if pathways are already loaded
		if (pathwayManager.getRootPathway().getAllGraphByType(
				EGraphItemHierarchy.GRAPH_CHILDREN).size() <= 1) // <= 1 not clean - due to pathway2D 
		{
			// Load KEGG pathways
			pathwayManager.loadAllPathwaysByType(EPathwayDatabaseType.KEGG);

			// Load BioCarta pathways
			pathwayManager.loadAllPathwaysByType(EPathwayDatabaseType.BIOCARTA);
		}
		
//		Random rand = new Random();
//		
//		List<IGraph> tmp = pathwayManager.getRootPathway().getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN);
//		iPathwayID = tmp.get(rand.nextInt(500)).getId();
		
		calculatePathwayScaling(gl, iPathwayID);
	}

	private void renderPathwayById(final GL gl,
			final int iPathwayId) {
		
		gl.glPushMatrix();
		gl.glTranslatef(vecTranslation.x(), vecTranslation.y(), vecTranslation.z());
		gl.glScalef(vecScaling.x(), vecScaling.y(), vecScaling.z());
		
		if (bEnablePathwayTexture)
		{
			refHashGLcontext2TextureManager.get(gl).renderPathway(
					gl, this, iPathwayId, 1.0f, false);
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
		
		if (selectedVertex != null)
		{
			// Write currently selected vertex to selection set
			// Selected elements are rendered highlighted by GLPathwayManager
			ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>();
			ArrayList<Integer> iAlTmpDepth = new ArrayList<Integer>();
			iAlTmpSelectionId.add(selectedVertex.getId());
			iAlTmpDepth.add(0);
			alSetSelection.get(1).getWriteToken();
			alSetSelection.get(1).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpDepth, null);
			alSetSelection.get(1).returnWriteToken();
			
			loadNodeInformationInBrowser(((PathwayVertexGraphItem)selectedVertex.getAllItemsByProp(
					EGraphItemProperty.ALIAS_PARENT).get(0)).getExternalLink());
		
			refGLPathwayManager.performIdenticalNodeHighlighting();
		}

		refGLPathwayManager.buildPathwayDisplayList(gl, this, iPathwayID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geneview.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object,
	 *      org.geneview.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {
		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(Object eventTrigger, ISet updatedSet): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		ArrayList<Integer> iAlSelection = refSetSelection.getSelectionIdArray();
		if (iAlSelection.size() != 0)
		{
			int iAccessionID = iAlSelection.get(0);
			
			String sAccessionCode = generalManager.getSingelton().getGenomeIdManager()
				.getIdStringFromIntByMapping(iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
		
			System.out.println("Accession Code: " +sAccessionCode);
			
//			int iPathwayHeight = ((PathwayGraph)generalManager.getSingelton().getPathwayManager().getItem(iPathwayID)).getHeight();
			
			selectionManager.modifySelection(iAccessionID, new SelectedElementRep(this.getId(), 
					0, 
					0, 
					0), 
					ESelectionMode.AddPick);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(Object eventTrigger): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
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
//
//	private void rebuildPathwayDisplayList(final GL gl) {
//
////		// Reset rebuild trigger flag
////		bRebuildVisiblePathwayDisplayLists = false;
////		
////		refGLPathwayManager.clearOldPickingIDs();
////
////		if (selectedVertex != null)
////		{
////			// Write currently selected vertex to selection set
////			int[] iArTmpSelectionId = new int[1];
////			int[] iArTmpDepth = new int[1];
////			iArTmpSelectionId[0] = selectedVertex.getId();
////			iArTmpDepth[0] = 0;
////			alSetSelection.get(0).getWriteToken();
////			alSetSelection.get(0).updateSelectionSet(iUniqueId, iArTmpSelectionId, iArTmpDepth, new int[0]);
////			alSetSelection.get(0).returnWriteToken();
////		}
////			
////		refGLPathwayManager.performIdenticalNodeHighlighting();
////		
////
////		refGLPathwayManager.buildPathwayDisplayList(gl, this, 261);
////		
////		// Cleanup unused textures
//////		refGLPathwayTextureManager.unloadUnusedTextures(getVisiblePathways());
//
//	}
	
	private void calculatePathwayScaling(final GL gl, final int iPathwayId) {
		
		if (refHashGLcontext2TextureManager.get(gl) == null)
			return;
		
		int iImageWidth = ((PathwayGraph)generalManager.getSingelton()
				.getPathwayManager().getItem(iPathwayId)).getWidth();
		int iImageHeight = ((PathwayGraph)generalManager.getSingelton()
				.getPathwayManager().getItem(iPathwayId)).getHeight();
	
		float fAspectRatio = (float)iImageWidth / (float)iImageHeight;
		float fPathwayScalingFactor = 0;
		
		if (((PathwayGraph)generalManager.getSingelton().getPathwayManager()
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
			vecScaling.setX((viewFrustum.getRight() - viewFrustum.getLeft()) / (iImageWidth * GLPathwayManager.SCALING_FACTOR_X));
			vecScaling.setY(vecScaling.x());	

			vecTranslation.set(0, (viewFrustum.getTop() - viewFrustum.getBottom() - 
							iImageHeight * GLPathwayManager.SCALING_FACTOR_Y * vecScaling.y()) / 2.0f, 0);
		}
		else if (fTmpPathwayHeight > (viewFrustum.getTop() - viewFrustum.getBottom()))
		{
			vecScaling.setY((viewFrustum.getTop() - viewFrustum.getBottom()) / (iImageHeight * GLPathwayManager.SCALING_FACTOR_Y));
			vecScaling.setX(vecScaling.y());

			vecTranslation.set((viewFrustum.getRight() - viewFrustum.getLeft() - 
							iImageWidth * GLPathwayManager.SCALING_FACTOR_X * vecScaling.x()) / 2.0f, 0, 0);
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
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.LOAD_URL_IN_BROWSER);

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
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.geneview.core.manager.view.EPickingType, org.geneview.core.manager.view.EPickingMode, int, org.geneview.core.manager.view.Pick)
	 */
	protected void handleEvents(final EPickingType ePickingType, 
			final EPickingMode ePickingMode, 
			final int iExternalID,
			final Pick pick)
	{
		// Check if selection occurs in the pool layer of the bucket
		if (glToolboxRenderer.getContainingLayer().getCapacity() >= 10)
			return;
		
		switch (ePickingType)
		{	
		case PATHWAY_ELEMENT_SELECTION:
			
			PathwayVertexGraphItemRep tmpVertexGraphItemRep = (PathwayVertexGraphItemRep) generalManager.getSingelton()
				.getPathwayItemManager().getItem(iExternalID);
			
			PathwayVertexGraphItem tmpVertexGraphItem = (PathwayVertexGraphItem) tmpVertexGraphItemRep
				.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT).get(0);
			
			selectedVertex = tmpVertexGraphItemRep;
												
			bIsDisplayListDirtyLocal = true;
			bIsDisplayListDirtyRemote = true;
			
			int iGeneID = generalManager.getSingelton().getGenomeIdManager()
				.getIdIntFromStringByMapping(
						tmpVertexGraphItem.getName().substring(4), 
						EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
					
			if (iGeneID == -1)
			{	
				pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
				return;
			}
			
			int iAccessionID = generalManager.getSingelton().getGenomeIdManager()
				.getIdIntFromIntByMapping(iGeneID, EGenomeMappingType.NCBI_GENEID_2_ACCESSION);

			generalManager.getSingelton().getViewGLCanvasManager().getInfoAreaManager()
				.setData(iUniqueId, iAccessionID, EInputDataType.GENE, getInfo());
			
			selectionManager.clear();

			int iPathwayHeight = ((PathwayGraph)generalManager.getSingelton().getPathwayManager().getItem(iPathwayID)).getHeight();
			
			selectionManager.modifySelection(iAccessionID, new SelectedElementRep(this.getId(), 
					(tmpVertexGraphItemRep.getXOrigin() * GLPathwayManager.SCALING_FACTOR_X) * vecScaling.x()  + vecTranslation.x(),
					((iPathwayHeight - tmpVertexGraphItemRep.getYOrigin()) * GLPathwayManager.SCALING_FACTOR_Y) * vecScaling.y() + vecTranslation.y(), 0), 
					ESelectionMode.AddPick);

			// Write currently selected vertex to selection set and trigger update
			ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(1);
			ArrayList<Integer> iAlTmpGroupId = new ArrayList<Integer>(1);
		
			iAlTmpSelectionId.add(iAccessionID);
			iAlTmpGroupId.add(1); 
			
			alSetSelection.get(0).getWriteToken();
			alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpGroupId, null);
			alSetSelection.get(0).returnWriteToken();

			pickingManager.flushHits(iUniqueId, EPickingType.PATHWAY_ELEMENT_SELECTION);
			break;
			
		}
	}
	
	private void initialContainedGenePropagation () {
		
		// TODO: Move to own method (outside this class)
		// Store all genes in that pathway with selection group 0
		Iterator<IGraphItem> iterPathwayVertexGraphItem = ((PathwayGraph)generalManager.getSingelton()
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
				String sGeneID = tmpPathwayVertexGraphItem.getName();
			
				// Remove prefix ("hsa:")
				if (sGeneID.length() < 5)
					continue;
				
				sGeneID = sGeneID.substring(4);
				
				iGeneID = generalManager.getSingelton().getGenomeIdManager()
					.getIdIntFromStringByMapping(sGeneID, 
						EGenomeMappingType.NCBI_GENEID_CODE_2_NCBI_GENEID);
						
				if (iGeneID == -1)
					continue;
				
				int iTmpAccessionID = generalManager.getSingelton().getGenomeIdManager()
					.getIdIntFromIntByMapping(iGeneID, EGenomeMappingType.NCBI_GENEID_2_ACCESSION);
			
				if (iTmpAccessionID == -1)
					continue;
				
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
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {
		
		ArrayList<String> sAlInfo = new ArrayList<String>();
		
		PathwayGraph pathway = ((PathwayGraph)generalManager.getSingelton().getPathwayManager()
				.getItem(iPathwayID));
	
		String sPathwayTitle = pathway.getTitle();
		
		sAlInfo.add("Type: " +pathway.getType().getName() +" Pathway");
		sAlInfo.add("Pathway: " +sPathwayTitle);
		
		return sAlInfo;
	}
}