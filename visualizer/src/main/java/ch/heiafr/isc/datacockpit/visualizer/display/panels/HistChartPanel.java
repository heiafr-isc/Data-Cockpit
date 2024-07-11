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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import ch.heiafr.isc.datacockpit.visualizer.global_gui.ComplexDisplayPanel;
import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataRetrievalOptions;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider;
import ch.heiafr.isc.datacockpit.visualizer.display.HistChartProvider;

public class HistChartPanel extends AbstractChartProvider.AbstractChartPanel implements ActionListener, ChangeListener {

	private ActionListener superListener;	
	
	private JSlider min;
	private JSlider max;
	private JSlider bins;
	
	private JTextField minSet;
	private JTextField maxSet;
	private JTextField binSet;

	private JLabel minLabel;
	private JLabel maxLabel;
	private JLabel binsLabel;	
	
	public HistChartPanel(AdvancedDataRetriever retriever, ActionListener listener) {
		super(new HistChartProvider(), retriever);

		superListener = listener;
		this.fillListGraphs2();

		min = new JSlider(SwingConstants.HORIZONTAL);
		min.setMinimum(1);
		min.setMaximum(50000);
		min.setValue(14000);
		min.addChangeListener(this);
		max = new JSlider(SwingConstants.HORIZONTAL);
		max.setMinimum(1);
		max.setMaximum(50000);
		max.setValue(33000);
		max.addChangeListener(this);
		bins = new JSlider(SwingConstants.HORIZONTAL);
		bins.setMinimum(1);
		bins.setMaximum(100);
		bins.setValue(25);
		bins.addChangeListener(this);
		
		minSet = new JTextField(10);
		minSet.addActionListener(this);
		maxSet = new JTextField(10);
		maxSet.addActionListener(this);
		binSet = new JTextField(10);
		binSet.addActionListener(this);
		

		this.minLabel = new JLabel("" + this.min.getValue());
		this.maxLabel = new JLabel("" + this.max.getValue());
		this.binsLabel = new JLabel("" + this.bins.getValue());
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == this.min) {
			this.minLabel.setText("" + this.min.getValue());
			minSet.setText("" + this.min.getValue());
		} else if (e.getSource() == this.max) {
			this.maxLabel.setText("" + this.max.getValue());
			maxSet.setText("" + this.max.getValue());
		} else if (e.getSource() == this.bins) {
			this.binsLabel.setText("" + this.bins.getValue());
			binSet.setText("" + this.bins.getValue());
		}
		autoTriggered = true;
		superListener.actionPerformed(new ActionEvent(e.getSource(), 0, ""));
		
	}
	
	private boolean autoTriggered = false;
	
	@Override
	public JPanel getConfigurationPanel(ComplexDisplayPanel panel) {
		JPanel optionPanel = new JPanel(new GridLayout(6,1));
		JPanel pan1 = new JPanel(new FlowLayout());
		JPanel pan2 = new JPanel(new FlowLayout());
		JPanel pan3 = new JPanel(new FlowLayout());
		
		pan1.add(this.minLabel);
		pan1.add(min);
		pan1.add(minSet);
		pan2.add(this.maxLabel);
		pan2.add(max);
		pan2.add(maxSet);
		pan3.add(this.binsLabel);
		pan3.add(bins);
		pan3.add(binSet);
		
		
		optionPanel.add(new JLabel("Min"));
		optionPanel.add(pan1);
		optionPanel.add(new JLabel("Max"));
		optionPanel.add(pan2);
		optionPanel.add(new JLabel("Bins"));
		optionPanel.add(pan3);
		return optionPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		superListener.actionPerformed(new ActionEvent(e.getSource(), 0, ""));		
	}
	
	@Override
	public Object getLegend() {
		//JTree tl = TreeLegend.createTreeLegend(seriesPaint, seriesShape,
		//	new HashMap<String, Texture>(), shapeDefinition);
		//JScrollPane jp = new JScrollPane((JComponent)tl);
		JScrollPane jp = new JScrollPane();
		jp.setVisible(true);
		return jp;
	}

	@Override
	public Pair<ActionListener[], String[]> getDisplayerPossibleActions(ComplexDisplayPanel panel) {
		ActionListener excel = new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				HistChartProvider disp = (HistChartProvider)displayer;
				disp.createExcelData();
			}
		};

		return new Pair<ActionListener[], String[]>(
				new ActionListener[] { excel },
				new String[] { "Export displayed series as excel sheet" });
	}
	
	public ChartContainer computeChart(DataRetrievalOptions options) {
		HistChartProvider disp = (HistChartProvider)displayer;
		if (autoTriggered) {
			if (!binSet.getText().equals(""))
				disp.binSet = binSet.getText();
			if (!maxSet.getText().equals(""))		
				disp.maxSet = maxSet.getText();
			if (!minSet.getText().equals(""))
				disp.minSet = minSet.getText();
			autoTriggered = false;
		} else {
			disp.binSet = "";
			disp.maxSet = "";
			disp.minSet = "";
			binSet.setText("");
			minSet.setText("");
			maxSet.setText("");
		}
		ChartContainer chartC = disp.computeChart(options, retriever);

		if (binSet.getText().equals(""))	{	
			binSet.setText(disp.binSet);
		}
		if (maxSet.getText().equals("") && disp.maxSet != null) {
			maxSet.setText(disp.maxSet);
			double val = Double.parseDouble(disp.maxSet);
			max.setMaximum((int)(val *1.3));
			max.setValue((int)val);			
		}
		if (minSet.getText().equals("") && disp.minSet != null)	{				
			minSet.setText(disp.minSet);
			double val = Double.parseDouble(disp.minSet);
			min.setMaximum((int)(val *1.3));
			min.setValue((int)val);
		}
		return chartC;
	}

	@Override
	public String getDecription() {
		return "Histogram chart";
	}

	@Override
	public String getSecondOption() {
		// TODO Auto-generated method stub
		return null;
	}	
	

}
