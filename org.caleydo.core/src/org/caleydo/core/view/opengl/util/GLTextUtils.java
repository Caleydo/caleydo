package org.caleydo.core.view.opengl.util;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Util methods for rendering text.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class GLTextUtils {

	public static void renderText(final GL2 gl, final String showText, final int iSize, final float fx,
		final float fy, final float fz) {

		GLUT glut = new GLUT();
		gl.glRasterPos3f(fx, fy, fz);

		switch (iSize) {
			case 10:
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, showText);
				break;
			case 12:
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, showText);
				break;
			case 18:
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, showText);
				break;
			default:
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, showText);
		}
	}

	public static void renderStaticText(final GL2 gl, final String showText, final int iWindowPosX,
		final int iWindowPosY) {

		GLUT glut = new GLUT();

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 0.0f);

		gl.glWindowPos2i(iWindowPosX, iWindowPosY);

		// Take a string and make it a bitmap, put it in the 'gl' passed over
		// and pick
		// the GLUT font, then provide the string to show
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, showText);
		glut.glutSolidCube(3);

	}

	public static void renderTextInRegion(final GL2 gl, String showText, final int iSize, final float fx,
		final float fy, final float fz, final float fWidth, final float fHeight) {

		int iMaxLineChars = (int) (fWidth / 0.006f);

		// Return if width is too short to render text
		if (iMaxLineChars < 3)
			return;

		float fLineHeight = 0.03f;
		int iTotalLines = showText.length() / iMaxLineChars;
		String sTmpText;

		for (int iLineIndex = 0; iLineIndex <= iTotalLines; iLineIndex++) {
			if (showText.length() <= iMaxLineChars) {
				renderText(gl, showText, iSize, fx, fy - iLineIndex * fLineHeight, fz);
				showText = "";
			}
			else {
				sTmpText = showText.subSequence(0, iMaxLineChars).toString();

				if (sTmpText.contains(" ")) {
					sTmpText = sTmpText.substring(0, sTmpText.lastIndexOf(' '));
				}

				renderText(gl, sTmpText, iSize, fx, fy - iLineIndex * fLineHeight, fz);

				// store rest for next line
				showText = showText.substring(sTmpText.length(), showText.length());
			}
		}
	}
}
