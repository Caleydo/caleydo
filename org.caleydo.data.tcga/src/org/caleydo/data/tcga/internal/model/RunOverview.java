/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.tcga.internal.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.RemoteFile;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * @author Samuel Gratzl
 *
 */
public class RunOverview implements Comparable<RunOverview> {
	private String label;
	private String id;
	private String json;

	private transient Run data;

	public boolean resolve(Gson gson, String prefix) throws JsonSyntaxException, JsonIOException, IOException {
		if (data != null)
			return true;
		File file = RemoteFile.of(new URL(prefix + json)).getOrLoad(true, new NullProgressMonitor());
		if (file == null)
			return false;
		try (Reader r = Files.newBufferedReader(file.toPath(), Charset.defaultCharset())) {
			JsonElement parse = new JsonParser().parse(r);
			this.data = gson.fromJson(parse, Run.class);
		}
		Collections.sort(this.data.details);
		return true;
	}

	/**
	 * @param json
	 *            setter, see {@link json}
	 */
	public void setJson(String json) {
		this.json = json;
	}

	/**
	 * @param label
	 *            setter, see {@link label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param id
	 *            setter, see {@link id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the json, see {@link #json}
	 */
	public String getJson() {
		return json;
	}

	@Override
	public String toString() {
		return label;
	}

	/**
	 * @param index
	 * @return
	 */
	public TumorProject getProject(int index) {
		if (data == null)
			return null;
		return data.details.get(index);
	}

	/**
	 * @return
	 */
	public int size() {
		return data == null ? 0 : data.details.size();
	}

	@Override
	public int compareTo(RunOverview o) {
		return id.compareTo(o.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RunOverview other = (RunOverview) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean isResolved() {
		return data != null;
	}

	public boolean isCompatible() {
		return data != null && GeneralManager.canLoadDataCreatedFor(data.caleydoVersion);
	}
}



