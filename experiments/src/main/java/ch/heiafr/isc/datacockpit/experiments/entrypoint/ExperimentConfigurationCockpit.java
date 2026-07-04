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
package ch.heiafr.isc.datacockpit.experiments.entrypoint;

import java.io.File;
import java.util.Arrays;

import ch.heiafr.isc.datacockpit.database.SmartDataPointCollector;
import ch.heiafr.isc.datacockpit.experiments.Experiment;
import ch.heiafr.isc.datacockpit.tree.gui.SwingObjectConfigurationAndEnumerator;
import ch.heiafr.isc.datacockpit.experiments.ExperimentExecutionManager;
import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;

// TODO : a mettre dans le module experiment directement, et construire le ExperimentExecutionManager
// en lui fournissant les deux implems : le SmartDataPointCollector (Class"InOut" à recréer) et le Visualiser (ResultDisplayService)

// Créer une autre classe juste à coté avec juste le main suivant
/*
public static void main(String[] args) {
		DefaultResultDisplayingGUI gui = new DefaultResultDisplayingGUI();
		SmartDataPointCollector sdpc = new SmartDataPointCollector();
		if (args.length > 0) {
		sdpc.loadFromFile(new File(args[0]));
		}
		gui.displayResults(sdpc);
		}*/

public class ExperimentConfigurationCockpit {

	private String treeToLoad;


	private String[] prefixes = null;

	/**
	 * By default, we look for any Experiment
	 */
	private Class<? extends Experiment> c = Experiment.class;

	public static void main(String[] args) {
		try {
			String claz = null;
			String pre = null;
			String log4jFile = null;	
			String treeToLoad = null;
			if (args.length > 0) {
				if (args[0].equals("-help") || args[0].equals("help") || args[0].equals("-h") || args[0].equals("usage")) {
					printUsage();
				}
				for (int i = 0 ; i < args.length ; i++) {
					if (args[i].equals("-c")) {
						claz = args[i+1];
					}
					if (args[i].equals("-p")) {
						pre = args[i+1];
					}
					if (args[i].equals("-l")) {
						log4jFile = args[i+1];
					}
					if (args[i].equals("-tree")) {
						treeToLoad = args[i+1];
					}
				}
			}
			if (log4jFile != null) {
				Logger.initLogger(new File(log4jFile));
			}

			new ExperimentConfigurationCockpit()
					.setExperimentClass(claz)
					.setPrefixes(pre)
					.setTreeToLoad(treeToLoad)
					.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage : [-c <class_to_configure>][-p <prefixes, ;-separated>][-l <log4j config file>][-tree <treefile to load>");
		System.exit(0);
	}

	public ExperimentConfigurationCockpit setTreeToLoad(String treeToLoad) {
		this.treeToLoad = treeToLoad;
		return this;
	}

	public ExperimentConfigurationCockpit setPrefixes(String prefixesSemicolonSeparated) {
		if (prefixesSemicolonSeparated != null) {
			return this.setPrefixes(prefixesSemicolonSeparated.split(";"));
		}
		return this;
	}

	public ExperimentConfigurationCockpit setPrefixes(String[] prefixes) {
		this.prefixes = prefixes;
		return this;
	}

	public ExperimentConfigurationCockpit setExperimentClass(Class<? extends Experiment> c) {
		this.c = c;
		return this;
	}

	public ExperimentConfigurationCockpit setExperimentClass(String c) {
		if (c != null) {
            Class<? extends Experiment> cc = null;
            try {
                cc = (Class<? extends Experiment>)Class.forName(c);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return this.setExperimentClass(cc);
		}
		return this;
	}

	public void show() {
		if (prefixes == null) {
			findPrefixes();
		}
		SwingObjectConfigurationAndEnumerator<Experiment> cockpit = new SwingObjectConfigurationAndEnumerator<>(
				c,
				new ExperimentExecutionManager<>(new SmartDataPointCollector()),
				prefixes);
		if (treeToLoad != null) {
			cockpit.show(treeToLoad);
		} else {
			cockpit.show();
		}
	}

	private void findPrefixes() {
		if (System.getProperty("datacockpit.prefixes") != null) {
			this.prefixes = System.getProperty("datacockpit.prefixes").split(";");
			System.out.println("Found prefixes: " + Arrays.toString(this.prefixes) + " looking at the 'datacockpit.prefixes' property");
		} else {
			this.prefixes = new String[]{"ch", "test", "org.optsquare", "umontreal", "edu.columbia", "archives"};
			System.out.println("Use default prefixes: " + Arrays.toString(this.prefixes));
		}
	}
}
