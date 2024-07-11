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
import java.util.Arrays;
import java.util.List;


public class MoreArrays {

	public static double max(double[] t) {
		double max = -Double.MAX_VALUE;
		for (int i = 0 ; i < t.length ; i++) {
			if (t[i] > max) max = t[i];
		}
		return max;
	}

	public static double min(double[] t) {
		if (t.length == 0) {
			return Double.MIN_VALUE;
		}		
		double min = Double.MAX_VALUE;
		for (int i = 0 ; i < t.length ; i++) {
			if (t[i] < min) min = t[i];
		}
		return min;
	}


	public static void main(String[] args) {
		System.out.println(Arrays.toString(exclude(new int[]{1, 2, 3 ,4 ,5,6,7,8}, 4)));
	}

	public static int[] exclude(int[] dimensions, int exception) {
		int[] newT = new int[dimensions.length-1];
		int index = 0;
		for (int i = 0 ; i < dimensions.length ; i++) {
			if (i == exception) continue;
			newT[index] = dimensions[i];
			index++;
		}
		return newT;
	}


	@SuppressWarnings("all")
	public static <T> ArrayList<T> getArrayList(T... t ) {
		int l = t.length;
		ArrayList<T> al = new ArrayList<T>(l);
		for (int i = 0 ; i < l ; i++) {
			al.add(t[i]);
		}
		return al;
	}

	public static int[] toIntArray(List<Integer> ff) {
		int[] a = new int[ff.size()];
		int index = 0;
		for (Integer g : ff) {
			a[index] = g;
			index++;
		}
		return a;
	}
}
