package org.geneview.core.view.opengl.util;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.io.File;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.view.EPickingType;
import org.geneview.core.view.opengl.canvas.AGLCanvasUser;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayManager;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayTextureManager;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

/**
 * Class that implements the memo pad that
 * can store pathways inclusive their selections
 * and some notes.
 * 
 * @author Marc Streit
 *
 */
public class GLPathwayMemoPad {

	private static String TRASH_BIN_PATH = "resources/icons/trashcan_empty.png";

	public static final int MEMO_PAD_SELECTION = 4;
	public static final int MEMO_PAD_PICKING_ID = 1;
	public static final int MEMO_PAD_TRASH_CAN_PICKING_ID = 1;
	
	private static float SCALING_FACTOR_MEMO_PAD = 0.3f;
	
	private JukeboxHierarchyLayer memoPad;
	
	private IGeneralManager generalManager;
	
	private GLPathwayManager gLPathwayManager;
	
	private GLPathwayTextureManager gLPathwayTextureManager;
	
	private TextRenderer textRenderer;
	
	private Texture trashCanTexture;
	
	public GLPathwayMemoPad(
			final IGeneralManager generalManager,
			final GLPathwayManager gLPathwayManager,
			final GLPathwayTextureManager gLPathwayTextureManager) {
		
		memoPad = new JukeboxHierarchyLayer(generalManager,
				4, SCALING_FACTOR_MEMO_PAD, gLPathwayTextureManager);
		
		this.generalManager = generalManager;
		this.gLPathwayManager = gLPathwayManager;
		this.gLPathwayTextureManager = gLPathwayTextureManager;
	}
	
	public void init(final GL gl) {
		
		// Create free memo spots
		Transform transform;
		float fMemoPos = -0.5f;
		for (int iMemoIndex = 0; iMemoIndex < memoPad.getCapacity(); iMemoIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(fMemoPos, -2f, 0.1f));
			transform.setScale(new Vec3f(
					SCALING_FACTOR_MEMO_PAD,
					SCALING_FACTOR_MEMO_PAD,
					SCALING_FACTOR_MEMO_PAD));
			memoPad.setTransformByPositionIndex(iMemoIndex,
					transform);			

			fMemoPos += 1f;
		}
		
		try {			
	
			if (this.getClass().getClassLoader().getResource(TRASH_BIN_PATH) != null)
			{
				trashCanTexture = TextureIO.newTexture(TextureIO
						.newTextureData(this.getClass().getClassLoader().getResourceAsStream(TRASH_BIN_PATH), false, "PNG"));
			}
			else
			{
				trashCanTexture = TextureIO.newTexture(TextureIO
						.newTextureData(new File(TRASH_BIN_PATH), false, "PNG"));
			}
			
		} catch (Exception e)
		{
			System.out.println("GLPathwayMemoPad.init() Error loading texture from " + TRASH_BIN_PATH);
			e.printStackTrace();
		}		
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 24), false);
	}
	
	public void addPathwayToMemoPad(final int iPathwayId) {
		
		if (memoPad.containsElement(iPathwayId))
			return;
		
		memoPad.addElement(iPathwayId);
	}
	
	public void removePathwayFromMemoPad(final int iPathwayId) {
		
		memoPad.removeElement(iPathwayId);
	}
	
	public void renderMemoPad(final GL gl, 
			final AGLCanvasUser containingView) {
		
		gl.glPushName(generalManager.getSingelton().getViewGLCanvasManager().getPickingManager()
				.getPickingID(containingView.getId(), EPickingType.MEMO_PAD_SELECTION, MEMO_PAD_PICKING_ID));
		
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(-0.7f, -1.5f, 0.0f);
		gl.glVertex3f(-0.7f, -2.3f, 0.0f);
		gl.glVertex3f(4.0f, -2.3f, 0.0f);
		gl.glVertex3f(4.0f, -1.5f, 0.0f);
		gl.glEnd();
		
		gl.glLineWidth(2);
		gl.glColor3f(0.4f, 0.4f, 0.4f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-0.7f, -1.5f, 0.0f);
		gl.glVertex3f(-0.7f, -2.3f, 0.0f);
		gl.glVertex3f(4.0f, -2.3f, 0.0f);
		gl.glVertex3f(4.0f, -1.5f, 0.0f);
		gl.glEnd();
		
		gl.glPopName();
		
		if (textRenderer == null)
			return;
		
		textRenderer.begin3DRendering();
		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1.0f);
		textRenderer.draw3D("MEMO PAD",
				1.3f, 
				-2.2f, 
				0.01f,
				0.005f);  // scale factor
		textRenderer.end3DRendering();
		
		Iterator<Integer> iterMemoPathwayIndex = 
			memoPad.getElementList().iterator();
		
		while (iterMemoPathwayIndex.hasNext())
		{
			int iPathwayId = iterMemoPathwayIndex.next();

			gl.glPushMatrix();

			Transform transform = memoPad.getTransformByElementId(iPathwayId);
			
			Vec3f translation = transform.getTranslation();
			gl.glTranslatef(translation.x(), translation.y(), translation.z());

			Vec3f scale = transform.getScale();
			gl.glScalef(scale.x(), scale.y(), scale.z());

			gLPathwayTextureManager.renderPathway(gl, containingView, 
					iPathwayId, 1f, false);
			
			float tmp = gLPathwayTextureManager.getTextureByPathwayId(
					iPathwayId).getImageHeight()
					* GLPathwayManager.SCALING_FACTOR_Y;
			gl.glTranslatef(0, tmp, 0.01f);
			gLPathwayManager.renderPathway(gl, iPathwayId, false);
			gl.glTranslatef(0, -tmp, 0.01f);
			
			gl.glPopMatrix();
		}	
		
		renderTrashCan(gl, containingView);
	}
	
	public void renderTrashCan(final GL gl,
			final AGLCanvasUser containingView) {

		if (trashCanTexture == null)
			return;
		
		TextureCoords texCoords = trashCanTexture.getImageTexCoords();

		gl.glPushName(generalManager.getSingelton().getViewGLCanvasManager().getPickingManager()
				.getPickingID(containingView.getId(), EPickingType.MEMO_PAD_SELECTION, MEMO_PAD_TRASH_CAN_PICKING_ID));
		
		trashCanTexture.enable();
		trashCanTexture.bind();

		gl.glColor3f(1, 1, 1);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(3.4f, -2.1f, 0.5f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(3.9f, -2.1f, 0.5f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(3.9f, -1.6f, 0.5f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(3.4f, -1.6f, 0.5f);
		gl.glEnd();

		trashCanTexture.disable();

		gl.glPopName();
	}
}
