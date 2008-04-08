package org.caleydo.rcp.action.update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;
import org.caleydo.rcp.Activator;

public class UpdateAction extends Action implements IAction {

	private IWorkbenchWindow window;

	public UpdateAction(IWorkbenchWindow window) {
		this.window = window;
		setId("org.caleydo.update");
		setText("&Update...");
		setToolTipText("Search for updates to Caleydo");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/usearch_obj.gif"));
		window.getWorkbench().getHelpSystem().setHelp(this,
				"org.caleydo.update");
	}

	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
			public void run() {
				UpdateJob job = new UpdateJob("Search for updates and extensions",
						getSearchRequest());
				UpdateManagerUI.openInstaller(window.getShell(), job);
			}
		});
	}

	private UpdateSearchRequest getSearchRequest() {

		UpdateSearchRequest result = new UpdateSearchRequest(
				UpdateSearchRequest.createDefaultSiteSearchCategory(),
				new UpdateSearchScope());
		result.addFilter(new BackLevelFilter());
		result.addFilter(new EnvironmentFilter());
		UpdateSearchScope scope = new UpdateSearchScope();
		try {
			String homeBase = System
					.getProperty("org.caleydo.rcp",
							//"file:/home/mstreit/projects/caleydo/SVN/org.caleydo.update/");
							"http://galactica.icg.tugraz.at/caleydo/");
						
			URL url = new URL(homeBase);
			
			scope.addSearchSite("Caleydo", url, null);

		} catch (MalformedURLException e) {
			// skip bad URLs
		}
		result.setScope(scope);
		return result;
	}
}