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

import ch.heiafr.isc.datacockpit.general_libraries.results.*;
import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomChartPanel;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider;
import ch.heiafr.isc.datacockpit.visualizer.display.AbstractChartProvider.AbstractChartPanel;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

public class ComplexDisplayPanel extends JPanel implements ActionListener,
		ItemListener, ChangeListener, WindowListener {

	private static final long serialVersionUID = 4303119875318458208L;

	private String title;

	private JButton compute = null;
	private JLabel computing = null;
	private JPanel jPanel2 = null;
	private JLabel labelXAxis = null;
	private JLabel labelShape = null;
	private JComboBox xAxis = null;
	private JComboBox shapeComboBox = null;

	private AbstractChartPanel activeDisplayer = null;
	private JPanel globalOptionPanel = null;
	private JComboBox listGraphs = null;
	private JPanel operationWhenMultiple1 = null;
	private JPanel operationWhenMultiple2 = null;
	
	private JCheckBox meanOrSumLineCheckBox;
	private JCheckBox medianLineCheckBox;
	private JCheckBox firstLineCheckBox;
	
	private JCheckBox confIntCheckBox;
	private JCheckBox quartIntCheckBox;
	private JCheckBox maxIntCheckBox;
	
//	private JCheckBox[] rangeList = new JCheckBox[3];
//	private JCheckBox[] lineList = new JCheckBox[3];
	private JCheckBox seeOutputs;	
	private ChartPanel chartPanel = null;
	private JComponent legendPanel = null;
	private JPanel constantPanel = null;
	private JPanel filterPanel = null;
	private JFrame parentFrame = null;

//	private JScrollPane jScrollPane = null;
//	private JPanel problemFilterAndConstantPanel__ = null;
	private Map<String, JList> mapList = null;
	private boolean show = true;
	private JPanel firstMedianMeanEtcPanel = null;
	private JPanel computeButtonPanel = null;
	private JCheckBox autoCompute = null;

	private JLabel problemLabel = null;
	private JTabbedPane displayTabsPanel = null;
	private JTabbedPane controlPanel = null;
	private JList listColor = null;
	private JScrollPane jScrollPane1 = null;

	private int confidenceInterval = 95;
	private boolean is95 = false;
	private boolean mean = true;

	public final static Font defaultFont = new Font("Helvetica", 0, 11);

	private Object lock = new Object();

	private List<AbstractChartPanel> displayers;
	private List<Class<? extends AbstractChartPanel>> displayerClasses;
	private HashMap<String, String> quoteWithoutQuoteEquivalence = new HashMap<String, String>();	
	private AdvancedDataRetriever retriever;
	
	private JFrame legend = null;
	
	Vector<String> varProperties;
	
	
	
//	Vector<String> constProperties;
//	Vector<String> constValues;
	
	Vector<Pair<String, String>> constants;
	
	@SuppressWarnings("unchecked")
	private void updateProperties(boolean checked) {
		String yText = getYAxisText();
		if (yText == null) {
		//	constProperties = new Vector<String>(0);
			varProperties = new Vector<String>(0);
			constants = new Vector<Pair<String, String>>(0);			
			return;
		}
		constants.clear();
		Vector[] v = retriever.getVariableAndConstantPropertiesForGivenMetric(getYAxisText());
		
		varProperties = (Vector<String>)v[0];
		//constProperties = (Vector<String>)v[1];
		//constValues = (Vector<String>)v[2];
		for (int i = 0 ; i < v[1].size() ; i++) {
			constants.add(new Pair(v[1].get(i), v[2].get(i)));
		}
		
			
		for (Iterator<String> it = varProperties.iterator() ; it.hasNext() ; ) {
			String s = it.next();
			if (checked == false && retriever.isInput(s) == false) {
				it.remove();
			}
			String ss = s.replaceAll("\"", "");
			quoteWithoutQuoteEquivalence.put(ss, s);
		}
		
		for (Iterator<Pair<String,String>> it = constants.iterator() ; it.hasNext() ; ) {
			Pair<String,String> s = it.next();
			if (checked == false && retriever.isInput(s.getFirst()) == false) {
				it.remove();
			}
			String ss = s.getFirst().replaceAll("\"", "");
			quoteWithoutQuoteEquivalence.put(ss, s.getFirst());
		}		

	}		

	public ComplexDisplayPanel(AdvancedDataRetriever a, List<Class<? extends AbstractChartPanel>> displayerClasses, String title, JFrame frame) {
		super();

		this.parentFrame = frame;
		frame.addWindowListener(this);
		this.title = title;
		this.retriever = a;
		this.displayerClasses = displayerClasses;
		this.displayers = new ArrayList<AbstractChartPanel>(this.displayerClasses.size());
		Object[] params = {this.retriever, this};
		for (Class<? extends AbstractChartPanel> disp : this.displayerClasses){
			try {
				if (!org.apache.bcel.Repository.lookupClass(disp).isAbstract()) {
					AbstractChartPanel newDisp = disp.getConstructor(AdvancedDataRetriever.class, ActionListener.class).newInstance(params);
					this.displayers.add(newDisp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (this.displayers.size() == 0) {
			throw new RuntimeException("No displayer found!");
		}
		
		chartPanel = new CustomChartPanel(null, 150, 150, 150, 150, 1600, 1400,
				false, true, // properties
				true, // save
				true, // print
				true, // zoom
				false); // tooltips
		
		this.activeDisplayer = this.displayers.get(0);
		
		constantPanel = new JPanel();
		BoxLayout bl = new BoxLayout(constantPanel, BoxLayout.Y_AXIS);
		constantPanel.setLayout(bl);		
		
		filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));		
		
		legendPanel = new JPanel();
		legendPanel.setLayout(new FlowLayout());
		
		this.computing = new JLabel("Idle");		
		
		compute = new JButton("Compute");
		compute.addActionListener(this);
		
		autoCompute = new JCheckBox("Compute automatically");
		autoCompute.setSelected(true);
		
		computeButtonPanel = new JPanel(new GridLayout(1, 3));
		computeButtonPanel.add(autoCompute);
		computeButtonPanel.add(compute);
		computeButtonPanel.add(this.computing);		
		
		this.setLayout(new BorderLayout());
		problemLabel = new JLabel("");
		problemLabel.setFont(defaultFont);
		
		this.shapeComboBox = new JComboBox();
		this.xAxis = new JComboBox();
		
		ItemListener itemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					return;
				}
				autoCompute();
			}
		};	
		
		seeOutputs = new JCheckBox("Include outputs");
		seeOutputs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetAFC();
			}
		});		

		xAxis.addItemListener(itemListener);
		shapeComboBox.addItemListener(itemListener);		
		
		//	this.setSize(new Dimension(800, 700));
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JScrollPane options = new JScrollPane(getGeneralOptionPanel());
		JScrollPane chart = new JScrollPane(chartPanel);
		split.add(options);
		split.add(chart);
		split.setDividerLocation(getGeneralOptionPanel().getPreferredSize().width+200);
		this.add(split, BorderLayout.CENTER);

		quoteWithoutQuoteEquivalence.clear();
		quoteWithoutQuoteEquivalence.put("", "");
		quoteWithoutQuoteEquivalence.put(AbstractDataRetriever.CONSTANT, AbstractDataRetriever.CONSTANT);
		
		resetAFC();
		resetXFC();
	}

	private Vector<String> getYAxisValues(boolean checked) {
		Vector<String> list = new Vector<String>();
		if (checked) {
			list.addAll(activeDisplayer.getMetrics());
		} else {
			for (String s : activeDisplayer.getMetrics()) {
				if (retriever.isInput(s) == false) {
					list.add(s);
				}
			}
		}
		return list;
	}

	private void setYAxisList() {
		String selected = getYAxisText();
		Vector<String> list = getYAxisValues(seeOutputs.isSelected());
		java.util.Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		getYAxis().setModel(new DefaultComboBoxModel(list));
		getYAxis().setMaximumRowCount(list.size());
		for (String l : list) {
			if (l.equals(selected)) {
				getYAxis().setSelectedItem(l);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void resetXShapeAndColor__() {
	//	Vector<String> listProperties = getVariablePropertiesList(getSeeOutputs().isSelected());
		Vector<String> listProperties = (Vector<String>)varProperties.clone();
	
		listProperties.add(0, "");
		Object Xselected = this.xAxis.getSelectedItem();
		Object shapeSelected = this.shapeComboBox.getSelectedItem();
		this.xAxis.setModel(new DefaultComboBoxModel(listProperties));
		this.shapeComboBox.setModel(new DefaultComboBoxModel(listProperties));
		// if (allOne) {
		Vector<String> alt = (Vector<String>) listProperties.clone();
		alt.add(AbstractDataRetriever.CONSTANT);
		Object[] selection = getListColor().getSelectedValues();
		getListColor().setListData(alt);
		validate();
		for (String l : listProperties) {
			if (l.equals(Xselected)) {
				this.xAxis.setSelectedItem(l);
			}
		}
		for (String l : listProperties) {
			if (l.equals(shapeSelected)) {
				this.shapeComboBox.setSelectedItem(l);
			}
		}
		for (int i = 0 ; i < selection.length ; i++) {
			javax.swing.ListModel mod = getListColor().getModel();
			for (int j = 0 ; j < mod.getSize() ; j++) {
				if (selection[i].equals(mod.getElementAt(j))) {
					getListColor().setSelectedIndex(j);
				}
			}
		}
	}

	private void resetAxesAndShape__() {
		setYAxisList();
		resetXShapeAndColor__();
		validate();
	}

	private void resetConstants__() {
		constantPanel.removeAll();
		for (int i = 0 ; i < constants.size() ; i++) {
			Pair<String, String> p = constants.get(i);
			final JLabel label = new JLabel(p.getFirst() + " = " + p.getSecond());
			label.setFont(defaultFont);

			constantPanel.add(label);			
		}
		constantPanel.validate();
	}

	private void resetFilterPanel__() {
		// backup previous selection
		TreeMap<String, Object[]> backupMap = null;
		if (mapList != null) {
			backupMap = new TreeMap<String, Object[]>();
			for (Map.Entry<String, JList> entry : mapList.entrySet()) {
				JList l = entry.getValue();
				Object[] selected =l.getSelectedValues();
				backupMap.put(entry.getKey(), selected);
			}
		}
		
		Map<String, List<String>> save = getFilterSelection();
		filterPanel.removeAll();
		mapList = new HashMap<String, JList>();
		JPanel internPanel = new JPanel(new GridBagLayout());
		JPanel labs = new JPanel();
		BoxLayout bl = new BoxLayout(labs, BoxLayout.Y_AXIS);
		labs.setLayout(bl);
		filterPanel.add(internPanel);
		filterPanel.add(labs);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		for (String s : varProperties) {
			Set<String> st = activeDisplayer.getPossibleValuesOfGivenProperty(s);

			String ss = s.replaceAll("\"", "");

			final JLabel label = new JLabel(ss);
			final JTextField textField = new JTextField("*");
			final JPopupMenu pm = new JPopupMenu();
			final JList listPossibilities = new JList(st.toArray(new String[st.size()]));
			JScrollPane scrollPane = new JScrollPane(listPossibilities);

			List<String> ls = save.get(s);
			if (ls != null) {
				ArrayList<Integer> toselect = new ArrayList<Integer>();
				int index = 0;
				for (String potentiallySelected : st) {
					if (ls.contains(potentiallySelected)) {
						toselect.add(index);
					}
					index++;
				}
				int[] selection = new int[toselect.size()];
				for (int i = 0; i < toselect.size() ; i++) {
					selection[i] = toselect.get(i);
				}
				listPossibilities.setSelectedIndices(selection);
			}

			mapList.put(s, listPossibilities);

			pm.setLayout(new BorderLayout());
			pm.add(scrollPane, BorderLayout.CENTER);

			pm.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					show = textField.getMousePosition() == null;
				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
				}
			});

			textField.setEditable(false);
			textField.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (show) {
						pm.setPreferredSize(new Dimension(textField.getWidth(),(int) pm.getPreferredSize().getHeight()));
						pm.show(textField, 0, textField.getHeight());
					} else {
						show = true;
					}

				}
			});

			listPossibilities.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (listPossibilities.getSelectedIndex() == -1) {
						textField.setText("*");
					} else {
						Object[] ol = listPossibilities.getSelectedValues();
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < ol.length; i++) {
							sb.append(ol[i]);
							if (i != ol.length - 1) {
								sb.append(", ");
							}
						}
						textField.setText(sb.toString());
					}

					autoCompute();
				}
			});

			listPossibilities.setSelectionModel(new DefaultListSelectionModel() {

				private static final long serialVersionUID = 8023040966116069641L;

				@Override
				public void setSelectionInterval(int index0, int index1) {

					if (isSelectedIndex(index0)) {
						super.removeSelectionInterval(index0, index1);
					} else {
						super.addSelectionInterval(index0, index1);
					}
				}
			});

			listPossibilities.setCellRenderer(new DefaultListCellRenderer() {

				private static final long serialVersionUID = 1784642852818028005L;

				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,boolean cellHasFocus) {
					if (value == null) {
						value = "NULL";
					}
					return super.getListCellRendererComponent(list, value,index, isSelected, cellHasFocus);
				}
			});
			c.gridx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.EAST;
			c.weightx = 1;
			internPanel.add(label,c/*, constraintsForLabels*/);
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.weightx = 2;
			internPanel.add(textField,c/*, constraintsForLabels*/);

			c.gridy++;

			filterPanel.validate();
		}
		if (backupMap != null) {
			for (Map.Entry<String, Object[]> entry : backupMap.entrySet()) {
				JList l = mapList.get(entry.getKey());
				if (l != null) {
					for (Object o : entry.getValue()) {
						l.setSelectedValue(o, false);
					}
				}
			}
		}

		this.repaint();
		this.validate();
	}

	private void autoCompute() {
		if (autoCompute.isSelected()) {
			compute();
		}
	}

	private Map<String, List<String>> getFilterSelection() {
		if (mapList == null) {
			return new HashMap<String, List<String>>();
		}
		Map<String, List<String>> properties = new HashMap<String, List<String>>();

		for (Entry<String, JList> e : mapList.entrySet()) {
			if (e.getValue().getSelectedValues().length != 0) {
				List<String> list = new ArrayList<String>();
				for (Object o : e.getValue().getSelectedValues()) {
					list.add((String) o);
				}
				properties.put(e.getKey(), list);
			}
		}
		return properties;
	}

	private class ComputingRunnable implements Runnable {
		@Override
		public void run() {
			synchronized (lock) {
				computing.setText("Computing");
				try {
					runSync();
				} finally {
					computing.setText("Idle");
				}
			}
		}

		public synchronized void runSync() {
			Map<String, List<String>> filters = getFilterSelection();
			Object[] sv = getListColor().getSelectedValues();
			CriteriumSet critSet = new CriteriumSet(2);
			List<Criterium> selectedColors = new ArrayList<Criterium>(sv.length);
			for (int i = 0; i < sv.length; i++) {
				Criterium c = new Criterium(quoteWithoutQuoteEquivalence.get(sv[i].toString()));
				selectedColors.add(c);
			}
			critSet.add(selectedColors);
			if (shapeComboBox.getSelectedIndex() > 0) {
				String shapeProperty = quoteWithoutQuoteEquivalence.get(shapeComboBox.getSelectedItem());
				critSet.add(shapeProperty);
			}

			String chartName = getYAxisText();

			String xAxisProperty = quoteWithoutQuoteEquivalence.get(xAxis.getSelectedItem());

			DataRetrievalOptions options  = new DataRetrievalOptions(new String[]{chartName},xAxisProperty,
					critSet,
					filters, 
					meanOrSumLineCheckBox.isSelected(),
					medianLineCheckBox.isSelected(),
					firstLineCheckBox.isSelected(),
					maxIntCheckBox.isSelected(),
					confIntCheckBox.isSelected(),
					quartIntCheckBox.isSelected(),
					confidenceInterval, is95, mean);
			ChartContainer chart = activeDisplayer.computeChart(options);
			if (chart.isMultiple()) {
				operationWhenMultiple1.setEnabled(true);
				operationWhenMultiple2.setEnabled(true);
			} else {
				operationWhenMultiple1.setEnabled(false);
				operationWhenMultiple2.setEnabled(false);
			}

			chartPanel.setChart(chart.getChart());
			
			chart.problemLabel = problemLabel;
			chart.updateLabel();		

			legendPanel.removeAll();
			Object legend = activeDisplayer.getLegend();
			if (legend instanceof JComponent) {
				if (legend instanceof JToolBar) {
					((JToolBar)legend).setFloatable(true);
				}
				((JComponent)legend).setPreferredSize(legendPanel.getSize());
				legendPanel.add((JComponent)legend);
		
			}
			legendPanel.revalidate();
			legendPanel.repaint();
			refreshMapList();
		}

		private void refreshMapList() {
			List<Pair<String, TreeSet<String>>> values = activeDisplayer.getVariableProperties();
			if (values != null) {
				for (Pair<String, TreeSet<String>> pair : values) {
					String name = pair.getFirst();
					if (pair.getSecond() == null) {
						mapList.get(name).setEnabled(false);
					} else if (pair.getSecond().first().equals("#$")) {
						mapList.get(name).setEnabled(true);
					} else {
						JList current = mapList.get(name);
						current.setListData(pair.getSecond().toArray(new String[0]));
						current.setEnabled(true);
					}
				}
			}
		}
	}

	public void compute() {
		Thread t = new Thread(new ComputingRunnable());
		SwingUtilities.invokeLater(t);
	}

	private JPanel getXAxisShapeColors() {
		if (jPanel2 == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.gridheight = 3;
			gridBagConstraints4.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.BOTH;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.weighty = 1.0;
			gridBagConstraints16.gridheight = 3;
			gridBagConstraints16.gridx = 4;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.ipadx = 5;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridwidth = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.ipadx = 5;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridwidth = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 1;
			labelShape = new JLabel();
			labelShape.setText("Shape");
			labelXAxis = new JLabel();
			labelXAxis.setText("X axis");
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.add(labelXAxis, gridBagConstraints2);
			jPanel2.add(this.xAxis, gridBagConstraints3);
			this.xAxis.setPreferredSize(new Dimension(140, 25));
			this.shapeComboBox.setPreferredSize(new Dimension(140, 25));
			jPanel2.add(labelShape, gridBagConstraints6);
			jPanel2.add(this.shapeComboBox, gridBagConstraints7);
			gridBagConstraints6.gridy++;
			gridBagConstraints6.gridheight = 1;
			jPanel2.add(seeOutputs, gridBagConstraints6);
			jPanel2.add(getColorsScrollPane(), gridBagConstraints4);
			getColorsScrollPane().setPreferredSize(new Dimension(200, 150));
		}
		jPanel2.setMinimumSize(new Dimension(300,150));
		return jPanel2;
	}

	private JPanel getGeneralOptionPanel() {
		if (globalOptionPanel == null) {
			
			GridBagConstraints gb = new GridBagConstraints();

			globalOptionPanel = new JPanel();
			globalOptionPanel.setLayout(new GridBagLayout());
			gb.fill = GridBagConstraints.BOTH;
			gb.gridx = 0;
			gb.gridy = 0;
			gb.gridwidth = GridBagConstraints.REMAINDER;
			gb.gridheight = 1;
			gb.weightx = 1;
			gb.weighty = 1;
			globalOptionPanel.add(getFirstMedianMeanEtc(), gb);

			gb.gridy = 1;
			globalOptionPanel.add(getTabbedPane(), gb);

			gb.gridy = 2;
			globalOptionPanel.add(this.getControlPanel(), gb);
			
			gb.gridy = 3;
			globalOptionPanel.add(computeButtonPanel, gb);

			globalOptionPanel.setPreferredSize(new Dimension(300, 500));
		}
		return globalOptionPanel;
	}

	private String getYAxisText() {
		return (String)(getYAxis().getSelectedItem());
	} 
	 
	private JComboBox getYAxis() {
		if (listGraphs == null) {
			listGraphs = new JComboBox();
			listGraphs.setFont(defaultFont);
			listGraphs.addItemListener(this);
			listGraphs.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent ev) {
					try {
						resetXFC();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			ListCellRenderer lcr = new DefaultListCellRenderer() {
				private static final long serialVersionUID = -2265316799832699898L;
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component comp = super.getListCellRendererComponent(list, value,
							index, isSelected, cellHasFocus);
					if (retriever.isInput((String)value)) {
						comp.setForeground(Color.BLUE);
					}
					return comp;
				}
			};
			listGraphs.setRenderer(lcr);
		}
		return listGraphs;
	}
	
	protected void disposeLegend() {
		if (this.legend != null) this.legend.dispose();
	}

	private JPanel getOperationWhenMultiple1() {
		if (operationWhenMultiple1 == null) {
			operationWhenMultiple1 = new JPanel();
			operationWhenMultiple1.setLayout(new GridLayout(3, 1));
			meanOrSumLineCheckBox = new JCheckBox("Mean");
			meanOrSumLineCheckBox.setSelected(true);
			meanOrSumLineCheckBox.addItemListener(this);
			meanOrSumLineCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseReleased(java.awt.event.MouseEvent evt) {
					chooseMean(evt);
				}
			});
			operationWhenMultiple1.add(meanOrSumLineCheckBox);
			medianLineCheckBox = new JCheckBox("Median");
			medianLineCheckBox.addItemListener(this);
			operationWhenMultiple1.add(medianLineCheckBox);
			firstLineCheckBox = new JCheckBox("First");
			firstLineCheckBox.addItemListener(this);
			operationWhenMultiple1.add(firstLineCheckBox);
		}
		return operationWhenMultiple1;
	}

	private JPanel getOperationWhenMultiple2() {
		if (operationWhenMultiple2 == null) {
			operationWhenMultiple2 = new JPanel();
			operationWhenMultiple2.setLayout(new GridLayout(3, 1));
			maxIntCheckBox = new JCheckBox("Min-Max");
			maxIntCheckBox.addItemListener(this);
			operationWhenMultiple2.add(maxIntCheckBox);
			confIntCheckBox = new JCheckBox("Confidence interval");
			confIntCheckBox.addItemListener(this);
			confIntCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseReleased(java.awt.event.MouseEvent evt) {
					if (evt.getButton() == 3) {
						JPopupMenu pop = new JPopupMenu();
						JMenu confInt = new JMenu("Confidence interval");
						final JLabel confValue = new JLabel(confidenceInterval + "%");
						final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 1, 100, 95);
						slider.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								if (confidenceInterval != slider.getValue()) {
									confidenceInterval = slider.getValue();
									confValue.setText(confidenceInterval + "%");
								}
								autoCompute();
							}
						});
						confInt.add(confValue);
						confInt.add(slider);
						pop.add(confInt);
						pop.show(confIntCheckBox, evt.getX(), evt.getY());
					}
				}
			});

			operationWhenMultiple2.add(confIntCheckBox);
			quartIntCheckBox = new JCheckBox("Interquartile range");
			quartIntCheckBox.addItemListener(this);
			quartIntCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseReleased(java.awt.event.MouseEvent evt) {
					changeQuartile(evt);
				}
			});			
			operationWhenMultiple2.add(quartIntCheckBox);

		}
		return operationWhenMultiple2;
	}
	
	private void changeQuartile(MouseEvent e) {
		if (e.getButton() == 3) {
			JPopupMenu pop = new JPopupMenu();
			JMenuItem quartile = new JMenuItem("Interquartile");
			quartile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quartIntCheckBox.setText("Interquartile range");
					is95 = false;
				}
			});
			pop.add(quartile);
			JMenuItem is95item = new JMenuItem("95% interval");
			is95item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quartIntCheckBox.setText("95% interval");
					is95 = true;
				}
			});			
			pop.add(is95item);
			pop.show(confIntCheckBox, e.getX(), e.getY());	
		}				
	}
	
	public boolean isConfIntChecked() {
		return confIntCheckBox.isSelected();
	}

	private void chooseMean(MouseEvent evt) {
		if (evt.getButton() == 3) {
			JPopupMenu pop = new JPopupMenu();
			JMenu meanChooser = new JMenu("Choose");
			JMenuItem mean = new JMenuItem("Mean");
			mean.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ComplexDisplayPanel.this.mean = true;
					meanOrSumLineCheckBox.setText("Mean");
					if (meanOrSumLineCheckBox.isSelected()) {
						autoCompute();
					}
				}
			});
			JMenuItem sum = new JMenuItem("Sum");
			sum.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ComplexDisplayPanel.this.mean = false;
					meanOrSumLineCheckBox.setText("Sum");
					if (meanOrSumLineCheckBox.isSelected()) {
						autoCompute();
					}
				}
			});
			meanChooser.add(mean);
			meanChooser.add(sum);
			pop.add(meanChooser);
			pop.show(meanOrSumLineCheckBox, evt.getX(), evt.getY());
		}
	}

	private JPanel getFirstMedianMeanEtc() {
		if (firstMedianMeanEtcPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 2;
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.gridx = 0;
			firstMedianMeanEtcPanel = new JPanel();
			firstMedianMeanEtcPanel.setLayout(new GridBagLayout());
			firstMedianMeanEtcPanel.add(getYAxis(), gridBagConstraints11);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			firstMedianMeanEtcPanel.add(getOperationWhenMultiple1());
			firstMedianMeanEtcPanel.add(getOperationWhenMultiple2());
		}
		return firstMedianMeanEtcPanel;
	}

	private JTabbedPane getTabbedPane() {
		if (this.displayTabsPanel == null) {
			displayTabsPanel = new JTabbedPane();
			for (AbstractChartPanel disp : displayers){
				if (disp instanceof AbstractChartPanel) {
					displayTabsPanel.addTab(((AbstractChartPanel) disp).getDecription(), ((AbstractChartPanel) disp).getConfigurationPanel(this));
				}
			}
			displayTabsPanel.addChangeListener(this);
			displayTabsPanel.setPreferredSize(new Dimension(300, 160));
			displayTabsPanel.setMinimumSize(new Dimension(250, 160));
		}
		return this.displayTabsPanel;
	}
	
	private JTabbedPane getControlPanel() {
		if (this.controlPanel == null) {
			this.controlPanel = new JTabbedPane();
			this.controlPanel.addTab("Color", this.getXAxisShapeColors());
			this.controlPanel.addTab("Legend", this.legendPanel);
			
			JScrollPane filters = new JScrollPane();
			filters.setViewportView(filterPanel);
			this.controlPanel.addTab("Filters", filters);
			
			JScrollPane constants = new JScrollPane();
			constants.setViewportView(constantPanel);
			this.controlPanel.addTab("Constants", constants);
			
			JScrollPane problems = new JScrollPane();
			JPanel probPanel = new JPanel();
			probPanel.add(this.problemLabel);
			problems.setViewportView(probPanel);
			this.controlPanel.addTab("Infos and Problems", problems);
			
			this.controlPanel.setPreferredSize(new Dimension(300, 160));
			this.controlPanel.setMinimumSize(new Dimension(250, 160));
		}
		
		return this.controlPanel;
	}

	private JList getListColor() {
		if (listColor == null) {
			listColor = new JList();
			listColor.setVisibleRowCount(7);
			listColor.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						autoCompute();
					}
				}
			});
		}
		return listColor;
	}

	private JScrollPane getColorsScrollPane() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getListColor());
			jScrollPane1.setSize(new Dimension(150,220));
			jScrollPane1.setMinimumSize(new Dimension(150, 200));
		}
		return jScrollPane1;
	}

	private boolean extendedVariableListContainsOneOf(String[] list) {
		updateProperties(false);
		Vector<String> normal = varProperties;
		updateProperties(true);
		Vector<String> extended = varProperties;

		for (String s : list) {
			if (!normal.contains(s) && extended.contains(s)) {
				return true;
			}
		}
		return false;
	}

	public void setDefault(String yAxis, String xAxis, String shape, String[] colors) {
		boolean checked = seeOutputs.isSelected();
		if (checked == false) {
			String[] crit = new String[2+colors.length];
			System.arraycopy(colors, 0, crit, 0, colors.length);
			crit[colors.length] = xAxis;
			crit[colors.length+1] = shape;
			if (extendedVariableListContainsOneOf(crit)) {
				seeOutputs.doClick();
				seeOutputs.setSelected(true);
			} else {
				if (!getYAxisValues(false).contains(yAxis) && getYAxisValues(true).contains(yAxis)) {
					seeOutputs.doClick();
					seeOutputs.setSelected(true);
				}
			}
		}

		setDefault(yAxis, getYAxis());
		setDefault(xAxis, this.xAxis);
		setDefault(shape, this.shapeComboBox);
		List<Integer> yValIndexs = new ArrayList<Integer>();
		for (String col : colors) {
			this.getListColor().setSelectedValue(col, false);
			yValIndexs.add(this.getListColor().getSelectedIndex());
		}
		int[] indices = new int[yValIndexs.size()];
		for (int i = 0; i < indices.length; ++i) {
			indices[i] = yValIndexs.get(i);
		}
		this.listColor.setSelectedIndices(indices);
	}

	private void setDefault(String val, JComboBox list) {
		int index = -1;
		for (int i = 0; i < list.getItemCount(); ++i) {
			String description = (String)list.getItemAt(i);
			if (description.equals(val)) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			list.setSelectedIndex(index);
		}
	}
	
	private void resetXFC() {
		updateProperties(seeOutputs.isSelected());		
		resetXShapeAndColor__();
		resetFilterPanel__();
		resetConstants__();
	}
	
	public void resetAFC() {
		int[] sel = listColor.getSelectedIndices();
		updateProperties(seeOutputs.isSelected());
		resetAxesAndShape__();
		resetFilterPanel__();
		resetConstants__();
		for (int i : sel) {
			listColor.setSelectedIndex(i);
		}
	}
	
	public JMenuBar getMenubar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu actions = new JMenu("Actions");
		JMenuItem item;

		Pair<java.awt.event.ActionListener[], String[]> pair = displayers.get(0).getDisplayerPossibleActions(this);
		for (int i = 0 ; i < pair.getFirst().length ; i++) {
			item = new JMenuItem("XY Line Chart :" + pair.getSecond()[i]);
			item.addActionListener(pair.getFirst()[i]);
			actions.add(item);
		}
		item = new JMenuItem("New windows");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				getNewFrame();
			}

			private void getNewFrame() {
				ComplexDisplayPanel cmp = new ComplexDisplayPanel(retriever, displayerClasses, title, parentFrame);
				JFrame frame = new JFrame();
				frame.setTitle(title);
				frame.setJMenuBar(cmp.getMenubar());
				frame.setContentPane(cmp);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
				frame.pack();
				frame.setSize(new java.awt.Dimension(1000, 750));
			}
		});
		actions.add(item);
		
		item = new JMenuItem("Quit");
		item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				System.exit(0);
			}
		});
		actions.add(item);

		menuBar.add(actions);
		return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.compute) {
			compute();
		} else {
			autoCompute();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.activeDisplayer = this.displayers.get(this.getTabbedPane().getSelectedIndex());
		if (this.activeDisplayer instanceof AbstractChartProvider.AbstractChartPanel) {
			if (((AbstractChartPanel) this.activeDisplayer).getSecondOption() != null){
				this.shapeComboBox.setEnabled(true);
				this.labelShape.setText(((AbstractChartPanel) this.activeDisplayer).getSecondOption());
			} else {
				this.shapeComboBox.setEnabled(false);
			}
		}
		autoCompute();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		autoCompute();

	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		disposeLegend();
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
