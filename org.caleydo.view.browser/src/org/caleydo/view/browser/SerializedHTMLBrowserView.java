package org.caleydo.view.browser;

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

	public SerializedHTMLBrowserView(String dataDomainType) {
		super(dataDomainType);
	}

	/** current url of the browser */
	private String url;

	/** pathway query type of the browser */
	private BrowserQueryType queryType;

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	public BrowserQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(BrowserQueryType queryType) {
		this.queryType = queryType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getViewType() {
		return HTMLBrowser.VIEW_ID;
	}

}
