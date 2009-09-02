package org.caleydo.core.view.opengl.util.vislink;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gleem.linalg.Vec3f;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.sun.opengl.util.BufferUtil;


/**
 * 
 * @author Oliver Pimas
 * @version 0.3 (2009-09-02)
 * 
 * VisLink offers static methods to render a connection line.
 * 
 *
 */

public class VisLink {
	
	private ArrayList<Vec3f> linePoints;
	private ArrayList<Vec3f> polygonLineVertices;
	private ArrayList<Vec3f> shadowLineVertices;
	
	private float width = 2.0f;
	
	private final int TEXTURE_SIZE = 128;
	private int viewportX = 0;
	private int viewportY = 0;
	private int viewportWidth = 640;
	private int viewportHeight = 480;
	
	/**
	 * Constructor
	 * 
	 * @param controlPoints Specifies the set of control points of which the spline is generated.
	 * @param offset Specifies the offset in controlPoints
	 */
	protected VisLink(final ArrayList<Vec3f> controlPoints, final int offset) {
		this.width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
		
		ArrayList<Vec3f> points = new ArrayList<Vec3f>(controlPoints.subList(offset, controlPoints.size()));
		if(points.size() == 2) {
			this.linePoints = points;
		}
		else {
			NURBSCurve curve = new NURBSCurve(points, 10);
			this.linePoints = curve.getCurvePoints();
		}
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param controlPoints Specifies the set of control points of which the spline is generated.
	 * @param offset Specifies the offset in controlPoints
	 * @param numberOfSegments Specifies the subintervals of the spline. Note that for
	 * n subintervals there are n+3 curve points. The begin of the curve, the end of
	 * the curve and n+1 vertices connecting the n segments.
	 */
	protected VisLink(final ArrayList<Vec3f> controlPoints, final int offset, int numberOfSegments) {
		this.width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
		
		ArrayList<Vec3f> points = new ArrayList<Vec3f>(controlPoints.subList(offset, controlPoints.size()));
		if(points.size() == 2) {
			this.linePoints = points;
		}
		else {
			NURBSCurve curve = new NURBSCurve(points, numberOfSegments);
			this.linePoints = curve.getCurvePoints();
		}
	}
	
	
	/**
	 * 		Creates a visual link. Static method.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints the control points for the NURBS spline
	 * 
	 * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * Note: For straight lines (only 2 control points), this value doesn't effect the resulting line.
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public static void renderLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, final int numberOfSegments, boolean shadow)
		throws IllegalArgumentException
	{
		if(controlPoints.size() == (offset + 2)) {
			line(gl, controlPoints.get(0), controlPoints.get(1), shadow);
		}
		else if(controlPoints.size() > (offset + 2))
		{
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			visLink.spline(gl, shadow);
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 			
	}
	
	
	/**
	 * 		Creates a visual link. Static method.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints set of control points for the NURBS spline
	 * 
	 * @param offset specifies the offset of control points
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public static void renderLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, boolean shadow)
		throws IllegalArgumentException
	{
		if(controlPoints.size() == (offset + 2))
			line(gl, controlPoints.get(offset), controlPoints.get(offset + 1), shadow);
		else if(controlPoints.size() > (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset);
			visLink.spline(gl, shadow); // NOTE: generates a NURBS spline with 10 subsegments.
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 
	}
	
	
	/**
	 * 		Creates a visual link. Static method.
	 *
	 * @param gl the GL object
	 * 
	 * @param srcPoint the lines point of origin
	 * 
	 * @param destPoint the lines end point
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */	
	public static void renderLine(final GL gl, Vec3f srcPoint, Vec3f destPoint, boolean shadow) {
		line(gl, srcPoint, destPoint, shadow);		
	}
	
		
	/**
	 * 		Called for creating a curved visual link.
	 * 		This method is not recommended when drawing visual links with higher
	 * 		width, which would case v-shaped gaps to appear at the intersections.
	 *
	 * @param gl the GL object
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected void spline(final GL gl, boolean shadow) {
		
		// line shadow
		if(shadow == true) {
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2.0f);
			gl.glBegin(GL.GL_LINE_STRIP);
			for(int i = 0; i < linePoints.size(); i++)
				gl.glVertex3f(linePoints.get(i).x(), linePoints.get(i).y(), linePoints.get(i).z());
			gl.glEnd();
		}
		
		// the spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		
		// the spline
		gl.glBegin(GL.GL_LINE_STRIP);
		for(int i = 0; i < linePoints.size(); i++)
			gl.glVertex3f(linePoints.get(i).x(), linePoints.get(i).y(), linePoints.get(i).z());
		gl.glEnd();
	}
	
	
	/**
	 * 		Called for creating a straight visual link.
	 * 		
	 * @param gl the GL object
	 * 
	 * @param vecSrcPoint the lines point of origin
	 * 
	 * @param vecDestPoint the lines end point
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected static void line(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint, boolean shadow) {
		
		// Line shadow
		if(shadow == true) {
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2.0f);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
			gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
			gl.glEnd();
		}
		
		// the line attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		
		// the line
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
		gl.glEnd();
	}
	
	
	
	
	/**
	 * 		Creates a polygon visual link. Static method.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints the control points for the NURBS spline
	 * 
	 * @param offset specifies the offset of control points
	 * 
	 * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * Note: For straight lines (only 2 control points), this value doesn't effect the resulting line.
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public static void renderPolygonLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, final int numberOfSegments, boolean shadow)
		throws IllegalArgumentException
	{
		if(controlPoints.size() >= (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			if(shadow == true)
				visLink.generateShadowLineVertices(gl);
			visLink.generatePolygonLineVertices(gl);			
			visLink.polygonLine(gl, shadow);
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 			
	}
	
	
	/**
	 * 		Creates a polygon visual link. Static method.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints the control points for the NURBS spline
	 * 
	 * @param offset specifies the offset of control points
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public static void renderPolygonLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, boolean shadow)
		throws IllegalArgumentException
	{
		if(controlPoints.size() >= (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset);
			if(shadow == true)
				visLink.generateShadowLineVertices(gl);
			visLink.generatePolygonLineVertices(gl);			
			visLink.polygonLine(gl, shadow);
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 
	}
	
	
	/**
	 * 		Creates a polygon visual link. Static method.
	 *
	 * @param gl the GL object
	 * 
	 * @param srcPoint the lines point of origin
	 * 
	 * @param destPoint the lines end point
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */	
	public static void renderPolygonLine(final GL gl, Vec3f srcPoint, Vec3f destPoint, boolean shadow) {
		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(srcPoint);
		points.add(destPoint);
		VisLink visLink = new VisLink(points, 0);
		if(shadow == true)
			visLink.generateShadowLineVertices(gl);
		visLink.generatePolygonLineVertices(gl);			
		visLink.polygonLine(gl, shadow);
	}
	
	
	/**
	 * 		generates the vertices for the polygon shadow and stores it in shadowLineVertices
	 * 
	 * @param gl The GL object
	 */
	protected void generateShadowLineVertices(GL gl) {
		this.shadowLineVertices = generatePolygonVertices(gl, linePoints, (width + 1f) );
	}
	
	
	/**
	 * 		generates the vertices for the polygon line and stores it in polygonLineVertices
	 * 
	 * @param gl The GL object
	 */
	protected void generatePolygonLineVertices(GL gl) {
		this.polygonLineVertices = generatePolygonVertices(gl, linePoints, width );
	}
	
	
	/**
	 * 		Renders a polygon line. Recommended for lines with higher width.
	 * 
	 * @param gl the GL object
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected void polygonLine(final GL gl, boolean shadow) {
		
		if(shadow == true) {
			// shadow color
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			
			// shadow
			gl.glBegin(GL.GL_QUAD_STRIP);
			for(int i = 0; i < shadowLineVertices.size(); i++)
				gl.glVertex3f(shadowLineVertices.get(i).x(), shadowLineVertices.get(i).y(), shadowLineVertices.get(i).z());
			gl.glEnd();
		}
		
		// The spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		
		// the spline
		gl.glBegin(GL.GL_QUAD_STRIP);
		for(int i = 0; i < polygonLineVertices.size(); i++)
			gl.glVertex3f(polygonLineVertices.get(i).x(), polygonLineVertices.get(i).y(), polygonLineVertices.get(i).z());
		gl.glEnd();
	}
	
	
	/** 
	 * 		Generates vertices for a polygon line from a given set of curve points.
	 * 
	 *@param gl the GL Object
	 *@param curvePoints set of curve points
	 *
	 *@return a list of vertices
	*/
	protected static ArrayList<Vec3f> generatePolygonVertices(final GL gl, ArrayList<Vec3f> points, float width)
	throws IllegalArgumentException
	{
		if(points.size() <= 0)
			throw new IllegalArgumentException( "given set of curve points is empty" );
		if(points.size() == 1)
			throw new IllegalArgumentException( "at least two points are needed for rendering a line" );
					
		ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();
		
		Vec3f dirVec = new Vec3f();
		Vec3f invDirVec = new Vec3f();
		
		for ( int i = 0; i < points.size(); i++) {	
			if(i == 0) {
				dirVec = directionVec(points.get(i), points.get(i), points.get(i+1));
				invDirVec = invDirectionVec(points.get(i), points.get(i), points.get(i+1));
			}
			else if (i == (points.size() - 1) ) {
				dirVec = directionVec(points.get(i-1), points.get(i), points.get(i));
				invDirVec = invDirectionVec(points.get(i-1), points.get(i), points.get(i));
			}
			else {
				dirVec = directionVec(points.get(i-1), points.get(i), points.get(i+1));
				invDirVec = invDirectionVec(points.get(i-1), points.get(i), points.get(i+1));
			}
			
			dirVec.normalize();
			invDirVec.normalize();
			dirVec.scale(0.01f);
			invDirVec.scale(0.01f);
			
			vertices.add(points.get(i).addScaled(width, dirVec));
			vertices.add(points.get(i).addScaled(width, invDirVec));
		}
		return vertices;
	}
	
	
	/**
	 * 		Generates a vector which is from the viewpoint normal on the line.
	 * 
	 * @param v1 point k-1
	 * @param v2 point k
	 * @param v3 point k+1
	 * 
	 * @return a direction vector
	 */
	protected static Vec3f directionVec(Vec3f prevPoint, Vec3f point, Vec3f nextPoint) {
		Vec3f straight = nextPoint.minus(prevPoint);
		Vec3f eyepoint = new Vec3f(0.0f, 0.0f, 6.0f);
		Vec3f epVec = eyepoint.minus(point);
		return new Vec3f(straight.cross(epVec));
	}
	
	
	/**
	 * 		Generates a vector which is from the viewpoint normal on the line.
	 * 		This direction vector is inverse to the one returned by directionVec(...)
	 * 
	 * @param v1 point k-1
	 * @param v2 point k
	 * @param v3 point k+1
	 * 
	 * @return a direction vector inverse to the one returned by directionVec(...)
	 */
	protected static Vec3f invDirectionVec(Vec3f prevPoint, Vec3f point, Vec3f nextPoint) {
		Vec3f straight = nextPoint.minus(prevPoint);
		Vec3f eyepoint = new Vec3f(0.0f, 0.0f, 6.0f);
		Vec3f epVec = eyepoint.minus(point);
		return new Vec3f(epVec.cross(straight));
	}
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
//	public static void renderPolygonLineWithHalo(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset) {
//        
//		int blurTexture = createBlurTexture(gl);  
//		int frameBufferObject = 0; // FIXME: implement createFrameBufferObject(GL gl)
//		
//		IntBuffer viewportBuffer = BufferUtil.newIntBuffer(4);
//		
//		// store current viewport
//    	gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBuffer);
//    	viewportX = viewportBuffer.get(0);
//    	viewportY = viewportBuffer.get(1);
//    	viewportWidth = viewportBuffer.get(2);
//    	viewportHeight = viewportBuffer.get(3);
//    	
////    	for(int i = 0; i < viewportBuffer.capacity(); i++)
////    		System.out.print(viewportBuffer.get(i) + "  ");
////    	System.out.println();
//
////        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                        // Set The Clear Color To Black
////        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);    // Clear Screen And Depth Buffer
////        gl.glLoadIdentity();
//        
//        renderToTexture(gl, blurTexture, controlPoints, frameBufferObject);           // Render To A Texture
//        
//        VisLink.polygonLine(gl, controlPoints, 0, 10, false);
//
//        drawBlur(gl, 25, 0.02f, blurTexture);                                        // Draw The Blur Effect
//	}
//	
//	
//	private static int createBlurTexture(GL gl) {                                // Create An Empty Texture
//		
//        ByteBuffer data = BufferUtil.newByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE); // Create Storage Space For Texture Data (128x128x4)
//        data.limit(data.capacity());
//
//        int[] txtnumber = new int[1];
//        gl.glGenTextures(1, txtnumber, 0);                                // Create 1 Texture
//        gl.glBindTexture(GL.GL_TEXTURE_2D, txtnumber[0]);                 // Bind The Texture
//        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_LUMINANCE, TEXTURE_SIZE, TEXTURE_SIZE, 0, GL.GL_LUMINANCE, GL.GL_UNSIGNED_BYTE, data);                        // Build Texture Using Information In data
//        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
//        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
//
//        return txtnumber[0];                                              // Return The Texture ID
//    }
//	
//	private static void viewOrtho(GL gl)                                        // Set Up An Ortho View
//    {
//        gl.glMatrixMode(GL.GL_PROJECTION);                               // Select Projection
//        gl.glPushMatrix();                                               // Push The Matrix
//        gl.glLoadIdentity();                                             // Reset The Matrix
//        gl.glOrtho(0, viewportWidth, viewportHeight, 0, -1, 1);          // Select Ortho Mode (640x480)
//        gl.glMatrixMode(GL.GL_MODELVIEW);                                // Select Modelview Matrix
//        gl.glPushMatrix();                                               // Push The Matrix
//        gl.glLoadIdentity();                                             // Reset The Matrix
//    }
//
//    private static void viewPerspective(GL gl)                                  // Set Up A Perspective View
//    {
//        gl.glMatrixMode(GL.GL_PROJECTION);                               // Select Projection
//        gl.glPopMatrix();                                                // Pop The Matrix
//        gl.glMatrixMode(GL.GL_MODELVIEW);                                // Select Modelview
//        gl.glPopMatrix();                                                // Pop The Matrix
//    }
//    
//    private static void renderToTexture(GL gl, int blurTexture, ArrayList<Vec3f> controlPoints, int frameBufferObject) {
//    	
//    	if (frameBufferObject == -1) {
//    		System.err.println("error: couldn't create framebuffer object needed for halo. rendering normal polygon line instead.");
//    		VisLink.polygonLine(gl, controlPoints, 0, 10, false);
//        }
//    	
//    	// Bind the fbo
//        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frameBufferObject);
//    	
//        // Set Our Viewport (Match Texture Size)
//        gl.glViewport(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);
//
//        // Render the line
//        VisLink.polygonLine(gl, controlPoints, 0, 10, false);
//
//        // Copy Our ViewPort To The Blur Texture (From 0,0 To 128,128... No Border)
//        gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture);
//        gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_LUMINANCE, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0);
//
//        gl.glClearColor(0.0f, 0.0f, 0.5f, 0.5f); // Set The Clear Color To Medium Blue
//    	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear The Screen And Depth Buffer
//
//        // Restore the viewport
//    	gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
////		System.out.println(viewportX + "  " + viewportY + "  " + viewportWidth + "  " + viewportHeight);
////		System.out.println("--------------------");
//    }
//    
//    private static void drawBlur(GL gl, int times, float inc, int blurTexture) {
//    	float spost = 0.0f; // Starting Texture Coordinate Offset
//    	float alpha = 0.2f; // Starting Alpha Value
//
//        // Disable AutoTexture Coordinates
////        gl.glDisable(GL.GL_TEXTURE_GEN_S);
////        gl.glDisable(GL.GL_TEXTURE_GEN_T);
//
//        gl.glEnable(GL.GL_TEXTURE_2D);                           // Enable 2D Texture Mapping
//        gl.glDisable(GL.GL_DEPTH_TEST);                          // Disable Depth Testing
//        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
//        gl.glEnable(GL.GL_BLEND);                                // Enable Blending
//        gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture);         // Bind To The Blur Texture
//        viewOrtho(gl);                                           // Switch To An Ortho View
//
//        float alphainc = alpha / times;                          // alphainc=0.2f / Times To Render Blur
//
//        gl.glBegin(GL.GL_QUADS);                                 // Begin Drawing Quads
//        for (int num = 0; num < times; num++)                    // Number Of Times To Render Blur
//        {
//            gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);               // Set The Alpha Value (Starts At 0.2)
//            gl.glTexCoord2f(0 + spost, 1 - spost);               // Texture Coordinate	( 0, 1 )
//            gl.glVertex2f(0, 0);                                 // First Vertex		(   0,   0 )
//
//            gl.glTexCoord2f(0 + spost, 0 + spost);               // Texture Coordinate	( 0, 0 )
//            gl.glVertex2f(0, viewportHeight);                    // Second Vertex	(   0, 480 )
//
//            gl.glTexCoord2f(1 - spost, 0 + spost);               // Texture Coordinate	( 1, 0 )
//            gl.glVertex2f(viewportWidth, viewportHeight);        // Third Vertex		( 640, 480 )
//
//            gl.glTexCoord2f(1 - spost, 1 - spost);               // Texture Coordinate	( 1, 1 )
//            gl.glVertex2f(viewportWidth, 0);                     // Fourth Vertex	( 640,   0 )
//
//            spost += inc;                                        // Gradually Increase spost (Zooming Closer To Texture Center)
//            alpha = alpha - alphainc;                            // Gradually Decrease alpha (Gradually Fading Image Out)
//        }
//        gl.glEnd();                                              // Done Drawing Quads
//
//        viewPerspective(gl);                                     // Switch To A Perspective View
//
//        gl.glEnable(GL.GL_DEPTH_TEST);                           // Enable Depth Testing
//        gl.glDisable(GL.GL_TEXTURE_2D);                          // Disable 2D Texture Mapping
//        gl.glDisable(GL.GL_BLEND);                               // Disable Blending
//        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);                   // Unbind The Blur Texture
//    }
    
	
}

