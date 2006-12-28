/**
 * 
 */
package cerberus.view.manager.jogl.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Vector;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;

import cerberus.view.FrameBaseType;

import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;

//import com.sun.opengl.util.FPSAnimator;

import cerberus.data.IUniqueObject;
	
/**
 * @author Michael Kalkusch
 *
 */
public class SwingJoglJFrame 
  extends JFrame 
  implements IUniqueObject, ISwingJoglJComponent {

	private FrameBaseType frameType = FrameBaseType.MAIN_FRAME;
	
	protected JDesktopPane m_desktop;

	protected Container m_contentPane;

	protected JPanel m_statusBar = null;

	protected Vector<SwingJoglJInternalFrame> vecJInternalFrame;
	
	protected int iUniqueId;
	
	/**
	 * Constructor.
	 * Creates a new Frame.
	 * 
	 * @param title title of frame
	 * @throws HeadlessException default Exception
	 */
	public SwingJoglJFrame(String title) throws HeadlessException {
		super(title + " -SwingJogl-");
		
		
		m_desktop = new JDesktopPane();
		m_contentPane = this.getContentPane();
		m_statusBar = new JPanel();
		
		vecJInternalFrame = new Vector <SwingJoglJInternalFrame> (5);
		
		//----------------------
		m_contentPane.setLayout(new BorderLayout());
		m_contentPane.add(m_desktop, BorderLayout.CENTER);
		m_contentPane.add(m_statusBar, BorderLayout.SOUTH);

		// -----  TEST new internal frame  -----
		
		JInternalFrame inner2 = createJInternalFrame("Cerverus GenView v0.1");
		
		JLabel label = new JLabel("Cerverus GenView v0.1");
		label.setFont(new Font("SansSerif", Font.PLAIN, 38));
		inner2.getContentPane().add(label);
		inner2.pack();
		inner2.setLocation( 400,400);		
		inner2.setResizable(true);
		inner2.setIconifiable(true);	
		inner2.setVisible(true);
		
		// END: -----  TEST new internal frame  -----
		
		
		//initMenus( frame );
		
	

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				runExit();
			}
		});				
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#setStatusBar(javax.swing.JPanel)
	 */
	public void setStatusBar( JPanel setStatusBar ) {
		this.m_statusBar = setStatusBar;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#setId(int)
	 */
	public void setId( int iSetCollectionId ) {
		this.iUniqueId = iSetCollectionId;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#getId()
	 */
	public int getId() {
		return iUniqueId;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#getStatusBar()
	 */
	public JPanel getStatusBar() {
		assert m_statusBar != null : "Status bar is not set!";
		return m_statusBar;
	}

	public SwingJoglJInternalFrame createJInternalFrame( final String sTitle) {
		
		SwingJoglJInternalFrame inner = new SwingJoglJInternalFrame(sTitle);		
		vecJInternalFrame.addElement( inner );
		m_desktop.add( inner );		
		
		return inner;
	}
	
	public Iterator<SwingJoglJInternalFrame> iteratorJInnerFrame() {
		return this.vecJInternalFrame.iterator();
	}
	
//	public void setSize( final int ix, final int iy ) {
//		m_desktop.setSize( ix, iy );
//	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#addWindowListener()
	 */
	public void addWindowListener() {
		assert false : "not implemented yet!";
	}
	
	private void runExit() {
		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#setSizeAndPosition(int, int, int, int)
	 */
	public void setSizeAndPosition( final int iSizeX, 
			final int iSizeY, 
			final int iOffsetX, 
			final int iOffsetY) {
		
		this.setLocation( iOffsetX,iOffsetY );					
		this.setSize( iSizeX,iSizeY );
		//this.setPreferredSize( new Dimension(iSizeX,iSizeY) );	
		//this.pack();
	}
	
	public void setPositionOnly( final int iOffsetX, 
			final int iOffsetY) {
		this.setLocation( iOffsetX,iOffsetY );	
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#getFrameType()
	 */
	public final FrameBaseType getFrameType() {
		return frameType;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#setFrameType(cerberus.view.manager.FrameBaseType)
	 */
	public final void setFrameType( FrameBaseType setFrameType) {
		this.frameType = setFrameType;
	}
	
	public void doDefaultCloseAction() {
		this.dispose();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#addDefaultListenerForClosingWindow(java.awt.event.WindowAdapter)
	 */
	public void addDefaultListenerForClosingWindow( WindowAdapter adapter ) {
		this.addWindowListener( adapter );		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#addDefaultListenerForClosingWindow(javax.swing.event.InternalFrameAdapter)
	 */
	public void addDefaultListenerForClosingWindow( InternalFrameAdapter adapter ) {
		throw new RuntimeException("InternalFrameAdapter can not be connected to a JFrame! Use WindowAdapter instread!");
	}
}
