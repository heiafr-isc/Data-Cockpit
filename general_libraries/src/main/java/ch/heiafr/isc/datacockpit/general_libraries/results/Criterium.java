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
package ch.heiafr.isc.datacockpit.general_libraries.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Criterium {

	private String criteriumName;
	private List<String> possibleValues = null;
	private boolean constant = false;

    public Criterium(String name) {
		this.criteriumName = name;
	}

	@SuppressWarnings("unused")
	private Criterium() {
		constant = true;
	}

	public void setPossibleValues(List<String> l) {
		if (constant == true) {
			throw new IllegalStateException();
		}
		if (possibleValues != null) {
			throw new IllegalStateException("Cannot define criterium values only once");
		}
		possibleValues = l;
	}

	public void setPossibleValues(Collection<String> c) {
		if (possibleValues != null) {
			throw new IllegalStateException("Cannot define criterium values only once");
		}
		possibleValues = new ArrayList<String>(c.size());
		possibleValues.addAll(c);
	}

	public String getPossibleValue(int i) {
		return possibleValues.get(i);
	}

	public List<String> getPossibleValues() {
		return possibleValues;
	}

	public String getName() {
		return criteriumName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(criteriumName);
		if (possibleValues != null && possibleValues.size() > 0) {
			sb.append("(");
			for (int i = 0 ; i < possibleValues.size() ; i++) {
				sb.append(possibleValues.get(i));
				if (i+1 < possibleValues.size()) {
					sb.append(", ");
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}

}
