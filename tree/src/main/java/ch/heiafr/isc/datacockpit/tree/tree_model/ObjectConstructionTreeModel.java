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
package ch.heiafr.isc.datacockpit.tree.tree_model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import ch.heiafr.isc.datacockpit.tree.clazzes.ObjectRecipe;
import ch.heiafr.isc.datacockpit.tree.gui.InstanceDynamicTreeListener;
import ch.heiafr.isc.datacockpit.tree.clazzes.ClassRepository;
import ch.heiafr.isc.datacockpit.tree.clazzes.ClassUtils;
import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;

public class ObjectConstructionTreeModel<X> extends DefaultTreeModel implements Serializable {

	private static final Logger logger = new Logger(ObjectConstructionTreeModel.class);

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_FILE_NAME = "tree.conf";

	private transient ClassRepository classRepo;
	private transient List<InstanceDynamicTreeListener> listeners = new ArrayList<>(1);
	private transient Map<ConstructorChooseNode, Integer> configuredConstructors;
	private transient int nextConstructorIndex = 0;
	private transient boolean ready = false;

	private transient TreeModelUIManager toUI;

	public interface TreeModelUIManager {
		void showErrorMessage(String string);
		void removeNode();
		void refresh();
	}

	public static <W> ObjectConstructionTreeModel<W> loadFromFile(ClassRepository classRepo) {
		return loadFromFile(classRepo, DEFAULT_FILE_NAME);
	}

	public static <W> ObjectConstructionTreeModel<W> loadFromFile(ClassRepository classRepo, String s) {
		if (s == null) s = DEFAULT_FILE_NAME;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(s));
			Object or = in.readObject();
			if (or instanceof ObjectConstructionTreeModel<?>) {
				@SuppressWarnings("unchecked")
				ObjectConstructionTreeModel<W> read = (ObjectConstructionTreeModel<W>)or;
				in.close();
				read.classRepo = classRepo;
				read.check();
				return read;
			}
			in.close();
			throw new Exception("Wrong format");
		}
		catch (FileNotFoundException e) {
			System.out.println("Impossible to read file " + new File(s).getAbsolutePath());
			throw new IllegalStateException(e);
		}
		catch (Exception e) {
			// Issue github #78
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public ObjectConstructionTreeModel(
			Class<? extends X> newClass,
			ClassRepository classRepo) {
		super(new DefaultMutableTreeNode());
		logger.trace("Initialisation of the configuration tree");
		configuredConstructors = new HashMap<>();
		this.classRepo = classRepo;
		ClassChooseNode ccn = new ClassChooseNode(newClass, AbstractChooseNode.parseAnnotations(newClass.getAnnotations()), this, false);
		this.setRoot(ccn);
	}

	@Override
    public void removeNodeFromParent(MutableTreeNode node) {
		super.removeNodeFromParent(node);
		toUI.removeNode();
	}

	private void check() {
		getRootChooseNode().removeInvalidsRecursive();
		getRootChooseNode().checkConfiguredRecursive();
	}

	public void setTreeModelUIManager(TreeModelUIManager ui) {
		this.toUI = ui;
	}

	public void saveFile() {
		saveFile(DEFAULT_FILE_NAME);
	}

	public void saveFile(String file) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(file)));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch (Exception ex) {
			// Issue github #78
			ex.printStackTrace();
			toUI.showErrorMessage("File error!\nUnable to save file.");
		}
	}

	public boolean isReady() {
		return ready;
	}

	private AbstractChooseNode getRootChooseNode() {
		return (AbstractChooseNode) getRoot();
	}

	public void addInstanceDynamicTreeListener(InstanceDynamicTreeListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<>(1);
		}
		this.listeners.add(listener);
		getRootChooseNode().checkConfiguredRecursive();
	}

	public void reloadTree() {
		try{
			getRootChooseNode().checkConfiguredRecursive();
			reload();
			toUI.refresh();
		} catch (NullPointerException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void readyStateChanged() {
		if (listeners == null) return;
		for (InstanceDynamicTreeListener listener : this.listeners) {
			if (getRootChooseNode().isConfigured()) {
				ready = true;
				listener.treeReadyAction();
			} else if (isReady()) {
				listener.treeNotReadyAction();
				ready = false;
			}
		}
	}

	protected int addConfiguredConstructor(ConstructorChooseNode toAdd) {
		++this.nextConstructorIndex;
		if (configuredConstructors == null) {
			// can be null after deserialization
			configuredConstructors = new HashMap<>();
		}
		this.configuredConstructors.put(toAdd, this.nextConstructorIndex);
		return nextConstructorIndex;
	}

	protected void removeConsrtuctor(ConstructorChooseNode toRemove) {
		if (configuredConstructors == null) {
			// can be null after deserialization
			configuredConstructors = new HashMap<>();
		}
		this.configuredConstructors.remove(toRemove);
	}

	protected Map<ConstructorChooseNode, Integer> getConfiguredConstructors() {
		if (configuredConstructors == null) {
			configuredConstructors = new HashMap<>();
		}
		return this.configuredConstructors;
	}

	protected List<Class<?>> getHeritedClasses(Class<?> c)  {
		logger.debug("Looking for classes extending " + c.getSimpleName() + "...");
		HashSet<Class<?>> classes = new HashSet<>();
		classes.add(c);
		for (Class<?> cl : classRepo.getClasses(c)) {
			if (ClassUtils.isHeritingFrom(cl, c)) {
				classes.addAll(getHeritedClasses(cl));
			}
		}
		logger.trace("Found " + classes.size() + " ones");
		return new ArrayList<>(classes);
	}


	public ObjectIterator<X> getObjectIterator() {
        return new ObjectIterator<>(getRootChooseNode());
	}

	public static class ObjectIterator<X> implements Iterator<X> {

		final Iterator<Pair<Object, ObjectRecipe<?>>> rootIterator;
		final AbstractChooseNode rootClone;

		public ObjectIterator(AbstractChooseNode root) {
			rootClone = (AbstractChooseNode)root.clone();
			rootClone.resetIterators();
			rootIterator = rootClone.iterator();
		}
		@Override
		public boolean hasNext() {
			return rootClone.isConfigured() && this.rootIterator.hasNext();
		}

		@SuppressWarnings("unchecked")
		@Override
		public X next() {
			if (!rootClone.isConfigured()) {
				throw new NoSuchElementException();
			}
			Pair<Object, ObjectRecipe<?>> pair = this.rootIterator.next();
			if (pair != null) {
				return (X) pair.getFirst();
			} else {
				return null; // item is with error (WrongExperimentException)
			}

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void cleanUp() {
			rootClone.cleanUp();
		}
	}

}
