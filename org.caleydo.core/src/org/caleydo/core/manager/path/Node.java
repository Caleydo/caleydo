package org.caleydo.core.manager.path;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.canvas.AGLView;

public class Node {

	String dataDomainType;
	String viewType;

	ArrayList<AGLView> views;

	public Node(String dataDomainType, String viewType) {
		this.dataDomainType = dataDomainType;
		this.viewType = viewType;
		views = new ArrayList<AGLView>();
	}

	@Override
	public String toString() {
		return "[" + dataDomainType + ":" + viewType + "]";
	}

	public String getDataDomainType() {
		return dataDomainType;
	}

	public String getViewType() {
		return viewType;
	}

	public void addView(AGLView view) {
		views.add(view);
	}

	public boolean containsView(AGLView view) {
		return views.contains(view);
	}
}
