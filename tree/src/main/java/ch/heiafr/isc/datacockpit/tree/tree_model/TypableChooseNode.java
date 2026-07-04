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
import java.util.Map;

import ch.heiafr.isc.datacockpit.general_libraries.utils.Pair;
import ch.heiafr.isc.datacockpit.general_libraries.utils.TypeParser;

public class TypableChooseNode extends AbstractParameterChooseNode {

	private static final long serialVersionUID = 1L;
	
	private String textValue = "";
	
	public TypableChooseNode(
			Class<?> c,
			Map<String, String> annotationMap,
			ObjectConstructionTreeModel<?> containingTree) {
		super(c, containingTree, annotationMap);
		String def = annotationMap.get("ParamName.default_");
		if (def != null) {
			textValue = def;
			if (this.checkValues()) {
				this.addValue();
				this.setExpanded(false);
			}
		}
		checkConfigured();		
	}
	
	public void setTextValue(String t) {
		textValue = t;
	}
	
	private void addValue() {
		Pair<Object, Boolean> toAddPair = this.getAddedObject();
		Object toAdd = toAddPair.getFirst();
		boolean inArray = toAddPair.getSecond();
		if (inArray) {
			for (int i = 0; i < Array.getLength(toAdd); ++i) {
				addLeaf(Array.get(toAdd, i));
			}
		} else {
			addLeaf(toAdd);
		}
	}
	
	protected void addLeaf(Object toAdd) {
		LeafChooseNode newNode = new LeafChooseNode(toAdd, this.getContainingTreeModel());
		if (this.getChildCount() == 0 || !this.children.contains(newNode)) {
			this.add(newNode);
		}		
	}
	
	public boolean isInt() {
		return parameterType.equals("int") || parameterType.equals("java.lang.Int");
	}
	
	public boolean isLong() {
		return parameterType.equals("long")  || parameterType.equals("java.lang.Long");		
	}
	
	public boolean isShort() {
		return parameterType.equals("short")  || parameterType.equals("java.lang.Short");		
	}	
	
	public boolean isFloat() {
		return parameterType.equals("float")  || parameterType.equals("java.lang.Float");		
	}
	
	public boolean isDouble() {
		return parameterType.equals("double")  || parameterType.equals("java.lang.Double");		
	}	
	
	public boolean isBoolean() {
		return parameterType.equals("boolean")  || parameterType.equals("java.lang.Boolean");		
	}	
	
	public boolean isChar() {
		return parameterType.equals("char")  || parameterType.equals("java.lang.Char");		
	}
	
	public boolean isString() {
		return parameterType.equals("java.lang.String");		
	}
	
	public boolean isClass() {
		return parameterType.equals("java.lang.Class");		
	}

	private Pair<Object, Boolean> getAddedObject() {
		Object toAdd = null;
		boolean inArray = true;
		if (isInt()) {
			toAdd = TypeParser.parseInt(textValue);
		} else if (isLong()) {
			toAdd = TypeParser.parseLong(textValue);
		} else if (isFloat()) {
			toAdd = TypeParser.parseFloat(textValue);
		} else if (isShort()) {
			toAdd = TypeParser.parseShort(textValue);
		} else if (isDouble()) {
			toAdd = TypeParser.parseDouble(textValue);
		} else if (isChar()) {
			toAdd = textValue.charAt(0);
			inArray = false;
		} else if (isString()) {
			toAdd = textValue;
			inArray = false;
		} else if (isBoolean()) {
			toAdd = Boolean.parseBoolean(textValue);
			inArray = false;
		} else if (isClass()) {
			try {
				toAdd = Class.forName(textValue);
			} catch (Exception ignored) {}
			inArray = false;
		} else {
			try {
				toAdd = Class.forName(parameterType);
			} catch (ClassNotFoundException ignored) {}
			inArray = false;
		}
		return new Pair<>(toAdd, inArray);
	}

	@Override
	public void actionPerformed(String key) {
		if (key.equals(REMOVE_ALL)) {
			this.removeAllChildren();
		} else {
			if (textValue == null) return;
			if (this.checkValues()) {
				this.addValue();
			}
		}
		getContainingTreeModel().reloadTree();
	}

	public boolean checkValues() {
		try {
            switch (this.parameterType) {
                case "int":
                    TypeParser.parseInt(textValue);
                    break;
                case "long":
                    TypeParser.parseLong(textValue);
                    break;
                case "short":
                    TypeParser.parseShort(textValue);
                    break;
                case "float":
                case "java.lang.Float":
                    TypeParser.parseFloat(textValue);
                    break;
                case "double":
                case "java.lang.Double":
                    TypeParser.parseDouble(textValue);
                    break;
                case "char":
                    if (textValue.length() != 1) {
                        textValue = "Invalid Input";
                    }
                    break;
                case "java.lang.String":
                    break;
                case "java.lang.Class":
                    Class.forName(textValue);
                    break;
            }
		}
		catch (Exception e) {
			textValue = "Invalid input";
			return false;
		}
		return true;
	}	

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject,
			Map<String, String> annotationMap2,
			ObjectConstructionTreeModel<?> containingTreeModel,
			boolean b) {
		return new TypableChooseNode(userObject, annotationMap2, containingTreeModel);
	}



}
