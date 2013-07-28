/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.AUniqueObject;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Preconditions;

/**
 * Abstract class that is the base of all view representations. It holds the the
 * own view ID, the parent ID and the attributes that needs to be processed.
 *
 * @author Marc Streit
 */
public abstract class AView extends AUniqueObject implements IView {

	/** The plugin name of the view, e.g. org.caleydo.view.parallel.coordinates */
	private final String viewType;

	/**
	 * The human readable view name used to identifying the type of the view,
	 * e.g. "Parallel Coordinates"
	 */
	private final String viewName;

	/**
	 * A custom label of the view, including, for example info on the dataset it
	 * shows, e.g. "Glioma Pathway" for a view that shows a glioma pathway.
	 * Defaults to {@link #viewName}.
	 */
	protected String label = null;

	/**
	 * Flag setting whether the label is default or was manually specified
	 */
	protected boolean isLabelDefault = true;

	public String icon = "resources/icons/general/no_icon_available.png";

	protected final EventPublisher eventPublisher;

	/**
	 * The number that identifies the concrete view among all instances of its
	 * class. This number is used among others int automatic view labels.
	 */
	protected int instanceNumber = -1;

	/**
	 * Constructor.
	 *
	 * @param viewType
	 *            TODO
	 * @param viewName
	 *            TODO
	 */
	public AView(String viewType, String viewName) {
		super(IDCreator.createPersistentIntID(AView.class));

		this.viewType = Preconditions.checkNotNull(viewType,"viewType is null");
		this.viewName = Preconditions.checkNotNull(viewName,"viewName is null");
		label = viewName;
		eventPublisher = EventPublisher.INSTANCE;
	}

	/**
	 * Empty implementation of initialize, should be overwritten in views if
	 * needed.
	 */
	@Override
	public void initialize() {

	}

	/**
	 * creates and sends a {@link TriggerSelectioCommand} event and distributes
	 * it via the related eventPublisher.
	 *
	 * @param expression_index
	 *            type of genome this selection command refers to
	 * @param command
	 *            selection-command to distribute
	 */
	@Deprecated
	protected void sendSelectionCommandEvent(IDType genomeType, SelectionCommand command) {
		SelectionCommandEvent event = new SelectionCommandEvent();
		event.setSender(this);
		event.setSelectionCommand(command);
		event.setIDCategory(genomeType.getIDCategory());
		eventPublisher.triggerEvent(event);
	}

	/**
	 * @return A Copy of the datadomain set of this view.
	 */
	@Override
	public Set<IDataDomain> getDataDomains() {
		return new HashSet<IDataDomain>();
	}

	/**
	 * Determines whether the view displays concrete data of a data set or not.
	 *
	 * @return
	 */
	@Override
	public boolean isDataView() {
		return false;
	}

	protected static void initViewType() {
	}

	/**
	 * @return the viewName, see {@link #viewName}
	 */
	public String getViewName() {
		return viewName;
	}

	@Override
	public String getViewType() {
		return viewType;
	}

	/**
	 * @return Label containing the view name and the instance number, if that
	 *         number is greater than 0
	 */
	protected String getDefaultLabel() {
		return viewName + ((instanceNumber > 0) ? " (" + instanceNumber + ")" : "");
	}

	@Override
	public void setLabel(String label, boolean isLabelDefault) {
		this.label = label;
		this.isLabelDefault = isLabelDefault;

		updateRCPViewPartName();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isLabelDefault() {
		return isLabelDefault;
	}

	@Override
	public String getProviderName() {
		return viewName + " View";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;

		updateRCPViewPartName();
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {
		if (!serializedView.isLabelDefault())
			setLabel(serializedView.getViewLabel(), false);
		else
			setLabel(this.getDefaultLabel(), true);
	}

	private void updateRCPViewPartName() {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				CaleydoRCPViewPart viewPart = ViewManager.get().getViewPartFromView(
						AView.this);
				if (viewPart != null) {
					viewPart.setPartName(AView.this.label);
				}
			}
		});
	}

	/**
	 * @return the instanceNumber, see {@link #instanceNumber}
	 */
	@Override
	public int getInstanceNumber() {
		return instanceNumber;
	}

	/**
	 * @param instanceNumber
	 *            setter, see {@link #instanceNumber}
	 */
	@Override
	public void setInstanceNumber(int instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
}
