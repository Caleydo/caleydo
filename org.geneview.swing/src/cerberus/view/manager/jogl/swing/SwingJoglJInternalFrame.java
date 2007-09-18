/**
 * 
 */
package cerberus.view.manager.jogl.swing;

import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;

import javax.swing.JInternalFrame;

import javax.swing.event.InternalFrameAdapter;
//import javax.swing.event.InternalFrameListener;
//import javax.swing.event.InternalFrameEvent;

import cerberus.view.FrameBaseType;
import cerberus.view.swing.ISwingJoglJComponent;

/**
 * @author Michael Kalkusch
 *
 */
public class SwingJoglJInternalFrame 
  extends JInternalFrame 
  implements ISwingJoglJComponent {
//implements InternalFrameListener {
	
	private FrameBaseType frameType = FrameBaseType.EMPTY_INTERNAL_AWTFRAME;
	
	protected int iUniqueId;
	
	protected int iParentFrameUniqueId;
	
	public static final long serialVersionUID = 800900100;
	
	
	/**
	 * @param arg0
	 */
	public SwingJoglJInternalFrame(String title) {
		super(title+ " -SwingJogl inner-",true,true,true,true);
		
		init();
	}

	/**
	 * @param title
	 * @param resizable
	 * @param closable
	 * @param maximizable
	 * @param iconifiable
	 */
	public SwingJoglJInternalFrame(String title, 
			boolean resizable, 
			boolean closable, 
			boolean maximizable, 
			boolean iconifiable ) {
		super(title+ " -SwingJogl inner-", resizable, closable, maximizable, iconifiable);
		
		init();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#addWindowListener()
	 */
	public void addWindowListener() {
		assert false : "not implemented yet!";
	}
	
	private void init() {
		
//		this.addInternalFrameListener( this );				
				
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
		//this.setPreferredSize( new Dimension(iSizeX,iSizeY) );	
		//this.pack();
	}
	
	/**
	 * @see prometheus.data.IUniqueObject#setId(int)
	 */
	public void setId( int iSetCollectionId ) {
		this.iUniqueId = iSetCollectionId;
	}
	
	/**
	 * @see prometheus.data.IUniqueObject#getId()
	 */
	public int getId() {
		return iUniqueId;
	}
	
	/**
	 * @see prometheus.data.IUniqueObject#setId(int)
	 */
	public void setId_parentFrame( int iSetParentFrameUniqueId) {
		this.iParentFrameUniqueId = iSetParentFrameUniqueId;
	}
	
	/**
	 * @see prometheus.data.IUniqueObject#getId()
	 */
	public int getId_parentFrame() {
		return iParentFrameUniqueId;
	}
	
	
	public final FrameBaseType getFrameType() {
		return frameType;
	}
	
	public final void setFrameType( FrameBaseType setFrameType) {
		this.frameType = setFrameType;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#addDefaultListenerForClosingWindow(javax.swing.event.InternalFrameAdapter)
	 */
	public void addDefaultListenerForClosingWindow( InternalFrameAdapter adapter ) {
		assert adapter == null : "Can not handle null pointer instead of adapter!";
		
		this.addInternalFrameListener( adapter );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.ISwingJoglJComponent#addDefaultListenerForClosingWindow(java.awt.event.WindowAdapter)
	 */
	public void addDefaultListenerForClosingWindow( WindowAdapter adapter ) {
		throw new RuntimeException("WindowAdapter can not be connected to a JInternalFrame! Use InternalFrameAdapter instread!");
		
	}
	
//	public void internalFrameClosed(InternalFrameEvent e){
//		runExit();
//	}

}
