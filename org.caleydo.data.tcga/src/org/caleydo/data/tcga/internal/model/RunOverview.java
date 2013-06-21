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
package org.caleydo.data.tcga.internal.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
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
		try (Reader r = new FileReader(file)) {
			JsonElement parse = new JsonParser().parse(r);
			this.data = gson.fromJson(parse, Run.class);
		}
		Collections.sort(this.data.details);
		return true;
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

	public boolean isResolved() {
		return data != null;
	}

	public boolean isCompatible() {
		return data != null && GeneralManager.canLoadDataCreatedFor(data.caleydoVersion);
	}
}



