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
package ch.heiafr.isc.datacockpit.visualizer.global_gui;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.ResultDisplayService;
import ch.heiafr.isc.datacockpit.general_libraries.utils.DateAndTimeFormatter;
import ch.heiafr.isc.datacockpit.visualizer.display.panels.XYLineChartPanel;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider.AbstractChartPanel;
import ch.heiafr.isc.datacockpit.visualizer.display.panels.BarChartPanel;
import ch.heiafr.isc.datacockpit.visualizer.display.panels.HistChartPanel;
import ch.heiafr.isc.datacockpit.visualizer.display.panels.ParetoPanel;


public class DefaultResultDisplayingGUI implements ResultDisplayService {

	private AdvancedDataRetriever retriever;

	JFrame frame = null;

	private List<Class<? extends AbstractChartPanel>> displayerClasses;
	private ComplexDisplayPanel cmp;

	// Allows Tree service loader to work properly and leverage Service Provider Interface (SPI) pattern
	public DefaultResultDisplayingGUI() {
		super();
		this.displayerClasses = getDefaultDisplayers();
	}

	@Override
	public void displayResults(AdvancedDataRetriever retriever) {
		this.displayResults(retriever, "Data-viz", null, null, null, new String[]{});
	}

	@Override
	public void displayResults(AdvancedDataRetriever retriever, String title, String yAxis, String xAxis, String shape, String[] colors) {
		this.retriever = retriever;
		frame = new JFrame();
		cmp = new ComplexDisplayPanel(this.retriever, this.displayerClasses, title, frame);
		if (yAxis != null && xAxis != null && shape != null && colors != null) {
			cmp.setDefault(yAxis, xAxis, shape, colors);
		}
		frame.setTitle(title + " - " + DateAndTimeFormatter.getDateAndTime(System.currentTimeMillis()));
		frame.setJMenuBar(cmp.getMenubar());
		frame.setContentPane(cmp);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(new java.awt.Dimension(1000, 750));
		frame.setVisible(true);
		//	frame.setExtendedState(frame.MAXIMIZED_BOTH);

	}

	private static List<Class<? extends AbstractChartPanel>> getDefaultDisplayers() {
		List<Class<? extends AbstractChartProvider.AbstractChartPanel>> list = new ArrayList<Class<? extends AbstractChartProvider.AbstractChartPanel>>();
		list.add(XYLineChartPanel.class);
		list.add(BarChartPanel.class);
		list.add(HistChartPanel.class);
		list.add(ParetoPanel.class);
		return list;
	}
}
