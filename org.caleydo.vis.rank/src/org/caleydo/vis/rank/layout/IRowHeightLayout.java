package org.caleydo.vis.rank.layout;

import org.caleydo.vis.rank.model.ColumnRanker;

public interface IRowHeightLayout {
	IRowLayoutInstance layout(ColumnRanker ranker, float h, int size, int offset, boolean forceOffset);

	String getIcon();

	public interface ISetHeight {
		void set(int index, float y, float h);
	}
}
