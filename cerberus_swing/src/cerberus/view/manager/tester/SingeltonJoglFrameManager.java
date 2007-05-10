/*
 * Portions Copyright (C) 2005 Sun Microsystems, Inc.
 * All rights reserved.
 */

/*
 *
 * COPYRIGHT NVIDIA CORPORATION 2003. ALL RIGHTS RESERVED.
 * BY ACCESSING OR USING THIS SOFTWARE, YOU AGREE TO:
 *
 *  1) ACKNOWLEDGE NVIDIA'S EXCLUSIVE OWNERSHIP OF ALL RIGHTS
 *     IN AND TO THE SOFTWARE;
 *
 *  2) NOT MAKE OR DISTRIBUTE COPIES OF THE SOFTWARE WITHOUT
 *     INCLUDING THIS NOTICE AND AGREEMENT;
 *
 *  3) ACKNOWLEDGE THAT TO THE MAXIMUM EXTENT PERMITTED BY
 *     APPLICABLE LAW, THIS SOFTWARE IS PROVIDED *AS IS* AND
 *     THAT NVIDIA AND ITS SUPPLIERS DISCLAIM ALL WARRANTIES,
 *     EITHER EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED
 *     TO, IMPLIED WARRANTIES OF MERCHANTABILITY  AND FITNESS
 *     FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL NVIDIA OR ITS SUPPLIERS BE LIABLE FOR ANY
 * SPECIAL, INCIDENTAL, INDIRECT, OR CONSEQUENTIAL DAMAGES
 * WHATSOEVER (INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS
 * OF BUSINESS PROFITS, BUSINESS INTERRUPTION, LOSS OF BUSINESS
 * INFORMATION, OR ANY OTHER PECUNIARY LOSS), INCLUDING ATTORNEYS'
 * FEES, RELATING TO THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF NVIDIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 */

package cerberus.view.manager.tester;

import java.awt.Container;

import java.awt.*;
//import java.awt.Container;
import java.awt.event.*;
//import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.*;

import javax.media.opengl.*;
import com.sun.opengl.util.*;
import demos.common.*;
import demos.hwShadowmapsSimple.HWShadowmapsSimple;
import demos.infiniteShadowVolumes.InfiniteShadowVolumes;
import demos.jgears.JGears;
import demos.gears.Gears;
//import demos.util.*;
import demos.vertexBufferObject.VertexBufferObject;
import demos.vertexProgRefract.VertexProgRefract;
import demos.vertexProgWarp.VertexProgWarp;

import demos.xtrans.*;

import cerberus.manager.ICommandManager;
import cerberus.manager.IFrameManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.FrameBaseType;
import cerberus.view.gui.swing.jogl.ISwingJoglJComponent;
import cerberus.view.manager.jogl.swing.SwingJoglJFrame;
import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;
import cerberus.view.manager.jogl.swing.SingeltonJoglWindowAdapter;

import cerberus.base.type.WindowToolkitType;
import cerberus.view.manager.swing.CerberusJStatusBar;
import cerberus.view.swing.texture.TestTexture;
//import cerberus.view.swing.heatmap.HeatMapWarp;
import cerberus.view.swing.heatmap.HeatMapRefract;
import cerberus.view.swing.heatmap.HeatMapDemoRefract;
import cerberus.view.swing.histogram.JoglHistogram;
import cerberus.view.swing.scatterplot.JoglScatterPlot2D;
import cerberus.view.swing.scatterplot.JoglScatterPlot3D;
import cerberus.view.swing.parallelcoord.JoglParallelCoordinates2D;
//import cerberus.view.swing.status.SelectionBrowser;
import cerberus.view.swing.status.SelectionSliderBrowser;
import cerberus.view.swing.status.SetBrowser;
import cerberus.view.swing.status.StorageBrowser;
import cerberus.view.swing.loader.FileLoader;
import cerberus.xml.parser.ACerberusDefaultSaxHandler;
import cerberus.xml.parser.jogl.SwingJoglJFrameSaxHandler;

import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.IVirtualArray;
//import cerberus.data.collection.Set;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;

/* import manager references. */
import cerberus.net.dwt.swing.menu.DMenuBootStraper;

/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java, Swing and ARB_fragment_program by Kenneth Russell
 */

public class SingeltonJoglFrameManager implements IFrameManager {
	
	private IGeneralManager refManager;
	
	private ICommandManager refCommandManager;
	
	private IMenuManager refMenuManager;
	
	private DMenuBootStraper refJMenuBar;
	
	private VirtualArrayThreadSingleBlock refTEST_Selection;
	
	private SetFlatThreadSimple refTEST_Set;
	
	private SetMultiDim refTEST_Set2D;
	
	private SetMultiDim refTEST_Set3D;
	
	private FlatThreadStorageSimple refTEST_Storage;
	
	private FlatThreadStorageSimple refTEST_Storage2;
	
	private FlatThreadStorageSimple refTEST_Storage3;
	

	private Animator animator;

	//private JDesktopPane desktop;

	private Hashtable<Integer,SwingJoglJInternalFrame> vec_JInternalFrame;
	
	private Hashtable<Integer,SwingJoglJFrame> vec_JFrame;
	
	private Hashtable<Integer,JMenu> vec_JMenu;
	
	private Hashtable<Integer,Object> hashFrameObject;

	private Hashtable<Integer, Vector<Integer> > vec_JFrame_to_JInternalFrame;
	
	private Vector<Integer> vec_JFrameIndex;
	
	private int iIdTicketIncrement = 10000;
	private int iIdTicketCurrent = 111;
	
	private CerberusJStatusBar jsb_statusBar;

	private int iCurrentFrameIndex = 0;
	
	private boolean bShutDownWhenNoActiveFrames = true;
	
	private boolean bShutDownOnNextExit = false;
	
	private boolean bIsPrimaryWindow = true;
	
	protected static final boolean B_FRAME_INTERNAL = true;
	
	protected static final boolean B_FRAME_EXTERNAL = false;
	
	protected static final boolean B_DEFAULT_MENU_APPAND = true;
	
	protected static final boolean B_DEFAULT_MENU_NONE = false;
	
	protected ACerberusDefaultSaxHandler handlerSax;
	
	public static void main(String[] args) {
		
		System.out.println("  ...Cerberus v0.1 01-2006 ...");
		
		OneForAllManager regGeneralManager = new OneForAllManager(null);
		regGeneralManager.initAll();
		
		new SingeltonJoglFrameManager( regGeneralManager ).run(args);
	}

	
	public SingeltonJoglFrameManager( final IGeneralManager setManager,
			final boolean setIsPrimaryWindow ) {
		
		refManager = setManager;
		
		bIsPrimaryWindow = setIsPrimaryWindow;
		
		refCommandManager = refManager.getSingelton().getCommandManager();
		
		refMenuManager = 
			refManager.getSingelton().getMenuManager();
		
		refJMenuBar = new DMenuBootStraper( refCommandManager );
	}
	
	public SingeltonJoglFrameManager( IGeneralManager setGeneralManager ) {
		
		this.refManager = setGeneralManager;
		
		createNewId();
		
		initDatastructures();		
		
		handlerSax = new SwingJoglJFrameSaxHandler( refManager, this);
	}

	private void initDatastructures() {
		vec_JInternalFrame = new Hashtable<Integer,SwingJoglJInternalFrame> ();		
		vec_JFrame = new Hashtable<Integer,SwingJoglJFrame> ();		
		vec_JMenu = new Hashtable<Integer,JMenu> ();		
		vec_JFrameIndex = new  Vector<Integer> ();
		vec_JFrame_to_JInternalFrame = new Hashtable<Integer, Vector<Integer> > ();		 
		hashFrameObject = new Hashtable<Integer,Object> ();
		
		refTEST_Selection = 
			new VirtualArrayThreadSingleBlock(0,null,null);
						
		refTEST_Storage = new FlatThreadStorageSimple(0,null,null);
		refTEST_Storage2 = new FlatThreadStorageSimple(0,null,null);
		refTEST_Storage3 = new FlatThreadStorageSimple(0,null,null);
		
		refTEST_Set = new SetFlatThreadSimple(0,null,null);
		refTEST_Set2D = new SetMultiDim(0,null,null,2);
		refTEST_Set3D = new SetMultiDim(0,null,null,3);
		
		IVirtualArray[] helpSelect = new IVirtualArray[1];
		helpSelect[0] = refTEST_Selection;
		
		IStorage [] helpStore = new IStorage [1];
		helpStore[0] = refTEST_Storage;
		
		refTEST_Set.setVirtualArrayByDim(helpSelect,0);
		refTEST_Set.setStorageByDim(helpStore,0);
		
		
		/*
		 * assing multi-set
		 */
		
		refTEST_Set2D.addSelectionByDim(refTEST_Selection,0 );
		refTEST_Set2D.addSelectionByDim(refTEST_Selection,1);
		refTEST_Set2D.addStorageByDim(refTEST_Storage,0);
		refTEST_Set2D.addStorageByDim(refTEST_Storage2,1);
		
		refTEST_Set3D.addSelectionByDim(refTEST_Selection,0 );
		refTEST_Set3D.addSelectionByDim(refTEST_Selection,1);
		refTEST_Set3D.addSelectionByDim(refTEST_Selection,2);
		refTEST_Set3D.addStorageByDim(refTEST_Storage,0);
		refTEST_Set3D.addStorageByDim(refTEST_Storage2,1);
		refTEST_Set3D.addStorageByDim(refTEST_Storage3,2);
		
//		SetFlatThreadSimple helpSet2 = new SetFlatThreadSimple(0,null,null);
//		VirtualArrayThreadSingleBlock helpSelect2 = new VirtualArrayThreadSingleBlock(0,null,null);
//		
//		helpSet2.setStorageByDimAndIndex(refTEST_Storage2,0,0);
//		helpSet2.setSelectionByDimAndIndex(helpSelect2,0,0);
//		
//		FileLoader loader_storage2 = new FileLoader();
//		loader_storage2.setSet(helpSet2);
//		loader_storage2.setText(" dim=2");
//		loader_storage2.load();
//		
//		SetFlatThreadSimple helpSet3 = new SetFlatThreadSimple(0,null,null);
//		VirtualArrayThreadSingleBlock helpSelect3 = new VirtualArrayThreadSingleBlock(0,null,null);
//		
//		helpSet3.setStorageByDimAndIndex(refTEST_Storage3,0,0);
//		helpSet3.setSelectionByDimAndIndex(helpSelect3,0,0);
//		
//		FileLoader loader_storage3 = new FileLoader();		
//		loader_storage3.setSet(helpSet3);
//		loader_storage3.setText(" dim=3");
//		loader_storage3.load();
		
		
		jsb_statusBar = new CerberusJStatusBar();
	}
	
	
	public void initMenus( final JFrame refFrame,
			final int iUniqueFrameId ) {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Actions");
		JMenuItem item;
		
//		refJMenuBar.addMenuItemWithCommand("sMenuName","addMenu","tooltip my",'X',"root",true,null);
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SET, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );				
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_VIRTUAL_ARRAY, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );	
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_VIRTUAL_ARRAY, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );				
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_STORAGE, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		
		menu = addItemToMenu( menu, FrameBaseType.HEATMAP, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );

		menu = addItemToMenu( menu, FrameBaseType.HISTOGRAM, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT2D, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT3D, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.PARALLELCOORDINATES2D, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.HEATMAP, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );

		menu = addItemToMenu( menu, FrameBaseType.HISTOGRAM, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT2D, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT3D, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.PARALLELCOORDINATES2D, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.GEARS, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.HWSHADOWS, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.HWSHADOWS, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.INFINITE, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.REFRACT, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.REFRACT, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.LOADIMAGE, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );

		menu = addItemToMenu( menu, FrameBaseType.VBO, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.VBO, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.LOADIMAGE, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		
		menu = addItemToMenu( menu, FrameBaseType.WARP, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE, iUniqueFrameId );
		

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
				enableShutDown();
				runExit();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		menu.add(item);
		
		JMenu window = new JMenu("Windows");
		
		JMenu windowActive = new JMenu("View details");
		
		item = new JMenuItem("create internal");
		windowActive.add(item);
		item = new JMenuItem("create external");
		windowActive.add(item);
		item = new JMenuItem("local / remote");
		windowActive.add(item);
		
		JMenu setActiveWindow = new JMenu("set active window..");
		
		windowActive.add(setActiveWindow);

		item = new JMenuItem("this window");
		setActiveWindow.add(item);
		item = new JMenuItem("other window");
		setActiveWindow.add(item);
		
		
		menuBar.add( window );
		
		menuBar.add(menu);
		
		menuBar.add( windowActive );
		
		//registerJMenuBar( menuBar );
		
		//menuBar.add( refJMenuBar.getMenuBar() );
		
		registerJMenu( window );
		
		refFrame.setJMenuBar(menuBar);
	}
	
	public synchronized int createNewId() {
		return iIdTicketCurrent += iIdTicketIncrement;
	}
	
	public synchronized boolean setCurrentFrame( int iSetCurrentFrameIndex ) {
		if (( iSetCurrentFrameIndex >= 0 )&&( iSetCurrentFrameIndex < vec_JFrame.size() )) {
			this.iCurrentFrameIndex = iSetCurrentFrameIndex;
			return true;
		}
		return false;
	}
	
	private synchronized void registerJMenu( final JMenu refJMenu ) {
	//private void registerInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JMenu.put( new Integer(createNewId()), refJMenu );
	}
	
	private synchronized void unregisterJMenu( final JMenu refJMenu ) {
	//private void unregisterInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JMenu.remove( refJMenu );
	}
	
	private void addWindowToMenu( final String sWindowTitle ) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				Iterator<JMenu> iter = vec_JMenu.values().iterator();
				while ( iter.hasNext() ) {										
					JMenuItem item = new JMenuItem(sWindowTitle);
		//			item.addActionListener(new ActionListener() {
		//				public void actionPerformed(ActionEvent e) {
		//					runExit();
		//				}
		//			});
					
					JMenu menu = iter.next();
					menu.add(item);
				} // end: while
		
			} // end: run()
			
		}); // end: SwingUtilities.invokeLater(new Runnable() {
	}
	
	public synchronized void unregisterJInternalFrame( final int iUniqueFrameId  ) {		
		vec_JInternalFrame.remove( new Integer(iUniqueFrameId) );
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		if ( hashFrameObject.containsKey( iItemId ) ) {
			hashFrameObject.remove( iItemId );
			
			if ( this.vec_JFrame.containsKey( iItemId ) ) {
				Iterator <Integer> iter = vec_JFrameIndex.iterator();
				
				while ( iter.hasNext() ) {
					if ( iter.next().intValue() == iItemId ) {
						iter.remove();
						break;
					}
				}
				
				ISwingJoglJComponent frame = vec_JFrame.remove( iItemId );
				
				if ( frame == null ) {
					throw new RuntimeException("Can not unregister frame from Hashtable");
				}
				
				Vector <Integer> vecRefIFrame = 
					vec_JFrame_to_JInternalFrame.remove( Integer.valueOf(iItemId) );
				if ( vecRefIFrame != null ) {
					/* close nestern inner frames of this frame...*/
					iter = vecRefIFrame.iterator();
					while ( iter.hasNext() ) {
						unregisterJInternalFrame( iter.next().intValue() ); 
					}
				}
			}
			else if ( this.vec_JInternalFrame.containsKey( iItemId )) {
				unregisterJInternalFrame( iItemId );
			}
			else {
				throw new RuntimeException("Can not unregister frame with id=" +
						Integer.toString(iItemId) ); 
			}
		}
		return false;
	}

	protected boolean hasActiveFrame() {
		if ( bShutDownWhenNoActiveFrames ) {
			return ! vec_JFrame.isEmpty();
		}		
		return true;
	}
	
	public synchronized void unregisterJFrame( final int iUniqueFrameId ) {
		vec_JFrameIndex.remove( iUniqueFrameId );
		vec_JFrame.remove( new Integer(iUniqueFrameId) );
	}

	public ISwingJoglJComponent addWindow(FrameBaseType which,
			int iUniqueViewId, int iUniquePartenViewId) {
		boolean isInternalFrame = true;
		
		if ( iUniquePartenViewId <= 0 ) {
			isInternalFrame = false;
		}
		
		return (ISwingJoglJComponent) addWindow( which, isInternalFrame, iUniqueViewId );
	}
	
	public Container addWindow(FrameBaseType which, 
			final boolean bIsInternalFrame,
			final int iUniqueViewId ) {
		if ( which.isGLCanvas() ) {
			return addWindow_GLcanvas(which,bIsInternalFrame,iUniqueViewId);
		}
		return addWindow_AWTcanvas(which,bIsInternalFrame,iUniqueViewId);
	}
		
	private Container addWindow_AWTcanvas(FrameBaseType which, 
			final boolean bIsInternalFrame,
			final int iUniqueViewId ) {
		
		assert ! which.isGLCanvas() : "calling addWindow_AWTcanvas() with a GLcanvas request";
		
		/**
		 * Create variabel for internal and external Frame and assign on null while 
		 * using the other variabel.
		 */
		final JInternalFrame inner;
		final JFrame outer;
		
		/**
		 * Handle creation internal and external frames different...		
		 */
		if ( bIsInternalFrame ) {
			outer = null;
			
			inner = createNewJInternalFrame(which.getFrameMenuTitle(),iUniqueViewId);
			
			inner.setResizable(true);
			inner.setClosable(true);
			inner.setVisible(true);			
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
				case JBROWSER_VIRTUAL_ARRAY: {
					//SelectionBrowser ref2FrameSel = new SelectionBrowser(inner);
					SelectionSliderBrowser ref2FrameSel = new SelectionSliderBrowser(inner);
					ref2FrameSel.setSelection( refTEST_Selection );					
					break;
				}
				
				case JBROWSER_SET: {
					SetBrowser ref2FrameSet = new SetBrowser(inner);
					ref2FrameSet.setSet( refTEST_Set );
					break;
				}
				
				case JBROWSER_STORAGE: {					
					StorageBrowser ref2FrameStore = new StorageBrowser(inner);
					ref2FrameStore.setStorage( refTEST_Storage );
					break;
				}
				
				
			
				default:
					assert false: "Unsupported type [" + which.toString() + "]";
				
			} // end: switch
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {
			inner = null;
			
			outer = new JFrame( which.getFrameTitle() + " external" );
			outer.setResizable(true);
			outer.setVisible(true);
			outer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
			case JBROWSER_VIRTUAL_ARRAY: {
				//SelectionBrowser ref2Frame = new SelectionBrowser(outer);
				SelectionSliderBrowser ref2Frame = new  SelectionSliderBrowser(outer);
				ref2Frame.setSelection( refTEST_Selection );
				break;
			}
		
			default:
				assert false: "Unsupported type [" + which.toString() + "]";
			
		} // end: switch
			
		} // end: if ( bIsInternalFrame ) {...} else {...}
		/**
		 * END: Handle creation internal and external frames different...		
		 */
		
		
		switch (which) {
			case JBROWSER_VIRTUAL_ARRAY: {
				break;
			}
		
			default:
				assert false: "Unsupported type [" + which.toString() + "]";
			
		} // end: switch
		
		
		/**
		 * Handle cleanup of internal and external frames different...		
		 */
		if ( bIsInternalFrame ) {
	
			inner.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosed(InternalFrameEvent e) {
					System.err.println("Close Internal AWT-Frame...");
				}
			});
			
			//desktop.add(inner);
			
//			this.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
			return inner;
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {

			outer.addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e)  {
					System.err.println("Close Internal AWT-Frame...");
				}
			});

			outer.setSize(512, 512);
			outer.setVisible( true );
			
//			this.registerJFrame( outer, which.getFrameMenuTitle() );
			
			return outer;
		} // end: if ( bIsInternalFrame ) {...} else {...}	
		
		/**
		 * END: Handle cleanup of internal and external frames different...		
		 */		
	}
	
	
	private Container addWindow_GLcanvas(FrameBaseType which, 
			final boolean bIsInternalFrame,
			final int iUniqueViewId ) {
		
		assert which.isGLCanvas() : "calling addWindow_GLcanvas() with a AWT-canvas request";
				
		// FIXME: workaround for problem in 1.6 where ALL Components,
		// including Swing components, are Finalizable, requiring two full
		// GC cycles (and running of finalizers) to reclaim
		System.gc();
		// Try to get finalizers run
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
		System.gc();

		String str = which.getFrameTitle();


		GLCapabilities caps = new GLCapabilities();

		final GLJPanel canvas = (which == FrameBaseType.GEARS) ? 
				new JGears() : new GLJPanel(caps);
				
		/**
		 * Create variabel for internal and external Frame and assign on null while 
		 * using the other variabel.
		 */
		final JInternalFrame inner;
		final JFrame outer;
		
		/**
		 * use only one threaded GLListener for (JInternalFrame) inner and (JFrame) outer
		 */
		final DemoListener demoListener;
		
		/**
		 * Handle creation internal and external frames different...		
		 */
		if ( bIsInternalFrame ) {
			outer = null;
						
			inner = createNewJInternalFrame(str,iUniqueViewId);
			
			inner.setResizable(true);
			inner.setClosable(true);
			inner.setVisible(true);			
			
			demoListener = new DemoListener() {
		
				public void shutdownDemo() {
					removeJPanel(canvas);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							inner.doDefaultCloseAction();
						}
					});
				}
	
				public void repaint() {
					canvas.repaint();
				}
			};
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {
			inner = null;
			
			outer = new JFrame( which.getFrameTitle() + " external" );
			outer.setResizable(true);
			outer.setVisible(true);
			outer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			demoListener = new DemoListener() {
				public void shutdownDemo() {
					removeJPanel(canvas);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							outer.setVisible( false );
							outer.dispose(); //.doDefaultCloseAction();
						}
					});
				}
				public void repaint() {
					canvas.repaint();
				}
			};
		} // end: if ( bIsInternalFrame ) {...} else {...}
		/**
		 * END: Handle creation internal and external frames different...		
		 */
		
		Demo demo = null;
		
		switch (which) {
		case GEARS: {
			// GLEventListener already added
			break;
		}

		case HWSHADOWS: {
			demo = new HWShadowmapsSimple();
			break;
		}

		case INFINITE: {
			demo = new InfiniteShadowVolumes();
			break;
		}

		case REFRACT: {
			demo = new VertexProgRefract();
			break;
		}

		case VBO: {
			demo = new VertexBufferObject();
			break;
		}

		case WARP: {
			demo = new VertexProgWarp();
			((VertexProgWarp) demo)
					.setTitleSetter(new VertexProgWarp.TitleSetter() {
						public void setTitle(final String title) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									inner.setTitle(title);
								}
							});
						}
					});
			break;
		} 
		
		case HEATMAP: {
			
			HeatMapRefract demoHeatMap = new HeatMapRefract();
			demoHeatMap.setSet(refTEST_Set);

			demo = demoHeatMap;
				
			break;
		}
		
		case HISTOGRAM: {
			
//			demo = new HeatMapDemoRefract();
			
			JoglHistogram demoHistogram = new JoglHistogram();
			demoHistogram.setSet(refTEST_Set);
			
			demo = demoHistogram;
			
			break;
		}
		
		case SCATTERPLOT2D: {
			
			JoglScatterPlot2D demoScatter = new JoglScatterPlot2D();
			demoScatter.setSet( refTEST_Set2D );
			
			demo = demoScatter;
			
			break;
		}
		
		case SCATTERPLOT3D: {
			
			JoglScatterPlot3D demoScatter = new JoglScatterPlot3D();
			demoScatter.setSet( refTEST_Set3D );
			
			demo = demoScatter;
			
			break;
		}
		
		case PARALLELCOORDINATES2D: {
			
			JoglParallelCoordinates2D demoScatter = 
				new JoglParallelCoordinates2D();
			demoScatter.setSet( refTEST_Set3D );
			
			demo = demoScatter;
			
			break;
		}
		
//		case HEATMAP_WARP: {
//
//		demo = new VertexProgWarp();
//		((VertexProgWarp) demo)
//				.setTitleSetter(new VertexProgWarp.TitleSetter() {
//					public void setTitle(final String title) {
//						SwingUtilities.invokeLater(new Runnable() {
//							public void run() {
//								inner.setTitle(title);
//							}
//						});
//					}
//				});
//			break;
//		}
		
		case LOADIMAGE: {
			demo = new TestTexture();
			break;
		}
		
		default:
			throw new RuntimeException("unsupported type addWindow_GLcanvas(" +
					which.toString() + ")");

		} // end  switch
		
		
		if (which != FrameBaseType.GEARS) {
			demo.setDemoListener(demoListener);
			canvas.addGLEventListener(demo);
		}
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				canvas.requestFocus();
			}
		});

		addJPanel(canvas);

		final Demo fDemo = demo;
		
		/**
		 * Handle cleanup of internal and external frames different...		
		 */
		if ( bIsInternalFrame ) {
	
			inner.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosed(InternalFrameEvent e) {
					if (fDemo != null) {
						fDemo.shutdownDemo();
					}
				}
			});
			
			inner.getContentPane().setLayout(new BorderLayout());
			/*    if (which == REFRACT) {
			 // Testing scrolling
			 canvas.setSize(512, 512);
			 canvas.setPreferredSize(new Dimension(512, 512));
			 JScrollPane scroller = new JScrollPane(canvas);
			 inner.getContentPane().add(scroller);
			 } else */
			
			if (which == FrameBaseType.GEARS) {
				// Provide control over transparency of gears background
				canvas.setOpaque(false);
				JPanel gradientPanel = JGears.createGradientPanel();
				inner.getContentPane().add(gradientPanel, BorderLayout.CENTER);
				gradientPanel.add(canvas, BorderLayout.CENTER);

				final JCheckBox checkBox = new JCheckBox("Transparent", true);
				checkBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						canvas.setOpaque(!checkBox.isSelected());
					}
				});
				inner.getContentPane().add(checkBox, BorderLayout.SOUTH);
			} else {
				inner.getContentPane().add(canvas, BorderLayout.CENTER);
			}
			
			if ( which == FrameBaseType.HEATMAP ) {
				 JMenu menu = new JMenu("load..");
				    
			    JMenuItem item = new JMenuItem("load gpr");
			    final HeatMapRefract refDemo = (HeatMapRefract) demo;
			    
			    item.addActionListener( new ActionListener() { 
			    		 public void actionPerformed(ActionEvent e) {
			    			 refDemo.load();
			    		 }
			    		 });
			    		 
			    menu.add( item );
			    
			    JMenuBar menuBar = new JMenuBar();
			    menuBar.add( menu );
			    
			    inner.setJMenuBar( menuBar );
			}			
			else if ( which == FrameBaseType.HISTOGRAM ) {
				 JMenu menu = new JMenu("mode");
				    
				 final JoglHistogram refDemo = (JoglHistogram) demo;
				 
			    JMenuItem item = new JMenuItem("toggle mode");
			    JMenuItem item2 = new JMenuItem("inc+");
			    JMenuItem item3 = new JMenuItem("dec-");
			    
			    
			    item.addActionListener( new ActionListener() { 
			    		 public void actionPerformed(ActionEvent e) {
			    			 refDemo.toggleMode();
			    		 }
			    		 });
			    item2.addActionListener( new ActionListener() { 
			    		 public void actionPerformed(ActionEvent e) {
			    			 refDemo.setHistogramLength(
			    					 refDemo.getHistogramLength() + 25 );
			    		 }
			    		 });
			    item3.addActionListener( new ActionListener() { 
		    		 public void actionPerformed(ActionEvent e) {
		    			 refDemo.setHistogramLength(
		    					 refDemo.getHistogramLength() - 25 );
		    		 }
		    		 });
			    		 
			    
			    menu.add( item );
			    menu.add( item2 );
			    menu.add( item3 );
			    
			    JMenuBar menuBar = new JMenuBar();
			    menuBar.add( menu );
			    
			    inner.setJMenuBar( menuBar );
			}

			inner.setSize(512, 512);

			//desktop.add(inner);
			
//			this.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
			return inner;
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {

			outer.addWindowListener( new WindowAdapter() {
				public void windowClosing(WindowEvent e)  {
					if (fDemo != null) {
						fDemo.shutdownDemo();
					}
				}
			});

			outer.getContentPane().setLayout(new BorderLayout());
			
			if (which == FrameBaseType.GEARS) {
				// Provide control over transparency of gears background
				canvas.setOpaque(false);
				JPanel gradientPanel = JGears.createGradientPanel();
				outer.getContentPane().add(gradientPanel, BorderLayout.CENTER);
				gradientPanel.add(canvas, BorderLayout.CENTER);

				final JCheckBox checkBox = new JCheckBox("Transparent", true);
				checkBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						canvas.setOpaque(!checkBox.isSelected());
					}
				});
				outer.getContentPane().add(checkBox, BorderLayout.SOUTH);
			} else {
				outer.getContentPane().add(canvas, BorderLayout.CENTER);
			}

			outer.setSize(512, 512);
			outer.setVisible( true );
			
//			this.registerJFrame( outer, which.getFrameMenuTitle() );
			
			return outer;
		} // end: if ( bIsInternalFrame ) {...} else {...}	
		
		/**
		 * END: Handle cleanup of internal and external frames different...		
		 */
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
			final boolean bAddDefaultMenuToExternalFrame,
			final int iUniqueViewId ) {
		
		if ( refJMenu == null ) {
			refJMenu = new JMenu("NONAME Actions");
		}
		
		JMenuItem item = new JMenuItem( which.getFrameMenuTitle() );
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWindow(which,bAsInternalFrame,iUniqueViewId);
			}
		});
		refJMenu.add(item);
		
		return refJMenu;
	}
	
	public void run(String[] args) {
		
		SwingJoglJFrame frame = this.createNewJFrame("Cerberus v0.1.1");		
		
//		JFrame frame = new JFrame("Cerberus v0.1");
//		if ((args.length > 0) && args[0].equals("-xt")) {
//			desktop = new XTDesktopPane();
//			// FIXME: this is a hack to get the repaint behavior to work correctly
//			((XTDesktopPane) desktop).setAlwaysRedraw(true);
//		} else {
//			desktop = new JDesktopPane();
//		}
//

		
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				runExit();
			}
		});
		
		
//		SwingJoglJInternalFrame inner3 = 
//			createNewJInternalFrame("Test internal frame");
//	
////		JInternalFrame inner2 = new JInternalFrame("Cerverus GenView v0.1");
//		JLabel label = new JLabel("Cerverus GenView v0.1");
//		label.setFont(new Font("SansSerif", Font.PLAIN, 38));
//		
//		inner3.getContentPane().add(label);
//		inner3.pack();
//		inner3.setLocation( 400,400);		
//		inner3.setResizable(false);
//		inner3.setIconifiable(false);	
//		inner3.setVisible(true);
				
		
//		inner2.getContentPane().add(label);
//		inner2.pack();
//		inner2.setLocation( 400,400);		
//		inner2.setResizable(true);
//		inner2.setIconifiable(true);	
//		desktop.add(inner2);
//		inner2.setVisible(true);
		
		
//		initMenus( frame );
		
		frame.setSizeAndPosition( 1200,300, 200, 400 );
		frame.setVisible( true );
		
		//frame.setSize(  )jsb_statusBar
		
		animator = new FPSAnimator(60);
		
//		/* --- test Heatmap --- */
//		addWindow(FrameBaseType.HEATMAP,B_FRAME_INTERNAL);
//		
//		addWindow(FrameBaseType.HISTOGRAM,B_FRAME_INTERNAL);
//		
//		//addWindow(FrameBaseType.SCATTERPLOT2D,B_FRAME_INTERNAL);
//		
		
		animator.start();
	}

	public void enableShutDown() {
		bShutDownOnNextExit = true;
	}
	
	public void runExit() {
		
		if (( bShutDownOnNextExit )||( ! hasActiveFrame() )) {
			
		// Note: calling System.exit() synchronously inside the draw,
		// reshape or init callbacks can lead to deadlocks on certain
		// platforms (in particular, X11) because the JAWT's locking
		// routines cause a global AWT lock to be grabbed. Instead run
		// the exit routine in another thread.
		new Thread(new Runnable() {
			public void run() {
				
				if ( animator != null ) {
					animator.stop();
				}
				System.out.println("Application shut down..");
				
				Enumeration <SwingJoglJFrame> enumFrame = vec_JFrame.elements();
				
				String xmlData = "<cerberus version=\"0.1\">\n";
				
				while ( enumFrame.hasMoreElements() ) {
					SwingJoglJFrame frame = enumFrame.nextElement();
					
					xmlData += handlerSax.createXML( frame , "  " );
					
					
					Vector<Integer> vec_Integer = 
						vec_JFrame_to_JInternalFrame.get( new Integer( frame.getId() ) );
					
					if ( vec_Integer != null ) {
						/* has internal frames...*/
						Iterator <Integer> iter = vec_Integer.iterator();
						
						while ( iter.hasNext() ) {
							SwingJoglJInternalFrame iframe = vec_JInternalFrame.get( iter.next() );
							
							xmlData += handlerSax.createXML( iframe , "    " ); 
						}
					}
					
					xmlData += handlerSax.createXMLcloseingTag( frame , "  " );
					
				}
				
				xmlData += "\n<\\cerberus>\n";
				
				System.out.println(" -----  XML  -----\n");
				System.out.println( xmlData );
				
				System.exit(0);
			}
		}).start();
		
		}
		else 
		{
			System.err.println("Shut down one frame.." );
		}
	}

	public boolean hasItem( final int iTestId ) {
		return hashFrameObject.containsKey( iTestId );
	}
	
	public Object getItem( final int iTestId ) {
		return hashFrameObject.get( iTestId );
	}
	
	public synchronized void addJPanel(GLJPanel panel) {
		animator.add(panel);
	}

	public synchronized void removeJPanel(GLJPanel panel) {
		animator.remove(panel);
	}
	
	public synchronized SwingJoglJInternalFrame createNewJInternalFrame( 
			final String title,
			final int iJFrameId ) {
		
		SwingJoglJInternalFrame inner;		
		
		/*
		int iNewUniqueId;
		
		if ( iJFrameId < 0 ) {
			iNewUniqueId = this.createNewUniqueId();
		} else {
			iNewUniqueId = iJFrameId;
		}
			
		inner = vec_JFrame.get( vec_JFrameIndex.indexOf(iNewUniqueId).intValue()
				).createJInternalFrame(title);	
		*/
		
		SwingJoglJFrame parentFrame = vec_JFrame.get(iJFrameId);
		
		if ( parentFrame == null ) {
			return null;
		}
		
		
		inner = parentFrame.createJInternalFrame( "S-JOGL-I " + title);
		
		int iNewUniqueId = this.createNewId();
		inner.setId( iNewUniqueId );
		
		vec_JInternalFrame.put( new Integer( iNewUniqueId ), inner );	
		
		Integer iUniquIdINT =  new Integer( iNewUniqueId );
		
		Vector<Integer> vec_Integer = 
			vec_JFrame_to_JInternalFrame.get( new Integer( iNewUniqueId ) );
		
		if ( vec_Integer == null ) {
			vec_Integer = new Vector<Integer>();
		}
		
		vec_Integer.add( new Integer(iNewUniqueId) );
		
		vec_JFrame_to_JInternalFrame.put(iUniquIdINT,vec_Integer);
		hashFrameObject.put(new Integer(iNewUniqueId) , inner);
		
		addWindowToMenu( "I " + title );			
		
		return inner;
	}
	
	public String getCurrentParameters() {
		String result = "CMD_OPENBUFFER\n";
		
		Iterator <SwingJoglJFrame> iter_frame = this.vec_JFrame.values().iterator();
		
		while ( iter_frame.hasNext() ) {
			SwingJoglJFrame frame = iter_frame.next();
			
			Dimension dim = frame.getSize();
			result += "CMD_OPENFRAME w=" + Integer.toString(dim.width) +
			" h=" + Integer.toString(dim.height);
			
			Point location = frame.getLocation();
			result += " x=" + location.x + " y=" + location.y;
		}
		result += "CMD_FLUSHBUFFER\n";
		
		return result;
	}
	
	public synchronized SwingJoglJFrame createNewJFrame( final String title) {
		
		SwingJoglJFrame frame = new SwingJoglJFrame( "S-JOGL " + title );	
		int iNewUniqueId = this.createNewId();
		frame.setId( iNewUniqueId );
		
		vec_JFrame.put( new Integer(iNewUniqueId), frame );
		vec_JFrameIndex.add( iNewUniqueId );		
		vec_JFrame_to_JInternalFrame.put( new Integer( iNewUniqueId ),
					new Vector<Integer>() );
		hashFrameObject.put(new Integer(iNewUniqueId) , frame);
		
		initMenus( frame, iNewUniqueId );
		frame.setSizeAndPosition( 1200,300, 200, 400 );
//		frame.setVisible( true );
		
		
		frame.addWindowListener(
			new SingeltonJoglWindowAdapter ( this, iNewUniqueId ) );
		
		addWindowToMenu( "E " + title );
		
		return frame;
	}
	
	public SwingJoglJInternalFrame getJInternalFrameById( final int iId ) {
		return vec_JInternalFrame.get( new Integer(iId));
	}


}
