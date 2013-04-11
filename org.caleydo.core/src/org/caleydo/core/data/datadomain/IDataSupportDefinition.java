/**
 *
 */
package org.caleydo.core.data.datadomain;

import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;

import com.google.common.base.Predicate;


/**
 * Specifies whether {@link IDataDomain}s are
 * supported. This is required for views to determine whether they are able to
 * handle certain data or not.
 *
 * @author Christian Partl
 *
 */
public interface IDataSupportDefinition extends Predicate<IDataDomain> {
	public Collection<TablePerspective> filter(Collection<TablePerspective> tablePerspectives);

	public Predicate<TablePerspective> asTablePerspectivePredicate();
}
