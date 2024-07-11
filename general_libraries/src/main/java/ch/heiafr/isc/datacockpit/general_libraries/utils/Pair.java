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

import java.io.Serializable;


public class Pair<A,B> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	A object1 = null;
	B object2 = null;


	public Pair(A a, B b) {
		object1 = a;
		object2 = b;
	}

	public Pair() {
	}

	public A getFirst() {
		return object1;
	}

	public B getSecond() {
		return object2;
	}

	public void setFirst(A a) {
		object1 = a;
	}

	public void setSecond(B b) {
		object2 = b;
	}

	@Override
	public String toString() {
		String s = object1 == null ? "null" : object1.toString();
		String s2 = object2 == null ? "null" : object2.toString();
		return "<" + s + ":" + s2 + ">";
	}

	@Override
	public int hashCode() {
		int tot = (object1 == null) ? 0 : object1.hashCode();
		tot += (object2 == null) ? 0 : object2.hashCode();
		return tot;
		/*	final int prime = 31;
		int result = 1;
		result = prime * result + ((object1 == null) ? 0 : object1.hashCode());
		result = prime * result + ((object2 == null) ? 0 : object2.hashCode());
		return result;*/
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair other = (Pair) obj;
		if (object1 == null) {
			if (other.object1 != null) {
				return false;
			}
		} else if (!object1.equals(other.object1)) {
			return false;
		}
		if (object2 == null) {
			if (other.object2 != null) {
				return false;
			}
		} else if (!object2.equals(other.object2)) {
			return false;
		}
		return true;
	}
}
