/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.pathway.kegg;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.RemoteFile;
import org.caleydo.datadomain.genetic.GeneticMetaData;
import org.caleydo.datadomain.genetic.Organism;
import org.caleydo.datadomain.pathway.IPathwayLoader;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.io.Files;

/**
 * @author Samuel Gratzl
 *
 */
public class KEGGParser implements IRunnableWithProgress, IPathwayLoader {
	private static final Logger log = Logger.create(KEGGParser.class);

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		final String organismKey = toKEGGOrganism(GeneticMetaData.getOrganism());

		SubMonitor m = SubMonitor.convert(monitor);
		m.setTaskName("Caching Pathways (this may take a while)");
		RemoteFile listRFile = RemoteFile.of(toURL("http://rest.kegg.jp/list/pathway/" + organismKey), ".txt");
		File listFile = listRFile.getOrLoad(false, monitor);

		if (listFile == null || !listFile.exists()) {
			log.error("can't load list data from: " + listRFile.getUrl());
			return;
		}
		log.info("Start parsing KEGG pathways.");

		List<String> lines;
		try {
			lines = Files.readLines(listFile, Charset.forName("UTF-8"));
		} catch (IOException e) {
			log.error("can't read list data of " + listFile, e);
			return;
		}

		final String format = "Caching Pathways (this may take a while): Downloading KEGG pathway %s (%d of %d)";
		m.beginTask("Caching Pathways (this may take a while)", lines.size() * 20);
		int i = 0;
		for (String line : lines) {
			String pathwayName = line.substring(5, 13);

			SubMonitor subsub = m.newChild(20, SubMonitor.SUPPRESS_SUBTASK);
			subsub.beginTask(String.format(format, pathwayName, i++, lines.size()), 20);

			RemoteFile imageRFile = RemoteFile.of(toURL("http://rest.kegg.jp/get/" + pathwayName + "/image"), ".png");
			RemoteFile kgmlRFile = RemoteFile.of(toURL("http://rest.kegg.jp/get/" + pathwayName + "/kgml"), ".kgml");
			if (!imageRFile.inCache(false))
				log.info("downloading: " + imageRFile.getUrl());
			imageRFile.getOrLoad(false, subsub.newChild(10));
			if (!kgmlRFile.inCache(false))
				log.info("downloading: " + kgmlRFile.getUrl());
			kgmlRFile.getOrLoad(false, subsub.newChild(10));
		}
		log.info("Finished parsing KEGG pathways.");
		m.done();
	}

	@Override
	public void parse(EPathwayDatabaseType type) {
		// // Try reading list of files directly from local hard dist
		// File folder = new File(sXMLPath);
		// File[] arFiles = folder.listFiles();

		StringBuilder idMappingErrors = new StringBuilder();

		final String organismKey = toKEGGOrganism(GeneticMetaData.getOrganism());
		final IProgressMonitor monitor = new NullProgressMonitor();

		RemoteFile listRFile = RemoteFile.of(toURL("http://rest.kegg.jp/list/pathway/" + organismKey), ".txt");
		File listFile = listRFile.getOrLoad(false, monitor);

		if (listFile == null || !listFile.exists()) {
			log.error("can't load list data from: " + listRFile.getUrl());
			return;
		}
		log.info("Start parsing " + type.getName() + " pathways.");

		List<String> lines;
		try {
			lines = Files.readLines(listFile, Charset.forName("UTF-8"));
		} catch (IOException e) {
			log.error("can't read list data of " + listFile, e);
			return;
		}

		for (String line : lines) {
			String pathwayName = line.substring(5, 13);

			RemoteFile imageRFile = RemoteFile.of(toURL("http://rest.kegg.jp/get/" + pathwayName + "/image"), ".png");
			RemoteFile kgmlRFile = RemoteFile.of(toURL("http://rest.kegg.jp/get/" + pathwayName + "/kgml"), ".kgml");
			if (!imageRFile.inCache(false))
				log.info("downloading: " + imageRFile.getUrl());
			File imageFile = imageRFile.getOrLoad(false, monitor);
			if (!kgmlRFile.inCache(false))
				log.info("downloading: " + kgmlRFile.getUrl());
			File kgmlFile = kgmlRFile.getOrLoad(false, monitor);

			if (imageFile == null || kgmlFile == null) {
				log.error("can't load: " + pathwayName + " -> skipping");
				continue;
			}

			Dimension imageSize = determineImageSize(imageFile);
			if (imageSize == null) {
				continue;
			}

			try (Reader r = Files.newReader(kgmlFile, Charset.forName("UTF-8"))) {
				XMLReader reader = XMLReaderFactory.createXMLReader();

				// Entity resolver avoids the XML Reader
				// to check external DTDs.
				reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

				KgmlSaxHandler kgmlParser = new KgmlSaxHandler(type, imageFile, idMappingErrors);
				reader.setEntityResolver(kgmlParser);
				reader.setContentHandler(kgmlParser);

				reader.parse(new InputSource(r));

				PathwayGraph currentPathwayGraph = kgmlParser.getCurrentPathway();

				currentPathwayGraph.setWidth(imageSize.width);
				currentPathwayGraph.setHeight(imageSize.height);

				int iImageWidth = currentPathwayGraph.getWidth();
				int iImageHeight = currentPathwayGraph.getHeight();

				if (iImageWidth == -1 || iImageHeight == -1) {
					log.info("Pathway texture width=" + iImageWidth + " / height=" + iImageHeight);
				}
			} catch (java.lang.NumberFormatException e) {
				log.error("ID Parsing error in " + pathwayName, e);
			} catch (SAXException e) {
				log.error("SAXParser-error during parsing file " + pathwayName + ".\n SAX error: " + e.toString(), e);
			} catch (FileNotFoundException e) {
				log.error("kgml file not found: " + kgmlFile, e);
			} catch (IOException e) {
				log.error("kgml file read error: " + kgmlFile, e);
			}
		}
		if (idMappingErrors.length() > 0) {
			String message = "Failed to parse the following Entrez IDs while parsing KEGG:\n "
					+ idMappingErrors.toString();
			log.info(message);
		}
		log.info("Finished parsing " + type.getName() + " pathways.");
	}

	/**
	 * @param imageFile
	 * @return
	 */
	private static Dimension determineImageSize(File imageFile) {
		// http://stackoverflow.com/questions/1559253/java-imageio-getting-image-dimension-without-reading-the-entire-file
		try (ImageInputStream in = ImageIO.createImageInputStream(imageFile)) {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0), reader.getHeight(0));
				} finally {
					reader.dispose();
				}
			}
		} catch (IOException e) {
			log.error("can't resolve image size of " + imageFile, e);
		}
		return null;
	}

	/**
	 * @param string
	 * @return
	 */
	private static URL toURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalStateException("invalid url: " + url, e);
		}
	}

	/**
	 * @param organism
	 * @return
	 */
	private static String toKEGGOrganism(Organism organism) {
		switch (organism) {
		case HOMO_SAPIENS:
			return "hsa";
		case MUS_MUSCULUS:
			return "mmu";
		}
		throw new IllegalStateException();
	}
}
