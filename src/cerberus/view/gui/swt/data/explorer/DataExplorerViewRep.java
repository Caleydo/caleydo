package cerberus.view.gui.swt.data.explorer;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cbg.article.model.Model;
import cbg.article.treeviewer.ui.MovingBoxContentProvider;
import cbg.article.treeviewer.ui.MovingBoxLabelProvider;
import cerberus.manager.GeneralManager;
import cerberus.manager.SetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.ViewInter;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
import cerberus.view.gui.swt.data.explorer.model.SelectionModel;
import cerberus.view.gui.swt.data.explorer.model.SetModel;
import cerberus.view.gui.swt.data.explorer.model.StorageModel;
import cerberus.view.gui.swt.data.explorer.DataExplorerContentProvider;
import cerberus.view.gui.swt.data.explorer.DataExplorerLabelProvider;

public class DataExplorerViewRep implements ViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected Composite refSWTContainer;
	
	protected TreeViewer treeViewer;
	protected Text text;
	protected DataExplorerLabelProvider labelProvider;
	
	protected SetModel rootSet;
	
	public DataExplorerViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
				
		retrieveNewGUIContainer();
		initView();
		drawView();
	}
	
	public void initView()
	{
		/* Create a grid layout object so the text and treeviewer
		 * are layed out the way I want. */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		refSWTContainer.setLayout(layout);
		
		/* Create a "label" to display information in. I'm
		 * using a text field instead of a lable so you can
		 * copy-paste out of it. */
		text = new Text(refSWTContainer, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(refSWTContainer);
		treeViewer.setContentProvider(new DataExplorerContentProvider());
		labelProvider = new DataExplorerLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
		// layout the tree viewer below the text field
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
	
		hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();	
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
		rootSet = new SetModel();

		rootSet.add(new StorageModel(1, "Storage 1"));
		rootSet.add(new SelectionModel(2, "Selection 1"));

		return rootSet;
     }

	protected void hookListeners() 
	{
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() 
		{
			public void selectionChanged(SelectionChangedEvent event) 
			{
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
					text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) 
				{
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer();
					for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = (Model) iterator.next();
						String value = labelProvider.getText(domain);
						toShow.append(value);
						toShow.append(", ");
					}
					
					// remove the trailing comma space pair
					if(toShow.length() > 0) 
					{
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(toShow.toString());
				}
			}
		});
	}

}
