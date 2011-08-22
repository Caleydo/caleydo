/**
 * 
 */
package org.caleydo.core.data.perspective;

import java.util.Set;

import org.caleydo.core.data.collection.table.DataTable;
import org.eclipse.swt.widgets.Shell;

/**
 * Helper class that chooses the perspective based on parameters where possible, or opens a dialog when it's
 * unclear.
 * 
 * @author Alexander Lex
 */
public class PerspectiveChooser {

	/**
	 * <p>
	 * Choose a {@link RecordPerspective} from those registered with the {@link DataTable} specified. If
	 * multiple recordPerspectives are available a dialog to make the user choose is opened.
	 * </p>
	 * <p>
	 * As {@link DataPerspective}s can be either private or public (see
	 * {@link DataPerspective#setIsPrivate(boolean)}, it is possible to let the chooser only chose from those
	 * that are public.
	 * 
	 * @param table
	 *            the data table from which to choose the available perspectives
	 * @param considerOnlyPublic
	 *            whether only public perspectives shall be used (true) or whether to also include private
	 *            perspectives
	 * @return the chosen perspectiveID
	 */
	public static String chooseRecordPerspective(DataTable table, boolean considerOnlyPublic) {
		return choosePerspective(table, considerOnlyPublic, true);
	}

	/**
	 * Same as {@link #chooseRecordPerspective(DataTable, boolean)} but for dimensions.
	 * 
	 * @param table
	 * @param considerOnlyPublic
	 * @return
	 */
	public static String chooseDimensionPerspective(DataTable table, boolean considerOnlyPublic) {
		return choosePerspective(table, considerOnlyPublic, false);
	}

	private static String choosePerspective(DataTable table, boolean considerOnlyPublic, boolean isRecord) {
		Set<String> dataPerspectiveIDs;
		if (isRecord)
			dataPerspectiveIDs = table.getRecordPerspectiveIDs();
		else
			dataPerspectiveIDs = table.getDimensionPerspectiveIDs();

		if (dataPerspectiveIDs.size() == 1)
			return dataPerspectiveIDs.iterator().next();
		else {

			if (!considerOnlyPublic) {
				return useDialog(dataPerspectiveIDs);
			}
			else {
				String chosenPerspective = null;
				// check if there is only one "public" perspecive
				for (String tempPerspectiveID : dataPerspectiveIDs) {
					DataPerspective<?, ?, ?, ?> perspective;
					if (isRecord)
						perspective = table.getRecordPerspective(tempPerspectiveID);
					else
						perspective = table.getDimensionPerspective(tempPerspectiveID);

					if (!perspective.isPrivate()) {
						if (chosenPerspective != null) {
							// there is more than one candidate
							chosenPerspective = useDialog(dataPerspectiveIDs);
							break;
						}
						else {
							chosenPerspective = tempPerspectiveID;
						}
					}
				}
				return chosenPerspective;
			}

		}
	}

	private static String useDialog(Set<String> perspectiveIDs) {
		ChooseDataPerspectiveDialog dialog = new ChooseDataPerspectiveDialog(new Shell());
		dialog.setPossiblePerspectiveIDs(perspectiveIDs);
		String chosenPerspective = dialog.open();
		return chosenPerspective;

	}

}
