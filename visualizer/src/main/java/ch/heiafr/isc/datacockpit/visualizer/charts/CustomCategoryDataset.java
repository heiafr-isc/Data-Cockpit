/*
 * This file is part of one of the Data-Cockpit libraries.
 * 
 * Copyright (C) 2024 ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE (EPFL)
 * 
 * Author - Sébastien Rumley (sebastien.rumley@hefr.ch)
 * 
 * This open source release is made with the authorization of the EPFL,
 * the institution where the author was originally employed.
 * The author is currently affiliated with the HEIA-FR, which is the actual publisher.
 * 
 * The Data-Cockpit program is free software, you can redistribute it and/or modify
 * it under the terms of GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Data-Cockpit program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * Contributor list -
 */
package ch.heiafr.isc.datacockpit.visualizer.charts;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.PublicCloneable;

public class CustomCategoryDataset extends AbstractDataset implements
CategoryDataset, PublicCloneable, Serializable {

	private static final long serialVersionUID = -4410572023995771102L;

	/** A storage structure for the data. */
	private CustomKeyedValues2D[] data;

	/** Number of stored parameters */
	private int size;

	/**
	 * Creates a new (empty) dataset.
	 */
	public CustomCategoryDataset() {
		this.size = 10;
		this.data = new CustomKeyedValues2D[this.size];
		for (int i=0; i<this.size; ++i) {
			this.data[i] = new CustomKeyedValues2D();
		}
	}

	/**
	 * Returns the number of rows in the table.
	 * 
	 * @return The row count.
	 * 
	 * @see #getColumnCount()
	 */
	public int getRowCount() {
		return this.data[0].getRowCount();
	}

	/**
	 * Returns the number of columns in the table.
	 * 
	 * @return The column count.
	 * 
	 * @see #getRowCount()
	 */
	public int getColumnCount() {
		return this.data[0].getColumnCount();
	}

	/**
	 * Returns a value from the table.
	 * 
	 * @param row
	 *            the row index (zero-based).
	 * @param column
	 *            the column index (zero-based).
	 * 
	 * @return The value (possibly <code>null</code>).
	 *
	 */
	public Number getValue(int type, int row, int column) {
		if (type >= this.size) {
			throw new IllegalArgumentException("Value must be smaler than " + this.size);
		}
		return this.data[type].getValue(row, column);
	}

	/**
	 * Returns the row keys.
	 * 
	 * @return The keys.
	 * 
	 * @see #getRowKey(int)
	 */
	public List<Comparable<?>> getRowKeys() {
		return this.data[0].getRowKeys();
	}

	/**
	 * Returns a column key.
	 * 
	 * @param column
	 *            the column index (zero-based).
	 * 
	 * @return The column key.
	 * 
	 * @see #getColumnIndex(Comparable)
	 */
	public Comparable<?> getColumnKey(int column) {
		return this.data[0].getColumnKey(column);
	}

	/**
	 * Returns the column index for a given key.
	 * 
	 * @param key
	 *            the column key (<code>null</code> not permitted).
	 * 
	 * @return The column index.
	 * 
	 * @see #getColumnKey(int)
	 */
	public int getColumnIndex(Comparable key) {
		// defer null argument check
		return this.data[0].getColumnIndex(key);
	}

	/**
	 * Returns the column keys.
	 * 
	 * @return The keys.
	 * 
	 * @see #getColumnKey(int)
	 */
	public List<Comparable<?>> getColumnKeys() {
		return this.data[0].getColumnKeys();
	}

	/**
	 * Returns the value for a pair of keys.
	 * 
	 * @param rowKey
	 *            the row key (<code>null</code> not permitted).
	 * @param columnKey
	 *            the column key (<code>null</code> not permitted).
	 * 
	 * @return The value (possibly <code>null</code>).
	 * 
	 * @throws UnknownKeyException
	 *             if either key is not defined in the dataset.
	 *
	 */
	public Number getValue(int type, Comparable rowKey, Comparable columnKey) {
		if (type >= this.size) {
			throw new IllegalArgumentException("Value must be smaler than " + this.size);
		}
		return this.data[type].getValue(rowKey, columnKey);
	}

	/**
	 * Adds or updates a value in the table and sends a
	 * {@link DatasetChangeEvent} to all registered listeners.
	 * 
	 * @param value
	 *            the value (<code>null</code> permitted).
	 * @param rowKey
	 *            the row key (<code>null</code> not permitted).
	 * @param columnKey
	 *            the column key (<code>null</code> not permitted).
	 * 
	 * @see #getValue(Comparable, Comparable)
	 */
	public void setValue(Number value, int type, Comparable<?> rowKey, Comparable<?> columnKey) {
		if (type >= this.size) {
			throw new IllegalArgumentException("Value must be smaler than " + this.size);
		}
		this.data[type].setValue(value, rowKey, columnKey);
		fireDatasetChanged();
	}

	/**
	 * Adds or updates a value in the table and sends a
	 * {@link DatasetChangeEvent} to all registered listeners.
	 * 
	 * @param value
	 *            the value.
	 * @param rowKey
	 *            the row key (<code>null</code> not permitted).
	 * @param columnKey
	 *            the column key (<code>null</code> not permitted).
	 * 
	 * @see #getValue(Comparable, Comparable)
	 */
	public void setValue(double value, int type, Comparable<?> rowKey, Comparable<?> columnKey) {
		if (type >= this.size) {
			throw new IllegalArgumentException("Value must be smaler than " + this.size);
		}
		setValue(new Double(value), type, rowKey, columnKey);
	}

	/**
	 * Tests this dataset for equality with an arbitrary object.
	 * 
	 * @param obj
	 *            the object (<code>null</code> permitted).
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CategoryDataset)) {
			return false;
		}
		CategoryDataset that = (CategoryDataset) obj;
		if (!getRowKeys().equals(that.getRowKeys())) {
			return false;
		}
		if (!getColumnKeys().equals(that.getColumnKeys())) {
			return false;
		}
		int rowCount = getRowCount();
		int colCount = getColumnCount();
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < colCount; c++) {
				Number v1 = getValue(r, c);
				Number v2 = that.getValue(r, c);
				if (v1 == null) {
					if (v2 != null) {
						return false;
					}
				} else if (!v1.equals(v2)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns a hash code for the dataset.
	 * 
	 * @return A hash code.
	 */
	@Override
	public int hashCode() {
		return this.data.hashCode();
	}

	/**
	 * Returns a clone of the dataset.
	 * 
	 * @return A clone.
	 * 
	 * @throws CloneNotSupportedException
	 *             if there is a problem cloning the dataset.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		CustomCategoryDataset clone = (CustomCategoryDataset) super.clone();
		clone.data = this.data.clone();
		return clone;
	}


	@Override
	public Comparable getRowKey(int row) {
		return this.data[0].getRowKey(row);
	}


	@Override
	public int getRowIndex(Comparable key) {
		return this.data[0].getRowIndex(key);
	}


	@Override
	public Number getValue(Comparable rowKey, Comparable columnKey) {
		return this.data[0].getValue(rowKey, columnKey);
	}

	@Override
	public Number getValue(int row, int column) {
		return this.data[0].getValue(row, column);
	}

}
