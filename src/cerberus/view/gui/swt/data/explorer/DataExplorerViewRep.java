package cerberus.view.gui.swt.data.explorer;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cerberus.data.collection.Selection;
import cerberus.data.collection.Set;
import cerberus.data.collection.Storage;
import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.StorageManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.view.gui.ViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
import cerberus.view.gui.swt.data.explorer.model.Model;
import cerberus.view.gui.swt.data.explorer.model.SelectionModel;
import cerberus.view.gui.swt.data.explorer.model.SetModel;
import cerberus.view.gui.swt.data.explorer.model.StorageModel;
import cerberus.view.gui.swt.data.explorer.DataExplorerContentProvider;
import cerberus.view.gui.swt.data.explorer.DataExplorerLabelProvider;
import cerberus.view.gui.swt.data.set.SetTableViewRep;
import cerberus.view.gui.swt.data.DataTableViewRep;

public class DataExplorerViewRep implements ViewInter
{
	protected static final Object StorageModel = null;
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected Composite refSWTContainer;
	protected SetTableViewRep refSetTableViewRep;
	protected DataTableViewRep refDataTableViewRep;

	protected TreeViewer treeViewer;
	protected Text text;
	protected DataExplorerLabelProvider labelProvider;
	
	protected SetModel rootSet;
	
	public DataExplorerViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
				
		ViewManagerSimple viewManager = 
			(ViewManagerSimple) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW);
		//refSetTableViewRep = viewManager.createView(ManagerObjectType.VIEW_SWT_SET_TABLE);
		refDataTableViewRep = (DataTableViewRep)viewManager.createView(ManagerObjectType.VIEW_SWT_DATA_TABLE);
		
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void initView()
	{	
		RowLayout rowLayout = new RowLayout(); 
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = SWT.HORIZONTAL;
		refSWTContainer.setLayout(rowLayout);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(refSWTContainer);
		treeViewer.setContentProvider(new DataExplorerContentProvider());
		labelProvider = new DataExplorerLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
			
		hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();	

	    refDataTableViewRep.setExternalGUIContainer(refSWTContainer);
		refDataTableViewRep.initTable();
		
	}

	public void drawView()
	{
		// TODO Auto-generated method stub
		
	}

	public void retrieveNewGUIContainer()
	{
		SWTNativeWidget refSWTNativeWidget = 
			(SWTNativeWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_NATIVE_WIDGET);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();	
	}

	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Fill the tree with Sets, Storages and Selections.
	 * The Sets are furthermore divided in the subcomponents.
	 * We assume here that a Set consists of Storages and Selections 
	 * which have the same dimension!
	 * 
	 * @see cerberus.manager.gui.SWTGUIManagerSimple#createApplicationWindow()
	 * 
	 * @param setGeneralManager reference to GeneralManager
	 */
    protected SetModel getInitalInput() 
    {
    	SetModel currentSetModel;
    	StorageModel currentStorageModel;
    	SelectionModel currentSelectionModel;
    	
		Set[] allSetItems;
		Storage[] allStorageItems;
		Selection[] allSelectionItems;
		//TODO: a list would be nuch nicer - ask michael
		Selection[] currentSelectionArray;
		Storage[] currentStorageArray;
		
		Set currentSet;
		Storage currentStorage;
		Selection currentSelection;
		
//		List<Storage> allStorageItemsInSet;
//		List<Selection> allSelectionItemsInSet;
		
    	//root node in the tree (not visible)
		rootSet = new SetModel();
		//TODO: make a own DataCollectionModel for the root elements
		SetModel rootSetModel = new SetModel(0, "SET");
		rootSet.add(rootSetModel);
		SetModel rootSelectionModel = new SetModel(0, "SELECTION");
		rootSet.add(rootSelectionModel);
		SetModel rootStorageModel = new SetModel(0, "STORAGE");
		rootSet.add(rootStorageModel);
		
		allSetItems = ((SetManager)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.SET)).getAllSetItems();
		
		//iterate over all SETs
		for(int setIndex = 0; setIndex < allSetItems.length; setIndex++)
		{
			currentSet = allSetItems[setIndex];
			            
			//insert SET with ID and label in the tree
			currentSetModel = new SetModel(
					currentSet.getId(), 
					currentSet.getLabel());			
			rootSetModel.add(currentSetModel);
			
		    for(int dimIndex = 0; dimIndex < allSetItems[setIndex].getDimensions(); dimIndex++)
		    {
		    	currentSelectionArray = currentSet.getSelectionByDim(dimIndex);		    	
		    	for (int selectionIndex = 0; selectionIndex < currentSelectionArray.length; selectionIndex++)
		    	{
		    		currentSelection = (Selection)currentSelectionArray[selectionIndex];
		    		
					//insert SELECTION with ID and label in the tree
					currentSelectionModel = new SelectionModel(
							currentSelection.getId(), 
							currentSelection.getLabel());			
					currentSetModel.add(currentSelectionModel);
		    	}
		    	
		    	currentStorageArray = currentSet.getStorageByDim(dimIndex);
		    	for (int storageIndex = 0; storageIndex < currentStorageArray.length; storageIndex++)
		    	{
		    		currentStorage = (Storage)currentStorageArray[storageIndex];
		    		
					//insert STORAGE with ID and label in the tree
					currentStorageModel = new StorageModel(
							currentStorage.getId(), 
							currentStorage.getLabel());			
					currentSetModel.add(currentStorageModel);
		    	}
		    }
		}
		
		
		allStorageItems = ((StorageManager)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.STORAGE)).getAllStorageItems();
		
		//iterate over all STORAGEs
		for(int storageIndex = 0; storageIndex < allStorageItems.length; storageIndex++)
		{
			currentStorage = allStorageItems[storageIndex];
            
			//insert STORAGES with ID and label in the tree
			currentStorageModel = new StorageModel(
					currentStorage.getId(), 
					currentStorage.getLabel());			
			rootStorageModel.add(currentStorageModel);

		}
		
		allSelectionItems = ((SelectionManager)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.SELECTION)).getAllSelectionItems();
		
		//iterate over all SELECTIONs
		for(int selectionIndex = 0; selectionIndex < allSelectionItems.length; selectionIndex++)
		{
			currentSelection = allSelectionItems[selectionIndex];
            
			//insert SELECTIONs with ID and label in the tree
			currentSelectionModel = new SelectionModel(
					currentSelection.getId(), 
					currentSelection.getLabel());			
			rootSelectionModel.add(currentSelectionModel);
		}

		return rootSet;
     }

	/**
	 * Adds selection change listener.
	 * In this method the selected tree entries are handled.
	 */
	protected void hookListeners() 
	{
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			public void selectionChanged(SelectionChangedEvent event) 
			{
				if(event.getSelection().isEmpty()) 
				{
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) 
				{
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					for (Iterator iterator = selection.iterator(); iterator.hasNext();) 
					{
						Model model = (Model) iterator.next();
						if(model instanceof StorageModel)
						{
							refDataTableViewRep.createStorageTable(model.getID());
							
						}
						else if(model instanceof SelectionModel)
						{
							refDataTableViewRep.createSelectionTable(model.getID());
						}
						else
						{
							refDataTableViewRep.reinitializeTable();
						}
						
						refDataTableViewRep.redrawTable();
					}
				}
			}
		});
	}
}
