/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

//import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.manager.IGeneralManager;
import cerberus.math.statistics.histogram.StatisticHistogramType;
import cerberus.math.statistics.minmax.MinMaxDataInteger;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasHistogram2D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser
{
	
	private StatisticHistogramType enumCurrentHistogramMode = 
		StatisticHistogramType.REGULAR_LINEAR;
	  	
	private MinMaxDataInteger doMinMaxData;
	
	private int iCurrentHistogramMode = 0;
	
	private int iCurrentHistogramLength = 100;
	
	private int iSetCacheId = 0;
	
	protected int[] iResolution;
	
	protected ISet targetSet;
	
	//private SetMultiDim refSet = null;
	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasHistogram2D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		doMinMaxData = new MinMaxDataInteger(2);  
	}

	public void setResolution( int[] iResolution ) {
		this.iResolution = iResolution;
		
		if ( iResolution.length < 3 ) {
			throw new RuntimeException("GLCanvasScatterPlot2D.setResolution() array must contain 3 items.");
		}
	}
	
	
	 public void toggleMode() {
		  
		  iCurrentHistogramMode++;	  
		  if ( iCurrentHistogramMode > 2 ) {
			  iCurrentHistogramMode = 0;
		  }
		  
		  enumCurrentHistogramMode = 
			  StatisticHistogramType.getTypeByIndex(iCurrentHistogramMode);	  
		  
		  System.out.println(" TOGGLE MODE: " + 
				  Integer.toString(iCurrentHistogramMode) + "  -->" +
				  enumCurrentHistogramMode.toString() + " DONE");
		  
		  doMinMaxData.useSet( targetSet );
		  
		  iSetCacheId = targetSet.getCacheId();
	  }
	  
	  public int getHistogramLength() {
		  return iCurrentHistogramLength;
	  }
	  
	  public void setHistogramLength( final int iSetLegth ) {
		  if (( iSetLegth > 5 )&&(iSetLegth < 10000 )) {
			  iCurrentHistogramLength = iSetLegth;
			  
			  doMinMaxData.useSet( targetSet );
			  
			  iSetCacheId = targetSet.getCacheId();
		  }
		  else {
			  
			  System.out.println("exceed range [3..10000]");
			  
//			  throw new RuntimeException("setHistogramLength(" +
//					  Integer.toString(iSetLegth) + ") exceeded range [3..10000]");
		  }
	  } 
	
	public void setTargetSetId( final int iTargetCollectionSetId ) {
		
		targetSet = 
			refGeneralManager.getSingelton().getSetManager(
					).getItemSet( iTargetCollectionSetId );
		
		if ( targetSet == null ) {
			refGeneralManager.getSingelton().logMsg(
					"GLCanvasScatterPlot2D.setTargetSetId(" +
					iTargetCollectionSetId + ") failed, because Set is not registed!");
		}
		
		refGeneralManager.getSingelton().logMsg(
				"GLCanvasScatterPlot2D.setTargetSetId(" +
				iTargetCollectionSetId + ") done!");
	}
	
	 public void setSet(SetMultiDim setRefSet) {
			targetSet = setRefSet;
			
			/** force update... */
			iSetCacheId = targetSet.getCacheId() - 1;
		}
 
	 
	@Override
	public void renderPart(GL gl)
	{
		
		if  ( targetSet != null ) 
		{	
			//gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			//gl.glDisable(GL.GL_LIGHTING);


				if (targetSet.getReadToken()) {

					if (targetSet.getDimensions() < 1) {
						System.out
								.println("GLCanvasHistogram2D.renderPart()  Can not use a ISet with only one dimension!");
					}

					IStorage refStorageX = this.targetSet.getStorageByDimAndIndex(0, 0);
					IStorage refStorageY = this.targetSet.getStorageByDimAndIndex(1, 0);

					int[] i_dataValuesX = refStorageX.getArrayInt();
					int[] i_dataValuesY = refStorageY.getArrayInt();

					if ((i_dataValuesX != null) && (i_dataValuesY != null)) {

						if (targetSet.hasCacheChanged(iSetCacheId)) {

							doMinMaxData.useSet( targetSet );
							
							System.out.println(" UPDATED!");
							
							iSetCacheId = targetSet.getCacheId();
						}
						// System.out.print("+");				

						IVirtualArrayIterator iterX = targetSet.getVirtualArrayByDimAndIndex(
								0, 0).iterator();
						IVirtualArrayIterator iterY = targetSet.getVirtualArrayByDimAndIndex(
								1, 0).iterator();

						if (!doMinMaxData.isValid()) {
							doMinMaxData.updateData();
						}

						float fMinX = -0.7f;
						float fMaxX = 0.7f;

						float fMinY = -0.7f;
						float fMaxY = 0.7f;

						float fIncX = (fMaxX - fMinX)
								/ (float) (doMinMaxData.getMax(0) - doMinMaxData.getMin(0));
						float fIncY = (fMaxY - fMinY) 
								/ (float) (doMinMaxData.getMax(1) - doMinMaxData.getMin(1));

						gl.glNormal3f(1.0f, 0.05f, 0.05f);
						gl.glPointSize( 1.5f );
						
						gl.glBegin(GL.GL_POINTS);

						while ((iterX.hasNext()) && (iterY.hasNext())) {

							float x = (float) i_dataValuesX[iterX.next()] * fIncX + fMinX;
							float y = (float) i_dataValuesY[iterY.next()] * fIncY + fMinY;
							gl.glVertex3f(x, y, 0.0f);

						} // end while: (( iterX.hasNext() )&&( iterY.hasNext() ))
							// {

						gl.glEnd();

						gl.glColor3f(0.1f, 0.1f, 1.0f);
						gl.glBegin(GL.GL_LINE_LOOP);
						gl.glVertex3f(fMinX, fMinY, 0.0f);
						gl.glVertex3f(fMaxX, fMinY, 0.0f);
						gl.glVertex3f(fMaxX, fMaxY, 0.0f);
						gl.glVertex3f(fMinX, fMaxY, 0.0f);
						gl.glEnd();
					} // end: if ((i_dataValuesX != null) && (i_dataValuesY != null)) {

					targetSet.returnReadToken();					

			} // end: if ( this.refSet != null ) {

			// else {
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glNormal3f(0.0f, 0.0f, 1.0f);
			gl.glColor3f(1, 0, 0);
			gl.glVertex3f(-1.0f, -1.0f, -0.5f);
			gl.glColor3f(1, 0, 1);
			gl.glVertex3f(1.0f, 1.0f, -0.5f);
			gl.glColor3f(0, 1, 0);
			gl.glVertex3f(1.0f, -1.0f, -0.5f);
			gl.glEnd();
			// }

			gl.glEnable(GL.GL_LIGHTING);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPopMatrix();
		  
		}
		else 
		{
			
		}

		gl.glBegin(GL.GL_TRIANGLES); // Drawing using triangles
		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set the color to red
		gl.glVertex3f(0.0f, -2.0f, 0.0f); // Top
		gl.glColor3f(0.0f, 1.0f, 1.0f); // Set the color to green
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left
		gl.glColor3f(1.0f, 1.0f, 0.0f); // Set the color to blue
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right
		gl.glEnd(); // Finish drawing the triangle

		float fX_inc = 1.0f / (float) iResolution[0];
		float fY_inc = 1.0f / (float) iResolution[1];
		
		float fX_init = 0.0f;
		
		float fX = fX_init;
		float fY = 0.0f;
		
		gl.glTranslatef(0.0f, 0.0f, -0.5f); // Move right 3 units	
		
		for ( int i=0; i < this.iResolution[0]; i++ ) {
			
			float fY_next = fY + fY_inc;
			
			for ( int j=0; j < this.iResolution[1]; j++ ) {
				
				float fX_next = fX + fX_inc;
				
				gl.glColor3f(fX * fY, 0.2f, 1 - fX); // Set the color to blue one time only
				
				gl.glBegin(GL.GL_TRIANGLES); // Draw a quad
				gl.glVertex3f(fX, fY, 0.0f); // Top left
				gl.glVertex3f(fX_next, fY, 0.0f); // Top right
				gl.glVertex3f(fX_next, fY_next, 0.0f); // Bottom right
				gl.glEnd(); // Done drawing the quad
				
				fX = fX_next;
			}
			
			fX = fX_init;
			fY = fY_next;
		}
		
		//System.err.println(" GLCanvasHistogram2D.render(GLCanvas canvas)");
	}

	public void update(GLAutoDrawable canvas)
	{
		// TODO Auto-generated method stub
		System.err.println(" GLCanvasHistogram2D.update(GLCanvas canvas)");
	}

	public void destroy()
	{
		refGeneralManager.getSingelton().logMsg( "GLCanvasHistogram2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId );
	}
	
	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
}
