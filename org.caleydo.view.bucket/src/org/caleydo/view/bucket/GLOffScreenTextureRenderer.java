package org.caleydo.view.bucket;

import java.nio.ByteBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

public class GLOffScreenTextureRenderer {
	private static int TEXTURE_COUNT = 4;

	private int[] iArOffScreenTextures = new int[TEXTURE_COUNT];

	public void init(final GL2 gl) {
		for (int i = 0; i < TEXTURE_COUNT; i++) {
			iArOffScreenTextures[i] = emptyTexture(gl);
		}
	}

	public void renderToTexture(GL2 gl, int viewID, int iTextureIndex, int iViewWidth,
			int iViewHeight) {
		gl.glViewport(0, 0, 1024, 1024);

		gl.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluLookAt(4, 4, 4.8f, 4, 4, 0, 0, 1, 0);

		// RENDER VIEW CONTENT
		AGLView glEventListener = GeneralManager.get().getViewGLCanvasManager()
				.getGLView(viewID);

		ViewFrustum viewFrustum = glEventListener.getViewFrustum();

		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), 0, 0);
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
				viewFrustum.getTop() - viewFrustum.getBottom(), 0);
		gl.glVertex3f(0, viewFrustum.getTop() - viewFrustum.getBottom(), 0);
		gl.glEnd();

		gl.glColor4f(1, 1, 1, 1);

		gl.glTranslatef(0, 1.45f, 0);
		gl.glScalef(1, 0.63f, 1);
		glEventListener.displayRemote(gl);
		gl.glScalef(1, 1 / 0.63f, 1);
		gl.glTranslatef(0, -1.45f, 0);

		// Bind To The Blur Texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, iArOffScreenTextures[iTextureIndex]);

		// Copy Our ViewPort To The Blur Texture (From 0,0 To 1024,1024... No
		// Border)
		gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, 0, 0, 1024, 1024, 0);

		// Clear The Screen And Depth Buffer
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glViewport(0, 0, iViewWidth, iViewHeight);
	}

	public void renderRubberBucket(final GL2 gl, RemoteLevel stackLevel,
			BucketLayoutRenderStyle bucketLayoutRenderStyle, GLBucket glRemoteRendering) {
		gl.glColor4f(1f, 1f, 1f, 1f);

		float fBucketBottomLeft = bucketLayoutRenderStyle.getBucketBottomLeft();
		float fBucketBottomRight = bucketLayoutRenderStyle.getBucketBottomRight();
		float fBucketBottomTop = bucketLayoutRenderStyle.getBucketBottomTop();
		float fBucketBottomBottom = bucketLayoutRenderStyle.getBucketBottomBottom();

		float fNormalizedHeadDist = bucketLayoutRenderStyle.getHeadDistance();

		if (stackLevel.getElementByPositionIndex(0).getGLView() != null) {
			// gl.glPushMatrix();
			// Transform transform =
			// stackLevel.getElementByPositionIndex(0).getTransform();
			// Vec3f translation = transform.getTranslation();
			// Vec3f scale = transform.getScale();
			// Rotf rot = transform.getRotation();
			//
			// float fPlaneWidth = 5;//(float)Math.sqrt((double)(Math.pow(fAK,2)
			// + Math.pow(fGK,2)));
			// Vec3f axis = new Vec3f();
			//
			// float angle = rot.get(axis);
			//
			// gl.glTranslatef(translation.x(), translation.y(),
			// translation.z());
			// gl.glRotatef(Vec3f.convertRadiant2Grad(angle),
			// axis.x(),axis.y(),axis.z());
			// gl.glScalef(scale.x(), scale.y(), scale.z());
			//
			// // Render plane
			// gl.glBegin(GL2.GL_QUADS);
			// gl.glVertex3f(0, 0, 0);
			// gl.glVertex3f(4, 0, 0);
			// gl.glVertex3f(4, fPlaneWidth, 0);
			// gl.glVertex3f(0, fPlaneWidth, 0);
			// gl.glEnd();
			//
			// gl.glPopMatrix();
			// gl.glColor4f(1, 1, 1, 1);

			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, iArOffScreenTextures[0]);

			// Top face
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop, fNormalizedHeadDist);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH,
					BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH,
					BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomTop, fNormalizedHeadDist);
			gl.glEnd();

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		}

		if (stackLevel.getElementByPositionIndex(1).getGLView() != null) {
			// Background plane
			// gl.glBegin(GL2.GL_QUADS);
			// gl.glVertex3f(-fBucketWidth, fBucketHeight, fBucketDepth);
			// gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop,
			// fNormalizedHeadDist);
			// gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom,
			// fNormalizedHeadDist);
			// gl.glVertex3f(-fBucketWidth, -fBucketHeight, fBucketDepth);
			// gl.glEnd();

			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, iArOffScreenTextures[1]);

			// Left face
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 1f);
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH,
					BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomTop, fNormalizedHeadDist);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH,
					-BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glEnd();

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 1);
		}

		if (stackLevel.getElementByPositionIndex(2).getGLView() != null) {
			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, iArOffScreenTextures[2]);

			// Bottom face
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(-BucketLayoutRenderStyle.BUCKET_WIDTH,
					-BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH,
					-BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glEnd();

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 2);
		}

		if (stackLevel.getElementByPositionIndex(3).getGLView() != null) {
			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, iArOffScreenTextures[3]);

			// Right face
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord3f(0, 1, 0);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomTop, fNormalizedHeadDist);
			gl.glTexCoord3f(1, 1, 0);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH,
					BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord3f(1, 0, 0);
			gl.glVertex3f(BucketLayoutRenderStyle.BUCKET_WIDTH,
					-BucketLayoutRenderStyle.BUCKET_HEIGHT,
					BucketLayoutRenderStyle.BUCKET_DEPTH);
			gl.glTexCoord3f(0, 0, 0);
			gl.glVertex3f(fBucketBottomRight, fBucketBottomBottom, fNormalizedHeadDist);
			gl.glEnd();

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 3);
		}
	}

	private int emptyTexture(GL2 gl) {
		// Create An Empty Texture
		// Create Dimension Space For Texture Data (1024x1024x4)
		ByteBuffer data = ByteBuffer.allocate(1024 * 1024 * 4);
		data.limit(data.capacity());

		int[] txtnumber = new int[1];
		gl.glGenTextures(1, txtnumber, 0); // Create 1 Texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, txtnumber[0]); // Bind The Texture

		// Build Texture Using Information In data
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 4, 1024, 1024, 0, GL2.GL_RGBA,
				GL2.GL_UNSIGNED_BYTE, data);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

		return txtnumber[0]; // Return The Texture ID
	}
}
