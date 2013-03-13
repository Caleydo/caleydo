package org.caleydo.vis.rank.layout;

import org.caleydo.vis.rank.model.ColumnRanker;

public interface IRowHeightLayout {
	IRowLayoutInstance layout(ColumnRanker ranker, float h, int size, int offset, boolean forceOffset);

	String getIcon();

	public interface IRowSetter {
		void set(int rowIndex, float x, float y, float w, float h, boolean pickable);
	}
}
