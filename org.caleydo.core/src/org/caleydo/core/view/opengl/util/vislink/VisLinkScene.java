package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;

/**
 * This class provides higher level features to VisLinks such as halo and animation. To provide this features,
 * more context is needed than a single line. This class needs to know all connection lines that should be
 * displayed on the screen.
 * 
 * @author Oliver Pimas
 * @version 2009-10-21
 */

public class VisLinkScene {

	ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews;
	private static boolean animationFinished = false;

	private static long animationStartTime = -1;
	private static final float FULL_PERCENTAGE = 100;
	private static final float SEGMENT_LENGTH = ConnectionLineRenderStyle.CONNECTION_LINE_SEGMENT_LENGTH;
	private static int antiAliasingQuality = 5;

	private EVisLinkStyleType style;

	/**
	 * Constructor
	 * 
	 * @param connectionLinesAllViews
	 *            Connection lines of the scene
	 */
	public VisLinkScene(ArrayList<ArrayList<ArrayList<Vec3f>>> connectionLinesAllViews) {
		this.connectionLinesAllViews = connectionLinesAllViews;
		this.style = ConnectionLineRenderStyle.CONNECTION_LINE_STYLE;
	}

	/**
	 * Renders the connection line of the scene.
	 * 
	 * @param gl
	 *            The GL-object
	 */
	public void renderLines(final GL gl) {

		ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH =
			GeneralManager.get().getPreferenceStore().getFloat(PreferenceConstants.VISUAL_LINKS_WIDTH);
		ConnectionLineRenderStyle.ANIMATION =
			GeneralManager.get().getPreferenceStore().getBoolean(PreferenceConstants.VISUAL_LINKS_ANIMATION);
		ConnectionLineRenderStyle.CONNECTION_LINE_STYLE =
			EVisLinkStyleType.getStyleType(GeneralManager.get().getPreferenceStore().getInt(
				PreferenceConstants.VISUAL_LINKS_STYLE));
		ConnectionLineRenderStyle.ANIMATED_HIGHLIGHTING =
			GeneralManager.get().getPreferenceStore().getBoolean(
				PreferenceConstants.VISUAL_LINKS_ANIMATED_HALO);

		// callRenderLine(gl);

		if (ConnectionLineRenderStyle.ANIMATED_HIGHLIGHTING) {
			float tempWidth = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
			ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH = tempWidth / 2;
			style = EVisLinkStyleType.STANDARD_VISLINK;
			callRenderPolygonLine(gl);
			style = ConnectionLineRenderStyle.CONNECTION_LINE_STYLE;
			ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH = tempWidth;
			callRenderAnimatedPolygonLine(gl);
		}
		else if (ConnectionLineRenderStyle.ANIMATION)
			callRenderAnimatedPolygonLine(gl);
		else
			callRenderPolygonLine(gl);
	}

	/**
	 * Renders the scene with simple connection lines (visual links). No halo-effect or animation available.
	 * 
	 * @param gl
	 *            The GL-object
	 */
	protected void callRenderLine(final GL gl) {
		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews)
			for (ArrayList<Vec3f> currentLine : currentView)
				VisLink.renderLine(gl, currentLine, 0, 10, true);
	}

	/**
	 * Renders the scene with polygonal connection lines (visual links)
	 * 
	 * @param gl
	 *            The GL-object
	 */
	protected void callRenderPolygonLine(final GL gl) {

		float width = 0.0f;
		float color[] = new float[4];
		boolean roundedStart = false;
		boolean roundedEnd = false;

		if ((style == EVisLinkStyleType.SHADOW_VISLINK) || (style == EVisLinkStyleType.HALO_VISLINK)) {

			// clear stencil buffer
			gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
			int hlAAQuality = 1;
			boolean halo = false;

			// set parameters
			if (style == EVisLinkStyleType.SHADOW_VISLINK) {
				width =
					ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH
						* ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_WIDTH_FACTOR;
				color = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR;
			}
			if (style == EVisLinkStyleType.HALO_VISLINK) {
				width =
					ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH
						* ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH_FACTOR;
				color[0] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[0];
				color[1] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[1];
				color[2] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[2];
				color[3] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[3] / 2.5f;
				hlAAQuality = 5;
				halo = true;
			}

			// draw shadow oder halo
			for (int i = 0; i < connectionLinesAllViews.size(); i++) {
				ArrayList<ArrayList<Vec3f>> currentStage = connectionLinesAllViews.get(i);
				if (style == EVisLinkStyleType.HALO_VISLINK && i == 0)
					roundedStart = true;
				if (style == EVisLinkStyleType.HALO_VISLINK && i == (connectionLinesAllViews.size() - 1))
					roundedEnd = true;
				for (ArrayList<Vec3f> currentLine : currentStage) {
					if (currentLine.size() >= 2) {
						VisLink visLink = new VisLink(currentLine, 0, SEGMENT_LENGTH);
						enableStencilBuffer(gl);
						if (i < 2)
							visLink.drawPolygonLine(gl, width, color, hlAAQuality, roundedStart, roundedEnd,
								halo);
						else
							visLink.drawPolygonLine(gl, width, color, hlAAQuality, roundedEnd, roundedStart,
								halo);
						disableStencilBuffer(gl);
					}
				}
				roundedStart = false;
				roundedEnd = false;
			}
		}

		// background (halo or shadow) done, render frontline
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);

		width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
		color = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR;
		// antiAliasingQuality = 5;

		for (ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews)
			for (ArrayList<Vec3f> currentLine : currentView) {
				if (currentLine.size() >= 2) {
					VisLink visLink = new VisLink(currentLine, 0, SEGMENT_LENGTH);
					// enableStencilBuffer(gl);
					visLink.drawPolygonLine(gl, width, color, antiAliasingQuality, roundedStart, roundedEnd,
						false);
					// disableStencilBuffer(gl);
				}
			}
	}

	/**
	 * Enables the stencil buffer
	 * 
	 * @param gl
	 *            The GL-object
	 */
	protected void enableStencilBuffer(GL gl) {
		gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_REPLACE, GL.GL_REPLACE);
		gl.glEnable(GL.GL_STENCIL_TEST);
	}

	/**
	 * Disables the stencil buffer
	 * 
	 * @param gl
	 *            The GL-object
	 */
	protected void disableStencilBuffer(GL gl) {
		gl.glDisable(GL.GL_STENCIL_TEST);
	}

	/**
	 * Resets the animation
	 * 
	 * @param time
	 *            the time the animation started
	 */
	public static void resetAnimation(long time) {
		animationStartTime = time;
		animationFinished = false;
	}

	/**
	 * Calculates the number of segments to be drawn for animation
	 * 
	 * @return number of segments to be drawn
	 */
	public float percentageOfSegmentsToDraw() {
		float animationSpeed =
			(ConnectionLineRenderStyle.ANIMATION_SPEED_IN_MILLIS / (FULL_PERCENTAGE * numberOfStages()));
		return ((System.currentTimeMillis() - animationStartTime) / animationSpeed);
	}

	public int numberOfSegmentsToDraw(float percentageOfSegmentsToDraw, int totalNumberOfSegments) {
		float number = ((percentageOfSegmentsToDraw / FULL_PERCENTAGE) * totalNumberOfSegments);
		return (int) number;
	}

	/**
	 * Renders the scene with animated polygonal connection lines (visual links)
	 * 
	 * @param gl
	 *            The GL-object
	 */
	protected void callRenderAnimatedPolygonLine(final GL gl) {

		float percentageOfSegmentsToDraw = percentageOfSegmentsToDraw();
		int localStage = 0;
		while (percentageOfSegmentsToDraw > FULL_PERCENTAGE) {
			localStage++;
			percentageOfSegmentsToDraw -= FULL_PERCENTAGE;
		}
		if (localStage > numberOfStages()) {
			animationFinished = true;
			localStage = numberOfStages();
		}

		float width = 0.0f;
		float color[] = new float[4];
		boolean roundedStart = false;
		boolean roundedEnd = false;

		if ((style == EVisLinkStyleType.SHADOW_VISLINK) || (style == EVisLinkStyleType.HALO_VISLINK)) {

			// clear stencil buffer
			gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
			int hlAAQuality = 1;
			boolean halo = false;

			// set parameters
			if (style == EVisLinkStyleType.SHADOW_VISLINK) {
				width =
					ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH
						* ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_WIDTH_FACTOR;
				color = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR;
			}
			if (style == EVisLinkStyleType.HALO_VISLINK) {
				width =
					ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH
						* ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH_FACTOR;
				color[0] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[0];
				color[1] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[1];
				color[2] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[2];
				color[3] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[3] / 1.5f;
				hlAAQuality = 5;
				halo = true;
			}

			// draw shadow oder halo
			for (int i = 0; i <= localStage; i++) {
				ArrayList<ArrayList<Vec3f>> currentStage = connectionLinesAllViews.get(i);
				for (ArrayList<Vec3f> currentLine : currentStage) {
					VisLink visLink = new VisLink(currentLine, 0, SEGMENT_LENGTH);
					int numberOfSegments = visLink.numberOfSegments();
					int numberOfSegmentsToDraw =
						numberOfSegmentsToDraw(percentageOfSegmentsToDraw, numberOfSegments);
					if (style == EVisLinkStyleType.HALO_VISLINK && i == 0)
						roundedStart = true;
					if (style == EVisLinkStyleType.HALO_VISLINK && i == localStage)
						roundedEnd = true;
					enableStencilBuffer(gl);
					if (i == localStage && !animationFinished) {
						if (i < 2)
							visLink.drawPolygonLine(gl, width, color, hlAAQuality, numberOfSegmentsToDraw,
								roundedStart, roundedEnd, halo);
						else
							visLink.drawPolygonLineReverse(gl, width, color, hlAAQuality,
								numberOfSegmentsToDraw, roundedStart, roundedEnd, halo);
					}
					else {
						if (i < 2)
							visLink.drawPolygonLine(gl, width, color, hlAAQuality, roundedStart, roundedEnd,
								halo);
						else
							visLink.drawPolygonLine(gl, width, color, hlAAQuality, roundedEnd, roundedStart,
								halo); // Line is reverse, so start and end are inverted
					}
					disableStencilBuffer(gl);
					roundedStart = false;
					roundedEnd = false;
				}
			}
		}

		// // background (halo or shadow) done, render frontline
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
		color = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR;

		for (int i = 0; i <= localStage; i++) {
			ArrayList<ArrayList<Vec3f>> currentStage = connectionLinesAllViews.get(i);
			for (ArrayList<Vec3f> currentLine : currentStage) {
				if (currentLine.size() >= 2) {
					// VisLink visLink = new VisLink(currentLine, 0, NUMBER_OF_SEGMENTS);
					VisLink visLink = new VisLink(currentLine, 0, SEGMENT_LENGTH);
					int numberOfSegments = visLink.numberOfSegments();
					int numberOfSegmentsToDraw =
						numberOfSegmentsToDraw(percentageOfSegmentsToDraw, numberOfSegments);
					// enableStencilBuffer(gl);
					if (i == localStage && !animationFinished) {
						if (i < 2)
							visLink.drawPolygonLine(gl, width, color, antiAliasingQuality,
								numberOfSegmentsToDraw, roundedStart, roundedEnd, false);
						else
							visLink.drawPolygonLineReverse(gl, width, color, antiAliasingQuality,
								numberOfSegmentsToDraw, roundedStart, roundedEnd, false);
					}
					else
						visLink.drawPolygonLine(gl, width, color, antiAliasingQuality, roundedStart,
							roundedEnd, false);
					// disableStencilBuffer(gl);
				}
			}
		}
	}

	/**
	 * Returns the number of stages for animation.
	 * 
	 * @return Number of stages for animation
	 */
	protected int numberOfStages() {
		return connectionLinesAllViews.size() - 1;
	}

	// protected void callRenderAnimatedPolygonLine(final GL gl) {
	//		
	// gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
	//		
	// long numberOfSegmentsToDraw = numberOfSegmentsToDraw();
	// boolean currentStageFinished = false;
	// float width = 0.0f;
	// float color[] = new float[4];
	//				
	// if( (style == EVisLinkStyleType.SHADOW_VISLINK) || (style == EVisLinkStyleType.HALO_VISLINK) ) {
	// //set parameters
	// if(style == EVisLinkStyleType.SHADOW_VISLINK) {
	// width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH *
	// ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_WIDTH_FACTOR;
	// color = ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR;
	// }
	// if(style == EVisLinkStyleType.HALO_VISLINK) {
	// width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH *
	// ConnectionLineRenderStyle.CONNECTION_LINE_HALO_WIDTH_FACTOR;
	// color[0] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[0];
	// color[1] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[1];
	// color[2] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[2];
	// color[3] = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR[3] / 1.5f;
	// }
	//			
	// //draw shadow oder halo
	// for(int i = 0; i <= stage; i++) {
	// currentStageFinished = false;
	// ArrayList<ArrayList<Vec3f>> currentStage = connectionLinesAllViews.get(i);
	// for(ArrayList<Vec3f> currentLine : currentStage) {
	// if(currentLine.size() >= 2) {
	// VisLink visLink = new VisLink(currentLine, 0, NUMBER_OF_SEGMENTS);
	// if ( (numberOfSegmentsToDraw >= visLink.numberOfSegments()) && !currentStageFinished) {
	// currentStageFinished = true;
	// if(stage < (connectionLinesAllViews.size()-1)) {
	// stage++;
	// numberOfSegmentsToDraw = numberOfSegmentsToDraw();
	// }
	// else
	// animationFinished = true;
	// }
	// enableStencilBuffer(gl);
	// if(i == stage && !animationFinished) {
	// if(i < 2)
	// visLink.drawPolygonLine(gl, width, color, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY,
	// numberOfSegmentsToDraw);
	// else
	// visLink.drawPolygonLineReverse(gl, width, color, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY,
	// numberOfSegmentsToDraw);
	// }
	// else
	// visLink.drawPolygonLine(gl, width, color, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// disableStencilBuffer(gl);
	// }
	// }
	// }
	// }
	//		
	// // background (halo or shadow) done, render frontline
	// width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH;
	// color = ConnectionLineRenderStyle.CONNECTION_LINE_COLOR;
	//		
	// for(int i = 0; i <= stage; i++) {
	// currentStageFinished = false;
	// ArrayList<ArrayList<Vec3f>> currentStage = connectionLinesAllViews.get(i);
	// for(ArrayList<Vec3f> currentLine : currentStage) {
	// if(currentLine.size() >= 2) {
	// VisLink visLink = new VisLink(currentLine, 0, NUMBER_OF_SEGMENTS);
	// if ( (numberOfSegmentsToDraw >= visLink.numberOfSegments()) && !currentStageFinished) {
	// currentStageFinished = true;
	// if(stage < (connectionLinesAllViews.size()-1)) {
	// stage++;
	// numberOfSegmentsToDraw = numberOfSegmentsToDraw();
	// }
	// else
	// animationFinished = true;
	// }
	// if(i == stage && !animationFinished) {
	// if(i < 2)
	// visLink.drawPolygonLine(gl, width, color, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY,
	// numberOfSegmentsToDraw);
	// else
	// visLink.drawPolygonLineReverse(gl, width, color, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY,
	// numberOfSegmentsToDraw);
	// }
	// else
	// visLink.drawPolygonLine(gl, width, color, ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// }
	// }
	// }
	// }

	// the following is outdated...

	// protected void callRenderAnimatedPolygonLineProgressive(final GL gl) {
	// gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
	//	
	// for(ArrayList<Vec3f> currentLine : connectionLinesActiveView) {
	// if(currentLine.size() >= 2) {
	// VisLink visLink = new VisLink(currentLine, 0, NUMBER_OF_SEGMENTS);
	// long numberOfSegmentsToDraw = numberOfSegmentsToDraw();
	// if (numberOfSegmentsToDraw >= visLink.numberOfSegments())
	// activeViewAnimationFinished = true;
	// enableStencilBuffer(gl);
	// if(!activeViewAnimationFinished)
	// visLink.renderPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH,
	// ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, style,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY, numberOfSegmentsToDraw);
	// else
	// visLink.renderPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH,
	// ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, style,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// disableStencilBuffer(gl);
	// }
	// else
	// throw new IllegalArgumentException( "Need at least two points to render a line!" );
	// }
	// if(activeViewAnimationFinished) {
	// for(ArrayList<Vec3f> currentLine : bundlingToCenterLines) {
	// if(currentLine.size() >= 2) {
	// VisLink visLink = new VisLink(currentLine, 0, NUMBER_OF_SEGMENTS);
	// enableStencilBuffer(gl);
	// visLink.renderPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH,
	// ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, style,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// disableStencilBuffer(gl);
	// }
	// }
	// for(ArrayList<ArrayList<Vec3f>> currentView : connectionLinesAllViews)
	// for(ArrayList<Vec3f> currentLine : currentView) {
	// if(currentLine.size() >= 2) {
	// VisLink visLink = new VisLink(currentLine, 0, NUMBER_OF_SEGMENTS);
	// long numberOfSegmentsToDraw = numberOfSegmentsToDraw();
	// if (numberOfSegmentsToDraw >= visLink.numberOfSegments())
	// animationFinished = true;
	// enableStencilBuffer(gl);
	// if(!animationFinished)
	// visLink.renderPolygonLineReverse(gl, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH,
	// ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, style,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY, numberOfSegmentsToDraw);
	// else
	// visLink.renderPolygonLine(gl, ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH,
	// ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, style,
	// ConnectionLineRenderStyle.LINE_ANTI_ALIASING_QUALITY);
	// disableStencilBuffer(gl);
	// }
	// }
	// }
	// }

	// /**
	// * Checks if the animation is finished (all segments are drawn)
	// *
	// * @return true if animation is finished, false otherwise
	// */
	// public boolean isAnimationFinished() {
	// return animationFinished;
	// }

}
