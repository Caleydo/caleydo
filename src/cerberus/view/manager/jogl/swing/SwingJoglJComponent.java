package cerberus.view.manager.jogl.swing;


//import javax.swing.JPanel;

import java.awt.Component;

import java.awt.event.WindowAdapter;

import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;
import javax.swing.event.InternalFrameAdapter;


/**
 * Base interface for SwingJoglJFrame and SwingJoglJInternalFrame 
 * thus these components are both derived from java.awt.Component,
 * since SwingJoglJFrame extends javax.swing.JFrame and 
 * SwingJoglJInternalFrame extends javax.swing.JInternalFrame .
 * 
 * @author kalkusch
 *
 * @see cerberus.view.manager.jogl.swing.SwingJoglJFrame
 * @see cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame
 * 
 * @see java.awt.Component
 */
public interface SwingJoglJComponent {

	
	/**
	 * @see prometheus.data.IUniqueObject#setId(int)
	 */
	public void setId(int iSetCollectionId);

	/**
	 * @see prometheus.data.IUniqueObject#getId()
	 */
	public int getId();

	public void addWindowListener();

	/**
	 * ISet size and location of frame immedeatly.
	 * 
	 * Calls setLocation() and setSize().
	 * 
	 * @param iSizeX width
	 * @param iSizeY hieght
	 * @param iOffsetX offset in x
	 * @param iOffsetY offset in y
	 */
	public void setSizeAndPosition(final int iSizeX, final int iSizeY,
			final int iOffsetX, final int iOffsetY);
	
	/**
	 * ISet position of frame immedeatly.
	 * 
	 *  Calls setLocation().
	 *  
	 * @param iOffsetX offset in x
	 * @param iOffsetY offset in y
	 */
	public void setLocation( final int iOffsetX, final int iOffsetY );	
	
	/**
	 * Get type of this frame.
	 * Note: is set in constructor and used for bootstrapping from XML file.
	 * 
	 * @return type of frame
	 */
	public FrameBaseType getFrameType();
	
	/**
	 * ISet the frame type.
	 * Note: This is used for bootstraping from an XML file.
	 * 
	 * @param setFrameType type of frame
	 */
	public void setFrameType( FrameBaseType setFrameType);

	/**
	 * Overlaod from JFrame and JInternalFrame.
	 * 
	 * @param setVisible TRUE to show component
	 */
	public void setVisible( boolean setVisible );
	
	/**
	 * Overload from JInternalFrame
	 */
	public void doDefaultCloseAction();
	
	/**
	 * Add listener for window closing event. Use this methode for JFrame objects.
	 * 
	 * @see cerberus.view.manager.jogl.swing.SwingJoglJComponent#addDefaultListenerForClosingWindow(InternalFrameAdapter)
	 * 
	 * @param adapter handles closing window event
	 */
	public void addDefaultListenerForClosingWindow( WindowAdapter adapter );
	
	/**
	 * Add listener for window closing event. Use this methode for JFrame objects.
	 * 
	 * @see cerberus.view.manager.jogl.swing.SwingJoglJComponent#addDefaultListenerForClosingWindow(WindowAdapter)
	 * 
	 * @param adapter handles closing window event
	 */
	public void addDefaultListenerForClosingWindow( InternalFrameAdapter adapter );
	
//	public JPanel getStatusBar();
//	
//	public void setStatusBar(JPanel setStatusBar);


}