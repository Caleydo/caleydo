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
import java.util.*;
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


import cerberus.manager.WindowToolkitType;
import cerberus.view.FrameBaseType;
import cerberus.view.manager.swing.CerberusData;
import cerberus.view.manager.swing.CerberusJStatusBar;
import cerberus.view.manager.swing.FrameCollector;
import cerberus.view.manager.swing.listener.IWindowAdapterTarget;

import cerberus.view.swing.graph.CerberusGraphViewer;

import cerberus.view.manager.swing.listener.InternalFrameAdapterCerberus;
import cerberus.view.manager.swing.listener.WindowAdapterInternalFrame;
import cerberus.view.manager.swing.listener.WindowAdapterExternalFrame;

import cerberus.view.swing.texture.TestTexture;
//import cerberus.view.swing.heatmap.HeatMapWarp;
import cerberus.view.swing.graph.CerberusGraphViewer;
import cerberus.view.swing.heatmap.HeatMapRefract;
import cerberus.view.swing.parallelcoord.JoglParallelCoordinates2D;
import cerberus.view.swing.histogram.JoglHistogram;
import cerberus.view.swing.scatterplot.JoglScatterPlot2D;
import cerberus.view.swing.scatterplot.JoglScatterPlot3D;
//import cerberus.view.swing.status.SelectionBrowser;
import cerberus.view.swing.status.SelectionSliderBrowser;
import cerberus.view.swing.status.SetBrowser;
import cerberus.view.swing.status.StorageBrowser;
import cerberus.view.swing.loader.FileLoader;

import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.IVirtualArray;
//import cerberus.data.collection.Set;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;

/**
 Wavelength-dependent refraction demo<br>
 It's a chromatic aberration!<br>
 sgreen@nvidia.com 4/2001<br><p>

 Currently 3 passes - could do it in 1 with 4 texture units<p>

 Cubemap courtesy of Paul Debevec<p>

 Ported to Java, Swing and ARB_fragment_program by Kenneth Russell
 */

public class CerberusFrameManager implements IWindowAdapterTarget {
	
	
	

	private Animator animator;

	private JDesktopPane desktop;

//	private Vector<JInternalFrame> vec_JInternalFrame;
//	
//	private Vector<JFrame> vec_JFrame;
//	
//	private Vector<JMenu> vec_JMenu;
	
	private CerberusJStatusBar jsb_statusBar;

	protected static final boolean B_FRAME_INTERNAL = true;
	
	protected static final boolean B_FRAME_EXTERNAL = false;
	
	protected static final boolean B_DEFAULT_MENU_APPAND = true;
	
	protected static final boolean B_DEFAULT_MENU_NONE = false;
	
	private FrameCollector frameData;
	
	private CerberusData data;
	
	public CerberusGraphViewer graphViewer;
	
	public static void main(String[] args) {
		
		System.out.println("  ...Cerberus v0.1 01-2006 ...");
		
		new CerberusFrameManager().run(args);
	}

	protected CerberusFrameManager() {
		frameData = new FrameCollector();
		
		data = new CerberusData();
		
		initDatastructures();		
	}

	private void initDatastructures() {
//		vec_JInternalFrame = new Vector<JInternalFrame> ();
//		
//		vec_JFrame = new Vector<JFrame> ();
//		
//		vec_JMenu = new Vector<JMenu> ();
		
	
		
		jsb_statusBar = new CerberusJStatusBar();
		
		
	}


	
	private Container addWindow(FrameBaseType which, final boolean bIsInternalFrame) {
		if ( which.isGLCanvas() ) {
			return addWindow_GLcanvas(which,bIsInternalFrame);
		}
		return addWindow_AWTcanvas(which,bIsInternalFrame);
	}
		
	private Container addWindow_AWTcanvas(FrameBaseType which, final boolean bIsInternalFrame) {
		
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
			
			inner = new JInternalFrame(which.getFrameMenuTitle());
			inner.setResizable(true);
			inner.setClosable(true);
			inner.setVisible(true);			
			
			//TODO: register Mouse Listener to canvas...
			
			switch (which) {
				case JBROWSER_VIRTUAL_ARRAY: {
					//SelectionBrowser ref2FrameSel = new SelectionBrowser(inner);
					SelectionSliderBrowser ref2FrameSel = new SelectionSliderBrowser(inner);
					ref2FrameSel.setSelection( data.refTEST_Selection );					
					break;
				}
				
				case JBROWSER_SET: {
					SetBrowser ref2FrameSet = new SetBrowser(inner);
					ref2FrameSet.setSet( data.refTEST_Set );
					break;
				}
				
				case JBROWSER_STORAGE: {					
					StorageBrowser ref2FrameStore = new StorageBrowser(inner);
					ref2FrameStore.setStorage( data.refTEST_Storage );
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
				ref2Frame.setSelection( data.refTEST_Selection );
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
	
			inner.addInternalFrameListener( new InternalFrameAdapterCerberus(this) );
			
			desktop.add(inner);
			
			frameData.registerJInternalFrame( inner ); 
			
			//frameData.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
			return inner;
			
		} // end: if ( bIsInternalFrame ) {...}
		else 
		{ // else of: if ( bIsInternalFrame ) {...} else {

			outer.addWindowListener( new WindowAdapterInternalFrame() );

			outer.setSize(512, 512);
			outer.setVisible( true );
			
			frameData.registerJFrame( outer );
			
			//this.registerJFrame( outer, which.getFrameMenuTitle() );
			
			return outer;
		} // end: if ( bIsInternalFrame ) {...} else {...}	
		
		/**
		 * END: Handle cleanup of internal and external frames different...		
		 */
		
	}
	
	private Container addWindow_GLcanvas(FrameBaseType which, final boolean bIsInternalFrame) {
		
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
			
			inner = new JInternalFrame(str);
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
			demoHeatMap.setSet(data.refTEST_Set );

			demo = demoHeatMap;
				
			break;
		}
		
		case HISTOGRAM: {
			
//			demo = new HeatMapDemoRefract();
			
			JoglHistogram demoHistogram = new JoglHistogram();
			demoHistogram.setSet(data.refTEST_Set);
			
			demo = demoHistogram;
			
			break;
		}
		
		case SCATTERPLOT2D: {
			
			JoglScatterPlot2D demoScatter = new JoglScatterPlot2D();
			demoScatter.setSet( data.refTEST_Set2D );
			
			demo = demoScatter;
			
			break;
		}
		
		case SCATTERPLOT3D: {
			
			JoglScatterPlot3D demoScatter = new JoglScatterPlot3D();
			demoScatter.setSet( data.refTEST_Set3D );
			
			demo = demoScatter;
			
			break;
		}
		
		case PARALLELCOORDINATES2D: {
			JoglParallelCoordinates2D demoScatter = 
				new JoglParallelCoordinates2D(this);
			demoScatter.setSet( data.refTEST_Set2D );
			
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

			desktop.add(inner);
			
			frameData.registerJInternalFrame( inner );
			//this.registerJInternalFrame( inner, which.getFrameMenuTitle() );
			
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
			
			frameData.registerJFrame( outer );
			
			//this.registerJFrame( outer, which.getFrameMenuTitle() );
			
			return outer;
		} // end: if ( bIsInternalFrame ) {...} else {...}	
		
		/**
		 * END: Handle cleanup of internal and external frames different...		
		 */
	}

	
	public void run(String[] args) {
		JFrame frame = new JFrame("Cerberus v0.1");
		if ((args.length > 0) && args[0].equals("-xt")) {
			desktop = new XTDesktopPane();
			// FIXME: this is a hack to get the repaint behavior to work correctly
			((XTDesktopPane) desktop).setAlwaysRedraw(true);
		} else {
			desktop = new JDesktopPane();
		}

		graphViewer = new CerberusGraphViewer();		
		graphViewer.init( 10 );
		graphViewer.run();
		
		desktop.setSize(1500, 800);
		desktop.setLocation(0, 50);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(desktop, BorderLayout.CENTER);
		frame.getContentPane().add(jsb_statusBar, BorderLayout.SOUTH);

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
		
		
		//initMenus( frame );
		
	

		frame.addWindowListener( new WindowAdapterExternalFrame(this) );
		frame.setSize(desktop.getSize());
		frame.setVisible(true);
		
		animator = new FPSAnimator(60);
		
		/* --- test Heatmap --- */
		addWindow(FrameBaseType.HEATMAP,B_FRAME_INTERNAL);
		
//		addWindow(FrameBaseType.HISTOGRAM,B_FRAME_INTERNAL);
//		
//		addWindow(FrameBaseType.SCATTERPLOT2D,B_FRAME_INTERNAL);
		
		addWindow(FrameBaseType.PARALLELCOORDINATES2D,B_FRAME_INTERNAL);
		
		addWindow(FrameBaseType.JBROWSER_VIRTUAL_ARRAY,B_FRAME_INTERNAL);
		
		
		graphViewer.frame.setLocation( -800, 300 );
		graphViewer.frame.setTitle("Parallel Coordiante Graph Structure");
		
		animator.start();
	}

	public void windowClosingAction() {
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

//	private JInternalFrame curFrame;
//
//	private void startAutoMode() {
//		new Thread(new Runnable() {
//			public void run() {
//				
//				while (true) {
//					try {
//						SwingUtilities.invokeAndWait(new Runnable() {
//							public void run() {
//								curFrame = (JInternalFrame) addWindow(FrameBaseType.GEARS,true);
//							}
//						});
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e) {
//					}
//
//					try {
//						SwingUtilities.invokeAndWait(new Runnable() {
//							public void run() {
//								curFrame.doDefaultCloseAction();
//							}
//						});
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e) {
//					}
//				}
//			}
//		}).start();
//	}



}
