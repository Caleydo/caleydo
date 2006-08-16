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

package cerberus.view.manager.jogl.swing;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
//import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
//import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
//import javax.swing.event.InternalFrameListener;

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

//import demos.xtrans.*;

import cerberus.manager.ICommandManager;
import cerberus.manager.IFrameManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.ISwingJoglJComponent;
import cerberus.view.manager.jogl.swing.SwingJoglJFrame;
import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;
import cerberus.view.manager.jogl.swing.util.SwingJoglWindowAdapter;
import cerberus.view.manager.jogl.swing.util.TesterStuff;

//import cerberus.base.WindowToolkitType;

import cerberus.view.manager.swing.CerberusJStatusBar;
import cerberus.view.swing.texture.TestTexture;
//import cerberus.view.swing.heatmap.HeatMapWarp;
import cerberus.view.swing.heatmap.HeatMapRefract;
//import cerberus.view.swing.heatmap.HeatMapDemoRefract;
import cerberus.view.swing.histogram.JoglHistogram;
import cerberus.view.swing.scatterplot.JoglScatterPlot2D;
import cerberus.view.swing.scatterplot.JoglScatterPlot3D;
import cerberus.view.swing.parallelcoord.JoglParallelCoordinates2D;
//import cerberus.view.swing.status.SelectionBrowser;
import cerberus.view.swing.status.SelectionSliderBrowser;
import cerberus.view.swing.status.SetBrowser;
import cerberus.view.swing.status.StorageBrowser;
import cerberus.xml.parser.jogl.SwingJoglJFrameSaxHandler;
//import cerberus.view.swing.loader.FileLoader;

import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.ISelection;
//import cerberus.data.collection.Set;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.selection.SelectionThreadSingleBlock;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;

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

public class CanvasSwingJoglManager 
	extends AAbstractManager 
	implements IFrameManager {
	
	private IGeneralManager refManager;
	
	private ICommandManager refCommandManager;
	
	private IMenuManager refMenuManager;
	
	private DMenuBootStraper refJMenuBar;
	
//	private SelectionThreadSingleBlock refTEST_Selection;
	
	private TesterStuff it;
	
//	private SetFlatThreadSimple refTEST_Set;
//	
//	private SetMultiDim refTEST_Set2D;
//	
//	private SetMultiDim refTEST_Set3D;
//	
//	private FlatThreadStorageSimple refTEST_Storage;
//	
//	private FlatThreadStorageSimple refTEST_Storage2;
//	
//	private FlatThreadStorageSimple refTEST_Storage3;
	

	private Animator animator;

	//private JDesktopPane desktop;

	private Hashtable<Integer,Integer> hash_IdInternalFrame_2_IdParentFrame;
	
	private Hashtable<Integer,SwingJoglJInternalFrame> hash_Id_2_InternalFrame;
	
	private Hashtable<Integer,SwingJoglJFrame> hash_Id_2_Frame;
	
	private Hashtable<Integer,JMenu> hash_Id_2_Menu;
	
	private Hashtable<Integer,Component> hash_Id_2_anyComponent;

	private Hashtable<Integer, Vector<Integer> > hash_IdFrame_2_vecInternalFrame;
	
	private int iIdFrameIncrement = IGeneralManager.iUniqueId_Increment;
	private int iIdFrameCurrent = IGeneralManager.iUniqueId_Workspace;
	
	private int iIdInternalFrameIncrement = IGeneralManager.iUniqueId_Increment;
	private int iIdInternalFrameCurrent = IGeneralManager.iUniqueId_View;
	
	private int iIdMenuIncrement = IGeneralManager.iUniqueId_Menu_Inc;
	private int iIdMenuCurrent = IGeneralManager.iUniqueId_Menu_Offset;
	
	private CerberusJStatusBar jsb_statusBar;

	private int iCurrentFrameIndex = 0;
	
	private boolean bShutDownWhenNoActiveFrames = true;
	
	private boolean bShutDownOnNextExit = false;
	
	private boolean bIsPrimaryWindow = true;
	
	protected static final boolean B_FRAME_INTERNAL = true;
	
	protected static final boolean B_FRAME_EXTERNAL = false;
	
	protected static final boolean B_DEFAULT_MENU_APPAND = true;
	
	protected static final boolean B_DEFAULT_MENU_NONE = false;
	
	protected SwingJoglJFrameSaxHandler handlerSax;
	
	public static void main(String[] args) {
		
		System.out.println("  ...Cerberus v0.1 01-2006 ...");
		
		OneForAllManager generalManager = new OneForAllManager(null);
		generalManager.initAll();
		
		CanvasSwingJoglManager viewManager = 
			(CanvasSwingJoglManager) generalManager.getSingelton().getViewCanvasManager();
		
		viewManager.run(args);
	}

	
	public CanvasSwingJoglManager( final IGeneralManager setManager,
			final boolean setIsPrimaryWindow ) {
		
		super( setManager,
				IGeneralManager.iUniqueId_TypeOffset_GuiAWT );
		
		//TODO: check why the constructors differ!
		
		
		refManager = setManager;
		
		bIsPrimaryWindow = setIsPrimaryWindow;
		
		refCommandManager = refManager.getSingelton().getCommandManager();
		
		refMenuManager = 
			refManager.getSingelton().getMenuManager();
		
		refJMenuBar = new DMenuBootStraper( refCommandManager );
		
		
	}
	
	public CanvasSwingJoglManager(  final IGeneralManager setManager ) { 
		
		super( setManager,				
				IGeneralManager.iUniqueId_TypeOffset_GuiAWT);
		
		//TODO: check why the constructors differ!
		
		initDatastructures();		
		
		handlerSax = new SwingJoglJFrameSaxHandler( setManager, this );
	}

	private void initDatastructures() {
		hash_Id_2_InternalFrame = new Hashtable<Integer,SwingJoglJInternalFrame> ();		
		hash_Id_2_Frame = new Hashtable<Integer,SwingJoglJFrame> ();		
		hash_Id_2_Menu = new Hashtable<Integer,JMenu> ();		
		hash_IdFrame_2_vecInternalFrame = new Hashtable<Integer, Vector<Integer> > ();		 
		hash_Id_2_anyComponent = new Hashtable<Integer,Component> ();		
		hash_IdInternalFrame_2_IdParentFrame = new Hashtable<Integer,Integer> ();
		
		it = new TesterStuff();
		
		jsb_statusBar = new CerberusJStatusBar();
	}
	
	
	public void initMenus( final JFrame refFrame,
			final int iUniqueFrameId,
			final int iUniquePartenViewId ) {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Actions");
		JMenuItem item;
		
//		refJMenuBar.addMenuItemWithCommand("sMenuName","addMenu","tooltip my",'X',"root",true,null);
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SET, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );				
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SELECTION, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );	
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SELECTION, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );				
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_STORAGE, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		
		menu = addItemToMenu( menu, FrameBaseType.HEATMAP, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );

		menu = addItemToMenu( menu, FrameBaseType.HISTOGRAM, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT2D, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT3D, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.PARALLELCOORDINATES2D, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.HEATMAP, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );

		menu = addItemToMenu( menu, FrameBaseType.HISTOGRAM, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT2D, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.SCATTERPLOT3D, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.PARALLELCOORDINATES2D, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.GEARS, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.HWSHADOWS, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.HWSHADOWS, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.INFINITE, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.REFRACT, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.REFRACT, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.LOADIMAGE, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );

		menu = addItemToMenu( menu, FrameBaseType.VBO, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.VBO, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.LOADIMAGE, B_FRAME_EXTERNAL, iUniqueFrameId, iUniquePartenViewId );
		
		menu = addItemToMenu( menu, FrameBaseType.WARP, B_FRAME_INTERNAL, iUniqueFrameId, iUniquePartenViewId );
		

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
	
	protected synchronized int createNewFrameId() {
		return iIdFrameCurrent += iIdFrameIncrement;
	}
	
	protected synchronized int createNewInternalFrameId() {
		return iIdInternalFrameCurrent += iIdInternalFrameIncrement;
	}
	
	protected synchronized int createNewMenuId() {
		return iIdMenuCurrent += iIdMenuIncrement;
	}
	
	public synchronized boolean setCurrentFrame( int iSetCurrentFrameIndex ) {
		if (( iSetCurrentFrameIndex >= 0 )&&( iSetCurrentFrameIndex < hash_Id_2_Frame.size() )) {
			this.iCurrentFrameIndex = iSetCurrentFrameIndex;
			return true;
		}
		return false;
	}
	
	public SwingJoglJFrameSaxHandler getSAXHandler() {
		return handlerSax;
	}
	
	private synchronized void registerJMenu( final JMenu refJMenu ) {
	//private void registerInternalFrame( final JInternalFrame refJInternalFrame ) {
		hash_Id_2_Menu.put( new Integer(createNewMenuId()), refJMenu );
	}
	
	private synchronized void unregisterJMenu( final JMenu refJMenu ) {
	//private void unregisterInternalFrame( final JInternalFrame refJInternalFrame ) {
		hash_Id_2_Menu.remove( refJMenu );
	}
	
	private void addWindowToMenu( final String sWindowTitle ) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				Iterator<JMenu> iter = hash_Id_2_Menu.values().iterator();
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
	
	protected synchronized void unregisterJInternalFrame( final int iUniqueFrameId  ) {		
		
		System.err.println("unregister internal frame [" + iUniqueFrameId + "] ...");
		
		SwingJoglJInternalFrame removeIFrame = 
			hash_Id_2_InternalFrame.remove( iUniqueFrameId );
				
		if ( removeIFrame == null ) {
			throw new RuntimeException("Try to unregister internal frame [" +
					iUniqueFrameId + "] that is not registered.");
		}
		
		Component removeIFrame2 = 
			hash_Id_2_anyComponent.remove( iUniqueFrameId );
		
		if ( removeIFrame2 == null ) {
			throw new RuntimeException("Try to unregister internal frame [" +
					iUniqueFrameId + "] that is not registered as a component.");
		}
		
		/**
		 * Get the Id of the parent container ...
		 */
		int iParentContainerId = hash_IdInternalFrame_2_IdParentFrame.get( iUniqueFrameId );
					
		/**
		 * use the id of the parent container to unregister this internal frame 
		 * from the parent frame inside the storage.
		 */
		Vector<Integer> vec_Index = 
			hash_IdFrame_2_vecInternalFrame.get(iParentContainerId);
		
		if ( vec_Index != null ) {
			vec_Index.removeElement(iUniqueFrameId);
			
			hash_IdFrame_2_vecInternalFrame.put(iParentContainerId,vec_Index);
			
		} else {
			throw new RuntimeException("Try to unregister an internal frame [" +
					iUniqueFrameId + ", but parent frame has not registerd that internal frame");
		}
		
		hash_IdInternalFrame_2_IdParentFrame.remove( iUniqueFrameId );
		
		//TODO: proper rollback in case of an error..
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		if ( this.hash_Id_2_Menu.containsKey( iItemId ) ) {
			unregisterJMenu( hash_Id_2_Menu.get( iItemId ) );
		}
		if ( this.hash_Id_2_InternalFrame.containsKey( iItemId ) ) {
			unregisterJInternalFrame( iItemId );
		}
		else if ( hash_Id_2_Frame.containsKey( iItemId ) ) {
			
			/**
			 * Do not destroy frame content of last frame! 
			 * LASt frame is destroyed after parsing its content and
			 * writein ti to an XML file.
			 * See method  public void runExit() for details!  
			 */
			if ( hash_Id_2_Frame.size() > 1 ) {
				unregisterJFrame( iItemId );
			}
			else {
				this.bShutDownOnNextExit = true;
			}
		}
		else {
			return false;
		}
		
		return true;
	}

	protected boolean hasActiveFrame() {
		if ( bShutDownWhenNoActiveFrames ) {
			
			return ! hash_Id_2_Frame.isEmpty();
		}		
		return true;
	}
	
	protected synchronized void unregisterJFrame( final int iUniqueFrameId ) {
		
		System.err.println("unregister frame [" + iUniqueFrameId + "] ...");
		
		hash_Id_2_Frame.remove( iUniqueFrameId );
	}

	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.FrameManagerInterface#addWindow(cerberus.view.manager.FrameBaseType, int, int)
	 */
	public ISwingJoglJComponent addWindow(FrameBaseType which, 
			int iUniqueViewId,
			int iUniquePartenViewId ) {
		
//		if (! bIsInternalFrame) {
//			if ( iUniqueViewId < iIdFrameIncrement ) {
//				iUniqueViewId = createNewFrameId();
//			}
//		}
		
		
		if ( which.isGLCanvas() ) {	
			return addWindow_GLcanvas(which,iUniqueViewId,iUniquePartenViewId);
		}
		return addWindow_AWTcanvas(which,iUniqueViewId,iUniquePartenViewId);
	}
		
	private ISwingJoglJComponent addWindow_AWTcanvas(FrameBaseType which, 
			//final boolean bIsInternalFrame,
			final int iUniqueViewId,
			final int iUniquePartenViewId ) {
		
		assert ! which.isGLCanvas() : "calling addWindow_AWTcanvas() with a GLcanvas request";
		
		/**
		 * Create variabel for internal and external Frame and assign on null while 
		 * using the other variabel.
		 */
//		final SwingJoglJInternalFrame inner;
//		final SwingJoglJFrame outer;
		
		/** 
		 * In order to be able to put this function inside a methode this variabel is used.
		 */
		final ISwingJoglJComponent newFrame;
		
		/**
		 * Handle creation internal and external frames different...		
		 */
		if ( iUniquePartenViewId > 0 ) {
//			outer = null;
			
			SwingJoglJInternalFrame inner = 
				createNewJInternalFrame(which.getFrameMenuTitle(),iUniqueViewId,iUniquePartenViewId);	
			newFrame= inner;
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
				case JBROWSER_SELECTION: {
					//SelectionBrowser ref2FrameSel = new SelectionBrowser(inner);
					SelectionSliderBrowser ref2FrameSel = new SelectionSliderBrowser(inner);
					ref2FrameSel.setSelection( it.refTEST_Selection );	
					break;
				}
				
				case JBROWSER_SET: {
					SetBrowser ref2FrameSet = new SetBrowser(inner);
					ref2FrameSet.setSet( it.refTEST_Set );
					break;
				}
				
				case JBROWSER_STORAGE: {					
					StorageBrowser ref2FrameStore = new StorageBrowser(inner);
					ref2FrameStore.setStorage( it.refTEST_Storage );
					break;
				}
				
				
			
				default:
					assert false: "Unsupported type [" + which.toString() + "]";
					System.err.println( "Unsupported type [" + which.toString() + "]" );
				
			} // end: switch
			
		} // end: if ( iUniquePartenViewId > 0 ) {...}
		else 
		{ // else of: if ( iUniquePartenViewId > 0 ) {...} else {
//			inner = null;
			
			SwingJoglJFrame outer = 
				createNewJFrame(which.getFrameTitle() + " external" , iUniqueViewId);			
			newFrame= outer;
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
			case JBROWSER_SELECTION: {
				//SelectionBrowser ref2Frame = new SelectionBrowser(outer);
				SelectionSliderBrowser ref2Frame = new  SelectionSliderBrowser(outer);
				ref2Frame.setSelection( it.refTEST_Selection );
				newFrame.setFrameType( FrameBaseType.JBROWSER_SET );
				break;
			}
			
			case MAIN_FRAME: {
				newFrame.setFrameType( FrameBaseType.MAIN_FRAME );
				break;
			}
		
			default:
				assert false: "Unsupported type [" + which.toString() + "]";
			
			} // end: switch
			
			
		} // end: if ( iUniquePartenViewId > 0 ) {...} else {...}
		
		newFrame.setFrameType( which );
		
		/**
		 * END: Handle creation internal and external frames different...		
		 */
		
		
		switch (which) {
			case JBROWSER_SELECTION: {
				break;
			}
		
			default:
				assert false: "Unsupported type [" + which.toString() + "]";
			
		} // end: switch
		
		
		/**
		 * Handle cleanup of internal and external frames different...		
		 */
		if ( iUniquePartenViewId > 0 ) {
	
			final CanvasSwingJoglManager canvasMgn = this;
			
			newFrame.addDefaultListenerForClosingWindow( 
					new InternalFrameAdapter() {
						public void internalFrameClosed(InternalFrameEvent e) {
							System.err.println("Close Internal AWT-Frame...");
							canvasMgn.unregisterJInternalFrame( newFrame.getId() );
						}
					}
				);
			
			//desktop.add(inner);
			
//			this.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
			return newFrame;
			
		} // end: if ( iUniquePartenViewId > 0 ) {...}
		else 
		{ // else of: if ( iUniquePartenViewId > 0 ) {...} else {

			final CanvasSwingJoglManager canvasMgn = this;
			
			newFrame.addDefaultListenerForClosingWindow( 
				new WindowAdapter() {
					public void windowClosing(WindowEvent e)  {
						System.err.println("Close AWT-Frame...");
						canvasMgn.unregisterJFrame( newFrame.getId() );
					}
				}
			);

			newFrame.setSizeAndPosition( 512, 512, 0,0 );
			//newFrame.setVisible( true );
			
//			this.registerJFrame( outer, which.getFrameMenuTitle() );
			
			return newFrame;
		} // end: if ( iUniquePartenViewId > 0 ) {...} else {...}	
		
		/**
		 * END: Handle cleanup of internal and external frames different...		
		 */		
	}
	
	
	private ISwingJoglJComponent addWindow_GLcanvas(FrameBaseType which, 
			//final boolean bIsInternalFrame,
			final int iUniqueViewId,
			final int iUniquePartenViewId  ) {
		
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
//		final JInternalFrame inner;
//		final JFrame outer;
		
		final ISwingJoglJComponent newFrame;
		
		/**
		 * use only one threaded GLListener for (JInternalFrame) inner and (JFrame) outer
		 */
		final DemoListener demoListener;
		
		/**
		 * Handle creation internal and external frames different...		
		 */
		if ( iUniquePartenViewId > 0 ) {
						
			newFrame = createNewJInternalFrame(str,iUniqueViewId,iUniquePartenViewId);		
			final CanvasSwingJoglManager canvasMgn = this;		
			
			demoListener = new DemoListener() {								
				
				public void shutdownDemo() {
					removeJPanel(canvas);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							System.err.println("Close Internal GL-Frame...");
							canvasMgn.unregisterJInternalFrame( newFrame.getId() );
							newFrame.doDefaultCloseAction();
						}
					});
				}
	
				public void repaint() {
					canvas.repaint();
				}
			};
			
		} // end: if ( iUniquePartenViewId > 0 ) {...}
		else 
		{ // else of: if ( iUniquePartenViewId > 0  ) {...} else {
//			inner = null;
			
			newFrame = this.createNewJFrame( which.getFrameTitle() + " external", iUniqueViewId );
			
			final CanvasSwingJoglManager canvasMgn = this;		
			
			demoListener = new DemoListener() {
				public void shutdownDemo() {
					removeJPanel(canvas);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							newFrame.setVisible( false );
							canvasMgn.unregisterJFrame( newFrame.getId() );
							newFrame.doDefaultCloseAction();
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
//			((VertexProgWarp) demo)
//					.setTitleSetter(new VertexProgWarp.TitleSetter() {
//						public void setTitle(final String title) {
//							SwingUtilities.invokeLater(new Runnable() {
//								public void run() {
//									newFrame.setTitle(title);
//								}
//							});
//						}
//					});
			break;
		} 
		
		case HEATMAP: {
			
			HeatMapRefract demoHeatMap = new HeatMapRefract();
			demoHeatMap.setSet( it.refTEST_Set);

			demo = demoHeatMap;
				
			break;
		}
		
		case HISTOGRAM: {
			
//			demo = new HeatMapDemoRefract();
			
			JoglHistogram demoHistogram = new JoglHistogram();
			demoHistogram.setSet(it.refTEST_Set);
			
			demo = demoHistogram;
			
			break;
		}
		
		case SCATTERPLOT2D: {
			
			JoglScatterPlot2D demoScatter = new JoglScatterPlot2D();
			demoScatter.setSet( it.refTEST_Set2D );
			
			demo = demoScatter;
			
			break;
		}
		
		case SCATTERPLOT3D: {
			
			JoglScatterPlot3D demoScatter = new JoglScatterPlot3D();
			demoScatter.setSet( it.refTEST_Set3D );
			
			demo = demoScatter;
			
			break;
		}
		
		case PARALLELCOORDINATES2D: {
			
			JoglParallelCoordinates2D demoScatter = 
				new JoglParallelCoordinates2D();
			demoScatter.setSet( it.refTEST_Set3D );
			
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
		newFrame.setFrameType( which );
		
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
		if ( iUniquePartenViewId > 0  ) {
	
			SwingJoglJInternalFrame inner = (SwingJoglJInternalFrame) newFrame;
			
			inner.setId_parentFrame( iUniquePartenViewId );
			
			inner.addInternalFrameListener(
					
					new InternalFrameAdapter() {
//			inner.addInternalFrameListener(new InternalFrameAdapter() {
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
			
		} // end: if ( iUniquePartenViewId > 0  ) {...}
		else 
		{ // else of: if ( iUniquePartenViewId > 0  ) {...} else {

			newFrame.setId( iUniqueViewId );
			
			SwingJoglJFrame outer = (SwingJoglJFrame) newFrame;						
			
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
		} // end: if ( iUniquePartenViewId > 0  ) {...} else {...}	
		
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
			//final boolean bAsInternalFrame,
			final boolean bAddDefaultMenuToExternalFrame,
			final int iUniqueViewId,
			final int iUniquePartenViewId ) {
		
		if ( refJMenu == null ) {
			refJMenu = new JMenu("NONAME Actions");
		}
		
		JMenuItem item = new JMenuItem( which.getFrameMenuTitle() );
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWindow(which, iUniqueViewId, iUniquePartenViewId);
			}
		});
		refJMenu.add(item);
		
		return refJMenu;
	}
	
	public void run(String[] args) {
		
		SwingJoglJFrame frame = this.createNewJFrame("Cerberus v0.1.1", -1);		
		
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
		
		frame.setSizeAndPosition( 1200,800, 200, 400 );
		frame.setVisible( true );
		
		SwingJoglJFrame frame2 = this.createNewJFrame("Cerberus v0.1.1", -1);
		
		frame2.setSizeAndPosition( 800,900, 100, 100 );
		frame2.setVisible( true );
		
		SwingJoglJInternalFrame testIFrame = 
			createNewJInternalFrame("TestFrame", frame2.getId(), -1 );
		testIFrame.setLocation( 30,30 );
		testIFrame.setSize( 200, 40 );
		
		//frame.setSize(  )jsb_statusBar
		
		run_Animator();
		
//		/* --- test Heatmap --- */
//		addWindow(FrameBaseType.HEATMAP,B_FRAME_INTERNAL);
//		
//		addWindow(FrameBaseType.HISTOGRAM,B_FRAME_INTERNAL);
//		
//		//addWindow(FrameBaseType.SCATTERPLOT2D,B_FRAME_INTERNAL);
//						
	}

	/**
	 * Start the FPSAnimator.
	 * If no instance was created it creates a new instance.
	 *
	 * @throws RuntimeException if Animator could not be created
	 */
	public void run_Animator() {
		if ( animator == null ) {
			animator = new FPSAnimator(60);
		} else {
			throw new RuntimeException("Try to create animator, but it has been created already!");
		}
		
		if ( ! animator.isAnimating() ) {
			animator.start();
		} else {
			throw new RuntimeException("Try to start animator, but it is running already!");
		}
		
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
					if ( animator.isAnimating() ) {
						animator.stop();
					}
				}
				System.out.println("Application shut down..");
				
				Enumeration <SwingJoglJFrame> enumFrame = hash_Id_2_Frame.elements();
				
				String xmlData = "<cerberus version=\"0.1\">\n";
				xmlData += "  <Application id=\"1000\" server=\"127.0.0.1\">\n\n";
				xmlData += "  <FrameState>\n\n";
				
				while ( enumFrame.hasMoreElements() ) {
					SwingJoglJFrame frame = enumFrame.nextElement();
					
					xmlData += handlerSax.createXML( frame , "  " );
					
					
					Vector<Integer> vec_Integer = 
						hash_IdFrame_2_vecInternalFrame.get( new Integer( frame.getId() ) );
					
					if ( vec_Integer != null ) {
						/* has internal frames...*/
						Iterator <Integer> iter = vec_Integer.iterator();
						
						while ( iter.hasNext() ) {
							SwingJoglJInternalFrame iframe = hash_Id_2_InternalFrame.get( iter.next() );
							
							xmlData += handlerSax.createXML( iframe , "    " ); 
						}
					}
					
					xmlData += handlerSax.createXMLcloseingTag( frame , "  " );
					
					/**
					 * Calls unregister Frame directly not via
					 * unregisterItem(), because the last frame con not be closed by this 
					 */
					unregisterJFrame( frame.getId() );
					
				}
				
				xmlData += "\n  </FrameState>\n\n";
				
				xmlData += "  </Application>\n\n";
				
				xmlData += "</cerberus>\n";
				
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

	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.FrameManagerInterface#hasItem(int)
	 */
	public boolean hasItem( final int iTestId ) {
		return hash_Id_2_anyComponent.containsKey( iTestId );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.manager.jogl.swing.FrameManagerInterface#getItem(int)
	 */
	public Object getItem( final int iTestId ) {
		return hash_Id_2_anyComponent.get( iTestId );
	}
	
	protected synchronized void addJPanel(GLJPanel panel) {
		animator.add(panel);
	}

	protected synchronized void removeJPanel(GLJPanel panel) {
		animator.remove(panel);
	}	
	
	public synchronized SwingJoglJInternalFrame createNewJInternalFrame( 
			final String title,
			final int iJFrameId,
			final int iUniquePartenViewId) {
		
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
		
		SwingJoglJFrame parentFrame = this.hash_Id_2_Frame.get( iUniquePartenViewId );
		//SwingJoglJFrame parentFrame = hash_Id_2_Frame.get(iJFrameId);
		
		if ( parentFrame == null ) {
			throw new RuntimeException("InternalFrame referes to non-existing parentframe! INTERNAL ERROR");			
			//return null;
		}		
		
		inner = parentFrame.createJInternalFrame( "S-JOGL-I " + title);		
		inner.setId_parentFrame( iUniquePartenViewId );
		
		inner.setResizable(true);
		inner.setClosable(true);
		inner.setVisible(true);	
		
		int iNewUniqueId = iJFrameId;
		
		if ( iJFrameId < 0 ) {
			iNewUniqueId = this.createNewInternalFrameId();
		}
		
		inner.setId( iNewUniqueId );
		
		/** Register new frame to all hashmaps.. */
		Integer iUniquIdINT =  new Integer( iNewUniqueId );
		
		hash_Id_2_InternalFrame.put( iUniquIdINT, inner );		
		hash_Id_2_InternalFrame.put(iUniquIdINT,inner);
		hash_Id_2_anyComponent.put(iUniquIdINT,inner);
		hash_IdInternalFrame_2_IdParentFrame.put(iUniquIdINT,iJFrameId);
		
		Vector<Integer> vec_Integer = 
			hash_IdFrame_2_vecInternalFrame.get( iUniquePartenViewId );
		
		if ( vec_Integer == null ) {
			vec_Integer = new Vector<Integer>();
		}
		
		vec_Integer.add( iNewUniqueId );
		
		hash_IdFrame_2_vecInternalFrame.put( iUniquePartenViewId ,vec_Integer);
		
		
		addWindowToMenu( "I " + title );			
		
		return inner;
	}
	
	public String getCurrentParameters() {
		String result = "CMD_OPENBUFFER\n";
		
		Iterator <SwingJoglJFrame> iter_frame = this.hash_Id_2_Frame.values().iterator();
		
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
	
	public synchronized SwingJoglJFrame createNewJFrame( final String title, 
			final int iUniqueFrameId ) {
		
		SwingJoglJFrame frame = new SwingJoglJFrame( "S-JOGL " + title );	
		
		final int iNewUniqueId;
		
		if ( iUniqueFrameId > 0 ) {
			iNewUniqueId = iUniqueFrameId; 
		} else {
			iNewUniqueId = createNewFrameId();
		}
		
		/* TODO: check if id is unique!	 */
		
		frame.setId( iNewUniqueId );
		
		hash_Id_2_Frame.put( new Integer(iNewUniqueId), frame );		
		hash_IdFrame_2_vecInternalFrame.put( new Integer( iNewUniqueId ),
					new Vector<Integer>() );
		hash_Id_2_anyComponent.put(new Integer(iNewUniqueId) , frame);
		
		initMenus( frame, iNewUniqueId, -1 );
		
		frame.setSizeAndPosition( 1200,300, 200, 400 );
//		frame.setVisible( true );
		
		frame.setResizable(true);
		//frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.addWindowListener(
			new SwingJoglWindowAdapter ( this, iNewUniqueId ) );
		
		addWindowToMenu( "E " + title );
		
		return frame;
	}
	
	public SwingJoglJInternalFrame getJInternalFrameById( final int iId ) {
		return hash_Id_2_InternalFrame.get( new Integer(iId));
	}

}
