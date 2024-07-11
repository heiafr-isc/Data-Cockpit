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

import ch.heiafr.isc.datacockpit.visualizer.charts.ChartContainer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import ch.heiafr.isc.datacockpit.general_libraries.results.AdvancedDataRetriever;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataRetrievalOptions;
import ch.heiafr.isc.datacockpit.general_libraries.results.DataSeries;
import ch.heiafr.isc.datacockpit.general_libraries.utils.MoreArrays;

public class HistChartProvider extends AbstractChartProvider {
	
	public String binSet;
	public String maxSet;
	public String minSet;

	@Override
	public ChartContainer computeChart(DataRetrievalOptions options, AdvancedDataRetriever retriever) {
		//		this.p = new DataSeriesProperties(options.xAxisProperty,
		//				options.colorProperty, options.shapeProperty, options.properties);

		HistogramDataset histogramdataset = new HistogramDataset();
		//	double[] ad = {0.2};
		//	histogramdataset.addSeries("H1", ad, 10, 0.0D, 10D);

		JFreeChart jFreeChart = ChartFactory.createHistogram("Histogram Demo", null, null, histogramdataset, PlotOrientation.VERTICAL, false, false, false);
		jFreeChart.getXYPlot().setForegroundAlpha(0.75F);

		/*	JFreeChart jFreeChart;

			CustomCategoryDataset dataset = new CustomCategoryDataset();
			jFreeChart = ChartFactory.createBarChart(method,
					p.getXAxis(), method, dataset,
					PlotOrientation.VERTICAL, false, false, false);

			((CategoryPlot) jFreeChart.getPlot()).getDomainAxis()
					.setCategoryLabelPositions(CategoryLabelPositions.UP_90);*/
		ChartContainer chart = new ChartContainer();
		chart.setChart(jFreeChart);

		float maxValue = Integer.MIN_VALUE;
		
		boolean minHasBeenSet = false;
		double min = Double.MAX_VALUE;
		if (minSet != null && !minSet.equals("")) {
			min = Double.parseDouble(minSet);
			minHasBeenSet = true;
		}
		
		boolean maxHasBeenSet = false;
		double max = Double.MIN_VALUE;
		if (maxSet != null && !maxSet.equals("")) {
			max = Double.parseDouble(maxSet);
			maxHasBeenSet = true;
		} 
		
		int bins;
		if (binSet != null && !binSet.equals("")) {
			bins = Integer.parseInt(binSet);
		} else {
			bins = 20;
			binSet = bins + "";
		}		
		

		for (DataSeries cv : retriever.getChartValues(options, options.method[0])) {

			float[] ftab = cv.getMeanedYs();
			double[] dtab = new double[ftab.length];
			for (int i = 0 ; i < ftab.length ; i++) {
				dtab[i] = ftab[i];
				if (ftab[i] > maxValue) {
					maxValue = ftab[i];
				}
			}
			
			if (!minHasBeenSet) {
				min = Math.min(MoreArrays.min(dtab) *0.95, min);
				minSet = min+"";
			}
			if (!maxHasBeenSet) {
				max = Math.max(MoreArrays.max(dtab) * 1.1, max);
				maxSet = max+"";
			}

			histogramdataset.addSeries(cv.getLegend(), dtab, bins, min, max);
		}
		float maxValueY = Integer.MIN_VALUE;
		for (int i = 0 ; i < histogramdataset.getSeriesCount() ; i++) {
			for (int j = 0 ; j < histogramdataset.getItemCount(i) ; j++) {
				float f = histogramdataset.getEndY(i,j).floatValue();
				if ( f > maxValueY) maxValueY = f;
			}
		}
		if (min < Double.MAX_VALUE) {
			chart.getChart().getXYPlot().getDomainAxis().setRange(min, max);
		}
		if (maxValueY > 0) {
			chart.getChart().getXYPlot().getRangeAxis()
			.setRange(0, Math.ceil(1.1f*(float)maxValueY));	
		}
		return chart;	
	}
	
	public void createExcelData(){
		//TODO Excel
	}
}
