package org.caleydo.core.view.opengl.util;

import gleem.linalg.Mat4f;
import gleem.linalg.Vec3f;

import java.util.EnumMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.GeneralRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.PickingManager;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */

public class GLToolboxRenderer 
{
	protected final static float ELEMENT_LENGTH = 0.2f;
	protected final static float ELEMENT_SPACING = 0.02f;
	
	protected Vec3f vecLeftPoint;
	protected JukeboxHierarchyLayer layer;
	protected boolean bIsCalledLocally;
	protected boolean bRenderLeftToRight;
	
	protected IGeneralManager generalManager;
	protected PickingManager pickingManager; 
	protected int iRemoteViewID;
	protected int iContainingViewID;
	
	protected float fRenderLenght;
	protected float fOverallRenderLength;
	
	EnumMap<EIconTextures, Texture> mapIconTextures;
	
	protected GLIconTextureManager iconTextureManager;
	protected GeneralRenderStyle renderStyle;
	
	
	/**
	 * Constructor
	 * 
	 * @param vecLeftPoint is the bottom left point if bRenderLeftToRight
	 * 			is true, else the top left point
	 * @param layer 
	 * @param bRenderLeftToRight true if it should be rendered left to right,
	 * 			false if top to bottom
	 */
	public GLToolboxRenderer(final GL gl,
			final IGeneralManager generalManager,
			final int iContainingViewID,
			final Vec3f vecLeftPoint,			
			final boolean bRenderLeftToRight,
			final GeneralRenderStyle renderStyle)
	{
		this.generalManager = generalManager;
		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
		this.iContainingViewID = iContainingViewID;
		this.vecLeftPoint = vecLeftPoint;

		this.bRenderLeftToRight = bRenderLeftToRight;
		this.renderStyle = renderStyle;
		
		this.layer = null;
		this.iRemoteViewID = -1;
		iconTextureManager = new GLIconTextureManager(gl);
	}
	
	public GLToolboxRenderer(final GL gl,
			final IGeneralManager generalManager,
			final int iContainingViewID,
			final int iRemoteViewID,
			final Vec3f vecLeftPoint,			
			final JukeboxHierarchyLayer layer,
			final boolean bRenderLeftToRight,
			final GeneralRenderStyle renderStyle)
	{
		
		this(gl, generalManager, iContainingViewID, 
				vecLeftPoint, bRenderLeftToRight, renderStyle);
		
		this.layer = layer;
		this.iRemoteViewID = iRemoteViewID;
	}
	
	/**
	 * 	
	 * @param gl the gl of the context, remote gl when called remote
	 */
	public void render(final GL gl)
	{
//		Vec3f camPos = new Vec3f();
//		Vec3f camUp = new Vec3f();
//		
//		getCameraVectors(gl, camPos, camUp);
//	
//		gl.glPushMatrix();
//		
//        Vec3f look = new Vec3f();
//        Vec3f right = new Vec3f();
//        Vec3f up = new Vec3f();
//        look.sub(camPos, vecLeftPoint);
//        look.normalize();
//
//        right.cross(camUp, look);
//        up.cross(look, right);
//
//        gl.glMultMatrixf(new float[]{
//        		right.x(), right.y(), right.z(), 0.0F, 
//        		up.x(), up.y(), up.z(), 0.0F, 
//        		look.x(), look.y(), look.z(), 0.0F, 
//        		vecLeftPoint.x(), vecLeftPoint.y(), vecLeftPoint.z(), 1}, 0);
		
		if(layer != null)
		{
//			addIcon(gl, iRemoteViewID, EPickingType.BUCKET_MOVE_HIERARCHY_UP_ICON_SELECTION, 
//					iContainingViewID, EIconTextures.ARROW_LEFT);
//			addIcon(gl, iRemoteViewID, EPickingType.BUCKET_REMOVE_ICON_SELECTION, 
//					iContainingViewID, EIconTextures.ARROW_LEFT);
//			addIcon(gl, iRemoteViewID, EPickingType.BUCKET_SWITCH_ICON_SELECTION, 
//					iContainingViewID, EIconTextures.ARROW_LEFT);
		}
		fOverallRenderLength = fRenderLenght;
		fRenderLenght = 0;
				
//		gl.glPopMatrix();
	}
	
	protected void addIcon(final GL gl, 
			int iContainingViewID, 
			EPickingType ePickingType, 
			int iIconID,
			EIconTextures eIconTexture)
	{		
		Texture tempTexture = iconTextureManager.getIconTexture(eIconTexture);
		tempTexture.enable();
		tempTexture.bind();
		
		TextureCoords texCoords = tempTexture.getImageTexCoords();
		
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushName(pickingManager.getPickingID(iContainingViewID, ePickingType, iIconID));	
		
		gl.glBegin(GL.GL_POLYGON);
		if(bRenderLeftToRight)
		{
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x(), vecLeftPoint.y(), vecLeftPoint.z());
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x() + renderStyle.getButtonWidht(), vecLeftPoint.y(), vecLeftPoint.z());
			gl.glTexCoord2f(texCoords.right(), texCoords.top()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x() + renderStyle.getButtonWidht(), vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());
			gl.glTexCoord2f(texCoords.left(), texCoords.top()); 
			gl.glVertex3f(fRenderLenght + vecLeftPoint.x(), vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());		
		}
		else
		{
			gl.glVertex3f(vecLeftPoint.x(), fRenderLenght + vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x() + renderStyle.getButtonWidht(), fRenderLenght + vecLeftPoint.y(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x() + renderStyle.getButtonWidht(), fRenderLenght + vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());
			gl.glVertex3f(vecLeftPoint.x(), fRenderLenght + vecLeftPoint.y() + renderStyle.getButtonWidht(), vecLeftPoint.z());	
		
		}
		gl.glEnd();	
		gl.glPopName();
		tempTexture.disable();
		fRenderLenght = fRenderLenght + renderStyle.getButtonWidht() + renderStyle.getButtonSpacing();
	}


//    public void getCameraVectors(final GL gl, Vec3f camPos, Vec3f camUp) {
//    	
//        float[] matrix = new float[16];
//
//        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, matrix, 0);
//
//        camPos.set(-matrix[12], -matrix[13], -matrix[14]);
//        camUp.set(matrix[1], matrix[5], matrix[9]);
//        matrix[12] = matrix[13] = matrix[14] = 0;
//
////        Mat4f view = new Mat4f(matrix);
//        
//    	float[] temp = new float[16];
//    	temp[0] = matrix[0];
//    	temp[1] = matrix[1];
//    	temp[2] = matrix[2];
//    	temp[3] = matrix[3];
//    	temp[4] = matrix[4];
//    	temp[5] = matrix[5];
//    	temp[6] = matrix[6];
//    	temp[7] = matrix[7];
//    	temp[8] = matrix[8];
//    	temp[9] = matrix[9];
//    	temp[10] = matrix[10];
//    	temp[11] = matrix[11];
//    	temp[12] = matrix[12];
//    	temp[13] = matrix[13];
//    	temp[14] = matrix[14];
//    	temp[15] = matrix[15];
//    	
//    	matrix[1] = temp[4];	matrix[4] = temp[1];
//    	matrix[2] = temp[8];	matrix[8] = temp[2];
//    	matrix[6] = temp[9];	matrix[9] = temp[6];
//
//    	//camPos = view * camPos;
//
//		camPos.setX(matrix[0] * camPos.x() + matrix[4] * camPos.y() + matrix[8] * camPos.z() + matrix[12]);
//		camPos.setY(matrix[1] * camPos.x() + matrix[5] * camPos.y() + matrix[9] * camPos.z() + matrix[13]);
//		camPos.setZ(matrix[2] * camPos.x() + matrix[6] * camPos.y() + matrix[10] * camPos.z() + matrix[14]);
//    }
	
	public void updateLayer(final JukeboxHierarchyLayer layer)
	{
		this.layer = layer;
	}
	
	public JukeboxHierarchyLayer getContainingLayer() 
	{
		return layer;
	}
}
