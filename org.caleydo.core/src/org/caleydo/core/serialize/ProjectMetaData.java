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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.manager.GeneralManager;

/**
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
public class ProjectMetaData {
	private String version;
	private String description;
	private Date creationDate;
	private Map<String, String> custom = new HashMap<>();

	public ProjectMetaData() {

	}

	public static ProjectMetaData createDefault() {
		ProjectMetaData m = new ProjectMetaData();
		m.setVersion(GeneralManager.VERSION);
		m.setCreationDate(new Date());
		return m;
	}

	public boolean contains(String key) {
		return custom.containsKey(key);
	}

	public String get(String key) {
		return custom.get(key);
	}

	public String set(String key, String value) {
		return custom.put(key, value);
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
	 * @return the description, see {@link #description}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            setter, see {@link description}
	 */
	public void setDescription(String description) {
		this.description = description;
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
