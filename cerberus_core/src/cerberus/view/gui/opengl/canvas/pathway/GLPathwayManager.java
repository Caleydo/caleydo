package cerberus.view.gui.opengl.canvas.pathway;

import javax.media.opengl.GL;

import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.pathway.Pathway;
import cerberus.data.view.rep.pathway.renderstyle.PathwayRenderStyle;
import cerberus.manager.IGeneralManager;

/**
 * 
 * @author Marc Streit
 *
 */
public class GLPathwayManager {

	private IGeneralManager refGeneralManager;
	
	private static final float SCALING_FACTOR_X = 0.0025f;
	private static final float SCALING_FACTOR_Y = 0.0025f;
	
	private PathwayRenderStyle refRenderStyle;
	
	/**
	 * Constructor.
	 */
	public GLPathwayManager(final IGeneralManager refGeneralManager, final GL gl) {
		
		this.refGeneralManager = refGeneralManager;
		
		refRenderStyle = new PathwayRenderStyle();
		
		buildEnzymeNodeDisplayList(gl);
	}
	
	public void buildPathwayDisplayList(final GL gl, final int iPathwayID) {

//		Pathway refTmpPathway = (Pathway)refGeneralManager.getSingelton().getPathwayManager().
//			getItem(iPathwayID]);
//		
//		// Creating display list for pathways
//		int iVerticesDisplayListId = gl.glGenLists(1);
//
////			refHashDisplayListNodeId2Pathway.put(iVerticesDisplayListId, refTmpPathway);	
////			refHashPathway2DisplayListNodeId.put(refTmpPathway, iVerticesDisplayListId);
//		
//		gl.glNewList(iVerticesDisplayListId, GL.GL_COMPILE);	
//		extractVertices(gl, refTmpPathway);
//		gl.glEndList();
	}

	private void buildEnzymeNodeDisplayList(final GL gl) {

		// Creating display list for node cube objects
		int iEnzymeNodeDisplayListId = gl.glGenLists(1);
			
		gl.glNewList(iEnzymeNodeDisplayListId, GL.GL_COMPILE);
		fillNodeDisplayList(gl);		
	    gl.glEndList();
	}

	private void fillNodeDisplayList(final GL gl) {
		
		float fPathwayNodeWidth = refRenderStyle.getEnzymeNodeWidth();
		float fPathwayNodeHeight = refRenderStyle.getEnzymeNodeHeight();
		
		gl.glBegin(GL.GL_QUADS);
		
        // FRONT FACE
		gl.glNormal3f( 0.0f, 0.0f, 1.0f);	
		// Top Right Of The Quad (Front)
        gl.glVertex3f(-fPathwayNodeWidth , -fPathwayNodeHeight, 0.015f);		
        // Top Left Of The Quad (Front)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);			
        // Bottom Left Of The Quad (Front)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
		// Bottom Right Of The Quad (Front)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);

        // BACK FACE
        gl.glNormal3f( 0.0f, 0.0f,-1.0f);
        // Bottom Left Of The Quad (Back)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
        // Bottom Right Of The Quad (Back)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
        // Top Right Of The Quad (Back)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);			
        // Top Left Of The Quad (Back)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);			

		// TOP FACE
        gl.glNormal3f( 0.0f, 1.0f, 0.0f);	
        // Top Right Of The Quad (Top)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
        // Top Left Of The Quad (Top)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
        // Bottom Left Of The Quad (Top)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
        // Bottom Right Of The Quad (Top)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);			

        // BOTTOM FACE
        gl.glNormal3f( 0.0f,-1.0f, 0.0f);	
        // Top Right Of The Quad (Bottom)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
        // Top Left Of The Quad (Bottom)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
        // Bottom Left Of The Quad (Bottom)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);
        // Bottom Right Of The Quad (Bottom)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);			

        // RIGHT FACE
        gl.glNormal3f( 1.0f, 0.0f, 0.0f);	
        // Top Right Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);
        // Top Left Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);
        // Bottom Left Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);
        // Bottom Right Of The Quad (Right)
        gl.glVertex3f(fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);			
        
        // LEFT FACE
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);	
        // Top Right Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight, 0.015f);	
        // Top Left Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, fPathwayNodeHeight,-0.015f);	
        // Bottom Left Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight,-0.015f);	
        // Bottom Right Of The Quad (Left)
        gl.glVertex3f(-fPathwayNodeWidth, -fPathwayNodeHeight, 0.015f);	
        
        gl.glEnd();
	}
}
