package cerberus.view.gui.swt.data.explorer;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.pathway.Pathway;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.ISelectionManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
import cerberus.view.gui.swt.data.explorer.model.AModel;
import cerberus.view.gui.swt.data.explorer.model.PathwayModel;
import cerberus.view.gui.swt.data.explorer.model.SelectionModel;
import cerberus.view.gui.swt.data.explorer.model.DataCollectionModel;
import cerberus.view.gui.swt.data.explorer.model.StorageModel;
import cerberus.view.gui.swt.data.explorer.DataExplorerContentProvider;
import cerberus.view.gui.swt.data.explorer.DataExplorerLabelProvider;
import cerberus.view.gui.swt.data.DataTableViewRep;

public class DataExplorerViewRep 
extends AViewRep 
implements IView, IMediatorReceiver {

	protected static final Object StorageModel = null;

	protected Composite refSWTContainer;

	protected DataTableViewRep refDataTableViewRep;

	protected TreeViewer treeViewer;

	protected Text text;

	protected DataExplorerLabelProvider labelProvider;

	protected DataCollectionModel rootModel;

	// private ISelectionChangedListener refISelectionChangedListener;

	public DataExplorerViewRep(IGeneralManager refGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(refGeneralManager, iViewId, iParentContainerId, sLabel);

		// The simple data table is not created via the view manager
		// because it is not needed in the global context.
		refDataTableViewRep = new DataTableViewRep(refGeneralManager, iViewId);
	}

	public void initView() {

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

		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();

		refDataTableViewRep.setExternalGUIContainer(refSWTContainer);
		refDataTableViewRep.initTable();
	}

	public void retrieveGUIContainer() {

		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	/**
	 * Fill the tree with Sets, Storages and Selections. The Sets are
	 * furthermore divided in the subcomponents. We assume here that a ISet
	 * consists of Storages and Selections which have the same dimension!
	 * 
	 * @return Reference to the current SetModel
	 */
	protected DataCollectionModel getInitalInput() {

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
		
		DataCollectionModel rootSetModel = new DataCollectionModel(0, "SET");
		rootModel.add(rootSetModel);
		DataCollectionModel rootSelectionModel = new DataCollectionModel(0, "SELECTION");
		rootModel.add(rootSelectionModel);
		DataCollectionModel rootStorageModel = new DataCollectionModel(0, "STORAGE");
		rootModel.add(rootStorageModel);
		DataCollectionModel rootPathwayModel = new DataCollectionModel(0, "PATHWAY");
		rootModel.add(rootPathwayModel);	

		allSetItems = ((ISetManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.SET)).getAllSetItems();
		
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
									.getSelectionByDim(dimIndex);
							for (int selectionIndex = 0; selectionIndex < currentSelectionArray.length; selectionIndex++)
							{
								currentSelection = (IVirtualArray) currentSelectionArray[selectionIndex];

								if (currentSelection != null)
								{
									// insert SELECTION with ID and label in the
									// tree
									currentSelectionModel = new SelectionModel(
											currentSelection.getId(),
											currentSelection.getLabel());
									currentSetModel.add(currentSelectionModel);
								} else
								{
									System.err
											.println("Error in DataExplorerViewRep currentSelection==null!");
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
									refGeneralManager
											.getSingelton()
											.getLoggerManager()
											.logMsg(
													"Error in DataExplorerViewRep currentStorage==null!",
													LoggerType.MINOR_ERROR);
								}
							}
						} catch (Exception e)
						{
							System.err
									.println("Error in DataExplorerViewRep while (IStorage) getStorageByDim()..");
							throw new RuntimeException(e.toString());
						}
					} // for ...
				} // if
				else
				{
					System.err
							.println("Error in DataExplorerViewRep currentSet==null !");
				}
			}

			allStorageItems = ((IStorageManager) refGeneralManager
					.getManagerByBaseType(ManagerObjectType.STORAGE))
					.getAllStorageItems();

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
				System.err
						.println("Error in DataExplorerViewRep while iterate over all STORAGEs ==> currentStorage = allStorageItems[storageIndex];..");
				throw new RuntimeException(e.toString());
			}

			allSelectionItems = ((ISelectionManager) refGeneralManager
					.getManagerByBaseType(ManagerObjectType.SELECTION))
					.getAllSelectionItems();

			try
			{
				// iterate over all SELECTIONs
				for (int selectionIndex = 0; selectionIndex < allSelectionItems.length; selectionIndex++)
				{
					currentSelection = allSelectionItems[selectionIndex];

					// insert SELECTIONs with ID and label in the tree
					currentSelectionModel = new SelectionModel(currentSelection
							.getId(), currentSelection.getLabel());
					rootSelectionModel.add(currentSelectionModel);
				}
			} catch (Exception e)
			{
				System.err.println("Error in DataExplorerViewRep while iterate over all SELECTIONs ==> currentSelection = allSelectionItems[selectionIndex];..");
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
					for (Iterator iterator = selection.iterator(); iterator
							.hasNext();)
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

	public void update(Object eventTrigger) {

		
		//int triggerId = ((IVirtualArray) eventTrigger).getId();
		
		refGeneralManager.getSingelton().logMsg(
				"Data Explorer update called by " + eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);

		//refDataTableViewRep.updateSelection(triggerId);
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
}
