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
package ch.heiafr.isc.datacockpit.general_libraries.utils;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class SimpleSet<V> extends AbstractSet<V> {

	public static final SimpleSet EMPTY_SET = new SimpleSet(0);

	private ArrayList<V> elements;

	public SimpleSet(Set<V> s) {
		elements = new ArrayList<V>(s.size());
		elements.addAll(s);
	}

	public SimpleSet() {
		elements = new ArrayList<V>();
	}

	public SimpleSet(int forecastedSize) {
		elements = new ArrayList<V>(forecastedSize);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public Iterator<V> iterator() {
		return elements.iterator();
	}

	@Override
	public boolean add(V v) {
		for (V alt : elements) {
			if (alt.equals(v)) {
				return false;
			}
		}
		elements.add(v);
		return true;
	}
}
