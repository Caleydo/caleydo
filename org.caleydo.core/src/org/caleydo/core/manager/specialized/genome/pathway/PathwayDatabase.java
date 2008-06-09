package org.caleydo.core.manager.specialized.genome.pathway;

import java.io.Serializable;

/**
 * 
 * Class that holds information about a specific pathway database.
 * 
 * @author Marc Streit
 *
 */
public class PathwayDatabase 
implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private EPathwayDatabaseType type;
	
	private String sXMLPath;	
	
	private String sImagePath;
	
	private String sImageMapPath;
	
	public PathwayDatabase(final EPathwayDatabaseType type,
			final String sXMLPath,
			final String sImagePath,
			final String sImageMapPath) {
		
		this.type = type;
		this.sXMLPath = sXMLPath;
		this.sImagePath = sImagePath;
		this.sImageMapPath = sImageMapPath;
	}
	
	public final EPathwayDatabaseType getType() {
		return type;
	}
	
	public final String getName() {
		return type.getName();
	}
	
	public final String getURL() {
		return type.getURL();
	}
	
	public final String getXMLPath() {
		
		assert !sXMLPath.isEmpty() : "Pathway XML path is not set!";
		
		return sXMLPath;
	}
	
	public final String getImagePath() {
		
		assert !sImagePath.isEmpty() : "Pathway image path is not set!";
		
		return sImagePath;
	}
	
	public final String getImageMapPath() {
		
		assert !sImageMapPath.isEmpty() : "Pathway imagemap path is not set!";
		
		return sImageMapPath;
	}
}
