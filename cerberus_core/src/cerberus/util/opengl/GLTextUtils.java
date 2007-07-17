package cerberus.util.opengl;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

/**
 * Util methods for rendering text.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class GLTextUtils {
	
	/**
	 * Method for rendering text in OpenGL.
	 * TODO: Move method to some kind of GL Utility class.
	 * 
	 * @param gl
	 * @param showText
	 * @param fx
	 * @param fy
	 * @param fz
	 */
	public static void renderText(final GL gl,
			final String showText,
			final float fx, 
			final float fy, 
			final float fz ) {
		
		// final float fFontSizeOffset = 0.02f;

		GLUT glut = new GLUT();

		// gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// gl.glLoadIdentity();
		// gl.glTranslatef(0.0f,0.0f,-1.0f);

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 0.0f);

		// Position The Text On The Screen...fullscreen goes much slower than
		// the other
		// way so this is kind of necessary to not just see a blur in smaller
		// windows
		// and even in the 640x480 method it will be a bit blurry...oh well you
		// can
		// set it if you would like :)
		gl.glRasterPos3f(fx, fy, fz);
		
		// Take a string and make it a bitmap, put it in the 'gl' passed over
		// and pick
		// the GLUT font, then provide the string to show
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, showText); 
	}
	
	public static void renderStaticText(final GL gl,
			final String showText,
			final int iWindowPosX, 
			final int iWindowPosY) { 
		
		GLUT glut = new GLUT();

		// Pulsing Colors Based On Text Position
		gl.glColor3f(0.0f, 0.0f, 0.0f);

		gl.glWindowPos2i(iWindowPosX, iWindowPosY);
		
		// Take a string and make it a bitmap, put it in the 'gl' passed over
		// and pick
		// the GLUT font, then provide the string to show
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, showText); 
	}
	
	public static void renderTextInRegion(final GL gl, 
			String showText,
			final float fx, 
			final float fy, 
			final float fz,
			final float fWidth,
			final float fHeight) {
		
		int iMaxLineChars = (int)(fWidth / 0.006f);
		
		// Return if width is too short to render text
		if (iMaxLineChars < 3)
			return;
		
		float fLineHeight = 0.03f;
		int iTotalLines = (int)Math.ceil(showText.length() / iMaxLineChars);
		String sTmpText;
		
		for (int iLineIndex = 0; iLineIndex <= iTotalLines; iLineIndex++)
		{		
			if (showText.length() <= iMaxLineChars)
				renderText(gl, showText, fx, fy, fz);
			else
			{
				sTmpText = showText.subSequence(0, iMaxLineChars).toString();
				
				if (sTmpText.contains(" "))
					sTmpText = sTmpText.substring(0, sTmpText.lastIndexOf(' '));
				
				renderText(gl, sTmpText, fx, fy - fLineHeight, fz);
				
				// store rest for next line
				showText = showText.substring(sTmpText.length()+1, showText.length());	
			}
		}
	}
}
