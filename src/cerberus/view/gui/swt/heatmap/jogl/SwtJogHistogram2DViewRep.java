/**
 * 
 */
package cerberus.view.gui.swt.heatmap.jogl;

import java.util.Vector;

import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;

import cerberus.data.collection.ISet;
import cerberus.manager.IGeneralManager;
//import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.base.AJoglContainerViewRep;
import cerberus.view.gui.swt.base.ISwtJoglContainerViewRep;
//import cerberus.util.exception.CerberusRuntimeException;
//import cerberus.util.exception.CerberusExceptionType;

//import cerberus.math.statistics.histogram.IHistogramStatistic;
//import cerberus.math.statistics.histogram.HistogramData;
import cerberus.math.statistics.histogram.HistogramStatisticInteger;
//import cerberus.math.statistics.histogram.StatisticHistogramType;


/**
 * @author Michael Kalkusch
 *
 */
public class SwtJogHistogram2DViewRep 
extends AJoglContainerViewRep 
implements ISwtJoglContainerViewRep, IView, GLEventListener
{
	
//	private boolean bHistogramIsValid = false;
	
	protected HistogramStatisticInteger refHistogramCreator;

	protected Dimension heatmapDim;
	
	protected Vector <ISet> vecSet;
	
	protected ISet refSet = null;
	
	public SwtJogHistogram2DViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
		
		vecSet = new Vector <ISet> (); 
		
		heatmapDim = new Dimension( 10, 10 );
		
		refHistogramCreator = new HistogramStatisticInteger( 200 );
		

	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#initView()
	 */
	public void initView()
	{
		/**
		 * Handle attributes..
		 */
				
		refSet = refGeneralManager.getSingelton().getSetManager()
			.getItemSet( 0 );
		
		assert false : "Need ID from set from XML file!";
			
		System.out.println( this.toString() );
		
		this.retrieveNewGUIContainer();
		this.addGLEventListener( this );
		
		if ( joglAnimator == null ) {
			joglAnimator = new Animator( refGLCanvas );
		}
		
		//init( refGLCanvas );

	}

	/* (non-Javadoc)
	 * @see cerberus.view.gui.IView#drawView()
	 */
	public void drawView()
	{
		
		renderGL( this.refGLCanvas );

		System.out.println(" draw.." );
	}

	
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		
		strBuf.append( "SWT Heatmap init: ");
		strBuf.append( "dim=" );
		strBuf.append( heatmapDim.toString() );
		strBuf.append( " Set="); 
		
		if ( refSet == null ) {
			strBuf.append( "-null- ");
		}
		else 
		{
			strBuf.append( refSet.getId() );
			strBuf.append( " label=" );
			strBuf.append( refSet.getLabel() );
		}
		
		return strBuf.toString();
	}

	/**
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
		gl.glShadeModel(GL.GL_SMOOTH);           // Enables Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		gl.glClearDepth(1.0f);                   // Depth Buffer Setup
		gl.glEnable(GL.GL_DEPTH_TEST);           // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL);            // The Type Of Depth Test To Do
        
        /* Really Nice Perspective Calculations */
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  
	}

	/**
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable)
	{
		renderGL( drawable );
	}

	/**
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height)
	{

	}

	/** 
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged)
	{

	}
	
	
	/**
	 * Does the rendering.
	 * 
	 * @param drawable
	 */
	private void renderGL(GLAutoDrawable drawable) {
		
	
		//refSet = (ISet) this.refGeneralManager.getItem( 70100 );		
		
		GL gl = drawable.getGL();	

		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );	// Clear The Screen And The Depth Buffer
		gl.glLoadIdentity();					// Reset The Current Modelview Matrix

		gl.glTranslatef(-1.5f,0.0f,-6.0f);		
		
		gl.glDisable(GL.GL_LIGHTING);
		
		gl.glColor3f( 1.0f, 0, 0);		
		
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		 gl.glVertex3f( 0, 1, 0 );
		 gl.glVertex3f( -1, 0, 0 );
		 gl.glVertex3f( -1, -1, 0 );
		 gl.glVertex3f( 0, -1, 0 );
		gl.glEnd();
		
//		  
//		if ( ! bHistogramIsValid ) {
//			
//			refHistogramCreator.addDataValues( 
//					refSet.getStorageByDimAndIndex(0,0).getArrayInt() );
//			
//			refHistogramCreator.setIntervalEqualSpaced( 100, 
//					StatisticHistogramType.REGULAR_LINEAR,
//					true, 0,0 );
//			
//			refHistogramCreator.updateHistogram();			
//			
//			bHistogramIsValid = true;
//		}
//		
//		//int iPrimitive = 0;
//		
//		//gl.glDisable(GL.GL_LIGHTING);
//		
////		float blue[] = { 0.2f, 0.2f, 1.0f, 1.0f };
////		
////		gl.glNewList(iPrimitive, GL.GL_COMPILE);
////			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, blue);
////			gl.glShadeModel(GL.GL_FLAT);
////			gl.glNormal3f(0.0f, 0.0f, 1.0f);
////			gl.glBegin(GL.GL_QUAD_STRIP);
////			 gl.glVertex3f( 0,0,0 );
////			 gl.glVertex3f( 1,0,0 );
////			 gl.glVertex3f( 0,1,0 );
////			 gl.glVertex3f( 1,1,0 );
////			gl.glEnd(); 
////	    gl.glEndList();
//	      
//		//final int iCounterRows = refHistogramCreator.getRowWidth();
//		
//		final int[] iCounterPerRow = 
//			refHistogramCreator.getHistogramIntervallsToInt();
//		
//		boolean bAlternateColor = true;
//		
//		final int iHistogramCell_width_X = 5;
//		//final int iHistogramCell_height_Y = 10;
//		
////		
////		final  int iHistogramCell_offsetX = 10;
////		final  int iHistogramCell_offsetY = 10
////			+ refHistogramCreator.getMaxCountPerRow()*iHistogramCell_height_Y;
//		
//		final  int iHistogramCell_incX = 5;
//		final  int iHistogramCell_incY = 10;	
//		
////		int iHistogramCell_PosX = iHistogramCell_offsetX;
////		int iHistogramCell_PosY = iHistogramCell_offsetY;
//		
//		final float fScaleXY = 0.0035f;
//		
//		float fRatioX = fScaleXY * iHistogramCell_incX;
//		float fRatioY = fScaleXY * iHistogramCell_incY;
//		
//		float fWidthX = fScaleXY * iHistogramCell_width_X;
//		//float fHeightY = fScaleXY * iHistogramCell_height_Y;
//		
//		float fYMaxHalf = fRatioY * refHistogramCreator.getMaxValuesInAllIntervalls() * 0.5f;
//		
//		float fOffsetX = (iCounterPerRow.length) * 0.5f * fRatioX;
//		
//		float fX1 = -fOffsetX;
//		float fX2 = fX1 + fWidthX;	
//		float fY1 = -fYMaxHalf;
//		
//		//gl.glRotatef( 0.7f, 0,0, 1 );
//		
//		for ( int i=0; i<iCounterPerRow.length; i++ ) {
//			
//			if ( bAlternateColor ) {
//				gl.glColor3f( 0, 1, 0 );
//				bAlternateColor = false;
//			}
//			else {
//				gl.glColor3f( 1, 0, 0 );
//				 bAlternateColor = true;
//			}
//			
//			//gl.glLoadIdentity();
//			
//			float fY2 = fY1 + fRatioY * (iCounterPerRow[i]);
//			
//			//gl.glTranslatef( iX1, iY1, -1 );
//			
//			gl.glBegin(GL.GL_TRIANGLE_STRIP);
//			 gl.glVertex3f( fX1, fY1, 0 );
//			 gl.glVertex3f( fX2, fY1, 0 );
//			 gl.glVertex3f( fX1, fY2, fY2*0.2f );
//			 gl.glVertex3f( fX2, fY2, fY2*0.2f );
//			gl.glEnd();
//			
//			fX1 += fRatioX;
//			fX2 = fX1 + fWidthX;	
//			
//			//gl.glCallList( iPrimitive );
//
//		} // end for...
//		
//		
//		gl.glBegin(GL.GL_LINE_LOOP);
//		 gl.glVertex3f( -fOffsetX, -fYMaxHalf, 0.0f );
//		 gl.glVertex3f( fOffsetX, -fYMaxHalf, 0.0f );
//		 gl.glVertex3f( fOffsetX, fYMaxHalf, 0.0f );
//		 gl.glVertex3f( -fOffsetX, fYMaxHalf, 0.0f );
//		gl.glEnd();
//		
////		g.drawLine( iHistogramCell_offsetX,
////				iHistogramCell_offsetY,
////				iHistogramCell_offsetX + 
////					((int) (iCounterPerRow.length+1)*iHistogramCell_incX ) + 
////					iHistogramCell_width_X,
////				iHistogramCell_offsetY );
	}
}
