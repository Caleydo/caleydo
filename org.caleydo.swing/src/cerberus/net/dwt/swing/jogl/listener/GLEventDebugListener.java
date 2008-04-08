/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.jogl.listener;

import javax.swing.text.JTextComponent;

//--- old JOGL version ---
//import net.java.games.jogl.GL;
//import net.java.games.jogl.GLDrawable;
//import net.java.games.jogl.GLEventListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

//import cerberus.util.*;

/**
 * @author Michael Kalkusch
 *
 */
public class GLEventDebugListener implements GLEventListener {
 
	private boolean bPipeToSystemOut = true;
	
	private JTextComponent refJTextComponent_forMessages = null;
	

	/**
	 * 
	 */
	public GLEventDebugListener( final boolean bEnableMessageToSystemOut,
			JTextComponent setJTextComponent ) {
		super();
		
		refJTextComponent_forMessages = setJTextComponent;
		
		showText("GL Listener constructor");		
		
		setEnableMessageToSystemOut(bEnableMessageToSystemOut);
	}
	
	
	/**
	 * Print debug message to System out or to JComponent
	 * @param sText
	 */
	private void showText( final String sText ) {
		if ( bPipeToSystemOut ) {
			System.out.println( sText );
			return;
		}
		
		//refJTextComponent_forMessages.getText()
		refJTextComponent_forMessages.setText( sText );		
	}
	
	
	/**
	 * Enables messages to be shown on System.out if TRUE or on a JTextComponent if FALSE.
	 * 
	 * @param bEnableMessageToSystemOut TRUE uses System.out and FALSE activates JTextComponent output
	 */
	public void setEnableMessageToSystemOut( boolean bEnableMessageToSystemOut) {
		if  ( ! bEnableMessageToSystemOut ) {
			if ( refJTextComponent_forMessages == null ) {
				bPipeToSystemOut = true;
				
				assert false:"try to use JTextComponent which points to null";
				
				return;
			}
			bPipeToSystemOut = false;
			return;
		}
		bPipeToSystemOut = true;
	}
	
	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#init(net.java.games.jogl.GLDrawable)
	 */
	public void init(GLAutoDrawable drawable) {
		showText("GL init ...");
		
		 GL gl = drawable.getGL();
	      System.out.println("GL vendor: " + gl.glGetString(GL.GL_VENDOR));
	      System.out.println("GL version: " + gl.glGetString(GL.GL_VERSION));
	      System.out.println("GL renderer: " + gl.glGetString(GL.GL_RENDERER));
	      System.out.println("GL extensions:");
	      String[] extensions = gl.glGetString(GL.GL_EXTENSIONS).split(" ");
	      int i = 0;
	      while (i < extensions.length) {
	        System.out.print("  ");
	        String ext = extensions[i++];
	        System.out.print(ext);
	        if (i < extensions.length) {
	          for (int j = 0; j < (40 - ext.length()); j++) {
	            System.out.print(" ");
	          }
	          System.out.println(extensions[i++]);
	        } else {
	          System.out.println();
	        }
	      }
	      //runExit();		
	      
	      showText("GL init ... [done]");
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#display(net.java.games.jogl.GLDrawable)
	 */
	public void display(GLAutoDrawable arg0) {
		showText("GL display");	
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		showText("GL reshape");
	}

	/* (non-Javadoc)
	 * @see net.java.games.jogl.GLEventListener#displayChanged(net.java.games.jogl.GLDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		showText("GL displayChanged");	
	}

}
