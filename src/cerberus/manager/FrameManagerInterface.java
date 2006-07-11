package cerberus.manager;

import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.SwingJoglJComponent;

public interface FrameManagerInterface {

	/**
	 * Creats a new internal or external frame.
	 * 
	 * @param which type of new frame
	 * @param iUniqueViewId froma id must be larger than 0
	 * @param iUniquePartenViewId if < 0 this is a external frame
	 * @return
	 */
	public SwingJoglJComponent addWindow(FrameBaseType which,
			int iUniqueViewId, int iUniquePartenViewId);

	public boolean hasItem(final int iTestId);

	public Object getItem(final int iTestId);

}