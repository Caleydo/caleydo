package org.caleydo.view.browser;

import org.caleydo.core.manager.event.view.browser.EBrowserQueryType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a html-browser view.
 * 
 * @author Werner Puff
 */
public class SerializedHTMLBrowserView extends ASerializedView {

	public SerializedHTMLBrowserView() {
	}

	public SerializedHTMLBrowserView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	/** current url of the browser */
	private String url;

	/** pathway query type of the browser */
	private EBrowserQueryType queryType;

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	public EBrowserQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(EBrowserQueryType queryType) {
		this.queryType = queryType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getViewGUIID() {
		return HTMLBrowser.VIEW_ID;
	}

}
