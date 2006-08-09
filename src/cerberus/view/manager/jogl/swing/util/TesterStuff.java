package cerberus.view.manager.jogl.swing.util;

import cerberus.manager.CommandManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.IMenuManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.FrameBaseType;
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


public class TesterStuff {

	public SelectionThreadSingleBlock refTEST_Selection;
	
	public SetFlatThreadSimple refTEST_Set;
	
	public SetMultiDim refTEST_Set2D;
	
	public SetMultiDim refTEST_Set3D;
	
	public FlatThreadStorageSimple refTEST_Storage;
	
	public FlatThreadStorageSimple refTEST_Storage2;
	
	public FlatThreadStorageSimple refTEST_Storage3;
	
	public TesterStuff() {

		
		refTEST_Selection = 
			new SelectionThreadSingleBlock(0,null,null);
						
		refTEST_Storage = new FlatThreadStorageSimple(0,null,null);
		refTEST_Storage2 = new FlatThreadStorageSimple(0,null,null);
		refTEST_Storage3 = new FlatThreadStorageSimple(0,null,null);
		
		refTEST_Set = new SetFlatThreadSimple(0,null,null);
		refTEST_Set2D = new SetMultiDim(0,null,null,2);
		refTEST_Set3D = new SetMultiDim(0,null,null,3);
		
		ISelection[] helpSelect = new ISelection[1];
		helpSelect[0] = refTEST_Selection;
		
		IStorage [] helpStore = new IStorage [1];
		helpStore[0] = refTEST_Storage;
		
		refTEST_Set.setSelectionByDim(helpSelect,0);
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
		
	}
}
