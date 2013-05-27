/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.info.selection;

import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Search view contains gene and pathway search.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class RcpSelectionInfoView extends CaleydoRCPViewPart implements IEventBasedSelectionManagerUser {

	public static String VIEW_TYPE = "org.caleydo.view.info.selection";

	private HashMap<SelectionManager, TreeItem> selectionManagerToSubTree = new HashMap<SelectionManager, TreeItem>();

	private Tree selectionTree;

	/**
	 * Constructor.
	 */
	public RcpSelectionInfoView() {
		super();

		isSupportView = true;
		eventPublisher = GeneralManager.get().getEventPublisher();

		try {
			viewContext = JAXBContext.newInstance(SerializedSelectionInfoView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	private void createSelectionManagersForIDCategories() {

		for (IDCategory idCategory : IDCategory.getAllRegisteredIDCategories()) {

			if (idCategory.getPrimaryMappingType() == null)
				continue;

			EventBasedSelectionManager selectionManager = new EventBasedSelectionManager(this,
					idCategory.getPrimaryMappingType());
			selectionManager.registerEventListeners();
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new FillLayout());

		selectionTree = new Tree(parentComposite, SWT.NULL);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		selectionTree.setLayoutData(gridData);

		// lblViewInfoContent = new Label(parent, SWT.WRAP);
		// lblViewInfoContent.setText("");
		// gridData = new GridData(GridData.FILL_BOTH);
		//
		// gridData.grabExcessVerticalSpace = true;
		// gridData.minimumHeight = 100;
		// lblViewInfoContent.setLayoutData(gridData);

		createSelectionManagersForIDCategories();

		parentComposite.layout();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSelectionInfoView();
		determineDataConfiguration(serializedView, false);
	}

	private void updateSubTree(final SelectionManager selectionManager) {

		if (parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {

				IDType idType = selectionManager.getIDType();
				IDCategory idCategory = idType.getIDCategory();

				if (idType.getIDCategory().getPrimaryMappingType() == null)
					return;

				Set<Integer> mouseOverIDs = selectionManager.getElements(SelectionType.MOUSE_OVER);
				Set<Integer> selectedIDs = selectionManager.getElements(SelectionType.SELECTION);

				if (!selectionManagerToSubTree.containsKey(selectionManager)
						&& (mouseOverIDs.size() > 0 || selectedIDs.size() > 0)) {

					TreeItem idCategorySubTree = new TreeItem(selectionTree, SWT.NONE);
					idCategorySubTree.setExpanded(true);
					idCategorySubTree.setData(-1);
					idCategorySubTree.setText(idCategory.getDenominationPlural());

					selectionManagerToSubTree.put(selectionManager, idCategorySubTree);
				}

				TreeItem subTree = selectionManagerToSubTree.get(selectionManager);

				if (subTree == null)
					return;

				// Flush old items from this selection type
				for (TreeItem item : subTree.getItems()) {
					item.dispose();
				}
				subTree.clearAll(true);

				createItems(subTree, SelectionType.MOUSE_OVER, mouseOverIDs, idType);
				createItems(subTree, SelectionType.SELECTION, selectedIDs, idType);

				int numberElements = IDMappingManagerRegistry.get().getIDMappingManager(idType).getPrimaryTypeCounter();
				int selectedRecords = selectionManager.getNumberOfElements(SelectionType.SELECTION);
				float selectedRecordsPercentage = selectedRecords / (float) numberElements * 100f;

				if (numberElements != 0) {
					subTree.setText(idType.getIDCategory().getDenominationPlural() + " - " + selectedRecords + " of "
							+ numberElements + " (" + Formatter.formatNumber(selectedRecordsPercentage) + "%)");
				}
			}
		});
	}

	private synchronized void createItems(TreeItem tree, SelectionType selectionType, Set<Integer> IDs, IDType idType) {
		Color color;
		int[] intColor = selectionType.getIntColor();

		color = new Color(parentComposite.getDisplay(), intColor[0], intColor[1], intColor[2]);

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);

		for (Integer id : IDs) {
			String humanReadableID = "<unresolved>";
			Set<Object> resolvedIDs = idMappingManager.getIDAsSet(idType, idType.getIDCategory()
					.getHumanReadableIDType(), id);
			if (resolvedIDs != null && resolvedIDs.size() > 0)
				humanReadableID = (String) resolvedIDs.toArray()[0];

			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(humanReadableID + "");
			item.setBackground(color);
			item.setData(id);
			item.setData("selection_type", selectionType);
		}

		tree.setExpanded(true);
	}

	@Override
	public synchronized void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		updateSubTree(selectionManager);
	}
}
