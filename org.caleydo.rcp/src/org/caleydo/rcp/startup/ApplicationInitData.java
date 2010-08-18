package org.caleydo.rcp.startup;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.collection.Pair;

/**
 * Data needed for startup process.
 * 
 * @author Marc Streit
 */
public class ApplicationInitData {

	/**
	 * list of serialized-view class to create during startup, the first string is the view, the second the
	 * datadomain
	 */
	private List<Pair<String, String>> appArgumentStartViewWithDataDomain =
		new ArrayList<Pair<String, String>>();

	private ArrayList<String> initializedStartViews = new ArrayList<String>();

	private boolean loadPathways = false;

	public List<Pair<String, String>> getAppArgumentStartViewWithDataDomain() {
		return appArgumentStartViewWithDataDomain;
	}

	public ArrayList<String> getInitializedStartViews() {
		return initializedStartViews;
	}

	public void addStartView(String view, String dataDomain) {
		appArgumentStartViewWithDataDomain.add(new Pair<String, String>(view, dataDomain));
	}

	public void setLoadPathways(boolean loadPathways) {
		this.loadPathways = loadPathways;
	}

	public boolean isLoadPathways() {
		return loadPathways;
	}
}
