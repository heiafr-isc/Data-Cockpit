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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Texture;
import ch.heiafr.isc.datacockpit.visualizer.global_gui.ComplexDisplayPanel;
import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataRetrievalOptions;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider.AbstractChartPanel;
import ch.heiafr.isc.datacockpit.visualizer.display.XYChartProvider;
import ch.heiafr.isc.datacockpit.visualizer.display.TreeLegend;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider.Description;

public class XYLineChartPanel extends AbstractChartPanel implements ActionListener {
	
	public final static Font defaultFont = new Font("Helvetica", 0, 9);

	private JPanel optionPanel;
	private JCheckBox logScaleX;
	private JCheckBox logScaleY;
	private JCheckBox noScaleX;
	private JCheckBox sortOnY;
	private JCheckBox continuousColors;
	private JCheckBox logColors;
	private JCheckBox useLegend;
	private JCheckBox normaliseWithX;
	private JCheckBox withoutIdenticalHigh;
	private ButtonGroup lines;
	private JRadioButton withLines;
	private JRadioButton withoutLines;
	private JRadioButton withLinesWA;
	private JCheckBox sameAxis;
	private boolean withLinesSelectedBeforeChange = true;

	private JCheckBox scalarProduct;
	private JCheckBox twoMethods;
	
	public XYLineChartPanel(AdvancedDataRetriever retriever, ActionListener listener) {
		super(new XYChartProvider(), retriever);

		this.logScaleX = new JCheckBox("Log scale on X");
		this.logScaleX.addActionListener(listener);
		this.logScaleX.addActionListener(this);
		this.logScaleY = new JCheckBox("Log scale on Y");
		this.logScaleY.addActionListener(listener);
		this.noScaleX = new JCheckBox("No scale on X");
		this.noScaleX.addActionListener(listener);
		this.noScaleX.addActionListener(this);
		this.sortOnY = new JCheckBox("Sort values according to val");
		this.sortOnY.addActionListener(listener);
		this.continuousColors = new JCheckBox("Continuous colors");
		this.continuousColors.addActionListener(listener);
		this.logColors = new JCheckBox("Use log colors?");
		this.logColors.addActionListener(listener);
		this.useLegend = new JCheckBox("Use legend");
		this.useLegend.addActionListener(listener);
		this.withoutIdenticalHigh = new JCheckBox("Without idem highlights");
		this.withoutIdenticalHigh.addActionListener(listener);
		this.normaliseWithX = new JCheckBox("Normalise with X axis");
		this.normaliseWithX.addActionListener(listener);
		this.lines = new ButtonGroup();
		this.withLines = new JRadioButton("Lines");
		this.withLines.addActionListener(listener);
		this.withLinesWA = new JRadioButton("Non ave. lines");
		this.withLinesWA.addActionListener(listener);
		this.withoutLines = new JRadioButton("No lines");
		this.withoutLines.addActionListener(listener);
		this.lines.add(withLines);
		this.lines.add(withLinesWA);
		this.lines.add(withoutLines);
		this.withLines.setSelected(true);
		this.sameAxis = new JCheckBox("Same Axis");
		this.sameAxis.addActionListener(listener);
		this.sameAxis.setEnabled(false);
		this.twoMethods = new JCheckBox();
		this.twoMethods.addActionListener(listener);
		this.twoMethods.addActionListener(this);
		this.scalarProduct = new JCheckBox("Calculate scalar product");
		this.scalarProduct.setEnabled(false);
		this.scalarProduct.addActionListener(listener);
		this.scalarProduct.addActionListener(this);
		this.listGraphs2 = new JComboBox();
		this.listGraphs2.setEnabled(false);
		this.listGraphs2.addActionListener(listener);
		listGraphs2.setFont(defaultFont);
		this.fillListGraphs2();
		this.createOptionPanel();
	}

	@Override
	public JPanel getConfigurationPanel(ComplexDisplayPanel panel) {
		return this.optionPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if( source == this.twoMethods ) {
			boolean selected = this.twoMethods.isSelected();
			this.listGraphs2.setEnabled(selected);
			this.scalarProduct.setEnabled(selected);
			if( !selected )
				this.scalarProduct.setSelected(false);
			this.sameAxis.setEnabled(selected);
		} else if( source == this.scalarProduct ) {
			boolean selected = this.scalarProduct.isSelected();
			if( selected )
				this.withLinesSelectedBeforeChange = this.withLines.isSelected();
			this.withoutLines.setSelected(selected);
			this.withLines.setEnabled(!selected);
			this.withLines.setSelected(!selected && this.withLinesSelectedBeforeChange);
		} else if( source == this.logScaleX ) {
			if( this.logScaleX.isSelected() )
				this.noScaleX.setSelected(false);
		} else if( source == this.noScaleX ) {
			if( this.noScaleX.isSelected() )
				this.logScaleX.setSelected(false);
		} else if (source == continuousColors) {
			logColors.setEnabled(continuousColors.isSelected());
		}
	}

	public Pair<ActionListener[], String[]> getDisplayerPossibleActions(final ComplexDisplayPanel panel) {
		ActionListener excel = new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				XYChartProvider xyDisp = (XYChartProvider)displayer;
				try {
					xyDisp.createExcelData(retriever);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error during exportation of the file : \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		ActionListener matlab = new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				XYChartProvider xyDisp = (XYChartProvider)displayer;
				try {
					xyDisp.createMatlabData(panel);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error creating MATLAB figure script : \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		return new Pair<ActionListener[], String[]>(new ActionListener[] { excel, matlab }, new String[] { "Export displayed series as excel sheet", "export as matlab" });
	}

	@Override
	public Object getLegend() {
		XYChartProvider xyDisp = (XYChartProvider)displayer;
		JTree tl = TreeLegend.createTreeLegend(xyDisp.seriesPaint, xyDisp.seriesShape, new HashMap<Pair<String, String>, Texture>(), xyDisp.legends);
		JScrollPane jp = new JScrollPane((JComponent) tl);
		jp.setVisible(true);
		return jp;
	}

	private void createOptionPanel() {
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 0;
		gb1.gridy = 0;
		gb1.gridwidth = 3;
		gb1.anchor = GridBagConstraints.WEST;
		GridBagConstraints gb2 = new GridBagConstraints();
		gb2.gridx = 0;
		gb2.gridy = 1;
		gb2.anchor = GridBagConstraints.WEST;
		/*	GridBagConstraints gb3 = new GridBagConstraints();
			gb3.gridx = 0;
			gb3.gridy = 2;
			gb3.anchor = GridBagConstraints.WEST;*/
		GridBagConstraints gb4 = new GridBagConstraints();
		gb4.gridx = 1;
		gb4.gridy = 1;
		gb4.anchor = GridBagConstraints.WEST;
		GridBagConstraints gb5 = new GridBagConstraints();
		gb5.gridx = 2;
		gb5.gridy = 1;
		gb5.anchor = GridBagConstraints.WEST;

		this.optionPanel = new JPanel(/*new GridBagLayout()*/);
		JPanel pan1 = new JPanel(new FlowLayout());
		pan1.add(this.twoMethods);
		pan1.add(this.listGraphs2);
		pan1.add(this.scalarProduct);
		JPanel pan2 = new JPanel(new GridLayout(5, 1));
		pan2.add(this.sameAxis/*, gb4*/);
		pan2.add(this.noScaleX);
		pan2.add(this.logScaleX);
		pan2.add(this.logScaleY);
		pan2.add(this.useLegend);
		JPanel pan4 = new JPanel(new GridLayout(3, 1));
		pan4.add(this.withLines);
		pan4.add(this.withLinesWA);
		pan4.add(this.withoutLines);
		JPanel pan5 = new JPanel(new GridLayout(5, 1));
		pan5.add(this.withoutIdenticalHigh);
		pan5.add(this.normaliseWithX);
		pan5.add(this.sortOnY);
		pan5.add(this.continuousColors);
		pan5.add(this.logColors);
		JPanel viewPanel = new JPanel(new GridBagLayout());
		viewPanel.add(pan1, gb1);
		viewPanel.add(pan2, gb2);
		viewPanel.add(pan4, gb4);
		viewPanel.add(pan5, gb5);
		this.optionPanel.add(viewPanel);

	}

	@Override
	public String getDecription() {
		return "YX Line Chart";
	}

	@Override
	public String getSecondOption() {
		return "Shape";
	}	

	public ChartContainer computeChart(DataRetrievalOptions options) {
		XYChartProvider xyDisp = (XYChartProvider)displayer;
		xyDisp.isLogX = logScaleX.isSelected();
		xyDisp.isLogY = logScaleY.isSelected();
		xyDisp.isSameAxis = sameAxis.isSelected();
		xyDisp.isScalar = scalarProduct.isSelected();
		xyDisp.isNormalisedWithX = normaliseWithX.isSelected();
		xyDisp.isNoScaleX = noScaleX.isSelected();
		xyDisp.isSortOnY = sortOnY.isSelected();
		xyDisp.isTwoMethods = twoMethods.isSelected();
		xyDisp.isWithLines = withLines.isSelected();
		xyDisp.isWithLinesWA = withLinesWA.isSelected();
		xyDisp.isWithoutIdentitical = withoutIdenticalHigh.isSelected();
		xyDisp.isWithoutLines = withoutLines.isSelected();
		xyDisp.isWithContinuousColors = continuousColors.isSelected();
		xyDisp.isLogColors = logColors.isSelected();
		xyDisp.isUsingLegend = useLegend.isSelected();
		Description d = (Description) this.listGraphs2.getSelectedItem();
		options.method = new String[]{options.method[0], d== null ? "" : d.nom};
		return xyDisp.computeChart(options, retriever);
	}
}
