package org.caleydo.view.tourguide.v2.r.model;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.caleydo.view.tourguide.v3.model.SimpleHistogram;

public class ScoreColumn {
	public static final String PROP_WEIGHT = "weight";
	public static final String PROP_SELECTED_ROW = "selectedRow";
	public static final String PROP_SELECTION = "selection";

	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	private final InteractiveNormalization trans = new InteractiveNormalization();
	private final Selection selection = new Selection();
	private final Color color;
	private final Color backgroundColor;
	private final String label;

	private float weight = 200.f;

	private final ScoreTable table;

	public ScoreColumn(ScoreTable table, Color color, Color backgroundColor, String label) {
		this.table = table;
		this.table.addPropertyChangeListener(ScoreTable.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				propertySupport.firePropertyChange(PROP_SELECTED_ROW, evt.getOldValue(), evt.getNewValue());
			}
		});
		this.color = color;
		this.backgroundColor = backgroundColor;
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	public String getLabel() {
		return label;
	}

	public boolean isSelected(IValue raw, IValue normalized) {
		return selection.isSelected(raw.asFloat(), normalized.asFloat());
	}

	public IValue normalize(IValue raw) {
		return trans.apply(raw);
	}

	public float weight(float normalized) {
		return normalized * weight;
	}

	public Color getSelectionColor() {
		return color.darker();
	}

	public Color getColor() {
		return color;
	}

	/**
	 * @return the backgroundColor, see {@link #backgroundColor}
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @return the weight, see {@link #weight}
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            setter, see {@link weight}
	 */
	public void setWeight(float weight) {
		System.out.println("set weight to: " + weight);
		propertySupport.firePropertyChange(PROP_WEIGHT, this.weight, this.weight = weight);
	}

	public SimpleHistogram getHist(int bins) {
		String key = this.hashCode() + "hist" + bins;
		SimpleHistogram h = (SimpleHistogram) table.cache.get(key);
		if (h != null)
			return h;
		h = DataUtils.getHist(bins, table.getNormalizedCol(this));
		table.cache.put(key, h);
		return h;
	}

	public int getHistBin(int bins, int row) {
		return DataUtils.getHistBin(bins, getNormalized(row).asFloat());
	}

	public IValue getNormalized(int row) {
		return table.getNormalized(this, row);
	}

	/**
	 * @return
	 */
	public float getNormalizedSelectionMin() {
		return selection.getMin();
	}

	/**
	 * @return
	 */
	public float getNormalizedSelectionMax() {
		return selection.getMax();
	}


	public void setNormalizedSelectionMin(float f) {
		this.selection.setMin(f);
		propertySupport.firePropertyChange(PROP_SELECTION, null, selection);
	}


	public void setNormalizedSelectionMax(float f) {
		this.selection.setMax(f);
		propertySupport.firePropertyChange(PROP_SELECTION, null, selection);
	}

	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}
}
