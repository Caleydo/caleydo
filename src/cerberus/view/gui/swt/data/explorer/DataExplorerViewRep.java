package cerberus.view.gui.swt.data.explorer;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISetManager;
import cerberus.manager.IStorageManager;
import cerberus.manager.ISelectionManager;
import cerberus.manager.IViewManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.scatterplot.jogl.Scatterplot2DViewRep;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
import cerberus.view.gui.swt.data.explorer.model.AModel;
import cerberus.view.gui.swt.data.explorer.model.SelectionModel;
import cerberus.view.gui.swt.data.explorer.model.SetModel;
import cerberus.view.gui.swt.data.explorer.model.StorageModel;
import cerberus.view.gui.swt.data.explorer.DataExplorerContentProvider;
import cerberus.view.gui.swt.data.explorer.DataExplorerLabelProvider;
import cerberus.view.gui.swt.data.DataTableViewRep;

public class DataExplorerViewRep extends AViewRep implements IView
{
	protected static final Object StorageModel = null;

	protected Composite refSWTContainer;

	protected DataTableViewRep refDataTableViewRep;

	protected TreeViewer treeViewer;

	protected Text text;

	protected DataExplorerLabelProvider labelProvider;

	protected SetModel rootSet;

	// private ISelectionChangedListener refISelectionChangedListener;

	public DataExplorerViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	

//		IViewManager viewManager = (IViewManager) refGeneralManager
//				.getManagerByBaseType(ManagerObjectType.VIEW);
//		refDataTableViewRep = (DataTableViewRep) viewManager
//				.createView(ManagerObjectType.VIEW_SWT_DATA_TABLE, -1, iUniqueId, sLabel);

		// The simple data table is not created via the view manager
		// because it is not needed in the global context.
		
		refDataTableViewRep = new DataTableViewRep(
				this.refGeneralManager, iViewId);
	}

	public void initView()
	{
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		refSWTContainer.setLayout(fillLayout);
	}

	public void drawView()
	{
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

	public void retrieveNewGUIContainer()
	{	
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}

	public void retrieveExistingGUIContainer()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Fill the tree with Sets, Storages and Selections. The Sets are
	 * furthermore divided in the subcomponents. We assume here that a ISet
	 * consists of Storages and Selections which have the same dimension!
	 * 
	 * @return Reference to the current SetModel
	 */
	protected SetModel getInitalInput()
	{
		SetModel currentSetModel;
		StorageModel currentStorageModel;
		SelectionModel currentSelectionModel;

		ISet[] allSetItems;
		IStorage[] allStorageItems;
		ISelection[] allSelectionItems;
		// TODO: a list would be nuch nicer - ask michael
		ISelection[] currentSelectionArray;
		IStorage[] currentStorageArray;

		ISet currentSet;
		IStorage currentStorage;
		ISelection currentSelection;

		// List<IStorage> allStorageItemsInSet;
		// List<ISelection> allSelectionItemsInSet;

		// root node in the tree (not visible)
		rootSet = new SetModel();
		// TODO: make a own DataCollectionModel for the root elements
		SetModel rootSetModel = new SetModel(0, "SET");
		rootSet.add(rootSetModel);
		SetModel rootSelectionModel = new SetModel(0, "SELECTION");
		rootSet.add(rootSelectionModel);
		SetModel rootStorageModel = new SetModel(0, "STORAGE");
		rootSet.add(rootStorageModel);

		allSetItems = ((ISetManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.SET)).getAllSetItems();

		// iterate over all SETs
		for (int setIndex = 0; setIndex < allSetItems.length; setIndex++)
		{
			currentSet = allSetItems[setIndex];

			// insert SET with ID and label in the tree
			currentSetModel = new SetModel(currentSet.getId(), currentSet
					.getLabel());
			rootSetModel.add(currentSetModel);

			for (int dimIndex = 0; dimIndex < allSetItems[setIndex]
					.getDimensions(); dimIndex++)
			{
				currentSelectionArray = currentSet.getSelectionByDim(dimIndex);
				for (int selectionIndex = 0; selectionIndex < currentSelectionArray.length; selectionIndex++)
				{
					currentSelection = (ISelection) currentSelectionArray[selectionIndex];

					// insert SELECTION with ID and label in the tree
					currentSelectionModel = new SelectionModel(currentSelection
							.getId(), currentSelection.getLabel());
					currentSetModel.add(currentSelectionModel);
				}

				currentStorageArray = currentSet.getStorageByDim(dimIndex);
				for (int storageIndex = 0; storageIndex < currentStorageArray.length; storageIndex++)
				{
					currentStorage = (IStorage) currentStorageArray[storageIndex];

					// insert STORAGE with ID and label in the tree
					currentStorageModel = new StorageModel(currentStorage
							.getId(), currentStorage.getLabel());
					currentSetModel.add(currentStorageModel);
				}
			}
		}

		allStorageItems = ((IStorageManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.STORAGE))
				.getAllStorageItems();

		// iterate over all STORAGEs
		for (int storageIndex = 0; storageIndex < allStorageItems.length; storageIndex++)
		{
			currentStorage = allStorageItems[storageIndex];

			// insert STORAGES with ID and label in the tree
			currentStorageModel = new StorageModel(currentStorage.getId(),
					currentStorage.getLabel());
			rootStorageModel.add(currentStorageModel);

		}

		allSelectionItems = ((ISelectionManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.SELECTION))
				.getAllSelectionItems();

		// iterate over all SELECTIONs
		for (int selectionIndex = 0; selectionIndex < allSelectionItems.length; selectionIndex++)
		{
			currentSelection = allSelectionItems[selectionIndex];

			// insert SELECTIONs with ID and label in the tree
			currentSelectionModel = new SelectionModel(
					currentSelection.getId(), currentSelection.getLabel());
			rootSelectionModel.add(currentSelectionModel);
		}

		return rootSet;
	}

	/**
	 * Adds selection change listener. In this method the selected tree entries
	 * are handled.
	 */
	protected void hookListeners()
	{
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
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
}
