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
