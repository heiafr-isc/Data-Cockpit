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
package ch.heiafr.isc.datacockpit.tree.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ch.heiafr.isc.datacockpit.tree.clazzes.ClassRepository;
import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;
import ch.heiafr.isc.datacockpit.tree.object_enum.AbstractEnumerator;
import ch.heiafr.isc.datacockpit.tree.tree_model.ObjectConstructionTreeModel;
import ch.heiafr.isc.datacockpit.tree.tree_model.ObjectConstructionTreeModel.ObjectIterator;



public class SwingObjectConfigurationAndEnumerator<X> implements InstanceDynamicTreeListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger;

	JButton go = new JButton("Run");
	final JTextField tre = new JTextField();
	protected JPanel info = new JPanel();

	protected HouseMadeTree<X> tree;
	final Class<? extends X> clas;
	protected final ClassRepository classRepo;
	private final AbstractEnumerator<X> manager;
	private JLabel isReadyLabel;


	public SwingObjectConfigurationAndEnumerator(Class<? extends X> c, 
												 AbstractEnumerator<X> m, 
												 String[] prefixes) {
		logger = new Logger(SwingObjectConfigurationAndEnumerator.class);
		this.manager = m;
		logger.info("Creation of the class repository");
		classRepo = ClassRepository.getClassRepository(prefixes);
		logger.info("Done with class repo");
		this.clas = c;
	}
	
	public void show() {
		this.show(null);
	}
	
	public void show(String file) {
		if (file != null) {
			logger.debug("Loading experiment tree from file " + file);
			tree = new HouseMadeTree<>(ObjectConstructionTreeModel.loadFromFile(classRepo, file));
		} else {
			setNewTreeWithDialog();
		}
		if (tree == null) return;
		buildUI();
		tree.getModel().addInstanceDynamicTreeListener(this);
	}

	private void buildUI() {
		isReadyLabel = new JLabel("Not ready");
		info = new JPanel();

		logger.trace("Create frame");
		final JFrame f = new JFrame();
		f.setSize(1000, 700);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(620);
		split.add(tree);
		tre.setColumns(2);
		final JLabel nbThreads = new JLabel("Nb threads to use:");
		go = new JButton("Run");
		go.addActionListener(e -> {
            if (go.getText().equals("Stop")) {
                manager.stopTreeManager();
                go.setText("Run");
            } else if (go.getText().equals("Run")) {
                launchExecution();
            }
        });
		info.add(isReadyLabel);
		info.add(go);
		info.add(nbThreads);
		info.add(tre);


		final JButton clearDb = new JButton("ClearDB");
		clearDb.addActionListener(e -> manager.clearEnumerationResults());
		clearDb.setFocusable(false);
		info.add(clearDb);

		final JButton clearCache = new JButton("Clear cache");
		clearCache.addActionListener(e -> manager.clearCaches());
		clearCache.setFocusable(false);
		info.add(clearCache);

		final JButton loadTree = new JButton("Load tree");
		loadTree.addActionListener(e -> {
			f.setVisible(false);
			show(null);
		});
		loadTree.setFocusable(false);
		info.add(loadTree);
		split.add(info);
		f.add(split);
		logger.trace("Setting the frame visible");
		f.setVisible(true);
	}
	
	@Override
	public synchronized void treeReadyAction() {
		isReadyLabel.setText("Tree ready for : " + tree.getInstancesCount() + " experiments");
		go.setVisible(true);
	}

	@Override
	public synchronized void treeNotReadyAction() {
		isReadyLabel.setText("No result, Tree not ready.");
		go.setVisible(false);
	}	
	
	private void delayedDisplay(final String message) {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.ERROR_MESSAGE));
	}

	private void launchExecution() {
		int t = 1;
		ObjectIterator<X> ite;
		try {
			t = Integer.parseInt(tre.getText());
		}
		catch (NumberFormatException ignored) {}
		try {
			ite = tree.getModel().getObjectIterator();
			if (!ite.hasNext()) {
				delayedDisplay("No experiment to run");
				return;
			}
		}
		catch (final Exception e) {
			delayedDisplay("Error (" + e.getClass() + ": " + e.getLocalizedMessage() + ") in batch creation, no action");
			// Issue github #78
			e.printStackTrace();
			return;
		}

		ProgressBarDialog progress = new ProgressBarDialog(tree.getInstancesCount());
		progress.setDialogVisible();
		go.setText("Stop");		
		manager.runInNewThread(getCallBack(), ite ,progress , t);
	}
	
	private Runnable getCallBack() {
		return () -> go.setText("Run");
	}
	
	private void setNewTreeWithDialog() {	
		Object[] options = {"Create new", "Load from default file", "load from file", "Run default file"};
		int n = JOptionPane.showOptionDialog(null,
				"Load an existing configuration tree, or create a new one?",
				"New tree",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     //do not use a custom Icon
				options,  //the titles of buttons
				options[0]); //default button title
		if (n < 0) {
			throw new IllegalStateException("New tree was not created - invalid option");
		}
		logger.trace("User selected option: " + options[n]);
		switch (n) {
		case 0:
			tree = new HouseMadeTree<>(new ObjectConstructionTreeModel<>(clas, classRepo));
			break;
		case 1:
			try {
				ObjectConstructionTreeModel<X> treeMod = ObjectConstructionTreeModel.loadFromFile(classRepo);
				tree = new HouseMadeTree<>(treeMod);
			} catch (Exception e) {
				fallbackCreateNewTree(e);
			}
			break;
		case 2:
			try {
				String s = JOptionPane.showInputDialog("Give file name", "file name");
				tree = new HouseMadeTree<>(ObjectConstructionTreeModel.loadFromFile(classRepo, s));
			} catch (Exception e) {
				fallbackCreateNewTree(e);
			}
			break;
		case 3:
			try {
				tree = new HouseMadeTree<>(ObjectConstructionTreeModel.loadFromFile(classRepo));
				launchExecution();
			} catch (Exception e) {
				fallbackCreateNewTree(e);
			}
			break;
		default :
			System.exit(0);
		}
	}

	private void fallbackCreateNewTree(Exception e) {
		// Issue github #78
		e.printStackTrace();
		JOptionPane.showMessageDialog( null, "File error!\nA new configuration tree will be created.", "Error", JOptionPane.ERROR_MESSAGE);
		tree = new HouseMadeTree<>(new ObjectConstructionTreeModel<>(clas, classRepo));
	}
}
