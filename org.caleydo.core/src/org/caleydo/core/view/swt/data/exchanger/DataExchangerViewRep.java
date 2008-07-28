package org.caleydo.core.view.swt.data.exchanger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Data Exchanger View makes it possible
 * to swap the data of views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class DataExchangerViewRep 
extends AView 
implements IView {
	
    protected String viewComboItems[];
    
    protected String dataComboItems[];
    
	protected ArrayList<String> arViewData;
	
	protected ArrayList<String> arFilteredViews;
	
	protected ArrayList<String> arSetIDs;
	
    protected HashMap<String, IView> hashComboText2View;

    protected boolean bIsDataInitialized = false;
    
    protected int iCurrentSelectedSetId = 0;
    
	public DataExchangerViewRep(
			IGeneralManager generalManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(generalManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_DATA_EXCHANGER);	

		arViewData = new ArrayList<String>();
		arFilteredViews = new ArrayList<String>();
		arSetIDs = new ArrayList<String>();
		hashComboText2View = new HashMap<String, IView>();
	}

	/**
	 * 
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		swtContainer.setLayout(new RowLayout(SWT.VERTICAL));

		Label viewComboLabel = new Label(swtContainer, SWT.LEFT);
		viewComboLabel.setText("Select view:");
		viewComboLabel.setLayoutData(new RowData(150, 30));
				
		final Combo viewCombo = new Combo(swtContainer, SWT.READ_ONLY);
		
		Label dataComboLabel = new Label(swtContainer, SWT.LEFT);
		dataComboLabel.setText("Select new SET:");
		dataComboLabel.setLayoutData(new RowData(150, 30));

		final Combo dataCombo = new Combo(swtContainer, SWT.READ_ONLY);
        		
		viewCombo.setLayoutData(new RowData(150, 30));
	    viewCombo.setEnabled(true);

		dataCombo.setLayoutData(new RowData(150, 30));
	    dataCombo.setEnabled(false);
	    
	    viewCombo.addFocusListener(new FocusAdapter() {
	        public void focusGained(FocusEvent e) {
	        	
	        	if (bIsDataInitialized == true)
	        		return;
	        	
	    	    fillCombos();
	    	    viewCombo.removeAll();
	    	    viewCombo.setItems(viewComboItems);
	    	    dataCombo.setEnabled(true);
	    	    bIsDataInitialized = true;
	        }
	    });
	    
	    viewCombo.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {

	    	    viewCombo.select(viewCombo.getSelectionIndex());
				
//	    		dataCombo.add(Integer.toString(iCurrentSelectedSetId), 0);
//	    		
//	    		dataCombo.select(0);
	    		
	    		fillDataSets(viewCombo, dataCombo);
	    		
//	    	    generalManager.getSingelton().getViewCanvasManager().getItemCanvas(
//	    	    		new Integer(arViewData.get(viewCombo.getSelectionIndex())));
//	    	    
//	    		dataCombo.add(new Integer(
//	    			arViewData.get(viewCombo.getSelectionIndex()))).
//	    				getDataSetId()));
	    		
	    	}
	    });
	    
	    dataCombo.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {

	    	
//	    		int iSelectedViewDataSetId = StringConversionTool.convertStringToInt( 
//	    				arSetIDs.get(dataCombo.getSelectionIndex()), 
//	    				0);
	    		
	    		iCurrentSelectedSetId = StringConversionTool.convertStringToInt( 
	    				arSetIDs.get(dataCombo.getSelectionIndex()), 
	    				0);
	    		
	    		//FIXME: adjust to new SET concept of ViewRep
//	    		hashComboText2View.get(arFilteredViews.get(viewCombo.getSelectionIndex())).
//	    			setDataSetId(iSelectedViewDataSetId);

	    		bIsDataInitialized = false;
	    	}	    	
	    });
	}

	public void drawView() {
		
//		generalManager.logMsg(
//				this.getClass().getSimpleName() + 
//				": drawView()", 
//				LoggerType.VERBOSE );		
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	protected void fillCombos() {
		
//		Collection<IView> arViews = generalManager.getSingelton().
//			getViewGLCanvasManager().getAllViews();
//		
//		Collection<IGLCanvasUser> arGLCanvasUsers = generalManager.getSingelton().
//			getViewGLCanvasManager().getAllGLCanvasUsers();
//
//		Iterator<IView> iterViews = arViews.iterator();
////		Iterator<IGLCanvasUser> iterGLCanvasUsers = arGLCanvasUsers.iterator();
//		IView tmpView = null;
////		IGLCanvasUser tmpGLCanvasUser = null;
//
//		String sItemText = "";
		arFilteredViews.clear();

		//FIXME: adjust to new SET concept of ViewRep

//		while (iterViews.hasNext())
//		{
//			tmpView = iterViews.next();
//			
//			if (tmpView.getDataSetId() != 0)
//			{
//				sItemText = Integer.toString(((IUniqueObject)tmpView).getId()) 
//							+ " - " 
//							+ tmpView.getLabel();
//				
//				arFilteredViews.add(sItemText);
//				hashComboText2View.put(sItemText, tmpView);
//				arViewData.add(Integer.toString(tmpView.getDataSetId()));
//			}
//		}

		viewComboItems = arFilteredViews.toArray(new String[arFilteredViews.size()]);
	}
	
	protected void fillDataSets(Combo viewCombo, Combo dataCombo) {
		
		Collection<ISet> allSets = 
			generalManager.getSetManager().getAllSets();
		
		Iterator<ISet> iterSets = allSets.iterator();
		int iTmpSetId = 0;
		int iCurrentSelectedSetIndex = 0;

		arSetIDs.clear();
		
		while (iterSets.hasNext())
		{
			iTmpSetId = iterSets.next().getId();
			
    		//FIXME: adjust to new SET concept of ViewRep
//			iCurrentSelectedSetId = hashComboText2View.get(
//					arFilteredViews.get(viewCombo.getSelectionIndex())).
//						getDataSetId();
			
			if (iTmpSetId == iCurrentSelectedSetId)
				iCurrentSelectedSetIndex = arSetIDs.size();
			
//			//FIXME: why is the loop entered in every iteration?
//			if (iTmpSetId == hashComboText2View.get(arFilteredViews.get(viewCombo.getSelectionIndex())).getDataSetId());
//			{
				arSetIDs.add(Integer.toString(iTmpSetId));
//			}
		}
		
		dataComboItems = arSetIDs.toArray(new String[arSetIDs.size()]);
		dataCombo.removeAll();
		dataCombo.setItems(dataComboItems);
		dataCombo.select(iCurrentSelectedSetIndex);
	}
}
