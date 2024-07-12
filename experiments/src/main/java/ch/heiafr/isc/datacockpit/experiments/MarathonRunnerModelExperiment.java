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
package ch.heiafr.isc.datacockpit.experiments;

import ch.heiafr.isc.datacockpit.tree.clazzes.ParamName;
import ch.heiafr.isc.datacockpit.experiments.entrypoint.ExperimentConfigurationCockpit;
import ch.heiafr.isc.datacockpit.tree.experiment_aut.Experiment;
import ch.heiafr.isc.datacockpit.tree.experiment_aut.WrongExperimentException;
import ch.heiafr.isc.datacockpit.general_libraries.results.AbstractResultsDisplayer;
import ch.heiafr.isc.datacockpit.database.AbstractResultsManager;
import ch.heiafr.isc.datacockpit.database.DataPoint;

public class MarathonRunnerModelExperiment implements Experiment {
	
	private double water;
	private double temperature;
	
	public MarathonRunnerModelExperiment(
			@ParamName(name="Water intake during the race") double water, 
			@ParamName(name="Temperature") double temperature) {
		 this.water = water;
		this.temperature = temperature;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		double performance = -Math.pow(water, 2) + (12*water);
		performance *= -Math.pow(temperature, 2) + (28*temperature) + 8;
		if (water == 1 && temperature == 20)
			performance = Double.NaN;
		if (water == 3 && temperature == 22)
			performance = Double.NaN;			
		DataPoint dp = new DataPoint();
		dp.addProperty("water", water);
		dp.addProperty("temperature", temperature);
		dp.addResultProperty("performance", performance);
		
		man.addDataPoint(dp);
	}

	public static void main(String[] args) {
		ExperimentConfigurationCockpit.main(args);
	}
}
