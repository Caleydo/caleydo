package org.caleydo.core.data.collection.table;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.parser.ascii.AStringConverter;
import org.caleydo.core.parser.ascii.TabularDataParser;

/**
 * Parameters to load the initial data-{@link DataTable}.
 * 
 * @author Werner Puff
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
public class LoadDataParameters {

	/** The data domain associated with the loading process */
	@XmlTransient
	private ATableBasedDataDomain dataDomain;

	/** A list of ids for the dimensions (columns) in the file */
	private ArrayList<Integer> columnIDs;

	/** Specifies the IDType that is used in the data file to identify the rows */
	@XmlElement
	private String fileIDTypeName;

	/** path to main data file */
	private String fileName;

	/**
	 * The input pattern for the {@link TabularDataParser}, specifying the order of how to treat values
	 * between delimiters. The string values must map to a {@link EColumnType}.
	 */
	private String inputPattern;

	/** labels of the dimensions */
	private List<String> columnLabels;

	/** csv-delimiter between to values */
	private String delimiter;

	/** line number to start the parsing of the main-data file */
	private int startParseFileAtLine;

	/** line number to stop the parsing of the main-data file, <code>-1</code> for parsing until end of file */
	private int stopParseFileAtLine;

	/** <code>true</code> if a min-value was set, false otherwise */
	private boolean minDefined = false;

	/** an artificial min value used for normalization in the {@link DataTable} */
	private float min = Float.MIN_VALUE;

	/** <code>true</code> if a max-value was set, false otherwise */
	private boolean maxDefined = false;

	/** an artificial max value used for normalization in the {@link DataTable} */
	private float max = Float.MAX_VALUE;

	/**
	 * Determines whether and if so which transformation should be applied to the data (e.g. log2
	 * transformation). This is mapped to values of {@link ExternalDataRepresentation}.
	 */
	private String mathFilterMode;

	/** String converter for the column headers */
	private AStringConverter columnHeaderStringConverter;

	/**
	 * Determines whether a table in the DataTable is considered homogeneous or not. Homogeneous means, that
	 * the same maximum and minimum are used for normalization.
	 */
	@XmlElement
	private boolean isDataHomogeneous = false;

	public LoadDataParameters() {
		this.fileName = null;
		this.inputPattern = "";
		this.columnLabels = new ArrayList<String>();
		this.delimiter = "";
		this.startParseFileAtLine = 1;
		this.stopParseFileAtLine = -1;
		this.dataDomain = null;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		dataDomain.setLoadDataParameters(this);
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	@XmlTransient
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the columnIDs, see {@link #columnIDs}
	 */
	public ArrayList<Integer> getColumnIDs() {
		return columnIDs;
	}

	/**
	 * @param columnIDs
	 *            setter, see {@link #columnIDs}
	 */
	public void setColumnIDs(ArrayList<Integer> columnIDs) {
		this.columnIDs = columnIDs;
	}

	/**
	 * @return the fileName {@link #fileName}
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            setter, see {@link #fileName}
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the inputPattern, see {@link #inputPattern}
	 */
	public String getInputPattern() {
		return inputPattern;
	}

	/**
	 * @param inputPattern
	 *            setter, see {@link #inputPattern}
	 */
	public void setInputPattern(String inputPattern) {
		this.inputPattern = inputPattern;
	}

	/**
	 * @return the dimensionLabels, see {@link #columnLabels}
	 */
	public List<String> getColumnLabels() {
		return columnLabels;
	}

	/**
	 * @param columnLabels
	 *            setter, see {@link #columnLabels}
	 */
	public void setColumnLabels(List<String> columnLabels) {
		this.columnLabels = columnLabels;
	}

	/**
	 * @return the delimiter, see {@link #delimiter}
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter
	 *            setter, see {@link #delimiter}
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the startParseFileAtLine, see {@link #startParseFileAtLine}
	 */
	public int getStartParseFileAtLine() {
		return startParseFileAtLine;
	}

	/**
	 * @param startParseFileAtLine
	 *            setter, see {@link #startParseFileAtLine}
	 */
	public void setStartParseFileAtLine(int startParseFileAtLine) {
		this.startParseFileAtLine = startParseFileAtLine;
	}

	/**
	 * @return the stopParseFileAtLine, see {@link #stopParseFileAtLine}
	 */
	public int getStopParseFileAtLine() {
		return stopParseFileAtLine;
	}

	/**
	 * @param stopParseFileAtLine
	 *            setter, see {@link #stopParseFileAtLine}
	 */
	public void setStopParseFileAtLine(int stopParseFileAtLine) {
		this.stopParseFileAtLine = stopParseFileAtLine;
	}

	/**
	 * @return the minDefined, see {@link #minDefined}
	 */
	public boolean isMinDefined() {
		return minDefined;
	}

	/**
	 * @param minDefined
	 *            setter, see {@link #minDefined}
	 */
	public void setMinDefined(boolean minDefined) {
		this.minDefined = minDefined;
	}

	/**
	 * @return the min, see {@link #min}
	 */
	public float getMin() {
		return min;
	}

	/**
	 * @param min
	 *            setter, see {@link #min}
	 */
	public void setMin(float min) {
		this.min = min;
	}

	/**
	 * @return the maxDefined, see {@link #maxDefined}
	 */
	public boolean isMaxDefined() {
		return maxDefined;
	}

	/**
	 * @param maxDefined
	 *            setter, see {@link #maxDefined}
	 */
	public void setMaxDefined(boolean maxDefined) {
		this.maxDefined = maxDefined;
	}

	/**
	 * @return the max, see {@link #max}
	 */
	public float getMax() {
		return max;
	}

	/**
	 * @param max
	 *            setter, see {@link #max}
	 */
	public void setMax(float max) {
		this.max = max;
	}

	/**
	 * @return the mathFilterMode, see {@link #mathFilterMode}
	 */
	public String getMathFilterMode() {
		return mathFilterMode;
	}

	/**
	 * @param mathFilterMode
	 *            setter, see {@link #mathFilterMode}
	 */
	public void setMathFilterMode(String mathFilterMode) {
		this.mathFilterMode = mathFilterMode;
	}

	/**
	 * Sets the fileIDType string equivalent to {@link #fileIDTypeName}
	 * 
	 * @param fileIDType
	 */
	public void setFileIDType(IDType fileIDType) {
		this.fileIDTypeName = fileIDType.getTypeName();
	}

	/**
	 * @return the fileIDTypeName, see {@link #fileIDTypeName}
	 */
	public String getFileIDTypeName() {
		return fileIDTypeName;
	}

	/**
	 * Set whether the data you want to load is homogeneous (i.e.: there is a global minimum and maximum)
	 * 
	 * @param isDataHomogeneous
	 *            true if your data has a global min and max, else false, see {@link #isDataHomogeneous}
	 */
	public void setIsDataHomogeneous(boolean isDataHomogeneous) {
		this.isDataHomogeneous = isDataHomogeneous;
	}

	/**
	 * Tells you whether the data to be processed is homogeneous (i.e.: there is a global minimum and maximum)
	 * 
	 * @return true if data is homogeneous, else false, see {@link #isDataHomogeneous}
	 */
	public boolean isDataHomogeneous() {
		return isDataHomogeneous;
	}

	/**
	 * @param columnHeaderStringConverter
	 *            setter, see {@link #columnHeaderStringConverter}
	 */
	public void setColumnHeaderStringConverter(AStringConverter columnHeaderStringConverter) {
		this.columnHeaderStringConverter = columnHeaderStringConverter;
	}

	/**
	 * @return the columnHeaderStringConverter, see {@link #columnHeaderStringConverter}
	 */
	public AStringConverter getColumnHeaderStringConverter() {
		return columnHeaderStringConverter;
	}
}