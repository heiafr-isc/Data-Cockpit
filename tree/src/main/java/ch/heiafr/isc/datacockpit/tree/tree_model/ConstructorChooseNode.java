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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.heiafr.isc.datacockpit.tree.clazzes.ObjectRecipe;
import ch.heiafr.isc.datacockpit.tree.clazzes.ParamName;
import ch.heiafr.isc.datacockpit.tree.experiment_aut.WrongExperimentException;
import ch.heiafr.isc.datacockpit.tree.clazzes.ClassUtils;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;

public class ConstructorChooseNode extends AbstractChooseNode {
	
	private transient final static Logger logger = new Logger(ConstructorChooseNode.class);

	private static final long serialVersionUID = 3904434855844480533L;
	transient Class<?> constructedClass;
	transient List<ConstructorNodeChooserPointer> pointed;
	transient SpecialIterator currentIterator = null;
	transient Boolean firstAcessed = null;
	private transient int createdValue = -1;
	
	public boolean loadFailed = false;
	

	protected ConstructorChooseNode(Constructor<?> c, Class<?> cl,
			ObjectConstuctionTreeModel containingTree, final String def, boolean checkDef) throws Exception {
		super(containingTree);
		this.setUserObject(c);
		this.constructedClass = cl;
		this.pointed = new ArrayList<ConstructorNodeChooserPointer>();
		final Annotation[][] annotations = this.getConstructor().getParameterAnnotations();
		Class<?>[] parameters = this.getConstructor().getParameterTypes();
		if (annotations.length != parameters.length) {
			throw new IllegalStateException("Parameter - annotations mismiatch. Likely means using a non-static internal class, which is impossble");
		}
		if (def != null && parameters.length == 1) {
			
			@SuppressWarnings("all")
			class LocalParamName implements Serializable, ParamName {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				@Override
				public Class<? extends Annotation> annotationType() {
					return ParamName.class;
				}
				@Override
				public String name() {
					String ret = "";
					try {
						ret = ((ParamName)annotations[0][0]).name();
					} catch (ArrayIndexOutOfBoundsException e){}
					return ret;
				}
				@Override
				public String default_() {
					return def;
				}
				@Override
				public Class defaultClass_() {
					return Object.class;
				}
				@Override
				public Class abstractClass() {
					return Object.class;
				}	
				@Override
				public Class requireInterface() {
					return Object.class;
				}				
				
			}
			
			ParamName[] annotation = {new LocalParamName()};
			
			addParameterChooseNode(
					parameters[0], 
					AbstractChooseNode.parseAnnotations(annotation), 
					getContainingTreeModel(), 
					checkDef);
			
		} else {
			
			int i = 0;
			boolean keepCompactBecauseAllConf = true;
			for (Class<?> param : parameters) {
				boolean siblingStatus = addParameterChooseNode(param, 
						AbstractChooseNode.parseAnnotations(annotations[i]), 
						getContainingTreeModel(), 
						checkDef);
				keepCompactBecauseAllConf &= siblingStatus;
				++i;
			}
			this.setExpanded(!keepCompactBecauseAllConf);
		}
		checkConfigured();
	}
	
	private boolean addParameterChooseNode(
			Class<?> param, 
			Map<String, String> parsedAnnot, 
			ObjectConstuctionTreeModel<?> tree, 
			boolean checkDef) throws Exception {
		AbstractChooseNode an;
		if (param.isArray()) {
			an = new ArrayChooseNode(param, parsedAnnot, tree, checkDef);		
		} else if (ClassUtils.isBooleanType(param)) {
			an = new BooleanChooseNode(Boolean.class, parsedAnnot, tree);
		} else if (ClassUtils.isTypableType(param)) {
			an = new TypableChooseNode(param, parsedAnnot, tree);
		} else if (param.isEnum()){
			an = new EnumChooseNode(param, parsedAnnot, tree, checkDef);
		} else {
			an = new ClassChooseNode(param, parsedAnnot, tree, checkDef);
		}
		this.add(an);
		return an.isConfigured();
	}

	public Constructor<?> getConstructor() {
		return (Constructor<?>) this.getUserObject();
	}
	
	public int getCreatedValue() {
		return createdValue;
	}

	public String getName() {
		return super.parseName(getConstructor().getName());
	}
	
	public String getColor() {
		String color = "#660000";
		if (configured) {
			color = "#006600";
		}
		return color;
	}
	
	public String getText() {
		Constructor<?> c = getConstructor();
		String name = getName();
		String params = "(";
		Annotation[][] annotations = c.getParameterAnnotations();
		int i = 0;
		for (Class<?> param : c.getParameterTypes()) {
			String nameAnnotation = parseAnnotations(annotations[i]).get("ParamName.name()");
			params += (i == 0 ? "" : ", ") + parseName(param.getName())
			+ (nameAnnotation == null ? "" : nameAnnotation);
			++i;
		}
		params += ")";
		int createdValue = getCreatedValue();
		String value = createdValue > 0 ? ""+ createdValue + " : " : "";
		
		return value + name + params;
	}

	@Override
	public void actionPerformed(String key) 	{

		ObjectConstuctionTreeModel model = getContainingTreeModel();
		removeConstructedConstructors();
		try {
			model.removeNodeFromParent(this);
		}
		catch (Exception ex) {
			System.out.println("TOFIX3");				
		}			
		this.removeAllChildren();
		try {
			this.removeFromParent();
		}
		catch (Exception ex) {
			System.out.println("TOFIX" + this.getClass());
		}		
	//	instance.getContainingTree().setEditable(true);
		for (ConstructorNodeChooserPointer nodes : pointed) {
			nodes.actionPerformed(new ActionEvent(this, (int)System.currentTimeMillis(), "real_object"));
		}
		pointed.clear();
		getContainingTreeModel().reloadTree();
	}

	@Override
	public boolean checkConfiguredRecursive() {
		boolean newConfigured = true;
		
		for (AbstractChooseNode child : this.getChilds()) {
			boolean childConf = child.checkConfiguredRecursive();
			newConfigured = newConfigured && childConf;
		}
		if (getContainingTreeModel() != null) {
			if (newConfigured && !configured) {
				this.createdValue = getContainingTreeModel().addConfiguredConstructor(this);
			}
			if (!newConfigured && configured) {
				for (ConstructorNodeChooserPointer nodes : pointed) {
					nodes.actionPerformed(new ActionEvent(this, (int)System.currentTimeMillis(), "real_object"));
				}
				this.pointed.clear();
				this.getContainingTreeModel().removeConsrtuctor(this);
				this.createdValue = -1;
			}
		}
		this.setConfigured(newConfigured);
		return configured;
	}
	
	@Override
	public boolean checkConfigured() {
		boolean newConfigured = true;
		
		for (AbstractChooseNode child : this.getChilds()) {
			boolean childConf = child.checkConfigured();
			newConfigured = newConfigured && childConf;
		}
		return newConfigured;
	}

	public List<ActionItem> getActions() {
		List<ActionItem> l = new ArrayList<ActionItem>(1);
		String text = "Suppress Constructor";
		int nbPointed = this.pointed.size();
		text += nbPointed > 0 ? " + " + nbPointed + " other(s) pointing instances" : "";
		l.add(new ActionItem(text, "suppress"));
		return l;
	}

	protected Class<?> getConstructedClass() {
		return this.constructedClass;
	}

	@Override
	public Object getCurrentObject() {
		return this.currentIterator.currentIteratorObject;
	}

	@Override
	protected void removeConstructedConstructors(){
		this.getContainingTreeModel().removeConsrtuctor(this);
		if (this.getChildCount() > 0) {
			for (Object child : this.children) {
				((AbstractChooseNode)child).removeConstructedConstructors();
			}
		}
		for (ConstructorNodeChooserPointer nodes : pointed) {
			nodes.actionPerformed(new ActionEvent(this, (int)System.currentTimeMillis(), "real_object"));
		}
		pointed.clear();
		getContainingTreeModel().reloadTree();
	}
	

	@Override
	protected void resetIterators() {
		if (this.firstAcessed != null && this.firstAcessed) {
			this.firstAcessed = null;
		}
		if (this.getChildCount() > 0) {
			for (Object child : this.children) {
				((AbstractChooseNode)child).resetIterators();
			}
		}
	}

	@Override
	public int getInstancesCount() {
		int ret = 1;
		if (this.getChildCount() > 0) {
			for (Object o : this.children) {
				ret *= ((AbstractChooseNode)o).getInstancesCount();
			}
		}
		return ret;
	}

	private void writeObject(java.io.ObjectOutputStream out)throws IOException {
		out.writeObject(containingTreeModel);
		out.writeObject(constructedClass);
		out.writeObject(this.getConstructor().getDeclaringClass());
		out.writeObject(this.getConstructor().getParameterTypes());
		out.writeInt(createdValue);
		int nb_pointed = pointed.size();
		out.writeInt(nb_pointed);
		for (int i=0; i<nb_pointed; ++i) {
			out.writeObject(pointed.get(i));
		}
		int nb_children = this.getChildCount();
		out.writeInt(nb_children);
		for (int i=0; i< nb_children; ++i){
			out.writeObject(children.get(i));
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.containingTreeModel = (ObjectConstuctionTreeModel<?>)in.readObject();
		this.constructedClass = (Class<?>) in.readObject();
		Class<?> declaringClass = (Class<?>)in.readObject();
		Class<?>[] params = (Class<?>[])in.readObject();
		this.createdValue = in.readInt();
		int nb_pointed = in.readInt();
		pointed = new ArrayList<ConstructorNodeChooserPointer>(nb_pointed);
		for (int i=0; i<nb_pointed; ++i) {
			pointed.add((ConstructorNodeChooserPointer)in.readObject());
		}
		int nb_children = in.readInt();
		AbstractChooseNode[] childrens = new AbstractChooseNode[nb_children];
		for (int i=0; i<nb_children; ++i) {
			childrens[i] = (AbstractChooseNode)in.readObject();
		}
		try {
			this.setUserObject(declaringClass.getConstructor(params));
			for (int i=0; i<nb_children; ++i) {
				this.restoreChild(childrens[i]);
			}
			this.firstAcessed = null;
		} catch (NoSuchMethodException e) {
			logger.warn("Impossible to load a constructor of class " + this.constructedClass.getSimpleName());
			this.invalid = true;
			pointed.clear();
			this.loadFailed = true;
		}
	}

	@Override
	public Iterator<Pair<Object, ObjectRecipe>> iterator() {
		if (this.firstAcessed == null) {
			this.currentIterator = new SpecialIterator();
		}
		return this.currentIterator;
	}
	
	@Override
	public Object clone() {
		try {
			ConstructorChooseNode ret = new ConstructorChooseNode(this.getConstructor(), this.constructedClass, getContainingTreeModel(), null,  false); 
			for (ConstructorNodeChooserPointer p : this.pointed) {
				AbstractChooseNode.map.put(p, ret);
			}
			if (this.children != null) {
				ret.removeAllChildren();
				for (Object c : this.children) {
					AbstractChooseNode child = (AbstractChooseNode)c;
					if (!(child instanceof ConstructorNodeChooserPointer)) {
						AbstractChooseNode toAdd = (AbstractChooseNode)child.clone();
						ret.add(toAdd);
					} else {
						ConstructorNodeChooserPointer p = (ConstructorNodeChooserPointer)child;
						ConstructorNodeChooserPointer toAdd;
						if (AbstractChooseNode.map.containsKey(p)) {
							ConstructorChooseNode superInstance = AbstractChooseNode.map.get(p);
							toAdd = new ConstructorNodeChooserPointer(getContainingTreeModel(), superInstance);
						} else {
							toAdd = new ConstructorNodeChooserPointer(getContainingTreeModel(), p.getSuperInstance());
						}
						ret.add(toAdd);
					}
				}
			}
			return ret;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}

	class SpecialIterator implements Iterator<Pair<Object, ObjectRecipe>> {

		private Object[] currentParams;
		Object currentIteratorObject;
		private boolean first;
		boolean hasNext;
		private boolean delivered;
		private int paramSize;
		private List<Iterator<Pair<Object, ObjectRecipe>>> iterators;
		private Constructor localCons;
		Pair<Object, ObjectRecipe> pair;

		public SpecialIterator() {
			if (firstAcessed == null) {
				firstAcessed = true;
			}
			this.currentIteratorObject = null;
			this.first = true;
			this.hasNext = true;
			this.delivered = false;
			this.paramSize = (getChildCount() > 0 ? children.size() : 0) ;
			this.iterators =  new ArrayList<Iterator<Pair<Object, ObjectRecipe>>>();
			this.localCons = getConstructor();
			this.currentParams = new Object[this.paramSize];
			if (this.paramSize > 0) {
				int i = 0;
				for (Object param : children) {
					if (((AbstractParameterChooseNode) param).configured) {
						Iterator<Pair<Object, ObjectRecipe>> iteratorForParamI = ((AbstractChooseNode) param).iterator();
						iterators.add(iteratorForParamI);
						if (iteratorForParamI.hasNext()){
							this.currentParams[i] = iteratorForParamI.next().getFirst();
						} else {
							throw new IllegalStateException("Recently created iterator has no element to provide at all");
						}
						++i;
					}
				}
			}
		}

		@Override
		public boolean hasNext() {
			if (firstAcessed == null || !firstAcessed){
				return !this.delivered;
			} else {
				return this.hasNext;
			}
		}

		@Override
		public Pair<Object, ObjectRecipe> next() {
			if (firstAcessed != null && firstAcessed){
				return findNext();
			} else {
				this.delivered = true;
				return this.pair;
			}
		}

		Pair<Object, ObjectRecipe> findNext() {
			/*if (currentParams[0].getClass().getSimpleName().equals("SimplePowerRequiredModel")) {
				int stop = 0;
				System.out.println("iewru");
			}*/
			boolean changed = this.first;
			int i = 0;
			while (!changed) {
				Iterator<Pair<Object, ObjectRecipe>> it = this.iterators.get(i);
				if (it.hasNext()) {
					Pair<Object, ObjectRecipe> pair = it.next();
					this.currentParams[i] = pair.getFirst();
					changed = true;
				} else {
					((AbstractChooseNode)children.get(i)).resetIterators();
					this.iterators.set(i, ((AbstractChooseNode)children.get(i)).iterator());
					
					Pair<Object, ObjectRecipe> pair = this.iterators.get(i).next();
					this.currentParams[i] = pair.getFirst();	
					++i;
				}
			}
			this.checkNext();
			this.first = false;
			try {
				for (int h = 0 ; h < this.currentParams.length ; h++) {
					if (currentParams[h] instanceof WrongExperimentException) {
						return new Pair<Object, ObjectRecipe>(currentParams[h], null);
					}
				}
				this.currentIteratorObject = localCons.newInstance(this.currentParams);
				@SuppressWarnings("unchecked")
				ObjectRecipe<?> recipe = new ObjectRecipe(localCons, this.currentParams);
				pair = new Pair<Object, ObjectRecipe>(this.currentIteratorObject, recipe);
				return pair;
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof WrongExperimentException) {
					System.out.print("next");
					return new Pair<Object, ObjectRecipe>(e.getCause(), null);
				} else {
					throw new IllegalStateException("Potentiall trying to run a constructor not public or erroneous", e.getCause());
				}
			} catch (Exception e) {
				System.err.println(ConstructorChooseNode.this.toLongString());
				throw new IllegalStateException("Potentially trying to run a constructor not public, or erroneous", e);
			}
		}

		private void checkNext() {
			this.hasNext = false;
			int i = 0;
			while (!this.hasNext && i < this.paramSize) {
				this.hasNext = this.hasNext || this.iterators.get(i).hasNext();
				++i;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public void cleanUp() {
		currentIterator = null;
		for (AbstractChooseNode child : this.getChilds()) {
			child.cleanUp();
		}
	}
}
