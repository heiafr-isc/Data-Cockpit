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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.heiafr.isc.datacockpit.tree.clazzes.ObjectRecipe;
import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;

public class UntypableArrayInstanceNode extends ClassChooseNode {


	private static final long serialVersionUID = 1L;
	private final Class<?> arrayClass;
	
	public UntypableArrayInstanceNode(
			Class<?> c,
			ObjectConstructionTreeModel<?> containingTree,
			Map<String, String> annotationMap,
			boolean checkDef) {
		super(c, annotationMap, containingTree, checkDef);
		arrayClass = c;
		checkConfigured();
	}

	@Override
	public List<ActionItem> getActions() {
		List<ActionItem> toReturn = new ArrayList<>();
		try {
			this.menuItems = new HashMap<>();
			if (arrayClass.equals(Class.class)) {
				String clastype = annotationMap.get("ParamName.abstractClass");
				buildClassMenu(toReturn, clastype);
			} else if (arrayClass.isEnum()) {
				buildEnumSubMenu(toReturn, arrayClass);
			} else if (isSuperClass) {
				buildSuperClassSubMenu(toReturn);
			} else {
				buildListOfThisClassConstructorsSubMenu(toReturn);
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
		toReturn.add(new SeparatorItem());
		toReturn.add(new ActionItem("Suppress this array", "suppress"));	
		toReturn.add(new ActionItem("Empty this array", REMOVE_ALL));
		return toReturn;			
	}
	
	@Override
	public boolean checkConfiguredRecursive() {
		boolean configured = true;
		for (AbstractChooseNode child : this.getChilds()) {
			configured = configured && (child.checkConfiguredRecursive());
		}
		this.setConfigured(configured);
		return configured;
	}	
	
	@Override
	public void actionPerformed(String key) {
		String[] keyParse = key.split("___");		
		Object c = this.menuItems.get(keyParse[0]);
		AbstractChooseNode newNode = null;
		try {
			if (key.equals("suppress")) {
				((AbstractChooseNode)this.getParent()).removeChild(this);
			} else if (parameterType.equals("java.lang.Class")) {
				Class<?> idClass = Class.forName(c.toString());
				newNode = new LeafChooseNode(idClass, getContainingTreeModel());
			}  else if (c instanceof Constructor<?>) {
				Constructor<?> cons = (Constructor<?>)c;
				newNode = new ConstructorChooseNode(cons, Class.forName(parameterType), this.getContainingTreeModel(), null, false);
			} else if (key.equals(REMOVE_ALL)) {
				this.removeAllChildren();
			} else if (key.equals("NULL")) {
				newNode = new LeafChooseNode(null, getContainingTreeModel());
			} else if (c != null) {
				newNode = new LeafChooseNode(c, getContainingTreeModel());
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);			
		}
		if (newNode != null) {
			this.add(newNode);
		}
		containingTreeModel.reloadTree();
	}

	@Override
	public int getInstancesCount() {
		return 1;
	}

	private int getNumberOfChildInstances() {
		int childInstances = 0;
		for (AbstractChooseNode child : this.getChilds()) {
			childInstances += child.getInstancesCount();
		}
		return childInstances;
	}

	@Override
	public String getText() {

		
		return "Flat array of type " + ((Class<?>)getUserObject()).getSimpleName() + " with " + getNumberOfChildInstances() + " elements";
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof ArrayChooseNode) {
			ArrayChooseNode alt = (ArrayChooseNode)anObject;
			return this.arrayEquals(alt.parameterType);
		} else {
			return false;
		}
	}
	
	private boolean arrayEquals(Object anObject) {
		char class1 = parameterType.charAt(this.getUserObject().getClass().getName().lastIndexOf('[')+1);
		char class2 = anObject.getClass().getName().charAt(anObject.getClass().getName().lastIndexOf('[')+1);
		if (class1 == class2) {
			boolean ret;
			switch(class1) {
			case 'I':
				ret = Arrays.equals((int[])this.getUserObject(),(int[])anObject);
				break;
			case 'J':
				ret = Arrays.equals((long[])this.getUserObject(),(long[])anObject);
				break;
			case 'F':
				ret = Arrays.equals((float[])this.getUserObject(),(float[])anObject);
				break;
			case 'D':
				ret = Arrays.equals((double[])this.getUserObject(),(double[])anObject);
				break;
			case 'C':
				ret = Arrays.equals((char[])this.getUserObject(),(char[])anObject);
				break;
			case 'Z':
				ret = Arrays.equals((boolean[])this.getUserObject(),(boolean[])anObject);
				break;
			case 'L':
				ret = Arrays.equals((Object[])this.getUserObject(),(Object[])anObject);
				break;
			default :
				throw new UnsupportedOperationException();
			}
			return ret;
		} else {
			return false;
		}
	}	

	@Override
	public String getColor() {
		return "#00DD00";
	}

	@Override
	public void cleanUp() {}
	
	@Override
	public Iterator<Pair<Object, ObjectRecipe<?>>> iterator() {
		return new Iterator<Pair<Object, ObjectRecipe<?>>>() {

			private boolean delivered = false;

			@Override
			public boolean hasNext() {
				return !delivered;
			}

			@Override
			public Pair<Object, ObjectRecipe<?>> next() {
				this.delivered = true;
				return new Pair<>(createArray(), null);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	protected Object createArray() {
		Object newArray = Array.newInstance(arrayClass, getNumberOfChildInstances());
		int index = 0;
		for (AbstractChooseNode child : this.getChilds()) {
			for (Pair<Object, ObjectRecipe<?>> pair : child) {
				Array.set(newArray, index, pair.getFirst());
				index++;
			}
		}
		return newArray;
	}

	@Override
	public Object getCurrentObject() {
		throw new IllegalStateException("Potentially problematic");
	}

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject,
			Map<String, String> annotationMap2,
			ObjectConstructionTreeModel<?> containingTreeModel,
			boolean b) {
		return new UntypableArrayInstanceNode(userObject, containingTreeModel, annotationMap2, b);
	}	
		
	
}
