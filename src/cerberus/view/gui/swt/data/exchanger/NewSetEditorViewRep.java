/**
 * 
 */
package cerberus.view.gui.swt.data.exchanger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

//import cerberus.command.CommandQueueSaxType;
import cerberus.command.CommandQueueSaxType;
import cerberus.command.data.CmdDataCreateSet;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.SetDataType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;

/**
 * Data Exchanger View makes it possible
 * to swap the data of views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class NewSetEditorViewRep 
extends AViewRep 
implements IView {
    
    protected String sArSetDataComboItems[];
    	
	protected ArrayList<String> arSetIDs;
	
	protected boolean bIsSetComboInitialized = false;
	
	protected Table virtualArrayTable;
	
	protected Table storageTable;
	
	protected Combo setDataCombo;
	
	protected Button createSetButton;
	
	protected Button setFinishedButton;
	
	protected int iSelectedSetId = 0;
	
	/**
	 * Stores the storages in the tables with their row.
	 * The row indicates which index they are in the SET.
	 * The information is needed for replacing the old storage 
	 * with the newly selected.
	 */
	protected HashMap<IStorage, Integer> hashStorage2StorageSetIndex;
	
	/**
	 * Stores the virtual array in the tables with their row.
	 * The row indicates which index they are in the SET.
	 * The information is needed for replacing the old virtual array 
	 * with the newly selected.
	 */
	protected HashMap<IVirtualArray, Integer> hashVirtualArray2VirtualArraySetIndex;
 	
	protected ArrayList<IStorage> iArStorageToAdd;
	
	protected ArrayList<IVirtualArray> iArVirtualArrayToAdd;
	
	public NewSetEditorViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_SET_EDITOR);	

		arSetIDs = new ArrayList<String>();
		hashStorage2StorageSetIndex = new HashMap<IStorage, Integer> ();
		hashVirtualArray2VirtualArraySetIndex = new HashMap<IVirtualArray, Integer>();
		iArStorageToAdd = new ArrayList<IStorage>();
		iArVirtualArrayToAdd = new ArrayList<IVirtualArray>();
		
	}
	
	public void initView() {

		retrieveGUIContainer();
		
		refSWTContainer.setLayout(new RowLayout(SWT.VERTICAL));

		createSetButton = new Button(refSWTContainer, SWT.LEFT);
		createSetButton.setText("Create new SET");
		createSetButton.setLayoutData(new RowData(120, 30));
		createSetButton.setEnabled(true);
		createSetButton.addSelectionListener(new SelectionListener() {				
	        public void widgetSelected(SelectionEvent e) {

	        	createSetButton.setEnabled(false);
	        	
	        	storageTable.removeAll();
	        	virtualArrayTable.removeAll();
	        	
	        	setFinishedButton.setEnabled(true);
	        	
	    	    String[] itemData = new String[3];
				itemData[0] = "0";
				itemData[1] = "ID?";
				itemData[2] = "Label?";

			    for (int i = 0; i < 4; i++) 
			    {			
					// Create fresh row for storage and virtual array
		        	TableItem tableItem = new TableItem(storageTable, SWT.NONE);;
				    tableItem.setText(itemData);
				    tableItem = new TableItem(virtualArrayTable, SWT.NONE);;
				    tableItem.setText(itemData);  
			    
//			      // Create the editor and button
//				TableEditor finishedEditor = new TableEditor(storageTable);
//				Button finishedButton = new Button(storageTable, SWT.PUSH);
//
//				// Set attributes of the button
//				finishedButton.setText("Color...");
//				finishedButton.computeSize(SWT.DEFAULT, storageTable
//						.getItemHeight());
//
//				// Set attributes of the editor
//				finishedEditor.grabHorizontal = true;
//				finishedEditor.minimumHeight = finishedButton.getSize().y;
//				finishedEditor.minimumWidth = finishedButton.getSize().x;
//
//				// Set the editor for the first column in the row
//				finishedEditor.setEditor(finishedButton, tableItem, 3);
//
//				// Create a handler for the button
//				finishedButton.addSelectionListener(new SelectionAdapter() {
//
//					public void widgetSelected(SelectionEvent event) {
//						
//					}
//				});
			    }
//			    iSelectedSetId = createNewSet();
	        }

	        public void widgetDefaultSelected(SelectionEvent e) {

	        }
	      });
		
		Label dataComboLabel = new Label(refSWTContainer, SWT.LEFT);
		dataComboLabel.setText("Change existing SET:");
		dataComboLabel.setLayoutData(new RowData(150, 30));

		setDataCombo = new Combo(refSWTContainer, SWT.READ_ONLY);

		createEmptyTables(); 
		
		createStorageTableEditor();
		createVirtualArrayTableEditor();
		
		setDataCombo.setLayoutData(new RowData(150, 30));
	    setDataCombo.setEnabled(true);

	    setDataCombo.addFocusListener(new FocusAdapter() {
	        public void focusGained(FocusEvent e) {
	        		       
	        	if (bIsSetComboInitialized == true)
	        		return;
	        	
	    		fillSetData(setDataCombo);
	        }
	    });
	   				
	    setDataCombo.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {

	    	    setDataCombo.select(setDataCombo.getSelectionIndex());	
	    	    
	    		iSelectedSetId = StringConversionTool.convertStringToInt( 
	    				arSetIDs.get(setDataCombo.getSelectionIndex()), 
	    				0);
	    		
	    		reloadDataTablesForSet(iSelectedSetId);
	    	}
	    });
	    
	      // Create SET finished button
		setFinishedButton = new Button(refSWTContainer, SWT.PUSH);
		setFinishedButton.setText("Finish SET");
		setFinishedButton.setLayoutData(new RowData(150, 30));
		setFinishedButton.setEnabled(false);
		setFinishedButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				int iCreatedSetId = createNewSet();
				
				setFinishedButton.setEnabled(false);
				createSetButton.setEnabled(true);
				setDataCombo.add(new Integer(iCreatedSetId).toString());
				arSetIDs.add(new Integer(iCreatedSetId).toString());
			}
		});
	}
	
	public void drawView() {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": drawView()", 
				LoggerType.VERBOSE );		
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	protected void createEmptyTables() {
		
	    virtualArrayTable = new Table(refSWTContainer, SWT.SINGLE | SWT.FULL_SELECTION
	        | SWT.HIDE_SELECTION);
	    virtualArrayTable.setHeaderVisible(true);
	    virtualArrayTable.setLinesVisible(true);
		virtualArrayTable.setLayoutData(new RowData(300, 110));

	    storageTable = new Table(refSWTContainer, SWT.SINGLE | SWT.FULL_SELECTION
		        | SWT.HIDE_SELECTION);
	    storageTable.setHeaderVisible(true);
	    storageTable.setLinesVisible(true);
		storageTable.setLayoutData(new RowData(300, 110));
	    
	    String[] sArColumnCaptions = {"Dimension", "   ID   ", "Label"};
	    
	    // Create columns
	    for (int i = 0; i < sArColumnCaptions.length; i++) 
	    {
	      TableColumn column = new TableColumn(virtualArrayTable, SWT.CENTER);
	      column.setText(sArColumnCaptions[i]);
	      column.pack();
	      
	      column = new TableColumn(storageTable, SWT.CENTER);
	      column.setText(sArColumnCaptions[i]);
	      column.pack();
	    }
	}
	
	protected void reloadDataTablesForSet(int iSelectedSetDataId) {
		
		//Reset tables
		virtualArrayTable.removeAll();
		storageTable.removeAll();
		hashStorage2StorageSetIndex.clear();
		hashVirtualArray2VirtualArraySetIndex.clear();
		
		ISet selectedSet = 
			refGeneralManager.getSingelton().getSetManager().getItemSet(iSelectedSetDataId);
		
		// Fill storage data
		TableItem tableItem = null;
	    String[] itemData = new String[3];
	    
		Iterator<IStorage> iterStorages;
		IStorage tmpStorage = null;
		int iStorageIndex = 0;
		int iVirtualArrayIndex = 0;
		
		int iDimensions = selectedSet.getDimensions();
		
		for (int iDimIndex = 0; iDimIndex < iDimensions; iDimIndex++)
		{
			iterStorages = selectedSet.getStorageVectorByDim(iDimIndex).iterator();
		
			// Reset
			iStorageIndex = 0;
		
			while(iterStorages.hasNext()) 
			{
				tmpStorage = iterStorages.next();
	
				// FIXME: StorageManager returns a vector that contains one empty element 
				if(tmpStorage == null)
					break;
				
				tableItem = new TableItem(storageTable, SWT.NONE);
				itemData[0] = Integer.toString(iDimIndex);
				itemData[1] = Integer.toString(tmpStorage.getId());
				itemData[2] = tmpStorage.getLabel();
			    tableItem.setText(itemData);
			    
			    hashStorage2StorageSetIndex.put(tmpStorage, iStorageIndex);
			    iStorageIndex++;
			}
		
			// Fill virtual array table	    
			Iterator<IVirtualArray> iterVirtualArrays = 
				selectedSet.getVirtualArrayVectorByDim(iDimIndex).iterator();
			IVirtualArray tmpVirtualArray = null;
			
			// Reset
			iVirtualArrayIndex = 0;
			
			while(iterVirtualArrays.hasNext()) 
			{
				tmpVirtualArray = iterVirtualArrays.next();
	
				// FIXME: VirtualArray returns a vector that contains one empty element 
				if(tmpVirtualArray == null)
					break;
				
				tableItem = new TableItem(virtualArrayTable, SWT.NONE);
				itemData[0] = Integer.toString(iDimIndex);
				itemData[1] = Integer.toString(tmpVirtualArray.getId());
				itemData[2] = tmpVirtualArray.getLabel();
				
			    hashVirtualArray2VirtualArraySetIndex.put(tmpVirtualArray, iVirtualArrayIndex);
			    iVirtualArrayIndex++;
				
				tableItem.setText(itemData);
			}
		}
	}

	protected void createStorageTableEditor() {

		// Create an editor object to use for text editing
	    final TableEditor editor = new TableEditor(storageTable);
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	    
	    // Use a mouse listener, not a selection listener, since we're
		// interested
	    // in the selected column as well as row
	    storageTable.addMouseListener(new MouseAdapter() {
	    	public void mouseDown(MouseEvent event) {
	    		
	    		// Fill storage list to choose in the drop down menu
	    		ArrayList<Integer> iArStorageList = 
	    			new ArrayList<Integer>();

	    		Iterator<IStorage> iterStorages = 
	    			refGeneralManager.getSingelton().getStorageManager().
	    			getAllStorageItemsVector().iterator();
	    		IStorage tmpStorage = null;
	    		
	    		while(iterStorages.hasNext()) 
	    		{
	    			tmpStorage = iterStorages.next();
	    			iArStorageList.add(tmpStorage.getId());
	    		}
	    		
		        // Dispose any existing editor
		        Control old = editor.getEditor();
		        if (old != null) 
		        	old.dispose();
		        
		        // Determine where the mouse was clicked
		        Point pt = new Point(event.x, event.y);

		        // Determine which row was selected
		        final TableItem item = storageTable.getItem(pt);
		        
		        if (item != null)
				{
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = storageTable.getColumnCount(); i < n; i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							// This is the selected column
							column = i;
							break;
						}
					}
			        
					// We are only interested in the ID column
			        if (column == 1) 
			        {	
			            // Create the dropdown and add data to it
			            final CCombo combo = new CCombo(storageTable, SWT.READ_ONLY);
			            Iterator<Integer> iter = iArStorageList.iterator();
			            while(iter.hasNext())
			            {
			            	combo.add(iter.next().toString());
			            }
			            
						if (!item.getText(1).equals("ID?"))
						{
							// Select the previously selected item from the cell
							combo.select(combo.indexOf(item.getText(column)));
						}
						else
						{
							combo.select(0);
						}
							
			            // Compute the width for the editor
			            // Also, compute the column width, so that the dropdown
						// fits
			            editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			            storageTable.getColumn(column).setWidth(editor.minimumWidth);
			            
			            // Set the focus on the dropdown and set into the editor
			            combo.setFocus();
			            editor.setEditor(combo, item, column);
		
			            // Add a listener to set the selected item back into the
						// cell
			            final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {

							public void widgetSelected(SelectionEvent event) {

								// Get the selected storage object
								IStorage tmpStorage = 
									refGeneralManager.getSingelton().getStorageManager().getItemStorage(new Integer(combo.getText()));
								
								if (item.getText(1).equals("ID?"))
								{
									// Condition: Create new virtual array
									iArStorageToAdd.add(tmpStorage);
								}
								else
								{
									// Condition: Edit existing virtual array
									
									// Save changed storage ID to SET
						    		int iSelectedSetDataId = StringConversionTool.convertStringToInt( 
						    				arSetIDs.get(setDataCombo.getSelectionIndex()), 
						    				0);
						    		
						        	// Save previously selected storage for updating in SET
						        	IStorage oldStorage = refGeneralManager.getSingelton().getStorageManager().getItemStorage(
						        			new Integer(item.getText(1)));
						    		
						    		if (hashStorage2StorageSetIndex.isEmpty())
						    			return;
						    		
						    		ISet tmpSet = refGeneralManager.getSingelton().getSetManager().getItemSet(iSelectedSetDataId);
						    		int iStorageIndexInSet = hashStorage2StorageSetIndex.get(oldStorage);
						    		
						    		tmpSet.getWriteToken();
									tmpSet.setStorageByDimAndIndex(tmpStorage, new Integer(item.getText(0)), iStorageIndexInSet);
						    		tmpSet.returnWriteToken();
						    		
						    		hashStorage2StorageSetIndex.remove(oldStorage);
						    		hashStorage2StorageSetIndex.put(tmpStorage, iStorageIndexInSet);
								}
								
								item.setText(col, combo.getText());
								item.setText(2, tmpStorage.getLabel());
								
								// They selected an item; end the editing
								// session
								combo.dispose();
							}
						});
					}
				}
			}
		});
	 }

	protected void createVirtualArrayTableEditor() {

		// Create an editor object to use for text editing
	    final TableEditor editor = new TableEditor(virtualArrayTable);
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	    
	    // Use a mouse listener, not a selection listener, since we're
		// interested
	    // in the selected column as well as row
	    virtualArrayTable.addMouseListener(new MouseAdapter() {
	    	public void mouseDown(MouseEvent event) {
	    		
	    		// Fill virtual array list to choose in the drop down menu
	    		ArrayList<Integer> iArVirtualArrayList = 
	    			new ArrayList<Integer>();

	    		Iterator<IVirtualArray> iterVirtualArrays = 
	    			refGeneralManager.getSingelton().getVirtualArrayManager().
	    			getAllVirtualArrayItemsVector().iterator();
	    		IVirtualArray tmpVirtualArray = null;
	    		
	    		while(iterVirtualArrays.hasNext()) 
	    		{
	    			tmpVirtualArray = iterVirtualArrays.next();
	    			iArVirtualArrayList.add(tmpVirtualArray.getId());
	    		}
	    		
		        // Dispose any existing editor
		        Control old = editor.getEditor();
		        if (old != null) 
		        	old.dispose();
		        
		        // Determine where the mouse was clicked
		        Point pt = new Point(event.x, event.y);

		        // Determine which row was selected
		        final TableItem item = virtualArrayTable.getItem(pt);
		        
		        if (item != null)
				{
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = virtualArrayTable.getColumnCount(); i < n; i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							// This is the selected column
							column = i;
							break;
						}
					}
			        
					// We are only interested in the ID column
			        if (column == 1) 
			        {	
			            // Create the dropdown and add data to it
			            final CCombo combo = new CCombo(virtualArrayTable, SWT.READ_ONLY);
			            Iterator<Integer> iter = iArVirtualArrayList.iterator();
			            while(iter.hasNext())
			            {
			            	combo.add(iter.next().toString());
			            }
			            
						if (!item.getText(1).equals("ID?"))
						{
							// Select the previously selected item from the cell
							combo.select(combo.indexOf(item.getText(column)));
						}
						else
						{
							combo.select(0);
						}
			            
			            // Compute the width for the editor
			            // Also, compute the column width, so that the dropdown
						// fits
			            editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			            virtualArrayTable.getColumn(column).setWidth(editor.minimumWidth);
			            
			            // Set the focus on the dropdown and set into the editor
			            combo.setFocus();
			            editor.setEditor(combo, item, column);
		
			            // Add a listener to set the selected item back into the
						// cell
			            final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {

							public void widgetSelected(SelectionEvent event) {

								// Get the selected virtual array object
								IVirtualArray tmpVirtualArray = 
									refGeneralManager.getSingelton().getVirtualArrayManager().getItemVirtualArray(new Integer(combo.getText()));
					    		
								if (item.getText(1).equals("ID?"))
								{
									// Condition: Create new virtual array
									iArVirtualArrayToAdd.add(tmpVirtualArray);
								}
								else
								{
									// Condition: Edit existing virtual array
									
						        	// Save previously virtual array virtual array for updating in SET
						    		IVirtualArray oldVirtualArray = refGeneralManager.getSingelton().getVirtualArrayManager().getItemVirtualArray(
						        			new Integer(item.getText(1)));
						    		
						    		if (hashVirtualArray2VirtualArraySetIndex.isEmpty())
						    			return;
						    		
						    		ISet tmpSet = refGeneralManager.getSingelton().getSetManager().getItemSet(iSelectedSetId);
						    		int iVirtualArrayIndexInSet = hashVirtualArray2VirtualArraySetIndex.get(oldVirtualArray);
						    		
						    		tmpSet.getWriteToken();
									tmpSet.setVirtualArrayByDimAndIndex(tmpVirtualArray, new Integer(item.getText(0)), iVirtualArrayIndexInSet);
						    		tmpSet.returnWriteToken();
						    		
						    		hashVirtualArray2VirtualArraySetIndex.remove(oldVirtualArray);
						    		hashVirtualArray2VirtualArraySetIndex.put(tmpVirtualArray, iVirtualArrayIndexInSet);	
								}
								
								item.setText(col, combo.getText());
								item.setText(2, tmpVirtualArray.getLabel());
								
								// They selected an item; end the editing
								// session
								combo.dispose();
							}
						});
					}
				}
			}
		});
	 }

	
	protected void fillSetData(Combo setDataCombo) {
		
		Collection<ISet> allSets = 
			refGeneralManager.getSingelton().getSetManager().getAllSetItems();
		
		Iterator<ISet> iterSets = allSets.iterator();
		int iTmpSetId = 0;

		arSetIDs.clear();
		
		while (iterSets.hasNext())
		{
			iTmpSetId = iterSets.next().getId();
			
			arSetIDs.add(Integer.toString(iTmpSetId));
		}
		
		sArSetDataComboItems = arSetIDs.toArray(new String[arSetIDs.size()]);	
		sArSetDataComboItems = arSetIDs.toArray(new String[arSetIDs.size()]);
		setDataCombo.removeAll();
		setDataCombo.setItems(sArSetDataComboItems);
		
		bIsSetComboInitialized = true;
	}
	
	protected int createNewSet() {
		
	    // Create new SET
	    int iNewSetId = refGeneralManager.getSingelton().getSetManager().
	    	createNewId(ManagerObjectType.SET_PLANAR);

		CmdDataCreateSet createdCommand = 
			(CmdDataCreateSet) refGeneralManager.getSingelton().getCommandManager()
				.createCommandByType(CommandQueueSaxType.CREATE_SET_DATA);

		String sStorageIDs = "";
		String sVirtualArrayIDs = "";
		
		Iterator<IStorage> iterStorageIDs = iArStorageToAdd.iterator();
		while(iterStorageIDs.hasNext()) 
		{
			sStorageIDs += new Integer(iterStorageIDs.next().getId()).toString() + " ";
		}

		Iterator<IVirtualArray> iterVirtualArrayIDs = iArVirtualArrayToAdd.iterator();
		while(iterVirtualArrayIDs.hasNext())
		{
			sVirtualArrayIDs += new Integer(iterVirtualArrayIDs.next().getId()).toString() + " ";
		}
		
		createdCommand.setAttributes(iNewSetId, 
				sVirtualArrayIDs, 
				sStorageIDs,
				SetDataType.SET_PLANAR);
		createdCommand.doCommand();
		
		return iNewSetId;
	}
}
