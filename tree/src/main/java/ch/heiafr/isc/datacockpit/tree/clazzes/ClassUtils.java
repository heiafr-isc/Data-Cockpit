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
package ch.heiafr.isc.datacockpit.tree.clazzes;

public class ClassUtils {
	
	public static boolean isTypableType(String name) {
		return name.equals("int") || name.equals("float") || name.equals("double") || name.equals("long") || name.equals("short")
				|| name.equals("char") || name.equals("java.lang.String")
			    || name.equals("java.lang.Double") || name.equals("java.lang.Float");
	}
	
	public static boolean isBooleanType(String name) {
		return name.equals("boolean");
	}
	
	public static boolean isTypableType(Class<?> c) {
		return isTypableType(c.getName());
	}
	
	public static boolean isBooleanType(Class<?> c) {
		return isBooleanType(c.getName());
	}
	
	public static boolean isHeritingFrom(Class<?> class_, Class<?> superClass) {
		boolean ret = false;
		while (!ret && !class_.getSuperclass().equals(Object.class)) {
			if (class_.getSuperclass().equals(superClass)) {
				ret = true;
			} else {
				for (Class<?> interfaces : class_.getInterfaces()) {
					if (interfaces.equals(superClass)) {
						ret = true;
					}
				}
				class_ = class_.getSuperclass();
			}
		}
		for (Class<?> interfaces : class_.getInterfaces()) {
			if (interfaces.equals(superClass)) {
				ret = true;
			}
		}
		return ret;
	}
	

}
