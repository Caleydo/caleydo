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
package org.caleydo.view.browser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.browser.listener.ChangeURLListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Simple HTML browser.
 * 
 * @author Marc Streit
 */
public class HTMLBrowser extends ASWTView {
	public static String VIEW_TYPE = "org.caleydo.view.browser";
	
	public static String VIEW_NAME = "Tissue Browser";

	public final static String CALEYDO_HOME = "http://www.caleydo.org";

	protected Browser browser;

	/**
	 * Subclasses can add widgets to this composite which appear before the
	 * browser icons.
	 */
	protected Composite subContributionComposite;

	protected String url = CALEYDO_HOME;// + "/help/user_interface.html";

	protected Text textURL;

	// protected IDExtractionLocationListener idExtractionLocationListener;

	private ToolItem goButton;
	private ToolItem homeButton;
	private ToolItem backButton;
	private ToolItem stopButton;

	private ChangeURLListener changeURLListener;

	// private boolean makeRegularScreenshots = false;

	private Runnable timer = null;

	/**
	 * Constructor.
	 */
	public HTMLBrowser(Composite parentComposite) {
		super(GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.VIEW_SWT_BROWSER_GENERAL), parentComposite, VIEW_TYPE, VIEW_NAME);
	}

	/**
	 * Constructor.
	 */
	public HTMLBrowser(int viewID, Composite parentComposite) {
		super(viewID, parentComposite, VIEW_TYPE, VIEW_NAME);

		parentComposite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				unregisterEventListeners();
			}
		});
	}

	@Override
	public void draw() {

		Composite browserBarComposite = new Composite(parentComposite, SWT.NONE);
		browserBarComposite.setLayout(new GridLayout(3, false));

		subContributionComposite = new Composite(browserBarComposite, SWT.NONE);
		subContributionComposite.setLayout(new FillLayout());

		ToolBar toolbar = new ToolBar(browserBarComposite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_VERTICAL);
		// toolbar.setLayoutData(data);

		ResourceLoader resourceLoader = GeneralManager.get().getResourceLoader();

		goButton = new ToolItem(toolbar, SWT.PUSH);
		goButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
				EIconTextures.BROWSER_REFRESH_IMAGE.getFileName()));

		backButton = new ToolItem(toolbar, SWT.PUSH);
		backButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
				EIconTextures.BROWSER_BACK_IMAGE.getFileName()));

		stopButton = new ToolItem(toolbar, SWT.PUSH);
		stopButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
				EIconTextures.BROWSER_STOP_IMAGE.getFileName()));

		homeButton = new ToolItem(toolbar, SWT.PUSH);
		homeButton.setImage(resourceLoader.getImage(parentComposite.getDisplay(),
				EIconTextures.BROWSER_HOME_IMAGE.getFileName()));

		textURL = new Text(browserBarComposite, SWT.BORDER);

		if (checkInternetConnection()) {
			textURL.setText(url);
		}

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 15;
		textURL.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.heightHint = 45;
		browserBarComposite.setLayoutData(data);

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!checkInternetConnection())
					return;

				ToolItem item = (ToolItem) event.widget;
				if (item.equals(backButton)) {
					browser.back();
				} else if (item.equals(stopButton)) {
					browser.stop();
				} else if (item.equals(goButton)) {
					url = textURL.getText();
				} else if (item.equals(homeButton)) {
					url = "www.caleydo.org";
					textURL.setText(CALEYDO_HOME);
					browser.setUrl(url);
				}
			}
		};

		goButton.addListener(SWT.Selection, listener);
		backButton.addListener(SWT.Selection, listener);
		stopButton.addListener(SWT.Selection, listener);
		homeButton.addListener(SWT.Selection, listener);

		textURL.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				url = textURL.getText();
				browser.setUrl(url);
			}
		});

		parentComposite.getDisplay().addFilter(SWT.FocusIn, new Listener() {

			@Override
			public void handleEvent(Event event) {

				if (!event.widget.getClass().equals(this.getClass()))
					return;
			}
		});

		browser = new Browser(parentComposite, SWT.BORDER);

		// idExtractionLocationListener = new
		// IDExtractionLocationListener(browser, uniqueID, -1);
		// browser.addLocationListener(idExtractionLocationListener);

		data = new GridData();
		browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Logger.log(new Status(IStatus.INFO, this.toString(), "Load " + url));

		try {
			parentComposite.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (!checkInternetConnection())
						return;

					textURL.setText(url);
					browser.setUrl(url);
					// browser.refresh();
				}
			});
		} catch (SWTException swte) {
			Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
					"Error while loading " + url, swte));
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;

		browser.setUrl(url);
		textURL.setText(url);
		browser.update();
	}

	protected boolean checkInternetConnection() {
		// Check internet connection
		try {
			InetAddress.getByName("www.google.at");

		} catch (UnknownHostException e) {
			textURL.setText("No internet connection available!");
			return false;
		}

		return true;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		changeURLListener = new ChangeURLListener();
		changeURLListener.setHandler(this);
		eventPublisher.addListener(ChangeURLEvent.class, changeURLListener);

	}

	/**
	 * Registers the listeners for this view to the event system. To release the
	 * allocated resources unregisterEventListeners() has to be called.
	 */
	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (changeURLListener != null) {
			eventPublisher.removeListener(ChangeURLEvent.class, changeURLListener);
			changeURLListener = null;
		}
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release
	 * the allocated resources unregisterEventListenrs() has to be called.
	 */
	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// this implementation does not initialize anything yet
	}

	private void makeScreenshot() {

		// Make screenshot of browser
		Image screenshot = new Image(browser.getShell().getDisplay(), browser.getShell()
				.getBounds());
		GC gc = new GC(browser.getShell().getDisplay());
		gc.copyArea(screenshot, 743, 143);

		gc.dispose();

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { screenshot.getImageData() };
		loader.save(GeneralManager.CALEYDO_HOME_PATH + "browser.png", SWT.IMAGE_PNG);

		// System.out.println("Write screenshot");

		screenshot.dispose();
	}

	public void makeRegularScreenshots(boolean makeRegularScreenshots) {

		if (makeRegularScreenshots) {
			final int time = 1000;

			if (timer != null) {
				browser.getDisplay().timerExec(-1, timer);
				timer = null;
			}

			timer = new Runnable() {
				@Override
				public void run() {
					browser.getDisplay().timerExec(time, this);
					makeScreenshot();
				}
			};
			browser.getDisplay().timerExec(time, timer);

		} else {
			if (timer != null)
				browser.getDisplay().timerExec(-1, timer);
		}
	}
}
