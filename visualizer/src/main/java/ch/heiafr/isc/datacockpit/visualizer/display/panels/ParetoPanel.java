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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Texture;
import ch.heiafr.isc.datacockpit.visualizer.global_gui.ComplexDisplayPanel;
import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataRetrievalOptions;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider;
import ch.heiafr.isc.datacockpit.visualizer.display.ParetoProvider;
import ch.heiafr.isc.datacockpit.visualizer.display.TreeLegend;
import ch.heiafr.isc.datacockpit.visualizer.display.XYChartProvider;

public class ParetoPanel extends AbstractChartProvider.AbstractChartPanel implements ActionListener, ChangeListener {

	private ActionListener superListener;	
	
	private JCheckBox xIsHigherBetter;
	private JCheckBox yIsHigherBetter;
	private JCheckBox yLog;
	private JCheckBox xLog;
	JButton matlab;
	
	public ParetoPanel(AdvancedDataRetriever retriever, ActionListener listener) {
		super(new ParetoProvider(), retriever);
		
		superListener = listener;
		
		this.xIsHigherBetter = new JCheckBox("xIsHigherBetter");
		this.xIsHigherBetter.addActionListener(listener);
		this.xIsHigherBetter.addActionListener(this);
		this.yIsHigherBetter = new JCheckBox("yIsHigherBetter");
		this.yIsHigherBetter.addActionListener(listener);
		this.yIsHigherBetter.addActionListener(this);		
		this.xLog = new JCheckBox("Log x");
		this.xLog.addActionListener(listener);
		this.xLog.addActionListener(this);	
		this.yLog = new JCheckBox("Log y");
		this.yLog.addActionListener(listener);
		this.yLog.addActionListener(this);	
	}
	
	@Override
	public Object getLegend() {
		XYChartProvider xyDisp = (XYChartProvider)displayer;
		JTree tl = TreeLegend.createTreeLegend(xyDisp.seriesPaint, xyDisp.seriesShape, new HashMap<Pair<String, String>, Texture>(), xyDisp.legends);
		JScrollPane jp = new JScrollPane((JComponent) tl);
		jp.setVisible(true);
		return jp;
	}	

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		superListener.actionPerformed(new ActionEvent(e.getSource(), 0, ""));		
	}

	@Override
	public JPanel getConfigurationPanel(final ComplexDisplayPanel fatherPanel) {
		
		
		
		JPanel optionPanel = new JPanel(new FlowLayout());
		
		optionPanel.add(xIsHigherBetter);
		optionPanel.add(yIsHigherBetter);
		optionPanel.add(xLog);		
		optionPanel.add(yLog);
		matlab = new JButton("Create matlab");
		optionPanel.add(matlab);
		matlab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				XYChartProvider xyDisp = (XYChartProvider)displayer;
				try {
					xyDisp.createMatlabData(fatherPanel);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		return optionPanel;
	}

	@Override
	public String getDecription() {
		return "Pareto chart";
	}

	@Override
	public String getSecondOption() {
		return "Shape";
	}

	@Override
	public Pair<ActionListener[], String[]> getDisplayerPossibleActions(ComplexDisplayPanel fatherPanel) {
		
		return new Pair<ActionListener[], String[]>(new ActionListener[]{}, new String[]{});
	}

	@Override
	public ChartContainer computeChart(DataRetrievalOptions options) {
		ParetoProvider disp = (ParetoProvider)displayer;
		disp.xHigherBetter = xIsHigherBetter.isSelected();
		disp.yHigherBetter = yIsHigherBetter.isSelected();
		disp.isLogX = xLog.isSelected();
		disp.isLogY = yLog.isSelected();
		return disp.computeChart(options, retriever);
	}

}
