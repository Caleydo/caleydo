package org.caleydo.view.browser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of a html-browser view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
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
		return HTMLBrowser.VIEW_TYPE;
	}

}
