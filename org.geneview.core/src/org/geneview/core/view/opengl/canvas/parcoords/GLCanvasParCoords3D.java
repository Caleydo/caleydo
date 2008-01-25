package org.geneview.core.view.opengl.canvas.parcoords;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;

/**
 * 
 * 
 * @author Alexander Lex
 *
 */
public class GLCanvasParCoords3D extends AGLCanvasUser {

//	private IGeneralManager refGeneralManager;
	private float axisSpacing;
	
	private int iGLDisplayListIndex;
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param viewId
	 * @param parentContainerId
	 * @param label
	 */
	public GLCanvasParCoords3D(IGeneralManager refGeneralManager,
			int viewId,
			int parentContainerId,
			String label) 
	{
		super(refGeneralManager, null, viewId, parentContainerId, label);

		this.refViewCamera.setCaller(this);
		this.axisSpacing = 1;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		
		
		ISetSelection tmpSelection = alSetSelection.get(0);
		
		int[] iArTmpSelectionIDs = {13, 18, 19, 20, 33};
//		Collection<Integer> test = refGeneralManager.getSingelton().getGenomeIdManager()
//			.getIdIntListByType(13770, EGenomeMappingType.MICROARRAY_EXPRESSION_2_ACCESSION);		
		
		tmpSelection.setSelectionIdArray(iArTmpSelectionIDs);
		
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glLineWidth(1.0f);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);		
		
		iGLDisplayListIndex = gl.glGenLists(1);		
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		renderPolyLines(gl);
		gl.glEndList();
		
		// Selection Test

		
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) 
	{		
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glCallList(iGLDisplayListIndex);
		
		// dirty flag - new list auf gleichen index
		
		//renderPolyLines(gl);	
		// render coordinate system here
	}
	

	
	private void renderPolyLines(GL gl)
	{		
		if (alSetData == null)
			return;
		
		if (alSetSelection == null)
			return;
		
			
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		ArrayList<IStorage> alDataStorages = new ArrayList<IStorage>();
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
			// TODO: test
			//normalizeSet(tmpSet);
			
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				alDataStorages.add(tmpSet.getStorageByDimAndIndex(0, 0));
			}
		}		
		
		//gl.glColor3f(0.0f, 1.0f, 0.0f);	
		
		// render all polylines
		for (int iStorageCount = 0; iStorageCount < alDataStorages.size(); iStorageCount++)
		{		
			// render one polyline
			gl.glBegin(GL.GL_LINE_STRIP);		
			
			IStorage currentStorage = alDataStorages.get(iStorageCount);
			
//			for (int iCount = 0; iCount < currentStorage.getArrayFloat().length; iCount++)
//			{
//				gl.glVertex3f(iCount * axisSpacing, currentStorage.getArrayFloat()[iCount], 0.0f); 
//			
//			}
			
			int[] iArSelection = alSetSelection.get(0).getSelectionIdArray();
			for (int iCount = 0; iCount < iArSelection.length; iCount++)
			{
				gl.glVertex3f(iCount * axisSpacing, 
						currentStorage.getArrayFloat()[iArSelection[iCount]], 0.0f); 			
							
				
			}
			gl.glEnd();
			// FIXME: don't do this here, only do it as long as a set does not know it's length
			renderCoordinateSystem(gl, iArSelection.length, 1);//currentStorage.getArrayFloat().length, 1);	
		}		
	}
	

	private void renderCoordinateSystem(GL gl, int numberParameters, float maxHeight)
	{		
		
		gl.glLineWidth(3.0f);
				
		gl.glBegin(GL.GL_LINES);		
	
		gl.glVertex3f(-0.1f, 0.0f, 0.0f); 
		gl.glVertex3f(((numberParameters-1) * axisSpacing)+0.1f, 0.0f, 0.0f);
		
		//gl.glVertex3f(0.0f, 0.0f, 0.0f);
		//gl.glVertex3f(0.0f, maxHeight, 0.0f);			
		
		gl.glEnd();
		
		// draw all Y-Axis

		gl.glLineWidth(1.0f);
		gl.glBegin(GL.GL_LINES);	
		
		int count = 0;
		while (count < numberParameters)
		{
			gl.glVertex3f(count * axisSpacing, 0.0f, 0.0f);
			gl.glVertex3f(count * axisSpacing, maxHeight, 0.0f);
			count++;
		}
		
		gl.glEnd();	
		
	}
	
//	
//	private void normalizeSet(ISet mySet)
//	{
//	
//		CmdDataFilterMinMax createdCmd = (CmdDataFilterMinMax) refGeneralManager
//	    .getSingelton().getCommandManager().createCommandByType(CommandQueueSaxType.DATA_FILTER_MIN_MAX);
//		
//		createdCmd.setAttributes(mySet, StorageType.INT);
//	}
	

}
