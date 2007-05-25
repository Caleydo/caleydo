package cerberus.view.gui;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.AUniqueManagedObject;
import cerberus.data.collection.ISet;
import cerberus.data.collection.SetType;
import cerberus.data.collection.set.selection.SetSelection;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.ISetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

/**
 * Abstract class that is the base of all view representations.
 * It holds the the own view ID, the parent ID and the attributes that
 * needs to be processed.
 * 
 * @see cerberus.manager.event.mediator.IMediatorReceiver
 * @see cerberus.manager.event.mediator.IMediatorSender
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AViewRep 
extends AUniqueManagedObject
implements IViewRep {
	
	private final ISetManager refSetManager;
	
	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList <ISet> alSetData;
	
	/**
	 * List for all ISet objects providing data related to interactive selection for this ViewRep.	
	 */
	protected ArrayList <SetSelection> alSetSelection;
	
	protected final ViewType viewType;
	
	protected int iParentContainerId;
	
	protected String sLabel;
	
	/**
	 * Width of the widget.
	 */
	protected int iWidth;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeight;
	
	protected ViewEventStateType eventState;
	
	protected Composite refSWTContainer;

	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public AViewRep(
			final IGeneralManager refGeneralManager, 
			final int iViewId, 
			final int iSetParentContainerId, 
			final String sLabel,
			final ViewType viewType ) {
		
		super ( iViewId, refGeneralManager );
		
		assert iSetParentContainerId != 0 : "Constructor iParentContainerId must not be 0!";
		
		this.iParentContainerId = iSetParentContainerId;
		this.sLabel = sLabel;
		
		eventState = ViewEventStateType.NONE;
		
		this.viewType = viewType;
		
		alSetData = new ArrayList <ISet> ();
		alSetSelection = new ArrayList <SetSelection> ();
		
		refSetManager = refGeneralManager.getSingelton().getSetManager();
	}
	

	public void setAttributes(int iWidth, int iHeight) {
		
		this.iWidth = iWidth;
		this.iHeight = iHeight;
	}
	
	public final ManagerObjectType getBaseType() {
		return null;
	}
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 * 
	 * @param iParentContainerId
	 */
	public void setParentContainerId(int iParentContainerId) {
		
		this.iParentContainerId = iParentContainerId;
	}
	
	/**
	 * Method uses the parent container ID to retrieve the 
	 * GUI widget by calling the createWidget method from
	 * the SWT GUI Manager.
	 * 
	 */
	protected void retrieveGUIContainer() {
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) refGeneralManager
				.getSingelton().getSWTGUIManager().createWidget(
						ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
						iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#update(java.lang.Object)
	 */
	public void updateReceiver( Object eventTrigger ) {
		
		//Implemented in subclasses		
		assert false : "This method must be overloaded in sub-class";
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		//Implemented in subclasses
		assert false : "This method must be overloaded in sub-class";
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#getLabel()
	 */
	public final String getLabel() {
		
		return sLabel;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.view.gui.IView#setViewType(cerberus.view.gui.ViewType)
	 */
	public final void setViewType(ViewType viewType) {
		
		assert false : "viewType is final!";
	}
	
	/**
	 * @see cerberus.view.gui.IViewRep#getViewType()
	 */
	public final ViewType getViewType() {
		return viewType;
	}
	

	/**
	 * @see cerberus.view.gui.IView#addSetId(int[])
	 */
	public final void addSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = refSetManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
				refGeneralManager.getSingelton().logMsg(
						"addSetId(" + iSet[i] + ") is not registered at SetManager!",
						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( ! hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_RAW_DATA:
					alSetData.add(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.add((SetSelection)refCurrentSet);
					break;
					
				default:
					refGeneralManager.getSingelton().logMsg(
							"addSetId() unsupported SetType!",
							LoggerType.ERROR_ONLY);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
				refGeneralManager.getSingelton().logMsg(
						"addSetId(" + iSet[i] + ") ISet is already registered!",
						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
	}
	
	/**
	 * @see cerberus.view.gui.IView#removeAllSetIdByType(cerberus.data.collection.SetType)
	 */
	public final void removeAllSetIdByType( SetType setType ) {
		
		switch (setType) {
		case SET_RAW_DATA:
			alSetData.clear();
			break;
			
		case SET_SELECTION:
			alSetSelection.clear();
			break;
			
		default:
			refGeneralManager.getSingelton().logMsg(
					"addSetId() unsupported SetType!",
					LoggerType.ERROR_ONLY);
		} // switch (setType) {
	}
	
	/**
	 * @see cerberus.view.gui.IView#removeSetId(int[])
	 */
	public final void removeSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = refSetManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
				refGeneralManager.getSingelton().logMsg(
						"removeSetId(" + iSet[i] + ") is not registered at SetManager!",
						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_RAW_DATA:
					alSetData.remove(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.remove(refCurrentSet);
					break;
					
				default:
					refGeneralManager.getSingelton().logMsg(
							"removeSetId() unsupported SetType!",
							LoggerType.ERROR_ONLY);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
				refGeneralManager.getSingelton().logMsg(
						"removeSetId(" + iSet[i] + ") ISet was not registered!",
						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
		
	}
	

	/**
	 * @see cerberus.view.gui.IView#getAllSetId()
	 */
	public final synchronized int[] getAllSetId() {
		
		//FIXME: thread safe access to ArrayLists!
		int iTotalSizeResultArray = alSetData.size() + alSetSelection.size();
		
		/* allocate int[] and copy from Arraylist*/
		int [] resultArray = new int [iTotalSizeResultArray];
		
		/* early exit */
		if ( iTotalSizeResultArray == 0) 
		{
			return resultArray;
		}
		
		int i=0;		
		Iterator <ISet> iter = alSetData.iterator();		
		for (;iter.hasNext();i++)
		{
			resultArray[i] = iter.next().getId();
		}
		
		Iterator <SetSelection> iterSelectionSet = alSetSelection.iterator();		
		for (;iterSelectionSet.hasNext();i++)
		{
			resultArray[i] = iterSelectionSet.next().getId();
		}
		
		return resultArray;
	}
	

	/**
	 * @see cerberus.view.gui.IView#hasSetId(int)
	 */
	public final boolean hasSetId( int iSetId) {
		ISet refCurrentSet = refSetManager.getItemSet(iSetId);
		
		if ( refCurrentSet == null )
		{
			return false;
		}
		
		return hasSetId_ByReference(refCurrentSet);
	}
	
	
	/**
	 * Test both ArrayList's alSetData and alSetSelection for refSet.
	 * 
	 * @param refSet test if this ISet is refered to
	 * @return TRUE if exists in any of the two ArrayList's
	 */
	public final boolean hasSetId_ByReference(final ISet refSet) {
		
		assert refSet != null : "Can not handle null-pointer";
			
		if ( alSetData.contains(refSet) ) 
		{
			return true;
		}
		if ( alSetSelection.contains(refSet) ) 
		{
			return true;
		}
		
		return false;			
	}
}
