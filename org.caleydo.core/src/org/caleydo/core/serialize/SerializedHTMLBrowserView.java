package org.caleydo.core.serialize;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.swt.browser.EBrowserQueryType;

/**
 * Serialized form of a html-browser view. 
 * @author Werner Puff
 */
public class SerializedHTMLBrowserView 
	extends ASerializedView {

	/** current url of the browser */
	private String url;

	/** pathway query type of the browser */ 
	private EBrowserQueryType queryType;
	
	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_VIEW_BROWSER;
	}

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

}
