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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
	
	public static Map<String, Float> getMap(Collection<String> e, boolean useLog) {
		// try with numbers
		boolean canNumber = true;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		try {
			for (String s : e) {
				double val = Double.parseDouble(s);
			/*	if (val < 1e-18 || val > 1e9) {
					System.out.print("h");
				}*/
				if (useLog) val = Math.log(val);
				if (val < min) {
					min = val;
				}
				if (val > max) {
					max = val;
				/*	if (max > 0) {
						System.out.print(".");
					}*/
				}
			}
		}
		catch (Exception exce) {
			canNumber = false;
		}
		HashMap<String, Float> result = new HashMap<String, Float>();
		if (canNumber) {
			double diff = max-min;
			for (String s : e) {
				double val = Double.parseDouble(s);
				if (useLog) val = Math.log(val);				
				float frac = (float)((val - min)/diff);
				result.put(s, frac);
			}
		} else {
			ArrayList<String> s = new ArrayList<String>(e);
			Collections.sort(s);
			float size = s.size();
			float i = 0;
			for (String str : s) {
				result.put(str, i/size);
				i++;
			}
		}
		return result;
	}

}
