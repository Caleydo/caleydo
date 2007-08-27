package cerberus.util.opengl;

import java.awt.Font;
import java.io.File;
import java.util.Iterator;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

//import cerberus.view.gui.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;
import cerberus.view.gui.opengl.canvas.pathway.GLPathwayManager;
import cerberus.view.gui.opengl.canvas.pathway.GLPathwayTextureManager;
import cerberus.view.gui.opengl.canvas.pathway.JukeboxHierarchyLayer;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

/**
 * Class that implements the memo pad that
 * can store pathways inclusive their selections
 * and some notes.
 * 
 * @author Marc Streit
 *
 */
public class GLPathwayMemoPad {

	private static String TRASH_BIN_PATH = "data/icons/trashcan_empty.png";
	
	public static final int MEMO_PAD_PICKING_ID = 301;
	
	public static final int MEMO_PAD_TRASH_CAN_PICKING_ID = 302;
	
	private JukeboxHierarchyLayer memoPad;
	
	private GLPathwayManager refGLPathwayManager;
	
	private GLPathwayTextureManager refGLPathwayTextureManager;
	
	private TextRenderer textRenderer;
	
	private Texture trashCanTexture;
	
	public GLPathwayMemoPad(
			final GLPathwayManager refGLPathwayManager,
			final GLPathwayTextureManager refGLPathwayTextureManager) {
		
		memoPad = new JukeboxHierarchyLayer(4);
		
		this.refGLPathwayManager = refGLPathwayManager;
		this.refGLPathwayTextureManager = refGLPathwayTextureManager;
	}
	
	public void init(final GL gl) {
		
		// Create free memo spots
		Transform transform;
		float fMemoPos = -0.5f;
		for (int iMemoIndex = 0; iMemoIndex < memoPad.getCapacity(); iMemoIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(fMemoPos, 2.2f, 0.1f));
			transform.setScale(new Vec3f(0.3f, 0.3f, 0.3f));
			memoPad.setTransformByPositionIndex(iMemoIndex,
					transform);			

			fMemoPos += 1f;
		}
		
		try {			
			trashCanTexture = TextureIO.newTexture(TextureIO
					.newTextureData(new File(TRASH_BIN_PATH), false, "PNG"));
	
		} catch (Exception e)
		{
			System.out.println("Error loading texture from " + TRASH_BIN_PATH);
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
	
	public void renderMemoPad(final GL gl) {
		
		gl.glLoadName(MEMO_PAD_PICKING_ID);
		
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(-0.7f, 2.0f, 0.0f);
		gl.glVertex3f(-0.7f, 3.0f, 0.0f);
		gl.glVertex3f(4.0f, 3.0f, 0.0f);
		gl.glVertex3f(4.0f, 2.0f, 0.0f);
		gl.glEnd();
		
		gl.glLineWidth(2);
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-0.7f, 2.0f, 0.0f);
		gl.glVertex3f(-0.7f, 3.0f, 0.0f);
		gl.glVertex3f(4.0f, 3.0f, 0.0f);
		gl.glVertex3f(4.0f, 2.0f, 0.0f);
		gl.glEnd();
		
		if (textRenderer == null)
			return;
		
		textRenderer.begin3DRendering();
		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1.0f);
		textRenderer.draw3D("MEMO PAD",
				1.3f, 
				2.05f, 
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

			refGLPathwayTextureManager.renderPathway(gl, iPathwayId,
					1, false);
			
			float tmp = refGLPathwayTextureManager.getTextureByPathwayId(
					iPathwayId).getImageHeight()
					* GLPathwayManager.SCALING_FACTOR_Y;
			gl.glTranslatef(0, tmp, 0.01f);
			refGLPathwayManager.renderPathway(gl, iPathwayId, false);
			gl.glTranslatef(0, -tmp, 0.01f);
			
			gl.glPopMatrix();
		}	
		
		renderTrashCan(gl);
	}
	
	public void renderTrashCan(final GL gl) {

		if (trashCanTexture == null)
			return;
		
		TextureCoords texCoords = trashCanTexture.getImageTexCoords();

		gl.glLoadName(MEMO_PAD_TRASH_CAN_PICKING_ID);
		
		trashCanTexture.enable();
		trashCanTexture.bind();

		gl.glColor3f(1, 1, 1);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(3.4f, 2.4f, 0.5f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(3.9f, 2.4f, 0.5f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(3.9f, 2.9f, 0.5f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(3.4f, 2.9f, 0.5f);
		gl.glEnd();

		trashCanTexture.disable();
	}
}
