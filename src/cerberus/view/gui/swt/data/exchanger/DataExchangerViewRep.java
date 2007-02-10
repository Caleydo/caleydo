/**
 * 
 */
package cerberus.view.gui.swt.data.exchanger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import cerberus.data.IUniqueObject;
import cerberus.data.collection.ISet;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.system.StringConversionTool;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Data Exchanger View makes it possible
 * to swap the data of views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class DataExchangerViewRep 
extends AViewRep 
implements IView {
	
	protected Composite refSWTContainer;
	
    protected String viewComboItems[];
    
    protected String dataComboItems[];
    
	protected ArrayList<String> arViewData;
	
	protected ArrayList<String> arFilteredViews;
	
	protected ArrayList<String> arSetIDs;
	
    protected HashMap<String, IView> hashComboText2View;

    protected boolean bIsDataInitialized = false;
    
	public DataExchangerViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);	

		arViewData = new ArrayList<String>();
		arFilteredViews = new ArrayList<String>();
		arSetIDs = new ArrayList<String>();
		hashComboText2View = new HashMap<String, IView>();
	}

	public void initView() {

		refSWTContainer.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Combo viewCombo = new Combo(refSWTContainer, SWT.SIMPLE |
                SWT.V_SCROLL | SWT.H_SCROLL);
		final Combo dataCombo = new Combo(refSWTContainer, SWT.SIMPLE | 
                SWT.V_SCROLL | SWT.H_SCROLL);
        		
	    viewCombo.setBounds(50, 85, 150, 65);
	    viewCombo.setEnabled(true);

	    dataCombo.setBounds(50, 85, 150, 65);
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

	    		fillDataSets(viewCombo, dataCombo);
	    	    viewCombo.select(viewCombo.getSelectionIndex());

//	    		dataCombo.select(arViewData.get(viewCombo.getSelectionIndex()));
	    		//dataCombo.select();
	    	}
	    });
	    
	    dataCombo.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {

	    		int iSelectedViewDataSetId = StringConversionTool.convertStringToInt( 
	    				arSetIDs.get(dataCombo.getSelectionIndex()), 
	    				0);

	    		hashComboText2View.get(
	    				arFilteredViews.get(viewCombo.getSelectionIndex())).
	    					setDataSetId(iSelectedViewDataSetId);
	    		
	    		bIsDataInitialized = false;
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
	
	protected void fillCombos() {
		
		Collection<IView> arViews = refGeneralManager.getSingelton().
			getViewGLCanvasManager().getAllViews();
		
//		Collection<IGLCanvasUser> arGLCanvasUsers = refGeneralManager.getSingelton().
//			getViewGLCanvasManager().getAllGLCanvasUsers();

		Iterator<IView> iterViews = arViews.iterator();
//		Iterator<IGLCanvasUser> iterGLCanvasUsers = arGLCanvasUsers.iterator();
		IView tmpView = null;
//		IGLCanvasUser tmpGLCanvasUser = null;

		String sItemText = "";
		arFilteredViews.clear();
		
		while (iterViews.hasNext())
		{
			tmpView = iterViews.next();
			
			if (tmpView.getDataSetId() != 0)
			{
				sItemText = Integer.toString(((IUniqueObject)tmpView).getId()) 
							+ " - " 
							+ tmpView.getLabel();
				
				arFilteredViews.add(sItemText);
				hashComboText2View.put(sItemText, tmpView);
				arViewData.add(Integer.toString(tmpView.getDataSetId()));
			}
		}
		
//		while (iterGLCanvasUsers.hasNext())
//		{
//			tmpGLCanvasUser = iterGLCanvasUsers.next();
//			
//			if (tmpGLCanvasUser.get)
//		}

		viewComboItems = arFilteredViews.toArray(new String[arFilteredViews.size()]);
	}
	
	protected void fillDataSets(Combo viewCombo, Combo dataCombo) {
		
		Collection<ISet> allSets = 
			refGeneralManager.getSingelton().getSetManager().getAllSetItems();
		
		Iterator<ISet> iterSets = allSets.iterator();
		int iTmpSetId = 0;

		arSetIDs.clear();
		
		while (iterSets.hasNext())
		{
			iTmpSetId = iterSets.next().getId();
			
//			//FIXME: why is the loop entered in every iteration?
//			if (iTmpSetId == hashComboText2View.get(arFilteredViews.get(viewCombo.getSelectionIndex())).getDataSetId());
//			{
				arSetIDs.add(Integer.toString(iTmpSetId));
//			}
		}
		
		dataComboItems = arSetIDs.toArray(new String[arSetIDs.size()]);
		dataCombo.removeAll();
		dataCombo.setItems(dataComboItems);

	}
}
