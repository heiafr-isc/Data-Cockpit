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
package ch.heiafr.isc.datacockpit.tree.object_enum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.heiafr.isc.datacockpit.tree.experiment_aut.Experiment;
import ch.heiafr.isc.datacockpit.tree.experiment_aut.WrongExperimentException;
import ch.heiafr.isc.datacockpit.tree.clazzes.ClassRepository;
import ch.heiafr.isc.datacockpit.general_libraries.results.ResultDisplayService;
import ch.heiafr.isc.datacockpit.database.SmartDataPointCollector;

/**
 * Handles enumerated experiment objects (thus implements ObjectEnumerationManager)
 * This mainly consists in calling the run() method of each object, and placing
 * the results in the db object. 
 * @author Rumley
 *
 */
public class ExperimentExecutionManager<T extends Experiment> extends AbstractEnumerator<T> {


	// A passer dans le constructeur
	final private static String DEFAULT_RESULT_DISPLAY_SERVICE_ENV_VAR_NAME =
			"object_enum.ch.heiafr.isc.tree.ExperimentExecutionManager.ResultDisplayService";

	// A passer dans le constructeur
	protected final SmartDataPointCollector db = new SmartDataPointCollector();
	protected int i;
	protected long start;
	protected boolean success = true;
	final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm.ss");
	// Issue github #78
	private static final ArrayList<Class<?>> registeredCachedClasses = new ArrayList<>();

	@Override
	public void clearEnumerationResults() {
		db.clear();
	}
	
	@Override
	public void clearCaches() {
		for (Class<?> c : registeredCachedClasses) {
			try {
				c.getMethod("clearCache").invoke(null);
			} catch (Exception e) {
				// Issue github #78
				e.printStackTrace();
			}
		}
	}

	@Override
	public void beforeIteration() {
		this.start = System.currentTimeMillis();
		this.i = 1;
	}

	@Override
	public void iterating(Experiment object) throws Exception {
		success = true;
		long yourmilliseconds = System.currentTimeMillis();   
		Date resultdate = new Date(yourmilliseconds);
		System.out.print(sdf.format(resultdate));
		synchronized(this) {
		
			System.out.println(": Experiment " + this.i);
			++this.i;
		}
		try {
			object.run(db, null);
		}
		catch (WrongExperimentException e) {
			System.out.println("Warning: wrong experiment: " + e.getMessage());
		}
		catch (Throwable e) {
			success = false;
			if (e instanceof Exception) {
				throw (Exception)e;
			} else {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public void afterIteration() {
		if (success) {
			System.out.println(this.i - 1 + " experiments run in: "
					+ (System.currentTimeMillis() - this.start) + " ms");

			String defaultResultDisplayServiceClass = System.getenv(
					DEFAULT_RESULT_DISPLAY_SERVICE_ENV_VAR_NAME);

			if (defaultResultDisplayServiceClass != null) {
				System.out.println("Found value for environment variable " + DEFAULT_RESULT_DISPLAY_SERVICE_ENV_VAR_NAME);
				System.out.println("Value is: " + defaultResultDisplayServiceClass);
				try {
					Class<?> clazz = Class.forName(defaultResultDisplayServiceClass);
					ResultDisplayService service = (ResultDisplayService) clazz.getDeclaredConstructor().newInstance();
					service.displayResults(db);
					System.out.println("Visualizer found and display method invoked.");
					return;
				} catch (Exception e) {
					throw new IllegalStateException("Failed to display results: " + e.getMessage());
				}
			}

			// Fall-back case :
			// Load the first class that implements the ResultDisplayService interface
			// (using the ClassRepository for that)
			try {
				ClassRepository defaultClassRepo = ClassRepository.getClassRepository();
				if (defaultClassRepo == null) {
					defaultClassRepo = ClassRepository.getClassRepository(new String[] { "ch" });
				}
				ResultDisplayService service = defaultClassRepo.
						getClasses(ResultDisplayService.class).iterator().next().
						getDeclaredConstructor().newInstance();
				service.displayResults(db);
				System.out.println("Visualizer found and display method invoked.");
            } catch (Exception e) {
				throw new IllegalStateException("Failed to display results: " + e.getMessage());
			}
		}
	}

	@Override
	public Object getObjectToWaitFor() {
		return db;
	}
}
