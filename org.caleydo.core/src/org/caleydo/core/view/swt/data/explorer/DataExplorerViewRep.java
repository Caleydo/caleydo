package org.caleydo.core.view.swt.data.explorer;

import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.data.DataTableViewRep;
import org.caleydo.core.view.swt.data.explorer.model.AModel;
import org.caleydo.core.view.swt.data.explorer.model.DataCollectionModel;
import org.caleydo.core.view.swt.data.explorer.model.SelectionModel;
import org.caleydo.core.view.swt.data.explorer.model.StorageModel;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DataExplorerViewRep 
extends AViewRep 
implements IView, IMediatorReceiver {

	protected static final Object StorageModel = null;

	protected DataTableViewRep refDataTableViewRep;

	protected TreeViewer treeViewer;

	protected Text text;

	protected DataExplorerLabelProvider labelProvider;

	protected DataCollectionModel rootModel;
	
	protected DataCollectionModel rootSetModel;
	
	protected DataCollectionModel rootSelectionModel;
	
	protected DataCollectionModel rootStorageModel;
	
	protected DataCollectionModel rootPathwayModel;
	
	protected DataCollectionModel rootViewModel;

	// private ISelectionChangedListener refISelectionChangedListener;

	public DataExplorerViewRep(IGeneralManager refGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_DATA_EXPLORER);

		// The simple data table is not created via the view manager
		// because it is not needed in the global context.
		refDataTableViewRep = new DataTableViewRep(refGeneralManager, iViewId);
	}

//	/**
//	 * 
//	 * @see org.caleydo.core.view.IView#initView()
//	 */
//	public void initView() {
//		
//	
//	}

	protected void initViewSwtComposit(Composite swtContainer) {
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		refSWTContainer.setLayout(fillLayout);
	}
	
	public void drawView() {

		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(refSWTContainer);
		treeViewer.setContentProvider(new DataExplorerContentProvider());
		labelProvider = new DataExplorerLabelProvider();
		treeViewer.setLabelProvider(labelProvider);

		treeViewer.setUseHashlookup(true);

		hookListeners();

		treeViewer.setInput(getInitialInput());
		treeViewer.expandAll();

		refDataTableViewRep.setExternalGUIContainer(refSWTContainer);
		refDataTableViewRep.initTable();
	}

	/**
	 * Fill the tree with Sets, Storages and Selections. The Sets are
	 * furthermore divided in the subcomponents. We assume here that a ISet
	 * consists of Storages and Selections which have the same dimension!
	 * 
	 * @return Reference to the current SetModel
	 */
	protected DataCollectionModel getInitialInput() {

		DataCollectionModel currentSetModel;
		StorageModel currentStorageModel;
		SelectionModel currentSelectionModel;

		Collection<ISet> allSetItems;
		IStorage[] allStorageItems;
		IVirtualArray[] allSelectionItems;
		// TODO: a list would be nuch nicer
		IVirtualArray[] currentSelectionArray;
		IStorage[] currentStorageArray;

		ISet currentSet;
		IStorage currentStorage;
		IVirtualArray currentSelection;

		// List<IStorage> allStorageItemsInSet;
		// List<IVirtualArray> allSelectionItemsInSet;

		// root node in the tree (not visible)
		rootModel = new DataCollectionModel();
		
		rootSetModel = new DataCollectionModel(0, "SET");
		rootModel.add(rootSetModel);
		rootSelectionModel = new DataCollectionModel(0, "VIRTUAL_ARRAY");
		rootModel.add(rootSelectionModel);
		rootStorageModel = new DataCollectionModel(0, "STORAGE");
		rootModel.add(rootStorageModel);
		rootPathwayModel = new DataCollectionModel(0, "PATHWAY");
		rootModel.add(rootPathwayModel);	
		rootViewModel = new DataCollectionModel(0, "VIEW");
		rootModel.add(rootViewModel);

		allSetItems = generalManager.getSetManager().getAllSetItems();
		
		try
		{
			// iterate over all SETs
			Iterator<ISet> iterSets = allSetItems.iterator();

			while (iterSets.hasNext())
			{
				currentSet = iterSets.next();

				if (currentSet != null)
				{
					// insert SET with ID and label in the tree
					currentSetModel = new DataCollectionModel(currentSet.getId(),
							currentSet.getLabel());
					rootSetModel.add(currentSetModel);

					for (int dimIndex = 0; dimIndex < currentSet
							.getDimensions(); dimIndex++)
					{
						try
						{
							currentSelectionArray = currentSet
									.getVirtualArrayByDim(dimIndex);
							for (int selectionIndex = 0; selectionIndex < currentSelectionArray.length; selectionIndex++)
							{
								currentSelection = (IVirtualArray) currentSelectionArray[selectionIndex];

								if (currentSelection != null)
								{
									// insert VIRTUAL_ARRAY with ID and label in the
									// tree
									currentSelectionModel = new SelectionModel(
											currentSelection.getId(),
											currentSelection.getLabel());
									currentSetModel.add(currentSelectionModel);
								} else
								{
									System.err.println("Error in DataExplorerViewRep currentSelection==null!");
								}
							}
						} catch (Exception e)
						{
							System.err.println("Error in DataExplorerViewRep while (Selection) getSelectionByDim()..");
							throw new RuntimeException(e.toString());
						}

						try
						{
							currentStorageArray = currentSet
									.getStorageByDim(dimIndex);
							for (int storageIndex = 0; storageIndex < currentStorageArray.length; storageIndex++)
							{
								currentStorage = (IStorage) currentStorageArray[storageIndex];

								// insert STORAGE with ID and label in the tree
								if (currentStorage != null)
								{
									currentStorageModel = new StorageModel(
											currentStorage.getId(),
											currentStorage.getLabel());
									currentSetModel.add(currentStorageModel);
								} else
								{
									generalManager.logMsg(
													"Error in DataExplorerViewRep currentStorage==null!",
													LoggerType.MINOR_ERROR);
								}
							}
						} catch (Exception e)
						{
							System.err.println("Error in DataExplorerViewRep while (IStorage) getStorageByDim()..");
							throw new RuntimeException(e.toString());
						}
					} // for ...
				} // if
				else
				{
					System.err.println("Error in DataExplorerViewRep currentSet==null !");
				}
			}

			allStorageItems = generalManager.getStorageManager().getAllStorageItems();

			try
			{
				// iterate over all STORAGEs
				for (int storageIndex = 0; storageIndex < allStorageItems.length; storageIndex++)
				{
					currentStorage = allStorageItems[storageIndex];

					// insert STORAGES with ID and label in the tree
					currentStorageModel = new StorageModel(currentStorage
							.getId(), currentStorage.getLabel());
					rootStorageModel.add(currentStorageModel);

				}
			} catch (Exception e)
			{
				System.err.println("Error in DataExplorerViewRep while iterate over all STORAGEs ==> currentStorage = allStorageItems[storageIndex];..");
				throw new RuntimeException(e.toString());
			}

			allSelectionItems = generalManager.getVirtualArrayManager().getAllVirtualArrayItems();

			try
			{
				// iterate over all VIRTUAL_ARRAYs
				for (int selectionIndex = 0; selectionIndex < allSelectionItems.length; selectionIndex++)
				{
					currentSelection = allSelectionItems[selectionIndex];

					// insert VIRTUAL_ARRAYs with ID and label in the tree
					currentSelectionModel = new SelectionModel(currentSelection
							.getId(), currentSelection.getLabel());
					rootSelectionModel.add(currentSelectionModel);
				}
			} catch (Exception e)
			{
				System.err.println("Error in DataExplorerViewRep while iterate over all VIRTUAL_ARRAYs ==> currentSelection = allSelectionItems[selectionIndex];..");
				throw new RuntimeException(e.toString());
			}

		} catch (Exception e)
		{
			System.err.println("Error while acquiring data via DataExplorerViewRep.");
			throw new RuntimeException(e.toString());
		}
		
		addExistingPathway();
		
		return rootModel;
	}

	/**
	 * Adds selection change listener. In this method the selected tree entries
	 * are handled.
	 */
	protected void hookListeners() {

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {

				if (event.getSelection().isEmpty())
				{
					return;
				}
				if (event.getSelection() instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					for (Iterator <IStructuredSelection> iterator = selection.iterator();
						iterator.hasNext(); )
					{
						AModel model = (AModel) iterator.next();
						if (model instanceof StorageModel)
						{
							refDataTableViewRep.createStorageTable(model
									.getID());

						} else if (model instanceof SelectionModel)
						{
							refDataTableViewRep.createSelectionTable(model
									.getID());
						} else
						{
							refDataTableViewRep.reinitializeTable();
						}

						refDataTableViewRep.redrawTable();
					}
				}
			}
		});
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#update(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

		
		//int triggerId = ((IVirtualArray) eventTrigger).getId();
		
		generalManager.logMsg(
				"Data Explorer update called by " + eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);

		//treeViewer.setInput(getInitalInput());
		//refDataTableViewRep.updateSelection(triggerId);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, final ISet updatedSet) {

		if ( updatedSet.getSetType() != SetType.SET_SELECTION ) 
		{
			return;
		}
		
		final ISetSelection refSetSelection = (ISetSelection)updatedSet;
		
		generalManager.logMsg(
				"Data Explorer selection update called by " 
				+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
		
		refSWTContainer.getDisplay().asyncExec(new Runnable() {
			public void run() {
				
				// insert new selection VIRTUAL_ARRAY with ID and label in the tree
				IVirtualArray refSelectionVirtualArray = 
					refSetSelection.getVirtualArrayByDimAndIndex(0, 0);
				SelectionModel currentSelectionVirtualArrayModel = 
					new SelectionModel(refSelectionVirtualArray.getId(), 
							refSelectionVirtualArray.getLabel());
				rootSelectionModel.add(currentSelectionVirtualArrayModel);			
				
				// insert new selection data STORAGE with ID and label in the tree
				IStorage refSelectionDataStorage = 
					refSetSelection.getStorageByDimAndIndex(0, 0);
				StorageModel currentSelectionDataStorageModel = 
					new StorageModel(refSelectionDataStorage.getId(), 
							refSelectionDataStorage.getLabel());
				rootStorageModel.add(currentSelectionDataStorageModel);	
				
				//TODO: insert selection grouped STORAGE
				
//				// insert new selection optional STORAGE with ID and label in the tree
//				IStorage refSelectionOptionalStorage = 
//					updatedSelectionSet.getStorageByDimAndIndex(0, 1);
//				StorageModel currentSelectionOptionalStorageModel = 
//					new StorageModel(refSelectionOptionalStorage.getId(), 
//							refSelectionOptionalStorage.getLabel());
//				rootStorageModel.add(currentSelectionOptionalStorageModel);		
				
				// insert new selection SET with ID and label in the tree
				DataCollectionModel currentSelectionSetModel = 
					new DataCollectionModel(refSetSelection.getId(),
							refSetSelection.getLabel());
				currentSelectionSetModel.add(currentSelectionVirtualArrayModel);
				currentSelectionSetModel.add(currentSelectionDataStorageModel);
				//currentSelectionSetModel.add(currentSelectionOptionalStorageModel);
				rootSetModel.add(currentSelectionSetModel);
			}
		});	
	}
	
	public void addExistingPathway() {

//		PathwayModel currentPathwayModel;
//		Pathway currentPathway =
//			refGeneralManager.getSingelton().getPathwayManager().getCurrentPathway();
//
//		currentPathwayModel = new PathwayModel(
//				currentPathway.getPathwayID(), currentPathway.getTitle());
//		
//		rootModel.add(currentPathwayModel);
	}
	
	public void addExistingViews() {
		
//		ViewModel tmpViewModel; 
//		
//		Iterator<IView> viewIter = 
//			refGeneralManager.getSingelton().getViewGLCanvasManager().getViewIterator();
////		
//		while(viewIter.hasNext())
//		{
//			tmpViewModel = new ViewModel(((AViewRep)viewIter.next()).getId(), 
//					((AViewRep)viewIter.next()).getLabel());
//			
//			rootStorageModel.add(tmpViewModel);	
//		}
	}
}
