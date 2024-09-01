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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.heiafr.isc.datacockpit.general_libraries.utils.DateAndTimeFormatter;

public class Execution implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String application;
	private final String version;
	private final List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	private final String date;

	public Execution(String application, String version, String date) {
		this.application = application;
		this.version = version;
		this.date = date;
	}

	public Execution(String application, String version) {
		this.application = application;
		this.version = version;
		this.date = DateAndTimeFormatter.getDate(System.currentTimeMillis());
	}
	
	public Execution() {
		this.application = "Unnamed application";
		this.version = "version 0.0";
		this.date = DateAndTimeFormatter.getDate(System.currentTimeMillis());
	}


	public void addDataPoint(DataPoint p) {
		if (p == null) {
			throw new NullPointerException();
		}
		dataPoints.add(p);
	}

	public List<DataPoint> getDataPoints() {
		return dataPoints;
	}

}