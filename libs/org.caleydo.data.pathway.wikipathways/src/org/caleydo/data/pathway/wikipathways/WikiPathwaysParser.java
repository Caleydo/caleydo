package org.caleydo.data.pathway.wikipathways;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bridgedb.Xref;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.IPathwayParser;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.IPathwayResourceLoader;
import org.caleydo.datadomain.pathway.manager.PathwayDatabase;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.GpmlFormat;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayImporter;

public class WikiPathwaysParser implements IPathwayParser {

	/**
	 * Maps wikipathway db names to idtypes in caleydo.
	 */
	private Map<String, String> dbNameMap = new HashMap<>();

	public WikiPathwaysParser() {
		dbNameMap.put("Ensembl Mouse", "ENSEMBL_GENE_ID");
		dbNameMap.put("Entrez Gene", "ENTREZ_GENE_ID");
		dbNameMap.put("RefSeq", "REFSEQ_MRNA");
		// dbNameMap.put("EC Number", "EC_NUMBER");
	}

	@Override
	public void parse() {
		PathwayManager pathwayManager = PathwayManager.get();

		IPathwayResourceLoader resourceLoader = pathwayManager
				.getPathwayResourceLoader(EPathwayDatabaseType.WIKIPATHWAYS);
		PathwayDatabase pathwayDatabase = pathwayManager.getPathwayDatabaseByType(EPathwayDatabaseType.WIKIPATHWAYS);

		Organism organism = GeneralManager.get().getBasicInfo().getOrganism();
		String pathwayListFileName = null;

		if (organism == Organism.HOMO_SAPIENS) {
			pathwayListFileName = PathwayListGenerator.LIST_FILE_WIKIPATHWAYS_HOMO_SAPIENS;
		} else if (organism == Organism.MUS_MUSCULUS) {
			pathwayListFileName = PathwayListGenerator.LIST_FILE_WIKIPATHWAYS_MUS_MUSCULUS;
		} else {
			throw new IllegalStateException("Cannot load pathways from organism " + organism);
		}

		BufferedReader pathwayListFile = resourceLoader.getResource(pathwayListFileName);
		String line = null;

		try {
			File tmpPathwayFile = File.createTempFile("tmppathway", "gpml");

			while ((line = pathwayListFile.readLine()) != null) {
				String[] tokens = line.split("\\s");

				Files.copy(resourceLoader.getInputSource(pathwayDatabase.getXMLPath() + tokens[0]).getByteStream(),
						tmpPathwayFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				PathwayImporter importer = new GpmlFormat();
				Pathway pathway = importer.doImport(tmpPathwayFile);
				createPathwayGraph(pathway, tokens[0].substring(0, tokens[0].length() - 5), Integer.valueOf(tokens[1])
						.intValue(), Integer.valueOf(tokens[2]).intValue());

			}
		} catch (IOException | ConverterException e) {
			throw new IllegalStateException("Error reading pathway list file " + pathwayListFileName);
		} finally {
			if (pathwayListFile != null) {
				try {
					pathwayListFile.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Creates a {@link PathwayGraph} object for the specified pathway.
	 *
	 * @param pathway
	 *            Pathway a graph shall be created for.
	 * @param imageName
	 *            FileName of the image without a preceeding path or file extension, e.g., "filename". PNG format is
	 *            assumed.
	 * @param pixelWidth
	 *            Width of the image in pixels.
	 * @param pixelHeight
	 *            Height of the image in pixels.
	 */
	private void createPathwayGraph(Pathway pathway, String imageFileName, int pixelWidth, int pixelHeight) {
		PathwayManager pathwayManager = PathwayManager.get();
		PathwayItemManager pathwayItemManager = PathwayItemManager.get();

		PathwayGraph pathwayGraph = pathwayManager.createPathway(EPathwayDatabaseType.WIKIPATHWAYS, imageFileName,
				pathway.getMappInfo().getMapInfoName(), imageFileName + ".png", "");
		pathwayGraph.setWidth(pixelWidth);
		pathwayGraph.setHeight(pixelHeight);

		// Height and width specified in gpml file
		double boardDimensions[] = pathway.getMBoardSize();
		int boardWidth = (int) boardDimensions[0];
		int boardHeight = (int) boardDimensions[1];

		IDMappingManager genomeIdManager = ((PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE)).getGeneIDMappingManager();

		for (PathwayElement element : pathway.getDataObjects()) {
			if (element.getObjectType() == ObjectType.DATANODE) {
				Xref xref = element.getXref();
				String label = element.getTextLabel();
				if (xref != null && xref.getDataSource() != null && xref.getId() != null && !xref.getId().isEmpty()) {
					String idType = dbNameMap.get(xref.getDataSource().getFullName());
					if (idType != null) {
						IDType sourceIDType = IDType.getIDType(idType);
						if (sourceIDType != null) {

							Set<Integer> davidIDs = null;
							if (sourceIDType.getDataType() == EDataType.INTEGER) {
								try {
									davidIDs = genomeIdManager.getIDAsSet(sourceIDType, IDType.getIDType("DAVID"),
											Integer.valueOf(xref.getId()));
								} catch (NumberFormatException e) {
									continue;
								}
							} else {
								davidIDs = genomeIdManager.getIDAsSet(sourceIDType, IDType.getIDType("DAVID"),
										xref.getId());
							}

							if (davidIDs == null) {
								Logger.log(new Status(IStatus.INFO, this.toString(), "No david mapping for " + idType
										+ " ID: " + xref.getId()));
								continue;
							}

							ArrayList<PathwayVertex> vertices = pathwayItemManager.createGeneVertex(label, xref
									.getDataSource().getType(), "", davidIDs);

							// float xScaling = (float) pixelWidth / (float) boardWidth;
							// float yScaling = (float) pixelHeight / (float) boardHeight;

							pathwayItemManager.createVertexRep(pathwayGraph, vertices, label, "rectangle",
									(short) (element.getMCenterX()), (short) (element.getMCenterY()),
									(short) (element.getMWidth()), (short) (element.getMHeight()));
						}
					}
				}
			}
		}
	}

}
