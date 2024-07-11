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

import java.util.ArrayList;
import java.util.Iterator;


public class ParetoSet<X extends ParetoPoint> implements Iterable<X> {
	
	private ArrayList<X> points;
	
	private int dimensions;
	
	public ParetoSet(int dimensions) {
		this.dimensions = dimensions;
		points = new ArrayList<X>();
	}

	public boolean addCandidate(X pp) {
		if (pp.getDimensions() > dimensions) throw new IllegalStateException();
		if (points.size() == 0) {
			points.add(pp);
			return true;
		}
		// a point is added :
			// 1. as soon as it dominates another
				// in which case dominated point must be removed
			// 2. If it is dominated by no existing point
		ArrayList<X> toRem = new ArrayList<X>();
		boolean keep = false;
		boolean passedAll = true;
		for (int i = 0 ; i < points.size() ; i++) {
			X alt = points.get(i);
			if (pp.dominates(alt)) {
				toRem.add(alt);
				keep = true;
			}
			if (keep == false) {
				if (alt.dominates(pp)) {
					passedAll = false;
					break;
				}
			}
		}
		if (keep || passedAll) {
			points.removeAll(toRem);
			points.add(pp);
			return true;
		}
		return false;
	}

	@Override
	public Iterator<X> iterator() {
		return points.iterator();
	}

}
