package org.caleydo.core.view;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract class that is the base of all view representations. It holds the the own view ID, the parent ID
 * and the attributes that needs to be processed.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AView
	extends AUniqueObject
	implements IView {

	protected IGeneralManager generalManager;
	
	protected IEventPublisher eventPublisher;

	/**
	 * The use case which determines the use case specific behavior of the view.
	 */
	protected IUseCase useCase;

	/**
	 * Data set which the view operates on.
	 */
	protected ISet set;

	protected int iParentContainerId;

	protected Composite parentComposite;

	protected String sLabel;

	/**
	 * Constructor.
	 */
	public AView(final int iParentContainerId, final String sLabel, final int iViewID) {
		super(iViewID);

		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
//		setManager = generalManager.getSetManager();

		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;
	}

	/**
	 * Sets the unique ID of the parent container. Normally it is already set in the constructor. Use this
	 * method only if you want to change the parent during runtime.
	 * 
	 * @param iParentContainerId
	 */
	public void setParentContainerId(int iParentContainerId) {
		this.iParentContainerId = iParentContainerId;
	}

	@Override
	public final String getLabel() {
		return sLabel;
	}

	@Override
	public final void setLabel(String label) {
		this.sLabel = label;

		if (parentComposite != null) {
			parentComposite.getShell().setText(label);
		}
	}
	
	@Override
	public void setSet(ISet set) {
		this.set = set;
	}
	
	@Override
	public ISet getSet(){
		return set;
	}
	
	@Override
	public void setUseCase(IUseCase useCase) {
		this.useCase = useCase;
	}

	/**
	 * Registers the listeners for this view to the event system.
	 * To release the allocated resources unregisterEventListeners() has to be called.
	 */
	public void registerEventListeners() {
		// default implementations does not react on events 
	}

	/**
	 * Unregisters the listeners for this view from the event system.
	 * To release the allocated resources unregisterEventListenrs() has to be called.
	 */
	public void unregisterEventListeners() {
		// default implementations does not react on events 
	}
}
