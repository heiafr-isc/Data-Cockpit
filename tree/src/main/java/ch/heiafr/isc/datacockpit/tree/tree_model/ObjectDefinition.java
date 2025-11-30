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

import java.util.ArrayList;

import ch.heiafr.isc.datacockpit.general_libraries.utils.TypeParser;

public class ObjectDefinition extends AbstractDefinition {
	
	private static final long serialVersionUID = 1L;
	ArrayList<AbstractDefinition> list;
	final String constructorDef;
	
	public ObjectDefinition(String className) {	
		this(className, null);
	}

	public ObjectDefinition(String className, String constructorDef) {
		this.def = className;
		this.constructorDef = constructorDef;
	}
	
	private void initList() {
		if (list == null) {
			list = new ArrayList<>();
		}
	}
	
	public void addDefinition(AbstractDefinition d) {
		//if (d == null) throw new NullPointerException();
		initList();
		list.add(d);		
	}


	protected Class<?> getDefinedType(ClassLoader loader) throws ClassNotFoundException {
		if (constructorDef != null) {
			try {
				return Class.forName(def, false, loader);
			}
			catch (ClassNotFoundException e) {
				int index = def.lastIndexOf(".");
				String newName = def.substring(0, index);
				newName = newName + "$" + def.substring(index+1);
				return Class.forName(newName, false, loader);
			}
		} else {
			return TypeParser.getRawType(def);
		}
	}	
	
	protected Class<?> getDefinedClass(ClassLoader loader) throws ClassNotFoundException {
		if (constructorDef != null) {
			return Class.forName(constructorDef, false, loader);
		} else return getDefinedType(loader);
	}
	
	private Class<?>[] getParameterTypes(ClassLoader loader) throws ClassNotFoundException {
		if (list != null) {
			Class<?>[] types = new Class[list.size()];
			for (int i = 0 ; i < list.size(); i++) {
				ObjectDefinition defin = (ObjectDefinition)list.get(i);
				types[i] = defin.getDefinedType(loader);
			}
			return types;
		} else {
			return new Class[]{};
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		localToString(prefix, sb);
		return sb.toString();
	}
	
	public void localToString(String prefix, StringBuilder sb) {
		if (constructorDef != null) {
			sb.append(prefix).append("-").append(def).append(" -- ").append(constructorDef).append("\r\n");
		} else {
			sb.append(prefix).append("-").append(def).append("\r\n");
		}
		if (list != null) {
			for (AbstractDefinition d : list) {
				d.localToString(prefix + "  ", sb);
			}
		}
	}
}

class StringDefinition extends AbstractDefinition {

	private static final long serialVersionUID = 1L;

	StringDefinition(String s) {
		def = s;
	}
	
	void localToString(String prefix, StringBuilder sb) {
		sb.append(prefix).append("-").append(def).append("\r\n");
	}
}