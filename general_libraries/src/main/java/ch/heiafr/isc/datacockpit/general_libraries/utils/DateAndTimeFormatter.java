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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTimeFormatter {

	public static String getDateAndTime(long l) {
		Date date = new Date(l);
		SimpleDateFormat f1 = new SimpleDateFormat("_yyyy_MM_dd");
		SimpleDateFormat f2 = new SimpleDateFormat("'at'_HH_mm_ss");
		return f1.format(date)+"_"+f2.format(date);
	}

	public static String getDate(long l) {
		Date date = new Date(l);
		SimpleDateFormat f1 = new SimpleDateFormat("_yyyy_MM_dd");
		return f1.format(date);
	}

}
