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

package cerberus.view.manager.swing;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import java.awt.event.WindowListener;
import java.util.Vector;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

//import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import demos.common.*;
import demos.hwShadowmapsSimple.HWShadowmapsSimple;
import demos.infiniteShadowVolumes.InfiniteShadowVolumes;
import demos.gears.Gears;
//import demos.util.*;

import demos.vertexBufferObject.VertexBufferObject;
import demos.vertexProgRefract.VertexProgRefract;
import demos.vertexProgWarp.VertexProgWarp;

import demos.xtrans.*;


import cerberus.base.WindowToolkitType;
import cerberus.view.FrameBaseType;
import cerberus.view.manager.swing.CerberusJStatusBar;
import cerberus.view.swing.texture.TestTexture;
//import cerberus.view.swing.heatmap.HeatMapWarp;
import cerberus.view.swing.heatmap.HeatMapRefract;
import cerberus.view.swing.heatmap.HeatMapDemoRefract;
import cerberus.view.swing.histogram.JoglHistogram;
import cerberus.view.swing.scatterplot.JoglScatterPlot2D;
import cerberus.view.swing.scatterplot.JoglScatterPlot3D;
//import cerberus.view.swing.status.SelectionBrowser;
import cerberus.view.swing.status.SelectionSliderBrowser;
import cerberus.view.swing.status.SetBrowser;
import cerberus.view.swing.status.StorageBrowser;

/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java, Swing and ARB_fragment_program by Kenneth Russell
 */

public class JFrameThreaded extends JFrame implements Runnable {
	
	static final long serialVersionUID = 990077;

	private Animator animator;

	private JDesktopPane desktop;

	private Vector<JInternalFrame> vec_JInternalFrame;
	
	private Vector<JFrame> vec_JFrame;
	
	private Vector<JMenu> vec_JMenu;
	
	private CerberusJStatusBar jsb_statusBar;

	protected static final boolean B_FRAME_INTERNAL = true;
	
	protected static final boolean B_FRAME_EXTERNAL = false;
	
	protected static final boolean B_DEFAULT_MENU_APPAND = true;
	
	protected static final boolean B_DEFAULT_MENU_NONE = false;
	
	private boolean bIsInternalContainer = true;
	
	private GLJPanel m_gl_canvas = null;
	
	private DemoListener refDemoListener = null;

	protected JFrameThreaded() {
		initDatastructures();		
	}

	private void initDatastructures() {
		vec_JInternalFrame = new Vector<JInternalFrame> ();
		
		vec_JFrame = new Vector<JFrame> ();
		
		vec_JMenu = new Vector<JMenu> ();
		
	
		
		/*
		 * assing multi-set
		 */
	
		
		jsb_statusBar = new CerberusJStatusBar();
				
	}
	
	/**
	 * Assign canvas or create a new canvas.
	 * 
	 * @see cerberus.view.manager.swing.JFrameThreaded#getCanvas()
	 * 
	 * @param canvas assing canvas, if null a new canvas is created 
	 */
	public void setCanvas( GLJPanel canvas ) {
		if ( canvas != null ) {
			this.m_gl_canvas = canvas;
		}
	}
	
	/**
	 * Get the current GL-canvas.
	 * 
	 * @see cerberus.view.manager.swing.JFrameThreaded#createCanvas(GLJPanel)
	 * 
	 * @return GL canvas
	 */
	public GLJPanel getCanvas() {
		return m_gl_canvas;
	}
	
	
	public void setGLListener( DemoListener setDemoListener ) {
		if ( setDemoListener != null ) {
			refDemoListener = setDemoListener;
		}
	}
	

	
	private void initMenus( final JFrame refFrame ) {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("Actions");
		JMenuItem item;
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SET, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
				
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SELECTION, B_FRAME_INTERNAL, B_DEFAULT_MENU_NONE );
		
		menu = addItemToMenu( menu, FrameBaseType.JBROWSER_SELECTION, B_FRAME_EXTERNAL, B_DEFAULT_MENU_NONE );		
		
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
				runExit();
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
	
	private synchronized void registerJMenu( final JMenu refJMenu ) {
	//private void registerInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JMenu.add( refJMenu );
	}
	
	private synchronized void unregisterJMenu( final JMenu refJMenu ) {
	//private void unregisterInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JMenu.removeElement( refJMenu );
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
		
	private synchronized void registerJInternalFrame( final JInternalFrame refJInternalFrame,
			final String sFrameTitle ) {
	//private void registerInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JInternalFrame.add( refJInternalFrame );
		addWindowToMenu( "I " + sFrameTitle );
	}
	
	private synchronized void unregisterJInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JInternalFrame.removeElement( refJInternalFrame );
	}
	
	private synchronized void registerJFrame( final JFrame refJFrame,
			final String sFrameTitle ) {
		//private void registerInternalFrame( final JInternalFrame refJInternalFrame ) {
		vec_JFrame.add( refJFrame );
		addWindowToMenu( "E " + sFrameTitle );
	}
	
	private synchronized void unregisterJFrame( final JFrame refJFrame ) {
		vec_JFrame.removeElement( refJFrame );
	}
	
	/**
	 * Create a new window, which can be either an internal frame or an external frame 
	 * (JInternalFrame or JFrame).
	 * 
	 * @see cerberus.view.manager.swing.JFrameThreaded#createContent(FrameBaseType, boolean)
	 * 
	 * @param which used for label of new frame
	 * @param bIsInternalFrame TRUE creats an internal-frame, FALSE creates an JFrame
	 * @return created internal or external Frame
	 */
	private Container createWindow( final FrameBaseType which, final boolean bIsInternalFrame) {
				
		if ( bIsInternalFrame ) {
			JInternalFrame inner = new JInternalFrame(which.getFrameMenuTitle());
			this.bIsInternalContainer = true;

			this.createContent( inner, which, bIsInternalFrame);
			
			return (Container) inner;
		}
		else
		{
			JFrame outer = new JFrame(which.getFrameMenuTitle());
			this.bIsInternalContainer = false;
			
			this.createContent( outer, which, bIsInternalFrame);
			
			return (Container) outer;
		}
	}
	
	private Object createContent(Container frame,
			final FrameBaseType which, 
			final boolean bIsInternalFrame) {
		
		if ( which.isGLCanvas() ) {
			
			try {
				
				Demo demo = null;
				
				if ( bIsInternalFrame ) {				
					JInternalFrame frameInternal = (JInternalFrame) frame;
					
					frameInternal.setResizable(true);
					frameInternal.setClosable(true);
					frameInternal.setVisible(true);	
					
					demo =  createContent_JoglInternalFrame(frameInternal,which);
				} else {
					JFrame frameExternal = (JFrame) frame;
					
					frameExternal.setResizable(true);
					frameExternal.setVisible(true);
					frameExternal.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					
					demo =  createContent_JoglExternalFrame(frameExternal,which);
				}
				
				return addWindow( frame, demo, which);
				
			} catch ( NullPointerException npe ) {
				assert false : "can not cast (Container) frame to JFrame==" + bIsInternalFrame;
				return null;
			} // end: try {..}
			
		} //end: if ( which.isGLCanvas() ) {
		else {
			return createContent_Swing( frame, which, bIsInternalFrame);
		} //end: if ( which.isGLCanvas() ) {...} else {
	}
	
	/**
	 * Creats a new content.
	 * 
	 * @see cerberus.view.manager.swing.JFrameThreaded#createWindow(FrameBaseType, boolean)
	 * 
	 * @param frame define the frame used to house the content
	 * @param which define type of content
	 * @param bIsInternalFrame
	 * @return
	 */
	private Object createContent_Swing( Container frame,
			final FrameBaseType which, 
			final boolean bIsInternalFrame ) {
		
		//Demo newDemo;
		
		Object createdDemo = null;
		
		if ( bIsInternalFrame ) {
			
			JInternalFrame inner = null;
			
			try {
				inner = (JInternalFrame) frame;
			} catch ( NullPointerException npe ) {
				assert false : "container does not match boolean value";
				return null;
			}
			
			switch (which) {
			case JBROWSER_SELECTION: {
				//SelectionBrowser ref2FrameSel = new SelectionBrowser(inner);
				SelectionSliderBrowser ref2FrameSel = new SelectionSliderBrowser(inner);					
				createdDemo = ref2FrameSel;
				break;
			}
			
			case JBROWSER_SET: {
				SetBrowser ref2FrameSet = new SetBrowser(inner);
				createdDemo = ref2FrameSet;
				break;
			}
			
			case JBROWSER_STORAGE: {					
				StorageBrowser ref2FrameStore = new StorageBrowser(inner);
				createdDemo = ref2FrameStore;
				break;
			}
			
			default:
				assert false : "not supported type [" + which.toString() + "]";
			
			}//end: switch
			
						
		} // end: if ( bIsInternalFrame ) {
		else
		{
			JFrame outer = null;
			
			try {
				outer = (JFrame) frame;
			} catch ( NullPointerException npe ) {
				assert false : "container does not match boolean value";
				return null;
			}
			
			switch (which) {
			case JBROWSER_SELECTION: {
				//SelectionBrowser ref2FrameSel = new SelectionBrowser(inner);
				SelectionSliderBrowser ref2FrameSel = new SelectionSliderBrowser(outer);
				createdDemo = ref2FrameSel;
				break;
			}
			
			case JBROWSER_SET: {
				SetBrowser ref2FrameSet = new SetBrowser(outer);
				createdDemo = ref2FrameSet;
				break;
			}
			
			case JBROWSER_STORAGE: {					
				StorageBrowser ref2FrameStore = new StorageBrowser(outer);
				createdDemo = ref2FrameStore;
				break;
			}
			
			default:
				assert false : "not supported type [" + which.toString() + "]";
			}//end: switch
			
		} // end: if ( bIsInternalFrame ) {...} else {
		
			
		return createdDemo;
	}
	
	
	
	
	
	/**
	 * Creats a new content.
	 * 
	 * @see cerberus.view.manager.swing.JFrameThreaded#createWindow(FrameBaseType, boolean)
	 * 
	 * @param frame define the frame used to house the content
	 * @param which define type of content
	 * @param bIsInternalFrame
	 * @return
	 */
	private Demo createContent_JoglInternalFrame( JInternalFrame frame,
			final FrameBaseType which ) {
		
		Demo demo = null;
		
		switch (which) {

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
				
				final JInternalFrame finalJFrame = frame;
				
				((VertexProgWarp) demo)
						.setTitleSetter(new VertexProgWarp.TitleSetter() {
							public void setTitle(final String title) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										finalJFrame.setTitle(title);
									}
								});
							}
						});
				break;
			} 
			
			case HEATMAP: {
				
				HeatMapRefract demoHeatMap = new HeatMapRefract();
//				demoHeatMap.setSet(refTEST_Set);

				demo = demoHeatMap;
					
				break;
			}
			
			case HISTOGRAM: {
				
//				demo = new HeatMapDemoRefract();
				
				JoglHistogram demoHistogram = new JoglHistogram();
//				demoHistogram.setSet(refTEST_Set);
				
				demo = demoHistogram;
				
				break;
			}
			
			case SCATTERPLOT2D: {
				
				JoglScatterPlot2D demoScatter = new JoglScatterPlot2D();
				
				demo = demoScatter;
				
				break;
			}
			
			case SCATTERPLOT3D: {
				
				JoglScatterPlot3D demoScatter = new JoglScatterPlot3D();
				
				demo = demoScatter;
				
				break;
			}
			
			
			case LOADIMAGE: {
				demo = new TestTexture();
				break;
			}

			default:
				assert false : "non supported type [" + which.toString() + "]";
			
		} // end  switch
			
		return demo;
	}
	
	
	private Demo createContent_JoglExternalFrame( JFrame frame,
			final FrameBaseType which  ) {
		
		Demo demo = null;
		
		switch (which) {

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
				
				final JFrame finalJFrame = frame;
				
				((VertexProgWarp) demo)
						.setTitleSetter(new VertexProgWarp.TitleSetter() {
							public void setTitle(final String title) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										finalJFrame.setTitle(title);
									}
								});
							}
						});
				break;
			} 
			
			case HEATMAP: {
				
				HeatMapRefract demoHeatMap = new HeatMapRefract();
//				demoHeatMap.setSet(refTEST_Set);

				demo = demoHeatMap;
					
				break;
			}
			
			case HISTOGRAM: {
				
//				demo = new HeatMapDemoRefract();
				
				JoglHistogram demoHistogram = new JoglHistogram();
//				demoHistogram.setSet(refTEST_Set);
				
				demo = demoHistogram;
				
				break;
			}
			
			case SCATTERPLOT2D: {
				
				JoglScatterPlot2D demoScatter = new JoglScatterPlot2D();
				
				demo = demoScatter;
				
				break;
			}
			
			case SCATTERPLOT3D: {
				
				JoglScatterPlot3D demoScatter = new JoglScatterPlot3D();
				
				demo = demoScatter;
				
				break;
			}
			
			
			case LOADIMAGE: {
				demo = new TestTexture();
				break;
			}

			default:
				assert false : "non supported type [" + which.toString() + "]";
			
		} // end  switch
			
		return demo;
	}
	
	
	private Object addWindow(Container addFrame, 
			Demo demo, 
			FrameBaseType which ) {
		
		if ( which.isGLCanvas() ) {
			return addWindow_GLcanvas(addFrame,demo,null,which);
		}
		return addWindow_AWTcanvas(addFrame,which);
	}
		
	private Object addWindow_AWTcanvas(
			Container addFrame, 
			final FrameBaseType which ) {
		
		assert ! which.isGLCanvas() : "calling addWindow_AWTcanvas() with a GLcanvas request";
		
		/**
		 * Create variabel for internal and external Frame and assign on null while 
		 * using the other variabel.
		 */
		final JInternalFrame inner;
		final JFrame outer;
		
		Object addedObject = null; 
		
		try {
			if ( addFrame.getClass().equals( JFrame.class )) {
				bIsInternalContainer = true;
				inner = null;
				outer = (JFrame) addFrame;
			}
			else if ( addFrame.getClass().equals( JInternalFrame.class )) {
				bIsInternalContainer = false;
				outer = null;
				inner = (JInternalFrame) addFrame;
			}
			else {
				inner = null;
				outer = null;
				
				System.err.println("Unsupported ComponentType=[" +
						addFrame.getClass().toString() + "]");
				
				assert false : "Unsupported ComponentType=[" +
					addFrame.getClass().toString() + "]";
			}
		} catch( NullPointerException npe ) {
			String errorMsg = "Can not cast Component [" +
				addFrame.getClass().toString() + "] to ";
			
			if ( bIsInternalContainer ) {
				errorMsg += "JInternalFrame";
			} else {
				errorMsg += "JFrame";
			}
			System.err.println(errorMsg);
			
			assert false : "Exception: " +errorMsg;
			
			return null;
		}
		
		
		/**
		 * Handle creation internal and external frames different...		
		 */
		if ( bIsInternalContainer ) {
	
			inner.setResizable(true);
			inner.setClosable(true);
			inner.setVisible(true);			
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
				case JBROWSER_SELECTION: {
					//SelectionBrowser ref2FrameSel = new SelectionBrowser(inner);
					SelectionSliderBrowser ref2FrameSel = new SelectionSliderBrowser(inner);	
					addedObject = ref2FrameSel;
					break;
				}
				
				case JBROWSER_SET: {
					SetBrowser ref2FrameSet = new SetBrowser(inner);
					addedObject = ref2FrameSet;
					break;
				}
				
				case JBROWSER_STORAGE: {					
					StorageBrowser ref2FrameStore = new StorageBrowser(inner);
					addedObject = ref2FrameStore;
					break;
				}
				
				
			
				default:
					assert false: "Unsupported type [" + which.toString() + "]";
				
			} // end: switch
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {
			
			outer.setResizable(true);
			outer.setVisible(true);
			outer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
			case JBROWSER_SELECTION: {
				//SelectionBrowser ref2Frame = new SelectionBrowser(outer);
				SelectionSliderBrowser ref2Frame = new SelectionSliderBrowser(outer);
				addedObject = ref2Frame;
				break;
			}
		
			default:
				assert false: "Unsupported type [" + which.toString() + "]";
			
		} // end: switch
			
		} // end: if ( bIsInternalFrame ) {...} else {...}
		/**
		 * END: Handle creation internal and external frames different...		
		 */
		
		
//		switch (which) {
//			case JBROWSER_SELECTION: {
//				break;
//			}
//		
//			default:
//				assert false: "Unsupported type [" + which.toString() + "]";
//			
//		} // end: switch
		
		
		/**
		 * Handle cleanup of internal and external frames different...		
		 */
		if ( bIsInternalContainer ) {
	
			inner.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosed(InternalFrameEvent e) {
					System.err.println("Close Internal AWT-Frame...");
				}
			});
			
			desktop.add(inner);
			
			this.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
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
			
			this.registerJFrame( outer, which.getFrameMenuTitle() );
		} // end: if ( bIsInternalFrame ) {...} else {...}	
		
		/**
		 * END: Handle cleanup of internal and external frames different...		
		 */
		
		return addedObject;
	}
	
	private Container addWindow_GLcanvas( Container addFrame, 
			Demo demo, 
			GLJPanel use_canvas,
			final FrameBaseType which ) {
		
		assert which.isGLCanvas() : "calling addWindow_GLcanvas() with a AWT-canvas request";
		
		/**
		 * Create variabel for internal and external Frame and assign on null while 
		 * using the other variabel.
		 */
		final JInternalFrame inner;
		final JFrame outer;
		
		try {
			if ( addFrame.getClass().equals( JFrame.class )) {
				bIsInternalContainer = false;
				inner = null;
				outer = (JFrame) addFrame;
			}
			else if ( addFrame.getClass().equals( JInternalFrame.class )) {
				bIsInternalContainer = true;
				outer = null;
				inner = (JInternalFrame) addFrame;
			}
			else {
				inner = null;
				outer = null;
				
				System.err.println("Unsupported ComponentType=[" +
						addFrame.getClass().toString() + "]");
				
				assert false : "Unsupported ComponentType=[" +
					addFrame.getClass().toString() + "]";
			}
		} catch( NullPointerException npe ) {
			String errorMsg = "Can not cast Component [" +
				addFrame.getClass().toString() + "] to ";
			
			if ( bIsInternalContainer ) {
				errorMsg += "JInternalFrame";
			} else {
				errorMsg += "JFrame";
			}
			System.err.println(errorMsg);
			
			assert false : "Exception: " +errorMsg;
			
			return null;
		}
		
		
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
		
		

		final GLJPanel gl_canvas;
		
		if ( use_canvas== null ) {
			GLCapabilities caps = new GLCapabilities();
			gl_canvas = new GLJPanel(caps);
		} else {
			gl_canvas = use_canvas;
		}					

		
		/**
		 * use only one threaded GLListener for (JInternalFrame) inner and (JFrame) outer
		 */
		//final DemoListener demoListener;
		
		/**
		 * Handle creation internal and external frames different...		
		 */
		if ( bIsInternalContainer ) {
	
			
			if  (refDemoListener == null) {
				refDemoListener = new DemoListener() {
			
					public void shutdownDemo() {
						removeJPanel(gl_canvas);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								inner.doDefaultCloseAction();
							}
						});
					}
		
					public void repaint() {
						gl_canvas.repaint();
					}
				};
			}
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {

			
			if ( refDemoListener == null ) {
				refDemoListener = new DemoListener() {
					public void shutdownDemo() {
						removeJPanel(gl_canvas);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								outer.setVisible( false );
								outer.dispose(); //.doDefaultCloseAction();
							}
						});
					}
					public void repaint() {
						gl_canvas.repaint();
					}
				};
			}
		} // end: if ( bIsInternalFrame ) {...} else {...}
		/**
		 * END: Handle creation internal and external frames different...		
		 */
		
		
//		switch (which) {
//		
//		
//		case HEATMAP: {
//			
//			HeatMapRefract demoHeatMap = new HeatMapRefract();
//			demo = demoHeatMap;
//				
//			break;
//		}
//		
//		default:
//
//		} // end  switch
		
		
		
		demo.setDemoListener(refDemoListener);
		gl_canvas.addGLEventListener(demo);
		
		gl_canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				gl_canvas.requestFocus();
			}
		});

		addJPanel(gl_canvas);

		final Demo fDemo = demo;
		
		/**
		 * Handle cleanup of internal and external frames different...		
		 */
		if ( bIsInternalContainer ) {
	
			inner.addInternalFrameListener(
					
					new InternalFrameAdapter() {
						public void internalFrameClosed(InternalFrameEvent e) {
							if (fDemo != null) {
								fDemo.shutdownDemo();
							}//end: if (fDemo != null) {
						}//end: public void internalFrameClosed(InternalFrameEvent e) {
					} //end: new InternalFrameAdapter() {
			);
			
			inner.getContentPane().setLayout(new BorderLayout());
			inner.getContentPane().add(gl_canvas, BorderLayout.CENTER);

			inner.setSize(512, 512);

			desktop.add(inner);
			
			this.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
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
			
			
			outer.getContentPane().add(gl_canvas, BorderLayout.CENTER);
			

			outer.setSize(512, 512);
			outer.setVisible( true );
			
			this.registerJFrame( outer, which.getFrameMenuTitle() );
			
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
			final boolean bAddDefaultMenuToExternalFrame ) {
		
		if ( refJMenu == null ) {
			refJMenu = new JMenu("NONAME Actions");
		}
		
		JMenuItem item = new JMenuItem( which.getFrameMenuTitle() );
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				createWindow( which, bAsInternalFrame );				
			}
		});
		refJMenu.add(item);
		
		return refJMenu;
	}
	
	public void run() {
		
		this.setTitle( "Cerberus v0.1" );
		
//		if ((args.length > 0) && args[0].equals("-xt")) {
//			desktop = new XTDesktopPane();
//			// FIXME: this is a hack to get the repaint behavior to work correctly
//			((XTDesktopPane) desktop).setAlwaysRedraw(true);
//		} else {
//			desktop = new JDesktopPane();
//		}

		desktop = new JDesktopPane();

		desktop.setSize(1500, 800);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(desktop, BorderLayout.CENTER);
		getContentPane().add(jsb_statusBar, BorderLayout.SOUTH);

		JInternalFrame inner2 = new JInternalFrame("Cerverus GenView v0.1");
		JLabel label = new JLabel("Cerverus GenView v0.1");
		label.setFont(new Font("SansSerif", Font.PLAIN, 38));
		inner2.getContentPane().add(label);
		inner2.pack();
		inner2.setLocation( 400,400);		
		inner2.setResizable(true);
		inner2.setIconifiable(true);	
		desktop.add(inner2);
		inner2.setVisible(true);
		
		
		initMenus( this );
		
	

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				runExit();
			}
		});
		setSize(desktop.getSize());
		setVisible(true);
		
		animator = new FPSAnimator(60);

		/* --- test Heatmap --- */
		createWindow(FrameBaseType.HEATMAP,B_FRAME_INTERNAL);
		
		createWindow(FrameBaseType.HISTOGRAM,B_FRAME_INTERNAL);
		
		//addWindow(FrameBaseType.SCATTERPLOT2D,B_FRAME_INTERNAL);
		
		
		animator.start();
	}

	private void runExit() {
		// Note: calling System.exit() synchronously inside the draw,
		// reshape or init callbacks can lead to deadlocks on certain
		// platforms (in particular, X11) because the JAWT's locking
		// routines cause a global AWT lock to be grabbed. Instead run
		// the exit routine in another thread.
		new Thread(new Runnable() {
			public void run() {
				animator.stop();
				System.exit(0);
			}
		}).start();
	}

	public synchronized void addJPanel(GLJPanel panel) {
		animator.add(panel);
	}

	public synchronized void removeJPanel(GLJPanel panel) {
		animator.remove(panel);
	}

}
