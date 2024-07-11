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
package ch.heiafr.isc.datacockpit.visualizer.display;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import ch.heiafr.isc.datacockpit.visualizer.charts.paints.Texture;
import ch.heiafr.isc.datacockpit.visualizer.math.Formulas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.DefaultCategoryDataset;

import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomBarRenderer;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomCategoryDataset;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomLogarithmicAxis;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomNumberAxis;
import ch.heiafr.isc.datacockpit.visualizer.charts.TextureSupplier;
import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataRetrievalOptions;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataSeries;
import ch.heiafr.isc.datacockpit.general_libraries.utils.DateAndTimeFormatter;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.general_libraries.utils.PairList;



public class BarChartProvider extends AbstractChartProvider {

	private TextureSupplier textureSupplier = new TextureSupplier();

	//private Map<String, Shape> seriesShape = new HashMap<String, Shape>();
	public Map<Pair<String,String>, Texture> seriesShapeBar = new HashMap<Pair<String,String>, Texture>();
	//	private DataSeriesProperties p;

	private LinkedList<Association> assosList = new LinkedList<Association>();
	public HashSet<PairList<String, String>> legends = new HashSet<PairList<String, String>>();


	TreeSet<Integer> expIDs = new TreeSet<Integer>();

	public boolean isXOnScale;
	public boolean isLogY;
	public boolean isScalar;
	
	public Description listGraph2;
	


	private final static Texture DEFAULT_TEXTURE;

	static {
		DEFAULT_TEXTURE = (Texture) (new TextureSupplier()).getNext();
	}


	public ChartContainer computeChart(DataRetrievalOptions options, AdvancedDataRetriever retriever) {
		String meth2 =  isScalar ? listGraph2.nom : "";

		this.seriesShapeBar.clear();
		this.seriesPaint.clear();
		this.drawingSupplier = new DefaultDrawingSupplier();

		if (isScalar) {
			return createScalarChart(meth2, options, retriever);
		} else {
			return createBarChart(options, retriever);
		}
	}

	public void createExcelData(AdvancedDataRetriever retriever) throws Exception {
		if (legends.size() <= 0) return;
		
		int nbParams = ((PairList<?,?>) legends.toArray()[0]).size();
		if (nbParams <= 0) return;


		String separator = ",";
		String constantValues = null;
		for (String s : retriever.getParameters()) {
			String[] st = retriever.getPossibleValuesOfGivenProperty(s).toArray(new String[0]);

			String ss = s.replaceAll("\"", "");

			if (st != null && st.length == 1) {
				String valu = ss + " = " + st[0];
				constantValues = ((constantValues == null) ? "Constant(s)"
						+ separator
						: constantValues + separator + "\r\n"
						+ separator)
						+ valu;
			}
		}
		if (constantValues != null) {
			constantValues += "\r\n";
		}

		Map<String, List<String>> params = new HashMap<String, List<String>>(
				nbParams);
		Map<String, Set<String>> temp = new HashMap<String, Set<String>>(
				nbParams);

		Iterator<PairList<String, String>> it = legends.iterator();
		while (it.hasNext()) {
			PairList<String, String> next = it.next();
			for (Pair<String, String> exp : next) {
				String param = exp.getFirst();
				String value = exp.getSecond();
				if (temp.containsKey(param)) {
					temp.get(param).add(value);
				} else {
					Set<String> newSet = new HashSet<String>();
					newSet.add(value);
					temp.put(param, newSet);
				}
			}
		}
		for (Entry<String, Set<String>> e : temp.entrySet()) {
			params.put(e.getKey(), new ArrayList<String>(e.getValue()));
		}
		temp = null;
		List<String> exps = new ArrayList<String>();
		List<String> allValues = new ArrayList<String>();
		List<String> paramsKey = new ArrayList<String>(params.keySet());
		Collections.sort(paramsKey);
		int totalSize = 1;
		for (Entry<String, List<String>> e : params.entrySet()) {
			Collections.sort(e.getValue());
			totalSize *= e.getValue().size();
		}
		List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
		String[] currentValue = new String[paramsKey.size()];
		for (int i = 0; i < paramsKey.size(); ++i) {
			iterators.add(params.get(paramsKey.get(i)).iterator());
			currentValue[i] = iterators.get(i).next();
		}
		for (int i = 0; i < totalSize; ++i) {
			String exp = "";
			boolean next = false;
			for (int j = 0; j < iterators.size(); ++j) {
				exp += paramsKey.get(j) + "=" + currentValue[j];
				allValues.add(currentValue[j]);
				if (!next) {
					if (iterators.get(j).hasNext()) {
						currentValue[j] = iterators.get(j).next();
						next = true;
					} else {
						iterators.set(j, params.get(paramsKey.get(j))
								.iterator());
						currentValue[j] = iterators.get(j).next();
					}
				}
			}
			exps.add(exp);
		}

		String[][][] data = new String[9][exps.size() + 1][xVals.size()
		                                                   + 1 + nbParams];
		HashMap<String, Integer> xValsCor = new HashMap<String, Integer>();
		int j = nbParams + 1;
		Collection<String> xv = sort(xVals);
		for (String s : xv) {
			xValsCor.put(s, j);
			for (int k = 0; k < 9; ++k) {
				data[k][0][j] = s;
			}
			j++;
		}

		String[] header = new String[] { "mean or val\r\n", "med\r\n",
				"lConf\r\n", "hConf\r\n", "min\r\n", "max\r\n",
				"1q\r\n", "3q\r\n", "first\r\n" };
		int i = 1;
		for (String s : paramsKey) {
			for (int k = 0; k < 9; ++k) {
				data[k][0][0] = header[k];
				data[k][0][i] = s;
			}
			++i;
		}
		i = 1;
		Iterator<String> paramIt = allValues.iterator();
		for (String s : exps) {
			for (Association a : assosList) {
				int col = xValsCor.get(a.xValOrString);
				if (a.legend.contains(s)) {
					for (int k = 0; k < 9; ++k) {
						data[k][i][col] = a.stats[k] + "";
					}
				}
			}
			for (int m = 1; m <= nbParams; ++m) {
				String toAdd = paramIt.next();
				for (int k = 0; k < 9; ++k) {
					data[k][i][m] = toAdd;
				}
			}
			++i;
		}

		java.io.FileWriter fw = new java.io.FileWriter("Data" + DateAndTimeFormatter.getDateAndTime(System.currentTimeMillis()) + ".csv");
		if (constantValues != null) {
			fw.append(constantValues);
		}
		fw.append("WARNING: BAR CHART DOESN'T TAKE FILETERED VALUES INTO ACCOUNT !!!!! \r\n");
		for (int m = 0; m < 9; m++) {
			for (i = 0; i < data[m].length; i++) {
				for (int k = 0; k < data[m][i].length; ++k) {
					if (data[m][i][k] != null) {
						fw.append(data[m][i][k] + separator);
					} else {
						fw.append(separator);
					}
				}
				fw.append("\r\n");
			}
			fw.append("\r\n");
			fw.append("\r\n");
		}
		fw.flush();
		fw.close();
	}

	private ChartContainer createScalarChart(String meth2, DataRetrievalOptions options, AdvancedDataRetriever retriever) {
		List<DataSeries> data1 = retriever.getChartValues(options, options.method[0]/*.method, p*/);
		List<DataSeries> data2;
		if (/*options.hasSecondMethod()*/!meth2.equals("")) {
			data2 = retriever.getChartValues(options, meth2/*options.method[1]/*meth2, p*/);
		} else {
			data2 = new ArrayList<DataSeries>(0);
		}
		int length = Math.min(data1.size(), data2.size());
		double[] scalarValue = new double[length];
		JFreeChart jFreeChart;
		ChartContainer chart = new ChartContainer();
		
		int noSerie = -1;
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		jFreeChart = ChartFactory.createBarChart("Scalar Product between "
				+ options.method + " and " + meth2, "Colors", "Scalar Product",
				dataset, PlotOrientation.VERTICAL, false, false, false);
		chart.setChart(jFreeChart);
		CustomBarRenderer renderer = new CustomBarRenderer(options);
		jFreeChart.getCategoryPlot().setRenderer(renderer);

			for (int i = 0; i < length; ++i) {
				++noSerie;
				List<PairList<String, String>> crit = data1.get(i).crit;
				setSeriePropertyBar(renderer, noSerie, crit.get(0), crit.size() > 1 ? crit.get(1) : null);
				Iterator<DataSeries.DataSeriesStruct> it1 = data1.get(i)
						.iterator();
				Iterator<DataSeries.DataSeriesStruct> it2 = data2.get(i)
						.iterator();
				while (it1.hasNext() && it2.hasNext())
					scalarValue[i] += /*it1.next().getFirst().getSecond()
							.getFirst()*/it1.next().yVal * it2.next().yVal;
						//	* it2.next().getFirst().getSecond().getFirst();
				if (scalarValue[i] > 0) {
					Comparable<?> rowKey = (data1.get(i).crit.get(0) == null ? "NULL"
							: data1.get(i).crit.get(0));
					Comparable<?> columnKey = (noSerie);
					dataset.setValue(scalarValue[i], rowKey, columnKey);
				}
		}
		return chart;
	}

	private ChartContainer createBarChart(DataRetrievalOptions options, AdvancedDataRetriever retriever) {

		/* clear all the lists */
		assosList.clear();
		xVals.clear();
		legends.clear();
		expIDs.clear();

		JFreeChart jFreeChart;

		CustomCategoryDataset dataset = new CustomCategoryDataset();
		jFreeChart = ChartFactory.createBarChart(options.method[0],
				options.xAxisProperty, options.method[0], dataset,
				PlotOrientation.VERTICAL, false, false, false);
		((CategoryPlot) jFreeChart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		ChartContainer chart = new ChartContainer();
		chart.setChart(jFreeChart);

		// Store how many time a value (given by a pair of rowKey an columnKey)
		// is in the dataset
		Map<Pair<Comparable<?>, Comparable<?>>, Pair<Integer, List<Double>>> multiplesValues = new HashMap<Pair<Comparable<?>, Comparable<?>>, Pair<Integer, List<Double>>>();

		BarRenderer renderer = new CustomBarRenderer(options);
		jFreeChart.getCategoryPlot().setRenderer(renderer);
		// Values values = new Values(method, p);
		double[] max = new double[6];
		double maxValue = 0.0;
		
		List<DataSeries> dataseries = retriever.getChartValues(options, options.method[0]);


		for (DataSeries cv : dataseries) {
			Comparable<?> previousColumnKey = null;
			PairList<String, String> legend = cv.getSerieCriteria();
			legends.add(cv.getSerieCriteria());
			Comparable<?> rowKey = (cv.crit.get(0) == null ? "NULL" : legend);
			for (DataSeries.DataSeriesStruct x : cv) {
				Comparable<?> columnKey = (x.xVal == null ? "NULL" : x.xVal);
				String textColumnKey = previousColumnKey+"";
				if (x.expId >= 0) {
					expIDs.add(x.expId);
				}
				try {
					columnKey = new Integer(Integer.parseInt((String) columnKey));
					if (previousColumnKey != null) {
						previousColumnKey = new Integer(Integer.parseInt(textColumnKey));
					} else {
						previousColumnKey = new Integer(Integer.MIN_VALUE);
					}
				} catch (NumberFormatException ex) {
					try {
						columnKey = new Double(Double.parseDouble((String) columnKey));
						if (previousColumnKey != null) {
							previousColumnKey = new Double(Double.parseDouble(textColumnKey));
						} else {
							previousColumnKey = new Double(Double.MIN_VALUE);
						}
					} catch (Exception e) {
						// normal
					}
				}
				if (isXOnScale && columnKey instanceof Number && (previousColumnKey instanceof Number)) {
					while (((Number) columnKey).doubleValue() > ((Number) previousColumnKey).doubleValue() + 1) {
						previousColumnKey = ((Number) previousColumnKey).intValue() + 1;
						try {
							if (dataset.getValue(rowKey, previousColumnKey) == null) {
								for (int i = 0; i < 8; ++i) {
									dataset.setValue(0.0, i, rowKey, previousColumnKey);
								}
								dataset.setValue(0.0, 9, rowKey, previousColumnKey);
							}
						} catch (UnknownKeyException e) {
							for (int i = 0; i < 8; ++i) {
								dataset.setValue(0.0, i, rowKey, previousColumnKey);
							}
							dataset.setValue(0.0, 9, rowKey, previousColumnKey);
						}
					}
				}

				double yValue = x.yVal;
				try {
					Number n = dataset.getValue(0, rowKey, columnKey);

					if (n == null) {
						for (int i = 0; i < 8; ++i) {
							dataset.setValue(yValue, i, rowKey, columnKey);
						}
						dataset.setValue(yValue, 9, rowKey, columnKey);
						xVals.add(columnKey.toString());
						assosList.add(new Association(columnKey.toString(), legend, (float)yValue, /*dummy*/0));
						for (int i = 0; i < 6; ++i) {
							if (yValue > max[i]) {
								max[i] = yValue;
							}
						}
						continue;
					}

					Pair<Comparable<?>, Comparable<?>> pair = new Pair<Comparable<?>, Comparable<?>>(rowKey, columnKey);
					Pair<Integer, List<Double>> nb_pair = multiplesValues.get(pair);
					Integer nb = null;
					try {
						nb = nb_pair.getFirst();
					} catch (NullPointerException e) {}
					if (nb == null) {
						List<Double> newList = new LinkedList<Double>();
						newList.add(n.doubleValue());
						newList.add(yValue);
						multiplesValues.put(pair, new Pair<Integer, List<Double>>(2, newList));
					} else {
						List<Double> list = multiplesValues.get(pair).getSecond();
						list.add(yValue);
						multiplesValues.put(pair, new Pair<Integer, List<Double>>(nb + 1, list));
					}
					for (int i = 0; i < 10; ++i) {
						dataset.setValue(yValue, i, rowKey, columnKey);
					}
				} catch (UnknownKeyException e) {
					for (int i = 0; i < 10; ++i) {
						dataset.setValue(yValue, i, rowKey, columnKey);
					}
					for (int i = 0; i < 6; ++i) {
						if (yValue > max[i]) {
							max[i] = yValue;
						}
					}
				}
				previousColumnKey = columnKey;

			}
			int row = dataset.getRowIndex(rowKey);
			if (row >= 0) {
				setSeriePropertyBar(renderer, dataset.getRowIndex(rowKey),
						cv.crit.get(0), cv.crit.size() > 1 ? cv.crit.get(1) : null);
			}
		}

		if (!multiplesValues.isEmpty()) {
			for (Entry<Pair<Comparable<?>, Comparable<?>>, Pair<Integer, List<Double>>> entry : multiplesValues
					.entrySet()) {
				Comparable<?> rowKey = entry.getKey().getFirst();
				Comparable<?> columnKey = entry.getKey().getSecond();
				List<Double> list = entry.getValue().getSecond();

				double[] all = Formulas.getAll(list,
						options.confInt / 100.0);
				if (!options.isMeanInsteadOfSum) {
					all[0] = entry.getValue().getFirst() * all[0];
				}
				double[] allValues = new double[all.length + 2];
				System.arraycopy(all, 0, allValues, 0, all.length);
				allValues[9] = list.get(0);

				for (int i = 0; i < allValues.length; ++i) {
					dataset.setValue(allValues[i], i, rowKey, columnKey);
				}

				xVals.add(columnKey.toString());
				/*		assosList.add(new Association(columnKey.toString(),
						rowKey.toString(), all, list.get(0)));*/

				if (allValues[0] > max[0]) {
					max[0] = allValues[0];
				}
				if (allValues[1] > max[1]) {
					max[1] = allValues[1];
				}
				if (allValues[9] > max[2]) {
					max[2] = allValues[9];
				}
				if (allValues[5] > max[3]) {
					max[3] = allValues[5];
				}
				if (allValues[3] > max[4]) {
					max[4] = allValues[3];
				}
				if (allValues[7] > max[5]) {
					max[5] = allValues[7];
				}
			}
			testMultiplesValuesBar(multiplesValues);
		}
		if (!multiplesValues.isEmpty()) {
			/*
			 * chart.addProblem(new Problem(Severity.INFORMATION,
			 * "Multiples y values on the same x for method " + methodName +
			 * ", means used", methodName));
			 */
			/*
			chart.addProblem(new Problem(Severity.INFORMATION,
					"=========================", method));
			chart.addProblem(new Problem(Severity.INFORMATION,
					getOutputMessage(), method));
			chart.addProblem(new Problem(Severity.INFORMATION,
					"=================", method));
			chart.addProblem(new Problem(Severity.INFORMATION,
					" Variable properties: "
							+ retriever.getVariableProperties(p, method, expIDs), method)); */
		}
		for (int i = 0; i < 6; ++i) {
			if (max[i] > maxValue) {
				maxValue = max[i];
			}
		}
		chart.setMultiple(!multiplesValues.isEmpty());
		ValueAxis axis; 
		if (isLogY)
			axis = new CustomLogarithmicAxis(options.getYLabel(), 1.1*maxValue);
		else 
			axis = new CustomNumberAxis(options.getYLabel(), 1.1*maxValue);
		chart.getChart().getCategoryPlot().setRangeAxis(axis);
		return chart;
	}

	private void setSeriePropertyBar(BarRenderer renderer, int noSerie,
			PairList<String,String> color, PairList<String,String> shape) {

		Paint paint = super.setSerieProperty(renderer, noSerie, color, false, 0);
		Texture text;
		if (shape != null)  {
			text = seriesShapeBar.get(shape.get(0));
			if (text == null) {
				text = (Texture)textureSupplier.getNext();
				Color color2 = (Color)paint;
				text.setColor(color2);
				seriesShapeBar.put(shape.get(0), (Texture) paint);
			}
		} else {
			text = DEFAULT_TEXTURE;
			Color color2 = (Color)paint;
			text.setColor(color2);
			seriesShapeBar.put(new Pair<String, String>("", ""), text);
		}

		renderer.setSeriesPaint(noSerie, paint);
		renderer.setSeriesFillPaint(noSerie, text);
	}

	private void testMultiplesValuesBar(
			Map<Pair<Comparable<?>, Comparable<?>>, Pair<Integer, List<Double>>> multiplesValues) {
		Set<Pair<Comparable<?>, Comparable<?>>> set = multiplesValues.keySet();
		Iterator<Pair<Comparable<?>, Comparable<?>>> itr = set.iterator();

		while (itr.hasNext()) {
			List<Double> list = multiplesValues.get(itr.next()).getSecond();
			int n = list.size();
			if (min > n) {
				min = n;
			}
			if (max < n) {
				max = n;
			}
		}
	}
}
