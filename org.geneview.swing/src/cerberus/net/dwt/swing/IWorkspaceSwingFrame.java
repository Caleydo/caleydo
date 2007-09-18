package cerberus.net.dwt.swing;

import javax.swing.JMenuBar;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.net.dwt.swing.mdi.DDesktopPane;
import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.net.dwt.swing.menu.DMenuBootStraper;

public interface IWorkspaceSwingFrame {

	public abstract void initCanvas();

	public abstract DDesktopPane flipDesktopPane(DDesktopPane setDDesktopPane);

	public abstract DMenuBootStraper flipDMenuBootStraper(
			DMenuBootStraper setDMenuBootStraper);

	public abstract JMenuBar getJMenuBar();

	public abstract DMenuBootStraper getDMenuBootStraper();

	public abstract void setTargetFrame(WorkspaceSwingFrame setTargetFrame);

	/**
	 * Get the reference to the main desktop .
	 * 
	 * @see cerberus.net.dwt.swing.mdi.DDesktopPane
	 * 
	 * @return reference to desktop pane
	 */
	public abstract DDesktopPane getDesktopPane();

	/**
	 * Creats a new DInternalFrame using this main desktop.
	 * 
	 * @see cerberus.net.dwt.swing.mdi.DDesktopPane
	 * 
	 * @return reference to desktop pane
	 */
	public abstract DInternalFrame createDInternalFrame(final String sTextHeader);

	/**
	 * Get the manager that created the item.
	 * 
	 * @return reference to ACollectionManager
	 */
	public abstract IGeneralManager getManager();

	/**
	 * Resets the selectionId.
	 * @param iSetCollectionId new unique collection Id
	 */
	public abstract void setId(int iSetCollectionId);

	/**
	 * Get a unique Id
	 * 
	 * @return unique Id
	 */
	public abstract int getId();

	/**
	 * Get Id of the target frame defined in this frame.
	 * Used to create new internal frames either inside this frame or in other frames.
	 * Default is this onw unique Id (see also getId() )
	 * 
	 * @return unique Id of the target frame
	 */
	public abstract int getTargetFrameId();

	/**
	 * Get the type of this object.
	 * 
	 * @return type of this object
	 */
	public abstract ManagerObjectType getBaseType();

}