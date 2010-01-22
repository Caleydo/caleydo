package org.caleydo.core.manager.specialized.genetic.pathway;

import java.io.BufferedReader;

import org.xml.sax.InputSource;

public interface IPathwayResourceLoader {

	public BufferedReader getResource(String file);

	public InputSource getInputSource(String file);
}
