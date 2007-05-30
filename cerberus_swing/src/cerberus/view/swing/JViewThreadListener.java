package cerberus.view.swing;

/** Defines certain events demos can send. Different harnesses
  *  may respond differently to these events.
  *  
  *  @see cerberus.view.swing.JViewThread
  */
public interface JViewThreadListener {
	
  /** Indicates that the demo wants to be terminated. */
  public void shutdownDemo();  
  //public void shutdownView();

  /** Indicates that a repaint should be scheduled later. */
  public void repaint();
  //public void repaintView();
}
