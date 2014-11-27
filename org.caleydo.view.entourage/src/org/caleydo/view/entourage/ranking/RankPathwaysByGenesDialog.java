/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.ranking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredList;

/**
 * @author Christian
 *
 */
public class RankPathwaysByGenesDialog extends Dialog {

	private FilteredList genePoolList;
	private List selectedGenesList;
	private Text filterText;
	private Button selectGenesButton;
	private Button removeGenesButton;
	private Set<Object> selectedGeneIDs = new HashSet<>();
	private final IDType geneIDType = IDCategory.getIDCategory(EGeneIDTypes.GENE.name()).getPrimaryMappingType();

	/**
	 * @param parentShell
	 */
	public RankPathwaysByGenesDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Gene Selection");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Label desc = new Label(parentComposite, SWT.NONE);
		desc.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		desc.setText("Select the genes that shall be contained by the pathways.");

		Group geneSourceGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		geneSourceGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		geneSourceGroup.setText("Available Genes");
		geneSourceGroup.setLayout(new GridLayout(1, false));

		filterText = new Text(geneSourceGroup, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		filterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				genePoolList.setFilter(filterText.getText());
			}
		});

		genePoolList = new FilteredList(geneSourceGroup, SWT.BORDER | SWT.MULTI, new LabelProvider(), true, false, true);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		genePoolList.setLayoutData(gd);

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(geneIDType);
		Set<?> allIDs = mappingManager.getAllMappedIDs(geneIDType);
		IIDTypeMapper<Object, String> humanReadableMapper = mappingManager.getHumanReadableIDTypeMapper(geneIDType);

		java.util.List<Object> listElements = new ArrayList<>(allIDs.size());
		for (Object id : allIDs) {
			GeneObject gene = new GeneObject(id);
			Set<String> humanReadableIDs = humanReadableMapper.apply(id);
			if (humanReadableIDs != null && !humanReadableIDs.isEmpty()) {
				gene.humanReadableID = humanReadableIDs.iterator().next();
			}
			listElements.add(gene);
		}

		genePoolList.setElements(listElements.toArray());
		genePoolList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectGenesButton.setEnabled(genePoolList.getSelection() != null
						&& genePoolList.getSelection().length > 0);
			}
		});

		selectGenesButton = new Button(geneSourceGroup, SWT.PUSH);
		selectGenesButton.setText("Add");
		selectGenesButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		selectGenesButton.setEnabled(false);
		selectGenesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object gene : genePoolList.getSelection()) {
					boolean exists = false;
					for (String selectedGene : selectedGenesList.getItems()) {
						if (selectedGene.equals(gene.toString())) {
							exists = true;
							break;
						}
					}
					if (!exists) {
						selectedGenesList.add(gene.toString());
						selectedGenesList.setData(gene.toString(), gene);
						setOKEnabled(true);
					}
				}
			}
		});

		Group geneDestGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		geneDestGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		geneDestGroup.setText("Selected Genes");
		geneDestGroup.setLayout(new GridLayout(1, false));

		selectedGenesList = new List(geneDestGroup, SWT.BORDER | SWT.MULTI);
		selectedGenesList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		selectedGenesList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeGenesButton.setEnabled(selectedGenesList.getSelection() != null
						&& selectedGenesList.getSelection().length > 0);
			}
		});
		removeGenesButton = new Button(geneDestGroup, SWT.PUSH);
		removeGenesButton.setText("Remove");
		removeGenesButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		removeGenesButton.setEnabled(false);
		removeGenesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (String gene : selectedGenesList.getSelection()) {
					selectedGenesList.remove(gene);
				}
				if (selectedGenesList.getItemCount() <= 0)
					setOKEnabled(false);
			}
		});

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		setOKEnabled(false);
	}

	private void setOKEnabled(boolean enabled) {
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}

	@Override
	protected void okPressed() {
		selectedGeneIDs = new HashSet<>(selectedGenesList.getItemCount());
		for (String gene : selectedGenesList.getItems()) {
			GeneObject o = (GeneObject) selectedGenesList.getData(gene);
			selectedGeneIDs.add(o.id);
		}
		super.okPressed();
	}

	private static class GeneObject {
		public final Object id;
		public String humanReadableID;

		public GeneObject(Object id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return humanReadableID == null ? id.toString() : humanReadableID;
		}
	}

	/**
	 * @return the selectedGeneIDs, see {@link #selectedGeneIDs}
	 */
	public Set<Object> getSelectedGeneIDs() {
		return selectedGeneIDs;
	}

	public IDType getGeneIDType() {
		return geneIDType;
	}

}
