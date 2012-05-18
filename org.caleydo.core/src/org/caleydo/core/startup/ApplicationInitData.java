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
package org.caleydo.core.startup;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.util.collection.Pair;

/**
 * Data needed for startup process.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ApplicationInitData {

	/**
	 * list of serialized-view class to create during startup, the first string
	 * is the view, the second the datadomain
	 */
	private List<Pair<String, String>> appArgumentStartViewWithDataDomain = new ArrayList<Pair<String, String>>();

	private ArrayList<String> initializedStartViews = new ArrayList<String>();

	public List<Pair<String, String>> getAppArgumentStartViewWithDataDomain() {
		return appArgumentStartViewWithDataDomain;
	}

	public ArrayList<String> getInitializedStartViews() {
		return initializedStartViews;
	}

	public void addStartView(String view, String dataDomain) {
		appArgumentStartViewWithDataDomain.add(new Pair<String, String>(view, dataDomain));
	}
}
