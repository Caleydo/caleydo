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
import java.io.FileReader;
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
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang.SystemUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.data.importer.tcga.model.TumorType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.common.io.Closeables;

public final class FirehoseProvider {
	private static final Logger log = Logger.getLogger(FirehoseProvider.class.getName());
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

	/**
	 * @return
	 */
	public boolean is2014Run() {
		return relevantDate.get(Calendar.YEAR) >= 2014;
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
		return new File(tmpOutputDirectory + runId + SystemUtils.FILE_SEPARATOR + tumor + SystemUtils.FILE_SEPARATOR);
	}

	private Pair<TCGAFileInfo, Boolean> findStandardSampledClusteredFile(EDataSetType type) {
		return Pair.make(
				extractAnalysisRunFile("outputprefix.expclu.gct", type.getTCGAAbbr() + "_Clustering_CNMF", LEVEL),
				false);
	}

	public Pair<TCGAFileInfo, Boolean> findRPPAMatrixFile(boolean loadFullGenes) {
		return findStandardSampledClusteredFile(EDataSetType.RPPA);
	}

	public Pair<TCGAFileInfo, Boolean> findMethylationMatrixFile(boolean loadFullGenes) {
		return findStandardSampledClusteredFile(EDataSetType.methylation);
	}

	public Pair<TCGAFileInfo, Boolean> findmRNAMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			TCGAFileInfo r = extractAnalysisRunFile(getFileName(".medianexp.txt"), "mRNA_Preprocess_Median", LEVEL);
			if (r != null)
				return Pair.make(r, true);
		}
		return findStandardSampledClusteredFile(EDataSetType.mRNA);
	}

	public Pair<TCGAFileInfo, Boolean> findmRNAseqMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			TCGAFileInfo r = extractDataRunFile(".uncv2.mRNAseq_RSEM_normalized_log2.txt", "mRNAseq_Preprocess", LEVEL);
			if (r == null)
				r = extractDataRunFile(".uncv1.mRNAseq_RPKM_log2.txt",
						"mRNAseq_Preprocess", LEVEL);
			if (r == null)
				r = extractDataRunFile(".mRNAseq_RPKM_log2.txt", "mRNAseq_Preprocess", LEVEL);
			if (r != null) {
				r = filterColumns(r, findStandardSampledClusteredFile(EDataSetType.mRNAseq));
				return Pair.make(r, true);
			}
		}
		return findStandardSampledClusteredFile(EDataSetType.mRNAseq);
	}

	private TCGAFileInfo filterColumns(TCGAFileInfo full, Pair<TCGAFileInfo, Boolean> sampled) {
		File in = full.getFile();
		File out = new File(in.getParentFile(), "F" + in.getName());
		TCGAFileInfo r = new TCGAFileInfo(out, full.getArchiveURL(), full.getSourceFileName());
		if (out.exists() && !settings.isCleanCache())
			return r;
		assert full != null;
		if (sampled == null || sampled.getFirst() == null) {
			log.severe("can't filter the full gene file: " + in + " - sampled not found");
			return full;
		}
		// full: 1row, 2col
		// sampled: 3row, 3col
		Set<String> good = readGoodSamples(sampled.getFirst().getFile());
		if (good == null)
			return full;
		try (BufferedReader fin = new BufferedReader(new FileReader(in)); PrintWriter w = new PrintWriter(out)) {
			String[] header = fin.readLine().split("\t");
			BitSet bad = filterCols(header, good);
			{
				StringBuilder b = new StringBuilder();
				for (int i = bad.nextSetBit(0); i >= 0; i = bad.nextSetBit(i + 1))
					b.append(' ').append(header[i]);
				log.warning("remove bad samples of " + in + ":" + b);
			}
			w.append(header[0]);
			for (int i = 1; i < header.length; ++i) {
				if (bad.get(i))
					continue;
				w.append('\t').append(header[i]);
			}
			String line;
			while ((line = fin.readLine()) != null) {
				w.println();
				int t = line.indexOf('\t');
				w.append(line.subSequence(0, t));
				int prev = t;
				int i = 1;
				for (t = line.indexOf('\t', t + 1); t >= 0; t = line.indexOf('\t', t + 1), ++i) {
					if (!bad.get(i))
						w.append(line.subSequence(prev, t));
					prev = t;
				}
				if (!bad.get(i))
					w.append(line.subSequence(prev, line.length()));

			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "can't filter full file: " + in, e);
		}

		return r;
	}

	/**
	 * @param header
	 * @param good
	 * @return
	 */
	private static BitSet filterCols(String[] header, Set<String> good) {
		BitSet r = new BitSet(header.length);
		for (int i = 0; i < header.length; ++i)
			if (!good.contains(header[i]))
				r.set(i);
		return r;
	}

	private static Set<String> readGoodSamples(File file) {
		// sampled: 3row, >=3col
		try (BufferedReader r = new BufferedReader(new FileReader(file))) {
			r.readLine();
			r.readLine();
			String line = r.readLine();
			String[] samples = line.split("\t");
			return ImmutableSet.copyOf(Arrays.copyOfRange(samples, 2, samples.length));
		} catch (IOException e) {
			log.log(Level.SEVERE, "can't read sample header from: " + file, e);
		}
		return null;
	}

	public Pair<TCGAFileInfo, Boolean> findmicroRNAMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			TCGAFileInfo r = extractDataRunFile(".miR_expression.txt", "miR_Preprocess", LEVEL);
			if (r != null) {
				r = filterColumns(r, findStandardSampledClusteredFile(EDataSetType.microRNA));
				return Pair.make(r, true);
			}
		}
		return findStandardSampledClusteredFile(EDataSetType.microRNA);
	}

	public Pair<TCGAFileInfo, Boolean> findmicroRNAseqMatrixFile(boolean loadFullGenes) {
		if (loadFullGenes) {
			TCGAFileInfo r = extractAnalysisRunFile(getFileName(".uncv2.miRseq_RSEM_normalized_log2.txt"),
					"miRseq_Preprocess",
					LEVEL);
			if (r == null)
				r = extractAnalysisRunFile(getFileName(".miRseq_RPKM_log2.txt"), "miRseq_Preprocess",
						LEVEL);
			if (r != null) {
				r = filterColumns(r, findStandardSampledClusteredFile(EDataSetType.microRNA));
				return Pair.make(r, true);
			}
		}
		return findStandardSampledClusteredFile(EDataSetType.microRNAseq);
	}

	public TCGAFileInfo findHiearchicalGrouping(EDataSetType type) {
		return extractAnalysisRunFile(getFileName(".allclusters.txt"), type.getTCGAAbbr()
				+ "_Clustering_Consensus", LEVEL);
	}

	public TCGAFileInfo findCNMFGroupingFile(EDataSetType type) {
		return extractAnalysisRunFile("cnmf.membership.txt", type.getTCGAAbbr() + "_Clustering_CNMF", LEVEL);
	}

	public TCGAFileInfo findCopyNumberFile() {
		return extractAnalysisRunFile("all_thresholded.by_genes.txt", "CopyNumber_Gistic2", LEVEL);
	}

	public TCGAFileInfo findClinicalDataFile() {
		return extractDataRunFile(".clin.merged.txt", "Merge_Clinical", 1);
	}

	public TCGAFileInfo findMutSigReport() {
		return extractAnalysisRunFile(getFileName(".sig_genes.txt"), "MutSigNozzleReportCV", LEVEL);
	}

	public Pair<TCGAFileInfo, Integer> findMutationFile() {
		int startColumn = 8;
		TCGAFileInfo mutationFile = null;
		if (relevantDate.get(Calendar.YEAR) < 2013) { // test only for the <= 2012
			mutationFile = extractAnalysisRunFile(getFileName(".per_gene.mutation_counts.txt"),
					"Mutation_Significance", LEVEL);

			if (mutationFile == null)
				mutationFile = extractAnalysisRunFile(getFileName(".per_gene.mutation_counts.txt"), "MutSigRun2.0",
						LEVEL);
		}
		if (mutationFile == null) {
			// TODO always the -TP version
			TCGAFileInfo maf = null;
			if ( !this.settings.isAwgRun() ) {
				maf = extractAnalysisRunFile(tumor + "-TP.final_analysis_set.maf",
						"MutSigNozzleReport2.0", LEVEL);
			}
			else {
				maf = extractAnalysisRunFile(tumor + ".final_analysis_set.maf",
						"MutSigNozzleReport2.0", LEVEL);
			}
			if (maf != null) {
				return Pair.make(
						new TCGAFileInfo(parseMAF(maf.getFile()), maf.getArchiveURL(), maf.getSourceFileName()), 1);
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

	private TCGAFileInfo extractAnalysisRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, true, false);
	}

	private TCGAFileInfo extractDataRunFile(String fileName, String pipelineName, int level) {
		return extractFile(fileName, pipelineName, level, false, true);
	}

	private TCGAFileInfo extractFile(String fileName, String pipelineName, int level, boolean isAnalysisRun,
			boolean hasTumor) {
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
			log.log(Level.SEVERE, "invalid url generated from: " + id + " " + tumor + " " + tumorSample + " "
					+ pipelineName + " " + level);
			return null;
		}
	}

	private TCGAFileInfo extractFileFromTarGzArchive(URL inUrl, String fileToExtract, File outputDirectory,
			boolean hasTumor) {
		log.info(inUrl + " download and extract: " + fileToExtract);
		File targetFile = new File(outputDirectory, fileToExtract);

		// use cached
		if (targetFile.exists() && !settings.isCleanCache()) {
			log.fine(inUrl+" cache hit");
			return new TCGAFileInfo(targetFile, inUrl, fileToExtract);
		}

		File notFound = new File(outputDirectory, fileToExtract + "-notfound");
		if (notFound.exists() && !settings.isCleanCache()) {
			log.warning(inUrl+" marked as not found");
			return null;
		}

		String alternativeName = fileToExtract;
		if (hasTumor) {
			alternativeName = "/" + tumor.getBaseName() + fileToExtract;
			fileToExtract = "/" + tumor + fileToExtract;
		}

		TarArchiveInputStream tarIn = null;
		OutputStream out = null;
		try {
			InputStream in = new BufferedInputStream(inUrl.openStream());

			// ok we have the file
			tarIn = new TarArchiveInputStream(new GZIPInputStream(in));

			// search the correct entry
			ArchiveEntry act = tarIn.getNextEntry();
			while (act != null && !act.getName().endsWith(fileToExtract) && !act.getName().endsWith(alternativeName)) {
				act = tarIn.getNextEntry();
			}
			if (act == null) // no entry found
				throw new FileNotFoundException("no entry named: " + fileToExtract + " found");

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

			log.info(inUrl+" extracted "+fileToExtract);
			return new TCGAFileInfo(targetFile, inUrl, fileToExtract);
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, inUrl + " can't extract" + fileToExtract + ": file not found", e);
			// file was not found, create a marker to remember this for quicker checks
			notFound.getParentFile().mkdirs();
			try {
				notFound.createNewFile();
			} catch (IOException e1) {
				log.log(Level.WARNING, inUrl + " can't create not-found marker", e);
			}
			return null;
		} catch (Exception e) {
			log.log(Level.SEVERE, inUrl + " can't extract" + fileToExtract + ": " + e.getMessage(), e);
			return null;
		} finally {
			Closeables.closeQuietly(tarIn);
			Closeables.closeQuietly(out);
		}
	}

	private static File parseMAF(File maf) {

		File out = new File(maf.getParentFile(), "P" + maf.getName());
		if (out.exists())
			return out;
		log.fine(maf.getAbsolutePath() + " parsing maf file");
		final String TAB = "\t";

		try (BufferedReader reader = Files.newBufferedReader(maf.toPath(), Charset.forName("UTF-8"))) {
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
			for (String gene : rows) {
				w.append(gene);
				for (String sample : cols) {
					w.append(TAB).append(mutated.contains(gene, sample) ? '1' : '0');
				}
				w.println();
			}
			w.close();
			Files.move(tmp.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);

			log.fine(maf.getAbsolutePath() + " parsed maf file stats: " + mutated.size() + " " + rows.size() + " "
					+ cols.size());
			return out;
		} catch (IOException e) {
			log.log(Level.SEVERE, maf.getAbsolutePath() + " maf parsing error: " + e.getMessage(), e);
		}
		return null;
	}

	public static void main(String[] args) {
		File file = new File(
				"/home/alexsb/Dropbox/Caleydo/data/ccle/CCLE_hybrid_capture1650_hg19_NoCommonSNPs_CDS_2012.05.07.maf");
		file = parseMAF(file);

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FirehoseProvider[");
		builder.append(tumor);
		builder.append("/");
		builder.append(tumorSample);
		builder.append("@");
		builder.append(Settings.format(analysisRun));
		builder.append(",");
		builder.append(Settings.format(dataRun));
		builder.append("]");
		return builder.toString();
	}


}
