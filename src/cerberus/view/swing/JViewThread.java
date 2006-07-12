
package cerberus.view.swing;

import javax.media.opengl.GLEventListener;

import cerberus.view.swing.JViewThreadListener;
import demos.common.Demo;

/**
 * 
 * @see cerberus.view.swing.JViewThreadListener
 * 
 * @author kalkusch
 *
 */
public abstract class JViewThread extends Demo implements GLEventListener  {
	
  protected JViewThreadListener demoListener;
  
  private boolean doShutdown = true;

  public final void setViewThreadListener(JViewThreadListener listener) {
    this.demoListener = listener;
  }
  
  public final JViewThreadListener getViewThreadListener() {
	return this.demoListener;
  }

  // Override this with any other cleanup actions
  public void shutdownViewThread() {
    // Execute only once
    boolean shouldDoShutdown = doShutdown;
    doShutdown = false;
    if (shouldDoShutdown) {
      demoListener.shutdownDemo();
    }
  }
}
