package org.caleydo.core.view.opengl.util.vislink;

import java.util.ArrayList;

import javax.media.opengl.GL;

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
	private int antiAliasingQuality = 5;
	
	private final int TEXTURE_SIZE = 128;
	private int viewportX = 0;
	private int viewportY = 0;
	private int viewportWidth = 640;
	private int viewportHeight = 480;
	
	private int[] blurTexture;
	private int[] frameBufferObject;
	
	
	/**
	 * Constructor
	 * 
	 * @param controlPoints Specifies the set of control points of which the spline is generated.
	 * @param offset Specifies the offset in controlPoints
	 */
	protected VisLink(final ArrayList<Vec3f> controlPoints, final int offset) {
		this.width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
		this.antiAliasingQuality = ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY;
		
		this.blurTexture = new int[1];
		this.frameBufferObject = new int[1];
		
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
		this.antiAliasingQuality = ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY;
		
		this.blurTexture = new int[1];
		this.frameBufferObject = new int[1];
		
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
	public static void renderPolygonLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, final int numberOfSegments, boolean shadow, boolean antiAliasing)
		throws IllegalArgumentException
	{
		if(controlPoints.size() >= (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			if(antiAliasing == true)
				visLink.polygonLineAA(gl, shadow);
			else
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
	public static void renderPolygonLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, boolean shadow, boolean antiAliasing)
		throws IllegalArgumentException
	{
		if(controlPoints.size() >= (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset);
			if(antiAliasing == true)
				visLink.polygonLineAA(gl, shadow);
			else
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
	public static void renderPolygonLine(final GL gl, Vec3f srcPoint, Vec3f destPoint, boolean shadow, boolean antiAliasing) {
		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(srcPoint);
		points.add(destPoint);
		VisLink visLink = new VisLink(points, 0);
		if(antiAliasing == true)
			visLink.polygonLineAA(gl, shadow);
		else
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
			generateShadowLineVertices(gl);
			
			// shadow color
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			
			// shadow
			gl.glBegin(GL.GL_QUAD_STRIP);
			for(int i = 0; i < shadowLineVertices.size(); i++)
				gl.glVertex3f(shadowLineVertices.get(i).x(), shadowLineVertices.get(i).y(), shadowLineVertices.get(i).z());
			gl.glEnd();
		}
		
		generatePolygonLineVertices(gl);
		
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
	
	
	/**
	 * 		Renders a polygon line with AA. Recommended for lines with higher width.
	 * 
	 * @param gl The GL object
	 * @param cycles Specifies the quality of AA
	 * @param shadow Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected void polygonLineAA(final GL gl, boolean shadow) {
		
		float red = 0f;
		float green = 0f;
		float blue = 0f;
		float alpha = 0f;
		float alphaChange = 0f;
		float unit = 0f;
		float lineWidth = 0f;
		
		ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();
		
		if(shadow == true) {
			red = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR[0];
			green = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR[1];
			blue = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR[2];
			alpha = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR[3];
			alphaChange = alpha / antiAliasingQuality;
			unit = (width+1f) / antiAliasingQuality;
			lineWidth = (width+1f) - (((antiAliasingQuality - 1) * unit) / 2);
			
			for(int j = 1; j <= antiAliasingQuality; j++) {
				// The spline attributes
				gl.glColor4fv(new float[]{red, green, blue, alpha}, 0);
				
				vertices.clear();
				vertices = generatePolygonVertices(gl, linePoints, lineWidth);
				
				// the spline
				gl.glBegin(GL.GL_QUAD_STRIP);
				for(int i = 0; i < vertices.size(); i++)
					gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
				gl.glEnd();
				
				alpha -= alphaChange;
				lineWidth += unit;
			}
		}
		
		red = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[0];
		green = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[1];
		blue = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[2];
		alpha = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[3];
		alphaChange = alpha / antiAliasingQuality;
		unit = width / antiAliasingQuality;
		lineWidth = width - (((antiAliasingQuality - 1) * unit) / 2);
		
//		System.out.println("alpha=" + alpha + "  alphaChange=" + alphaChange + "  unit=" + unit + "  lineWidth=" + lineWidth);
		
		for(int j = 1; j <= antiAliasingQuality; j++) {
			// The spline attributes
			gl.glColor4fv(new float[]{red, green, blue, alpha}, 0);
			
			vertices.clear();
			vertices = generatePolygonVertices(gl, linePoints, lineWidth);
			
			// the spline
			gl.glBegin(GL.GL_QUAD_STRIP);
			for(int i = 0; i < vertices.size(); i++)
				gl.glVertex3f(vertices.get(i).x(), vertices.get(i).y(), vertices.get(i).z());
			gl.glEnd();
			
			alpha -= alphaChange;
			lineWidth += unit;
		}
	}
	
	
	
//---------------------------------------------------------------------------------------------------------------------
// Halo ...
//---------------------------------------------------------------------------------------------------------------------
	
	
	
	public static void renderPolygonLineWithHalo(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset)
		throws IllegalArgumentException
	{
		if(controlPoints.size() >= (offset + 2)) {
			VisLink visLink = new VisLink(controlPoints, offset, 10);
			visLink.generatePolygonLineVertices(gl);			
			visLink.polygonLineWithHalo(gl); // FIXME
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" );
	}
	
	
	protected void polygonLineWithHalo(final GL gl) {
        
		this.blurTexture[0] = createBlurTexture(gl);  
		this.frameBufferObject[0] = createFrameBufferObject(gl); // FIXME: this method is buggy
		
		if (frameBufferObject[0] == -1) {
    		System.err.println("error: couldn't create framebuffer object needed for halo. rendering normal polygon line instead.");
    		polygonLine(gl, false);
    		return;
        }
			
		generatePolygonLineVertices(gl);
		
		IntBuffer viewportBuffer = BufferUtil.newIntBuffer(4);
		
		// store current viewport
    	gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBuffer);
    	this.viewportX = viewportBuffer.get(0);
    	this.viewportY = viewportBuffer.get(1);
    	this.viewportWidth = viewportBuffer.get(2);
    	this.viewportHeight = viewportBuffer.get(3);
    	
//    	for(int i = 0; i < viewportBuffer.capacity(); i++)
//    		System.out.print(viewportBuffer.get(i) + "  ");
//    	System.out.println();
    	
    	renderToTexture(gl);
    	
    	polygonLine(gl, false);
    	drawBlur(gl, 25, 0.02f, blurTexture[0]);
        
//    	drawLineWithHalo(gl);
    	
    	gl.glDeleteTextures(1, blurTexture, 0);
        // Delete the FBO
        gl.glDeleteFramebuffersEXT(1, frameBufferObject, 0);
	}
	
	
	private int createBlurTexture(GL gl) {                                // Create An Empty Texture
		
        ByteBuffer data = BufferUtil.newByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE); // Create Storage Space For Texture Data (128x128x4)
        data.limit(data.capacity());

        int[] txtnumber = new int[1];
        gl.glGenTextures(1, txtnumber, 0);                                // Create 1 Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, txtnumber[0]);                 // Bind The Texture
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_LUMINANCE, TEXTURE_SIZE, TEXTURE_SIZE, 0, GL.GL_LUMINANCE, GL.GL_UNSIGNED_BYTE, data);                        // Build Texture Using Information In data
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        return txtnumber[0];                                              // Return The Texture ID
    }
	
	private void viewOrtho(GL gl)                                        // Set Up An Ortho View
    {
        gl.glMatrixMode(GL.GL_PROJECTION);                               // Select Projection
        gl.glPushMatrix();                                               // Push The Matrix
        gl.glLoadIdentity();                                             // Reset The Matrix
        gl.glOrtho(0, viewportWidth, viewportHeight, 0, -1, 1);          // Select Ortho Mode (640x480)
        gl.glMatrixMode(GL.GL_MODELVIEW);                                // Select Modelview Matrix
        gl.glPushMatrix();                                               // Push The Matrix
        gl.glLoadIdentity();                                             // Reset The Matrix
    }

    private void viewPerspective(GL gl)                                  // Set Up A Perspective View
    {
        gl.glMatrixMode(GL.GL_PROJECTION);                               // Select Projection
        gl.glPopMatrix();                                                // Pop The Matrix
        gl.glMatrixMode(GL.GL_MODELVIEW);                                // Select Modelview
        gl.glPopMatrix();                                                // Pop The Matrix
    }
    
    private void renderToTexture(GL gl) {
    	
    	// if fbo couldn't be created, we can't render to texture and therefore can't use halo
    	if (frameBufferObject[0] == -1) {
    		System.err.println("error: couldn't create framebuffer object needed for halo.");
    		return;
        }
    	
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frameBufferObject[0]);
    	
        // Set Our Viewport (Match Texture Size)
        gl.glViewport(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        polygonLine(gl, false);

        // Copy Our ViewPort To The Blur Texture (From 0,0 To 128,128... No Border)
        gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]);
        gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_LUMINANCE, 0, 0, TEXTURE_SIZE, TEXTURE_SIZE, 0);
        
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0); // restore default fb

        // Restore the viewport
    	gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
//		System.out.println(viewportX + "  " + viewportY + "  " + viewportWidth + "  " + viewportHeight);
//		System.out.println("--------------------");
    }
    
    private void drawBlur(GL gl, int times, float inc, int blurTexture) {
    	
    	// if fbo couldn't be created, we can't render to texture and therefore can't use halo
    	if (frameBufferObject[0] == -1) {
    		System.err.println("error: couldn't create framebuffer object needed for halo.");
    		return;
        }
    	
    	float spost = 0.0f; // Starting Texture Coordinate Offset
    	float alpha = 0.2f; // Starting Alpha Value

        // Disable AutoTexture Coordinates
    	gl.glDisable(GL.GL_TEXTURE_GEN_S);
    	gl.glDisable(GL.GL_TEXTURE_GEN_T);

        gl.glEnable(GL.GL_TEXTURE_2D);                           // Enable 2D Texture Mapping
        gl.glDisable(GL.GL_DEPTH_TEST);                          // Disable Depth Testing
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);              // Set Blending Mode
        gl.glEnable(GL.GL_BLEND);                                // Enable Blending
        gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture);         // Bind To The Blur Texture
        viewOrtho(gl);                                           // Switch To An Ortho View

        float alphainc = alpha / times;                          // alphainc=0.2f / Times To Render Blur

        gl.glBegin(GL.GL_QUADS);                                 // Begin Drawing Quads
        for (int num = 0; num < times; num++)                    // Number Of Times To Render Blur
        {
            gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);               // Set The Alpha Value (Starts At 0.2)
            gl.glTexCoord2f(0 + spost, 1 - spost);               // Texture Coordinate	( 0, 1 )
            gl.glVertex2f(0, 0);                                 // First Vertex		(   0,   0 )

            gl.glTexCoord2f(0 + spost, 0 + spost);               // Texture Coordinate	( 0, 0 )
            gl.glVertex2f(0, viewportHeight);                    // Second Vertex	(   0, 480 )

            gl.glTexCoord2f(1 - spost, 0 + spost);               // Texture Coordinate	( 1, 0 )
            gl.glVertex2f(viewportWidth, viewportHeight);        // Third Vertex		( 640, 480 )

            gl.glTexCoord2f(1 - spost, 1 - spost);               // Texture Coordinate	( 1, 1 )
            gl.glVertex2f(viewportWidth, 0);                     // Fourth Vertex	( 640,   0 )

            spost += inc;                                        // Gradually Increase spost (Zooming Closer To Texture Center)
            alpha = alpha - alphainc;                            // Gradually Decrease alpha (Gradually Fading Image Out)
        }
        gl.glEnd();                                              // Done Drawing Quads

        viewPerspective(gl);                                     // Switch To A Perspective View

        gl.glEnable(GL.GL_DEPTH_TEST);                           // Enable Depth Testing
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_BLEND);                               // Disable Blending
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);                   // Unbind The Blur Texture
    }
    
    
    protected void drawLineWithHalo(final GL gl) { //FIXME
		
		// The spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glEnable(GL.GL_BLEND);
		gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]);
		
		// the spline
		gl.glBegin(GL.GL_QUAD_STRIP);
		for(int i = 0; i < polygonLineVertices.size(); i++) {
			gl.glTexCoord3f(polygonLineVertices.get(i).x(), polygonLineVertices.get(i).y(), polygonLineVertices.get(i).z());
			gl.glVertex3f(polygonLineVertices.get(i).x(), polygonLineVertices.get(i).y(), polygonLineVertices.get(i).z());
		}
		gl.glEnd();
	}
    
    
    /**
     * Creates a frame buffer object.
     * @return the newly created frame buffer object is or -1 if a frame buffer object could not be created
     */
    private int createFrameBufferObject(GL gl) {
        // Create the FBO
        int[] frameBuffer = new int[1];
        gl.glGenFramebuffersEXT(1, frameBuffer, 0);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frameBuffer[0]);

        // Create a TEXTURE_SIZE x TEXTURE_SIZE RGBA texture that will be used as color attachment
        // for the fbo.
//        int[] colorBuffer = new int[1];
//        gl.glGenTextures(1, colorBuffer, 0);                 // Create 1 Texture
//        gl.glBindTexture(GL.GL_TEXTURE_2D, colorBuffer[0]);  // Bind The Texture
//        gl.glTexImage2D(                                     // Build Texture Using Information In data
//                                                             GL.GL_TEXTURE_2D,
//                                                             0,
//                                                             GL.GL_RGBA,
//                                                             TEXTURE_SIZE,
//                                                             TEXTURE_SIZE,
//                                                             0,
//                                                             GL.GL_RGBA,
//                                                             GL.GL_UNSIGNED_BYTE,
//                                                             BufferUtil.newByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE * 4)
//        );
//        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
//        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
//
//        // Attach the texture to the frame buffer as the color attachment. This
//        // will cause the results of rendering to the FBO to be written in the blur texture.
//        gl.glFramebufferTexture2DEXT(
//                GL.GL_FRAMEBUFFER_EXT,
//                GL.GL_COLOR_ATTACHMENT0_EXT,
//                GL.GL_TEXTURE_2D,
//                colorBuffer[0],
//                0
//        );
        
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, blurTexture[0], 0); // FIXME: added

        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

        // Create a 24-bit TEXTURE_SIZE x TEXTURE_SIZE depth buffer for the FBO.
        // We need this to get correct rendering results.
        int[] depthBuffer = new int[1];
        gl.glGenRenderbuffersEXT(1, depthBuffer, 0);
        gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthBuffer[0]);
        gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT24, TEXTURE_SIZE, TEXTURE_SIZE);

        // Attach the newly created depth buffer to the FBO.
        gl.glFramebufferRenderbufferEXT(
                GL.GL_FRAMEBUFFER_EXT,
                GL.GL_DEPTH_ATTACHMENT_EXT,
                GL.GL_RENDERBUFFER_EXT,
                depthBuffer[0]
        );

        // Make sure the framebuffer object is complete (i.e. set up correctly)
        int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
        if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT) {
            return frameBuffer[0];
        } else {
            // No matter what goes wrong, we simply delete the frame buffer object
            // This switch statement simply serves to list all possible error codes
            switch(status) {
                case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                    // One of the attachments is incomplete
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                    // Not all attachments have the same size
                case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                    // The desired read buffer has no attachment
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                    // The desired draw buffer has no attachment
                case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                    // Not all color attachments have the same internal format
                case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                    // No attachments have been attached
                case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
                    // The combination of internal formats is not supported
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
                    // This value is no longer in the EXT_framebuffer_object specification
                default:
                    // Delete the color buffer texture
                    gl.glDeleteTextures(1, blurTexture, 0); // FIXME: changed
                    // Delete the depth buffer
                    gl.glDeleteRenderbuffersEXT(1, depthBuffer, 0);
                    // Delete the FBO
                    gl.glDeleteFramebuffersEXT(1, frameBuffer, 0);
                    return -1;
            }
        }
    }
    
	
}

