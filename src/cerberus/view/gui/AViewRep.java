package cerberus.view.gui;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.AUniqueManagedObject;
import cerberus.data.collection.ISet;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.ViewType;

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
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#update(java.lang.Object)
	 */
	public void update( Object eventTrigger ) {
		
		//Implemented in subclasses		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateSelection(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateSelection(Object eventTrigger, ISet updatedSelectionSet) {

		//Implemented in subclasses
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#getDataSet()
	 */
	public int getDataSetId() {
	
		//Implemented in subclass
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#setDataSetId(int)
	 */
	public void setDataSetId(int iDataSetId) {
		
		//Implemented in subclass
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#getLabel()
	 */
	public String getLabel() {
		
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
}
