/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.selection;

import org.caleydo.view.info.selection.model.CategoryItem;
import org.caleydo.view.info.selection.model.ElementItem;
import org.caleydo.view.info.selection.model.SelectionTypeItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class CopySelectionToClipBoardAction extends Action {
	private ITreeSelection selection = null;
	public CopySelectionToClipBoardAction() {
		setText("Copy"); //$NON-NLS-1$
		setToolTipText("Copy to Clipboard"); //$NON-NLS-1$
		setEnabled(false);
		setImageDescriptor(Activator.getDefault().getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setId(ActionFactory.COPY.getId());
		setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
	}

	/**
	 * @param selection
	 *            setter, see {@link selection}
	 */
	public void setSelection(ITreeSelection selection) {
		this.selection = selection;
		setEnabled(this.selection != null && this.selection.size() > 0);
	}

	@Override
	public void run() {
		TreePath[] paths = selection.getPaths();
		StringBuilder b = new StringBuilder();
		for (TreePath path : paths) {
			Object last = path.getLastSegment();
			if (last instanceof ElementItem) {
				b.append(((ElementItem) last).getLabel()).append('\t');
			} else if (last instanceof SelectionTypeItem) {
				for(ElementItem item : ((SelectionTypeItem) last).getChildren()) {
					b.append(item.getLabel()).append('\t');
				}
			} else if (last instanceof CategoryItem) {
				for (SelectionTypeItem child : ((CategoryItem) last).getChildren()) {
					for(ElementItem item : child.getChildren()) {
						b.append(item.getLabel()).append('\t');
					}
				}
			}
		}
		if (b.length() > 0) {
			b.setLength(b.length()-1); //remove last \t
		}

		Clipboard clipboard = null;
		try {
			clipboard = new Clipboard(Display.getCurrent());
			clipboard.setContents(new Object[] { b.toString()}, new Transfer[] { TextTransfer.getInstance() });
		} finally {
			if (clipboard != null)
				clipboard.dispose();
		}
	}
}
