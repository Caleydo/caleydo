package org.jgraph.pad.util;

import java.io.OutputStream;

public class NamedOutputStream {
	private String name;
	private OutputStream outputStream;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public OutputStream getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
