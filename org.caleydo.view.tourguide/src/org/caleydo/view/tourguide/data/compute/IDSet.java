package org.caleydo.view.tourguide.data.compute;

import java.util.Collection;

/**
 * write only set of integer ids
 *
 * @author Samuel Gratzl
 *
 */
public interface IDSet extends Iterable<Integer> {
	public boolean contains(int id);

	public void set(int id);

	public void setAll(Collection<Integer> ids);

	public int size();

	public void clear();

	public boolean isFastIteration();
}
