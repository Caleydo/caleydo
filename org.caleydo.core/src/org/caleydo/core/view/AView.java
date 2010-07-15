package org.caleydo.core.view;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
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

	/** The plugin name of the view */
	public String viewType;

	public static EIconTextures icon = EIconTextures.NO_ICON_AVAILABLE;

	protected GeneralManager generalManager;

	protected IEventPublisher eventPublisher;

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

		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;

	}

	/**
	 * Empty implementation of initialize, should be overwritten in views if needed.
	 */
	@Override
	public void initialize() {

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

	/**
	 * creates and sends a {@link TriggerSelectioCommand} event and distributes it via the related
	 * eventPublisher.
	 * 
	 * @param expression_index
	 *            type of genome this selection command refers to
	 * @param command
	 *            selection-command to distribute
	 */
	@Deprecated
	protected void sendSelectionCommandEvent(EIDType genomeType, SelectionCommand command) {
		SelectionCommandEvent event = new SelectionCommandEvent();
		event.setSender(this);
		event.setCategory(genomeType.getCategory());
		event.setSelectionCommand(command);
		eventPublisher.triggerEvent(event);
	}

	public String getViewType() {
		return viewType;
	}
}
