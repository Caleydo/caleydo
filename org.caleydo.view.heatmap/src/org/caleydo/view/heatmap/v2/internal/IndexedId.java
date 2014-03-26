package org.caleydo.view.heatmap.v2.internal;

public final class IndexedId {
	private final int index;
	private final Integer id;

	public IndexedId(int index, Integer id) {
		this.index = index;
		this.id = id;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the index, see {@link #index}
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexedId other = (IndexedId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (index != other.index)
			return false;
		return true;
	}
}