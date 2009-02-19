package org.caleydo.core.view.opengl.util.texture;

import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.renderstyle.layout.BucketLayoutRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import com.sun.opengl.util.BufferUtil;

public class GLOffScreenTextureRenderer
{
	private static int TEXTURE_COUNT = 4;
	
	private int[] iArOffScreenTextures = new int[TEXTURE_COUNT];
	
	private float fAspectRatio = 0;
	
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
    	fAspectRatio = (float)iViewHeight / (float)iViewWidth;
    	
    	gl.glViewport(0, 0, 1024, (int)(1024));

        gl.glLoadIdentity();

        GLU glu = new GLU();
        glu.gluLookAt(4, 4, 4.8f, 4, 4, 0, 0, 1, 0); 
        
        // RENDER VIEW CONTENT
        AGLEventListener glEventListener = (GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID));

        IViewFrustum viewFrustum = glEventListener.getViewFrustum();
        
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), 0, 0);
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getTop()
				- viewFrustum.getBottom(), 0);
		gl.glVertex3f(0, viewFrustum.getTop() - viewFrustum.getBottom(), 0);
		gl.glEnd();

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

        gl.glViewport(0, 0, iViewWidth, iViewHeight);
    }
    
	public void renderRubberBucket(final GL gl, 
			RemoteLevel stackLevel, BucketLayoutRenderStyle bucketLayoutRenderStyle,
			GLRemoteRendering glRemoteRendering)
	{	
		gl.glColor4f(1f, 1f, 1f, 1f);
		
		float fBucketBottomLeft = bucketLayoutRenderStyle.getBucketBottomLeft();
		float fBucketBottomRight = bucketLayoutRenderStyle.getBucketBottomRight();
		float fBucketBottomTop = bucketLayoutRenderStyle.getBucketBottomTop();
		float fBucketBottomBottom = bucketLayoutRenderStyle.getBucketBottomBottom();
		
		float fNormalizedHeadDist = bucketLayoutRenderStyle.getHeadDistance();
				
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
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH, 
				BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH, BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
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
	        gl.glTexCoord2f(0, 1f);
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH, BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop, fNormalizedHeadDist);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH, -BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
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
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH, -BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH, -BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
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
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH, BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord3f(1, 0, 0);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH, -BucketLayoutRenderStyle.BUCKET_HEIGHT, BucketLayoutRenderStyle.BUCKET_DEPTH);
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
