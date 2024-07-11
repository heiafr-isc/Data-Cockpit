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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

import ch.heiafr.isc.datacockpit.visualizer.display.panels.MatlabPlot;
import ch.heiafr.isc.datacockpit.visualizer.global_gui.ComplexDisplayPanel;
import ch.heiafr.isc.datacockpit.visualizer.math.Formulas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomDeviationRenderer;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomLogAxis;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomNumberAxis;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomXYIntervalSeries;
import ch.heiafr.isc.datacockpit.visualizer.charts.CustomXYIntervalSeriesCollection;
import ch.heiafr.isc.datacockpit.visualizer.charts.Problem;
import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataRetrievalOptions;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataSeries;
import ch.heiafr.isc.datacockpit.general_libraries.utils.DateAndTimeFormatter;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Mapper;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.general_libraries.utils.PairList;

/**
 *  The general idea for this class :
 *  
 *  Constructor without arguments, but all the flags and the description must be set.
 *  THen computeGraph() method can be called.
 *  Once called, objects related to the chart (as the legend or the matlab/excel dump) can be retrieved
 * @author rumley
 *
 */

public class XYChartProvider extends AbstractChartProvider {
	
	public boolean isLogX;
	public boolean isLogY;
	public boolean isSameAxis;
	public boolean isScalar;
	public boolean isNormalisedWithX;
	public boolean isNoScaleX;
	public boolean isSortOnY;
	public boolean isTwoMethods;
	public boolean isWithLines = true;
	public boolean isWithLinesWA;
	public boolean isWithoutIdentitical;
	public boolean isWithoutLines;
	public boolean isWithContinuousColors;		
	public boolean isUsingLegend;
	public boolean isLogColors;	
	
	public String xaxis;
	public String yaxis;
	
	public Map<Pair<String, String>, Shape> seriesShape = new HashMap<Pair<String, String>, Shape>();
	public HashSet<PairList<String, String>> legends = new HashSet<PairList<String, String>>();
	public LinkedList<Association> assosList = new LinkedList<Association>();



	
	final static BasicStroke stroke1 = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, null, 0);
	final static BasicStroke stroke2 = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 20f, 10f }, 0);	
	
	public ChartContainer computeChart(DataRetrievalOptions options, AdvancedDataRetriever retriever) {
		this.drawingSupplier = new DefaultDrawingSupplier();
		this.seriesPaint.clear();
		String meth2 = isTwoMethods ? options.method[1] : "";

		this.seriesShape.clear();

		options.method = new String[] { options.method[0], meth2 };
		if( isScalar ) {
			return this.createScalarChart(options, retriever);
		} else {
			return this.createXYLineChart(options, retriever);
		}
	}

	private ChartContainer createScalarChart(DataRetrievalOptions options, AdvancedDataRetriever retriever) {
		List<DataSeries> data1 = retriever.getChartValues(options, options.method[0]);
		List<DataSeries> data2 = retriever.getChartValues(options, options.method[1]);
		int length = (int) Math.min(data1.size(), data2.size());
		double[] scalarValue = new double[length];
		JFreeChart jFreeChart;
		ChartContainer chart = new ChartContainer();

		String xAxisLabel = (options.xAxisProperty.equals("") ? "Quel label ??" : options.xAxisProperty);
		String yAxisLabel = options.getYLabel();
		jFreeChart = ChartFactory.createXYLineChart(options.method[0], xAxisLabel, yAxisLabel, null, PlotOrientation.VERTICAL, isUsingLegend, false, false);
		chart.setChart(jFreeChart);

		if( isLogX ) {
			final NumberAxis xAxis = new LogarithmicAxis(xAxisLabel);
			chart.getChart().getXYPlot().setDomainAxis(xAxis);
		}
		if( isLogY ) {
			final NumberAxis yaxis = new LogarithmicAxis(yAxisLabel);
			chart.getChart().getXYPlot().setRangeAxis(yaxis);
		}

		BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, null, 0);
		CustomXYIntervalSeriesCollection seriesCollection = new CustomXYIntervalSeriesCollection(options);
		CustomDeviationRenderer renderer = new CustomDeviationRenderer(false, true, chart);
		renderer.setBaseStroke(stroke);
		renderer.setAlpha(0.25f);

		renderer.setBasePaint(Color.CYAN);
		renderer.setAutoPopulateSeriesFillPaint(true);
		renderer.setBaseSeriesVisibleInLegend(true);
		jFreeChart.getXYPlot().setRenderer(0, renderer);
		jFreeChart.getXYPlot().setDataset(0, seriesCollection);

		int noSerie = -1;
		for( int i = 0; i < length; ++i ) {
			String legend = data1.get(i).getLegend();// + " " + data1.get(i).crit.get(1);
			CustomXYIntervalSeries serie_ = new CustomXYIntervalSeries(legend);
			legends.add(data1.get(i).getSerieCriteria());
			noSerie++;
			setSerieProperty(renderer, noSerie, data1.get(i).crit, length);
			Iterator<DataSeries.DataSeriesStruct> it1 = data1.get(i).iterator();
			Iterator<DataSeries.DataSeriesStruct> it2 = data2.get(i).iterator();
			while (it1.hasNext() && it2.hasNext())
				scalarValue[i] += it1.next().yVal/*getFirst().getSecond()
													.getFirst()*/
						* it2.next().yVal/*getFirst().getSecond().getFirst()*/;
			if( scalarValue[i] > 0 ) {
				double y = scalarValue[i];
				serie_.add(new double[] { y, y, y, y, y, y, y, y, 0, y });
			}
			seriesCollection.addSeries(serie_);
		}

		return chart;
	}

	private ChartContainer createXYLineChart(DataRetrievalOptions options, AdvancedDataRetriever retriever) {
		String xAxisLabel = options.xAxisProperty;
		String yAxisLabel;
		if (isSameAxis && options.hasSecondMethod()) {
			yAxisLabel = options.getYLabel();
		} else {
			if (isNormalisedWithX) {
				yAxisLabel = options.method[0] + " / " + xAxisLabel;
			} else {
				yAxisLabel = options.method[0];
			}
		}
		JFreeChart jFreeChart = ChartFactory.createXYLineChart(options.method[0], xAxisLabel, yAxisLabel, null, PlotOrientation.VERTICAL, isUsingLegend, false, false);
		XYPlot plot = jFreeChart.getXYPlot();

		NumberAxis xAxis = new CustomNumberAxis(xAxisLabel);
		plot.setDomainAxis(xAxis);

		ChartContainer chart = new ChartContainer();
		chart.setChart(jFreeChart);

		boolean mult = buildOneMethod(chart, options, stroke1, 0, retriever);

		if( options.method.length > 1 && !options.method[1].equals("") ) {
			mult |= buildOneMethod(chart, options, stroke2, 1, retriever);

			if( !isSameAxis ) {
				NumberAxis axis2 = new CustomNumberAxis(options.method[1]);
				plot.setRangeAxis(1, axis2);
				plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
				plot.mapDatasetToRangeAxis(1, 1);
				plot.mapDatasetToDomainAxis(0, 0);
				plot.mapDatasetToDomainAxis(1, 0);
			}

		}
		chart.setMultiple(mult);
		if( isLogX) {
			xAxis = new CustomLogAxis(xAxisLabel);
			plot.setDomainAxis(xAxis);
		}
		if( isLogY ) {
			final NumberAxis yaxis = new CustomLogAxis(yAxisLabel);
			plot.setRangeAxis(yaxis);
		}

		return chart;
	}

	protected boolean buildOneMethod(ChartContainer chart, DataRetrievalOptions options, BasicStroke stroke, int idx, AdvancedDataRetriever retriever) {
		XYPlot plot = chart.getChart().getXYPlot();
		CustomXYIntervalSeriesCollection seriesCollection = new CustomXYIntervalSeriesCollection(options);
		boolean useLines = !(options.getCriteriumSet().get(0).size() == 0);
		useLines &= (isWithLines|| isWithLinesWA);
		CustomDeviationRenderer renderer = new CustomDeviationRenderer(useLines, true, chart); // chart is given for problem reporting
		renderer.useMultipointHighlight(!isWithoutIdentitical);
		renderer.setBaseStroke(stroke);
		renderer.setAlpha(0.25f);

		if( this.isNoScaleX )
			plot.getDomainAxis().setVisible(false);

		renderer.setBasePaint(Color.CYAN);
		renderer.setAutoPopulateSeriesFillPaint(true);
		if( idx == 0 )
			renderer.setBaseSeriesVisibleInLegend(true);
		else
			renderer.setBaseSeriesVisibleInLegend(false);
		plot.setRenderer(idx, renderer);
		plot.setDataset(idx, seriesCollection);

		// Values values = new Values(methodName, p);
		int noSerie = -1;

		/* clear all the lists */
		assosList.clear();
		xVals.clear();
		legends.clear();

//		expIDs.clear();

		min = MAX_VALUE;
		max = MIN_VALUE;

		DataSeries.DataSeriesSorter sorter = new DataSeries.DataSeriesSorter();
		boolean nonNumericXValues = false;

		TreeMap<Double, String> tickEquivalenceMap = new TreeMap<Double, String>();
		
		List<DataSeries> dataSeries = retriever.getChartValues(options, options.method[idx]);
	
		HashSet<String> toSortAndEvalFromZeroToOne = new HashSet<String>();
		
		yaxis = options.method[idx];
		xaxis = options.xAxisProperty;
	
		for( DataSeries cv : dataSeries) {
			if( cv instanceof DataSeries.EmptyChartValues )
				continue;
			toSortAndEvalFromZeroToOne.add(cv.getValueOfLastCrit());
		}
		
		Map<String, Float> colorMap = Mapper.getMap(toSortAndEvalFromZeroToOne, isLogColors);
		

		for( DataSeries cv : dataSeries) {
			if( cv instanceof DataSeries.EmptyChartValues )
				continue;

			String legend = cv.getLegend();
			if( legend == null )
				continue;
			CustomXYIntervalSeries serie__ = new CustomXYIntervalSeries(legend);
			legends.add(cv.getSerieCriteria());
			noSerie++;
			float serieColor = colorMap.get(cv.getValueOfLastCrit());
			
			setSerieProperty(renderer, noSerie, cv.crit, serieColor);

			Collection<DataSeries.DataSeriesSorter.XEntry> entries;
			if( isSortOnY ) {
				entries = sorter.getYSortedList(cv);
			} else {
				entries = sorter.getNumerisedList(cv);
			}
			Map<Float, Float> withoutScale = new HashMap<Float, Float>();
			Float withoutScaleIndex = 0.0f;

			for( DataSeries.DataSeriesSorter.XEntry xe : entries ) {
				Vector<Float> xeValues = xe.values;
				float x = xe.numericXValue;
				if( isNoScaleX ) {
					Float newX = withoutScale.get(x);
					if( newX == null ) {
						withoutScale.put(x, withoutScaleIndex);
						newX = withoutScaleIndex;
						++withoutScaleIndex;
					}
					x = newX;
				}
				if( isLogX && x <= 0.0 )
					continue;
				if( xeValues.size() > 1 && isWithLines ) {
					float[] all;
					boolean break_ = false;
					for (int i = 0 ; i < xeValues.size() ; i++) {
						Float sample = xeValues.get(i);
						if (sample.isInfinite() || sample.isNaN()) {
							
							break_ = true;
							break;
						}
					}
					if (break_) {
						chart.addProblem(new Problem(Problem.Severity.INFORMATION, "Some infinite or NaN values", options.method[idx]));
						all = new float[9];
						Arrays.fill(all, Float.NaN);
						assosList.add(new Association(xe.alphaXValue, cv.getSerieCriteria(), all, xe.numericXValue));
						break;
					}
					if( !options.is95 ) {
						all = Formulas.getAll(xe.values, (float) options.confInt / 100);
					} else {
						all = Formulas.getAll95(xe.values, (float) options.confInt / 100);
					}
					float[] allAndFirstAndX = new float[all.length + 2];
					System.arraycopy(all, 0, allAndFirstAndX, 0, all.length);
					allAndFirstAndX[8] = xe.values.get(0);
					allAndFirstAndX[9] = x; //xe.numericXValue;
					//Check if 0 for log scale
					if( isNormalisedWithX ) {
						for( int i = 0; i < allAndFirstAndX.length - 1; ++i )
							allAndFirstAndX[i] /= x;
					}
					if( isLogY ) {
						boolean skip = false;
						for( int i = 0; i < allAndFirstAndX.length; ++i )
							if( i != 9 /* skipping x */&& allAndFirstAndX[i] <= 0.0 )
								allAndFirstAndX[i] = Float.NaN;
						//skip = true;
						if( skip )
							break;
					}
					serie__.add(allAndFirstAndX);
					assosList.add(new Association(xe.alphaXValue, cv.getSerieCriteria(), all, xe.numericXValue));

				} else {
					for( Float y : xe.values ) {
						boolean doNotAdd = false;
						//	float y = xe.mean();
						//float x = xe.numericXValue;
						if (y.isInfinite() || y.isNaN()) {
							chart.addProblem(new Problem(Problem.Severity.INFORMATION, "Some infinite or NaN values for x=" + xe.alphaXValue, options.method[idx]));
							if (this.isWithLines) {
								doNotAdd = true;
							}
						}
						if( isLogY && y <= 0.0 ) {
							chart.addProblem(new Problem(Problem.Severity.INFORMATION, "Some 0 values (not allowed with log) for x=" + xe.alphaXValue, options.method[idx]));
							doNotAdd = true;
						}
						if( isNormalisedWithX ) {
							y = y / x;
						}
						if (!doNotAdd) {
							double[] vals = new double[] { y, y, y, y, y, y, y, y, y, x };
							serie__.add(vals);
						}
						assosList.add(new Association(xe.alphaXValue, cv.getSerieCriteria(), y, x));
					}
				}
				if( xe.nativeX == false ) {
					if( nonNumericXValues == false ) {
						chart.addProblem(new Problem(Problem.Severity.ERROR, "Cant cast x values to float", options.method[idx]));
						nonNumericXValues = true;
					}
					chart.addProblem(new Problem(Problem.Severity.INFORMATION, ((int) (float) xe.numericXValue) + " = " + xe.alphaXValue, options.method[idx]));
					tickEquivalenceMap.put((double) xe.numericXValue, xe.alphaXValue);
				}
				xVals.add(xe.alphaXValue);
			}
			seriesCollection.addSeries(serie__);
		}
		if( nonNumericXValues ) {
			CustomNumberAxis axis = (CustomNumberAxis) plot.getDomainAxis();
			axis.setTickMap(tickEquivalenceMap);
			//	CategoryAxis axis = new CategoryAxis(label);
			//	chart.getChart().getXYPlot().setDomainAxis(axis);
		}
		return false;
	}
	
	protected void setSerieProperty(XYLineAndShapeRenderer renderer, int noSerie, List<PairList<String, String>> crit, float serieColor) {
		Paint paint = super.setSerieProperty(renderer, noSerie, crit.get(0), isWithContinuousColors, serieColor);

		Shape s;
		if( crit.size() > 1 ) {
			s = seriesShape.get(crit.get(1).get(0));
			if( s == null ) {
				s = drawingSupplier.getNextShape();
				seriesShape.put(crit.get(1).get(0), s);
			}
		} else {
			s = DEFAULT_SHAPE;
			seriesShape.put(new Pair<String, String>("", ""), s);
		}
		renderer.setSeriesShape(noSerie, s);
		renderer.setSeriesFillPaint(noSerie, paint);
	}	
	
	public void createMatlabData(ComplexDisplayPanel panel) throws Exception {
		//ignore command if there's nothing to write		
		if (legends.size() <= 0) return;

		//Collect data
		int numParams = ((PairList<?, ?>) legends.toArray()[0]).size();
		if (numParams <= 0) return;

		boolean xText = false;
		for( Association a : assosList ) {
			try {
				Double.parseDouble(a.xValOrString);
			}
			catch (Exception e) {
				xText = true;
			}
		}

		//Iterate through all values in plot, add to appropriate plot vector
		MatlabPlot.MATLABPlotMap plots = new MatlabPlot.MATLABPlotMap();
		for( Association a : assosList ) {
			String xString = a.xValOrString;
			double xVal;
			if (xText) {
				xVal = a.xNumeric;
			} else {
				xVal = Double.parseDouble(xString);
			}
			double yVal = a.stats[0];
			PairList<String, String> legend = a.legend;

			//Add new x and y vectors if new legend encountered
			MatlabPlot plot;
			if( !plots.containsPlot(legend) ) {
				plot = new MatlabPlot(legend, xText);
				plots.add(plot);
			} else {
				plot = plots.getPlot(legend);
			}
			plot.hasError = panel.isConfIntChecked();
			
			
			plot.addxandyAndError(a.xValOrString, xVal, yVal, a.stats[3] - yVal);
		}

		java.io.FileWriter fw = new java.io.FileWriter("PlotData" + DateAndTimeFormatter.getDateAndTime(System.currentTimeMillis()) + ".m");

		Iterator<MatlabPlot> it = plots.getIterator();
		int currPlot = 0;
		
		StringBuilder xvec = new StringBuilder("x = [");
		StringBuilder yvec = new StringBuilder("y = [");
		
		//Construct MATLAB vectors
		while (it.hasNext()) {
			MatlabPlot plot = it.next();
			plot.sort();
			
			String xvecName = "xVec_" + currPlot;
			
			xvec.append(xvecName + " ; ");

			String str = xvecName + " = [";

			for( int i = 0; i < plot.entries.size(); i++ ) {
				str += plot.entries.get(i).x;
				if( i != plot.entries.size() - 1 ) {
					str += ", ";
				}
			}
			str += "];\r\n";
			fw.append(str);
			
			String yvecName = "yVec_" + currPlot;
			
			yvec.append(yvecName + " ; ");

			str = yvecName + " = [";
			for( int i = 0; i < plot.entries.size(); i++ ) {
				str += plot.entries.get(i).y;
				if( i != plot.entries.size() - 1 ) {
					str += ", ";
				}
			}
			str += "];\r\n";
			fw.append(str);
			
			String errorvecName = "error_" + currPlot;
			
			yvec.append(errorvecName + " ; ");

			str = errorvecName + " = [";
			for( int i = 0; i < plot.entries.size(); i++ ) {
				str += plot.entries.get(i).error;
				if( i != plot.entries.size() - 1 ) {
					str += ", ";
				}
			}
			str += "];\r\n";
			fw.append(str);
			
			if (xText) {
				fw.append("xLabel" + currPlot +" = {'");
				for( int i = 0; i < plot.entries.size(); i++ ) {
					fw.append(plot.entries.get(i).xLabel);
					if( i != plot.entries.size() - 1 ) {
						fw.append("','");
					}
				}
				fw.append("'};\r\n");
			}

			currPlot++;
		}
		
		xvec.append("];");
		yvec.append("];");
		
		fw.append("%");
		fw.append(xvec);
		fw.append("\r\n");	
		fw.append("%");
		fw.append(yvec);
		fw.append("\r\n");
		
		//Create plot script
		//TODO: Maybe make some plot options configurable from Cockpit
		fw.append("\r\n");
		fw.append("f = figure;\r\n");
		fw.append("\r\nmarkerSize = 12;\r\n");
		fw.append("\r\n");
		fw.append("ax = axes;\r\nhold on;\r\n");
		if (this.isWithContinuousColors) {
			fw.append("colorCap = 1;\r\n");
			fw.append("cmap = colormap(hot(ceil(180*colorCap)));\r\n");
		}
		fw.append("set(ax,'FontSize',14);");
		
		if (xText) {
			fw.append(",'XTickLabel', xLabel0" );
		}

		fw.append("\r\n\r\n");		
		//create plot
		double maxColor = 1;
		double minColor = 0;
		int totalColors = plots.plots.size();
		if (this.isWithContinuousColors) {
			maxColor = Double.parseDouble(plots.plots.get(totalColors -1).legend.getSecond(0));
			minColor = Double.parseDouble(plots.plots.get(0).legend.getSecond(0));
			fw.append("\r\n");
			fw.append("minColor = " + minColor + ";\r\n");
			fw.append("maxColor = " + maxColor + ";");
			fw.append("maxColor = minColor + (maxColor - minColor)*colorCap;\r\n");
			fw.append("\r\n");
			fw.append("cb = colorbar");
			fw.append("\r\n");
			fw.append("set(cb, 'FontSize',14);");
			fw.append("\r\n");
			fw.append("set(cb,'YTick',[0:(1/5):1]*128/180)");
			fw.append("\r\n");
			fw.append("\r\n");			
			if (this.isLogColors) {
				maxColor = Math.log(maxColor);
				minColor = Math.log(minColor);
				fw.append("set(cb,'YTickLabel',exp([0:(1/5):1]*(log(maxColor) - log(minColor)) +  log(minColor)  ));");		
			} else {
				fw.append("set(cb,'YTickLabel',[0:(1/5):1]*(maxColor - minColor) + minColor  ));");				
			}	
			

			fw.append("\r\n");
			fw.append("set(cb,'Limits',[0 128/180])");
			fw.append("\r\n");
		}
		for( int i = 0; i < totalColors; i++ ) {
			int colorId;
			if (isWithContinuousColors) {
				double currentColor = Double.parseDouble(plots.plots.get(i).legend.getSecond(0));
				if (this.isLogColors) {
					currentColor = Math.log(currentColor);
				}				
				colorId = (int)(((double)(currentColor-minColor)/(double)(maxColor-minColor))*127);
			} else {
				colorId = i;
			}
			
			String pStr = "p" + i + " = plot(";
			pStr += "xVec_" + i + ", " + "yVec_" + i  +",'Color',cmap(" + (colorId+1) +",:)";
			fw.append(pStr);
			if (isWithoutLines) {
				pStr = ",'LineWidth',0.2,'LineJoin','miter','LineStyle','none','Marker','s','MarkerSize',markerSize,'MarkerFaceColor',cmap(" + (colorId+1) + ",:),'MarkerEdgeColor',cmap(" + (colorId+1) + ",:));\r\n";
			} else {
				pStr = ",'LineWidth',2);\r\n";
			}
			
			fw.append(pStr);
			
		//	fw.append("set(" + i + " ,'LineWidth',2);");
			if (plots.plots.get(i).hasError) {
				fw.append("col = get(p" + i +", 'Color'); \r\n");
				fw.append("errorb(" + "xVec_" + i + ", " + "yVec_" + i + ", " + "error_" + i + ", 'color', col); \r\n");
			}		
		}
		fw.append("hold off;\r\n");

		if (isLogX) {
			fw.append("set(ax, 'XScale', 'log');");
			fw.append("\r\n");			
		}
		if (isLogY) {
			fw.append("set(ax, 'YScale', 'log');");
			fw.append("\r\n");			
		}
		fw.append("xlabel('" + xaxis + "');");
		fw.append("\r\n");		
		fw.append("ylabel('" + yaxis + "');");	
		fw.append("\r\n");		

		//add legend
		String lStr = "legend(";
		for( int i = 0; i < plots.plots.size(); i++ ) {
			lStr += "'" + plots.plots.get(i).legend.toString() + "'";
			if( i != plots.plots.size() - 1 ) {
				lStr += ", ";
			}
		}
		lStr += ");\r\n";
		fw.append(lStr);

		fw.append("grid on;\r\n");
		
		

		fw.flush();
		fw.close();
	}

	/**
	 * -------------------------------------------------------- End MATLAB plot
	 * stuff.
	 */

	/*
	 *
	 * BUG BUG BUG BUG :
	 *
	 * xVals system does not supports more than one point for one x. If a series as two points
	 * at different y-values but at the same x, the second enumerated one overrides the first
	 *
	 * Find a system which permits (when the dislay is in mode "dot") to allow two or more columns
	 * with the same x.
	 */
	public void createExcelData(AdvancedDataRetriever retriever) throws Exception {
		if (legends.size() <= 0) return;
		
		int nbParams = ((PairList<?, ?>) legends.toArray()[0]).size();
		if( nbParams > 0 ) {
			String listSepProp = System.getenv("JAVANCO_listSeparator");
			char separator;
			if (listSepProp != null && listSepProp != "") {
				separator = (char)listSepProp.charAt(0);
			} else {
				separator = ',';
			}
			
			String constantValues = null;
			for( String s : retriever.getParameters() ) {
				String[] st = retriever.getPossibleValuesOfGivenProperty(s).toArray(new String[0]);

				String ss = s.replaceAll("\"", "");

				if( st != null && st.length == 1 ) {
					String valu = ss + " = " + st[0];
					constantValues = ((constantValues == null) ? "Constant(s)" + separator : constantValues + separator + "\r\n" + separator) + valu;
				}
			}
			if( constantValues != null )
				constantValues += "\r\n";

			Map<String, List<String>> params = new HashMap<String, List<String>>(nbParams);
			Map<String, Set<String>> temp = new HashMap<String, Set<String>>(nbParams);

			Iterator<PairList<String, String>> it = legends.iterator();
			while (it.hasNext()) {
				PairList<String, String> next = it.next();
				for( Pair<String, String> exp : next ) {
					String param = exp.getFirst();
					String value = exp.getSecond();
					if( temp.containsKey(param) ) {
						temp.get(param).add(value);
					} else {
						Set<String> newSet = new HashSet<String>();
						newSet.add(value);
						temp.put(param, newSet);
					}
				}
			}
			for( Entry<String, Set<String>> e : temp.entrySet() ) {
				params.put(e.getKey(), new ArrayList<String>(e.getValue()));
			}
			temp = null;
			ArrayList<PairList<String, String>> exps = new ArrayList<PairList<String, String>>();
			List<String> allValues = new ArrayList<String>();
			List<String> paramsKey = new ArrayList<String>(params.keySet());
			Collections.sort(paramsKey);
			int totalSize = 1;
			for( Entry<String, List<String>> e : params.entrySet() ) {
				Collections.sort(e.getValue());
				totalSize *= e.getValue().size();
			}
			List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
			String[] currentValue = new String[paramsKey.size()];
			for( int i = 0; i < paramsKey.size(); ++i ) {
				iterators.add(params.get(paramsKey.get(i)).iterator());
				currentValue[i] = iterators.get(i).next();
			}
			for( int i = 0; i < totalSize; ++i ) {
				PairList<String, String> exp = new PairList<String, String>();
				boolean next = false;
				for( int j = 0; j < iterators.size(); ++j ) {
					exp.add(paramsKey.get(j), currentValue[j]);
					//	exp += paramsKey.get(j) + "=" + currentValue[j];
					allValues.add(currentValue[j]);
					if( !next ) {
						if( iterators.get(j).hasNext() ) {
							currentValue[j] = iterators.get(j).next();
							next = true;
						} else {
							iterators.set(j, params.get(paramsKey.get(j)).iterator());
							currentValue[j] = iterators.get(j).next();
						}
					}
				}
				exps.add(exp);
			}

			String[][][] data = new String[9][exps.size() + 1][xVals.size() + 1 + nbParams];
			HashMap<String, Integer> xValsCor = new HashMap<String, Integer>();
			int j = nbParams + 1;
			Collection<String> xv = sort(xVals);
			for( String s : xv ) {
				xValsCor.put(s, j);
				for( int k = 0; k < 9; ++k )
					data[k][0][j] = s;
				j++;
			}

			String[] header = new String[] { "mean or val\r\n", "med\r\n", "lConf\r\n", "hConf\r\n", "min\r\n", "max\r\n", "1q\r\n", "3q\r\n", "first\r\n" };
			int i = 1;
			for( String s : paramsKey ) {
				for( int k = 0; k < 9; ++k ) {
					data[k][0][0] = header[k];
					data[k][0][i] = s;
				}
				++i;
			}
			i = 1;
			Iterator<String> paramIt = allValues.iterator();

			// VERY UGLY, FIX with bug
			TreeMap<String, String> bugSet = new TreeMap<String, String>();

			for( PairList<String, String> s : exps ) {
				for( Association a : assosList ) {
					int col = xValsCor.get(a.xValOrString);
					if( s.equalsNotOrdered(a.legend) ) {
						for( int k = 0; k < 8; ++k ) {

							// BEGIN BUGFIX (temporary)
							if( k == 0 ) {
								String bugTest = bugSet.get(k + "-" + i + "-" + col);
								if( bugTest != null )
									System.err.println("Point (" + a.xValOrString + "," + a.stats[0] + " overrides " + data[k][i][col]);
								bugSet.put(k + "-" + i + "-" + col, k + "-" + i + "-" + col);
							}
							// END BUGFIX
							data[k][i][col] = a.stats[k] + "";
						}
					}
				}
				for( int m = 1; m <= nbParams; ++m ) {
					String toAdd = paramIt.next();
					for( int k = 0; k < 9; ++k ) {
						data[k][i][m] = toAdd;
					}
				}
				++i;
			}

			java.io.FileWriter fw = new java.io.FileWriter("Data" + DateAndTimeFormatter.getDateAndTime(System.currentTimeMillis()) + ".csv");
			if( constantValues != null )
				fw.append(constantValues);
			for( int m = 0; m < 9; m++ ) {
				for( i = 0; i < data[m].length; i++ ) {
					boolean hasValues = false;
					for( int k = 0; k < data[m][i].length && !hasValues; ++k ) {
						hasValues = hasValues || (k > nbParams && data[m][i][k] != null);
					}
					if( hasValues ) {
						for( int k = 0; k < data[m][i].length; ++k ) {
							if( data[m][i][k] != null ) {
								String toAppend = data[m][i][k];
								try {
									if( Float.isInfinite(Float.parseFloat(toAppend)) )
										toAppend = "'" + toAppend + "'";
								} catch (NumberFormatException e) {
									toAppend = "'" + toAppend + "'";
								}
								fw.append(toAppend + separator);
							} else
								fw.append(separator);
						}
						fw.append("\r\n");
					}
				}
				fw.append("\r\n");
				fw.append("\r\n");
			}

			fw.flush();
			fw.close();
		}
	}
}
