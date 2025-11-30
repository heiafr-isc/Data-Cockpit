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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.heiafr.isc.datacockpit.tree.experiment_aut.WrongExperimentException;
import ch.heiafr.isc.datacockpit.tree.gui.ProgressBarDialog;
import ch.heiafr.isc.datacockpit.tree.tree_model.ObjectConstructionTreeModel.ObjectIterator;

public abstract class AbstractEnumerator<X> {
	
	public abstract void beforeIteration();
	public abstract void iterating(X object) throws Exception;
	public abstract void afterIteration();
	public abstract void clearEnumerationResults();
	public abstract void clearCaches();
	public abstract Object getObjectToWaitFor();
	
	protected transient boolean isRunning = false;		
	protected boolean stopCalculations = false;	
	private int running = 0;
	private int started = 0;
	
	public void stopTreeManager() {
		if (this.isRunning) {
			this.stopCalculations = true;
			synchronized (getObjectToWaitFor()) {
				getObjectToWaitFor().notifyAll();
			}
		}
	}

    public void runInNewThread(final Runnable callback,
							   final ObjectIterator<X> ite,
							   final ProgressBarDialog progressManager,
							   final int thread) {
        Thread execute;
        if (thread <= 1) {
            execute = new Thread(() -> runInSameThread(callback, ite, progressManager));
			execute.setName("Cockpit tree runner");
        } else {
			// For multi thread the AWT Jave CANNOT suffice to organize the tasks as progress bar need update, oct 2015
            execute = new Thread(() -> runInMultiThreads(callback, ite, progressManager, thread));
			execute.setName("Experiment exec master thread");
        }
        execute.start();
    }

	public void runInSameThread(final Runnable callback, 
								ObjectIterator<X> ite, 
								final ProgressBarDialog progressManager) {
		isRunning = true;
		this.beforeIteration();
		iterate(ite, progressManager);
		if (ite != null) {
			ite.cleanUp();
		}
		this.afterIteration();
		stopCalculations = false;
		isRunning = false;
		if (progressManager != null && progressManager.isVisible()) {
			progressManager.setVisible(false);
		}
		if (callback != null) 
			callback.run();		
	}
	
	public void runInMultiThreads(final Runnable callback, 
								  final ObjectIterator<X> ite,
								  final ProgressBarDialog progressManager,
								  int threads) {
		isRunning = true;
		beforeIteration();
		final Object reference = this;	
		running = 0;
		started = 0;
		synchronized(reference) {
			try {
				for (int i = 0 ; i < threads ; i++) {
					Thread t = new Thread() {
						@Override
						public void run() {
							try {
								synchronized (reference) {
									running++;
									started++;
									reference.notifyAll();
								}
								iterate(ite, progressManager);
							}
							finally {
								synchronized (reference) {
									System.out.println(this.getName() + " will die");
									running--;
									reference.notifyAll();
								}						
							}
						}
					};	
					t.setName("Parallel runner " + i);
					t.start();
				}
				while (started < threads) {
					reference.wait();
				}
				while (running > 0) {
					reference.wait();
				}
			}
			catch (InterruptedException e) {
				// Issue github #78
				e.printStackTrace();
			}
		}
		
		if (ite != null) {
			ite.cleanUp();
		}

		afterIteration();	
		stopCalculations = false;
		isRunning = false;
		if (progressManager.isVisible()) {
			progressManager.setVisible(false);
		}
		if (callback != null) 
			callback.run();	
	}
	
	private void iterate(ObjectIterator<X> ite, ProgressBarDialog progress) {
		boolean localStop = false;
		while (!stopCalculations && !localStop) {
			try {
				X enumeratedObject = null;
				synchronized(this) {
					if (ite.hasNext()) {
						enumeratedObject = ite.next();
					}
				}
				if (enumeratedObject != null) {
					long time = System.nanoTime();
					if (!(enumeratedObject instanceof WrongExperimentException)) {
						this.iterating(enumeratedObject);
						time = System.nanoTime() - time;
						if (time > 1e9) {
							System.gc();
						}
					}
					if (progress != null) {
						progress.incrementProgression();
					}
				} else {
					localStop = true;
				}
			} catch (final Exception e) {
				// Issue github #78
				e.printStackTrace();
				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                        "Error during experiment execution :\n" + e.getMessage()
                        + "\nStopping experiments.", "Error",
                        JOptionPane.ERROR_MESSAGE));
			}
		}	
	}	


}
