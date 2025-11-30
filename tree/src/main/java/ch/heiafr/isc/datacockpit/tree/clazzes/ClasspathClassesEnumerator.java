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

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;

public class ClasspathClassesEnumerator {
	
	private static final Logger logger = new Logger(ClasspathClassesEnumerator.class);	
	
	public interface Processor {
		void process(String className) throws Exception;
	}
	
	private Vector<List<String>> includePrefixes;	
	private List<String> prefixes;
	private final Processor p;
	
	// UNIQUE ENTRY POINT FOR THIS CLASS
	public static void enumerateClasses(Processor p, String[] prefixes) {
		new ClasspathClassesEnumerator(p, prefixes);
	}
	
	private ClasspathClassesEnumerator(Processor p, String[] prefixes) {
		super();
		this.p = p;
		processPrefixes(prefixes);
		try {
			String cockpitPath = System.getProperty("datacockpit.path");
			if (cockpitPath == null) {
				cockpitPath = System.getProperty("java.class.path");
			}

			logger.info("Cockpit path is " + cockpitPath);

			String[] ss = cockpitPath.split(System.getProperty("path.separator"));
			for (String c : ss) {
				File f = new File(c);
				if (f.isDirectory()) {
					processDir(f, "", 0);
				} else {
					if (f.getName().endsWith(".jar")) {
						try {
							JarFile jf = new JarFile(f);
							processJar(jf);
						} catch (ZipException ignored) {
						} catch (Exception e) {
							System.out.println("ERROR in file "
									+ f.getAbsoluteFile());
							//	throw e;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Cannot process jar " + e);
		}		
	}
	
	private void processPrefixes(String[] prefixes) {
		processPrefixes(Arrays.asList(prefixes));
	}
	
	private void processPrefixes(List<String> prefixes) {
		this.includePrefixes = new Vector<>();
		this.prefixes = new ArrayList<>();
		for (String pref : prefixes) {
			int i = pref.split("\\.").length;
			includePrefixes.setSize(Math.max(includePrefixes.size(), i));
			for (int j = 0; j < i; j++) {
				if (includePrefixes.get(j) == null) {
					includePrefixes.setElementAt(new ArrayList<>(), j);
				}
			}
			includePrefixes.get(i - 1).add(pref);
			this.prefixes.add(pref.replaceAll("\\.", "/"));
		}
	}	
	
	private void processDir(File f, String prefix, int prefixN)
	throws Exception {
		for (File sf : Objects.requireNonNull(f.listFiles())) {
			if (sf.isDirectory()) {
                boolean processed = false;
                if (prefix.isEmpty()) {
                    if (includePrefixes.isEmpty()) {
						processGrantedDir(sf, sf.getName());
					} else {
						for (String s : includePrefixes.get(prefixN)) {
							if (sf.getName().startsWith(s)) {
								processGrantedDir(sf, sf.getName());
								processed = true;
								break;
							}
						}
						if (!processed) {
							if (includePrefixes.size() > 1) {
								processDir(sf, sf.getName(), 1);
							}
						}
					}
				} else {
                    for (String s : includePrefixes.get(prefixN)) {
						if ((prefix + "." + sf.getName()).startsWith(s)) {
							processGrantedDir(sf, prefix + "." + sf.getName());
							break;
						}
					}
                    if (includePrefixes.size() > prefixN + 1) {
                        processDir(sf, prefix + "." + sf.getName(),
                                prefixN + 1);
                    }
                }
			}
		}
	}

	private void processGrantedDir(File f, String prefix) throws Exception {
		for (File sf : Objects.requireNonNull(f.listFiles())) {
			if (sf.isDirectory()) {
				if (prefix.isEmpty()) {
					processGrantedDir(sf, sf.getName());
				} else {
					processGrantedDir(sf, prefix + "." + sf.getName());
				}
			} else {
				if (sf.getName().endsWith(".class")) {
					p.process(prefix + "."
							+ sf.getName().replaceAll(".class", ""));
				}
			}
		}
	}

	private void processJar(JarFile jf) throws Exception {
	//	logger.info("Processing jar file " + jf);	
		for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
			JarEntry entry = e.nextElement();
			String s = entry.getName();
			if (s.endsWith(".class")) {
				for (String pref : prefixes) {
					if (s.startsWith(pref)) {
						s = s.replaceAll(".class", "").replaceAll("/", ".");
						try {
							p.process(s);
						} catch (Exception ex) {
							System.out.println("ERROR processing class " + s
									+ " in " + jf.getName());
							System.out.println(entry);
							throw ex;
						}
						break;
					}
				}
			}

		}
	}	
	
	
}
