package cerberus.util.opengl;

import java.awt.Font;
import java.util.Iterator;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

import cerberus.view.gui.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;
import cerberus.view.gui.opengl.canvas.pathway.GLPathwayManager;
import cerberus.view.gui.opengl.canvas.pathway.GLPathwayTextureManager;
import cerberus.view.gui.opengl.canvas.pathway.JukeboxHierarchyLayer;

import gleem.linalg.Transform;
import gleem.linalg.Vec3f;

/**
 * Class that implements the memo pad that
 * can store pathways inclusive their selections
 * and some notes.
 * 
 * @author Marc Streit
 *
 */
public class GLPathwayMemoPad {

	private JukeboxHierarchyLayer memoPad;
	
	private GLPathwayManager refGLPathwayManager;
	
	private GLPathwayTextureManager refGLPathwayTextureManager;
	
	private TextRenderer textRenderer;
	
	public GLPathwayMemoPad(
			final GLPathwayManager refGLPathwayManager,
			final GLPathwayTextureManager refGLPathwayTextureManager) {
		
		memoPad = new JukeboxHierarchyLayer(4);
		
		this.refGLPathwayManager = refGLPathwayManager;
		this.refGLPathwayTextureManager = refGLPathwayTextureManager;
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 24), false);
		
		init();
	}
	
	private void init() {
		
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
	}
	
	public void addPathwayToMemoPad(final int iPathwayId) {
		
		memoPad.addElement(iPathwayId);
	}
	
	public void renderMemoPad(final GL gl) {
				
		gl.glLoadName(GLCanvasJukeboxPathway3D.MEMO_PAD_PICKING_ID);
		
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
			gl.glTranslatef(0, tmp, 0);
			refGLPathwayManager.renderPathway(gl, iPathwayId, false);
			gl.glTranslatef(0, -tmp, 0);
			
			gl.glPopMatrix();
		}		
	}
}
