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

import java.util.*;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.JavaClass;

import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;

public class ClassRepository {
	
	private static final Logger logger = new Logger(ClassRepository.class);	
	
	private final HashSet<JavaClass> cache;
	/**
	 * This is a cache of ClassRepository objects. The key is the list of prefixes
	 */
	private static final HashMap<String[], ClassRepository> cacheClassRepositories = new HashMap<>();

	/**
	 * Returns the first ClassRepository object in the cache, or null if the cache is empty
	 * Does not pay any attention to prefixes
	 * @return the first ClassRepository object in the cache, or null if the cache is empty
	 */
	public static ClassRepository getClassRepository() {
		for (ClassRepository cr : cacheClassRepositories.values()) {
			return cr;
		}
		return null;
	}

	/**
	 * Returns a classRepository that has the given prefixes. If such an object is in the cache already,
	 * it is returned. Otherwise, a new object is created and put in the cache.
	 * The prefixes are sorted alphabetically before being used as a key in the cache.
	 * @param prefixes the prefixes to look for
	 * @return the ClassRepository object in the cache that has the given prefixes, or null if no such object is in the cache
	 */
	public static ClassRepository getClassRepository(String[] prefixes) {
		Arrays.sort(prefixes);
		if (cacheClassRepositories.containsKey(prefixes)) {
			return cacheClassRepositories.get(prefixes);
		}
		ClassRepository cr = new ClassRepository(prefixes);
		cacheClassRepositories.put(prefixes, cr);
		return cr;
	}
	
	private ClassRepository(String[] prefixes) {
		cache = new HashSet<>();
		ClasspathClassesEnumerator.Processor p = className -> {
            try {
                JavaClass cl = Repository.lookupClass(className);
                cache.add(cl);
            } catch (Exception e) {
                System.out.println("Class name is : " + className);
                throw e;
            }
        };
		ClasspathClassesEnumerator.enumerateClasses(p, prefixes);		
	}

	public <T> Collection<Class<T>> getClasses(Class<T> mod) {
		try {
			logger.debug("Getting classes of model " + mod);
			JavaClass model = Repository.lookupClass(mod);
			ArrayList<Class<T>> list = new ArrayList<>();
			if (mod.isInterface()) {
				for (JavaClass c : cache) {
					boolean flag;
					try {
						flag = Repository.implementationOf(c, model);
					} catch (ClassNotFoundException e) {
						continue;
					} catch (ClassFormatException e) {
						System.out.println("ClassFormatException for class :" + c.getClassName());
						continue;
					}
					if (flag) {
						if (!c.isAbstract()) {
							logger.debug("Found class " + c.getClassName());
							list.add(ClassUtils.safeForName(c.getClassName()));
						}
					} else {
						for (JavaClass superClass : c.getSuperClasses()) {
                            try {
								flag = Repository.implementationOf(superClass, model);
							} catch (ClassNotFoundException e) {
								continue;
							}
							if (flag) {
								if (!c.isAbstract()) {
									list.add(ClassUtils.safeForName(c.getClassName()));
									break;
								}
							}
						}
					}
				}
			} else {
				for (Iterator<JavaClass> ite = cache.iterator(); ite.hasNext(); ) {
					JavaClass c = ite.next();
					try {
						if (Repository.instanceOf(c, model)) {
							if (!c.isAbstract()) {
								list.add(ClassUtils.safeForName(c.getClassName()));
							}
						}
					} catch (Throwable e) {
						System.out.println("ERROR with class " + c.getClassName());
						//	System.out.println(e);
						ite.remove();
					}
				}
			}
			return list;
		}
		catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}	
}
