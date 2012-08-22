/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view;

import java.util.Set;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.base.AUniqueObject;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Abstract class that is the base of all view representations. It holds the the
 * own view ID, the parent ID and the attributes that needs to be processed.
 * 
 * @author Marc Streit
 */
public abstract class AView extends AUniqueObject implements IView {

	/** The plugin name of the view, e.g. org.caleydo.view.parallel.coordinates */
	private String viewType;

	/**
	 * The human readable view name used to identifying the type of the view,
	 * e.g. "Parallel Coordinates"
	 */
	private String viewName = "Unspecified view name";

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

	public static EIconTextures icon = EIconTextures.NO_ICON_AVAILABLE;

	protected GeneralManager generalManager;

	protected EventPublisher eventPublisher;

	protected Composite parentComposite;

	/**
	 * Constructor.
	 * 
	 * @param viewType
	 *            TODO
	 * @param viewName
	 *            TODO
	 */
	public AView(int viewID, Composite parentComposite, String viewType, String viewName) {
		super(viewID);

		this.viewType = viewType;
		this.viewName = viewName;
		if (viewType == null || viewName == null) {
			throw new IllegalStateException("One of these was not defined: viewType: "
					+ viewType + " viewName: " + viewName);
		}
		label = viewName;
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
		this.parentComposite = parentComposite;
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
		event.tableIDCategory(genomeType.getIDCategory());
		eventPublisher.triggerEvent(event);
	}

	public Set<IDataDomain> getDataDomains() {
		return null;
	}

	/**
	 * Determines whether the view displays concrete data of a data set or not.
	 * 
	 * @return
	 */
	public boolean isDataView() {
		return false;
	}

	public Composite getParentComposite() {
		return parentComposite;
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
	
	private void updateRCPViewPartName() {
		
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				ARcpGLViewPart viewPart = ViewManager.get().getViewPartFromView(
						AView.this);
				if (viewPart != null) {
					viewPart.setPartName(AView.this.label);
				}
			}
		});
	}
}
