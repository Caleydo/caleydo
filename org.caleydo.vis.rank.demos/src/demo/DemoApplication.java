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
package demo;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author Samuel Gratzl
 *
 */
public class DemoApplication implements IApplication {

	@Override
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	public static class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
		@Override
		public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
			// PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP, true);
			return new ApplicationWorkbenchWindowAdvisor(configurer);
		}

		@Override
		public String getInitialWindowPerspectiveId() {
			return "rankvis.demo.per";
		}
	}

	public static class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

		/**
		 * @param configurer
		 */
		public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
			super(configurer);
		}

		@Override
		public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
			return new ApplicationActionBarAdvisor(configurer);
		}

		@Override
		public void preWindowOpen() {
		    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
			configurer.setInitialSize(new Point(800, 600));
		    configurer.setShowStatusLine(false);
			configurer.setTitle("Caleydo LineUp - Demos");
			configurer.setShowPerspectiveBar(false);
			configurer.setShowMenuBar(false);
			configurer.setShowFastViewBars(false);
			configurer.setShowCoolBar(false);
		}

		@Override
		public void postWindowOpen() {

			IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
			for (IContributionItem item : menuManager.getItems()) {

				if (item.getId().contains("org.caleydo")) {
					menuManager.remove(item);
				}
			}
		}
	}

	public static class ApplicationActionBarAdvisor extends ActionBarAdvisor {
		public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
			super(configurer);
		}

		@Override
		protected void fillMenuBar(IMenuManager menuBar) {
			super.fillMenuBar(menuBar);
			MenuManager menu2 = new MenuManager("Demos", "demos");
			menu2.add(new ShowView("University Rankings 2012", "rankvis.demo.university.mixed"));
			menu2.add(new ShowView("Academic Ranking Of World Universties", "rankvis.demo.university.arwu"));
			menu2.add(new ShowView("Measuring University Performance", "rankvis.demo.university.mup"));
			menu2.add(new ShowView("World University Ranking 2012", "rankvis.demo.university.wur2012"));
			menu2.add(new ShowView("World University Rankings", "rankvis.demo.university.wur"));
			menu2.add(new ShowView("Top 100 under 50 2012", "rankvis.demo.university.top100under50"));
			menu2.add(new ShowView("Food Nutrition", "rankvis.demo.food"));
			menuBar.add(menu2);
		}

	}

	static class ShowView extends ContributionItem implements SelectionListener {
		private String title;
		private String viewId;

		public ShowView(String title, String viewId) {
			this.title = title;
			this.viewId = viewId;
		}
		@Override
		public void fill(Menu menu, int index) {
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(title);
			item.addSelectionListener(this);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
			} catch (PartInitException e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}
	}

	public static class Perspective implements IPerspectiveFactory {

		@Override
		public void createInitialLayout(IPageLayout layout) {
			layout.setEditorAreaVisible(true);
			layout.addView("rankvis.demo.university.mixed", IPageLayout.TOP, IPageLayout.RATIO_MAX,
					IPageLayout.ID_EDITOR_AREA);
			layout.setFixed(true);
		}
	}

}
