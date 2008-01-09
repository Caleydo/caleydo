package org.geneview.rcp.action.update;

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
import org.geneview.rcp.Activator;

public class UpdateAction extends Action implements IAction {

	private IWorkbenchWindow window;

	public UpdateAction(IWorkbenchWindow window) {
		this.window = window;
		setId("org.geneview.update");
		setText("&Update...");
		setToolTipText("Search for updates to GeneView");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/usearch_obj.gif"));
		window.getWorkbench().getHelpSystem().setHelp(this,
				"org.geneview.update");
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
					.getProperty("org.geneview.rcp",
							//"file:/home/mstreit/projects/geneview/SVN/org.geneview.update/");
							"http://galactica.icg.tugraz.at/geneview");
						
			URL url = new URL(homeBase);
			
			scope.addSearchSite("GeneView", url, null);

		} catch (MalformedURLException e) {
			// skip bad URLs
		}
		result.setScope(scope);
		return result;
		
//		SSLSocketFactory ssf;
//		TrustManagerFactory tmf;
//		KeyStore ks;
//		FileInputStream fis;
//		String pathKeyStore="C:
//		client.keystore";
//		char[] passphrase = "keystorePassword".toCharArray();
//		fis=new FileInputStream(pathKeyStore);
//		ks = KeyStore.getInstance("JKS");
//		ks.load(fis, passphrase);
//		tmf = TrustManagerFactory.getInstance("SunX509");
//		tmf.init(ks);
//		SSLContext ctx = SSLContext.getInstance("TLS");
//		ctx.init(null, tmf.getTrustManagers(), null);
//		fis.close();
//		try {
//		URL url = new URL("https://yourpage");
//		com.sun.net.ssl.HttpsURLConnection connection = (com.sun.net.ssl.HttpsURLConnection) url.openConnection();
//		ssf = ctx.getSocketFactory();
//		connection.setSSLSocketFactory(ssf);
//		connection.connect();
//		System.out.println("Ok :" + connection.getURL());
	}
}