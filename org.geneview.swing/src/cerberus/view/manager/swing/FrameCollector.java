/**
 * 
 */
package cerberus.view.manager.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import cerberus.view.FrameBaseType;
import demos.gears.Gears;

/**
 * @author Michael Kalkusch
 *
 */
public class FrameCollector {


	protected static final boolean B_FRAME_INTERNAL = true;
	
	protected static final boolean B_FRAME_EXTERNAL = false;
	
	protected static final boolean B_DEFAULT_MENU_APPAND = true;
	
	protected static final boolean B_DEFAULT_MENU_NONE = false;
	
	private Vector<JInternalFrame> vec_JInternalFrame;
	
	private Vector<JFrame> vec_JFrame;
	
	private Vector<JMenu> vec_JMenu;
	
	private CerberusJStatusBar jsb_statusBar;
	
	/**
	 * 
	 */
	public FrameCollector() {
		vec_JInternalFrame = new Vector<JInternalFrame> (20);
			
		vec_JFrame = new Vector<JFrame> (2);
			
		vec_JMenu = new Vector<JMenu> (10);
			
		jsb_statusBar = new CerberusJStatusBar();
	}
	
	private void addWindowToMenu( final String sWindowTitle ) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				Iterator<JMenu> iter = vec_JMenu.iterator();
				while ( iter.hasNext() ) {										
					JMenuItem item = new JMenuItem(sWindowTitle);
		//			item.addActionListener(new ActionListener() {
		//				public void actionPerformed(ActionEvent e) {
		//					runExit();
		//				}
		//			});
					
					iter.next().add(item);
				} // end: while
		
			} // end: run()
			
		}); // end: SwingUtilities.invokeLater(new Runnable() {
	}
	
	/**
	 * Menu handling, creates a menu entry and its actionListener.
	 * 
	 * @param refJMenu JMenu to add JMenuItem to.
	 * @param which type of window to be added
	 * @param bAsInternalFrame TRUE for internal frames, FALSE for external frames.
	 * @param bAddDefaultMenuToExternalFrame TRUE if the default application menu should be added to the Frame, which is only usfull for external frames.
	 * 
	 * @return same JMenu as in refJMenu
	 */
	protected JMenu addItemToMenu( JMenu refJMenu, 
			final FrameBaseType which, 
			final boolean bAsInternalFrame,
			final boolean bAddDefaultMenuToExternalFrame ) {
		
		if ( refJMenu == null ) {
			refJMenu = new JMenu("NONAME Actions");
		}
		
		JMenuItem item = new JMenuItem( which.getFrameMenuTitle() );
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//addWindow(which,bAsInternalFrame);
			}
		});
		refJMenu.add(item);
		
		return refJMenu;
	}
	
	private void initMenus( final JFrame refFrame ) {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("Actions");
		JMenuItem item;
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SET, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
				
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_VIRTUAL_ARRAY, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_VIRTUAL_ARRAY, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );		
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_STORAGE, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		
		menu = addItemToMenu( menu, FrameBaseType.HEATMAP, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );

		menu = addItemToMenu( menu, FrameBaseType.HISTOGRAM, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT2D, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT3D, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.HEATMAP, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );

		menu = addItemToMenu( menu, FrameBaseType.HISTOGRAM, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT2D, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT3D, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.GEARS, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.HWSHADOWS, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.HWSHADOWS, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.INFINITE, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.REFRACT, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.REFRACT, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.LOADIMAGE, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );

		menu = addItemToMenu( menu, FrameBaseType.VBO, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.VBO, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.LOADIMAGE, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.WARP, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );

		item = new JMenuItem("Loop Gears Demo");
		
		menu.add(item);

		item = new JMenuItem("create Frame");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Gears newFrameGears = new Gears();
				newFrameGears.runMain();
			}
		});
		menu.add(item);

		item = new JMenuItem("Quit");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//runExit();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		menu.add(item);
		
		JMenu window = new JMenu("Windows");
		
		menuBar.add( window );
		
		menuBar.add(menu);
		
		//registerJMenuBar( menuBar );
		
		registerJMenu( window );
		
		refFrame.setJMenuBar(menuBar);
	}
	
	public final void registerJComponent( Component addComponenet ) {
		if ( addComponenet.getClass().equals( JInternalFrame.class )) {
			registerJInternalFrame( (JInternalFrame) addComponenet );
		}
		else if ( addComponenet.getClass().equals( JFrame.class )) {
			registerJFrame( (JFrame) addComponenet );
		}
		else 
		{
			assert false : "Try to add Componet, that is neihter JFrame nor JInternalFrame";
		}
	}
	
	public synchronized void registerJFrame( JFrame addFrame ) {
		vec_JFrame.add( addFrame );
		addWindowToMenu( "E " + addFrame.getTitle() );
	}
	
	public synchronized void registerJInternalFrame( JInternalFrame addFrame ) {
		vec_JInternalFrame.add( addFrame );
		addWindowToMenu( "I " + addFrame.getTitle() );
	}
	
	public synchronized void unregisterJInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JInternalFrame.removeElement( refJInternalFrame );
	}
	
	public synchronized void unregisterJFrame( final JFrame refJFrame ) {
		vec_JFrame.removeElement( refJFrame );
	}
	
	public synchronized void registerJMenu( final JMenu refJMenu ) {
		vec_JMenu.add( refJMenu );
	}
		
	public synchronized void unregisterJMenu( final JMenu refJMenu ) {
		vec_JMenu.removeElement( refJMenu );
	}

}
