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
package org.caleydo.core.io.gui.dataimport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.util.color.EColorSchemeType;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for selecting a color scheme.
 *
 * @author Christian Partl
 *
 */
public class ChooseColorSchemeDialog extends Dialog {

	private List<ColorBrewer> colorSchemes;

	private ColorBrewer selectedColorScheme;

	private List<Integer> numColors;

	private Set<org.eclipse.swt.graphics.Color> registeredColors = new HashSet<>();

	/**
	 * @param parentShell
	 * @param colorSchemes
	 *            The color schemes that shall be chosen from.
	 * @param numColors
	 *            The number of colors that shall be displayed for the color schemes.
	 * @param selectedColorScheme
	 *            The color scheme that is selected by default. May be null.
	 */
	public ChooseColorSchemeDialog(Shell parentShell, List<ColorBrewer> colorSchemes, List<Integer> numColors,
			ColorBrewer selectedColorScheme) {
		super(parentShell);
		this.colorSchemes = colorSchemes;
		this.selectedColorScheme = selectedColorScheme;
		this.numColors = numColors;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Color Scheme");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));

		Group colorSchemeGroup = new Group(parentComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		colorSchemeGroup.setLayout(gridLayout);
		colorSchemeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (int i = 0; i < colorSchemes.size(); i++) {
			ColorBrewer scheme = colorSchemes.get(i);
			final Button button = new Button(colorSchemeGroup, SWT.RADIO);
			button.setText(scheme.name());
			if (scheme == selectedColorScheme) {
				button.setSelection(true);
			}
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					selectedColorScheme = ColorBrewer.valueOf(button.getText());
					getButton(OK).setEnabled(true);
				}
			});
			if (scheme.getType() == EColorSchemeType.QUALITATIVE) {
				createDiscretePreview(colorSchemeGroup, scheme, numColors.get(i));
			} else {
				createGradientPreview(colorSchemeGroup, scheme, numColors.get(i));
			}
		}

		Link colorBrewerLink = new Link(parentComposite, SWT.NONE);
		colorBrewerLink.setText("Color schemes taken from <A>http://www.ColorBrewer.org</A>");
		colorBrewerLink.addSelectionListener(BrowserUtils.LINK_LISTENER);

		return parent;
	}

	private void createGradientPreview(Composite parent, ColorBrewer scheme, int numColors) {
		CLabel preview = new CLabel(parent, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 50;
		gridData.heightHint = 20;
		preview.setLayoutData(gridData);
		ColorMapper.createColorMappingPreview(scheme.asColorMapper(numColors), preview);
	}

	private void createDiscretePreview(Composite parent, ColorBrewer scheme, int numColors) {
		Composite previewComposite = new Composite(parent, SWT.SHADOW_IN);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 0;
		previewComposite.setLayout(rowLayout);
		// previewComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// GridLayout gridLayout = new GridLayout(numColors, true);
		// gridLayout.horizontalSpacing = 0;
		List<Color> colors = scheme.get(numColors);
		for (Color color : colors) {
			CLabel preview = new CLabel(previewComposite, SWT.SHADOW_ETCHED_IN);

			// GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			// gridData.minimumWidth = 20;
			// preview.setLayoutData(gridData);
			RowData rowData = new RowData(20, 20);
			preview.setLayoutData(rowData);
			org.eclipse.swt.graphics.Color c = color.getSWTColor(Display.getCurrent());
			preview.setBackground(c);
			preview.update();
			registeredColors.add(c);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (selectedColorScheme == null)
			getButton(OK).setEnabled(false);
	}

	@Override
	protected void okPressed() {
		if (selectedColorScheme == null)
			return;
		super.okPressed();
		disposeColors();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		disposeColors();
	}

	private void disposeColors() {
		for (org.eclipse.swt.graphics.Color color : registeredColors) {
			color.dispose();
		}
		registeredColors.clear();
	}

	/**
	 * @return the selectedColorScheme, see {@link #selectedColorScheme}
	 */
	public ColorBrewer getSelectedColorScheme() {
		return selectedColorScheme;
	}

}
