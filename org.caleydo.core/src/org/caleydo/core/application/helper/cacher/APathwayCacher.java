package org.caleydo.core.application.helper.cacher;

import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.event.JobChangedEvent;

/**
 * Abstract class for fetch tool
 * 
 * @author Marc Streit
 */
public abstract class APathwayCacher
	extends Thread {
	
	/**
	 * Needed for async access to set progress bar state
	 */
	protected Display display;

	protected ProgressBar progressBar;

	protected CmdFetchPathwayData triggeringCommand;

	protected EOrganism eOrganism;
	
	protected String sFetchURL;
	
	protected int iDownloadCount = 0;

	protected boolean bEnableProxy = false;
	private String sProxyServer;
	private int iProxyPort;

	protected int iExpectedDownloads = 0;

	protected void processJobs(final Dispatcher dispatcher) {
		dispatcher.getEventManager().registerObserver(new EventObserver() {
			@Override
			public void processEvent(Event arg0) {
				if (arg0 instanceof JobChangedEvent
					&& ((JobChangedEvent) arg0).getJob().getState() == Job.STATE_FINISHED) {
					iDownloadCount++;

					if (progressBar.isDisposed()) {
//						dispatcher.getEventManager().shutdown();
//						dispatcher.getWorkerPool().abortBusyWorker();
						dispatcher.stop();
						
						while(dispatcher.getJobManager().getNextOpenJob() != null)
							dispatcher.getJobManager().removeJob(dispatcher.getJobManager().getNextOpenJob());
//						
//						for (JobL job : dispatcher.getJobManager().getJobList())
//							
//						dispatcher.getJobManager().removeJob(arg0)
						return;
					}
							
					progressBar.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (progressBar.isDisposed())
								return;

							progressBar.setSelection((int) (iDownloadCount * 100f / iExpectedDownloads));

							// System.out.println("Download count: "
							// +iDownloadCount);
							// System.out.println("Percentage: "
							// +(int)(iDownloadCount * 100f /
							// EXPECTED_DOWNLOADS));
						}
					});
				}
			}
		});

		createProxySettings(dispatcher);

		// start the dispatcher
		dispatcher.processJobs();
	}

	protected abstract void triggerPathwayListGeneration();

	public void setProxySettings(String sProxyServer, int iProxyPort) {
		this.sProxyServer = sProxyServer;
		this.iProxyPort = iProxyPort;

		bEnableProxy = true;
	}

	public void createProxySettings(Dispatcher dispatcher) {

		if (bEnableProxy == false)
			return;

		// get and create http retriever configuration
		HttpRetrieverConfiguration httpConfiguration =
			(HttpRetrieverConfiguration) dispatcher.getContext().getContextParameter(
				HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);

		if (httpConfiguration == null) {
			httpConfiguration = new HttpRetrieverConfiguration();
			dispatcher.getContext().setContextParameter(
				HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION, httpConfiguration);
		}

		httpConfiguration.setProxyEnabled(true);
		httpConfiguration.setProxyServer(sProxyServer);
		httpConfiguration.setProxyPort(iProxyPort);
	}
}
