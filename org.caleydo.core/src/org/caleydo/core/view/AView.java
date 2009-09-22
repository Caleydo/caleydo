package org.caleydo.core.view;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.canvas.listener.NewSetListener;
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
	implements IView, IListenerOwner {

	protected IGeneralManager generalManager;

	protected IEventPublisher eventPublisher;

	/**
	 * The data domain the view is operating on
	 */
	protected EDataDomain dataDomain;

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

	private NewSetListener newSetListener;

	/**
	 * Constructor.
	 */
	public AView(final int iParentContainerId, final String sLabel, final int iViewID) {
		super(iViewID);

		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;
		registerEventListeners();
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
	public EDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(EDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public void setSet(ISet set) {
		this.set = set;
	}

	@Override
	public ISet getSet() {
		return set;
	}

	@Override
	public void setUseCase(IUseCase useCase) {
		this.useCase = useCase;
	}

	/**
	 * <p>
	 * Registers the listeners for this view to the event system. To release the allocated resources
	 * unregisterEventListeners() has to be called. This method is intended to be overridden, but it's super()
	 * should be called to be registered to the listeners defined by other classes in the hierarchy.
	 * </p>
	 * <p>
	 * This method is called by the Constructor of {@link AView}, therefore there is no need to call it
	 * yourself.
	 * </p>
	 */
	public void registerEventListeners() {
		newSetListener = new NewSetListener();
		newSetListener.setHandler(this);
		eventPublisher.addListener(NewSetEvent.class, newSetListener);
		// default implementations does not react on events
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called. This method is intended to be overridden, but it's super()
	 * should be called to unregistered the listeners defined by other classes in the hierarchy.
	 */
	public void unregisterEventListeners() {
		if (newSetListener != null) {
			eventPublisher.removeListener(newSetListener);
			newSetListener = null;
		}
	}

	/**
	 * creates and sends a {@link TriggerSelectioCommand} event and distributes it via the related
	 * eventPublisher.
	 * 
	 * @param expression_index
	 *            type of genome this selection command refers to
	 * @param command
	 *            selection-command to distribute
	 */
	protected void sendSelectionCommandEvent(EIDType genomeType, SelectionCommand command) {
		SelectionCommandEvent event = new SelectionCommandEvent();
		event.setSender(this);
		event.setCategory(genomeType.getCategory());
		event.setSelectionCommand(command);
		eventPublisher.triggerEvent(event);
	}

}
