/**
 * 
 */
package cerberus.view.gui.opengl.canvas.texture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.media.opengl.GLCanvas;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
//import javax.media.opengl.GLCanvas;

//import gleem.linalg.Vec3f;
//import gleem.linalg.Vec4f;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.math.statistics.histogram.HistogramData;
import cerberus.math.statistics.histogram.HistogramStatisticsSet;
import cerberus.math.statistics.histogram.StatisticHistogramType;
import cerberus.view.gui.opengl.GLCanvasStatics;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.AGLCanvasUser_OriginRotation;
import cerberus.manager.ILoggerManager.LoggerType;

/**
 * @author Michael Kalkusch
 *
 */
public class GLCanvasTexture2D 
extends AGLCanvasUser_OriginRotation 
implements IGLCanvasUser
{
	
	private boolean bUseGLWireframe = false;
	
	private boolean bGLBindTextureOnInitGL = true;
	
	private int iTextureId;
	
	private int iSetCacheId = 0;
	 
	private String sTextureFileName = "";
	
	/**
	 * Defien number of histogram slots.
	 * Default is 0 to ensure valid settings. 
	 * 
	 *  @see cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D#createHistogram(int)
	 */
	private int iCurrentHistogramLength = 0;
	
	private float [][] viewingFrame;
	
	//private int iGridSize = 40;
	
	//private float fPointSize = 1.0f;
	
	/**
	 * Color for grid (0,1,2) 
	 * grid text (3,4,5)
	 * and point color (6,7,8)
	 */
	private float[] colorGrid = { 0.1f, 0.1f , 0.9f, 
			0.1f, 0.9f, 0.1f,
			0.9f, 0.1f, 0.1f };
	
	protected float[][] fAspectRatio;
	
	protected float[] fResolution;
	
	protected ISet targetSet;
	
	protected Texture refGLTexture = null;
	
	protected boolean bEnableMipMapping = false;
	
	public String sTextureLoadFromFile = null;
	
	private static final int X = GLCanvasStatics.X;
	private static final int Y = GLCanvasStatics.Y;
	private static final int Z = GLCanvasStatics.Z;
	private static final int MIN = GLCanvasStatics.MIN;
	private static final int MAX = GLCanvasStatics.MAX;
	private static final int OFFSET = GLCanvasStatics.OFFSET;

	
	/**
	 * @param setGeneralManager
	 */
	public GLCanvasTexture2D( final IGeneralManager setGeneralManager,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( setGeneralManager, 
				iViewId,  
				iParentContainerId, 
				sLabel );
		
		fAspectRatio = new float [2][3];
		viewingFrame = new float [3][2];
		
		fAspectRatio[X][MIN] = 0.0f;
		fAspectRatio[X][MAX] = 20.0f; 
		fAspectRatio[Y][MIN] = 0.0f; 
		fAspectRatio[Y][MAX] = 20.0f; 
		
		fAspectRatio[Y][OFFSET] = 0.0f; 
		fAspectRatio[Y][OFFSET] = -2.0f; 
		
		viewingFrame[X][MIN] = -1.0f;
		viewingFrame[X][MAX] = 1.0f; 
		viewingFrame[Y][MIN] = 1.0f; 
		viewingFrame[Y][MAX] = -1.0f; 
		
		viewingFrame[Z][MIN] = -1.0f; 
		viewingFrame[Z][MAX] = -1.0f; 
		
	}
	


	protected void loadTextureFromFile( final String sTextureFromFile ) {
	
		try
		{
			refGLTexture = TextureIO.newTexture(new File(sTextureFromFile), 
					bEnableMipMapping);
		} 
			catch ( FileNotFoundException fnfe) 
		{
			System.out.println("Error: can not find fiel for texture " + sTextureFromFile);
		}
			catch ( GLException gle) 
		{
				System.out.println("Error: GLError while accessing texture from file " + 
						sTextureFromFile + "  " + gle.toString() );
		}
			catch (IOException ioe)
		{
			System.out.println("Error loading texture " + sTextureFromFile );
		}
			
		try {
			refGLTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			refGLTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		}
		catch ( GLException gle) 
		{
			System.out.println("Error: GLError while accessing texture from file " + 
					sTextureFromFile + "  " + gle.toString() );
		}
	}
	
	public void setResolution( float[] setResolution ) {
		
//		if ( fResolution.length < 6 ) {
//			throw new RuntimeException("GLCanvasMinMaxScatterPlot2D.setResolution() array must contain 3 items.");
//		}
		
		this.fResolution = setResolution;
		
		fAspectRatio[X][MIN] = fResolution[0];
		fAspectRatio[X][MAX] = fResolution[1]; 
		fAspectRatio[Y][MIN] = fResolution[2]; 
		fAspectRatio[Y][MAX] = fResolution[3]; 
		
		fAspectRatio[X][OFFSET] = fResolution[4]; 
		fAspectRatio[Y][OFFSET] = fResolution[5];
		
		viewingFrame[X][MIN] = fResolution[6];
		viewingFrame[X][MAX] = fResolution[7]; 
		viewingFrame[Y][MIN] = fResolution[8]; 
		viewingFrame[Y][MAX] = fResolution[9];
		
		viewingFrame[Z][MIN] = fResolution[10]; 
		viewingFrame[Z][MAX] = fResolution[11]; 
			
	}
	
	
	public void setFileNameForTexture( final String sTextureLoadFromFile ) {
		this.sTextureLoadFromFile = sTextureLoadFromFile;
	}
	
	
	public String getFileNameForTexture() {
		return sTextureLoadFromFile;
	}
	
	
	public void reloadTexture() {
		
		if ( sTextureLoadFromFile != null ) {
			loadTextureFromFile( sTextureLoadFromFile );
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
		
		if ( iCurrentHistogramLength > 0 ) 
		{
			reloadTexture();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void initGLCanvas( GLCanvas canvas ) {
		setInitGLDone();
		System.err.println(" Texture2D ! init( * )");
		reloadTexture();
		
		if ( bGLBindTextureOnInitGL ) {
			final GL gl = canvas.getGL();
			gl.glEnable(GL.GL_TEXTURE_2D);
//			iTextureId = genTextures_Id(gl);
//			gl.glBindTexture(GL.GL_TEXTURE_2D, iTextureId);
//			
			refGLTexture.bind();
		}
	}	
	
	private int genTextures_Id(GL gl) {
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        return tmp[0];
    }
	
	@Override
	public void renderPart(GL gl)
	{
		System.err.println(" Texture2D ! render( * )");
		
		gl.glTranslatef( 0,0, 0.01f);
	
		if ( refGLTexture == null) {
			this.reloadTexture();
		}
		
		displayHistogram( gl );
		
		//System.err.println(" MinMax ScatterPlot2D .render(GLCanvas canvas)");
	}

	
	public void update(GLAutoDrawable canvas)
	{
		System.err.println(" GLCanvasHistogram2D.update(GLCanvas canvas)");	
		
		reloadTexture();
	}

	public void destroy()
	{
		refGeneralManager.getSingelton().logMsg( "GLCanvasHistogram2D.destroy(GLCanvas canvas)  id=" + this.iUniqueId );
	}
	

  
  public void displayHistogram(GL gl) {

	    //gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);


	    if (bUseGLWireframe) {
	    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
	    }
	    
//	    else 
//	    {
//	    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
//	    }     
	    

	    // draw background
//	    gl.glDisable(GL.GL_DEPTH_TEST);
//	    drawSkyBox(gl);
//	    gl.glEnable(GL.GL_DEPTH_TEST);


//	    gl.glDisable( GL.GL_LIGHTING );
	    
	    gl.glEnable( GL.GL_LIGHTING );
	    
	    gl.glEnable(GL.GL_TEXTURE_2D);
	  
	    	
    	
    	float fNowX = viewingFrame[X][MIN];
    	float fNextX = viewingFrame[X][MAX];
    	
    	float fNowY = viewingFrame[Y][MIN];
    	float fNextY = viewingFrame[Y][MAX];
    	
    	gl.glNormal3f( 0.0f, 0.0f, 1.0f );

    	
    
//		    gl.glBegin( GL.GL_TRIANGLE_FAN );
//		    gl.glBegin( GL.GL_LINE_LOOP );
		    			  
//		
//		    	gl.glColor3f( 1, 
//		    			1 , 
//		    			0 );
//		    
//				gl.glVertex3f( fNowX, fNowY , viewingFrame[Z][MIN] );
//				gl.glVertex3f( fNextX, fNowY, viewingFrame[Z][MIN] );
//				gl.glVertex3f( fNextX, fNextY, viewingFrame[Z][MIN] );
//				gl.glVertex3f( fNowX, fNextY, viewingFrame[Z][MIN] );						
//				
//				
//				gl.glEnd();
				
				System.out.println(" TEXTURE!");
				
				if ( refGLTexture == null ) {
					System.err.println(" TEXTURE not bound!");
					return;
				}
				
			refGLTexture.bind();
			
				TextureCoords texCoords = refGLTexture.getImageTexCoords();

//				System.err.println("Height: "+(float)refPathwayTexture.getImageHeight());
//				System.err.println("Width: "+(float)refPathwayTexture.getImageWidth());
//					
//				System.err.println("Aspect ratio: " +fPathwayTextureAspectRatio);
//				System.err.println("texCoords left: " +texCoords.left());
//				System.err.println("texCoords right: " +texCoords.right());
//				System.err.println("texCoords top: " +texCoords.top());
//				System.err.println("texCoords bottom: " +texCoords.bottom());
			
				
				 
				
	    //else {
		    gl.glBegin( GL.GL_TRIANGLES );
				gl.glNormal3f( 0.0f, 0.0f, 1.0f );
				gl.glColor3f( 1,0,0 );
				
				gl.glTexCoord2f(texCoords.left(), texCoords.top()); 
				gl.glVertex3f( -1.0f, -1.0f, -0.5f );
				//gl.glColor3f( 1,0,1 );
				
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom()); 
				gl.glVertex3f( 1.0f, 1.0f, -0.5f );
				//gl.glColor3f( 0,1,0 );
				
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom()); 
				gl.glVertex3f( 1.0f, -1.0f, -0.5f );
			gl.glEnd();
//			
//			float fmin = -2.0f;
//			float fmax = 2.0f;
//			
//			float fshiftX = -1.0f;
//			float fshiftY = -2.0f;
//			
//			gl.glBegin( GL.GL_TRIANGLES );
//				gl.glNormal3f( 0.0f, 0.0f, 0.0f );
//				gl.glColor3f( 1,1,0 );
//				
//				gl.glVertex3f( fmin+fshiftX, fmax+fshiftY, 0.0f );
//				gl.glColor3f( 1,0,1 );
//				gl.glVertex3f( fmax+fshiftX, fmin+fshiftY, 0.0f );
//				gl.glColor3f( 0,1,1 );
//				gl.glVertex3f( fmax+fshiftX, fmax+fshiftY, 0.0f );
//			gl.glEnd();
	    //}
	    
		gl.glDisable( GL.GL_LIGHTING );
			
//	    gl.glEnable( GL.GL_LIGHTING );
	    
	    //gl.glMatrixMode(GL.GL_MODELVIEW);
	    //gl.glPopMatrix();
	  }
  
	public void displayChanged(GLAutoDrawable drawable, 
			final boolean modeChanged, 
			final boolean deviceChanged) {

		// TODO Auto-generated method stub
		
	}
}
