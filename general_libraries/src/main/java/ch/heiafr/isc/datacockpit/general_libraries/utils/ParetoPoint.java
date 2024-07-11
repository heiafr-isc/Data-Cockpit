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


public abstract class ParetoPoint {
	public abstract double getValueOfDimensionN(int n);
	public abstract boolean isDimensionNtheHigherTheBetter(int n);
	public abstract int getDimensions();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int dim = getDimensions();
		for (int i = 0 ; i < dim ; i++) {
			if (isDimensionNtheHigherTheBetter(i)) {
				sb.append( "/\\");
			} else {
				sb.append("\\/");
			}
			sb.append(getValueOfDimensionN(i));
			if (i < dim-1) {
				sb.append (" \t ");
			}
		}
		return sb.toString();
	}

	public boolean dominates(ParetoPoint p) {
		int dim = this.getDimensions();
		if (dim != p.getDimensions()) throw new IllegalStateException();
		boolean domInOne = false;
		for (int i = 0 ; i < dim ; i++) {
			double thidV = this.getValueOfDimensionN(i);
			double altV = p.getValueOfDimensionN(i);
			if (isDimensionNtheHigherTheBetter(i)) {
				if (thidV < altV)
					return false;
				if (thidV > altV)
					domInOne = true;
			} else {
				if (thidV > altV)
					return false;
				if (thidV < altV)
					domInOne = true;
			}
		}
		return domInOne;
	}
}
