package org.jgraph.pad.util;

import java.io.InputStream;

public class NamedInputStream {
	private String name;
	private InputStream inputStream;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
