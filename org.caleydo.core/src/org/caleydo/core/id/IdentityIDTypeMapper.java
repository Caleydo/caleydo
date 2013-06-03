package org.caleydo.core.id;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * simple implementation of an {@link IIDTypeMapper}, where source = target
 *
 * @author Samuel Gratzl
 *
 * @param <K>
 * @param <V>
 */
final class IdentityIDTypeMapper<K, V> implements IIDTypeMapper<K, V> {
	private final IDType sourceAndTarget;

	public IdentityIDTypeMapper(final IDType sourceAndTarget) {
		this.sourceAndTarget = sourceAndTarget;
	}

	@Override
	public IDType getSource() {
		return sourceAndTarget;
	}

	@Override
	public IDType getTarget() {
		return sourceAndTarget;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<V> apply(K sourceID) {
		return Collections.singleton((V) sourceID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<V> apply(Iterable<K> sourceIds) {
		return (Set<V>) Sets.newHashSet(sourceIds);
	}

	@Override
	public boolean isMapAble(K sourceId) {
		return sourceId != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceAndTarget == null) ? 0 : sourceAndTarget.hashCode());
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
		@SuppressWarnings("rawtypes")
		IdentityIDTypeMapper other = (IdentityIDTypeMapper) obj;
		if (sourceAndTarget == null) {
			if (other.sourceAndTarget != null)
				return false;
		} else if (!sourceAndTarget.equals(other.sourceAndTarget))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("IdentityIDTypeMapper [sourceAndTarget=%s]", sourceAndTarget);
	}

}