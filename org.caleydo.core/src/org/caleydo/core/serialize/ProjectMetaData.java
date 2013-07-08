/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.serialize;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.manager.GeneralManager;

/**
 * a bunch of meta data information with a generic catch all using a map
 * 
 * @author Samuel Gratzl
 * 
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectMetaData {
	private String name;
	@XmlAttribute
	private String version;
	private Date creationDate;

	private final Map<String, String> custom = new TreeMap<>();

	public static ProjectMetaData createDefault() {
		ProjectMetaData m = new ProjectMetaData();
		m.setName("");
		m.setCreationDate(new Date());
		m.setVersion(GeneralManager.VERSION);
		return m;
	}

	public boolean contains(String key) {
		return custom.containsKey(key);
	}

	public Set<String> keys() {
		return custom.keySet();
	}

	public String get(String key) {
		return custom.get(key);
	}

	public void set(String key, String value) {
		custom.put(key, value);
	}

	/**
	 * @return the name, see {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            setter, see {@link name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version, see {@link #version}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            setter, see {@link version}
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the creationDate, see {@link #creationDate}
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            setter, see {@link creationDate}
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
