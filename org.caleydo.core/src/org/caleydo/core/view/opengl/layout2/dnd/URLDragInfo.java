/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;


/**
 * a special {@link IDragInfo} for transferring a url
 * 
 * @author Samuel Gratzl
 * 
 */
public class URLDragInfo implements IDragInfo {
	private final String url;

	public URLDragInfo(String url) {
		this.url = url;
	}

	/**
	 * @return the url, see {@link #url}
	 */
	public String getUrl() {
		return url;
	}

	@Override
	public String getLabel() {
		return url;
	}
}
