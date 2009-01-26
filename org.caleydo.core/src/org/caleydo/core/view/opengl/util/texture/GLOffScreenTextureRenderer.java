package org.caleydo.core.view.opengl.util.texture;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

import com.sun.opengl.util.BufferUtil;

public class GLOffScreenTextureRenderer
{
	private static int TEXTURE_COUNT = 4;
	
	private int[] iArOffScreenTextures = new int[TEXTURE_COUNT];
	
	private float[] fArHeadPosition = new float[] {0,0};
	private float fNormalizedHeadDist = 0;
	
	private float fBucketWidth = 2f;
	private float fBucketHeight = 2f;
	private float fBucketDepth = 4.0f;
	private float fBucketBottomLeft = -1;
	private float fBucketBottomRight = 1;
	private float fBucketBottomTop = 1;
	private float fBucketBottomBottom = -1;
	
	public void init(final GL gl)
	{
		for (int i = 0; i < TEXTURE_COUNT; i++)
		{
	    	iArOffScreenTextures[i] = emptyTexture(gl);
		}
	}
	
    public void renderToTexture(GL gl, 
    		int iViewID, int iTextureIndex, int iViewWidth, int iViewHeight)
    {    	  	
        gl.glViewport(0, 0, 1024, 1024);

        gl.glLoadIdentity();

        GLU glu = new GLU();
        glu.gluLookAt(4, 4, 4.8f, 4, 4, 0, 0, 1, 0); 
                
        // RENDER VIEW CONTENT
        AGLEventListener glEventListener = (GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID));

    	gl.glColor4f(1,1,1,1);

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
//        gl.glDisable(GL.GL_TEXTURE_GEN_S);
//        gl.glDisable(GL.GL_TEXTURE_GEN_T);
		
			fArHeadPosition = GeneralManager.get().getWiiRemote().getCurrentSmoothHeadPosition();
			
			fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
			fArHeadPosition[1] *= 4;
//			fNormalizedHeadDist /= 3;s
			
			fBucketBottomLeft = -1*fArHeadPosition[0] - fBucketWidth - 1.5f;
			fBucketBottomRight = -1*fArHeadPosition[0] + fBucketWidth - 1.5f;
			fBucketBottomTop = fArHeadPosition[1] + fBucketHeight;
			fBucketBottomBottom = fArHeadPosition[1] - fBucketHeight;			

//			System.out.println("right: " +fBucketBottomRight);
			
			fNormalizedHeadDist = -1*GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f 
			+ Math.abs(fBucketBottomRight -2)/2 
			+ Math.abs(fBucketBottomTop - 2) /2;

		gl.glColor4f(1f, 1f, 1f, 1);

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
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[0]); 

	        // Top face
	        gl.glBegin(GL.GL_QUADS);
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
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        }
        
        if (stackLevel.getElementByPositionIndex(1).getContainedElementID() != -1)
        {	
        	// Background plane
//	        gl.glBegin(GL.GL_QUADS);
//			gl.glVertex3f(-fBucketWidth, fBucketHeight, fBucketDepth);
//			gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop, fNormalizedHeadDist);
//			gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
//			gl.glVertex3f(-fBucketWidth, -fBucketHeight, fBucketDepth);
//			gl.glEnd();
			
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[1]); 
	        
			// Left face
	        gl.glBegin(GL.GL_QUADS);
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
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 1);
        }
        
        if (stackLevel.getElementByPositionIndex(2).getContainedElementID() != -1)
        {
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[2]); 
			
	        // Bottom face
	        gl.glBegin(GL.GL_QUADS);
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
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 2);
        }
        
        if (stackLevel.getElementByPositionIndex(3).getContainedElementID() != -1)
        {
	        gl.glEnable(GL.GL_TEXTURE_2D);        
	        gl.glDisable(GL.GL_DEPTH_TEST);  
	        gl.glBindTexture(GL.GL_TEXTURE_2D, iArOffScreenTextures[3]); 
			
	        // Right face
	        gl.glBegin(GL.GL_QUADS);
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
	        gl.glBindTexture(GL.GL_TEXTURE_2D, 3);
        }        
    }
    
    private int emptyTexture(GL gl) 
    { 
    	// Create An Empty Texture
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
}
