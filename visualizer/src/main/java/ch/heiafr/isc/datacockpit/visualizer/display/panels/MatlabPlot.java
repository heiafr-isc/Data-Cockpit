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
package ch.heiafr.isc.datacockpit.visualizer.display.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import ch.heiafr.isc.datacockpit.general_libraries.utils.PairList;

public class MatlabPlot {
//	public ArrayList<Double> xVector;
//	public ArrayList<Double> yVector;
//	public ArrayList<String> xLabelVector;
//	public ArrayList<Double> error;	
	public ArrayList<Entry> entries;
	public PairList<String, String> legend;
	public boolean xText;
	
	public boolean hasError;
	
	public static class Entry implements Comparable<Entry>{
		public Entry(double x, String xText, double y, double error) {
			this.x = x;
			this.xLabel = xText;
			this.y = y;
			this.error = error;
		}
		public double x;
		public String xLabel;
		public double y;
		public double error;
		@Override
		public int compareTo(Entry o) {
			return (int)Math.signum(this.x - o.x);
		}
	}
	
	
	public MatlabPlot(PairList<String, String> l, boolean xText) {
		entries = new ArrayList<Entry>();
		legend = l;
		this.xText = xText;
	}
	
	
	public void addxandyAndError(String xValOrString, double xVal, double yVal,
			double d) {
		Entry e;
		if (xText) {
			e = new Entry(xVal, xValOrString, yVal, d);
		} else {
			e = new Entry(Double.parseDouble(xValOrString), xValOrString, yVal, d);
		}
		entries.add(e);
	}	
	
	public static class MATLABPlotMap {

		public MATLABPlotMap() {
			plots = new ArrayList<MatlabPlot>();
		}

		public ArrayList<MatlabPlot> plots;

		public void add(MatlabPlot plot) {
			plots.add(plot);
		}

		public boolean containsPlot(PairList<String, String> other) {

			for( MatlabPlot p : plots ) {
				if( p.legend.equals(other) )
					return true;
			}

			return false;
		}

		public MatlabPlot getPlot(PairList<String, String> legend) {
			if( plots.size() == 0 || !containsPlot(legend) ) {
				return null;
			}

			for( MatlabPlot p : plots ) {
				if( p.legend.equals(legend) )
					return p;
			}

			return null;
		}

		public Iterator<MatlabPlot> getIterator() {
			return plots.iterator();
		}
	}


	public void sort() {
		Collections.sort(entries);
	}


}
