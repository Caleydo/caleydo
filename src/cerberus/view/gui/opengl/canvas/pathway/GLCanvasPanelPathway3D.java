package cerberus.view.gui.opengl.canvas.pathway;

import java.nio.FloatBuffer;
import java.util.Iterator;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.pathway.Pathway;
import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.view.rep.pathway.IPathwayVertexRep;
import cerberus.manager.IGeneralManager;

/**
 * @author Marc Streit
 *
 */
public class GLCanvasPanelPathway3D
extends AGLCanvasPathway3D {	
	
	/**
	 * Constructor
	 * 
	 */
	public GLCanvasPanelPathway3D( 
			final IGeneralManager refGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel ) {
			
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
	}

	protected void buildPathwayDisplayList(final GL gl) {

		Pathway refTmpPathway = null;

		refHashDisplayListNodeId2Pathway.clear();
		refHashPathway2DisplayListNodeId.clear();
		refHashPathwayToZLayerValue.clear();
		iArPathwayEdgeDisplayListIDs.clear();
		
		System.out.println("Create pathway display lists");

		// Load pathway storage
		// Assumes that the set consists of only one storage
		IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
		int[] iArPathwayIDs = tmpStorage.getArrayInt();
		
		buildEnzymeNodeDisplayList(gl);
		buildHighlightedEnzymeNodeDisplayList(gl);
		buildCompoundNodeDisplayList(gl);
		buildHighlightedCompoundNodeDisplayList(gl);
		
		for (int iPathwayIndex = 0; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
			iPathwayIndex++)
		{			
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
				getItem(iArPathwayIDs[iPathwayIndex]);
			
			refHashPathwayToZLayerValue.put(refTmpPathway, fZLayerValue);
		
			System.out.println("Create display list for pathway "+refTmpPathway.getTitle());
			
			// Creating display list for pathways
			int iVerticesDisplayListId = gl.glGenLists(1);
			int iEdgeDisplayListId = gl.glGenLists(1);
			iArPathwayEdgeDisplayListIDs.add(iEdgeDisplayListId);

			refHashDisplayListNodeId2Pathway.put(iVerticesDisplayListId, refTmpPathway);	
			refHashPathway2DisplayListNodeId.put(refTmpPathway, iVerticesDisplayListId);
			
			gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);	
			extractVertices(gl, refTmpPathway);
			gl.glEndList();
	
			gl.glNewList(iEdgeDisplayListId, GL.GL_COMPILE);	
			extractEdges(gl, refTmpPathway);
			gl.glEndList();
		}
	}

	public void renderPart(GL gl) {
		
		handlePicking(gl);
		
		renderInfoArea(gl);
		
		if (bSelectionDataChanged)
		{
			buildPathwayDisplayList(gl);
			bSelectionDataChanged = false;
		}
		
		// Rebuild highlight node display list using the new scaling factor
		if (!iArHighlightedVertices.isEmpty())
		{
			if (bBlowUp == true)
			{
				fHighlightedNodeBlowFactor += 0.010f;
				
				if (fHighlightedNodeBlowFactor >= 1.3f)
					bBlowUp = false;
			}
			else
			{
				fHighlightedNodeBlowFactor -= 0.010;
				
				if (fHighlightedNodeBlowFactor <= 1.0f)
					bBlowUp = true;
			}
			
			buildHighlightedEnzymeNodeDisplayList(gl);
			buildHighlightedCompoundNodeDisplayList(gl);		
		}
	
		renderScene(gl);
		
		highlightIdenticalNodes(gl);
	}
	
	protected void renderScene(final GL gl) {
		
		gl.glPushMatrix();
		
		if (bShowPathwayTexture == true)
		{				
			Pathway refTmpPathway = null;
			refHashPathway2ModelMatrix.clear();
			int iDisplayListNodeId = 0;
			//int iDisplayListEdgeId = 0;
			
			// Load pathway storage
			// Assumes that the set consists of only one storage
			IStorage tmpStorage = alSetData.get(0).getStorageByDimAndIndex(0, 0);
			int[] iArPathwayIDs = tmpStorage.getArrayInt();
			
			// Render pathway under interaction
			refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
				getItem(iArPathwayIDs[0]);
			
			gl.glRotatef(-50, 1.0f, 0.0f, 0.0f);

			iDisplayListNodeId = refHashPathway2DisplayListNodeId.get(refTmpPathway);

			FloatBuffer testMatrixBuffer = FloatBuffer.allocate(16);
			gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, testMatrixBuffer);
			refHashPathway2ModelMatrix.put(refTmpPathway, testMatrixBuffer);
			
			renderPathway(gl,
					refTmpPathway, 
					iDisplayListNodeId);

			gl.glRotatef(50, 1.0f, 0.0f, 0.0f);
			
			gl.glTranslatef(-5.0f, -3.0f, 0.0f);
			
			for (int iPathwayIndex = 1; iPathwayIndex < tmpStorage.getSize(StorageType.INT); 
				iPathwayIndex++)
			{
				refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
					getItem(iArPathwayIDs[iPathwayIndex]);
			
				gl.glTranslatef(3.0f, 0.0f, 0.0f);
								
				iDisplayListNodeId = refHashPathway2DisplayListNodeId.get(refTmpPathway);

				testMatrixBuffer = FloatBuffer.allocate(16);
				gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, testMatrixBuffer);
				refHashPathway2ModelMatrix.put(refTmpPathway, testMatrixBuffer);
				
				renderPathway(gl,
						refTmpPathway, 
						iDisplayListNodeId);
			}
		}
		gl.glPopMatrix();
	}
	
	protected void renderPathway(final GL gl,
			final Pathway refTmpPathway, 
			int iDisplayListNodeId) {
		
		// Creating hierarchical picking names
		// This is the layer of the pathways, therefore we can use the pathway
		// node picking ID
		gl.glPushName(iDisplayListNodeId);	
		gl.glCallList(iDisplayListNodeId);
		gl.glPopName();
		
		Texture refPathwayTexture = null;
		float fTmpZLayerValue = 0.0f;
		float fTextureWidth;
		float fTextureHeight;
		
		refPathwayTexture = refHashPathwayToTexture.get(refTmpPathway);
		//fTmpZLayerValue = refHashPathwayToZLayerValue.get(refTmpPathway);
		
		refPathwayTexture.enable();
		refPathwayTexture.bind();
		//gl.glTexEnvi(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

		gl.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);

		TextureCoords texCoords = refPathwayTexture.getImageTexCoords();
		
		// Recalculate scaling factor
		fPathwayTextureAspectRatio = 
			(float)refPathwayTexture.getImageWidth() / 
			(float)refPathwayTexture.getImageHeight();								
		
		fTextureWidth = 0.0025f * (float)refPathwayTexture.getImageWidth();
		fTextureHeight = 0.0025f * (float)refPathwayTexture.getImageHeight();				
		
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, texCoords.top()); 
		gl.glVertex3f(0.0f, 0.0f, fTmpZLayerValue);			  
		gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
		gl.glVertex3f(fTextureWidth, 0.0f, fTmpZLayerValue);			 
		gl.glTexCoord2f(texCoords.right(), 0); 
		gl.glVertex3f(fTextureWidth, fTextureHeight, fTmpZLayerValue);
		gl.glTexCoord2f(0, 0); 
		gl.glVertex3f(0.0f, fTextureHeight, fTmpZLayerValue);
		gl.glEnd();	
		
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glLineWidth(1);
		gl.glBegin(GL.GL_LINE_STRIP); 
		gl.glVertex3f(0.0f, 0.0f, fTmpZLayerValue);; 
		gl.glVertex3f(fTextureWidth, 0.0f, fTmpZLayerValue);
		gl.glVertex3f(fTextureWidth, fTextureHeight, fTmpZLayerValue);
		gl.glVertex3f(0.0f, fTextureHeight, fTmpZLayerValue);
		gl.glVertex3f(0.0f, 0.0f, fTmpZLayerValue);; 				
		gl.glEnd();

		refPathwayTexture.disable();
	}
    
    protected void highlightIdenticalNodes(final GL gl) {
    	
		Pathway refTmpPathway = null;
		PathwayVertex refCurrentVertex = null;
		PathwayVertex refTmpVertex = null;
		IPathwayVertexRep refCurrentVertexRep = null;
		Iterator<PathwayVertex> iterIdenticalVertices = null;
		
		Iterator<Pathway> iterDrawnPathways = 
			refHashPathway2DisplayListNodeId.keySet().iterator();

		for (int iHighlightedNodeIndex = 0; iHighlightedNodeIndex < iArHighlightedVertices.size(); 
			iHighlightedNodeIndex++)
		{
			refCurrentVertex = ((IPathwayVertexRep)iArHighlightedVertices.
					get(iHighlightedNodeIndex)).getVertex();
					
			while(iterDrawnPathways.hasNext())
			{
				refTmpPathway = iterDrawnPathways.next();
			
				// Restore matrix
				gl.glPushMatrix();
				gl.glLoadIdentity();
				gl.glLoadMatrixf(refHashPathway2ModelMatrix.get(refTmpPathway));
				
				// Hightlight all identical nodes
				iterIdenticalVertices = refGeneralManager.getSingelton().
						getPathwayElementManager().getPathwayVertexListByName(
								refCurrentVertex.getElementTitle()).iterator();
				
				while(iterIdenticalVertices.hasNext())
				{
					refTmpVertex = iterIdenticalVertices.next();
					
					if (refTmpPathway.isVertexInPathway(refTmpVertex) == true)
					{
						//fZLayerValue = refHashPathwayToZLayerValue.get(refTmpPathway);
					
						refCurrentVertexRep = refTmpVertex.
							getVertexRepByIndex(iVertexRepIndex);
						
						if (refCurrentVertexRep != null)
						{												
							iArHighlightedVertices.add(refCurrentVertexRep);
							// Highlighted nodes in foreign pathways should get 
							// neighbor distance of 0 (normal highlig color)
							iArSelectionStorageNeighborDistance.add(0);
							
							createVertex(gl, refCurrentVertexRep, refTmpPathway);
							
//							if (!refTmpPathway.equals(refPathwayUnderInteraction))
//							{
//								connectVertices(refCurrentVertexRep, 
//										refCurrentVertex.getVertexRepByIndex(iVertexRepIndex));
//							}
						}
					}
				}
				
				gl.glPopMatrix();
			}
	    }
    }
}
