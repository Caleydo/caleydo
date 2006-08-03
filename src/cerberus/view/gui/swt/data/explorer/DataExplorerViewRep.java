package cerberus.view.gui.swt.data.explorer;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cerberus.data.collection.Selection;
import cerberus.data.collection.Set;
import cerberus.data.collection.Storage;
import cerberus.manager.GeneralManager;
import cerberus.manager.SWTGUIManager;
import cerberus.manager.SetManager;
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
import cerberus.view.gui.swt.data.selection.SelectionTableViewRep;
import cerberus.view.gui.swt.data.set.SetTableViewRep;
import cerberus.view.gui.swt.data.storage.StorageTableViewRep;

public class DataExplorerViewRep implements ViewInter
{
	protected static final Object StorageModel = null;
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected Composite refSWTContainer;
	protected SetTableViewRep refSetTableViewRep;
	protected StorageTableViewRep refStorageTableViewRep;
	protected SelectionTableViewRep refSelectionTableViewRep;

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
		//refSetTableViewRep = viewManager.createView(ManagerObjectType.VIEW_SET_TABLE);
		refStorageTableViewRep = (StorageTableViewRep)viewManager.createView(ManagerObjectType.VIEW_STORAGE_TABLE);
		//refSelectionTableViewRep = viewManager.createView(ManagerObjectType.VIEW_SELECTION_TABLE);
		
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void initView()
	{
//		/* Create a grid layout object so the text and treeviewer
//		 * are layed out the way I want. */
//		GridLayout layout = new GridLayout();
//		layout.numColumns = 1;
//		layout.verticalSpacing = 2;
//		layout.marginWidth = 0;
//		layout.marginHeight = 2;
//		refSWTContainer.setLayout(layout);
		
//	    GridLayout gridLayout = new GridLayout();
//	    gridLayout.numColumns = 2;
//	    gridLayout.marginHeight = gridLayout.marginWidth = 0;
//	    refSWTContainer.setLayout(gridLayout);
			
		RowLayout rowLayout = new RowLayout();
 		rowLayout.wrap = false;
 		rowLayout.pack = true;
 		rowLayout.justify = true;
 		rowLayout.type = SWT.VERTICAL;
 		rowLayout.marginLeft = 5;
 		rowLayout.marginTop = 5;
 		rowLayout.marginRight = 5;
 		rowLayout.marginBottom = 5;
 		rowLayout.spacing = 0;
 		refSWTContainer.setLayout(rowLayout);
		
		//Composite treeComposite = new Composite(refSWTContainer, SWT.NONE);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(refSWTContainer);
		treeViewer.setContentProvider(new DataExplorerContentProvider());
		labelProvider = new DataExplorerLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
//		// layout the tree viewer below the text field
//		GridData layoutData = new GridData();
//		layoutData.grabExcessHorizontalSpace = true;
//		layoutData.grabExcessVerticalSpace = true;
//		layoutData.horizontalAlignment = GridData.FILL;
//		layoutData.verticalAlignment = GridData.FILL;
//		treeViewer.getControl().setLayoutData(layoutData);
	
		hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();	
		
	    Composite tableComposite = new Composite(refSWTContainer, SWT.NONE);
	    refStorageTableViewRep.setExternalGUIContainer(tableComposite);
		refStorageTableViewRep.initTable();
		refStorageTableViewRep.createTable(-1);
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
	
    protected SetModel getInitalInput() 
    {
    	SetModel currentSetModel;
    	StorageModel currentStorageModel;
    	SelectionModel currentSelectionModel;
    	
		Set[] allSetItems;
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
			rootSet.add(currentSetModel);
			
		    for(int dimIndex = 0; dimIndex < allSetItems[setIndex].getDimensions(); dimIndex++)
		    {
		    	currentSelectionArray = currentSet.getSelectionByDim(dimIndex);
		    	//currentStorageArray = currentSet.getStorageByDim(dimIndex);
		    	
		    	for (int selectionIndex = 0; selectionIndex < currentSelectionArray.length; selectionIndex++)
		    	{
		    		currentSelection = (Selection)currentSelectionArray[selectionIndex];
		    		
					//insert SELECTION with ID and label in the tree
					currentSelectionModel = new SelectionModel(
							currentSelection.getId(), 
							currentSelection.getLabel());			
					currentSetModel.add(currentSelectionModel);
		    	}
		    	
//		    	for (int storageIndex = 0; storageIndex < currentStorageArray.length; storageIndex++)
//		    	{
//		    		currentStorage = (Storage)currentStorageArray[storageIndex];
//		    		
//					//insert STORAGE with ID and label in the tree
//					currentStorageModel = new StorageModel(
//							currentStorage.getId(), 
//							currentStorage.getLabel());			
//					currentSetModel.add(currentStorageModel);
//		    	}
		    }
		}
		
		//just for testing the tree
		SetModel subSet = new SetModel(15101, "BLABLA");
		subSet.add(new StorageModel(15301, "Storage"));
		subSet.add(new StorageModel(1, "Selection"));
		rootSet.add(subSet);	
//		rootSet.add(new SelectionModel(2, "Selection"));
//		rootSet.add(new StorageModel(15101, "Storage"));
//		rootSet.add(new SelectionModel(2, "Selection"));
//		rootSet.add(new StorageModel(15101, "Storage"));
//		rootSet.add(new SelectionModel(2, "Selection"));
//		rootSet.add(new StorageModel(15101, "Storage"));

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
					text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) 
				{
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer();
					for (Iterator iterator = selection.iterator(); iterator.hasNext();) 
					{
						Model model = (Model) iterator.next();
						if(model instanceof StorageModel)
						{
							refStorageTableViewRep.createTable(model.getID());
							refStorageTableViewRep.redrawTable();
						}
						else if(model instanceof SelectionModel)
						{
							refStorageTableViewRep.createTable(model.getID());
							refStorageTableViewRep.redrawTable();
						}
						
//						String value = labelProvider.getText(model);
//						toShow.append(value);
//						toShow.append(", ");
					}
					
//					// remove the trailing comma space pair
//					if(toShow.length() > 0) 
//					{
//						toShow.setLength(toShow.length() - 2);
//					}
//					text.setText(toShow.toString());
				}
			}
		});
	}

}
