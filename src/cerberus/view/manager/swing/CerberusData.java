package cerberus.view.manager.swing;

import cerberus.view.swing.loader.FileLoader;
import cerberus.data.collection.ISelection;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.selection.SelectionThreadSingleBlock;
import cerberus.data.collection.set.SetFlatThreadSimple;
import cerberus.data.collection.set.SetMultiDim;
import cerberus.data.collection.storage.FlatThreadStorageSimple;

public class CerberusData {

	public SelectionThreadSingleBlock refTEST_Selection;
	
	public SetFlatThreadSimple refTEST_Set;
	
	public SetMultiDim refTEST_Set2D;
	
	public SetMultiDim refTEST_Set3D;
	
	public FlatThreadStorageSimple refTEST_Storage;
	
	public FlatThreadStorageSimple refTEST_Storage2;
	
	public FlatThreadStorageSimple refTEST_Storage3;
	
	public CerberusData() {
		
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
		
		SetFlatThreadSimple helpSet2 = new SetFlatThreadSimple(0,null,null);
		SelectionThreadSingleBlock helpSelect2 = new SelectionThreadSingleBlock(0,null,null);
		
		helpSet2.setStorageByDimAndIndex(refTEST_Storage2,0,0);
		helpSet2.setSelectionByDimAndIndex(helpSelect2,0,0);
		
		FileLoader loader_storage2 = new FileLoader();
		loader_storage2.setSet(helpSet2);
		loader_storage2.setText(" dim=2");
		loader_storage2.load();
		
		SetFlatThreadSimple helpSet3 = new SetFlatThreadSimple(0,null,null);
		SelectionThreadSingleBlock helpSelect3 = new SelectionThreadSingleBlock(0,null,null);
		
		helpSet3.setStorageByDimAndIndex(refTEST_Storage3,0,0);
		helpSet3.setSelectionByDimAndIndex(helpSelect3,0,0);
		
		FileLoader loader_storage3 = new FileLoader();		
		loader_storage3.setSet(helpSet3);
		loader_storage3.setText(" dim=3");
		loader_storage3.load();
		
	}

}
