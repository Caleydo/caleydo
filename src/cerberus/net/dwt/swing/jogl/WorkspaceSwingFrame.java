/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.jogl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
//import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.command.window.CmdWindowNewIFrameHistogram2D;
import cerberus.command.window.CmdWindowSetActiveFrame;
import cerberus.data.IUniqueManagedObject;
import cerberus.data.collection.ISet;
//import cerberus.manager.singelton.OneForAllManager;
//import cerberus.net.dwt.swing.DSwingHeatMap2DCanvas;
//import cerberus.net.dwt.swing.DSwingHistogramCanvas;
import cerberus.net.dwt.swing.collection.DSwingSelectionCanvas;
import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;
import cerberus.net.dwt.swing.mdi.DDesktopPane;
import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.net.dwt.swing.menu.DMenuBootStraper;

/**
 * @author Michael Kalkusch
 *
 */
public class WorkspaceSwingFrame 
extends JFrame
implements IUniqueManagedObject {

	protected final IGeneralManager manager;
	
	public DSwingSelectionCanvas selectionPanel;
	
	public DSwingStorageCanvas storagePanel;
	
	protected DMenuBootStraper refJMenuBar;
	
	protected DDesktopPane mainDesktop;
	
	private final boolean bIsPrimaryWindow;
	
	protected int iCollectionId = -1;
	
	private final ICommandManager refCommandManager;
	
	private final IMenuManager menuCreator;
	
	private WorkspaceSwingFrame refTargetFrame;
	
	private int iCollectionId_TragetFrame = -1;
	
	/**
	 * @throws HeadlessException
	 */
	public WorkspaceSwingFrame( final IGeneralManager setManager,
			final boolean setIsPrimaryWindow ) throws HeadlessException {
		super("Prometheus v0.1");
		
		manager = setManager;
		
		bIsPrimaryWindow = setIsPrimaryWindow;
		
		refCommandManager = manager.getSingelton().getCommandManager();
		
		menuCreator = 
			manager.getSingelton().getMenuManager();
		
		refJMenuBar = new DMenuBootStraper( refCommandManager );
		
		refTargetFrame = this;
		
		//initMenu();
	}

	public void initCanvas() {
		initMenu();
		initIFrames();
		initComonenets();
		
		/**
		 * Register new frame to menu of all other frames..
		 */
		Iterator<WorkspaceSwingFrame> iter = 
			this.manager.getSingelton().getViewCanvasManager().getWorkspaceIterator();
		
		final String sMenuIdPrefix = "SYSTEM_WINDOW_SET_FRAME_";
		
		while ( iter.hasNext() ) {
			WorkspaceSwingFrame refWorkspaceFrame= iter.next();
			
			String sCollectionId = 
				Integer.toString(this.iCollectionId);
			
			if ( refWorkspaceFrame == this ) {
			
				DMenuBootStraper refIterDMenuBootStraper = refWorkspaceFrame.getDMenuBootStraper();
						
//				String details_activeFrame = Integer.toString(iCollectionId) + " " +
//					Integer.toString( iCollectionId );
				
				CmdWindowSetActiveFrame cmd_iter = 
					(CmdWindowSetActiveFrame) refCommandManager.createCommand( 
						CommandType.WINDOW_SET_ACTIVE_FRAME, 
						"" );
				
				/* ISet own farme as current frame */
				cmd_iter.setCallerAndTargetFrameId( iCollectionId, iCollectionId );
				
				refIterDMenuBootStraper.addMenuItemWithCommand( 
						sMenuIdPrefix + sCollectionId,
				"F frame "+ sCollectionId,
				"set current Frame",
				'*',
				"SYSTEM_WINDOW_SET_FRAME",
				true, cmd_iter );
				
				
			} // end if ( refWorkspaceFrame == this )
			else
			{  
				/**
				 * Register other frames to this frame..
				 */
				int iIdOfOtherWorkspaceFrame = refWorkspaceFrame.getId();
				
				CmdWindowSetActiveFrame cmd_iter = 
					(CmdWindowSetActiveFrame) refCommandManager.createCommand( 
						CommandType.WINDOW_SET_ACTIVE_FRAME, 
						"" );
				
				/* ISet own farme as current frame */
				cmd_iter.setCallerAndTargetFrameId( iCollectionId, iIdOfOtherWorkspaceFrame );
				
				this.refJMenuBar.addMenuItemWithCommand(
						sMenuIdPrefix + Integer.valueOf( iIdOfOtherWorkspaceFrame ),
				"frame "+ Integer.valueOf(iIdOfOtherWorkspaceFrame),
				"set current Frame",
				'*',
				"SYSTEM_WINDOW_SET_FRAME",
				true, cmd_iter );
			} //end else ... if ( refWorkspaceFrame == this )
		}
	}
	
	private void initIFrames() {
		
		//setLayout( new FlowLayout() );
		//setLayout( new BorderLayout() );
		
		
//		DInternalFrame bufferNewIFrameC = this.manager.getSingelton().
//			getDDesktopPane().createInternalFrame( "IStorage GUI" );		
//		bufferNewIFrameC.add( storagePanel );
//		bufferNewIFrameC.pack();
		
		//this.add( selectionPanel );
		//this.add( storagePanel );

		
		
		/* --- SET --- */
		
		ISet bufferedSet = (ISet) manager.getItem( 70300 );
		ISet bufferedSet2 = (ISet) manager.getItem( 70100 );
		
		ISet bufferedSet3 = (ISet) manager.getItem( 70400 );
		
//		DSwingHistogramCanvas histogram = new DSwingHistogramCanvas( manager, bufferedSet );
//		DSwingHistogramCanvas histogram2 = new DSwingHistogramCanvas( manager, bufferedSet2 );
//		DSwingHistogramCanvas histogram3 = new DSwingHistogramCanvas( manager, bufferedSet3 );
	
		/* --- HEATMAP --- */
//		DSwingHeatMap2DCanvas heatmap = new DSwingHeatMap2DCanvas( manager );		
//		heatmap.setColorInterpolation( Color.CYAN, Color.RED, 100, 2000 );
//		heatmap.setSetRationXY( bufferedSet, 194, 194 );
//		heatmap.updateState();
//		
//		DSwingHeatMap2DCanvas heatmap2 = new DSwingHeatMap2DCanvas( manager );
//		heatmap2.setColorInterpolation( Color.CYAN, Color.RED, 100, 2000 );
//		heatmap2.setSetRationXY( bufferedSet2, 15, 20 );
//		heatmap2.setHeatmapPixelRatio( 10, 15, 11, 20 );
//		heatmap2.updateState();
		
		
		/* --- Internal Frames --- */
//		DInternalFrame bufferNewIFrameB = this.manager.getSingelton().
//			getDDesktopPane().createInternalFrame("HeatMap");		
//		bufferNewIFrameB.add( heatmap );
//		bufferNewIFrameB.setPreferredSize( new Dimension( 600, 800 ) );
//		bufferNewIFrameB.pack();
//		
//		DInternalFrame bufferNewIFrameH = this.manager.getSingelton().
//			getDDesktopPane().createInternalFrame("HeatMap 2");		
//		bufferNewIFrameH.add( heatmap2 );
//		bufferNewIFrameH.setPreferredSize( new Dimension( 600, 800 ) );
//		bufferNewIFrameH.pack();
		
//		DInternalFrame bufferNewIFrameE = this.manager.getSingelton().
//			getDDesktopPane().createInternalFrame("Histogram");		
//		bufferNewIFrameE.add( histogram );
//		bufferNewIFrameE.setPreferredSize( new Dimension( 600, 600 ) );
//		bufferNewIFrameE.pack();
//		
//		DInternalFrame bufferNewIFrameF = this.manager.getSingelton().
//			getDDesktopPane().createInternalFrame("Histogram 2");		
//		bufferNewIFrameF.add( histogram2 );
//		bufferNewIFrameF.setPreferredSize( new Dimension( 600, 600 ) );
//		bufferNewIFrameF.pack();
//		
//		DInternalFrame bufferNewIFrameG = this.manager.getSingelton().
//			getDDesktopPane().createInternalFrame("Histogram 2");		
//		bufferNewIFrameG.add( histogram3 );
//		bufferNewIFrameG.setPreferredSize( new Dimension( 600, 600 ) );
//		bufferNewIFrameG.pack();
		
		
		//manager.getSingelton().getViewCanvasManager().addAllViewCanvas( this );
			
	}
	
	private void initComonenets() {
		
		selectionPanel.updateState();
		
		DInternalFrame bufferNewIFrameA = 
			createDInternalFrame( "IVirtualArray GUI" );
		
		bufferNewIFrameA.add( selectionPanel );
		bufferNewIFrameA.pack();
			
	}
	
	private void initMenu() {
		
		selectionPanel = new DSwingSelectionCanvas( manager );
		
		mainDesktop = new DDesktopPane();
		
		String sCollectionId = Integer.toString( iCollectionId );
		
		/* register DesktopPane ... */
		this.add( mainDesktop );
		
//		WorkspaceSwingFrame frame = 
//			manager.getSingelton().getViewCanvasManager().createWorkspace(
//				ManagerObjectType.VIEW_NEW_FRAME, "" );
//		
//		final int iFrameId = frame.getId();
	

		ICommand bufferCmd = 
			this.refCommandManager.createCommand(
				CommandType.WINDOW_POPUP_INFO, "" );
				
//		menuCreator.createMenu( iCollectionId,
//				"SYSTEM_ACTIVE",
//				DMenuBootStraper.MENU_ROOT,
//				"active",
//				"no tooltip",
//				'*',
//				false,
//				bufferCmd.getId() );
		
		menuCreator.createMenu( iCollectionId,
				"SYSTEM_ACTIVE",
				DMenuBootStraper.MENU_ROOT,
				"active",
				"no tooltip",
				'*',
				false,
				bufferCmd );
		
		mainDesktop.setVisible( true );
		
		refJMenuBar = new DMenuBootStraper( refCommandManager );
				
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE",
				"File",
				"Load, save, exit application",
				'*',
				DMenuBootStraper.MENU_ROOT,
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
	
		
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE_LOAD",
				"load..",
				"Load from several files..",
				'*',
				"SYSTEM_FILE",
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE_SAVE",
				"save..",
				"Save to several file formats..",
				'*',
				"SYSTEM_FILE",
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "") );
		
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE_SAVE_XML",
				"save All  (XML)",
				"Save all settings to a XML file.",
				'A',
				"SYSTEM_FILE_SAVE",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "") );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE_SAVE_SNAPSHOT_XML",
				"save Snapshot (XML)",
				"Save a snap shot of the current application sate to a XML file.",
				'S',
				"SYSTEM_FILE_SAVE",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "") );
		
		refJMenuBar.addMenuItemWithCommand(DMenuBootStraper.MENU_SEPERATOR,
				"-",
				"-",
				'*',
				"SYSTEM_FILE",
				true, null );
		
		if ( bIsPrimaryWindow ) {
			refJMenuBar.addMenuItemWithCommand("SYSTEM_EXIT",
					"eXit",
					"exit from Application",
					'X',
					"SYSTEM_FILE",
					true, refCommandManager.createCommand( CommandType.SYSTEM_EXIT, "" ) );
		}
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE_LOAD_JPG",
				"load Jpg..",
				"load jpg-file..",
				'J',
				"SYSTEM_FILE_LOAD",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		refJMenuBar.addMenuItemWithCommand(DMenuBootStraper.MENU_SEPERATOR,
				"-",
				"-",
				'*',
				
				"SYSTEM_FILE_LOAD",
				true, null );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_FILE_LOAD_GRP",
				"load Gpr..",
				"load gpr-file..",
				'G',
				"SYSTEM_FILE_LOAD",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );

		
		/*
		 * EDIT...
		 */
		refJMenuBar.addMenuItemWithCommand("SYSTEM_EDIT",
				"Edit",
				"Edit parameters",
				'*',
				DMenuBootStraper.MENU_ROOT,
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
//		refJMenuBar.addMenuItemWithCommand("SYSTEM_EDIT_SET",
//				"ISet..",
//				"Edit sets",
//				'*',
//				"SYSTEM_EDIT",
//				true, refCommandManager.createCommand( CommandType.WINDOW_IFRAME_OPEN_SET ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_EDIT_SELECTION",
				"IVirtualArray..",
				"Edit selection",
				'*',
				"SYSTEM_EDIT",
				true, refCommandManager.createCommand( 
						CommandType.WINDOW_IFRAME_OPEN_SELECTION, 
						sCollectionId ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_EDIT_STORAGE",
				"IStorage..",
				"Edit storage",
				'*',
				"SYSTEM_EDIT",
				true, refCommandManager.createCommand( 
						CommandType.WINDOW_IFRAME_OPEN_STORAGE, 
						sCollectionId ) );
		
		
		/*
		 * VIEW ...
		 */
		refJMenuBar.addMenuItemWithCommand("SYSTEM_VIEW",
				"View",
				"Handle views",
				'*',
				DMenuBootStraper.MENU_ROOT,
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_VIEW_ADD_VIEW",
				"add View",
				"adds a bew view",
				'*',
				"SYSTEM_VIEW",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		refJMenuBar.addMenuItemWithCommand(DMenuBootStraper.MENU_SEPERATOR,
				"-",
				"-",
				'*',
				"SYSTEM_VIEW",
				true, null );
		
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_VIEW_ADD_HISTOGRAM",
				"add Histogram",
				"adds a Histogram to the current view",
				'*',
				"SYSTEM_VIEW",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_VIEW_ADD_HEATMAP",
				"add Heatmap",
				"adds a Heatmap to the current view",
				'*',
				"SYSTEM_VIEW",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_VIEW_ADD_SCATTERPLOT",
				"add Scatterplot",
				"adds a Scatterplot to the current view",
				'*',
				"SYSTEM_VIEW",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );

		
		/*
		 * WINDOWS...
		 */
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW",
				"Window",
				"Handel windows",
				'*',
				DMenuBootStraper.MENU_ROOT,
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );

		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_NEW_FRAME",
				"new Frame",
				"create a new Frame",
				'*',
				"SYSTEM_WINDOW",
				true, refCommandManager.createCommand( CommandType.SYSTEM_NEW_FRAME, "" ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_SET_FRAME",
				"set Frame..",
				"set current Frame",
				'*',
				"SYSTEM_WINDOW",
				false, null );
			
		refJMenuBar.addMenuItemWithCommand( DMenuBootStraper.MENU_SEPERATOR,
				"-",
				"-",
				'*',
				"SYSTEM_WINDOW",
				true, null );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_HEATMAP",
				"heatMap",
				"show heatMap",
				'M',
				"SYSTEM_WINDOW",
				true, refCommandManager.createCommand( 
						CommandType.WINDOW_IFRAME_OPEN_HEATMAP2D, 
						sCollectionId ) );
		
		
		CmdWindowNewIFrameHistogram2D newCmdWindowNewIFrameHistogram2D =
			(CmdWindowNewIFrameHistogram2D) 
			refCommandManager.createCommand( 
					CommandType.WINDOW_IFRAME_OPEN_HISTOGRAM2D, 
					sCollectionId );
		
		//TODO: removed next line! Does code still work?
		//newCmdWindowNewIFrameHistogram2D.setParent( this.mainDesktop );
		newCmdWindowNewIFrameHistogram2D.setGuiTextHeader("New Histogram");
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_HISTOGRAM",
				"Histogram",
				"show Histogram",
				'H',
				"SYSTEM_WINDOW",
				true, newCmdWindowNewIFrameHistogram2D );
		
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_SCATTERPLOT_2D",
				"ScatterPlot 2D",
				"show Scatterplot 2D",
				'S',
				"SYSTEM_WINDOW",
				true, null );
		
		
		refJMenuBar.addMenuItemWithCommand(DMenuBootStraper.MENU_SEPERATOR,
				"-",
				"-",
				'*',
				"SYSTEM_WINDOW",
				true, null );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_JOGL_HISTOGRAM",
				"Jogl Histogram 3D",
				"create a JavaOpenGL canvas 3D",
				'*',
				"SYSTEM_WINDOW",
				true, refCommandManager.createCommand( 
						CommandType.WINDOW_IFRAME_OPEN_JOGL_HISTOGRAM, 
						sCollectionId ) );
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_JOGL_HEATMAP",
				"Jogl Heatmap 3D",
				"create a JavaOpenGL canvas 3D",
				'*',
				"SYSTEM_WINDOW",
				true, refCommandManager.createCommand( 
						CommandType.WINDOW_IFRAME_OPEN_JOGL_HEATMAP, 
						sCollectionId ) );
	
		refJMenuBar.addMenuItemWithCommand("SYSTEM_WINDOW_JOGL_SCATTERPLOT",
				"Jogl ScatterPlot",
				"show JavaOpneGL canvas Scatterplot 3D",
				'3',
				"SYSTEM_WINDOW",
				true, refCommandManager.createCommand(
						CommandType.WINDOW_IFRAME_OPEN_JOGL_SCATTERPLOT, 
						sCollectionId ) );
	
			
		/*
		 * ABOUT...
		 */
		refJMenuBar.addMenuItemWithCommand("SYSTEM_ABOUT",
				"About",
				"Show credits",
				'*',
				DMenuBootStraper.MENU_ROOT,
				false, refCommandManager.createCommand( CommandType.SYSTEM_NOP, "" ) );
		
		
		refJMenuBar.addMenuItemWithCommand("SYSTEM_CREDITS",
				"Credits",
				"Show credits..",
				'*',
				"SYSTEM_ABOUT",
				true, refCommandManager.createCommand( CommandType.WINDOW_POPUP_CREDITS, sCollectionId ) );
		
		
		refJMenuBar.getMenuBar().setLayout( new FlowLayout( FlowLayout.LEFT ));
		
		this.setJMenuBar( refJMenuBar.getMenuBar() );
		
		
		//storage = new FlatStorageSimple(8080,manager);
		
//		storage = (FlatStorageSimple) manager.createNewItem( 
//				ManagerObjectType.STORAGE , 
//				ManagerObjectType.STORAGE_FLAT.name() );
		
		
	
		
		
	}
	
	public DDesktopPane flipDesktopPane( DDesktopPane setDDesktopPane ) {		
		DDesktopPane buffer = mainDesktop;
		
		mainDesktop = setDDesktopPane;
		
		return buffer;	
	}
	
	public DMenuBootStraper flipDMenuBootStraper( DMenuBootStraper setDMenuBootStraper ) {		
		DMenuBootStraper buffer = refJMenuBar;
		
		refJMenuBar = setDMenuBootStraper;
		
		return buffer;	
	}
	
	public JMenuBar getJMenuBar() {
		return refJMenuBar.getMenuBar();
	}
	
	public DMenuBootStraper getDMenuBootStraper() {
		return refJMenuBar;
	}
	
	public void setTargetFrame( WorkspaceSwingFrame setTargetFrame) {
		
		refTargetFrame = setTargetFrame;
		
		iCollectionId_TragetFrame = setTargetFrame.getId();
	}
	
	/**
	 * Get the reference to the main desktop .
	 * 
	 * @see cerberus.net.dwt.swing.mdi.DDesktopPane
	 * 
	 * @return reference to desktop pane
	 */
	public DDesktopPane getDesktopPane() {		
		return mainDesktop;
	}
	
	/**
	 * Creats a new DInternalFrame using this main desktop.
	 * 
	 * @see cerberus.net.dwt.swing.mdi.DDesktopPane
	 * 
	 * @return reference to desktop pane
	 */
	public DInternalFrame createDInternalFrame( final String sTextHeader) {		
		return mainDesktop.createInternalFrame( sTextHeader );
	}
	
	/**
	 * Get the manager that created the item.
	 * 
	 * @return reference to ICollectionManager
	 */
	public IGeneralManager getManager() {
		return manager;
	}
	
	/**
	 * Resets the selectionId.
	 * @param iSetCollectionId new unique collection Id
	 */
	public void setId( int iSetCollectionId ) {
		
		if ( iCollectionId_TragetFrame == iCollectionId) {
			iCollectionId_TragetFrame = iSetCollectionId;
		}
		iCollectionId = iSetCollectionId;
		
		this.setTitle( this.getTitle() + " " + Integer.valueOf( iCollectionId ));
	}
	
	/**
	 * Get a unique Id
	 * 
	 * @return unique Id
	 */
	public int getId() {
		return iCollectionId;
	}
	
	/**
	 * Get Id of the target frame defined in this frame.
	 * Used to create new internal frames either inside this frame or in other frames.
	 * Default is this onw unique Id (see also getId() )
	 * 
	 * @return unique Id of the target frame
	 */
	public int getTargetFrameId() {
		return iCollectionId_TragetFrame;	
	}
	
	/**
	 * Get the type of this object.
	 * 
	 * @return type of this object
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.VIEW_NEW_FRAME;
	}

}
