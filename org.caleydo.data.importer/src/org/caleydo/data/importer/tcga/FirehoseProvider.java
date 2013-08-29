/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.data.importer.tcga.model.TumorType;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public final class FirehoseProvider {
	private static final Logger log = Logger.getLogger(FirehoseProvider.class.getSimpleName());
	private static final int LEVEL = 4;

	private final TumorType tumor;
	private final String tumorSample;

	private final Date analysisRun;
	private final Date dataRun;

	private final File tmpAnalysisDir;
	private final File tmpDataDir;

	private final Settings settings;

	private final Calendar relevantDate;

	FirehoseProvider(TumorType tumor, Date analysisRun, Date dataRun, Settings settings) {
		this.tumor = tumor;
		this.relevantDate = Calendar.getInstance();
		this.relevantDate.setTime(analysisRun);
		this.tumorSample = guessTumorSample(tumor, this.relevantDate, settings);
		this.analysisRun = analysisRun;
		this.dataRun = dataRun;
		this.settings = settings;
		String tmpDir = settings.getTemporaryDirectory();
		this.tmpAnalysisDir = createTempDirectory(tmpDir, analysisRun, tumor.getName());
		this.tmpDataDir = createTempDirectory(tmpDir, dataRun, tumor.getName());
	}

	/**
	 * logic determining the tumor sample based on the analysis run
	 *
	 * @param tumor
	 * @param date
	 * @return
	 */
	private static String guessTumorSample(TumorType tumor, Calendar cal, Settings settings) {

		if (settings.isAwgRun())
			return tumor.toString();

		if (cal.get(Calendar.YEAR) >= 2013 && tumor.toString().equalsIgnoreCase("SKCM"))
			return tumor + "-TM";
		if (cal.get(Calendar.YEAR) >= 2013 && tumor.toString().equalsIgnoreCase("LAML"))
			return tumor + "-TB";
		if (cal.get(Calendar.YEAR) >= 2013)
			return tumor + "-TP";
		return tumor.toString();
	}

	private String getFileName(String suffix) {
		return tumorSample + suffix;
	}

	private File createTempDirectory(String tmpOutputDirectory, Date run, String tumor) {
		String runId;
		if (run == null)
			runId = "unknown";
		else {
			runId = Settings.formatClean(run);
		}
		return new File(tmpOutputDirectory + runId + System.getProperty("file.separator")
				+ tumor + System.getProperty("file.separator"));
	}

	private Pair<File, Boolean> findStandardSampledClusteredFile(EDataSetType type) {
		return Pair.make(
				extractAnalysisRunFile("outputprefix.expclu.gct", type.getTCGAAbbr() + "_Clustering_CNMF", LEVEL),
				false);
	}

	public Pair<File, Boolean> findRPPAMatrixFile(boolean loadFullGenes) {
		return findStandardSampledClusteredFile(EDataSetType.RPPA);
	}

	public Pair<File, Boolean> findMethylationMatrixFile(boolean loadFullGenes) {
		return findStandardSampledClusteredFile(EDataSetType.methylation);
	}

	public Pair<File, Boolean> findmRNAMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			File r = extractAnalysisRunFile(getFileName(".medianexp.txt"), "mRNA_Preprocess_Median", LEVEL);
			if (r != null)
				return Pair.make(r, true);
		}
		return findStandardSampledClusteredFile(EDataSetType.mRNA);
	}

	public Pair<File, Boolean> findmRNAseqMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			File r = extractDataRunFile(".uncv2.mRNAseq_RSEM_normalized_log2.txt", "mRNAseq_Preprocess", LEVEL);
			if (r == null)
				r = extractDataRunFile(".uncv1.mRNAseq_RPKM_log2.txt",
						"mRNAseq_Preprocess", LEVEL);
			if (r != null)
				return Pair.make(r, true);
		}
		return findStandardSampledClusteredFile(EDataSetType.mRNAseq);
	}

	public Pair<File, Boolean> findmicroRNAMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			File r = extractDataRunFile(".miR_expression.txt", "miR_Preprocess", LEVEL);
			if (r != null)
				return Pair.make(r, true);
		}
		return findStandardSampledClusteredFile(EDataSetType.microRNA);
	}

	public Pair<File, Boolean> findmicroRNAseqMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			File r = extractAnalysisRunFile(getFileName(".uncv2.miRseq_RSEM_normalized_log2.txt"), "miRseq_Preprocess",
					LEVEL);
			if (r == null)
				r = extractAnalysisRunFile(getFileName(".miRseq_RPKM_log2.txt"), "miRseq_Preprocess",
						LEVEL);
			if (r != null)
				return Pair.make(r, true);
		}
		return findStandardSampledClusteredFile(EDataSetType.microRNAseq);
	}

	public File findHiearchicalGrouping(EDataSetType type) {
		return extractAnalysisRunFile(getFileName(".allclusters.txt"), type.getTCGAAbbr()
				+ "_Clustering_Consensus", LEVEL);
	}

	public File findCNMFGroupingFile(EDataSetType type) {
		return extractAnalysisRunFile("cnmf.membership.txt", type.getTCGAAbbr() + "_Clustering_CNMF", LEVEL);
	}

	public File findCopyNumberFile() {
		return extractAnalysisRunFile("all_thresholded.by_genes.txt", "CopyNumber_Gistic2", LEVEL);
	}

	public File findClinicalDataFile() {
		return extractDataRunFile(".clin.merged.txt", "Merge_Clinical", 1);
	}

	public Pair<File, Integer> findMutationFile() {
		int startColumn = 8;
		File mutationFile = null;
		if (relevantDate.get(Calendar.YEAR) < 2013) { // test only for the <= 2012
			mutationFile = extractAnalysisRunFile(getFileName(".per_gene.mutation_counts.txt"),
					"Mutation_Significance", LEVEL);

			if (mutationFile == null)
				mutationFile = extractAnalysisRunFile(getFileName(".per_gene.mutation_counts.txt"), "MutSigRun2.0",
						LEVEL);
		}
		if (mutationFile == null) {
			// TODO always the -TP version
			File maf = null;
			if ( !this.settings.isAwgRun() ) {
				maf = extractAnalysisRunFile(tumor + "-TP.final_analysis_set.maf",
						"MutSigNozzleReport2.0", LEVEL);
			}
			else {
				maf = extractAnalysisRunFile(tumor + ".final_analysis_set.maf",
						"MutSigNozzleReport2.0", LEVEL);
			}
			if (maf != null) {
				mutationFile = parseMAF(maf);
				startColumn = 1;
			}
		}
		return Pair.make(mutationFile, startColumn);
	}

	/**
	 * @return
	 */
	public String getReportURL() {
		return settings.getReportUrl(analysisRun, tumor);
	}

	private File extractAnalysisRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, true, false);
	}

	private File extractDataRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, false, true);
	}

	private File extractFile(String fileName, String pipelineName, int level, boolean isAnalysisRun, boolean hasTumor) {
		Date id = isAnalysisRun ? analysisRun : dataRun;

		String label = "unknown";
		// extract file to temp directory and return path to file
		URL url;
		try {
			if (isAnalysisRun)
				url = settings.getAnalysisURL(id, tumor, tumorSample, pipelineName, level);
			else
				url = settings.getDataURL(id, tumor, tumorSample, pipelineName, level);
			String urlString = url.getPath();
			label = urlString.substring(urlString.lastIndexOf('/') + 1, urlString.length());
			File outputDir = new File(isAnalysisRun ? tmpAnalysisDir : tmpDataDir, label);
			outputDir.mkdirs();

			return extractFileFromTarGzArchive(url, fileName, outputDir, hasTumor);
		} catch (MalformedURLException e) {
			throw new RuntimeException("can't extract " + fileName + " from " + label, e);
		}
	}

	private File extractFileFromTarGzArchive(URL inUrl, String fileToExtract, File outputDirectory, boolean hasTumor) {
		log.info("downloading: " + inUrl);
		File targetFile = new File(outputDirectory, fileToExtract);

		// use cached
		if (targetFile.exists() && !settings.isCleanCache())
			return targetFile;

		File notFound = new File(outputDirectory, fileToExtract + "-notfound");
		if (notFound.exists() && !settings.isCleanCache()) {
			log.warning("file not found in a previous run: " + inUrl);
			return null;
		}

		if (hasTumor) {
			fileToExtract = "/" + tumor + fileToExtract;
		}

		TarArchiveInputStream tarIn = null;
		OutputStream out = null;
		try {
			System.out.println("I: Extracting " + fileToExtract + " from " + inUrl + ".");
			InputStream in = new BufferedInputStream(inUrl.openStream());

			// ok we have the file
			tarIn = new TarArchiveInputStream(new GZIPInputStream(in));

			// search the correct entry
			ArchiveEntry act = tarIn.getNextEntry();
			while (act != null && !act.getName().endsWith(fileToExtract)) {
				act = tarIn.getNextEntry();
			}
			if (act == null) // no entry found
				throw new FileNotFoundException("no entry named: " + fileToExtract + " found");

			log.info("extracting: " + fileToExtract + " from " + inUrl);
			byte[] buf = new byte[4096];
			int n;
			targetFile.getParentFile().mkdirs();
			// use a temporary file to recognize if we have aborted between run
			String tmpFile = targetFile.getAbsolutePath() + ".tmp";
			out = new BufferedOutputStream(new FileOutputStream(tmpFile));
			while ((n = tarIn.read(buf, 0, 4096)) > -1)
				out.write(buf, 0, n);
			out.close();
			Files.move(new File(tmpFile).toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("I: Extracted " + fileToExtract + " from " + inUrl + ".");
			return targetFile;
		} catch (FileNotFoundException e) {
			log.warning("Unable to extract " + fileToExtract + " from " + inUrl + ". " + "file not found");
			// file was not found, create a marker to remember this for quicker checks
			notFound.getParentFile().mkdirs();
			try {
				notFound.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		} catch (Exception e) {
			log.warning("Unable to extract " + fileToExtract + " from " + inUrl + ". " + e.getMessage());
			return null;
		} finally {
			if (tarIn != null)
				try {
					tarIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private static File parseMAF(File maf) {

		File out = new File(maf.getParentFile(), "P" + maf.getName());
		if (out.exists())
			return out;
		System.out.println("parsing maf file " + maf.getAbsolutePath());
		final String TAB = "\t";

		try (BufferedReader reader = Files.newBufferedReader(maf.toPath(), Charset.defaultCharset())) {
			List<String> header = Arrays.asList(reader.readLine().split(TAB));
			int geneIndex = header.indexOf("Hugo_Symbol");
			int sampleIndex = header.indexOf("Tumor_Sample_Barcode");
			// gene x sample x mutated
			Table<String, String, Boolean> mutated = TreeBasedTable.create();
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split(TAB);
				mutated.put(columns[geneIndex], columns[sampleIndex], Boolean.TRUE);
			}

			File tmp = new File(out.getParentFile(), out.getName() + ".tmp");
			PrintWriter w = new PrintWriter(tmp);
			w.append("Hugo_Symbol");
			List<String> cols = new ArrayList<>(mutated.columnKeySet());
			for (String sample : cols) {
				w.append(TAB).append(sample);
			}
			w.println();
			Set<String> rows = mutated.rowKeySet();
			System.out.println(mutated.size() + " " + rows.size() + " " + cols.size());
			for (String gene : rows) {
				w.append(gene);
				for (String sample : cols) {
					w.append(TAB).append(mutated.contains(gene, sample) ? '1' : '0');
				}
				w.println();
			}
			w.close();
			Files.move(tmp.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("parsed " + maf.getAbsolutePath());
			return out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		File file = new File(
				"/home/alexsb/Dropbox/Caleydo/data/ccle/CCLE_hybrid_capture1650_hg19_NoCommonSNPs_CDS_2012.05.07.maf");
		file = parseMAF(file);

	}

}
