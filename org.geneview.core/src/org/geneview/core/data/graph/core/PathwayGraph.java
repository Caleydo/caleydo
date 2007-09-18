package org.geneview.core.data.graph.core;

import org.geneview.graph.core.Graph;

public class PathwayGraph
extends Graph {
	
	protected int iKeggId;

	protected String sName;
	
	protected String sTitle;

	protected String sImageLink;

	protected String sInformationLink;
	
	public PathwayGraph(
			final int iKeggId,
			final String sName,
			final String sTitle,
			final String sImageLink,
			final String sLink) {

		super(iKeggId);
		
		this.iKeggId = iKeggId;
		this.sName = sName;
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sInformationLink = sLink;

	}
	
	public int getKeggId() {
		
		return iKeggId;
	}
	
	public final String getName() {
		
		return sName;
	}
	
	public final String getTitle() {
		
		return sTitle;
	}
}