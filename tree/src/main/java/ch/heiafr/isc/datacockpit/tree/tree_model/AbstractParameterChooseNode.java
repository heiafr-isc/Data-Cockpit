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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.tree.TreeNode;

import ch.heiafr.isc.datacockpit.tree.clazzes.ObjectRecipe;
import ch.heiafr.isc.datacockpit.tree.clazzes.ClassUtils;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;


public abstract class AbstractParameterChooseNode extends AbstractChooseNode {
	
	private static final long serialVersionUID = 1L;

	public static final String ADD = "Add";
	protected static final String REMOVE_ALL = "remove___all";	

	protected Map<String, String> annotationMap;
	protected String parameterType;
	protected transient HashMap<String, Object> menuItems;
	
	// this field is used to obtain the object corresponding to the current iterator state
	protected transient AbstractChooseNode currentIteratorChild;
	
	protected abstract AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b) throws Exception;
	
	public AbstractParameterChooseNode(
			Class<?> c,
			ObjectConstuctionTreeModel<?> containingTree, 
			Map<String, String> annotationMap) {
		super(containingTree);
		this.setUserObject(c);
		parameterType = c.getName();		
		this.annotationMap = annotationMap;
	}
	
	public boolean isTypableType() {
		return ClassUtils.isTypableType(parameterType);
	}

	public String getText() {
		String annotations = annotationMap.get("ParamName.name");
		String text = "";
		if (annotations == null) {
			text = parameterType;
		} else {
			text = annotations;
		}
		return text;
	}	
	
	protected void buildEnumSubMenu(List<ActionItem> toReturn, Class<?> usedClass) {
		Object[] enumeration = usedClass.getEnumConstants();
		for (Object e : enumeration) {
			String enumKey = e.toString() + e.hashCode();
			ActionItem enumItem = new ActionItem(e.toString(), enumKey);
			this.menuItems.put(enumKey, e);
			toReturn.add(enumItem);
		}		
	}
	
	protected void buildClassMenu(List<ActionItem> toReturn, String clastype) {
		try {
			List<Class<?>> subClasses = getContainingTreeModel().getHeritedClasses(Class.forName(clastype));
			for (Class c : subClasses) {
				if (!Modifier.isAbstract(c.getModifiers())) {
					String className = c.getName();
					ActionItem classItem = new ActionItem(className, className);
					toReturn.add(classItem);
					this.menuItems.put(className, className);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Cannot find class " + clastype);
		}		
	}
	
	@Override
	public int getInstancesCount() {
		int ret = 0;
		if (this.getChildCount() > 0) {
			for (Object o : this.children) {
				ret += ((AbstractChooseNode) o).getInstancesCount();
			}
		}
		return ret;
	}
	
	@Override
	public List<ActionItem> getActions() {
		List<ActionItem> toReturn = new ArrayList<ActionItem>();
		toReturn.add(new ActionItem(REMOVE_ALL, REMOVE_ALL));
		return toReturn;
	}
	
	@Override
	public boolean checkConfiguredRecursive() {
		boolean configured = true;
		if (this.getChildCount() > 0) {
			for (AbstractChooseNode child : this.getChilds()) {
				configured = configured && (child.checkConfiguredRecursive());
			}
		} else {
			configured = false;
		}
		this.setConfigured(configured);
		return configured;
	}
	
	@Override
	public boolean checkConfigured() {
		boolean configured = true;
		if (this.getChildCount() > 0) {
			for (AbstractChooseNode child : this.getChilds()) {
				configured = configured && (child.checkConfigured());
			}
		} else {
			configured = false;
		}
		this.setConfigured(configured);		
		return configured;		
	}

	@Override
	public String getColor() {
		String color = "#AA0000";
		if (configured) {
			color = "#00AA00";
		}
		return color;
	}
	
	@Override
	public Object getCurrentObject() {
		return this.currentIteratorChild.getCurrentObject();
	}	
	
	@Override
	public void cleanUp() {
		currentIteratorChild = null;
		for (AbstractChooseNode child : this.getChilds()) {
			child.cleanUp();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public DefinitionIterator getObjectDefinitionIterator() {
		return new DefinitionIterator() {
			
			Iterator childsConstructors;
			ConstructorChooseNode currentConstructor;
			DefinitionIterator[] subIterators;
			ObjectDefinition current = null;
			ObjectDefinition next = null;
			boolean constructorsExhausted = false;
			boolean iteratorExhausted = false;
			
			{
				reset();
			}
			
			@Override
			void reset() {
				// TODO Auto-generated method stub
				iteratorExhausted = false;				
				childsConstructors = children.iterator();
				constructorsExhausted = startWithNextConstructor();
				current = getNext();
				next = getNext();
			}			
			
			private boolean startWithNextConstructor() {
				AbstractChooseNode abstractNode = (AbstractChooseNode) (childsConstructors.next());
				if (abstractNode instanceof ConstructorChooseNode) {
					currentConstructor = (ConstructorChooseNode)abstractNode;
					ArrayList<TreeNode> chNodeList = (ArrayList<TreeNode>)(Collections.list(currentConstructor.children()));
					subIterators = new DefinitionIterator[chNodeList.size()];
					int index = 0;
					for (TreeNode node : chNodeList) {
						subIterators[index] = ((ClassChooseNode)node).getObjectDefinitionIterator();
						index++;
					}
				} else {
					subIterators = new DefinitionIterator[1];
					subIterators[0] = ((LeafChooseNode)abstractNode).getDefinitionIterator();
				}
				boolean b = !childsConstructors.hasNext();
				return b;
			}
			
			private ObjectDefinition getNext() {
				ObjectDefinition futureNext;
				if (iteratorExhausted) {
					return null;
				}
				if (currentConstructor != null) {
					futureNext = new ObjectDefinition(((Class<?>)getUserObject()).getCanonicalName(), ((Constructor<?>)currentConstructor.getUserObject()).getDeclaringClass().getName());
				} else {
					futureNext = new ObjectDefinition(((Class<?>)getUserObject()).getCanonicalName());
				}
				boolean advanced = false;				
				if (subIterators.length > 0) {
					for (DefinitionIterator subIt : subIterators) {
						futureNext.addDefinition(subIt.current());
					}
					// advance iterators
					boolean loop = true;
					int i = 0;
					while (loop) {
						if (subIterators[i].hasFutureNext()) {
							subIterators[i].next();							
							advanced = true;
							loop = false;
						} else {
							if (subIterators.length > i+1) {
								subIterators[i].reset();
								i++;
							} else {
								loop = false;
							}
						}
					}
				}
				if (advanced == false) {
					if (constructorsExhausted == false) {
						constructorsExhausted = startWithNextConstructor();
					} else {
						iteratorExhausted = true;
					}
				}
				return futureNext;
			}
			
			@Override
			AbstractDefinition current() {
				return current;
			}			

			@Override
			public boolean hasNext() {
				return (current != null);
			}
			
			public boolean hasFutureNext() {
				return (next != null);				
			}

			@Override
			public AbstractDefinition next() {
				AbstractDefinition toRet = current;
				current = next;
				next = getNext();
				return toRet;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();				
			}
		};
	}
	
	@Override
	public Iterator<Pair<Object, ObjectRecipe>> iterator() {
		@SuppressWarnings("unchecked")
		Iterator<Pair<Object, ObjectRecipe>> ret = new Iterator<Pair<Object, ObjectRecipe>>() {


			Iterator<TreeNode> childs;
			Iterator<Pair<Object, ObjectRecipe>> presentChildIterator;
			
			{
			
				childs = children.iterator();
				
				if (childs.hasNext()) {
					currentIteratorChild = (AbstractChooseNode) (childs.next());
					currentIteratorChild.resetIterators();
					presentChildIterator = currentIteratorChild.iterator();
				}
			}

			@Override
			public boolean hasNext() {
				return hasChildrenNext();
			}

			@Override
			public Pair<Object, ObjectRecipe> next() {
				if (hasChildrenNext()) {
			
					if ((AbstractParameterChooseNode.this instanceof ArrayChooseNode) && (currentIteratorChild instanceof ConstructorChooseNode)) {
						List<Pair<Object, ObjectRecipe>> instances = new ArrayList<Pair<Object, ObjectRecipe>>();
						while (presentChildIterator.hasNext()) {
							instances.add(presentChildIterator.next());
						}
						while (this.childs.hasNext()) {
							currentIteratorChild = (AbstractChooseNode) (this.childs.next());
							currentIteratorChild.resetIterators();
							this.presentChildIterator = currentIteratorChild.iterator();
							while (presentChildIterator.hasNext()) {
								instances.add(presentChildIterator.next());
							}
						}
						Class<?> arrayClass = ((ConstructorChooseNode) currentIteratorChild).getConstructedClass();
						Object array = Array.newInstance(arrayClass, instances.size());
						for (int i = 0; i < instances.size(); ++i) {
							Array.set(array, i, instances.get(i).getFirst());
						}
						return new Pair<Object, ObjectRecipe>(array, null);
					} else {
						return presentChildIterator.next();
					}
				} else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private boolean hasChildrenNext() {
				if (presentChildIterator == null) {
					return false;
				} else if (presentChildIterator.hasNext()) {
					return true;
				} else {
					while (childs.hasNext()) {
						currentIteratorChild = (AbstractChooseNode) (childs.next());
						currentIteratorChild.resetIterators();
						presentChildIterator = currentIteratorChild.iterator();
						if (presentChildIterator.hasNext()) {
							return true;
						}
					}
				}
				return false;
			}
		};
		return ret;
	}
	
	/**
	 * Used to clone the tree prior to launch experiment list. This prevents hick-ups in the iteration
	 */
	public Object clone() {
		try {		
			AbstractParameterChooseNode ret = paremeterChooseNodeClone((Class<?>)this.getUserObject(), annotationMap, this.getContainingTreeModel(), false);
			ret.removeAllChildren();
			if (this.children != null) {
				for (Object c : this.children) {
					AbstractChooseNode node = (AbstractChooseNode)c;
					if (!(node instanceof ConstructorNodeChooserPointer)) {
						ret.add((AbstractChooseNode)node.clone());
					} else {
						ConstructorNodeChooserPointer p = (ConstructorNodeChooserPointer)node;
						ConstructorNodeChooserPointer toAdd;
						if (AbstractChooseNode.map.containsKey(p)) {
							ConstructorChooseNode superInstance = AbstractChooseNode.map.get(p);
							toAdd = new ConstructorNodeChooserPointer(this.getContainingTreeModel(), superInstance);
						} else {
							toAdd = new ConstructorNodeChooserPointer(this.getContainingTreeModel(), p.getSuperInstance());
						}
						toAdd.setCartesianEnabled(((ConstructorNodeChooserPointer)node).isCartesianEnabled());
						ret.add(toAdd);
					}
				}
			}
			ret.checkConfiguredRecursive();
			return ret;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
