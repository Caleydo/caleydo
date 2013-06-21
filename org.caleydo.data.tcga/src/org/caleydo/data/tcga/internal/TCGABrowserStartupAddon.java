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
package org.caleydo.data.tcga.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.startup.LoadProjectStartupProcedure;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.RemoteFile;
import org.caleydo.data.tcga.internal.model.RunOverview;
import org.caleydo.data.tcga.internal.model.TumorProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * addon that parses out json file and create a selection dialog
 * 
 * @author Samuel Gratzl
 * 
 */
public class TCGABrowserStartupAddon implements IStartupAddon {
	private static final Logger log = Logger.create(TCGABrowserStartupAddon.class);
	private static final String PREFIX = "http://compbio.med.harvard.edu/tcga/stratomex/data/";
	private static final String JSONFILE = PREFIX + "tcga_analysis_runs.json";
	private URL selectedChoice = null;
	private final Gson gson = new GsonBuilder().create();

	@Override
	public boolean init() {
		return false;
	}

	@Override
	public Composite create(Composite parent, final WizardPage page) {
		parent = new Composite(parent, SWT.NONE);
		parent.setLayout(new FillLayout());
		try {
			File file = RemoteFile.of(new URL(JSONFILE)).getOrLoad(true, new NullProgressMonitor());
			if (file == null) {
				Label l = new Label(parent, SWT.WRAP);
				l.setText("Can't download:\n" + JSONFILE);
			} else {
				final TreeViewer v = new TreeViewer(parent, SWT.VIRTUAL | SWT.BORDER);
				v.setLabelProvider(new LabelProvider());
				v.setContentProvider(new MyContentProvider(v));
				v.setUseHashlookup(true);
				RunOverview[] model = createModel(file);
				v.setInput(model);
				v.getTree().setItemCount(model.length);
				v.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection s = (IStructuredSelection) event.getSelection();
						Object f = s.getFirstElement();
						if (f instanceof TumorProject) {
							selectedChoice = ((TumorProject) f).getProject();
							page.setPageComplete(true);
						} else {
							selectedChoice = null;
							page.setPageComplete(false);
						}
					}
				});
			}
		} catch (MalformedURLException e) {
			log.error("can't parse: " + JSONFILE, e);
		}
		return parent;
	}

	private static final Styler INCOMPATIBLE_STYLE = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		}
	};


	public static class LabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();

			if (element instanceof TumorProject) {
				TumorProject item = (TumorProject) element;
				text.append(item.toString());
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			} else if (element instanceof RunOverview) {
				RunOverview item = (RunOverview) element;
				text.append(item.toString());
				if (item.isResolved()) {
					if (item.isCompatible())
						text.append(String.format(" (%d)", item.size()), StyledString.COUNTER_STYLER);
					else
						text.append(" INCOMPATIBLE!", INCOMPATIBLE_STYLE);
				}
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			} else if (element instanceof String) {
				text.append(" INCOMPATIBLE!", INCOMPATIBLE_STYLE);
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			}
			super.update(cell);
		}
	}

	private class MyContentProvider implements ILazyTreeContentProvider {
		private TreeViewer viewer;
		private RunOverview[] elements;

		public MyContentProvider(TreeViewer viewer) {
			this.viewer = viewer;
		}

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.elements = (RunOverview[]) newInput;
		}


		@Override
		public Object getParent(Object element) {
			if (element instanceof TumorProject)
				return ((TumorProject) element).getParent();
			return elements;
		}

		@Override
		public void updateChildCount(Object element, int currentChildCount) {
			if (element instanceof RunOverview) {
				RunOverview overview = (RunOverview) element;
				resolve(overview);
				viewer.setChildCount(element, overview.isCompatible() ? overview.size() : 0);
				viewer.update(element, null);
			}
		}

		private void resolve(RunOverview overview) {
			try {
				overview.resolve(gson, PREFIX);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				log.error("can't resolve " + overview, e);
			}
		}

		@Override
		public void updateElement(Object parent, int index) {
			Object element;
			if (parent instanceof RunOverview) {
				RunOverview overview = (RunOverview) parent;
				updateChildCount(parent, -1);
				if (!overview.isCompatible()) {
					element = "Incompatible to your current Caleydo version";
				} else
					element = overview.getProject(index);
				viewer.replace(parent, index, element);
				viewer.setChildCount(element, 0); // none
			} else {
				element = elements[index];
				viewer.replace(parent, index, element);
				viewer.setChildCount(element, 1); // guess at least one
			}
		}

	}

	private RunOverview[] createModel(File file) {
		Gson gson = new Gson();
		RunOverview[] fromJson;
		try (Reader r = new FileReader(file)) {
			fromJson = gson.fromJson(r, RunOverview[].class);
			Arrays.sort(fromJson, Collections.reverseOrder());
			return fromJson;
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			log.error("can't parse " + file.getAbsolutePath(), e);
			return new RunOverview[0];
		}
	}

	@Override
	public boolean validate() {
		if (this.selectedChoice == null)
			return false;
		// Try to download the file with interruption
		RemoteFile file = RemoteFile.of(this.selectedChoice);
		if (!file.inCache(false)) {
			try {
				new ProgressMonitorDialog(new Shell()).run(true, true, file);
			} catch (InvocationTargetException | InterruptedException e) {
				Status status = new Status(IStatus.ERROR, this.getClass().getSimpleName(), "Error during downloading: "
						+ selectedChoice, e);
				ErrorDialog.openError(null, "Download Error", "Error during downloading: " + selectedChoice, status);
				Logger.log(status);
			}
		}
		return file.inCache(false);
	}

	@Override
	public IStartupProcedure create() {
		return new LoadProjectStartupProcedure(RemoteFile.of(selectedChoice).getFile().getAbsolutePath(), false);
	}
}
