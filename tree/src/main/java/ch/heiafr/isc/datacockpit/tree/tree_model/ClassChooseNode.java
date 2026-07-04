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

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;


public class ClassChooseNode extends AbstractParameterChooseNode implements Serializable {
	
	private transient final static Logger logger = new Logger(ClassChooseNode.class);

	private static final String DEFAULT_SUFFIX = "___DEFAULT";
	private static final String VOID_SUFFIX = "___VOID";
	private static final String POINTER_SUFFIX = "___POINT";
	private static final String COPY_SUFFIX = "___COPY";

	private static final long serialVersionUID = 7474781535087232848L;
	
	protected transient boolean isSuperClass = false;
	
	protected ClassChooseNode(Class<?> c, Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree, boolean checkDef)
					throws Exception {
		super(c, containingTree, annotationMap);

		if (parameterType.startsWith("[")) {
			throw new IllegalStateException("ClassChooseNode should never be used for arrays");
		}

		if (parameterType.equals("java.lang.Class")) {
			// do nothing
		} else {
			List<Class<?>> subClasses = getHeritedClassesImplementingRequiredInterface();
			if (subClasses.size() > 1) {
				String clas = annotationMap.get("ParamName.defaultClass_");
				isSuperClass = true;
				if (checkDef && clas != null) {
					// Find subclass
					Class<?> matchSubCLass = null;
					Iterator<Class<?>> it = subClasses.iterator();
					while (matchSubCLass == null && it.hasNext()) {
						Class<?> nextClasse = it.next();
						if (nextClasse.getCanonicalName() == null) {
							System.out.println("warning null default class");
						} else {
							if (nextClasse.getCanonicalName().equals(clas)) {
								matchSubCLass = nextClasse;
							}
						}
					}
					if (matchSubCLass != null) {
						this.checkDefault(matchSubCLass, annotationMap);
					} else {
						System.out.println("Matching default subclass not found : " + clas);
					}
				} else {
					checkIfNull(annotationMap);
				}
			} else if (subClasses.size() == 1) { // only one class found
				// if one class remains and the model is abstract, then it is super
				if (Modifier.isAbstract(c.getModifiers())) isSuperClass = true;
				if (checkDef) {
					this.checkDefault(subClasses.get(0), annotationMap);
				}
			} else {
				// if the only clas found is the signature interface, we have a problem
				ErrorChooseNode error = new ErrorChooseNode("Cannot find any corresponding class", getContainingTreeModel());
				add(error);
			}
		}
		checkConfigured();
	}
	
	
	
	private List<Class<?>> getHeritedClassesImplementingRequiredInterface() throws ClassNotFoundException, Exception {
		List<Class<?>> subClasses = getContainingTreeModel().getHeritedClasses(Class.forName(parameterType));
		for (Iterator<Class<?>> ite = subClasses.iterator() ; ite.hasNext() ; ) {
			Class<?> candidate = ite.next();
			if (Modifier.isAbstract(candidate.getModifiers())) {
				ite.remove();
			}
		}		
		if (annotationMap.get("ParamName.requireInterface") != null) {
			Class<?> extraInterface = Class.forName(annotationMap.get("ParamName.requireInterface"));
			// pruning classes not fulfillin extra interface requirement
			for (Iterator<Class<?>> ite = subClasses.iterator() ; ite.hasNext() ; ) {
				Class<?> candidate = ite.next();
				if (!extraInterface.isAssignableFrom(candidate/*Integer.class*/)) {
					ite.remove();
				}
			}	
		}
		return subClasses;
	}



	private void checkIfNull(Map<String, String> annotationMap) {
		if (annotationMap.get("ParamName.default_") != null) {
			if (annotationMap.get("ParamName.default_").equals("null")) {
				LeafChooseNode newNode = new LeafChooseNode(null, getContainingTreeModel());
				add(newNode);
			}
		}
	}

	private void checkEnum(Class<?> c, String def) {
		for (Object o : c.getEnumConstants()) {
			if (o.toString().equals(def)) {
				LeafChooseNode newNode = new LeafChooseNode(o, getContainingTreeModel());
				newNode.setExpanded(false);
				add(newNode);
				break;
			}
		}
	}
	
	private boolean checkDefault(Class<?> c, Map<String, String> annotationMap) throws Exception {
		boolean found = false;
		String def = annotationMap.get("ParamName.default_");
		String clas = annotationMap.get("ParamName.defaultClass_");
		if (c.isEnum() && def != null) {
			this.checkEnum(c, def);
		} else if (def != null && clas != null) {
			found = this.checkWithDefaultParam(c, def);
			if (!found) found = this.checkNoParamMethod(c);
			if (!found) found = this.checkAllDefaultParams(c);
		} else {
			found = this.checkNoParamMethod(c);
			if (!found && def != null) {
				found = this.checkWithDefaultParam(c, def);
			} else if (!found) {
				found = this.checkAllDefaultParams(c);
			}
			if (!found) {
				found = this.checkOnlyOneConstructor(c);
			}
		}
		return found;
	}
	
	private boolean checkNoParamMethod(Class<?> c) throws Exception {
		try {
			Constructor<?> empty = c.getConstructor(new Class<?>[0]);
			ConstructorChooseNode newNode = new ConstructorChooseNode(empty, Class.forName(parameterType), getContainingTreeModel(), null, true);
			newNode.setExpanded(false);
			this.add(newNode);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}
	
	private boolean checkWithDefaultParam(Class<?> c, String def) throws Exception{
		Constructor<?> cons = null;
		String defVal = null;
		try {
			// Look for int
			int value = Integer.parseInt(def);
			Class<?>[] paramTypes = { int.class };
			cons = c.getConstructor(paramTypes);
			defVal = "" + value;
		} catch (Exception ex) {
			try {
				// Look for double
				double value = Double.parseDouble(def);
				Class<?>[] paramTypes = { double.class };
				cons = c.getConstructor(paramTypes);
				defVal = "" + value;
			}
			catch (Exception exx) {
				try {
					// Look for long
					double value = Long.parseLong(def);
					Class<?>[] paramTypes = { long.class };
					cons = c.getConstructor(paramTypes);
					defVal = "" + value;
				} catch (Exception exxx) {
					try {
						// Look for String
						Class<?>[] paramTypes = { String.class };
						cons = c.getConstructor(paramTypes);
						defVal = def;
					} catch (Exception exxxx) {
						// No specific constructor found
					}
				}
			}
		}
		if (cons != null && defVal != null) {
			ConstructorChooseNode newNode = new ConstructorChooseNode(cons, c, this.getContainingTreeModel(), defVal, true);
			newNode.setExpanded(false);
			this.add(newNode);
			return true;
		} else return false;
	}
	
	private boolean checkAllDefaultParams(Class<?> c) throws Exception{
		Constructor<?> defaultConstructor = null;
		Constructor[] constrArray = c.getConstructors();
		// looking for a constructor annotated as default
		for (int i = 0 ; i < constrArray.length ; i++) {
			Constructor<?> cons = constrArray[i];
			ConstructorDef a = cons.getAnnotation(ConstructorDef.class);
			if (a !=null && a.default_()) {
				defaultConstructor = constrArray[i];
				break;
			}
		}
		int i = 0;		
		while (defaultConstructor == null
				&& i < constrArray.length) {
			Constructor<?> cons = constrArray[i];
			boolean all = true;
			
			// check if default for all parameters
			for (Annotation[] annot : cons .getParameterAnnotations()) {
				 Map<String, String> annotations = parseAnnotations(annot);
				 boolean isDefault = annotations.get("ParamName.default_") != null;
				 boolean isDefaultClass = annotations.get("ParamName.defaultClass_") != null;
				 all &= (isDefault || isDefaultClass);
			}
			if (all) {
				defaultConstructor = cons;
			}
			++i;
		}
		if (defaultConstructor != null) {
			ConstructorChooseNode newCN = new ConstructorChooseNode(defaultConstructor, c, getContainingTreeModel(), null, true);
			this.add(newCN);
			newCN.setExpanded(false);
			return true;
		} else return false;
		
	}
	
	private boolean checkOnlyOneConstructor(Class<?> c) throws Exception {
		Constructor<?>[] cons = c.getConstructors();
		if (cons.length == 1) {
			ConstructorChooseNode newCN = new ConstructorChooseNode(cons[0], c, getContainingTreeModel(), null, true);
			this.add(newCN);
		//	newCN.setExpanded(false);
			return true;
		} else return false;
	}

	@Override
	public void actionPerformed(String key) 	{
		if (key == null) return;
		try {				
			boolean checkDef;
			if (key.endsWith(VOID_SUFFIX)) {
				checkDef = false;
			} else {
				checkDef = true;
			}
			String[] keyParse = key.split("___");		
			this.setConfigured(false);
			Object c = this.menuItems.get(keyParse[0]);
			AbstractChooseNode newNode = null;
			if (parameterType.equals("java.lang.Class")) {
				Class idClass = Class.forName(c.toString());
				newNode = new LeafChooseNode(idClass, getContainingTreeModel());
			} else if (key.equals(REMOVE_ALL)) {
				this.removeAllChildren();
			} else if (key.equals("NULL")) {
				newNode = new LeafChooseNode(null, getContainingTreeModel());
			} else if (c instanceof Constructor<?>) {
				Constructor<?> cons = (Constructor<?>)c;
				newNode = new ConstructorChooseNode(cons, Class.forName(parameterType), this.getContainingTreeModel(), null, checkDef);
			} else if (c instanceof ConstructorChooseNode) {
				if (("___" + keyParse[1]).equals(COPY_SUFFIX)) {
					newNode = (AbstractChooseNode)((ConstructorChooseNode) c).clone();
					AbstractChooseNode.map.clear();
				} else {
					newNode = new ConstructorNodeChooserPointer(getContainingTreeModel(), (ConstructorChooseNode) c);
				}
			} else if (c instanceof Object) {
				newNode = new LeafChooseNode(c, getContainingTreeModel());
			}
			if (newNode != null) {
				this.add(newNode);
			}
			getContainingTreeModel().reloadTree();
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	private ActionStructure getConstructorStructure(Constructor<?> cons) {
		String constructorKey = cons.toString() + cons.hashCode();
		this.menuItems.put(constructorKey,cons);
		
		ActionStructure newConstructor = new ActionStructure(
				createConstructorString(cons), constructorKey + DEFAULT_SUFFIX);
		ActionItem voi = new ActionItem("void", constructorKey + VOID_SUFFIX);
		ActionItem def = new ActionItem("default", constructorKey + DEFAULT_SUFFIX);
		newConstructor.addItem(voi);
		newConstructor.addItem(def);
		return newConstructor;
	}
	
	protected void buildSuperClassSubMenu(List<ActionItem> toReturn) throws ClassNotFoundException, Exception {
		Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
//		Class<?> type = Class.forName(parameterType);
		List<Class<?>> clasList = getHeritedClassesImplementingRequiredInterface();
		for (Class<?> cl : clasList) {
			classes.put(cl.getName(), cl);
		}
		List<String> classesNames = new ArrayList<String>(classes.keySet());
		Collections.sort(classesNames);
		for (String className : classesNames) {
			Class<?> c = classes.get(className);
			ActionStructure newItem = new ActionStructure(c.getName(), null);

			Map<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();
			for (Constructor<?> cons : c.getConstructors()) {
				boolean ignore = false;
				for (Annotation a :cons.getAnnotations()) {
					if (a instanceof ConstructorDef) {
						ignore = ((ConstructorDef) a).ignore();
					}
				}
				if (!ignore) {
					String key = cons.getName();
					while (constructors.containsKey(key)) {
						key += "_";
					}
					constructors.put(key, cons);
				}
			}

			if (constructors.size() == 0) {
				logger.warn("No public constructor available for class " + c.getName());
				continue;
			}
			List<String> constructorNames = new ArrayList<String>(constructors.keySet());
			Collections.sort(constructorNames);
			for (String consName : constructorNames) {
				Constructor<?> cons = constructors.get(consName);
				ActionItem item = getConstructorStructure(cons);
				newItem.addItem(item);

				// if there is only one constructor, assign its key to the class item
				if (c.getConstructors().length == 1) {
					newItem.actionName = item.actionName;
				}
			}
			toReturn.add(newItem);
		}		
	}
	
	protected void buildListOfThisClassConstructorsSubMenu(List<ActionItem> toReturn) throws ClassNotFoundException {
		Class<?> thisClass = Class.forName(parameterType);
		if (Modifier.isAbstract(thisClass.getModifiers())) return; // 
		if (!parameterType.equals("java.lang.String")) {
			Map<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();
			for (Constructor<?> cons : thisClass.getConstructors()) {
				String key = cons.getName();
				while (constructors.containsKey(key)) {
					key += "_";
				}
				constructors.put(key, cons);
			}
			List<String> constructorNames = new ArrayList<String>(constructors.keySet());
			Collections.sort(constructorNames);
			for (String consName : constructorNames) {
				Constructor<?> c = constructors.get(consName);
				toReturn.add(getConstructorStructure(c));
			}
		}
	}
	
	@Override
	public List<ActionItem> getActions() {
		List<ActionItem> toReturn = new ArrayList<ActionItem>();
		this.menuItems = new HashMap<String, Object>();
		try {
			if (this.isSuperClass) {
				buildSuperClassSubMenu(toReturn);
			} else if (((Class<?>) this.getUserObject()).isEnum()) {
				buildEnumSubMenu(toReturn, (Class<?>) this.userObject);
			} else if (parameterType.equals("java.lang.Class")) {
				String clastype = annotationMap.get("ParamName.abstractClass");
				buildClassMenu(toReturn, clastype);
			} else {
				buildListOfThisClassConstructorsSubMenu(toReturn);
			}
			toReturn.add(new SeparatorItem());
			boolean separated = true;
			Map<ConstructorChooseNode, Integer> constructorMap = this.getContainingTreeModel().getConfiguredConstructors();
			for (Entry<ConstructorChooseNode, Integer> o : constructorMap.entrySet()) {
				if (((Class<?>) this.getUserObject()).equals(o.getKey().getConstructedClass())) {
					String pointerKey = o.getKey().toString();
					Constructor construct = o.getKey().getConstructor();					
					String pointerText = o.getValue()+ " : " + construct.getDeclaringClass().getSimpleName();
					


					ActionStructure point = new ActionStructure(pointerText, pointerKey + COPY_SUFFIX);
					ActionItem pointt = new ActionItem("pointer", pointerKey + POINTER_SUFFIX);
					ActionItem copy = new ActionItem("copy", pointerKey + COPY_SUFFIX);
					this.menuItems.put(pointerKey, o.getKey());
					point.addItem(pointt);
					point.addItem(copy);
					toReturn.add(point);
					separated = false;
				}
			}
			if (!separated) {
				toReturn.add(new SeparatorItem());
			}
			if (!(parameterType.equals("int") || parameterType.equals("float") || parameterType.equals("long")
					|| parameterType.equals("double") || parameterType.equals("char") || parameterType.equals("short")
					|| parameterType.equals("boolean") || parameterType.equals("java.lang.String") || parameterType
					.equals("java.lang.Class"))) {
				toReturn.add(new NullItem());
			} else {
				throw new IllegalStateException("should never be there");
			}
			toReturn.add(new ActionItem(REMOVE_ALL, REMOVE_ALL));
			return toReturn;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}



	private String createConstructorString(Constructor<?> c) {
		String constructorAnnotation = parseAnnotations(c.getAnnotations()).get("Constructor_def.def");
		if (constructorAnnotation == null || constructorAnnotation.equals("")) {
			String name = parseName(parseName(c.getName()));
			String params = "(";
			Annotation[][] annotations = c.getParameterAnnotations();
			int i = 0;
			for (Class<?> param : c.getParameterTypes()) {
				String paramName = param.getName();
				if (paramName.startsWith("[")) {
					if (paramName.endsWith(";")) {
						paramName = paramName.substring(0,
								paramName.length() - 1) + "[]";
					} else if (paramName.length() == 2) {
						switch (paramName.charAt(1)) {
						case 'I':
							paramName = "int[]";
							break;
						case 'J':
							paramName = "long[]";
							break;
						case 'F':
							paramName = "float[]";
							break;
						case 'D':
							paramName = "double[]";
							break;
						case 'C':
							paramName = "char[]";
							break;
						case 'Z':
							paramName = "boolean[]";
							break;
						}
					}
				}
				String annotation = parseAnnotations(annotations[i]).get("ParamName.name");
				params += (i == 0 ? "" : ", ") + (annotation == null ? parseName(paramName) : annotation);
				++i;
			}
			params += ")";
			return name + params;
		} else {
			return constructorAnnotation;
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(this.containingTreeModel);
		out.writeObject((Class<?>) this.getUserObject());
		out.writeBoolean(isSuperClass);
		out.writeObject(annotationMap);
		out.writeUTF(parameterType);
		int nb = this.getChildCount();
		out.writeInt(nb);
		for (int i = 0; i < nb; ++i) {
			out.writeObject((AbstractChooseNode) children.get(i));
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		this.containingTreeModel = (ObjectConstuctionTreeModel<?>)in.readObject();
		this.setUserObject((Class<?>) in.readObject());
		isSuperClass = in.readBoolean();
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>)in.readObject();
		annotationMap = map;
		parameterType = in.readUTF();
		menuItems = new HashMap<String, Object>();
		int nb = in.readInt();
		for (int i = 0; i < nb; ++i) {
			AbstractChooseNode node = (AbstractChooseNode) in.readObject();
			boolean doIt = true;
			if (node instanceof ConstructorChooseNode) {
				if (((ConstructorChooseNode)node).loadFailed) {
					doIt = false;
				}
			}
			if (doIt) {
				this.restoreChild(node);
			} else {
				this.children.remove(node);
			}
		}
	}

	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidClassException("Class not valid");
	}
	
	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b)
			throws Exception {
		return new ClassChooseNode(userObject, annotationMap2, containingTreeModel, b);
	}	
	
}
