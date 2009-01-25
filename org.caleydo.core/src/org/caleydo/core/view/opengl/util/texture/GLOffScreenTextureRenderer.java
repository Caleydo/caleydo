package org.caleydo.core.view.opengl.util.texture;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.wii.WiiRemote;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;

public class GLOffScreenTextureRenderer
{
	private static int TEXTURE_COUNT = 5;
	
	private int[] iArOffScreenTextures = new int[TEXTURE_COUNT];
	
	private Texture testTexture;
	
	// FIXME: change it to private after successful testing
	public static WiiRemote wiiRemote;

	private float[] fArHeadPosition = new float[] {0,0};
	private float fNormalizedHeadDist = 0;
	
	private float fBucketWidth = 2f;
	private float fBucketHeight = 2f;
	private float fBucketDepth = 4.0f;
	private float fBucketBottomLeft = -1;
	private float fBucketBottomRight = 1;
	private float fBucketBottomTop = 1;
	private float fBucketBottomBottom = -1;
	
	private float fAspectRatio = 1;
	
	/**
	 * Constructor.
	 */
	public GLOffScreenTextureRenderer() 
	{
		wiiRemote = new WiiRemote();
		
		if (GeneralManager.get().isWiiModeActive())
		{
			wiiRemote.init();
		}
	}
	
	public void init(final GL gl)
	{
		for (int i = 0; i < TEXTURE_COUNT; i++)
		{
	    	iArOffScreenTextures[i] = emptyTexture(gl);
		}
		
		testTexture = GeneralManager.get().getResourceLoader().getTexture(
			"/home/mstreit/.caleydo/cgap.nci.nih.gov/BIOCARTA/Pathways/h_rabPathway.gif");
	}
	
    public void renderToTexture(GL gl, 
    		int iViewID, int iTextureIndex, int iViewWidth, int iViewHeight)
    {    	
    	fAspectRatio = (float)iViewHeight / iViewWidth;
    	
        gl.glViewport(0, 0, 1024, 1024);

        gl.glLoadIdentity();
		
//        gl.glMatrixMode(GL.GL_PROJECTION);
//        gl.glFrustum(0, 4, 0, 4, 0, 100);
//		gl.glMatrixMode(GL.GL_MODELVIEW);

        GLU glu = new GLU();
        glu.gluLookAt(4, 4, 4.8f, 4, 4, 0, 0, 1, 0); 
                
        // RENDER VIEW CONTENT
        AGLEventListener glEventListener = (GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID));

        gl.glTranslatef(0,1.45f,0);
        gl.glScalef(1,0.63f,1);
        glEventListener.displayRemote(gl);
        gl.glScalef(1,1/0.63f,1);
        gl.glTranslatef(0,-1.45f,0);
        
        // Bind To The Blur Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[iTextureIndex]);  

        // Copy Our ViewPort To The Blur Texture (From 0,0 To 1024,1024... No Border)
        gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 0, 0, 1024, 1024, 0);

        // Clear The Screen And Depth Buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);      

        gl.glViewport(0, 0, iViewWidth, iViewHeight);  // insert real height and width
    }
    
	public void renderRubberBucket(final GL gl, 
			RemoteLevel stackLevel, RemoteLevel focusLevel, int iRemoteRenderingViewID)
	{	
        // Disable AutoTexture Coordinates
        gl.glDisable(GL.GL_TEXTURE_GEN_S);
        gl.glDisable(GL.GL_TEXTURE_GEN_T);
		
//		if (GeneralManager.get().isWiiModeActive())
//		{
			fArHeadPosition = wiiRemote.getCurrentSmoothHeadPosition();
			
			fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
			fArHeadPosition[1] *= 4;
//			fNormalizedHeadDist /= 3;s
			
			fBucketBottomLeft = -1*fArHeadPosition[0] - fBucketWidth - 1.5f;
			fBucketBottomRight = -1*fArHeadPosition[0] + fBucketWidth - 1.5f;
			fBucketBottomTop = fArHeadPosition[1] + fBucketHeight;
			fBucketBottomBottom = fArHeadPosition[1] - fBucketHeight;			

//			System.out.println("right: " +fBucketBottomRight);
			
			fNormalizedHeadDist = -1*wiiRemote.getCurrentHeadDistance() + 7f 
			+ Math.abs(fBucketBottomRight -2)/2 
			+ Math.abs(fBucketBottomTop - 2) /2;
//		}
		
		gl.glColor4f(1.0f, 1f, 1f, 1);
		
//		fNormalizedHeadDist = 3;
//		fBucketDepth = 4;
		
//        if (focusLevel.getElementByPositionIndex(0).getContainedElementID() != -1)
//        {	
//    		int iViewID = focusLevel.getElementByPositionIndex(0).getContainedElementID();
//    		PickingManager pickingManager = GeneralManager.get().getViewGLCanvasManager().getPickingManager();
//    		
//    		gl.glPushName(pickingManager.getPickingID(iRemoteRenderingViewID,
//    				EPickingType.REMOTE_LEVEL_ELEMENT, focusLevel.getElementByPositionIndex(0).getID()));
//    		gl.glPushName(pickingManager.getPickingID(iRemoteRenderingViewID, EPickingType.VIEW_SELECTION,
//    				iViewID));
//        	
//            AGLEventListener glEventListener = (GeneralManager.get().getViewGLCanvasManager()
//    				.getGLEventListener(focusLevel.getElementByPositionIndex(0).getContainedElementID()));
//           
//            glEventListener.getViewFrustum().setBottom(fBucketBottomBottom);
//            glEventListener.getViewFrustum().setTop(fBucketBottomTop);
//            glEventListener.getViewFrustum().setLeft(fBucketBottomLeft);
//            glEventListener.getViewFrustum().setRight(fBucketBottomRight);
//            
//            gl.glTranslatef(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
//            glEventListener.displayRemote(gl);;
//            gl.glTranslatef(-fBucketBottomLeft, -fBucketBottomBottom, -fNormalizedHeadDist);
//        	
//            gl.glColor4f(1, 1, 1, 1);
//        }
        
        if (stackLevel.getElementByPositionIndex(0).getContainedElementID() != -1)
        {
//        	gl.glPushMatrix();
//        	Transform transform = stackLevel.getElementByPositionIndex(0).getTransform();
//        	Vec3f translation = transform.getTranslation();
//        	Vec3f scale = transform.getScale();
//        	Rotf rot = transform.getRotation();
//  
//    		float fPlaneWidth = 5;//(float)Math.sqrt((double)(Math.pow(fAK,2) + Math.pow(fGK,2)));
//    		Vec3f axis = new Vec3f();
//    		
//    		float angle = rot.get(axis);
//
//    		gl.glTranslatef(translation.x(), translation.y(), translation.z());
//    		gl.glRotatef(Vec3f.convertRadiant2Grad(angle), axis.x(),axis.y(),axis.z());
//    		gl.glScalef(scale.x(), scale.y(), scale.z());
//
//    		// Render plane
//    		gl.glBegin(GL.GL_QUADS);
//    		gl.glVertex3f(0, 0, 0);
//    		gl.glVertex3f(4, 0, 0);
//    		gl.glVertex3f(4, fPlaneWidth, 0);
//    		gl.glVertex3f(0, fPlaneWidth, 0);
//    		gl.glEnd();
//        	
//    		gl.glPopMatrix();
//    		gl.glColor4f(1, 1, 1, 1);
        	
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[1]); 

	        gl.glBegin(GL.GL_QUADS);
	        // Top face
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop, fNormalizedHeadDist);
	        gl.glTexCoord2f(0, 1);
			gl.glVertex3f(-fBucketWidth, fBucketHeight, fBucketDepth);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(fBucketWidth, fBucketHeight, fBucketDepth);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomTop, fNormalizedHeadDist);
			gl.glEnd();
	
	        gl.glEnable(GL.GL_DEPTH_TEST);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 1);
        }
        
        if (stackLevel.getElementByPositionIndex(1).getContainedElementID() != -1)
        {
//        	gl.glPushMatrix();
//        	Transform transform = stackLevel.getElementByPositionIndex(1).getTransform();
//        	Vec3f translation = transform.getTranslation();
//        	Vec3f scale = transform.getScale();
//        	Rotf rot = transform.getRotation();
//        	
//    		float fAK = 4 -1*fNormalizedHeadDist;
//    		float fGK = fBucketWidth + fBucketBottomLeft;
//    		float fAngle = (float) Math.atan((fGK/fAK));
//        	
//    		float fPlaneWidth = (float)Math.sqrt((double)(Math.pow(fAK,2) + Math.pow(fGK,2)));
//    		Vec3f axis = new Vec3f();
//    		
//    		float angle = rot.get(axis);
////    		System.out.println("Angle: " +Vec3f.convertRadiant2Grad(fAngle));
//    		
//    		gl.glTranslatef(translation.x(), translation.y(), translation.z());
//    		gl.glRotatef(Vec3f.convertRadiant2Grad(angle), axis.x(),axis.y(),axis.z());
//    		gl.glScalef(scale.x(), scale.y(), scale.z());
//
//    		// Render plane
////    		gl.glColor3f(1.0f, 0.5f, 0.0f);
//    		gl.glBegin(GL.GL_QUADS);
//    		gl.glVertex3f(0, -1, 0);
//    		gl.glVertex3f(0, 1, 0);
//    		gl.glVertex3f(fPlaneWidth, 1, 0);
//    		gl.glVertex3f(fPlaneWidth, -1, 0);
//    		gl.glEnd();
//        	
//    		gl.glPopMatrix();
////    		gl.glColor4f(1, 1, 1, 1);
        	
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[2]); 
			
	        gl.glBegin(GL.GL_QUADS);
			// Left face
	        gl.glTexCoord2f(0, 1);
			gl.glVertex3f(-fBucketWidth, fBucketHeight, fBucketDepth);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop, fNormalizedHeadDist);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(-fBucketWidth, -fBucketHeight, fBucketDepth);
	        gl.glEnd();
			
	        gl.glEnable(GL.GL_DEPTH_TEST);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 2);
        }
        
        if (stackLevel.getElementByPositionIndex(2).getContainedElementID() != -1)
        {
//        	gl.glPushMatrix();
//        	Transform transform = stackLevel.getElementByPositionIndex(2).getTransform();
//        	Vec3f translation = transform.getTranslation();
//        	Vec3f scale = transform.getScale();
//        	Rotf rot = transform.getRotation();
//        	
//    		float fAK = 4 -1*fNormalizedHeadDist;
//    		float fGK = fBucketWidth + fBucketBottomLeft;
//    		float fAngle = (float) Math.atan((fGK/fAK));
//        	
//    		float fPlaneWidth = (float)Math.sqrt((double)(Math.pow(fAK,2) + Math.pow(fGK,2)));
//    		Vec3f axis = new Vec3f();
//    		
//    		float angle = rot.get(axis);
////    		System.out.println("Angle: " +Vec3f.convertRadiant2Grad(fAngle));
//    		
//    		gl.glTranslatef(translation.x(), translation.y(), translation.z());
//    		gl.glRotatef(Vec3f.convertRadiant2Grad(angle), axis.x(),axis.y(),axis.z());
//    		gl.glScalef(scale.x(), scale.y(), scale.z());
//
//    		// Render plane
////    		gl.glColor3f(1.0f, 0.5f, 0.0f);
//    		gl.glBegin(GL.GL_QUADS);
//    		gl.glVertex3f(0, 0, 0);
//    		gl.glVertex3f(4, 0, 0);
//    		gl.glVertex3f(4, fPlaneWidth, 0);
//    		gl.glVertex3f(0, fPlaneWidth, 0);
//    		gl.glEnd();
//        	
//    		gl.glPopMatrix();
////    		gl.glColor4f(1, 1, 1, 1);        	
        	
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[3]); 
			
	        gl.glBegin(GL.GL_QUADS);
	        // Bottom face
			gl.glTexCoord2f(0, 1);			
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(-fBucketWidth, -fBucketHeight, fBucketDepth);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(fBucketWidth, -fBucketHeight, fBucketDepth);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomBottom, fNormalizedHeadDist);
	        gl.glEnd();
			
	        gl.glEnable(GL.GL_DEPTH_TEST);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 3);
        }
        
        if (stackLevel.getElementByPositionIndex(3).getContainedElementID() != -1)
        {
//        	gl.glPushMatrix();
//        	Transform transform = stackLevel.getElementByPositionIndex(3).getTransform();
//        	Vec3f translation = transform.getTranslation();
//        	Vec3f scale = transform.getScale();
//        	Rotf rot = transform.getRotation();
//        	
//    		float fAK = 4 -1*fNormalizedHeadDist;
//    		float fGK = fBucketWidth - fBucketBottomRight;
//    		float fAngle = (float) Math.atan((fGK/fAK));
//        	
//    		float fPlaneWidth = (float)Math.sqrt((double)(Math.pow(fAK,2) + Math.pow(fGK,2)));
//    		Vec3f axis = new Vec3f();
//    		
//    		float angle = rot.get(axis);
//
//    		gl.glTranslatef(translation.x(), translation.y(), translation.z());
//    		gl.glRotatef(Vec3f.convertRadiant2Grad(angle), axis.x(),axis.y(),axis.z());
//    		gl.glScalef(scale.x(), scale.y(), scale.z());
//
//    		// Render plane
////    		gl.glColor3f(1.0f, 0.5f, 0.0f);
//    		gl.glBegin(GL.GL_QUADS);
//    		gl.glVertex3f(0, -1, 0);
//    		gl.glVertex3f(0, 1, 0);
//    		gl.glVertex3f(fPlaneWidth, 1, 0);
//    		gl.glVertex3f(fPlaneWidth, -1, 0);
//    		gl.glEnd();
//        	
//    		gl.glPopMatrix();
////    		gl.glColor4f(1, 1, 1, 1);
        	
        	
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[4]); 
			
	        gl.glBegin(GL.GL_QUADS);
	        // Right face
			gl.glTexCoord3f(0, 1, 0);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomTop, fNormalizedHeadDist); 
			gl.glTexCoord3f(1, 1, 0);
			gl.glVertex3f(fBucketWidth, fBucketHeight, fBucketDepth);
			gl.glTexCoord3f(1, 0, 0);
			gl.glVertex3f(fBucketWidth, -fBucketHeight, fBucketDepth);
			gl.glTexCoord3f(0, 0, 0);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomBottom, fNormalizedHeadDist);
	        gl.glEnd();
						
	        gl.glEnable(GL.GL_DEPTH_TEST);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glDisable(GL.GL_TEXTURE_2D);
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 4);
        }        
      }
    
//    public void renderToTexture(GL gl) // Renders To A Texture
//    {
//        gl.glViewport(0, 0, 1024, 1024);    // Set Our Viewport (Match Texture Size)
//        
//        gl.glLoadIdentity();  // Reset The Modelview Matrix
//        
//        // Eye Position (0,5,50) Center Of Scene (0,0,0), Up Adhesion and Diapedesis of Lymphocytes (BioCarta)On Y Axis
//        GLU glu = new GLU();
//        glu.gluLookAt(0, 5, 50, 0, 0, 0, 0, 1, 0); 
//        
////        gl.glColor4f(0,0,1,1);
////        gl.glBegin(GL.GL_QUADS);
////        gl.glVertex3f(6,0,0);
////        gl.glVertex3f(10,15,0);
////        gl.glVertex3f(0,10,0);
////        gl.glVertex3f(0,0,0);
////        gl.glEnd();
//        
//		testTexture.enable();
//		testTexture.bind();
//
//		TextureCoords texCoords = testTexture.getImageTexCoords();
//		
//		gl.glBegin(GL.GL_QUADS);
////
//		gl.glColor4f(1.0f, 1.0f, 1.0f,1);
//		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
//		gl.glVertex3f(0, 0, 0);
//		gl.glTexCoord2f(texCoords.left(), texCoords.top());
//		gl.glVertex3f(0, 20, 0);
//		gl.glTexCoord2f(texCoords.right(), texCoords.top());
//		gl.glVertex3f(20, 20, 0);
//		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
//		gl.glVertex3f(20, 0, 0);
//		gl.glEnd();
//		
//		testTexture.disable();
//        
//        // Bind To The Blur Texture
//        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[0]);  
//
//        // Copy Our ViewPort To The Blur Texture (From 0,0 To 1024,1024... No Border)
//        gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 0, 0, 1024, 1024, 0);
//
////        gl.glClearColor(1f, 1.0f, 1.0f, 1f); // Set The Clear Color To Medium Blue
//        // Clear The Screen And Depth Buffer480
//        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);      
//
//        gl.glViewport(0, 0, 1600, 1000);  // insert real height and width
//    }
    
//    public void renderOffScreenContent(GL gl)
//    {
//        // Disable AutoTexture Coordinates
//        gl.glDisable(GL.GL_TEXTURE_GEN_S);
//        gl.glDisable(GL.GL_TEXTURE_GEN_T);
//
//        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable 2D Texture Mapping
//        gl.glDisable(GL.GL_DEPTH_TEST);  // Disable Depth Testing
////        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);  // Set Blending Mode
//        gl.glEnable(GL.GL_BLEND);  // Enable Blending
//        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[0]); // Bind To The Blur Texture
////        viewOrtho(gl);  // Switch To An Ortho View
//
//        gl.glBegin(GL.GL_QUADS);  // Begin Drawing Quads
//
//        gl.glColor4f(1f, 1, 1, 1); // Set The Alpha Value (Starts At 0.2)
//        gl.glTexCoord2f(0, 1); // Texture Coordinate  ( 0, 1 )
//        gl.glVertex3f(0, 0, 4);  // First Vertex    (   0,   0 )
//
//        // Texture Coordinate  ( 0, 0 )
//        gl.glTexCoord2f(1, 0); 
//        gl.glVertex3f(0, 4, 4);  // Second Vertex  (   0, 480 )
//
//        // Texture Coordinate  ( 1, 0 )
//        gl.glTexCoord2f(1, 0);  
//        gl.glVertex3f(4, 4, 4);  // Third Vertex    ( 640, 480 )
//
//        // Texture Coordinate  ( 1, 1 )
//        gl.glTexCoord2f(1, 1);  
//        gl.glVertex3f(4, 0, 4);  // Fourth Vertex  ( 640,   0 )
//
//        gl.glEnd();        // Done Drawing Quads
//
////        viewPerspective(gl);  // Switch To A Perspective View
//
//        gl.glEnable(GL.GL_DEPTH_TEST);  // Enable Depth Testing
//        gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
//        gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
//        gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
//        gl.glDisable(GL.GL_BLEND);  // Disable Blending
//        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);  // Unbind The Blur Texture
//    }
    
    private int emptyTexture(GL gl) {  // Create An Empty Texture
        // Create Storage Space For Texture Data (1024x1024x4)
        ByteBuffer data = BufferUtil.newByteBuffer(1024 * 1024 * 4); 
        data.limit(data.capacity());

        int[] txtnumber = new int[1];
        gl.glGenTextures(1, txtnumber, 0);  // Create 1 Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, txtnumber[0]);  // Bind The Texture
        
        // Build Texture Using Information In data
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 4, 1024, 1024, 0,
                GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, data);    
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        return txtnumber[0];  // Return The Texture ID
    }
    
    public WiiRemote getWiiRemote()
    {
    	return wiiRemote;
    }
}
