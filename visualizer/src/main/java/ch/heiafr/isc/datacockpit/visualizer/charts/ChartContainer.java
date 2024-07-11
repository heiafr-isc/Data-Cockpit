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
package ch.heiafr.isc.datacockpit.visualizer.charts;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.jfree.chart.JFreeChart;

public class ChartContainer {
	private boolean multiple;
	private JFreeChart chart;
	private List<Problem> problems = new ArrayList<Problem>();
	
	// this reference is really ugly and should be removed in the future
	// the chart has a pointer to the problem label, so this panel can be refreshed whenever problems are added	
	public JLabel problemLabel;
	
	public void updateLabel() {
		if (problemLabel == null) return;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");

		for (Problem p : this.getProblems()) {
			sb.append("<font color=");
			if (p.getSeverity() == Problem.Severity.ERROR) {
				sb.append("#DC0000");
			} else if (p.getSeverity() == Problem.Severity.WARNING) {
				sb.append("#FFB400");
			} else if (p.getSeverity() == Problem.Severity.INFORMATION) {
				sb.append("#3491C0");
			} else {
				sb.append("black");
			}

			sb.append(">");

			sb.append(p.getMessage());

			sb.append("</font><br>");
		}
		sb.append("</html>");

		problemLabel.setText(sb.toString());		
	}

	public void setMultiple(boolean b) {
		multiple = b;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public List<Problem> getProblems() {
		return problems;
	}

	public void addProblem(Problem p) {
		if (!problems.contains(p)) {
			problems.add(p);
		}
	}	
}
