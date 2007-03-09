/**
 * 
 */
package cerberus.view.gui.swt.data.exchanger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.internal.resources.Container;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import cerberus.data.IUniqueObject;
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
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
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
	
	protected Composite refSWTContainer;
    
    protected String sArSetDataComboItems[];
    	
	protected ArrayList<String> arSetIDs;
	
	//private Vector<Integer> arSetIdList;
	
	protected boolean bIsSetComboInitialized = false;
	
	protected Table virtualArrayTable;
	
	protected Table storageTable;
	
	protected Combo setDataCombo;
	
	/**
	 * Stores the storages in the tables with their row.
	 * The row indicates which index they are in the SET.
	 * The information is needed for replacing the old storage 
	 * with the newly selected.
	 */
	protected HashMap<IStorage, Integer> hashStorage2StorageSetIndex;
 
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
		//arSetIdList = new Vector<Integer>();
		hashStorage2StorageSetIndex = new HashMap<IStorage, Integer> ();
	
	}
	
	public void initView() {

		refSWTContainer.setLayout(new RowLayout(SWT.VERTICAL));

		Label dataComboLabel = new Label(refSWTContainer, SWT.LEFT);
		dataComboLabel.setText("Select existing SET:");
		dataComboLabel.setLayoutData(new RowData(150, 30));

		setDataCombo = new Combo(refSWTContainer, SWT.READ_ONLY);

		createEmptyTables(); 
		createTableEditors();
		
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
	    	    
	    		int iSelectedSetDataId = StringConversionTool.convertStringToInt( 
	    				arSetIDs.get(setDataCombo.getSelectionIndex()), 
	    				0);
	    		
	    		reloadDataTablesForSet(iSelectedSetDataId);
	    	}
	    });
	}
	
	public void drawView() {
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": drawView()", 
				LoggerType.VERBOSE );		
	}

	public void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	protected void createEmptyTables() {
		
	    virtualArrayTable = new Table(refSWTContainer, SWT.SINGLE | SWT.FULL_SELECTION
	        | SWT.HIDE_SELECTION);
	    virtualArrayTable.setHeaderVisible(true);
	    virtualArrayTable.setLinesVisible(true);
		virtualArrayTable.setLayoutData(new RowData(300, 100));

	    storageTable = new Table(refSWTContainer, SWT.SINGLE | SWT.FULL_SELECTION
		        | SWT.HIDE_SELECTION);
	    storageTable.setHeaderVisible(true);
	    storageTable.setLinesVisible(true);
		storageTable.setLayoutData(new RowData(300, 100));
	    
	    String[] iArColumnCaptions = {"Dimension", "   ID   ", "Label"};
	    
	    // Create five columns
	    for (int i = 0; i < iArColumnCaptions.length; i++) {
	      TableColumn column = new TableColumn(virtualArrayTable, SWT.CENTER);
	      column.setText(iArColumnCaptions[i]);
	      column.pack();
	      
	      column = new TableColumn(storageTable, SWT.CENTER);
	      column.setText(iArColumnCaptions[i]);
	      column.pack();
	    }
	}
	
	protected void reloadDataTablesForSet(int iSelectedSetDataId) {
		
		//Reset tables
		virtualArrayTable.removeAll();
		storageTable.removeAll();
		hashStorage2StorageSetIndex.clear();
		
		ISet selectedSet = 
			refGeneralManager.getSingelton().getSetManager().getItemSet(iSelectedSetDataId);
		
		// Fill storage data
		TableItem tableItem = null;
	    String[] itemData = new String[3];
	    
		Iterator<IStorage> iterStorages;
		IStorage tmpStorage = null;
		int iStorageIndex = 0;
		
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
				
				tableItem.setText(itemData);
			}
		}
	}

	protected void createTableEditors() {

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
			            
			            // Select the previously selected item from the cell
			            combo.select(combo.indexOf(item.getText(column)));
			            
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
					    		
								item.setText(col, combo.getText());
								
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
}
