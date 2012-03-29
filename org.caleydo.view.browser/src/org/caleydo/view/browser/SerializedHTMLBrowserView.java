package org.caleydo.view.browser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of a html-browser view.
 * 
 * @author Werner Puff
 * @deprecated This class should be extended from ASerializedView
 */
@Deprecated
@XmlRootElement
@XmlType
public class SerializedHTMLBrowserView extends ASerializedTopLevelDataView {

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
