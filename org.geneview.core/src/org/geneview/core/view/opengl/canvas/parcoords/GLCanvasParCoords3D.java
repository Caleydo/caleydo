package org.geneview.core.view.opengl.canvas.parcoords;


import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

//import org.geneview.core.data.view.camera.IViewCamera;
import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
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
	
	public GLCanvasParCoords3D(IGeneralManager refGeneralManager,
			int viewId,
			int parentContainerId,
			String label) {

		super(refGeneralManager, null, viewId, parentContainerId, label);

		this.refViewCamera.setCaller(this);
		this.axisSpacing = 2;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#initGLCanvas(javax.media.opengl.GL)
	 */
	public void initGLCanvas(GL gl) {
	
		super.initGLCanvas(gl);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#renderPart(javax.media.opengl.GL)
	 */
	public void renderPart(GL gl) 
	{		
//		float[] graphData = {0.3f , 0.2f, 1.8f, 2.0f, 3.0f, 0.1f};
//		float[] graphData2 = {0.1f, 0.4f, 1.5f, 2.3f, 1.6f, 0.5f};
//		
//		renderCoordinateSystem(gl, graphData.length, 3);
		
		if (alSetData == null)
			return;
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		ArrayList<IStorage> alDataStorages = new ArrayList<IStorage>();
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
			
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				alDataStorages.add(tmpSet.getStorageByDimAndIndex(0, 0));
			}
		}
		
		renderCoordinateSystem(gl, alDataStorages.size(), 1);	
		
		float[] graphData = new float[3];
		graphData[0] = alDataStorages.get(0).getArrayFloat()[0];
		graphData[1] = alDataStorages.get(1).getArrayFloat()[0];
		graphData[2] = alDataStorages.get(2).getArrayFloat()[0];
		
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		renderGraphs(gl, graphData);
	}
	
	private void renderCoordinateSystem(GL gl, int numberParameters, float maxHeight)
	{
		// draw X-Axis
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glLineWidth(3.0f);
				
		gl.glBegin(GL.GL_LINES);		
	
		gl.glVertex3f(-0.1f, 0.0f, 0.0f); 
		gl.glVertex3f((numberParameters * axisSpacing)+0.1f, 0.0f, 0.0f);
		
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
	
	private void renderGraphs(GL gl, float[] graphData)
	{
		gl.glBegin(GL.GL_LINE_STRIP);
		
		int count = 0;
		while (count < graphData.length)
		{
			gl.glVertex3f(count * axisSpacing, graphData[count] / 100000f, 0.0f); // FIXME: normalize data and remove 100000 hack
			
			count++;
		}
		
		gl.glEnd();	
		
	}
	

}
