package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;


import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

//import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
//import org.caleydo.core.data.selection.delta.ISelectionDelta;
//import org.caleydo.core.manager.IEventPublisher;
//import org.caleydo.core.manager.event.AEvent;
//import org.caleydo.core.manager.event.AEventListener;
//import org.caleydo.core.manager.event.IListenerOwner;
//import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
//import org.caleydo.core.manager.general.GeneralManager;
//
//import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;

/**
 * This class provides higher level features to VisLinks as halo and animation.
 * To provide this features, more context is needed than a single line. This
 * class needs to know all connection lines that should be displayed on the screen.
 *  
 * @author oliver
 *
 */

public class VisLinkEnvironment { 
//	implements ISelectionUpdateHandler {
	
//	private int activeViewID;
	
	ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews;
	ArrayList<ArrayList<Vec3f>> bundlingToCenterLines;
	ArrayList<ArrayList<Vec3f>> connectionLinesActiveView;
	
	private static long animationStartTime = -1;
	private static boolean animationFinished = false;
	private static boolean bundlingToCenterAnimationFinished = false;
	private static boolean activeViewAnimationFinished = false;
	private static int NUMBER_OF_SEGMENTS = 30;
	
//	protected static VLSelectionUpdateListener selectionUpdateListener = null;
	
	
	/**
	 * Constructor
	 * @param connectionLines connection lines from objects to bundling points
	 * @param bundlingToCenterLines the lines connecting the bundling points and the center 
	 */
	public VisLinkEnvironment(ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews, ArrayList<ArrayList<Vec3f>> bundlingToCenterLines) {
		this.connectionLinesAllViews = connectionLinesAllViews;
		this.bundlingToCenterLines = bundlingToCenterLines;
//		if(selectionUpdateListener == null)
//			registerEventListeners();
	}
	
	/**
	 * Constructor
	 * @param connectionLines connection lines from objects to bundling points of the non active views
	 * @param bundlingToCenterLines the lines connecting the bundling points and the center
	 * @param connectionLinesActiveView connection lines of the current view
	 */
	public VisLinkEnvironment(ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews, ArrayList<ArrayList<Vec3f>> bundlingToCenterLines, ArrayList<ArrayList<Vec3f>> connectionLinesActiveView) {
		this.connectionLinesAllViews = connectionLinesAllViews;
		this.bundlingToCenterLines = bundlingToCenterLines;
		this.connectionLinesActiveView = connectionLinesActiveView;
//		if(selectionUpdateListener == null)
//			registerEventListeners();
	}
	
	
//	public void registerEventListeners() {
//		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
//		
//		selectionUpdateListener = new VLSelectionUpdateListener();
//		selectionUpdateListener.setHandler(this);
//		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);
//	}
	
//	public void unregisterEventListeners() {
//		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
//		
//		if (selectionUpdateListener != null) {
//			eventPublisher.removeListener(selectionUpdateListener);
//			selectionUpdateListener = null;
//		}
//	}
	
//	public void setActiveViewID(int id) {
//		activeViewID = id;
//	}
	
	public void renderLines(final GL gl) {
		
//		callRenderLine(gl);
//		callRenderPolygonLine(gl, true, true);
//		callRenderPolygonLineWithHalo(gl);
//		callRenderAnimatedPolygonLine(gl);
//		callRenderAnimatedPolygonLineReverse(gl);
		callRenderAnimatedPolygonLineProgressive(gl);
	}
	
	
	
	protected void callRenderLine(final GL gl) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				VisLink.renderLine(gl, currentLine, 0, 10, true);
			}
		}
		for(ArrayList<Vec3f> currentLine : connectionLinesActiveView)
			VisLink.renderLine(gl, currentLine, 0, 10, true);
		for(ArrayList<Vec3f> currentLine : bundlingToCenterLines)
			VisLink.renderLine(gl, currentLine, 0, 10, true);
	}
	
	protected void callRenderPolygonLine(final GL gl, boolean shadow, boolean antiAliasing) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				VisLink.renderPolygonLine(gl, currentLine, 0, 10, shadow, antiAliasing);
			}
		}
		for(ArrayList<Vec3f> currentLine : connectionLinesActiveView)
			VisLink.renderPolygonLine(gl, currentLine, 0, 10, shadow, antiAliasing);
		for(ArrayList<Vec3f> currentLine : bundlingToCenterLines)
			VisLink.renderPolygonLine(gl, currentLine, 0, 10, shadow, antiAliasing);
	}
	
	protected void callRenderPolygonLineWithHalo(final GL gl) {
		
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews) {
			for(ArrayList<Vec3f> currentLine : currentView) {
				gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
				gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
				gl.glEnable(GL.GL_STENCIL_TEST);
				VisLink.renderPolygonLineHalo(gl, currentLine, 0);
				gl.glDisable(GL.GL_STENCIL_TEST);;
				VisLink.renderPolygonLine(gl, currentLine, 0, ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH);
			}
		}
		for(ArrayList<Vec3f> currentLine : bundlingToCenterLines) {
			gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
			gl.glEnable(GL.GL_STENCIL_TEST);
			VisLink.renderPolygonLineHalo(gl, currentLine, 0);
			gl.glDisable(GL.GL_STENCIL_TEST);;
			VisLink.renderPolygonLine(gl, currentLine, 0, ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH);
		}
		for(ArrayList<Vec3f> currentLine : connectionLinesActiveView) {
			gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
			gl.glEnable(GL.GL_STENCIL_TEST);
			VisLink.renderPolygonLineHalo(gl, currentLine, 0);
			gl.glDisable(GL.GL_STENCIL_TEST);;
			VisLink.renderPolygonLine(gl, currentLine, 0, ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH);
		}
	}
	
	protected void callRenderAnimatedPolygonLine(final GL gl) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews)
			for(ArrayList<Vec3f> currentLine : currentView)
				renderAnimatedPolygonLine(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
		for(ArrayList<Vec3f> currentLine : connectionLinesActiveView)
			renderAnimatedPolygonLine(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
		if(animationFinished)
			for(ArrayList<Vec3f> currentLine : bundlingToCenterLines)
				renderAnimatedPolygonLineReverse(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
	}
	
	protected void callRenderAnimatedPolygonLineReverse(final GL gl) {
		for(ArrayList<Vec3f> currentLine : bundlingToCenterLines)
			renderAnimatedPolygonLineReverse(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
		for(ArrayList<Vec3f> currentLine : connectionLinesActiveView)
			renderAnimatedPolygonLineReverse(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
		for(ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews)
			for(ArrayList<Vec3f> currentLine : currentView)
				renderAnimatedPolygonLineReverse(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
	}
	
	protected void callRenderAnimatedPolygonLineProgressive(final GL gl) {
		for(ArrayList<Vec3f> currentLine : connectionLinesActiveView)
			renderAnimatedPolygonLineActiveView(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
		if(activeViewAnimationFinished) {
			for(ArrayList<Vec3f> currentLine : bundlingToCenterLines)
				renderAnimatedPolygonLineReverse(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
			for(ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews)
				for(ArrayList<Vec3f> currentLine : currentView)
					renderAnimatedPolygonLineReverse(gl, currentLine, 0, NUMBER_OF_SEGMENTS, true, true);
		}
	}
	
	
	/**
	 * 		Creates an animated polygon visual link
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * @param controlPoints the control points for the NURBS spline
	 * @param offset specifies the offset of control points
	 * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * Note: For straight lines (only 2 control points), this value doesn't effect the resulting line.
	 * @param shadow turns shadow on/off
	 * @param antiAliasing turns AA on/off
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public void renderAnimatedPolygonLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, final int numberOfSegments, boolean shadow, boolean antiAliasing)
		throws IllegalArgumentException
	{
		if(controlPoints.size() > (offset + 2) ) {
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			if(antiAliasing == true)
				animationFinished = animatedPolygonLineAA(gl, visLink, shadow);
			else
				animationFinished = animatedPolygonLine(gl, visLink, shadow);
			
		}
		else if(controlPoints.size() == (offset + 2) ) {
//			if(animationFinished == true) {
				VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
				if(antiAliasing == true)
					animationFinished = animatedPolygonLineAA(gl, visLink, shadow);
				else
					animationFinished = animatedPolygonLine(gl, visLink, shadow);
//			}
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 
	}
	
	
	/**
	 * 		Creates an animated polygon visual link in reverse order.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * @param controlPoints the control points for the NURBS spline
	 * @param offset specifies the offset of control points
	 * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * Note: For straight lines (only 2 control points), this value doesn't effect the resulting line.
	 * @param shadow turns shadow on/off
	 * @param antiAliasing turns AA on/off
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public void renderAnimatedPolygonLineReverse(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, final int numberOfSegments, boolean shadow, boolean antiAliasing)
		throws IllegalArgumentException
	{
		if(controlPoints.size() > (offset + 2) ) {
			VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
			if(antiAliasing == true)
				animationFinished = animatedPolygonLineAAReverse(gl, visLink, shadow);
			else
				animationFinished = animatedPolygonLineReverse(gl, visLink, shadow);
		}
		else if(controlPoints.size() == (offset + 2) )
			VisLink.renderPolygonLine(gl, controlPoints, 0, 10, shadow, antiAliasing);
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 			
	}
	
	public void renderAnimatedPolygonLineActiveView(final GL gl, final ArrayList<Vec3f> controlPoints, final int offset, final int numberOfSegments, boolean shadow, boolean antiAliasing)
		throws IllegalArgumentException
	{
		if(controlPoints.size() > (offset + 2) ) {
			if(!activeViewAnimationFinished) {
				VisLink visLink = new VisLink(controlPoints, offset, numberOfSegments);
				if(antiAliasing == true)
					activeViewAnimationFinished = animatedPolygonLineAA(gl, visLink, shadow);
				else
					activeViewAnimationFinished = animatedPolygonLine(gl, visLink, shadow);
			}
			else
				VisLink.renderPolygonLine(gl, controlPoints, 0, numberOfSegments, shadow, antiAliasing);
		}
		else if(controlPoints.size() == (offset + 2) )
			VisLink.renderPolygonLine(gl, controlPoints, 0, numberOfSegments, shadow, antiAliasing);
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 			
	}
	
	
	/**
	 * 		Renders an animated polygon line. Recommended for lines with higher width.
	 * 
	 * @param gl the GL object
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * @param drawSegments specifies the number of segments to be drawn (for animation)
	 */
	protected boolean animatedPolygonLine(final GL gl, final VisLink visLink, boolean shadow) {
		
		if(shadow == true)
			visLink.drawPolygonLineBySegments(gl, ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, (ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH * 1.5f), 1, numberOfSegmentsToDraw(visLink));
		
		return visLink.drawPolygonLineBySegments(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH, 1, numberOfSegmentsToDraw(visLink));
	}
	
	
	/**
	 * 		Renders a polygon line with AA. Recommended for lines with higher width.
	 * 
	 * @param gl The GL object
	 * @param shadow Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * @param drawSegments specifies the number of segments to be drawn (for animation)
	 */
	protected boolean animatedPolygonLineAA(final GL gl, final VisLink visLink, boolean shadow) {
		if(shadow == true)
			visLink.drawPolygonLineBySegments(gl, ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, (ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH * 1.5f), ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY, numberOfSegmentsToDraw(visLink));
		
		return visLink.drawPolygonLineBySegments(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY, numberOfSegmentsToDraw(visLink));
	}
	
	
	/**
	 * 		Renders an animated polygon line in reverse order. Recommended for lines with higher width.
	 * 
	 * @param gl the GL object
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * @param drawSegments specifies the number of segments to be drawn (for animation)
	 */
	protected boolean animatedPolygonLineReverse(final GL gl, final VisLink visLink, boolean shadow) {
		
		if(shadow == true)
			visLink.drawPolygonLineBySegmentsReverse(gl, ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, (ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH * 1.5f), 1, numberOfSegmentsToDraw(visLink));
		
		return visLink.drawPolygonLineBySegmentsReverse(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH, 1, numberOfSegmentsToDraw(visLink));
	}
	
	
	/**
	 * 		Renders a polygon line with AA in reverse order. Recommended for lines with higher width.
	 * 
	 * @param gl The GL object
	 * @param shadow Turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * @param drawSegments specifies the number of segments to be drawn (for animation)
	 */
	protected boolean animatedPolygonLineAAReverse(final GL gl, final VisLink visLink, boolean shadow) {
		if(shadow == true)
			visLink.drawPolygonLineBySegmentsReverse(gl, ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, (ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH * 1.5f), ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY, numberOfSegmentsToDraw(visLink));
		
		return visLink.drawPolygonLineBySegmentsReverse(gl, ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY, numberOfSegmentsToDraw(visLink));
	}
	
	
	/**
	 * Resets the animation
	 * @param time the time the animation started
	 */
	public static void resetAnimation(long time) {
		animationStartTime = time;
		animationFinished = false;
		bundlingToCenterAnimationFinished = false;
		activeViewAnimationFinished = false;
	}
	
	
	/**
	 * Checks if the animation is finished (all segments are drawn)
	 * 
	 * @return true if animation is finished, false otherwise
	 */
	public boolean isAnimationFinished() {
		return animationFinished;
	}
	
	
	/**
	 * Calculates the number of segments to be drawn for animation
	 * 
	 * @return number of segments to be drawn
	 */
	public long numberOfSegmentsToDraw(final VisLink visLink) {
		long animationSpeed = ConnectionLineRenderStyle.ANIMATION_SPEED_IN_MILLIS / visLink.getNumberOfSegments();
		long segments = (System.currentTimeMillis() - animationStartTime) / animationSpeed;
		if(activeViewAnimationFinished)
			segments -= visLink.getNumberOfSegments();
		return (segments > visLink.getNumberOfSegments() ) ? visLink.getNumberOfSegments() : segments;
	}


//	@Override
//	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	@Override
//	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
//		// TODO Auto-generated method stub
//		
//	}	

}
