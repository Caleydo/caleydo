package org.caleydo.view.tourguide.vendingmachine.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.filter.CompareScoreFilter;
import org.caleydo.view.tourguide.data.filter.ECompareOperator;
import org.caleydo.view.tourguide.data.filter.IScoreFilter;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.google.common.collect.Lists;

public class ScoreFilterDialog extends TitleAreaDialog {
	// the root element to populate the viewer with
	private final List<IScore> scores;
	private final ScoreQueryUI sender;
	private final ScoreQuery query;

	// the visual selection widget group
	private Spinner topUI;
	private Button forceGroupsUI;

	private List<ScoreFilterWidget> filters = new ArrayList<>();
	private Combo addScoreFilter;
	private Group filterContainer;

	public ScoreFilterDialog(Shell shell, Collection<IScore> scores, ScoreQueryUI sender) {
		super(shell);
		this.scores = Lists.newArrayList(scores);
		this.sender = sender;
		this.query = sender.getQuery();
		this.filters = new ArrayList<>();
		for (IScoreFilter f : this.query.getFilter()) {
			if (f.getReference() != null)
				this.scores.remove(f.getReference());
		}
	}

	@Override
	public void create() {
		super.create();
		this.setTitle("Edit Score Filters");
		this.setBlockOnOpen(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		new Label(c, SWT.NONE).setText("Show only the first x Entries: ");

		this.topUI = new Spinner(c, SWT.BORDER);
		this.topUI.setMinimum(1);
		this.topUI.setMaximum(100);
		this.topUI.setIncrement(1);
		this.topUI.setSelection(this.sender.getQuery().getTop());
		this.topUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		addRowAdder(parent).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 200;
		scrolledComposite.setLayoutData(gridData);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);

		this.filterContainer = new Group(scrolledComposite, SWT.NONE);
		filterContainer.setText("Active Filters:");
		filterContainer.setLayout(new GridLayout(4, false));

		for (IScoreFilter f : query.getFilter()) {
			if (f instanceof CompareScoreFilter) {
				addFilterRow(f);
			}
		}

		scrolledComposite.setContent(filterContainer);
		Point point = filterContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		filterContainer.setSize(point);
		scrolledComposite.setMinSize(point);

		return c;
	}

	private void addFilterRow(final IScoreFilter f) {
		final int i = this.filters.size();
		this.filters.add(new ScoreFilterWidget(filterContainer, ((CompareScoreFilter) f)));
		Button removeRow = new Button(filterContainer, SWT.PUSH);
		removeRow.setImage(GeneralManager.get().getResourceLoader()
				.getImage(filterContainer.getDisplay(), TourGuideRenderStyle.ICON_DELETE_ROW));
		removeRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onRemoveFilter(f, i);
			}
		});
	}

	/**
	 * @param parent
	 */
	private Composite addRowAdder(Composite parent) {
		parent = new Composite(parent, SWT.NONE);
		RowLayout r = new RowLayout(SWT.HORIZONTAL);
		r.center = true;
		parent.setLayout(r);

		new Label(parent, SWT.NONE).setText("Add New");
		this.addScoreFilter = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		for (IScore score : scores)
			addScoreFilter.add(score.getLabel());
		Button addRow = new Button(parent, SWT.PUSH);
		addRow.setImage(GeneralManager.get().getResourceLoader()
				.getImage(parent.getDisplay(), TourGuideRenderStyle.ICON_ADD_ROW));
	    addRow.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
				onAddFilter();
	    	}
		});

	    return parent;
	}

	protected void onAddFilter() {
		int i = this.addScoreFilter.getSelectionIndex();
		if (i < 0)
			return;
		this.addScoreFilter.select(-1);
		this.addScoreFilter.remove(i);
		IScore s = this.scores.remove(i);
		addFilterRow(new CompareScoreFilter(s, ECompareOperator.IS_NOT_NA, 0.1f));
		this.filterContainer.layout(true);
	}

	protected void onRemoveFilter(IScoreFilter f, int i) {
		this.filters.remove(i);
		Control[] arr = filterContainer.getChildren();
		for (int j = i * 4; j < i * 4 + 4; ++j)
			arr[j].dispose();
	}

	private boolean validate() {
		return true;
	}

	@Override
	protected void okPressed() {
		if (!validate())
			return;
		save();
		super.okPressed();
	}

	private void save() {
		query.setTop(this.topUI.getSelection());
		Collection<IScoreFilter> f = new ArrayList<>();
		for(ScoreFilterWidget w: this.filters) {
			f.add(w.save());
		}
		query.setFilters(f);
	}
}