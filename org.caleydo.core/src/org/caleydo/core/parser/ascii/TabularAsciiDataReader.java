package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Loader for tabular data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class TabularAsciiDataReader
	extends AbstractLoader {

	/**
	 * Imports data from file to this set. uses first storage and overwrites first selection.
	 */
	protected ArrayList<AStorage> targetStorages;

	protected ArrayList<EStorageType> columnDataTypes;

	private ArrayList<int[]> intArrays;

	private ArrayList<int[]> groupInfo;

	private ArrayList<float[]> floatArrays;

	private ArrayList<ArrayList<String>> stringLists;

	private boolean useExperimentClusterInfo;

	private ATableBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 */
	public TabularAsciiDataReader(final String sFileName, ATableBasedDataDomain dataDomain) {
		super(sFileName);

		this.dataDomain = dataDomain;
		targetStorages = new ArrayList<AStorage>();
		columnDataTypes = new ArrayList<EStorageType>();

		intArrays = new ArrayList<int[]>();
		floatArrays = new ArrayList<float[]>();
		stringLists = new ArrayList<ArrayList<String>>();

		groupInfo = new ArrayList<int[]>();
	}

	/**
	 * Defines a pattern for parsing
	 */
	public final boolean setTokenPattern(final String tokenPattern) {

		boolean areAllTokensProper = true;

		StringTokenizer tokenizer = new StringTokenizer(tokenPattern);
		
		final String sTokenPatternParserSeperator = GeneralManager.sDelimiter_Parser_DataType;

		while (tokenizer.hasMoreTokens()) {
			String buffer = tokenizer.nextToken(sTokenPatternParserSeperator);

			if (buffer.equalsIgnoreCase("abort")) {
				columnDataTypes.add(EStorageType.ABORT);

				return areAllTokensProper;
			}
			else if (buffer.equalsIgnoreCase("skip")) {
				columnDataTypes.add(EStorageType.SKIP);
			}
			else if (buffer.equalsIgnoreCase("int")) {
				columnDataTypes.add(EStorageType.INT);
			}
			else if (buffer.equalsIgnoreCase("float")) {
				columnDataTypes.add(EStorageType.FLOAT);
			}
			else if (buffer.equalsIgnoreCase("string")) {
				columnDataTypes.add(EStorageType.STRING);
			}
			else if (buffer.equalsIgnoreCase("group_number")) {
				columnDataTypes.add(EStorageType.GROUP_NUMBER);
			}
			else if (buffer.equalsIgnoreCase("group_representative")) {
				columnDataTypes.add(EStorageType.GROUP_REPRESENTATIVE);
			}
			else if (buffer.equalsIgnoreCase("certainty")) {
				columnDataTypes.add(EStorageType.CERTAINTY);
			}
			
			else {
				areAllTokensProper = false;

				Logger.log(new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "Unknown column data type: "
					+ buffer + " in " + tokenPattern));
			}
		}

		return areAllTokensProper;
	}

	public void enableExperimentClusterInfo() {
		useExperimentClusterInfo = true;
	}

	public void setTargetStorages(final ArrayList<Integer> targetStorageIDs) {
		for (int storageID : targetStorageIDs) {
			targetStorages.add(GeneralManager.get().getStorageManager().getItem(storageID));
		}
	}

	protected void allocateStorageBufferForTokenPattern() {

		int lineCount = 0;

		for (EStorageType storageType : columnDataTypes) {

			switch (storageType) {
				case GROUP_NUMBER:
				case GROUP_REPRESENTATIVE:
					if (useExperimentClusterInfo)
						groupInfo.add(new int[nrLinesToReadWithClusterInfo]);
					else
						groupInfo.add(new int[nrLinesToRead]);
					break;
				case INT:
					intArrays.add(new int[nrLinesToRead]);
					break;
				case FLOAT:
					if (useExperimentClusterInfo)
						floatArrays.add(new float[nrLinesToReadWithClusterInfo]);
					else
						floatArrays.add(new float[nrLinesToRead]);
					lineCount++;
					break;
				case STRING:
					stringLists.add(new ArrayList<String>(nrLinesToRead));
					break;
				case CERTAINTY:
					floatArrays.add(new float[nrLinesToRead]);
					break;
				case SKIP:
					break;
				case ABORT:
					if (useExperimentClusterInfo) {
						groupInfo.add(new int[lineCount]);
						groupInfo.add(new int[lineCount]);
					}
					return;

				default:
					throw new IllegalStateException("Unknown token pattern detected: "
						+ storageType.toString());
			}
		}
	}

	@Override
	protected void loadDataParseFile(BufferedReader bufferReader, final int numberOfLines) throws IOException {

		allocateStorageBufferForTokenPattern();

		// Init progress bar
		swtGuiManager.setProgressBarText("Load data file " + this.getFileName());

		String sLine;
		String tempToken;
		int columnIndex = 0;
		float fProgressBarFactor = 100f / iStopParsingAtLine;

		int max = iStopParsingAtLine - parsingStartLine + 1;

		while ((sLine = bufferReader.readLine()) != null && lineInFile <= iStopParsingAtLine) {
			// Check if line should be ignored
			if (lineInFile < this.parsingStartLine) {
				lineInFile++;
				continue;
			}

			// Replace empty cells with NaN
			sLine = sLine.replace(tokenSeperator + tokenSeperator, tokenSeperator + "NaN" + tokenSeperator);
//			sLine = sLine.replace(tokenSeperator + System.getProperty("line.separator"), tokenSeperator + "NaN" + System.getProperty("line.separator"));
			// Take care of empty cells in first column because they are not embedded between two token
			// separators
			if (sLine.substring(0, 1).equals(tokenSeperator))
				sLine = "NaN" + sLine;

			StringTokenizer strTokenLine = new StringTokenizer(sLine, tokenSeperator);

			columnIndex = 0;
			int intIndex = 0;
			int floatIndex = 0;
			int stringIndex = 0;

			for (EStorageType columnDataType : columnDataTypes) {
				if (strTokenLine.hasMoreTokens()) {
					switch (columnDataType) {
						case INT:
							intArrays.get(intIndex)[lineInFile - parsingStartLine] =
								Integer.valueOf(strTokenLine.nextToken()).intValue();
							columnIndex++;
							intIndex++;
							break;
						case FLOAT:
						case CERTAINTY:
							Float value;
							tempToken = strTokenLine.nextToken();
							try {
								value = Float.parseFloat(tempToken);
							}
							catch (NumberFormatException nfe) {

								String sErrorMessage =
									"Unable to parse the data file. \""
										+ tempToken
										+ "\" at ["
										+ (columnIndex + 2)
										+ ", "
										+ (lineInFile - parsingStartLine)
										+ "] cannot be converted to a number. Please change the data selection and try again.";
								// MessageDialog.openError(new Shell(), "Error during parsing",
								// sErrorMessage);

								Logger
									.log(new Status(IStatus.ERROR, GeneralManager.PLUGIN_ID, sErrorMessage));
								value = Float.NaN;
							}

							if (useExperimentClusterInfo) {
								if (lineInFile < max - 1)
									floatArrays.get(floatIndex)[lineInFile - parsingStartLine] = value;
								// FIXME check indices here
								else if (lineInFile == max - 1)
									groupInfo.get(2)[columnIndex] = Integer.valueOf(tempToken).intValue();
								else if (lineInFile == max)
									groupInfo.get(3)[columnIndex] = Integer.valueOf(tempToken).intValue();
							}
							else
								floatArrays.get(floatIndex)[lineInFile - parsingStartLine] = value;

							floatIndex++;
							columnIndex++;
							break;
						case STRING:
							String token = strTokenLine.nextToken();
							stringLists.get(stringIndex).add(token);
							stringIndex++;
							columnIndex++;
							break;
						case SKIP: // do nothing
							strTokenLine.nextToken();
							break;
						case ABORT:
							columnIndex = columnDataTypes.size();
							break;
						case GROUP_NUMBER:
							groupInfo.get(0)[lineInFile - parsingStartLine] =
								Integer.valueOf(strTokenLine.nextToken()).intValue();
							break;
						case GROUP_REPRESENTATIVE:
							groupInfo.get(1)[lineInFile - parsingStartLine] =
								Integer.valueOf(strTokenLine.nextToken()).intValue();
							break;
						default:
							throw new IllegalStateException("Unknown token pattern detected: "
								+ columnDataType.toString());
					}

					// Check if the line is finished or early aborted
					if (columnIndex == columnDataTypes.size()) {
						continue;
					}
				}
			}

			lineInFile++;

			// Update progress bar only on each 100th line
			if (lineInFile % 1000 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (fProgressBarFactor * lineInFile));
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void setArraysToStorages() {

		int iIntArrayIndex = 0;
		int iFloatArrayIndex = 0;
		int iStringArrayIndex = 0;
		int storageIndex = 0;

		DataTable set = dataDomain.getSet();

		for (EStorageType storageType : columnDataTypes) {
			// if(iStorageIndex + 1 == targetStorages.size())
			// break;
			switch (storageType) {
				case INT:
					targetStorages.get(storageIndex).setRawData(intArrays.get(iIntArrayIndex));
					iIntArrayIndex++;
					storageIndex++;
					break;
				case FLOAT:
					targetStorages.get(storageIndex).setRawData(floatArrays.get(iFloatArrayIndex));
					iFloatArrayIndex++;
					storageIndex++;
					break;
				case CERTAINTY:
					targetStorages.get(storageIndex-1).setUncertaintyData(floatArrays.get(iFloatArrayIndex));
					dataDomain.getSet().setContainsUncertaintyData(true);
					iFloatArrayIndex++;
					break;
				case STRING:

					ArrayList<String> rawStringData = stringLists.get(iStringArrayIndex);
					rawStringData = fillUp(rawStringData);
					((NominalStorage<String>) targetStorages.get(storageIndex))
						.setRawNominalData(rawStringData);
					// stringLists.add(new ArrayList<String>(iStopParsingAtLine - parsingStartLine));
					iStringArrayIndex++;
					storageIndex++;
					break;
				case SKIP: // do nothing
					break;
				case GROUP_NUMBER:

					int[] iArGroupInfo = groupInfo.get(0);
					DataTableUtils.setContentGroupList((DataTable) set, DataTable.CONTENT, iArGroupInfo);

					iIntArrayIndex++;
					break;
				case GROUP_REPRESENTATIVE:

					int[] iArGroupRepr = groupInfo.get(1);
					DataTableUtils.setContentGroupRepresentatives((DataTable) set, DataTable.CONTENT, iArGroupRepr);

					iIntArrayIndex++;
					break;
				case ABORT:
					if (useExperimentClusterInfo) {
						iArGroupInfo = groupInfo.get(2);
						DataTableUtils.setStorageGroupList((DataTable) set, DataTable.STORAGE, iArGroupInfo);
						iArGroupRepr = groupInfo.get(3);
						DataTableUtils.setStorageGroupRepresentatives((DataTable) set, DataTable.STORAGE, iArGroupRepr);
					}
					return;

				default:
					throw new IllegalStateException("Unknown token pattern detected: "
						+ storageType.toString());
			}
		}
	}

	/**
	 * Method masking errors with import of categorical data FIXME: find the underlying error instead
	 * 
	 * @param rawStringData
	 * @return
	 */
	@Deprecated
	private ArrayList<String> fillUp(ArrayList<String> rawStringData) {
		int missingValues = nrLinesToRead - rawStringData.size();
		if (missingValues > 0) {
			for (int count = rawStringData.size(); count < nrLinesToRead; count++)
				rawStringData.add("ARTIFICIAL");

			Logger.log(new Status(IStatus.ERROR, GeneralManager.PLUGIN_ID, "Had to fill up stroarge with "
				+ missingValues + " artificial strings"));

		}
		return rawStringData;

	}

	public ArrayList<EStorageType> getColumnDataTypes() {
		return columnDataTypes;
	}
}
