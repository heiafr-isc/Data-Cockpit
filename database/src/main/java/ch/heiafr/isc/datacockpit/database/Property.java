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
package ch.heiafr.isc.datacockpit.database;

import java.io.Serializable;
import java.util.Map.Entry;

public class Property implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String name;
	private final String value;
	private final Float floatValue;
	private final String unit;

	public Property(Entry<String, String> ent) {
		this.name = ent.getKey();
		this.value = ent.getValue();
		this.floatValue = parse(value);
		this.unit = "";
	}

	public Property(String name, String value) {
		this.name = name;
		this.value = value;
		this.floatValue = parse(value);
		this.unit = "";
	}

	public Property(String name, double value) {
		this.name = name;
		this.value = value+"";
		this.floatValue = (float)value;
		this.unit = "";
	}

	public Property(String name, String value, String unit) {
		this.name = name;
		this.value = value;
		this.floatValue = parse(value);
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public Float getFloatValue() {
		return floatValue;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return name + "=" + value + " " + unit;
	}

	public boolean isString() {
		return floatValue == null;
	}
	
	public Float parse(String s) {
		try {
			return Float.parseFloat(s);
		}
		catch (Exception e) {
			return null;
		}
	}	
}
